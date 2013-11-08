// 大字段显示窗口定义
DBE.CLobPanel = function(config, lobWindow) {
	// CLOB资源路径
	var filePath = false;
	if (config.name && !config.isNull) {
		// filePath = "../dbeAction/readfile.do?targetFile=";
		filePath = "../dbeAction/downfile.do?targetFile=";
		filePath += config.name;
	}
	// save Action
	var action = new Ext.Action({
		text : '保存',
		scope : this,
		disabled : true,
		handler : function() {
			// alert('save...');
			var clob = this.getTextare().getValue();
			if (clob != this.clobValue) {
				// alert('update..');
				var params = config.param;
				params.clob = clob;
				Ext.Ajax.request({
					url : '../dbeGridAction/updateclob.do',
					params : params,
					scope : this,
					success : function(response) {
						// alert('save ok~.');
						this.clobValue = clob;
						refreshSaveActionStat();
						// alert('保存成功~~..');
						Ext.Msg.info({
							message : "保存成功~!",
							alignRef : lobWindow.getId(),
							alignType : 'tl-tr?'
						});
					},
					failure : function(response) {
						var json = response.responseText;
						alert("保存CLOB出错：" + json);
					}
				});
			}
		}
	});
	var refreshSaveActionStat = (function() {
		var value = this.getTextare().getValue();
		// alert(value + "--> " + this.clobValue);
		action.setDisabled(value == this.clobValue);
	}).createDelegate(this);

	// textarea
	var textArea = new Ext.form.TextArea({
		id : 'textCLOB',
		disabled : true,
		value : 'Reading...',
		enableKeyEvents : true,
		listeners : {
			keypress : {
				scope : this,
				fn : function(textarea, objEvent) {
					refreshSaveActionStat.defer(100);
				}
			},
			change : function(textarea, newValue, oldValue) {
				action.setDisabled(false);
			}
		}
	});

	// 准备配置参数..
	var cfg = {
		layout : 'fit',
		width : 10,
		height : 10,
		plain : true,
		frame : false,
		border : false,
		items : textArea,
		keys : [{
			key : [Ext.EventObject.BACKSPACE, Ext.EventObject.DELETE],
			fn : function(key, eventObj) {
				refreshSaveActionStat.defer(100);
			}
		}],
		bbar : ['->', {
			text : '下载',
			handler : function() {
				if (!config.isNull) {
					if (filePath) {
						DBE.downfile(filePath);
					} else {
						alert("程序错误，无法下载：[filePath is null]");
					}
				} else {
					alert("当前字段为空，无法下载。");
				}
			}
		}, action]
	};
	// call父类构建器
	DBE.CLobPanel.superclass.constructor.call(this, cfg);

	// 公布属性
	this.filePath = filePath;// 资源文件路径
	this.clobValue = "";// 源clob内容..
}
Ext.extend(DBE.CLobPanel, Ext.Panel, {
	getTextare : function() {
		return this.items.get(0);
	},
	init : function() {
		// 设置窗口大小
		var size = Ext.getBody().getSize(true);
		size.width = size.width / 2;
		size.height = size.height / 2;
		this.setSize(size);

		// 读取CLOB数据
		var wait = Ext.Msg.wait('读取中...', '请稍等');
		var textareCLOB = this.getTextare();
		if (this.filePath) {
			var url = this.filePath;
			Ext.Ajax.request({
				url : url,
				scope : this,
				success : function(resp, opt) {
					var json = resp.responseText;
					this.clobValue = json;// 记录源内容
					if (textareCLOB) {
						textareCLOB.setValue(json);
						textareCLOB.setDisabled(false);
						textareCLOB.focus(false, 100);
					} else {
						alert("程序错误：[textare is null]~");
					}
					wait.hide();
				},
				failure : function(resp, opt) {
					var json = resp.responseText;
					wait.hide();
					alert("保存CLOB出错：" + json);
				}
			});
		} else {
			textareCLOB.setValue("");
			textareCLOB.setDisabled(false);
			textareCLOB.focus(false, 100);
			wait.hide();
		}
	}
});