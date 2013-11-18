// 数据导出窗口定义
DBE.DataExport = function(config) {
	// 从配置文件中取得信息
	var tableinfo = config.tableinfo;
	var paging = config.pagingInfo;
	var baseParams = config.baseParams;

	// create 导出字段列表
	var utils = new DBE.DataExportUtils();
	var fsExportFields = utils.createExportFieldsFromSet(tableinfo);
	// create 范围选择控件
	var fsExportRange = utils.createExportRangeFromSet(paging.pageNo,
			paging.pageTotal);
	// create 导出内容格式
	var fsExportFormat = utils.createExportFormatFormSet();
	// 导出信息收集面板
	var dataExportForm = new Ext.form.FormPanel({
		labelWidth : 0,
		title : '数据导出',
		frame : true,
		buttonAlign : 'right',
		buttons : [{
			text : '确定',
			scope : this,
			handler : function() {
				// alert('next:' + dataExportForm.getForm().getValues(true));
				var form = dataExportForm.getForm();
				if (form.isValid()) {
					form.findField('fldStartPageNo').enable();
					form.findField('fldEndPageNo').enable();
					form.doAction('submit', {
						url : '../dbeGridAction/export.do',
						waitTitle : '请稍等',
						waitMsg : '数据读取中……',
						params : baseParams,
						scope : this,
						success : function(form, action) {
							// alert("数据导出成功~~:" + action.result.file);
							var dURL = "../dbeAction/downfile.do?targetFile="
									+ action.result.file;
							DBE.downfile(dURL);
							this.close();
						},
						failure : function(form, action) {
							alert("数据导出失败~~:" + action.result.msg);
							// this.close();
						}
					});
				} else {
					Ext.Msg.info({
						message : '数据填写无效，请检查~~!',
						alignRef : dataExportForm.getId(),
						alignType : 'tl-tr?'
					});
				}
			}
		}, {
			text : '取消',
			scope : this,
			handler : function() {
				this.close();
			}
		}],
		items : [fsExportFields, fsExportRange, fsExportFormat]
	});

	// 准备配置参数
	var cfg = {
		id : 'dataExportWindow',
		layout : 'fit',
		width : 460,
		height : 550,
		plain : true,
		frame : false,
		border : false,
		modal : true,
		draggable : false,
		resizable : false,
		autoScroll : true,
		closable : false,
		// closeAction : 'hide',
		items : dataExportForm
	};
	config = Ext.applyIf(config || {}, cfg);

	// call superclass
	DBE.DataExport.superclass.constructor.call(this, config);
}
Ext.extend(DBE.DataExport, Ext.Window, {
	/**
	 * 重新计算高度
	 */
	reCalculateHeight : function() {
		var fromPanel = this.items.get(0);
		var realHeight = 100;// 初始值 是100
		fromPanel.items.each(function(item, idx, length) {
			// alert(item.getSize().height);
			realHeight += item.getSize().height;
		});
		return realHeight;
	}
});