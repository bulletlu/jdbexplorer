// DEB SQLEditor定义..
DBE.SQLSwfEditor = function(config) {
	// 初始默认值
	if (!config.height) {
		config.height = 180
	};
	var cfg = {
		layout : 'fit',
		plain : true,
		frame : false,
		border : false,
		autoSize : true,
		height : config.height,
		tbar : config.actions
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.SQLSwfEditor.superclass.constructor.call(this, config);
};
Ext.extend(DBE.SQLSwfEditor, Ext.Panel, {
	/*
	 * 初始化编辑器
	 */
	init : function() {
		var body = this.getEl().child('.x-panel-body');
		if (body) {
			var divId = 'se_' + this.getId();
			body.createChild({
				tag : 'div',
				id : divId
			});
			this.editor = new SqlEditor({
				id : 'fl_' + this.getId(),
				div : divId,
				swf : '../sqleditor/starter.swf'
			});
		} else {
			alert("create sqlEditor div container errer~~.");
		}
	},
	/**
	 * 取得当前的 SQL文本
	 */
	getSQLText : function(all) {
		all = !!all;
		if (all) {
			return this.editor.getAllText();
		} else {
			return this.editor.getText();
		}
	},

	/**
	 * 设置新的SQL文本
	 */
	setSQLText : function(sql, append) {
		append = !!append;
		var fun = function(type) {
			try {
				this.editor.setText(sql, type);
			} catch (e) {
				//alert(e);
				fun.defer(300, type);
			}
		}
		fun = fun.createDelegate(this);

		if (append) {
			// this.editor.setText(sql, 1);
			fun.defer(500, 1);
		} else {
			// this.editor.setText(sql, 0);
			fun.defer(500, 0);
		}
	}
});