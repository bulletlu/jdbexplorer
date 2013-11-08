// 创建DynamicGrid时的一些工具方法
DBE.DynamicGridUtils = {
	/**
	 * 为TableInfo对象添加方法
	 * 
	 * @param tableInfo
	 */
	boostupTableInfo : function(tableInfo) {
		// 修正readonly标准
		if (tableInfo.readOnly == undefined) {
			tableInfo.readOnly = true;
		}
		// 添加通过名称取得列信息的方法
		tableInfo.getColumnInfoByName = function(name) {
			for (var i = 0; i < tableInfo.columns.length; i++) {
				var col = tableInfo.columns[i];
				if (col.name == name) {
					return col;
				}
			}
			alert("tableInfo.getColumnInfoByName出错，列名称无效：" + name);
		}
	},
	/**
	 * 通过tableInfo创建列模型，
	 * 
	 * @param tableInfo
	 * @param initColumns：初始的列信息
	 */
	createColumnModel : function(tableInfo, initColumns) {
		var columnInfos = tableInfo.columns;
		// 生成列 对象信息
		Ext.each(columnInfos, function(column) {
			// 列是否隐藏，
			var hidden = false;
			//撤销了将主键隐藏的逻辑 by cnetwei
			//if (tableInfo.pkColumnName && column.name == tableInfo.pkColumnName) {
			//	hidden = true;// 默认主键列隐藏,
			//}
			// 列类型
			var type = 'auto';
			if (column.extType && column.extType.type) {
				type = column.extType.type;
			}
			// 列是否可排序
			var sortable = false;
			if (column.extType && column.extType.sortable != undefined) {
				sortable = column.extType.sortable;
			}
			// 列对象
			var columnObj = {
				header : column.name,
				dataIndex : column.name,
				hidden : hidden,
				sortable : sortable,
				type : type
			};
			// 添加渲染时日期类型的转换器
			if (column.extType && column.extType.dateType) {
				var format = column.extType.dateFormat;
				columnObj.renderer = Ext.util.Format.dateRenderer(format);
			}
			// 创建列Editor
			if (!tableInfo.readOnly) {
				// table不是只读，设置列编辑对象 Editor
				DBE.DynamicGridUtils.createColumnEditor(column, columnObj);
			}
			// append to columnObj arrays..
			initColumns = initColumns.concat(columnObj);
		});

		// 构建列模型
		return new Ext.grid.ColumnModel(initColumns);
	},
	/**
	 * 根据列信息（colInfo），为列对象（colObj）创建Editor
	 */
	createColumnEditor : function(colInfo, colObj) {
		if (colInfo.extType && !colInfo.extType.longType) {
			// 列类型不是长类型时才设置编辑对象
			if (colInfo.extType.dateType) {
				colObj.editor = new Ext.form.DateField({
					selectOnFocus : true,
					allowBlank : colInfo.nullable,
					format : colInfo.extType.dateFormat
				});
			} else if (colInfo.extType.booleanType) {
				colObj.editor = new Ext.form.ComboBox({
					store : DBE.booleanComboItemStore,
					valueField : "value",
					displayField : "name",
					mode : 'local',
					blankText : '请选择',
					emptyText : '请选择',
					editable : false,
					triggerAction : 'all',
					allowBlank : colInfo.nullable
				});
			} else if (colInfo.fkColumn) {
				// alert("创建外键列修改 Editor....");
				colObj.editor = new Ext.form.ComboBox({
					loadingText : 'Loading...',
					store : new Ext.data.JsonStore({
						url : '../dbeGridAction/fkvalue.do',
						root : 'fkvalues',
						baseParams : {
							table : colInfo.fkInfo.table,
							field : colInfo.fkInfo.column
						},
						fields : ['key', 'value']
					}),
					valueField : 'value',
					displayField : 'key',
					typeAhead : true,
					triggerAction : 'all',
					selectOnFocus : true,
					editable : false,
					forceSelection : true,
					allowBlank : colInfo.nullable
				});
			} else {
				// alert('默认的文本editor');
				colObj.editor = new Ext.form.TextField({
					selectOnFocus : true,
					allowBlank : colInfo.nullable
				});
			}
		}
	}
};