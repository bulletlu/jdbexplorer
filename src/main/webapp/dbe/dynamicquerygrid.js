// Table Grid 定义
DBE.DynamicQueryGrid = function(config) {
	// config params
	var cfg = {
		loadURL : '../dbeSQLQueryAction/query.do',
		pageSize : 25
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.DynamicQueryGrid.superclass.constructor.call(this, config);

	// 设置事件 - load前附加sql值..
	this.store.addListener('beforeload', function(store, options) {
		store.baseParams.sql = this.sqlText;
	}, this);

	// 设置事件 - load后 调整grid宽度，使column fit
	this.store.on('load', function(store, records, opt) {
		var size = this.getSize();
		this.setWidth(size.width - 1);
		this.setWidth(size.width);
	}, this);

	// 公布属性
	this.sqlText = '';
}
Ext.extend(DBE.DynamicQueryGrid, DBE.DynamicGrid, {
	reload : function(sql) {
		// 通过指定sql 重写load
		this.sqlText = sql;
		this.store.load({
			params : {
				start : 0,
				limit : this.pageSize
			}
		});
	}
});