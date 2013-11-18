// 登录窗口定义
DBE.LoginWindow = function(config) {
	var cm = new Ext.grid.ColumnModel([{
		header : '登陆历史',
		resizable : false,
		menuDisabled : true,
		dataIndex : 'history',
		width : '380'
	}]);

	var jsonds = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../initAction/history.do'
		}),
		reader : new Ext.data.JsonReader({
			root : 'root'
		}, [{
			name : 'history'
		},])
	});

	var _grid = new Ext.grid.GridPanel({
		ds : jsonds,
		cm : cm,
		scope : this,
		height : 100,
		listeners : {
			celldblclick : function() {
				// 双击历史纪录时自动填充表单
				var _record = this.getSelectionModel().getSelected();
				var history = _record.data['history'];
				if (history != '没有历史登陆记录') {
					his = new Array();
					his = history.split('|');
					var form = loginForm.getForm();
					var fldDbtypes = form.findField('fldDbtype');
					var fldDbhost = form.findField('fldDbhost');
					var fldDbname = form.findField('fldDbname');
					var fldUsername = form.findField('fldUsername');
					var fldURL = form.findField('fldDburl');
					fldDbtypes.setValue(his[0]);
					fldDbhost.setValue(his[1]);
					fldDbname.setValue(his[2]);
					fldUsername.setValue(his[3]);
					fldURL.setValue(his[4]);
					var fld = form.findField('fldPassword');
					fld.focus();
				}

				Ext.QuickTips.init();
				var tips = null;
				if (!tips) {
					tips = new Ext.ToolTip({
						target : 'fldPassword',
						html : '请输入数据库密码！',
						title : '提示：'
					});
				}

			}
		}

	});
	jsonds.load();

	var loginForm = new Ext.form.FormPanel({
		id : 'loginWindowForm',
		title : '请登录...',
		frame : true,
		labelWidth : 70,
		buttonAlign : 'right',
		reader : new Ext.data.JsonReader({
			success : 'success',
			root : 'data'
		}, [{
			name : 'dbtype'
		}, {
			name : 'dbhost'
		}, {
			name : 'dbname'
		}, {
			name : 'user'
		}, {
			name : 'password'
		}, {
			name : 'url'
		}]),
		items : [{
			xtype : 'fieldset',
			title : '',
			autoHeight : true,
			defaults : {
				width : 270,
				msgTarget : 'side',
				xtype : 'textfield',
				selectOnFocus : true,
				listeners : {
					change : {
						scope : this,
						fn : function(field, newValue, oldValue) {
							// 重写生成URL
							var form = this.loginForm.getForm();
							var fldURL = form.findField('fldDburl');
							var dbURL = fldURL.getValue();
							var vals = form.getValues();
							var fieldname = field.name;
							if (fieldname == "dbtype") {
								if (vals["dbtype"] != "选择数据库类型") {
									var fldDBType = form.findField('fldDbtype');
									dbURL = fldDBType.getValue();
								}
								var regex = /<server>/ig;
								dbURL = dbURL.replace(regex, vals["dbhost"]);
								regex = /<dbname>/ig;
								dbURL = dbURL.replace(regex, vals["dbname"]);
								fldURL.setValue(dbURL);
							} else {
								if (fieldname != "password") {
									dbURL = dbURL.replace(oldValue, newValue);
								}
								fldURL.setValue(dbURL);
							}
						}
					}
				}

			},
			items : [{
				id : 'fldDbtype',
				xtype : 'combo',
				fieldLabel : '     数据库类型',
				loadingText : 'Loading...',
				store : new Ext.data.JsonStore({
					url : '../initAction/dbtypes.do',
					root : 'types',
					fields : ['key', 'value']
				}),
				valueField : 'value',
				displayField : 'key',
				typeAhead : true,
				triggerAction : 'all',
				emptyText : '选择数据库类型',
				selectOnFocus : true,
				allowBlank : false,
				editable : false,
				forceSelection : true,
				name : 'dbtype'
			}, {
				id : 'fldDbhost',
				fieldLabel : '     服务器地址',
				allowBlank : false,
				value : 'localhost',
				name : 'dbhost'
			}, {
				id : 'fldDbname',
				fieldLabel : '     数据库名称',
				allowBlank : false,
				value : 'dbname',
				name : 'dbname'
			}, {
				id : 'fldUsername',
				fieldLabel : '     用户',
				allowBlank : false,
				value : 'user',
				name : 'user'
			}, {
				id : 'fldPassword',
				fieldLabel : '     密码',
				inputType : 'password',
				name : 'password'
			}, {
				id : 'fldDburl',
				fieldLabel : '     URL',
				allowBlank : false,
				readOnly : true,
				value : 'jdbc:driver:type://<server>',
				name : 'url'
			}]
		}, _grid],
		buttons : [{
			text : '确定',
			scope : this,
			handler : function() {
				this.submit();
			}
		}],
		keys : [{
			key : [10, 13],
			scope : this,
			fn : function() {
				this.submit();
			}
		}]
	});

	// 准备配置参数
	var cfg = {
		id : 'loginWindow',
		layout : 'fit',
		width : 400,
		height : 360,
		plain : true,
		frame : false,
		border : false,
		modal : true,
		draggable : false,
		resizable : false,
		autoScroll : true,
		closable : false,
		closeAction : 'hide',
		items : loginForm
	};
	config = Ext.applyIf(config || {}, cfg);

	// call superclass
	DBE.LoginWindow.superclass.constructor.call(this, config);

	// 公布属性
	this.loginForm = loginForm;
	this.successCallback = false;// 登录成功后的回调
}
Ext.extend(DBE.LoginWindow, Ext.Window, {
	load : function() {
		var myform = this.loginForm.form;
		myform.load({
			url : '../initAction/load.do',
			success : function() {
				var fld = myform.findField('fldPassword');
				fld.focus(100);
			}
		});
	},
	submit : function() {
		var form = this.loginForm.getForm();
		if (form.isValid()) {
			// alert('login...');
			form.doAction('submit', {
				url : '../initAction/login.do',
				method : 'post',
				waitTitle : '请稍等',
				waitMsg : '登录中……',
				scope : this,
				success : function(form, action) {
					// alert('登录成功:' + action.result);
					if (this.successCallback) {
						this.successCallback();
					}
					this.close();

					// 为Ajax注册事件Handle
					DBE.initAjaxListeners();
				},
				failure : function(form, action) {
					if (action.result) {
						var msg = "未知错误~!";
						var rst = action.result;
						if (rst && rst.msg) {
							msg = rst.msg;
						}
						Ext.Msg.info({
							message : msg,
							alignRef : 'loginWindow',
							alignType : 'tl-tr?'
						});
					} else {
						var resp = action.response;
						var msg = resp.statusText + "[" + resp.status + "]";
						alert('登录失败:' + msg);
					}
				}
			});
		} else {
			// alert('数据填写不完整~~!');
			Ext.Msg.info({
				message : '数据填写不完整~~!',
				alignRef : 'loginWindow',
				alignType : 'tl-tr?'
			});
		}
	}
});