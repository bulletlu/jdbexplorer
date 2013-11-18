Ext.onReady(function() {
	var combo = new Ext.form.ComboBox({
		store : DBE.fieldDataTypesStore,
		valueField : "key",
		displayField : "key",
		typeAhead : false,
		triggerAction : 'all',
		emptyText : 'Select a state...',
		selectOnFocus : true,
		disableKeyFilter : true,
		renderTo : 'divComboBox'
	});

	var combo2 = new Ext.form.ComboBox({
		store : DBE.booleanComboItemStore,
		mode : 'local',
		valueField : "name",
		displayField : "name",
		typeAhead : true,
		triggerAction : 'all',
		emptyText : 'Select a state...',
		selectOnFocus : true,
		disableKeyFilter : true,
		renderTo : 'divComboBox2'
	});

	// create the editor grid
	var dataRecord = Ext.data.Record.create([{
		name : 'name',
		type : 'string'
	}, {
		name : 'value',
		type : 'string'
	}, {
		name : 'other',
		type : 'string'
	}]);
	var store = new Ext.data.SimpleStore({
		fields : ["name", "value", "other"],
		data : [['姓名', 'Wangwei', 'o1'], ['年龄', '30', 'o2']]
	});
	var cm = new Ext.grid.ColumnModel([{
		header : "项目",
		dataIndex : 'name',
		width : 100,
		editor : new Ext.form.TextField({
			allowBlank : false
		})
	}, {
		header : "值",
		dataIndex : 'value',
		width : 100,
		editor : new Ext.form.ComboBox({
			store : DBE.fieldDataTypesStore,
			valueField : "key",
			displayField : "key",
			typeAhead : false,
			triggerAction : 'all',
			emptyText : 'Select a state...',
			selectOnFocus : true,
			readonly : false,
			disableKeyFilter : true
		})
	}, {
		header : "其它",
		dataIndex : 'other',
		width : 100,
		editor : combo2
	}]);

	var grid = new Ext.grid.EditorGridPanel({
		store : store,
		cm : cm,
		renderTo : 'editor-grid',
		width : 600,
		height : 300,
		title : 'Test ComboBox',
		frame : true,
		clicksToEdit : 1,
		tbar : [{
			text : 'Add',
			handler : function() {
				var p = new dataRecord({
					name : 'New Plant 1',
					value : 'Mostly Shade',
					other : 'other..'
				});
				grid.stopEditing();
				store.insert(0, p);
				grid.startEditing(0, 0);
			}
		}]
	});
});