// Table Grid 定义
DBE.DynamicTableGrid = function(config) {
	// config params
	var cfg = {
		loadURL : '../dbeGridAction/load.do',
		pageSize : 25
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.DynamicTableGrid.superclass.constructor.call(this, config);

	// 公布属性
	this.nodeInfo = null;
	this.pageSize = config.pageSize;

	// 设置事件 - load前附加必要参数
	this.store.on('beforeload', function(store, options) {
		store.baseParams.node = this.nodeInfo.id;
		store.baseParams.path = this.nodeInfo.getPath('text');
		store.baseParams.text = this.nodeInfo.text;
	}, this);

	// 检查表格是否是可修改的
	var tableinfo = config.tableinfo;
	if (!tableinfo.readOnly) {
		// 表格是可修改的，添加必要的功能和事件
		var actions = new DBE.DynamicTableGridActions(this, tableinfo);
		this.insertAction(0, "remove", actions.remove);

		// 添加事件-- table数据被修改后
		this.on('afteredit', function(object) {
			actions.modifyUpdatePost(object);
		});

		// 添加事件-- 长类型格式的数据修改和查看..
		this.on('celldblclick', function(grid, rowIdx, columnIdx, eventObj) {
			var tablename = this.nodeInfo.text;
			return actions.processLongTypeContent(grid, rowIdx, columnIdx,
					eventObj, tablename);
		});
	}
}
Ext.extend(DBE.DynamicTableGrid, DBE.DynamicGrid, {
	load : function(node) {
		this.nodeInfo = node;
		this.store.load({
			params : {
				start : 0,
				limit : this.pageSize
			}
		});
	}
});