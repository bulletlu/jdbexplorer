// 表格创建/信息显示面板 定义
DBE.TableInfoPanel = function(config) {
	// 助手类
	var utils = new DBE.TableInfoPanelUtils(this);

	// 创建基本信息Panel
	var dbName = config.databaseName;
	var schemaName = config.schemaName;
	var tableName = config.tableName;
	var fsBase = utils.createBaseInfoFieldSet(dbName, schemaName, tableName);

	// 创建字段信息Panel
	var panel = new DBE.TableColumnPanel({
		isCreate : config.isCreate,
		readOnly : config.readOnly
	});
	var fsColumns = new Ext.form.FieldSet({
		layout : 'fit',
		collapsed : true,
		collapsible : true,
		title : '列信息',
		items : panel,
		listeners : {
			resize : {
				scope : this,
				fn : function(fs, adjWidth, adjHeight, rawWidth, rawHeight) {
					// 智能调整表格大小
					if (panel.rendered) {
						var width = this.getSize(true).width - 15;
						if (width > 300) {
							panel.resetSize({
								width : width * 0.6,
								height : rawHeight
							});
						}
					}
				}
			}
		}
	});

	// 准备配置参数
	var cfg = {
		plain : true,
		frame : true,
		border : false,
		autoSize : true,
		layout : 'fit',
		// html : 'sdfasdfa'
		items : [fsBase, fsColumns]
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.TableInfoPanel.superclass.constructor.call(this, config);

	// 公布属性
	this.baseFieldSet = fsBase;
	this.columnPanel = panel;
	this.columnsFieldSet = fsColumns;
};
Ext.extend(DBE.TableInfoPanel, Ext.Panel, {
	/**
	 * 设置面板中元素大小
	 */
	resetSize : function(size) {
		// 展开面板
		this.baseFieldSet.expand(true);
		this.columnsFieldSet.expand(true);

		// 重新计算大小
		this.baseFieldSet.setWidth(size.width - 12);

		size.height = size.height - this.baseFieldSet.getFrameHeight();
		size.height -= 160;// 按钮栏
		this.columnsFieldSet.setHeight(size.height);
	},
	/*
	 * 初始化 预置的列信息
	 */
	initColumnInfo : function(columns) {
		this.columnPanel.setColumnInfo(columns);
	},
	/**
	 * 执行 表创建SQL
	 */
	createTable : function(callback) {
		// alert("生成SQL语句，执行创建~~>..");
		var schema = this.initialConfig.schemaName;
		var tableName = this.baseFieldSet.getTableName();
		if (tableName) {
			tableName = tableName.trim();
			if (tableName.length == 0) {
				alert("创建表失败:未指定表名称~！");
				return;
			}

			// 取得sql语句
			var sql = this.columnPanel.sqlMaker.build(schema, tableName);
			// alert("sql:" + sql);
			if (sql && sql.length > 0) {

				var wait = Ext.Msg.wait('正在创建表..', '请稍等');
				Ext.Ajax.request({
					url : '../dbeSQLQueryAction/query.do',
					scope : this,
					params : {
						sql : sql
					},
					success : function(resp, opt) {
						// alert("创建表 [" + tableName + "] 成功~~.");
						wait.hide();
						if (callback) {
							callback(true, tableName);
						}
					},
					failure : function(resp, opt) {
						wait.hide();
						var json = resp.responseText;
						var result = eval("(" + json + ")");
						var msg = null;
						if (result && result.msg) {
							msg = result.msg;
						} else {
							msg = "未知错误~!";
						}
						if (callback) {
							callback(false, tableName, msg);
						}
						// alert("创建表失败:" + result.msg);
					}
				});
			} else {
				alert("取得create table sql语句出错：" + sql);
			}

		} else {
			alert("创建表失败：取得表名称出错~~！");
		}
	},
	/**
	 * 修改表结构
	 */
	modifyTablie : function(callback) {
		var schema = this.initialConfig.schemaName;
		var tableName = this.initialConfig.tableName;
		var sql = this.columnPanel.sqlMaker.build(schema, tableName);

		alert('修改表结构...:' + sql);
	}
});