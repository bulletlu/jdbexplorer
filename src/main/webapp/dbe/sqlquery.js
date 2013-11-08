// DEB Action定义..
DBE.SQLQueryPanel = function(config) {
	// actions
	var actions = new DBE.SQLQueryPanelActions(this);

	// sql editor
	var editorConfig = {
		region : 'north',
		split : true,
		collapsible : true,
		collapseMode : 'mini',
		minSize : 80,
		actions : actions.getActions(),
		keys : [{
			key : [Ext.EventObject.ENTER],
			scope : this,
			ctrl : false,
			shift : false,
			alt : true,
			fn : function(key, eventObj) {
				// alt + Enter 时运行sql
				this.runSQL();
			}
		}]
	};
	var sqlEditor = null;
	if (Ext.isIE) {
		sqlEditor = new DBE.SQLSwfEditor(editorConfig);
	} else {
		sqlEditor = new DBE.SQLEditor(editorConfig);
	}

	// 创建grid（使用一个默认的TableInfo模型）
	var grid = new DBE.DynamicQueryGrid({
		tableinfo : {
			columns : [{
				name : 'Result',
				id : 'Result',
				extType : {
					booleanType : false,
					dateFormat : "",
					dateType : false,
					directShow : true,
					format : "",
					longType : false,
					numberType : false,
					sortable : true,
					type : "auto"
				}
			}]
		}
	});

	// 构建数据窗口
	var grid1 = new Ext.Panel({
		title : '网格数据',
		region : 'center',
		minSize : 80,
		layout : 'fit',
		plain : true,
		frame : false,
		border : false,
		items : grid
	});

	var message = new Ext.form.TextArea({
		id : 'node',
		value : ""
	})
	// 构建消息窗口
	var grid2 = new Ext.Panel({
		title : '消息窗口',
		region : 'center',
		minSize : 80,
		layout : 'fit',
		plain : true,
		frame : false,
		border : false,
		items : message
	});

	var queryGridPanel = new Ext.TabPanel({
		region : 'center',
		width : 450,
		height : 300,
		activeTab : 0,
		frame : true,
		items : [grid1, grid2]
	});

	var ds = grid.getStore();
	ds.on('load', function() {
		var record = ds.getAt(0);
		if (record != null) {
			var data = ds.getAt(0).data['Result'];
			if (data == undefined) {
				// sql 为select语，显示数据窗口
				queryGridPanel.setActiveTab(0);
			} else {
				// sql 为update,insert和delete时，显示消息窗口
				message.setValue(data);
				queryGridPanel.setActiveTab(1);
			}
		}

	});

	// 准备配置参数
	var cfg = {
		layout : 'border',
		plain : true,
		frame : false,
		border : false,
		autoScroll : true,
		autoSize : true,
		split : true,
		items : [sqlEditor, queryGridPanel]
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.SQLQueryPanel.superclass.constructor.call(this, config);

	// 公布属性
	this.grid = grid;
	this.grid1 = grid1;
	this.message = message;
	this.queryGridPanel = queryGridPanel;
	this.sqlEditor = sqlEditor;
	this.actions = actions;
};
Ext.extend(DBE.SQLQueryPanel, Ext.Panel, {
	/**
	 * 初始化 查询面板，包括设置sql，以及执行查询..
	 */
	init : function(sql) {
		this.sqlEditor.init();
		// 构建工具栏按钮
		this.grid.buildTBarItems();

		if (sql && sql.length > 0) {
			// alert("runsql"+sql);
			this.runSQL(sql);
		} else {
			// load初始空数据
			this.grid.store.loadData({
				total : 1,
				rows : [{
					Result : 'no records.'
				}]
			});
		}
	},
	/**
	 * 执行指定的sql语句
	 */
	runSQL : function(sql, append) {
		var hasInitSQL = !!sql;
		if (!sql) {
			// 从编辑器中取得默认sql
			sql = this.sqlEditor.getSQLText();
		}

		if (sql && sql.length > 0) {
			this.grid.reload(sql);
			this.queryGridPanel.setActiveTab(0);
		} else {
			alert("请输入SQL语句~~");
		}
		//alert('wait');
		if (hasInitSQL) {
			if (append) {
				this.sqlEditor.setSQLText(sql, true);
			} else {
				this.sqlEditor.setSQLText(sql, false);
			}
		}
	},
	/*
	 * 执行SQL文件，返回相应的消息
	 * 
	 */
	runSQLFile : function(data) {
		this.message.setValue(data);
		this.queryGridPanel.setActiveTab(1);

	},

	/*
	 * 打开SQL文件，将文件内容在sqlEditor中显示
	 * 
	 */
	openSQLFile : function(data) {
		this.sqlEditor.setSQLText(data);

	}

});