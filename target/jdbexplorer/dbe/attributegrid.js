// 表属性窗口定义
DBE.AttributeWindow = function(node,config) {
	
	// 获取表的schema和database
	var column ="";
	var database = "";
	var schema = "";
	var path_array =node.getPath('text').split("/");
  	 database = path_array[1];
  	 schema = path_array[2];  	
	
  	//根据表构造显示表属性的grid
	var cm = new Ext.grid.ColumnModel(
	[	 new Ext.grid.RowNumberer(),
		{header : '列名',dataIndex : 'columName',width:80},
		{header : '数据类型',dataIndex : 'dataType'},
		{header : '最大长度',dataIndex : 'maxlength'},
		{header : '可否为空',dataIndex : 'isNull'}
	]);

	var jsonds = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../dbeTreeAction/loadTableAttribute.do'
		}),
		baseParams: {
		node : node.id,
		path  : node.path,
		text : node.text
		},
		reader : new Ext.data.JsonReader(
		    
			{root : 'columns'}, 
			[{name : 'columName'},
			{name : 'dataType'},
			{name : 'maxlength'},
			{name : 'isNull'}])
	});

	var _grid = new Ext.grid.GridPanel({
		ds : jsonds,
		cm : cm,
		scope : this,
		height : 200,
		listeners : {
			cellclick : function() {
				// 
				var record = this.getSelectionModel().getSelected();
				var columName = record.data['columName'];
				if (columName != '') {
					column = columName;
					jsonds1.proxy= new Ext.data.HttpProxy({
					url: '../dbeTreeAction/loadColumnPar.do?column='+column
					});
 
					jsonds1.load();
				}
			}
		}
	  });
	  
	jsonds.load();

	
	//根据列名构造显示列详细信息的grid
	var cm1 = new Ext.grid.ColumnModel(
	[	 new Ext.grid.RowNumberer(),
		{header : '参数',dataIndex : 'parameter',width:200},
		{header : '数值',dataIndex : 'value'}
	]);
	
	var jsonds1 = new Ext.data.Store({
		proxy : new Ext.data.HttpProxy({
			url : '../dbeTreeAction/loadColumnPar.do'
		}),
		baseParams: {
		node : node.id,
		path  : node.path,
		text : node.text
		},
		reader : new Ext.data.JsonReader(
		    
			{root : 'root'}, 
			[{name : 'parameter'},
			{name : 'value'}])
	});
	
	var clumnGrid = new Ext.grid.GridPanel({
		
		ds : jsonds1,
		cm : cm1,
		scope : this,
		height : 200
	})
	
	jsonds1.load();
	
	
	var attributeForm = new Ext.form.FormPanel({
		id : 'attributeForm',
		frame : true,
		labelWidth : 70,
		buttonAlign : 'right',
		reader : new Ext.data.JsonReader({
			success : 'success',
			root : 'root'
		}, [{
			name : 'database'
		}, {
			name : 'schema'
		}, {
			name : 'tablename'
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
				readOnly: true
			},
			items : [{
				id : 'fldDatabase',
				fieldLabel : 'database',
				allowBlank : false,
				value :database,
				name : 'database'
			}, {
				id : 'fldSchema',
				fieldLabel : 'schema',
				allowBlank : false,
				value : schema,
				name : 'schema'
			}, {
				id : 'fldTablename',
				fieldLabel : 'table',
				allowBlank : false,
				value :node.text,
				name : 'tablename'
			}]
		}, _grid,clumnGrid],
		buttons : [{
			text : '关闭',
			scope : this,
			handler : function() {
				this.close();
			}
		}],
		keys : [{
			key : [10, 13],
			scope : this,
			fn : function() {
				this.close();
			}
		}]
	});

	// 准备配置参数
	var cfg = {
		title : '表格属性',
		id : 'attributeWindow',
		width : 300,
		height : 320,
		layout : 'fit',
		plain : true,
		frame : true,
		border : false,
		modal : true,
		draggable : false,
		resizable : false,
		autoScroll : true,
		closable : true,
		items : attributeForm
	};
	config = Ext.applyIf(config || {}, cfg);

	// call superclass
	DBE.AttributeWindow.superclass.constructor.call(this, config);

	// 公布属性
	this.attributeForm = attributeForm;
	this.successCallback = false;
}
Ext.extend(DBE.AttributeWindow, Ext.Window, {
});