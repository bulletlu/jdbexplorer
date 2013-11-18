// DEB SQLEditor定义..
DBE.SQLEditor = function(config) {
	// 初始默认高度..
	if (!config.height) {
		config.height = 180
	};

	// create editor
	var style = 'font-size:10pt;font-weight:bold;font-family:'
			+ 'Courier New,Sans Serif,Times New Roman';

	this.editor = new Ext.form.TextArea({
		preventScrollbars : true,
		enableKeyEvents : true,
		height : config.height,
		style : style
	});

	var cfg = {
		layout : 'fit',
		plain : true,
		frame : false,
		border : false,
		autoSize : true,
		height : config.height,
		items : this.editor,
		tbar : config.actions
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.SQLEditor.superclass.constructor.call(this, config);

	// 公布属性(编辑框TextArea的当前选择内容范围)
	this.selectStartIdx = 0;
	this.selectEndIdx = 0;
};
Ext.extend(DBE.SQLEditor, Ext.Panel, {
	init : function() {
	},
	
	/*
	 * 同步输入框选择内容的范围索引
	 */
	syncSelectRangeIdx : function() {
		var editor = this.editor;
		var textBox = editor.getEl().dom;

		this.selectStartIdx = 0;
		this.selectEndIdx = 0;

		if (typeof(textBox.selectionStart) == "number") {
			// 如果是Firefox的话，方法很简单
			this.selectStartIdx = textBox.selectionStart;
			this.selectEndIdx = textBox.selectionEnd;
		} else if (document.selection) {
			// 初始化 位置变量
			var start = 0, end = 0;

			var range = document.selection.createRange();
			if (range.parentElement().id == textBox.id) {
				// 计算开始位置
				var range_all = document.body.createTextRange();
				range_all.moveToElementText(textBox);
				for (start = 0; range_all.compareEndPoints("StartToStart",
						range) < 0; start++) {
					range_all.moveStart('character', 1);
				}
				for (var i = 0; i <= start; i++) {
					if (textBox.value.charAt(i) == '\n') {
						start++;
					}
				}

				// 计算结束位置
				var range_all = document.body.createTextRange();
				range_all.moveToElementText(textBox);
				for (end = 0; range_all.compareEndPoints('StartToEnd', range) < 0; end++) {
					range_all.moveStart('character', 1);
				}
				for (var i = 0; i <= end; i++) {
					if (textBox.value.charAt(i) == '\n') {
						end++;
					}
				}
			}
			// 记录位置变量..
			this.selectStartIdx = start;
			this.selectEndIdx = end;
		}
	},
	/**
	 * 取得当前的 SQL文本
	 */
	getSQLText : function(all) {
		// 取得全部sql
		all = !!all;
		if (all) {
			return this.editor.getValue();
		}

		// 仅取得选择范围内的 sql
		this.syncSelectRangeIdx();
		var start = this.selectStartIdx, end = this.selectEndIdx;
		// alert("[" + start + "," + end + "]");

		var sql = null;
		if (this.selectStartIdx == this.selectEndIdx) {
			// 当前未选择内容，取得最后一行
			sql = this.editor.getValue();
			// var ss = new String("ss");
			if (sql.indexOf("\n") > -1) {
				var lines = sql.split('\n');
				for (var i = lines.length; i > 0; i--) {
					sql = lines[i - 1];
					if (sql.length > 0) {
						break;
					}
				}
			}
		} else {
			var value = this.editor.getValue();
			sql = value.substr(start, (end - start));
		}
		return sql;
	},
	
	/**
	 * 设置新的SQL文本
	 */
	setSQLText : function(sql, append) {
		append = !!append;
		if (append) {
			sql = this.editor.getValue() + "\n" + sql;
		} else {
			this.editor.setValue(sql);
		}
	}
});