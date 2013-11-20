// 主窗口定义..
MainWindow = function(config) {
	// Config Params.
	var mainPanel = {
		id : 'panel_center',
		region : 'center',
		autoScroll : true,
		minSite : 30,
		html : '<h1>Panel Center</h1>'
	};

	var leftPanel = {
		id : 'panel_west',
		region : 'west',
		split : true,
		minSize : 120,
		width : 180,
		autoScroll : true,
		html : '<h1>Panel West</h1>'
	}

	var cfg = {
		id : 'mypanel',
		layout : 'border',
		title : 'Test Panel Border Layout',
		border : false,
		width : 650,
		height : 500,
		items : [leftPanel, mainPanel]
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	MainWindow.superclass.constructor.call(this, config);
}
Ext.extend(MainWindow, Ext.Panel);