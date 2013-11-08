// Table 列扩展信息Form 定义
DBE.TableColumnExtendForm = function(config) {
	// 准备配置参数
	var cfg = {
		plain : true,
		frame : true,
		border : false,
		labelWidth : 40,
		items : {
			xtype : 'tabpanel',
			activeTab : 0,
			defaults : {
				autoHeight : true,
				frame : true,
				border : false,
				bodyStyle : 'padding:10px'
			},
			items : [{
				title : '基本信息',
				layout : 'form',
				defaults : {
					xtype : 'textfield',
					selectOnFocus : true,
					msgTarget : 'side',
					readOnly : config.readOnly,
					anchor : '90%'
				},
				items : [{
					id : 'fldName',
					fieldLabel : '名称',
					name : 'name'
				}, {
					id : 'fldType',
					xtype : 'combo',
					fieldLabel : '类型',
					name : 'datatype',
					store : DBE.fieldDataTypesStore,
					valueField : "typeName",
					displayField : "typeName",
					selectOnFocus : true,
					typeAhead : false,
					triggerAction : 'all'
				}, {
					id : 'fldDataValue',
					fieldLabel : '默认值',
					name : 'datavalue'
				}, {
					id : 'fldLength',
					xtype : 'numberfield',
					fieldLabel : '长度',
					name : 'datalength'
				}, {
					id : 'fldScale',
					xtype : 'numberfield',
					fieldLabel : '精度',
					name : 'datascale'
				}, {
					id : 'fldComment',
					fieldLabel : '备注',
					height : 50,
					name : 'comment'
				}]
			}]
		}
	};
	config = Ext.applyIf(config || {}, cfg);

	// call父类构建器
	DBE.TableColumnExtendForm.superclass.constructor.call(this, config);
}
Ext.extend(DBE.TableColumnExtendForm, Ext.form.FormPanel, {
	/**
	 * 取得所有输入字段
	 */
	getFields : function() {
		var tabPanel = this.items.get(0);
		var fldsPanel = tabPanel.items.get(0);
		return fldsPanel.items;
	}
})