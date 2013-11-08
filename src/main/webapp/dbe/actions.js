// DEB Action定义..
DBE.Actions = function(mainWindow) {
	// 退出系统..
	this.exit = function() {
		if (confirm('您确认要退出系统~?')) {
			var html = [
					'<div id="divByebye" style="width:320px;height:220px;">',
					'<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
					'<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>',
					'Bye Bye~',
					'</h3>',
					'<a href="javascript:window.location.reload();">重新登入</a>',
					'</div></div></div>',
					'<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
					'</div>'].join('');

			Ext.Ajax.request({
				url : '../initAction/logout.do',
				success : function(response) {
					// var json = response.responseText;
					// alert("退出程序成功：" + json);

					Ext.get(Ext.getBody()).update(html);
					var div = Ext.get("divByebye");
					if (div) {
						div.center();
					}
				},
				failure : function(response) {
					// var json = response.responseText;
					// alert('退出程序失败:' + json);
					Ext.get(Ext.getBody()).update(html);
					var div = Ext.get("divByebye");
					if (div) {
						div.center();
					}
				}
			});
		}
	}

	// 系统配置
	this.config = function() {
		// alert('System config~：' + DBE.SysConfigWindow);
		var config = new DBE.SysConfigWindow();
		config.showToCenter(mainWindow);
	}

	// Actions
	this.exitAction = new Ext.Action({
		text : '退出系统',
		qtip : '退出系统',
		iconCls : 'exit',
		scope : this,
		handler : this.exit
	});
	this.configAction = new Ext.Action({
		text : '系统配置',
		iconCls : 'config',
		scope : this,
		qtip : '系统配置',
		handler : this.config
	});
};
