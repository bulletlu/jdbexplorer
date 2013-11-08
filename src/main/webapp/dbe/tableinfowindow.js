// 表格创建/属性显示窗口 定义
DBE.TableInfoWindow = function(config) {
	// 取得当前节点
	var node = config.node;
	var nodes = node.getPath('text').split("/");

	// 创建TableInfoPanel
	var tableInfoPanel = new DBE.TableInfoPanel({
		isCreate : config.isCreate,// 是否是创建表（反之是查看表格属性）
		readOnly : config.readOnly,// 是否只读
		databaseName : nodes[1],
		schemaName : nodes[2],
		tableName : config.isCreate ? 'NewTable' : node.text
	});

	// 处理 创建新表
	var processCreateTableHandler = function(winInfo) {
		tableInfoPanel.createTable(function(succeed, tableName, msg) {
			if (succeed) {
				// 刷新DBTree
				if (node.reload!=undefined) {
					node.reload();
				}
				alert("[" + tableName + "] 表创建成功~~.");
				winInfo.close();
			} else {
				alert("[" + tableName + "] 表创建失败:" + msg);
			}
		});
	};
	// 处理 表结构修改
	var processModifyTableHandler = function(winInfo) {
		tableInfoPanel.modifyTablie(function(succeed, msg) {
			alert('sdfsdfsad');
		});
	}

	// 准备配置参数
	var size = Ext.getBody().getSize(true);// 取得默认大小
	var title = config.isCreate ? '创建表' : '查看属性 - ' + node.text;
	var cfg = {
		width : size.width * 0.85,
		height : size.height * 0.85,
		title : title,
		layout : 'fit',
		plain : true,
		frame : true,
		border : false,
		modal : true,
		draggable : false,
		resizable : true,
		closable : true,
		items : tableInfoPanel
	// html:'window'
	};
	if (!config.readOnly) {
		cfg.buttons = [{
			text : '确认',
			scope : this,
			handler : function() {
				if (config.isCreate) {
					processCreateTableHandler(this);
				} else {
					processModifyTableHandler(this);
				}
			}
		}, {
			text : '取消',
			scope : this,
			handler : function() {
				if (config.isCreate) {
					if (confirm('您确认要放弃本次创建吗~?')) {
						this.close();
					}
				} else {
					// alert("检查表表结构是否有变动..~");
					if (tableInfoPanel.columnPanel.checkChanges()) {
						if (!confirm('您确认要放弃表结构修改吗?')) {
							return;
						}
					}
					this.close();
				}
			}
		}]
	}
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.TableInfoWindow.superclass.constructor.call(this, config);
};
Ext.extend(DBE.TableInfoWindow, Ext.Window, {
	/*
	 * 重新设置大小
	 */
	resetSize : function() {
		var size = this.getSize();
		var panel = this.items.get(0);
		panel.resetSize(size);
	},
	/*
	 * 取得TablInfoPanel
	 */
	getTableInfoPanel : function() {
		var panel = this.items.get(0);
		return panel;
	}
});