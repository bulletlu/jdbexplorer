// DEB Action定义..
DBE.TableInfoPanelUtils = function(tableInfoPanel) {
	/**
	 * 创建基本信息面板
	 */
	this.createBaseInfoFieldSet = function(db, schema, table) {
		var fsBase = new Ext.form.FieldSet({
			autoHeight : true,
			autoShow : true,
			collapsed : true,
			collapsible : true,
			layout : 'column',
			title : '基本信息',
			defaults : {
				columnWidth : .30,
				layout : 'form',
				labelAlign : 'top'
			},
			items : [{
				items : [{
					xtype : 'textfield',
					msgTarget : 'side',
					id : table + '_fldDatabase',
					fieldLabel : '数据库名称',
					value : db,
					name : 'database',
					anchor : '95%',
					readOnly : true
				}]
			}, {
				items : {
					xtype : 'textfield',
					msgTarget : 'side',
					id : table + '_fldSchema',
					fieldLabel : '模式名称',
					value : schema,
					name : 'schema',
					anchor : '95%',
					readOnly : true
				}
			}, {
				items : {
					xtype : 'textfield',
					msgTarget : 'side',
					id : table + '_fldTableName',
					fieldLabel : '表名称',
					value : table,
					name : 'table',
					anchor : '92%',
					selectOnFocus : true,
					allowBlank : false
				}
			}]
		});

		// 增强fs
		fsBase.getTableName = function() {
			var fld = fsBase.findById(table + '_fldTableName');
			var tableName = fld.getValue();
			if (!tableName) {
				tableName = "";
			}
			return tableName;
		}
		return fsBase;
	}
};