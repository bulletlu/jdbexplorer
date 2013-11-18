// 填充图片的本地引用
Ext.BLANK_IMAGE_URL = '../extjs/resources/images/default/s.gif';

// 剪贴板Flash的本地路径
Ext.CLIPBOARD_FLASH_URL = "../extjs/resources/_clipboard.swf";

// 文件上传Flash的路径
Ext.SWFUPLOADER_FLASH_URL = "../swfupload/swfupload_f9.swf";

Ext.Msg.info = function(config) {
	// create info box
	var createBox = function(title, message) {
		return [
				'<div class="msg">',
				'<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
				'<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>',
				title,
				'</h3>',
				message,
				'</div></div></div>',
				'<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
				'</div>'].join('');
	};

	// config params
	var cfg = {
		width : 220,
		height : 50,
		title : '提示信息',
		message : '提示信息内容',
		alignRef : Ext.getBody(),
		alignType : 't-t?'
	}
	config = Ext.applyIf(config || {}, cfg);

	// show~~
	var msgCt = Ext.get('msg-div');
	if (!msgCt) {
		msgCt = Ext.DomHelper.append(document.body, {
			id : 'msg-div'
		}, true);
	}
	var m = Ext.DomHelper.append(msgCt, {
		html : createBox(config.title, config.message)
	}, true);
	msgCt.setSize(config.width, config.height);
	msgCt.alignTo(config.alignRef, config.alignType);
	m.slideIn('t').pause(1).ghost("t", {
		remove : true
	});
}