Ext.onReady(function() {
	// 声明编辑器
	var sqlEditor = false;

	// 创建Panel
	var panel = new Ext.Panel({
		title : 'Test SQLEditor',
		frame : true,
		height : 340,
		width : 500,
		tbar : ['->', {
			text : 'Run',
			handler : function() {
				if (sqlEditor) {
					var text = sqlEditor.getText();
					alert("SQL:" + text);
				} else {
					alert("sqlEditor 还未初始化~~");
				}
			}
		}, {
			text : 'Set',
			handler : function() {
				if (sqlEditor) {
					sqlEditor.setText('insert into mytable(');
				} else {
					alert("sqlEditor 还未初始化~~");
				}
			}
		}, {
			text : 'Append',
			handler : function() {
				if (sqlEditor) {
					sqlEditor.setText('insert into mytable2(', 1);
				} else {
					alert("sqlEditor 还未初始化~~");
				}
			}
		}]
	});
	panel.render(Ext.getBody());
	panel.getEl().center();

	// 插入编辑器
	var body = panel.getEl().child('.x-panel-body');
	if (body) {
		var divId = 'se_' + panel.getId();
		body.createChild({
			tag : 'div',
			id : divId
		});
		sqlEditor = new SqlEditor({
			id : 'fl_' + panel.getId(),
			div : divId,
			swf : '../sqleditor/starter.swf'
		});
	} else {
		alert("create sqlEditor div container errer~~.");
	}
});