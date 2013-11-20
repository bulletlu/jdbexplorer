// 大字段显示窗口定义
DBE.BLobPanel = function(config, lobWindow) {
	// BLOB资源路径
	var filePath = false;
	if (config.name) {
		filePath = "../dbeAction/downfile.do?targetFile=";
		filePath += config.name;
	}

	// 资源信息
	var obj = {};
	obj.type = config.type == "BLOB" ? "BINARY" : "TEXT";
	obj.format = config.type == "BLOB" ? "未知" : "文本";
	obj.format = config.isImage ? "图片" : obj.format;
	obj.format = config.isHtml ? "HTML" : obj.format;
	obj.format = config.isNull ? "NULL" : obj.format;
	obj.imgsrc = "../dbe/images/unkonwType.png";
	obj.imgsrc = config.isNull ? "../dbe/images/field_null.png" : obj.imgsrc;
	obj.imgsrc = config.isImage ? filePath : obj.imgsrc;

	// 生成资源显示html
	var imageTPL = new Ext.XTemplate(
			// '<table border="0" width="100%"><tr><td valign="middle">',
			'<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
			'<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc">',
			'<img id="imgBLOB" src="{imgsrc}"/><br/><br/><b>{type}</b>数据，格式<b>{format}</b>。',
			'</div></div></div><div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>'
			// ',</td></tr></table>'
	);
	var html = imageTPL.apply(obj);

	// 按钮定义
	var btnDownload = {
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
	};

	// 准备配置参数
	var cfg = {
		plain : true,
		frame : false,
		border : false,
		html : html,
		bbar : ['->', btnDownload, {
			text : '修改',
			scope : this,
			handler : function() {
				// alert("上传新文件，替换当前内容..");
				var blobPanel = this;
				Ext.ux.SwfUploader.upload({
					upload_url : '../dbeGridAction/updateblob.do',
					post_params : config.param,
					file_types : {
						type : '*.*',
						desc : '所有文件'
					},
					callback : {
						uploadComplete : function(file) {
							// tip
							Ext.Msg.info({
								message : file.name + " 上传完成~!",
								alignRef : lobWindow.getId(),
								alignType : 'tl-tr?'
							});

							// TODO:根据文件后缀名称 判断是否是图片，以决定src的正确值..
							//
							// 修改图片显示
							var src = "../dbeAction/downfile.do?targetFile=";
							src += file.name;
							blobPanel.resetImagePath(src);
						}
					}
				});
			}
		}]
	};
	// call 父类构建器
	DBE.BLobPanel.superclass.constructor.call(this, cfg);
}
Ext.extend(DBE.BLobPanel, Ext.Panel, {
	resetImagePath : function(path) {
		var image = this.getImageElement();
		image.dom.src = path;
	},
	getImageElement : function() {
		var ele = this.getEl();
		ele = ele.select('img', true);
		ele = ele.last();
		return ele
	},
	init : function() {
		// 取得图片元素
		var eleImage = this.getImageElement();// Ext.get("imgBLOB");
		var width = 220, height = 180;
		if (eleImage) {
			// 图片的元素宽，高
			height = eleImage.getHeight();
			width = eleImage.getWidth();
			// alert("image.old(w*h):" + width + " * " + height);

			// 图片有效的宽，高
			var size = Ext.getBody().getSize(true);
			var maxHeight = size.height / 2, minHeight = 50;
			var maxWidth = size.width / 2, minWidth = 120;
			// 修正图片的宽和高
			height = height > maxHeight ? maxHeight : height;
			height = height < minHeight ? minHeight : height;
			width = width > maxWidth ? maxWidth : width;
			width = width < minWidth ? minWidth : width;
			eleImage.setHeight(height);
			eleImage.setWidth(width);
			// alert("image.new(w*h):" + width + " * " + height);
		}
		this.setHeight(height);
		this.setWidth(width);
		// alert("panel.size:" + Ext.encode(this.getSize()));
	}
});