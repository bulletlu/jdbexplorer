// Table Grid 定义
DBE.WelcomeGrid = function(config) {
	// 准备数据源
	var url = "../initAction/welcome.do";
	var ds = new Ext.data.JsonStore({
		url : url,
		listeners : {
			load : {
				scope : this,
				fn : function(store, records, opt) {
					// 可以在这里动态创建列模型...
					// var data = records[0].data;
					// for (x in data) {
					// alert(x);
					// }
					if (records.length > 0) {
						this.getSelectionModel().selectFirstRow();
					}
				}
			},
			loadexception : function() {
				// load 数据失败..
				alert("Load WelcomeGrid 失败:[" + url + "]");
			}
		}
	});

	// 创建列模型
	// new Ext.grid.RowNumberer(),
	var cm = new Ext.grid.ColumnModel([{
		header : '名称',
		menuDisabled : true,
		dataIndex : 'name'
	}, {
		header : '值',
		menuDisabled : true,
		dataIndex : 'value'
	}]);

	// create config params.
	var cfg = {
		ds : ds,
		cm : cm,
		sm : new Ext.grid.RowSelectionModel({
			singleSelect : true
		}),
		width : 10,
		stripeRows : true,
		loadMask : {
			msg : "读取中..."
		},
		viewConfig : {
			forceFit : true
		}
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.WelcomeGrid.superclass.constructor.call(this, config);
}
Ext.extend(DBE.WelcomeGrid, Ext.grid.GridPanel);