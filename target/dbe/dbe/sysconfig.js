// 系统配置窗口定义
DBE.SysConfigWindow = function(config) {
	// def config params
	var cfg = {
		title : '系统配置',
		layout : 'fit',
		height : 400,
		width : 520,
		plain : true,
		frame : true,
		border : false,
		modal : true,
		draggable : false,
		resizable : false,
		closable : true,
		// items : []
		html : '<h1>System Config</h1>'
	};
	config = Ext.applyIf(config || {}, cfg);

	// call superclass
	DBE.SysConfigWindow.superclass.constructor.call(this, config);
}
Ext.extend(DBE.SysConfigWindow, Ext.Window, {
	/**
	 * 显示窗口，并且自动大小
	 */
	showToCenter : function(animateTarget) {
		var self = this;
		this.show(animateTarget, function() {
			var size = Ext.getBody().getSize(true);
			size.width = size.width / 2;
			size.height = size.height / 2;
			// 校正高度
			//var oheight = size.height + 150; 
			size.height = size.height + 150;
			self.setSize(size);
			self.getEl().center();
		});
	}
});