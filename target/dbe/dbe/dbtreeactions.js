// DBTree 的功能Action 定义
DBE.DBTreePanelActions = function(dbtree, tabPanel) {
	/*
	 * 通过 node 取得元素类型：TABLE、VIEW、Field、UNKNOW 等..
	 */
	var getDBElementTypeOfNode = function(node) {
		// if (!node.childNodes || node.childNodes.length < 1) {
		if (node.leaf) {
			// alert(node.leaf)
			// 是叶子节点，有可能是FIELD类型;
			var parent = node.parentNode;
			while (parent) {
				var parentText = parent.text;
				if (parentText == "TABLE" || parentText == "VIEW") {
					return "FIELD";
				}
				parent = parent.parentNode;
			}
		} else {
			var type = node.parentNode ? node.parentNode.text : "UNKNOW";
			return type;
		}
	};
	/*
	 * 打开node 节点代表的数据库元素（例如：表、视图、存储过程等等）
	 */
	var funOpenDBElement = function(node, objEvent) {
		var schema = node.parentNode.parentNode.text;
		var tabName = "tab" + schema + '.' + node.text;
		var tab = tabPanel.getItem(tabName);
		if (!tab) {
			var type = getDBElementTypeOfNode(node);
			if (type == "TABLE" || type == "VIEW") {
				// alert('open Table ' + node.text);
				var wait = Ext.Msg.wait('读取中...', '请稍等');
				DBE.getTableInfo(node, function(tableinfo) {
					if (tableinfo) {
						// 取得了结构信息，准备构建动态表格...
						var grid = new DBE.DynamicTableGrid({
							tableinfo : tableinfo
						});

						// create panel.
						var panel = tabPanel.add({
							id : tabName,
							title : schema + '.' + node.text,
							autoScroll : true,
							closable : true,
							plain : true,
							layout : 'fit',
							items : grid
						})
						panel.show();
						// init grid
						grid.buildTBarItems();
						grid.load(node);

						// 校正 grid.size
						var gSize = panel.getSize();
						gSize.width = gSize.width - 1;
						gSize.height = gSize.height - 1;
						grid.setSize(gSize);
						// alert("panel:" + Ext.encode(panel.getSize()));
						// alert("grid:" + Ext.encode(grid.getSize()));
					}
					//
					wait.getDialog().close();
				});
			} else {
				//alert('未知的元素类型，，：' + type);
				Ext.Msg.info({
					title:'提示',
					message : '未知的元素类型' + type,
					alignType : 'tl-tl?'
				});
			}
		} else {
			tabPanel.setActiveTab(tab);
		}
	}
	/**
	 * 刷新 节点内容..
	 */
	this.refresh = new Ext.Action({
		text : '刷新',
		iconCls : 'refresh',
		handler : function() {
			var node = dbtree.getSelectNode();
			if (node.reload) {
				node.reload();
			}
		}
	});
	/**
	 * 打开指定元素
	 */
	this.open = new Ext.Action({
		text : '打开',
		iconCls : 'open',
		handler : function(btn, objEvent) {
			var node = dbtree.getSelectNode();
			funOpenDBElement(node, objEvent);
		}
	});
	/**
	 * 删除当前节点代表的数据库元素
	 */
	this.remove = new Ext.Action({
		text : '删除',
		iconCls : 'remove',
		handler : function(btn, objEvent) {
			var node = dbtree.getSelectNode();
			Ext.MessageBox.confirm('提醒',"您确认要删除 [" + node.text + " ]?",function(btn){
			//if (confirm("您确认要删除 [" + node.text + " ]?")) {
			  if(btn =='yes'){
				Ext.Ajax.request({
					scope : this,
					url : dbtree.serviceURL + '/drop.do',
					params : {
						node : node.id,
						path : node.getPath('text'),
						text : node.text
					},
					success : function() {
						Ext.Msg.info({
							message : '[' + node.text + ' ]删除成功',
							alignType : 'tl-tl?'
						});
						node.remove();
					},
					failure : function() {
						//alert("删除失败");
						Ext.Msg.info({
							message : '[' + node.text + ' ]删除失败',
							alignType : 'tl-tl?'
						});
					}
				});
			}});
		}
	});
	/**
	 * 打开sql查询窗口
	 */
	this.query = new Ext.Action({
		text : 'SQL查询',
		iconCls : 'query',
		handler : function() {
			var node = dbtree.getSelectNode();
			// 取得合适的tabPanel名称，（）
			var tabTitle = "SQL_Query";
			var tabName = "";
			var tab = false;
			for (var i = 1; i < 1000; i++) {
				tabName = tabTitle + "_" + i;
				tab = tabPanel.getItem(tabName);
				if (!tab) {
					break;
				}
			}

			// create QueryPanel
			var query = new DBE.SQLQueryPanel();
			// create Panel
			var ctl = tabPanel.add({
				id : tabName,
				title : tabName,
				autoScroll : true,
				closable : true,
				plain : true,
				items : query
			})
			ctl.show();

			// init QueryPanel
			var type = getDBElementTypeOfNode(node);
			var sql = false;
			if (type == "TABLE" || type == "VIEW") {
				var schema = node.parentNode.parentNode.text;
				sql = 'select * from ' + schema + '.' + node.text;
			} else if (type == "FIELD") {
				var schema = node.parentNode.parentNode.parentNode.text;
				var table = node.parentNode.text;
				sql = 'select ' + node.text + ' from ' + schema + '.' + table;
			}
			query.init(sql);
		}
	});
	/**
	 * copy node Name
	 */
	this.copy = new Ext.Action({
		text : '复制名称',
		iconCls : 'copy',
		handler : function() {
			var node = dbtree.getSelectNode();
			DBE.copyToClipboard(node.text);
		}
	});

	/**
	 * 创建表
	 */
	this.createTable = new Ext.Action({
		text : '创建表',
		iconCls : 'createtable',
		handler : function() {
			// create TableInfoWindow
			var window = new DBE.TableInfoWindow({
				isCreate : true,// 是create，表示是创建新表
				readOnly : false,
				node : dbtree.getSelectNode()
			});
			window.show(Ext.getBody(), function() {
				window.resetSize();
			});
		}
	});

	/**
	 * 查看表或者服务器的属性
	 */
	this.attribute = new Ext.Action({
		text : '属性',
		iconCls : 'attribute',
		handler : function() {
			var node = dbtree.getSelectNode();
			var type = getDBElementTypeOfNode(node);
			if (type == "TABLE" || type == "VIEW") {
				// alert('open Table ' + node.text);
				var wait = Ext.Msg.wait('读取中...', '请稍等');
				// 取得列信息
				DBE.getTableInfo(node, function(tableinfo) {
					var columns = tableinfo.columns;
					var window = new DBE.TableInfoWindow({
						isCreate : false,// 不是create，表示是查看/修改表结构
						readOnly : true,// TODO:目前暂时不允许表结构修改，将来功能完成后应设置成 仅视图只读
						node : node
					});
					window.show(Ext.getBody(), function() {
						window.resetSize();
						window.getTableInfoPanel().initColumnInfo(columns);
						wait.hide();
					});
				});
			} else {
				//alert('未知的元素类型，，：' + type);
				Ext.Msg.info({
					title:'提示',
					message : '未知的元素类型' + type,
					alignType : 'tl-tl?'
				});
			}
		}
	});

	/**
	 * 根据当前dbtree Node的选择情况，设置 各个Action的可见状态
	 */
	this.resetActionState = function() {
		var node = dbtree.getSelectNode();
		var type = getDBElementTypeOfNode(node);
		var torv = type == "TABLE" || type == "VIEW";
		this.open.setHidden(!torv);
		this.remove.setHidden(!torv);
	};
	/**
	 * 取得所有 Ext.Action 类型的成员，用于外部菜单的创建
	 */
	this.getActionItems = (function() {
		var as = [];
		for (action in this) {
			if (this[action].execute) {
				// 用Ext.Action对象独有的 execute 成员作为判断的依据
				as = as.concat(this[action]);
			}
		}
		return as;
	}).createDelegate(this);
}