// 大字段显示窗口定义
DBE.ShowLobWindow = function(config) {
	// 检查参数，确认资源格式
	var cfg = {
		type : "BLOB",
		isNull : true
	};
	config = Ext.applyIf(config || {}, cfg);

	// 创建Lob的显示面板..
	var lobPanel = null;
	if (config.type == "BLOB") {
		// 二进制数据，准备构建二进制显示面板
		lobPanel = new DBE.BLobPanel(config, this);
	} else {
		// 文本数据，准备构建文本显示面板..
		lobPanel = new DBE.CLobPanel(config, this);
	}

	// 准备配置参数
	var cfg = {
		title : '查看LOB数据',
		layout : 'fit',
		height : 10,
		width : 10,
		plain : true,
		frame : true,
		border : false,
		modal : true,
		draggable : false,
		resizable : false,
		closable : true,
		items : lobPanel
	};
	config = Ext.applyIf(config || {}, cfg);

	// call superclass
	DBE.ShowLobWindow.superclass.constructor.call(this, config);
}
Ext.extend(DBE.ShowLobWindow, Ext.Window, {
	/**
	 * 矫正窗口大小
	 */
	syncSize : function() {
		var panel = this.items.get(0);
		if (panel.init) {
			panel.init();
			var size = panel.getSize();
			size.width = size.width + 50;
			size.height = size.height + 115;
			size.height = size.width < 250 ? size.height + 15 : size.height;
			this.setSize(size);
			this.getEl().center();
			// alert("window.size:" + Ext.encode(size));
		}
	}
});