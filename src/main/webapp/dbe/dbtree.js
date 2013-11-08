// DBTree定义
DBE.DBTreePanel = function(config) {
	// 后台Action URL
	this.serviceURL = "../dbeTreeAction";

	// 配置参数传入 grid 要显示的tabPanel
	var tabPanel = config.gridTabPanel;

	// Config Params.
	var cfg = {
		id : 'dbTree',
		border : false,
		bodyBorder : false,
		autoScroll : true,
		containerScroll : true,
		animate : false,
		listeners : {
			contextmenu : function(node, objEvent) {
				this.select(node);
				objEvent.preventDefault();
				this.menus.showAt(objEvent.getXY());
			},
			click : function(node, objEvent) {
				if (!node.isExpanded()) {
					node.expand();
				}
			},
			dblclick : {
				scope : this,
				fn : function(node, objEvent) {
					if (node.getDepth() < 3 || node.getDepth() == 4) {
						return;
					}
					// funOpen(node, objEvent);
					// alert(this.actions.open.execute);
					this.actions.open.execute();
				}
			}
		},
		root : new Ext.tree.AsyncTreeNode({
			id : 'database',
			icon : 'images/database.png',
			text : 'Loading...'
		}),
		loader : new Ext.tree.TreeLoader({
			url : this.serviceURL + "/tree.do",
			listeners : {
				beforeload : function(loader, node) {
					loader.baseParams.path = node.getPath('text');
				}
			}
		})
	};
	config = Ext.applyIf(config || {}, cfg);

	// call 父类构建器
	DBE.DBTreePanel.superclass.constructor.call(this, config);

	// 初始功能菜单
	this.actions = new DBE.DBTreePanelActions(this, tabPanel);
	this.menus = new Ext.menu.Menu({
		listeners : {
			beforeshow : {
				scope : this,
				fn : function() {
					this.actions.resetActionState();
				}
			}
		}
	});
	var treeActions = this.actions.getActionItems();
	for (var i = 0; i < treeActions.length; i++) {
		var action = treeActions[i];
		this.menus.add(action);
	}
}
Ext.extend(DBE.DBTreePanel, Ext.tree.TreePanel, {
	select : function(node) {// 选择指定Node.
		this.getSelectionModel().select(node);
		node.expand();
	},
	getSelectNode : function() {
		var current = this.getSelectionModel().getSelectedNode();
		if (current) {
			return current;
		}
		return this.getRootNode();
	}
});