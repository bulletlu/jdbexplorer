// DEB Action定义..
DBE.SQLQueryPanelActions = function(queryPanel) {
	/**
	 * 运行SQL语句的Action
	 */
	this.run = new Ext.Action({
		text : 'Run',
		iconCls : 'run',
		handler : function() {
			queryPanel.runSQL();
		}
	});

		/**
	 * 执行SQL文件
	 */
	this.exec = new Ext.Action({
		text : '执行文件',
		iconCls : 'runfile',
		handler : function() {
			Ext.ux.SwfUploader.upload({
					upload_url : '../dbeSQLQueryAction/execSqlFile.do',
					file_types : {
						type : '*.sql',
						desc : '所有文件'
					},
					upload_success_handler:function(file,data){
					//alert("服务器端返回数据：" + data);
					var data=file.name+' 文件执行成功!   '+data;
					queryPanel.runSQLFile(data);
					}
				});
		}
	});
	
	
	/**
	 * 打开SQL文件
	 */
	this.open = new Ext.Action({
		text : '打开文件',
		iconCls : 'openfile',
		handler : function() {
			Ext.ux.SwfUploader.upload({
					upload_url : '../dbeSQLQueryAction/openSqlFile.do',
					file_types : {
						type : '*.sql',
						desc : '所有文件'
					},
					upload_success_handler:function(file,data){
					//alert("服务器端返回数据：" + data);
					queryPanel.openSQLFile(data);
					}
				});
		}
	});

	/**
	 * 保存sql内容
	 */
	this.save = new Ext.Action({
		text : '保存',
		iconCls : 'save',
		handler : function() {
			var sql = queryPanel.sqlEditor.getSQLText(true);
			window.location.href = '../dbeSQLQueryAction/saveAsSQL.do?sql=' + sql;
		}
	});

	/**
	 * 取得相关Action对象
	 */
	this.getActions = (function() {
		return [this.run, this.exec, this.open, this.save];
	}).createDelegate(this);
};