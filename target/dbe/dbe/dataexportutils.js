// 数据导出窗口工具集 定义
DBE.DataExportUtils = function() {
	/**
	 * create 导出字段列表选择的FromSet
	 */
	this.createExportFieldsFromSet = function(tableinfo) {
		var fields1 = [];
		var fields2 = [];
		var fields3 = [];
		var fields4 = [];
		var i=0;
		Ext.each(tableinfo.columns, function(column) {
			var field ={
					xtype : 'checkbox',
					fieldLabel : '',
					hideLabel : true,
					labelWidth : 0,
					labelSeparator : '',
					id : 'fld' + column.name,
					inputValue : column.name,
					checked : true,
					boxLabel : column.name,
					name : 'fields',
					anchor : '100%'
				};
				//alert(i+'  '+i%4);
			if(i%4==0) fields1 = fields1.concat(field);
			if(i%4==1) fields2 = fields2.concat(field);
			if(i%4==2) fields3 = fields3.concat(field);
			if(i%4==3) fields4 = fields4.concat(field);
			i++;
		});
	
		var formset = new Ext.form.FieldSet({
			title : '导出字段',
			width:600,
			height:100,
			autoHeight : false,
			items : [{
				layout :'column',
				items:[{				
					columnWidth : .25,
					layout : 'form',
					items : fields1
				},{				
					columnWidth : .25,
					layout : 'form',
					items : fields2
				},{				
					columnWidth : .25,
					layout : 'form',
					items : fields3
				},{				
					columnWidth : .25,
					layout : 'form',
					items : fields4
				}]
			}]
		});
		//alert('i='+i);
		var h = Math.ceil(i/4)+1;
		//alert('i='+i+'  h='+h);
		formset.setHeight(h*27);
		return formset;
	}

	/**
	 * 创建导出范围的选择 fromset
	 */
	this.createExportRangeFromSet = function(currentPageNo, totalPageNo) {
		// 申明范围选择控件
		var chkCurrentPage = null;// 当前页;
		var chkAll = null;// 全部
		var chkRangePages = null;// 页码范围
		var fldStartPageNo = null;// 开始页码值
		var fldEndPageNo = null;// 结束页码值

		// 复选框共享的配置选项
		var chkDefaultsConfig = {
			fieldLabel : '',
			hideLabel : true,
			labelWidth : 0,
			labelSeparator : '',
			checked : false,
			name : 'rangeType',
			anchor : '98%',
			listeners : {
				check : function(radio, checked) {
					// 重置页码范围输入框的状态
					fldStartPageNo.disable()
					fldEndPageNo.disable();

					if (chkCurrentPage.checked) {
						fldStartPageNo.setValue(currentPageNo);
						fldEndPageNo.setValue(currentPageNo);
					} else if (chkAll.checked) {
						fldStartPageNo.setValue(1);
						fldEndPageNo.setValue(totalPageNo);
					} else if (chkRangePages.checked) {
						fldStartPageNo.enable();
						fldEndPageNo.enable();
						fldStartPageNo.focus(true, true);
					}

					if (!fldStartPageNo.disabled) {
						fldStartPageNo.fireEvent('change', fldStartPageNo,fldStartPageNo.getValue());
					}
				}
			}
		};

		// 复选框.全部
		var cfg = Ext.applyIf({
			boxLabel : '全部',
			inputValue : 'ALL'
		}, chkDefaultsConfig);
		chkAll = new Ext.form.Radio(cfg);
		// 复选框.当前页
		cfg = Ext.applyIf({
			boxLabel : '当前页',
			checked : true,
			inputValue : 'CURRENT'
		}, chkDefaultsConfig);
		chkCurrentPage = new Ext.form.Radio(cfg);
		// 复选框.页码范围
		cfg = Ext.applyIf({
			boxLabel : '页码范围',
			inputValue : 'RANGE'
		}, chkDefaultsConfig);
		chkRangePages = new Ext.form.Radio(cfg);

		// 输入框.起始页码
		cfg = Ext.applyIf({
			id : 'fldStartPageNo',
			width : 25,
			name : 'startPageNo',
			disabled : true,
			minValue : 1,
			maxValue : totalPageNo,
			anchor : '100%',
			listeners : {
				change : function(field, newValue, oldValue) {
					if (field == fldStartPageNo) {
						// alert("重置最小值.....:" + newValue);
						fldEndPageNo.minValue = newValue;
					}
				}
			}
		}, chkDefaultsConfig);
		fldStartPageNo = new Ext.form.NumberField(cfg);
		// 输入框.终止页码
		cfg = Ext.applyIf({
			id : 'fldEndPageNo',
			name : 'endPageNo'
		}, cfg);
		fldEndPageNo = new Ext.form.NumberField(cfg);
		// return formset结果
		var formset = new Ext.form.FieldSet({
			title : '导出范围',
			autoHeight : false,
			width:600,
			height:110,
			items : [{
				layout : 'column',
				items : [{
					columnWidth : 1,
					layout : 'form',
					items : chkCurrentPage
				}, {
					columnWidth : 1,
					layout : 'form',
					items : chkAll
				}, {
					columnWidth : .20,
					layout : 'form',
					items : chkRangePages
				}, {
					columnWidth : .25,
					layout : 'form',
					items : fldStartPageNo
				}, {
					columnWidth : .03,
					layout : 'form',
					items : {
						xtype : 'label',
						text : ' —',
						disabled : true,
						anchor : '100%'
					}
				}, {
					columnWidth : .25,
					layout : 'form',
					items : fldEndPageNo
				}]
			}]
		});
		fldStartPageNo.setValue(currentPageNo);
		fldEndPageNo.setValue(currentPageNo);
		return formset;
	}
	/**
	 * create 导出内容格式
	 */
	this.createExportFormatFormSet = function() {
		var defaultRadioConfig = {
			xtype : 'radio',
			fieldLabel : '',
			hideLabel : true,
			labelWidth : 0,
			labelSeparator : '',
			checked : false,
			name : 'formatType',
			anchor : '98%'
		};

		return new Ext.form.FieldSet({
			title : '导出内容格式',
			autoHeight : false,
			width:600,
			height: 60,
			items : {
				layout : 'column',
				defaults : {
					columnWidth : .25,
					layout : 'form'
				},
				items : [{
					items : Ext.applyIf({
						checked : true,
						boxLabel : '.html',
						inputValue : 'HTML'
					}, defaultRadioConfig)
				}, {
					items : Ext.applyIf({
						boxLabel : '.pdf',
						inputValue : 'PDF'
					}, defaultRadioConfig)
				}, {
					items : Ext.applyIf({
						boxLabel : '.sql',
						inputValue : 'SQL'
					}, defaultRadioConfig)
				}, {
					items : Ext.applyIf({
						boxLabel : '.csv',
						inputValue : 'CSV'
					}, defaultRadioConfig)
				}]
			}
		});
	}

}