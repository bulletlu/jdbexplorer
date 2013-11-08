// DEB 全局空间定义
DBE = function() {
	return {
		init : function() {
		},
		/**
		 * 从指定url处下载文件..，dbeIFrame 申明在main.html中；
		 */
		downfile : function(url) {
			// window.open(dURL);
			if (url && url.length > 0) {
				var iframe = Ext.get("dbeIFrame");
				// alert(iframe);
				// alert(iframe.dom.src);
				iframe.dom.src = url;
			} else {
				alert("无法下载：URL为空~~!");
			}
		},
		/**
		 * 用于boolean类型的可选值
		 */
		booleanComboItemStore : new Ext.data.SimpleStore({
			fields : ["name", "value"],
			data : [['true', 'true'], ['false', 'false']]
		}),
		/**
		 * 字段数据类型的可选值
		 */
		fieldDataTypesStore : new Ext.data.JsonStore({
			url : '../dbeTreeAction/datatypes.do',
			root : 'types',
			fields : ['typeName', 'resetLength', 'resetScale']
		}),
		/**
		 * 为Ajax控件添加事件，以处理login已失效的情况
		 */
		initAjaxListeners : function() {
			// 初始化 Ajax事件监听器..
			return;// TODO:暂时因为无法处理 alert窗口的问题，先关闭该功能。
			Ext.Ajax.on("requestcomplete", function(conn, response, opt) {
				var json = response.responseText;
				alert("@@@:" + json);
				if (json.charAt(0) == "{") {
					var rst = eval("(" + json + ")");
					if (rst.notLogin) {
						var msg = "当前登录状态已无效，需要重新登录，是否现在重新登录~?";
						if (confirm(msg)) {
							window.location.reload(true);
						}
					}
				}
			});
		},
		copyToClipboard : function(value) {
			// copy value 到剪贴板
			new Ext.ux.ClipBoard(value);
			return true;
		},
		getTableInfo : function(node, callback) {
			// 检查回调函数，并运行...
			var runCallback = function(ti) {
				if (callback) {
					// 呼叫回调函数，传出结构信息 tableinfo
					callback(ti);
				} else {
					alert("回调函数 callback 无效~~!");
				}
			}

			// 读取数据库结构信息~~...
			Ext.Ajax.request({
				url : '../dbeTreeAction/open.do',
				params : {
					node : node.text,
					path : node.getPath('text')
				},
				success : function(response) {
					var json = response.responseText;
					// alert("请求[" + node.text + "]结构完成：" + json);
					var rst = eval("(" + json + ")");
					var tableinfo = false;
					if (!rst.succeed && !rst.table) {
						var msg = rst.msg ? rst.msg : Ext.encode(rst);
						alert('打开[' + node.text + ']失败:' + msg);
					} else {
						tableinfo = rst.table;
					}
					runCallback(tableinfo);
				},
				failure : function(response) {
					var json = response.responseText;
					alert("请求[" + node.text + "]结构出错：" + json);
					runCallback(false);
				}
			});
		}
	};
}();
