// 主窗口定义..
DBE.MainWindow = function(config) {
	// create TabPanel
	this.welcomeGrid = new DBE.WelcomeGrid();
	var mainPanel = new Ext.TabPanel({
		region : 'center',
		margins : '5 5 5 0',
		autoScroll : true,
		enableTabScroll : true,
		activeTab : 0,
		frame : true,
		plain : true,
		plugins : new Ext.ux.TabCloseMenu(),
		listeners : {
			bodyresize : function(panel, width, height) {
				if (Ext.isIE6) {
					var grid = panel.items.get(0);
					var size = grid.getSize();
					// alert("o.grid.size:" + Ext.encode(size));
					// grid.setSize(psize);
					grid.setWidth(width - 10);
					grid.setHeight(height);
					// size = grid.getSize();
					// alert("n.grid.size:" + Ext.encode(size));
					// alert('panel.size,width:' + width + ",height:" + height);
				}
			}
		},
		defaults : {
			layout : 'fit',
			autoScroll : true,
			// autoWidth : true,
			// autoHeight : true,
			autoSize : true
		},
		items : [{
			id : 'tabWelcome',
			layout : 'fit',
			title : '欢迎',
			closable : false,
			// html : '<h1>Welcome Tabpanel~.</h1>'
			items : [this.welcomeGrid]
		}]
	});

	// create DBTreePanel
	this.treePanel = new DBE.DBTreePanel({
		gridTabPanel : mainPanel
	});

	// create mainMenus,合并 dbtree上的Actions 和main Actions
	this.actions = new DBE.Actions(this);
	var treeActions = this.treePanel.actions.getActionItems();
	var menus = new Ext.menu.Menu({
		listeners : {
			beforeshow : {
				scope : this,
				fn : function(btn, objEvent) {
					// 菜单显示前重置 显示状态
					this.treePanel.actions.resetActionState();
				}
			}
		}
	});
	for (var i = 0; i < treeActions.length; i++) {
		var action = treeActions[i];
		menus.add(action);
	}
	menus.addSeparator();
	menus.add(this.actions.configAction);
	menus.add(this.actions.exitAction);

	// create leftpanel
	var leftPanel = {
		id : 'dbtreePanel',
		region : 'west',
		layout : 'fit',
		margins : '5 0 5 5',
		collapsible : true,
		collapseMode : 'mini',
		split : true,
		minSize : 180,
		width : 210,
		tbar : ['->', {
			text : '操作',
			tooltip : '更多操作',
			tooltipType : 'title',
			iconCls : 'menus',
			menu : menus
		}],
		items : this.treePanel
	};
	// create TopPanel
	var topPanel = new Ext.Panel({
		region : 'north',
		title : 'Welcome To DBExplorer',
		plain : true,
		frame : false,
		border : false,
		autoScroll : false,
		autoSize : true,
		border : false,
		height : 0,
		tools : [{
			id : 'gear',
			scope : this,
			qtip : '系统配置',
			handler : function() {
				this.actions.config();
			}
		}, {
			id : 'close',
			scope : this,
			qtip : '退出系统',
			handler : function() {
				this.actions.exit();
			}
		}]
	});

	// Config Params.
	var cfg = {
		layout : 'border',
		items : [topPanel, leftPanel, mainPanel]
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.MainWindow.superclass.constructor.call(this, config);
}
Ext.extend(DBE.MainWindow, Ext.Viewport, {
	ie6Fix : function() {
		// fix toppanel..
		var topp = this.items.get(0);// 取得toppanel
		var tope = topp.getEl();
		var last = tope.last();
		last.remove();
		this.doLayout();
	},
	init : function(callback) {
		// alert('init:数据初始化~~');
		Ext.Ajax.request({
			url : '../initAction/init.do',
			scope : this,
			success : function(response) {
				var json = response.responseText.trim();
				// alert("请求成功：" + json);
				var rst = eval("(" + json + ")");
				if (!rst.login) {
					// alert("还没有登录...:" + this.login);
					this.login(callback);
				} else {
					callback();
				}
			},
			failure : function(response) {
				var json = response.responseText.trim();
				alert('请求失败:' + json);
			}
		});
	},
	login : function(callback) {
		// 登录数据库
		if (!DBE.loginWindow) {
			DBE.loginWindow = new DBE.LoginWindow();
			DBE.loginWindow.successCallback = callback;
		}
		DBE.loginWindow.show();
		DBE.loginWindow.load();
	},
	refresh : function() {
		// 登录后 刷新页面内容.刷新DBTree
		Ext.Ajax.request({
			url : this.treePanel.serviceURL + "/tree.do",
			scope : this,
			success : function(response) {
				var json = response.responseText;
				// alert("请求数据库结构成功：" + json);
				var rst = eval("(" + json + ")");

				var root = this.treePanel.getRootNode();
				root.setText(rst.text);
				this.treePanel.select(root);
			},
			failure : function(response) {
				if (response.status == 200) {
					var json = response.responseText.trim();
					alert("Load DBTreePanel失败：" + json);
				} else {
					var msg = response.statusText;
					msg += "[" + response.status + "]";
					alert('Load DBTreePanel出错:' + msg);
				}
			}
		});

		// 刷新 WelcomeGrid
		this.welcomeGrid.getStore().load();
	}
});