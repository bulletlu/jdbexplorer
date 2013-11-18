// 主窗口定义..
DBE.MainWindow = function(config) {
	// create TabPanel
	var mainPanel = new Ext.TabPanel({
		region : 'center',
		margins : '5 5 5 0',
		autoScroll : true,
		enableTabScroll : true,
		activeTab : 0,
		frame : true,
		plain : true,
		// plugins : new Ext.ux.TabCloseMenu(),
		defaults : {
			layout : 'fit',
			autoScroll : true,
			autoWidth : true,
			autoSize : true
		},
		items : [{
			id : 'tabWelcome',
			title : '欢迎',
			closable : false,
			html : '<h1>Welcome Tabpanel~.</h1>'
		// items : this.welcomeGrid
		}]
	});

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
		// items : this.treePanel
		html : 'left panel'
	};
	// create TopPanel
	var topPanel = new Ext.Panel({
		id : 'topPanel',
		region : 'north',
		title : 'Welcome To DBExplorer',
		plain : true,
		frame : false,
		border : false,
		autoScroll : false,
		autoSize : false,
		border : false,
		height : 0,
		tools : [{
			id : 'close',
			scope : this,
			handler : function() {
				this.actions.exit();
			}
		}]
	});

	// Config Params.
	// var size = Ext.getBody().getSize(true);
	// alert(Ext.encode(size));
	var cfg = {
		layout : 'border',
		border : false,
		items : [topPanel, leftPanel, mainPanel]
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.MainWindow.superclass.constructor.call(this, config);
}
Ext.extend(DBE.MainWindow, Ext.Viewport, {
	ie6Fix : function() {
		var topp = this.items.get(0);// 取得toppanel
		var tope = topp.getEl();
		var last = tope.last();
		last.remove();
		this.doLayout();
	}
});