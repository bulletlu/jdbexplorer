// Table 列信息Grid
DBE.TableColumnGrid = function(config) {
	// 数据类型的编辑器
	// 列信息
	var defValue = {
		align : 'center',
		menuDisabled : true,
		sortable : false
	};
	var c1 = Ext.applyIf({
		header : '名称',
		dataIndex : 'name',
		editor : new Ext.form.TextField({
			allowBlank : false,
			selectOnFocus : true
		})
	}, defValue);
	var c2 = Ext.applyIf({
		header : '类型',
		dataIndex : 'datatype',
		editor : new Ext.form.ComboBox({
			store : DBE.fieldDataTypesStore,
			valueField : "typeName",
			displayField : "typeName",
			typeAhead : true,
			editable : false,
			forceSelection : true,
			triggerAction : 'all',
			selectOnFocus : true
		})
	}, defValue);
	var c3 = Ext.applyIf({
		header : '长度',
		dataIndex : 'datalength',
		editor : new Ext.form.NumberField({
			selectOnFocus : true
		})
	}, defValue);
	var c4 = new Ext.ux.grid.CheckColumn(Ext.applyIf({
		header : '是否主键',
		dataIndex : 'ispk',
		width : 55
	}, defValue));
	var c5 = new Ext.ux.grid.CheckColumn(Ext.applyIf({
		header : '允许空值',
		dataIndex : 'cannull'
	}, defValue));
	var rn = new Ext.grid.RowNumberer();
	var cm = new Ext.grid.ColumnModel([rn, c1, c2, c3, c4, c5]);

	// 数据源信息
	var fields = [{
		// 名称
		name : 'name',
		type : 'string',
		defaultValue : 'newColumn'
	}, {
		// 数据类型
		name : 'datatype',
		type : 'string',
		defaultValue : 'VARCHAR'
	}, {
		// 数据长度
		name : 'datalength',
		type : 'int',
		defaultValue : 100
	}, {
		// 是否主键
		name : 'ispk',
		type : 'boolean',
		defaultValue : false
	}, {
		// 允许为空
		name : 'cannull',
		type : 'boolean',
		defaultValue : true
	}, {
		// 默认值
		name : 'datavalue',
		type : 'auto'
	}, {
		// 精度
		name : 'datascale',
		type : 'auto'
	}, {
		// 备注
		name : 'comment',
		type : 'string'
	}];
	var record = Ext.data.Record.create(fields);
	var data = [];
	if (config.isCreate) {
		data = [['newColumn1', 'VARCHAR', 100, false, true, '', '', '']];
	}
	var reader = new Ext.data.ArrayReader({}, record);
	var ds = new Ext.data.Store({
		proxy : new Ext.data.MemoryProxy(data),
		reader : reader
	});
	ds.load();

	// 工具栏定义
	var actAdd = new Ext.Action({
		text : '添加',
		iconCls : 'add',
		scope : this,
		handler : function() {
			//
			this.stopEditing();

			// 添加新字段
			var idx = ds.getCount();
			var colItem = new record({
				name : 'newColumn' + (idx + 1),
				datatype : 'VARCHAR',
				datalength : 100,
				ispk : false,
				cannull : true,
				datavalue : '',
				datascale : '',
				comment : ''
			});
			ds.add(colItem);

			// 选中当前新添加的行.
			this.getSelectionModel().selectLastRow();
			this.startEditing(idx, 1);
		}
	});
	var actRemove = new Ext.Action({
		text : '删除',
		iconCls : 'remove',
		scope : this,
		handler : function() {
			// alert('删除字段');
			var count = ds.getCount();
			if (count > 1) {
				var sm = this.getSelectionModel();
				var select = sm.getSelected();
				if (select) {
					this.stopEditing();

					if (sm.hasPrevious()) {
						sm.selectPrevious();
					} else if (sm.hasNext()) {
						sm.selectNext();
					}
					ds.remove(select);

					// 重写顺序索引
					for (i = 0; i < ds.getCount(); i++) {
						var idx = i + 1;
						this.getView().getCell(i, 0).firstChild.innerText = idx;
					}
				}
			} else {
				alert("删除列失败：您至少应该留一个列~。");
			}
		}
	});

	// 准备配置参数
	var cfg = {
		ds : ds,
		cm : cm,
		plain : true,
		frame : true,
		border : false,
		clicksToEdit : config.isCreate ? 1 : 2,
		plugins : [c4, c5],
		tbar : ['->', actAdd, actRemove],
		sm : new Ext.grid.RowSelectionModel({
			singleSelect : true
		}),
		viewConfig : {
			forceFit : true
		},
		listeners : {
			afteredit : function(eventObj) {
				var fieldName = eventObj.field;
				if (fieldName == "datatype") {
					// 取得当前列 类型，并保存到 record中
					var type = eventObj.record.get("datatype");
					for (var i = 0; i < DBE.fieldDataTypesStore.getCount(); i++) {
						var record = DBE.fieldDataTypesStore.getAt(i);
						if (record.data.typeName == type) {
							// alert('find type info of ' + typeInfo);
							var typeInfo = record.data;
							eventObj.record.typeInfo = typeInfo;
							if (!typeInfo.resetLength) {
								eventObj.record.set("datalength", "");
							} else {
								if (eventObj.record.get("datalength") == "") {
									eventObj.record.set("datalength", "100");
								}
							}
							break;
						}
					};
				} else if (fieldName == "datalength") {
					var col = eventObj.column + 1;
					var row = eventObj.row;
					var cm = eventObj.grid.getColumnModel();
					var typeInfo = eventObj.record.typeInfo;
					if (typeInfo && !typeInfo.resetLength) {
						eventObj.record.set("datalength", "");
					}
				}
			}
		}
	};
	config = Ext.applyIf(config || {}, cfg);

	// call父类构建器
	DBE.TableColumnGrid.superclass.constructor.call(this, config);

	// 公布属性
	this.columnRecord = record;
}
Ext.extend(DBE.TableColumnGrid, Ext.grid.EditorGridPanel, {
	/*
	 * 设置 初始列 数据编辑状态
	 */
	initColumnEditing : function() {
		// 设置grid进入编辑状态..
		this.getSelectionModel().selectFirstRow();
		this.startEditing(0, 1);
	},
	/*
	 * 设置初始的列数据
	 */
	resetColumnInfo : function(columns) {
		// 添加列信息
		Ext.each(columns, function(item, index) {
			var column = new this.columnRecord({
				name : item.name,
				datatype : item.typeName,
				datalength : item.size,
				ispk : item.pkColumn,
				cannull : item.nullable,
				datavalue : item.defaultValue,
				datascale : item.digits,
				comment : item.comment
			});
			this.getStore().add(column);
		}, this);

		// 选择首行..
		this.getSelectionModel().selectFirstRow();
	}
});