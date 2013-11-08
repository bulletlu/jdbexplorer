// Table Grid 定义
DBE.DynamicGrid = function(config) {
	var dgutils = DBE.DynamicGridUtils;

	// 增强tableinfo对象
	var tableInfo = config.tableinfo;
	dgutils.boostupTableInfo(tableInfo);

	// 构造列模型
	var rn = new Ext.grid.RowNumberer(); // 行号生成器
	var sm = new Ext.grid.CheckboxSelectionModel(); // 选择器
	var cm = dgutils.createColumnModel(tableInfo, [rn, sm]);

	// 创建record对象..
	var fields = [];
	Ext.each(tableInfo.columns, function(columnInfo) {
		var rd = {
			name : columnInfo.name,
			type : columnInfo.extType.type
		}
		// 指定数据类型转换器
		if (columnInfo.extType.dateType) {
			// 日期类型转换器
			rd.convert = function(value, record) {
				if (value.time) {
					return new Date(value.time);
				} else {
					return value;
				}
			};
		}
		fields = fields.concat(rd);
	});
	var gRecord = Ext.data.Record.create(fields);

	// create 数据源
	var ds = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : config.loadURL
		}),
		reader : new Ext.data.JsonReader({
			totalProperty : 'total',
			root : 'rows'
		}, gRecord),
		listeners : {
			beforeload : {
				scope : this,
				fn : function(store, options) {
					// load前 设置search相关的参数
					store.baseParams = store.baseParams || {};
					var search = this.getSearchFields();
					var value = this.searchInput.getValue();
					if (search && value) {
						store.baseParams.search = search;
						store.baseParams.searchValue = value;
					} else {
						store.baseParams.search = null;
						store.baseParams.searchValue = null;
					}
				}
			},
			load : {
				scope : this,
				fn : function(store, records, opt) {
					// load后 选择第一条记录..
					if (records.length > 0) {
						this.getSelectionModel().selectFirstRow();
					}
				}
			},
			loadexception : function() {
				// load 数据失败..
				alert("Load Grid Data 失败~~~!");
			},
			metachange : {
				scope : this,
				fn : function(store, meta) {
					var tinfo = this.initialConfig.tableinfo;
					tinfo.readOnly = true;
					tinfo.columns = meta.fields;
					var ncm = dgutils.createColumnModel(tinfo, [rn, sm]);
					this.reconfigure(store, ncm);
					this.buildSearchFieldMenu(tinfo);

				}
			}
		}
	});

	// 创建搜索栏
	this.searchInput = new Ext.ux.SearchField({
		width : 300,
		onSearchClick : function() {
			ds.reload();
		},
		onSearchCleanClick : function() {
			ds.reload();
		}
	});

	// 创建搜索菜单
	this.searchFieldMenu = new Ext.menu.Menu();
	this.buildSearchFieldMenu(tableInfo);

	// 创建工具栏.top_bar
	var tbar = new Ext.Toolbar([{
		text : '搜索: ',
		menu : this.searchFieldMenu
	}, this.searchInput, '->']);

	// bottom_bar
	var bbar = new Ext.PagingToolbar({
		displayInfo : true,
		autoHeight : true,
		store : ds,
		pageSize : config.pageSize
	});

	// create config params.
	var cfg = {
		ds : ds,
		cm : cm,
		sm : sm,
		stripeRows : true,
		tbar : tbar,
		bbar : bbar,
		width : 10,
		loadMask : {
			msg : "读取中..."
		},
		viewConfig : {
			// 字段数多于10个时，不进行强制平铺.
			forceFit : fields.length < 10
		},
		listeners : {
			cellcontextmenu : {
				scope : this,
				fn : function(grid, row, cell, objEvent) {
					this.cellRightMenuEventObject.isRightClick = true;
					this.cellRightMenuEventObject.isHeaderRightClick = false;
					this.cellRightMenuEventObject.rowIdx = row;
					this.cellRightMenuEventObject.cellIdx = cell;

					objEvent.preventDefault();
					this.operateMenu.showAt(objEvent.getXY());
				}
			},
			headercontextmenu : {
				scope : this,
				fn : function(grid, cell, objEvent) {
					this.cellRightMenuEventObject.isRightClick = true;
					this.cellRightMenuEventObject.isHeaderRightClick = true;
					this.cellRightMenuEventObject.cellIdx = cell;

					objEvent.preventDefault();
					this.operateMenu.showAt(objEvent.getXY());
				}
			}
		}
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.DynamicGrid.superclass.constructor.call(this, config);

	// 创建功能Action对象
	var actions = new DBE.DynamicGridActions(this);

	// 创建单元格右键菜单相关信息的记录对象
	this.cellRightMenuEventObject = {
		isRightClick : false,// 是否右键点击
		isHeaderRightClick : false,// 是否 列头上的 右键点击
		cellIdx : -1,
		rowIdx : -1
	};
	// 初始化右键/操作菜单
	this.operateMenu = new Ext.menu.Menu({
		listeners : {
			beforeshow : {
				scope : this,
				fn : function() {
					// 先全部复位
					actions.copyCellToClipboard.setHidden(false);
					actions.copyColumnToClipboard.setHidden(false);

					// 不是右键隐藏 ...
					var hide = !this.cellRightMenuEventObject.isRightClick;
					actions.copyCellToClipboard.setHidden(hide);
					actions.copyColumnToClipboard.setHidden(hide);

					if (!hide) {
						// 是右键，再看是否列头
						hide = this.cellRightMenuEventObject.isHeaderRightClick;
						actions.copyCellToClipboard.setHidden(hide);
					}
				}
			}
		},
		items : [actions.copyCellToClipboard, actions.copyToClipboard,
				actions.copyColumnToClipboard,
				actions.copyColumnHeaderToClipboard, '-', actions.dataExport]
	});

	// 初始 TopBar 对象
	this.topbarItems = new Ext.util.MixedCollection();
	this.topbarItems.add("refresh", actions.refresh);
	this.topbarItems.add("operate", new Ext.Toolbar.Button({
		text : '操作',
		iconCls : 'menus',
		scope : this,
		// menu : operateMenu,
		handler : function(btn, objEvent) {
			// reset 右键菜单相关信息
			this.cellRightMenuEventObject.isRightClick = false;
			this.cellRightMenuEventObject.isHeaderRightClick = false;

			// 显示菜单
			objEvent.preventDefault();
			this.operateMenu.showAt(objEvent.getXY());
		}
	}));
}
Ext.extend(DBE.DynamicGrid, Ext.grid.EditorGridPanel, {
	/**
	 * 取得 搜索字段名称列表（以逗号--,分隔）
	 */
	getSearchFields : function() {
		var fields = "";
		this.searchFieldMenu.items.each(function(item) {
			if (item.checked) {
				if (fields != "") {
					fields += ",";
				}
				fields += item.text;
			}
		});
		return fields;
	},
	/**
	 * 刷新 搜索字段子菜单
	 */
	buildSearchFieldMenu : function(tableInfo) {
		this.searchFieldMenu.removeAll();

		var focusSearchInput = (function() {
			this.searchInput.focus(100);
		}).createDelegate(this);

		Ext.each(tableInfo.columns, function(col) {
			// 日期型、长类型不能搜索..
			var checked = !col.extType.dateType && !col.extType.longType;
			if (checked) {
				// 默认情况下 数值型、布尔 类型的字段不选中（即不参加搜索）
				checked = !col.extType.numberType && !col.extType.booleanType;
				this.searchFieldMenu.add({
					text : col.name,
					checked : checked,
					checkHandler : focusSearchInput
				});
			}
		}, this);
	},
	/**
	 * 添加 Action到指定索引
	 */
	insertAction : function(index, key, action) {
		this.topbarItems.insert(index, key, action);
	},
	/**
	 * 构建TopBarItem，该方法使子类添加新 button使可以添加到工具栏上
	 */
	buildTBarItems : function() {
		var tbar = this.getTopToolbar();
		var items = this.topbarItems;
		var length = items.getCount();
		for (var i = 0; i < length; i++) {
			if (i > 0) {
				tbar.addSeparator();
			}
			tbar.add(items.itemAt(i));
		}
	},
	/**
	 * 取得分页信息，即当前页码、共多少页等
	 */
	getPagingInfo : function() {
		var result = {
			pageNo : 0,
			pageTotal : 0
		};
		var items = this.getBottomToolbar().items;
		if (items && items.length) {
			for (var i = 0; i < items.length; i++) {
				var item = items.get(i);
				if (!item.getXType) {
					// 取得当前页码
					var dom = item.getEl();
					if (dom.nodeName.toUpperCase() == "INPUT") {
						result.pageNo = dom.value;

						// 继续取得总页数
						item = items.get(i + 1);
						dom = item.getEl();
						var html = dom.innerHTML;
						var pageMaxNo = 0;
						for (var x = 0; x < html.length; x++) {
							var xchar = html.charAt(x);
							pageMaxNo = new Number(xchar);
							if (!isNaN(pageMaxNo) && pageMaxNo > 0) {
								break;
							}
						}
						result.pageTotal = pageMaxNo;
						break;
					}
				}
			}
		}
		return result;
	}
});