// DEB DynamicGrid Actions定义
DBE.DynamicGridActions = function(dyGrid) {
	/**
	 * 复制表格内容（选择行）到剪贴板
	 */
	this.copyToClipboard = new Ext.Action({
		text : '复制行到剪贴板',
		iconCls : 'copy',
		scope : this,
		handler : function() {
			var records = dyGrid.getSelectionModel().getSelections();
			// copyRecords(records, false);
			if (records && records.length) {
				// 生成数据
				var data = "";
				for (var i = 0; i < records.length; i++) {
					var rd = records[i];
					if (data.length > 0) {
						data += "\n";
					}
					var newLine = true;
					for (x in rd.data) {
						if (newLine) {
							newLine = false;
						} else {
							data += ",";
						}

						// 取得列值
						var idx = dyGrid.getColumnModel().findColumnIndex(x);
						var col = dyGrid.getColumnModel().getColumnById(idx);
						var renderer = col.renderer;
						var val = renderer ? renderer(rd.data[x]) : rd.data[x];
						data += val;
					}
				}
				// copy...
				DBE.copyToClipboard(data);
				Ext.Msg.info({
					message : "[" + records.length + " 行数据] 已被复制到剪贴板~!"
				});
			} else {
				Ext.Msg.info({
					message : "未发现数据，请选择需要复制的数据行~~!"
				});
			}
		}
	});
	/**
	 * 复制单元格内容到剪贴板
	 */
	this.copyCellToClipboard = new Ext.Action({
		text : '复制单元格到剪贴板',
		iconCls : 'copy',
		scope : dyGrid,
		handler : function() {
			var e = this.cellRightMenuEventObject;
			if (e.isRightClick && !e.isHeaderRightClick) {
				var cm = this.getColumnModel();
				var cellName = cm.getColumnHeader(e.cellIdx);
				var record = this.store.getAt(e.rowIdx);

				var data = record.data[cellName];
				var renderer = cm.getColumnById(e.cellIdx).renderer;
				if (renderer) {
					data = renderer(data);
				}

				DBE.copyToClipboard(data);
				Ext.Msg.info({
					message : '[' + data + '] 已复制到剪贴板~'
				});
			}
		}
	});
	/**
	 * 复制列数据到剪贴板
	 */
	this.copyColumnToClipboard = new Ext.Action({
		text : '复制列到剪贴板',
		iconCls : 'copy',
		scope : dyGrid,
		handler : function() {
			var e = this.cellRightMenuEventObject;
			if (e.isRightClick) {
				var cm = this.getColumnModel();
				var cellName = cm.getColumnHeader(e.cellIdx);
				var renderer = cm.getColumnById(e.cellIdx).renderer;
				
				var records = this.store.getRange();
				var data = "";
				for (var i = 0; i < records.length; i++) {
					if (data.length > 0) {
						data += "\n";
					}
					var record = records[i];
					var value = record.data[cellName];
					if (renderer) {
						value = renderer(value);
					}
					data += value;
				}

				DBE.copyToClipboard(data);
				Ext.Msg.info({
					message : '[' + cellName + '列] 已复制到剪贴板~'
				});
			}
		}
	});
	/**
	 * 复制列头到剪贴板
	 */
	this.copyColumnHeaderToClipboard = new Ext.Action({
		text : '复制列头到剪贴板',
		iconCls : 'copy',
		scope : dyGrid,
		handler : function() {
			var cm = this.getColumnModel();
			var data = "";
			for (var i = 0; i < cm.getColumnCount(); i++) {
				if (i > 1) {// 跳过第0/1 序号/选择框
					if (data.length > 0) {
						data += ",";
					}
					data += cm.getColumnHeader(i);
				}
			}
			DBE.copyToClipboard(data);
			Ext.Msg.info({
				message : '列名称 已复制到剪贴板~'
			});
		}
	});
	/**
	 * 数据导出
	 */
	this.dataExport = new Ext.Action({
		text : '数据导出',
		iconCls : 'export',
		handler : function() {
			// alert("数据导出~~》:" + DBE.DataExport);
			var paging = dyGrid.getPagingInfo();
			var baseParams = null;
			if (dyGrid.nodeInfo) {
				baseParams = {
					node : dyGrid.nodeInfo.id,
					text : dyGrid.nodeInfo.text,
					path : dyGrid.nodeInfo.getPath('text')
				}
			} else if (dyGrid.sqlText && dyGrid.sqlText.length > 0) {
				baseParams = {
					sql : dyGrid.sqlText
				}
			} else {
				alert("程序错误：无法取得 nodeInfo or sqlText~!");
				return;
			}

			var dataExport = new DBE.DataExport({
				tableinfo : dyGrid.initialConfig.tableinfo,
				pagingInfo : paging,
				baseParams : baseParams
			});
			dataExport.show(dyGrid, function() {
				// 自动计算高度，并重设窗口高度..
				var height = this.reCalculateHeight();
				// alert("height:" + height);
				this.setHeight(height);
				this.center();
				this.getLayout();
			}, dataExport);
		}
	});

	/**
	 * 刷新：重新读取Grid数据
	 */
	this.refresh = new Ext.Action({
		text : '刷新',
		tooltip : '刷新数据',
		tooltipType : 'title',
		iconCls : 'refresh',
		handler : function() {
			dyGrid.store.reload();
		}
	});
};
