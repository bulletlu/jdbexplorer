// Table 列信息面板
DBE.TableColumnPanel = function(config) {
	// create grid and form and link
	var grid = new DBE.TableColumnGrid({
		isCreate : config.isCreate,
		readOnly : config.readOnly
	});
	grid.columnWidth = .7;
	var form = new DBE.TableColumnExtendForm({
		isCreate : config.isCreate,
		readOnly : config.readOnly
	});
	form.columnWidth = .3;

	var cfg = {
		plain : true,
		frame : false,
		border : false,
		layout : 'column',
		items : [grid, form]
	};
	config = Ext.applyIf(config || {}, cfg);

	// call
	DBE.TableColumnPanel.superclass.constructor.call(this, config);

	// 注册事件,同步Grid 与 Form之间的值.
	var funFieldOnChangeHandler = function(fld, newVal, oldVal, silent) {
		var record = grid.getSelectionModel().getSelected();
		var name = fld ? fld.getName() : 'datatype';
		if (!silent) {
			// 修改 gird数据..
			record.data[name] = newVal;
			record.commit();

			// 触发 grid.afteredit事件
			var store = grid.getStore();
			var rowIdx = grid.getStore().indexOfId(record.id);
			var columnIdx = grid.getColumnModel().findColumnIndex(name);
			if (rowIdx != -1 && columnIdx != -1) {
				grid.fireEvent('afteredit', {
					grid : grid,
					record : record,
					field : name,
					value : newVal,
					originalValue : oldVal,
					row : rowIdx,
					column : columnIdx
				});
			}
		}

		// 如果是数据类型字段 改变，则重设长度和精度状态
		if (silent || (name == "datatype")) {
			var typeInfo = record.typeInfo;
			var fldLength = form.getForm().findField("fldLength");
			var fldScale = form.getForm().findField("fldScale");
			if (record.typeInfo) {
				if (fldLength && fldLength.setDisabled) {
					fldLength.setDisabled(!typeInfo.resetLength);
				} else {
					fldLength.setDisabled(false);
				}
				if (fldScale && fldScale.setDisabled) {
					fldScale.setDisabled(!typeInfo.resetScale);
				} else {
					fldScale.setDisabled(false);
				}
			} else {
				fldLength.setDisabled(false);
				fldScale.setDisabled(false);
			}
		}
	};
	grid.on("afteredit", function(eventObj) {
		var record = eventObj.record;
		if (record && record.data) {
			form.getForm().loadRecord(record);
			var fld = form.getForm().findField(eventObj.field);
			var newVal = eventObj.value;
			var oldVal = eventObj.originalValue;
			funFieldOnChangeHandler(fld, newVal, oldVal, true);
		}
	});
	grid.getSelectionModel().on("selectionchange", function(sm) {
		var record = sm.getSelected();
		if (record && record.data) {
			form.getForm().loadRecord(record);
			funFieldOnChangeHandler(null, null, null, true);
		}
	});
	form.getFields().each(function(item) {
		item.on('change', funFieldOnChangeHandler);
	});

	// 用于生成SQL语句的工具函数..
	var funCreateTableSQL = function(schema, table) {
		var store = grid.getStore();
		// 读取数据源生成SQL
		var sql = "";
		var pkFields = "";
		store.findBy(function(record, id) {
			var data = record.data;
			if (sql.length > 0) {
				sql += ",";
			}
			// 名称
			sql += data.name;
			// 类型
			sql += " " + data.datatype;
			if (data.datalength != "") {
				// 长度
				sql += "(" + data.datalength;
				// 精度
				if (data.datascale != "") {
					sql += "," + data.datascale;
				}
				sql += ")"
			}
			// 默认值
			if (data.datavalue != "") {
				sql += " default " + data.datavalue;
			}
			// 是否可以为空
			if (!data.cannull) {
				sql += " " + "not null";
			}
			// 是否主键
			if (data.ispk) {
				if (pkFields.length > 0) {
					pkFields += ",";
				}
				pkFields += data.name;
			}
		});
		// 主键约束
		if (pkFields.length > 0) {
			sql += ",constraint PK_" + table + " primary key (";
			sql += pkFields + ")"
		}
		// 表名称
		sql = "create table " + schema + "." + table + "(" + sql + ")";
		return sql;
	}
	var funModifyTableSQL = function(schema, table) {
		var store = grid.getStore();
		return "sql:no sql~!+:" + schema + "_" + table;
	}

	// 公布属性
	this.columnGrid = grid;
	this.sqlMaker = {
		build : function(schema, table) {
			if (config.isCreate) {
				return funCreateTableSQL(schema, table)
			} else {
				return funModifyTableSQL(schema, table);
			}
		}
	};
}
Ext.extend(DBE.TableColumnPanel, Ext.Panel, {
	/*
	 * 调整控件大小
	 */
	resetSize : function(size) {
		// alert(Ext.encode(size));
		this.setSize(size);
		var itemHeight = size.height - 30;
		this.items.each(function(item, idx, length) {
			item.setHeight(itemHeight);
		});

		//
		if (this.initialConfig.isCreate) {
			// 如果是新建表，自动进入编辑状态
			this.columnGrid.initColumnEditing();
		}
	},
	/*
	 * 设置初始的列数据
	 */
	setColumnInfo : function(columns) {
		this.columnGrid.resetColumnInfo(columns);
	},
	/*
	 * 检查列信息是否有被改变..
	 */
	checkChanges : function() {
		var rs = this.columnGrid.getStore().getModifiedRecords();
		return rs && rs.length > 0
	}
});