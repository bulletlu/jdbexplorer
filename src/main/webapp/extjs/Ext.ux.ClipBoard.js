//剪贴板Flash的本地路径
Ext.CLIPBOARD_FLASH_URL = "../extjs/resources/_clipboard.swf";

/**
 * @class Ext.ux.clipBoard
 * @extends Ext.util.Observable
 * @version 0.1 Simulates systems clipboard behaviors. However the data is
 *          stored via System Clipboard, Any data will be serialized as
 *          String(Todo). FireFox fails to get the clipbord data dut to 'Denied
 *          Permission'.More details at:
 *          http://developer.mozilla.org/en/docs/Using_the_Clipboard
 * @param {Mixed}
 *            data
 * @param {Boolean}
 *            copyORcut True if this action is 'CUT', othetwise "COPY".Default
 *            action is "COPY".
 */
Ext.namespace("Ext.ux");
Ext.ux.ClipBoard = function(data, copyORcut) {
	// 'copyORcut' could be boolean(true/fale),undefiend.
	if (copyORcut) {
		this.deleteDataAfterPaste = !!copyORcut;
	}

	// 事件..
	this.addEvents({
		"copy" : true,
		"cut" : true,
		"paste" : true,
		"beforepaste" : true
	});

	// this.clipBoardDataHolder = Ext.DomHelper.append(document.body, {
	// tag : 'textarea',
	// style : 'width:1;height:1'
	// }, true).dom;
	
	this.setData(data);
}
Ext.extend(Ext.ux.ClipBoard, Ext.util.Observable, {
	/**
	 * @private
	 * @properties
	 * @type {Boolean} deleteDataAfterPaste True if this action is 'CUT',
	 *       othetwise "COPY".Default action is "COPY".
	 */
	deleteDataAfterPaste : false,
	/**
	 * @type {Boolean} clipBoardHasData True if ClipBoard Has Data.
	 */
	clipBoardHasData : true,

	setData : function(data) {
		if (!this.deleteDataAfterPaste) {
			if (!this.fireEvent('copy', data)) {
				return false;
			}
		} else {
			if (!this.fireEvent('cut', data)) {
				return false;
			}
		}

		var tempSwf = Ext.DomHelper.append(document.body, {
			tag : 'embed',
			src : Ext.CLIPBOARD_FLASH_URL,
			FlashVars : "clipboard=" + encodeURIComponent(data),
			width : 0,
			height : 0,
			bgcolor : "#FF6600",
			type : 'application/x-shockwave-flash'
		}, true);

		var funRemoveSwf = function() {
			tempSwf.remove();
		};
		funRemoveSwf.defer(10000);
		return true;
	}
});