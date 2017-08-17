(function($){
	var settings = {}, roots = {}, caches = {},
	//default consts of core
	_consts = {
		className: {
			BUTTON: "button",
			LEVEL: "level",
			ICO_LOADING: "ico_loading",
			SWITCH: "switch",
			NAME: 'node_name'
		},
		event: {
			NODECREATED: "ztree_nodeCreated",
			CLICK: "ztree_click",
			EXPAND: "ztree_expand",
			COLLAPSE: "ztree_collapse",
			ASYNC_SUCCESS: "ztree_async_success",
			ASYNC_ERROR: "ztree_async_error",
			REMOVE: "ztree_remove",
			SELECTED: "ztree_selected",
			UNSELECTED: "ztree_unselected"
		},
		id: {
			A: "_a",
			ICON: "_ico",
			SPAN: "_span",
			SWITCH: "_switch",
			UL: "_ul"
		},
		line: {
			ROOT: "root",
			ROOTS: "roots",
			CENTER: "center",
			BOTTOM: "bottom",
			NOLINE: "noline",
			LINE: "line"
		},
		folder: {
			OPEN: "open",
			CLOSE: "close",
			DOCU: "docu"
		},
		node: {
			CURSELECTED: "curSelectedNode"
		}
	},
	//default setting of core
	_setting = {
		treeId: "",
		treeObj: null,
		view: {
			addDiyDom: null,
			autoCancelSelected: true,
			dblClickExpand: true,
			expandSpeed: "fast",
			fontCss: {},
			nameIsHTML: false,
			selectedMulti: true,
			showIcon: true,
			showLine: true,
			showTitle: true,
			txtSelectedEnable: false
		},
		data: {
			key: {
				children: "children",
				name: "name",
				title: "",
				url: "url",
				icon: "icon"
			},
			simpleData: {
				enable: false,
				idKey: "id",
				pIdKey: "pId",
				rootPId: null
			},
			keep: {
				parent: false,
				leaf: false
			}
		},
		async: {
			enable: false,
			contentType: "application/x-www-form-urlencoded",
			type: "post",
			dataType: "text",
			url: "",
			autoParam: [],
			otherParam: [],
			dataFilter: null
		},
		callback: {
			beforeAsync:null,
			beforeClick:null,
			beforeDblClick:null,
			beforeRightClick:null,
			beforeMouseDown:null,
			beforeMouseUp:null,
			beforeExpand:null,
			beforeCollapse:null,
			beforeRemove:null,

			onAsyncError:null,
			onAsyncSuccess:null,
			onNodeCreated:null,
			onClick:null,
			onDblClick:null,
			onRightClick:null,
			onMouseDown:null,
			onMouseUp:null,
			onExpand:null,
			onCollapse:null,
			onRemove:null
		}
	},
	//default root of core
	//zTree use root to save full data
	_initRoot = function (setting) {
		var r = data.getRoot(setting);
		if (!r) {
			r = {};
			data.setRoot(setting, r);
		}
		r[setting.data.key.children] = [];
		r.expandTriggerFlag = false;
		r.curSelectedList = [];
		r.noSelection = true;
		r.createdNodes = [];
		r.zId = 0;
		r._ver = (new Date()).getTime();
	},
	//default cache of core
	_initCache = function(setting) {
		var c = data.getCache(setting);
		if (!c) {
			c = {};
			data.setCache(setting, c);
		}
		c.nodes = [];
		c.doms = [];
	},
	//default bindEvent of core
	_bindEvent = function(setting) {
		var o = setting.treeObj,
		c = consts.event;
		o.bind(c.NODECREATED, function (event, treeId, node) {
			tools.apply(setting.callback.onNodeCreated, [event, treeId, node]);
		});

		o.bind(c.CLICK, function (event, srcEvent, treeId, node, clickFlag) {
			tools.apply(setting.callback.onClick, [srcEvent, treeId, node, clickFlag]);
		});

		o.bind(c.EXPAND, function (event, treeId, node) {
			tools.apply(setting.callback.onExpand, [event, treeId, node]);
		});

		o.bind(c.COLLAPSE, function (event, treeId, node) {
			tools.apply(setting.callback.onCollapse, [event, treeId, node]);
		});

		o.bind(c.ASYNC_SUCCESS, function (event, treeId, node, msg) {
			tools.apply(setting.callback.onAsyncSuccess, [event, treeId, node, msg]);
		});

		o.bind(c.ASYNC_ERROR, function (event, treeId, node, XMLHttpRequest, textStatus, errorThrown) {
			tools.apply(setting.callback.onAsyncError, [event, treeId, node, XMLHttpRequest, textStatus, errorThrown]);
		});

		o.bind(c.REMOVE, function (event, treeId, treeNode) {
			tools.apply(setting.callback.onRemove, [event, treeId, treeNode]);
		});

		o.bind(c.SELECTED, function (event, treeId, node) {
			tools.apply(setting.callback.onSelected, [treeId, node]);
		});
		o.bind(c.UNSELECTED, function (event, treeId, node) {
			tools.apply(setting.callback.onUnSelected, [treeId, node]);
		});
	},
	_unbindEvent = function(setting) {
		var o = setting.treeObj,
		c = consts.event;
		o.unbind(c.NODECREATED)
		.unbind(c.CLICK)
		.unbind(c.EXPAND)
		.unbind(c.COLLAPSE)
		.unbind(c.ASYNC_SUCCESS)
		.unbind(c.ASYNC_ERROR)
		.unbind(c.REMOVE)
		.unbind(c.SELECTED)
		.unbind(c.UNSELECTED);
	},
	//default event proxy of core
	_eventProxy = function(event) {
		var target = event.target,
		setting = data.getSetting(event.data.treeId),
		tId = "", node = null,
		nodeEventType = "", treeEventType = "",
		nodeEventCallback = null, treeEventCallback = null,
		tmp = null;

		if (tools.eqs(event.type, "mousedown")) {
			treeEventType = "mousedown";
		} else if (tools.eqs(event.type, "mouseup")) {
			treeEventType = "mouseup";
		} else if (tools.eqs(event.type, "contextmenu")) {
			treeEventType = "contextmenu";
		} else if (tools.eqs(event.type, "click")) {
			if (tools.eqs(target.tagName, "span") && target.getAttribute("treeNode"+ consts.id.SWITCH) !== null) {
				tId = tools.getNodeMainDom(target).id;
				nodeEventType = "switchNode";
			} else {
				tmp = tools.getMDom(setting, target, [{tagName:"a", attrName:"treeNode"+consts.id.A}]);
				if (tmp) {
					tId = tools.getNodeMainDom(tmp).id;
					nodeEventType = "clickNode";
				}
			}
		} else if (tools.eqs(event.type, "dblclick")) {
			treeEventType = "dblclick";
			tmp = tools.getMDom(setting, target, [{tagName:"a", attrName:"treeNode"+consts.id.A}]);
			if (tmp) {
				tId = tools.getNodeMainDom(tmp).id;
				nodeEventType = "switchNode";
			}
		}
		if (treeEventType.length > 0 && tId.length == 0) {
			tmp = tools.getMDom(setting, target, [{tagName:"a", attrName:"treeNode"+consts.id.A}]);
			if (tmp) {tId = tools.getNodeMainDom(tmp).id;}
		}
		// event to node
		if (tId.length>0) {
			node = data.getNodeCache(setting, tId);
			switch (nodeEventType) {
				case "switchNode" :
					if (!node.isParent) {
						nodeEventType = "";
					} else if (tools.eqs(event.type, "click")
						|| (tools.eqs(event.type, "dblclick") && tools.apply(setting.view.dblClickExpand, [setting.treeId, node], setting.view.dblClickExpand))) {
						nodeEventCallback = handler.onSwitchNode;
					} else {
						nodeEventType = "";
					}
					break;
				case "clickNode" :
					nodeEventCallback = handler.onClickNode;
					break;
			}
		}
		// event to zTree
		switch (treeEventType) {
			case "mousedown" :
				treeEventCallback = handler.onZTreeMousedown;
				break;
			case "mouseup" :
				treeEventCallback = handler.onZTreeMouseup;
				break;
			case "dblclick" :
				treeEventCallback = handler.onZTreeDblclick;
				break;
			case "contextmenu" :
				treeEventCallback = handler.onZTreeContextmenu;
				break;
		}
		var proxyResult = {
			stop: false,
			node: node,
			nodeEventType: nodeEventType,
			nodeEventCallback: nodeEventCallback,
			treeEventType: treeEventType,
			treeEventCallback: treeEventCallback
		};
		return proxyResult
	},
	//default init node of core
	_initNode = function(setting, level, n, parentNode, isFirstNode, isLastNode, openFlag) {
		if (!n) return;
		var r = data.getRoot(setting),
		childKey = setting.data.key.children;
		n.level = level;
		n.tId = setting.treeId + "_" + (++r.zId);
		n.parentTId = parentNode ? parentNode.tId : null;
		n.open = (typeof n.open == "string") ? tools.eqs(n.open, "true") : !!n.open;
		if (n[childKey] && n[childKey].length > 0) {
			n.isParent = true;
			n.zAsync = true;
		} else {
			n.isParent = (typeof n.isParent == "string") ? tools.eqs(n.isParent, "true") : !!n.isParent;
			n.open = (n.isParent && !setting.async.enable) ? n.open : false;
			n.zAsync = !n.isParent;
		}
		n.isFirstNode = isFirstNode;
		n.isLastNode = isLastNode;
		n.getParentNode = function() {return data.getNodeCache(setting, n.parentTId);};
		n.getPreNode = function() {return data.getPreNode(setting, n);};
		n.getNextNode = function() {return data.getNextNode(setting, n);};
		n.getIndex = function() {return data.getNodeIndex(setting, n);};
		n.getPath = function() {return data.getNodePath(setting, n);};
		n.isAjaxing = false;
		data.fixPIdKeyValue(setting, n);
	},
	_init = {
		bind: [_bindEvent],
		unbind: [_unbindEvent],
		caches: [_initCache],
		nodes: [_initNode],
		proxys: [_eventProxy],
		roots: [_initRoot],
		beforeA: [],
		afterA: [],
		innerBeforeA: [],
		innerAfterA: [],
		zTreeTools: []
	},
	//method of operate data
	data = {
		addNodeCache: function(setting, node) {
			data.getCache(setting).nodes[data.getNodeCacheId(node.tId)] = node;
		},
		getNodeCacheId: function(tId) {
			return tId.substring(tId.lastIndexOf("_")+1);
		},
		addAfterA: function(afterA) {
			_init.afterA.push(afterA);
		},
		addBeforeA: function(beforeA) {
			_init.beforeA.push(beforeA);
		},
		addInnerAfterA: function(innerAfterA) {
			_init.innerAfterA.push(innerAfterA);
		},
		addInnerBeforeA: function(innerBeforeA) {
			_init.innerBeforeA.push(innerBeforeA);
		},
		addInitBind: function(bindEvent) {
			_init.bind.push(bindEvent);
		},
		addInitUnBind: function(unbindEvent) {
			_init.unbind.push(unbindEvent);
		},
		addInitCache: function(initCache) {
			_init.caches.push(initCache);
		},
		addInitNode: function(initNode) {
			_init.nodes.push(initNode);
		},
		addInitProxy: function(initProxy, isFirst) {
			if (!!isFirst) {
				_init.proxys.splice(0,0,initProxy);
			} else {
				_init.proxys.push(initProxy);
			}
		},
		addInitRoot: function(initRoot) {
			_init.roots.push(initRoot);
		},
		addNodesData: function(setting, parentNode, index, nodes) {
			var childKey = setting.data.key.children, params;
			if (!parentNode[childKey]) {
				parentNode[childKey] = [];
				index = -1;
			} else if (index >= parentNode[childKey].length) {
				index = -1;
			}

			if (parentNode[childKey].length > 0 && index === 0) {
				parentNode[childKey][0].isFirstNode = false;
				view.setNodeLineIcos(setting, parentNode[childKey][0]);
			} else if (parentNode[childKey].length > 0 && index < 0) {
				parentNode[childKey][parentNode[childKey].length - 1].isLastNode = false;
				view.setNodeLineIcos(setting, parentNode[childKey][parentNode[childKey].length - 1]);
			}
			parentNode.isParent = true;

			if (index<0) {
				parentNode[childKey] = parentNode[childKey].concat(nodes);
			} else {
				params = [index, 0].concat(nodes);
				parentNode[childKey].splice.apply(parentNode[childKey], params);
			}
		},
		addSelectedNode: function(setting, node) {
			var root = data.getRoot(setting);
			if (!data.isSelectedNode(setting, node)) {
				root.curSelectedList.push(node);
			}
		},
		addCreatedNode: function(setting, node) {
			if (!!setting.callback.onNodeCreated || !!setting.view.addDiyDom) {
				var root = data.getRoot(setting);
				root.createdNodes.push(node);
			}
		},
		addZTreeTools: function(zTreeTools) {
			_init.zTreeTools.push(zTreeTools);
		},
		exSetting: function(s) {
			$.extend(true, _setting, s);
		},
		fixPIdKeyValue: function(setting, node) {
			if (setting.data.simpleData.enable) {
				node[setting.data.simpleData.pIdKey] = node.parentTId ? node.getParentNode()[setting.data.simpleData.idKey] : setting.data.simpleData.rootPId;
			}
		},
		getAfterA: function(setting, node, array) {
			for (var i=0, j=_init.afterA.length; i<j; i++) {
				_init.afterA[i].apply(this, arguments);
			}
		},
		getBeforeA: function(setting, node, array) {
			for (var i=0, j=_init.beforeA.length; i<j; i++) {
				_init.beforeA[i].apply(this, arguments);
			}
		},
		getInnerAfterA: function(setting, node, array) {
			for (var i=0, j=_init.innerAfterA.length; i<j; i++) {
				_init.innerAfterA[i].apply(this, arguments);
			}
		},
		getInnerBeforeA: function(setting, node, array) {
			for (var i=0, j=_init.innerBeforeA.length; i<j; i++) {
				_init.innerBeforeA[i].apply(this, arguments);
			}
		},
		getCache: function(setting) {
			return caches[setting.treeId];
		},
		getNodeIndex: function(setting, node) {
			if (!node) return null;
			var childKey = setting.data.key.children,
			p = node.parentTId ? node.getParentNode() : data.getRoot(setting);
			for (var i=0, l=p[childKey].length-1; i<=l; i++) {
				if (p[childKey][i] === node) {
					return i;
				}
			}
			return -1;
		},
		getNextNode: function(setting, node) {
			if (!node) return null;
			var childKey = setting.data.key.children,
			p = node.parentTId ? node.getParentNode() : data.getRoot(setting);
			for (var i=0, l=p[childKey].length-1; i<=l; i++) {
				if (p[childKey][i] === node) {
					return (i==l ? null : p[childKey][i+1]);
				}
			}
			return null;
		},
		getNodeByParam: function(setting, nodes, key, value) {
			if (!nodes || !key) return null;
			var childKey = setting.data.key.children;
			for (var i = 0, l = nodes.length; i < l; i++) {
				if (nodes[i][key] == value) {
					return nodes[i];
				}
				var tmp = data.getNodeByParam(setting, nodes[i][childKey], key, value);
				if (tmp) return tmp;
			}
			return null;
		},
		getNodeCache: function(setting, tId) {
			if (!tId) return null;
			var n = caches[setting.treeId].nodes[data.getNodeCacheId(tId)];
			return n ? n : null;
		},
		getNodeName: function(setting, node) {
			var nameKey = setting.data.key.name;
			return "" + node[nameKey];
		},
		getNodePath: function(setting, node) {
			if (!node) return null;

			var path;
			if(node.parentTId) {
				path = node.getParentNode().getPath();
			} else {
				path = [];
			}

			if (path) {
				path.push(node);
			}

			return path;
		},
		getNodeTitle: function(setting, node) {
			var t = setting.data.key.title === "" ? setting.data.key.name : setting.data.key.title;
			return "" + node[t];
		},
		getNodes: function(setting) {
			return data.getRoot(setting)[setting.data.key.children];
		},
		getNodesByParam: function(setting, nodes, key, value) {
			if (!nodes || !key) return [];
			var childKey = setting.data.key.children,
			result = [];
			for (var i = 0, l = nodes.length; i < l; i++) {
				if (nodes[i][key] == value) {
					result.push(nodes[i]);
				}
				result = result.concat(data.getNodesByParam(setting, nodes[i][childKey], key, value));
			}
			return result;
		},
		getNodesByParamFuzzy: function(setting, nodes, key, value) {
			if (!nodes || !key) return [];
			var childKey = setting.data.key.children,
			result = [];
			value = value.toLowerCase();
			for (var i = 0, l = nodes.length; i < l; i++) {
				if (typeof nodes[i][key] == "string" && nodes[i][key].toLowerCase().indexOf(value)>-1) {
					result.push(nodes[i]);
				}
				result = result.concat(data.getNodesByParamFuzzy(setting, nodes[i][childKey], key, value));
			}
			return result;
		},
		getNodesByFilter: function(setting, nodes, filter, isSingle, invokeParam) {
			if (!nodes) return (isSingle ? null : []);
			var childKey = setting.data.key.children,
			result = isSingle ? null : [];
			for (var i = 0, l = nodes.length; i < l; i++) {
				if (tools.apply(filter, [nodes[i], invokeParam], false)) {
					if (isSingle) {return nodes[i];}
					result.push(nodes[i]);
				}
				var tmpResult = data.getNodesByFilter(setting, nodes[i][childKey], filter, isSingle, invokeParam);
				if (isSingle && !!tmpResult) {return tmpResult;}
				result = isSingle ? tmpResult : result.concat(tmpResult);
			}
			return result;
		},
		getPreNode: function(setting, node) {
			if (!node) return null;
			var childKey = setting.data.key.children,
			p = node.parentTId ? node.getParentNode() : data.getRoot(setting);
			for (var i=0, l=p[childKey].length; i<l; i++) {
				if (p[childKey][i] === node) {
					return (i==0 ? null : p[childKey][i-1]);
				}
			}
			return null;
		},
		getRoot: function(setting) {
			return setting ? roots[setting.treeId] : null;
		},
		getRoots: function() {
			return roots;
		},
		getSetting: function(treeId) {
			return settings[treeId];
		},
		getSettings: function() {
			return settings;
		},
		getZTreeTools: function(treeId) {
			var r = this.getRoot(this.getSetting(treeId));
			return r ? r.treeTools : null;
		},
		initCache: function(setting) {
			for (var i=0, j=_init.caches.length; i<j; i++) {
				_init.caches[i].apply(this, arguments);
			}
		},
		initNode: function(setting, level, node, parentNode, preNode, nextNode) {
			for (var i=0, j=_init.nodes.length; i<j; i++) {
				_init.nodes[i].apply(this, arguments);
			}
		},
		initRoot: function(setting) {
			for (var i=0, j=_init.roots.length; i<j; i++) {
				_init.roots[i].apply(this, arguments);
			}
		},
		isSelectedNode: function(setting, node) {
			var root = data.getRoot(setting);
			for (var i=0, j=root.curSelectedList.length; i<j; i++) {
				if(node === root.curSelectedList[i]) return true;
			}
			return false;
		},
		removeNodeCache: function(setting, node) {
			var childKey = setting.data.key.children;
			if (node[childKey]) {
				for (var i=0, l=node[childKey].length; i<l; i++) {
					arguments.callee(setting, node[childKey][i]);
				}
			}
			data.getCache(setting).nodes[data.getNodeCacheId(node.tId)] = null;
		},
		removeSelectedNode: function(setting, node) {
			var root = data.getRoot(setting);
			for (var i=0, j=root.curSelectedList.length; i<j; i++) {
				if(node === root.curSelectedList[i] || !data.getNodeCache(setting, root.curSelectedList[i].tId)) {
					root.curSelectedList.splice(i, 1);
					setting.treeObj.trigger(consts.event.UNSELECTED, [setting.treeId, node]);
					i--;j--;
				}
			}
		},
		setCache: function(setting, cache) {
			caches[setting.treeId] = cache;
		},
		setRoot: function(setting, root) {
			roots[setting.treeId] = root;
		},
		setZTreeTools: function(setting, zTreeTools) {
			for (var i=0, j=_init.zTreeTools.length; i<j; i++) {
				_init.zTreeTools[i].apply(this, arguments);
			}
		},
		transformToArrayFormat: function (setting, nodes) {
			if (!nodes) return [];
			var childKey = setting.data.key.children,
			r = [];
			if (tools.isArray(nodes)) {
				for (var i=0, l=nodes.length; i<l; i++) {
					r.push(nodes[i]);
					if (nodes[i][childKey])
						r = r.concat(data.transformToArrayFormat(setting, nodes[i][childKey]));
				}
			} else {
				r.push(nodes);
				if (nodes[childKey])
					r = r.concat(data.transformToArrayFormat(setting, nodes[childKey]));
			}
			return r;
		},
		transformTozTreeFormat: function(setting, sNodes) {
			var i,l,
			key = setting.data.simpleData.idKey,
			parentKey = setting.data.simpleData.pIdKey,
			childKey = setting.data.key.children;
			if (!key || key=="" || !sNodes) return [];

			if (tools.isArray(sNodes)) {
				var r = [];
				var tmpMap = [];
				for (i=0, l=sNodes.length; i<l; i++) {
					tmpMap[sNodes[i][key]] = sNodes[i];
				}
				for (i=0, l=sNodes.length; i<l; i++) {
					if (tmpMap[sNodes[i][parentKey]] && sNodes[i][key] != sNodes[i][parentKey]) {
						if (!tmpMap[sNodes[i][parentKey]][childKey])
							tmpMap[sNodes[i][parentKey]][childKey] = [];
						tmpMap[sNodes[i][parentKey]][childKey].push(sNodes[i]);
					} else {
						r.push(sNodes[i]);
					}
				}
				return r;
			}else {
				return [sNodes];
			}
		}
	},
	//method of event proxy
	event = {
		bindEvent: function(setting) {
			for (var i=0, j=_init.bind.length; i<j; i++) {
				_init.bind[i].apply(this, arguments);
			}
		},
		unbindEvent: function(setting) {
			for (var i=0, j=_init.unbind.length; i<j; i++) {
				_init.unbind[i].apply(this, arguments);
			}
		},
		bindTree: function(setting) {
			var eventParam = {
				treeId: setting.treeId
			},
			o = setting.treeObj;
			if (!setting.view.txtSelectedEnable) {
				// for can't select text
				o.bind('selectstart', handler.onSelectStart).css({
					"-moz-user-select":"-moz-none"
				});
			}
			o.bind('click', eventParam, event.proxy);
			o.bind('dblclick', eventParam, event.proxy);
			o.bind('mouseover', eventParam, event.proxy);
			o.bind('mouseout', eventParam, event.proxy);
			o.bind('mousedown', eventParam, event.proxy);
			o.bind('mouseup', eventParam, event.proxy);
			o.bind('contextmenu', eventParam, event.proxy);
		},
		unbindTree: function(setting) {
			var o = setting.treeObj;
			o.unbind('selectstart', handler.onSelectStart)
				.unbind('click', event.proxy)
				.unbind('dblclick', event.proxy)
				.unbind('mouseover', event.proxy)
				.unbind('mouseout', event.proxy)
				.unbind('mousedown', event.proxy)
				.unbind('mouseup', event.proxy)
				.unbind('contextmenu', event.proxy);
		},
		doProxy: function(e) {
			var results = [];
			for (var i=0, j=_init.proxys.length; i<j; i++) {
				var proxyResult = _init.proxys[i].apply(this, arguments);
				results.push(proxyResult);
				if (proxyResult.stop) {
					break;
				}
			}
			return results;
		},
		proxy: function(e) {
			var setting = data.getSetting(e.data.treeId);
			if (!tools.uCanDo(setting, e)) return true;
			var results = event.doProxy(e),
			r = true, x = false;
			for (var i=0, l=results.length; i<l; i++) {
				var proxyResult = results[i];
				if (proxyResult.nodeEventCallback) {
					x = true;
					r = proxyResult.nodeEventCallback.apply(proxyResult, [e, proxyResult.node]) && r;
				}
				if (proxyResult.treeEventCallback) {
					x = true;
					r = proxyResult.treeEventCallback.apply(proxyResult, [e, proxyResult.node]) && r;
				}
			}
			return r;
		}
	},
	//method of event handler
	handler = {
		onSwitchNode: function (event, node) {
			var setting = data.getSetting(event.data.treeId);
			if (node.open) {
				if (tools.apply(setting.callback.beforeCollapse, [setting.treeId, node], true) == false) return true;
				data.getRoot(setting).expandTriggerFlag = true;
				view.switchNode(setting, node);
			} else {
				if (tools.apply(setting.callback.beforeExpand, [setting.treeId, node], true) == false) return true;
				data.getRoot(setting).expandTriggerFlag = true;
				view.switchNode(setting, node);
			}
			return true;
		},
		onClickNode: function (event, node) {
			var setting = data.getSetting(event.data.treeId),
			clickFlag = ( (setting.view.autoCancelSelected && (event.ctrlKey || event.metaKey)) && data.isSelectedNode(setting, node)) ? 0 : (setting.view.autoCancelSelected && (event.ctrlKey || event.metaKey) && setting.view.selectedMulti) ? 2 : 1;
			if (tools.apply(setting.callback.beforeClick, [setting.treeId, node, clickFlag], true) == false) return true;
			if (clickFlag === 0) {
				view.cancelPreSelectedNode(setting, node);
			} else {
				view.selectNode(setting, node, clickFlag === 2);
			}
			setting.treeObj.trigger(consts.event.CLICK, [event, setting.treeId, node, clickFlag]);
			return true;
		},
		onZTreeMousedown: function(event, node) {
			var setting = data.getSetting(event.data.treeId);
			if (tools.apply(setting.callback.beforeMouseDown, [setting.treeId, node], true)) {
				tools.apply(setting.callback.onMouseDown, [event, setting.treeId, node]);
			}
			return true;
		},
		onZTreeMouseup: function(event, node) {
			var setting = data.getSetting(event.data.treeId);
			if (tools.apply(setting.callback.beforeMouseUp, [setting.treeId, node], true)) {
				tools.apply(setting.callback.onMouseUp, [event, setting.treeId, node]);
			}
			return true;
		},
		onZTreeDblclick: function(event, node) {
			var setting = data.getSetting(event.data.treeId);
			if (tools.apply(setting.callback.beforeDblClick, [setting.treeId, node], true)) {
				tools.apply(setting.callback.onDblClick, [event, setting.treeId, node]);
			}
			return true;
		},
		onZTreeContextmenu: function(event, node) {
			var setting = data.getSetting(event.data.treeId);
			if (tools.apply(setting.callback.beforeRightClick, [setting.treeId, node], true)) {
				tools.apply(setting.callback.onRightClick, [event, setting.treeId, node]);
			}
			return (typeof setting.callback.onRightClick) != "function";
		},
		onSelectStart: function(e){
			var n = e.originalEvent.srcElement.nodeName.toLowerCase();
			return (n === "input" || n === "textarea" );
		}
	},
	//method of tools for zTree
	tools = {
		apply: function(fun, param, defaultValue) {
			if ((typeof fun) == "function") {
				return fun.apply(zt, param?param:[]);
			}
			return defaultValue;
		},
		canAsync: function(setting, node) {
			var childKey = setting.data.key.children;
			return (setting.async.enable && node && node.isParent && !(node.zAsync || (node[childKey] && node[childKey].length > 0)));
		},
		clone: function (obj){
			if (obj === null) return null;
			var o = tools.isArray(obj) ? [] : {};
			for(var i in obj){
				o[i] = (obj[i] instanceof Date) ? new Date(obj[i].getTime()) : (typeof obj[i] === "object" ? arguments.callee(obj[i]) : obj[i]);
			}
			return o;
		},
		eqs: function(str1, str2) {
			return str1.toLowerCase() === str2.toLowerCase();
		},
		isArray: function(arr) {
			return Object.prototype.toString.apply(arr) === "[object Array]";
		},
		$: function(node, exp, setting) {
			if (!!exp && typeof exp != "string") {
				setting = exp;
				exp = "";
			}
			if (typeof node == "string") {
				return $(node, setting ? setting.treeObj.get(0).ownerDocument : null);
			} else {
				return $("#" + node.tId + exp, setting ? setting.treeObj : null);
			}
		},
		getMDom: function (setting, curDom, targetExpr) {
			if (!curDom) return null;
			while (curDom && curDom.id !== setting.treeId) {
				for (var i=0, l=targetExpr.length; curDom.tagName && i<l; i++) {
					if (tools.eqs(curDom.tagName, targetExpr[i].tagName) && curDom.getAttribute(targetExpr[i].attrName) !== null) {
						return curDom;
					}
				}
				curDom = curDom.parentNode;
			}
			return null;
		},
		getNodeMainDom:function(target) {
			return ($(target).parent("li").get(0) || $(target).parentsUntil("li").parent().get(0));
		},
		isChildOrSelf: function(dom, parentId) {
			return ( $(dom).closest("#" + parentId).length> 0 );
		},
		uCanDo: function(setting, e) {
			return true;
		}
	},
	//method of operate ztree dom
	view = {
		addNodes: function(setting, parentNode, index, newNodes, isSilent) {
			if (setting.data.keep.leaf && parentNode && !parentNode.isParent) {
				return;
			}
			if (!tools.isArray(newNodes)) {
				newNodes = [newNodes];
			}
			if (setting.data.simpleData.enable) {
				newNodes = data.transformTozTreeFormat(setting, newNodes);
			}
			if (parentNode) {
				var target_switchObj = $$(parentNode, consts.id.SWITCH, setting),
				target_icoObj = $$(parentNode, consts.id.ICON, setting),
				target_ulObj = $$(parentNode, consts.id.UL, setting);

				if (!parentNode.open) {
					view.replaceSwitchClass(parentNode, target_switchObj, consts.folder.CLOSE);
					view.replaceIcoClass(parentNode, target_icoObj, consts.folder.CLOSE);
					parentNode.open = false;
					target_ulObj.css({
						"display": "none"
					});
				}

				data.addNodesData(setting, parentNode, index, newNodes);
				view.createNodes(setting, parentNode.level + 1, newNodes, parentNode, index);
				if (!isSilent) {
					view.expandCollapseParentNode(setting, parentNode, true);
				}
			} else {
				data.addNodesData(setting, data.getRoot(setting), index, newNodes);
				view.createNodes(setting, 0, newNodes, null, index);
			}
		},
		appendNodes: function(setting, level, nodes, parentNode, index, initFlag, openFlag) {
			if (!nodes) return [];
			var html = [],
			childKey = setting.data.key.children;

			var tmpPNode = (parentNode) ? parentNode: data.getRoot(setting),
				tmpPChild = tmpPNode[childKey],
				isFirstNode, isLastNode;

			if (!tmpPChild || index >= tmpPChild.length) {
				index = -1;
			}

			for (var i = 0, l = nodes.length; i < l; i++) {
				var node = nodes[i];
				if (initFlag) {
					isFirstNode = ((index===0 || tmpPChild.length == nodes.length) && (i == 0));
					isLastNode = (index < 0 && i == (nodes.length - 1));
					data.initNode(setting, level, node, parentNode, isFirstNode, isLastNode, openFlag);
					data.addNodeCache(setting, node);
				}

				var childHtml = [];
				if (node[childKey] && node[childKey].length > 0) {
					//make child html first, because checkType
					childHtml = view.appendNodes(setting, level + 1, node[childKey], node, -1, initFlag, openFlag && node.open);
				}
				if (openFlag) {

					view.makeDOMNodeMainBefore(html, setting, node);
					view.makeDOMNodeLine(html, setting, node);
					data.getBeforeA(setting, node, html);
					view.makeDOMNodeNameBefore(html, setting, node);
					data.getInnerBeforeA(setting, node, html);
					view.makeDOMNodeIcon(html, setting, node);
					data.getInnerAfterA(setting, node, html);
					view.makeDOMNodeNameAfter(html, setting, node);
					data.getAfterA(setting, node, html);
					if (node.isParent && node.open) {
						view.makeUlHtml(setting, node, html, childHtml.join(''));
					}
					view.makeDOMNodeMainAfter(html, setting, node);
					data.addCreatedNode(setting, node);
				}
			}
			return html;
		},
		appendParentULDom: function(setting, node) {
			var html = [],
			nObj = $$(node, setting);
			if (!nObj.get(0) && !!node.parentTId) {
				view.appendParentULDom(setting, node.getParentNode());
				nObj = $$(node, setting);
			}
			var ulObj = $$(node, consts.id.UL, setting);
			if (ulObj.get(0)) {
				ulObj.remove();
			}
			var childKey = setting.data.key.children,
			childHtml = view.appendNodes(setting, node.level+1, node[childKey], node, -1, false, true);
			view.makeUlHtml(setting, node, html, childHtml.join(''));
			nObj.append(html.join(''));
		},
		asyncNode: function(setting, node, isSilent, callback) {
			var i, l;
			if (node && !node.isParent) {
				tools.apply(callback);
				return false;
			} else if (node && node.isAjaxing) {
				return false;
			} else if (tools.apply(setting.callback.beforeAsync, [setting.treeId, node], true) == false) {
				tools.apply(callback);
				return false;
			}
			if (node) {
				node.isAjaxing = true;
				var icoObj = $$(node, consts.id.ICON, setting);
				icoObj.attr({"style":"", "class":consts.className.BUTTON + " " + consts.className.ICO_LOADING});
			}

			var tmpParam = {};
			for (i = 0, l = setting.async.autoParam.length; node && i < l; i++) {
				var pKey = setting.async.autoParam[i].split("="), spKey = pKey;
				if (pKey.length>1) {
					spKey = pKey[1];
					pKey = pKey[0];
				}
				tmpParam[spKey] = node[pKey];
			}
			if (tools.isArray(setting.async.otherParam)) {
				for (i = 0, l = setting.async.otherParam.length; i < l; i += 2) {
					tmpParam[setting.async.otherParam[i]] = setting.async.otherParam[i + 1];
				}
			} else {
				for (var p in setting.async.otherParam) {
					tmpParam[p] = setting.async.otherParam[p];
				}
			}

			var _tmpV = data.getRoot(setting)._ver;
			$.ajax({
				contentType: setting.async.contentType,
                cache: false,
				type: setting.async.type,
				url: tools.apply(setting.async.url, [setting.treeId, node], setting.async.url),
				data: tmpParam,
				dataType: setting.async.dataType,
				success: function(msg) {
					if (_tmpV != data.getRoot(setting)._ver) {
						return;
					}
					var newNodes = [];
					try {
						if (!msg || msg.length == 0) {
							newNodes = [];
						} else if (typeof msg == "string") {
							newNodes = eval("(" + msg + ")");
						} else {
							newNodes = msg;
						}
					} catch(err) {
						newNodes = msg;
					}

					if (node) {
						node.isAjaxing = null;
						node.zAsync = true;
					}
					view.setNodeLineIcos(setting, node);
					if (newNodes && newNodes !== "") {
						newNodes = tools.apply(setting.async.dataFilter, [setting.treeId, node, newNodes], newNodes);
						view.addNodes(setting, node, -1, !!newNodes ? tools.clone(newNodes) : [], !!isSilent);
					} else {
						view.addNodes(setting, node, -1, [], !!isSilent);
					}
					setting.treeObj.trigger(consts.event.ASYNC_SUCCESS, [setting.treeId, node, msg]);
					tools.apply(callback);
				},
				error: function(XMLHttpRequest, textStatus, errorThrown) {
					if (_tmpV != data.getRoot(setting)._ver) {
						return;
					}
					if (node) node.isAjaxing = null;
					view.setNodeLineIcos(setting, node);
					setting.treeObj.trigger(consts.event.ASYNC_ERROR, [setting.treeId, node, XMLHttpRequest, textStatus, errorThrown]);
				}
			});
			return true;
		},
		cancelPreSelectedNode: function (setting, node, excludeNode) {
			var list = data.getRoot(setting).curSelectedList,
				i, n;
			for (i=list.length-1; i>=0; i--) {
				n = list[i];
				if (node === n || (!node && (!excludeNode || excludeNode !== n))) {
					$$(n, consts.id.A, setting).removeClass(consts.node.CURSELECTED);
					if (node) {
						data.removeSelectedNode(setting, node);
						break;
					} else {
						list.splice(i, 1);
						setting.treeObj.trigger(consts.event.UNSELECTED, [setting.treeId, n]);
					}
				}
			}
		},
		createNodeCallback: function(setting) {
			if (!!setting.callback.onNodeCreated || !!setting.view.addDiyDom) {
				var root = data.getRoot(setting);
				while (root.createdNodes.length>0) {
					var node = root.createdNodes.shift();
					tools.apply(setting.view.addDiyDom, [setting.treeId, node]);
					if (!!setting.callback.onNodeCreated) {
						setting.treeObj.trigger(consts.event.NODECREATED, [setting.treeId, node]);
					}
				}
			}
		},
		createNodes: function(setting, level, nodes, parentNode, index) {
			if (!nodes || nodes.length == 0) return;
			var root = data.getRoot(setting),
			childKey = setting.data.key.children,
			openFlag = !parentNode || parentNode.open || !!$$(parentNode[childKey][0], setting).get(0);
			root.createdNodes = [];
			var zTreeHtml = view.appendNodes(setting, level, nodes, parentNode, index, true, openFlag),
				parentObj, nextObj;

			if (!parentNode) {
				parentObj = setting.treeObj;
				//setting.treeObj.append(zTreeHtml.join(''));
			} else {
				var ulObj = $$(parentNode, consts.id.UL, setting);
				if (ulObj.get(0)) {
					parentObj = ulObj;
					//ulObj.append(zTreeHtml.join(''));
				}
			}
			if (parentObj) {
				if (index >= 0) {
					nextObj = parentObj.children()[index];
				}
				if (index >=0 && nextObj) {
					$(nextObj).before(zTreeHtml.join(''));
				} else {
					parentObj.append(zTreeHtml.join(''));
				}
			}

			view.createNodeCallback(setting);
		},
		destroy: function(setting) {
			if (!setting) return;
			data.initCache(setting);
			data.initRoot(setting);
			event.unbindTree(setting);
			event.unbindEvent(setting);
			setting.treeObj.empty();
			delete settings[setting.treeId];
		},
		expandCollapseNode: function(setting, node, expandFlag, animateFlag, callback) {
			var root = data.getRoot(setting),
			childKey = setting.data.key.children;
			if (!node) {
				tools.apply(callback, []);
				return;
			}
			if (root.expandTriggerFlag) {
				var _callback = callback;
				callback = function(){
					if (_callback) _callback();
					if (node.open) {
						setting.treeObj.trigger(consts.event.EXPAND, [setting.treeId, node]);
					} else {
						setting.treeObj.trigger(consts.event.COLLAPSE, [setting.treeId, node]);
					}
				};
				root.expandTriggerFlag = false;
			}
			if (!node.open && node.isParent && ((!$$(node, consts.id.UL, setting).get(0)) || (node[childKey] && node[childKey].length>0 && !$$(node[childKey][0], setting).get(0)))) {
				view.appendParentULDom(setting, node);
				view.createNodeCallback(setting);
			}
			if (node.open == expandFlag) {
				tools.apply(callback, []);
				return;
			}
			var ulObj = $$(node, consts.id.UL, setting),
			switchObj = $$(node, consts.id.SWITCH, setting),
			icoObj = $$(node, consts.id.ICON, setting);

			if (node.isParent) {
				node.open = !node.open;
				if (node.iconOpen && node.iconClose) {
					icoObj.attr("style", view.makeNodeIcoStyle(setting, node));
				}

				if (node.open) {
					view.replaceSwitchClass(node, switchObj, consts.folder.OPEN);
					view.replaceIcoClass(node, icoObj, consts.folder.OPEN);
					if (animateFlag == false || setting.view.expandSpeed == "") {
						ulObj.show();
						tools.apply(callback, []);
					} else {
						if (node[childKey] && node[childKey].length > 0) {
							ulObj.slideDown(setting.view.expandSpeed, callback);
						} else {
							ulObj.show();
							tools.apply(callback, []);
						}
					}
				} else {
					view.replaceSwitchClass(node, switchObj, consts.folder.CLOSE);
					view.replaceIcoClass(node, icoObj, consts.folder.CLOSE);
					if (animateFlag == false || setting.view.expandSpeed == "" || !(node[childKey] && node[childKey].length > 0)) {
						ulObj.hide();
						tools.apply(callback, []);
					} else {
						ulObj.slideUp(setting.view.expandSpeed, callback);
					}
				}
			} else {
				tools.apply(callback, []);
			}
		},
		expandCollapseParentNode: function(setting, node, expandFlag, animateFlag, callback) {
			if (!node) return;
			if (!node.parentTId) {
				view.expandCollapseNode(setting, node, expandFlag, animateFlag, callback);
				return;
			} else {
				view.expandCollapseNode(setting, node, expandFlag, animateFlag);
			}
			if (node.parentTId) {
				view.expandCollapseParentNode(setting, node.getParentNode(), expandFlag, animateFlag, callback);
			}
		},
		expandCollapseSonNode: function(setting, node, expandFlag, animateFlag, callback) {
			var root = data.getRoot(setting),
			childKey = setting.data.key.children,
			treeNodes = (node) ? node[childKey]: root[childKey],
			selfAnimateSign = (node) ? false : animateFlag,
			expandTriggerFlag = data.getRoot(setting).expandTriggerFlag;
			data.getRoot(setting).expandTriggerFlag = false;
			if (treeNodes) {
				for (var i = 0, l = treeNodes.length; i < l; i++) {
					if (treeNodes[i]) view.expandCollapseSonNode(setting, treeNodes[i], expandFlag, selfAnimateSign);
				}
			}
			data.getRoot(setting).expandTriggerFlag = expandTriggerFlag;
			view.expandCollapseNode(setting, node, expandFlag, animateFlag, callback );
		},
		isSelectedNode: function (setting, node) {
			if (!node) {
				return false;
			}
			var list = data.getRoot(setting).curSelectedList,
				i;
			for (i=list.length-1; i>=0; i--) {
				if (node === list[i]) {
					return true;
				}
			}
			return false;
		},
		makeDOMNodeIcon: function(html, setting, node) {
			var nameStr = data.getNodeName(setting, node),
			name = setting.view.nameIsHTML ? nameStr : nameStr.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');
			html.push("<span id='", node.tId, consts.id.ICON,
				"' title='' treeNode", consts.id.ICON," class='", view.makeNodeIcoClass(setting, node),
				"' style='", view.makeNodeIcoStyle(setting, node), "'></span><span id='", node.tId, consts.id.SPAN,
				"' class='", consts.className.NAME,
				"'>",name,"</span>");
		},
		makeDOMNodeLine: function(html, setting, node) {
			html.push("<span id='", node.tId, consts.id.SWITCH,	"' title='' class='", view.makeNodeLineClass(setting, node), "' treeNode", consts.id.SWITCH,"></span>");
		},
		makeDOMNodeMainAfter: function(html, setting, node) {
			html.push("</li>");
		},
		makeDOMNodeMainBefore: function(html, setting, node) {
			html.push("<li id='", node.tId, "' class='", consts.className.LEVEL, node.level,"' tabindex='0' hidefocus='true' treenode>");
		},
		makeDOMNodeNameAfter: function(html, setting, node) {
			html.push("</a>");
		},
		makeDOMNodeNameBefore: function(html, setting, node) {
			var title = data.getNodeTitle(setting, node),
			url = view.makeNodeUrl(setting, node),
			fontcss = view.makeNodeFontCss(setting, node),
			fontStyle = [];
			for (var f in fontcss) {
				fontStyle.push(f, ":", fontcss[f], ";");
			}
			html.push("<a id='", node.tId, consts.id.A, "' class='", consts.className.LEVEL, node.level,"' treeNode", consts.id.A," onclick=\"", (node.click || ''),
				"\" ", ((url != null && url.length > 0) ? "href='" + url + "'" : ""), " target='",view.makeNodeTarget(node),"' style='", fontStyle.join(''),
				"'");
			if (tools.apply(setting.view.showTitle, [setting.treeId, node], setting.view.showTitle) && title) {html.push("title='", title.replace(/'/g,"&#39;").replace(/</g,'&lt;').replace(/>/g,'&gt;'),"'");}
			html.push(">");
		},
		makeNodeFontCss: function(setting, node) {
			var fontCss = tools.apply(setting.view.fontCss, [setting.treeId, node], setting.view.fontCss);
			return (fontCss && ((typeof fontCss) != "function")) ? fontCss : {};
		},
		makeNodeIcoClass: function(setting, node) {
			var icoCss = ["ico"];
			if (!node.isAjaxing) {
				icoCss[0] = (node.iconSkin ? node.iconSkin + "_" : "") + icoCss[0];
				if (node.isParent) {
					icoCss.push(node.open ? consts.folder.OPEN : consts.folder.CLOSE);
				} else {
					icoCss.push(consts.folder.DOCU);
				}
			}
			return consts.className.BUTTON + " " + icoCss.join('_');
		},
		makeNodeIcoStyle: function(setting, node) {
			var icoStyle = [];
			if (!node.isAjaxing) {
				var icon = (node.isParent && node.iconOpen && node.iconClose) ? (node.open ? node.iconOpen : node.iconClose) : node[setting.data.key.icon];
				if (icon) icoStyle.push("background:url(", icon, ") 0 0 no-repeat;");
				if (setting.view.showIcon == false || !tools.apply(setting.view.showIcon, [setting.treeId, node], true)) {
					icoStyle.push("width:0px;height:0px;");
				}
			}
			return icoStyle.join('');
		},
		makeNodeLineClass: function(setting, node) {
			var lineClass = [];
			if (setting.view.showLine) {
				if (node.level == 0 && node.isFirstNode && node.isLastNode) {
					lineClass.push(consts.line.ROOT);
				} else if (node.level == 0 && node.isFirstNode) {
					lineClass.push(consts.line.ROOTS);
				} else if (node.isLastNode) {
					lineClass.push(consts.line.BOTTOM);
				} else {
					lineClass.push(consts.line.CENTER);
				}
			} else {
				lineClass.push(consts.line.NOLINE);
			}
			if (node.isParent) {
				lineClass.push(node.open ? consts.folder.OPEN : consts.folder.CLOSE);
			} else {
				lineClass.push(consts.folder.DOCU);
			}
			return view.makeNodeLineClassEx(node) + lineClass.join('_');
		},
		makeNodeLineClassEx: function(node) {
			return consts.className.BUTTON + " " + consts.className.LEVEL + node.level + " " + consts.className.SWITCH + " ";
		},
		makeNodeTarget: function(node) {
			return (node.target || "_blank");
		},
		makeNodeUrl: function(setting, node) {
			var urlKey = setting.data.key.url;
			return node[urlKey] ? node[urlKey] : null;
		},
		makeUlHtml: function(setting, node, html, content) {
			html.push("<ul id='", node.tId, consts.id.UL, "' class='", consts.className.LEVEL, node.level, " ", view.makeUlLineClass(setting, node), "' style='display:", (node.open ? "block": "none"),"'>");
			html.push(content);
			html.push("</ul>");
		},
		makeUlLineClass: function(setting, node) {
			return ((setting.view.showLine && !node.isLastNode) ? consts.line.LINE : "");
		},
		removeChildNodes: function(setting, node) {
			if (!node) return;
			var childKey = setting.data.key.children,
			nodes = node[childKey];
			if (!nodes) return;

			for (var i = 0, l = nodes.length; i < l; i++) {
				data.removeNodeCache(setting, nodes[i]);
			}
			data.removeSelectedNode(setting);
			delete node[childKey];

			if (!setting.data.keep.parent) {
				node.isParent = false;
				node.open = false;
				var tmp_switchObj = $$(node, consts.id.SWITCH, setting),
				tmp_icoObj = $$(node, consts.id.ICON, setting);
				view.replaceSwitchClass(node, tmp_switchObj, consts.folder.DOCU);
				view.replaceIcoClass(node, tmp_icoObj, consts.folder.DOCU);
				$$(node, consts.id.UL, setting).remove();
			} else {
				$$(node, consts.id.UL, setting).empty();
			}
		},
		setFirstNode: function(setting, parentNode) {
			var childKey = setting.data.key.children, childLength = parentNode[childKey].length;
			if ( childLength > 0) {
				parentNode[childKey][0].isFirstNode = true;
			}
		},
		setLastNode: function(setting, parentNode) {
			var childKey = setting.data.key.children, childLength = parentNode[childKey].length;
			if ( childLength > 0) {
				parentNode[childKey][childLength - 1].isLastNode = true;
			}
		},
		removeNode: function(setting, node) {
			var root = data.getRoot(setting),
			childKey = setting.data.key.children,
			parentNode = (node.parentTId) ? node.getParentNode() : root;

			node.isFirstNode = false;
			node.isLastNode = false;
			node.getPreNode = function() {return null;};
			node.getNextNode = function() {return null;};

			if (!data.getNodeCache(setting, node.tId)) {
				return;
			}

			$$(node, setting).remove();
			data.removeNodeCache(setting, node);
			data.removeSelectedNode(setting, node);

			for (var i = 0, l = parentNode[childKey].length; i < l; i++) {
				if (parentNode[childKey][i].tId == node.tId) {
					parentNode[childKey].splice(i, 1);
					break;
				}
			}
			view.setFirstNode(setting, parentNode);
			view.setLastNode(setting, parentNode);

			var tmp_ulObj,tmp_switchObj,tmp_icoObj,
			childLength = parentNode[childKey].length;

			//repair nodes old parent
			if (!setting.data.keep.parent && childLength == 0) {
				//old parentNode has no child nodes
				parentNode.isParent = false;
				parentNode.open = false;
				tmp_ulObj = $$(parentNode, consts.id.UL, setting);
				tmp_switchObj = $$(parentNode, consts.id.SWITCH, setting);
				tmp_icoObj = $$(parentNode, consts.id.ICON, setting);
				view.replaceSwitchClass(parentNode, tmp_switchObj, consts.folder.DOCU);
				view.replaceIcoClass(parentNode, tmp_icoObj, consts.folder.DOCU);
				tmp_ulObj.css("display", "none");

			} else if (setting.view.showLine && childLength > 0) {
				//old parentNode has child nodes
				var newLast = parentNode[childKey][childLength - 1];
				tmp_ulObj = $$(newLast, consts.id.UL, setting);
				tmp_switchObj = $$(newLast, consts.id.SWITCH, setting);
				tmp_icoObj = $$(newLast, consts.id.ICON, setting);
				if (parentNode == root) {
					if (parentNode[childKey].length == 1) {
						//node was root, and ztree has only one root after move node
						view.replaceSwitchClass(newLast, tmp_switchObj, consts.line.ROOT);
					} else {
						var tmp_first_switchObj = $$(parentNode[childKey][0], consts.id.SWITCH, setting);
						view.replaceSwitchClass(parentNode[childKey][0], tmp_first_switchObj, consts.line.ROOTS);
						view.replaceSwitchClass(newLast, tmp_switchObj, consts.line.BOTTOM);
					}
				} else {
					view.replaceSwitchClass(newLast, tmp_switchObj, consts.line.BOTTOM);
				}
				tmp_ulObj.removeClass(consts.line.LINE);
			}
		},
		replaceIcoClass: function(node, obj, newName) {
			if (!obj || node.isAjaxing) return;
			var tmpName = obj.attr("class");
			if (tmpName == undefined) return;
			var tmpList = tmpName.split("_");
			switch (newName) {
				case consts.folder.OPEN:
				case consts.folder.CLOSE:
				case consts.folder.DOCU:
					tmpList[tmpList.length-1] = newName;
					break;
			}
			obj.attr("class", tmpList.join("_"));
		},
		replaceSwitchClass: function(node, obj, newName) {
			if (!obj) return;
			var tmpName = obj.attr("class");
			if (tmpName == undefined) return;
			var tmpList = tmpName.split("_");
			switch (newName) {
				case consts.line.ROOT:
				case consts.line.ROOTS:
				case consts.line.CENTER:
				case consts.line.BOTTOM:
				case consts.line.NOLINE:
					tmpList[0] = view.makeNodeLineClassEx(node) + newName;
					break;
				case consts.folder.OPEN:
				case consts.folder.CLOSE:
				case consts.folder.DOCU:
					tmpList[1] = newName;
					break;
			}
			obj.attr("class", tmpList.join("_"));
			if (newName !== consts.folder.DOCU) {
				obj.removeAttr("disabled");
			} else {
				obj.attr("disabled", "disabled");
			}
		},
		selectNode: function(setting, node, addFlag) {
			if (!addFlag) {
				view.cancelPreSelectedNode(setting, null, node);
			}
			$$(node, consts.id.A, setting).addClass(consts.node.CURSELECTED);
			data.addSelectedNode(setting, node);
			setting.treeObj.trigger(consts.event.SELECTED, [setting.treeId, node]);
		},
		setNodeFontCss: function(setting, treeNode) {
			var aObj = $$(treeNode, consts.id.A, setting),
			fontCss = view.makeNodeFontCss(setting, treeNode);
			if (fontCss) {
				aObj.css(fontCss);
			}
		},
		setNodeLineIcos: function(setting, node) {
			if (!node) return;
			var switchObj = $$(node, consts.id.SWITCH, setting),
			ulObj = $$(node, consts.id.UL, setting),
			icoObj = $$(node, consts.id.ICON, setting),
			ulLine = view.makeUlLineClass(setting, node);
			if (ulLine.length==0) {
				ulObj.removeClass(consts.line.LINE);
			} else {
				ulObj.addClass(ulLine);
			}
			switchObj.attr("class", view.makeNodeLineClass(setting, node));
			if (node.isParent) {
				switchObj.removeAttr("disabled");
			} else {
				switchObj.attr("disabled", "disabled");
			}
			icoObj.removeAttr("style");
			icoObj.attr("style", view.makeNodeIcoStyle(setting, node));
			icoObj.attr("class", view.makeNodeIcoClass(setting, node));
		},
		setNodeName: function(setting, node) {
			var title = data.getNodeTitle(setting, node),
			nObj = $$(node, consts.id.SPAN, setting);
			nObj.empty();
			if (setting.view.nameIsHTML) {
				nObj.html(data.getNodeName(setting, node));
			} else {
				nObj.text(data.getNodeName(setting, node));
			}
			if (tools.apply(setting.view.showTitle, [setting.treeId, node], setting.view.showTitle)) {
				var aObj = $$(node, consts.id.A, setting);
				aObj.attr("title", !title ? "" : title);
			}
		},
		setNodeTarget: function(setting, node) {
			var aObj = $$(node, consts.id.A, setting);
			aObj.attr("target", view.makeNodeTarget(node));
		},
		setNodeUrl: function(setting, node) {
			var aObj = $$(node, consts.id.A, setting),
			url = view.makeNodeUrl(setting, node);
			if (url == null || url.length == 0) {
				aObj.removeAttr("href");
			} else {
				aObj.attr("href", url);
			}
		},
		switchNode: function(setting, node) {
			if (node.open || !tools.canAsync(setting, node)) {
				view.expandCollapseNode(setting, node, !node.open);
			} else if (setting.async.enable) {
				if (!view.asyncNode(setting, node)) {
					view.expandCollapseNode(setting, node, !node.open);
					return;
				}
			} else if (node) {
				view.expandCollapseNode(setting, node, !node.open);
			}
		}
	};
	// zTree defind
	$.fn.zTree = {
		consts : _consts,
		_z : {
			tools: tools,
			view: view,
			event: event,
			data: data
		},
		getZTreeObj: function(treeId) {
			var o = data.getZTreeTools(treeId);
			return o ? o : null;
		},
		destroy: function(treeId) {
			if (!!treeId && treeId.length > 0) {
				view.destroy(data.getSetting(treeId));
			} else {
				for(var s in settings) {
					view.destroy(settings[s]);
				}
			}
		},
		init: function(obj, zSetting, zNodes) {
			var setting = tools.clone(_setting);
			$.extend(true, setting, zSetting);
			setting.treeId = obj.attr("id");
			setting.treeObj = obj;
			setting.treeObj.empty();
			settings[setting.treeId] = setting;
			//For some older browser,(e.g., ie6)
			if(typeof document.body.style.maxHeight === "undefined") {
				setting.view.expandSpeed = "";
			}
			data.initRoot(setting);
			var root = data.getRoot(setting),
			childKey = setting.data.key.children;
			zNodes = zNodes ? tools.clone(tools.isArray(zNodes)? zNodes : [zNodes]) : [];
			if (setting.data.simpleData.enable) {
				root[childKey] = data.transformTozTreeFormat(setting, zNodes);
			} else {
				root[childKey] = zNodes;
			}

			data.initCache(setting);
			event.unbindTree(setting);
			event.bindTree(setting);
			event.unbindEvent(setting);
			event.bindEvent(setting);

			var zTreeTools = {
				setting : setting,
				addNodes : function(parentNode, index, newNodes, isSilent) {
					if (!parentNode) parentNode = null;
					if (parentNode && !parentNode.isParent && setting.data.keep.leaf) return null;

					var i = parseInt(index, 10);
					if (isNaN(i)) {
						isSilent = !!newNodes;
						newNodes = index;
						index = -1;
					} else {
						index = i;
					}
					if (!newNodes) return null;


					var xNewNodes = tools.clone(tools.isArray(newNodes)? newNodes: [newNodes]);
					function addCallback() {
						view.addNodes(setting, parentNode, index, xNewNodes, (isSilent==true));
					}

					if (tools.canAsync(setting, parentNode)) {
						view.asyncNode(setting, parentNode, isSilent, addCallback);
					} else {
						addCallback();
					}
					return xNewNodes;
				},
				cancelSelectedNode : function(node) {
					view.cancelPreSelectedNode(setting, node);
				},
				destroy : function() {
					view.destroy(setting);
				},
				expandAll : function(expandFlag) {
					expandFlag = !!expandFlag;
					view.expandCollapseSonNode(setting, null, expandFlag, true);
					return expandFlag;
				},
				expandNode : function(node, expandFlag, sonSign, focus, callbackFlag) {
					if (!node || !node.isParent) return null;
					if (expandFlag !== true && expandFlag !== false) {
						expandFlag = !node.open;
					}
					callbackFlag = !!callbackFlag;

					if (callbackFlag && expandFlag && (tools.apply(setting.callback.beforeExpand, [setting.treeId, node], true) == false)) {
						return null;
					} else if (callbackFlag && !expandFlag && (tools.apply(setting.callback.beforeCollapse, [setting.treeId, node], true) == false)) {
						return null;
					}
					if (expandFlag && node.parentTId) {
						view.expandCollapseParentNode(setting, node.getParentNode(), expandFlag, false);
					}
					if (expandFlag === node.open && !sonSign) {
						return null;
					}

					data.getRoot(setting).expandTriggerFlag = callbackFlag;
					if (!tools.canAsync(setting, node) && sonSign) {
						view.expandCollapseSonNode(setting, node, expandFlag, true, showNodeFocus);
					} else {
						node.open = !expandFlag;
						view.switchNode(this.setting, node);
						showNodeFocus();
					}
					return expandFlag;

					function showNodeFocus() {
						var a = $$(node, setting).get(0);
						if (a && focus !== false) {
							if (a.scrollIntoView) {
								a.scrollIntoView();
							} else {
								try{a.focus().blur();}catch(e){}
							}
						}
					}
				},
				getNodes : function() {
					return data.getNodes(setting);
				},
				getNodeByParam : function(key, value, parentNode) {
					if (!key) return null;
					return data.getNodeByParam(setting, parentNode?parentNode[setting.data.key.children]:data.getNodes(setting), key, value);
				},
				getNodeByTId : function(tId) {
					return data.getNodeCache(setting, tId);
				},
				getNodesByParam : function(key, value, parentNode) {
					if (!key) return null;
					return data.getNodesByParam(setting, parentNode?parentNode[setting.data.key.children]:data.getNodes(setting), key, value);
				},
				getNodesByParamFuzzy : function(key, value, parentNode) {
					if (!key) return null;
					return data.getNodesByParamFuzzy(setting, parentNode?parentNode[setting.data.key.children]:data.getNodes(setting), key, value);
				},
				getNodesByFilter: function(filter, isSingle, parentNode, invokeParam) {
					isSingle = !!isSingle;
					if (!filter || (typeof filter != "function")) return (isSingle ? null : []);
					return data.getNodesByFilter(setting, parentNode?parentNode[setting.data.key.children]:data.getNodes(setting), filter, isSingle, invokeParam);
				},
				getNodeIndex : function(node) {
					if (!node) return null;
					var childKey = setting.data.key.children,
					parentNode = (node.parentTId) ? node.getParentNode() : data.getRoot(setting);
					for (var i=0, l = parentNode[childKey].length; i < l; i++) {
						if (parentNode[childKey][i] == node) return i;
					}
					return -1;
				},
				getSelectedNodes : function() {
					var r = [], list = data.getRoot(setting).curSelectedList;
					for (var i=0, l=list.length; i<l; i++) {
						r.push(list[i]);
					}
					return r;
				},
				isSelectedNode : function(node) {
					return data.isSelectedNode(setting, node);
				},
				reAsyncChildNodes : function(parentNode, reloadType, isSilent) {
					if (!this.setting.async.enable) return;
					var isRoot = !parentNode;
					if (isRoot) {
						parentNode = data.getRoot(setting);
					}
					if (reloadType=="refresh") {
						var childKey = this.setting.data.key.children;
						for (var i = 0, l = parentNode[childKey] ? parentNode[childKey].length : 0; i < l; i++) {
							data.removeNodeCache(setting, parentNode[childKey][i]);
						}
						data.removeSelectedNode(setting);
						parentNode[childKey] = [];
						if (isRoot) {
							this.setting.treeObj.empty();
						} else {
							var ulObj = $$(parentNode, consts.id.UL, setting);
							ulObj.empty();
						}
					}
					view.asyncNode(this.setting, isRoot? null:parentNode, !!isSilent);
				},
				refresh : function() {
					this.setting.treeObj.empty();
					var root = data.getRoot(setting),
					nodes = root[setting.data.key.children]
					data.initRoot(setting);
					root[setting.data.key.children] = nodes
					data.initCache(setting);
					view.createNodes(setting, 0, root[setting.data.key.children], null, -1);
				},
				removeChildNodes : function(node) {
					if (!node) return null;
					var childKey = setting.data.key.children,
					nodes = node[childKey];
					view.removeChildNodes(setting, node);
					return nodes ? nodes : null;
				},
				removeNode : function(node, callbackFlag) {
					if (!node) return;
					callbackFlag = !!callbackFlag;
					if (callbackFlag && tools.apply(setting.callback.beforeRemove, [setting.treeId, node], true) == false) return;
					view.removeNode(setting, node);
					if (callbackFlag) {
						this.setting.treeObj.trigger(consts.event.REMOVE, [setting.treeId, node]);
					}
				},
				selectNode : function(node, addFlag) {
					if (!node) return;
					if (tools.uCanDo(setting)) {
						addFlag = setting.view.selectedMulti && addFlag;
						if (node.parentTId) {
							view.expandCollapseParentNode(setting, node.getParentNode(), true, false, showNodeFocus);
						} else {
							try{$$(node, setting).focus().blur();}catch(e){}
						}
						view.selectNode(setting, node, addFlag);
					}

					function showNodeFocus() {
						var a = $$(node, setting).get(0);
						if (a) {
							if (a.scrollIntoView) {
								a.scrollIntoView(false);
							} else {
								try{a.focus().blur();}catch(e){}
							}
						}
					}
				},
				transformTozTreeNodes : function(simpleNodes) {
					return data.transformTozTreeFormat(setting, simpleNodes);
				},
				transformToArray : function(nodes) {
					return data.transformToArrayFormat(setting, nodes);
				},
				updateNode : function(node, checkTypeFlag) {
					if (!node) return;
					var nObj = $$(node, setting);
					if (nObj.get(0) && tools.uCanDo(setting)) {
						view.setNodeName(setting, node);
						view.setNodeTarget(setting, node);
						view.setNodeUrl(setting, node);
						view.setNodeLineIcos(setting, node);
						view.setNodeFontCss(setting, node);
					}
				}
			}
			root.treeTools = zTreeTools;
			data.setZTreeTools(setting, zTreeTools);

			if (root[childKey] && root[childKey].length > 0) {
				view.createNodes(setting, 0, root[childKey], null, -1);
			} else if (setting.async.enable && setting.async.url && setting.async.url !== '') {
				view.asyncNode(setting);
			}
			return zTreeTools;
		}
	};

	var zt = $.fn.zTree,
	$$ = tools.$,
	consts = zt.consts;
})(jQuery);;(function($){
	//default consts of excheck
	var _consts = {
		event: {
			CHECK: "ztree_check"
		},
		id: {
			CHECK: "_check"
		},
		checkbox: {
			STYLE: "checkbox",
			DEFAULT: "chk",
			DISABLED: "disable",
			FALSE: "false",
			TRUE: "true",
			FULL: "full",
			PART: "part",
			FOCUS: "focus"
		},
		radio: {
			STYLE: "radio",
			TYPE_ALL: "all",
			TYPE_LEVEL: "level"
		}
	},
	//default setting of excheck
	_setting = {
		check: {
			enable: false,
			autoCheckTrigger: false,
			chkStyle: _consts.checkbox.STYLE,
			nocheckInherit: false,
			chkDisabledInherit: false,
			radioType: _consts.radio.TYPE_LEVEL,
			chkboxType: {
				"Y": "ps",
				"N": "ps"
			}
		},
		data: {
			key: {
				checked: "checked"
			}
		},
		callback: {
			beforeCheck:null,
			onCheck:null
		}
	},
	//default root of excheck
	_initRoot = function (setting) {
		var r = data.getRoot(setting);
		r.radioCheckedList = [];
	},
	//default cache of excheck
	_initCache = function(treeId) {},
	//default bind event of excheck
	_bindEvent = function(setting) {
		var o = setting.treeObj,
		c = consts.event;
		o.bind(c.CHECK, function (event, srcEvent, treeId, node) {
			event.srcEvent = srcEvent;
			tools.apply(setting.callback.onCheck, [event, treeId, node]);
		});
	},
	_unbindEvent = function(setting) {
		var o = setting.treeObj,
		c = consts.event;
		o.unbind(c.CHECK);
	},
	//default event proxy of excheck
	_eventProxy = function(e) {
		var target = e.target,
		setting = data.getSetting(e.data.treeId),
		tId = "", node = null,
		nodeEventType = "", treeEventType = "",
		nodeEventCallback = null, treeEventCallback = null;

		if (tools.eqs(e.type, "mouseover")) {
			if (setting.check.enable && tools.eqs(target.tagName, "span") && target.getAttribute("treeNode"+ consts.id.CHECK) !== null) {
				tId = tools.getNodeMainDom(target).id;
				nodeEventType = "mouseoverCheck";
			}
		} else if (tools.eqs(e.type, "mouseout")) {
			if (setting.check.enable && tools.eqs(target.tagName, "span") && target.getAttribute("treeNode"+ consts.id.CHECK) !== null) {
				tId = tools.getNodeMainDom(target).id;
				nodeEventType = "mouseoutCheck";
			}
		} else if (tools.eqs(e.type, "click")) {
			if (setting.check.enable && tools.eqs(target.tagName, "span") && target.getAttribute("treeNode"+ consts.id.CHECK) !== null) {
				tId = tools.getNodeMainDom(target).id;
				nodeEventType = "checkNode";
			}
		}
		if (tId.length>0) {
			node = data.getNodeCache(setting, tId);
			switch (nodeEventType) {
				case "checkNode" :
					nodeEventCallback = _handler.onCheckNode;
					break;
				case "mouseoverCheck" :
					nodeEventCallback = _handler.onMouseoverCheck;
					break;
				case "mouseoutCheck" :
					nodeEventCallback = _handler.onMouseoutCheck;
					break;
			}
		}
		var proxyResult = {
			stop: nodeEventType === "checkNode",
			node: node,
			nodeEventType: nodeEventType,
			nodeEventCallback: nodeEventCallback,
			treeEventType: treeEventType,
			treeEventCallback: treeEventCallback
		};
		return proxyResult
	},
	//default init node of excheck
	_initNode = function(setting, level, n, parentNode, isFirstNode, isLastNode, openFlag) {
		if (!n) return;
		var checkedKey = setting.data.key.checked;
		if (typeof n[checkedKey] == "string") n[checkedKey] = tools.eqs(n[checkedKey], "true");
		n[checkedKey] = !!n[checkedKey];
		n.checkedOld = n[checkedKey];
		if (typeof n.nocheck == "string") n.nocheck = tools.eqs(n.nocheck, "true");
		n.nocheck = !!n.nocheck || (setting.check.nocheckInherit && parentNode && !!parentNode.nocheck);
		if (typeof n.chkDisabled == "string") n.chkDisabled = tools.eqs(n.chkDisabled, "true");
		n.chkDisabled = !!n.chkDisabled || (setting.check.chkDisabledInherit && parentNode && !!parentNode.chkDisabled);
		if (typeof n.halfCheck == "string") n.halfCheck = tools.eqs(n.halfCheck, "true");
		n.halfCheck = !!n.halfCheck;
		n.check_Child_State = -1;
		n.check_Focus = false;
		n.getCheckStatus = function() {return data.getCheckStatus(setting, n);};

		if (setting.check.chkStyle == consts.radio.STYLE && setting.check.radioType == consts.radio.TYPE_ALL && n[checkedKey] ) {
			var r = data.getRoot(setting);
			r.radioCheckedList.push(n);
		}
	},
	//add dom for check
	_beforeA = function(setting, node, html) {
		var checkedKey = setting.data.key.checked;
		if (setting.check.enable) {
			data.makeChkFlag(setting, node);
			html.push("<span ID='", node.tId, consts.id.CHECK, "' class='", view.makeChkClass(setting, node), "' treeNode", consts.id.CHECK, (node.nocheck === true?" style='display:none;'":""),"></span>");
		}
	},
	//update zTreeObj, add method of check
	_zTreeTools = function(setting, zTreeTools) {
		zTreeTools.checkNode = function(node, checked, checkTypeFlag, callbackFlag) {
			var checkedKey = this.setting.data.key.checked;
			if (node.chkDisabled === true) return;
			if (checked !== true && checked !== false) {
				checked = !node[checkedKey];
			}
			callbackFlag = !!callbackFlag;

			if (node[checkedKey] === checked && !checkTypeFlag) {
				return;
			} else if (callbackFlag && tools.apply(this.setting.callback.beforeCheck, [this.setting.treeId, node], true) == false) {
				return;
			}
			if (tools.uCanDo(this.setting) && this.setting.check.enable && node.nocheck !== true) {
				node[checkedKey] = checked;
				var checkObj = $$(node, consts.id.CHECK, this.setting);
				if (checkTypeFlag || this.setting.check.chkStyle === consts.radio.STYLE) view.checkNodeRelation(this.setting, node);
				view.setChkClass(this.setting, checkObj, node);
				view.repairParentChkClassWithSelf(this.setting, node);
				if (callbackFlag) {
					this.setting.treeObj.trigger(consts.event.CHECK, [null, this.setting.treeId, node]);
				}
			}
		}

		zTreeTools.checkAllNodes = function(checked) {
			view.repairAllChk(this.setting, !!checked);
		}

		zTreeTools.getCheckedNodes = function(checked) {
			var childKey = this.setting.data.key.children;
			checked = (checked !== false);
			return data.getTreeCheckedNodes(this.setting, data.getRoot(this.setting)[childKey], checked);
		}

		zTreeTools.getChangeCheckedNodes = function() {
			var childKey = this.setting.data.key.children;
			return data.getTreeChangeCheckedNodes(this.setting, data.getRoot(this.setting)[childKey]);
		}

		zTreeTools.setChkDisabled = function(node, disabled, inheritParent, inheritChildren) {
			disabled = !!disabled;
			inheritParent = !!inheritParent;
			inheritChildren = !!inheritChildren;
			view.repairSonChkDisabled(this.setting, node, disabled, inheritChildren);
			view.repairParentChkDisabled(this.setting, node.getParentNode(), disabled, inheritParent);
		}

		var _updateNode = zTreeTools.updateNode;
		zTreeTools.updateNode = function(node, checkTypeFlag) {
			if (_updateNode) _updateNode.apply(zTreeTools, arguments);
			if (!node || !this.setting.check.enable) return;
			var nObj = $$(node, this.setting);
			if (nObj.get(0) && tools.uCanDo(this.setting)) {
				var checkObj = $$(node, consts.id.CHECK, this.setting);
				if (checkTypeFlag == true || this.setting.check.chkStyle === consts.radio.STYLE) view.checkNodeRelation(this.setting, node);
				view.setChkClass(this.setting, checkObj, node);
				view.repairParentChkClassWithSelf(this.setting, node);
			}
		}
	},
	//method of operate data
	_data = {
		getRadioCheckedList: function(setting) {
			var checkedList = data.getRoot(setting).radioCheckedList;
			for (var i=0, j=checkedList.length; i<j; i++) {
				if(!data.getNodeCache(setting, checkedList[i].tId)) {
					checkedList.splice(i, 1);
					i--; j--;
				}
			}
			return checkedList;
		},
		getCheckStatus: function(setting, node) {
			if (!setting.check.enable || node.nocheck || node.chkDisabled) return null;
			var checkedKey = setting.data.key.checked,
			r = {
				checked: node[checkedKey],
				half: node.halfCheck ? node.halfCheck : (setting.check.chkStyle == consts.radio.STYLE ? (node.check_Child_State === 2) : (node[checkedKey] ? (node.check_Child_State > -1 && node.check_Child_State < 2) : (node.check_Child_State > 0)))
			};
			return r;
		},
		getTreeCheckedNodes: function(setting, nodes, checked, results) {
			if (!nodes) return [];
			var childKey = setting.data.key.children,
			checkedKey = setting.data.key.checked,
			onlyOne = (checked && setting.check.chkStyle == consts.radio.STYLE && setting.check.radioType == consts.radio.TYPE_ALL);
			results = !results ? [] : results;
			for (var i = 0, l = nodes.length; i < l; i++) {
				if (nodes[i].nocheck !== true && nodes[i].chkDisabled !== true && nodes[i][checkedKey] == checked) {
					results.push(nodes[i]);
					if(onlyOne) {
						break;
					}
				}
				data.getTreeCheckedNodes(setting, nodes[i][childKey], checked, results);
				if(onlyOne && results.length > 0) {
					break;
				}
			}
			return results;
		},
		getTreeChangeCheckedNodes: function(setting, nodes, results) {
			if (!nodes) return [];
			var childKey = setting.data.key.children,
			checkedKey = setting.data.key.checked;
			results = !results ? [] : results;
			for (var i = 0, l = nodes.length; i < l; i++) {
				if (nodes[i].nocheck !== true && nodes[i].chkDisabled !== true && nodes[i][checkedKey] != nodes[i].checkedOld) {
					results.push(nodes[i]);
				}
				data.getTreeChangeCheckedNodes(setting, nodes[i][childKey], results);
			}
			return results;
		},
		makeChkFlag: function(setting, node) {
			if (!node) return;
			var childKey = setting.data.key.children,
			checkedKey = setting.data.key.checked,
			chkFlag = -1;
			if (node[childKey]) {
				for (var i = 0, l = node[childKey].length; i < l; i++) {
					var cNode = node[childKey][i];
					var tmp = -1;
					if (setting.check.chkStyle == consts.radio.STYLE) {
						if (cNode.nocheck === true || cNode.chkDisabled === true) {
							tmp = cNode.check_Child_State;
						} else if (cNode.halfCheck === true) {
							tmp = 2;
						} else if (cNode[checkedKey]) {
							tmp = 2;
						} else {
							tmp = cNode.check_Child_State > 0 ? 2:0;
						}
						if (tmp == 2) {
							chkFlag = 2; break;
						} else if (tmp == 0){
							chkFlag = 0;
						}
					} else if (setting.check.chkStyle == consts.checkbox.STYLE) {
						if (cNode.nocheck === true || cNode.chkDisabled === true) {
							tmp = cNode.check_Child_State;
						} else if (cNode.halfCheck === true) {
							tmp = 1;
						} else if (cNode[checkedKey] ) {
							tmp = (cNode.check_Child_State === -1 || cNode.check_Child_State === 2) ? 2 : 1;
						} else {
							tmp = (cNode.check_Child_State > 0) ? 1 : 0;
						}
						if (tmp === 1) {
							chkFlag = 1; break;
						} else if (tmp === 2 && chkFlag > -1 && i > 0 && tmp !== chkFlag) {
							chkFlag = 1; break;
						} else if (chkFlag === 2 && tmp > -1 && tmp < 2) {
							chkFlag = 1; break;
						} else if (tmp > -1) {
							chkFlag = tmp;
						}
					}
				}
			}
			node.check_Child_State = chkFlag;
		}
	},
	//method of event proxy
	_event = {

	},
	//method of event handler
	_handler = {
		onCheckNode: function (event, node) {
			if (node.chkDisabled === true) return false;
			var setting = data.getSetting(event.data.treeId),
			checkedKey = setting.data.key.checked;
			if (tools.apply(setting.callback.beforeCheck, [setting.treeId, node], true) == false) return true;
			node[checkedKey] = !node[checkedKey];
			view.checkNodeRelation(setting, node);
			var checkObj = $$(node, consts.id.CHECK, setting);
			view.setChkClass(setting, checkObj, node);
			view.repairParentChkClassWithSelf(setting, node);
			setting.treeObj.trigger(consts.event.CHECK, [event, setting.treeId, node]);
			return true;
		},
		onMouseoverCheck: function(event, node) {
			if (node.chkDisabled === true) return false;
			var setting = data.getSetting(event.data.treeId),
			checkObj = $$(node, consts.id.CHECK, setting);
			node.check_Focus = true;
			view.setChkClass(setting, checkObj, node);
			return true;
		},
		onMouseoutCheck: function(event, node) {
			if (node.chkDisabled === true) return false;
			var setting = data.getSetting(event.data.treeId),
			checkObj = $$(node, consts.id.CHECK, setting);
			node.check_Focus = false;
			view.setChkClass(setting, checkObj, node);
			return true;
		}
	},
	//method of tools for zTree
	_tools = {

	},
	//method of operate ztree dom
	_view = {
		checkNodeRelation: function(setting, node) {
			var pNode, i, l,
			childKey = setting.data.key.children,
			checkedKey = setting.data.key.checked,
			r = consts.radio;
			if (setting.check.chkStyle == r.STYLE) {
				var checkedList = data.getRadioCheckedList(setting);
				if (node[checkedKey]) {
					if (setting.check.radioType == r.TYPE_ALL) {
						for (i = checkedList.length-1; i >= 0; i--) {
							pNode = checkedList[i];
							if (pNode[checkedKey] && pNode != node) {
								pNode[checkedKey] = false;
								checkedList.splice(i, 1);

								view.setChkClass(setting, $$(pNode, consts.id.CHECK, setting), pNode);
								if (pNode.parentTId != node.parentTId) {
									view.repairParentChkClassWithSelf(setting, pNode);
								}
							}
						}
						checkedList.push(node);
					} else {
						var parentNode = (node.parentTId) ? node.getParentNode() : data.getRoot(setting);
						for (i = 0, l = parentNode[childKey].length; i < l; i++) {
							pNode = parentNode[childKey][i];
							if (pNode[checkedKey] && pNode != node) {
								pNode[checkedKey] = false;
								view.setChkClass(setting, $$(pNode, consts.id.CHECK, setting), pNode);
							}
						}
					}
				} else if (setting.check.radioType == r.TYPE_ALL) {
					for (i = 0, l = checkedList.length; i < l; i++) {
						if (node == checkedList[i]) {
							checkedList.splice(i, 1);
							break;
						}
					}
				}

			} else {
				if (node[checkedKey] && (!node[childKey] || node[childKey].length==0 || setting.check.chkboxType.Y.indexOf("s") > -1)) {
					view.setSonNodeCheckBox(setting, node, true);
				}
				if (!node[checkedKey] && (!node[childKey] || node[childKey].length==0 || setting.check.chkboxType.N.indexOf("s") > -1)) {
					view.setSonNodeCheckBox(setting, node, false);
				}
				if (node[checkedKey] && setting.check.chkboxType.Y.indexOf("p") > -1) {
					view.setParentNodeCheckBox(setting, node, true);
				}
				if (!node[checkedKey] && setting.check.chkboxType.N.indexOf("p") > -1) {
					view.setParentNodeCheckBox(setting, node, false);
				}
			}
		},
		makeChkClass: function(setting, node) {
			var checkedKey = setting.data.key.checked,
			c = consts.checkbox, r = consts.radio,
			fullStyle = "";
			if (node.chkDisabled === true) {
				fullStyle = c.DISABLED;
			} else if (node.halfCheck) {
				fullStyle = c.PART;
			} else if (setting.check.chkStyle == r.STYLE) {
				fullStyle = (node.check_Child_State < 1)? c.FULL:c.PART;
			} else {
				fullStyle = node[checkedKey] ? ((node.check_Child_State === 2 || node.check_Child_State === -1) ? c.FULL:c.PART) : ((node.check_Child_State < 1)? c.FULL:c.PART);
			}
			var chkName = setting.check.chkStyle + "_" + (node[checkedKey] ? c.TRUE : c.FALSE) + "_" + fullStyle;
			chkName = (node.check_Focus && node.chkDisabled !== true) ? chkName + "_" + c.FOCUS : chkName;
			return consts.className.BUTTON + " " + c.DEFAULT + " " + chkName;
		},
		repairAllChk: function(setting, checked) {
			if (setting.check.enable && setting.check.chkStyle === consts.checkbox.STYLE) {
				var checkedKey = setting.data.key.checked,
				childKey = setting.data.key.children,
				root = data.getRoot(setting);
				for (var i = 0, l = root[childKey].length; i<l ; i++) {
					var node = root[childKey][i];
					if (node.nocheck !== true && node.chkDisabled !== true) {
						node[checkedKey] = checked;
					}
					view.setSonNodeCheckBox(setting, node, checked);
				}
			}
		},
		repairChkClass: function(setting, node) {
			if (!node) return;
			data.makeChkFlag(setting, node);
			if (node.nocheck !== true) {
				var checkObj = $$(node, consts.id.CHECK, setting);
				view.setChkClass(setting, checkObj, node);
			}
		},
		repairParentChkClass: function(setting, node) {
			if (!node || !node.parentTId) return;
			var pNode = node.getParentNode();
			view.repairChkClass(setting, pNode);
			view.repairParentChkClass(setting, pNode);
		},
		repairParentChkClassWithSelf: function(setting, node) {
			if (!node) return;
			var childKey = setting.data.key.children;
			if (node[childKey] && node[childKey].length > 0) {
				view.repairParentChkClass(setting, node[childKey][0]);
			} else {
				view.repairParentChkClass(setting, node);
			}
		},
		repairSonChkDisabled: function(setting, node, chkDisabled, inherit) {
			if (!node) return;
			var childKey = setting.data.key.children;
			if (node.chkDisabled != chkDisabled) {
				node.chkDisabled = chkDisabled;
			}
			view.repairChkClass(setting, node);
			if (node[childKey] && inherit) {
				for (var i = 0, l = node[childKey].length; i < l; i++) {
					var sNode = node[childKey][i];
					view.repairSonChkDisabled(setting, sNode, chkDisabled, inherit);
				}
			}
		},
		repairParentChkDisabled: function(setting, node, chkDisabled, inherit) {
			if (!node) return;
			if (node.chkDisabled != chkDisabled && inherit) {
				node.chkDisabled = chkDisabled;
			}
			view.repairChkClass(setting, node);
			view.repairParentChkDisabled(setting, node.getParentNode(), chkDisabled, inherit);
		},
		setChkClass: function(setting, obj, node) {
			if (!obj) return;
			if (node.nocheck === true) {
				obj.hide();
			} else {
				obj.show();
			}
            obj.attr('class', view.makeChkClass(setting, node));
		},
		setParentNodeCheckBox: function(setting, node, value, srcNode) {
			var childKey = setting.data.key.children,
			checkedKey = setting.data.key.checked,
			checkObj = $$(node, consts.id.CHECK, setting);
			if (!srcNode) srcNode = node;
			data.makeChkFlag(setting, node);
			if (node.nocheck !== true && node.chkDisabled !== true) {
				node[checkedKey] = value;
				view.setChkClass(setting, checkObj, node);
				if (setting.check.autoCheckTrigger && node != srcNode) {
					setting.treeObj.trigger(consts.event.CHECK, [null, setting.treeId, node]);
				}
			}
			if (node.parentTId) {
				var pSign = true;
				if (!value) {
					var pNodes = node.getParentNode()[childKey];
					for (var i = 0, l = pNodes.length; i < l; i++) {
						if ((pNodes[i].nocheck !== true && pNodes[i].chkDisabled !== true && pNodes[i][checkedKey])
						|| ((pNodes[i].nocheck === true || pNodes[i].chkDisabled === true) && pNodes[i].check_Child_State > 0)) {
							pSign = false;
							break;
						}
					}
				}
				if (pSign) {
					view.setParentNodeCheckBox(setting, node.getParentNode(), value, srcNode);
				}
			}
		},
		setSonNodeCheckBox: function(setting, node, value, srcNode) {
			if (!node) return;
			var childKey = setting.data.key.children,
			checkedKey = setting.data.key.checked,
			checkObj = $$(node, consts.id.CHECK, setting);
			if (!srcNode) srcNode = node;

			var hasDisable = false;
			if (node[childKey]) {
				for (var i = 0, l = node[childKey].length; i < l && node.chkDisabled !== true; i++) {
					var sNode = node[childKey][i];
					view.setSonNodeCheckBox(setting, sNode, value, srcNode);
					if (sNode.chkDisabled === true) hasDisable = true;
				}
			}

			if (node != data.getRoot(setting) && node.chkDisabled !== true) {
				if (hasDisable && node.nocheck !== true) {
					data.makeChkFlag(setting, node);
				}
				if (node.nocheck !== true && node.chkDisabled !== true) {
					node[checkedKey] = value;
					if (!hasDisable) node.check_Child_State = (node[childKey] && node[childKey].length > 0) ? (value ? 2 : 0) : -1;
				} else {
					node.check_Child_State = -1;
				}
				view.setChkClass(setting, checkObj, node);
				if (setting.check.autoCheckTrigger && node != srcNode && node.nocheck !== true && node.chkDisabled !== true) {
					setting.treeObj.trigger(consts.event.CHECK, [null, setting.treeId, node]);
				}
			}

		}
	},

	_z = {
		tools: _tools,
		view: _view,
		event: _event,
		data: _data
	};
	$.extend(true, $.fn.zTree.consts, _consts);
	$.extend(true, $.fn.zTree._z, _z);

	var zt = $.fn.zTree,
	tools = zt._z.tools,
	consts = zt.consts,
	view = zt._z.view,
	data = zt._z.data,
	event = zt._z.event,
	$$ = tools.$;

	data.exSetting(_setting);
	data.addInitBind(_bindEvent);
	data.addInitUnBind(_unbindEvent);
	data.addInitCache(_initCache);
	data.addInitNode(_initNode);
	data.addInitProxy(_eventProxy, true);
	data.addInitRoot(_initRoot);
	data.addBeforeA(_beforeA);
	data.addZTreeTools(_zTreeTools);

	var _createNodes = view.createNodes;
	view.createNodes = function(setting, level, nodes, parentNode, index) {
		if (_createNodes) _createNodes.apply(view, arguments);
		if (!nodes) return;
		view.repairParentChkClassWithSelf(setting, parentNode);
	}
	var _removeNode = view.removeNode;
	view.removeNode = function(setting, node) {
		var parentNode = node.getParentNode();
		if (_removeNode) _removeNode.apply(view, arguments);
		if (!node || !parentNode) return;
		view.repairChkClass(setting, parentNode);
		view.repairParentChkClass(setting, parentNode);
	}

	var _appendNodes = view.appendNodes;
	view.appendNodes = function(setting, level, nodes, parentNode, index, initFlag, openFlag) {
		var html = "";
		if (_appendNodes) {
			html = _appendNodes.apply(view, arguments);
		}
		if (parentNode) {
			data.makeChkFlag(setting, parentNode);
		}
		return html;
	}
})(jQuery);;(function($){
	//default consts of exedit
	var _consts = {
		event: {
			DRAG: "ztree_drag",
			DROP: "ztree_drop",
			RENAME: "ztree_rename",
			DRAGMOVE:"ztree_dragmove"
		},
		id: {
			EDIT: "_edit",
			INPUT: "_input",
			REMOVE: "_remove"
		},
		move: {
			TYPE_INNER: "inner",
			TYPE_PREV: "prev",
			TYPE_NEXT: "next"
		},
		node: {
			CURSELECTED_EDIT: "curSelectedNode_Edit",
			TMPTARGET_TREE: "tmpTargetzTree",
			TMPTARGET_NODE: "tmpTargetNode"
		}
	},
	//default setting of exedit
	_setting = {
		edit: {
			enable: false,
			editNameSelectAll: false,
			showRemoveBtn: true,
			showRenameBtn: true,
			removeTitle: "remove",
			renameTitle: "rename",
			drag: {
				autoExpandTrigger: false,
				isCopy: true,
				isMove: true,
				prev: true,
				next: true,
				inner: true,
				minMoveSize: 5,
				borderMax: 10,
				borderMin: -5,
				maxShowNodeNum: 5,
				autoOpenTime: 500
			}
		},
		view: {
			addHoverDom: null,
			removeHoverDom: null
		},
		callback: {
			beforeDrag:null,
			beforeDragOpen:null,
			beforeDrop:null,
			beforeEditName:null,
			beforeRename:null,
			onDrag:null,
			onDragMove:null,
			onDrop:null,
			onRename:null
		}
	},
	//default root of exedit
	_initRoot = function (setting) {
		var r = data.getRoot(setting), rs = data.getRoots();
		r.curEditNode = null;
		r.curEditInput = null;
		r.curHoverNode = null;
		r.dragFlag = 0;
		r.dragNodeShowBefore = [];
		r.dragMaskList = new Array();
		rs.showHoverDom = true;
	},
	//default cache of exedit
	_initCache = function(treeId) {},
	//default bind event of exedit
	_bindEvent = function(setting) {
		var o = setting.treeObj;
		var c = consts.event;
		o.bind(c.RENAME, function (event, treeId, treeNode, isCancel) {
			tools.apply(setting.callback.onRename, [event, treeId, treeNode, isCancel]);
		});

		o.bind(c.DRAG, function (event, srcEvent, treeId, treeNodes) {
			tools.apply(setting.callback.onDrag, [srcEvent, treeId, treeNodes]);
		});

		o.bind(c.DRAGMOVE,function(event, srcEvent, treeId, treeNodes){
			tools.apply(setting.callback.onDragMove,[srcEvent, treeId, treeNodes]);
		});

		o.bind(c.DROP, function (event, srcEvent, treeId, treeNodes, targetNode, moveType, isCopy) {
			tools.apply(setting.callback.onDrop, [srcEvent, treeId, treeNodes, targetNode, moveType, isCopy]);
		});
	},
	_unbindEvent = function(setting) {
		var o = setting.treeObj;
		var c = consts.event;
		o.unbind(c.RENAME);
		o.unbind(c.DRAG);
		o.unbind(c.DRAGMOVE);
		o.unbind(c.DROP);
	},
	//default event proxy of exedit
	_eventProxy = function(e) {
		var target = e.target,
		setting = data.getSetting(e.data.treeId),
		relatedTarget = e.relatedTarget,
		tId = "", node = null,
		nodeEventType = "", treeEventType = "",
		nodeEventCallback = null, treeEventCallback = null,
		tmp = null;

		if (tools.eqs(e.type, "mouseover")) {
			tmp = tools.getMDom(setting, target, [{tagName:"a", attrName:"treeNode"+consts.id.A}]);
			if (tmp) {
				tId = tools.getNodeMainDom(tmp).id;
				nodeEventType = "hoverOverNode";
			}
		} else if (tools.eqs(e.type, "mouseout")) {
			tmp = tools.getMDom(setting, relatedTarget, [{tagName:"a", attrName:"treeNode"+consts.id.A}]);
			if (!tmp) {
				tId = "remove";
				nodeEventType = "hoverOutNode";
			}
		} else if (tools.eqs(e.type, "mousedown")) {
			tmp = tools.getMDom(setting, target, [{tagName:"a", attrName:"treeNode"+consts.id.A}]);
			if (tmp) {
				tId = tools.getNodeMainDom(tmp).id;
				nodeEventType = "mousedownNode";
			}
		}
		if (tId.length>0) {
			node = data.getNodeCache(setting, tId);
			switch (nodeEventType) {
				case "mousedownNode" :
					nodeEventCallback = _handler.onMousedownNode;
					break;
				case "hoverOverNode" :
					nodeEventCallback = _handler.onHoverOverNode;
					break;
				case "hoverOutNode" :
					nodeEventCallback = _handler.onHoverOutNode;
					break;
			}
		}
		var proxyResult = {
			stop: false,
			node: node,
			nodeEventType: nodeEventType,
			nodeEventCallback: nodeEventCallback,
			treeEventType: treeEventType,
			treeEventCallback: treeEventCallback
		};
		return proxyResult
	},
	//default init node of exedit
	_initNode = function(setting, level, n, parentNode, isFirstNode, isLastNode, openFlag) {
		if (!n) return;
		n.isHover = false;
		n.editNameFlag = false;
	},
	//update zTreeObj, add method of edit
	_zTreeTools = function(setting, zTreeTools) {
		zTreeTools.cancelEditName = function(newName) {
			var root = data.getRoot(this.setting);
			if (!root.curEditNode) return;
			view.cancelCurEditNode(this.setting, newName?newName:null, true);
		}
		zTreeTools.copyNode = function(targetNode, node, moveType, isSilent) {
			if (!node) return null;
			if (targetNode && !targetNode.isParent && this.setting.data.keep.leaf && moveType === consts.move.TYPE_INNER) return null;
			var _this = this,
				newNode = tools.clone(node);
			if (!targetNode) {
				targetNode = null;
				moveType = consts.move.TYPE_INNER;
			}
			if (moveType == consts.move.TYPE_INNER) {
				function copyCallback() {
					view.addNodes(_this.setting, targetNode, -1, [newNode], isSilent);
				}

				if (tools.canAsync(this.setting, targetNode)) {
					view.asyncNode(this.setting, targetNode, isSilent, copyCallback);
				} else {
					copyCallback();
				}
			} else {
				view.addNodes(this.setting, targetNode.parentNode, -1, [newNode], isSilent);
				view.moveNode(this.setting, targetNode, newNode, moveType, false, isSilent);
			}
			return newNode;
		}
		zTreeTools.editName = function(node) {
			if (!node || !node.tId || node !== data.getNodeCache(this.setting, node.tId)) return;
			if (node.parentTId) view.expandCollapseParentNode(this.setting, node.getParentNode(), true);
			view.editNode(this.setting, node)
		}
		zTreeTools.moveNode = function(targetNode, node, moveType, isSilent) {
			if (!node) return node;
			if (targetNode && !targetNode.isParent && this.setting.data.keep.leaf && moveType === consts.move.TYPE_INNER) {
				return null;
			} else if (targetNode && ((node.parentTId == targetNode.tId && moveType == consts.move.TYPE_INNER) || $$(node, this.setting).find("#" + targetNode.tId).length > 0)) {
				return null;
			} else if (!targetNode) {
				targetNode = null;
			}
			var _this = this;
			function moveCallback() {
				view.moveNode(_this.setting, targetNode, node, moveType, false, isSilent);
			}
			if (tools.canAsync(this.setting, targetNode) && moveType === consts.move.TYPE_INNER) {
				view.asyncNode(this.setting, targetNode, isSilent, moveCallback);
			} else {
				moveCallback();
			}
			return node;
		}
		zTreeTools.setEditable = function(editable) {
			this.setting.edit.enable = editable;
			return this.refresh();
		}
	},
	//method of operate data
	_data = {
		setSonNodeLevel: function(setting, parentNode, node) {
			if (!node) return;
			var childKey = setting.data.key.children;
			node.level = (parentNode)? parentNode.level + 1 : 0;
			if (!node[childKey]) return;
			for (var i = 0, l = node[childKey].length; i < l; i++) {
				if (node[childKey][i]) data.setSonNodeLevel(setting, node, node[childKey][i]);
			}
		}
	},
	//method of event proxy
	_event = {

	},
	//method of event handler
	_handler = {
		onHoverOverNode: function(event, node) {
			var setting = data.getSetting(event.data.treeId),
			root = data.getRoot(setting);
			if (root.curHoverNode != node) {
				_handler.onHoverOutNode(event);
			}
			root.curHoverNode = node;
			view.addHoverDom(setting, node);
		},
		onHoverOutNode: function(event, node) {
			var setting = data.getSetting(event.data.treeId),
			root = data.getRoot(setting);
			if (root.curHoverNode && !data.isSelectedNode(setting, root.curHoverNode)) {
				view.removeTreeDom(setting, root.curHoverNode);
				root.curHoverNode = null;
			}
		},
		onMousedownNode: function(eventMouseDown, _node) {
			var i,l,
			setting = data.getSetting(eventMouseDown.data.treeId),
			root = data.getRoot(setting), roots = data.getRoots();
			//right click can't drag & drop
			if (eventMouseDown.button == 2 || !setting.edit.enable || (!setting.edit.drag.isCopy && !setting.edit.drag.isMove)) return true;

			//input of edit node name can't drag & drop
			var target = eventMouseDown.target,
			_nodes = data.getRoot(setting).curSelectedList,
			nodes = [];
			if (!data.isSelectedNode(setting, _node)) {
				nodes = [_node];
			} else {
				for (i=0, l=_nodes.length; i<l; i++) {
					if (_nodes[i].editNameFlag && tools.eqs(target.tagName, "input") && target.getAttribute("treeNode"+consts.id.INPUT) !== null) {
						return true;
					}
					nodes.push(_nodes[i]);
					if (nodes[0].parentTId !== _nodes[i].parentTId) {
						nodes = [_node];
						break;
					}
				}
			}

			view.editNodeBlur = true;
			view.cancelCurEditNode(setting);

			var doc = $(setting.treeObj.get(0).ownerDocument),
			body = $(setting.treeObj.get(0).ownerDocument.body), curNode, tmpArrow, tmpTarget,
			isOtherTree = false,
			targetSetting = setting,
			sourceSetting = setting,
			preNode, nextNode,
			preTmpTargetNodeId = null,
			preTmpMoveType = null,
			tmpTargetNodeId = null,
			moveType = consts.move.TYPE_INNER,
			mouseDownX = eventMouseDown.clientX,
			mouseDownY = eventMouseDown.clientY,
			startTime = (new Date()).getTime();

			if (tools.uCanDo(setting)) {
				doc.bind("mousemove", _docMouseMove);
			}
			function _docMouseMove(event) {
				//avoid start drag after click node
				if (root.dragFlag == 0 && Math.abs(mouseDownX - event.clientX) < setting.edit.drag.minMoveSize
					&& Math.abs(mouseDownY - event.clientY) < setting.edit.drag.minMoveSize) {
					return true;
				}
				var i, l, tmpNode, tmpDom, tmpNodes,
				childKey = setting.data.key.children;
				body.css("cursor", "pointer");

				if (root.dragFlag == 0) {
					if (tools.apply(setting.callback.beforeDrag, [setting.treeId, nodes], true) == false) {
						_docMouseUp(event);
						return true;
					}

					for (i=0, l=nodes.length; i<l; i++) {
						if (i==0) {
							root.dragNodeShowBefore = [];
						}
						tmpNode = nodes[i];
						if (tmpNode.isParent && tmpNode.open) {
							view.expandCollapseNode(setting, tmpNode, !tmpNode.open);
							root.dragNodeShowBefore[tmpNode.tId] = true;
						} else {
							root.dragNodeShowBefore[tmpNode.tId] = false;
						}
					}

					root.dragFlag = 1;
					roots.showHoverDom = false;
					tools.showIfameMask(setting, true);

					//sort
					var isOrder = true, lastIndex = -1;
					if (nodes.length>1) {
						var pNodes = nodes[0].parentTId ? nodes[0].getParentNode()[childKey] : data.getNodes(setting);
						tmpNodes = [];
						for (i=0, l=pNodes.length; i<l; i++) {
							if (root.dragNodeShowBefore[pNodes[i].tId] !== undefined) {
								if (isOrder && lastIndex > -1 && (lastIndex+1) !== i) {
									isOrder = false;
								}
								tmpNodes.push(pNodes[i]);
								lastIndex = i;
							}
							if (nodes.length === tmpNodes.length) {
								nodes = tmpNodes;
								break;
							}
						}
					}
					if (isOrder) {
						preNode = nodes[0].getPreNode();
						nextNode = nodes[nodes.length-1].getNextNode();
					}

					//set node in selected
					curNode = $$("<ul class='zTreeDragUL'></ul>", setting);
					for (i=0, l=nodes.length; i<l; i++) {
						tmpNode = nodes[i];
						tmpNode.editNameFlag = false;
						view.selectNode(setting, tmpNode, i>0);
						view.removeTreeDom(setting, tmpNode);

						if (i > setting.edit.drag.maxShowNodeNum-1) {
							continue;
						}

						tmpDom = $$("<li id='"+ tmpNode.tId +"_tmp'></li>", setting);
						tmpDom.append($$(tmpNode, consts.id.A, setting).clone());
						tmpDom.css("padding", "0");
						tmpDom.children("#" + tmpNode.tId + consts.id.A).removeClass(consts.node.CURSELECTED);
						curNode.append(tmpDom);
						if (i == setting.edit.drag.maxShowNodeNum-1) {
							tmpDom = $$("<li id='"+ tmpNode.tId +"_moretmp'><a>  ...  </a></li>", setting);
							curNode.append(tmpDom);
						}
					}
					curNode.attr("id", nodes[0].tId + consts.id.UL + "_tmp");
					curNode.addClass(setting.treeObj.attr("class"));
					curNode.appendTo(body);

					tmpArrow = $$("<span class='tmpzTreeMove_arrow'></span>", setting);
					tmpArrow.attr("id", "zTreeMove_arrow_tmp");
					tmpArrow.appendTo(body);

					setting.treeObj.trigger(consts.event.DRAG, [event, setting.treeId, nodes]);
				}

				if (root.dragFlag == 1) {
					if (tmpTarget && tmpArrow.attr("id") == event.target.id && tmpTargetNodeId && (event.clientX + doc.scrollLeft()+2) > ($("#" + tmpTargetNodeId + consts.id.A, tmpTarget).offset().left)) {
						var xT = $("#" + tmpTargetNodeId + consts.id.A, tmpTarget);
						event.target = (xT.length > 0) ? xT.get(0) : event.target;
					} else if (tmpTarget) {
						tmpTarget.removeClass(consts.node.TMPTARGET_TREE);
						if (tmpTargetNodeId) $("#" + tmpTargetNodeId + consts.id.A, tmpTarget).removeClass(consts.node.TMPTARGET_NODE + "_" + consts.move.TYPE_PREV)
							.removeClass(consts.node.TMPTARGET_NODE + "_" + _consts.move.TYPE_NEXT).removeClass(consts.node.TMPTARGET_NODE + "_" + _consts.move.TYPE_INNER);
					}
					tmpTarget = null;
					tmpTargetNodeId = null;

					//judge drag & drop in multi ztree
					isOtherTree = false;
					targetSetting = setting;
					var settings = data.getSettings();
					for (var s in settings) {
						if (settings[s].treeId && settings[s].edit.enable && settings[s].treeId != setting.treeId
							&& (event.target.id == settings[s].treeId || $(event.target).parents("#" + settings[s].treeId).length>0)) {
							isOtherTree = true;
							targetSetting = settings[s];
						}
					}

					var docScrollTop = doc.scrollTop(),
					docScrollLeft = doc.scrollLeft(),
					treeOffset = targetSetting.treeObj.offset(),
					scrollHeight = targetSetting.treeObj.get(0).scrollHeight,
					scrollWidth = targetSetting.treeObj.get(0).scrollWidth,
					dTop = (event.clientY + docScrollTop - treeOffset.top),
					dBottom = (targetSetting.treeObj.height() + treeOffset.top - event.clientY - docScrollTop),
					dLeft = (event.clientX + docScrollLeft - treeOffset.left),
					dRight = (targetSetting.treeObj.width() + treeOffset.left - event.clientX - docScrollLeft),
					isTop = (dTop < setting.edit.drag.borderMax && dTop > setting.edit.drag.borderMin),
					isBottom = (dBottom < setting.edit.drag.borderMax && dBottom > setting.edit.drag.borderMin),
					isLeft = (dLeft < setting.edit.drag.borderMax && dLeft > setting.edit.drag.borderMin),
					isRight = (dRight < setting.edit.drag.borderMax && dRight > setting.edit.drag.borderMin),
					isTreeInner = dTop > setting.edit.drag.borderMin && dBottom > setting.edit.drag.borderMin && dLeft > setting.edit.drag.borderMin && dRight > setting.edit.drag.borderMin,
					isTreeTop = (isTop && targetSetting.treeObj.scrollTop() <= 0),
					isTreeBottom = (isBottom && (targetSetting.treeObj.scrollTop() + targetSetting.treeObj.height()+10) >= scrollHeight),
					isTreeLeft = (isLeft && targetSetting.treeObj.scrollLeft() <= 0),
					isTreeRight = (isRight && (targetSetting.treeObj.scrollLeft() + targetSetting.treeObj.width()+10) >= scrollWidth);

					if (event.target && tools.isChildOrSelf(event.target, targetSetting.treeId)) {
						//get node <li> dom
						var targetObj = event.target;
						while (targetObj && targetObj.tagName && !tools.eqs(targetObj.tagName, "li") && targetObj.id != targetSetting.treeId) {
							targetObj = targetObj.parentNode;
						}

						var canMove = true;
						//don't move to self or children of self
						for (i=0, l=nodes.length; i<l; i++) {
							tmpNode = nodes[i];
							if (targetObj.id === tmpNode.tId) {
								canMove = false;
								break;
							} else if ($$(tmpNode, setting).find("#" + targetObj.id).length > 0) {
								canMove = false;
								break;
							}
						}
						if (canMove && event.target && tools.isChildOrSelf(event.target, targetObj.id + consts.id.A)) {
							tmpTarget = $(targetObj);
							tmpTargetNodeId = targetObj.id;
						}
					}

					//the mouse must be in zTree
					tmpNode = nodes[0];
					if (isTreeInner && tools.isChildOrSelf(event.target, targetSetting.treeId)) {
						//judge mouse move in root of ztree
						if (!tmpTarget && (event.target.id == targetSetting.treeId || isTreeTop || isTreeBottom || isTreeLeft || isTreeRight) && (isOtherTree || (!isOtherTree && tmpNode.parentTId))) {
							tmpTarget = targetSetting.treeObj;
						}
						//auto scroll top
						if (isTop) {
							targetSetting.treeObj.scrollTop(targetSetting.treeObj.scrollTop()-10);
						} else if (isBottom)  {
							targetSetting.treeObj.scrollTop(targetSetting.treeObj.scrollTop()+10);
						}
						if (isLeft) {
							targetSetting.treeObj.scrollLeft(targetSetting.treeObj.scrollLeft()-10);
						} else if (isRight) {
							targetSetting.treeObj.scrollLeft(targetSetting.treeObj.scrollLeft()+10);
						}
						//auto scroll left
						if (tmpTarget && tmpTarget != targetSetting.treeObj && tmpTarget.offset().left < targetSetting.treeObj.offset().left) {
							targetSetting.treeObj.scrollLeft(targetSetting.treeObj.scrollLeft()+ tmpTarget.offset().left - targetSetting.treeObj.offset().left);
						}
					}

					curNode.css({
						"top": (event.clientY + docScrollTop + 3) + "px",
						"left": (event.clientX + docScrollLeft + 3) + "px",
						"z-index":20
					});

					var dX = 0;
					var dY = 0;
					if (tmpTarget && tmpTarget.attr("id")!=targetSetting.treeId) {
						var tmpTargetNode = tmpTargetNodeId == null ? null: data.getNodeCache(targetSetting, tmpTargetNodeId),
						isCopy = ((event.ctrlKey || event.metaKey) && setting.edit.drag.isMove && setting.edit.drag.isCopy) || (!setting.edit.drag.isMove && setting.edit.drag.isCopy),
						isPrev = !!(preNode && tmpTargetNodeId === preNode.tId),
						isNext = !!(nextNode && tmpTargetNodeId === nextNode.tId),
						isInner = (tmpNode.parentTId && tmpNode.parentTId == tmpTargetNodeId),
						canPrev = (isCopy || !isNext) && tools.apply(targetSetting.edit.drag.prev, [targetSetting.treeId, nodes, tmpTargetNode], !!targetSetting.edit.drag.prev),
						canNext = (isCopy || !isPrev) && tools.apply(targetSetting.edit.drag.next, [targetSetting.treeId, nodes, tmpTargetNode], !!targetSetting.edit.drag.next),
						canInner = (isCopy || !isInner) && !(targetSetting.data.keep.leaf && !tmpTargetNode.isParent) && tools.apply(targetSetting.edit.drag.inner, [targetSetting.treeId, nodes, tmpTargetNode], !!targetSetting.edit.drag.inner);
						if (!canPrev && !canNext && !canInner) {
							tmpTarget = null;
							tmpTargetNodeId = "";
							moveType = consts.move.TYPE_INNER;
							tmpArrow.css({
								"display":"none"
							});
							if (window.zTreeMoveTimer) {
								clearTimeout(window.zTreeMoveTimer);
								window.zTreeMoveTargetNodeTId = null
							}
						} else {
							var tmpTargetA = $("#" + tmpTargetNodeId + consts.id.A, tmpTarget),
							tmpNextA = tmpTargetNode.isLastNode ? null : $("#" + tmpTargetNode.getNextNode().tId + consts.id.A, tmpTarget.next()),
							tmpTop = tmpTargetA.offset().top,
							tmpLeft = tmpTargetA.offset().left,
							prevPercent = canPrev ? (canInner ? 0.25 : (canNext ? 0.5 : 1) ) : -1,
							nextPercent = canNext ? (canInner ? 0.75 : (canPrev ? 0.5 : 0) ) : -1,
							dY_percent = (event.clientY + docScrollTop - tmpTop)/tmpTargetA.height();
							if ((prevPercent==1 ||dY_percent<=prevPercent && dY_percent>=-.2) && canPrev) {
								dX = 1 - tmpArrow.width();
								dY = tmpTop - tmpArrow.height()/2;
								//overlay
								dY = dY-5;
								moveType = consts.move.TYPE_PREV;
							} else if ((nextPercent==0 || dY_percent>=nextPercent && dY_percent<=1.2) && canNext) {
								dX = 1 - tmpArrow.width();
								dY = (tmpNextA == null || (tmpTargetNode.isParent && tmpTargetNode.open)) ? (tmpTop + tmpTargetA.height() - tmpArrow.height()/2) : (tmpNextA.offset().top - tmpArrow.height()/2);
								//overlay
								dY = dY-5;
								moveType = consts.move.TYPE_NEXT;
							}else {
								dX = 5 - tmpArrow.width();
								dY = tmpTop;
								//overlay
								dX = dX-5;
								moveType = consts.move.TYPE_INNER;
							}
							tmpArrow.css({
								"display":"block",
								"top": dY + "px",
								"left": (tmpLeft + dX) + "px",
								"z-index":20
							});
							tmpTargetA.addClass(consts.node.TMPTARGET_NODE + "_" + moveType);

							if (preTmpTargetNodeId != tmpTargetNodeId || preTmpMoveType != moveType) {
								startTime = (new Date()).getTime();
							}
							if (tmpTargetNode && tmpTargetNode.isParent && moveType == consts.move.TYPE_INNER) {
								var startTimer = true;
								if (window.zTreeMoveTimer && window.zTreeMoveTargetNodeTId !== tmpTargetNode.tId) {
									clearTimeout(window.zTreeMoveTimer);
									window.zTreeMoveTargetNodeTId = null;
								}else if (window.zTreeMoveTimer && window.zTreeMoveTargetNodeTId === tmpTargetNode.tId) {
									startTimer = false;
								}
								if (startTimer) {
									window.zTreeMoveTimer = setTimeout(function() {
										if (moveType != consts.move.TYPE_INNER) return;
										if (tmpTargetNode && tmpTargetNode.isParent && !tmpTargetNode.open && (new Date()).getTime() - startTime > targetSetting.edit.drag.autoOpenTime
											&& tools.apply(targetSetting.callback.beforeDragOpen, [targetSetting.treeId, tmpTargetNode], true)) {
											view.switchNode(targetSetting, tmpTargetNode);
											if (targetSetting.edit.drag.autoExpandTrigger) {
												targetSetting.treeObj.trigger(consts.event.EXPAND, [targetSetting.treeId, tmpTargetNode]);
											}
										}
									}, targetSetting.edit.drag.autoOpenTime+50);
									window.zTreeMoveTargetNodeTId = tmpTargetNode.tId;
								}
							}
						}
					} else {
						moveType = consts.move.TYPE_INNER;
						if (tmpTarget && tools.apply(targetSetting.edit.drag.inner, [targetSetting.treeId, nodes, null], !!targetSetting.edit.drag.inner)) {
							tmpTarget.addClass(consts.node.TMPTARGET_TREE);
						} else {
							tmpTarget = null;
						}
						tmpArrow.css({
							"display":"none"
						});
						if (window.zTreeMoveTimer) {
							clearTimeout(window.zTreeMoveTimer);
							window.zTreeMoveTargetNodeTId = null;
						}
					}
					preTmpTargetNodeId = tmpTargetNodeId;
					preTmpMoveType = moveType;

					setting.treeObj.trigger(consts.event.DRAGMOVE, [event, setting.treeId, nodes]);
				}
				return false;
			}

			doc.bind("mouseup", _docMouseUp);
			function _docMouseUp(event) {
				if (window.zTreeMoveTimer) {
					clearTimeout(window.zTreeMoveTimer);
					window.zTreeMoveTargetNodeTId = null;
				}
				preTmpTargetNodeId = null;
				preTmpMoveType = null;
				doc.unbind("mousemove", _docMouseMove);
				doc.unbind("mouseup", _docMouseUp);
				doc.unbind("selectstart", _docSelect);
				body.css("cursor", "auto");
				if (tmpTarget) {
					tmpTarget.removeClass(consts.node.TMPTARGET_TREE);
					if (tmpTargetNodeId) $("#" + tmpTargetNodeId + consts.id.A, tmpTarget).removeClass(consts.node.TMPTARGET_NODE + "_" + consts.move.TYPE_PREV)
							.removeClass(consts.node.TMPTARGET_NODE + "_" + _consts.move.TYPE_NEXT).removeClass(consts.node.TMPTARGET_NODE + "_" + _consts.move.TYPE_INNER);
				}
				tools.showIfameMask(setting, false);

				roots.showHoverDom = true;
				if (root.dragFlag == 0) return;
				root.dragFlag = 0;

				var i, l, tmpNode;
				for (i=0, l=nodes.length; i<l; i++) {
					tmpNode = nodes[i];
					if (tmpNode.isParent && root.dragNodeShowBefore[tmpNode.tId] && !tmpNode.open) {
						view.expandCollapseNode(setting, tmpNode, !tmpNode.open);
						delete root.dragNodeShowBefore[tmpNode.tId];
					}
				}

				if (curNode) curNode.remove();
				if (tmpArrow) tmpArrow.remove();

				var isCopy = ((event.ctrlKey || event.metaKey) && setting.edit.drag.isMove && setting.edit.drag.isCopy) || (!setting.edit.drag.isMove && setting.edit.drag.isCopy);
				if (!isCopy && tmpTarget && tmpTargetNodeId && nodes[0].parentTId && tmpTargetNodeId==nodes[0].parentTId && moveType == consts.move.TYPE_INNER) {
					tmpTarget = null;
				}
				if (tmpTarget) {
					var dragTargetNode = tmpTargetNodeId == null ? null: data.getNodeCache(targetSetting, tmpTargetNodeId);
					if (tools.apply(setting.callback.beforeDrop, [targetSetting.treeId, nodes, dragTargetNode, moveType, isCopy], true) == false) {
						view.selectNodes(sourceSetting, nodes);
						return;
					}
					var newNodes = isCopy ? tools.clone(nodes) : nodes;

					function dropCallback() {
						if (isOtherTree) {
							if (!isCopy) {
								for(var i=0, l=nodes.length; i<l; i++) {
									view.removeNode(setting, nodes[i]);
								}
							}
							if (moveType == consts.move.TYPE_INNER) {
								view.addNodes(targetSetting, dragTargetNode, -1, newNodes);
							} else {
								view.addNodes(targetSetting, dragTargetNode.getParentNode(), moveType == consts.move.TYPE_PREV ? dragTargetNode.getIndex() : dragTargetNode.getIndex()+1, newNodes);
							}
						} else {
							if (isCopy && moveType == consts.move.TYPE_INNER) {
								view.addNodes(targetSetting, dragTargetNode, -1, newNodes);
							} else if (isCopy) {
								view.addNodes(targetSetting, dragTargetNode.getParentNode(), moveType == consts.move.TYPE_PREV ? dragTargetNode.getIndex() : dragTargetNode.getIndex()+1, newNodes);
							} else {
								if (moveType != consts.move.TYPE_NEXT) {
									for (i=0, l=newNodes.length; i<l; i++) {
										view.moveNode(targetSetting, dragTargetNode, newNodes[i], moveType, false);
									}
								} else {
									for (i=-1, l=newNodes.length-1; i<l; l--) {
										view.moveNode(targetSetting, dragTargetNode, newNodes[l], moveType, false);
									}
								}
							}
						}
						view.selectNodes(targetSetting, newNodes);

						var a = $$(newNodes[0], setting).get(0);
						if (a) {
							if (a.scrollIntoView) {
								a.scrollIntoView();
							} else {
								try{a.focus().blur();}catch(e){}
							}
						}

						setting.treeObj.trigger(consts.event.DROP, [event, targetSetting.treeId, newNodes, dragTargetNode, moveType, isCopy]);
					}

					if (moveType == consts.move.TYPE_INNER && tools.canAsync(targetSetting, dragTargetNode)) {
						view.asyncNode(targetSetting, dragTargetNode, false, dropCallback);
					} else {
						dropCallback();
					}

				} else {
					view.selectNodes(sourceSetting, nodes);
					setting.treeObj.trigger(consts.event.DROP, [event, setting.treeId, nodes, null, null, null]);
				}
			}

			doc.bind("selectstart", _docSelect);
			function _docSelect() {
				return false;
			}

			//Avoid FireFox's Bug
			//If zTree Div CSS set 'overflow', so drag node outside of zTree, and event.target is error.
			if(eventMouseDown.preventDefault) {
				eventMouseDown.preventDefault();
			}
			return true;
		}
	},
	//method of tools for zTree
	_tools = {
		getAbs: function (obj) {
			var oRect = obj.getBoundingClientRect(),
			scrollTop = document.body.scrollTop+document.documentElement.scrollTop,
			scrollLeft = document.body.scrollLeft+document.documentElement.scrollLeft;
			return [oRect.left+scrollLeft,oRect.top+scrollTop];
		},
		inputFocus: function(inputObj) {
			if (inputObj.get(0)) {
				inputObj.focus();
				tools.setCursorPosition(inputObj.get(0), inputObj.val().length);
			}
		},
		inputSelect: function(inputObj) {
			if (inputObj.get(0)) {
				inputObj.focus();
				inputObj.select();
			}
		},
		setCursorPosition: function(obj, pos){
			if(obj.setSelectionRange) {
				obj.focus();
				obj.setSelectionRange(pos,pos);
			} else if (obj.createTextRange) {
				var range = obj.createTextRange();
				range.collapse(true);
				range.moveEnd('character', pos);
				range.moveStart('character', pos);
				range.select();
			}
		},
		showIfameMask: function(setting, showSign) {
			var root = data.getRoot(setting);
			//clear full mask
			while (root.dragMaskList.length > 0) {
				root.dragMaskList[0].remove();
				root.dragMaskList.shift();
			}
			if (showSign) {
				//show mask
				var iframeList = $$("iframe", setting);
				for (var i = 0, l = iframeList.length; i < l; i++) {
					var obj = iframeList.get(i),
					r = tools.getAbs(obj),
					dragMask = $$("<div id='zTreeMask_" + i + "' class='zTreeMask' style='top:" + r[1] + "px; left:" + r[0] + "px; width:" + obj.offsetWidth + "px; height:" + obj.offsetHeight + "px;'></div>", setting);
					dragMask.appendTo($$("body", setting));
					root.dragMaskList.push(dragMask);
				}
			}
		}
	},
	//method of operate ztree dom
	_view = {
		addEditBtn: function(setting, node) {
			if (node.editNameFlag || $$(node, consts.id.EDIT, setting).length > 0) {
				return;
			}
			if (!tools.apply(setting.edit.showRenameBtn, [setting.treeId, node], setting.edit.showRenameBtn)) {
				return;
			}
			var aObj = $$(node, consts.id.A, setting),
			editStr = "<span class='" + consts.className.BUTTON + " edit' id='" + node.tId + consts.id.EDIT + "' title='"+tools.apply(setting.edit.renameTitle, [setting.treeId, node], setting.edit.renameTitle)+"' treeNode"+consts.id.EDIT+" style='display:none;'></span>";
			aObj.append(editStr);

			$$(node, consts.id.EDIT, setting).bind('click',
				function() {
					if (!tools.uCanDo(setting) || tools.apply(setting.callback.beforeEditName, [setting.treeId, node], true) == false) return false;
					view.editNode(setting, node);
					return false;
				}
				).show();
		},
		addRemoveBtn: function(setting, node) {
			if (node.editNameFlag || $$(node, consts.id.REMOVE, setting).length > 0) {
				return;
			}
			if (!tools.apply(setting.edit.showRemoveBtn, [setting.treeId, node], setting.edit.showRemoveBtn)) {
				return;
			}
			var aObj = $$(node, consts.id.A, setting),
			removeStr = "<span class='" + consts.className.BUTTON + " remove' id='" + node.tId + consts.id.REMOVE + "' title='"+tools.apply(setting.edit.removeTitle, [setting.treeId, node], setting.edit.removeTitle)+"' treeNode"+consts.id.REMOVE+" style='display:none;'></span>";
			aObj.append(removeStr);

			$$(node, consts.id.REMOVE, setting).bind('click',
				function() {
					if (!tools.uCanDo(setting) || tools.apply(setting.callback.beforeRemove, [setting.treeId, node], true) == false) return false;
					view.removeNode(setting, node);
					setting.treeObj.trigger(consts.event.REMOVE, [setting.treeId, node]);
					return false;
				}
				).bind('mousedown',
				function(eventMouseDown) {
					return true;
				}
				).show();
		},
		addHoverDom: function(setting, node) {
			if (data.getRoots().showHoverDom) {
				node.isHover = true;
				if (setting.edit.enable) {
					view.addEditBtn(setting, node);
					view.addRemoveBtn(setting, node);
				}
				tools.apply(setting.view.addHoverDom, [setting.treeId, node]);
			}
		},
		cancelCurEditNode: function (setting, forceName, isCancel) {
			var root = data.getRoot(setting),
			nameKey = setting.data.key.name,
			node = root.curEditNode;

			if (node) {
				var inputObj = root.curEditInput,
				newName = forceName ? forceName:(isCancel ? node[nameKey]: inputObj.val());
				if (tools.apply(setting.callback.beforeRename, [setting.treeId, node, newName, isCancel], true) === false) {
					return false;
				}
                node[nameKey] = newName;
                var aObj = $$(node, consts.id.A, setting);
				aObj.removeClass(consts.node.CURSELECTED_EDIT);
				inputObj.unbind();
				view.setNodeName(setting, node);
				node.editNameFlag = false;
				root.curEditNode = null;
				root.curEditInput = null;
				view.selectNode(setting, node, false);
                setting.treeObj.trigger(consts.event.RENAME, [setting.treeId, node, isCancel]);
			}
			root.noSelection = true;
			return true;
		},
		editNode: function(setting, node) {
			var root = data.getRoot(setting);
			view.editNodeBlur = false;
			if (data.isSelectedNode(setting, node) && root.curEditNode == node && node.editNameFlag) {
				setTimeout(function() {tools.inputFocus(root.curEditInput);}, 0);
				return;
			}
			var nameKey = setting.data.key.name;
			node.editNameFlag = true;
			view.removeTreeDom(setting, node);
			view.cancelCurEditNode(setting);
			view.selectNode(setting, node, false);
			$$(node, consts.id.SPAN, setting).html("<input type=text class='rename' id='" + node.tId + consts.id.INPUT + "' treeNode" + consts.id.INPUT + " >");
			var inputObj = $$(node, consts.id.INPUT, setting);
			inputObj.attr("value", node[nameKey]);
			if (setting.edit.editNameSelectAll) {
				tools.inputSelect(inputObj);
			} else {
				tools.inputFocus(inputObj);
			}

			inputObj.bind('blur', function(event) {
				if (!view.editNodeBlur) {
					view.cancelCurEditNode(setting);
				}
			}).bind('keydown', function(event) {
				if (event.keyCode=="13") {
					view.editNodeBlur = true;
					view.cancelCurEditNode(setting);
				} else if (event.keyCode=="27") {
					view.cancelCurEditNode(setting, null, true);
				}
			}).bind('click', function(event) {
				return false;
			}).bind('dblclick', function(event) {
				return false;
			});

			$$(node, consts.id.A, setting).addClass(consts.node.CURSELECTED_EDIT);
			root.curEditInput = inputObj;
			root.noSelection = false;
			root.curEditNode = node;
		},
		moveNode: function(setting, targetNode, node, moveType, animateFlag, isSilent) {
			var root = data.getRoot(setting),
			childKey = setting.data.key.children;
			if (targetNode == node) return;
			if (setting.data.keep.leaf && targetNode && !targetNode.isParent && moveType == consts.move.TYPE_INNER) return;
			var oldParentNode = (node.parentTId ? node.getParentNode(): root),
			targetNodeIsRoot = (targetNode === null || targetNode == root);
			if (targetNodeIsRoot && targetNode === null) targetNode = root;
			if (targetNodeIsRoot) moveType = consts.move.TYPE_INNER;
			var targetParentNode = (targetNode.parentTId ? targetNode.getParentNode() : root);

			if (moveType != consts.move.TYPE_PREV && moveType != consts.move.TYPE_NEXT) {
				moveType = consts.move.TYPE_INNER;
			}

			if (moveType == consts.move.TYPE_INNER) {
				if (targetNodeIsRoot) {
					//parentTId of root node is null
					node.parentTId = null;
				} else {
					if (!targetNode.isParent) {
						targetNode.isParent = true;
						targetNode.open = !!targetNode.open;
						view.setNodeLineIcos(setting, targetNode);
					}
					node.parentTId = targetNode.tId;
				}
			}

			//move node Dom
			var targetObj, target_ulObj;
			if (targetNodeIsRoot) {
				targetObj = setting.treeObj;
				target_ulObj = targetObj;
			} else {
				if (!isSilent && moveType == consts.move.TYPE_INNER) {
					view.expandCollapseNode(setting, targetNode, true, false);
				} else if (!isSilent) {
					view.expandCollapseNode(setting, targetNode.getParentNode(), true, false);
				}
				targetObj = $$(targetNode, setting);
				target_ulObj = $$(targetNode, consts.id.UL, setting);
				if (!!targetObj.get(0) && !target_ulObj.get(0)) {
					var ulstr = [];
					view.makeUlHtml(setting, targetNode, ulstr, '');
					targetObj.append(ulstr.join(''));
				}
				target_ulObj = $$(targetNode, consts.id.UL, setting);
			}
			var nodeDom = $$(node, setting);
			if (!nodeDom.get(0)) {
				nodeDom = view.appendNodes(setting, node.level, [node], null, -1, false, true).join('');
			} else if (!targetObj.get(0)) {
				nodeDom.remove();
			}
			if (target_ulObj.get(0) && moveType == consts.move.TYPE_INNER) {
				target_ulObj.append(nodeDom);
			} else if (targetObj.get(0) && moveType == consts.move.TYPE_PREV) {
				targetObj.before(nodeDom);
			} else if (targetObj.get(0) && moveType == consts.move.TYPE_NEXT) {
				targetObj.after(nodeDom);
			}

			//repair the data after move
			var i,l,
			tmpSrcIndex = -1,
			tmpTargetIndex = 0,
			oldNeighbor = null,
			newNeighbor = null,
			oldLevel = node.level;
			if (node.isFirstNode) {
				tmpSrcIndex = 0;
				if (oldParentNode[childKey].length > 1 ) {
					oldNeighbor = oldParentNode[childKey][1];
					oldNeighbor.isFirstNode = true;
				}
			} else if (node.isLastNode) {
				tmpSrcIndex = oldParentNode[childKey].length -1;
				oldNeighbor = oldParentNode[childKey][tmpSrcIndex - 1];
				oldNeighbor.isLastNode = true;
			} else {
				for (i = 0, l = oldParentNode[childKey].length; i < l; i++) {
					if (oldParentNode[childKey][i].tId == node.tId) {
						tmpSrcIndex = i;
						break;
					}
				}
			}
			if (tmpSrcIndex >= 0) {
				oldParentNode[childKey].splice(tmpSrcIndex, 1);
			}
			if (moveType != consts.move.TYPE_INNER) {
				for (i = 0, l = targetParentNode[childKey].length; i < l; i++) {
					if (targetParentNode[childKey][i].tId == targetNode.tId) tmpTargetIndex = i;
				}
			}
			if (moveType == consts.move.TYPE_INNER) {
				if (!targetNode[childKey]) targetNode[childKey] = new Array();
				if (targetNode[childKey].length > 0) {
					newNeighbor = targetNode[childKey][targetNode[childKey].length - 1];
					newNeighbor.isLastNode = false;
				}
				targetNode[childKey].splice(targetNode[childKey].length, 0, node);
				node.isLastNode = true;
				node.isFirstNode = (targetNode[childKey].length == 1);
			} else if (targetNode.isFirstNode && moveType == consts.move.TYPE_PREV) {
				targetParentNode[childKey].splice(tmpTargetIndex, 0, node);
				newNeighbor = targetNode;
				newNeighbor.isFirstNode = false;
				node.parentTId = targetNode.parentTId;
				node.isFirstNode = true;
				node.isLastNode = false;

			} else if (targetNode.isLastNode && moveType == consts.move.TYPE_NEXT) {
				targetParentNode[childKey].splice(tmpTargetIndex + 1, 0, node);
				newNeighbor = targetNode;
				newNeighbor.isLastNode = false;
				node.parentTId = targetNode.parentTId;
				node.isFirstNode = false;
				node.isLastNode = true;

			} else {
				if (moveType == consts.move.TYPE_PREV) {
					targetParentNode[childKey].splice(tmpTargetIndex, 0, node);
				} else {
					targetParentNode[childKey].splice(tmpTargetIndex + 1, 0, node);
				}
				node.parentTId = targetNode.parentTId;
				node.isFirstNode = false;
				node.isLastNode = false;
			}
			data.fixPIdKeyValue(setting, node);
			data.setSonNodeLevel(setting, node.getParentNode(), node);

			//repair node what been moved
			view.setNodeLineIcos(setting, node);
			view.repairNodeLevelClass(setting, node, oldLevel)

			//repair node's old parentNode dom
			if (!setting.data.keep.parent && oldParentNode[childKey].length < 1) {
				//old parentNode has no child nodes
				oldParentNode.isParent = false;
				oldParentNode.open = false;
				var tmp_ulObj = $$(oldParentNode, consts.id.UL, setting),
				tmp_switchObj = $$(oldParentNode, consts.id.SWITCH, setting),
				tmp_icoObj = $$(oldParentNode, consts.id.ICON, setting);
				view.replaceSwitchClass(oldParentNode, tmp_switchObj, consts.folder.DOCU);
				view.replaceIcoClass(oldParentNode, tmp_icoObj, consts.folder.DOCU);
				tmp_ulObj.css("display", "none");

			} else if (oldNeighbor) {
				//old neigbor node
				view.setNodeLineIcos(setting, oldNeighbor);
			}

			//new neigbor node
			if (newNeighbor) {
				view.setNodeLineIcos(setting, newNeighbor);
			}

			//repair checkbox / radio
			if (!!setting.check && setting.check.enable && view.repairChkClass) {
				view.repairChkClass(setting, oldParentNode);
				view.repairParentChkClassWithSelf(setting, oldParentNode);
				if (oldParentNode != node.parent)
					view.repairParentChkClassWithSelf(setting, node);
			}

			//expand parents after move
			if (!isSilent) {
				view.expandCollapseParentNode(setting, node.getParentNode(), true, animateFlag);
			}
		},
		removeEditBtn: function(setting, node) {
			$$(node, consts.id.EDIT, setting).unbind().remove();
		},
		removeRemoveBtn: function(setting, node) {
			$$(node, consts.id.REMOVE, setting).unbind().remove();
		},
		removeTreeDom: function(setting, node) {
			node.isHover = false;
			view.removeEditBtn(setting, node);
			view.removeRemoveBtn(setting, node);
			tools.apply(setting.view.removeHoverDom, [setting.treeId, node]);
		},
		repairNodeLevelClass: function(setting, node, oldLevel) {
			if (oldLevel === node.level) return;
			var liObj = $$(node, setting),
			aObj = $$(node, consts.id.A, setting),
			ulObj = $$(node, consts.id.UL, setting),
			oldClass = consts.className.LEVEL + oldLevel,
			newClass = consts.className.LEVEL + node.level;
			liObj.removeClass(oldClass);
			liObj.addClass(newClass);
			aObj.removeClass(oldClass);
			aObj.addClass(newClass);
			ulObj.removeClass(oldClass);
			ulObj.addClass(newClass);
		},
		selectNodes : function(setting, nodes) {
			for (var i=0, l=nodes.length; i<l; i++) {
				view.selectNode(setting, nodes[i], i>0);
			}
		}
	},

	_z = {
		tools: _tools,
		view: _view,
		event: _event,
		data: _data
	};
	$.extend(true, $.fn.zTree.consts, _consts);
	$.extend(true, $.fn.zTree._z, _z);

	var zt = $.fn.zTree,
	tools = zt._z.tools,
	consts = zt.consts,
	view = zt._z.view,
	data = zt._z.data,
	event = zt._z.event,
	$$ = tools.$;

	data.exSetting(_setting);
	data.addInitBind(_bindEvent);
	data.addInitUnBind(_unbindEvent);
	data.addInitCache(_initCache);
	data.addInitNode(_initNode);
	data.addInitProxy(_eventProxy);
	data.addInitRoot(_initRoot);
	data.addZTreeTools(_zTreeTools);

	var _cancelPreSelectedNode = view.cancelPreSelectedNode;
	view.cancelPreSelectedNode = function (setting, node) {
		var list = data.getRoot(setting).curSelectedList;
		for (var i=0, j=list.length; i<j; i++) {
			if (!node || node === list[i]) {
				view.removeTreeDom(setting, list[i]);
				if (node) break;
			}
		}
		if (_cancelPreSelectedNode) _cancelPreSelectedNode.apply(view, arguments);
	}

	var _createNodes = view.createNodes;
	view.createNodes = function(setting, level, nodes, parentNode, index) {
		if (_createNodes) {
			_createNodes.apply(view, arguments);
		}
		if (!nodes) return;
		if (view.repairParentChkClassWithSelf) {
			view.repairParentChkClassWithSelf(setting, parentNode);
		}
	}

	var _makeNodeUrl = view.makeNodeUrl;
	view.makeNodeUrl = function(setting, node) {
		return setting.edit.enable ? null : (_makeNodeUrl.apply(view, arguments));
	}

	var _removeNode = view.removeNode;
	view.removeNode = function(setting, node) {
		var root = data.getRoot(setting);
		if (root.curEditNode === node) root.curEditNode = null;
		if (_removeNode) {
			_removeNode.apply(view, arguments);
		}
	}

	var _selectNode = view.selectNode;
	view.selectNode = function(setting, node, addFlag) {
		var root = data.getRoot(setting);
		if (data.isSelectedNode(setting, node) && root.curEditNode == node && node.editNameFlag) {
			return false;
		}
		if (_selectNode) _selectNode.apply(view, arguments);
		view.addHoverDom(setting, node);
		return true;
	}

	var _uCanDo = tools.uCanDo;
	tools.uCanDo = function(setting, e) {
		var root = data.getRoot(setting);
		if (e && (tools.eqs(e.type, "mouseover") || tools.eqs(e.type, "mouseout") || tools.eqs(e.type, "mousedown") || tools.eqs(e.type, "mouseup"))) {
			return true;
		}
		if (root.curEditNode) {
			view.editNodeBlur = false;
			root.curEditInput.focus();
		}
		return (!root.curEditNode) && (_uCanDo ? _uCanDo.apply(view, arguments) : true);
	}
})(jQuery);;!function(){function a(b){var c=a.modules[b];if(!c)throw new Error('failed to require "'+b+'"');return"exports"in c||"function"!=typeof c.definition||(c.client=c.component=!0,c.definition.call(this,c.exports={},c),delete c.definition),c.exports}a.modules={},a.register=function(b,c){a.modules[b]={definition:c}},a.define=function(b,c){a.modules[b]={exports:c}},a.register("component~emitter@1.1.2",function(a,b){function c(a){return a?d(a):void 0}function d(a){for(var b in c.prototype)a[b]=c.prototype[b];return a}b.exports=c,c.prototype.on=c.prototype.addEventListener=function(a,b){return this._callbacks=this._callbacks||{},(this._callbacks[a]=this._callbacks[a]||[]).push(b),this},c.prototype.once=function(a,b){function c(){d.off(a,c),b.apply(this,arguments)}var d=this;return this._callbacks=this._callbacks||{},c.fn=b,this.on(a,c),this},c.prototype.off=c.prototype.removeListener=c.prototype.removeAllListeners=c.prototype.removeEventListener=function(a,b){if(this._callbacks=this._callbacks||{},0==arguments.length)return this._callbacks={},this;var c=this._callbacks[a];if(!c)return this;if(1==arguments.length)return delete this._callbacks[a],this;for(var d,e=0;e<c.length;e++)if(d=c[e],d===b||d.fn===b){c.splice(e,1);break}return this},c.prototype.emit=function(a){this._callbacks=this._callbacks||{};var b=[].slice.call(arguments,1),c=this._callbacks[a];if(c){c=c.slice(0);for(var d=0,e=c.length;e>d;++d)c[d].apply(this,b)}return this},c.prototype.listeners=function(a){return this._callbacks=this._callbacks||{},this._callbacks[a]||[]},c.prototype.hasListeners=function(a){return!!this.listeners(a).length}}),a.register("dropzone",function(b,c){c.exports=a("dropzone/lib/dropzone.js")}),a.register("dropzone/lib/dropzone.js",function(b,c){(function(){var b,d,e,f,g,h,i,j,k={}.hasOwnProperty,l=function(a,b){function c(){this.constructor=a}for(var d in b)k.call(b,d)&&(a[d]=b[d]);return c.prototype=b.prototype,a.prototype=new c,a.__super__=b.prototype,a},m=[].slice;d="undefined"!=typeof Emitter&&null!==Emitter?Emitter:a("component~emitter@1.1.2"),i=function(){},b=function(a){function b(a,d){var e,f,g;if(this.element=a,this.version=b.version,this.defaultOptions.previewTemplate=this.defaultOptions.previewTemplate.replace(/\n*/g,""),this.clickableElements=[],this.listeners=[],this.files=[],"string"==typeof this.element&&(this.element=document.querySelector(this.element)),!this.element||null==this.element.nodeType)throw new Error("Invalid dropzone element.");if(this.element.dropzone)throw new Error("Dropzone already attached.");if(b.instances.push(this),this.element.dropzone=this,e=null!=(g=b.optionsForElement(this.element))?g:{},this.options=c({},this.defaultOptions,e,null!=d?d:{}),this.options.forceFallback||!b.isBrowserSupported())return this.options.fallback.call(this);if(null==this.options.url&&(this.options.url=this.element.getAttribute("action")),!this.options.url)throw new Error("No URL provided.");if(this.options.acceptedFiles&&this.options.acceptedMimeTypes)throw new Error("You can't provide both 'acceptedFiles' and 'acceptedMimeTypes'. 'acceptedMimeTypes' is deprecated.");this.options.acceptedMimeTypes&&(this.options.acceptedFiles=this.options.acceptedMimeTypes,delete this.options.acceptedMimeTypes),this.options.method=this.options.method.toUpperCase(),(f=this.getExistingFallback())&&f.parentNode&&f.parentNode.removeChild(f),this.options.previewsContainer!==!1&&(this.previewsContainer=this.options.previewsContainer?b.getElement(this.options.previewsContainer,"previewsContainer"):this.element),this.options.clickable&&(this.clickableElements=this.options.clickable===!0?[this.element]:b.getElements(this.options.clickable,"clickable")),this.init()}var c;return l(b,a),b.prototype.events=["drop","dragstart","dragend","dragenter","dragover","dragleave","addedfile","removedfile","thumbnail","error","errormultiple","processing","processingmultiple","uploadprogress","totaluploadprogress","sending","sendingmultiple","success","successmultiple","canceled","canceledmultiple","complete","completemultiple","reset","maxfilesexceeded","maxfilesreached"],b.prototype.defaultOptions={url:null,method:"post",withCredentials:!1,parallelUploads:2,uploadMultiple:!1,maxFilesize:256,paramName:"file",createImageThumbnails:!0,maxThumbnailFilesize:10,thumbnailWidth:100,thumbnailHeight:100,maxFiles:null,params:{},clickable:!0,ignoreHiddenFiles:!0,acceptedFiles:null,acceptedMimeTypes:null,autoProcessQueue:!0,autoQueue:!0,addRemoveLinks:!1,previewsContainer:null,dictDefaultMessage:"Drop files here to upload",dictFallbackMessage:"Your browser does not support drag'n'drop file uploads.",dictFallbackText:"Please use the fallback form below to upload your files like in the olden days.",dictFileTooBig:"File is too big ({{filesize}}MiB). Max filesize: {{maxFilesize}}MiB.",dictInvalidFileType:"You can't upload files of this type.",dictResponseError:"Server responded with {{statusCode}} code.",dictCancelUpload:"Cancel upload",dictCancelUploadConfirmation:"Are you sure you want to cancel this upload?",dictRemoveFile:"Remove file",dictRemoveFileConfirmation:null,dictMaxFilesExceeded:"You can not upload any more files.",accept:function(a,b){return b()},init:function(){return i},forceFallback:!1,fallback:function(){var a,c,d,e,f,g;for(this.element.className=""+this.element.className+" dz-browser-not-supported",g=this.element.getElementsByTagName("div"),e=0,f=g.length;f>e;e++)a=g[e],/(^| )dz-message($| )/.test(a.className)&&(c=a,a.className="dz-message");return c||(c=b.createElement('<div class="dz-message"><span></span></div>'),this.element.appendChild(c)),d=c.getElementsByTagName("span")[0],d&&(d.textContent=this.options.dictFallbackMessage),this.element.appendChild(this.getFallbackForm())},resize:function(a){var b,c,d;return b={srcX:0,srcY:0,srcWidth:a.width,srcHeight:a.height},c=a.width/a.height,b.optWidth=this.options.thumbnailWidth,b.optHeight=this.options.thumbnailHeight,null==b.optWidth&&null==b.optHeight?(b.optWidth=b.srcWidth,b.optHeight=b.srcHeight):null==b.optWidth?b.optWidth=c*b.optHeight:null==b.optHeight&&(b.optHeight=1/c*b.optWidth),d=b.optWidth/b.optHeight,a.height<b.optHeight||a.width<b.optWidth?(b.trgHeight=b.srcHeight,b.trgWidth=b.srcWidth):c>d?(b.srcHeight=a.height,b.srcWidth=b.srcHeight*d):(b.srcWidth=a.width,b.srcHeight=b.srcWidth/d),b.srcX=(a.width-b.srcWidth)/2,b.srcY=(a.height-b.srcHeight)/2,b},drop:function(){return this.element.classList.remove("dz-drag-hover")},dragstart:i,dragend:function(){return this.element.classList.remove("dz-drag-hover")},dragenter:function(){return this.element.classList.add("dz-drag-hover")},dragover:function(){return this.element.classList.add("dz-drag-hover")},dragleave:function(){return this.element.classList.remove("dz-drag-hover")},paste:i,reset:function(){return this.element.classList.remove("dz-started")},addedfile:function(a){var c,d,e,f,g,h,i,j,k,l,m,n,o;if(this.element===this.previewsContainer&&this.element.classList.add("dz-started"),this.previewsContainer){for(a.previewElement=b.createElement(this.options.previewTemplate.trim()),a.previewTemplate=a.previewElement,this.previewsContainer.appendChild(a.previewElement),l=a.previewElement.querySelectorAll("[data-dz-name]"),f=0,i=l.length;i>f;f++)c=l[f],c.textContent=a.name;for(m=a.previewElement.querySelectorAll("[data-dz-size]"),g=0,j=m.length;j>g;g++)c=m[g],c.innerHTML=this.filesize(a.size);for(this.options.addRemoveLinks&&(a._removeLink=b.createElement('<a class="dz-remove" href="javascript:undefined;" data-dz-remove>'+this.options.dictRemoveFile+"</a>"),a.previewElement.appendChild(a._removeLink)),d=function(c){return function(d){return d.preventDefault(),d.stopPropagation(),a.status===b.UPLOADING?b.confirm(c.options.dictCancelUploadConfirmation,function(){return c.removeFile(a)}):c.options.dictRemoveFileConfirmation?b.confirm(c.options.dictRemoveFileConfirmation,function(){return c.removeFile(a)}):c.removeFile(a)}}(this),n=a.previewElement.querySelectorAll("[data-dz-remove]"),o=[],h=0,k=n.length;k>h;h++)e=n[h],o.push(e.addEventListener("click",d));return o}},removedfile:function(a){var b;return a.previewElement&&null!=(b=a.previewElement)&&b.parentNode.removeChild(a.previewElement),this._updateMaxFilesReachedClass()},thumbnail:function(a,b){var c,d,e,f,g;if(a.previewElement){for(a.previewElement.classList.remove("dz-file-preview"),a.previewElement.classList.add("dz-image-preview"),f=a.previewElement.querySelectorAll("[data-dz-thumbnail]"),g=[],d=0,e=f.length;e>d;d++)c=f[d],c.alt=a.name,g.push(c.src=b);return g}},error:function(a,b){var c,d,e,f,g;if(a.previewElement){for(a.previewElement.classList.add("dz-error"),"String"!=typeof b&&b.error&&(b=b.error),f=a.previewElement.querySelectorAll("[data-dz-errormessage]"),g=[],d=0,e=f.length;e>d;d++)c=f[d],g.push(c.textContent=b);return g}},errormultiple:i,processing:function(a){return a.previewElement&&(a.previewElement.classList.add("dz-processing"),a._removeLink)?a._removeLink.textContent=this.options.dictCancelUpload:void 0},processingmultiple:i,uploadprogress:function(a,b){var c,d,e,f,g;if(a.previewElement){for(f=a.previewElement.querySelectorAll("[data-dz-uploadprogress]"),g=[],d=0,e=f.length;e>d;d++)c=f[d],g.push(c.style.width=""+b+"%");return g}},totaluploadprogress:i,sending:i,sendingmultiple:i,success:function(a){return a.previewElement?a.previewElement.classList.add("dz-success"):void 0},successmultiple:i,canceled:function(a){return this.emit("error",a,"Upload canceled.")},canceledmultiple:i,complete:function(a){return a._removeLink?a._removeLink.textContent=this.options.dictRemoveFile:void 0},completemultiple:i,maxfilesexceeded:i,maxfilesreached:i,previewTemplate:'<div class="dz-preview dz-file-preview">\n  <div class="dz-details">\n    <div class="dz-filename"><span data-dz-name></span></div>\n    <div class="dz-size" data-dz-size></div>\n    <img data-dz-thumbnail />\n  </div>\n  <div class="dz-progress"><span class="dz-upload" data-dz-uploadprogress></span></div>\n  <div class="dz-success-mark"><span>✔</span></div>\n  <div class="dz-error-mark"><span>✘</span></div>\n  <div class="dz-error-message"><span data-dz-errormessage></span></div>\n</div>'},c=function(){var a,b,c,d,e,f,g;for(d=arguments[0],c=2<=arguments.length?m.call(arguments,1):[],f=0,g=c.length;g>f;f++){b=c[f];for(a in b)e=b[a],d[a]=e}return d},b.prototype.getAcceptedFiles=function(){var a,b,c,d,e;for(d=this.files,e=[],b=0,c=d.length;c>b;b++)a=d[b],a.accepted&&e.push(a);return e},b.prototype.getRejectedFiles=function(){var a,b,c,d,e;for(d=this.files,e=[],b=0,c=d.length;c>b;b++)a=d[b],a.accepted||e.push(a);return e},b.prototype.getFilesWithStatus=function(a){var b,c,d,e,f;for(e=this.files,f=[],c=0,d=e.length;d>c;c++)b=e[c],b.status===a&&f.push(b);return f},b.prototype.getQueuedFiles=function(){return this.getFilesWithStatus(b.QUEUED)},b.prototype.getUploadingFiles=function(){return this.getFilesWithStatus(b.UPLOADING)},b.prototype.getActiveFiles=function(){var a,c,d,e,f;for(e=this.files,f=[],c=0,d=e.length;d>c;c++)a=e[c],(a.status===b.UPLOADING||a.status===b.QUEUED)&&f.push(a);return f},b.prototype.init=function(){var a,c,d,e,f,g,h;for("form"===this.element.tagName&&this.element.setAttribute("enctype","multipart/form-data"),this.element.classList.contains("dropzone")&&!this.element.querySelector(".dz-message")&&this.element.appendChild(b.createElement('<div class="dz-default dz-message"><span>'+this.options.dictDefaultMessage+"</span></div>")),this.clickableElements.length&&(d=function(a){return function(){return a.hiddenFileInput&&document.body.removeChild(a.hiddenFileInput),a.hiddenFileInput=document.createElement("input"),a.hiddenFileInput.setAttribute("type","file"),(null==a.options.maxFiles||a.options.maxFiles>1)&&a.hiddenFileInput.setAttribute("multiple","multiple"),a.hiddenFileInput.className="dz-hidden-input",null!=a.options.acceptedFiles&&a.hiddenFileInput.setAttribute("accept",a.options.acceptedFiles),a.hiddenFileInput.style.visibility="hidden",a.hiddenFileInput.style.position="absolute",a.hiddenFileInput.style.top="0",a.hiddenFileInput.style.left="0",a.hiddenFileInput.style.height="0",a.hiddenFileInput.style.width="0",document.body.appendChild(a.hiddenFileInput),a.hiddenFileInput.addEventListener("change",function(){var b,c,e,f;if(c=a.hiddenFileInput.files,c.length)for(e=0,f=c.length;f>e;e++)b=c[e],a.addFile(b);return d()})}}(this))(),this.URL=null!=(g=window.URL)?g:window.webkitURL,h=this.events,e=0,f=h.length;f>e;e++)a=h[e],this.on(a,this.options[a]);return this.on("uploadprogress",function(a){return function(){return a.updateTotalUploadProgress()}}(this)),this.on("removedfile",function(a){return function(){return a.updateTotalUploadProgress()}}(this)),this.on("canceled",function(a){return function(b){return a.emit("complete",b)}}(this)),this.on("complete",function(a){return function(){return 0===a.getUploadingFiles().length&&0===a.getQueuedFiles().length?setTimeout(function(){return a.emit("queuecomplete")},0):void 0}}(this)),c=function(a){return a.stopPropagation(),a.preventDefault?a.preventDefault():a.returnValue=!1},this.listeners=[{element:this.element,events:{dragstart:function(a){return function(b){return a.emit("dragstart",b)}}(this),dragenter:function(a){return function(b){return c(b),a.emit("dragenter",b)}}(this),dragover:function(a){return function(b){var d;try{d=b.dataTransfer.effectAllowed}catch(e){}return b.dataTransfer.dropEffect="move"===d||"linkMove"===d?"move":"copy",c(b),a.emit("dragover",b)}}(this),dragleave:function(a){return function(b){return a.emit("dragleave",b)}}(this),drop:function(a){return function(b){return c(b),a.drop(b)}}(this),dragend:function(a){return function(b){return a.emit("dragend",b)}}(this)}}],this.clickableElements.forEach(function(a){return function(c){return a.listeners.push({element:c,events:{click:function(d){return c!==a.element||d.target===a.element||b.elementInside(d.target,a.element.querySelector(".dz-message"))?a.hiddenFileInput.click():void 0}}})}}(this)),this.enable(),this.options.init.call(this)},b.prototype.destroy=function(){var a;return this.disable(),this.removeAllFiles(!0),(null!=(a=this.hiddenFileInput)?a.parentNode:void 0)&&(this.hiddenFileInput.parentNode.removeChild(this.hiddenFileInput),this.hiddenFileInput=null),delete this.element.dropzone,b.instances.splice(b.instances.indexOf(this),1)},b.prototype.updateTotalUploadProgress=function(){var a,b,c,d,e,f,g,h;if(d=0,c=0,a=this.getActiveFiles(),a.length){for(h=this.getActiveFiles(),f=0,g=h.length;g>f;f++)b=h[f],d+=b.upload.bytesSent,c+=b.upload.total;e=100*d/c}else e=100;return this.emit("totaluploadprogress",e,c,d)},b.prototype._getParamName=function(a){return"function"==typeof this.options.paramName?this.options.paramName(a):""+this.options.paramName+(this.options.uploadMultiple?"["+a+"]":"")},b.prototype.getFallbackForm=function(){var a,c,d,e;return(a=this.getExistingFallback())?a:(d='<div class="dz-fallback">',this.options.dictFallbackText&&(d+="<p>"+this.options.dictFallbackText+"</p>"),d+='<input type="file" name="'+this._getParamName(0)+'" '+(this.options.uploadMultiple?'multiple="multiple"':void 0)+' /><input type="submit" value="Upload!"></div>',c=b.createElement(d),"FORM"!==this.element.tagName?(e=b.createElement('<form action="'+this.options.url+'" enctype="multipart/form-data" method="'+this.options.method+'"></form>'),e.appendChild(c)):(this.element.setAttribute("enctype","multipart/form-data"),this.element.setAttribute("method",this.options.method)),null!=e?e:c)},b.prototype.getExistingFallback=function(){var a,b,c,d,e,f;for(b=function(a){var b,c,d;for(c=0,d=a.length;d>c;c++)if(b=a[c],/(^| )fallback($| )/.test(b.className))return b},f=["div","form"],d=0,e=f.length;e>d;d++)if(c=f[d],a=b(this.element.getElementsByTagName(c)))return a},b.prototype.setupEventListeners=function(){var a,b,c,d,e,f,g;for(f=this.listeners,g=[],d=0,e=f.length;e>d;d++)a=f[d],g.push(function(){var d,e;d=a.events,e=[];for(b in d)c=d[b],e.push(a.element.addEventListener(b,c,!1));return e}());return g},b.prototype.removeEventListeners=function(){var a,b,c,d,e,f,g;for(f=this.listeners,g=[],d=0,e=f.length;e>d;d++)a=f[d],g.push(function(){var d,e;d=a.events,e=[];for(b in d)c=d[b],e.push(a.element.removeEventListener(b,c,!1));return e}());return g},b.prototype.disable=function(){var a,b,c,d,e;for(this.clickableElements.forEach(function(a){return a.classList.remove("dz-clickable")}),this.removeEventListeners(),d=this.files,e=[],b=0,c=d.length;c>b;b++)a=d[b],e.push(this.cancelUpload(a));return e},b.prototype.enable=function(){return this.clickableElements.forEach(function(a){return a.classList.add("dz-clickable")}),this.setupEventListeners()},b.prototype.filesize=function(a){var b;return a>=109951162777.6?(a/=109951162777.6,b="TiB"):a>=107374182.4?(a/=107374182.4,b="GiB"):a>=104857.6?(a/=104857.6,b="MiB"):a>=102.4?(a/=102.4,b="KiB"):(a=10*a,b="b"),"<strong>"+Math.round(a)/10+"</strong> "+b},b.prototype._updateMaxFilesReachedClass=function(){return null!=this.options.maxFiles&&this.getAcceptedFiles().length>=this.options.maxFiles?(this.getAcceptedFiles().length===this.options.maxFiles&&this.emit("maxfilesreached",this.files),this.element.classList.add("dz-max-files-reached")):this.element.classList.remove("dz-max-files-reached")},b.prototype.drop=function(a){var b,c;a.dataTransfer&&(this.emit("drop",a),b=a.dataTransfer.files,b.length&&(c=a.dataTransfer.items,c&&c.length&&null!=c[0].webkitGetAsEntry?this._addFilesFromItems(c):this.handleFiles(b)))},b.prototype.paste=function(a){var b,c;if(null!=(null!=a&&null!=(c=a.clipboardData)?c.items:void 0))return this.emit("paste",a),b=a.clipboardData.items,b.length?this._addFilesFromItems(b):void 0},b.prototype.handleFiles=function(a){var b,c,d,e;for(e=[],c=0,d=a.length;d>c;c++)b=a[c],e.push(this.addFile(b));return e},b.prototype._addFilesFromItems=function(a){var b,c,d,e,f;for(f=[],d=0,e=a.length;e>d;d++)c=a[d],f.push(null!=c.webkitGetAsEntry&&(b=c.webkitGetAsEntry())?b.isFile?this.addFile(c.getAsFile()):b.isDirectory?this._addFilesFromDirectory(b,b.name):void 0:null!=c.getAsFile?null==c.kind||"file"===c.kind?this.addFile(c.getAsFile()):void 0:void 0);return f},b.prototype._addFilesFromDirectory=function(a,b){var c,d;return c=a.createReader(),d=function(a){return function(c){var d,e,f;for(e=0,f=c.length;f>e;e++)d=c[e],d.isFile?d.file(function(c){return a.options.ignoreHiddenFiles&&"."===c.name.substring(0,1)?void 0:(c.fullPath=""+b+"/"+c.name,a.addFile(c))}):d.isDirectory&&a._addFilesFromDirectory(d,""+b+"/"+d.name)}}(this),c.readEntries(d,function(a){return"undefined"!=typeof console&&null!==console&&"function"==typeof console.log?console.log(a):void 0})},b.prototype.accept=function(a,c){return a.size>1024*this.options.maxFilesize*1024?c(this.options.dictFileTooBig.replace("{{filesize}}",Math.round(a.size/1024/10.24)/100).replace("{{maxFilesize}}",this.options.maxFilesize)):b.isValidFile(a,this.options.acceptedFiles)?null!=this.options.maxFiles&&this.getAcceptedFiles().length>=this.options.maxFiles?(c(this.options.dictMaxFilesExceeded.replace("{{maxFiles}}",this.options.maxFiles)),this.emit("maxfilesexceeded",a)):this.options.accept.call(this,a,c):c(this.options.dictInvalidFileType)},b.prototype.addFile=function(a){return a.upload={progress:0,total:a.size,bytesSent:0},this.files.push(a),a.status=b.ADDED,this.emit("addedfile",a),this._enqueueThumbnail(a),this.accept(a,function(b){return function(c){return c?(a.accepted=!1,b._errorProcessing([a],c)):(a.accepted=!0,b.options.autoQueue&&b.enqueueFile(a)),b._updateMaxFilesReachedClass()}}(this))},b.prototype.enqueueFiles=function(a){var b,c,d;for(c=0,d=a.length;d>c;c++)b=a[c],this.enqueueFile(b);return null},b.prototype.enqueueFile=function(a){if(a.status!==b.ADDED||a.accepted!==!0)throw new Error("This file can't be queued because it has already been processed or was rejected.");return a.status=b.QUEUED,this.options.autoProcessQueue?setTimeout(function(a){return function(){return a.processQueue()}}(this),0):void 0},b.prototype._thumbnailQueue=[],b.prototype._processingThumbnail=!1,b.prototype._enqueueThumbnail=function(a){return this.options.createImageThumbnails&&a.type.match(/image.*/)&&a.size<=1024*this.options.maxThumbnailFilesize*1024?(this._thumbnailQueue.push(a),setTimeout(function(a){return function(){return a._processThumbnailQueue()}}(this),0)):void 0},b.prototype._processThumbnailQueue=function(){return this._processingThumbnail||0===this._thumbnailQueue.length?void 0:(this._processingThumbnail=!0,this.createThumbnail(this._thumbnailQueue.shift(),function(a){return function(){return a._processingThumbnail=!1,a._processThumbnailQueue()}}(this)))},b.prototype.removeFile=function(a){return a.status===b.UPLOADING&&this.cancelUpload(a),this.files=j(this.files,a),this.emit("removedfile",a),0===this.files.length?this.emit("reset"):void 0},b.prototype.removeAllFiles=function(a){var c,d,e,f;for(null==a&&(a=!1),f=this.files.slice(),d=0,e=f.length;e>d;d++)c=f[d],(c.status!==b.UPLOADING||a)&&this.removeFile(c);return null},b.prototype.createThumbnail=function(a,b){var c;return c=new FileReader,c.onload=function(d){return function(){var e;return e=document.createElement("img"),e.onload=function(){var c,f,g,i,j,k,l,m;return a.width=e.width,a.height=e.height,g=d.options.resize.call(d,a),null==g.trgWidth&&(g.trgWidth=g.optWidth),null==g.trgHeight&&(g.trgHeight=g.optHeight),c=document.createElement("canvas"),f=c.getContext("2d"),c.width=g.trgWidth,c.height=g.trgHeight,h(f,e,null!=(j=g.srcX)?j:0,null!=(k=g.srcY)?k:0,g.srcWidth,g.srcHeight,null!=(l=g.trgX)?l:0,null!=(m=g.trgY)?m:0,g.trgWidth,g.trgHeight),i=c.toDataURL("image/png"),d.emit("thumbnail",a,i),null!=b?b():void 0},e.src=c.result}}(this),c.readAsDataURL(a)},b.prototype.processQueue=function(){var a,b,c,d;if(b=this.options.parallelUploads,c=this.getUploadingFiles().length,a=c,!(c>=b)&&(d=this.getQueuedFiles(),d.length>0)){if(this.options.uploadMultiple)return this.processFiles(d.slice(0,b-c));for(;b>a;){if(!d.length)return;this.processFile(d.shift()),a++}}},b.prototype.processFile=function(a){return this.processFiles([a])},b.prototype.processFiles=function(a){var c,d,e;for(d=0,e=a.length;e>d;d++)c=a[d],c.processing=!0,c.status=b.UPLOADING,this.emit("processing",c);return this.options.uploadMultiple&&this.emit("processingmultiple",a),this.uploadFiles(a)},b.prototype._getFilesWithXhr=function(a){var b,c;return c=function(){var c,d,e,f;for(e=this.files,f=[],c=0,d=e.length;d>c;c++)b=e[c],b.xhr===a&&f.push(b);return f}.call(this)},b.prototype.cancelUpload=function(a){var c,d,e,f,g,h,i;if(a.status===b.UPLOADING){for(d=this._getFilesWithXhr(a.xhr),e=0,g=d.length;g>e;e++)c=d[e],c.status=b.CANCELED;for(a.xhr.abort(),f=0,h=d.length;h>f;f++)c=d[f],this.emit("canceled",c);this.options.uploadMultiple&&this.emit("canceledmultiple",d)}else((i=a.status)===b.ADDED||i===b.QUEUED)&&(a.status=b.CANCELED,this.emit("canceled",a),this.options.uploadMultiple&&this.emit("canceledmultiple",[a]));return this.options.autoProcessQueue?this.processQueue():void 0},b.prototype.uploadFile=function(a){return this.uploadFiles([a])},b.prototype.uploadFiles=function(a){var d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,A,B,C,D,E,F,G,H,I;for(t=new XMLHttpRequest,u=0,y=a.length;y>u;u++)d=a[u],d.xhr=t;t.open(this.options.method,this.options.url,!0),t.withCredentials=!!this.options.withCredentials,q=null,f=function(b){return function(){var c,e,f;for(f=[],c=0,e=a.length;e>c;c++)d=a[c],f.push(b._errorProcessing(a,q||b.options.dictResponseError.replace("{{statusCode}}",t.status),t));return f}}(this),r=function(b){return function(c){var e,f,g,h,i,j,k,l,m;if(null!=c)for(f=100*c.loaded/c.total,g=0,j=a.length;j>g;g++)d=a[g],d.upload={progress:f,total:c.total,bytesSent:c.loaded};else{for(e=!0,f=100,h=0,k=a.length;k>h;h++)d=a[h],(100!==d.upload.progress||d.upload.bytesSent!==d.upload.total)&&(e=!1),d.upload.progress=f,d.upload.bytesSent=d.upload.total;if(e)return}for(m=[],i=0,l=a.length;l>i;i++)d=a[i],m.push(b.emit("uploadprogress",d,f,d.upload.bytesSent));return m}}(this),t.onload=function(c){return function(d){var e;if(a[0].status!==b.CANCELED&&4===t.readyState){if(q=t.responseText,t.getResponseHeader("content-type")&&~t.getResponseHeader("content-type").indexOf("application/json"))try{q=JSON.parse(q)}catch(g){d=g,q="Invalid JSON response from server."}return r(),200<=(e=t.status)&&300>e?c._finished(a,q,d):f()}}}(this),t.onerror=function(){return function(){return a[0].status!==b.CANCELED?f():void 0}}(this),p=null!=(D=t.upload)?D:t,p.onprogress=r,i={Accept:"application/json","Cache-Control":"no-cache","X-Requested-With":"XMLHttpRequest"},this.options.headers&&c(i,this.options.headers);for(g in i)h=i[g],t.setRequestHeader(g,h);if(e=new FormData,this.options.params){E=this.options.params;for(n in E)s=E[n],e.append(n,s)}for(v=0,z=a.length;z>v;v++)d=a[v],this.emit("sending",d,t,e);if(this.options.uploadMultiple&&this.emit("sendingmultiple",a,t,e),"FORM"===this.element.tagName)for(F=this.element.querySelectorAll("input, textarea, select, button"),w=0,A=F.length;A>w;w++)if(k=F[w],l=k.getAttribute("name"),m=k.getAttribute("type"),"SELECT"===k.tagName&&k.hasAttribute("multiple"))for(G=k.options,x=0,B=G.length;B>x;x++)o=G[x],o.selected&&e.append(l,o.value);else(!m||"checkbox"!==(H=m.toLowerCase())&&"radio"!==H||k.checked)&&e.append(l,k.value);for(j=C=0,I=a.length-1;I>=0?I>=C:C>=I;j=I>=0?++C:--C)e.append(this._getParamName(j),a[j],a[j].name);return t.send(e)},b.prototype._finished=function(a,c,d){var e,f,g;for(f=0,g=a.length;g>f;f++)e=a[f],e.status=b.SUCCESS,this.emit("success",e,c,d),this.emit("complete",e);return this.options.uploadMultiple&&(this.emit("successmultiple",a,c,d),this.emit("completemultiple",a)),this.options.autoProcessQueue?this.processQueue():void 0},b.prototype._errorProcessing=function(a,c,d){var e,f,g;for(f=0,g=a.length;g>f;f++)e=a[f],e.status=b.ERROR,this.emit("error",e,c,d),this.emit("complete",e);return this.options.uploadMultiple&&(this.emit("errormultiple",a,c,d),this.emit("completemultiple",a)),this.options.autoProcessQueue?this.processQueue():void 0},b}(d),b.version="3.10.2",b.options={},b.optionsForElement=function(a){return a.getAttribute("id")?b.options[e(a.getAttribute("id"))]:void 0},b.instances=[],b.forElement=function(a){if("string"==typeof a&&(a=document.querySelector(a)),null==(null!=a?a.dropzone:void 0))throw new Error("No Dropzone found for given element. This is probably because you're trying to access it before Dropzone had the time to initialize. Use the `init` option to setup any additional observers on your Dropzone.");return a.dropzone},b.autoDiscover=!0,b.discover=function(){var a,c,d,e,f,g;for(document.querySelectorAll?d=document.querySelectorAll(".dropzone"):(d=[],a=function(a){var b,c,e,f;for(f=[],c=0,e=a.length;e>c;c++)b=a[c],f.push(/(^| )dropzone($| )/.test(b.className)?d.push(b):void 0);return f},a(document.getElementsByTagName("div")),a(document.getElementsByTagName("form"))),g=[],e=0,f=d.length;f>e;e++)c=d[e],g.push(b.optionsForElement(c)!==!1?new b(c):void 0);return g},b.blacklistedBrowsers=[/opera.*Macintosh.*version\/12/i],b.isBrowserSupported=function(){var a,c,d,e,f;if(a=!0,window.File&&window.FileReader&&window.FileList&&window.Blob&&window.FormData&&document.querySelector)if("classList"in document.createElement("a"))for(f=b.blacklistedBrowsers,d=0,e=f.length;e>d;d++)c=f[d],c.test(navigator.userAgent)&&(a=!1);else a=!1;else a=!1;return a},j=function(a,b){var c,d,e,f;for(f=[],d=0,e=a.length;e>d;d++)c=a[d],c!==b&&f.push(c);return f},e=function(a){return a.replace(/[\-_](\w)/g,function(a){return a.charAt(1).toUpperCase()})},b.createElement=function(a){var b;return b=document.createElement("div"),b.innerHTML=a,b.childNodes[0]},b.elementInside=function(a,b){if(a===b)return!0;for(;a=a.parentNode;)if(a===b)return!0;return!1},b.getElement=function(a,b){var c;if("string"==typeof a?c=document.querySelector(a):null!=a.nodeType&&(c=a),null==c)throw new Error("Invalid `"+b+"` option provided. Please provide a CSS selector or a plain HTML element.");return c},b.getElements=function(a,b){var c,d,e,f,g,h,i,j;if(a instanceof Array){e=[];try{for(f=0,h=a.length;h>f;f++)d=a[f],e.push(this.getElement(d,b))}catch(k){c=k,e=null}}else if("string"==typeof a)for(e=[],j=document.querySelectorAll(a),g=0,i=j.length;i>g;g++)d=j[g],e.push(d);else null!=a.nodeType&&(e=[a]);if(null==e||!e.length)throw new Error("Invalid `"+b+"` option provided. Please provide a CSS selector, a plain HTML element or a list of those.");return e},b.confirm=function(a,b,c){return window.confirm(a)?b():null!=c?c():void 0},b.isValidFile=function(a,b){var c,d,e,f,g;if(!b)return!0;for(b=b.split(","),d=a.type,c=d.replace(/\/.*$/,""),f=0,g=b.length;g>f;f++)if(e=b[f],e=e.trim(),"."===e.charAt(0)){if(-1!==a.name.toLowerCase().indexOf(e.toLowerCase(),a.name.length-e.length))return!0}else if(/\/\*$/.test(e)){if(c===e.replace(/\/.*$/,""))return!0}else if(d===e)return!0;return!1},"undefined"!=typeof jQuery&&null!==jQuery&&(jQuery.fn.dropzone=function(a){return this.each(function(){return new b(this,a)})}),"undefined"!=typeof c&&null!==c?c.exports=b:window.Dropzone=b,b.ADDED="added",b.QUEUED="queued",b.ACCEPTED=b.QUEUED,b.UPLOADING="uploading",b.PROCESSING=b.UPLOADING,b.CANCELED="canceled",b.ERROR="error",b.SUCCESS="success",g=function(a){var b,c,d,e,f,g,h,i,j,k;for(h=a.naturalWidth,g=a.naturalHeight,c=document.createElement("canvas"),c.width=1,c.height=g,d=c.getContext("2d"),d.drawImage(a,0,0),e=d.getImageData(0,0,1,g).data,k=0,f=g,i=g;i>k;)b=e[4*(i-1)+3],0===b?f=i:k=i,i=f+k>>1;return j=i/g,0===j?1:j},h=function(a,b,c,d,e,f,h,i,j,k){var l;return l=g(b),a.drawImage(b,c,d,e,f,h,i,j,k/l)},f=function(a,b){var c,d,e,f,g,h,i,j,k;if(e=!1,k=!0,d=a.document,j=d.documentElement,c=d.addEventListener?"addEventListener":"attachEvent",i=d.addEventListener?"removeEventListener":"detachEvent",h=d.addEventListener?"":"on",f=function(c){return"readystatechange"!==c.type||"complete"===d.readyState?(("load"===c.type?a:d)[i](h+c.type,f,!1),!e&&(e=!0)?b.call(a,c.type||c):void 0):void 0},g=function(){var a;try{j.doScroll("left")}catch(b){return a=b,void setTimeout(g,50)}return f("poll")},"complete"!==d.readyState){if(d.createEventObject&&j.doScroll){try{k=!a.frameElement}catch(l){}k&&g()}return d[c](h+"DOMContentLoaded",f,!1),d[c](h+"readystatechange",f,!1),a[c](h+"load",f,!1)}},b._autoDiscoverFunction=function(){return b.autoDiscover?b.discover():void 0},f(window,b._autoDiscoverFunction)}).call(this)}),"object"==typeof exports?module.exports=a("dropzone"):"function"==typeof define&&define.amd?define([],function(){return a("dropzone")}):this.Dropzone=a("dropzone")}();;/*! DataTables 1.10.2
 * ©2008-2014 SpryMedia Ltd - datatables.net/license
 */
(function(za,O,l){var N=function(h){function T(a){var b,c,d={};h.each(a,function(e){if((b=e.match(/^([^A-Z]+?)([A-Z])/))&&-1!=="a aa ai ao as b fn i m o s ".indexOf(b[1]+" "))c=e.replace(b[0],b[2].toLowerCase()),d[c]=e,"o"===b[1]&&T(a[e])});a._hungarianMap=d}function G(a,b,c){a._hungarianMap||T(a);var d;h.each(b,function(e){d=a._hungarianMap[e];if(d!==l&&(c||b[d]===l))"o"===d.charAt(0)?(b[d]||(b[d]={}),h.extend(!0,b[d],b[e]),G(a[d],b[d],c)):b[d]=b[e]})}function N(a){var b=p.defaults.oLanguage,c=a.sZeroRecords;
!a.sEmptyTable&&(c&&"No data available in table"===b.sEmptyTable)&&D(a,a,"sZeroRecords","sEmptyTable");!a.sLoadingRecords&&(c&&"Loading..."===b.sLoadingRecords)&&D(a,a,"sZeroRecords","sLoadingRecords");a.sInfoThousands&&(a.sThousands=a.sInfoThousands);(a=a.sDecimal)&&cb(a)}function db(a){w(a,"ordering","bSort");w(a,"orderMulti","bSortMulti");w(a,"orderClasses","bSortClasses");w(a,"orderCellsTop","bSortCellsTop");w(a,"order","aaSorting");w(a,"orderFixed","aaSortingFixed");w(a,"paging","bPaginate");
w(a,"pagingType","sPaginationType");w(a,"pageLength","iDisplayLength");w(a,"searching","bFilter");if(a=a.aoSearchCols)for(var b=0,c=a.length;b<c;b++)a[b]&&G(p.models.oSearch,a[b])}function eb(a){w(a,"orderable","bSortable");w(a,"orderData","aDataSort");w(a,"orderSequence","asSorting");w(a,"orderDataType","sortDataType")}function fb(a){var a=a.oBrowser,b=h("<div/>").css({position:"absolute",top:0,left:0,height:1,width:1,overflow:"hidden"}).append(h("<div/>").css({position:"absolute",top:1,left:1,width:100,
overflow:"scroll"}).append(h('<div class="test"/>').css({width:"100%",height:10}))).appendTo("body"),c=b.find(".test");a.bScrollOversize=100===c[0].offsetWidth;a.bScrollbarLeft=1!==c.offset().left;b.remove()}function gb(a,b,c,d,e,f){var g,j=!1;c!==l&&(g=c,j=!0);for(;d!==e;)a.hasOwnProperty(d)&&(g=j?b(g,a[d],d,a):a[d],j=!0,d+=f);return g}function Aa(a,b){var c=p.defaults.column,d=a.aoColumns.length,c=h.extend({},p.models.oColumn,c,{nTh:b?b:O.createElement("th"),sTitle:c.sTitle?c.sTitle:b?b.innerHTML:
"",aDataSort:c.aDataSort?c.aDataSort:[d],mData:c.mData?c.mData:d,idx:d});a.aoColumns.push(c);c=a.aoPreSearchCols;c[d]=h.extend({},p.models.oSearch,c[d]);fa(a,d,null)}function fa(a,b,c){var b=a.aoColumns[b],d=a.oClasses,e=h(b.nTh);if(!b.sWidthOrig){b.sWidthOrig=e.attr("width")||null;var f=(e.attr("style")||"").match(/width:\s*(\d+[pxem%]+)/);f&&(b.sWidthOrig=f[1])}c!==l&&null!==c&&(eb(c),G(p.defaults.column,c),c.mDataProp!==l&&!c.mData&&(c.mData=c.mDataProp),c.sType&&(b._sManualType=c.sType),c.className&&
!c.sClass&&(c.sClass=c.className),h.extend(b,c),D(b,c,"sWidth","sWidthOrig"),"number"===typeof c.iDataSort&&(b.aDataSort=[c.iDataSort]),D(b,c,"aDataSort"));var g=b.mData,j=U(g),i=b.mRender?U(b.mRender):null,c=function(a){return"string"===typeof a&&-1!==a.indexOf("@")};b._bAttrSrc=h.isPlainObject(g)&&(c(g.sort)||c(g.type)||c(g.filter));b.fnGetData=function(a,b,c){var d=j(a,b,l,c);return i&&b?i(d,b,a,c):d};b.fnSetData=function(a,b,c){return Ba(g)(a,b,c)};a.oFeatures.bSort||(b.bSortable=!1,e.addClass(d.sSortableNone));
a=-1!==h.inArray("asc",b.asSorting);c=-1!==h.inArray("desc",b.asSorting);!b.bSortable||!a&&!c?(b.sSortingClass=d.sSortableNone,b.sSortingClassJUI=""):a&&!c?(b.sSortingClass=d.sSortableAsc,b.sSortingClassJUI=d.sSortJUIAscAllowed):!a&&c?(b.sSortingClass=d.sSortableDesc,b.sSortingClassJUI=d.sSortJUIDescAllowed):(b.sSortingClass=d.sSortable,b.sSortingClassJUI=d.sSortJUI)}function V(a){if(!1!==a.oFeatures.bAutoWidth){var b=a.aoColumns;Ca(a);for(var c=0,d=b.length;c<d;c++)b[c].nTh.style.width=b[c].sWidth}b=
a.oScroll;(""!==b.sY||""!==b.sX)&&W(a);u(a,null,"column-sizing",[a])}function ga(a,b){var c=X(a,"bVisible");return"number"===typeof c[b]?c[b]:null}function Y(a,b){var c=X(a,"bVisible"),c=h.inArray(b,c);return-1!==c?c:null}function Z(a){return X(a,"bVisible").length}function X(a,b){var c=[];h.map(a.aoColumns,function(a,e){a[b]&&c.push(e)});return c}function Da(a){var b=a.aoColumns,c=a.aoData,d=p.ext.type.detect,e,f,g,j,i,h,m,o,k;e=0;for(f=b.length;e<f;e++)if(m=b[e],k=[],!m.sType&&m._sManualType)m.sType=
m._sManualType;else if(!m.sType){g=0;for(j=d.length;g<j;g++){i=0;for(h=c.length;i<h&&!(k[i]===l&&(k[i]=A(a,i,e,"type")),o=d[g](k[i],a),!o||"html"===o);i++);if(o){m.sType=o;break}}m.sType||(m.sType="string")}}function hb(a,b,c,d){var e,f,g,j,i,n,m=a.aoColumns;if(b)for(e=b.length-1;0<=e;e--){n=b[e];var o=n.targets!==l?n.targets:n.aTargets;h.isArray(o)||(o=[o]);f=0;for(g=o.length;f<g;f++)if("number"===typeof o[f]&&0<=o[f]){for(;m.length<=o[f];)Aa(a);d(o[f],n)}else if("number"===typeof o[f]&&0>o[f])d(m.length+
o[f],n);else if("string"===typeof o[f]){j=0;for(i=m.length;j<i;j++)("_all"==o[f]||h(m[j].nTh).hasClass(o[f]))&&d(j,n)}}if(c){e=0;for(a=c.length;e<a;e++)d(e,c[e])}}function I(a,b,c,d){var e=a.aoData.length,f=h.extend(!0,{},p.models.oRow,{src:c?"dom":"data"});f._aData=b;a.aoData.push(f);for(var b=a.aoColumns,f=0,g=b.length;f<g;f++)c&&Ea(a,e,f,A(a,e,f)),b[f].sType=null;a.aiDisplayMaster.push(e);(c||!a.oFeatures.bDeferRender)&&Fa(a,e,c,d);return e}function ha(a,b){var c;b instanceof h||(b=h(b));return b.map(function(b,
e){c=ia(a,e);return I(a,c.data,e,c.cells)})}function A(a,b,c,d){var e=a.iDraw,f=a.aoColumns[c],g=a.aoData[b]._aData,j=f.sDefaultContent,c=f.fnGetData(g,d,{settings:a,row:b,col:c});if(c===l)return a.iDrawError!=e&&null===j&&(P(a,0,"Requested unknown parameter "+("function"==typeof f.mData?"{function}":"'"+f.mData+"'")+" for row "+b,4),a.iDrawError=e),j;if((c===g||null===c)&&null!==j)c=j;else if("function"===typeof c)return c.call(g);return null===c&&"display"==d?"":c}function Ea(a,b,c,d){a.aoColumns[c].fnSetData(a.aoData[b]._aData,
d,{settings:a,row:b,col:c})}function Ga(a){return h.map(a.match(/(\\.|[^\.])+/g),function(a){return a.replace(/\\./g,".")})}function U(a){if(h.isPlainObject(a)){var b={};h.each(a,function(a,c){c&&(b[a]=U(c))});return function(a,c,f,g){var j=b[c]||b._;return j!==l?j(a,c,f,g):a}}if(null===a)return function(a){return a};if("function"===typeof a)return function(b,c,f,g){return a(b,c,f,g)};if("string"===typeof a&&(-1!==a.indexOf(".")||-1!==a.indexOf("[")||-1!==a.indexOf("("))){var c=function(a,b,f){var g,
j;if(""!==f){j=Ga(f);for(var i=0,h=j.length;i<h;i++){f=j[i].match($);g=j[i].match(Q);if(f){j[i]=j[i].replace($,"");""!==j[i]&&(a=a[j[i]]);g=[];j.splice(0,i+1);j=j.join(".");i=0;for(h=a.length;i<h;i++)g.push(c(a[i],b,j));a=f[0].substring(1,f[0].length-1);a=""===a?g:g.join(a);break}else if(g){j[i]=j[i].replace(Q,"");a=a[j[i]]();continue}if(null===a||a[j[i]]===l)return l;a=a[j[i]]}}return a};return function(b,e){return c(b,e,a)}}return function(b){return b[a]}}function Ba(a){if(h.isPlainObject(a))return Ba(a._);
if(null===a)return function(){};if("function"===typeof a)return function(b,d,e){a(b,"set",d,e)};if("string"===typeof a&&(-1!==a.indexOf(".")||-1!==a.indexOf("[")||-1!==a.indexOf("("))){var b=function(a,d,e){var e=Ga(e),f;f=e[e.length-1];for(var g,j,i=0,h=e.length-1;i<h;i++){g=e[i].match($);j=e[i].match(Q);if(g){e[i]=e[i].replace($,"");a[e[i]]=[];f=e.slice();f.splice(0,i+1);g=f.join(".");j=0;for(h=d.length;j<h;j++)f={},b(f,d[j],g),a[e[i]].push(f);return}j&&(e[i]=e[i].replace(Q,""),a=a[e[i]](d));if(null===
a[e[i]]||a[e[i]]===l)a[e[i]]={};a=a[e[i]]}if(f.match(Q))a[f.replace(Q,"")](d);else a[f.replace($,"")]=d};return function(c,d){return b(c,d,a)}}return function(b,d){b[a]=d}}function Ha(a){return C(a.aoData,"_aData")}function ja(a){a.aoData.length=0;a.aiDisplayMaster.length=0;a.aiDisplay.length=0}function ka(a,b,c){for(var d=-1,e=0,f=a.length;e<f;e++)a[e]==b?d=e:a[e]>b&&a[e]--; -1!=d&&c===l&&a.splice(d,1)}function la(a,b,c,d){var e=a.aoData[b],f;if("dom"===c||(!c||"auto"===c)&&"dom"===e.src)e._aData=
ia(a,e).data;else{var g=e.anCells,j;if(g){c=0;for(f=g.length;c<f;c++){for(j=g[c];j.childNodes.length;)j.removeChild(j.firstChild);g[c].innerHTML=A(a,b,c,"display")}}}e._aSortData=null;e._aFilterData=null;a=a.aoColumns;if(d!==l)a[d].sType=null;else{c=0;for(f=a.length;c<f;c++)a[c].sType=null}Ia(e)}function ia(a,b){var c=[],d=[],e=b.firstChild,f,g,j,i=0,n,m=a.aoColumns,o=function(a,b,c){"string"===typeof a&&(b=a.indexOf("@"),-1!==b&&(a=a.substring(b+1),j["@"+a]=c.getAttribute(a)))},k=function(a){g=m[i];
n=h.trim(a.innerHTML);g&&g._bAttrSrc?(j={display:n},o(g.mData.sort,j,a),o(g.mData.type,j,a),o(g.mData.filter,j,a),c.push(j)):c.push(n);i++};if(e)for(;e;){f=e.nodeName.toUpperCase();if("TD"==f||"TH"==f)k(e),d.push(e);e=e.nextSibling}else{d=b.anCells;e=0;for(f=d.length;e<f;e++)k(d[e])}return{data:c,cells:d}}function Fa(a,b,c,d){var e=a.aoData[b],f=e._aData,g=[],j,i,h,m,o;if(null===e.nTr){j=c||O.createElement("tr");e.nTr=j;e.anCells=g;j._DT_RowIndex=b;Ia(e);m=0;for(o=a.aoColumns.length;m<o;m++){h=a.aoColumns[m];
i=c?d[m]:O.createElement(h.sCellType);g.push(i);if(!c||h.mRender||h.mData!==m)i.innerHTML=A(a,b,m,"display");h.sClass&&(i.className+=" "+h.sClass);h.bVisible&&!c?j.appendChild(i):!h.bVisible&&c&&i.parentNode.removeChild(i);h.fnCreatedCell&&h.fnCreatedCell.call(a.oInstance,i,A(a,b,m),f,b,m)}u(a,"aoRowCreatedCallback",null,[j,f,b])}e.nTr.setAttribute("role","row")}function Ia(a){var b=a.nTr,c=a._aData;if(b){c.DT_RowId&&(b.id=c.DT_RowId);if(c.DT_RowClass){var d=c.DT_RowClass.split(" ");a.__rowc=a.__rowc?
Ja(a.__rowc.concat(d)):d;h(b).removeClass(a.__rowc.join(" ")).addClass(c.DT_RowClass)}c.DT_RowData&&h(b).data(c.DT_RowData)}}function ib(a){var b,c,d,e,f,g=a.nTHead,j=a.nTFoot,i=0===h("th, td",g).length,n=a.oClasses,m=a.aoColumns;i&&(e=h("<tr/>").appendTo(g));b=0;for(c=m.length;b<c;b++)f=m[b],d=h(f.nTh).addClass(f.sClass),i&&d.appendTo(e),a.oFeatures.bSort&&(d.addClass(f.sSortingClass),!1!==f.bSortable&&(d.attr("tabindex",a.iTabIndex).attr("aria-controls",a.sTableId),Ka(a,f.nTh,b))),f.sTitle!=d.html()&&
d.html(f.sTitle),La(a,"header")(a,d,f,n);i&&aa(a.aoHeader,g);h(g).find(">tr").attr("role","row");h(g).find(">tr>th, >tr>td").addClass(n.sHeaderTH);h(j).find(">tr>th, >tr>td").addClass(n.sFooterTH);if(null!==j){a=a.aoFooter[0];b=0;for(c=a.length;b<c;b++)f=m[b],f.nTf=a[b].cell,f.sClass&&h(f.nTf).addClass(f.sClass)}}function ba(a,b,c){var d,e,f,g=[],j=[],i=a.aoColumns.length,n;if(b){c===l&&(c=!1);d=0;for(e=b.length;d<e;d++){g[d]=b[d].slice();g[d].nTr=b[d].nTr;for(f=i-1;0<=f;f--)!a.aoColumns[f].bVisible&&
!c&&g[d].splice(f,1);j.push([])}d=0;for(e=g.length;d<e;d++){if(a=g[d].nTr)for(;f=a.firstChild;)a.removeChild(f);f=0;for(b=g[d].length;f<b;f++)if(n=i=1,j[d][f]===l){a.appendChild(g[d][f].cell);for(j[d][f]=1;g[d+i]!==l&&g[d][f].cell==g[d+i][f].cell;)j[d+i][f]=1,i++;for(;g[d][f+n]!==l&&g[d][f].cell==g[d][f+n].cell;){for(c=0;c<i;c++)j[d+c][f+n]=1;n++}h(g[d][f].cell).attr("rowspan",i).attr("colspan",n)}}}}function K(a){var b=u(a,"aoPreDrawCallback","preDraw",[a]);if(-1!==h.inArray(!1,b))B(a,!1);else{var b=
[],c=0,d=a.asStripeClasses,e=d.length,f=a.oLanguage,g=a.iInitDisplayStart,j="ssp"==z(a),i=a.aiDisplay;a.bDrawing=!0;g!==l&&-1!==g&&(a._iDisplayStart=j?g:g>=a.fnRecordsDisplay()?0:g,a.iInitDisplayStart=-1);var g=a._iDisplayStart,n=a.fnDisplayEnd();if(a.bDeferLoading)a.bDeferLoading=!1,a.iDraw++,B(a,!1);else if(j){if(!a.bDestroying&&!jb(a))return}else a.iDraw++;if(0!==i.length){f=j?a.aoData.length:n;for(j=j?0:g;j<f;j++){var m=i[j],o=a.aoData[m];null===o.nTr&&Fa(a,m);m=o.nTr;if(0!==e){var k=d[c%e];o._sRowStripe!=
k&&(h(m).removeClass(o._sRowStripe).addClass(k),o._sRowStripe=k)}u(a,"aoRowCallback",null,[m,o._aData,c,j]);b.push(m);c++}}else c=f.sZeroRecords,1==a.iDraw&&"ajax"==z(a)?c=f.sLoadingRecords:f.sEmptyTable&&0===a.fnRecordsTotal()&&(c=f.sEmptyTable),b[0]=h("<tr/>",{"class":e?d[0]:""}).append(h("<td />",{valign:"top",colSpan:Z(a),"class":a.oClasses.sRowEmpty}).html(c))[0];u(a,"aoHeaderCallback","header",[h(a.nTHead).children("tr")[0],Ha(a),g,n,i]);u(a,"aoFooterCallback","footer",[h(a.nTFoot).children("tr")[0],
Ha(a),g,n,i]);d=h(a.nTBody);d.children().detach();d.append(h(b));u(a,"aoDrawCallback","draw",[a]);a.bSorted=!1;a.bFiltered=!1;a.bDrawing=!1}}function L(a,b){var c=a.oFeatures,d=c.bFilter;c.bSort&&kb(a);d?ca(a,a.oPreviousSearch):a.aiDisplay=a.aiDisplayMaster.slice();!0!==b&&(a._iDisplayStart=0);a._drawHold=b;K(a);a._drawHold=!1}function lb(a){var b=a.oClasses,c=h(a.nTable),c=h("<div/>").insertBefore(c),d=a.oFeatures,e=h("<div/>",{id:a.sTableId+"_wrapper","class":b.sWrapper+(a.nTFoot?"":" "+b.sNoFooter)});
a.nHolding=c[0];a.nTableWrapper=e[0];a.nTableReinsertBefore=a.nTable.nextSibling;for(var f=a.sDom.split(""),g,j,i,n,m,o,k=0;k<f.length;k++){g=null;j=f[k];if("<"==j){i=h("<div/>")[0];n=f[k+1];if("'"==n||'"'==n){m="";for(o=2;f[k+o]!=n;)m+=f[k+o],o++;"H"==m?m=b.sJUIHeader:"F"==m&&(m=b.sJUIFooter);-1!=m.indexOf(".")?(n=m.split("."),i.id=n[0].substr(1,n[0].length-1),i.className=n[1]):"#"==m.charAt(0)?i.id=m.substr(1,m.length-1):i.className=m;k+=o}e.append(i);e=h(i)}else if(">"==j)e=e.parent();else if("l"==
j&&d.bPaginate&&d.bLengthChange)g=mb(a);else if("f"==j&&d.bFilter)g=nb(a);else if("r"==j&&d.bProcessing)g=ob(a);else if("t"==j)g=pb(a);else if("i"==j&&d.bInfo)g=qb(a);else if("p"==j&&d.bPaginate)g=rb(a);else if(0!==p.ext.feature.length){i=p.ext.feature;o=0;for(n=i.length;o<n;o++)if(j==i[o].cFeature){g=i[o].fnInit(a);break}}g&&(i=a.aanFeatures,i[j]||(i[j]=[]),i[j].push(g),e.append(g))}c.replaceWith(e)}function aa(a,b){var c=h(b).children("tr"),d,e,f,g,j,i,n,m,o,k;a.splice(0,a.length);f=0;for(i=c.length;f<
i;f++)a.push([]);f=0;for(i=c.length;f<i;f++){d=c[f];for(e=d.firstChild;e;){if("TD"==e.nodeName.toUpperCase()||"TH"==e.nodeName.toUpperCase()){m=1*e.getAttribute("colspan");o=1*e.getAttribute("rowspan");m=!m||0===m||1===m?1:m;o=!o||0===o||1===o?1:o;g=0;for(j=a[f];j[g];)g++;n=g;k=1===m?!0:!1;for(j=0;j<m;j++)for(g=0;g<o;g++)a[f+g][n+j]={cell:e,unique:k},a[f+g].nTr=d}e=e.nextSibling}}}function ma(a,b,c){var d=[];c||(c=a.aoHeader,b&&(c=[],aa(c,b)));for(var b=0,e=c.length;b<e;b++)for(var f=0,g=c[b].length;f<
g;f++)if(c[b][f].unique&&(!d[f]||!a.bSortCellsTop))d[f]=c[b][f].cell;return d}function na(a,b,c){u(a,"aoServerParams","serverParams",[b]);if(b&&h.isArray(b)){var d={},e=/(.*?)\[\]$/;h.each(b,function(a,b){var c=b.name.match(e);c?(c=c[0],d[c]||(d[c]=[]),d[c].push(b.value)):d[b.name]=b.value});b=d}var f,g=a.ajax,j=a.oInstance;if(h.isPlainObject(g)&&g.data){f=g.data;var i=h.isFunction(f)?f(b):f,b=h.isFunction(f)&&i?i:h.extend(!0,b,i);delete g.data}i={data:b,success:function(b){var d=b.error||b.sError;
d&&a.oApi._fnLog(a,0,d);a.json=b;u(a,null,"xhr",[a,b]);c(b)},dataType:"json",cache:!1,type:a.sServerMethod,error:function(b,c){var d=a.oApi._fnLog;"parsererror"==c?d(a,0,"Invalid JSON response",1):4===b.readyState&&d(a,0,"Ajax error",7);B(a,!1)}};a.oAjaxData=b;u(a,null,"preXhr",[a,b]);a.fnServerData?a.fnServerData.call(j,a.sAjaxSource,h.map(b,function(a,b){return{name:b,value:a}}),c,a):a.sAjaxSource||"string"===typeof g?a.jqXHR=h.ajax(h.extend(i,{url:g||a.sAjaxSource})):h.isFunction(g)?a.jqXHR=g.call(j,
b,c,a):(a.jqXHR=h.ajax(h.extend(i,g)),g.data=f)}function jb(a){return a.bAjaxDataGet?(a.iDraw++,B(a,!0),na(a,sb(a),function(b){tb(a,b)}),!1):!0}function sb(a){var b=a.aoColumns,c=b.length,d=a.oFeatures,e=a.oPreviousSearch,f=a.aoPreSearchCols,g,j=[],i,n,m,o=R(a);g=a._iDisplayStart;i=!1!==d.bPaginate?a._iDisplayLength:-1;var k=function(a,b){j.push({name:a,value:b})};k("sEcho",a.iDraw);k("iColumns",c);k("sColumns",C(b,"sName").join(","));k("iDisplayStart",g);k("iDisplayLength",i);var l={draw:a.iDraw,
columns:[],order:[],start:g,length:i,search:{value:e.sSearch,regex:e.bRegex}};for(g=0;g<c;g++)n=b[g],m=f[g],i="function"==typeof n.mData?"function":n.mData,l.columns.push({data:i,name:n.sName,searchable:n.bSearchable,orderable:n.bSortable,search:{value:m.sSearch,regex:m.bRegex}}),k("mDataProp_"+g,i),d.bFilter&&(k("sSearch_"+g,m.sSearch),k("bRegex_"+g,m.bRegex),k("bSearchable_"+g,n.bSearchable)),d.bSort&&k("bSortable_"+g,n.bSortable);d.bFilter&&(k("sSearch",e.sSearch),k("bRegex",e.bRegex));d.bSort&&
(h.each(o,function(a,b){l.order.push({column:b.col,dir:b.dir});k("iSortCol_"+a,b.col);k("sSortDir_"+a,b.dir)}),k("iSortingCols",o.length));b=p.ext.legacy.ajax;return null===b?a.sAjaxSource?j:l:b?j:l}function tb(a,b){var c=b.sEcho!==l?b.sEcho:b.draw,d=b.iTotalRecords!==l?b.iTotalRecords:b.recordsTotal,e=b.iTotalDisplayRecords!==l?b.iTotalDisplayRecords:b.recordsFiltered;if(c){if(1*c<a.iDraw)return;a.iDraw=1*c}ja(a);a._iRecordsTotal=parseInt(d,10);a._iRecordsDisplay=parseInt(e,10);c=oa(a,b);d=0;for(e=
c.length;d<e;d++)I(a,c[d]);a.aiDisplay=a.aiDisplayMaster.slice();a.bAjaxDataGet=!1;K(a);a._bInitComplete||pa(a,b);a.bAjaxDataGet=!0;B(a,!1)}function oa(a,b){var c=h.isPlainObject(a.ajax)&&a.ajax.dataSrc!==l?a.ajax.dataSrc:a.sAjaxDataProp;return"data"===c?b.aaData||b[c]:""!==c?U(c)(b):b}function nb(a){var b=a.oClasses,c=a.sTableId,d=a.oLanguage,e=a.oPreviousSearch,f=a.aanFeatures,g='<input type="search" class="'+b.sFilterInput+'"/>',j=d.sSearch,j=j.match(/_INPUT_/)?j.replace("_INPUT_",g):j+g,b=h("<div/>",
{id:!f.f?c+"_filter":null,"class":b.sFilter}).append(h("<label/>").append(j)),f=function(){var b=!this.value?"":this.value;b!=e.sSearch&&(ca(a,{sSearch:b,bRegex:e.bRegex,bSmart:e.bSmart,bCaseInsensitive:e.bCaseInsensitive}),a._iDisplayStart=0,K(a))},i=h("input",b).val(e.sSearch).attr("placeholder",d.sSearchPlaceholder).bind("keyup.DT search.DT input.DT paste.DT cut.DT","ssp"===z(a)?Ma(f,400):f).bind("keypress.DT",function(a){if(13==a.keyCode)return!1}).attr("aria-controls",c);h(a.nTable).on("search.dt.DT",
function(b,c){if(a===c)try{i[0]!==O.activeElement&&i.val(e.sSearch)}catch(d){}});return b[0]}function ca(a,b,c){var d=a.oPreviousSearch,e=a.aoPreSearchCols,f=function(a){d.sSearch=a.sSearch;d.bRegex=a.bRegex;d.bSmart=a.bSmart;d.bCaseInsensitive=a.bCaseInsensitive};Da(a);if("ssp"!=z(a)){ub(a,b.sSearch,c,b.bEscapeRegex!==l?!b.bEscapeRegex:b.bRegex,b.bSmart,b.bCaseInsensitive);f(b);for(b=0;b<e.length;b++)vb(a,e[b].sSearch,b,e[b].bEscapeRegex!==l?!e[b].bEscapeRegex:e[b].bRegex,e[b].bSmart,e[b].bCaseInsensitive);
wb(a)}else f(b);a.bFiltered=!0;u(a,null,"search",[a])}function wb(a){for(var b=p.ext.search,c=a.aiDisplay,d,e,f=0,g=b.length;f<g;f++){for(var j=[],i=0,h=c.length;i<h;i++)e=c[i],d=a.aoData[e],b[f](a,d._aFilterData,e,d._aData,i)&&j.push(e);c.length=0;c.push.apply(c,j)}}function vb(a,b,c,d,e,f){if(""!==b)for(var g=a.aiDisplay,d=Na(b,d,e,f),e=g.length-1;0<=e;e--)b=a.aoData[g[e]]._aFilterData[c],d.test(b)||g.splice(e,1)}function ub(a,b,c,d,e,f){var d=Na(b,d,e,f),e=a.oPreviousSearch.sSearch,f=a.aiDisplayMaster,
g;0!==p.ext.search.length&&(c=!0);g=xb(a);if(0>=b.length)a.aiDisplay=f.slice();else{if(g||c||e.length>b.length||0!==b.indexOf(e)||a.bSorted)a.aiDisplay=f.slice();b=a.aiDisplay;for(c=b.length-1;0<=c;c--)d.test(a.aoData[b[c]]._sFilterRow)||b.splice(c,1)}}function Na(a,b,c,d){a=b?a:Oa(a);c&&(a="^(?=.*?"+h.map(a.match(/"[^"]+"|[^ ]+/g)||"",function(a){return'"'===a.charAt(0)?a.match(/^"(.*)"$/)[1]:a}).join(")(?=.*?")+").*$");return RegExp(a,d?"i":"")}function Oa(a){return a.replace(Vb,"\\$1")}function xb(a){var b=
a.aoColumns,c,d,e,f,g,j,i,h,m=p.ext.type.search;c=!1;d=0;for(f=a.aoData.length;d<f;d++)if(h=a.aoData[d],!h._aFilterData){j=[];e=0;for(g=b.length;e<g;e++)c=b[e],c.bSearchable?(i=A(a,d,e,"filter"),m[c.sType]&&(i=m[c.sType](i)),null===i&&(i=""),"string"!==typeof i&&i.toString&&(i=i.toString())):i="",i.indexOf&&-1!==i.indexOf("&")&&(qa.innerHTML=i,i=Wb?qa.textContent:qa.innerText),i.replace&&(i=i.replace(/[\r\n]/g,"")),j.push(i);h._aFilterData=j;h._sFilterRow=j.join("  ");c=!0}return c}function yb(a){return{search:a.sSearch,
smart:a.bSmart,regex:a.bRegex,caseInsensitive:a.bCaseInsensitive}}function zb(a){return{sSearch:a.search,bSmart:a.smart,bRegex:a.regex,bCaseInsensitive:a.caseInsensitive}}function qb(a){var b=a.sTableId,c=a.aanFeatures.i,d=h("<div/>",{"class":a.oClasses.sInfo,id:!c?b+"_info":null});c||(a.aoDrawCallback.push({fn:Ab,sName:"information"}),d.attr("role","status").attr("aria-live","polite"),h(a.nTable).attr("aria-describedby",b+"_info"));return d[0]}function Ab(a){var b=a.aanFeatures.i;if(0!==b.length){var c=
a.oLanguage,d=a._iDisplayStart+1,e=a.fnDisplayEnd(),f=a.fnRecordsTotal(),g=a.fnRecordsDisplay(),j=g?c.sInfo:c.sInfoEmpty;g!==f&&(j+=" "+c.sInfoFiltered);j+=c.sInfoPostFix;j=Bb(a,j);c=c.fnInfoCallback;null!==c&&(j=c.call(a.oInstance,a,d,e,f,g,j));h(b).html(j)}}function Bb(a,b){var c=a.fnFormatNumber,d=a._iDisplayStart+1,e=a._iDisplayLength,f=a.fnRecordsDisplay(),g=-1===e;return b.replace(/_START_/g,c.call(a,d)).replace(/_END_/g,c.call(a,a.fnDisplayEnd())).replace(/_MAX_/g,c.call(a,a.fnRecordsTotal())).replace(/_TOTAL_/g,
c.call(a,f)).replace(/_PAGE_/g,c.call(a,g?1:Math.ceil(d/e))).replace(/_PAGES_/g,c.call(a,g?1:Math.ceil(f/e)))}function ra(a){var b,c,d=a.iInitDisplayStart,e=a.aoColumns,f;c=a.oFeatures;if(a.bInitialised){lb(a);ib(a);ba(a,a.aoHeader);ba(a,a.aoFooter);B(a,!0);c.bAutoWidth&&Ca(a);b=0;for(c=e.length;b<c;b++)f=e[b],f.sWidth&&(f.nTh.style.width=s(f.sWidth));L(a);e=z(a);"ssp"!=e&&("ajax"==e?na(a,[],function(c){var f=oa(a,c);for(b=0;b<f.length;b++)I(a,f[b]);a.iInitDisplayStart=d;L(a);B(a,!1);pa(a,c)},a):
(B(a,!1),pa(a)))}else setTimeout(function(){ra(a)},200)}function pa(a,b){a._bInitComplete=!0;b&&V(a);u(a,"aoInitComplete","init",[a,b])}function Pa(a,b){var c=parseInt(b,10);a._iDisplayLength=c;Qa(a);u(a,null,"length",[a,c])}function mb(a){for(var b=a.oClasses,c=a.sTableId,d=a.aLengthMenu,e=h.isArray(d[0]),f=e?d[0]:d,d=e?d[1]:d,e=h("<select/>",{name:c+"_length","aria-controls":c,"class":b.sLengthSelect}),g=0,j=f.length;g<j;g++)e[0][g]=new Option(d[g],f[g]);var i=h("<div><label/></div>").addClass(b.sLength);
a.aanFeatures.l||(i[0].id=c+"_length");i.children().append(a.oLanguage.sLengthMenu.replace("_MENU_",e[0].outerHTML));h("select",i).val(a._iDisplayLength).bind("change.DT",function(){Pa(a,h(this).val());K(a)});h(a.nTable).bind("length.dt.DT",function(b,c,d){a===c&&h("select",i).val(d)});return i[0]}function rb(a){var b=a.sPaginationType,c=p.ext.pager[b],d="function"===typeof c,e=function(a){K(a)},b=h("<div/>").addClass(a.oClasses.sPaging+b)[0],f=a.aanFeatures;d||c.fnInit(a,b,e);f.p||(b.id=a.sTableId+
"_paginate",a.aoDrawCallback.push({fn:function(a){if(d){var b=a._iDisplayStart,i=a._iDisplayLength,h=a.fnRecordsDisplay(),m=-1===i,b=m?0:Math.ceil(b/i),i=m?1:Math.ceil(h/i),h=c(b,i),o,m=0;for(o=f.p.length;m<o;m++)La(a,"pageButton")(a,f.p[m],m,h,b,i)}else c.fnUpdate(a,e)},sName:"pagination"}));return b}function Ra(a,b,c){var d=a._iDisplayStart,e=a._iDisplayLength,f=a.fnRecordsDisplay();0===f||-1===e?d=0:"number"===typeof b?(d=b*e,d>f&&(d=0)):"first"==b?d=0:"previous"==b?(d=0<=e?d-e:0,0>d&&(d=0)):"next"==
b?d+e<f&&(d+=e):"last"==b?d=Math.floor((f-1)/e)*e:P(a,0,"Unknown paging action: "+b,5);b=a._iDisplayStart!==d;a._iDisplayStart=d;b&&(u(a,null,"page",[a]),c&&K(a));return b}function ob(a){return h("<div/>",{id:!a.aanFeatures.r?a.sTableId+"_processing":null,"class":a.oClasses.sProcessing}).html(a.oLanguage.sProcessing).insertBefore(a.nTable)[0]}function B(a,b){a.oFeatures.bProcessing&&h(a.aanFeatures.r).css("display",b?"block":"none");u(a,null,"processing",[a,b])}function pb(a){var b=h(a.nTable);b.attr("role",
"grid");var c=a.oScroll;if(""===c.sX&&""===c.sY)return a.nTable;var d=c.sX,e=c.sY,f=a.oClasses,g=b.children("caption"),j=g.length?g[0]._captionSide:null,i=h(b[0].cloneNode(!1)),n=h(b[0].cloneNode(!1)),m=b.children("tfoot");c.sX&&"100%"===b.attr("width")&&b.removeAttr("width");m.length||(m=null);c=h("<div/>",{"class":f.sScrollWrapper}).append(h("<div/>",{"class":f.sScrollHead}).css({overflow:"hidden",position:"relative",border:0,width:d?!d?null:s(d):"100%"}).append(h("<div/>",{"class":f.sScrollHeadInner}).css({"box-sizing":"content-box",
width:c.sXInner||"100%"}).append(i.removeAttr("id").css("margin-left",0).append(b.children("thead")))).append("top"===j?g:null)).append(h("<div/>",{"class":f.sScrollBody}).css({overflow:"auto",height:!e?null:s(e),width:!d?null:s(d)}).append(b));m&&c.append(h("<div/>",{"class":f.sScrollFoot}).css({overflow:"hidden",border:0,width:d?!d?null:s(d):"100%"}).append(h("<div/>",{"class":f.sScrollFootInner}).append(n.removeAttr("id").css("margin-left",0).append(b.children("tfoot")))).append("bottom"===j?g:
null));var b=c.children(),o=b[0],f=b[1],k=m?b[2]:null;d&&h(f).scroll(function(){var a=this.scrollLeft;o.scrollLeft=a;m&&(k.scrollLeft=a)});a.nScrollHead=o;a.nScrollBody=f;a.nScrollFoot=k;a.aoDrawCallback.push({fn:W,sName:"scrolling"});return c[0]}function W(a){var b=a.oScroll,c=b.sX,d=b.sXInner,e=b.sY,f=b.iBarWidth,g=h(a.nScrollHead),j=g[0].style,i=g.children("div"),n=i[0].style,m=i.children("table"),i=a.nScrollBody,o=h(i),k=i.style,l=h(a.nScrollFoot).children("div"),p=l.children("table"),r=h(a.nTHead),
q=h(a.nTable),da=q[0],M=da.style,J=a.nTFoot?h(a.nTFoot):null,u=a.oBrowser,v=u.bScrollOversize,y,t,x,w,z,A=[],B=[],C=[],D,E=function(a){a=a.style;a.paddingTop="0";a.paddingBottom="0";a.borderTopWidth="0";a.borderBottomWidth="0";a.height=0};q.children("thead, tfoot").remove();z=r.clone().prependTo(q);y=r.find("tr");x=z.find("tr");z.find("th, td").removeAttr("tabindex");J&&(w=J.clone().prependTo(q),t=J.find("tr"),w=w.find("tr"));c||(k.width="100%",g[0].style.width="100%");h.each(ma(a,z),function(b,c){D=
ga(a,b);c.style.width=a.aoColumns[D].sWidth});J&&F(function(a){a.style.width=""},w);b.bCollapse&&""!==e&&(k.height=o[0].offsetHeight+r[0].offsetHeight+"px");g=q.outerWidth();if(""===c){if(M.width="100%",v&&(q.find("tbody").height()>i.offsetHeight||"scroll"==o.css("overflow-y")))M.width=s(q.outerWidth()-f)}else""!==d?M.width=s(d):g==o.width()&&o.height()<q.height()?(M.width=s(g-f),q.outerWidth()>g-f&&(M.width=s(g))):M.width=s(g);g=q.outerWidth();F(E,x);F(function(a){C.push(a.innerHTML);A.push(s(h(a).css("width")))},
x);F(function(a,b){a.style.width=A[b]},y);h(x).height(0);J&&(F(E,w),F(function(a){B.push(s(h(a).css("width")))},w),F(function(a,b){a.style.width=B[b]},t),h(w).height(0));F(function(a,b){a.innerHTML='<div class="dataTables_sizing" style="height:0;overflow:hidden;">'+C[b]+"</div>";a.style.width=A[b]},x);J&&F(function(a,b){a.innerHTML="";a.style.width=B[b]},w);if(q.outerWidth()<g){t=i.scrollHeight>i.offsetHeight||"scroll"==o.css("overflow-y")?g+f:g;if(v&&(i.scrollHeight>i.offsetHeight||"scroll"==o.css("overflow-y")))M.width=
s(t-f);(""===c||""!==d)&&P(a,1,"Possible column misalignment",6)}else t="100%";k.width=s(t);j.width=s(t);J&&(a.nScrollFoot.style.width=s(t));!e&&v&&(k.height=s(da.offsetHeight+f));e&&b.bCollapse&&(k.height=s(e),b=c&&da.offsetWidth>i.offsetWidth?f:0,da.offsetHeight<i.offsetHeight&&(k.height=s(da.offsetHeight+b)));b=q.outerWidth();m[0].style.width=s(b);n.width=s(b);m=q.height()>i.clientHeight||"scroll"==o.css("overflow-y");u="padding"+(u.bScrollbarLeft?"Left":"Right");n[u]=m?f+"px":"0px";J&&(p[0].style.width=
s(b),l[0].style.width=s(b),l[0].style[u]=m?f+"px":"0px");o.scroll();if((a.bSorted||a.bFiltered)&&!a._drawHold)i.scrollTop=0}function F(a,b,c){for(var d=0,e=0,f=b.length,g,j;e<f;){g=b[e].firstChild;for(j=c?c[e].firstChild:null;g;)1===g.nodeType&&(c?a(g,j,d):a(g,d),d++),g=g.nextSibling,j=c?j.nextSibling:null;e++}}function Ca(a){var b=a.nTable,c=a.aoColumns,d=a.oScroll,e=d.sY,f=d.sX,g=d.sXInner,j=c.length,d=X(a,"bVisible"),i=h("th",a.nTHead),n=b.getAttribute("width"),m=b.parentNode,o=!1,k,l;for(k=0;k<
d.length;k++)l=c[d[k]],null!==l.sWidth&&(l.sWidth=Cb(l.sWidthOrig,m),o=!0);if(!o&&!f&&!e&&j==Z(a)&&j==i.length)for(k=0;k<j;k++)c[k].sWidth=s(i.eq(k).width());else{j=h(b).clone().empty().css("visibility","hidden").removeAttr("id").append(h(a.nTHead).clone(!1)).append(h(a.nTFoot).clone(!1)).append(h("<tbody><tr/></tbody>"));j.find("tfoot th, tfoot td").css("width","");var p=j.find("tbody tr"),i=ma(a,j.find("thead")[0]);for(k=0;k<d.length;k++)l=c[d[k]],i[k].style.width=null!==l.sWidthOrig&&""!==l.sWidthOrig?
s(l.sWidthOrig):"";if(a.aoData.length)for(k=0;k<d.length;k++)o=d[k],l=c[o],h(Db(a,o)).clone(!1).append(l.sContentPadding).appendTo(p);j.appendTo(m);f&&g?j.width(g):f?(j.css("width","auto"),j.width()<m.offsetWidth&&j.width(m.offsetWidth)):e?j.width(m.offsetWidth):n&&j.width(n);Eb(a,j[0]);if(f){for(k=g=0;k<d.length;k++)l=c[d[k]],e=h(i[k]).outerWidth(),g+=null===l.sWidthOrig?e:parseInt(l.sWidth,10)+e-h(i[k]).width();j.width(s(g));b.style.width=s(g)}for(k=0;k<d.length;k++)if(l=c[d[k]],e=h(i[k]).width())l.sWidth=
s(e);b.style.width=s(j.css("width"));j.remove()}n&&(b.style.width=s(n));if((n||f)&&!a._reszEvt)h(za).bind("resize.DT-"+a.sInstance,Ma(function(){V(a)})),a._reszEvt=!0}function Ma(a,b){var c=b||200,d,e;return function(){var b=this,g=+new Date,j=arguments;d&&g<d+c?(clearTimeout(e),e=setTimeout(function(){d=l;a.apply(b,j)},c)):d?(d=g,a.apply(b,j)):d=g}}function Cb(a,b){if(!a)return 0;var c=h("<div/>").css("width",s(a)).appendTo(b||O.body),d=c[0].offsetWidth;c.remove();return d}function Eb(a,b){var c=
a.oScroll;if(c.sX||c.sY)c=!c.sX?c.iBarWidth:0,b.style.width=s(h(b).outerWidth()-c)}function Db(a,b){var c=Fb(a,b);if(0>c)return null;var d=a.aoData[c];return!d.nTr?h("<td/>").html(A(a,c,b,"display"))[0]:d.anCells[b]}function Fb(a,b){for(var c,d=-1,e=-1,f=0,g=a.aoData.length;f<g;f++)c=A(a,f,b,"display")+"",c=c.replace(Xb,""),c.length>d&&(d=c.length,e=f);return e}function s(a){return null===a?"0px":"number"==typeof a?0>a?"0px":a+"px":a.match(/\d$/)?a+"px":a}function Gb(){if(!p.__scrollbarWidth){var a=
h("<p/>").css({width:"100%",height:200,padding:0})[0],b=h("<div/>").css({position:"absolute",top:0,left:0,width:200,height:150,padding:0,overflow:"hidden",visibility:"hidden"}).append(a).appendTo("body"),c=a.offsetWidth;b.css("overflow","scroll");a=a.offsetWidth;c===a&&(a=b[0].clientWidth);b.remove();p.__scrollbarWidth=c-a}return p.__scrollbarWidth}function R(a){var b,c,d=[],e=a.aoColumns,f,g,j,i;b=a.aaSortingFixed;c=h.isPlainObject(b);var n=[];f=function(a){a.length&&!h.isArray(a[0])?n.push(a):n.push.apply(n,
a)};h.isArray(b)&&f(b);c&&b.pre&&f(b.pre);f(a.aaSorting);c&&b.post&&f(b.post);for(a=0;a<n.length;a++){i=n[a][0];f=e[i].aDataSort;b=0;for(c=f.length;b<c;b++)g=f[b],j=e[g].sType||"string",d.push({src:i,col:g,dir:n[a][1],index:n[a][2],type:j,formatter:p.ext.type.order[j+"-pre"]})}return d}function kb(a){var b,c,d=[],e=p.ext.type.order,f=a.aoData,g=0,j,i=a.aiDisplayMaster,h;Da(a);h=R(a);b=0;for(c=h.length;b<c;b++)j=h[b],j.formatter&&g++,Hb(a,j.col);if("ssp"!=z(a)&&0!==h.length){b=0;for(c=i.length;b<c;b++)d[i[b]]=
b;g===h.length?i.sort(function(a,b){var c,e,g,j,i=h.length,l=f[a]._aSortData,p=f[b]._aSortData;for(g=0;g<i;g++)if(j=h[g],c=l[j.col],e=p[j.col],c=c<e?-1:c>e?1:0,0!==c)return"asc"===j.dir?c:-c;c=d[a];e=d[b];return c<e?-1:c>e?1:0}):i.sort(function(a,b){var c,g,j,i,l=h.length,p=f[a]._aSortData,r=f[b]._aSortData;for(j=0;j<l;j++)if(i=h[j],c=p[i.col],g=r[i.col],i=e[i.type+"-"+i.dir]||e["string-"+i.dir],c=i(c,g),0!==c)return c;c=d[a];g=d[b];return c<g?-1:c>g?1:0})}a.bSorted=!0}function Ib(a){for(var b,c,
d=a.aoColumns,e=R(a),a=a.oLanguage.oAria,f=0,g=d.length;f<g;f++){c=d[f];var j=c.asSorting;b=c.sTitle.replace(/<.*?>/g,"");var i=c.nTh;i.removeAttribute("aria-sort");c.bSortable&&(0<e.length&&e[0].col==f?(i.setAttribute("aria-sort","asc"==e[0].dir?"ascending":"descending"),c=j[e[0].index+1]||j[0]):c=j[0],b+="asc"===c?a.sSortAscending:a.sSortDescending);i.setAttribute("aria-label",b)}}function Sa(a,b,c,d){var e=a.aaSorting,f=a.aoColumns[b].asSorting,g=function(a){var b=a._idx;b===l&&(b=h.inArray(a[1],
f));return b+1>=f.length?0:b+1};"number"===typeof e[0]&&(e=a.aaSorting=[e]);c&&a.oFeatures.bSortMulti?(c=h.inArray(b,C(e,"0")),-1!==c?(b=g(e[c]),e[c][1]=f[b],e[c]._idx=b):(e.push([b,f[0],0]),e[e.length-1]._idx=0)):e.length&&e[0][0]==b?(b=g(e[0]),e.length=1,e[0][1]=f[b],e[0]._idx=b):(e.length=0,e.push([b,f[0]]),e[0]._idx=0);L(a);"function"==typeof d&&d(a)}function Ka(a,b,c,d){var e=a.aoColumns[c];Ta(b,{},function(b){!1!==e.bSortable&&(a.oFeatures.bProcessing?(B(a,!0),setTimeout(function(){Sa(a,c,b.shiftKey,
d);"ssp"!==z(a)&&B(a,!1)},0)):Sa(a,c,b.shiftKey,d))})}function sa(a){var b=a.aLastSort,c=a.oClasses.sSortColumn,d=R(a),e=a.oFeatures,f,g;if(e.bSort&&e.bSortClasses){e=0;for(f=b.length;e<f;e++)g=b[e].src,h(C(a.aoData,"anCells",g)).removeClass(c+(2>e?e+1:3));e=0;for(f=d.length;e<f;e++)g=d[e].src,h(C(a.aoData,"anCells",g)).addClass(c+(2>e?e+1:3))}a.aLastSort=d}function Hb(a,b){var c=a.aoColumns[b],d=p.ext.order[c.sSortDataType],e;d&&(e=d.call(a.oInstance,a,b,Y(a,b)));for(var f,g=p.ext.type.order[c.sType+
"-pre"],j=0,i=a.aoData.length;j<i;j++)if(c=a.aoData[j],c._aSortData||(c._aSortData=[]),!c._aSortData[b]||d)f=d?e[j]:A(a,j,b,"sort"),c._aSortData[b]=g?g(f):f}function ta(a){if(a.oFeatures.bStateSave&&!a.bDestroying){var b={time:+new Date,start:a._iDisplayStart,length:a._iDisplayLength,order:h.extend(!0,[],a.aaSorting),search:yb(a.oPreviousSearch),columns:h.map(a.aoColumns,function(b,d){return{visible:b.bVisible,search:yb(a.aoPreSearchCols[d])}})};u(a,"aoStateSaveParams","stateSaveParams",[a,b]);a.oSavedState=
b;a.fnStateSaveCallback.call(a.oInstance,a,b)}}function Jb(a){var b,c,d=a.aoColumns;if(a.oFeatures.bStateSave){var e=a.fnStateLoadCallback.call(a.oInstance,a);if(e&&e.time&&(b=u(a,"aoStateLoadParams","stateLoadParams",[a,e]),-1===h.inArray(!1,b)&&(b=a.iStateDuration,!(0<b&&e.time<+new Date-1E3*b)&&d.length===e.columns.length))){a.oLoadedState=h.extend(!0,{},e);a._iDisplayStart=e.start;a.iInitDisplayStart=e.start;a._iDisplayLength=e.length;a.aaSorting=[];h.each(e.order,function(b,c){a.aaSorting.push(c[0]>=
d.length?[0,c[1]]:c)});h.extend(a.oPreviousSearch,zb(e.search));b=0;for(c=e.columns.length;b<c;b++){var f=e.columns[b];d[b].bVisible=f.visible;h.extend(a.aoPreSearchCols[b],zb(f.search))}u(a,"aoStateLoaded","stateLoaded",[a,e])}}}function ua(a){var b=p.settings,a=h.inArray(a,C(b,"nTable"));return-1!==a?b[a]:null}function P(a,b,c,d){c="DataTables warning: "+(null!==a?"table id="+a.sTableId+" - ":"")+c;d&&(c+=". For more information about this error, please see http://datatables.net/tn/"+d);if(b)za.console&&
console.log&&console.log(c);else if(a=p.ext,"alert"==(a.sErrMode||a.errMode))alert(c);else throw Error(c);}function D(a,b,c,d){h.isArray(c)?h.each(c,function(c,d){h.isArray(d)?D(a,b,d[0],d[1]):D(a,b,d)}):(d===l&&(d=c),b[c]!==l&&(a[d]=b[c]))}function Kb(a,b,c){var d,e;for(e in b)b.hasOwnProperty(e)&&(d=b[e],h.isPlainObject(d)?(h.isPlainObject(a[e])||(a[e]={}),h.extend(!0,a[e],d)):a[e]=c&&"data"!==e&&"aaData"!==e&&h.isArray(d)?d.slice():d);return a}function Ta(a,b,c){h(a).bind("click.DT",b,function(b){a.blur();
c(b)}).bind("keypress.DT",b,function(a){13===a.which&&(a.preventDefault(),c(a))}).bind("selectstart.DT",function(){return!1})}function x(a,b,c,d){c&&a[b].push({fn:c,sName:d})}function u(a,b,c,d){var e=[];b&&(e=h.map(a[b].slice().reverse(),function(b){return b.fn.apply(a.oInstance,d)}));null!==c&&h(a.nTable).trigger(c+".dt",d);return e}function Qa(a){var b=a._iDisplayStart,c=a.fnDisplayEnd(),d=a._iDisplayLength;c===a.fnRecordsDisplay()&&(b=c-d);if(-1===d||0>b)b=0;a._iDisplayStart=b}function La(a,b){var c=
a.renderer,d=p.ext.renderer[b];return h.isPlainObject(c)&&c[b]?d[c[b]]||d._:"string"===typeof c?d[c]||d._:d._}function z(a){return a.oFeatures.bServerSide?"ssp":a.ajax||a.sAjaxSource?"ajax":"dom"}function Ua(a,b){var c=[],c=Lb.numbers_length,d=Math.floor(c/2);b<=c?c=S(0,b):a<=d?(c=S(0,c-2),c.push("ellipsis"),c.push(b-1)):(a>=b-1-d?c=S(b-(c-2),b):(c=S(a-1,a+2),c.push("ellipsis"),c.push(b-1)),c.splice(0,0,"ellipsis"),c.splice(0,0,0));c.DT_el="span";return c}function cb(a){h.each({num:function(b){return va(b,
a)},"num-fmt":function(b){return va(b,a,Va)},"html-num":function(b){return va(b,a,wa)},"html-num-fmt":function(b){return va(b,a,wa,Va)}},function(b,c){t.type.order[b+a+"-pre"]=c})}function Mb(a){return function(){var b=[ua(this[p.ext.iApiIndex])].concat(Array.prototype.slice.call(arguments));return p.ext.internal[a].apply(this,b)}}var p,t,q,r,v,Wa={},Nb=/[\r\n]/g,wa=/<.*?>/g,Yb=/^[\w\+\-]/,Zb=/[\w\+\-]$/,Vb=RegExp("(\\/|\\.|\\*|\\+|\\?|\\||\\(|\\)|\\[|\\]|\\{|\\}|\\\\|\\$|\\^|\\-)","g"),Va=/[',$\u00a3\u20ac\u00a5%\u2009\u202F]/g,
H=function(a){return!a||!0===a||"-"===a?!0:!1},Ob=function(a){var b=parseInt(a,10);return!isNaN(b)&&isFinite(a)?b:null},Pb=function(a,b){Wa[b]||(Wa[b]=RegExp(Oa(b),"g"));return"string"===typeof a?a.replace(/\./g,"").replace(Wa[b],"."):a},Xa=function(a,b,c){var d="string"===typeof a;b&&d&&(a=Pb(a,b));c&&d&&(a=a.replace(Va,""));return H(a)||!isNaN(parseFloat(a))&&isFinite(a)},Qb=function(a,b,c){return H(a)?!0:!(H(a)||"string"===typeof a)?null:Xa(a.replace(wa,""),b,c)?!0:null},C=function(a,b,c){var d=
[],e=0,f=a.length;if(c!==l)for(;e<f;e++)a[e]&&a[e][b]&&d.push(a[e][b][c]);else for(;e<f;e++)a[e]&&d.push(a[e][b]);return d},xa=function(a,b,c,d){var e=[],f=0,g=b.length;if(d!==l)for(;f<g;f++)e.push(a[b[f]][c][d]);else for(;f<g;f++)e.push(a[b[f]][c]);return e},S=function(a,b){var c=[],d;b===l?(b=0,d=a):(d=b,b=a);for(var e=b;e<d;e++)c.push(e);return c},Ja=function(a){var b=[],c,d,e=a.length,f,g=0;d=0;a:for(;d<e;d++){c=a[d];for(f=0;f<g;f++)if(b[f]===c)continue a;b.push(c);g++}return b},w=function(a,
b,c){a[b]!==l&&(a[c]=a[b])},$=/\[.*?\]$/,Q=/\(\)$/,qa=h("<div>")[0],Wb=qa.textContent!==l,Xb=/<.*?>/g;p=function(a){this.$=function(a,b){return this.api(!0).$(a,b)};this._=function(a,b){return this.api(!0).rows(a,b).data()};this.api=function(a){return a?new q(ua(this[t.iApiIndex])):new q(this)};this.fnAddData=function(a,b){var c=this.api(!0),d=h.isArray(a)&&(h.isArray(a[0])||h.isPlainObject(a[0]))?c.rows.add(a):c.row.add(a);(b===l||b)&&c.draw();return d.flatten().toArray()};this.fnAdjustColumnSizing=
function(a){var b=this.api(!0).columns.adjust(),c=b.settings()[0],d=c.oScroll;a===l||a?b.draw(!1):(""!==d.sX||""!==d.sY)&&W(c)};this.fnClearTable=function(a){var b=this.api(!0).clear();(a===l||a)&&b.draw()};this.fnClose=function(a){this.api(!0).row(a).child.hide()};this.fnDeleteRow=function(a,b,c){var d=this.api(!0),a=d.rows(a),e=a.settings()[0],h=e.aoData[a[0][0]];a.remove();b&&b.call(this,e,h);(c===l||c)&&d.draw();return h};this.fnDestroy=function(a){this.api(!0).destroy(a)};this.fnDraw=function(a){this.api(!0).draw(!a)};
this.fnFilter=function(a,b,c,d,e,h){e=this.api(!0);null===b||b===l?e.search(a,c,d,h):e.column(b).search(a,c,d,h);e.draw()};this.fnGetData=function(a,b){var c=this.api(!0);if(a!==l){var d=a.nodeName?a.nodeName.toLowerCase():"";return b!==l||"td"==d||"th"==d?c.cell(a,b).data():c.row(a).data()||null}return c.data().toArray()};this.fnGetNodes=function(a){var b=this.api(!0);return a!==l?b.row(a).node():b.rows().nodes().flatten().toArray()};this.fnGetPosition=function(a){var b=this.api(!0),c=a.nodeName.toUpperCase();
return"TR"==c?b.row(a).index():"TD"==c||"TH"==c?(a=b.cell(a).index(),[a.row,a.columnVisible,a.column]):null};this.fnIsOpen=function(a){return this.api(!0).row(a).child.isShown()};this.fnOpen=function(a,b,c){return this.api(!0).row(a).child(b,c).show().child()[0]};this.fnPageChange=function(a,b){var c=this.api(!0).page(a);(b===l||b)&&c.draw(!1)};this.fnSetColumnVis=function(a,b,c){a=this.api(!0).column(a).visible(b);(c===l||c)&&a.columns.adjust().draw()};this.fnSettings=function(){return ua(this[t.iApiIndex])};
this.fnSort=function(a){this.api(!0).order(a).draw()};this.fnSortListener=function(a,b,c){this.api(!0).order.listener(a,b,c)};this.fnUpdate=function(a,b,c,d,e){var h=this.api(!0);c===l||null===c?h.row(b).data(a):h.cell(b,c).data(a);(e===l||e)&&h.columns.adjust();(d===l||d)&&h.draw();return 0};this.fnVersionCheck=t.fnVersionCheck;var b=this,c=a===l,d=this.length;c&&(a={});this.oApi=this.internal=t.internal;for(var e in p.ext.internal)e&&(this[e]=Mb(e));this.each(function(){var e={},g=1<d?Kb(e,a,!0):
a,j=0,i,n=this.getAttribute("id"),e=!1,m=p.defaults;if("table"!=this.nodeName.toLowerCase())P(null,0,"Non-table node initialisation ("+this.nodeName+")",2);else{db(m);eb(m.column);G(m,m,!0);G(m.column,m.column,!0);G(m,g);var o=p.settings,j=0;for(i=o.length;j<i;j++){if(o[j].nTable==this){i=g.bRetrieve!==l?g.bRetrieve:m.bRetrieve;if(c||i)return o[j].oInstance;if(g.bDestroy!==l?g.bDestroy:m.bDestroy){o[j].oInstance.fnDestroy();break}else{P(o[j],0,"Cannot reinitialise DataTable",3);return}}if(o[j].sTableId==
this.id){o.splice(j,1);break}}if(null===n||""===n)this.id=n="DataTables_Table_"+p.ext._unique++;var k=h.extend(!0,{},p.models.oSettings,{nTable:this,oApi:b.internal,oInit:g,sDestroyWidth:h(this)[0].style.width,sInstance:n,sTableId:n});o.push(k);k.oInstance=1===b.length?b:h(this).dataTable();db(g);g.oLanguage&&N(g.oLanguage);g.aLengthMenu&&!g.iDisplayLength&&(g.iDisplayLength=h.isArray(g.aLengthMenu[0])?g.aLengthMenu[0][0]:g.aLengthMenu[0]);g=Kb(h.extend(!0,{},m),g);D(k.oFeatures,g,"bPaginate bLengthChange bFilter bSort bSortMulti bInfo bProcessing bAutoWidth bSortClasses bServerSide bDeferRender".split(" "));
D(k,g,["asStripeClasses","ajax","fnServerData","fnFormatNumber","sServerMethod","aaSorting","aaSortingFixed","aLengthMenu","sPaginationType","sAjaxSource","sAjaxDataProp","iStateDuration","sDom","bSortCellsTop","iTabIndex","fnStateLoadCallback","fnStateSaveCallback","renderer",["iCookieDuration","iStateDuration"],["oSearch","oPreviousSearch"],["aoSearchCols","aoPreSearchCols"],["iDisplayLength","_iDisplayLength"],["bJQueryUI","bJUI"]]);D(k.oScroll,g,[["sScrollX","sX"],["sScrollXInner","sXInner"],
["sScrollY","sY"],["bScrollCollapse","bCollapse"]]);D(k.oLanguage,g,"fnInfoCallback");x(k,"aoDrawCallback",g.fnDrawCallback,"user");x(k,"aoServerParams",g.fnServerParams,"user");x(k,"aoStateSaveParams",g.fnStateSaveParams,"user");x(k,"aoStateLoadParams",g.fnStateLoadParams,"user");x(k,"aoStateLoaded",g.fnStateLoaded,"user");x(k,"aoRowCallback",g.fnRowCallback,"user");x(k,"aoRowCreatedCallback",g.fnCreatedRow,"user");x(k,"aoHeaderCallback",g.fnHeaderCallback,"user");x(k,"aoFooterCallback",g.fnFooterCallback,
"user");x(k,"aoInitComplete",g.fnInitComplete,"user");x(k,"aoPreDrawCallback",g.fnPreDrawCallback,"user");n=k.oClasses;g.bJQueryUI?(h.extend(n,p.ext.oJUIClasses,g.oClasses),g.sDom===m.sDom&&"lfrtip"===m.sDom&&(k.sDom='<"H"lfr>t<"F"ip>'),k.renderer)?h.isPlainObject(k.renderer)&&!k.renderer.header&&(k.renderer.header="jqueryui"):k.renderer="jqueryui":h.extend(n,p.ext.classes,g.oClasses);h(this).addClass(n.sTable);if(""!==k.oScroll.sX||""!==k.oScroll.sY)k.oScroll.iBarWidth=Gb();!0===k.oScroll.sX&&(k.oScroll.sX=
"100%");k.iInitDisplayStart===l&&(k.iInitDisplayStart=g.iDisplayStart,k._iDisplayStart=g.iDisplayStart);null!==g.iDeferLoading&&(k.bDeferLoading=!0,j=h.isArray(g.iDeferLoading),k._iRecordsDisplay=j?g.iDeferLoading[0]:g.iDeferLoading,k._iRecordsTotal=j?g.iDeferLoading[1]:g.iDeferLoading);""!==g.oLanguage.sUrl?(k.oLanguage.sUrl=g.oLanguage.sUrl,h.getJSON(k.oLanguage.sUrl,null,function(a){N(a);G(m.oLanguage,a);h.extend(true,k.oLanguage,g.oLanguage,a);ra(k)}),e=!0):h.extend(!0,k.oLanguage,g.oLanguage);
null===g.asStripeClasses&&(k.asStripeClasses=[n.sStripeOdd,n.sStripeEven]);var j=k.asStripeClasses,r=h("tbody tr:eq(0)",this);-1!==h.inArray(!0,h.map(j,function(a){return r.hasClass(a)}))&&(h("tbody tr",this).removeClass(j.join(" ")),k.asDestroyStripes=j.slice());var o=[],q,j=this.getElementsByTagName("thead");0!==j.length&&(aa(k.aoHeader,j[0]),o=ma(k));if(null===g.aoColumns){q=[];j=0;for(i=o.length;j<i;j++)q.push(null)}else q=g.aoColumns;j=0;for(i=q.length;j<i;j++)Aa(k,o?o[j]:null);hb(k,g.aoColumnDefs,
q,function(a,b){fa(k,a,b)});if(r.length){var s=function(a,b){return a.getAttribute("data-"+b)?b:null};h.each(ia(k,r[0]).cells,function(a,b){var c=k.aoColumns[a];if(c.mData===a){var d=s(b,"sort")||s(b,"order"),e=s(b,"filter")||s(b,"search");if(d!==null||e!==null){c.mData={_:a+".display",sort:d!==null?a+".@data-"+d:l,type:d!==null?a+".@data-"+d:l,filter:e!==null?a+".@data-"+e:l};fa(k,a)}}})}var t=k.oFeatures;g.bStateSave&&(t.bStateSave=!0,Jb(k,g),x(k,"aoDrawCallback",ta,"state_save"));if(g.aaSorting===
l){o=k.aaSorting;j=0;for(i=o.length;j<i;j++)o[j][1]=k.aoColumns[j].asSorting[0]}sa(k);t.bSort&&x(k,"aoDrawCallback",function(){if(k.bSorted){var a=R(k),b={};h.each(a,function(a,c){b[c.src]=c.dir});u(k,null,"order",[k,a,b]);Ib(k)}});x(k,"aoDrawCallback",function(){(k.bSorted||z(k)==="ssp"||t.bDeferRender)&&sa(k)},"sc");fb(k);j=h(this).children("caption").each(function(){this._captionSide=h(this).css("caption-side")});i=h(this).children("thead");0===i.length&&(i=h("<thead/>").appendTo(this));k.nTHead=
i[0];i=h(this).children("tbody");0===i.length&&(i=h("<tbody/>").appendTo(this));k.nTBody=i[0];i=h(this).children("tfoot");if(0===i.length&&0<j.length&&(""!==k.oScroll.sX||""!==k.oScroll.sY))i=h("<tfoot/>").appendTo(this);0===i.length||0===i.children().length?h(this).addClass(n.sNoFooter):0<i.length&&(k.nTFoot=i[0],aa(k.aoFooter,k.nTFoot));if(g.aaData)for(j=0;j<g.aaData.length;j++)I(k,g.aaData[j]);else(k.bDeferLoading||"dom"==z(k))&&ha(k,h(k.nTBody).children("tr"));k.aiDisplay=k.aiDisplayMaster.slice();
k.bInitialised=!0;!1===e&&ra(k)}});b=null;return this};var Rb=[],y=Array.prototype,$b=function(a){var b,c,d=p.settings,e=h.map(d,function(a){return a.nTable});if(a){if(a.nTable&&a.oApi)return[a];if(a.nodeName&&"table"===a.nodeName.toLowerCase())return b=h.inArray(a,e),-1!==b?[d[b]]:null;if(a&&"function"===typeof a.settings)return a.settings().toArray();"string"===typeof a?c=h(a):a instanceof h&&(c=a)}else return[];if(c)return c.map(function(){b=h.inArray(this,e);return-1!==b?d[b]:null}).toArray()};
q=function(a,b){if(!this instanceof q)throw"DT API must be constructed as a new object";var c=[],d=function(a){(a=$b(a))&&c.push.apply(c,a)};if(h.isArray(a))for(var e=0,f=a.length;e<f;e++)d(a[e]);else d(a);this.context=Ja(c);b&&this.push.apply(this,b.toArray?b.toArray():b);this.selector={rows:null,cols:null,opts:null};q.extend(this,this,Rb)};p.Api=q;q.prototype={concat:y.concat,context:[],each:function(a){for(var b=0,c=this.length;b<c;b++)a.call(this,this[b],b,this);return this},eq:function(a){var b=
this.context;return b.length>a?new q(b[a],this[a]):null},filter:function(a){var b=[];if(y.filter)b=y.filter.call(this,a,this);else for(var c=0,d=this.length;c<d;c++)a.call(this,this[c],c,this)&&b.push(this[c]);return new q(this.context,b)},flatten:function(){var a=[];return new q(this.context,a.concat.apply(a,this.toArray()))},join:y.join,indexOf:y.indexOf||function(a,b){for(var c=b||0,d=this.length;c<d;c++)if(this[c]===a)return c;return-1},iterator:function(a,b,c){var d=[],e,f,g,h,i,n=this.context,
m,o,k=this.selector;"string"===typeof a&&(c=b,b=a,a=!1);f=0;for(g=n.length;f<g;f++)if("table"===b)e=c(n[f],f),e!==l&&d.push(e);else if("columns"===b||"rows"===b)e=c(n[f],this[f],f),e!==l&&d.push(e);else if("column"===b||"column-rows"===b||"row"===b||"cell"===b){o=this[f];"column-rows"===b&&(m=Ya(n[f],k.opts));h=0;for(i=o.length;h<i;h++)e=o[h],e="cell"===b?c(n[f],e.row,e.column,f,h):c(n[f],e,f,h,m),e!==l&&d.push(e)}return d.length?(a=new q(n,a?d.concat.apply([],d):d),b=a.selector,b.rows=k.rows,b.cols=
k.cols,b.opts=k.opts,a):this},lastIndexOf:y.lastIndexOf||function(a,b){return this.indexOf.apply(this.toArray.reverse(),arguments)},length:0,map:function(a){var b=[];if(y.map)b=y.map.call(this,a,this);else for(var c=0,d=this.length;c<d;c++)b.push(a.call(this,this[c],c));return new q(this.context,b)},pluck:function(a){return this.map(function(b){return b[a]})},pop:y.pop,push:y.push,reduce:y.reduce||function(a,b){return gb(this,a,b,0,this.length,1)},reduceRight:y.reduceRight||function(a,b){return gb(this,
a,b,this.length-1,-1,-1)},reverse:y.reverse,selector:null,shift:y.shift,sort:y.sort,splice:y.splice,toArray:function(){return y.slice.call(this)},to$:function(){return h(this)},toJQuery:function(){return h(this)},unique:function(){return new q(this.context,Ja(this))},unshift:y.unshift};q.extend=function(a,b,c){if(b&&(b instanceof q||b.__dt_wrapper)){var d,e,f,g=function(a,b,c){return function(){var d=b.apply(a,arguments);q.extend(d,d,c.methodExt);return d}};d=0;for(e=c.length;d<e;d++)f=c[d],b[f.name]=
"function"===typeof f.val?g(a,f.val,f):h.isPlainObject(f.val)?{}:f.val,b[f.name].__dt_wrapper=!0,q.extend(a,b[f.name],f.propExt)}};q.register=r=function(a,b){if(h.isArray(a))for(var c=0,d=a.length;c<d;c++)q.register(a[c],b);else for(var e=a.split("."),f=Rb,g,j,c=0,d=e.length;c<d;c++){g=(j=-1!==e[c].indexOf("()"))?e[c].replace("()",""):e[c];var i;a:{i=0;for(var n=f.length;i<n;i++)if(f[i].name===g){i=f[i];break a}i=null}i||(i={name:g,val:{},methodExt:[],propExt:[]},f.push(i));c===d-1?i.val=b:f=j?i.methodExt:
i.propExt}};q.registerPlural=v=function(a,b,c){q.register(a,c);q.register(b,function(){var a=c.apply(this,arguments);return a===this?this:a instanceof q?a.length?h.isArray(a[0])?new q(a.context,a[0]):a[0]:l:a})};r("tables()",function(a){var b;if(a){b=q;var c=this.context;if("number"===typeof a)a=[c[a]];else var d=h.map(c,function(a){return a.nTable}),a=h(d).filter(a).map(function(){var a=h.inArray(this,d);return c[a]}).toArray();b=new b(a)}else b=this;return b});r("table()",function(a){var a=this.tables(a),
b=a.context;return b.length?new q(b[0]):a});v("tables().nodes()","table().node()",function(){return this.iterator("table",function(a){return a.nTable})});v("tables().body()","table().body()",function(){return this.iterator("table",function(a){return a.nTBody})});v("tables().header()","table().header()",function(){return this.iterator("table",function(a){return a.nTHead})});v("tables().footer()","table().footer()",function(){return this.iterator("table",function(a){return a.nTFoot})});v("tables().containers()",
"table().container()",function(){return this.iterator("table",function(a){return a.nTableWrapper})});r("draw()",function(a){return this.iterator("table",function(b){L(b,!1===a)})});r("page()",function(a){return a===l?this.page.info().page:this.iterator("table",function(b){Ra(b,a)})});r("page.info()",function(){if(0===this.context.length)return l;var a=this.context[0],b=a._iDisplayStart,c=a._iDisplayLength,d=a.fnRecordsDisplay(),e=-1===c;return{page:e?0:Math.floor(b/c),pages:e?1:Math.ceil(d/c),start:b,
end:a.fnDisplayEnd(),length:c,recordsTotal:a.fnRecordsTotal(),recordsDisplay:d}});r("page.len()",function(a){return a===l?0!==this.context.length?this.context[0]._iDisplayLength:l:this.iterator("table",function(b){Pa(b,a)})});var Sb=function(a,b,c){"ssp"==z(a)?L(a,b):(B(a,!0),na(a,[],function(c){ja(a);for(var c=oa(a,c),d=0,g=c.length;d<g;d++)I(a,c[d]);L(a,b);B(a,!1)}));if(c){var d=new q(a);d.one("draw",function(){c(d.ajax.json())})}};r("ajax.json()",function(){var a=this.context;if(0<a.length)return a[0].json});
r("ajax.params()",function(){var a=this.context;if(0<a.length)return a[0].oAjaxData});r("ajax.reload()",function(a,b){return this.iterator("table",function(c){Sb(c,!1===b,a)})});r("ajax.url()",function(a){var b=this.context;if(a===l){if(0===b.length)return l;b=b[0];return b.ajax?h.isPlainObject(b.ajax)?b.ajax.url:b.ajax:b.sAjaxSource}return this.iterator("table",function(b){h.isPlainObject(b.ajax)?b.ajax.url=a:b.ajax=a})});r("ajax.url().load()",function(a,b){return this.iterator("table",function(c){Sb(c,
!1===b,a)})});var Za=function(a,b){var c=[],d,e,f,g,j,i;if(!a||"string"===typeof a||a.length===l)a=[a];f=0;for(g=a.length;f<g;f++){e=a[f]&&a[f].split?a[f].split(","):[a[f]];j=0;for(i=e.length;j<i;j++)(d=b("string"===typeof e[j]?h.trim(e[j]):e[j]))&&d.length&&c.push.apply(c,d)}return c},$a=function(a){a||(a={});a.filter&&!a.search&&(a.search=a.filter);return{search:a.search||"none",order:a.order||"current",page:a.page||"all"}},ab=function(a){for(var b=0,c=a.length;b<c;b++)if(0<a[b].length)return a[0]=
a[b],a.length=1,a.context=[a.context[b]],a;a.length=0;return a},Ya=function(a,b){var c,d,e,f=[],g=a.aiDisplay;c=a.aiDisplayMaster;var j=b.search;d=b.order;e=b.page;if("ssp"==z(a))return"removed"===j?[]:S(0,c.length);if("current"==e){c=a._iDisplayStart;for(d=a.fnDisplayEnd();c<d;c++)f.push(g[c])}else if("current"==d||"applied"==d)f="none"==j?c.slice():"applied"==j?g.slice():h.map(c,function(a){return-1===h.inArray(a,g)?a:null});else if("index"==d||"original"==d){c=0;for(d=a.aoData.length;c<d;c++)"none"==
j?f.push(c):(e=h.inArray(c,g),(-1===e&&"removed"==j||0<=e&&"applied"==j)&&f.push(c))}return f};r("rows()",function(a,b){a===l?a="":h.isPlainObject(a)&&(b=a,a="");var b=$a(b),c=this.iterator("table",function(c){var e=b;return Za(a,function(a){var b=Ob(a);if(b!==null&&!e)return[b];var j=Ya(c,e);if(b!==null&&h.inArray(b,j)!==-1)return[b];if(!a)return j;for(var b=[],i=0,n=j.length;i<n;i++)b.push(c.aoData[j[i]].nTr);return a.nodeName&&h.inArray(a,b)!==-1?[a._DT_RowIndex]:h(b).filter(a).map(function(){return this._DT_RowIndex}).toArray()})});
c.selector.rows=a;c.selector.opts=b;return c});r("rows().nodes()",function(){return this.iterator("row",function(a,b){return a.aoData[b].nTr||l})});r("rows().data()",function(){return this.iterator(!0,"rows",function(a,b){return xa(a.aoData,b,"_aData")})});v("rows().cache()","row().cache()",function(a){return this.iterator("row",function(b,c){var d=b.aoData[c];return"search"===a?d._aFilterData:d._aSortData})});v("rows().invalidate()","row().invalidate()",function(a){return this.iterator("row",function(b,
c){la(b,c,a)})});v("rows().indexes()","row().index()",function(){return this.iterator("row",function(a,b){return b})});v("rows().remove()","row().remove()",function(){var a=this;return this.iterator("row",function(b,c,d){var e=b.aoData;e.splice(c,1);for(var f=0,g=e.length;f<g;f++)null!==e[f].nTr&&(e[f].nTr._DT_RowIndex=f);h.inArray(c,b.aiDisplay);ka(b.aiDisplayMaster,c);ka(b.aiDisplay,c);ka(a[d],c,!1);Qa(b)})});r("rows.add()",function(a){var b=this.iterator("table",function(b){var c,f,g,h=[];f=0;
for(g=a.length;f<g;f++)c=a[f],c.nodeName&&"TR"===c.nodeName.toUpperCase()?h.push(ha(b,c)[0]):h.push(I(b,c));return h}),c=this.rows(-1);c.pop();c.push.apply(c,b.toArray());return c});r("row()",function(a,b){return ab(this.rows(a,b))});r("row().data()",function(a){var b=this.context;if(a===l)return b.length&&this.length?b[0].aoData[this[0]]._aData:l;b[0].aoData[this[0]]._aData=a;la(b[0],this[0],"data");return this});r("row().node()",function(){var a=this.context;return a.length&&this.length?a[0].aoData[this[0]].nTr||
null:null});r("row.add()",function(a){a instanceof h&&a.length&&(a=a[0]);var b=this.iterator("table",function(b){return a.nodeName&&"TR"===a.nodeName.toUpperCase()?ha(b,a)[0]:I(b,a)});return this.row(b[0])});var bb=function(a){var b=a.context;b.length&&a.length&&(a=b[0].aoData[a[0]],a._details&&(a._details.remove(),a._detailsShow=l,a._details=l))},Tb=function(a,b){var c=a.context;if(c.length&&a.length){var d=c[0].aoData[a[0]];if(d._details){(d._detailsShow=b)?d._details.insertAfter(d.nTr):d._details.detach();
var e=c[0],f=new q(e),g=e.aoData;f.off("draw.dt.DT_details column-visibility.dt.DT_details destroy.dt.DT_details");0<C(g,"_details").length&&(f.on("draw.dt.DT_details",function(a,b){e===b&&f.rows({page:"current"}).eq(0).each(function(a){a=g[a];a._detailsShow&&a._details.insertAfter(a.nTr)})}),f.on("column-visibility.dt.DT_details",function(a,b){if(e===b)for(var c,d=Z(b),f=0,h=g.length;f<h;f++)c=g[f],c._details&&c._details.children("td[colspan]").attr("colspan",d)}),f.on("destroy.dt.DT_details",function(a,
b){if(e===b)for(var c=0,d=g.length;c<d;c++)g[c]._details&&bb(g[c])}))}}};r("row().child()",function(a,b){var c=this.context;if(a===l)return c.length&&this.length?c[0].aoData[this[0]]._details:l;if(!0===a)this.child.show();else if(!1===a)bb(this);else if(c.length&&this.length){var d=c[0],c=c[0].aoData[this[0]],e=[],f=function(a,b){if(a.nodeName&&"tr"===a.nodeName.toLowerCase())e.push(a);else{var c=h("<tr><td/></tr>").addClass(b);h("td",c).addClass(b).html(a)[0].colSpan=Z(d);e.push(c[0])}};if(h.isArray(a)||
a instanceof h)for(var g=0,j=a.length;g<j;g++)f(a[g],b);else f(a,b);c._details&&c._details.remove();c._details=h(e);c._detailsShow&&c._details.insertAfter(c.nTr)}return this});r(["row().child.show()","row().child().show()"],function(){Tb(this,!0);return this});r(["row().child.hide()","row().child().hide()"],function(){Tb(this,!1);return this});r(["row().child.remove()","row().child().remove()"],function(){bb(this);return this});r("row().child.isShown()",function(){var a=this.context;return a.length&&
this.length?a[0].aoData[this[0]]._detailsShow||!1:!1});var ac=/^(.+):(name|visIdx|visible)$/;r("columns()",function(a,b){a===l?a="":h.isPlainObject(a)&&(b=a,a="");var b=$a(b),c=this.iterator("table",function(b){var c=a,f=b.aoColumns,g=C(f,"sName"),j=C(f,"nTh");return Za(c,function(a){var c=Ob(a);if(a==="")return S(f.length);if(c!==null)return[c>=0?c:f.length+c];var e=typeof a==="string"?a.match(ac):"";if(e)switch(e[2]){case "visIdx":case "visible":a=parseInt(e[1],10);if(a<0){c=h.map(f,function(a,
b){return a.bVisible?b:null});return[c[c.length+a]]}return[ga(b,a)];case "name":return h.map(g,function(a,b){return a===e[1]?b:null})}else return h(j).filter(a).map(function(){return h.inArray(this,j)}).toArray()})});c.selector.cols=a;c.selector.opts=b;return c});v("columns().header()","column().header()",function(){return this.iterator("column",function(a,b){return a.aoColumns[b].nTh})});v("columns().footer()","column().footer()",function(){return this.iterator("column",function(a,b){return a.aoColumns[b].nTf})});
v("columns().data()","column().data()",function(){return this.iterator("column-rows",function(a,b,c,d,e){for(var c=[],d=0,f=e.length;d<f;d++)c.push(A(a,e[d],b,""));return c})});v("columns().cache()","column().cache()",function(a){return this.iterator("column-rows",function(b,c,d,e,f){return xa(b.aoData,f,"search"===a?"_aFilterData":"_aSortData",c)})});v("columns().nodes()","column().nodes()",function(){return this.iterator("column-rows",function(a,b,c,d,e){return xa(a.aoData,e,"anCells",b)})});v("columns().visible()",
"column().visible()",function(a,b){return this.iterator("column",function(c,d){var e;if(a===l)e=c.aoColumns[d].bVisible;else{var f=c.aoColumns;e=f[d];var g=c.aoData,j,i,n;if(a===l)e=e.bVisible;else{if(e.bVisible!==a){if(a){var m=h.inArray(!0,C(f,"bVisible"),d+1);j=0;for(i=g.length;j<i;j++)n=g[j].nTr,f=g[j].anCells,n&&n.insertBefore(f[d],f[m]||null)}else h(C(c.aoData,"anCells",d)).detach();e.bVisible=a;ba(c,c.aoHeader);ba(c,c.aoFooter);if(b===l||b)V(c),(c.oScroll.sX||c.oScroll.sY)&&W(c);u(c,null,"column-visibility",
[c,d,a]);ta(c)}e=void 0}}return e})});v("columns().indexes()","column().index()",function(a){return this.iterator("column",function(b,c){return"visible"===a?Y(b,c):c})});r("columns.adjust()",function(){return this.iterator("table",function(a){V(a)})});r("column.index()",function(a,b){if(0!==this.context.length){var c=this.context[0];if("fromVisible"===a||"toData"===a)return ga(c,b);if("fromData"===a||"toVisible"===a)return Y(c,b)}});r("column()",function(a,b){return ab(this.columns(a,b))});r("cells()",
function(a,b,c){h.isPlainObject(a)&&(typeof a.row!==l?(c=b,b=null):(c=a,a=null));h.isPlainObject(b)&&(c=b,b=null);if(null===b||b===l)return this.iterator("table",function(b){var d=a,e=$a(c),f=b.aoData,g=Ya(b,e),e=xa(f,g,"anCells"),j=h([].concat.apply([],e)),i,m=b.aoColumns.length,n,p,r,q;return Za(d,function(a){if(a===null||a===l){n=[];p=0;for(r=g.length;p<r;p++){i=g[p];for(q=0;q<m;q++)n.push({row:i,column:q})}return n}return h.isPlainObject(a)?[a]:j.filter(a).map(function(a,b){i=b.parentNode._DT_RowIndex;
return{row:i,column:h.inArray(b,f[i].anCells)}}).toArray()})});var d=this.columns(b,c),e=this.rows(a,c),f,g,j,i,n,m=this.iterator("table",function(a,b){f=[];g=0;for(j=e[b].length;g<j;g++){i=0;for(n=d[b].length;i<n;i++)f.push({row:e[b][g],column:d[b][i]})}return f});h.extend(m.selector,{cols:b,rows:a,opts:c});return m});v("cells().nodes()","cell().node()",function(){return this.iterator("cell",function(a,b,c){return a.aoData[b].anCells[c]})});r("cells().data()",function(){return this.iterator("cell",
function(a,b,c){return A(a,b,c)})});v("cells().cache()","cell().cache()",function(a){a="search"===a?"_aFilterData":"_aSortData";return this.iterator("cell",function(b,c,d){return b.aoData[c][a][d]})});v("cells().indexes()","cell().index()",function(){return this.iterator("cell",function(a,b,c){return{row:b,column:c,columnVisible:Y(a,c)}})});r(["cells().invalidate()","cell().invalidate()"],function(a){var b=this.selector;this.rows(b.rows,b.opts).invalidate(a);return this});r("cell()",function(a,b,
c){return ab(this.cells(a,b,c))});r("cell().data()",function(a){var b=this.context,c=this[0];if(a===l)return b.length&&c.length?A(b[0],c[0].row,c[0].column):l;Ea(b[0],c[0].row,c[0].column,a);la(b[0],c[0].row,"data",c[0].column);return this});r("order()",function(a,b){var c=this.context;if(a===l)return 0!==c.length?c[0].aaSorting:l;"number"===typeof a?a=[[a,b]]:h.isArray(a[0])||(a=Array.prototype.slice.call(arguments));return this.iterator("table",function(b){b.aaSorting=a.slice()})});r("order.listener()",
function(a,b,c){return this.iterator("table",function(d){Ka(d,a,b,c)})});r(["columns().order()","column().order()"],function(a){var b=this;return this.iterator("table",function(c,d){var e=[];h.each(b[d],function(b,c){e.push([c,a])});c.aaSorting=e})});r("search()",function(a,b,c,d){var e=this.context;return a===l?0!==e.length?e[0].oPreviousSearch.sSearch:l:this.iterator("table",function(e){e.oFeatures.bFilter&&ca(e,h.extend({},e.oPreviousSearch,{sSearch:a+"",bRegex:null===b?!1:b,bSmart:null===c?!0:
c,bCaseInsensitive:null===d?!0:d}),1)})});v("columns().search()","column().search()",function(a,b,c,d){return this.iterator("column",function(e,f){var g=e.aoPreSearchCols;if(a===l)return g[f].sSearch;e.oFeatures.bFilter&&(h.extend(g[f],{sSearch:a+"",bRegex:null===b?!1:b,bSmart:null===c?!0:c,bCaseInsensitive:null===d?!0:d}),ca(e,e.oPreviousSearch,1))})});r("state()",function(){return this.context.length?this.context[0].oSavedState:null});r("state.clear()",function(){return this.iterator("table",function(a){a.fnStateSaveCallback.call(a.oInstance,
a,{})})});r("state.loaded()",function(){return this.context.length?this.context[0].oLoadedState:null});r("state.save()",function(){return this.iterator("table",function(a){ta(a)})});p.versionCheck=p.fnVersionCheck=function(a){for(var b=p.version.split("."),a=a.split("."),c,d,e=0,f=a.length;e<f;e++)if(c=parseInt(b[e],10)||0,d=parseInt(a[e],10)||0,c!==d)return c>d;return!0};p.isDataTable=p.fnIsDataTable=function(a){var b=h(a).get(0),c=!1;h.each(p.settings,function(a,e){if(e.nTable===b||e.nScrollHead===
b||e.nScrollFoot===b)c=!0});return c};p.tables=p.fnTables=function(a){return jQuery.map(p.settings,function(b){if(!a||a&&h(b.nTable).is(":visible"))return b.nTable})};p.camelToHungarian=G;r("$()",function(a,b){var c=this.rows(b).nodes(),c=h(c);return h([].concat(c.filter(a).toArray(),c.find(a).toArray()))});h.each(["on","one","off"],function(a,b){r(b+"()",function(){var a=Array.prototype.slice.call(arguments);a[0].match(/\.dt\b/)||(a[0]+=".dt");var d=h(this.tables().nodes());d[b].apply(d,a);return this})});
r("clear()",function(){return this.iterator("table",function(a){ja(a)})});r("settings()",function(){return new q(this.context,this.context)});r("data()",function(){return this.iterator("table",function(a){return C(a.aoData,"_aData")}).flatten()});r("destroy()",function(a){a=a||!1;return this.iterator("table",function(b){var c=b.nTableWrapper.parentNode,d=b.oClasses,e=b.nTable,f=b.nTBody,g=b.nTHead,j=b.nTFoot,i=h(e),f=h(f),l=h(b.nTableWrapper),m=h.map(b.aoData,function(a){return a.nTr}),o;b.bDestroying=
!0;u(b,"aoDestroyCallback","destroy",[b]);a||(new q(b)).columns().visible(!0);l.unbind(".DT").find(":not(tbody *)").unbind(".DT");h(za).unbind(".DT-"+b.sInstance);e!=g.parentNode&&(i.children("thead").detach(),i.append(g));j&&e!=j.parentNode&&(i.children("tfoot").detach(),i.append(j));i.detach();l.detach();b.aaSorting=[];b.aaSortingFixed=[];sa(b);h(m).removeClass(b.asStripeClasses.join(" "));h("th, td",g).removeClass(d.sSortable+" "+d.sSortableAsc+" "+d.sSortableDesc+" "+d.sSortableNone);b.bJUI&&
(h("th span."+d.sSortIcon+", td span."+d.sSortIcon,g).detach(),h("th, td",g).each(function(){var a=h("div."+d.sSortJUIWrapper,this);h(this).append(a.contents());a.detach()}));!a&&c&&c.insertBefore(e,b.nTableReinsertBefore);f.children().detach();f.append(m);i.css("width",b.sDestroyWidth).removeClass(d.sTable);(o=b.asDestroyStripes.length)&&f.children().each(function(a){h(this).addClass(b.asDestroyStripes[a%o])});c=h.inArray(b,p.settings);-1!==c&&p.settings.splice(c,1)})});p.version="1.10.2";p.settings=
[];p.models={};p.models.oSearch={bCaseInsensitive:!0,sSearch:"",bRegex:!1,bSmart:!0};p.models.oRow={nTr:null,anCells:null,_aData:[],_aSortData:null,_aFilterData:null,_sFilterRow:null,_sRowStripe:"",src:null};p.models.oColumn={idx:null,aDataSort:null,asSorting:null,bSearchable:null,bSortable:null,bVisible:null,_sManualType:null,_bAttrSrc:!1,fnCreatedCell:null,fnGetData:null,fnSetData:null,mData:null,mRender:null,nTh:null,nTf:null,sClass:null,sContentPadding:null,sDefaultContent:null,sName:null,sSortDataType:"std",
sSortingClass:null,sSortingClassJUI:null,sTitle:null,sType:null,sWidth:null,sWidthOrig:null};p.defaults={aaData:null,aaSorting:[[0,"asc"]],aaSortingFixed:[],ajax:null,aLengthMenu:[10,25,50,100],aoColumns:null,aoColumnDefs:null,aoSearchCols:[],asStripeClasses:null,bAutoWidth:!0,bDeferRender:!1,bDestroy:!1,bFilter:!0,bInfo:!0,bJQueryUI:!1,bLengthChange:!0,bPaginate:!0,bProcessing:!1,bRetrieve:!1,bScrollCollapse:!1,bServerSide:!1,bSort:!0,bSortMulti:!0,bSortCellsTop:!1,bSortClasses:!0,bStateSave:!1,
fnCreatedRow:null,fnDrawCallback:null,fnFooterCallback:null,fnFormatNumber:function(a){return a.toString().replace(/\B(?=(\d{3})+(?!\d))/g,this.oLanguage.sThousands)},fnHeaderCallback:null,fnInfoCallback:null,fnInitComplete:null,fnPreDrawCallback:null,fnRowCallback:null,fnServerData:null,fnServerParams:null,fnStateLoadCallback:function(a){try{return JSON.parse((-1===a.iStateDuration?sessionStorage:localStorage).getItem("DataTables_"+a.sInstance+"_"+location.pathname))}catch(b){}},fnStateLoadParams:null,
fnStateLoaded:null,fnStateSaveCallback:function(a,b){try{(-1===a.iStateDuration?sessionStorage:localStorage).setItem("DataTables_"+a.sInstance+"_"+location.pathname,JSON.stringify(b))}catch(c){}},fnStateSaveParams:null,iStateDuration:7200,iDeferLoading:null,iDisplayLength:10,iDisplayStart:0,iTabIndex:0,oClasses:{},oLanguage:{oAria:{sSortAscending:": activate to sort column ascending",sSortDescending:": activate to sort column descending"},oPaginate:{sFirst:"First",sLast:"Last",sNext:"Next",sPrevious:"Previous"},
sEmptyTable:"No data available in table",sInfo:"Showing _START_ to _END_ of _TOTAL_ entries",sInfoEmpty:"Showing 0 to 0 of 0 entries",sInfoFiltered:"(filtered from _MAX_ total entries)",sInfoPostFix:"",sDecimal:"",sThousands:",",sLengthMenu:"Show _MENU_ entries",sLoadingRecords:"Loading...",sProcessing:"Processing...",sSearch:"Search:",sSearchPlaceholder:"",sUrl:"",sZeroRecords:"No matching records found"},oSearch:h.extend({},p.models.oSearch),sAjaxDataProp:"data",sAjaxSource:null,sDom:"lfrtip",sPaginationType:"simple_numbers",
sScrollX:"",sScrollXInner:"",sScrollY:"",sServerMethod:"GET",renderer:null};T(p.defaults);p.defaults.column={aDataSort:null,iDataSort:-1,asSorting:["asc","desc"],bSearchable:!0,bSortable:!0,bVisible:!0,fnCreatedCell:null,mData:null,mRender:null,sCellType:"td",sClass:"",sContentPadding:"",sDefaultContent:null,sName:"",sSortDataType:"std",sTitle:null,sType:null,sWidth:null};T(p.defaults.column);p.models.oSettings={oFeatures:{bAutoWidth:null,bDeferRender:null,bFilter:null,bInfo:null,bLengthChange:null,
bPaginate:null,bProcessing:null,bServerSide:null,bSort:null,bSortMulti:null,bSortClasses:null,bStateSave:null},oScroll:{bCollapse:null,iBarWidth:0,sX:null,sXInner:null,sY:null},oLanguage:{fnInfoCallback:null},oBrowser:{bScrollOversize:!1,bScrollbarLeft:!1},ajax:null,aanFeatures:[],aoData:[],aiDisplay:[],aiDisplayMaster:[],aoColumns:[],aoHeader:[],aoFooter:[],oPreviousSearch:{},aoPreSearchCols:[],aaSorting:null,aaSortingFixed:[],asStripeClasses:null,asDestroyStripes:[],sDestroyWidth:0,aoRowCallback:[],
aoHeaderCallback:[],aoFooterCallback:[],aoDrawCallback:[],aoRowCreatedCallback:[],aoPreDrawCallback:[],aoInitComplete:[],aoStateSaveParams:[],aoStateLoadParams:[],aoStateLoaded:[],sTableId:"",nTable:null,nTHead:null,nTFoot:null,nTBody:null,nTableWrapper:null,bDeferLoading:!1,bInitialised:!1,aoOpenRows:[],sDom:null,sPaginationType:"two_button",iStateDuration:0,aoStateSave:[],aoStateLoad:[],oSavedState:null,oLoadedState:null,sAjaxSource:null,sAjaxDataProp:null,bAjaxDataGet:!0,jqXHR:null,json:l,oAjaxData:l,
fnServerData:null,aoServerParams:[],sServerMethod:null,fnFormatNumber:null,aLengthMenu:null,iDraw:0,bDrawing:!1,iDrawError:-1,_iDisplayLength:10,_iDisplayStart:0,_iRecordsTotal:0,_iRecordsDisplay:0,bJUI:null,oClasses:{},bFiltered:!1,bSorted:!1,bSortCellsTop:null,oInit:null,aoDestroyCallback:[],fnRecordsTotal:function(){return"ssp"==z(this)?1*this._iRecordsTotal:this.aiDisplayMaster.length},fnRecordsDisplay:function(){return"ssp"==z(this)?1*this._iRecordsDisplay:this.aiDisplay.length},fnDisplayEnd:function(){var a=
this._iDisplayLength,b=this._iDisplayStart,c=b+a,d=this.aiDisplay.length,e=this.oFeatures,f=e.bPaginate;return e.bServerSide?!1===f||-1===a?b+d:Math.min(b+a,this._iRecordsDisplay):!f||c>d||-1===a?d:c},oInstance:null,sInstance:null,iTabIndex:0,nScrollHead:null,nScrollFoot:null,aLastSort:[],oPlugins:{}};p.ext=t={classes:{},errMode:"alert",feature:[],search:[],internal:{},legacy:{ajax:null},pager:{},renderer:{pageButton:{},header:{}},order:{},type:{detect:[],search:{},order:{}},_unique:0,fnVersionCheck:p.fnVersionCheck,
iApiIndex:0,oJUIClasses:{},sVersion:p.version};h.extend(t,{afnFiltering:t.search,aTypes:t.type.detect,ofnSearch:t.type.search,oSort:t.type.order,afnSortData:t.order,aoFeatures:t.feature,oApi:t.internal,oStdClasses:t.classes,oPagination:t.pager});h.extend(p.ext.classes,{sTable:"dataTable",sNoFooter:"no-footer",sPageButton:"paginate_button",sPageButtonActive:"current",sPageButtonDisabled:"disabled",sStripeOdd:"odd",sStripeEven:"even",sRowEmpty:"dataTables_empty",sWrapper:"dataTables_wrapper",sFilter:"dataTables_filter",
sInfo:"dataTables_info",sPaging:"dataTables_paginate paging_",sLength:"dataTables_length",sProcessing:"dataTables_processing",sSortAsc:"sorting_asc",sSortDesc:"sorting_desc",sSortable:"sorting",sSortableAsc:"sorting_asc_disabled",sSortableDesc:"sorting_desc_disabled",sSortableNone:"sorting_disabled",sSortColumn:"sorting_",sFilterInput:"",sLengthSelect:"",sScrollWrapper:"dataTables_scroll",sScrollHead:"dataTables_scrollHead",sScrollHeadInner:"dataTables_scrollHeadInner",sScrollBody:"dataTables_scrollBody",
sScrollFoot:"dataTables_scrollFoot",sScrollFootInner:"dataTables_scrollFootInner",sHeaderTH:"",sFooterTH:"",sSortJUIAsc:"",sSortJUIDesc:"",sSortJUI:"",sSortJUIAscAllowed:"",sSortJUIDescAllowed:"",sSortJUIWrapper:"",sSortIcon:"",sJUIHeader:"",sJUIFooter:""});var ya="",ya="",E=ya+"ui-state-default",ea=ya+"css_right ui-icon ui-icon-",Ub=ya+"fg-toolbar ui-toolbar ui-widget-header ui-helper-clearfix";h.extend(p.ext.oJUIClasses,p.ext.classes,{sPageButton:"fg-button ui-button "+E,sPageButtonActive:"ui-state-disabled",
sPageButtonDisabled:"ui-state-disabled",sPaging:"dataTables_paginate fg-buttonset ui-buttonset fg-buttonset-multi ui-buttonset-multi paging_",sSortAsc:E+" sorting_asc",sSortDesc:E+" sorting_desc",sSortable:E+" sorting",sSortableAsc:E+" sorting_asc_disabled",sSortableDesc:E+" sorting_desc_disabled",sSortableNone:E+" sorting_disabled",sSortJUIAsc:ea+"triangle-1-n",sSortJUIDesc:ea+"triangle-1-s",sSortJUI:ea+"carat-2-n-s",sSortJUIAscAllowed:ea+"carat-1-n",sSortJUIDescAllowed:ea+"carat-1-s",sSortJUIWrapper:"DataTables_sort_wrapper",
sSortIcon:"DataTables_sort_icon",sScrollHead:"dataTables_scrollHead "+E,sScrollFoot:"dataTables_scrollFoot "+E,sHeaderTH:E,sFooterTH:E,sJUIHeader:Ub+" ui-corner-tl ui-corner-tr",sJUIFooter:Ub+" ui-corner-bl ui-corner-br"});var Lb=p.ext.pager;h.extend(Lb,{simple:function(){return["previous","next"]},full:function(){return["first","previous","next","last"]},simple_numbers:function(a,b){return["previous",Ua(a,b),"next"]},full_numbers:function(a,b){return["first","previous",Ua(a,b),"next","last"]},_numbers:Ua,
numbers_length:7});h.extend(!0,p.ext.renderer,{pageButton:{_:function(a,b,c,d,e,f){var g=a.oClasses,j=a.oLanguage.oPaginate,i,l,m=0,o=function(b,d){var k,p,r,q,s=function(b){Ra(a,b.data.action,true)};k=0;for(p=d.length;k<p;k++){q=d[k];if(h.isArray(q)){r=h("<"+(q.DT_el||"div")+"/>").appendTo(b);o(r,q)}else{l=i="";switch(q){case "ellipsis":b.append("<span>&hellip;</span>");break;case "first":i=j.sFirst;l=q+(e>0?"":" "+g.sPageButtonDisabled);break;case "previous":i=j.sPrevious;l=q+(e>0?"":" "+g.sPageButtonDisabled);
break;case "next":i=j.sNext;l=q+(e<f-1?"":" "+g.sPageButtonDisabled);break;case "last":i=j.sLast;l=q+(e<f-1?"":" "+g.sPageButtonDisabled);break;default:i=q+1;l=e===q?g.sPageButtonActive:""}if(i){r=h("<a>",{"class":g.sPageButton+" "+l,"aria-controls":a.sTableId,"data-dt-idx":m,tabindex:a.iTabIndex,id:c===0&&typeof q==="string"?a.sTableId+"_"+q:null}).html(i).appendTo(b);Ta(r,{action:q},s);m++}}}};try{var k=h(O.activeElement).data("dt-idx");o(h(b).empty(),d);k!==null&&h(b).find("[data-dt-idx="+k+"]").focus()}catch(p){}}}});
var va=function(a,b,c,d){if(!a||"-"===a)return-Infinity;b&&(a=Pb(a,b));a.replace&&(c&&(a=a.replace(c,"")),d&&(a=a.replace(d,"")));return 1*a};h.extend(t.type.order,{"date-pre":function(a){return Date.parse(a)||0},"html-pre":function(a){return H(a)?"":a.replace?a.replace(/<.*?>/g,"").toLowerCase():a+""},"string-pre":function(a){return H(a)?"":"string"===typeof a?a.toLowerCase():!a.toString?"":a.toString()},"string-asc":function(a,b){return a<b?-1:a>b?1:0},"string-desc":function(a,b){return a<b?1:a>
b?-1:0}});cb("");h.extend(p.ext.type.detect,[function(a,b){var c=b.oLanguage.sDecimal;return Xa(a,c)?"num"+c:null},function(a){if(a&&(!Yb.test(a)||!Zb.test(a)))return null;var b=Date.parse(a);return null!==b&&!isNaN(b)||H(a)?"date":null},function(a,b){var c=b.oLanguage.sDecimal;return Xa(a,c,!0)?"num-fmt"+c:null},function(a,b){var c=b.oLanguage.sDecimal;return Qb(a,c)?"html-num"+c:null},function(a,b){var c=b.oLanguage.sDecimal;return Qb(a,c,!0)?"html-num-fmt"+c:null},function(a){return H(a)||"string"===
typeof a&&-1!==a.indexOf("<")?"html":null}]);h.extend(p.ext.type.search,{html:function(a){return H(a)?a:"string"===typeof a?a.replace(Nb," ").replace(wa,""):""},string:function(a){return H(a)?a:"string"===typeof a?a.replace(Nb," "):a}});h.extend(!0,p.ext.renderer,{header:{_:function(a,b,c,d){h(a.nTable).on("order.dt.DT",function(e,f,g,h){if(a===f){e=c.idx;b.removeClass(c.sSortingClass+" "+d.sSortAsc+" "+d.sSortDesc).addClass(h[e]=="asc"?d.sSortAsc:h[e]=="desc"?d.sSortDesc:c.sSortingClass)}})},jqueryui:function(a,
b,c,d){var e=c.idx;h("<div/>").addClass(d.sSortJUIWrapper).append(b.contents()).append(h("<span/>").addClass(d.sSortIcon+" "+c.sSortingClassJUI)).appendTo(b);h(a.nTable).on("order.dt.DT",function(f,g,h,i){if(a===g){b.removeClass(d.sSortAsc+" "+d.sSortDesc).addClass(i[e]=="asc"?d.sSortAsc:i[e]=="desc"?d.sSortDesc:c.sSortingClass);b.find("span."+d.sSortIcon).removeClass(d.sSortJUIAsc+" "+d.sSortJUIDesc+" "+d.sSortJUI+" "+d.sSortJUIAscAllowed+" "+d.sSortJUIDescAllowed).addClass(i[e]=="asc"?d.sSortJUIAsc:
i[e]=="desc"?d.sSortJUIDesc:c.sSortingClassJUI)}})}}});p.render={number:function(a,b,c,d){return{display:function(e){var f=0>e?"-":"",e=Math.abs(parseFloat(e)),g=parseInt(e,10),e=c?b+(e-g).toFixed(c).substring(2):"";return f+(d||"")+g.toString().replace(/\B(?=(\d{3})+(?!\d))/g,a)+e}}}};h.extend(p.ext.internal,{_fnExternApiFunc:Mb,_fnBuildAjax:na,_fnAjaxUpdate:jb,_fnAjaxParameters:sb,_fnAjaxUpdateDraw:tb,_fnAjaxDataSrc:oa,_fnAddColumn:Aa,_fnColumnOptions:fa,_fnAdjustColumnSizing:V,_fnVisibleToColumnIndex:ga,
_fnColumnIndexToVisible:Y,_fnVisbleColumns:Z,_fnGetColumns:X,_fnColumnTypes:Da,_fnApplyColumnDefs:hb,_fnHungarianMap:T,_fnCamelToHungarian:G,_fnLanguageCompat:N,_fnBrowserDetect:fb,_fnAddData:I,_fnAddTr:ha,_fnNodeToDataIndex:function(a,b){return b._DT_RowIndex!==l?b._DT_RowIndex:null},_fnNodeToColumnIndex:function(a,b,c){return h.inArray(c,a.aoData[b].anCells)},_fnGetCellData:A,_fnSetCellData:Ea,_fnSplitObjNotation:Ga,_fnGetObjectDataFn:U,_fnSetObjectDataFn:Ba,_fnGetDataMaster:Ha,_fnClearTable:ja,
_fnDeleteIndex:ka,_fnInvalidateRow:la,_fnGetRowElements:ia,_fnCreateTr:Fa,_fnBuildHead:ib,_fnDrawHead:ba,_fnDraw:K,_fnReDraw:L,_fnAddOptionsHtml:lb,_fnDetectHeader:aa,_fnGetUniqueThs:ma,_fnFeatureHtmlFilter:nb,_fnFilterComplete:ca,_fnFilterCustom:wb,_fnFilterColumn:vb,_fnFilter:ub,_fnFilterCreateSearch:Na,_fnEscapeRegex:Oa,_fnFilterData:xb,_fnFeatureHtmlInfo:qb,_fnUpdateInfo:Ab,_fnInfoMacros:Bb,_fnInitialise:ra,_fnInitComplete:pa,_fnLengthChange:Pa,_fnFeatureHtmlLength:mb,_fnFeatureHtmlPaginate:rb,
_fnPageChange:Ra,_fnFeatureHtmlProcessing:ob,_fnProcessingDisplay:B,_fnFeatureHtmlTable:pb,_fnScrollDraw:W,_fnApplyToChildren:F,_fnCalculateColumnWidths:Ca,_fnThrottle:Ma,_fnConvertToWidth:Cb,_fnScrollingWidthAdjust:Eb,_fnGetWidestNode:Db,_fnGetMaxLenString:Fb,_fnStringToCss:s,_fnScrollBarWidth:Gb,_fnSortFlatten:R,_fnSort:kb,_fnSortAria:Ib,_fnSortListener:Sa,_fnSortAttachListener:Ka,_fnSortingClasses:sa,_fnSortData:Hb,_fnSaveState:ta,_fnLoadState:Jb,_fnSettingsFromNode:ua,_fnLog:P,_fnMap:D,_fnBindAction:Ta,
_fnCallbackReg:x,_fnCallbackFire:u,_fnLengthOverflow:Qa,_fnRenderer:La,_fnDataSource:z,_fnRowAttributes:Ia,_fnCalculateEnd:function(){}});h.fn.dataTable=p;h.fn.dataTableSettings=p.settings;h.fn.dataTableExt=p.ext;h.fn.DataTable=function(a){return h(this).dataTable(a).api()};h.each(p,function(a,b){h.fn.DataTable[a]=b});return h.fn.dataTable};"function"===typeof define&&define.amd?define("datatables",["jquery"],N):"object"===typeof exports?N(require("jquery")):jQuery&&!jQuery.fn.dataTable&&N(jQuery)})(window,
document);
;/*! DataTables Bootstrap integration
 * ©2011-2014 SpryMedia Ltd - datatables.net/license
 */

/**
 * DataTables integration for Bootstrap 3. This requires Bootstrap 3 and
 * DataTables 1.10 or newer.
 *
 * This file sets the defaults and adds options to DataTables to style its
 * controls using Bootstrap. See http://datatables.net/manual/styling/bootstrap
 * for further information.
 */
(function(window, document, undefined){

var factory = function( $, DataTable ) {
"use strict";


/* Set the defaults for DataTables initialisation */
$.extend( true, DataTable.defaults, {
	dom:
		"<'row'<'col-xs-6'l><'col-xs-6'f>r>"+
		"t"+
		"<'row'<'col-xs-6'i><'col-xs-6'p>>",
	renderer: 'bootstrap'
} );


/* Default class modification */
$.extend( DataTable.ext.classes, {
	sWrapper:      "dataTables_wrapper form-inline dt-bootstrap",
	sFilterInput:  "form-control input-sm",
	sLengthSelect: "form-control input-sm"
} );


/* Bootstrap paging button renderer */
DataTable.ext.renderer.pageButton.bootstrap = function ( settings, host, idx, buttons, page, pages ) {
	var api     = new DataTable.Api( settings );
	var classes = settings.oClasses;
	var lang    = settings.oLanguage.oPaginate;
	var btnDisplay, btnClass;

	var attach = function( container, buttons ) {
		var i, ien, node, button;
		var clickHandler = function ( e ) {
			e.preventDefault();
			if ( e.data.action !== 'ellipsis' ) {
				api.page( e.data.action ).draw( false );
			}
		};

		for ( i=0, ien=buttons.length ; i<ien ; i++ ) {
			button = buttons[i];

			if ( $.isArray( button ) ) {
				attach( container, button );
			}
			else {
				btnDisplay = '';
				btnClass = '';

				switch ( button ) {
					case 'ellipsis':
						btnDisplay = '&hellip;';
						btnClass = 'disabled';
						break;

					case 'first':
						btnDisplay = lang.sFirst;
						btnClass = button + (page > 0 ?
							'' : ' disabled');
						break;

					case 'previous':
						btnDisplay = lang.sPrevious;
						btnClass = button + (page > 0 ?
							'' : ' disabled');
						break;

					case 'next':
						btnDisplay = lang.sNext;
						btnClass = button + (page < pages-1 ?
							'' : ' disabled');
						break;

					case 'last':
						btnDisplay = lang.sLast;
						btnClass = button + (page < pages-1 ?
							'' : ' disabled');
						break;

					default:
						btnDisplay = button + 1;
						btnClass = page === button ?
							'active' : '';
						break;
				}

				if ( btnDisplay ) {
					node = $('<li>', {
							'class': classes.sPageButton+' '+btnClass,
							'aria-controls': settings.sTableId,
							'tabindex': settings.iTabIndex,
							'id': idx === 0 && typeof button === 'string' ?
								settings.sTableId +'_'+ button :
								null
						} )
						.append( $('<a>', {
								'href': '#'
							} )
							.html( btnDisplay )
						)
						.appendTo( container );

					settings.oApi._fnBindAction(
						node, {action: button}, clickHandler
					);
				}
			}
		}
	};

	attach(
		$(host).empty().html('<ul class="pagination"/>').children('ul'),
		buttons
	);
};


/*
 * TableTools Bootstrap compatibility
 * Required TableTools 2.1+
 */
if ( DataTable.TableTools ) {
	// Set the classes that TableTools uses to something suitable for Bootstrap
	$.extend( true, DataTable.TableTools.classes, {
		"container": "DTTT btn-group",
		"buttons": {
			"normal": "btn btn-default",
			"disabled": "disabled"
		},
		"collection": {
			"container": "DTTT_dropdown dropdown-menu",
			"buttons": {
				"normal": "",
				"disabled": "disabled"
			}
		},
		"print": {
			"info": "DTTT_print_info modal"
		},
		"select": {
			"row": "active"
		}
	} );

	// Have the collection use a bootstrap compatible drop down
	$.extend( true, DataTable.TableTools.DEFAULTS.oTags, {
		"collection": {
			"container": "ul",
			"button": "li",
			"liner": "a"
		}
	} );
}

}; // /factory


// Define as an AMD module if possible
if ( typeof define === 'function' && define.amd ) {
	define( ['jquery', 'datatables'], factory );
}
else if ( typeof exports === 'object' ) {
    // Node/CommonJS
    factory( require('jquery'), require('datatables') );
}
else if ( jQuery ) {
	// Otherwise simply initialise as normal, stopping multiple evaluation
	factory( jQuery, jQuery.fn.dataTable );
}


})(window, document);

;/*jslint plusplus: true, nomen: true */
/*!
* Yet Another DataTables Column Filter - (yadcf)
* 
* File:        jquery.dataTables.yadcf.js
* Version:     0.8.3
* 
* Author:      Daniel Reznick
* Info:        https://github.com/vedmack/yadcf
* Contact:     vedmack@gmail.com
* Twitter:	   @danielreznick
* Q&A		   https://groups.google.com/forum/#!forum/daniels_code	
*
* Copyright 2013 Daniel Reznick, all rights reserved.
* Dual licensed under two licenses: GPL v2 license or a BSD (3-point) license (just like DataTables itself)
* 
* This source file is distributed in the hope that it will be useful, but 
* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
* or FITNESS FOR A PARTICULAR PURPOSE. See the license files for details.
*/
/*
* Parameters:
*
*					
* -------------

* column_number
				Required:			true
				Type:				String
				Description:		The number of the column to which the filter will be applied

* filter_type				
				Required:			false
				Type:				String
				Default value:		select
				Possible values:	select / multi_select / auto_complete / text / date / range_number / range_number_slider / range_date / custom_func
				Description:		The type of the filter to be used in the column

* custom_func				
				Required:			true (when filter_type is custom_func)
				Type:				function
				Default value:		undefined
				Description:		should be pointing to a function with the following signature myCustomFilterFunction(filterVal, columnVal) , where filterVal is the value from the select box and
									columnVal is the value from the relevant row column, this function should return true if teh row matches your condition and the row should be displayed) and false otherwise
	
* data
				Required:			false / true (when filter_type is custom_func)
				Type:				Array (of string or objects)
				Description:		When the need of predefined data for filter is needed just use an array of strings ["value1","value2"....] (supported in select / multi_select / auto_complete filters) or
									array of objects [{value: 'Some Data 1', label: 'One'}, {value: 'Some Data 3', label: 'Three'}] (supported in select / multi_select filters)
				Note:				that when filter_type is custom_func this array will populate the custom filter select element
					
* column_data_type
				Required:			false
				Type:				String
				Default value:		text
				Possible values:	text / html	
				Description:		The type of data in column , use "html" when you have some html code in the column (support parsing of multiple elements per cell)

* text_data_delimiter
				Required:			false
				Type:				String
				Description:		Delimiter that seperates text in table column, for example text_data_delimiter: ","
										
* html_data_type
				Required:			false
				Type:				String
				Default value:		text
				Possible values:	text / value / id			
				Description:		When using "html" for column_data_type argument you can choose how exactly to parse your html element/s in column , for example use "text" for the following <span class="someClass">Some text</span>

* filter_container_id
				Required:			false
				Type:				String
				Description:		In case that user don't want to place the filter in column header , he can pass an id of the desired container for the column filter 
		
* filter_default_label
				Required:			false
				Type:				String / Array of string in case of range_number filter (first entry is for the first input and the second entry is for the second input
				Default value:		Select value
				Description:		The label that will appear in the select menu filter when no value is selected from the filter
									
* filter_reset_button_text
				Required:			false
				Type:				String / boolean
				Default value:		x
				Description:		The text that will appear inside the reset button next to the select drop down (set this to false (boolean) in order to hide it from that column filter)

* enable_auto_complete (this attribute is deprecated , and will become obsolete in the future , so you better start using filter_type: "auto_complete")
				Required:			false
				Type:				boolean
				Default value:		false
				Description:		Turns the filter into an autocomplete input - make use of the jQuery UI Autocomplete widget (with some enhancements)

* sort_as
				Required:			false
				Type:				String
				Default value:		alpha
				Possible values:	alpha / num
				Description:		Defines how the values in the filter will be sorted, alphabetically or numerically

* sort_order
				Required:			false
				Type:				String
				Default value:		asc
				Possible values:	asc / desc
				Description:		Defines the order in which the values in the filter will be sorted, ascending or descending

* date_format
				Required:			false
				Type:				String
				Default value:		mm/dd/yyyy
				Possible values:	mm/dd/yyyy / dd/mm/yyyy (eventually I replace yyyy into yy for jquery datepicker)
				Description:		Defines the format in which the date values are being parsed into Date object

* ignore_char
				Required:			false
				Type:				String
				Description:		Tells the range_number and range_number_slide to ignore specific char while filtering (that char can used as number separator)
				
* filter_match_mode
				Required:			false
				Type:				String
				Default value:		contains
				Possible values:	contains / exact / startsWith
				Description:		Allows to control the matching mode of the filter (supported in select / auto_complete / text filters)
				
* select_type
				Required:			false
				Type:				String
				Default value:		undefined
				Possible values:	chosen / select2
				Description:		Turns the simple select element into "Chosen/Select2 select" (make use of the Chosen/Select2 jQuery plugin)
				
				
* select_type_options
				Required:			false
				Type:				Object
				Default value:		{}
				Description:		This parameter will be passed "as is" to the Chosen/Select2 plugin constructor
				
				
* case_insensitive
				Required:			false
				Type:				boolean
				Default value:		true
				Description:		Do case-insensitive filtering (supported in select / auto_complete / text filters)


* filter_delay
				Required:			false
				Type:				integer
				Default value:		undefined
				Description:		Delay filter execution for a XXX milliseconds - filter will fire XXX milliseconds after the last keyup.
				Special Notes:		Currently supported in text / range_number / range_date filters / range_number_slider

*				
*				
*				
* External API functions:
*
*					
* -------------				

* exFilterColumn
				Description:		Allows to trigger filter/s externally/programmatically (support ALL filter types!!!) , perfect for showing table with pre filtered columns
				Arguments:			table_arg: (variable of the datatable), 
									array of pairs: column number String/Object with from and to, filter_value (the actual string value that we want to filter by)
				Usage Example:		yadcf.exFilterColumn(oTable, [[0, 'Some Data 2']]); //pre filter one column
									yadcf.exFilterColumn(oTable, [[0, 'Some Data 1'], [1, {from: 111, to: 1110}], [2, {from: "", to: "11/25/2014"}]]); //pre filter several columns

* exGetColumnFilterVal									
				Description:		Allows to retreive  column current filtered value (support ALL filter types!!!)
				Arguments:			table_arg: (variable of the datatable), 
									column number:  column number from which we want the value
				Usage Example:		yadcf.exGetColumnFilterVal(oTable,1);
				Return value:		String (for simple filter) / Object (for range filter) with from and to properties / Array of strings for multi_select filter

*
*				
*				
* Server-side processing API (see more on showcase):
* 
* From server to client:
* In order to populate the filters with data from server (select / auto_complete / range_number_slider (min and max values), you should add to your current json respond the following properties: 
* lets say for first column you add yadcf_data_0 filled with array of values, for column second column yadcf_data_1 and so on...
* 
* From client to server:
* Read the filtered value like this (for first column) req.getParameter("columns[0][search][value]"); <- java code , php/.Net/etc you just need to get it from the request
* Range filter value will arrive delimited by  -yadcf_delim- , so just split it into an array or something like this: String[] minMax = sSearch_0.split("-yadcf_delim-");
*
*					
* -------------
*
*/
var yadcf = (function ($) {

	'use strict';

	var oTables = {},
		oTablesIndex = {},
		options = {},
		exFilterColumnQueue = [],
		yadcfDelay;

	function getOptions(selector) {
		return options[selector];
	}

	function setOptions(selector_arg, options_arg) {
		var tmpOptions = {},
			i,
			j,
			col_num_as_int,
			default_options = {
				filter_type : "select",
				enable_auto_complete : false,
				sort_as : "alpha",
				sort_order : "asc",
				date_format : "mm/dd/yyyy",
				ignore_char : undefined,
				filter_match_mode : "contains",
				select_type : undefined,
				select_type_options : {},
				case_insensitive : true
			},
			adaptContainerCssClassImpl = function (dummy) { return ''; };

		for (i = 0; i < options_arg.length; i++) {
			if (options_arg[i].select_type === 'select2') {
				default_options.select_type_options = {adaptContainerCssClass: adaptContainerCssClassImpl};
			}
			//validate custom function required attributes
			if (options_arg[i].filter_type === 'custom_func') {
				if (options_arg[i].custom_func === undefined) {
					alert('You are trying to use filter_type: "custom_func" for column ' + options_arg[i].column_number + ' but there is no such custom_func attribute provided (custom_func: \"function reference goes here...\")');
					return;
				}
				if (options_arg[i].data === undefined) {
					alert('You are trying to use filter_type: "custom_func" for column ' + options_arg[i].column_number + ' but there is no such data attribute provided (data: \"array of options goes here...\")');
					return;
				}
			}
			col_num_as_int = +options_arg[i].column_number;
			tmpOptions[col_num_as_int] = $.extend(true, {}, default_options, options_arg[i]);
		}
		options[selector_arg] = tmpOptions;
	}

	function resetIApiIndex() {
		$.fn.dataTableExt.iApiIndex = 0;

	}

	function generateTableSelectorJQFriendly(tmpStr) {
		return tmpStr.replace(":", "-").replace("(", "").replace(")", "").replace(".", "-").replace("#", "-");
	}

	yadcfDelay = (function () {
		var timer = 0;
		return function (callback, ms, param) {
			clearTimeout(timer);
			timer = setTimeout(function () {
				callback(param);
			}, ms);
			return timer;
		};
	}());

	//Used by exFilterColumn for translating readable search value into proper search string for datatables filtering
	function yadcfMatchFilterString(table_arg, column_number, selected_value, filter_match_mode, multiple) {
		var case_insensitive = yadcf.getOptions(table_arg.selector)[column_number].case_insensitive,
			ret_val;

		table_arg.fnSettings().aoPreSearchCols[column_number].bSmart = false;
		table_arg.fnSettings().aoPreSearchCols[column_number].bCaseInsensitive = case_insensitive;

		if (multiple === undefined || multiple === false) {
			if (filter_match_mode === "contains") {
				table_arg.fnSettings().aoPreSearchCols[column_number].bRegex = false;
				ret_val = selected_value;
			} else if (filter_match_mode === "exact") {
				table_arg.fnSettings().aoPreSearchCols[column_number].bRegex = true;
				ret_val = "^" + selected_value + "$";
			} else if (filter_match_mode === "startsWith") {
				table_arg.fnSettings().aoPreSearchCols[column_number].bRegex = true;
				ret_val = "^" + selected_value;
			}
		} else {
			if (filter_match_mode === "contains") {
				table_arg.fnSettings().aoPreSearchCols[column_number].bRegex = true;
				ret_val = selected_value.join("|");
			} else if (filter_match_mode === "exact") {
				table_arg.fnSettings().aoPreSearchCols[column_number].bRegex = true;
				ret_val = "^(" + selected_value.join("|") + ")$";
			} else if (filter_match_mode === "startsWith") {
				table_arg.fnSettings().aoPreSearchCols[column_number].bRegex = true;
				ret_val = "^(" + selected_value.join("|") + ")";
			}
		}
		return ret_val;
	}

	function yadcfMatchFilter(oTable, selected_value, filter_match_mode, column_number) {
		var case_insensitive = yadcf.getOptions(oTable.selector)[column_number].case_insensitive;
		if (filter_match_mode === "contains") {
			oTable.fnFilter(selected_value, column_number, false, false, true, case_insensitive);
		} else if (filter_match_mode === "exact") {
			oTable.fnFilter("^" + selected_value + "$", column_number, true, false, true, case_insensitive);
		} else if (filter_match_mode === "startsWith") {
			oTable.fnFilter("^" + selected_value, column_number, true, false, true, case_insensitive);
		}
	}

	function yadcfParseMatchFilter(tmpStr, filter_match_mode) {
		var retVal;
		if (filter_match_mode === "contains") {
			retVal = tmpStr;
		} else if (filter_match_mode === "exact") {
			retVal = tmpStr.substring(1, tmpStr.length - 1);
		} else if (filter_match_mode === "startsWith") {
			retVal = tmpStr.substring(1, tmpStr.length);
		}
		return retVal;
	}

	function doFilterCustomDateFunc(arg, table_selector_jq_friendly, column_number) {
		var oTable = oTables[table_selector_jq_friendly];
		if (arg === "clear" || arg.value === "-1") {
			if (arg === "clear") {
				$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).val("-1").focus();
			}
			$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).removeClass("inuse");
		} else {
			$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).addClass("inuse");
		}

		oTable.fnDraw();
	}

	function doFilter(arg, table_selector_jq_friendly, column_number, filter_match_mode) {
		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		var oTable = oTables[table_selector_jq_friendly],
			selected_value;

		if (arg === "clear") {
			$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).val("-1").focus();
			$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).removeClass("inuse");
			$(document).data("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "_val", "-1");
			oTable.fnFilter("", column_number);
			resetIApiIndex();
			if (yadcf.getOptions(oTable.selector)[column_number].select_type === 'chosen') {
				$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).trigger("chosen:updated");
			}
			return;
		}

		$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).addClass("inuse");

		$(document).data("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "_val", arg.value);

		selected_value = $.trim($(arg).find('option:selected').val());

		if (arg.value !== "-1") {
			yadcfMatchFilter(oTable, selected_value, filter_match_mode, column_number);
		} else {
			oTable.fnFilter("", column_number);
			$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).removeClass("inuse");
		}
		resetIApiIndex();
	}

	function doFilterMultiSelect(arg, table_selector_jq_friendly, column_number, filter_match_mode) {
		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		var oTable = oTables[table_selector_jq_friendly],
			aEscapedTerms = [],
			selected_values = $(arg).val(),
			i,
			stringForSearch;

		$(document).data("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "_val", selected_values);

		if (selected_values !== null) {
			for (i = selected_values.length - 1; i >= 0; i--) {
				if (selected_values[i] === "-1") {
					selected_values.splice(i, 1);
					break;
				}
			}
			if (selected_values.length !== 0) {
				stringForSearch = selected_values.join("|");
				stringForSearch = stringForSearch.replace(/([.*+?^=!:${}()\[\]\/\\])/g, "\\$1");
				if (filter_match_mode === "contains") {
					oTable.fnFilter(stringForSearch, column_number, true, false, true);
				} else if (filter_match_mode === "exact") {
					oTable.fnFilter("^(" + stringForSearch + ")$", column_number, true, false, true);
				} else if (filter_match_mode === "startsWith") {
					oTable.fnFilter("^(" + stringForSearch + ")", column_number, true, false, true);
				}
			} else {
				oTable.fnFilter("", column_number);
			}
		} else {
			oTable.fnFilter("", column_number);
		}
		resetIApiIndex();
	}

	function yadcfParseMatchFilterMultiSelect(tmpStr, filter_match_mode) {
		var retVal;
		if (filter_match_mode === "contains") {
			retVal = tmpStr;
		} else if (filter_match_mode === "exact") {
			retVal = tmpStr.substring(1, tmpStr.length - 1);
			retVal = retVal.substring(1, retVal.length - 1);
		} else if (filter_match_mode === "startsWith") {
			retVal = tmpStr.substring(1, tmpStr.length);
			retVal = retVal.substring(1, retVal.length - 1);
		}
		return retVal;
	}

	function doFilterAutocomplete(arg, table_selector_jq_friendly, column_number, filter_match_mode) {
		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		var oTable = oTables[table_selector_jq_friendly];

		if (arg === "clear") {
			$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).val("").focus();
			$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).removeClass("inuse");
			$(document).removeData("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "_val");
			oTable.fnFilter("", column_number);
			resetIApiIndex();
			return;
		}

		$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).addClass("inuse");

		$(document).data("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "_val", arg.value);

		yadcfMatchFilter(oTable, arg.value, filter_match_mode, column_number);

		resetIApiIndex();
	}

	function autocompleteSelect(event, ui) {

		var table_column = event.target.id.replace("yadcf-filter-", ""),
			dashIndex = table_column.lastIndexOf("-"),
			table_selector_jq_friendly = table_column.substring(0, dashIndex),
			col_num = parseInt(table_column.substring(dashIndex + 1), 10),
			filter_match_mode = $(event.target).attr("filter_match_mode");
		doFilterAutocomplete(ui.item, table_selector_jq_friendly, col_num, filter_match_mode);
	}

	function sortNumAsc(a, b) {
		return a - b;
	}

	function sortNumDesc(a, b) {
		return b - a;
	}

	function findMinInArray(array, ignore_char) {
		var narray = [], i;
		for (i = 0; i < array.length; i++) {
			if (array[i] !== null) {
				if (ignore_char !== undefined) {
					array[i] = array[i].toString().replace(ignore_char, "");
				}
				narray.push(array[i]);
			}
		}
		return Math.min.apply(Math, narray);
	}

	function findMaxInArray(array, ignore_char) {
		var narray = [], i;
		for (i = 0; i < array.length; i++) {
			if (array[i] !== null) {
				if (ignore_char !== undefined) {
					array[i] = array[i].toString().replace(ignore_char, "");
				}
				narray.push(array[i]);
			}
		}
		return Math.max.apply(Math, narray);
	}

	function addRangeNumberFilterCapability(table_selector_jq_friendly, fromId, toId, col_num, ignore_char) {

		$.fn.dataTableExt.afnFiltering.push(
			function (oSettings, aData, iDataIndex) {
				var min = document.getElementById(fromId)?document.getElementById(fromId).value:'',
					max = document.getElementById(toId)?document.getElementById(toId).value:'',
					val = aData[col_num] === "-" ? 0 : aData[col_num],
					retVal = false,
					table_selector_jq_friendly_local = table_selector_jq_friendly,
					current_table_selector_jq_friendly = yadcf.generateTableSelectorJQFriendly(oSettings.oInstance.selector),
					ignore_char_local = ignore_char;

				if (table_selector_jq_friendly_local !== current_table_selector_jq_friendly) {
					return true;
				}

				if (ignore_char_local !== undefined) {
					min = min.replace(ignore_char_local, "");
					max = max.replace(ignore_char_local, "");
					val = val.toString().replace(ignore_char_local, "");
				}

				min = (min !== "") ? (+min) : min;
				max = (max !== "") ? (+max) : max;
				val = (val !== "") ? (+val) : val;
				if (min === "" && max === "") {
					retVal = true;
				} else if (min === "" && val <= max) {
					retVal = true;
				} else if (min <= val && "" === max) {
					retVal = true;
				} else if (min <= val && val <= max) {
					retVal = true;
				}
				return retVal;
			}
		);
	}

	function addCustomFunctionFilterCapability(table_selector_jq_friendly, filterId, col_num) {

		$.fn.dataTableExt.afnFiltering.push(
			function (oSettings, aData, iDataIndex) {
				var filterVal = document.getElementById(filterId) !== null ? document.getElementById(filterId).value : "",
					columnVal = aData[col_num] === "-" ? 0 : aData[col_num],
					retVal = false,
					table_selector_jq_friendly_local = table_selector_jq_friendly,
					current_table_selector_jq_friendly = yadcf.generateTableSelectorJQFriendly(oSettings.oInstance.selector),
					custom_func;

				if (table_selector_jq_friendly_local !== current_table_selector_jq_friendly) {
					return true;
				}

				custom_func = yadcf.getOptions(oSettings.oInstance.selector)[col_num].custom_func;

				retVal = custom_func(filterVal, columnVal);

				return retVal;
			}
		);
	}
	function addRangeDateFilterCapability(table_selector_jq_friendly, fromId, toId, col_num, date_format) {

		$.fn.dataTableExt.afnFiltering.push(
			function (oSettings, aData, iDataIndex) {
				var min = document.getElementById(fromId) !== null ? document.getElementById(fromId).value : "",
					max = document.getElementById(toId) !== null ? document.getElementById(toId).value : "",
					val = aData[col_num] === "-" ? 0 : aData[col_num],
					retVal = false,
					table_selector_jq_friendly_local = table_selector_jq_friendly,
					current_table_selector_jq_friendly = yadcf.generateTableSelectorJQFriendly(oSettings.oInstance.selector);

				if (table_selector_jq_friendly_local !== current_table_selector_jq_friendly) {
					return true;
				}
				try {
					if (min.length === (date_format.length + 2)) {
						min = (min !== "") ? $.datepicker.parseDate(date_format, min) : min;
					}
				} catch (err1) {}
				try {
					if (max.length === (date_format.length + 2)) {
						max = (max !== "") ? $.datepicker.parseDate(date_format, max) : max;
					}
				} catch (err2) {}
				try {
					val = (val !== "") ? $.datepicker.parseDate(date_format, val) : val;
				} catch (err3) {}

				if ((min === "" || !(min instanceof Date)) && (max === "" || !(max instanceof Date))) {
					retVal = true;
				} else if (min === "" && val <= max) {
					retVal = true;
				} else if (min <= val && "" === max) {
					retVal = true;
				} else if (min <= val && val <= max) {
					retVal = true;
				}

				return retVal;
			}
		);
	}

	function addRangeNumberSliderFilterCapability(table_selector_jq_friendly, fromId, toId, col_num, ignore_char) {

		$.fn.dataTableExt.afnFiltering.push(
			function (oSettings, aData, iDataIndex) {
				var min = $('#' + fromId).text(),
					max = $('#' + toId).text(),
					val = aData[col_num] === "-" ? 0 : aData[col_num],
					retVal = false,
					table_selector_jq_friendly_local = table_selector_jq_friendly,
					current_table_selector_jq_friendly = yadcf.generateTableSelectorJQFriendly(oSettings.oInstance.selector),
					ignore_char_local = ignore_char,
					column_data_type,
					html_data_type,
					i,
					columnObjKey;

				if (table_selector_jq_friendly_local !== current_table_selector_jq_friendly) {
					return true;
				}

				if (!isFinite(min) || !isFinite(max)) {
					return true;
				}

				column_data_type = yadcf.getOptions(oSettings.oInstance.selector)[col_num].column_data_type;
				html_data_type = yadcf.getOptions(oSettings.oInstance.selector)[col_num].html_data_type;

				if (column_data_type === "html") {
					if (html_data_type === undefined) {
						html_data_type = "text";
					}
					switch (html_data_type) {
					case "text":
						val = $(val).text();
						break;
					case "value":
						val = $(val).val();
						break;
					case "id":
						val = val.id;
						break;
					}
				}

				if (ignore_char_local !== undefined) {
					min = min.replace(ignore_char_local, "");
					max = max.replace(ignore_char_local, "");
					val = val.toString().replace(ignore_char_local, "");
				}

				min = (min !== "") ? (+min) : min;
				max = (max !== "") ? (+max) : max;
				val = (val !== "") ? (+val) : val;
				if (min === "" && max === "") {
					retVal = true;
				} else if (min === "" && val <= max) {
					retVal = true;
				} else if (min <= val && "" === max) {
					retVal = true;
				} else if (min <= val && val <= max) {
					retVal = true;
				}
				return retVal;
			}
		);
	}

	function addRangeNumberFilter(filter_selector_string, table_selector_jq_friendly, column_number, filter_reset_button_text, filter_default_label, ignore_char) {
		var fromId = "yadcf-filter-" + table_selector_jq_friendly + "-from-" + column_number,
			toId = "yadcf-filter-" + table_selector_jq_friendly + "-to-" + column_number,
			filter_selector_string_tmp,
			filter_wrapper_id,
			oTable;

		filter_wrapper_id = "yadcf-filter-wrapper-" + table_selector_jq_friendly + "-" + column_number;

		if ($("#" + filter_wrapper_id).length > 0) {
			return;
		}

		//add a wrapper to hold both filter and reset button
		$(filter_selector_string).append("<div onclick=\"yadcf.stopPropagation(event);\" id=\"" + filter_wrapper_id + "\" class=\"yadcf-filter-wrapper\"></div>");
		filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper";
		filter_selector_string_tmp = filter_selector_string;

		$(filter_selector_string).append("<div id=\"yadcf-filter-wrapper-inner-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter-wrapper-inner\"></div>");
		filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper-inner";

		$(filter_selector_string).append("<input placeholder=\"" + filter_default_label[0] + "\" id=\"" + fromId + "\" class=\"yadcf-filter-range-number yadcf-filter-range\" onkeyup=\"yadcf.rangeNumberKeyUP('" + table_selector_jq_friendly + "',event);\">" +
			"</input>");
		$(filter_selector_string).append("<span class=\"yadcf-filter-range-number-seperator\" >" +
			"</span>");
		$(filter_selector_string).append("<input placeholder=\"" + filter_default_label[1] + "\" id=\"" + toId + "\" class=\"yadcf-filter-range-number yadcf-filter-range\" onkeyup=\"yadcf.rangeNumberKeyUP('" + table_selector_jq_friendly + "',event);\">" +
			"</input>");

		if (filter_reset_button_text !== false) {
			$(filter_selector_string_tmp).append("<input value=\"" + filter_reset_button_text + "\" type=\"button\" " +
				"onclick=\"yadcf.stopPropagation(event);yadcf.rangeClear('" + table_selector_jq_friendly + "',event); return false;\" class=\"yadcf-filter-reset-button\">");
		}

		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		oTable = oTables[table_selector_jq_friendly];
		if (oTable.fnSettings().oFeatures.bStateSave === true && oTable.fnSettings().oLoadedState) {
			if (oTable.fnSettings().oLoadedState.yadcfState && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly] && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number]) {
				$('#' + fromId).val(oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].from);
				if (oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].from !== "") {
					$('#' + fromId).addClass("inuse");
				}
				$('#' + toId).val(oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].to);
				if (oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].to !== "") {
					$('#' + toId).addClass("inuse");
				}
			}
		}
		resetIApiIndex();

		if (oTable.fnSettings().oFeatures.bServerSide !== true) {
			addRangeNumberFilterCapability(table_selector_jq_friendly, fromId, toId, column_number, ignore_char);
		}

	}

	function dateSelectSingle(date, event, clear) {
		var oTable,
			column_number = $(event).attr('id').replace('yadcf-filter-', '').replace('-date', '').replace('-reset', ''),
			dashIndex = column_number.lastIndexOf("-"),
			table_selector_jq_friendly = column_number.substring(0, dashIndex),
			date_str;

		column_number = column_number.substring(dashIndex + 1);
		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		oTable = oTables[table_selector_jq_friendly];

		if (clear === undefined) {
			date_str = document.getElementById($(event).attr('id')).value;
			oTable.fnFilter(date, column_number);
			$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).addClass("inuse");
		} else if (clear === 'clear') {
			oTable.fnFilter('', column_number);
			$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val('').removeClass("inuse");
		}

		resetIApiIndex();
	}

	function dateSelect(date, event) {

		var oTable,
			column_number = $(event).attr("id").replace("yadcf-filter-", "").replace("-from-date", "").replace("-to-date", ""),
			dashIndex = column_number.lastIndexOf("-"),
			table_selector_jq_friendly = column_number.substring(0, dashIndex),
			yadcfState,
			from,
			to,
			min,
			max,
			min_server,
			max_server,
			date_format;

		column_number = column_number.substring(dashIndex + 1);
		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];

		oTable = oTables[table_selector_jq_friendly];
		date_format = yadcf.getOptions(oTable.selector)[column_number].date_format;
		date_format = date_format.replace("yyyy", "yy");

		$("#" + $(event).attr("id")).addClass("inuse");

		if ($(event).attr("id").indexOf("-from-") !== -1) {
			from = document.getElementById($(event).attr("id")).value;
			to = document.getElementById($(event).attr("id").replace("-from-", "-to-")).value;
		} else {
			to = document.getElementById($(event).attr("id")).value;
			from = document.getElementById($(event).attr("id").replace("-to-", "-from-")).value;
		}

		if (oTable.fnSettings().oFeatures.bServerSide !== true) {
			oTable.fnDraw();
		} else {
			min = from;
			max = to;

			try {
				if (min.length === (date_format.length + 2)) {
					min = (min !== "") ? $.datepicker.parseDate(date_format, min) : min;
				}
			} catch (err1) {}
			try {
				if (max.length === (date_format.length + 2)) {
					max = (max !== "") ? $.datepicker.parseDate(date_format, max) : max;
				}
			} catch (err2) {}
			if (min instanceof Date) {
				min_server = min.getTime();
			} else {
				min_server = min;
			}
			if (max instanceof Date) {
				max_server = max.getTime();
			} else {
				max_server = max;
			}
			oTable.fnFilter(min_server + '-yadcf_delim-' + max_server, column_number);
		}

		if (!oTable.fnSettings().oLoadedState) {
			oTable.fnSettings().oLoadedState = {};
			oTable.fnSettings().oApi._fnSaveState(oTable.fnSettings());
		}
		if (oTable.fnSettings().oFeatures.bStateSave === true) {
			if (oTable.fnSettings().oLoadedState.yadcfState !== undefined && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly] !== undefined) {
				oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number] =
					{
						'from' : from,
						'to' : to
					};
			} else {
				yadcfState = {};
				yadcfState[table_selector_jq_friendly] = [];
				yadcfState[table_selector_jq_friendly][column_number] = {
					'from' : from,
					'to' : to
				};
				oTable.fnSettings().oLoadedState.yadcfState = yadcfState;
			}
			oTable.fnSettings().oApi._fnSaveState(oTable.fnSettings());
		}

		resetIApiIndex();
	}

	function addRangeDateFilter(filter_selector_string, table_selector_jq_friendly, column_number, filter_reset_button_text, filter_default_label, date_format) {
		var fromId = "yadcf-filter-" + table_selector_jq_friendly + "-from-date-" + column_number,
			toId = "yadcf-filter-" + table_selector_jq_friendly + "-to-date-" + column_number,
			filter_selector_string_tmp,
			filter_wrapper_id,
			oTable;

		filter_wrapper_id = "yadcf-filter-wrapper-" + table_selector_jq_friendly + "-" + column_number;

		if ($("#" + filter_wrapper_id).length > 0) {
			return;
		}

		//add a wrapper to hold both filter and reset button
		$(filter_selector_string).append("<div onclick=\"yadcf.stopPropagation(event);\" id=\"" + filter_wrapper_id + "\" class=\"yadcf-filter-wrapper\"></div>");
		filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper";
		filter_selector_string_tmp = filter_selector_string;

		$(filter_selector_string).append("<div id=\"yadcf-filter-wrapper-inner-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter-wrapper-inner\"></div>");
		filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper-inner";

		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		oTable = oTables[table_selector_jq_friendly];
		$(filter_selector_string).append("<input placeholder=\"" + filter_default_label[0] + "\" id=\"" + fromId + "\" class=\"yadcf-filter-range-date yadcf-filter-range\" onkeyup=\"yadcf.rangeDateKeyUP('" + table_selector_jq_friendly + "','" + date_format + "',event);\">" +
			"</input>");
		$(filter_selector_string).append("<span class=\"yadcf-filter-range-date-seperator\" >" +
			"</span>");
		$(filter_selector_string).append("<input placeholder=\"" + filter_default_label[1] + "\" id=\"" + toId + "\" class=\"yadcf-filter-range-date yadcf-filter-range\" onkeyup=\"yadcf.rangeDateKeyUP('" + table_selector_jq_friendly + "','" + date_format + "',event);\">" +
			"</input>");

		if (filter_reset_button_text !== false) {
			$(filter_selector_string_tmp).append("<input value=\"" + filter_reset_button_text + "\" type=\"button\" " +
				"onclick=\"yadcf.stopPropagation(event);yadcf.rangeClear('" + table_selector_jq_friendly + "',event); return false;\" class=\"yadcf-filter-reset-button\">");
		}

		$("#" + fromId).datepicker({
			dateFormat: date_format,
			onSelect: dateSelect
		});
		$("#" + toId).datepicker({
			dateFormat: date_format,
			onSelect: dateSelect
		});

		if (oTable.fnSettings().oFeatures.bStateSave === true && oTable.fnSettings().oLoadedState) {
			if (oTable.fnSettings().oLoadedState.yadcfState && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly] && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number]) {
				$('#' + fromId).val(oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].from);
				if (oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].from !== "") {
					$('#' + fromId).addClass("inuse");
				}
				$('#' + toId).val(oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].to);
				if (oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].to !== "") {
					$('#' + toId).addClass("inuse");
				}
			}
		}

		if (oTable.fnSettings().oFeatures.bServerSide !== true) {
			addRangeDateFilterCapability(table_selector_jq_friendly, fromId, toId, column_number, date_format);
		}

		resetIApiIndex();
	}

	function addDateFilter(filter_selector_string, table_selector_jq_friendly, column_number, filter_reset_button_text, filter_default_label, date_format) {
		var dateId = "yadcf-filter-" + table_selector_jq_friendly + "-" + column_number,
			filter_selector_string_tmp,
			filter_wrapper_id,
			oTable;

		filter_wrapper_id = "yadcf-filter-wrapper-" + table_selector_jq_friendly + "-" + column_number;

		if ($("#" + filter_wrapper_id).length > 0) {
			return;
		}

		//add a wrapper to hold both filter and reset button
		$(filter_selector_string).append("<div onclick=\"yadcf.stopPropagation(event);\" id=\"" + filter_wrapper_id + "\" class=\"yadcf-filter-wrapper\"></div>");
		filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper";
		filter_selector_string_tmp = filter_selector_string;

		$(filter_selector_string).append("<input placeholder=\"" + filter_default_label + "\" id=\"" + dateId + "\" class=\"yadcf-filter-date\" onkeyup=\"yadcf.dateKeyUP('" +
			table_selector_jq_friendly + "','" + date_format + "',event);\"></input>");

		if (filter_reset_button_text !== false) {
			$(filter_selector_string_tmp).append('<input value="' + filter_reset_button_text + '" type="button" id="' + dateId + '-reset"' +
				'onclick="yadcf.stopPropagation(event);yadcf.dateSelectSingle(\'' + table_selector_jq_friendly + '\',event.target, \'clear\'); return false;" class="yadcf-filter-reset-button">');
		}

		$("#" + dateId).datepicker({
			dateFormat: date_format,
			onSelect: dateSelectSingle
		});

		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		oTable = oTables[table_selector_jq_friendly];

		if (oTable.fnSettings().aoPreSearchCols[column_number].sSearch !== '') {
			$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val(oTable.fnSettings().aoPreSearchCols[column_number].sSearch).addClass("inuse");
		}

		resetIApiIndex();

	}

	function rangeNumberSldierDrawTips(min_tip_val, max_tip_val, min_tip_id, max_tip_id, table_selector_jq_friendly, column_number) {
		var first_handle = $("#yadcf-filter-wrapper-inner-" + table_selector_jq_friendly + "-" + column_number + " .ui-slider-handle:first"),
			last_handle = $("#yadcf-filter-wrapper-inner-" + table_selector_jq_friendly + "-" + column_number + " .ui-slider-handle:last"),
			min_tip_inner,
			max_tip_inner;

		min_tip_inner = "<div id=\"" + min_tip_id + "\" class=\"yadcf-filter-range-number-slider-min-tip-inner\">" + min_tip_val + "</div>";
		max_tip_inner = "<div id=\"" + max_tip_id + "\" class=\"yadcf-filter-range-number-slider-max-tip-inner\">" + max_tip_val + "</div>";

		$(first_handle).addClass("yadcf-filter-range-number-slider-min-tip").html(min_tip_inner);
		$(last_handle).addClass("yadcf-filter-range-number-slider-max-tip").html(max_tip_inner);
	}

	function rangeNumberSliderChange(table_selector_jq_friendly, event, ui) {
		var oTable,
			min_val,
			max_val,
			slider_inuse,
			yadcfState,
			column_number = $(event.target).attr('id').replace("yadcf-filter-", "").replace(table_selector_jq_friendly, "").replace("-slider-", ""),
			options,
			keyUp;

		oTable = oTables[table_selector_jq_friendly];
		options = getOptions(oTable.selector)[column_number];

		keyUp = function () {

			$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];

			if (oTable.fnSettings().oFeatures.bServerSide !== true) {
				oTable.fnDraw();
			} else {
				oTable.fnFilter(ui.values[0] + '-yadcf_delim-' + ui.values[1], column_number);
			}

			min_val = +$($(event.target).parent().find(".yadcf-filter-range-number-slider-min-tip-hidden")).text();
			max_val = +$($(event.target).parent().find(".yadcf-filter-range-number-slider-max-tip-hidden")).text();


			if (min_val !== ui.values[0]) {
				$($(event.target).find(".ui-slider-handle")[0]).addClass("inuse");
				slider_inuse = true;
			} else {
				$($(event.target).find(".ui-slider-handle")[0]).removeClass("inuse");
			}
			if (max_val !== ui.values[1]) {
				$($(event.target).find(".ui-slider-handle")[1]).addClass("inuse");
				slider_inuse = true;
			} else {
				$($(event.target).find(".ui-slider-handle")[1]).removeClass("inuse");
			}

			if (slider_inuse === true) {
				$(event.target).find(".ui-slider-range").addClass("inuse");
			} else {
				$(event.target).find(".ui-slider-range").removeClass("inuse");
			}
			if (!oTable.fnSettings().oLoadedState) {
				oTable.fnSettings().oLoadedState = {};
				oTable.fnSettings().oApi._fnSaveState(oTable.fnSettings());
			}
			if (oTable.fnSettings().oFeatures.bStateSave === true) {
				if (oTable.fnSettings().oLoadedState.yadcfState !== undefined && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly] !== undefined) {
					oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number] =
						{
							'from' : ui.values[0],
							'to' : ui.values[1]
						};
				} else {
					yadcfState = {};
					yadcfState[table_selector_jq_friendly] = [];
					yadcfState[table_selector_jq_friendly][column_number] = {
						'from' : ui.values[0],
						'to' : ui.values[1]
					};
					oTable.fnSettings().oLoadedState.yadcfState = yadcfState;
				}
				oTable.fnSettings().oApi._fnSaveState(oTable.fnSettings());
			}

			resetIApiIndex();
		};

		if (options.filter_delay === undefined) {
			keyUp();
		} else {
			yadcfDelay(function () {
				keyUp();
			}, options.filter_delay);
		}
	}

	function addRangeNumberSliderFilter(filter_selector_string, table_selector_jq_friendly, column_number, filter_reset_button_text, min_val, max_val, ignore_char) {
		var sliderId = "yadcf-filter-" + table_selector_jq_friendly + "-slider-" + column_number,
			min_tip_id = "yadcf-filter-" + table_selector_jq_friendly + "-min_tip-" + column_number,
			max_tip_id = "yadcf-filter-" + table_selector_jq_friendly + "-max_tip-" + column_number,
			filter_selector_string_tmp,
			filter_wrapper_id,
			oTable,
			min_state_val = min_val,
			max_state_val = max_val;

		filter_wrapper_id = "yadcf-filter-wrapper-" + table_selector_jq_friendly + "-" + column_number;

		if ($("#" + filter_wrapper_id).length > 0) {
			if (!isFinite($('#' + min_tip_id).text()) || !isFinite($('#' + max_tip_id).text()) || !isFinite(min_val) || !isFinite(max_val) ||
					!isFinite($(filter_selector_string).find('.yadcf-filter-range-number-slider-min-tip-hidden.hide').text()) ||
					!isFinite($(filter_selector_string).find('.yadcf-filter-range-number-slider-max-tip-hidden.hide').text())) {
				$(filter_selector_string).empty();
			} else {
				return;
			}
		}

		//add a wrapper to hold both filter and reset button
		$(filter_selector_string).append("<div onclick=\"yadcf.stopPropagation(event);\" id=\"" + filter_wrapper_id + "\" class=\"yadcf-filter-wrapper\"></div>");
		filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper";
		filter_selector_string_tmp = filter_selector_string;

		$(filter_selector_string).append("<div id=\"yadcf-filter-wrapper-inner-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-number-slider-filter-wrapper-inner\"></div>");
		filter_selector_string = filter_selector_string + " div.yadcf-number-slider-filter-wrapper-inner";

		$(filter_selector_string).append("<div id=\"" + sliderId + "\" class=\"yadcf-filter-range-number-slider\"></div>");
		filter_selector_string = filter_selector_string + " #" + sliderId;

		$(filter_selector_string).append("<span class=\"yadcf-filter-range-number-slider-min-tip-hidden hide\">" + min_val + "</span>");
		$(filter_selector_string).append("<span class=\"yadcf-filter-range-number-slider-max-tip-hidden hide\">" + max_val + "</span>");

		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		oTable = oTables[table_selector_jq_friendly];
		if (oTable.fnSettings().oFeatures.bStateSave === true && oTable.fnSettings().oLoadedState) {
			if (oTable.fnSettings().oLoadedState.yadcfState && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly] && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number]) {
				if (min_val !== oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].from) {
					min_state_val = oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].from;
				}
				if (max_val !== oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].to) {
					max_state_val = oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].to;
				}
			}
		}

		if (isFinite(min_val) && isFinite(max_val) && isFinite(min_state_val) && isFinite(max_state_val)) {
			$("#" + sliderId).slider({
				range: true,
				min: min_val,
				max: max_val,
				values: [min_state_val, max_state_val],
				create: function (event, ui) {
					rangeNumberSldierDrawTips(min_state_val, max_state_val, min_tip_id, max_tip_id, table_selector_jq_friendly, column_number);
				},
				slide: function (event, ui) {
					rangeNumberSldierDrawTips(ui.values[0], ui.values[1], min_tip_id, max_tip_id, table_selector_jq_friendly, column_number);
					rangeNumberSliderChange(table_selector_jq_friendly, event, ui);
				},
				change: function (event, ui) {
					rangeNumberSldierDrawTips(ui.values[0], ui.values[1], min_tip_id, max_tip_id, table_selector_jq_friendly, column_number);
					rangeNumberSliderChange(table_selector_jq_friendly, event, ui);
				}
			});

			if (filter_reset_button_text !== false) {
				$(filter_selector_string_tmp).append("<input value=\"" + filter_reset_button_text + "\" type=\"button\" " +
					"onclick=\"yadcf.stopPropagation(event);yadcf.rangeNumberSliderClear('" + table_selector_jq_friendly + "',event); return false;\" class=\"yadcf-filter-reset-button range-number-slider-reset-button\">");
			}
		}

		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		oTable = oTables[table_selector_jq_friendly];
		if (oTable.fnSettings().oFeatures.bStateSave === true && oTable.fnSettings().oLoadedState) {
			if (oTable.fnSettings().oLoadedState.yadcfState && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly] && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number]) {
				if (isFinite(min_val) && min_val !== oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].from) {
					$($(filter_selector_string).find(".ui-slider-handle")[0]).addClass("inuse");
				}
				if (isFinite(max_val) && max_val !== oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].to) {
					$($(filter_selector_string).find(".ui-slider-handle")[1]).addClass("inuse");
				}
				if ((isFinite(min_val) && isFinite(max_val)) && (min_val !== oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].from || max_val !== oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number].to)) {
					$($(filter_selector_string).find(".ui-slider-range")).addClass("inuse");
				}
			}
		}
		resetIApiIndex();

		if (oTable.fnSettings().oFeatures.bServerSide !== true) {
			addRangeNumberSliderFilterCapability(table_selector_jq_friendly, min_tip_id, max_tip_id, column_number, ignore_char);
		}
	}

	function dot2obj(tmpObj, dot_refs) {
		var i = 0;
		dot_refs = dot_refs.split(".");
		for (i = 0; i < dot_refs.length; i++) {
			tmpObj = tmpObj[dot_refs[i]];
		}
		return tmpObj;
	}

	function appendFilters(oTable, args, table_selector) {

		var i = 0,
			$filter_selector,
			filter_selector_string,

			data,
			filter_container_id,
			column_number_data,
			column_number,
			column_data_type,
			html_data_type,
			text_data_delimiter,
			filter_default_label,
			filter_reset_button_text,
			enable_auto_complete,
			sort_as,
			sort_order,
			date_format,
			ignore_char,
			filter_match_mode,

			options,
			options_tmp,
			j,
			k,
			data_length,
			col_inner_elements,
			col_inner_data,
			col_filter_array,
			ii,
			table_selector_jq_friendly,
			min_val,
			max_val,
			col_num_visible,
			col_num_visible_iter,
			tmpStr,
			columnObjKey,
			columnObj;


		for (columnObjKey in args) {
			if (args.hasOwnProperty(columnObjKey)) {
				columnObj = args[columnObjKey];

				data = columnObj.data;
				filter_container_id = columnObj.filter_container_id;
				column_number = columnObj.column_number;
				column_number = +column_number;
				column_number_data = undefined;
				if (isNaN(oTable.fnSettings().aoColumns[column_number].mData) && typeof oTable.fnSettings().aoColumns[column_number].mData !== 'object') {
					column_number_data = oTable.fnSettings().aoColumns[column_number].mData;
				}
				column_data_type = columnObj.column_data_type;
				html_data_type = columnObj.html_data_type;
				text_data_delimiter = columnObj.text_data_delimiter;
				filter_default_label = columnObj.filter_default_label;
				filter_reset_button_text = columnObj.filter_reset_button_text;
				enable_auto_complete = columnObj.enable_auto_complete;
				sort_as = columnObj.sort_as;
				sort_order = columnObj.sort_order;
				date_format = columnObj.date_format;
				//for jquery datepicker
				date_format = date_format.replace("yyyy", "yy");

				if (columnObj.ignore_char !== undefined) {
					ignore_char = new RegExp(columnObj.ignore_char, "g");
				}
				filter_match_mode = columnObj.filter_match_mode;

				if (column_number === undefined) {
					alert("You must specify column number");
					return;
				}

				if (column_data_type === undefined) {
					column_data_type = "text";
				} else if (column_data_type === "html") {
					if (html_data_type === undefined) {
						html_data_type = "text";
					}
				}

				if (enable_auto_complete === true) {
					columnObj.filter_type = "auto_complete";
				}

				if (filter_default_label === undefined) {
					if (columnObj.filter_type === "select" || columnObj.filter_type === 'custom_func') {
						filter_default_label = "Select value";
					} else if (columnObj.filter_type === "multi_select") {
						filter_default_label = "Select values";
					} else if (columnObj.filter_type === "auto_complete" || columnObj.filter_type === "text") {
						filter_default_label = "Type a value";
					} else if (columnObj.filter_type === "range_number" || columnObj.filter_type === "range_date") {
						filter_default_label = ["from", "to"];
					} else if (columnObj.filter_type === "date") {
						filter_default_label = "Select a date";
					}
				}

				if (filter_reset_button_text === undefined) {
					filter_reset_button_text = "x";
				}

				options = [];
				col_filter_array = {};

				if (data === undefined) {
					data = oTable.fnSettings().aoData;
					data_length = data.length;

					for (j = 0; j < data_length; j++) {
						if (column_data_type === "html") {
							if (column_number_data === undefined) {
								col_inner_elements = $(data[j]._aData[column_number]);
							} else {
								col_inner_elements = dot2obj(data[j]._aData, column_number_data);
								col_inner_elements = $(col_inner_elements);
							}
							for (k = 0; k < col_inner_elements.length; k++) {
								switch (html_data_type) {
								case "text":
									col_inner_data = $(col_inner_elements[k]).text();
									break;
								case "value":
									col_inner_data = $(col_inner_elements[k]).val();
									break;
								case "id":
									col_inner_data = col_inner_elements[k].id;
									break;
								}
								if (!(col_filter_array.hasOwnProperty(col_inner_data))) {
									col_filter_array[col_inner_data] = col_inner_data;
									options.push(col_inner_data);
								}
							}
						} else if (column_data_type === "text") {
							if (text_data_delimiter !== undefined) {
								if (column_number_data === undefined) {
									col_inner_elements = data[j]._aData[column_number].split(text_data_delimiter);
								} else {
									col_inner_elements = dot2obj(data[j]._aData, column_number_data);
									col_inner_elements = col_inner_elements.split(text_data_delimiter);
								}
								for (k = 0; k < col_inner_elements.length; k++) {
									col_inner_data = col_inner_elements[k];
									if (!(col_filter_array.hasOwnProperty(col_inner_data))) {
										col_filter_array[col_inner_data] = col_inner_data;
										options.push(col_inner_data);
									}
								}
							} else {
								if (column_number_data === undefined) {
									col_inner_data = data[j]._aData[column_number];
								} else {
									col_inner_data = dot2obj(data[j]._aData, column_number_data);
								}
								if (!(col_filter_array.hasOwnProperty(col_inner_data))) {
									col_filter_array[col_inner_data] = col_inner_data;
									options.push(col_inner_data);
								}
							}
						}
					}

				} else {
					for (ii = 0; ii < data.length; ii++) {
						options.push(data[ii]);
					}
				}

				if (columnObj.filter_type === "range_number_slider") {
					min_val = findMinInArray(options, ignore_char);
					max_val = findMaxInArray(options, ignore_char);
				}


				if (filter_container_id === undefined) {
					//Can't show filter inside a column for a hidden one (place it outside using filter_container_id) 
					if (oTable.fnSettings().aoColumns[column_number].bVisible === false) {
						//console.log('Yadcf warning: Can\'t show filter inside a column N#' + column_number + ' for a hidden one (place it outside using filter_container_id)');
						continue;
					}
					col_num_visible = column_number;

					for (col_num_visible_iter = 0; col_num_visible_iter < oTable.fnSettings().aoColumns.length && col_num_visible_iter < column_number; col_num_visible_iter++) {
						if (oTable.fnSettings().aoColumns[col_num_visible_iter].bVisible === false) {
							col_num_visible--;
						}
					}
					filter_selector_string = table_selector + " thead th:eq(" + col_num_visible + ")";
					$filter_selector = $(filter_selector_string).find(".yadcf-filter");
				} else {
					if ($("#" + filter_container_id).length === 0) {
						alert("Filter container could not be found.");
						return;
					}
					filter_selector_string = "#" + filter_container_id;
					$filter_selector = $(filter_selector_string).find(".yadcf-filter");
				}

				table_selector_jq_friendly = yadcf.generateTableSelectorJQFriendly(table_selector);

				if (columnObj.filter_type === "select" || columnObj.filter_type === "auto_complete" || columnObj.filter_type === "multi_select" || columnObj.filter_type === "custom_func") {
					if (sort_as === "alpha") {
						if (sort_order === "asc") {
							options.sort();
						} else if (sort_order === "desc") {
							options.sort();
							options.reverse();
						}
					} else if (sort_as === "num") {
						if (sort_order === "asc") {
							options.sort(sortNumAsc);
						} else if (sort_order === "desc") {
							options.sort(sortNumDesc);
						}
					}
				}

				if (columnObj.filter_type === "select" || columnObj.filter_type === 'custom_func') {
					options_tmp = "<option value=\"" + "-1" + "\">" + filter_default_label + "</option>";
					if (typeof options[0] === 'object') {
						for (ii = 0; ii < options.length; ii++) {
							options_tmp += "<option value=\"" + options[ii].value + "\">" + options[ii].label + "</option>";
						}
					} else {
						for (ii = 0; ii < options.length; ii++) {
							options_tmp += "<option value=\"" + options[ii] + "\">" + options[ii] + "</option>";
						}
					}
					options = options_tmp;

				} else if (columnObj.filter_type === "multi_select") {
					if (columnObj.select_type === undefined) {
						options_tmp = "<option data-placeholder=\"true\" value=\"" + "-1" + "\">" + filter_default_label + "</option>";
					} else {
						options_tmp = "";
					}
					if (typeof options[0] === 'object') {
						for (ii = 0; ii < options.length; ii++) {
							options_tmp += "<option value=\"" + options[ii].value + "\">" + options[ii].label + "</option>";
						}
					} else {
						for (ii = 0; ii < options.length; ii++) {
							options_tmp += "<option value=\"" + options[ii] + "\">" + options[ii] + "</option>";
						}
					}
					options = options_tmp;
				}

				if ($filter_selector.length === 1) {
					if (columnObj.filter_type === "select" || columnObj.filter_type === "multi_select") {

						$filter_selector.empty();
						$filter_selector.append(options);
						if (oTable.fnSettings().aoPreSearchCols[column_number].sSearch !== '') {
							tmpStr = oTable.fnSettings().aoPreSearchCols[column_number].sSearch;
							if (columnObj.filter_type === "select") {
								tmpStr = yadcfParseMatchFilter(tmpStr, getOptions(oTable.selector)[column_number].filter_match_mode);
								$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val(tmpStr).addClass("inuse");
							} else if (columnObj.filter_type === "multi_select") {
								tmpStr = yadcfParseMatchFilterMultiSelect(tmpStr, getOptions(oTable.selector)[column_number].filter_match_mode);
								tmpStr = tmpStr.replace(/\\/g, "");
								tmpStr = tmpStr.split("|");
								$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val(tmpStr);
							}
						}
						if (columnObj.select_type !== undefined && columnObj.select_type === 'chosen') {
							$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).trigger("chosen:updated");
						} else if (columnObj.select_type !== undefined && columnObj.select_type === 'select2') {
							$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).select2("destroy").select2(columnObj.select_type_options);
						}
					} else if (columnObj.filter_type === "auto_complete") {
						$(document).data("yadcf-filter-" + table_selector_jq_friendly + "-" + column_number, options);
					}
				} else {
					if (filter_container_id === undefined) {
						if ($(filter_selector_string + " div.DataTables_sort_wrapper").length > 0) {
							$(filter_selector_string + " div.DataTables_sort_wrapper").css("display", "inline-block");
						}
					} else {
						if ($("#yadcf-filter-wrapper-" + filter_container_id).length === 0) {
							$("#" + filter_container_id).append("<div id=\"yadcf-filter-wrapper-" + filter_container_id + "\"></div>");
						}
						filter_selector_string = "#yadcf-filter-wrapper-" + filter_container_id;
					}

					if (columnObj.filter_type === "select" || columnObj.filter_type === 'custom_func') {

						//add a wrapper to hold both filter and reset button
						$(filter_selector_string).append("<div id=\"yadcf-filter-wrapper-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter-wrapper\"></div>");
						filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper";

						if (columnObj.filter_type === "select") {
							$(filter_selector_string).append("<select id=\"yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter\" " +
								"onchange=\"yadcf.doFilter(this, '" + table_selector_jq_friendly + "', " + column_number + ", '" + filter_match_mode + "')\" onclick='yadcf.stopPropagation(event);'>" + options + "</select>");
							if (filter_reset_button_text !== false) {
								$(filter_selector_string).find(".yadcf-filter").after("<input value=\"" + filter_reset_button_text + "\" type=\"button\" " +
									"onclick=\"yadcf.stopPropagation(event);yadcf.doFilter('clear', '" + table_selector_jq_friendly + "', " + column_number + "); return false;\" class=\"yadcf-filter-reset-button\">");
							}
						} else {
							$(filter_selector_string).append("<select id=\"yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter\" " +
									"onchange=\"yadcf.doFilterCustomDateFunc(this, '" + table_selector_jq_friendly + "', " + column_number + ")\" onclick='yadcf.stopPropagation(event);'>" + options + "</select>");
							if (filter_reset_button_text !== false) {
								$(filter_selector_string).find(".yadcf-filter").after("<input value=\"" + filter_reset_button_text + "\" type=\"button\" " +
									"onclick=\"yadcf.stopPropagation(event);yadcf.doFilterCustomDateFunc('clear', '" + table_selector_jq_friendly + "', " + column_number + "); return false;\" class=\"yadcf-filter-reset-button\">");
							}
							if (oTable.fnSettings().oFeatures.bServerSide !== true) {
								addCustomFunctionFilterCapability(table_selector_jq_friendly, "yadcf-filter-" + table_selector_jq_friendly + "-" + column_number, column_number);
							}
						}

						if (oTable.fnSettings().aoPreSearchCols[column_number].sSearch !== '') {
							tmpStr = oTable.fnSettings().aoPreSearchCols[column_number].sSearch;
							tmpStr = yadcfParseMatchFilter(tmpStr, getOptions(oTable.selector)[column_number].filter_match_mode);
							$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val(tmpStr).addClass("inuse");
						}

						if (columnObj.select_type !== undefined && columnObj.select_type === 'chosen') {
							$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).chosen(columnObj.select_type_options);
							$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).next().attr("onclick", "yadcf.stopPropagation(event);");
						} else if (columnObj.select_type !== undefined && columnObj.select_type === 'select2') {
							$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).select2(columnObj.select_type_options);
						}

					} else if (columnObj.filter_type === "multi_select") {

						//add a wrapper to hold both filter and reset button
						$(filter_selector_string).append("<div id=\"yadcf-filter-wrapper-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter-wrapper\"></div>");
						filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper";

						$(filter_selector_string).append("<select multiple data-placeholder=\"" + filter_default_label + "\" id=\"yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter\" " +
							"onchange=\"yadcf.doFilterMultiSelect(this, '" + table_selector_jq_friendly + "', " + column_number + ", '" + filter_match_mode + "')\" onclick='yadcf.stopPropagation(event);'>" + options + "</select>");

						if (filter_reset_button_text !== false) {
							$(filter_selector_string).find(".yadcf-filter").after("<input value=\"" + filter_reset_button_text + "\" type=\"button\" " +
								"onclick=\"yadcf.stopPropagation(event);yadcf.doFilter('clear', '" + table_selector_jq_friendly + "', " + column_number + "); return false;\" class=\"yadcf-filter-reset-button\">");
						}

						if (oTable.fnSettings().aoPreSearchCols[column_number].sSearch !== '') {
							tmpStr = oTable.fnSettings().aoPreSearchCols[column_number].sSearch;
							tmpStr = yadcfParseMatchFilterMultiSelect(tmpStr, getOptions(oTable.selector)[column_number].filter_match_mode);
							tmpStr = tmpStr.replace(/\\/g, "");
							tmpStr = tmpStr.split("|");
							$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val(tmpStr);
						}

						if (columnObj.filter_container_id === undefined && columnObj.select_type_options.width === undefined) {
							columnObj.select_type_options = $.extend(columnObj.select_type_options, {width: $(filter_selector_string).closest("th").width() + "px"});
						}
						if (columnObj.select_type !== undefined && columnObj.select_type === 'chosen') {
							$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).chosen(columnObj.select_type_options);
							$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).next().attr("onclick", "yadcf.stopPropagation(event);");
						} else if (columnObj.select_type !== undefined && columnObj.select_type === 'select2') {
							$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).select2(columnObj.select_type_options);
						}

					} else if (columnObj.filter_type === "auto_complete") {

						//add a wrapper to hold both filter and reset button
						$(filter_selector_string).append("<div id=\"yadcf-filter-wrapper-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter-wrapper\"></div>");
						filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper";

						$(filter_selector_string).append("<input id=\"yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter\" onclick='yadcf.stopPropagation(event);"
							+ "' placeholder='" + filter_default_label + "'" + " filter_match_mode='" + filter_match_mode + "'" + " onkeyup=\"yadcf.autocompleteKeyUP('" + table_selector_jq_friendly + "',event);\"></input>");
						$(document).data("yadcf-filter-" + table_selector_jq_friendly + "-" + column_number, options);

						if (filter_reset_button_text !== false) {
							$(filter_selector_string).find(".yadcf-filter").after("<input value=\"" + filter_reset_button_text + "\" type=\"button\" " +
								"onclick=\"yadcf.stopPropagation(event);yadcf.doFilterAutocomplete('clear', '" + table_selector_jq_friendly + "', " + column_number + "); return false;\" class=\"yadcf-filter-reset-button\">");
						}

					} else if (columnObj.filter_type === "text") {

						//add a wrapper to hold both filter and reset button
						$(filter_selector_string).append("<div id=\"yadcf-filter-wrapper-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter-wrapper\"></div>");
						filter_selector_string = filter_selector_string + " div.yadcf-filter-wrapper";

						$(filter_selector_string).append("<input type=\"text\" id=\"yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "\" class=\"yadcf-filter\" onclick='yadcf.stopPropagation(event);"
							+ "' placeholder='" + filter_default_label + "'" + " filter_match_mode='" + filter_match_mode + "'" + " onkeyup=\"yadcf.textKeyUP('" + table_selector_jq_friendly + "',event);\"></input>");

						if (filter_reset_button_text !== false) {
							$(filter_selector_string).find(".yadcf-filter").after("<input value=\"" + filter_reset_button_text + "\" type=\"button\" " + " id=\"yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "-reset\"" +
								"onclick=\"yadcf.stopPropagation(event);yadcf.textKeyUP('" + table_selector_jq_friendly + "', event, 'clear'); return false;\" class=\"yadcf-filter-reset-button\">");
						}

						if (oTable.fnSettings().aoPreSearchCols[column_number].sSearch !== '') {
							tmpStr = oTable.fnSettings().aoPreSearchCols[column_number].sSearch;
							tmpStr = yadcfParseMatchFilter(tmpStr, getOptions(oTable.selector)[column_number].filter_match_mode);
							$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val(tmpStr).addClass("inuse");
						}

					} else if (columnObj.filter_type === "date") {

						addDateFilter(filter_selector_string, table_selector_jq_friendly, column_number, filter_reset_button_text, filter_default_label, date_format);

					} else if (columnObj.filter_type === "range_number") {

						addRangeNumberFilter(filter_selector_string, table_selector_jq_friendly, column_number, filter_reset_button_text, filter_default_label, ignore_char);

					} else if (columnObj.filter_type === "range_number_slider") {

						addRangeNumberSliderFilter(filter_selector_string, table_selector_jq_friendly, column_number, filter_reset_button_text, min_val, max_val, ignore_char);

					} else if (columnObj.filter_type === "range_date") {

						addRangeDateFilter(filter_selector_string, table_selector_jq_friendly, column_number, filter_reset_button_text, filter_default_label, date_format);

					}
				}

				if ($(document).data("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "_val") !== undefined && $(document).data("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "_val") !== "-1") {
					$(filter_selector_string).find(".yadcf-filter").val($(document).data("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "_val"));
				}
				if (columnObj.filter_type === "auto_complete") {
					$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).autocomplete({
						source: $(document).data("yadcf-filter-" + table_selector_jq_friendly + "-" + column_number),
						select: autocompleteSelect
					});
					if (oTable.fnSettings().aoPreSearchCols[column_number].sSearch !== '') {
						tmpStr = oTable.fnSettings().aoPreSearchCols[column_number].sSearch;
						tmpStr = yadcfParseMatchFilter(tmpStr, getOptions(oTable.selector)[column_number].filter_match_mode);
						$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val(tmpStr).addClass("inuse");
					}
				}
			}
		}
		if (exFilterColumnQueue.length > 0) {
			(exFilterColumnQueue.shift())();
		}
	}

	function endsWith(str, suffix) {
		return str.indexOf(suffix, str.length - suffix.length) !== -1;
	}

	function rangeClear(table_selector_jq_friendly, event) {
		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		var oTable = oTables[table_selector_jq_friendly],
			yadcfState,
			column_number;

		column_number = parseInt($(event.target).parent().attr("id").replace('yadcf-filter-wrapper-' + table_selector_jq_friendly + '-', ''), 10);

		$(event.target).parent().find(".yadcf-filter-range").val("");
		if ($(event.target).parent().find(".yadcf-filter-range-number").length > 0) {
			$($(event.target).parent().find(".yadcf-filter-range")[0]).focus();
		}

		if (oTable.fnSettings().oFeatures.bServerSide !== true) {
			oTable.fnDraw();
		} else {
			oTable.fnFilter('-yadcf_delim-', column_number);
		}

		if (!oTable.fnSettings().oLoadedState) {
			oTable.fnSettings().oLoadedState = {};
			oTable.fnSettings().oApi._fnSaveState(oTable.fnSettings());
		}
		if (oTable.fnSettings().oFeatures.bStateSave === true) {
			if (oTable.fnSettings().oLoadedState.yadcfState !== undefined && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly] !== undefined) {
				oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number] =
					{
						'from' : "",
						'to' : ""
					};
			} else {
				yadcfState = {};
				yadcfState[table_selector_jq_friendly] = [];
				yadcfState[table_selector_jq_friendly][column_number] = {
					'from' : "",
					'to' : ""
				};
				oTable.fnSettings().oLoadedState.yadcfState = yadcfState;
			}
			oTable.fnSettings().oApi._fnSaveState(oTable.fnSettings());
		}
		resetIApiIndex();

		$(event.target).parent().find(".yadcf-filter-range").removeClass("inuse");

		return;
	}

	function rangeNumberSliderClear(table_selector_jq_friendly, event) {
		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		var oTable = oTables[table_selector_jq_friendly],
			min_val,
			max_val;

		min_val = +$($(event.target).parent().find(".yadcf-filter-range-number-slider-min-tip-hidden")).text();
		max_val = +$($(event.target).parent().find(".yadcf-filter-range-number-slider-max-tip-hidden")).text();

		$("#" + $(event.target).prev().find(".yadcf-filter-range-number-slider").attr("id")).slider("option", "values", [min_val, max_val]);

		$($(event.target).prev().find(".ui-slider-handle")[0]).attr("tabindex", -1).focus();

		$($(event.target).prev().find(".ui-slider-handle")[0]).removeClass("inuse");
		$($(event.target).prev().find(".ui-slider-handle")[1]).removeClass("inuse");
		$(event.target).prev().find(".ui-slider-range").removeClass("inuse");

		oTable.fnDraw();
		resetIApiIndex();

		return;
	}

	function dateKeyUP(table_selector_jq_friendly, date_format, event) {
		var oTable,
			date,
			dateId,
			column_number;

		dateId = event.target.id;
		date = document.getElementById(dateId).value;
		try {
			if (date.length === (date_format.length + 2)) {
				date = (date !== "") ? $.datepicker.parseDate(date_format, date) : date;
			}
		} catch (err1) {}

		if (((date instanceof Date) || date === "")) {

			$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
			oTable = oTables[table_selector_jq_friendly];
			column_number = parseInt(dateId.replace("yadcf-filter-" + table_selector_jq_friendly + "-", ""), 10);

			if (date instanceof Date) {
				$("#" + dateId).addClass('inuse');
				oTable.fnFilter(document.getElementById(dateId).value, column_number);
			} else {
				$("#" + dateId).removeClass('inuse');
			}
			if ($.trim(event.target.value) === '' && $(event.target).hasClass('inuse')) {
				$('#' + event.target.id).removeClass('inuse');
				oTable.fnFilter('', column_number);
			}
			resetIApiIndex();
		} else if ($(event.target).hasClass('inuse')) {

			$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
			oTable = oTables[table_selector_jq_friendly];
			column_number = parseInt(dateId.replace("yadcf-filter-" + table_selector_jq_friendly + "-", ""), 10);

			$("#" + dateId).removeClass('inuse');

			oTable.fnFilter('', column_number);
			resetIApiIndex();
		}
	}

	function rangeDateKeyUP(table_selector_jq_friendly, date_format, event) {
		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		var oTable = oTables[table_selector_jq_friendly],
			min,
			min_server,
			max,
			max_server,
			fromId,
			toId,
			column_number,
			options,
			keyUp;

		column_number = parseInt($(event.target).attr("id").replace('-from-date-', '').replace('-to-date-', '').replace('yadcf-filter-' + table_selector_jq_friendly, ''), 10);
		options = getOptions(oTable.selector)[column_number];

		keyUp = function () {
			if (event.target.id.indexOf("-from-") !== -1) {
				fromId = event.target.id;
				toId = event.target.id.replace("-from-", "-to-");

				min = document.getElementById(fromId).value;
				max = document.getElementById(toId).value;
			} else {
				toId = event.target.id;
				fromId = event.target.id.replace("-to-", "-from-");

				max =   document.getElementById(toId).value;
				min = document.getElementById(fromId).value;
			}

			try {
				if (min.length === (date_format.length + 2)) {
					min = (min !== "") ? $.datepicker.parseDate(date_format, min) : min;
				}
			} catch (err1) {}
			try {
				if (max.length === (date_format.length + 2)) {
					max = (max !== "") ? $.datepicker.parseDate(date_format, max) : max;
				}
			} catch (err2) {}

			if (((max instanceof Date) && (min instanceof Date) && (max >= min)) || min === "" || max === "") {

				if (oTable.fnSettings().oFeatures.bServerSide !== true) {
					oTable.fnDraw();
				} else {
					if (min instanceof Date) {
						min_server = min.getTime();
					} else {
						min_server = min;
					}
					if (max instanceof Date) {
						max_server = max.getTime();
					} else {
						max_server = max;
					}
					oTable.fnFilter(min_server + '-yadcf_delim-' + max_server, column_number);
				}

				if (min instanceof Date) {
					$("#" + fromId).addClass("inuse");
				} else {
					$("#" + fromId).removeClass("inuse");
				}
				if (max instanceof Date) {
					$("#" + toId).addClass("inuse");
				} else {
					$("#" + toId).removeClass("inuse");
				}

				if ($.trim(event.target.value) === "" && $(event.target).hasClass("inuse")) {
					$("#" + event.target.id).removeClass("inuse");
				}

			}
			resetIApiIndex();
		};

		if (options.filter_delay === undefined) {
			keyUp(table_selector_jq_friendly, event);
		} else {
			yadcfDelay(function () {
				keyUp(table_selector_jq_friendly, event);
			}, options.filter_delay);
		}
	}

	function rangeNumberKeyUP(table_selector_jq_friendly, event) {
		$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
		var oTable = oTables[table_selector_jq_friendly],
			min,
			max,
			fromId,
			toId,
			yadcfState,
			column_number,
			options,
			keyUp;

		column_number = parseInt($(event.target).attr("id").replace('-from-', '').replace('-to-', '').replace('yadcf-filter-' + table_selector_jq_friendly, ''), 10);
		options = getOptions(oTable.selector)[column_number];

		keyUp = function () {

			if (event.target.id.indexOf("-from-") !== -1) {
				fromId = event.target.id;
				toId = event.target.id.replace("-from-", "-to-");

				min = document.getElementById(fromId).value;
				max = document.getElementById(toId).value;
			} else {
				toId = event.target.id;
				fromId = event.target.id.replace("-to-", "-from-");

				max =   document.getElementById(toId).value;
				min = document.getElementById(fromId).value;
			}

			min = (min !== "") ? (+min) : min;
			max = (max !== "") ? (+max) : max;

			if ((!isNaN(max) && !isNaN(min) && (max >= min)) || min === "" || max === "") {

				if (oTable.fnSettings().oFeatures.bServerSide !== true) {
					oTable.fnDraw();
				} else {
					oTable.fnFilter(min + '-yadcf_delim-' + max, column_number);
				}
				if (document.getElementById(fromId).value !== "") {
					$("#" + fromId).addClass("inuse");
				}
				if (document.getElementById(toId).value !== "") {
					$("#" + toId).addClass("inuse");
				}

				if ($.trim(event.target.value) === "" && $(event.target).hasClass("inuse")) {
					$("#" + event.target.id).removeClass("inuse");
				}
				if (!oTable.fnSettings().oLoadedState) {
					oTable.fnSettings().oLoadedState = {};
					oTable.fnSettings().oApi._fnSaveState(oTable.fnSettings());
				}
				if (oTable.fnSettings().oFeatures.bStateSave === true) {
					if (oTable.fnSettings().oLoadedState.yadcfState !== undefined && oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly] !== undefined) {
						oTable.fnSettings().oLoadedState.yadcfState[table_selector_jq_friendly][column_number] =
							{
								'from' : min,
								'to' : max
							};
					} else {
						yadcfState = {};
						yadcfState[table_selector_jq_friendly] = [];
						yadcfState[table_selector_jq_friendly][column_number] = {
							'from' : min,
							'to' : max
						};
						oTable.fnSettings().oLoadedState.yadcfState = yadcfState;
					}
					oTable.fnSettings().oApi._fnSaveState(oTable.fnSettings());
				}
			}
			resetIApiIndex();
		};

		if (options.filter_delay === undefined) {
			keyUp();
		} else {
			yadcfDelay(function () {
				keyUp();
			}, options.filter_delay);
		}
	}

	function textKeyUP(table_selector_jq_friendly, event, clear) {

		var column_number = parseInt($(event.target).attr("id").replace("yadcf-filter-" + table_selector_jq_friendly + "-", "").replace('-reset', ''), 10),
			oTable = oTables[table_selector_jq_friendly],
			options = getOptions(oTable.selector)[column_number],

			keyUp = function (table_selector_jq_friendly, event, clear) {
				$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];

				if (clear !== undefined || event.target.value === '') {
					$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).val("").focus();
					$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).removeClass("inuse");
					oTable.fnFilter("", column_number);
					resetIApiIndex();
					return;
				}

				$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).addClass("inuse");

				yadcfMatchFilter(oTable, event.target.value, options.filter_match_mode, column_number);

				resetIApiIndex();
			};

		if (options.filter_delay === undefined) {
			keyUp(table_selector_jq_friendly, event, clear);
		} else {
			yadcfDelay(function () {
				keyUp(table_selector_jq_friendly, event, clear);
			}, options.filter_delay);
		}
	}

	function autocompleteKeyUP(table_selector_jq_friendly, event) {
		if (event.target.value === "" && event.keyCode === 8 && $(event.target).hasClass("inuse")) {
			$.fn.dataTableExt.iApiIndex = oTablesIndex[table_selector_jq_friendly];
			var oTable = oTables[table_selector_jq_friendly],
				column_number = parseInt($(event.target).attr("id").replace("yadcf-filter-" + table_selector_jq_friendly + "-", ""), 10);

			$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).removeClass("inuse");
			$(document).removeData("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number + "_val");
			oTable.fnFilter("", column_number);
			resetIApiIndex();
		}
	}

	//taken and modified from DataTables 1.10.0-beta.2 source 
	function yadcfVersionCheck(version) {
		var aThis = $.fn.dataTable.ext.sVersion.split('.'),
			aThat = version.split('.'),
			iThis,
			iThat,
			i,
			iLen;

		for (i = 0, iLen = aThat.length; i < iLen; i++) {
			iThis = parseInt(aThis[i], 10) || 0;
			iThat = parseInt(aThat[i], 10) || 0;

			// Parts are the same, keep comparing
			if (iThis === iThat) {
				continue;
			}

			// Parts are different, return immediately
			return iThis > iThat;
		}

		return true;
	}

	function initAndBindTable(oTable, table_selector, index) {

		var table_selector_jq_friendly = yadcf.generateTableSelectorJQFriendly(table_selector),
			table_selector_tmp;
        oTables[table_selector_jq_friendly] = oTable;
		oTablesIndex[table_selector_jq_friendly] = index;

        if (oTable.fnSettings().sAjaxSource === null && oTable.fnSettings().ajax === null) {
			table_selector_tmp = table_selector;
			if (table_selector.indexOf(":eq") !== -1) {
				table_selector_tmp = table_selector.substring(0, table_selector.lastIndexOf(":eq"));
			}
			appendFilters(oTable, yadcf.getOptions(table_selector_tmp), table_selector);
        } else {
			appendFilters(oTable, yadcf.getOptions(table_selector), table_selector);
			if (yadcfVersionCheck('1.10')) {
				$(document).off('draw.dt').on('draw.dt', oTable.selector, function (event, ui) {
					appendFilters(oTable, yadcf.getOptions(ui.oInstance.selector), ui.oInstance.selector);
				});
				$(document).off('xhr.dt').on('xhr.dt', function (e, settings, json) {
					var col_num;
					for (col_num in yadcf.getOptions(settings.oInstance.selector)) {
						if (yadcf.getOptions(settings.oInstance.selector).hasOwnProperty(col_num)) {
							if (json['yadcf_data_' + col_num] !== undefined) {
								yadcf.getOptions(settings.oInstance.selector)[col_num].data = json['yadcf_data_' + col_num];
							}
						}
					}
				});
				$(document).off('column-visibility.dt').on('column-visibility.dt', function (e, settings, col_num, state) {
					var obj = {};
					if (state === true) {
						obj[col_num] = yadcf.getOptions(settings.oInstance.selector)[col_num];
						appendFilters(oTables[yadcf.generateTableSelectorJQFriendly(settings.oInstance.selector)],
							obj,
							settings.oInstance.selector);
					}
				});
			} else {
				$(document).off('draw').on('draw', oTable.selector, function (event, ui) {
					appendFilters(oTable, yadcf.getOptions(ui.oInstance.selector), ui.oInstance.selector);
				});
			}
        }
		if (oTable.fnSettings().oFeatures.bStateSave === true) {
			if (yadcfVersionCheck('1.10')) {
				$(oTable.selector).off('stateSaveParams.dt').on('stateSaveParams.dt', function (e, settings, data) {
					if (settings.oLoadedState && settings.oLoadedState.yadcfState !== undefined) {
						data.yadcfState = settings.oLoadedState.yadcfState;
					} else {
						data.naruto = 'kurama';
					}
				});
			} else {
				$(oTable.selector).off('stateSaveParams').on('stateSaveParams', function (e, settings, data) {
					if (settings.oLoadedState && settings.oLoadedState.yadcfState !== undefined) {
						data.yadcfState = settings.oLoadedState.yadcfState;
					} else {
						data.naruto = 'kurama';
					}
				});
			}
			//when using DOM source
			if (oTable.fnSettings().sAjaxSource === null && oTable.fnSettings().ajax === null) {
				//we need to make sure that the yadcf state will be saved after page reload
				oTable.fnSettings().oApi._fnSaveState(oTable.fnSettings());
				//redraw the table in order to apply the filters
				oTable.fnDraw();
			}
		}
	}

    $.fn.yadcf = function (options_arg) {

		if ($(this.selector).length === 1) {
			setOptions(this.selector, options_arg);
			initAndBindTable(this, this.selector, 0);
		} else {
			var i = 0,
				selector;
			for (i; i < $(this.selector).length; i++) {
				$.fn.dataTableExt.iApiIndex = i;
				selector = this.selector + ":eq(" + i + ")";
				setOptions(this.selector, options_arg);
				initAndBindTable(this, selector, i);
			}
			$.fn.dataTableExt.iApiIndex = 0;
		}
        return this;
    };

	function init(oTable, options_arg) {
		var instance = oTable.settings()[0].oInstance,
			i = 0,
			selector;
		if ($(instance.selector).length === 1) {
			setOptions(instance.selector, options_arg);
			initAndBindTable(instance, instance.selector, 0);
		} else {
			for (i; i < $(instance.selector).length; i++) {
				$.fn.dataTableExt.iApiIndex = i;
				selector = instance.selector + ":eq(" + i + ")";
				setOptions(instance.selector, options_arg);
				initAndBindTable(instance, selector, i);
			}
			$.fn.dataTableExt.iApiIndex = 0;
		}
	}

    function stopPropagation(evt) {
		if (evt.stopPropagation !== undefined) {
			evt.stopPropagation();
		} else {
			evt.cancelBubble = true;
		}
	}

	//--------------------------------------------------------
	function exInternalFilterColumnAJAXQueue(table_arg, col_filter_arr) {
		return function () {
			exFilterColumn(table_arg, col_filter_arr, true);
		};
	}

	function exFilterColumn(table_arg, col_filter_arr, ajaxSource) {
		var table_selector_jq_friendly,
			j,
			tmpStr,
			column_number,
			filter_value,
			fromId,
			toId,
			sliderId,
			optionsObj;
		//check if the table arg is from new datatables API (capital "D")
		if (table_arg.settings !== undefined) {
			table_arg = table_arg.settings()[0].oInstance;
		}
		table_selector_jq_friendly = yadcf.generateTableSelectorJQFriendly(table_arg.selector);
		if ((table_arg.fnSettings().sAjaxSource === null && table_arg.fnSettings().ajax === null) || ajaxSource === true) {
			for (j = 0; j < col_filter_arr.length; j++) {
				column_number = col_filter_arr[j][0];
				optionsObj = getOptions(table_arg.selector)[column_number];
				filter_value = col_filter_arr[j][1];

				switch (optionsObj.filter_type) {
				case 'select':
				case 'auto_complete':
				case 'text':
				case 'date':
					$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val(filter_value).addClass('inuse');
					tmpStr = yadcfMatchFilterString(table_arg, column_number, filter_value, optionsObj.filter_match_mode, false);
					table_arg.fnSettings().aoPreSearchCols[column_number].sSearch = tmpStr;
					break;
				case 'multi_select':
					$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val(filter_value);
					tmpStr = yadcfMatchFilterString(table_arg, column_number, filter_value, optionsObj.filter_match_mode, true);
					table_arg.fnSettings().aoPreSearchCols[column_number].sSearch = tmpStr;
					if (optionsObj.select_type !== undefined) {
						if (optionsObj.select_type === 'chosen') {
							$('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).trigger('chosen:updated');
						} else if (optionsObj.select_type === 'select2') {
							$("#yadcf-filter-" + table_selector_jq_friendly + "-" + column_number).select2("destroy").select2(optionsObj.select_type_options);
						}
					}
					break;
				case 'range_date':
					fromId = 'yadcf-filter-' + table_selector_jq_friendly + '-from-date-' + column_number;
					toId = 'yadcf-filter-' + table_selector_jq_friendly + '-to-date-' + column_number;
					if (filter_value.from !== '') {
						$('#' + fromId).val(filter_value.from);
						$('#' + fromId).addClass('inuse');
					}
					if (filter_value.to !== '') {
						$('#' + toId).val(filter_value.to);
						$('#' + toId).addClass('inuse');
					}
					break;
				case 'range_number':
					fromId = 'yadcf-filter-' + table_selector_jq_friendly + '-from-' + column_number;
					toId = 'yadcf-filter-' + table_selector_jq_friendly + '-to-' + column_number;
					if (filter_value.from !== '') {
						$('#' + fromId).val(filter_value.from);
						$('#' + fromId).addClass('inuse');
					}
					if (filter_value.to !== '') {
						$('#' + toId).val(filter_value.to);
						$('#' + toId).addClass('inuse');
					}
					break;
				case 'range_number_slider':
					sliderId = 'yadcf-filter-' + table_selector_jq_friendly + '-slider-' + column_number;
					fromId = 'yadcf-filter-' + table_selector_jq_friendly + '-min_tip-' + column_number;
					toId = 'yadcf-filter-' + table_selector_jq_friendly + '-max_tip-' + column_number;
					if (filter_value.from !== '') {
						$('#' + fromId).text(filter_value.from);
						$('#' + fromId).parent().addClass('inuse');
						$('#' + fromId).parent().parent().find('ui-slider-range').addClass('inuse');
						$('#' + sliderId).slider('values', 0, filter_value.from);
					}
					if (filter_value.to !== '') {
						$('#' + toId).text(filter_value.to);
						$('#' + toId).parent().addClass('inuse');
						$('#' + toId).parent().parent().find('.ui-slider-range').addClass('inuse');
						$('#' + sliderId).slider('values', 1, filter_value.to);
					}
					break;
				}

			}
			if (table_arg.fnSettings().oFeatures.bServerSide !== true) {
				table_arg.fnDraw();
			} else {
				switch (optionsObj.filter_type) {
				case 'select':
				case 'auto_complete':
				case 'text':
				case 'date':
					setTimeout(function () {
						table_arg.fnFilter(filter_value, column_number);
					}, 10);
					break;
				default:
					console.log('exFilterColumn is not supported for ' + optionsObj.filter_type);
					break;
				}
			}
        } else {
			exFilterColumnQueue.push(exInternalFilterColumnAJAXQueue(table_arg, col_filter_arr));
        }
	}

	function exGetColumnFilterVal(table_arg, column_number) {
		var retVal,
			fromId,
			toId,
			table_selector_jq_friendly,
			optionsObj = getOptions(table_arg.selector)[column_number];

		table_selector_jq_friendly = yadcf.generateTableSelectorJQFriendly(table_arg.selector);

		switch (optionsObj.filter_type) {
		case 'select':
			retVal = $('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val();
			if (retVal === '-1') {
				retVal = '';
			}
			break;
		case 'auto_complete':
		case 'text':
		case 'date':
			retVal = $('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val();
			break;
		case 'multi_select':
			retVal = $('#yadcf-filter-' + table_selector_jq_friendly + '-' + column_number).val();
			if (retVal === null) {
				retVal = '';
			}
			break;
		case 'range_date':
			retVal = {};
			fromId = 'yadcf-filter-' + table_selector_jq_friendly + '-from-date-' + column_number;
			toId = 'yadcf-filter-' + table_selector_jq_friendly + '-to-date-' + column_number;

			retVal.from = $('#' + fromId).val();
			retVal.to = $('#' + toId).val();
			break;
		case 'range_number':
			retVal = {};
			fromId = 'yadcf-filter-' + table_selector_jq_friendly + '-from-' + column_number;
			toId = 'yadcf-filter-' + table_selector_jq_friendly + '-to-' + column_number;

			retVal.from = $('#' + fromId).val();
			retVal.to = $('#' + toId).val();
			break;
		case 'range_number_slider':
			retVal = {};
			fromId = 'yadcf-filter-' + table_selector_jq_friendly + '-min_tip-' + column_number;
			toId = 'yadcf-filter-' + table_selector_jq_friendly + '-max_tip-' + column_number;

			retVal.from = $('#' + fromId).text();
			retVal.to = $('#' + toId).text();
			break;
		}
		return retVal;
	}
    return {
		init : init,
		doFilter : doFilter,
		doFilterMultiSelect : doFilterMultiSelect,
		doFilterAutocomplete : doFilterAutocomplete,
		autocompleteKeyUP : autocompleteKeyUP,
		getOptions : getOptions,
		rangeNumberKeyUP : rangeNumberKeyUP,
		rangeDateKeyUP : rangeDateKeyUP,
		rangeClear : rangeClear,
		rangeNumberSliderClear : rangeNumberSliderClear,
		stopPropagation : stopPropagation,
		generateTableSelectorJQFriendly : generateTableSelectorJQFriendly,
		exFilterColumn : exFilterColumn,
		exGetColumnFilterVal : exGetColumnFilterVal,
		dateKeyUP : dateKeyUP,
		dateSelectSingle : dateSelectSingle,
		textKeyUP : textKeyUP,
		doFilterCustomDateFunc : doFilterCustomDateFunc
    };

}(jQuery));
;/*! jQuery Validation Plugin - v1.13.0 - 7/1/2014
 * http://jqueryvalidation.org/
 * Copyright (c) 2014 Jörn Zaefferer; Licensed MIT */
!function(a){"function"==typeof define&&define.amd?define(["jquery"],a):a(jQuery)}(function(a){a.extend(a.fn,{validate:function(b){if(!this.length)return void(b&&b.debug&&window.console&&console.warn("Nothing selected, can't validate, returning nothing."));var c=a.data(this[0],"validator");return c?c:(this.attr("novalidate","novalidate"),c=new a.validator(b,this[0]),a.data(this[0],"validator",c),c.settings.onsubmit&&(this.validateDelegate(":submit","click",function(b){c.settings.submitHandler&&(c.submitButton=b.target),a(b.target).hasClass("cancel")&&(c.cancelSubmit=!0),void 0!==a(b.target).attr("formnovalidate")&&(c.cancelSubmit=!0)}),this.submit(function(b){function d(){var d;return c.settings.submitHandler?(c.submitButton&&(d=a("<input type='hidden'/>").attr("name",c.submitButton.name).val(a(c.submitButton).val()).appendTo(c.currentForm)),c.settings.submitHandler.call(c,c.currentForm,b),c.submitButton&&d.remove(),!1):!0}return c.settings.debug&&b.preventDefault(),c.cancelSubmit?(c.cancelSubmit=!1,d()):c.form()?c.pendingRequest?(c.formSubmitted=!0,!1):d():(c.focusInvalid(),!1)})),c)},valid:function(){var b,c;return a(this[0]).is("form")?b=this.validate().form():(b=!0,c=a(this[0].form).validate(),this.each(function(){b=c.element(this)&&b})),b},removeAttrs:function(b){var c={},d=this;return a.each(b.split(/\s/),function(a,b){c[b]=d.attr(b),d.removeAttr(b)}),c},rules:function(b,c){var d,e,f,g,h,i,j=this[0];if(b)switch(d=a.data(j.form,"validator").settings,e=d.rules,f=a.validator.staticRules(j),b){case"add":a.extend(f,a.validator.normalizeRule(c)),delete f.messages,e[j.name]=f,c.messages&&(d.messages[j.name]=a.extend(d.messages[j.name],c.messages));break;case"remove":return c?(i={},a.each(c.split(/\s/),function(b,c){i[c]=f[c],delete f[c],"required"===c&&a(j).removeAttr("aria-required")}),i):(delete e[j.name],f)}return g=a.validator.normalizeRules(a.extend({},a.validator.classRules(j),a.validator.attributeRules(j),a.validator.dataRules(j),a.validator.staticRules(j)),j),g.required&&(h=g.required,delete g.required,g=a.extend({required:h},g),a(j).attr("aria-required","true")),g.remote&&(h=g.remote,delete g.remote,g=a.extend(g,{remote:h})),g}}),a.extend(a.expr[":"],{blank:function(b){return!a.trim(""+a(b).val())},filled:function(b){return!!a.trim(""+a(b).val())},unchecked:function(b){return!a(b).prop("checked")}}),a.validator=function(b,c){this.settings=a.extend(!0,{},a.validator.defaults,b),this.currentForm=c,this.init()},a.validator.format=function(b,c){return 1===arguments.length?function(){var c=a.makeArray(arguments);return c.unshift(b),a.validator.format.apply(this,c)}:(arguments.length>2&&c.constructor!==Array&&(c=a.makeArray(arguments).slice(1)),c.constructor!==Array&&(c=[c]),a.each(c,function(a,c){b=b.replace(new RegExp("\\{"+a+"\\}","g"),function(){return c})}),b)},a.extend(a.validator,{defaults:{messages:{},groups:{},rules:{},errorClass:"error",validClass:"valid",errorElement:"label",focusInvalid:!0,errorContainer:a([]),errorLabelContainer:a([]),onsubmit:!0,ignore:":hidden",ignoreTitle:!1,onfocusin:function(a){this.lastActive=a,this.settings.focusCleanup&&!this.blockFocusCleanup&&(this.settings.unhighlight&&this.settings.unhighlight.call(this,a,this.settings.errorClass,this.settings.validClass),this.hideThese(this.errorsFor(a)))},onfocusout:function(a){this.checkable(a)||!(a.name in this.submitted)&&this.optional(a)||this.element(a)},onkeyup:function(a,b){(9!==b.which||""!==this.elementValue(a))&&(a.name in this.submitted||a===this.lastElement)&&this.element(a)},onclick:function(a){a.name in this.submitted?this.element(a):a.parentNode.name in this.submitted&&this.element(a.parentNode)},highlight:function(b,c,d){"radio"===b.type?this.findByName(b.name).addClass(c).removeClass(d):a(b).addClass(c).removeClass(d)},unhighlight:function(b,c,d){"radio"===b.type?this.findByName(b.name).removeClass(c).addClass(d):a(b).removeClass(c).addClass(d)}},setDefaults:function(b){a.extend(a.validator.defaults,b)},messages:{required:"This field is required.",remote:"Please fix this field.",email:"Please enter a valid email address.",url:"Please enter a valid URL.",date:"Please enter a valid date.",dateISO:"Please enter a valid date ( ISO ).",number:"Please enter a valid number.",digits:"Please enter only digits.",creditcard:"Please enter a valid credit card number.",equalTo:"Please enter the same value again.",maxlength:a.validator.format("Please enter no more than {0} characters."),minlength:a.validator.format("Please enter at least {0} characters."),rangelength:a.validator.format("Please enter a value between {0} and {1} characters long."),range:a.validator.format("Please enter a value between {0} and {1}."),max:a.validator.format("Please enter a value less than or equal to {0}."),min:a.validator.format("Please enter a value greater than or equal to {0}.")},autoCreateRanges:!1,prototype:{init:function(){function b(b){var c=a.data(this[0].form,"validator"),d="on"+b.type.replace(/^validate/,""),e=c.settings;e[d]&&!this.is(e.ignore)&&e[d].call(c,this[0],b)}this.labelContainer=a(this.settings.errorLabelContainer),this.errorContext=this.labelContainer.length&&this.labelContainer||a(this.currentForm),this.containers=a(this.settings.errorContainer).add(this.settings.errorLabelContainer),this.submitted={},this.valueCache={},this.pendingRequest=0,this.pending={},this.invalid={},this.reset();var c,d=this.groups={};a.each(this.settings.groups,function(b,c){"string"==typeof c&&(c=c.split(/\s/)),a.each(c,function(a,c){d[c]=b})}),c=this.settings.rules,a.each(c,function(b,d){c[b]=a.validator.normalizeRule(d)}),a(this.currentForm).validateDelegate(":text, [type='password'], [type='file'], select, textarea, [type='number'], [type='search'] ,[type='tel'], [type='url'], [type='email'], [type='datetime'], [type='date'], [type='month'], [type='week'], [type='time'], [type='datetime-local'], [type='range'], [type='color'], [type='radio'], [type='checkbox']","focusin focusout keyup",b).validateDelegate("select, option, [type='radio'], [type='checkbox']","click",b),this.settings.invalidHandler&&a(this.currentForm).bind("invalid-form.validate",this.settings.invalidHandler),a(this.currentForm).find("[required], [data-rule-required], .required").attr("aria-required","true")},form:function(){return this.checkForm(),a.extend(this.submitted,this.errorMap),this.invalid=a.extend({},this.errorMap),this.valid()||a(this.currentForm).triggerHandler("invalid-form",[this]),this.showErrors(),this.valid()},checkForm:function(){this.prepareForm();for(var a=0,b=this.currentElements=this.elements();b[a];a++)this.check(b[a]);return this.valid()},element:function(b){var c=this.clean(b),d=this.validationTargetFor(c),e=!0;return this.lastElement=d,void 0===d?delete this.invalid[c.name]:(this.prepareElement(d),this.currentElements=a(d),e=this.check(d)!==!1,e?delete this.invalid[d.name]:this.invalid[d.name]=!0),a(b).attr("aria-invalid",!e),this.numberOfInvalids()||(this.toHide=this.toHide.add(this.containers)),this.showErrors(),e},showErrors:function(b){if(b){a.extend(this.errorMap,b),this.errorList=[];for(var c in b)this.errorList.push({message:b[c],element:this.findByName(c)[0]});this.successList=a.grep(this.successList,function(a){return!(a.name in b)})}this.settings.showErrors?this.settings.showErrors.call(this,this.errorMap,this.errorList):this.defaultShowErrors()},resetForm:function(){a.fn.resetForm&&a(this.currentForm).resetForm(),this.submitted={},this.lastElement=null,this.prepareForm(),this.hideErrors(),this.elements().removeClass(this.settings.errorClass).removeData("previousValue").removeAttr("aria-invalid")},numberOfInvalids:function(){return this.objectLength(this.invalid)},objectLength:function(a){var b,c=0;for(b in a)c++;return c},hideErrors:function(){this.hideThese(this.toHide)},hideThese:function(a){a.not(this.containers).text(""),this.addWrapper(a).hide()},valid:function(){return 0===this.size()},size:function(){return this.errorList.length},focusInvalid:function(){if(this.settings.focusInvalid)try{a(this.findLastActive()||this.errorList.length&&this.errorList[0].element||[]).filter(":visible").focus().trigger("focusin")}catch(b){}},findLastActive:function(){var b=this.lastActive;return b&&1===a.grep(this.errorList,function(a){return a.element.name===b.name}).length&&b},elements:function(){var b=this,c={};return a(this.currentForm).find("input, select, textarea").not(":submit, :reset, :image, [disabled]").not(this.settings.ignore).filter(function(){return!this.name&&b.settings.debug&&window.console&&console.warn("%o has no name assigned",this),this.name in c||!b.objectLength(a(this).rules())?!1:(c[this.name]=!0,!0)})},clean:function(b){return a(b)[0]},errors:function(){var b=this.settings.errorClass.split(" ").join(".");return a(this.settings.errorElement+"."+b,this.errorContext)},reset:function(){this.successList=[],this.errorList=[],this.errorMap={},this.toShow=a([]),this.toHide=a([]),this.currentElements=a([])},prepareForm:function(){this.reset(),this.toHide=this.errors().add(this.containers)},prepareElement:function(a){this.reset(),this.toHide=this.errorsFor(a)},elementValue:function(b){var c,d=a(b),e=b.type;return"radio"===e||"checkbox"===e?a("input[name='"+b.name+"']:checked").val():"number"===e&&"undefined"!=typeof b.validity?b.validity.badInput?!1:d.val():(c=d.val(),"string"==typeof c?c.replace(/\r/g,""):c)},check:function(b){b=this.validationTargetFor(this.clean(b));var c,d,e,f=a(b).rules(),g=a.map(f,function(a,b){return b}).length,h=!1,i=this.elementValue(b);for(d in f){e={method:d,parameters:f[d]};try{if(c=a.validator.methods[d].call(this,i,b,e.parameters),"dependency-mismatch"===c&&1===g){h=!0;continue}if(h=!1,"pending"===c)return void(this.toHide=this.toHide.not(this.errorsFor(b)));if(!c)return this.formatAndAdd(b,e),!1}catch(j){throw this.settings.debug&&window.console&&console.log("Exception occurred when checking element "+b.id+", check the '"+e.method+"' method.",j),j}}if(!h)return this.objectLength(f)&&this.successList.push(b),!0},customDataMessage:function(b,c){return a(b).data("msg"+c.charAt(0).toUpperCase()+c.substring(1).toLowerCase())||a(b).data("msg")},customMessage:function(a,b){var c=this.settings.messages[a];return c&&(c.constructor===String?c:c[b])},findDefined:function(){for(var a=0;a<arguments.length;a++)if(void 0!==arguments[a])return arguments[a];return void 0},defaultMessage:function(b,c){return this.findDefined(this.customMessage(b.name,c),this.customDataMessage(b,c),!this.settings.ignoreTitle&&b.title||void 0,a.validator.messages[c],"<strong>Warning: No message defined for "+b.name+"</strong>")},formatAndAdd:function(b,c){var d=this.defaultMessage(b,c.method),e=/\$?\{(\d+)\}/g;"function"==typeof d?d=d.call(this,c.parameters,b):e.test(d)&&(d=a.validator.format(d.replace(e,"{$1}"),c.parameters)),this.errorList.push({message:d,element:b,method:c.method}),this.errorMap[b.name]=d,this.submitted[b.name]=d},addWrapper:function(a){return this.settings.wrapper&&(a=a.add(a.parent(this.settings.wrapper))),a},defaultShowErrors:function(){var a,b,c;for(a=0;this.errorList[a];a++)c=this.errorList[a],this.settings.highlight&&this.settings.highlight.call(this,c.element,this.settings.errorClass,this.settings.validClass),this.showLabel(c.element,c.message);if(this.errorList.length&&(this.toShow=this.toShow.add(this.containers)),this.settings.success)for(a=0;this.successList[a];a++)this.showLabel(this.successList[a]);if(this.settings.unhighlight)for(a=0,b=this.validElements();b[a];a++)this.settings.unhighlight.call(this,b[a],this.settings.errorClass,this.settings.validClass);this.toHide=this.toHide.not(this.toShow),this.hideErrors(),this.addWrapper(this.toShow).show()},validElements:function(){return this.currentElements.not(this.invalidElements())},invalidElements:function(){return a(this.errorList).map(function(){return this.element})},showLabel:function(b,c){var d,e,f,g=this.errorsFor(b),h=this.idOrName(b),i=a(b).attr("aria-describedby");g.length?(g.removeClass(this.settings.validClass).addClass(this.settings.errorClass),g.html(c)):(g=a("<"+this.settings.errorElement+">").attr("id",h+"-error").addClass(this.settings.errorClass).html(c||""),d=g,this.settings.wrapper&&(d=g.hide().show().wrap("<"+this.settings.wrapper+"/>").parent()),this.labelContainer.length?this.labelContainer.append(d):this.settings.errorPlacement?this.settings.errorPlacement(d,a(b)):d.insertAfter(b),g.is("label")?g.attr("for",h):0===g.parents("label[for='"+h+"']").length&&(f=g.attr("id"),i?i.match(new RegExp("\b"+f+"\b"))||(i+=" "+f):i=f,a(b).attr("aria-describedby",i),e=this.groups[b.name],e&&a.each(this.groups,function(b,c){c===e&&a("[name='"+b+"']",this.currentForm).attr("aria-describedby",g.attr("id"))}))),!c&&this.settings.success&&(g.text(""),"string"==typeof this.settings.success?g.addClass(this.settings.success):this.settings.success(g,b)),this.toShow=this.toShow.add(g)},errorsFor:function(b){var c=this.idOrName(b),d=a(b).attr("aria-describedby"),e="label[for='"+c+"'], label[for='"+c+"'] *";return d&&(e=e+", #"+d.replace(/\s+/g,", #")),this.errors().filter(e)},idOrName:function(a){return this.groups[a.name]||(this.checkable(a)?a.name:a.id||a.name)},validationTargetFor:function(a){return this.checkable(a)&&(a=this.findByName(a.name).not(this.settings.ignore)[0]),a},checkable:function(a){return/radio|checkbox/i.test(a.type)},findByName:function(b){return a(this.currentForm).find("[name='"+b+"']")},getLength:function(b,c){switch(c.nodeName.toLowerCase()){case"select":return a("option:selected",c).length;case"input":if(this.checkable(c))return this.findByName(c.name).filter(":checked").length}return b.length},depend:function(a,b){return this.dependTypes[typeof a]?this.dependTypes[typeof a](a,b):!0},dependTypes:{"boolean":function(a){return a},string:function(b,c){return!!a(b,c.form).length},"function":function(a,b){return a(b)}},optional:function(b){var c=this.elementValue(b);return!a.validator.methods.required.call(this,c,b)&&"dependency-mismatch"},startRequest:function(a){this.pending[a.name]||(this.pendingRequest++,this.pending[a.name]=!0)},stopRequest:function(b,c){this.pendingRequest--,this.pendingRequest<0&&(this.pendingRequest=0),delete this.pending[b.name],c&&0===this.pendingRequest&&this.formSubmitted&&this.form()?(a(this.currentForm).submit(),this.formSubmitted=!1):!c&&0===this.pendingRequest&&this.formSubmitted&&(a(this.currentForm).triggerHandler("invalid-form",[this]),this.formSubmitted=!1)},previousValue:function(b){return a.data(b,"previousValue")||a.data(b,"previousValue",{old:null,valid:!0,message:this.defaultMessage(b,"remote")})}},classRuleSettings:{required:{required:!0},email:{email:!0},url:{url:!0},date:{date:!0},dateISO:{dateISO:!0},number:{number:!0},digits:{digits:!0},creditcard:{creditcard:!0}},addClassRules:function(b,c){b.constructor===String?this.classRuleSettings[b]=c:a.extend(this.classRuleSettings,b)},classRules:function(b){var c={},d=a(b).attr("class");return d&&a.each(d.split(" "),function(){this in a.validator.classRuleSettings&&a.extend(c,a.validator.classRuleSettings[this])}),c},attributeRules:function(b){var c,d,e={},f=a(b),g=b.getAttribute("type");for(c in a.validator.methods)"required"===c?(d=b.getAttribute(c),""===d&&(d=!0),d=!!d):d=f.attr(c),/min|max/.test(c)&&(null===g||/number|range|text/.test(g))&&(d=Number(d)),d||0===d?e[c]=d:g===c&&"range"!==g&&(e[c]=!0);return e.maxlength&&/-1|2147483647|524288/.test(e.maxlength)&&delete e.maxlength,e},dataRules:function(b){var c,d,e={},f=a(b);for(c in a.validator.methods)d=f.data("rule"+c.charAt(0).toUpperCase()+c.substring(1).toLowerCase()),void 0!==d&&(e[c]=d);return e},staticRules:function(b){var c={},d=a.data(b.form,"validator");return d.settings.rules&&(c=a.validator.normalizeRule(d.settings.rules[b.name])||{}),c},normalizeRules:function(b,c){return a.each(b,function(d,e){if(e===!1)return void delete b[d];if(e.param||e.depends){var f=!0;switch(typeof e.depends){case"string":f=!!a(e.depends,c.form).length;break;case"function":f=e.depends.call(c,c)}f?b[d]=void 0!==e.param?e.param:!0:delete b[d]}}),a.each(b,function(d,e){b[d]=a.isFunction(e)?e(c):e}),a.each(["minlength","maxlength"],function(){b[this]&&(b[this]=Number(b[this]))}),a.each(["rangelength","range"],function(){var c;b[this]&&(a.isArray(b[this])?b[this]=[Number(b[this][0]),Number(b[this][1])]:"string"==typeof b[this]&&(c=b[this].replace(/[\[\]]/g,"").split(/[\s,]+/),b[this]=[Number(c[0]),Number(c[1])]))}),a.validator.autoCreateRanges&&(b.min&&b.max&&(b.range=[b.min,b.max],delete b.min,delete b.max),b.minlength&&b.maxlength&&(b.rangelength=[b.minlength,b.maxlength],delete b.minlength,delete b.maxlength)),b},normalizeRule:function(b){if("string"==typeof b){var c={};a.each(b.split(/\s/),function(){c[this]=!0}),b=c}return b},addMethod:function(b,c,d){a.validator.methods[b]=c,a.validator.messages[b]=void 0!==d?d:a.validator.messages[b],c.length<3&&a.validator.addClassRules(b,a.validator.normalizeRule(b))},methods:{required:function(b,c,d){if(!this.depend(d,c))return"dependency-mismatch";if("select"===c.nodeName.toLowerCase()){var e=a(c).val();return e&&e.length>0}return this.checkable(c)?this.getLength(b,c)>0:a.trim(b).length>0},email:function(a,b){return this.optional(b)||/^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test(a)},url:function(a,b){return this.optional(b)||/^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(a)},date:function(a,b){return this.optional(b)||!/Invalid|NaN/.test(new Date(a).toString())},dateISO:function(a,b){return this.optional(b)||/^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])$/.test(a)},number:function(a,b){return this.optional(b)||/^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(a)},digits:function(a,b){return this.optional(b)||/^\d+$/.test(a)},creditcard:function(a,b){if(this.optional(b))return"dependency-mismatch";if(/[^0-9 \-]+/.test(a))return!1;var c,d,e=0,f=0,g=!1;if(a=a.replace(/\D/g,""),a.length<13||a.length>19)return!1;for(c=a.length-1;c>=0;c--)d=a.charAt(c),f=parseInt(d,10),g&&(f*=2)>9&&(f-=9),e+=f,g=!g;return e%10===0},minlength:function(b,c,d){var e=a.isArray(b)?b.length:this.getLength(a.trim(b),c);return this.optional(c)||e>=d},maxlength:function(b,c,d){var e=a.isArray(b)?b.length:this.getLength(a.trim(b),c);return this.optional(c)||d>=e},rangelength:function(b,c,d){var e=a.isArray(b)?b.length:this.getLength(a.trim(b),c);return this.optional(c)||e>=d[0]&&e<=d[1]},min:function(a,b,c){return this.optional(b)||a>=c},max:function(a,b,c){return this.optional(b)||c>=a},range:function(a,b,c){return this.optional(b)||a>=c[0]&&a<=c[1]},equalTo:function(b,c,d){var e=a(d);return this.settings.onfocusout&&e.unbind(".validate-equalTo").bind("blur.validate-equalTo",function(){a(c).valid()}),b===e.val()},remote:function(b,c,d){if(this.optional(c))return"dependency-mismatch";var e,f,g=this.previousValue(c);return this.settings.messages[c.name]||(this.settings.messages[c.name]={}),g.originalMessage=this.settings.messages[c.name].remote,this.settings.messages[c.name].remote=g.message,d="string"==typeof d&&{url:d}||d,g.old===b?g.valid:(g.old=b,e=this,this.startRequest(c),f={},f[c.name]=b,a.ajax(a.extend(!0,{url:d,mode:"abort",port:"validate"+c.name,dataType:"json",data:f,context:e.currentForm,success:function(d){var f,h,i,j=d===!0||"true"===d;e.settings.messages[c.name].remote=g.originalMessage,j?(i=e.formSubmitted,e.prepareElement(c),e.formSubmitted=i,e.successList.push(c),delete e.invalid[c.name],e.showErrors()):(f={},h=d||e.defaultMessage(c,"remote"),f[c.name]=g.message=a.isFunction(h)?h(b):h,e.invalid[c.name]=!0,e.showErrors(f)),g.valid=j,e.stopRequest(c,j)}},d)),"pending")}}}),a.format=function(){throw"$.format has been deprecated. Please use $.validator.format instead."};var b,c={};a.ajaxPrefilter?a.ajaxPrefilter(function(a,b,d){var e=a.port;"abort"===a.mode&&(c[e]&&c[e].abort(),c[e]=d)}):(b=a.ajax,a.ajax=function(d){var e=("mode"in d?d:a.ajaxSettings).mode,f=("port"in d?d:a.ajaxSettings).port;return"abort"===e?(c[f]&&c[f].abort(),c[f]=b.apply(this,arguments),c[f]):b.apply(this,arguments)}),a.extend(a.fn,{validateDelegate:function(b,c,d){return this.bind(c,function(c){var e=a(c.target);return e.is(b)?d.apply(e,arguments):void 0})}})});;!function(a){"undefined"==typeof a.fn.each2&&a.extend(a.fn,{each2:function(b){for(var c=a([0]),d=-1,e=this.length;++d<e&&(c.context=c[0]=this[d])&&b.call(c[0],d,c)!==!1;);return this}})}(jQuery),function(a,b){"use strict";function n(b){var c=a(document.createTextNode(""));b.before(c),c.before(b),c.remove()}function o(a){function b(a){return m[a]||a}return a.replace(/[^\u0000-\u007E]/g,b)}function p(a,b){for(var c=0,d=b.length;d>c;c+=1)if(r(a,b[c]))return c;return-1}function q(){var b=a(l);b.appendTo("body");var c={width:b.width()-b[0].clientWidth,height:b.height()-b[0].clientHeight};return b.remove(),c}function r(a,c){return a===c?!0:a===b||c===b?!1:null===a||null===c?!1:a.constructor===String?a+""==c+"":c.constructor===String?c+""==a+"":!1}function s(b,c){var d,e,f;if(null===b||b.length<1)return[];for(d=b.split(c),e=0,f=d.length;f>e;e+=1)d[e]=a.trim(d[e]);return d}function t(a){return a.outerWidth(!1)-a.width()}function u(c){var d="keyup-change-value";c.on("keydown",function(){a.data(c,d)===b&&a.data(c,d,c.val())}),c.on("keyup",function(){var e=a.data(c,d);e!==b&&c.val()!==e&&(a.removeData(c,d),c.trigger("keyup-change"))})}function v(c){c.on("mousemove",function(c){var d=i;(d===b||d.x!==c.pageX||d.y!==c.pageY)&&a(c.target).trigger("mousemove-filtered",c)})}function w(a,c,d){d=d||b;var e;return function(){var b=arguments;window.clearTimeout(e),e=window.setTimeout(function(){c.apply(d,b)},a)}}function x(a,b){var c=w(a,function(a){b.trigger("scroll-debounced",a)});b.on("scroll",function(a){p(a.target,b.get())>=0&&c(a)})}function y(a){a[0]!==document.activeElement&&window.setTimeout(function(){var d,b=a[0],c=a.val().length;a.focus();var e=b.offsetWidth>0||b.offsetHeight>0;e&&b===document.activeElement&&(b.setSelectionRange?b.setSelectionRange(c,c):b.createTextRange&&(d=b.createTextRange(),d.collapse(!1),d.select()))},0)}function z(b){b=a(b)[0];var c=0,d=0;if("selectionStart"in b)c=b.selectionStart,d=b.selectionEnd-c;else if("selection"in document){b.focus();var e=document.selection.createRange();d=document.selection.createRange().text.length,e.moveStart("character",-b.value.length),c=e.text.length-d}return{offset:c,length:d}}function A(a){a.preventDefault(),a.stopPropagation()}function B(a){a.preventDefault(),a.stopImmediatePropagation()}function C(b){if(!h){var c=b[0].currentStyle||window.getComputedStyle(b[0],null);h=a(document.createElement("div")).css({position:"absolute",left:"-10000px",top:"-10000px",display:"none",fontSize:c.fontSize,fontFamily:c.fontFamily,fontStyle:c.fontStyle,fontWeight:c.fontWeight,letterSpacing:c.letterSpacing,textTransform:c.textTransform,whiteSpace:"nowrap"}),h.attr("class","select2-sizer"),a("body").append(h)}return h.text(b.val()),h.width()}function D(b,c,d){var e,g,f=[];e=a.trim(b.attr("class")),e&&(e=""+e,a(e.split(/\s+/)).each2(function(){0===this.indexOf("select2-")&&f.push(this)})),e=a.trim(c.attr("class")),e&&(e=""+e,a(e.split(/\s+/)).each2(function(){0!==this.indexOf("select2-")&&(g=d(this),g&&f.push(g))})),b.attr("class",f.join(" "))}function E(a,b,c,d){var e=o(a.toUpperCase()).indexOf(o(b.toUpperCase())),f=b.length;return 0>e?(c.push(d(a)),void 0):(c.push(d(a.substring(0,e))),c.push("<span class='select2-match'>"),c.push(d(a.substring(e,e+f))),c.push("</span>"),c.push(d(a.substring(e+f,a.length))),void 0)}function F(a){var b={"\\":"&#92;","&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;","/":"&#47;"};return String(a).replace(/[&<>"'\/\\]/g,function(a){return b[a]})}function G(c){var d,e=null,f=c.quietMillis||100,g=c.url,h=this;return function(i){window.clearTimeout(d),d=window.setTimeout(function(){var d=c.data,f=g,j=c.transport||a.fn.select2.ajaxDefaults.transport,k={type:c.type||"GET",cache:c.cache||!1,jsonpCallback:c.jsonpCallback||b,dataType:c.dataType||"json"},l=a.extend({},a.fn.select2.ajaxDefaults.params,k);d=d?d.call(h,i.term,i.page,i.context):null,f="function"==typeof f?f.call(h,i.term,i.page,i.context):f,e&&"function"==typeof e.abort&&e.abort(),c.params&&(a.isFunction(c.params)?a.extend(l,c.params.call(h)):a.extend(l,c.params)),a.extend(l,{url:f,dataType:c.dataType,data:d,success:function(a){var b=c.results(a,i.page,i);i.callback(b)},error:function(a,b,c){var d={hasError:!0,jqXHR:a,textStatus:b,errorThrown:c};i.callback(d)}}),e=j.call(h,l)},f)}}function H(b){var d,e,c=b,f=function(a){return""+a.text};a.isArray(c)&&(e=c,c={results:e}),a.isFunction(c)===!1&&(e=c,c=function(){return e});var g=c();return g.text&&(f=g.text,a.isFunction(f)||(d=g.text,f=function(a){return a[d]})),function(b){var g,d=b.term,e={results:[]};return""===d?(b.callback(c()),void 0):(g=function(c,e){var h,i;if(c=c[0],c.children){h={};for(i in c)c.hasOwnProperty(i)&&(h[i]=c[i]);h.children=[],a(c.children).each2(function(a,b){g(b,h.children)}),(h.children.length||b.matcher(d,f(h),c))&&e.push(h)}else b.matcher(d,f(c),c)&&e.push(c)},a(c().results).each2(function(a,b){g(b,e.results)}),b.callback(e),void 0)}}function I(c){var d=a.isFunction(c);return function(e){var f=e.term,g={results:[]},h=d?c(e):c;a.isArray(h)&&(a(h).each(function(){var a=this.text!==b,c=a?this.text:this;(""===f||e.matcher(f,c))&&g.results.push(a?this:{id:this,text:this})}),e.callback(g))}}function J(b,c){if(a.isFunction(b))return!0;if(!b)return!1;if("string"==typeof b)return!0;throw new Error(c+" must be a string, function, or falsy value")}function K(b,c){if(a.isFunction(b)){var d=Array.prototype.slice.call(arguments,2);return b.apply(c,d)}return b}function L(b){var c=0;return a.each(b,function(a,b){b.children?c+=L(b.children):c++}),c}function M(a,c,d,e){var h,i,j,k,l,f=a,g=!1;if(!e.createSearchChoice||!e.tokenSeparators||e.tokenSeparators.length<1)return b;for(;;){for(i=-1,j=0,k=e.tokenSeparators.length;k>j&&(l=e.tokenSeparators[j],i=a.indexOf(l),!(i>=0));j++);if(0>i)break;if(h=a.substring(0,i),a=a.substring(i+l.length),h.length>0&&(h=e.createSearchChoice.call(this,h,c),h!==b&&null!==h&&e.id(h)!==b&&null!==e.id(h))){for(g=!1,j=0,k=c.length;k>j;j++)if(r(e.id(h),e.id(c[j]))){g=!0;break}g||d(h)}}return f!==a?a:void 0}function N(){var b=this;a.each(arguments,function(a,c){b[c].remove(),b[c]=null})}function O(b,c){var d=function(){};return d.prototype=new b,d.prototype.constructor=d,d.prototype.parent=b.prototype,d.prototype=a.extend(d.prototype,c),d}if(window.Select2===b){var c,d,e,f,g,h,j,k,i={x:0,y:0},c={TAB:9,ENTER:13,ESC:27,SPACE:32,LEFT:37,UP:38,RIGHT:39,DOWN:40,SHIFT:16,CTRL:17,ALT:18,PAGE_UP:33,PAGE_DOWN:34,HOME:36,END:35,BACKSPACE:8,DELETE:46,isArrow:function(a){switch(a=a.which?a.which:a){case c.LEFT:case c.RIGHT:case c.UP:case c.DOWN:return!0}return!1},isControl:function(a){var b=a.which;switch(b){case c.SHIFT:case c.CTRL:case c.ALT:return!0}return a.metaKey?!0:!1},isFunctionKey:function(a){return a=a.which?a.which:a,a>=112&&123>=a}},l="<div class='select2-measure-scrollbar'></div>",m={"\u24b6":"A","\uff21":"A","\xc0":"A","\xc1":"A","\xc2":"A","\u1ea6":"A","\u1ea4":"A","\u1eaa":"A","\u1ea8":"A","\xc3":"A","\u0100":"A","\u0102":"A","\u1eb0":"A","\u1eae":"A","\u1eb4":"A","\u1eb2":"A","\u0226":"A","\u01e0":"A","\xc4":"A","\u01de":"A","\u1ea2":"A","\xc5":"A","\u01fa":"A","\u01cd":"A","\u0200":"A","\u0202":"A","\u1ea0":"A","\u1eac":"A","\u1eb6":"A","\u1e00":"A","\u0104":"A","\u023a":"A","\u2c6f":"A","\ua732":"AA","\xc6":"AE","\u01fc":"AE","\u01e2":"AE","\ua734":"AO","\ua736":"AU","\ua738":"AV","\ua73a":"AV","\ua73c":"AY","\u24b7":"B","\uff22":"B","\u1e02":"B","\u1e04":"B","\u1e06":"B","\u0243":"B","\u0182":"B","\u0181":"B","\u24b8":"C","\uff23":"C","\u0106":"C","\u0108":"C","\u010a":"C","\u010c":"C","\xc7":"C","\u1e08":"C","\u0187":"C","\u023b":"C","\ua73e":"C","\u24b9":"D","\uff24":"D","\u1e0a":"D","\u010e":"D","\u1e0c":"D","\u1e10":"D","\u1e12":"D","\u1e0e":"D","\u0110":"D","\u018b":"D","\u018a":"D","\u0189":"D","\ua779":"D","\u01f1":"DZ","\u01c4":"DZ","\u01f2":"Dz","\u01c5":"Dz","\u24ba":"E","\uff25":"E","\xc8":"E","\xc9":"E","\xca":"E","\u1ec0":"E","\u1ebe":"E","\u1ec4":"E","\u1ec2":"E","\u1ebc":"E","\u0112":"E","\u1e14":"E","\u1e16":"E","\u0114":"E","\u0116":"E","\xcb":"E","\u1eba":"E","\u011a":"E","\u0204":"E","\u0206":"E","\u1eb8":"E","\u1ec6":"E","\u0228":"E","\u1e1c":"E","\u0118":"E","\u1e18":"E","\u1e1a":"E","\u0190":"E","\u018e":"E","\u24bb":"F","\uff26":"F","\u1e1e":"F","\u0191":"F","\ua77b":"F","\u24bc":"G","\uff27":"G","\u01f4":"G","\u011c":"G","\u1e20":"G","\u011e":"G","\u0120":"G","\u01e6":"G","\u0122":"G","\u01e4":"G","\u0193":"G","\ua7a0":"G","\ua77d":"G","\ua77e":"G","\u24bd":"H","\uff28":"H","\u0124":"H","\u1e22":"H","\u1e26":"H","\u021e":"H","\u1e24":"H","\u1e28":"H","\u1e2a":"H","\u0126":"H","\u2c67":"H","\u2c75":"H","\ua78d":"H","\u24be":"I","\uff29":"I","\xcc":"I","\xcd":"I","\xce":"I","\u0128":"I","\u012a":"I","\u012c":"I","\u0130":"I","\xcf":"I","\u1e2e":"I","\u1ec8":"I","\u01cf":"I","\u0208":"I","\u020a":"I","\u1eca":"I","\u012e":"I","\u1e2c":"I","\u0197":"I","\u24bf":"J","\uff2a":"J","\u0134":"J","\u0248":"J","\u24c0":"K","\uff2b":"K","\u1e30":"K","\u01e8":"K","\u1e32":"K","\u0136":"K","\u1e34":"K","\u0198":"K","\u2c69":"K","\ua740":"K","\ua742":"K","\ua744":"K","\ua7a2":"K","\u24c1":"L","\uff2c":"L","\u013f":"L","\u0139":"L","\u013d":"L","\u1e36":"L","\u1e38":"L","\u013b":"L","\u1e3c":"L","\u1e3a":"L","\u0141":"L","\u023d":"L","\u2c62":"L","\u2c60":"L","\ua748":"L","\ua746":"L","\ua780":"L","\u01c7":"LJ","\u01c8":"Lj","\u24c2":"M","\uff2d":"M","\u1e3e":"M","\u1e40":"M","\u1e42":"M","\u2c6e":"M","\u019c":"M","\u24c3":"N","\uff2e":"N","\u01f8":"N","\u0143":"N","\xd1":"N","\u1e44":"N","\u0147":"N","\u1e46":"N","\u0145":"N","\u1e4a":"N","\u1e48":"N","\u0220":"N","\u019d":"N","\ua790":"N","\ua7a4":"N","\u01ca":"NJ","\u01cb":"Nj","\u24c4":"O","\uff2f":"O","\xd2":"O","\xd3":"O","\xd4":"O","\u1ed2":"O","\u1ed0":"O","\u1ed6":"O","\u1ed4":"O","\xd5":"O","\u1e4c":"O","\u022c":"O","\u1e4e":"O","\u014c":"O","\u1e50":"O","\u1e52":"O","\u014e":"O","\u022e":"O","\u0230":"O","\xd6":"O","\u022a":"O","\u1ece":"O","\u0150":"O","\u01d1":"O","\u020c":"O","\u020e":"O","\u01a0":"O","\u1edc":"O","\u1eda":"O","\u1ee0":"O","\u1ede":"O","\u1ee2":"O","\u1ecc":"O","\u1ed8":"O","\u01ea":"O","\u01ec":"O","\xd8":"O","\u01fe":"O","\u0186":"O","\u019f":"O","\ua74a":"O","\ua74c":"O","\u01a2":"OI","\ua74e":"OO","\u0222":"OU","\u24c5":"P","\uff30":"P","\u1e54":"P","\u1e56":"P","\u01a4":"P","\u2c63":"P","\ua750":"P","\ua752":"P","\ua754":"P","\u24c6":"Q","\uff31":"Q","\ua756":"Q","\ua758":"Q","\u024a":"Q","\u24c7":"R","\uff32":"R","\u0154":"R","\u1e58":"R","\u0158":"R","\u0210":"R","\u0212":"R","\u1e5a":"R","\u1e5c":"R","\u0156":"R","\u1e5e":"R","\u024c":"R","\u2c64":"R","\ua75a":"R","\ua7a6":"R","\ua782":"R","\u24c8":"S","\uff33":"S","\u1e9e":"S","\u015a":"S","\u1e64":"S","\u015c":"S","\u1e60":"S","\u0160":"S","\u1e66":"S","\u1e62":"S","\u1e68":"S","\u0218":"S","\u015e":"S","\u2c7e":"S","\ua7a8":"S","\ua784":"S","\u24c9":"T","\uff34":"T","\u1e6a":"T","\u0164":"T","\u1e6c":"T","\u021a":"T","\u0162":"T","\u1e70":"T","\u1e6e":"T","\u0166":"T","\u01ac":"T","\u01ae":"T","\u023e":"T","\ua786":"T","\ua728":"TZ","\u24ca":"U","\uff35":"U","\xd9":"U","\xda":"U","\xdb":"U","\u0168":"U","\u1e78":"U","\u016a":"U","\u1e7a":"U","\u016c":"U","\xdc":"U","\u01db":"U","\u01d7":"U","\u01d5":"U","\u01d9":"U","\u1ee6":"U","\u016e":"U","\u0170":"U","\u01d3":"U","\u0214":"U","\u0216":"U","\u01af":"U","\u1eea":"U","\u1ee8":"U","\u1eee":"U","\u1eec":"U","\u1ef0":"U","\u1ee4":"U","\u1e72":"U","\u0172":"U","\u1e76":"U","\u1e74":"U","\u0244":"U","\u24cb":"V","\uff36":"V","\u1e7c":"V","\u1e7e":"V","\u01b2":"V","\ua75e":"V","\u0245":"V","\ua760":"VY","\u24cc":"W","\uff37":"W","\u1e80":"W","\u1e82":"W","\u0174":"W","\u1e86":"W","\u1e84":"W","\u1e88":"W","\u2c72":"W","\u24cd":"X","\uff38":"X","\u1e8a":"X","\u1e8c":"X","\u24ce":"Y","\uff39":"Y","\u1ef2":"Y","\xdd":"Y","\u0176":"Y","\u1ef8":"Y","\u0232":"Y","\u1e8e":"Y","\u0178":"Y","\u1ef6":"Y","\u1ef4":"Y","\u01b3":"Y","\u024e":"Y","\u1efe":"Y","\u24cf":"Z","\uff3a":"Z","\u0179":"Z","\u1e90":"Z","\u017b":"Z","\u017d":"Z","\u1e92":"Z","\u1e94":"Z","\u01b5":"Z","\u0224":"Z","\u2c7f":"Z","\u2c6b":"Z","\ua762":"Z","\u24d0":"a","\uff41":"a","\u1e9a":"a","\xe0":"a","\xe1":"a","\xe2":"a","\u1ea7":"a","\u1ea5":"a","\u1eab":"a","\u1ea9":"a","\xe3":"a","\u0101":"a","\u0103":"a","\u1eb1":"a","\u1eaf":"a","\u1eb5":"a","\u1eb3":"a","\u0227":"a","\u01e1":"a","\xe4":"a","\u01df":"a","\u1ea3":"a","\xe5":"a","\u01fb":"a","\u01ce":"a","\u0201":"a","\u0203":"a","\u1ea1":"a","\u1ead":"a","\u1eb7":"a","\u1e01":"a","\u0105":"a","\u2c65":"a","\u0250":"a","\ua733":"aa","\xe6":"ae","\u01fd":"ae","\u01e3":"ae","\ua735":"ao","\ua737":"au","\ua739":"av","\ua73b":"av","\ua73d":"ay","\u24d1":"b","\uff42":"b","\u1e03":"b","\u1e05":"b","\u1e07":"b","\u0180":"b","\u0183":"b","\u0253":"b","\u24d2":"c","\uff43":"c","\u0107":"c","\u0109":"c","\u010b":"c","\u010d":"c","\xe7":"c","\u1e09":"c","\u0188":"c","\u023c":"c","\ua73f":"c","\u2184":"c","\u24d3":"d","\uff44":"d","\u1e0b":"d","\u010f":"d","\u1e0d":"d","\u1e11":"d","\u1e13":"d","\u1e0f":"d","\u0111":"d","\u018c":"d","\u0256":"d","\u0257":"d","\ua77a":"d","\u01f3":"dz","\u01c6":"dz","\u24d4":"e","\uff45":"e","\xe8":"e","\xe9":"e","\xea":"e","\u1ec1":"e","\u1ebf":"e","\u1ec5":"e","\u1ec3":"e","\u1ebd":"e","\u0113":"e","\u1e15":"e","\u1e17":"e","\u0115":"e","\u0117":"e","\xeb":"e","\u1ebb":"e","\u011b":"e","\u0205":"e","\u0207":"e","\u1eb9":"e","\u1ec7":"e","\u0229":"e","\u1e1d":"e","\u0119":"e","\u1e19":"e","\u1e1b":"e","\u0247":"e","\u025b":"e","\u01dd":"e","\u24d5":"f","\uff46":"f","\u1e1f":"f","\u0192":"f","\ua77c":"f","\u24d6":"g","\uff47":"g","\u01f5":"g","\u011d":"g","\u1e21":"g","\u011f":"g","\u0121":"g","\u01e7":"g","\u0123":"g","\u01e5":"g","\u0260":"g","\ua7a1":"g","\u1d79":"g","\ua77f":"g","\u24d7":"h","\uff48":"h","\u0125":"h","\u1e23":"h","\u1e27":"h","\u021f":"h","\u1e25":"h","\u1e29":"h","\u1e2b":"h","\u1e96":"h","\u0127":"h","\u2c68":"h","\u2c76":"h","\u0265":"h","\u0195":"hv","\u24d8":"i","\uff49":"i","\xec":"i","\xed":"i","\xee":"i","\u0129":"i","\u012b":"i","\u012d":"i","\xef":"i","\u1e2f":"i","\u1ec9":"i","\u01d0":"i","\u0209":"i","\u020b":"i","\u1ecb":"i","\u012f":"i","\u1e2d":"i","\u0268":"i","\u0131":"i","\u24d9":"j","\uff4a":"j","\u0135":"j","\u01f0":"j","\u0249":"j","\u24da":"k","\uff4b":"k","\u1e31":"k","\u01e9":"k","\u1e33":"k","\u0137":"k","\u1e35":"k","\u0199":"k","\u2c6a":"k","\ua741":"k","\ua743":"k","\ua745":"k","\ua7a3":"k","\u24db":"l","\uff4c":"l","\u0140":"l","\u013a":"l","\u013e":"l","\u1e37":"l","\u1e39":"l","\u013c":"l","\u1e3d":"l","\u1e3b":"l","\u017f":"l","\u0142":"l","\u019a":"l","\u026b":"l","\u2c61":"l","\ua749":"l","\ua781":"l","\ua747":"l","\u01c9":"lj","\u24dc":"m","\uff4d":"m","\u1e3f":"m","\u1e41":"m","\u1e43":"m","\u0271":"m","\u026f":"m","\u24dd":"n","\uff4e":"n","\u01f9":"n","\u0144":"n","\xf1":"n","\u1e45":"n","\u0148":"n","\u1e47":"n","\u0146":"n","\u1e4b":"n","\u1e49":"n","\u019e":"n","\u0272":"n","\u0149":"n","\ua791":"n","\ua7a5":"n","\u01cc":"nj","\u24de":"o","\uff4f":"o","\xf2":"o","\xf3":"o","\xf4":"o","\u1ed3":"o","\u1ed1":"o","\u1ed7":"o","\u1ed5":"o","\xf5":"o","\u1e4d":"o","\u022d":"o","\u1e4f":"o","\u014d":"o","\u1e51":"o","\u1e53":"o","\u014f":"o","\u022f":"o","\u0231":"o","\xf6":"o","\u022b":"o","\u1ecf":"o","\u0151":"o","\u01d2":"o","\u020d":"o","\u020f":"o","\u01a1":"o","\u1edd":"o","\u1edb":"o","\u1ee1":"o","\u1edf":"o","\u1ee3":"o","\u1ecd":"o","\u1ed9":"o","\u01eb":"o","\u01ed":"o","\xf8":"o","\u01ff":"o","\u0254":"o","\ua74b":"o","\ua74d":"o","\u0275":"o","\u01a3":"oi","\u0223":"ou","\ua74f":"oo","\u24df":"p","\uff50":"p","\u1e55":"p","\u1e57":"p","\u01a5":"p","\u1d7d":"p","\ua751":"p","\ua753":"p","\ua755":"p","\u24e0":"q","\uff51":"q","\u024b":"q","\ua757":"q","\ua759":"q","\u24e1":"r","\uff52":"r","\u0155":"r","\u1e59":"r","\u0159":"r","\u0211":"r","\u0213":"r","\u1e5b":"r","\u1e5d":"r","\u0157":"r","\u1e5f":"r","\u024d":"r","\u027d":"r","\ua75b":"r","\ua7a7":"r","\ua783":"r","\u24e2":"s","\uff53":"s","\xdf":"s","\u015b":"s","\u1e65":"s","\u015d":"s","\u1e61":"s","\u0161":"s","\u1e67":"s","\u1e63":"s","\u1e69":"s","\u0219":"s","\u015f":"s","\u023f":"s","\ua7a9":"s","\ua785":"s","\u1e9b":"s","\u24e3":"t","\uff54":"t","\u1e6b":"t","\u1e97":"t","\u0165":"t","\u1e6d":"t","\u021b":"t","\u0163":"t","\u1e71":"t","\u1e6f":"t","\u0167":"t","\u01ad":"t","\u0288":"t","\u2c66":"t","\ua787":"t","\ua729":"tz","\u24e4":"u","\uff55":"u","\xf9":"u","\xfa":"u","\xfb":"u","\u0169":"u","\u1e79":"u","\u016b":"u","\u1e7b":"u","\u016d":"u","\xfc":"u","\u01dc":"u","\u01d8":"u","\u01d6":"u","\u01da":"u","\u1ee7":"u","\u016f":"u","\u0171":"u","\u01d4":"u","\u0215":"u","\u0217":"u","\u01b0":"u","\u1eeb":"u","\u1ee9":"u","\u1eef":"u","\u1eed":"u","\u1ef1":"u","\u1ee5":"u","\u1e73":"u","\u0173":"u","\u1e77":"u","\u1e75":"u","\u0289":"u","\u24e5":"v","\uff56":"v","\u1e7d":"v","\u1e7f":"v","\u028b":"v","\ua75f":"v","\u028c":"v","\ua761":"vy","\u24e6":"w","\uff57":"w","\u1e81":"w","\u1e83":"w","\u0175":"w","\u1e87":"w","\u1e85":"w","\u1e98":"w","\u1e89":"w","\u2c73":"w","\u24e7":"x","\uff58":"x","\u1e8b":"x","\u1e8d":"x","\u24e8":"y","\uff59":"y","\u1ef3":"y","\xfd":"y","\u0177":"y","\u1ef9":"y","\u0233":"y","\u1e8f":"y","\xff":"y","\u1ef7":"y","\u1e99":"y","\u1ef5":"y","\u01b4":"y","\u024f":"y","\u1eff":"y","\u24e9":"z","\uff5a":"z","\u017a":"z","\u1e91":"z","\u017c":"z","\u017e":"z","\u1e93":"z","\u1e95":"z","\u01b6":"z","\u0225":"z","\u0240":"z","\u2c6c":"z","\ua763":"z","\u0386":"\u0391","\u0388":"\u0395","\u0389":"\u0397","\u038a":"\u0399","\u03aa":"\u0399","\u038c":"\u039f","\u038e":"\u03a5","\u03ab":"\u03a5","\u038f":"\u03a9","\u03ac":"\u03b1","\u03ad":"\u03b5","\u03ae":"\u03b7","\u03af":"\u03b9","\u03ca":"\u03b9","\u0390":"\u03b9","\u03cc":"\u03bf","\u03cd":"\u03c5","\u03cb":"\u03c5","\u03b0":"\u03c5","\u03c9":"\u03c9","\u03c2":"\u03c3"};j=a(document),g=function(){var a=1;return function(){return a++}}(),d=O(Object,{bind:function(a){var b=this;return function(){a.apply(b,arguments)}},init:function(c){var d,e,f=".select2-results";this.opts=c=this.prepareOpts(c),this.id=c.id,c.element.data("select2")!==b&&null!==c.element.data("select2")&&c.element.data("select2").destroy(),this.container=this.createContainer(),this.liveRegion=a("<span>",{role:"status","aria-live":"polite"}).addClass("select2-hidden-accessible").appendTo(document.body),this.containerId="s2id_"+(c.element.attr("id")||"autogen"+g()),this.containerEventName=this.containerId.replace(/([.])/g,"_").replace(/([;&,\-\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g,"\\$1"),this.container.attr("id",this.containerId),this.container.attr("title",c.element.attr("title")),this.body=a("body"),D(this.container,this.opts.element,this.opts.adaptContainerCssClass),this.container.attr("style",c.element.attr("style")),this.container.css(K(c.containerCss,this.opts.element)),this.container.addClass(K(c.containerCssClass,this.opts.element)),this.elementTabIndex=this.opts.element.attr("tabindex"),this.opts.element.data("select2",this).attr("tabindex","-1").before(this.container).on("click.select2",A),this.container.data("select2",this),this.dropdown=this.container.find(".select2-drop"),D(this.dropdown,this.opts.element,this.opts.adaptDropdownCssClass),this.dropdown.addClass(K(c.dropdownCssClass,this.opts.element)),this.dropdown.data("select2",this),this.dropdown.on("click",A),this.results=d=this.container.find(f),this.search=e=this.container.find("input.select2-input"),this.queryCount=0,this.resultsPage=0,this.context=null,this.initContainer(),this.container.on("click",A),v(this.results),this.dropdown.on("mousemove-filtered",f,this.bind(this.highlightUnderEvent)),this.dropdown.on("touchstart touchmove touchend",f,this.bind(function(a){this._touchEvent=!0,this.highlightUnderEvent(a)})),this.dropdown.on("touchmove",f,this.bind(this.touchMoved)),this.dropdown.on("touchstart touchend",f,this.bind(this.clearTouchMoved)),this.dropdown.on("click",this.bind(function(){this._touchEvent&&(this._touchEvent=!1,this.selectHighlighted())})),x(80,this.results),this.dropdown.on("scroll-debounced",f,this.bind(this.loadMoreIfNeeded)),a(this.container).on("change",".select2-input",function(a){a.stopPropagation()}),a(this.dropdown).on("change",".select2-input",function(a){a.stopPropagation()}),a.fn.mousewheel&&d.mousewheel(function(a,b,c,e){var f=d.scrollTop();e>0&&0>=f-e?(d.scrollTop(0),A(a)):0>e&&d.get(0).scrollHeight-d.scrollTop()+e<=d.height()&&(d.scrollTop(d.get(0).scrollHeight-d.height()),A(a))}),u(e),e.on("keyup-change input paste",this.bind(this.updateResults)),e.on("focus",function(){e.addClass("select2-focused")}),e.on("blur",function(){e.removeClass("select2-focused")}),this.dropdown.on("mouseup",f,this.bind(function(b){a(b.target).closest(".select2-result-selectable").length>0&&(this.highlightUnderEvent(b),this.selectHighlighted(b))})),this.dropdown.on("click mouseup mousedown touchstart touchend focusin",function(a){a.stopPropagation()}),this.nextSearchTerm=b,a.isFunction(this.opts.initSelection)&&(this.initSelection(),this.monitorSource()),null!==c.maximumInputLength&&this.search.attr("maxlength",c.maximumInputLength);var h=c.element.prop("disabled");h===b&&(h=!1),this.enable(!h);var i=c.element.prop("readonly");i===b&&(i=!1),this.readonly(i),k=k||q(),this.autofocus=c.element.prop("autofocus"),c.element.prop("autofocus",!1),this.autofocus&&this.focus(),this.search.attr("placeholder",c.searchInputPlaceholder)},destroy:function(){var a=this.opts.element,c=a.data("select2"),d=this;this.close(),a.length&&a[0].detachEvent&&a.each(function(){this.detachEvent("onpropertychange",d._sync)}),this.propertyObserver&&(this.propertyObserver.disconnect(),this.propertyObserver=null),this._sync=null,c!==b&&(c.container.remove(),c.liveRegion.remove(),c.dropdown.remove(),a.removeClass("select2-offscreen").removeData("select2").off(".select2").prop("autofocus",this.autofocus||!1),this.elementTabIndex?a.attr({tabindex:this.elementTabIndex}):a.removeAttr("tabindex"),a.show()),N.call(this,"container","liveRegion","dropdown","results","search")},optionToData:function(a){return a.is("option")?{id:a.prop("value"),text:a.text(),element:a.get(),css:a.attr("class"),disabled:a.prop("disabled"),locked:r(a.attr("locked"),"locked")||r(a.data("locked"),!0)}:a.is("optgroup")?{text:a.attr("label"),children:[],element:a.get(),css:a.attr("class")}:void 0},prepareOpts:function(c){var d,e,f,h,i=this;if(d=c.element,"select"===d.get(0).tagName.toLowerCase()&&(this.select=e=c.element),e&&a.each(["id","multiple","ajax","query","createSearchChoice","initSelection","data","tags"],function(){if(this in c)throw new Error("Option '"+this+"' is not allowed for Select2 when attached to a <select> element.")}),c=a.extend({},{populateResults:function(d,e,f){var h,j=this.opts.id,k=this.liveRegion;h=function(d,e,l){var m,n,o,p,q,r,s,t,u,v;d=c.sortResults(d,e,f);var w=[];for(m=0,n=d.length;n>m;m+=1)o=d[m],q=o.disabled===!0,p=!q&&j(o)!==b,r=o.children&&o.children.length>0,s=a("<li></li>"),s.addClass("select2-results-dept-"+l),s.addClass("select2-result"),s.addClass(p?"select2-result-selectable":"select2-result-unselectable"),q&&s.addClass("select2-disabled"),r&&s.addClass("select2-result-with-children"),s.addClass(i.opts.formatResultCssClass(o)),s.attr("role","presentation"),t=a(document.createElement("div")),t.addClass("select2-result-label"),t.attr("id","select2-result-label-"+g()),t.attr("role","option"),v=c.formatResult(o,t,f,i.opts.escapeMarkup),v!==b&&(t.html(v),s.append(t)),r&&(u=a("<ul></ul>"),u.addClass("select2-result-sub"),h(o.children,u,l+1),s.append(u)),s.data("select2-data",o),w.push(s[0]);e.append(w),k.text(c.formatMatches(d.length))},h(e,d,0)}},a.fn.select2.defaults,c),"function"!=typeof c.id&&(f=c.id,c.id=function(a){return a[f]}),a.isArray(c.element.data("select2Tags"))){if("tags"in c)throw"tags specified as both an attribute 'data-select2-tags' and in options of Select2 "+c.element.attr("id");c.tags=c.element.data("select2Tags")}if(e?(c.query=this.bind(function(a){var f,g,h,c={results:[],more:!1},e=a.term;h=function(b,c){var d;b.is("option")?a.matcher(e,b.text(),b)&&c.push(i.optionToData(b)):b.is("optgroup")&&(d=i.optionToData(b),b.children().each2(function(a,b){h(b,d.children)}),d.children.length>0&&c.push(d))},f=d.children(),this.getPlaceholder()!==b&&f.length>0&&(g=this.getPlaceholderOption(),g&&(f=f.not(g))),f.each2(function(a,b){h(b,c.results)}),a.callback(c)}),c.id=function(a){return a.id}):"query"in c||("ajax"in c?(h=c.element.data("ajax-url"),h&&h.length>0&&(c.ajax.url=h),c.query=G.call(c.element,c.ajax)):"data"in c?c.query=H(c.data):"tags"in c&&(c.query=I(c.tags),c.createSearchChoice===b&&(c.createSearchChoice=function(b){return{id:a.trim(b),text:a.trim(b)}}),c.initSelection===b&&(c.initSelection=function(b,d){var e=[];a(s(b.val(),c.separator)).each(function(){var b={id:this,text:this},d=c.tags;a.isFunction(d)&&(d=d()),a(d).each(function(){return r(this.id,b.id)?(b=this,!1):void 0}),e.push(b)}),d(e)}))),"function"!=typeof c.query)throw"query function not defined for Select2 "+c.element.attr("id");if("top"===c.createSearchChoicePosition)c.createSearchChoicePosition=function(a,b){a.unshift(b)};else if("bottom"===c.createSearchChoicePosition)c.createSearchChoicePosition=function(a,b){a.push(b)};else if("function"!=typeof c.createSearchChoicePosition)throw"invalid createSearchChoicePosition option must be 'top', 'bottom' or a custom function";return c},monitorSource:function(){var d,c=this.opts.element,e=this;c.on("change.select2",this.bind(function(){this.opts.element.data("select2-change-triggered")!==!0&&this.initSelection()})),this._sync=this.bind(function(){var a=c.prop("disabled");a===b&&(a=!1),this.enable(!a);var d=c.prop("readonly");d===b&&(d=!1),this.readonly(d),D(this.container,this.opts.element,this.opts.adaptContainerCssClass),this.container.addClass(K(this.opts.containerCssClass,this.opts.element)),D(this.dropdown,this.opts.element,this.opts.adaptDropdownCssClass),this.dropdown.addClass(K(this.opts.dropdownCssClass,this.opts.element))}),c.length&&c[0].attachEvent&&c.each(function(){this.attachEvent("onpropertychange",e._sync)}),d=window.MutationObserver||window.WebKitMutationObserver||window.MozMutationObserver,d!==b&&(this.propertyObserver&&(delete this.propertyObserver,this.propertyObserver=null),this.propertyObserver=new d(function(b){a.each(b,e._sync)}),this.propertyObserver.observe(c.get(0),{attributes:!0,subtree:!1}))},triggerSelect:function(b){var c=a.Event("select2-selecting",{val:this.id(b),object:b,choice:b});return this.opts.element.trigger(c),!c.isDefaultPrevented()},triggerChange:function(b){b=b||{},b=a.extend({},b,{type:"change",val:this.val()}),this.opts.element.data("select2-change-triggered",!0),this.opts.element.trigger(b),this.opts.element.data("select2-change-triggered",!1),this.opts.element.click(),this.opts.blurOnChange&&this.opts.element.blur()},isInterfaceEnabled:function(){return this.enabledInterface===!0},enableInterface:function(){var a=this._enabled&&!this._readonly,b=!a;return a===this.enabledInterface?!1:(this.container.toggleClass("select2-container-disabled",b),this.close(),this.enabledInterface=a,!0)},enable:function(a){a===b&&(a=!0),this._enabled!==a&&(this._enabled=a,this.opts.element.prop("disabled",!a),this.enableInterface())},disable:function(){this.enable(!1)},readonly:function(a){a===b&&(a=!1),this._readonly!==a&&(this._readonly=a,this.opts.element.prop("readonly",a),this.enableInterface())},opened:function(){return this.container?this.container.hasClass("select2-dropdown-open"):!1},positionDropdown:function(){var t,u,v,w,x,b=this.dropdown,c=this.container.offset(),d=this.container.outerHeight(!1),e=this.container.outerWidth(!1),f=b.outerHeight(!1),g=a(window),h=g.width(),i=g.height(),j=g.scrollLeft()+h,l=g.scrollTop()+i,m=c.top+d,n=c.left,o=l>=m+f,p=c.top-f>=g.scrollTop(),q=b.outerWidth(!1),r=j>=n+q,s=b.hasClass("select2-drop-above");s?(u=!0,!p&&o&&(v=!0,u=!1)):(u=!1,!o&&p&&(v=!0,u=!0)),v&&(b.hide(),c=this.container.offset(),d=this.container.outerHeight(!1),e=this.container.outerWidth(!1),f=b.outerHeight(!1),j=g.scrollLeft()+h,l=g.scrollTop()+i,m=c.top+d,n=c.left,q=b.outerWidth(!1),r=j>=n+q,b.show(),this.focusSearch()),this.opts.dropdownAutoWidth?(x=a(".select2-results",b)[0],b.addClass("select2-drop-auto-width"),b.css("width",""),q=b.outerWidth(!1)+(x.scrollHeight===x.clientHeight?0:k.width),q>e?e=q:q=e,f=b.outerHeight(!1),r=j>=n+q):this.container.removeClass("select2-drop-auto-width"),"static"!==this.body.css("position")&&(t=this.body.offset(),m-=t.top,n-=t.left),r||(n=c.left+this.container.outerWidth(!1)-q),w={left:n,width:e},u?(w.top=c.top-f,w.bottom="auto",this.container.addClass("select2-drop-above"),b.addClass("select2-drop-above")):(w.top=m,w.bottom="auto",this.container.removeClass("select2-drop-above"),b.removeClass("select2-drop-above")),w=a.extend(w,K(this.opts.dropdownCss,this.opts.element)),b.css(w)},shouldOpen:function(){var b;return this.opened()?!1:this._enabled===!1||this._readonly===!0?!1:(b=a.Event("select2-opening"),this.opts.element.trigger(b),!b.isDefaultPrevented())},clearDropdownAlignmentPreference:function(){this.container.removeClass("select2-drop-above"),this.dropdown.removeClass("select2-drop-above")},open:function(){return this.shouldOpen()?(this.opening(),j.on("mousemove.select2Event",function(a){i.x=a.pageX,i.y=a.pageY}),!0):!1},opening:function(){var f,b=this.containerEventName,c="scroll."+b,d="resize."+b,e="orientationchange."+b;this.container.addClass("select2-dropdown-open").addClass("select2-container-active"),this.clearDropdownAlignmentPreference(),this.dropdown[0]!==this.body.children().last()[0]&&this.dropdown.detach().appendTo(this.body),f=a("#select2-drop-mask"),0==f.length&&(f=a(document.createElement("div")),f.attr("id","select2-drop-mask").attr("class","select2-drop-mask"),f.hide(),f.appendTo(this.body),f.on("mousedown touchstart click",function(b){n(f);var d,c=a("#select2-drop");c.length>0&&(d=c.data("select2"),d.opts.selectOnBlur&&d.selectHighlighted({noFocus:!0}),d.close(),b.preventDefault(),b.stopPropagation())})),this.dropdown.prev()[0]!==f[0]&&this.dropdown.before(f),a("#select2-drop").removeAttr("id"),this.dropdown.attr("id","select2-drop"),f.show(),this.positionDropdown(),this.dropdown.show(),this.positionDropdown(),this.dropdown.addClass("select2-drop-active");var g=this;this.container.parents().add(window).each(function(){a(this).on(d+" "+c+" "+e,function(){g.opened()&&g.positionDropdown()})})},close:function(){if(this.opened()){var b=this.containerEventName,c="scroll."+b,d="resize."+b,e="orientationchange."+b;this.container.parents().add(window).each(function(){a(this).off(c).off(d).off(e)}),this.clearDropdownAlignmentPreference(),a("#select2-drop-mask").hide(),this.dropdown.removeAttr("id"),this.dropdown.hide(),this.container.removeClass("select2-dropdown-open").removeClass("select2-container-active"),this.results.empty(),j.off("mousemove.select2Event"),this.clearSearch(),this.search.removeClass("select2-active"),this.opts.element.trigger(a.Event("select2-close"))}},externalSearch:function(a){this.open(),this.search.val(a),this.updateResults(!1)},clearSearch:function(){},getMaximumSelectionSize:function(){return K(this.opts.maximumSelectionSize,this.opts.element)},ensureHighlightVisible:function(){var c,d,e,f,g,h,i,j,b=this.results;if(d=this.highlight(),!(0>d)){if(0==d)return b.scrollTop(0),void 0;c=this.findHighlightableChoices().find(".select2-result-label"),e=a(c[d]),j=(e.offset()||{}).top||0,f=j+e.outerHeight(!0),d===c.length-1&&(i=b.find("li.select2-more-results"),i.length>0&&(f=i.offset().top+i.outerHeight(!0))),g=b.offset().top+b.outerHeight(!0),f>g&&b.scrollTop(b.scrollTop()+(f-g)),h=j-b.offset().top,0>h&&"none"!=e.css("display")&&b.scrollTop(b.scrollTop()+h)}},findHighlightableChoices:function(){return this.results.find(".select2-result-selectable:not(.select2-disabled):not(.select2-selected)")},moveHighlight:function(b){for(var c=this.findHighlightableChoices(),d=this.highlight();d>-1&&d<c.length;){d+=b;var e=a(c[d]);if(e.hasClass("select2-result-selectable")&&!e.hasClass("select2-disabled")&&!e.hasClass("select2-selected")){this.highlight(d);
break}}},highlight:function(b){var d,e,c=this.findHighlightableChoices();return 0===arguments.length?p(c.filter(".select2-highlighted")[0],c.get()):(b>=c.length&&(b=c.length-1),0>b&&(b=0),this.removeHighlight(),d=a(c[b]),d.addClass("select2-highlighted"),this.search.attr("aria-activedescendant",d.find(".select2-result-label").attr("id")),this.ensureHighlightVisible(),this.liveRegion.text(d.text()),e=d.data("select2-data"),e&&this.opts.element.trigger({type:"select2-highlight",val:this.id(e),choice:e}),void 0)},removeHighlight:function(){this.results.find(".select2-highlighted").removeClass("select2-highlighted")},touchMoved:function(){this._touchMoved=!0},clearTouchMoved:function(){this._touchMoved=!1},countSelectableResults:function(){return this.findHighlightableChoices().length},highlightUnderEvent:function(b){var c=a(b.target).closest(".select2-result-selectable");if(c.length>0&&!c.is(".select2-highlighted")){var d=this.findHighlightableChoices();this.highlight(d.index(c))}else 0==c.length&&this.removeHighlight()},loadMoreIfNeeded:function(){var c,a=this.results,b=a.find("li.select2-more-results"),d=this.resultsPage+1,e=this,f=this.search.val(),g=this.context;0!==b.length&&(c=b.offset().top-a.offset().top-a.height(),c<=this.opts.loadMorePadding&&(b.addClass("select2-active"),this.opts.query({element:this.opts.element,term:f,page:d,context:g,matcher:this.opts.matcher,callback:this.bind(function(c){e.opened()&&(e.opts.populateResults.call(this,a,c.results,{term:f,page:d,context:g}),e.postprocessResults(c,!1,!1),c.more===!0?(b.detach().appendTo(a).text(K(e.opts.formatLoadMore,e.opts.element,d+1)),window.setTimeout(function(){e.loadMoreIfNeeded()},10)):b.remove(),e.positionDropdown(),e.resultsPage=d,e.context=c.context,this.opts.element.trigger({type:"select2-loaded",items:c}))})})))},tokenize:function(){},updateResults:function(c){function m(){d.removeClass("select2-active"),h.positionDropdown(),e.find(".select2-no-results,.select2-selection-limit,.select2-searching").length?h.liveRegion.text(e.text()):h.liveRegion.text(h.opts.formatMatches(e.find(".select2-result-selectable").length))}function n(a){e.html(a),m()}var g,i,l,d=this.search,e=this.results,f=this.opts,h=this,j=d.val(),k=a.data(this.container,"select2-last-term");if((c===!0||!k||!r(j,k))&&(a.data(this.container,"select2-last-term",j),c===!0||this.showSearchInput!==!1&&this.opened())){l=++this.queryCount;var o=this.getMaximumSelectionSize();if(o>=1&&(g=this.data(),a.isArray(g)&&g.length>=o&&J(f.formatSelectionTooBig,"formatSelectionTooBig")))return n("<li class='select2-selection-limit'>"+K(f.formatSelectionTooBig,f.element,o)+"</li>"),void 0;if(d.val().length<f.minimumInputLength)return J(f.formatInputTooShort,"formatInputTooShort")?n("<li class='select2-no-results'>"+K(f.formatInputTooShort,f.element,d.val(),f.minimumInputLength)+"</li>"):n(""),c&&this.showSearch&&this.showSearch(!0),void 0;if(f.maximumInputLength&&d.val().length>f.maximumInputLength)return J(f.formatInputTooLong,"formatInputTooLong")?n("<li class='select2-no-results'>"+K(f.formatInputTooLong,f.element,d.val(),f.maximumInputLength)+"</li>"):n(""),void 0;f.formatSearching&&0===this.findHighlightableChoices().length&&n("<li class='select2-searching'>"+K(f.formatSearching,f.element)+"</li>"),d.addClass("select2-active"),this.removeHighlight(),i=this.tokenize(),i!=b&&null!=i&&d.val(i),this.resultsPage=1,f.query({element:f.element,term:d.val(),page:this.resultsPage,context:null,matcher:f.matcher,callback:this.bind(function(g){var i;if(l==this.queryCount){if(!this.opened())return this.search.removeClass("select2-active"),void 0;if(g.hasError!==b&&J(f.formatAjaxError,"formatAjaxError"))return n("<li class='select2-ajax-error'>"+K(f.formatAjaxError,f.element,g.jqXHR,g.textStatus,g.errorThrown)+"</li>"),void 0;if(this.context=g.context===b?null:g.context,this.opts.createSearchChoice&&""!==d.val()&&(i=this.opts.createSearchChoice.call(h,d.val(),g.results),i!==b&&null!==i&&h.id(i)!==b&&null!==h.id(i)&&0===a(g.results).filter(function(){return r(h.id(this),h.id(i))}).length&&this.opts.createSearchChoicePosition(g.results,i)),0===g.results.length&&J(f.formatNoMatches,"formatNoMatches"))return n("<li class='select2-no-results'>"+K(f.formatNoMatches,f.element,d.val())+"</li>"),void 0;e.empty(),h.opts.populateResults.call(this,e,g.results,{term:d.val(),page:this.resultsPage,context:null}),g.more===!0&&J(f.formatLoadMore,"formatLoadMore")&&(e.append("<li class='select2-more-results'>"+f.escapeMarkup(K(f.formatLoadMore,f.element,this.resultsPage))+"</li>"),window.setTimeout(function(){h.loadMoreIfNeeded()},10)),this.postprocessResults(g,c),m(),this.opts.element.trigger({type:"select2-loaded",items:g})}})})}},cancel:function(){this.close()},blur:function(){this.opts.selectOnBlur&&this.selectHighlighted({noFocus:!0}),this.close(),this.container.removeClass("select2-container-active"),this.search[0]===document.activeElement&&this.search.blur(),this.clearSearch(),this.selection.find(".select2-search-choice-focus").removeClass("select2-search-choice-focus")},focusSearch:function(){y(this.search)},selectHighlighted:function(a){if(this._touchMoved)return this.clearTouchMoved(),void 0;var b=this.highlight(),c=this.results.find(".select2-highlighted"),d=c.closest(".select2-result").data("select2-data");d?(this.highlight(b),this.onSelect(d,a)):a&&a.noFocus&&this.close()},getPlaceholder:function(){var a;return this.opts.element.attr("placeholder")||this.opts.element.attr("data-placeholder")||this.opts.element.data("placeholder")||this.opts.placeholder||((a=this.getPlaceholderOption())!==b?a.text():b)},getPlaceholderOption:function(){if(this.select){var c=this.select.children("option").first();if(this.opts.placeholderOption!==b)return"first"===this.opts.placeholderOption&&c||"function"==typeof this.opts.placeholderOption&&this.opts.placeholderOption(this.select);if(""===a.trim(c.text())&&""===c.val())return c}},initContainerWidth:function(){function c(){var c,d,e,f,g,h;if("off"===this.opts.width)return null;if("element"===this.opts.width)return 0===this.opts.element.outerWidth(!1)?"auto":this.opts.element.outerWidth(!1)+"px";if("copy"===this.opts.width||"resolve"===this.opts.width){if(c=this.opts.element.attr("style"),c!==b)for(d=c.split(";"),f=0,g=d.length;g>f;f+=1)if(h=d[f].replace(/\s/g,""),e=h.match(/^width:(([-+]?([0-9]*\.)?[0-9]+)(px|em|ex|%|in|cm|mm|pt|pc))/i),null!==e&&e.length>=1)return e[1];return"resolve"===this.opts.width?(c=this.opts.element.css("width"),c.indexOf("%")>0?c:0===this.opts.element.outerWidth(!1)?"auto":this.opts.element.outerWidth(!1)+"px"):null}return a.isFunction(this.opts.width)?this.opts.width():this.opts.width}var d=c.call(this);null!==d&&this.container.css("width",d)}}),e=O(d,{createContainer:function(){var b=a(document.createElement("div")).attr({"class":"select2-container"}).html(["<a href='javascript:void(0)' class='select2-choice' tabindex='-1'>","   <span class='select2-chosen'>&#160;</span><abbr class='select2-search-choice-close'></abbr>","   <span class='select2-arrow' role='presentation'><b role='presentation'></b></span>","</a>","<label for='' class='select2-offscreen'></label>","<input class='select2-focusser select2-offscreen' type='text' aria-haspopup='true' role='button' />","<div class='select2-drop select2-display-none'>","   <div class='select2-search'>","       <label for='' class='select2-offscreen'></label>","       <input type='text' autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' class='select2-input' role='combobox' aria-expanded='true'","       aria-autocomplete='list' />","   </div>","   <ul class='select2-results' role='listbox'>","   </ul>","</div>"].join(""));return b},enableInterface:function(){this.parent.enableInterface.apply(this,arguments)&&this.focusser.prop("disabled",!this.isInterfaceEnabled())},opening:function(){var c,d,e;this.opts.minimumResultsForSearch>=0&&this.showSearch(!0),this.parent.opening.apply(this,arguments),this.showSearchInput!==!1&&this.search.val(this.focusser.val()),this.opts.shouldFocusInput(this)&&(this.search.focus(),c=this.search.get(0),c.createTextRange?(d=c.createTextRange(),d.collapse(!1),d.select()):c.setSelectionRange&&(e=this.search.val().length,c.setSelectionRange(e,e))),""===this.search.val()&&this.nextSearchTerm!=b&&(this.search.val(this.nextSearchTerm),this.search.select()),this.focusser.prop("disabled",!0).val(""),this.updateResults(!0),this.opts.element.trigger(a.Event("select2-open"))},close:function(){this.opened()&&(this.parent.close.apply(this,arguments),this.focusser.prop("disabled",!1),this.opts.shouldFocusInput(this)&&this.focusser.focus())},focus:function(){this.opened()?this.close():(this.focusser.prop("disabled",!1),this.opts.shouldFocusInput(this)&&this.focusser.focus())},isFocused:function(){return this.container.hasClass("select2-container-active")},cancel:function(){this.parent.cancel.apply(this,arguments),this.focusser.prop("disabled",!1),this.opts.shouldFocusInput(this)&&this.focusser.focus()},destroy:function(){a("label[for='"+this.focusser.attr("id")+"']").attr("for",this.opts.element.attr("id")),this.parent.destroy.apply(this,arguments),N.call(this,"selection","focusser")},initContainer:function(){var b,h,d=this.container,e=this.dropdown,f=g();this.opts.minimumResultsForSearch<0?this.showSearch(!1):this.showSearch(!0),this.selection=b=d.find(".select2-choice"),this.focusser=d.find(".select2-focusser"),b.find(".select2-chosen").attr("id","select2-chosen-"+f),this.focusser.attr("aria-labelledby","select2-chosen-"+f),this.results.attr("id","select2-results-"+f),this.search.attr("aria-owns","select2-results-"+f),this.focusser.attr("id","s2id_autogen"+f),h=a("label[for='"+this.opts.element.attr("id")+"']"),this.focusser.prev().text(h.text()).attr("for",this.focusser.attr("id"));var i=this.opts.element.attr("title");this.opts.element.attr("title",i||h.text()),this.focusser.attr("tabindex",this.elementTabIndex),this.search.attr("id",this.focusser.attr("id")+"_search"),this.search.prev().text(a("label[for='"+this.focusser.attr("id")+"']").text()).attr("for",this.search.attr("id")),this.search.on("keydown",this.bind(function(a){if(this.isInterfaceEnabled()&&229!=a.keyCode){if(a.which===c.PAGE_UP||a.which===c.PAGE_DOWN)return A(a),void 0;switch(a.which){case c.UP:case c.DOWN:return this.moveHighlight(a.which===c.UP?-1:1),A(a),void 0;case c.ENTER:return this.selectHighlighted(),A(a),void 0;case c.TAB:return this.selectHighlighted({noFocus:!0}),void 0;case c.ESC:return this.cancel(a),A(a),void 0}}})),this.search.on("blur",this.bind(function(){document.activeElement===this.body.get(0)&&window.setTimeout(this.bind(function(){this.opened()&&this.search.focus()}),0)})),this.focusser.on("keydown",this.bind(function(a){if(this.isInterfaceEnabled()&&a.which!==c.TAB&&!c.isControl(a)&&!c.isFunctionKey(a)&&a.which!==c.ESC){if(this.opts.openOnEnter===!1&&a.which===c.ENTER)return A(a),void 0;if(a.which==c.DOWN||a.which==c.UP||a.which==c.ENTER&&this.opts.openOnEnter){if(a.altKey||a.ctrlKey||a.shiftKey||a.metaKey)return;return this.open(),A(a),void 0}return a.which==c.DELETE||a.which==c.BACKSPACE?(this.opts.allowClear&&this.clear(),A(a),void 0):void 0}})),u(this.focusser),this.focusser.on("keyup-change input",this.bind(function(a){if(this.opts.minimumResultsForSearch>=0){if(a.stopPropagation(),this.opened())return;this.open()}})),b.on("mousedown touchstart","abbr",this.bind(function(a){this.isInterfaceEnabled()&&(this.clear(),B(a),this.close(),this.selection.focus())})),b.on("mousedown touchstart",this.bind(function(c){n(b),this.container.hasClass("select2-container-active")||this.opts.element.trigger(a.Event("select2-focus")),this.opened()?this.close():this.isInterfaceEnabled()&&this.open(),A(c)})),e.on("mousedown touchstart",this.bind(function(){this.opts.shouldFocusInput(this)&&this.search.focus()})),b.on("focus",this.bind(function(a){A(a)})),this.focusser.on("focus",this.bind(function(){this.container.hasClass("select2-container-active")||this.opts.element.trigger(a.Event("select2-focus")),this.container.addClass("select2-container-active")})).on("blur",this.bind(function(){this.opened()||(this.container.removeClass("select2-container-active"),this.opts.element.trigger(a.Event("select2-blur")))})),this.search.on("focus",this.bind(function(){this.container.hasClass("select2-container-active")||this.opts.element.trigger(a.Event("select2-focus")),this.container.addClass("select2-container-active")})),this.initContainerWidth(),this.opts.element.addClass("select2-offscreen"),this.setPlaceholder()},clear:function(b){var c=this.selection.data("select2-data");if(c){var d=a.Event("select2-clearing");if(this.opts.element.trigger(d),d.isDefaultPrevented())return;var e=this.getPlaceholderOption();this.opts.element.val(e?e.val():""),this.selection.find(".select2-chosen").empty(),this.selection.removeData("select2-data"),this.setPlaceholder(),b!==!1&&(this.opts.element.trigger({type:"select2-removed",val:this.id(c),choice:c}),this.triggerChange({removed:c}))}},initSelection:function(){if(this.isPlaceholderOptionSelected())this.updateSelection(null),this.close(),this.setPlaceholder();else{var c=this;this.opts.initSelection.call(null,this.opts.element,function(a){a!==b&&null!==a&&(c.updateSelection(a),c.close(),c.setPlaceholder(),c.nextSearchTerm=c.opts.nextSearchTerm(a,c.search.val()))})}},isPlaceholderOptionSelected:function(){var a;return this.getPlaceholder()===b?!1:(a=this.getPlaceholderOption())!==b&&a.prop("selected")||""===this.opts.element.val()||this.opts.element.val()===b||null===this.opts.element.val()},prepareOpts:function(){var b=this.parent.prepareOpts.apply(this,arguments),c=this;return"select"===b.element.get(0).tagName.toLowerCase()?b.initSelection=function(a,b){var d=a.find("option").filter(function(){return this.selected&&!this.disabled});b(c.optionToData(d))}:"data"in b&&(b.initSelection=b.initSelection||function(c,d){var e=c.val(),f=null;b.query({matcher:function(a,c,d){var g=r(e,b.id(d));return g&&(f=d),g},callback:a.isFunction(d)?function(){d(f)}:a.noop})}),b},getPlaceholder:function(){return this.select&&this.getPlaceholderOption()===b?b:this.parent.getPlaceholder.apply(this,arguments)},setPlaceholder:function(){var a=this.getPlaceholder();if(this.isPlaceholderOptionSelected()&&a!==b){if(this.select&&this.getPlaceholderOption()===b)return;this.selection.find(".select2-chosen").html(this.opts.escapeMarkup(a)),this.selection.addClass("select2-default"),this.container.removeClass("select2-allowclear")}},postprocessResults:function(a,b,c){var d=0,e=this;if(this.findHighlightableChoices().each2(function(a,b){return r(e.id(b.data("select2-data")),e.opts.element.val())?(d=a,!1):void 0}),c!==!1&&(b===!0&&d>=0?this.highlight(d):this.highlight(0)),b===!0){var g=this.opts.minimumResultsForSearch;g>=0&&this.showSearch(L(a.results)>=g)}},showSearch:function(b){this.showSearchInput!==b&&(this.showSearchInput=b,this.dropdown.find(".select2-search").toggleClass("select2-search-hidden",!b),this.dropdown.find(".select2-search").toggleClass("select2-offscreen",!b),a(this.dropdown,this.container).toggleClass("select2-with-searchbox",b))},onSelect:function(a,b){if(this.triggerSelect(a)){var c=this.opts.element.val(),d=this.data();this.opts.element.val(this.id(a)),this.updateSelection(a),this.opts.element.trigger({type:"select2-selected",val:this.id(a),choice:a}),this.nextSearchTerm=this.opts.nextSearchTerm(a,this.search.val()),this.close(),b&&b.noFocus||!this.opts.shouldFocusInput(this)||this.focusser.focus(),r(c,this.id(a))||this.triggerChange({added:a,removed:d})}},updateSelection:function(a){var d,e,c=this.selection.find(".select2-chosen");this.selection.data("select2-data",a),c.empty(),null!==a&&(d=this.opts.formatSelection(a,c,this.opts.escapeMarkup)),d!==b&&c.append(d),e=this.opts.formatSelectionCssClass(a,c),e!==b&&c.addClass(e),this.selection.removeClass("select2-default"),this.opts.allowClear&&this.getPlaceholder()!==b&&this.container.addClass("select2-allowclear")},val:function(){var a,c=!1,d=null,e=this,f=this.data();if(0===arguments.length)return this.opts.element.val();if(a=arguments[0],arguments.length>1&&(c=arguments[1]),this.select)this.select.val(a).find("option").filter(function(){return this.selected}).each2(function(a,b){return d=e.optionToData(b),!1}),this.updateSelection(d),this.setPlaceholder(),c&&this.triggerChange({added:d,removed:f});else{if(!a&&0!==a)return this.clear(c),void 0;if(this.opts.initSelection===b)throw new Error("cannot call val() if initSelection() is not defined");this.opts.element.val(a),this.opts.initSelection(this.opts.element,function(a){e.opts.element.val(a?e.id(a):""),e.updateSelection(a),e.setPlaceholder(),c&&e.triggerChange({added:a,removed:f})})}},clearSearch:function(){this.search.val(""),this.focusser.val("")},data:function(a){var c,d=!1;return 0===arguments.length?(c=this.selection.data("select2-data"),c==b&&(c=null),c):(arguments.length>1&&(d=arguments[1]),a?(c=this.data(),this.opts.element.val(a?this.id(a):""),this.updateSelection(a),d&&this.triggerChange({added:a,removed:c})):this.clear(d),void 0)}}),f=O(d,{createContainer:function(){var b=a(document.createElement("div")).attr({"class":"select2-container select2-container-multi"}).html(["<ul class='select2-choices'>","  <li class='select2-search-field'>","    <label for='' class='select2-offscreen'></label>","    <input type='text' autocomplete='off' autocorrect='off' autocapitalize='off' spellcheck='false' class='select2-input'>","  </li>","</ul>","<div class='select2-drop select2-drop-multi select2-display-none'>","   <ul class='select2-results'>","   </ul>","</div>"].join(""));return b},prepareOpts:function(){var b=this.parent.prepareOpts.apply(this,arguments),c=this;return"select"===b.element.get(0).tagName.toLowerCase()?b.initSelection=function(a,b){var d=[];a.find("option").filter(function(){return this.selected&&!this.disabled}).each2(function(a,b){d.push(c.optionToData(b))}),b(d)}:"data"in b&&(b.initSelection=b.initSelection||function(c,d){var e=s(c.val(),b.separator),f=[];b.query({matcher:function(c,d,g){var h=a.grep(e,function(a){return r(a,b.id(g))}).length;return h&&f.push(g),h},callback:a.isFunction(d)?function(){for(var a=[],c=0;c<e.length;c++)for(var g=e[c],h=0;h<f.length;h++){var i=f[h];if(r(g,b.id(i))){a.push(i),f.splice(h,1);break}}d(a)}:a.noop})}),b},selectChoice:function(a){var b=this.container.find(".select2-search-choice-focus");b.length&&a&&a[0]==b[0]||(b.length&&this.opts.element.trigger("choice-deselected",b),b.removeClass("select2-search-choice-focus"),a&&a.length&&(this.close(),a.addClass("select2-search-choice-focus"),this.opts.element.trigger("choice-selected",a)))},destroy:function(){a("label[for='"+this.search.attr("id")+"']").attr("for",this.opts.element.attr("id")),this.parent.destroy.apply(this,arguments),N.call(this,"searchContainer","selection")},initContainer:function(){var d,b=".select2-choices";this.searchContainer=this.container.find(".select2-search-field"),this.selection=d=this.container.find(b);var e=this;this.selection.on("click",".select2-search-choice:not(.select2-locked)",function(){e.search[0].focus(),e.selectChoice(a(this))}),this.search.attr("id","s2id_autogen"+g()),this.search.prev().text(a("label[for='"+this.opts.element.attr("id")+"']").text()).attr("for",this.search.attr("id")),this.search.on("input paste",this.bind(function(){this.search.attr("placeholder")&&0==this.search.val().length||this.isInterfaceEnabled()&&(this.opened()||this.open())})),this.search.attr("tabindex",this.elementTabIndex),this.keydowns=0,this.search.on("keydown",this.bind(function(a){if(this.isInterfaceEnabled()){++this.keydowns;var b=d.find(".select2-search-choice-focus"),e=b.prev(".select2-search-choice:not(.select2-locked)"),f=b.next(".select2-search-choice:not(.select2-locked)"),g=z(this.search);if(b.length&&(a.which==c.LEFT||a.which==c.RIGHT||a.which==c.BACKSPACE||a.which==c.DELETE||a.which==c.ENTER)){var h=b;return a.which==c.LEFT&&e.length?h=e:a.which==c.RIGHT?h=f.length?f:null:a.which===c.BACKSPACE?this.unselect(b.first())&&(this.search.width(10),h=e.length?e:f):a.which==c.DELETE?this.unselect(b.first())&&(this.search.width(10),h=f.length?f:null):a.which==c.ENTER&&(h=null),this.selectChoice(h),A(a),h&&h.length||this.open(),void 0}if((a.which===c.BACKSPACE&&1==this.keydowns||a.which==c.LEFT)&&0==g.offset&&!g.length)return this.selectChoice(d.find(".select2-search-choice:not(.select2-locked)").last()),A(a),void 0;if(this.selectChoice(null),this.opened())switch(a.which){case c.UP:case c.DOWN:return this.moveHighlight(a.which===c.UP?-1:1),A(a),void 0;case c.ENTER:return this.selectHighlighted(),A(a),void 0;case c.TAB:return this.selectHighlighted({noFocus:!0}),this.close(),void 0;case c.ESC:return this.cancel(a),A(a),void 0}if(a.which!==c.TAB&&!c.isControl(a)&&!c.isFunctionKey(a)&&a.which!==c.BACKSPACE&&a.which!==c.ESC){if(a.which===c.ENTER){if(this.opts.openOnEnter===!1)return;if(a.altKey||a.ctrlKey||a.shiftKey||a.metaKey)return}this.open(),(a.which===c.PAGE_UP||a.which===c.PAGE_DOWN)&&A(a),a.which===c.ENTER&&A(a)}}})),this.search.on("keyup",this.bind(function(){this.keydowns=0,this.resizeSearch()})),this.search.on("blur",this.bind(function(b){this.container.removeClass("select2-container-active"),this.search.removeClass("select2-focused"),this.selectChoice(null),this.opened()||this.clearSearch(),b.stopImmediatePropagation(),this.opts.element.trigger(a.Event("select2-blur"))})),this.container.on("click",b,this.bind(function(b){this.isInterfaceEnabled()&&(a(b.target).closest(".select2-search-choice").length>0||(this.selectChoice(null),this.clearPlaceholder(),this.container.hasClass("select2-container-active")||this.opts.element.trigger(a.Event("select2-focus")),this.open(),this.focusSearch(),b.preventDefault()))})),this.container.on("focus",b,this.bind(function(){this.isInterfaceEnabled()&&(this.container.hasClass("select2-container-active")||this.opts.element.trigger(a.Event("select2-focus")),this.container.addClass("select2-container-active"),this.dropdown.addClass("select2-drop-active"),this.clearPlaceholder())})),this.initContainerWidth(),this.opts.element.addClass("select2-offscreen"),this.clearSearch()},enableInterface:function(){this.parent.enableInterface.apply(this,arguments)&&this.search.prop("disabled",!this.isInterfaceEnabled())},initSelection:function(){if(""===this.opts.element.val()&&""===this.opts.element.text()&&(this.updateSelection([]),this.close(),this.clearSearch()),this.select||""!==this.opts.element.val()){var c=this;this.opts.initSelection.call(null,this.opts.element,function(a){a!==b&&null!==a&&(c.updateSelection(a),c.close(),c.clearSearch())})}},clearSearch:function(){var a=this.getPlaceholder(),c=this.getMaxSearchWidth();a!==b&&0===this.getVal().length&&this.search.hasClass("select2-focused")===!1?(this.search.val(a).addClass("select2-default"),this.search.width(c>0?c:this.container.css("width"))):this.search.val("").width(10)},clearPlaceholder:function(){this.search.hasClass("select2-default")&&this.search.val("").removeClass("select2-default")},opening:function(){this.clearPlaceholder(),this.resizeSearch(),this.parent.opening.apply(this,arguments),this.focusSearch(),""===this.search.val()&&this.nextSearchTerm!=b&&(this.search.val(this.nextSearchTerm),this.search.select()),this.updateResults(!0),this.opts.shouldFocusInput(this)&&this.search.focus(),this.opts.element.trigger(a.Event("select2-open"))},close:function(){this.opened()&&this.parent.close.apply(this,arguments)},focus:function(){this.close(),this.search.focus()},isFocused:function(){return this.search.hasClass("select2-focused")},updateSelection:function(b){var c=[],d=[],e=this;a(b).each(function(){p(e.id(this),c)<0&&(c.push(e.id(this)),d.push(this))}),b=d,this.selection.find(".select2-search-choice").remove(),a(b).each(function(){e.addSelectedChoice(this)}),e.postprocessResults()},tokenize:function(){var a=this.search.val();a=this.opts.tokenizer.call(this,a,this.data(),this.bind(this.onSelect),this.opts),null!=a&&a!=b&&(this.search.val(a),a.length>0&&this.open())},onSelect:function(a,c){this.triggerSelect(a)&&""!==a.text&&(this.addSelectedChoice(a),this.opts.element.trigger({type:"selected",val:this.id(a),choice:a}),this.nextSearchTerm=this.opts.nextSearchTerm(a,this.search.val()),this.clearSearch(),this.updateResults(),(this.select||!this.opts.closeOnSelect)&&this.postprocessResults(a,!1,this.opts.closeOnSelect===!0),this.opts.closeOnSelect?(this.close(),this.search.width(10)):this.countSelectableResults()>0?(this.search.width(10),this.resizeSearch(),this.getMaximumSelectionSize()>0&&this.val().length>=this.getMaximumSelectionSize()?this.updateResults(!0):this.nextSearchTerm!=b&&(this.search.val(this.nextSearchTerm),this.updateResults(),this.search.select()),this.positionDropdown()):(this.close(),this.search.width(10)),this.triggerChange({added:a}),c&&c.noFocus||this.focusSearch())},cancel:function(){this.close(),this.focusSearch()},addSelectedChoice:function(c){var j,k,d=!c.locked,e=a("<li class='select2-search-choice'>    <div></div>    <a href='#' class='select2-search-choice-close' tabindex='-1'></a></li>"),f=a("<li class='select2-search-choice select2-locked'><div></div></li>"),g=d?e:f,h=this.id(c),i=this.getVal();j=this.opts.formatSelection(c,g.find("div"),this.opts.escapeMarkup),j!=b&&g.find("div").replaceWith("<div>"+j+"</div>"),k=this.opts.formatSelectionCssClass(c,g.find("div")),k!=b&&g.addClass(k),d&&g.find(".select2-search-choice-close").on("mousedown",A).on("click dblclick",this.bind(function(b){this.isInterfaceEnabled()&&(this.unselect(a(b.target)),this.selection.find(".select2-search-choice-focus").removeClass("select2-search-choice-focus"),A(b),this.close(),this.focusSearch())})).on("focus",this.bind(function(){this.isInterfaceEnabled()&&(this.container.addClass("select2-container-active"),this.dropdown.addClass("select2-drop-active"))})),g.data("select2-data",c),g.insertBefore(this.searchContainer),i.push(h),this.setVal(i)},unselect:function(b){var d,e,c=this.getVal();if(b=b.closest(".select2-search-choice"),0===b.length)throw"Invalid argument: "+b+". Must be .select2-search-choice";if(d=b.data("select2-data")){var f=a.Event("select2-removing");if(f.val=this.id(d),f.choice=d,this.opts.element.trigger(f),f.isDefaultPrevented())return!1;for(;(e=p(this.id(d),c))>=0;)c.splice(e,1),this.setVal(c),this.select&&this.postprocessResults();return b.remove(),this.opts.element.trigger({type:"select2-removed",val:this.id(d),choice:d}),this.triggerChange({removed:d}),!0}},postprocessResults:function(a,b,c){var d=this.getVal(),e=this.results.find(".select2-result"),f=this.results.find(".select2-result-with-children"),g=this;e.each2(function(a,b){var c=g.id(b.data("select2-data"));p(c,d)>=0&&(b.addClass("select2-selected"),b.find(".select2-result-selectable").addClass("select2-selected"))}),f.each2(function(a,b){b.is(".select2-result-selectable")||0!==b.find(".select2-result-selectable:not(.select2-selected)").length||b.addClass("select2-selected")}),-1==this.highlight()&&c!==!1&&g.highlight(0),!this.opts.createSearchChoice&&!e.filter(".select2-result:not(.select2-selected)").length>0&&(!a||a&&!a.more&&0===this.results.find(".select2-no-results").length)&&J(g.opts.formatNoMatches,"formatNoMatches")&&this.results.append("<li class='select2-no-results'>"+K(g.opts.formatNoMatches,g.opts.element,g.search.val())+"</li>")},getMaxSearchWidth:function(){return this.selection.width()-t(this.search)},resizeSearch:function(){var a,b,c,d,e,f=t(this.search);a=C(this.search)+10,b=this.search.offset().left,c=this.selection.width(),d=this.selection.offset().left,e=c-(b-d)-f,a>e&&(e=c-f),40>e&&(e=c-f),0>=e&&(e=a),this.search.width(Math.floor(e))},getVal:function(){var a;return this.select?(a=this.select.val(),null===a?[]:a):(a=this.opts.element.val(),s(a,this.opts.separator))},setVal:function(b){var c;this.select?this.select.val(b):(c=[],a(b).each(function(){p(this,c)<0&&c.push(this)}),this.opts.element.val(0===c.length?"":c.join(this.opts.separator)))},buildChangeDetails:function(a,b){for(var b=b.slice(0),a=a.slice(0),c=0;c<b.length;c++)for(var d=0;d<a.length;d++)r(this.opts.id(b[c]),this.opts.id(a[d]))&&(b.splice(c,1),c>0&&c--,a.splice(d,1),d--);return{added:b,removed:a}},val:function(c,d){var e,f=this;if(0===arguments.length)return this.getVal();if(e=this.data(),e.length||(e=[]),!c&&0!==c)return this.opts.element.val(""),this.updateSelection([]),this.clearSearch(),d&&this.triggerChange({added:this.data(),removed:e}),void 0;if(this.setVal(c),this.select)this.opts.initSelection(this.select,this.bind(this.updateSelection)),d&&this.triggerChange(this.buildChangeDetails(e,this.data()));else{if(this.opts.initSelection===b)throw new Error("val() cannot be called if initSelection() is not defined");this.opts.initSelection(this.opts.element,function(b){var c=a.map(b,f.id);f.setVal(c),f.updateSelection(b),f.clearSearch(),d&&f.triggerChange(f.buildChangeDetails(e,f.data()))})}this.clearSearch()},onSortStart:function(){if(this.select)throw new Error("Sorting of elements is not supported when attached to <select>. Attach to <input type='hidden'/> instead.");this.search.width(0),this.searchContainer.hide()},onSortEnd:function(){var b=[],c=this;this.searchContainer.show(),this.searchContainer.appendTo(this.searchContainer.parent()),this.resizeSearch(),this.selection.find(".select2-search-choice").each(function(){b.push(c.opts.id(a(this).data("select2-data")))}),this.setVal(b),this.triggerChange()},data:function(b,c){var e,f,d=this;return 0===arguments.length?this.selection.children(".select2-search-choice").map(function(){return a(this).data("select2-data")}).get():(f=this.data(),b||(b=[]),e=a.map(b,function(a){return d.opts.id(a)}),this.setVal(e),this.updateSelection(b),this.clearSearch(),c&&this.triggerChange(this.buildChangeDetails(f,this.data())),void 0)}}),a.fn.select2=function(){var d,e,f,g,h,c=Array.prototype.slice.call(arguments,0),i=["val","destroy","opened","open","close","focus","isFocused","container","dropdown","onSortStart","onSortEnd","enable","disable","readonly","positionDropdown","data","search"],j=["opened","isFocused","container","dropdown"],k=["val","data"],l={search:"externalSearch"};return this.each(function(){if(0===c.length||"object"==typeof c[0])d=0===c.length?{}:a.extend({},c[0]),d.element=a(this),"select"===d.element.get(0).tagName.toLowerCase()?h=d.element.prop("multiple"):(h=d.multiple||!1,"tags"in d&&(d.multiple=h=!0)),e=h?new window.Select2["class"].multi:new window.Select2["class"].single,e.init(d);else{if("string"!=typeof c[0])throw"Invalid arguments to select2 plugin: "+c;if(p(c[0],i)<0)throw"Unknown method: "+c[0];if(g=b,e=a(this).data("select2"),e===b)return;if(f=c[0],"container"===f?g=e.container:"dropdown"===f?g=e.dropdown:(l[f]&&(f=l[f]),g=e[f].apply(e,c.slice(1))),p(c[0],j)>=0||p(c[0],k)>=0&&1==c.length)return!1}}),g===b?this:g},a.fn.select2.defaults={width:"copy",loadMorePadding:0,closeOnSelect:!0,openOnEnter:!0,containerCss:{},dropdownCss:{},containerCssClass:"",dropdownCssClass:"",formatResult:function(a,b,c,d){var e=[];return E(a.text,c.term,e,d),e.join("")},formatSelection:function(a,c,d){return a?d(a.text):b},sortResults:function(a){return a},formatResultCssClass:function(a){return a.css},formatSelectionCssClass:function(){return b},minimumResultsForSearch:0,minimumInputLength:0,maximumInputLength:null,maximumSelectionSize:0,id:function(a){return a==b?null:a.id},matcher:function(a,b){return o(""+b).toUpperCase().indexOf(o(""+a).toUpperCase())>=0},separator:",",tokenSeparators:[],tokenizer:M,escapeMarkup:F,blurOnChange:!1,selectOnBlur:!1,adaptContainerCssClass:function(a){return a},adaptDropdownCssClass:function(){return null},nextSearchTerm:function(){return b},searchInputPlaceholder:"",createSearchChoicePosition:"top",shouldFocusInput:function(a){var b="ontouchstart"in window||navigator.msMaxTouchPoints>0;return b?a.opts.minimumResultsForSearch<0?!1:!0:!0}},a.fn.select2.locales=[],a.fn.select2.locales.en={formatMatches:function(a){return 1===a?"One result is available, press enter to select it.":a+" results are available, use up and down arrow keys to navigate."
},formatNoMatches:function(){return"No matches found"},formatAjaxError:function(){return"Loading failed"},formatInputTooShort:function(a,b){var c=b-a.length;return"Please enter "+c+" or more character"+(1==c?"":"s")},formatInputTooLong:function(a,b){var c=a.length-b;return"Please delete "+c+" character"+(1==c?"":"s")},formatSelectionTooBig:function(a){return"You can only select "+a+" item"+(1==a?"":"s")},formatLoadMore:function(){return"Loading more results\u2026"},formatSearching:function(){return"Searching\u2026"}},a.extend(a.fn.select2.defaults,a.fn.select2.locales.en),a.fn.select2.ajaxDefaults={transport:a.ajax,params:{type:"GET",cache:!1,dataType:"json"}},window.Select2={query:{ajax:G,local:H,tags:I},util:{debounce:w,markMatch:E,escapeMarkup:F,stripDiacritics:o},"class":{"abstract":d,single:e,multi:f}}}}(jQuery);;/*! jQuery UI - v1.11.1 - 2014-09-18
* http://jqueryui.com
* Includes: core.js, widget.js, mouse.js, position.js, draggable.js, droppable.js, resizable.js, selectable.js, sortable.js, slider.js
* Copyright 2014 jQuery Foundation and other contributors; Licensed MIT */

(function(e){"function"==typeof define&&define.amd?define(["jquery"],e):e(jQuery)})(function(e){function t(t,s){var n,a,o,r=t.nodeName.toLowerCase();return"area"===r?(n=t.parentNode,a=n.name,t.href&&a&&"map"===n.nodeName.toLowerCase()?(o=e("img[usemap='#"+a+"']")[0],!!o&&i(o)):!1):(/input|select|textarea|button|object/.test(r)?!t.disabled:"a"===r?t.href||s:s)&&i(t)}function i(t){return e.expr.filters.visible(t)&&!e(t).parents().addBack().filter(function(){return"hidden"===e.css(this,"visibility")}).length}e.ui=e.ui||{},e.extend(e.ui,{version:"1.11.1",keyCode:{BACKSPACE:8,COMMA:188,DELETE:46,DOWN:40,END:35,ENTER:13,ESCAPE:27,HOME:36,LEFT:37,PAGE_DOWN:34,PAGE_UP:33,PERIOD:190,RIGHT:39,SPACE:32,TAB:9,UP:38}}),e.fn.extend({scrollParent:function(t){var i=this.css("position"),s="absolute"===i,n=t?/(auto|scroll|hidden)/:/(auto|scroll)/,a=this.parents().filter(function(){var t=e(this);return s&&"static"===t.css("position")?!1:n.test(t.css("overflow")+t.css("overflow-y")+t.css("overflow-x"))}).eq(0);return"fixed"!==i&&a.length?a:e(this[0].ownerDocument||document)},uniqueId:function(){var e=0;return function(){return this.each(function(){this.id||(this.id="ui-id-"+ ++e)})}}(),removeUniqueId:function(){return this.each(function(){/^ui-id-\d+$/.test(this.id)&&e(this).removeAttr("id")})}}),e.extend(e.expr[":"],{data:e.expr.createPseudo?e.expr.createPseudo(function(t){return function(i){return!!e.data(i,t)}}):function(t,i,s){return!!e.data(t,s[3])},focusable:function(i){return t(i,!isNaN(e.attr(i,"tabindex")))},tabbable:function(i){var s=e.attr(i,"tabindex"),n=isNaN(s);return(n||s>=0)&&t(i,!n)}}),e("<a>").outerWidth(1).jquery||e.each(["Width","Height"],function(t,i){function s(t,i,s,a){return e.each(n,function(){i-=parseFloat(e.css(t,"padding"+this))||0,s&&(i-=parseFloat(e.css(t,"border"+this+"Width"))||0),a&&(i-=parseFloat(e.css(t,"margin"+this))||0)}),i}var n="Width"===i?["Left","Right"]:["Top","Bottom"],a=i.toLowerCase(),o={innerWidth:e.fn.innerWidth,innerHeight:e.fn.innerHeight,outerWidth:e.fn.outerWidth,outerHeight:e.fn.outerHeight};e.fn["inner"+i]=function(t){return void 0===t?o["inner"+i].call(this):this.each(function(){e(this).css(a,s(this,t)+"px")})},e.fn["outer"+i]=function(t,n){return"number"!=typeof t?o["outer"+i].call(this,t):this.each(function(){e(this).css(a,s(this,t,!0,n)+"px")})}}),e.fn.addBack||(e.fn.addBack=function(e){return this.add(null==e?this.prevObject:this.prevObject.filter(e))}),e("<a>").data("a-b","a").removeData("a-b").data("a-b")&&(e.fn.removeData=function(t){return function(i){return arguments.length?t.call(this,e.camelCase(i)):t.call(this)}}(e.fn.removeData)),e.ui.ie=!!/msie [\w.]+/.exec(navigator.userAgent.toLowerCase()),e.fn.extend({focus:function(t){return function(i,s){return"number"==typeof i?this.each(function(){var t=this;setTimeout(function(){e(t).focus(),s&&s.call(t)},i)}):t.apply(this,arguments)}}(e.fn.focus),disableSelection:function(){var e="onselectstart"in document.createElement("div")?"selectstart":"mousedown";return function(){return this.bind(e+".ui-disableSelection",function(e){e.preventDefault()})}}(),enableSelection:function(){return this.unbind(".ui-disableSelection")},zIndex:function(t){if(void 0!==t)return this.css("zIndex",t);if(this.length)for(var i,s,n=e(this[0]);n.length&&n[0]!==document;){if(i=n.css("position"),("absolute"===i||"relative"===i||"fixed"===i)&&(s=parseInt(n.css("zIndex"),10),!isNaN(s)&&0!==s))return s;n=n.parent()}return 0}}),e.ui.plugin={add:function(t,i,s){var n,a=e.ui[t].prototype;for(n in s)a.plugins[n]=a.plugins[n]||[],a.plugins[n].push([i,s[n]])},call:function(e,t,i,s){var n,a=e.plugins[t];if(a&&(s||e.element[0].parentNode&&11!==e.element[0].parentNode.nodeType))for(n=0;a.length>n;n++)e.options[a[n][0]]&&a[n][1].apply(e.element,i)}};var s=0,n=Array.prototype.slice;e.cleanData=function(t){return function(i){var s,n,a;for(a=0;null!=(n=i[a]);a++)try{s=e._data(n,"events"),s&&s.remove&&e(n).triggerHandler("remove")}catch(o){}t(i)}}(e.cleanData),e.widget=function(t,i,s){var n,a,o,r,h={},l=t.split(".")[0];return t=t.split(".")[1],n=l+"-"+t,s||(s=i,i=e.Widget),e.expr[":"][n.toLowerCase()]=function(t){return!!e.data(t,n)},e[l]=e[l]||{},a=e[l][t],o=e[l][t]=function(e,t){return this._createWidget?(arguments.length&&this._createWidget(e,t),void 0):new o(e,t)},e.extend(o,a,{version:s.version,_proto:e.extend({},s),_childConstructors:[]}),r=new i,r.options=e.widget.extend({},r.options),e.each(s,function(t,s){return e.isFunction(s)?(h[t]=function(){var e=function(){return i.prototype[t].apply(this,arguments)},n=function(e){return i.prototype[t].apply(this,e)};return function(){var t,i=this._super,a=this._superApply;return this._super=e,this._superApply=n,t=s.apply(this,arguments),this._super=i,this._superApply=a,t}}(),void 0):(h[t]=s,void 0)}),o.prototype=e.widget.extend(r,{widgetEventPrefix:a?r.widgetEventPrefix||t:t},h,{constructor:o,namespace:l,widgetName:t,widgetFullName:n}),a?(e.each(a._childConstructors,function(t,i){var s=i.prototype;e.widget(s.namespace+"."+s.widgetName,o,i._proto)}),delete a._childConstructors):i._childConstructors.push(o),e.widget.bridge(t,o),o},e.widget.extend=function(t){for(var i,s,a=n.call(arguments,1),o=0,r=a.length;r>o;o++)for(i in a[o])s=a[o][i],a[o].hasOwnProperty(i)&&void 0!==s&&(t[i]=e.isPlainObject(s)?e.isPlainObject(t[i])?e.widget.extend({},t[i],s):e.widget.extend({},s):s);return t},e.widget.bridge=function(t,i){var s=i.prototype.widgetFullName||t;e.fn[t]=function(a){var o="string"==typeof a,r=n.call(arguments,1),h=this;return a=!o&&r.length?e.widget.extend.apply(null,[a].concat(r)):a,o?this.each(function(){var i,n=e.data(this,s);return"instance"===a?(h=n,!1):n?e.isFunction(n[a])&&"_"!==a.charAt(0)?(i=n[a].apply(n,r),i!==n&&void 0!==i?(h=i&&i.jquery?h.pushStack(i.get()):i,!1):void 0):e.error("no such method '"+a+"' for "+t+" widget instance"):e.error("cannot call methods on "+t+" prior to initialization; "+"attempted to call method '"+a+"'")}):this.each(function(){var t=e.data(this,s);t?(t.option(a||{}),t._init&&t._init()):e.data(this,s,new i(a,this))}),h}},e.Widget=function(){},e.Widget._childConstructors=[],e.Widget.prototype={widgetName:"widget",widgetEventPrefix:"",defaultElement:"<div>",options:{disabled:!1,create:null},_createWidget:function(t,i){i=e(i||this.defaultElement||this)[0],this.element=e(i),this.uuid=s++,this.eventNamespace="."+this.widgetName+this.uuid,this.options=e.widget.extend({},this.options,this._getCreateOptions(),t),this.bindings=e(),this.hoverable=e(),this.focusable=e(),i!==this&&(e.data(i,this.widgetFullName,this),this._on(!0,this.element,{remove:function(e){e.target===i&&this.destroy()}}),this.document=e(i.style?i.ownerDocument:i.document||i),this.window=e(this.document[0].defaultView||this.document[0].parentWindow)),this._create(),this._trigger("create",null,this._getCreateEventData()),this._init()},_getCreateOptions:e.noop,_getCreateEventData:e.noop,_create:e.noop,_init:e.noop,destroy:function(){this._destroy(),this.element.unbind(this.eventNamespace).removeData(this.widgetFullName).removeData(e.camelCase(this.widgetFullName)),this.widget().unbind(this.eventNamespace).removeAttr("aria-disabled").removeClass(this.widgetFullName+"-disabled "+"ui-state-disabled"),this.bindings.unbind(this.eventNamespace),this.hoverable.removeClass("ui-state-hover"),this.focusable.removeClass("ui-state-focus")},_destroy:e.noop,widget:function(){return this.element},option:function(t,i){var s,n,a,o=t;if(0===arguments.length)return e.widget.extend({},this.options);if("string"==typeof t)if(o={},s=t.split("."),t=s.shift(),s.length){for(n=o[t]=e.widget.extend({},this.options[t]),a=0;s.length-1>a;a++)n[s[a]]=n[s[a]]||{},n=n[s[a]];if(t=s.pop(),1===arguments.length)return void 0===n[t]?null:n[t];n[t]=i}else{if(1===arguments.length)return void 0===this.options[t]?null:this.options[t];o[t]=i}return this._setOptions(o),this},_setOptions:function(e){var t;for(t in e)this._setOption(t,e[t]);return this},_setOption:function(e,t){return this.options[e]=t,"disabled"===e&&(this.widget().toggleClass(this.widgetFullName+"-disabled",!!t),t&&(this.hoverable.removeClass("ui-state-hover"),this.focusable.removeClass("ui-state-focus"))),this},enable:function(){return this._setOptions({disabled:!1})},disable:function(){return this._setOptions({disabled:!0})},_on:function(t,i,s){var n,a=this;"boolean"!=typeof t&&(s=i,i=t,t=!1),s?(i=n=e(i),this.bindings=this.bindings.add(i)):(s=i,i=this.element,n=this.widget()),e.each(s,function(s,o){function r(){return t||a.options.disabled!==!0&&!e(this).hasClass("ui-state-disabled")?("string"==typeof o?a[o]:o).apply(a,arguments):void 0}"string"!=typeof o&&(r.guid=o.guid=o.guid||r.guid||e.guid++);var h=s.match(/^([\w:-]*)\s*(.*)$/),l=h[1]+a.eventNamespace,u=h[2];u?n.delegate(u,l,r):i.bind(l,r)})},_off:function(e,t){t=(t||"").split(" ").join(this.eventNamespace+" ")+this.eventNamespace,e.unbind(t).undelegate(t)},_delay:function(e,t){function i(){return("string"==typeof e?s[e]:e).apply(s,arguments)}var s=this;return setTimeout(i,t||0)},_hoverable:function(t){this.hoverable=this.hoverable.add(t),this._on(t,{mouseenter:function(t){e(t.currentTarget).addClass("ui-state-hover")},mouseleave:function(t){e(t.currentTarget).removeClass("ui-state-hover")}})},_focusable:function(t){this.focusable=this.focusable.add(t),this._on(t,{focusin:function(t){e(t.currentTarget).addClass("ui-state-focus")},focusout:function(t){e(t.currentTarget).removeClass("ui-state-focus")}})},_trigger:function(t,i,s){var n,a,o=this.options[t];if(s=s||{},i=e.Event(i),i.type=(t===this.widgetEventPrefix?t:this.widgetEventPrefix+t).toLowerCase(),i.target=this.element[0],a=i.originalEvent)for(n in a)n in i||(i[n]=a[n]);return this.element.trigger(i,s),!(e.isFunction(o)&&o.apply(this.element[0],[i].concat(s))===!1||i.isDefaultPrevented())}},e.each({show:"fadeIn",hide:"fadeOut"},function(t,i){e.Widget.prototype["_"+t]=function(s,n,a){"string"==typeof n&&(n={effect:n});var o,r=n?n===!0||"number"==typeof n?i:n.effect||i:t;n=n||{},"number"==typeof n&&(n={duration:n}),o=!e.isEmptyObject(n),n.complete=a,n.delay&&s.delay(n.delay),o&&e.effects&&e.effects.effect[r]?s[t](n):r!==t&&s[r]?s[r](n.duration,n.easing,a):s.queue(function(i){e(this)[t](),a&&a.call(s[0]),i()})}}),e.widget;var a=!1;e(document).mouseup(function(){a=!1}),e.widget("ui.mouse",{version:"1.11.1",options:{cancel:"input,textarea,button,select,option",distance:1,delay:0},_mouseInit:function(){var t=this;this.element.bind("mousedown."+this.widgetName,function(e){return t._mouseDown(e)}).bind("click."+this.widgetName,function(i){return!0===e.data(i.target,t.widgetName+".preventClickEvent")?(e.removeData(i.target,t.widgetName+".preventClickEvent"),i.stopImmediatePropagation(),!1):void 0}),this.started=!1},_mouseDestroy:function(){this.element.unbind("."+this.widgetName),this._mouseMoveDelegate&&this.document.unbind("mousemove."+this.widgetName,this._mouseMoveDelegate).unbind("mouseup."+this.widgetName,this._mouseUpDelegate)},_mouseDown:function(t){if(!a){this._mouseStarted&&this._mouseUp(t),this._mouseDownEvent=t;var i=this,s=1===t.which,n="string"==typeof this.options.cancel&&t.target.nodeName?e(t.target).closest(this.options.cancel).length:!1;return s&&!n&&this._mouseCapture(t)?(this.mouseDelayMet=!this.options.delay,this.mouseDelayMet||(this._mouseDelayTimer=setTimeout(function(){i.mouseDelayMet=!0},this.options.delay)),this._mouseDistanceMet(t)&&this._mouseDelayMet(t)&&(this._mouseStarted=this._mouseStart(t)!==!1,!this._mouseStarted)?(t.preventDefault(),!0):(!0===e.data(t.target,this.widgetName+".preventClickEvent")&&e.removeData(t.target,this.widgetName+".preventClickEvent"),this._mouseMoveDelegate=function(e){return i._mouseMove(e)},this._mouseUpDelegate=function(e){return i._mouseUp(e)},this.document.bind("mousemove."+this.widgetName,this._mouseMoveDelegate).bind("mouseup."+this.widgetName,this._mouseUpDelegate),t.preventDefault(),a=!0,!0)):!0}},_mouseMove:function(t){return e.ui.ie&&(!document.documentMode||9>document.documentMode)&&!t.button?this._mouseUp(t):t.which?this._mouseStarted?(this._mouseDrag(t),t.preventDefault()):(this._mouseDistanceMet(t)&&this._mouseDelayMet(t)&&(this._mouseStarted=this._mouseStart(this._mouseDownEvent,t)!==!1,this._mouseStarted?this._mouseDrag(t):this._mouseUp(t)),!this._mouseStarted):this._mouseUp(t)},_mouseUp:function(t){return this.document.unbind("mousemove."+this.widgetName,this._mouseMoveDelegate).unbind("mouseup."+this.widgetName,this._mouseUpDelegate),this._mouseStarted&&(this._mouseStarted=!1,t.target===this._mouseDownEvent.target&&e.data(t.target,this.widgetName+".preventClickEvent",!0),this._mouseStop(t)),a=!1,!1},_mouseDistanceMet:function(e){return Math.max(Math.abs(this._mouseDownEvent.pageX-e.pageX),Math.abs(this._mouseDownEvent.pageY-e.pageY))>=this.options.distance},_mouseDelayMet:function(){return this.mouseDelayMet},_mouseStart:function(){},_mouseDrag:function(){},_mouseStop:function(){},_mouseCapture:function(){return!0}}),function(){function t(e,t,i){return[parseFloat(e[0])*(p.test(e[0])?t/100:1),parseFloat(e[1])*(p.test(e[1])?i/100:1)]}function i(t,i){return parseInt(e.css(t,i),10)||0}function s(t){var i=t[0];return 9===i.nodeType?{width:t.width(),height:t.height(),offset:{top:0,left:0}}:e.isWindow(i)?{width:t.width(),height:t.height(),offset:{top:t.scrollTop(),left:t.scrollLeft()}}:i.preventDefault?{width:0,height:0,offset:{top:i.pageY,left:i.pageX}}:{width:t.outerWidth(),height:t.outerHeight(),offset:t.offset()}}e.ui=e.ui||{};var n,a,o=Math.max,r=Math.abs,h=Math.round,l=/left|center|right/,u=/top|center|bottom/,d=/[\+\-]\d+(\.[\d]+)?%?/,c=/^\w+/,p=/%$/,f=e.fn.position;e.position={scrollbarWidth:function(){if(void 0!==n)return n;var t,i,s=e("<div style='display:block;position:absolute;width:50px;height:50px;overflow:hidden;'><div style='height:100px;width:auto;'></div></div>"),a=s.children()[0];return e("body").append(s),t=a.offsetWidth,s.css("overflow","scroll"),i=a.offsetWidth,t===i&&(i=s[0].clientWidth),s.remove(),n=t-i},getScrollInfo:function(t){var i=t.isWindow||t.isDocument?"":t.element.css("overflow-x"),s=t.isWindow||t.isDocument?"":t.element.css("overflow-y"),n="scroll"===i||"auto"===i&&t.width<t.element[0].scrollWidth,a="scroll"===s||"auto"===s&&t.height<t.element[0].scrollHeight;return{width:a?e.position.scrollbarWidth():0,height:n?e.position.scrollbarWidth():0}},getWithinInfo:function(t){var i=e(t||window),s=e.isWindow(i[0]),n=!!i[0]&&9===i[0].nodeType;return{element:i,isWindow:s,isDocument:n,offset:i.offset()||{left:0,top:0},scrollLeft:i.scrollLeft(),scrollTop:i.scrollTop(),width:s||n?i.width():i.outerWidth(),height:s||n?i.height():i.outerHeight()}}},e.fn.position=function(n){if(!n||!n.of)return f.apply(this,arguments);n=e.extend({},n);var p,m,g,v,y,b,_=e(n.of),x=e.position.getWithinInfo(n.within),w=e.position.getScrollInfo(x),k=(n.collision||"flip").split(" "),T={};return b=s(_),_[0].preventDefault&&(n.at="left top"),m=b.width,g=b.height,v=b.offset,y=e.extend({},v),e.each(["my","at"],function(){var e,t,i=(n[this]||"").split(" ");1===i.length&&(i=l.test(i[0])?i.concat(["center"]):u.test(i[0])?["center"].concat(i):["center","center"]),i[0]=l.test(i[0])?i[0]:"center",i[1]=u.test(i[1])?i[1]:"center",e=d.exec(i[0]),t=d.exec(i[1]),T[this]=[e?e[0]:0,t?t[0]:0],n[this]=[c.exec(i[0])[0],c.exec(i[1])[0]]}),1===k.length&&(k[1]=k[0]),"right"===n.at[0]?y.left+=m:"center"===n.at[0]&&(y.left+=m/2),"bottom"===n.at[1]?y.top+=g:"center"===n.at[1]&&(y.top+=g/2),p=t(T.at,m,g),y.left+=p[0],y.top+=p[1],this.each(function(){var s,l,u=e(this),d=u.outerWidth(),c=u.outerHeight(),f=i(this,"marginLeft"),b=i(this,"marginTop"),D=d+f+i(this,"marginRight")+w.width,S=c+b+i(this,"marginBottom")+w.height,N=e.extend({},y),M=t(T.my,u.outerWidth(),u.outerHeight());"right"===n.my[0]?N.left-=d:"center"===n.my[0]&&(N.left-=d/2),"bottom"===n.my[1]?N.top-=c:"center"===n.my[1]&&(N.top-=c/2),N.left+=M[0],N.top+=M[1],a||(N.left=h(N.left),N.top=h(N.top)),s={marginLeft:f,marginTop:b},e.each(["left","top"],function(t,i){e.ui.position[k[t]]&&e.ui.position[k[t]][i](N,{targetWidth:m,targetHeight:g,elemWidth:d,elemHeight:c,collisionPosition:s,collisionWidth:D,collisionHeight:S,offset:[p[0]+M[0],p[1]+M[1]],my:n.my,at:n.at,within:x,elem:u})}),n.using&&(l=function(e){var t=v.left-N.left,i=t+m-d,s=v.top-N.top,a=s+g-c,h={target:{element:_,left:v.left,top:v.top,width:m,height:g},element:{element:u,left:N.left,top:N.top,width:d,height:c},horizontal:0>i?"left":t>0?"right":"center",vertical:0>a?"top":s>0?"bottom":"middle"};d>m&&m>r(t+i)&&(h.horizontal="center"),c>g&&g>r(s+a)&&(h.vertical="middle"),h.important=o(r(t),r(i))>o(r(s),r(a))?"horizontal":"vertical",n.using.call(this,e,h)}),u.offset(e.extend(N,{using:l}))})},e.ui.position={fit:{left:function(e,t){var i,s=t.within,n=s.isWindow?s.scrollLeft:s.offset.left,a=s.width,r=e.left-t.collisionPosition.marginLeft,h=n-r,l=r+t.collisionWidth-a-n;t.collisionWidth>a?h>0&&0>=l?(i=e.left+h+t.collisionWidth-a-n,e.left+=h-i):e.left=l>0&&0>=h?n:h>l?n+a-t.collisionWidth:n:h>0?e.left+=h:l>0?e.left-=l:e.left=o(e.left-r,e.left)},top:function(e,t){var i,s=t.within,n=s.isWindow?s.scrollTop:s.offset.top,a=t.within.height,r=e.top-t.collisionPosition.marginTop,h=n-r,l=r+t.collisionHeight-a-n;t.collisionHeight>a?h>0&&0>=l?(i=e.top+h+t.collisionHeight-a-n,e.top+=h-i):e.top=l>0&&0>=h?n:h>l?n+a-t.collisionHeight:n:h>0?e.top+=h:l>0?e.top-=l:e.top=o(e.top-r,e.top)}},flip:{left:function(e,t){var i,s,n=t.within,a=n.offset.left+n.scrollLeft,o=n.width,h=n.isWindow?n.scrollLeft:n.offset.left,l=e.left-t.collisionPosition.marginLeft,u=l-h,d=l+t.collisionWidth-o-h,c="left"===t.my[0]?-t.elemWidth:"right"===t.my[0]?t.elemWidth:0,p="left"===t.at[0]?t.targetWidth:"right"===t.at[0]?-t.targetWidth:0,f=-2*t.offset[0];0>u?(i=e.left+c+p+f+t.collisionWidth-o-a,(0>i||r(u)>i)&&(e.left+=c+p+f)):d>0&&(s=e.left-t.collisionPosition.marginLeft+c+p+f-h,(s>0||d>r(s))&&(e.left+=c+p+f))},top:function(e,t){var i,s,n=t.within,a=n.offset.top+n.scrollTop,o=n.height,h=n.isWindow?n.scrollTop:n.offset.top,l=e.top-t.collisionPosition.marginTop,u=l-h,d=l+t.collisionHeight-o-h,c="top"===t.my[1],p=c?-t.elemHeight:"bottom"===t.my[1]?t.elemHeight:0,f="top"===t.at[1]?t.targetHeight:"bottom"===t.at[1]?-t.targetHeight:0,m=-2*t.offset[1];0>u?(s=e.top+p+f+m+t.collisionHeight-o-a,e.top+p+f+m>u&&(0>s||r(u)>s)&&(e.top+=p+f+m)):d>0&&(i=e.top-t.collisionPosition.marginTop+p+f+m-h,e.top+p+f+m>d&&(i>0||d>r(i))&&(e.top+=p+f+m))}},flipfit:{left:function(){e.ui.position.flip.left.apply(this,arguments),e.ui.position.fit.left.apply(this,arguments)},top:function(){e.ui.position.flip.top.apply(this,arguments),e.ui.position.fit.top.apply(this,arguments)}}},function(){var t,i,s,n,o,r=document.getElementsByTagName("body")[0],h=document.createElement("div");t=document.createElement(r?"div":"body"),s={visibility:"hidden",width:0,height:0,border:0,margin:0,background:"none"},r&&e.extend(s,{position:"absolute",left:"-1000px",top:"-1000px"});for(o in s)t.style[o]=s[o];t.appendChild(h),i=r||document.documentElement,i.insertBefore(t,i.firstChild),h.style.cssText="position: absolute; left: 10.7432222px;",n=e(h).offset().left,a=n>10&&11>n,t.innerHTML="",i.removeChild(t)}()}(),e.ui.position,e.widget("ui.draggable",e.ui.mouse,{version:"1.11.1",widgetEventPrefix:"drag",options:{addClasses:!0,appendTo:"parent",axis:!1,connectToSortable:!1,containment:!1,cursor:"auto",cursorAt:!1,grid:!1,handle:!1,helper:"original",iframeFix:!1,opacity:!1,refreshPositions:!1,revert:!1,revertDuration:500,scope:"default",scroll:!0,scrollSensitivity:20,scrollSpeed:20,snap:!1,snapMode:"both",snapTolerance:20,stack:!1,zIndex:!1,drag:null,start:null,stop:null},_create:function(){"original"!==this.options.helper||/^(?:r|a|f)/.test(this.element.css("position"))||(this.element[0].style.position="relative"),this.options.addClasses&&this.element.addClass("ui-draggable"),this.options.disabled&&this.element.addClass("ui-draggable-disabled"),this._setHandleClassName(),this._mouseInit()},_setOption:function(e,t){this._super(e,t),"handle"===e&&(this._removeHandleClassName(),this._setHandleClassName())},_destroy:function(){return(this.helper||this.element).is(".ui-draggable-dragging")?(this.destroyOnClear=!0,void 0):(this.element.removeClass("ui-draggable ui-draggable-dragging ui-draggable-disabled"),this._removeHandleClassName(),this._mouseDestroy(),void 0)},_mouseCapture:function(t){var i=this.document[0],s=this.options;try{i.activeElement&&"body"!==i.activeElement.nodeName.toLowerCase()&&e(i.activeElement).blur()}catch(n){}return this.helper||s.disabled||e(t.target).closest(".ui-resizable-handle").length>0?!1:(this.handle=this._getHandle(t),this.handle?(e(s.iframeFix===!0?"iframe":s.iframeFix).each(function(){e("<div class='ui-draggable-iframeFix' style='background: #fff;'></div>").css({width:this.offsetWidth+"px",height:this.offsetHeight+"px",position:"absolute",opacity:"0.001",zIndex:1e3}).css(e(this).offset()).appendTo("body")}),!0):!1)},_mouseStart:function(t){var i=this.options;return this.helper=this._createHelper(t),this.helper.addClass("ui-draggable-dragging"),this._cacheHelperProportions(),e.ui.ddmanager&&(e.ui.ddmanager.current=this),this._cacheMargins(),this.cssPosition=this.helper.css("position"),this.scrollParent=this.helper.scrollParent(!0),this.offsetParent=this.helper.offsetParent(),this.offsetParentCssPosition=this.offsetParent.css("position"),this.offset=this.positionAbs=this.element.offset(),this.offset={top:this.offset.top-this.margins.top,left:this.offset.left-this.margins.left},this.offset.scroll=!1,e.extend(this.offset,{click:{left:t.pageX-this.offset.left,top:t.pageY-this.offset.top},parent:this._getParentOffset(),relative:this._getRelativeOffset()}),this.originalPosition=this.position=this._generatePosition(t,!1),this.originalPageX=t.pageX,this.originalPageY=t.pageY,i.cursorAt&&this._adjustOffsetFromHelper(i.cursorAt),this._setContainment(),this._trigger("start",t)===!1?(this._clear(),!1):(this._cacheHelperProportions(),e.ui.ddmanager&&!i.dropBehaviour&&e.ui.ddmanager.prepareOffsets(this,t),this._mouseDrag(t,!0),e.ui.ddmanager&&e.ui.ddmanager.dragStart(this,t),!0)},_mouseDrag:function(t,i){if("fixed"===this.offsetParentCssPosition&&(this.offset.parent=this._getParentOffset()),this.position=this._generatePosition(t,!0),this.positionAbs=this._convertPositionTo("absolute"),!i){var s=this._uiHash();if(this._trigger("drag",t,s)===!1)return this._mouseUp({}),!1;this.position=s.position}return this.helper[0].style.left=this.position.left+"px",this.helper[0].style.top=this.position.top+"px",e.ui.ddmanager&&e.ui.ddmanager.drag(this,t),!1},_mouseStop:function(t){var i=this,s=!1;return e.ui.ddmanager&&!this.options.dropBehaviour&&(s=e.ui.ddmanager.drop(this,t)),this.dropped&&(s=this.dropped,this.dropped=!1),"invalid"===this.options.revert&&!s||"valid"===this.options.revert&&s||this.options.revert===!0||e.isFunction(this.options.revert)&&this.options.revert.call(this.element,s)?e(this.helper).animate(this.originalPosition,parseInt(this.options.revertDuration,10),function(){i._trigger("stop",t)!==!1&&i._clear()}):this._trigger("stop",t)!==!1&&this._clear(),!1},_mouseUp:function(t){return e("div.ui-draggable-iframeFix").each(function(){this.parentNode.removeChild(this)}),e.ui.ddmanager&&e.ui.ddmanager.dragStop(this,t),this.element.focus(),e.ui.mouse.prototype._mouseUp.call(this,t)},cancel:function(){return this.helper.is(".ui-draggable-dragging")?this._mouseUp({}):this._clear(),this},_getHandle:function(t){return this.options.handle?!!e(t.target).closest(this.element.find(this.options.handle)).length:!0},_setHandleClassName:function(){this.handleElement=this.options.handle?this.element.find(this.options.handle):this.element,this.handleElement.addClass("ui-draggable-handle")},_removeHandleClassName:function(){this.handleElement.removeClass("ui-draggable-handle")},_createHelper:function(t){var i=this.options,s=e.isFunction(i.helper)?e(i.helper.apply(this.element[0],[t])):"clone"===i.helper?this.element.clone().removeAttr("id"):this.element;return s.parents("body").length||s.appendTo("parent"===i.appendTo?this.element[0].parentNode:i.appendTo),s[0]===this.element[0]||/(fixed|absolute)/.test(s.css("position"))||s.css("position","absolute"),s},_adjustOffsetFromHelper:function(t){"string"==typeof t&&(t=t.split(" ")),e.isArray(t)&&(t={left:+t[0],top:+t[1]||0}),"left"in t&&(this.offset.click.left=t.left+this.margins.left),"right"in t&&(this.offset.click.left=this.helperProportions.width-t.right+this.margins.left),"top"in t&&(this.offset.click.top=t.top+this.margins.top),"bottom"in t&&(this.offset.click.top=this.helperProportions.height-t.bottom+this.margins.top)},_isRootNode:function(e){return/(html|body)/i.test(e.tagName)||e===this.document[0]},_getParentOffset:function(){var t=this.offsetParent.offset(),i=this.document[0];return"absolute"===this.cssPosition&&this.scrollParent[0]!==i&&e.contains(this.scrollParent[0],this.offsetParent[0])&&(t.left+=this.scrollParent.scrollLeft(),t.top+=this.scrollParent.scrollTop()),this._isRootNode(this.offsetParent[0])&&(t={top:0,left:0}),{top:t.top+(parseInt(this.offsetParent.css("borderTopWidth"),10)||0),left:t.left+(parseInt(this.offsetParent.css("borderLeftWidth"),10)||0)}},_getRelativeOffset:function(){if("relative"!==this.cssPosition)return{top:0,left:0};var e=this.element.position(),t=this._isRootNode(this.scrollParent[0]);return{top:e.top-(parseInt(this.helper.css("top"),10)||0)+(t?0:this.scrollParent.scrollTop()),left:e.left-(parseInt(this.helper.css("left"),10)||0)+(t?0:this.scrollParent.scrollLeft())}},_cacheMargins:function(){this.margins={left:parseInt(this.element.css("marginLeft"),10)||0,top:parseInt(this.element.css("marginTop"),10)||0,right:parseInt(this.element.css("marginRight"),10)||0,bottom:parseInt(this.element.css("marginBottom"),10)||0}},_cacheHelperProportions:function(){this.helperProportions={width:this.helper.outerWidth(),height:this.helper.outerHeight()}},_setContainment:function(){var t,i,s,n=this.options,a=this.document[0];return this.relativeContainer=null,n.containment?"window"===n.containment?(this.containment=[e(window).scrollLeft()-this.offset.relative.left-this.offset.parent.left,e(window).scrollTop()-this.offset.relative.top-this.offset.parent.top,e(window).scrollLeft()+e(window).width()-this.helperProportions.width-this.margins.left,e(window).scrollTop()+(e(window).height()||a.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top],void 0):"document"===n.containment?(this.containment=[0,0,e(a).width()-this.helperProportions.width-this.margins.left,(e(a).height()||a.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top],void 0):n.containment.constructor===Array?(this.containment=n.containment,void 0):("parent"===n.containment&&(n.containment=this.helper[0].parentNode),i=e(n.containment),s=i[0],s&&(t="hidden"!==i.css("overflow"),this.containment=[(parseInt(i.css("borderLeftWidth"),10)||0)+(parseInt(i.css("paddingLeft"),10)||0),(parseInt(i.css("borderTopWidth"),10)||0)+(parseInt(i.css("paddingTop"),10)||0),(t?Math.max(s.scrollWidth,s.offsetWidth):s.offsetWidth)-(parseInt(i.css("borderRightWidth"),10)||0)-(parseInt(i.css("paddingRight"),10)||0)-this.helperProportions.width-this.margins.left-this.margins.right,(t?Math.max(s.scrollHeight,s.offsetHeight):s.offsetHeight)-(parseInt(i.css("borderBottomWidth"),10)||0)-(parseInt(i.css("paddingBottom"),10)||0)-this.helperProportions.height-this.margins.top-this.margins.bottom],this.relativeContainer=i),void 0):(this.containment=null,void 0)},_convertPositionTo:function(e,t){t||(t=this.position);var i="absolute"===e?1:-1,s=this._isRootNode(this.scrollParent[0]);return{top:t.top+this.offset.relative.top*i+this.offset.parent.top*i-("fixed"===this.cssPosition?-this.offset.scroll.top:s?0:this.offset.scroll.top)*i,left:t.left+this.offset.relative.left*i+this.offset.parent.left*i-("fixed"===this.cssPosition?-this.offset.scroll.left:s?0:this.offset.scroll.left)*i}},_generatePosition:function(e,t){var i,s,n,a,o=this.options,r=this._isRootNode(this.scrollParent[0]),h=e.pageX,l=e.pageY;return r&&this.offset.scroll||(this.offset.scroll={top:this.scrollParent.scrollTop(),left:this.scrollParent.scrollLeft()}),t&&(this.containment&&(this.relativeContainer?(s=this.relativeContainer.offset(),i=[this.containment[0]+s.left,this.containment[1]+s.top,this.containment[2]+s.left,this.containment[3]+s.top]):i=this.containment,e.pageX-this.offset.click.left<i[0]&&(h=i[0]+this.offset.click.left),e.pageY-this.offset.click.top<i[1]&&(l=i[1]+this.offset.click.top),e.pageX-this.offset.click.left>i[2]&&(h=i[2]+this.offset.click.left),e.pageY-this.offset.click.top>i[3]&&(l=i[3]+this.offset.click.top)),o.grid&&(n=o.grid[1]?this.originalPageY+Math.round((l-this.originalPageY)/o.grid[1])*o.grid[1]:this.originalPageY,l=i?n-this.offset.click.top>=i[1]||n-this.offset.click.top>i[3]?n:n-this.offset.click.top>=i[1]?n-o.grid[1]:n+o.grid[1]:n,a=o.grid[0]?this.originalPageX+Math.round((h-this.originalPageX)/o.grid[0])*o.grid[0]:this.originalPageX,h=i?a-this.offset.click.left>=i[0]||a-this.offset.click.left>i[2]?a:a-this.offset.click.left>=i[0]?a-o.grid[0]:a+o.grid[0]:a),"y"===o.axis&&(h=this.originalPageX),"x"===o.axis&&(l=this.originalPageY)),{top:l-this.offset.click.top-this.offset.relative.top-this.offset.parent.top+("fixed"===this.cssPosition?-this.offset.scroll.top:r?0:this.offset.scroll.top),left:h-this.offset.click.left-this.offset.relative.left-this.offset.parent.left+("fixed"===this.cssPosition?-this.offset.scroll.left:r?0:this.offset.scroll.left)}},_clear:function(){this.helper.removeClass("ui-draggable-dragging"),this.helper[0]===this.element[0]||this.cancelHelperRemoval||this.helper.remove(),this.helper=null,this.cancelHelperRemoval=!1,this.destroyOnClear&&this.destroy()},_trigger:function(t,i,s){return s=s||this._uiHash(),e.ui.plugin.call(this,t,[i,s,this],!0),"drag"===t&&(this.positionAbs=this._convertPositionTo("absolute")),e.Widget.prototype._trigger.call(this,t,i,s)},plugins:{},_uiHash:function(){return{helper:this.helper,position:this.position,originalPosition:this.originalPosition,offset:this.positionAbs}}}),e.ui.plugin.add("draggable","connectToSortable",{start:function(t,i,s){var n=s.options,a=e.extend({},i,{item:s.element});s.sortables=[],e(n.connectToSortable).each(function(){var i=e(this).sortable("instance");i&&!i.options.disabled&&(s.sortables.push({instance:i,shouldRevert:i.options.revert}),i.refreshPositions(),i._trigger("activate",t,a))})},stop:function(t,i,s){var n=e.extend({},i,{item:s.element});e.each(s.sortables,function(){this.instance.isOver?(this.instance.isOver=0,s.cancelHelperRemoval=!0,this.instance.cancelHelperRemoval=!1,this.shouldRevert&&(this.instance.options.revert=this.shouldRevert),this.instance._mouseStop(t),this.instance.options.helper=this.instance.options._helper,"original"===s.options.helper&&this.instance.currentItem.css({top:"auto",left:"auto"})):(this.instance.cancelHelperRemoval=!1,this.instance._trigger("deactivate",t,n))})},drag:function(t,i,s){var n=this;e.each(s.sortables,function(){var a=!1,o=this;this.instance.positionAbs=s.positionAbs,this.instance.helperProportions=s.helperProportions,this.instance.offset.click=s.offset.click,this.instance._intersectsWith(this.instance.containerCache)&&(a=!0,e.each(s.sortables,function(){return this.instance.positionAbs=s.positionAbs,this.instance.helperProportions=s.helperProportions,this.instance.offset.click=s.offset.click,this!==o&&this.instance._intersectsWith(this.instance.containerCache)&&e.contains(o.instance.element[0],this.instance.element[0])&&(a=!1),a})),a?(this.instance.isOver||(this.instance.isOver=1,this.instance.currentItem=e(n).clone().removeAttr("id").appendTo(this.instance.element).data("ui-sortable-item",!0),this.instance.options._helper=this.instance.options.helper,this.instance.options.helper=function(){return i.helper[0]},t.target=this.instance.currentItem[0],this.instance._mouseCapture(t,!0),this.instance._mouseStart(t,!0,!0),this.instance.offset.click.top=s.offset.click.top,this.instance.offset.click.left=s.offset.click.left,this.instance.offset.parent.left-=s.offset.parent.left-this.instance.offset.parent.left,this.instance.offset.parent.top-=s.offset.parent.top-this.instance.offset.parent.top,s._trigger("toSortable",t),s.dropped=this.instance.element,s.currentItem=s.element,this.instance.fromOutside=s),this.instance.currentItem&&this.instance._mouseDrag(t)):this.instance.isOver&&(this.instance.isOver=0,this.instance.cancelHelperRemoval=!0,this.instance.options.revert=!1,this.instance._trigger("out",t,this.instance._uiHash(this.instance)),this.instance._mouseStop(t,!0),this.instance.options.helper=this.instance.options._helper,this.instance.currentItem.remove(),this.instance.placeholder&&this.instance.placeholder.remove(),s._trigger("fromSortable",t),s.dropped=!1)
})}}),e.ui.plugin.add("draggable","cursor",{start:function(t,i,s){var n=e("body"),a=s.options;n.css("cursor")&&(a._cursor=n.css("cursor")),n.css("cursor",a.cursor)},stop:function(t,i,s){var n=s.options;n._cursor&&e("body").css("cursor",n._cursor)}}),e.ui.plugin.add("draggable","opacity",{start:function(t,i,s){var n=e(i.helper),a=s.options;n.css("opacity")&&(a._opacity=n.css("opacity")),n.css("opacity",a.opacity)},stop:function(t,i,s){var n=s.options;n._opacity&&e(i.helper).css("opacity",n._opacity)}}),e.ui.plugin.add("draggable","scroll",{start:function(e,t,i){i.scrollParentNotHidden||(i.scrollParentNotHidden=i.helper.scrollParent(!1)),i.scrollParentNotHidden[0]!==i.document[0]&&"HTML"!==i.scrollParentNotHidden[0].tagName&&(i.overflowOffset=i.scrollParentNotHidden.offset())},drag:function(t,i,s){var n=s.options,a=!1,o=s.scrollParentNotHidden[0],r=s.document[0];o!==r&&"HTML"!==o.tagName?(n.axis&&"x"===n.axis||(s.overflowOffset.top+o.offsetHeight-t.pageY<n.scrollSensitivity?o.scrollTop=a=o.scrollTop+n.scrollSpeed:t.pageY-s.overflowOffset.top<n.scrollSensitivity&&(o.scrollTop=a=o.scrollTop-n.scrollSpeed)),n.axis&&"y"===n.axis||(s.overflowOffset.left+o.offsetWidth-t.pageX<n.scrollSensitivity?o.scrollLeft=a=o.scrollLeft+n.scrollSpeed:t.pageX-s.overflowOffset.left<n.scrollSensitivity&&(o.scrollLeft=a=o.scrollLeft-n.scrollSpeed))):(n.axis&&"x"===n.axis||(t.pageY-e(r).scrollTop()<n.scrollSensitivity?a=e(r).scrollTop(e(r).scrollTop()-n.scrollSpeed):e(window).height()-(t.pageY-e(r).scrollTop())<n.scrollSensitivity&&(a=e(r).scrollTop(e(r).scrollTop()+n.scrollSpeed))),n.axis&&"y"===n.axis||(t.pageX-e(r).scrollLeft()<n.scrollSensitivity?a=e(r).scrollLeft(e(r).scrollLeft()-n.scrollSpeed):e(window).width()-(t.pageX-e(r).scrollLeft())<n.scrollSensitivity&&(a=e(r).scrollLeft(e(r).scrollLeft()+n.scrollSpeed)))),a!==!1&&e.ui.ddmanager&&!n.dropBehaviour&&e.ui.ddmanager.prepareOffsets(s,t)}}),e.ui.plugin.add("draggable","snap",{start:function(t,i,s){var n=s.options;s.snapElements=[],e(n.snap.constructor!==String?n.snap.items||":data(ui-draggable)":n.snap).each(function(){var t=e(this),i=t.offset();this!==s.element[0]&&s.snapElements.push({item:this,width:t.outerWidth(),height:t.outerHeight(),top:i.top,left:i.left})})},drag:function(t,i,s){var n,a,o,r,h,l,u,d,c,p,f=s.options,m=f.snapTolerance,g=i.offset.left,v=g+s.helperProportions.width,y=i.offset.top,b=y+s.helperProportions.height;for(c=s.snapElements.length-1;c>=0;c--)h=s.snapElements[c].left,l=h+s.snapElements[c].width,u=s.snapElements[c].top,d=u+s.snapElements[c].height,h-m>v||g>l+m||u-m>b||y>d+m||!e.contains(s.snapElements[c].item.ownerDocument,s.snapElements[c].item)?(s.snapElements[c].snapping&&s.options.snap.release&&s.options.snap.release.call(s.element,t,e.extend(s._uiHash(),{snapItem:s.snapElements[c].item})),s.snapElements[c].snapping=!1):("inner"!==f.snapMode&&(n=m>=Math.abs(u-b),a=m>=Math.abs(d-y),o=m>=Math.abs(h-v),r=m>=Math.abs(l-g),n&&(i.position.top=s._convertPositionTo("relative",{top:u-s.helperProportions.height,left:0}).top-s.margins.top),a&&(i.position.top=s._convertPositionTo("relative",{top:d,left:0}).top-s.margins.top),o&&(i.position.left=s._convertPositionTo("relative",{top:0,left:h-s.helperProportions.width}).left-s.margins.left),r&&(i.position.left=s._convertPositionTo("relative",{top:0,left:l}).left-s.margins.left)),p=n||a||o||r,"outer"!==f.snapMode&&(n=m>=Math.abs(u-y),a=m>=Math.abs(d-b),o=m>=Math.abs(h-g),r=m>=Math.abs(l-v),n&&(i.position.top=s._convertPositionTo("relative",{top:u,left:0}).top-s.margins.top),a&&(i.position.top=s._convertPositionTo("relative",{top:d-s.helperProportions.height,left:0}).top-s.margins.top),o&&(i.position.left=s._convertPositionTo("relative",{top:0,left:h}).left-s.margins.left),r&&(i.position.left=s._convertPositionTo("relative",{top:0,left:l-s.helperProportions.width}).left-s.margins.left)),!s.snapElements[c].snapping&&(n||a||o||r||p)&&s.options.snap.snap&&s.options.snap.snap.call(s.element,t,e.extend(s._uiHash(),{snapItem:s.snapElements[c].item})),s.snapElements[c].snapping=n||a||o||r||p)}}),e.ui.plugin.add("draggable","stack",{start:function(t,i,s){var n,a=s.options,o=e.makeArray(e(a.stack)).sort(function(t,i){return(parseInt(e(t).css("zIndex"),10)||0)-(parseInt(e(i).css("zIndex"),10)||0)});o.length&&(n=parseInt(e(o[0]).css("zIndex"),10)||0,e(o).each(function(t){e(this).css("zIndex",n+t)}),this.css("zIndex",n+o.length))}}),e.ui.plugin.add("draggable","zIndex",{start:function(t,i,s){var n=e(i.helper),a=s.options;n.css("zIndex")&&(a._zIndex=n.css("zIndex")),n.css("zIndex",a.zIndex)},stop:function(t,i,s){var n=s.options;n._zIndex&&e(i.helper).css("zIndex",n._zIndex)}}),e.ui.draggable,e.widget("ui.droppable",{version:"1.11.1",widgetEventPrefix:"drop",options:{accept:"*",activeClass:!1,addClasses:!0,greedy:!1,hoverClass:!1,scope:"default",tolerance:"intersect",activate:null,deactivate:null,drop:null,out:null,over:null},_create:function(){var t,i=this.options,s=i.accept;this.isover=!1,this.isout=!0,this.accept=e.isFunction(s)?s:function(e){return e.is(s)},this.proportions=function(){return arguments.length?(t=arguments[0],void 0):t?t:t={width:this.element[0].offsetWidth,height:this.element[0].offsetHeight}},this._addToManager(i.scope),i.addClasses&&this.element.addClass("ui-droppable")},_addToManager:function(t){e.ui.ddmanager.droppables[t]=e.ui.ddmanager.droppables[t]||[],e.ui.ddmanager.droppables[t].push(this)},_splice:function(e){for(var t=0;e.length>t;t++)e[t]===this&&e.splice(t,1)},_destroy:function(){var t=e.ui.ddmanager.droppables[this.options.scope];this._splice(t),this.element.removeClass("ui-droppable ui-droppable-disabled")},_setOption:function(t,i){if("accept"===t)this.accept=e.isFunction(i)?i:function(e){return e.is(i)};else if("scope"===t){var s=e.ui.ddmanager.droppables[this.options.scope];this._splice(s),this._addToManager(i)}this._super(t,i)},_activate:function(t){var i=e.ui.ddmanager.current;this.options.activeClass&&this.element.addClass(this.options.activeClass),i&&this._trigger("activate",t,this.ui(i))},_deactivate:function(t){var i=e.ui.ddmanager.current;this.options.activeClass&&this.element.removeClass(this.options.activeClass),i&&this._trigger("deactivate",t,this.ui(i))},_over:function(t){var i=e.ui.ddmanager.current;i&&(i.currentItem||i.element)[0]!==this.element[0]&&this.accept.call(this.element[0],i.currentItem||i.element)&&(this.options.hoverClass&&this.element.addClass(this.options.hoverClass),this._trigger("over",t,this.ui(i)))},_out:function(t){var i=e.ui.ddmanager.current;i&&(i.currentItem||i.element)[0]!==this.element[0]&&this.accept.call(this.element[0],i.currentItem||i.element)&&(this.options.hoverClass&&this.element.removeClass(this.options.hoverClass),this._trigger("out",t,this.ui(i)))},_drop:function(t,i){var s=i||e.ui.ddmanager.current,n=!1;return s&&(s.currentItem||s.element)[0]!==this.element[0]?(this.element.find(":data(ui-droppable)").not(".ui-draggable-dragging").each(function(){var i=e(this).droppable("instance");return i.options.greedy&&!i.options.disabled&&i.options.scope===s.options.scope&&i.accept.call(i.element[0],s.currentItem||s.element)&&e.ui.intersect(s,e.extend(i,{offset:i.element.offset()}),i.options.tolerance,t)?(n=!0,!1):void 0}),n?!1:this.accept.call(this.element[0],s.currentItem||s.element)?(this.options.activeClass&&this.element.removeClass(this.options.activeClass),this.options.hoverClass&&this.element.removeClass(this.options.hoverClass),this._trigger("drop",t,this.ui(s)),this.element):!1):!1},ui:function(e){return{draggable:e.currentItem||e.element,helper:e.helper,position:e.position,offset:e.positionAbs}}}),e.ui.intersect=function(){function e(e,t,i){return e>=t&&t+i>e}return function(t,i,s,n){if(!i.offset)return!1;var a=(t.positionAbs||t.position.absolute).left,o=(t.positionAbs||t.position.absolute).top,r=a+t.helperProportions.width,h=o+t.helperProportions.height,l=i.offset.left,u=i.offset.top,d=l+i.proportions().width,c=u+i.proportions().height;switch(s){case"fit":return a>=l&&d>=r&&o>=u&&c>=h;case"intersect":return a+t.helperProportions.width/2>l&&d>r-t.helperProportions.width/2&&o+t.helperProportions.height/2>u&&c>h-t.helperProportions.height/2;case"pointer":return e(n.pageY,u,i.proportions().height)&&e(n.pageX,l,i.proportions().width);case"touch":return(o>=u&&c>=o||h>=u&&c>=h||u>o&&h>c)&&(a>=l&&d>=a||r>=l&&d>=r||l>a&&r>d);default:return!1}}}(),e.ui.ddmanager={current:null,droppables:{"default":[]},prepareOffsets:function(t,i){var s,n,a=e.ui.ddmanager.droppables[t.options.scope]||[],o=i?i.type:null,r=(t.currentItem||t.element).find(":data(ui-droppable)").addBack();e:for(s=0;a.length>s;s++)if(!(a[s].options.disabled||t&&!a[s].accept.call(a[s].element[0],t.currentItem||t.element))){for(n=0;r.length>n;n++)if(r[n]===a[s].element[0]){a[s].proportions().height=0;continue e}a[s].visible="none"!==a[s].element.css("display"),a[s].visible&&("mousedown"===o&&a[s]._activate.call(a[s],i),a[s].offset=a[s].element.offset(),a[s].proportions({width:a[s].element[0].offsetWidth,height:a[s].element[0].offsetHeight}))}},drop:function(t,i){var s=!1;return e.each((e.ui.ddmanager.droppables[t.options.scope]||[]).slice(),function(){this.options&&(!this.options.disabled&&this.visible&&e.ui.intersect(t,this,this.options.tolerance,i)&&(s=this._drop.call(this,i)||s),!this.options.disabled&&this.visible&&this.accept.call(this.element[0],t.currentItem||t.element)&&(this.isout=!0,this.isover=!1,this._deactivate.call(this,i)))}),s},dragStart:function(t,i){t.element.parentsUntil("body").bind("scroll.droppable",function(){t.options.refreshPositions||e.ui.ddmanager.prepareOffsets(t,i)})},drag:function(t,i){t.options.refreshPositions&&e.ui.ddmanager.prepareOffsets(t,i),e.each(e.ui.ddmanager.droppables[t.options.scope]||[],function(){if(!this.options.disabled&&!this.greedyChild&&this.visible){var s,n,a,o=e.ui.intersect(t,this,this.options.tolerance,i),r=!o&&this.isover?"isout":o&&!this.isover?"isover":null;r&&(this.options.greedy&&(n=this.options.scope,a=this.element.parents(":data(ui-droppable)").filter(function(){return e(this).droppable("instance").options.scope===n}),a.length&&(s=e(a[0]).droppable("instance"),s.greedyChild="isover"===r)),s&&"isover"===r&&(s.isover=!1,s.isout=!0,s._out.call(s,i)),this[r]=!0,this["isout"===r?"isover":"isout"]=!1,this["isover"===r?"_over":"_out"].call(this,i),s&&"isout"===r&&(s.isout=!1,s.isover=!0,s._over.call(s,i)))}})},dragStop:function(t,i){t.element.parentsUntil("body").unbind("scroll.droppable"),t.options.refreshPositions||e.ui.ddmanager.prepareOffsets(t,i)}},e.ui.droppable,e.widget("ui.resizable",e.ui.mouse,{version:"1.11.1",widgetEventPrefix:"resize",options:{alsoResize:!1,animate:!1,animateDuration:"slow",animateEasing:"swing",aspectRatio:!1,autoHide:!1,containment:!1,ghost:!1,grid:!1,handles:"e,s,se",helper:!1,maxHeight:null,maxWidth:null,minHeight:10,minWidth:10,zIndex:90,resize:null,start:null,stop:null},_num:function(e){return parseInt(e,10)||0},_isNumber:function(e){return!isNaN(parseInt(e,10))},_hasScroll:function(t,i){if("hidden"===e(t).css("overflow"))return!1;var s=i&&"left"===i?"scrollLeft":"scrollTop",n=!1;return t[s]>0?!0:(t[s]=1,n=t[s]>0,t[s]=0,n)},_create:function(){var t,i,s,n,a,o=this,r=this.options;if(this.element.addClass("ui-resizable"),e.extend(this,{_aspectRatio:!!r.aspectRatio,aspectRatio:r.aspectRatio,originalElement:this.element,_proportionallyResizeElements:[],_helper:r.helper||r.ghost||r.animate?r.helper||"ui-resizable-helper":null}),this.element[0].nodeName.match(/canvas|textarea|input|select|button|img/i)&&(this.element.wrap(e("<div class='ui-wrapper' style='overflow: hidden;'></div>").css({position:this.element.css("position"),width:this.element.outerWidth(),height:this.element.outerHeight(),top:this.element.css("top"),left:this.element.css("left")})),this.element=this.element.parent().data("ui-resizable",this.element.resizable("instance")),this.elementIsWrapper=!0,this.element.css({marginLeft:this.originalElement.css("marginLeft"),marginTop:this.originalElement.css("marginTop"),marginRight:this.originalElement.css("marginRight"),marginBottom:this.originalElement.css("marginBottom")}),this.originalElement.css({marginLeft:0,marginTop:0,marginRight:0,marginBottom:0}),this.originalResizeStyle=this.originalElement.css("resize"),this.originalElement.css("resize","none"),this._proportionallyResizeElements.push(this.originalElement.css({position:"static",zoom:1,display:"block"})),this.originalElement.css({margin:this.originalElement.css("margin")}),this._proportionallyResize()),this.handles=r.handles||(e(".ui-resizable-handle",this.element).length?{n:".ui-resizable-n",e:".ui-resizable-e",s:".ui-resizable-s",w:".ui-resizable-w",se:".ui-resizable-se",sw:".ui-resizable-sw",ne:".ui-resizable-ne",nw:".ui-resizable-nw"}:"e,s,se"),this.handles.constructor===String)for("all"===this.handles&&(this.handles="n,e,s,w,se,sw,ne,nw"),t=this.handles.split(","),this.handles={},i=0;t.length>i;i++)s=e.trim(t[i]),a="ui-resizable-"+s,n=e("<div class='ui-resizable-handle "+a+"'></div>"),n.css({zIndex:r.zIndex}),"se"===s&&n.addClass("ui-icon ui-icon-gripsmall-diagonal-se"),this.handles[s]=".ui-resizable-"+s,this.element.append(n);this._renderAxis=function(t){var i,s,n,a;t=t||this.element;for(i in this.handles)this.handles[i].constructor===String&&(this.handles[i]=this.element.children(this.handles[i]).first().show()),this.elementIsWrapper&&this.originalElement[0].nodeName.match(/textarea|input|select|button/i)&&(s=e(this.handles[i],this.element),a=/sw|ne|nw|se|n|s/.test(i)?s.outerHeight():s.outerWidth(),n=["padding",/ne|nw|n/.test(i)?"Top":/se|sw|s/.test(i)?"Bottom":/^e$/.test(i)?"Right":"Left"].join(""),t.css(n,a),this._proportionallyResize()),e(this.handles[i]).length},this._renderAxis(this.element),this._handles=e(".ui-resizable-handle",this.element).disableSelection(),this._handles.mouseover(function(){o.resizing||(this.className&&(n=this.className.match(/ui-resizable-(se|sw|ne|nw|n|e|s|w)/i)),o.axis=n&&n[1]?n[1]:"se")}),r.autoHide&&(this._handles.hide(),e(this.element).addClass("ui-resizable-autohide").mouseenter(function(){r.disabled||(e(this).removeClass("ui-resizable-autohide"),o._handles.show())}).mouseleave(function(){r.disabled||o.resizing||(e(this).addClass("ui-resizable-autohide"),o._handles.hide())})),this._mouseInit()},_destroy:function(){this._mouseDestroy();var t,i=function(t){e(t).removeClass("ui-resizable ui-resizable-disabled ui-resizable-resizing").removeData("resizable").removeData("ui-resizable").unbind(".resizable").find(".ui-resizable-handle").remove()};return this.elementIsWrapper&&(i(this.element),t=this.element,this.originalElement.css({position:t.css("position"),width:t.outerWidth(),height:t.outerHeight(),top:t.css("top"),left:t.css("left")}).insertAfter(t),t.remove()),this.originalElement.css("resize",this.originalResizeStyle),i(this.originalElement),this},_mouseCapture:function(t){var i,s,n=!1;for(i in this.handles)s=e(this.handles[i])[0],(s===t.target||e.contains(s,t.target))&&(n=!0);return!this.options.disabled&&n},_mouseStart:function(t){var i,s,n,a=this.options,o=this.element;return this.resizing=!0,this._renderProxy(),i=this._num(this.helper.css("left")),s=this._num(this.helper.css("top")),a.containment&&(i+=e(a.containment).scrollLeft()||0,s+=e(a.containment).scrollTop()||0),this.offset=this.helper.offset(),this.position={left:i,top:s},this.size=this._helper?{width:this.helper.width(),height:this.helper.height()}:{width:o.width(),height:o.height()},this.originalSize=this._helper?{width:o.outerWidth(),height:o.outerHeight()}:{width:o.width(),height:o.height()},this.sizeDiff={width:o.outerWidth()-o.width(),height:o.outerHeight()-o.height()},this.originalPosition={left:i,top:s},this.originalMousePosition={left:t.pageX,top:t.pageY},this.aspectRatio="number"==typeof a.aspectRatio?a.aspectRatio:this.originalSize.width/this.originalSize.height||1,n=e(".ui-resizable-"+this.axis).css("cursor"),e("body").css("cursor","auto"===n?this.axis+"-resize":n),o.addClass("ui-resizable-resizing"),this._propagate("start",t),!0},_mouseDrag:function(t){var i,s,n=this.originalMousePosition,a=this.axis,o=t.pageX-n.left||0,r=t.pageY-n.top||0,h=this._change[a];return this._updatePrevProperties(),h?(i=h.apply(this,[t,o,r]),this._updateVirtualBoundaries(t.shiftKey),(this._aspectRatio||t.shiftKey)&&(i=this._updateRatio(i,t)),i=this._respectSize(i,t),this._updateCache(i),this._propagate("resize",t),s=this._applyChanges(),!this._helper&&this._proportionallyResizeElements.length&&this._proportionallyResize(),e.isEmptyObject(s)||(this._updatePrevProperties(),this._trigger("resize",t,this.ui()),this._applyChanges()),!1):!1},_mouseStop:function(t){this.resizing=!1;var i,s,n,a,o,r,h,l=this.options,u=this;return this._helper&&(i=this._proportionallyResizeElements,s=i.length&&/textarea/i.test(i[0].nodeName),n=s&&this._hasScroll(i[0],"left")?0:u.sizeDiff.height,a=s?0:u.sizeDiff.width,o={width:u.helper.width()-a,height:u.helper.height()-n},r=parseInt(u.element.css("left"),10)+(u.position.left-u.originalPosition.left)||null,h=parseInt(u.element.css("top"),10)+(u.position.top-u.originalPosition.top)||null,l.animate||this.element.css(e.extend(o,{top:h,left:r})),u.helper.height(u.size.height),u.helper.width(u.size.width),this._helper&&!l.animate&&this._proportionallyResize()),e("body").css("cursor","auto"),this.element.removeClass("ui-resizable-resizing"),this._propagate("stop",t),this._helper&&this.helper.remove(),!1},_updatePrevProperties:function(){this.prevPosition={top:this.position.top,left:this.position.left},this.prevSize={width:this.size.width,height:this.size.height}},_applyChanges:function(){var e={};return this.position.top!==this.prevPosition.top&&(e.top=this.position.top+"px"),this.position.left!==this.prevPosition.left&&(e.left=this.position.left+"px"),this.size.width!==this.prevSize.width&&(e.width=this.size.width+"px"),this.size.height!==this.prevSize.height&&(e.height=this.size.height+"px"),this.helper.css(e),e},_updateVirtualBoundaries:function(e){var t,i,s,n,a,o=this.options;a={minWidth:this._isNumber(o.minWidth)?o.minWidth:0,maxWidth:this._isNumber(o.maxWidth)?o.maxWidth:1/0,minHeight:this._isNumber(o.minHeight)?o.minHeight:0,maxHeight:this._isNumber(o.maxHeight)?o.maxHeight:1/0},(this._aspectRatio||e)&&(t=a.minHeight*this.aspectRatio,s=a.minWidth/this.aspectRatio,i=a.maxHeight*this.aspectRatio,n=a.maxWidth/this.aspectRatio,t>a.minWidth&&(a.minWidth=t),s>a.minHeight&&(a.minHeight=s),a.maxWidth>i&&(a.maxWidth=i),a.maxHeight>n&&(a.maxHeight=n)),this._vBoundaries=a},_updateCache:function(e){this.offset=this.helper.offset(),this._isNumber(e.left)&&(this.position.left=e.left),this._isNumber(e.top)&&(this.position.top=e.top),this._isNumber(e.height)&&(this.size.height=e.height),this._isNumber(e.width)&&(this.size.width=e.width)},_updateRatio:function(e){var t=this.position,i=this.size,s=this.axis;return this._isNumber(e.height)?e.width=e.height*this.aspectRatio:this._isNumber(e.width)&&(e.height=e.width/this.aspectRatio),"sw"===s&&(e.left=t.left+(i.width-e.width),e.top=null),"nw"===s&&(e.top=t.top+(i.height-e.height),e.left=t.left+(i.width-e.width)),e},_respectSize:function(e){var t=this._vBoundaries,i=this.axis,s=this._isNumber(e.width)&&t.maxWidth&&t.maxWidth<e.width,n=this._isNumber(e.height)&&t.maxHeight&&t.maxHeight<e.height,a=this._isNumber(e.width)&&t.minWidth&&t.minWidth>e.width,o=this._isNumber(e.height)&&t.minHeight&&t.minHeight>e.height,r=this.originalPosition.left+this.originalSize.width,h=this.position.top+this.size.height,l=/sw|nw|w/.test(i),u=/nw|ne|n/.test(i);return a&&(e.width=t.minWidth),o&&(e.height=t.minHeight),s&&(e.width=t.maxWidth),n&&(e.height=t.maxHeight),a&&l&&(e.left=r-t.minWidth),s&&l&&(e.left=r-t.maxWidth),o&&u&&(e.top=h-t.minHeight),n&&u&&(e.top=h-t.maxHeight),e.width||e.height||e.left||!e.top?e.width||e.height||e.top||!e.left||(e.left=null):e.top=null,e},_getPaddingPlusBorderDimensions:function(e){for(var t=0,i=[],s=[e.css("borderTopWidth"),e.css("borderRightWidth"),e.css("borderBottomWidth"),e.css("borderLeftWidth")],n=[e.css("paddingTop"),e.css("paddingRight"),e.css("paddingBottom"),e.css("paddingLeft")];4>t;t++)i[t]=parseInt(s[t],10)||0,i[t]+=parseInt(n[t],10)||0;return{height:i[0]+i[2],width:i[1]+i[3]}},_proportionallyResize:function(){if(this._proportionallyResizeElements.length)for(var e,t=0,i=this.helper||this.element;this._proportionallyResizeElements.length>t;t++)e=this._proportionallyResizeElements[t],this.outerDimensions||(this.outerDimensions=this._getPaddingPlusBorderDimensions(e)),e.css({height:i.height()-this.outerDimensions.height||0,width:i.width()-this.outerDimensions.width||0})},_renderProxy:function(){var t=this.element,i=this.options;this.elementOffset=t.offset(),this._helper?(this.helper=this.helper||e("<div style='overflow:hidden;'></div>"),this.helper.addClass(this._helper).css({width:this.element.outerWidth()-1,height:this.element.outerHeight()-1,position:"absolute",left:this.elementOffset.left+"px",top:this.elementOffset.top+"px",zIndex:++i.zIndex}),this.helper.appendTo("body").disableSelection()):this.helper=this.element},_change:{e:function(e,t){return{width:this.originalSize.width+t}},w:function(e,t){var i=this.originalSize,s=this.originalPosition;return{left:s.left+t,width:i.width-t}},n:function(e,t,i){var s=this.originalSize,n=this.originalPosition;return{top:n.top+i,height:s.height-i}},s:function(e,t,i){return{height:this.originalSize.height+i}},se:function(t,i,s){return e.extend(this._change.s.apply(this,arguments),this._change.e.apply(this,[t,i,s]))},sw:function(t,i,s){return e.extend(this._change.s.apply(this,arguments),this._change.w.apply(this,[t,i,s]))},ne:function(t,i,s){return e.extend(this._change.n.apply(this,arguments),this._change.e.apply(this,[t,i,s]))},nw:function(t,i,s){return e.extend(this._change.n.apply(this,arguments),this._change.w.apply(this,[t,i,s]))}},_propagate:function(t,i){e.ui.plugin.call(this,t,[i,this.ui()]),"resize"!==t&&this._trigger(t,i,this.ui())},plugins:{},ui:function(){return{originalElement:this.originalElement,element:this.element,helper:this.helper,position:this.position,size:this.size,originalSize:this.originalSize,originalPosition:this.originalPosition}}}),e.ui.plugin.add("resizable","animate",{stop:function(t){var i=e(this).resizable("instance"),s=i.options,n=i._proportionallyResizeElements,a=n.length&&/textarea/i.test(n[0].nodeName),o=a&&i._hasScroll(n[0],"left")?0:i.sizeDiff.height,r=a?0:i.sizeDiff.width,h={width:i.size.width-r,height:i.size.height-o},l=parseInt(i.element.css("left"),10)+(i.position.left-i.originalPosition.left)||null,u=parseInt(i.element.css("top"),10)+(i.position.top-i.originalPosition.top)||null;i.element.animate(e.extend(h,u&&l?{top:u,left:l}:{}),{duration:s.animateDuration,easing:s.animateEasing,step:function(){var s={width:parseInt(i.element.css("width"),10),height:parseInt(i.element.css("height"),10),top:parseInt(i.element.css("top"),10),left:parseInt(i.element.css("left"),10)};n&&n.length&&e(n[0]).css({width:s.width,height:s.height}),i._updateCache(s),i._propagate("resize",t)}})}}),e.ui.plugin.add("resizable","containment",{start:function(){var t,i,s,n,a,o,r,h=e(this).resizable("instance"),l=h.options,u=h.element,d=l.containment,c=d instanceof e?d.get(0):/parent/.test(d)?u.parent().get(0):d;c&&(h.containerElement=e(c),/document/.test(d)||d===document?(h.containerOffset={left:0,top:0},h.containerPosition={left:0,top:0},h.parentData={element:e(document),left:0,top:0,width:e(document).width(),height:e(document).height()||document.body.parentNode.scrollHeight}):(t=e(c),i=[],e(["Top","Right","Left","Bottom"]).each(function(e,s){i[e]=h._num(t.css("padding"+s))}),h.containerOffset=t.offset(),h.containerPosition=t.position(),h.containerSize={height:t.innerHeight()-i[3],width:t.innerWidth()-i[1]},s=h.containerOffset,n=h.containerSize.height,a=h.containerSize.width,o=h._hasScroll(c,"left")?c.scrollWidth:a,r=h._hasScroll(c)?c.scrollHeight:n,h.parentData={element:c,left:s.left,top:s.top,width:o,height:r}))},resize:function(t){var i,s,n,a,o=e(this).resizable("instance"),r=o.options,h=o.containerOffset,l=o.position,u=o._aspectRatio||t.shiftKey,d={top:0,left:0},c=o.containerElement,p=!0;c[0]!==document&&/static/.test(c.css("position"))&&(d=h),l.left<(o._helper?h.left:0)&&(o.size.width=o.size.width+(o._helper?o.position.left-h.left:o.position.left-d.left),u&&(o.size.height=o.size.width/o.aspectRatio,p=!1),o.position.left=r.helper?h.left:0),l.top<(o._helper?h.top:0)&&(o.size.height=o.size.height+(o._helper?o.position.top-h.top:o.position.top),u&&(o.size.width=o.size.height*o.aspectRatio,p=!1),o.position.top=o._helper?h.top:0),n=o.containerElement.get(0)===o.element.parent().get(0),a=/relative|absolute/.test(o.containerElement.css("position")),n&&a?(o.offset.left=o.parentData.left+o.position.left,o.offset.top=o.parentData.top+o.position.top):(o.offset.left=o.element.offset().left,o.offset.top=o.element.offset().top),i=Math.abs(o.sizeDiff.width+(o._helper?o.offset.left-d.left:o.offset.left-h.left)),s=Math.abs(o.sizeDiff.height+(o._helper?o.offset.top-d.top:o.offset.top-h.top)),i+o.size.width>=o.parentData.width&&(o.size.width=o.parentData.width-i,u&&(o.size.height=o.size.width/o.aspectRatio,p=!1)),s+o.size.height>=o.parentData.height&&(o.size.height=o.parentData.height-s,u&&(o.size.width=o.size.height*o.aspectRatio,p=!1)),p||(o.position.left=o.prevPosition.left,o.position.top=o.prevPosition.top,o.size.width=o.prevSize.width,o.size.height=o.prevSize.height)},stop:function(){var t=e(this).resizable("instance"),i=t.options,s=t.containerOffset,n=t.containerPosition,a=t.containerElement,o=e(t.helper),r=o.offset(),h=o.outerWidth()-t.sizeDiff.width,l=o.outerHeight()-t.sizeDiff.height;t._helper&&!i.animate&&/relative/.test(a.css("position"))&&e(this).css({left:r.left-n.left-s.left,width:h,height:l}),t._helper&&!i.animate&&/static/.test(a.css("position"))&&e(this).css({left:r.left-n.left-s.left,width:h,height:l})}}),e.ui.plugin.add("resizable","alsoResize",{start:function(){var t=e(this).resizable("instance"),i=t.options,s=function(t){e(t).each(function(){var t=e(this);t.data("ui-resizable-alsoresize",{width:parseInt(t.width(),10),height:parseInt(t.height(),10),left:parseInt(t.css("left"),10),top:parseInt(t.css("top"),10)})})};"object"!=typeof i.alsoResize||i.alsoResize.parentNode?s(i.alsoResize):i.alsoResize.length?(i.alsoResize=i.alsoResize[0],s(i.alsoResize)):e.each(i.alsoResize,function(e){s(e)})},resize:function(t,i){var s=e(this).resizable("instance"),n=s.options,a=s.originalSize,o=s.originalPosition,r={height:s.size.height-a.height||0,width:s.size.width-a.width||0,top:s.position.top-o.top||0,left:s.position.left-o.left||0},h=function(t,s){e(t).each(function(){var t=e(this),n=e(this).data("ui-resizable-alsoresize"),a={},o=s&&s.length?s:t.parents(i.originalElement[0]).length?["width","height"]:["width","height","top","left"];e.each(o,function(e,t){var i=(n[t]||0)+(r[t]||0);i&&i>=0&&(a[t]=i||null)}),t.css(a)})};"object"!=typeof n.alsoResize||n.alsoResize.nodeType?h(n.alsoResize):e.each(n.alsoResize,function(e,t){h(e,t)})},stop:function(){e(this).removeData("resizable-alsoresize")}}),e.ui.plugin.add("resizable","ghost",{start:function(){var t=e(this).resizable("instance"),i=t.options,s=t.size;t.ghost=t.originalElement.clone(),t.ghost.css({opacity:.25,display:"block",position:"relative",height:s.height,width:s.width,margin:0,left:0,top:0}).addClass("ui-resizable-ghost").addClass("string"==typeof i.ghost?i.ghost:""),t.ghost.appendTo(t.helper)},resize:function(){var t=e(this).resizable("instance");t.ghost&&t.ghost.css({position:"relative",height:t.size.height,width:t.size.width})},stop:function(){var t=e(this).resizable("instance");t.ghost&&t.helper&&t.helper.get(0).removeChild(t.ghost.get(0))}}),e.ui.plugin.add("resizable","grid",{resize:function(){var t,i=e(this).resizable("instance"),s=i.options,n=i.size,a=i.originalSize,o=i.originalPosition,r=i.axis,h="number"==typeof s.grid?[s.grid,s.grid]:s.grid,l=h[0]||1,u=h[1]||1,d=Math.round((n.width-a.width)/l)*l,c=Math.round((n.height-a.height)/u)*u,p=a.width+d,f=a.height+c,m=s.maxWidth&&p>s.maxWidth,g=s.maxHeight&&f>s.maxHeight,v=s.minWidth&&s.minWidth>p,y=s.minHeight&&s.minHeight>f;s.grid=h,v&&(p+=l),y&&(f+=u),m&&(p-=l),g&&(f-=u),/^(se|s|e)$/.test(r)?(i.size.width=p,i.size.height=f):/^(ne)$/.test(r)?(i.size.width=p,i.size.height=f,i.position.top=o.top-c):/^(sw)$/.test(r)?(i.size.width=p,i.size.height=f,i.position.left=o.left-d):((0>=f-u||0>=p-l)&&(t=i._getPaddingPlusBorderDimensions(this)),f-u>0?(i.size.height=f,i.position.top=o.top-c):(f=u-t.height,i.size.height=f,i.position.top=o.top+a.height-f),p-l>0?(i.size.width=p,i.position.left=o.left-d):(p=u-t.height,i.size.width=p,i.position.left=o.left+a.width-p))}}),e.ui.resizable,e.widget("ui.selectable",e.ui.mouse,{version:"1.11.1",options:{appendTo:"body",autoRefresh:!0,distance:0,filter:"*",tolerance:"touch",selected:null,selecting:null,start:null,stop:null,unselected:null,unselecting:null},_create:function(){var t,i=this;this.element.addClass("ui-selectable"),this.dragged=!1,this.refresh=function(){t=e(i.options.filter,i.element[0]),t.addClass("ui-selectee"),t.each(function(){var t=e(this),i=t.offset();e.data(this,"selectable-item",{element:this,$element:t,left:i.left,top:i.top,right:i.left+t.outerWidth(),bottom:i.top+t.outerHeight(),startselected:!1,selected:t.hasClass("ui-selected"),selecting:t.hasClass("ui-selecting"),unselecting:t.hasClass("ui-unselecting")})})},this.refresh(),this.selectees=t.addClass("ui-selectee"),this._mouseInit(),this.helper=e("<div class='ui-selectable-helper'></div>")},_destroy:function(){this.selectees.removeClass("ui-selectee").removeData("selectable-item"),this.element.removeClass("ui-selectable ui-selectable-disabled"),this._mouseDestroy()},_mouseStart:function(t){var i=this,s=this.options;this.opos=[t.pageX,t.pageY],this.options.disabled||(this.selectees=e(s.filter,this.element[0]),this._trigger("start",t),e(s.appendTo).append(this.helper),this.helper.css({left:t.pageX,top:t.pageY,width:0,height:0}),s.autoRefresh&&this.refresh(),this.selectees.filter(".ui-selected").each(function(){var s=e.data(this,"selectable-item");s.startselected=!0,t.metaKey||t.ctrlKey||(s.$element.removeClass("ui-selected"),s.selected=!1,s.$element.addClass("ui-unselecting"),s.unselecting=!0,i._trigger("unselecting",t,{unselecting:s.element}))}),e(t.target).parents().addBack().each(function(){var s,n=e.data(this,"selectable-item");return n?(s=!t.metaKey&&!t.ctrlKey||!n.$element.hasClass("ui-selected"),n.$element.removeClass(s?"ui-unselecting":"ui-selected").addClass(s?"ui-selecting":"ui-unselecting"),n.unselecting=!s,n.selecting=s,n.selected=s,s?i._trigger("selecting",t,{selecting:n.element}):i._trigger("unselecting",t,{unselecting:n.element}),!1):void 0}))},_mouseDrag:function(t){if(this.dragged=!0,!this.options.disabled){var i,s=this,n=this.options,a=this.opos[0],o=this.opos[1],r=t.pageX,h=t.pageY;return a>r&&(i=r,r=a,a=i),o>h&&(i=h,h=o,o=i),this.helper.css({left:a,top:o,width:r-a,height:h-o}),this.selectees.each(function(){var i=e.data(this,"selectable-item"),l=!1;i&&i.element!==s.element[0]&&("touch"===n.tolerance?l=!(i.left>r||a>i.right||i.top>h||o>i.bottom):"fit"===n.tolerance&&(l=i.left>a&&r>i.right&&i.top>o&&h>i.bottom),l?(i.selected&&(i.$element.removeClass("ui-selected"),i.selected=!1),i.unselecting&&(i.$element.removeClass("ui-unselecting"),i.unselecting=!1),i.selecting||(i.$element.addClass("ui-selecting"),i.selecting=!0,s._trigger("selecting",t,{selecting:i.element}))):(i.selecting&&((t.metaKey||t.ctrlKey)&&i.startselected?(i.$element.removeClass("ui-selecting"),i.selecting=!1,i.$element.addClass("ui-selected"),i.selected=!0):(i.$element.removeClass("ui-selecting"),i.selecting=!1,i.startselected&&(i.$element.addClass("ui-unselecting"),i.unselecting=!0),s._trigger("unselecting",t,{unselecting:i.element}))),i.selected&&(t.metaKey||t.ctrlKey||i.startselected||(i.$element.removeClass("ui-selected"),i.selected=!1,i.$element.addClass("ui-unselecting"),i.unselecting=!0,s._trigger("unselecting",t,{unselecting:i.element})))))
}),!1}},_mouseStop:function(t){var i=this;return this.dragged=!1,e(".ui-unselecting",this.element[0]).each(function(){var s=e.data(this,"selectable-item");s.$element.removeClass("ui-unselecting"),s.unselecting=!1,s.startselected=!1,i._trigger("unselected",t,{unselected:s.element})}),e(".ui-selecting",this.element[0]).each(function(){var s=e.data(this,"selectable-item");s.$element.removeClass("ui-selecting").addClass("ui-selected"),s.selecting=!1,s.selected=!0,s.startselected=!0,i._trigger("selected",t,{selected:s.element})}),this._trigger("stop",t),this.helper.remove(),!1}}),e.widget("ui.sortable",e.ui.mouse,{version:"1.11.1",widgetEventPrefix:"sort",ready:!1,options:{appendTo:"parent",axis:!1,connectWith:!1,containment:!1,cursor:"auto",cursorAt:!1,dropOnEmpty:!0,forcePlaceholderSize:!1,forceHelperSize:!1,grid:!1,handle:!1,helper:"original",items:"> *",opacity:!1,placeholder:!1,revert:!1,scroll:!0,scrollSensitivity:20,scrollSpeed:20,scope:"default",tolerance:"intersect",zIndex:1e3,activate:null,beforeStop:null,change:null,deactivate:null,out:null,over:null,receive:null,remove:null,sort:null,start:null,stop:null,update:null},_isOverAxis:function(e,t,i){return e>=t&&t+i>e},_isFloating:function(e){return/left|right/.test(e.css("float"))||/inline|table-cell/.test(e.css("display"))},_create:function(){var e=this.options;this.containerCache={},this.element.addClass("ui-sortable"),this.refresh(),this.floating=this.items.length?"x"===e.axis||this._isFloating(this.items[0].item):!1,this.offset=this.element.offset(),this._mouseInit(),this._setHandleClassName(),this.ready=!0},_setOption:function(e,t){this._super(e,t),"handle"===e&&this._setHandleClassName()},_setHandleClassName:function(){this.element.find(".ui-sortable-handle").removeClass("ui-sortable-handle"),e.each(this.items,function(){(this.instance.options.handle?this.item.find(this.instance.options.handle):this.item).addClass("ui-sortable-handle")})},_destroy:function(){this.element.removeClass("ui-sortable ui-sortable-disabled").find(".ui-sortable-handle").removeClass("ui-sortable-handle"),this._mouseDestroy();for(var e=this.items.length-1;e>=0;e--)this.items[e].item.removeData(this.widgetName+"-item");return this},_mouseCapture:function(t,i){var s=null,n=!1,a=this;return this.reverting?!1:this.options.disabled||"static"===this.options.type?!1:(this._refreshItems(t),e(t.target).parents().each(function(){return e.data(this,a.widgetName+"-item")===a?(s=e(this),!1):void 0}),e.data(t.target,a.widgetName+"-item")===a&&(s=e(t.target)),s?!this.options.handle||i||(e(this.options.handle,s).find("*").addBack().each(function(){this===t.target&&(n=!0)}),n)?(this.currentItem=s,this._removeCurrentsFromItems(),!0):!1:!1)},_mouseStart:function(t,i,s){var n,a,o=this.options;if(this.currentContainer=this,this.refreshPositions(),this.helper=this._createHelper(t),this._cacheHelperProportions(),this._cacheMargins(),this.scrollParent=this.helper.scrollParent(),this.offset=this.currentItem.offset(),this.offset={top:this.offset.top-this.margins.top,left:this.offset.left-this.margins.left},e.extend(this.offset,{click:{left:t.pageX-this.offset.left,top:t.pageY-this.offset.top},parent:this._getParentOffset(),relative:this._getRelativeOffset()}),this.helper.css("position","absolute"),this.cssPosition=this.helper.css("position"),this.originalPosition=this._generatePosition(t),this.originalPageX=t.pageX,this.originalPageY=t.pageY,o.cursorAt&&this._adjustOffsetFromHelper(o.cursorAt),this.domPosition={prev:this.currentItem.prev()[0],parent:this.currentItem.parent()[0]},this.helper[0]!==this.currentItem[0]&&this.currentItem.hide(),this._createPlaceholder(),o.containment&&this._setContainment(),o.cursor&&"auto"!==o.cursor&&(a=this.document.find("body"),this.storedCursor=a.css("cursor"),a.css("cursor",o.cursor),this.storedStylesheet=e("<style>*{ cursor: "+o.cursor+" !important; }</style>").appendTo(a)),o.opacity&&(this.helper.css("opacity")&&(this._storedOpacity=this.helper.css("opacity")),this.helper.css("opacity",o.opacity)),o.zIndex&&(this.helper.css("zIndex")&&(this._storedZIndex=this.helper.css("zIndex")),this.helper.css("zIndex",o.zIndex)),this.scrollParent[0]!==document&&"HTML"!==this.scrollParent[0].tagName&&(this.overflowOffset=this.scrollParent.offset()),this._trigger("start",t,this._uiHash()),this._preserveHelperProportions||this._cacheHelperProportions(),!s)for(n=this.containers.length-1;n>=0;n--)this.containers[n]._trigger("activate",t,this._uiHash(this));return e.ui.ddmanager&&(e.ui.ddmanager.current=this),e.ui.ddmanager&&!o.dropBehaviour&&e.ui.ddmanager.prepareOffsets(this,t),this.dragging=!0,this.helper.addClass("ui-sortable-helper"),this._mouseDrag(t),!0},_mouseDrag:function(t){var i,s,n,a,o=this.options,r=!1;for(this.position=this._generatePosition(t),this.positionAbs=this._convertPositionTo("absolute"),this.lastPositionAbs||(this.lastPositionAbs=this.positionAbs),this.options.scroll&&(this.scrollParent[0]!==document&&"HTML"!==this.scrollParent[0].tagName?(this.overflowOffset.top+this.scrollParent[0].offsetHeight-t.pageY<o.scrollSensitivity?this.scrollParent[0].scrollTop=r=this.scrollParent[0].scrollTop+o.scrollSpeed:t.pageY-this.overflowOffset.top<o.scrollSensitivity&&(this.scrollParent[0].scrollTop=r=this.scrollParent[0].scrollTop-o.scrollSpeed),this.overflowOffset.left+this.scrollParent[0].offsetWidth-t.pageX<o.scrollSensitivity?this.scrollParent[0].scrollLeft=r=this.scrollParent[0].scrollLeft+o.scrollSpeed:t.pageX-this.overflowOffset.left<o.scrollSensitivity&&(this.scrollParent[0].scrollLeft=r=this.scrollParent[0].scrollLeft-o.scrollSpeed)):(t.pageY-e(document).scrollTop()<o.scrollSensitivity?r=e(document).scrollTop(e(document).scrollTop()-o.scrollSpeed):e(window).height()-(t.pageY-e(document).scrollTop())<o.scrollSensitivity&&(r=e(document).scrollTop(e(document).scrollTop()+o.scrollSpeed)),t.pageX-e(document).scrollLeft()<o.scrollSensitivity?r=e(document).scrollLeft(e(document).scrollLeft()-o.scrollSpeed):e(window).width()-(t.pageX-e(document).scrollLeft())<o.scrollSensitivity&&(r=e(document).scrollLeft(e(document).scrollLeft()+o.scrollSpeed))),r!==!1&&e.ui.ddmanager&&!o.dropBehaviour&&e.ui.ddmanager.prepareOffsets(this,t)),this.positionAbs=this._convertPositionTo("absolute"),this.options.axis&&"y"===this.options.axis||(this.helper[0].style.left=this.position.left+"px"),this.options.axis&&"x"===this.options.axis||(this.helper[0].style.top=this.position.top+"px"),i=this.items.length-1;i>=0;i--)if(s=this.items[i],n=s.item[0],a=this._intersectsWithPointer(s),a&&s.instance===this.currentContainer&&n!==this.currentItem[0]&&this.placeholder[1===a?"next":"prev"]()[0]!==n&&!e.contains(this.placeholder[0],n)&&("semi-dynamic"===this.options.type?!e.contains(this.element[0],n):!0)){if(this.direction=1===a?"down":"up","pointer"!==this.options.tolerance&&!this._intersectsWithSides(s))break;this._rearrange(t,s),this._trigger("change",t,this._uiHash());break}return this._contactContainers(t),e.ui.ddmanager&&e.ui.ddmanager.drag(this,t),this._trigger("sort",t,this._uiHash()),this.lastPositionAbs=this.positionAbs,!1},_mouseStop:function(t,i){if(t){if(e.ui.ddmanager&&!this.options.dropBehaviour&&e.ui.ddmanager.drop(this,t),this.options.revert){var s=this,n=this.placeholder.offset(),a=this.options.axis,o={};a&&"x"!==a||(o.left=n.left-this.offset.parent.left-this.margins.left+(this.offsetParent[0]===document.body?0:this.offsetParent[0].scrollLeft)),a&&"y"!==a||(o.top=n.top-this.offset.parent.top-this.margins.top+(this.offsetParent[0]===document.body?0:this.offsetParent[0].scrollTop)),this.reverting=!0,e(this.helper).animate(o,parseInt(this.options.revert,10)||500,function(){s._clear(t)})}else this._clear(t,i);return!1}},cancel:function(){if(this.dragging){this._mouseUp({target:null}),"original"===this.options.helper?this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper"):this.currentItem.show();for(var t=this.containers.length-1;t>=0;t--)this.containers[t]._trigger("deactivate",null,this._uiHash(this)),this.containers[t].containerCache.over&&(this.containers[t]._trigger("out",null,this._uiHash(this)),this.containers[t].containerCache.over=0)}return this.placeholder&&(this.placeholder[0].parentNode&&this.placeholder[0].parentNode.removeChild(this.placeholder[0]),"original"!==this.options.helper&&this.helper&&this.helper[0].parentNode&&this.helper.remove(),e.extend(this,{helper:null,dragging:!1,reverting:!1,_noFinalSort:null}),this.domPosition.prev?e(this.domPosition.prev).after(this.currentItem):e(this.domPosition.parent).prepend(this.currentItem)),this},serialize:function(t){var i=this._getItemsAsjQuery(t&&t.connected),s=[];return t=t||{},e(i).each(function(){var i=(e(t.item||this).attr(t.attribute||"id")||"").match(t.expression||/(.+)[\-=_](.+)/);i&&s.push((t.key||i[1]+"[]")+"="+(t.key&&t.expression?i[1]:i[2]))}),!s.length&&t.key&&s.push(t.key+"="),s.join("&")},toArray:function(t){var i=this._getItemsAsjQuery(t&&t.connected),s=[];return t=t||{},i.each(function(){s.push(e(t.item||this).attr(t.attribute||"id")||"")}),s},_intersectsWith:function(e){var t=this.positionAbs.left,i=t+this.helperProportions.width,s=this.positionAbs.top,n=s+this.helperProportions.height,a=e.left,o=a+e.width,r=e.top,h=r+e.height,l=this.offset.click.top,u=this.offset.click.left,d="x"===this.options.axis||s+l>r&&h>s+l,c="y"===this.options.axis||t+u>a&&o>t+u,p=d&&c;return"pointer"===this.options.tolerance||this.options.forcePointerForContainers||"pointer"!==this.options.tolerance&&this.helperProportions[this.floating?"width":"height"]>e[this.floating?"width":"height"]?p:t+this.helperProportions.width/2>a&&o>i-this.helperProportions.width/2&&s+this.helperProportions.height/2>r&&h>n-this.helperProportions.height/2},_intersectsWithPointer:function(e){var t="x"===this.options.axis||this._isOverAxis(this.positionAbs.top+this.offset.click.top,e.top,e.height),i="y"===this.options.axis||this._isOverAxis(this.positionAbs.left+this.offset.click.left,e.left,e.width),s=t&&i,n=this._getDragVerticalDirection(),a=this._getDragHorizontalDirection();return s?this.floating?a&&"right"===a||"down"===n?2:1:n&&("down"===n?2:1):!1},_intersectsWithSides:function(e){var t=this._isOverAxis(this.positionAbs.top+this.offset.click.top,e.top+e.height/2,e.height),i=this._isOverAxis(this.positionAbs.left+this.offset.click.left,e.left+e.width/2,e.width),s=this._getDragVerticalDirection(),n=this._getDragHorizontalDirection();return this.floating&&n?"right"===n&&i||"left"===n&&!i:s&&("down"===s&&t||"up"===s&&!t)},_getDragVerticalDirection:function(){var e=this.positionAbs.top-this.lastPositionAbs.top;return 0!==e&&(e>0?"down":"up")},_getDragHorizontalDirection:function(){var e=this.positionAbs.left-this.lastPositionAbs.left;return 0!==e&&(e>0?"right":"left")},refresh:function(e){return this._refreshItems(e),this._setHandleClassName(),this.refreshPositions(),this},_connectWith:function(){var e=this.options;return e.connectWith.constructor===String?[e.connectWith]:e.connectWith},_getItemsAsjQuery:function(t){function i(){r.push(this)}var s,n,a,o,r=[],h=[],l=this._connectWith();if(l&&t)for(s=l.length-1;s>=0;s--)for(a=e(l[s]),n=a.length-1;n>=0;n--)o=e.data(a[n],this.widgetFullName),o&&o!==this&&!o.options.disabled&&h.push([e.isFunction(o.options.items)?o.options.items.call(o.element):e(o.options.items,o.element).not(".ui-sortable-helper").not(".ui-sortable-placeholder"),o]);for(h.push([e.isFunction(this.options.items)?this.options.items.call(this.element,null,{options:this.options,item:this.currentItem}):e(this.options.items,this.element).not(".ui-sortable-helper").not(".ui-sortable-placeholder"),this]),s=h.length-1;s>=0;s--)h[s][0].each(i);return e(r)},_removeCurrentsFromItems:function(){var t=this.currentItem.find(":data("+this.widgetName+"-item)");this.items=e.grep(this.items,function(e){for(var i=0;t.length>i;i++)if(t[i]===e.item[0])return!1;return!0})},_refreshItems:function(t){this.items=[],this.containers=[this];var i,s,n,a,o,r,h,l,u=this.items,d=[[e.isFunction(this.options.items)?this.options.items.call(this.element[0],t,{item:this.currentItem}):e(this.options.items,this.element),this]],c=this._connectWith();if(c&&this.ready)for(i=c.length-1;i>=0;i--)for(n=e(c[i]),s=n.length-1;s>=0;s--)a=e.data(n[s],this.widgetFullName),a&&a!==this&&!a.options.disabled&&(d.push([e.isFunction(a.options.items)?a.options.items.call(a.element[0],t,{item:this.currentItem}):e(a.options.items,a.element),a]),this.containers.push(a));for(i=d.length-1;i>=0;i--)for(o=d[i][1],r=d[i][0],s=0,l=r.length;l>s;s++)h=e(r[s]),h.data(this.widgetName+"-item",o),u.push({item:h,instance:o,width:0,height:0,left:0,top:0})},refreshPositions:function(t){this.offsetParent&&this.helper&&(this.offset.parent=this._getParentOffset());var i,s,n,a;for(i=this.items.length-1;i>=0;i--)s=this.items[i],s.instance!==this.currentContainer&&this.currentContainer&&s.item[0]!==this.currentItem[0]||(n=this.options.toleranceElement?e(this.options.toleranceElement,s.item):s.item,t||(s.width=n.outerWidth(),s.height=n.outerHeight()),a=n.offset(),s.left=a.left,s.top=a.top);if(this.options.custom&&this.options.custom.refreshContainers)this.options.custom.refreshContainers.call(this);else for(i=this.containers.length-1;i>=0;i--)a=this.containers[i].element.offset(),this.containers[i].containerCache.left=a.left,this.containers[i].containerCache.top=a.top,this.containers[i].containerCache.width=this.containers[i].element.outerWidth(),this.containers[i].containerCache.height=this.containers[i].element.outerHeight();return this},_createPlaceholder:function(t){t=t||this;var i,s=t.options;s.placeholder&&s.placeholder.constructor!==String||(i=s.placeholder,s.placeholder={element:function(){var s=t.currentItem[0].nodeName.toLowerCase(),n=e("<"+s+">",t.document[0]).addClass(i||t.currentItem[0].className+" ui-sortable-placeholder").removeClass("ui-sortable-helper");return"tr"===s?t.currentItem.children().each(function(){e("<td>&#160;</td>",t.document[0]).attr("colspan",e(this).attr("colspan")||1).appendTo(n)}):"img"===s&&n.attr("src",t.currentItem.attr("src")),i||n.css("visibility","hidden"),n},update:function(e,n){(!i||s.forcePlaceholderSize)&&(n.height()||n.height(t.currentItem.innerHeight()-parseInt(t.currentItem.css("paddingTop")||0,10)-parseInt(t.currentItem.css("paddingBottom")||0,10)),n.width()||n.width(t.currentItem.innerWidth()-parseInt(t.currentItem.css("paddingLeft")||0,10)-parseInt(t.currentItem.css("paddingRight")||0,10)))}}),t.placeholder=e(s.placeholder.element.call(t.element,t.currentItem)),t.currentItem.after(t.placeholder),s.placeholder.update(t,t.placeholder)},_contactContainers:function(t){var i,s,n,a,o,r,h,l,u,d,c=null,p=null;for(i=this.containers.length-1;i>=0;i--)if(!e.contains(this.currentItem[0],this.containers[i].element[0]))if(this._intersectsWith(this.containers[i].containerCache)){if(c&&e.contains(this.containers[i].element[0],c.element[0]))continue;c=this.containers[i],p=i}else this.containers[i].containerCache.over&&(this.containers[i]._trigger("out",t,this._uiHash(this)),this.containers[i].containerCache.over=0);if(c)if(1===this.containers.length)this.containers[p].containerCache.over||(this.containers[p]._trigger("over",t,this._uiHash(this)),this.containers[p].containerCache.over=1);else{for(n=1e4,a=null,u=c.floating||this._isFloating(this.currentItem),o=u?"left":"top",r=u?"width":"height",d=u?"clientX":"clientY",s=this.items.length-1;s>=0;s--)e.contains(this.containers[p].element[0],this.items[s].item[0])&&this.items[s].item[0]!==this.currentItem[0]&&(h=this.items[s].item.offset()[o],l=!1,t[d]-h>this.items[s][r]/2&&(l=!0),n>Math.abs(t[d]-h)&&(n=Math.abs(t[d]-h),a=this.items[s],this.direction=l?"up":"down"));if(!a&&!this.options.dropOnEmpty)return;if(this.currentContainer===this.containers[p])return;a?this._rearrange(t,a,null,!0):this._rearrange(t,null,this.containers[p].element,!0),this._trigger("change",t,this._uiHash()),this.containers[p]._trigger("change",t,this._uiHash(this)),this.currentContainer=this.containers[p],this.options.placeholder.update(this.currentContainer,this.placeholder),this.containers[p]._trigger("over",t,this._uiHash(this)),this.containers[p].containerCache.over=1}},_createHelper:function(t){var i=this.options,s=e.isFunction(i.helper)?e(i.helper.apply(this.element[0],[t,this.currentItem])):"clone"===i.helper?this.currentItem.clone():this.currentItem;return s.parents("body").length||e("parent"!==i.appendTo?i.appendTo:this.currentItem[0].parentNode)[0].appendChild(s[0]),s[0]===this.currentItem[0]&&(this._storedCSS={width:this.currentItem[0].style.width,height:this.currentItem[0].style.height,position:this.currentItem.css("position"),top:this.currentItem.css("top"),left:this.currentItem.css("left")}),(!s[0].style.width||i.forceHelperSize)&&s.width(this.currentItem.width()),(!s[0].style.height||i.forceHelperSize)&&s.height(this.currentItem.height()),s},_adjustOffsetFromHelper:function(t){"string"==typeof t&&(t=t.split(" ")),e.isArray(t)&&(t={left:+t[0],top:+t[1]||0}),"left"in t&&(this.offset.click.left=t.left+this.margins.left),"right"in t&&(this.offset.click.left=this.helperProportions.width-t.right+this.margins.left),"top"in t&&(this.offset.click.top=t.top+this.margins.top),"bottom"in t&&(this.offset.click.top=this.helperProportions.height-t.bottom+this.margins.top)},_getParentOffset:function(){this.offsetParent=this.helper.offsetParent();var t=this.offsetParent.offset();return"absolute"===this.cssPosition&&this.scrollParent[0]!==document&&e.contains(this.scrollParent[0],this.offsetParent[0])&&(t.left+=this.scrollParent.scrollLeft(),t.top+=this.scrollParent.scrollTop()),(this.offsetParent[0]===document.body||this.offsetParent[0].tagName&&"html"===this.offsetParent[0].tagName.toLowerCase()&&e.ui.ie)&&(t={top:0,left:0}),{top:t.top+(parseInt(this.offsetParent.css("borderTopWidth"),10)||0),left:t.left+(parseInt(this.offsetParent.css("borderLeftWidth"),10)||0)}},_getRelativeOffset:function(){if("relative"===this.cssPosition){var e=this.currentItem.position();return{top:e.top-(parseInt(this.helper.css("top"),10)||0)+this.scrollParent.scrollTop(),left:e.left-(parseInt(this.helper.css("left"),10)||0)+this.scrollParent.scrollLeft()}}return{top:0,left:0}},_cacheMargins:function(){this.margins={left:parseInt(this.currentItem.css("marginLeft"),10)||0,top:parseInt(this.currentItem.css("marginTop"),10)||0}},_cacheHelperProportions:function(){this.helperProportions={width:this.helper.outerWidth(),height:this.helper.outerHeight()}},_setContainment:function(){var t,i,s,n=this.options;"parent"===n.containment&&(n.containment=this.helper[0].parentNode),("document"===n.containment||"window"===n.containment)&&(this.containment=[0-this.offset.relative.left-this.offset.parent.left,0-this.offset.relative.top-this.offset.parent.top,e("document"===n.containment?document:window).width()-this.helperProportions.width-this.margins.left,(e("document"===n.containment?document:window).height()||document.body.parentNode.scrollHeight)-this.helperProportions.height-this.margins.top]),/^(document|window|parent)$/.test(n.containment)||(t=e(n.containment)[0],i=e(n.containment).offset(),s="hidden"!==e(t).css("overflow"),this.containment=[i.left+(parseInt(e(t).css("borderLeftWidth"),10)||0)+(parseInt(e(t).css("paddingLeft"),10)||0)-this.margins.left,i.top+(parseInt(e(t).css("borderTopWidth"),10)||0)+(parseInt(e(t).css("paddingTop"),10)||0)-this.margins.top,i.left+(s?Math.max(t.scrollWidth,t.offsetWidth):t.offsetWidth)-(parseInt(e(t).css("borderLeftWidth"),10)||0)-(parseInt(e(t).css("paddingRight"),10)||0)-this.helperProportions.width-this.margins.left,i.top+(s?Math.max(t.scrollHeight,t.offsetHeight):t.offsetHeight)-(parseInt(e(t).css("borderTopWidth"),10)||0)-(parseInt(e(t).css("paddingBottom"),10)||0)-this.helperProportions.height-this.margins.top])},_convertPositionTo:function(t,i){i||(i=this.position);var s="absolute"===t?1:-1,n="absolute"!==this.cssPosition||this.scrollParent[0]!==document&&e.contains(this.scrollParent[0],this.offsetParent[0])?this.scrollParent:this.offsetParent,a=/(html|body)/i.test(n[0].tagName);return{top:i.top+this.offset.relative.top*s+this.offset.parent.top*s-("fixed"===this.cssPosition?-this.scrollParent.scrollTop():a?0:n.scrollTop())*s,left:i.left+this.offset.relative.left*s+this.offset.parent.left*s-("fixed"===this.cssPosition?-this.scrollParent.scrollLeft():a?0:n.scrollLeft())*s}},_generatePosition:function(t){var i,s,n=this.options,a=t.pageX,o=t.pageY,r="absolute"!==this.cssPosition||this.scrollParent[0]!==document&&e.contains(this.scrollParent[0],this.offsetParent[0])?this.scrollParent:this.offsetParent,h=/(html|body)/i.test(r[0].tagName);return"relative"!==this.cssPosition||this.scrollParent[0]!==document&&this.scrollParent[0]!==this.offsetParent[0]||(this.offset.relative=this._getRelativeOffset()),this.originalPosition&&(this.containment&&(t.pageX-this.offset.click.left<this.containment[0]&&(a=this.containment[0]+this.offset.click.left),t.pageY-this.offset.click.top<this.containment[1]&&(o=this.containment[1]+this.offset.click.top),t.pageX-this.offset.click.left>this.containment[2]&&(a=this.containment[2]+this.offset.click.left),t.pageY-this.offset.click.top>this.containment[3]&&(o=this.containment[3]+this.offset.click.top)),n.grid&&(i=this.originalPageY+Math.round((o-this.originalPageY)/n.grid[1])*n.grid[1],o=this.containment?i-this.offset.click.top>=this.containment[1]&&i-this.offset.click.top<=this.containment[3]?i:i-this.offset.click.top>=this.containment[1]?i-n.grid[1]:i+n.grid[1]:i,s=this.originalPageX+Math.round((a-this.originalPageX)/n.grid[0])*n.grid[0],a=this.containment?s-this.offset.click.left>=this.containment[0]&&s-this.offset.click.left<=this.containment[2]?s:s-this.offset.click.left>=this.containment[0]?s-n.grid[0]:s+n.grid[0]:s)),{top:o-this.offset.click.top-this.offset.relative.top-this.offset.parent.top+("fixed"===this.cssPosition?-this.scrollParent.scrollTop():h?0:r.scrollTop()),left:a-this.offset.click.left-this.offset.relative.left-this.offset.parent.left+("fixed"===this.cssPosition?-this.scrollParent.scrollLeft():h?0:r.scrollLeft())}},_rearrange:function(e,t,i,s){i?i[0].appendChild(this.placeholder[0]):t.item[0].parentNode.insertBefore(this.placeholder[0],"down"===this.direction?t.item[0]:t.item[0].nextSibling),this.counter=this.counter?++this.counter:1;var n=this.counter;this._delay(function(){n===this.counter&&this.refreshPositions(!s)})},_clear:function(e,t){function i(e,t,i){return function(s){i._trigger(e,s,t._uiHash(t))}}this.reverting=!1;var s,n=[];if(!this._noFinalSort&&this.currentItem.parent().length&&this.placeholder.before(this.currentItem),this._noFinalSort=null,this.helper[0]===this.currentItem[0]){for(s in this._storedCSS)("auto"===this._storedCSS[s]||"static"===this._storedCSS[s])&&(this._storedCSS[s]="");this.currentItem.css(this._storedCSS).removeClass("ui-sortable-helper")}else this.currentItem.show();for(this.fromOutside&&!t&&n.push(function(e){this._trigger("receive",e,this._uiHash(this.fromOutside))}),!this.fromOutside&&this.domPosition.prev===this.currentItem.prev().not(".ui-sortable-helper")[0]&&this.domPosition.parent===this.currentItem.parent()[0]||t||n.push(function(e){this._trigger("update",e,this._uiHash())}),this!==this.currentContainer&&(t||(n.push(function(e){this._trigger("remove",e,this._uiHash())}),n.push(function(e){return function(t){e._trigger("receive",t,this._uiHash(this))}}.call(this,this.currentContainer)),n.push(function(e){return function(t){e._trigger("update",t,this._uiHash(this))}}.call(this,this.currentContainer)))),s=this.containers.length-1;s>=0;s--)t||n.push(i("deactivate",this,this.containers[s])),this.containers[s].containerCache.over&&(n.push(i("out",this,this.containers[s])),this.containers[s].containerCache.over=0);if(this.storedCursor&&(this.document.find("body").css("cursor",this.storedCursor),this.storedStylesheet.remove()),this._storedOpacity&&this.helper.css("opacity",this._storedOpacity),this._storedZIndex&&this.helper.css("zIndex","auto"===this._storedZIndex?"":this._storedZIndex),this.dragging=!1,this.cancelHelperRemoval){if(!t){for(this._trigger("beforeStop",e,this._uiHash()),s=0;n.length>s;s++)n[s].call(this,e);this._trigger("stop",e,this._uiHash())}return this.fromOutside=!1,!1}if(t||this._trigger("beforeStop",e,this._uiHash()),this.placeholder[0].parentNode.removeChild(this.placeholder[0]),this.helper[0]!==this.currentItem[0]&&this.helper.remove(),this.helper=null,!t){for(s=0;n.length>s;s++)n[s].call(this,e);this._trigger("stop",e,this._uiHash())}return this.fromOutside=!1,!0},_trigger:function(){e.Widget.prototype._trigger.apply(this,arguments)===!1&&this.cancel()},_uiHash:function(t){var i=t||this;return{helper:i.helper,placeholder:i.placeholder||e([]),position:i.position,originalPosition:i.originalPosition,offset:i.positionAbs,item:i.currentItem,sender:t?t.element:null}}}),e.widget("ui.slider",e.ui.mouse,{version:"1.11.1",widgetEventPrefix:"slide",options:{animate:!1,distance:0,max:100,min:0,orientation:"horizontal",range:!1,step:1,value:0,values:null,change:null,slide:null,start:null,stop:null},numPages:5,_create:function(){this._keySliding=!1,this._mouseSliding=!1,this._animateOff=!0,this._handleIndex=null,this._detectOrientation(),this._mouseInit(),this.element.addClass("ui-slider ui-slider-"+this.orientation+" ui-widget"+" ui-widget-content"+" ui-corner-all"),this._refresh(),this._setOption("disabled",this.options.disabled),this._animateOff=!1},_refresh:function(){this._createRange(),this._createHandles(),this._setupEvents(),this._refreshValue()},_createHandles:function(){var t,i,s=this.options,n=this.element.find(".ui-slider-handle").addClass("ui-state-default ui-corner-all"),a="<span class='ui-slider-handle ui-state-default ui-corner-all' tabindex='0'></span>",o=[];for(i=s.values&&s.values.length||1,n.length>i&&(n.slice(i).remove(),n=n.slice(0,i)),t=n.length;i>t;t++)o.push(a);this.handles=n.add(e(o.join("")).appendTo(this.element)),this.handle=this.handles.eq(0),this.handles.each(function(t){e(this).data("ui-slider-handle-index",t)})},_createRange:function(){var t=this.options,i="";t.range?(t.range===!0&&(t.values?t.values.length&&2!==t.values.length?t.values=[t.values[0],t.values[0]]:e.isArray(t.values)&&(t.values=t.values.slice(0)):t.values=[this._valueMin(),this._valueMin()]),this.range&&this.range.length?this.range.removeClass("ui-slider-range-min ui-slider-range-max").css({left:"",bottom:""}):(this.range=e("<div></div>").appendTo(this.element),i="ui-slider-range ui-widget-header ui-corner-all"),this.range.addClass(i+("min"===t.range||"max"===t.range?" ui-slider-range-"+t.range:""))):(this.range&&this.range.remove(),this.range=null)},_setupEvents:function(){this._off(this.handles),this._on(this.handles,this._handleEvents),this._hoverable(this.handles),this._focusable(this.handles)},_destroy:function(){this.handles.remove(),this.range&&this.range.remove(),this.element.removeClass("ui-slider ui-slider-horizontal ui-slider-vertical ui-widget ui-widget-content ui-corner-all"),this._mouseDestroy()},_mouseCapture:function(t){var i,s,n,a,o,r,h,l,u=this,d=this.options;return d.disabled?!1:(this.elementSize={width:this.element.outerWidth(),height:this.element.outerHeight()},this.elementOffset=this.element.offset(),i={x:t.pageX,y:t.pageY},s=this._normValueFromMouse(i),n=this._valueMax()-this._valueMin()+1,this.handles.each(function(t){var i=Math.abs(s-u.values(t));(n>i||n===i&&(t===u._lastChangedValue||u.values(t)===d.min))&&(n=i,a=e(this),o=t)}),r=this._start(t,o),r===!1?!1:(this._mouseSliding=!0,this._handleIndex=o,a.addClass("ui-state-active").focus(),h=a.offset(),l=!e(t.target).parents().addBack().is(".ui-slider-handle"),this._clickOffset=l?{left:0,top:0}:{left:t.pageX-h.left-a.width()/2,top:t.pageY-h.top-a.height()/2-(parseInt(a.css("borderTopWidth"),10)||0)-(parseInt(a.css("borderBottomWidth"),10)||0)+(parseInt(a.css("marginTop"),10)||0)},this.handles.hasClass("ui-state-hover")||this._slide(t,o,s),this._animateOff=!0,!0))},_mouseStart:function(){return!0},_mouseDrag:function(e){var t={x:e.pageX,y:e.pageY},i=this._normValueFromMouse(t);return this._slide(e,this._handleIndex,i),!1},_mouseStop:function(e){return this.handles.removeClass("ui-state-active"),this._mouseSliding=!1,this._stop(e,this._handleIndex),this._change(e,this._handleIndex),this._handleIndex=null,this._clickOffset=null,this._animateOff=!1,!1},_detectOrientation:function(){this.orientation="vertical"===this.options.orientation?"vertical":"horizontal"},_normValueFromMouse:function(e){var t,i,s,n,a;return"horizontal"===this.orientation?(t=this.elementSize.width,i=e.x-this.elementOffset.left-(this._clickOffset?this._clickOffset.left:0)):(t=this.elementSize.height,i=e.y-this.elementOffset.top-(this._clickOffset?this._clickOffset.top:0)),s=i/t,s>1&&(s=1),0>s&&(s=0),"vertical"===this.orientation&&(s=1-s),n=this._valueMax()-this._valueMin(),a=this._valueMin()+s*n,this._trimAlignValue(a)},_start:function(e,t){var i={handle:this.handles[t],value:this.value()};return this.options.values&&this.options.values.length&&(i.value=this.values(t),i.values=this.values()),this._trigger("start",e,i)},_slide:function(e,t,i){var s,n,a;this.options.values&&this.options.values.length?(s=this.values(t?0:1),2===this.options.values.length&&this.options.range===!0&&(0===t&&i>s||1===t&&s>i)&&(i=s),i!==this.values(t)&&(n=this.values(),n[t]=i,a=this._trigger("slide",e,{handle:this.handles[t],value:i,values:n}),s=this.values(t?0:1),a!==!1&&this.values(t,i))):i!==this.value()&&(a=this._trigger("slide",e,{handle:this.handles[t],value:i}),a!==!1&&this.value(i))},_stop:function(e,t){var i={handle:this.handles[t],value:this.value()};this.options.values&&this.options.values.length&&(i.value=this.values(t),i.values=this.values()),this._trigger("stop",e,i)},_change:function(e,t){if(!this._keySliding&&!this._mouseSliding){var i={handle:this.handles[t],value:this.value()};this.options.values&&this.options.values.length&&(i.value=this.values(t),i.values=this.values()),this._lastChangedValue=t,this._trigger("change",e,i)}},value:function(e){return arguments.length?(this.options.value=this._trimAlignValue(e),this._refreshValue(),this._change(null,0),void 0):this._value()},values:function(t,i){var s,n,a;if(arguments.length>1)return this.options.values[t]=this._trimAlignValue(i),this._refreshValue(),this._change(null,t),void 0;if(!arguments.length)return this._values();if(!e.isArray(arguments[0]))return this.options.values&&this.options.values.length?this._values(t):this.value();for(s=this.options.values,n=arguments[0],a=0;s.length>a;a+=1)s[a]=this._trimAlignValue(n[a]),this._change(null,a);this._refreshValue()},_setOption:function(t,i){var s,n=0;switch("range"===t&&this.options.range===!0&&("min"===i?(this.options.value=this._values(0),this.options.values=null):"max"===i&&(this.options.value=this._values(this.options.values.length-1),this.options.values=null)),e.isArray(this.options.values)&&(n=this.options.values.length),"disabled"===t&&this.element.toggleClass("ui-state-disabled",!!i),this._super(t,i),t){case"orientation":this._detectOrientation(),this.element.removeClass("ui-slider-horizontal ui-slider-vertical").addClass("ui-slider-"+this.orientation),this._refreshValue(),this.handles.css("horizontal"===i?"bottom":"left","");break;case"value":this._animateOff=!0,this._refreshValue(),this._change(null,0),this._animateOff=!1;break;case"values":for(this._animateOff=!0,this._refreshValue(),s=0;n>s;s+=1)this._change(null,s);this._animateOff=!1;break;case"min":case"max":this._animateOff=!0,this._refreshValue(),this._animateOff=!1;break;case"range":this._animateOff=!0,this._refresh(),this._animateOff=!1}},_value:function(){var e=this.options.value;return e=this._trimAlignValue(e)},_values:function(e){var t,i,s;if(arguments.length)return t=this.options.values[e],t=this._trimAlignValue(t);if(this.options.values&&this.options.values.length){for(i=this.options.values.slice(),s=0;i.length>s;s+=1)i[s]=this._trimAlignValue(i[s]);return i}return[]},_trimAlignValue:function(e){if(this._valueMin()>=e)return this._valueMin();if(e>=this._valueMax())return this._valueMax();
var t=this.options.step>0?this.options.step:1,i=(e-this._valueMin())%t,s=e-i;return 2*Math.abs(i)>=t&&(s+=i>0?t:-t),parseFloat(s.toFixed(5))},_valueMin:function(){return this.options.min},_valueMax:function(){return this.options.max},_refreshValue:function(){var t,i,s,n,a,o=this.options.range,r=this.options,h=this,l=this._animateOff?!1:r.animate,u={};this.options.values&&this.options.values.length?this.handles.each(function(s){i=100*((h.values(s)-h._valueMin())/(h._valueMax()-h._valueMin())),u["horizontal"===h.orientation?"left":"bottom"]=i+"%",e(this).stop(1,1)[l?"animate":"css"](u,r.animate),h.options.range===!0&&("horizontal"===h.orientation?(0===s&&h.range.stop(1,1)[l?"animate":"css"]({left:i+"%"},r.animate),1===s&&h.range[l?"animate":"css"]({width:i-t+"%"},{queue:!1,duration:r.animate})):(0===s&&h.range.stop(1,1)[l?"animate":"css"]({bottom:i+"%"},r.animate),1===s&&h.range[l?"animate":"css"]({height:i-t+"%"},{queue:!1,duration:r.animate}))),t=i}):(s=this.value(),n=this._valueMin(),a=this._valueMax(),i=a!==n?100*((s-n)/(a-n)):0,u["horizontal"===this.orientation?"left":"bottom"]=i+"%",this.handle.stop(1,1)[l?"animate":"css"](u,r.animate),"min"===o&&"horizontal"===this.orientation&&this.range.stop(1,1)[l?"animate":"css"]({width:i+"%"},r.animate),"max"===o&&"horizontal"===this.orientation&&this.range[l?"animate":"css"]({width:100-i+"%"},{queue:!1,duration:r.animate}),"min"===o&&"vertical"===this.orientation&&this.range.stop(1,1)[l?"animate":"css"]({height:i+"%"},r.animate),"max"===o&&"vertical"===this.orientation&&this.range[l?"animate":"css"]({height:100-i+"%"},{queue:!1,duration:r.animate}))},_handleEvents:{keydown:function(t){var i,s,n,a,o=e(t.target).data("ui-slider-handle-index");switch(t.keyCode){case e.ui.keyCode.HOME:case e.ui.keyCode.END:case e.ui.keyCode.PAGE_UP:case e.ui.keyCode.PAGE_DOWN:case e.ui.keyCode.UP:case e.ui.keyCode.RIGHT:case e.ui.keyCode.DOWN:case e.ui.keyCode.LEFT:if(t.preventDefault(),!this._keySliding&&(this._keySliding=!0,e(t.target).addClass("ui-state-active"),i=this._start(t,o),i===!1))return}switch(a=this.options.step,s=n=this.options.values&&this.options.values.length?this.values(o):this.value(),t.keyCode){case e.ui.keyCode.HOME:n=this._valueMin();break;case e.ui.keyCode.END:n=this._valueMax();break;case e.ui.keyCode.PAGE_UP:n=this._trimAlignValue(s+(this._valueMax()-this._valueMin())/this.numPages);break;case e.ui.keyCode.PAGE_DOWN:n=this._trimAlignValue(s-(this._valueMax()-this._valueMin())/this.numPages);break;case e.ui.keyCode.UP:case e.ui.keyCode.RIGHT:if(s===this._valueMax())return;n=this._trimAlignValue(s+a);break;case e.ui.keyCode.DOWN:case e.ui.keyCode.LEFT:if(s===this._valueMin())return;n=this._trimAlignValue(s-a)}this._slide(t,o,n)},keyup:function(t){var i=e(t.target).data("ui-slider-handle-index");this._keySliding&&(this._keySliding=!1,this._stop(t,i),this._change(t,i),e(t.target).removeClass("ui-state-active"))}}})});;/*! jquery.selectBoxIt - v3.8.1 - 2013-11-17 
* http://www.selectboxit.com
* Copyright (c) 2013 Greg Franko; Licensed MIT*/
!function(a){"use strict";a(window.jQuery,window,document)}(function(a,b,c,d){"use strict";a.widget("selectBox.selectBoxIt",{VERSION:"3.8.1",options:{showEffect:"none",showEffectOptions:{},showEffectSpeed:"medium",hideEffect:"none",hideEffectOptions:{},hideEffectSpeed:"medium",showFirstOption:!0,defaultText:"",defaultIcon:"",downArrowIcon:"",theme:"default",keydownOpen:!0,isMobile:function(){var a=navigator.userAgent||navigator.vendor||b.opera;return/iPhone|iPod|iPad|Silk|Android|BlackBerry|Opera Mini|IEMobile/.test(a)},"native":!1,aggressiveChange:!1,selectWhenHidden:!0,viewport:a(b),similarSearch:!1,copyAttributes:["title","rel"],copyClasses:"button",nativeMousedown:!1,customShowHideEvent:!1,autoWidth:!0,html:!0,populate:"",dynamicPositioning:!0,hideCurrent:!1},getThemes:function(){var b=this,c=a(b.element).attr("data-theme")||"c";return{bootstrap:{focus:"active",hover:"",enabled:"enabled",disabled:"disabled",arrow:"caret",button:"btn",list:"dropdown-menu",container:"bootstrap",open:"open"},jqueryui:{focus:"ui-state-focus",hover:"ui-state-hover",enabled:"ui-state-enabled",disabled:"ui-state-disabled",arrow:"ui-icon ui-icon-triangle-1-s",button:"ui-widget ui-state-default",list:"ui-widget ui-widget-content",container:"jqueryui",open:"selectboxit-open"},jquerymobile:{focus:"ui-btn-down-"+c,hover:"ui-btn-hover-"+c,enabled:"ui-enabled",disabled:"ui-disabled",arrow:"ui-icon ui-icon-arrow-d ui-icon-shadow",button:"ui-btn ui-btn-icon-right ui-btn-corner-all ui-shadow ui-btn-up-"+c,list:"ui-btn ui-btn-icon-right ui-btn-corner-all ui-shadow ui-btn-up-"+c,container:"jquerymobile",open:"selectboxit-open"},"default":{focus:"selectboxit-focus",hover:"selectboxit-hover",enabled:"selectboxit-enabled",disabled:"selectboxit-disabled",arrow:"selectboxit-default-arrow",button:"selectboxit-btn",list:"selectboxit-list",container:"selectboxit-container",open:"selectboxit-open"}}},isDeferred:function(b){return a.isPlainObject(b)&&b.promise&&b.done},_create:function(b){var d=this,e=d.options.populate,f=d.options.theme;if(d.element.is("select"))return d.widgetProto=a.Widget.prototype,d.originalElem=d.element[0],d.selectBox=d.element,d.options.populate&&d.add&&!b&&d.add(e),d.selectItems=d.element.find("option"),d.firstSelectItem=d.selectItems.slice(0,1),d.documentHeight=a(c).height(),d.theme=a.isPlainObject(f)?a.extend({},d.getThemes()["default"],f):d.getThemes()[f]?d.getThemes()[f]:d.getThemes()["default"],d.currentFocus=0,d.blur=!0,d.textArray=[],d.currentIndex=0,d.currentText="",d.flipped=!1,b||(d.selectBoxStyles=d.selectBox.attr("style")),d._createDropdownButton()._createUnorderedList()._copyAttributes()._replaceSelectBox()._addClasses(d.theme)._eventHandlers(),d.originalElem.disabled&&d.disable&&d.disable(),d._ariaAccessibility&&d._ariaAccessibility(),d.isMobile=d.options.isMobile(),d._mobile&&d._mobile(),d.options["native"]&&this._applyNativeSelect(),d.triggerEvent("create"),d},_createDropdownButton:function(){var b=this,c=b.originalElemId=b.originalElem.id||"",d=b.originalElemValue=b.originalElem.value||"",e=b.originalElemName=b.originalElem.name||"",f=b.options.copyClasses,g=b.selectBox.attr("class")||"";return b.dropdownText=a("<span/>",{id:c&&c+"SelectBoxItText","class":"selectboxit-text",unselectable:"on",text:b.firstSelectItem.text()}).attr("data-val",d),b.dropdownImageContainer=a("<span/>",{"class":"selectboxit-option-icon-container"}),b.dropdownImage=a("<i/>",{id:c&&c+"SelectBoxItDefaultIcon","class":"selectboxit-default-icon",unselectable:"on"}),b.dropdown=a("<span/>",{id:c&&c+"SelectBoxIt","class":"selectboxit "+("button"===f?g:"")+" "+(b.selectBox.prop("disabled")?b.theme.disabled:b.theme.enabled),name:e,tabindex:b.selectBox.attr("tabindex")||"0",unselectable:"on"}).append(b.dropdownImageContainer.append(b.dropdownImage)).append(b.dropdownText),b.dropdownContainer=a("<span/>",{id:c&&c+"SelectBoxItContainer","class":"selectboxit-container "+b.theme.container+" "+("container"===f?g:"")}).append(b.dropdown),b},_createUnorderedList:function(){var b,c,d,e,f,g,h,i,j,k,l,m,n,o=this,p="",q=o.originalElemId||"",r=a("<ul/>",{id:q&&q+"SelectBoxItOptions","class":"selectboxit-options",tabindex:-1});if(o.options.showFirstOption||(o.selectItems.first().attr("disabled","disabled"),o.selectItems=o.selectBox.find("option").slice(1)),o.selectItems.each(function(q){m=a(this),c="",d="",b=m.prop("disabled"),e=m.attr("data-icon")||"",f=m.attr("data-iconurl")||"",g=f?"selectboxit-option-icon-url":"",h=f?"style=\"background-image:url('"+f+"');\"":"",i=m.attr("data-selectedtext"),j=m.attr("data-text"),l=j?j:m.text(),n=m.parent(),n.is("optgroup")&&(c="selectboxit-optgroup-option",0===m.index()&&(d='<span class="selectboxit-optgroup-header '+n.first().attr("class")+'"data-disabled="true">'+n.first().attr("label")+"</span>")),m.attr("value",this.value),p+=d+'<li data-id="'+q+'" data-val="'+this.value+'" data-disabled="'+b+'" class="'+c+" selectboxit-option "+(a(this).attr("class")||"")+'"><a class="selectboxit-option-anchor"><span class="selectboxit-option-icon-container"><i class="selectboxit-option-icon '+e+" "+(g||o.theme.container)+'"'+h+"></i></span>"+(o.options.html?l:o.htmlEscape(l))+"</a></li>",k=m.attr("data-search"),o.textArray[q]=b?"":k?k:l,this.selected&&(o._setText(o.dropdownText,i||l),o.currentFocus=q)}),o.options.defaultText||o.selectBox.attr("data-text")){var s=o.options.defaultText||o.selectBox.attr("data-text");o._setText(o.dropdownText,s),o.options.defaultText=s}return r.append(p),o.list=r,o.dropdownContainer.append(o.list),o.listItems=o.list.children("li"),o.listAnchors=o.list.find("a"),o.listItems.first().addClass("selectboxit-option-first"),o.listItems.last().addClass("selectboxit-option-last"),o.list.find("li[data-disabled='true']").not(".optgroupHeader").addClass(o.theme.disabled),o.dropdownImage.addClass(o.selectBox.attr("data-icon")||o.options.defaultIcon||o.listItems.eq(o.currentFocus).find("i").attr("class")),o.dropdownImage.attr("style",o.listItems.eq(o.currentFocus).find("i").attr("style")),o},_replaceSelectBox:function(){var b,c,e,f=this,g=f.originalElem.id||"",h=f.selectBox.attr("data-size"),i=f.listSize=h===d?"auto":"0"===h?"auto":+h;return f.selectBox.css("display","none").after(f.dropdownContainer),f.dropdownContainer.appendTo("body").addClass("selectboxit-rendering"),b=f.dropdown.height(),f.downArrow=a("<i/>",{id:g&&g+"SelectBoxItArrow","class":"selectboxit-arrow",unselectable:"on"}),f.downArrowContainer=a("<span/>",{id:g&&g+"SelectBoxItArrowContainer","class":"selectboxit-arrow-container",unselectable:"on"}).append(f.downArrow),f.dropdown.append(f.downArrowContainer),f.listItems.removeClass("selectboxit-selected").eq(f.currentFocus).addClass("selectboxit-selected"),c=f.downArrowContainer.outerWidth(!0),e=f.dropdownImage.outerWidth(!0),f.options.autoWidth&&(f.dropdown.css({width:"auto"}).css({width:f.list.outerWidth(!0)+c+e}),f.list.css({"min-width":f.dropdown.width()})),f.dropdownText.css({"max-width":f.dropdownContainer.outerWidth(!0)-(c+e)}),f.selectBox.after(f.dropdownContainer),f.dropdownContainer.removeClass("selectboxit-rendering"),"number"===a.type(i)&&(f.maxHeight=f.listAnchors.outerHeight(!0)*i),f},_scrollToView:function(a){var b=this,c=b.listItems.eq(b.currentFocus),d=b.list.scrollTop(),e=c.height(),f=c.position().top,g=Math.abs(f),h=b.list.height();return"search"===a?e>h-f?b.list.scrollTop(d+(f-(h-e))):-1>f&&b.list.scrollTop(f-e):"up"===a?-1>f&&b.list.scrollTop(d-g):"down"===a&&e>h-f&&b.list.scrollTop(d+(g-h+e)),b},_callbackSupport:function(b){var c=this;return a.isFunction(b)&&b.call(c,c.dropdown),c},_setText:function(a,b){var c=this;return c.options.html?a.html(b):a.text(b),c},open:function(a){var b=this,c=b.options.showEffect,d=b.options.showEffectSpeed,e=b.options.showEffectOptions,f=b.options["native"],g=b.isMobile;return!b.listItems.length||b.dropdown.hasClass(b.theme.disabled)?b:(f||g||this.list.is(":visible")||(b.triggerEvent("open"),b._dynamicPositioning&&b.options.dynamicPositioning&&b._dynamicPositioning(),"none"===c?b.list.show():"show"===c||"slideDown"===c||"fadeIn"===c?b.list[c](d):b.list.show(c,e,d),b.list.promise().done(function(){b._scrollToView("search"),b.triggerEvent("opened")})),b._callbackSupport(a),b)},close:function(a){var b=this,c=b.options.hideEffect,d=b.options.hideEffectSpeed,e=b.options.hideEffectOptions,f=b.options["native"],g=b.isMobile;return f||g||!b.list.is(":visible")||(b.triggerEvent("close"),"none"===c?b.list.hide():"hide"===c||"slideUp"===c||"fadeOut"===c?b.list[c](d):b.list.hide(c,e,d),b.list.promise().done(function(){b.triggerEvent("closed")})),b._callbackSupport(a),b},toggle:function(){var a=this,b=a.list.is(":visible");b?a.close():b||a.open()},_keyMappings:{38:"up",40:"down",13:"enter",8:"backspace",9:"tab",32:"space",27:"esc"},_keydownMethods:function(){var a=this,b=a.list.is(":visible")||!a.options.keydownOpen;return{down:function(){a.moveDown&&b&&a.moveDown()},up:function(){a.moveUp&&b&&a.moveUp()},enter:function(){var b=a.listItems.eq(a.currentFocus);a._update(b),"true"!==b.attr("data-preventclose")&&a.close(),a.triggerEvent("enter")},tab:function(){a.triggerEvent("tab-blur"),a.close()},backspace:function(){a.triggerEvent("backspace")},esc:function(){a.close()}}},_eventHandlers:function(){var b,c,d=this,e=d.options.nativeMousedown,f=d.options.customShowHideEvent,g=d.focusClass,h=d.hoverClass,i=d.openClass;return this.dropdown.on({"click.selectBoxIt":function(){d.dropdown.trigger("focus",!0),d.originalElem.disabled||(d.triggerEvent("click"),e||f||d.toggle())},"mousedown.selectBoxIt":function(){a(this).data("mdown",!0),d.triggerEvent("mousedown"),e&&!f&&d.toggle()},"mouseup.selectBoxIt":function(){d.triggerEvent("mouseup")},"blur.selectBoxIt":function(){d.blur&&(d.triggerEvent("blur"),d.close(),a(this).removeClass(g))},"focus.selectBoxIt":function(b,c){var e=a(this).data("mdown");a(this).removeData("mdown"),e||c||setTimeout(function(){d.triggerEvent("tab-focus")},0),c||(a(this).hasClass(d.theme.disabled)||a(this).addClass(g),d.triggerEvent("focus"))},"keydown.selectBoxIt":function(a){var b=d._keyMappings[a.keyCode],c=d._keydownMethods()[b];c&&(c(),!d.options.keydownOpen||"up"!==b&&"down"!==b||d.open()),c&&"tab"!==b&&a.preventDefault()},"keypress.selectBoxIt":function(a){var b=a.charCode||a.keyCode,c=d._keyMappings[a.charCode||a.keyCode],e=String.fromCharCode(b);d.search&&(!c||c&&"space"===c)&&d.search(e,!0,!0),"space"===c&&a.preventDefault()},"mouseenter.selectBoxIt":function(){d.triggerEvent("mouseenter")},"mouseleave.selectBoxIt":function(){d.triggerEvent("mouseleave")}}),d.list.on({"mouseover.selectBoxIt":function(){d.blur=!1},"mouseout.selectBoxIt":function(){d.blur=!0},"focusin.selectBoxIt":function(){d.dropdown.trigger("focus",!0)}}),d.list.on({"mousedown.selectBoxIt":function(){d._update(a(this)),d.triggerEvent("option-click"),"false"===a(this).attr("data-disabled")&&"true"!==a(this).attr("data-preventclose")&&d.close(),setTimeout(function(){d.dropdown.trigger("focus",!0)},0)},"focusin.selectBoxIt":function(){d.listItems.not(a(this)).removeAttr("data-active"),a(this).attr("data-active","");var b=d.list.is(":hidden");(d.options.searchWhenHidden&&b||d.options.aggressiveChange||b&&d.options.selectWhenHidden)&&d._update(a(this)),a(this).addClass(g)},"mouseup.selectBoxIt":function(){e&&!f&&(d._update(a(this)),d.triggerEvent("option-mouseup"),"false"===a(this).attr("data-disabled")&&"true"!==a(this).attr("data-preventclose")&&d.close())},"mouseenter.selectBoxIt":function(){"false"===a(this).attr("data-disabled")&&(d.listItems.removeAttr("data-active"),a(this).addClass(g).attr("data-active",""),d.listItems.not(a(this)).removeClass(g),a(this).addClass(g),d.currentFocus=+a(this).attr("data-id"))},"mouseleave.selectBoxIt":function(){"false"===a(this).attr("data-disabled")&&(d.listItems.not(a(this)).removeClass(g).removeAttr("data-active"),a(this).addClass(g),d.currentFocus=+a(this).attr("data-id"))},"blur.selectBoxIt":function(){a(this).removeClass(g)}},".selectboxit-option"),d.list.on({"click.selectBoxIt":function(a){a.preventDefault()}},"a"),d.selectBox.on({"change.selectBoxIt, internal-change.selectBoxIt":function(a,e){var f,g;e||(f=d.list.find('li[data-val="'+d.originalElem.value+'"]'),f.length&&(d.listItems.eq(d.currentFocus).removeClass(d.focusClass),d.currentFocus=+f.attr("data-id"))),f=d.listItems.eq(d.currentFocus),g=f.attr("data-selectedtext"),b=f.attr("data-text"),c=b?b:f.find("a").text(),d._setText(d.dropdownText,g||c),d.dropdownText.attr("data-val",d.originalElem.value),f.find("i").attr("class")&&(d.dropdownImage.attr("class",f.find("i").attr("class")).addClass("selectboxit-default-icon"),d.dropdownImage.attr("style",f.find("i").attr("style"))),d.triggerEvent("changed")},"disable.selectBoxIt":function(){d.dropdown.addClass(d.theme.disabled)},"enable.selectBoxIt":function(){d.dropdown.removeClass(d.theme.disabled)},"open.selectBoxIt":function(){var a,b=d.list.find("li[data-val='"+d.dropdownText.attr("data-val")+"']");b.length||(b=d.listItems.not("[data-disabled=true]").first()),d.currentFocus=+b.attr("data-id"),a=d.listItems.eq(d.currentFocus),d.dropdown.addClass(i).removeClass(h).addClass(g),d.listItems.removeClass(d.selectedClass).removeAttr("data-active").not(a).removeClass(g),a.addClass(d.selectedClass).addClass(g),d.options.hideCurrent&&(d.listItems.show(),a.hide())},"close.selectBoxIt":function(){d.dropdown.removeClass(i)},"blur.selectBoxIt":function(){d.dropdown.removeClass(g)},"mouseenter.selectBoxIt":function(){a(this).hasClass(d.theme.disabled)||d.dropdown.addClass(h)},"mouseleave.selectBoxIt":function(){d.dropdown.removeClass(h)},destroy:function(a){a.preventDefault(),a.stopPropagation()}}),d},_update:function(a){var b,c,d,e=this,f=e.options.defaultText||e.selectBox.attr("data-text"),g=e.listItems.eq(e.currentFocus);"false"===a.attr("data-disabled")&&(b=e.listItems.eq(e.currentFocus).attr("data-selectedtext"),c=g.attr("data-text"),d=c?c:g.text(),(f&&e.options.html?e.dropdownText.html()===f:e.dropdownText.text()===f)&&e.selectBox.val()===a.attr("data-val")?e.triggerEvent("change"):(e.selectBox.val(a.attr("data-val")),e.currentFocus=+a.attr("data-id"),e.originalElem.value!==e.dropdownText.attr("data-val")&&e.triggerEvent("change")))},_addClasses:function(a){var b=this,c=(b.focusClass=a.focus,b.hoverClass=a.hover,a.button),d=a.list,e=a.arrow,f=a.container;return b.openClass=a.open,b.selectedClass="selectboxit-selected",b.downArrow.addClass(b.selectBox.attr("data-downarrow")||b.options.downArrowIcon||e),b.dropdownContainer.addClass(f),b.dropdown.addClass(c),b.list.addClass(d),b},refresh:function(a,b){var c=this;return c._destroySelectBoxIt()._create(!0),b||c.triggerEvent("refresh"),c._callbackSupport(a),c},htmlEscape:function(a){return String(a).replace(/&/g,"&amp;").replace(/"/g,"&quot;").replace(/'/g,"&#39;").replace(/</g,"&lt;").replace(/>/g,"&gt;")},triggerEvent:function(a){var b=this,c=b.options.showFirstOption?b.currentFocus:b.currentFocus-1>=0?b.currentFocus:0;return b.selectBox.trigger(a,{selectbox:b.selectBox,selectboxOption:b.selectItems.eq(c),dropdown:b.dropdown,dropdownOption:b.listItems.eq(b.currentFocus)}),b},_copyAttributes:function(){var a=this;return a._addSelectBoxAttributes&&a._addSelectBoxAttributes(),a},_realOuterWidth:function(a){if(a.is(":visible"))return a.outerWidth(!0);var b,c=a.clone();return c.css({visibility:"hidden",display:"block",position:"absolute"}).appendTo("body"),b=c.outerWidth(!0),c.remove(),b}});var e=a.selectBox.selectBoxIt.prototype;e.add=function(b,c){this._populate(b,function(b){var d,e,f=this,g=a.type(b),h=0,i=[],j=f._isJSON(b),k=j&&f._parseJSON(b);if(b&&("array"===g||j&&k.data&&"array"===a.type(k.data))||"object"===g&&b.data&&"array"===a.type(b.data)){for(f._isJSON(b)&&(b=k),b.data&&(b=b.data),e=b.length;e-1>=h;h+=1)d=b[h],a.isPlainObject(d)?i.push(a("<option/>",d)):"string"===a.type(d)&&i.push(a("<option/>",{text:d,value:d}));f.selectBox.append(i)}else b&&"string"===g&&!f._isJSON(b)?f.selectBox.append(b):b&&"object"===g?f.selectBox.append(a("<option/>",b)):b&&f._isJSON(b)&&a.isPlainObject(f._parseJSON(b))&&f.selectBox.append(a("<option/>",f._parseJSON(b)));return f.dropdown?f.refresh(function(){f._callbackSupport(c)},!0):f._callbackSupport(c),f})},e._parseJSON=function(b){return JSON&&JSON.parse&&JSON.parse(b)||a.parseJSON(b)},e._isJSON=function(a){var b,c=this;try{return b=c._parseJSON(a),!0}catch(d){return!1}},e._populate=function(b,c){var d=this;return b=a.isFunction(b)?b.call():b,d.isDeferred(b)?b.done(function(a){c.call(d,a)}):c.call(d,b),d},e._ariaAccessibility=function(){var b=this,c=a("label[for='"+b.originalElem.id+"']");return b.dropdownContainer.attr({role:"combobox","aria-autocomplete":"list","aria-haspopup":"true","aria-expanded":"false","aria-owns":b.list[0].id}),b.dropdownText.attr({"aria-live":"polite"}),b.dropdown.on({"disable.selectBoxIt":function(){b.dropdownContainer.attr("aria-disabled","true")},"enable.selectBoxIt":function(){b.dropdownContainer.attr("aria-disabled","false")}}),c.length&&b.dropdownContainer.attr("aria-labelledby",c[0].id),b.list.attr({role:"listbox","aria-hidden":"true"}),b.listItems.attr({role:"option"}),b.selectBox.on({"open.selectBoxIt":function(){b.list.attr("aria-hidden","false"),b.dropdownContainer.attr("aria-expanded","true")},"close.selectBoxIt":function(){b.list.attr("aria-hidden","true"),b.dropdownContainer.attr("aria-expanded","false")}}),b},e._addSelectBoxAttributes=function(){var b=this;return b._addAttributes(b.selectBox.prop("attributes"),b.dropdown),b.selectItems.each(function(c){b._addAttributes(a(this).prop("attributes"),b.listItems.eq(c))}),b},e._addAttributes=function(b,c){var d=this,e=d.options.copyAttributes;return b.length&&a.each(b,function(b,d){var f=d.name.toLowerCase(),g=d.value;"null"===g||-1===a.inArray(f,e)&&-1===f.indexOf("data")||c.attr(f,g)}),d},e.destroy=function(a){var b=this;return b._destroySelectBoxIt(),b.widgetProto.destroy.call(b),b._callbackSupport(a),b},e._destroySelectBoxIt=function(){var b=this;return b.dropdown.off(".selectBoxIt"),a.contains(b.dropdownContainer[0],b.originalElem)&&b.dropdownContainer.before(b.selectBox),b.dropdownContainer.remove(),b.selectBox.removeAttr("style").attr("style",b.selectBoxStyles),b.triggerEvent("destroy"),b},e.disable=function(a){var b=this;return b.options.disabled||(b.close(),b.selectBox.attr("disabled","disabled"),b.dropdown.removeAttr("tabindex").removeClass(b.theme.enabled).addClass(b.theme.disabled),b.setOption("disabled",!0),b.triggerEvent("disable")),b._callbackSupport(a),b},e.disableOption=function(b,c){var d,e,f,g=this,h=a.type(b);return"number"===h&&(g.close(),d=g.selectBox.find("option").eq(b),g.triggerEvent("disable-option"),d.attr("disabled","disabled"),g.listItems.eq(b).attr("data-disabled","true").addClass(g.theme.disabled),g.currentFocus===b&&(e=g.listItems.eq(g.currentFocus).nextAll("li").not("[data-disabled='true']").first().length,f=g.listItems.eq(g.currentFocus).prevAll("li").not("[data-disabled='true']").first().length,e?g.moveDown():f?g.moveUp():g.disable())),g._callbackSupport(c),g},e._isDisabled=function(){var a=this;return a.originalElem.disabled&&a.disable(),a},e._dynamicPositioning=function(){var b=this;if("number"===a.type(b.listSize))b.list.css("max-height",b.maxHeight||"none");else{var c=b.dropdown.offset().top,d=b.list.data("max-height")||b.list.outerHeight(),e=b.dropdown.outerHeight(),f=b.options.viewport,g=f.height(),h=a.isWindow(f.get(0))?f.scrollTop():f.offset().top,i=g+h>=c+e+d,j=!i;if(b.list.data("max-height")||b.list.data("max-height",b.list.outerHeight()),j)if(b.dropdown.offset().top-h>=d)b.list.css("max-height",d),b.list.css("top",b.dropdown.position().top-b.list.outerHeight());else{var k=Math.abs(c+e+d-(g+h)),l=Math.abs(b.dropdown.offset().top-h-d);l>k?(b.list.css("max-height",d-k-e/2),b.list.css("top","auto")):(b.list.css("max-height",d-l-e/2),b.list.css("top",b.dropdown.position().top-b.list.outerHeight()))}else b.list.css("max-height",d),b.list.css("top","auto")}return b},e.enable=function(a){var b=this;return b.options.disabled&&(b.triggerEvent("enable"),b.selectBox.removeAttr("disabled"),b.dropdown.attr("tabindex",0).removeClass(b.theme.disabled).addClass(b.theme.enabled),b.setOption("disabled",!1),b._callbackSupport(a)),b},e.enableOption=function(b,c){var d,e=this,f=a.type(b);return"number"===f&&(d=e.selectBox.find("option").eq(b),e.triggerEvent("enable-option"),d.removeAttr("disabled"),e.listItems.eq(b).attr("data-disabled","false").removeClass(e.theme.disabled)),e._callbackSupport(c),e},e.moveDown=function(a){var b=this;b.currentFocus+=1;var c="true"===b.listItems.eq(b.currentFocus).attr("data-disabled")?!0:!1,d=b.listItems.eq(b.currentFocus).nextAll("li").not("[data-disabled='true']").first().length;if(b.currentFocus===b.listItems.length)b.currentFocus-=1;else{if(c&&d)return b.listItems.eq(b.currentFocus-1).blur(),b.moveDown(),void 0;c&&!d?b.currentFocus-=1:(b.listItems.eq(b.currentFocus-1).blur().end().eq(b.currentFocus).focusin(),b._scrollToView("down"),b.triggerEvent("moveDown"))}return b._callbackSupport(a),b},e.moveUp=function(a){var b=this;b.currentFocus-=1;var c="true"===b.listItems.eq(b.currentFocus).attr("data-disabled")?!0:!1,d=b.listItems.eq(b.currentFocus).prevAll("li").not("[data-disabled='true']").first().length;if(-1===b.currentFocus)b.currentFocus+=1;else{if(c&&d)return b.listItems.eq(b.currentFocus+1).blur(),b.moveUp(),void 0;c&&!d?b.currentFocus+=1:(b.listItems.eq(this.currentFocus+1).blur().end().eq(b.currentFocus).focusin(),b._scrollToView("up"),b.triggerEvent("moveUp"))}return b._callbackSupport(a),b},e._setCurrentSearchOption=function(a){var b=this;return(b.options.aggressiveChange||b.options.selectWhenHidden||b.listItems.eq(a).is(":visible"))&&b.listItems.eq(a).data("disabled")!==!0&&(b.listItems.eq(b.currentFocus).blur(),b.currentIndex=a,b.currentFocus=a,b.listItems.eq(b.currentFocus).focusin(),b._scrollToView("search"),b.triggerEvent("search")),b},e._searchAlgorithm=function(a,b){var c,d,e,f,g=this,h=!1,i=g.textArray,j=g.currentText;for(c=a,e=i.length;e>c;c+=1){for(f=i[c],d=0;e>d;d+=1)-1!==i[d].search(b)&&(h=!0,d=e);if(h||(g.currentText=g.currentText.charAt(g.currentText.length-1).replace(/[|()\[{.+*?$\\]/g,"\\$0"),j=g.currentText),b=new RegExp(j,"gi"),j.length<3){if(b=new RegExp(j.charAt(0),"gi"),-1!==f.charAt(0).search(b))return g._setCurrentSearchOption(c),(f.substring(0,j.length).toLowerCase()!==j.toLowerCase()||g.options.similarSearch)&&(g.currentIndex+=1),!1}else if(-1!==f.search(b))return g._setCurrentSearchOption(c),!1;if(f.toLowerCase()===g.currentText.toLowerCase())return g._setCurrentSearchOption(c),g.currentText="",!1}return!0},e.search=function(a,b,c){var d=this;c?d.currentText+=a.replace(/[|()\[{.+*?$\\]/g,"\\$0"):d.currentText=a.replace(/[|()\[{.+*?$\\]/g,"\\$0");var e=d._searchAlgorithm(d.currentIndex,new RegExp(d.currentText,"gi"));return e&&d._searchAlgorithm(0,d.currentText),d._callbackSupport(b),d},e._updateMobileText=function(){var a,b,c,d=this;a=d.selectBox.find("option").filter(":selected"),b=a.attr("data-text"),c=b?b:a.text(),d._setText(d.dropdownText,c),d.list.find('li[data-val="'+a.val()+'"]').find("i").attr("class")&&d.dropdownImage.attr("class",d.list.find('li[data-val="'+a.val()+'"]').find("i").attr("class")).addClass("selectboxit-default-icon")},e._applyNativeSelect=function(){var a=this;return a.dropdownContainer.append(a.selectBox),a.dropdown.attr("tabindex","-1"),a.selectBox.css({display:"block",visibility:"visible",width:a._realOuterWidth(a.dropdown),height:a.dropdown.outerHeight(),opacity:"0",position:"absolute",top:"0",left:"0",cursor:"pointer","z-index":"999999",margin:a.dropdown.css("margin"),padding:"0","-webkit-appearance":"menulist-button"}),a.originalElem.disabled&&a.triggerEvent("disable"),this},e._mobileEvents=function(){var a=this;a.selectBox.on({"changed.selectBoxIt":function(){a.hasChanged=!0,a._updateMobileText(),a.triggerEvent("option-click")},"mousedown.selectBoxIt":function(){a.hasChanged||!a.options.defaultText||a.originalElem.disabled||(a._updateMobileText(),a.triggerEvent("option-click"))},"enable.selectBoxIt":function(){a.selectBox.removeClass("selectboxit-rendering")},"disable.selectBoxIt":function(){a.selectBox.addClass("selectboxit-rendering")}})},e._mobile=function(){var a=this;return a.isMobile&&(a._applyNativeSelect(),a._mobileEvents()),this},e.remove=function(b,c){var d,e,f=this,g=a.type(b),h=0,i="";if("array"===g){for(e=b.length;e-1>=h;h+=1)d=b[h],"number"===a.type(d)&&(i+=i.length?", option:eq("+d+")":"option:eq("+d+")");f.selectBox.find(i).remove()}else"number"===g?f.selectBox.find("option").eq(b).remove():f.selectBox.find("option").remove();return f.dropdown?f.refresh(function(){f._callbackSupport(c)},!0):f._callbackSupport(c),f},e.selectOption=function(b,c){var d=this,e=a.type(b);return"number"===e?d.selectBox.val(d.selectItems.eq(b).val()).change():"string"===e&&d.selectBox.val(b).change(),d._callbackSupport(c),d},e.setOption=function(b,c,d){var e=this;return"string"===a.type(b)&&(e.options[b]=c),e.refresh(function(){e._callbackSupport(d)},!0),e},e.setOptions=function(b,c){var d=this;return a.isPlainObject(b)&&(d.options=a.extend({},d.options,b)),d.refresh(function(){d._callbackSupport(c)},!0),d},e.wait=function(a,b){var c=this;return c.widgetProto._delay.call(c,b,a),c}});;!function ($) {

  "use strict";


 /* MULTISELECT CLASS DEFINITION
  * ====================== */

  var MultiSelect = function (element, options) {
    this.options = options;
    this.$element = $(element);
    this.$container = $('<div/>', { 'class': "ms-container" });
    this.$selectableContainer = $('<div/>', { 'class': 'ms-selectable' });
    this.$selectionContainer = $('<div/>', { 'class': 'ms-selection' });
    this.$selectableUl = $('<ul/>', { 'class': "ms-list", 'tabindex' : '-1' });
    this.$selectionUl = $('<ul/>', { 'class': "ms-list", 'tabindex' : '-1' });
    this.scrollTo = 0;
    this.elemsSelector = 'li:visible:not(.ms-optgroup-label,.ms-optgroup-container,.'+options.disabledClass+')';
  };

  MultiSelect.prototype = {
    constructor: MultiSelect,

    init: function(){
      var that = this,
          ms = this.$element;

      if (ms.next('.ms-container').length === 0){
        ms.css({ position: 'absolute', left: '-9999px' });
        ms.attr('id', ms.attr('id') ? ms.attr('id') : Math.ceil(Math.random()*1000)+'multiselect');
        this.$container.attr('id', 'ms-'+ms.attr('id'));
        this.$container.addClass(that.options.cssClass);
        ms.find('option').each(function(){
          that.generateLisFromOption(this);
        });

        this.$selectionUl.find('.ms-optgroup-label').hide();

        if (that.options.selectableHeader){
          that.$selectableContainer.append(that.options.selectableHeader);
        }
        that.$selectableContainer.append(that.$selectableUl);
        if (that.options.selectableFooter){
          that.$selectableContainer.append(that.options.selectableFooter);
        }

        if (that.options.selectionHeader){
          that.$selectionContainer.append(that.options.selectionHeader);
        }
        that.$selectionContainer.append(that.$selectionUl);
        if (that.options.selectionFooter){
          that.$selectionContainer.append(that.options.selectionFooter);
        }

        that.$container.append(that.$selectableContainer);
        that.$container.append(that.$selectionContainer);
        ms.after(that.$container);

        that.activeMouse(that.$selectableUl);
        that.activeKeyboard(that.$selectableUl);

        var action = that.options.dblClick ? 'dblclick' : 'click';

        that.$selectableUl.on(action, '.ms-elem-selectable', function(){
          that.select($(this).data('ms-value'));
        });
        that.$selectionUl.on(action, '.ms-elem-selection', function(){
          that.deselect($(this).data('ms-value'));
        });

        that.activeMouse(that.$selectionUl);
        that.activeKeyboard(that.$selectionUl);

        ms.on('focus', function(){
          that.$selectableUl.focus();
        })
      }

      var selectedValues = ms.find('option:selected').map(function(){ return $(this).val(); }).get();
      that.select(selectedValues, 'init');

      if (typeof that.options.afterInit === 'function') {
        that.options.afterInit.call(this, this.$container);
      }
    },

    'generateLisFromOption' : function(option, index, $container){
      var that = this,
          ms = that.$element,
          attributes = "",
          $option = $(option);

      for (var cpt = 0; cpt < option.attributes.length; cpt++){
        var attr = option.attributes[cpt];

        if(attr.name !== 'value' && attr.name !== 'disabled'){
          attributes += attr.name+'="'+attr.value+'" ';
        }
      }
      var selectableLi = $('<li '+attributes+'><span>'+that.escapeHTML($option.text())+'</span></li>'),
          selectedLi = selectableLi.clone(),
          value = $option.val(),
          elementId = that.sanitize(value);

      selectableLi
        .data('ms-value', value)
        .addClass('ms-elem-selectable')
        .attr('id', elementId+'-selectable');

      selectedLi
        .data('ms-value', value)
        .addClass('ms-elem-selection')
        .attr('id', elementId+'-selection')
        .hide();

      if ($option.prop('disabled') || ms.prop('disabled')){
        selectedLi.addClass(that.options.disabledClass);
        selectableLi.addClass(that.options.disabledClass);
      }

      var $optgroup = $option.parent('optgroup');

      if ($optgroup.length > 0){
        var optgroupLabel = $optgroup.attr('label'),
            optgroupId = that.sanitize(optgroupLabel),
            $selectableOptgroup = that.$selectableUl.find('#optgroup-selectable-'+optgroupId),
            $selectionOptgroup = that.$selectionUl.find('#optgroup-selection-'+optgroupId);

        if ($selectableOptgroup.length === 0){
          var optgroupContainerTpl = '<li class="ms-optgroup-container"></li>',
              optgroupTpl = '<ul class="ms-optgroup"><li class="ms-optgroup-label"><span>'+optgroupLabel+'</span></li></ul>';

          $selectableOptgroup = $(optgroupContainerTpl);
          $selectionOptgroup = $(optgroupContainerTpl);
          $selectableOptgroup.attr('id', 'optgroup-selectable-'+optgroupId);
          $selectionOptgroup.attr('id', 'optgroup-selection-'+optgroupId);
          $selectableOptgroup.append($(optgroupTpl));
          $selectionOptgroup.append($(optgroupTpl));
          if (that.options.selectableOptgroup){
            $selectableOptgroup.find('.ms-optgroup-label').on('click', function(){
              var values = $optgroup.children(':not(:selected, :disabled)').map(function(){ return $(this).val() }).get();
              that.select(values);
            });
            $selectionOptgroup.find('.ms-optgroup-label').on('click', function(){
              var values = $optgroup.children(':selected:not(:disabled)').map(function(){ return $(this).val() }).get();
              that.deselect(values);
            });
          }
          that.$selectableUl.append($selectableOptgroup);
          that.$selectionUl.append($selectionOptgroup);
        }
        index = index == undefined ? $selectableOptgroup.find('ul').children().length : index + 1;
        selectableLi.insertAt(index, $selectableOptgroup.children());
        selectedLi.insertAt(index, $selectionOptgroup.children());
      } else {
        index = index == undefined ? that.$selectableUl.children().length : index;

        selectableLi.insertAt(index, that.$selectableUl);
        selectedLi.insertAt(index, that.$selectionUl);
      }
    },

    'addOption' : function(options){
      var that = this;

      if (options.value) options = [options];
      $.each(options, function(index, option){
        if (option.value && that.$element.find("option[value='"+option.value+"']").length === 0){
          var $option = $('<option value="'+option.value+'">'+option.text+'</option>'),
              index = parseInt((typeof option.index === 'undefined' ? that.$element.children().length : option.index)),
              $container = option.nested == undefined ? that.$element : $("optgroup[label='"+option.nested+"']")

          $option.insertAt(index, $container);
          that.generateLisFromOption($option.get(0), index, option.nested);
        }
      })
    },

    'escapeHTML' : function(text){
      return $("<div>").text(text).html();
    },

    'activeKeyboard' : function($list){
      var that = this;

      $list.on('focus', function(){
        $(this).addClass('ms-focus');
      })
      .on('blur', function(){
        $(this).removeClass('ms-focus');
      })
      .on('keydown', function(e){
        switch (e.which) {
          case 40:
          case 38:
            e.preventDefault();
            e.stopPropagation();
            that.moveHighlight($(this), (e.which === 38) ? -1 : 1);
            return;
          case 37:
          case 39:
            e.preventDefault();
            e.stopPropagation();
            that.switchList($list);
            return;
          case 9:
            if(that.$element.is('[tabindex]')){
              e.preventDefault();
              var tabindex = parseInt(that.$element.attr('tabindex'), 10);
              tabindex = (e.shiftKey) ? tabindex-1 : tabindex+1;
              $('[tabindex="'+(tabindex)+'"]').focus();
              return;
            }else{
              if(e.shiftKey){
                that.$element.trigger('focus');
              }
            }
        }
        if($.inArray(e.which, that.options.keySelect) > -1){
          e.preventDefault();
          e.stopPropagation();
          that.selectHighlighted($list);
          return;
        }
      });
    },

    'moveHighlight': function($list, direction){
      var $elems = $list.find(this.elemsSelector),
          $currElem = $elems.filter('.ms-hover'),
          $nextElem = null,
          elemHeight = $elems.first().outerHeight(),
          containerHeight = $list.height(),
          containerSelector = '#'+this.$container.prop('id');

      $elems.removeClass('ms-hover');
      if (direction === 1){ // DOWN

        $nextElem = $currElem.nextAll(this.elemsSelector).first();
        if ($nextElem.length === 0){
          var $optgroupUl = $currElem.parent();

          if ($optgroupUl.hasClass('ms-optgroup')){
            var $optgroupLi = $optgroupUl.parent(),
                $nextOptgroupLi = $optgroupLi.next(':visible');

            if ($nextOptgroupLi.length > 0){
              $nextElem = $nextOptgroupLi.find(this.elemsSelector).first();
            } else {
              $nextElem = $elems.first();
            }
          } else {
            $nextElem = $elems.first();
          }
        }
      } else if (direction === -1){ // UP

        $nextElem = $currElem.prevAll(this.elemsSelector).first();
        if ($nextElem.length === 0){
          var $optgroupUl = $currElem.parent();

          if ($optgroupUl.hasClass('ms-optgroup')){
            var $optgroupLi = $optgroupUl.parent(),
                $prevOptgroupLi = $optgroupLi.prev(':visible');

            if ($prevOptgroupLi.length > 0){
              $nextElem = $prevOptgroupLi.find(this.elemsSelector).last();
            } else {
              $nextElem = $elems.last();
            }
          } else {
            $nextElem = $elems.last();
          }
        }
      }
      if ($nextElem.length > 0){
        $nextElem.addClass('ms-hover');
        var scrollTo = $list.scrollTop() + $nextElem.position().top - 
                       containerHeight / 2 + elemHeight / 2;

        $list.scrollTop(scrollTo);
      }
    },

    'selectHighlighted' : function($list){
      var $elems = $list.find(this.elemsSelector),
          $highlightedElem = $elems.filter('.ms-hover').first();

      if ($highlightedElem.length > 0){
        if ($list.parent().hasClass('ms-selectable')){
          this.select($highlightedElem.data('ms-value'));
        } else {
          this.deselect($highlightedElem.data('ms-value'));
        }
        $elems.removeClass('ms-hover');
      }
    },

    'switchList' : function($list){
      $list.blur();
      this.$container.find(this.elemsSelector).removeClass('ms-hover');
      if ($list.parent().hasClass('ms-selectable')){
        this.$selectionUl.focus();
      } else {
        this.$selectableUl.focus();
      }
    },

    'activeMouse' : function($list){
      var that = this;

      $('body').on('mouseenter', that.elemsSelector, function(){
        $(this).parents('.ms-container').find(that.elemsSelector).removeClass('ms-hover');
        $(this).addClass('ms-hover');
      });

      $('body').on('mouseleave', that.elemsSelector, function () {
          $(this).parents('.ms-container').find(that.elemsSelector).removeClass('ms-hover');;
      });
    },

    'refresh' : function() {
      this.destroy();
      this.$element.multiSelect(this.options);
    },

    'destroy' : function(){
      $("#ms-"+this.$element.attr("id")).remove();
      this.$element.css('position', '').css('left', '')
      this.$element.removeData('multiselect');
    },

    'select' : function(value, method){
      if (typeof value === 'string'){ value = [value]; }

      var that = this,
          ms = this.$element,
          msIds = $.map(value, function(val){ return(that.sanitize(val)); }),
          selectables = this.$selectableUl.find('#' + msIds.join('-selectable, #')+'-selectable').filter(':not(.'+that.options.disabledClass+')'),
          selections = this.$selectionUl.find('#' + msIds.join('-selection, #') + '-selection').filter(':not(.'+that.options.disabledClass+')'),
          options = ms.find('option:not(:disabled)').filter(function(){ return($.inArray(this.value, value) > -1); });

      if (method === 'init'){
        selectables = this.$selectableUl.find('#' + msIds.join('-selectable, #')+'-selectable'),
        selections = this.$selectionUl.find('#' + msIds.join('-selection, #') + '-selection');
      }

      if (selectables.length > 0){
        selectables.addClass('ms-selected').hide();
        selections.addClass('ms-selected').show();

        options.prop('selected', true);

        that.$container.find(that.elemsSelector).removeClass('ms-hover');

        var selectableOptgroups = that.$selectableUl.children('.ms-optgroup-container');
        if (selectableOptgroups.length > 0){
          selectableOptgroups.each(function(){
            var selectablesLi = $(this).find('.ms-elem-selectable');
            if (selectablesLi.length === selectablesLi.filter('.ms-selected').length){
              $(this).find('.ms-optgroup-label').hide();
            }
          });

          var selectionOptgroups = that.$selectionUl.children('.ms-optgroup-container');
          selectionOptgroups.each(function(){
            var selectionsLi = $(this).find('.ms-elem-selection');
            if (selectionsLi.filter('.ms-selected').length > 0){
              $(this).find('.ms-optgroup-label').show();
            }
          });
        } else {
          if (that.options.keepOrder && method !== 'init'){
            var selectionLiLast = that.$selectionUl.find('.ms-selected');
            if((selectionLiLast.length > 1) && (selectionLiLast.last().get(0) != selections.get(0))) {
              selections.insertAfter(selectionLiLast.last());
            }
          }
        }
        if (method !== 'init'){
          ms.trigger('change');
          if (typeof that.options.afterSelect === 'function') {
            that.options.afterSelect.call(this, value);
          }
        }
      }
    },

    'deselect' : function(value){
      if (typeof value === 'string'){ value = [value]; }

      var that = this,
          ms = this.$element,
          msIds = $.map(value, function(val){ return(that.sanitize(val)); }),
          selectables = this.$selectableUl.find('#' + msIds.join('-selectable, #')+'-selectable'),
          selections = this.$selectionUl.find('#' + msIds.join('-selection, #')+'-selection').filter('.ms-selected').filter(':not(.'+that.options.disabledClass+')'),
          options = ms.find('option').filter(function(){ return($.inArray(this.value, value) > -1); });

      if (selections.length > 0){
        selectables.removeClass('ms-selected').show();
        selections.removeClass('ms-selected').hide();
        options.prop('selected', false);

        that.$container.find(that.elemsSelector).removeClass('ms-hover');

        var selectableOptgroups = that.$selectableUl.children('.ms-optgroup-container');
        if (selectableOptgroups.length > 0){
          selectableOptgroups.each(function(){
            var selectablesLi = $(this).find('.ms-elem-selectable');
            if (selectablesLi.filter(':not(.ms-selected)').length > 0){
              $(this).find('.ms-optgroup-label').show();
            }
          });

          var selectionOptgroups = that.$selectionUl.children('.ms-optgroup-container');
          selectionOptgroups.each(function(){
            var selectionsLi = $(this).find('.ms-elem-selection');
            if (selectionsLi.filter('.ms-selected').length === 0){
              $(this).find('.ms-optgroup-label').hide();
            }
          });
        }
        ms.trigger('change');
        if (typeof that.options.afterDeselect === 'function') {
          that.options.afterDeselect.call(this, value);
        }
      }
    },

    'select_all' : function(){
      var ms = this.$element,
          values = ms.val();

      ms.find('option:not(":disabled")').prop('selected', true);
      this.$selectableUl.find('.ms-elem-selectable').filter(':not(.'+this.options.disabledClass+')').addClass('ms-selected').hide();
      this.$selectionUl.find('.ms-optgroup-label').show();
      this.$selectableUl.find('.ms-optgroup-label').hide();
      this.$selectionUl.find('.ms-elem-selection').filter(':not(.'+this.options.disabledClass+')').addClass('ms-selected').show();
      this.$selectionUl.focus();
      ms.trigger('change');
      if (typeof this.options.afterSelect === 'function') {
        var selectedValues = $.grep(ms.val(), function(item){
          return $.inArray(item, values) < 0;
        });
        this.options.afterSelect.call(this, selectedValues);
      }
    },

    'deselect_all' : function(){
      var ms = this.$element,
          values = ms.val();

      ms.find('option').prop('selected', false);
      this.$selectableUl.find('.ms-elem-selectable').removeClass('ms-selected').show();
      this.$selectionUl.find('.ms-optgroup-label').hide();
      this.$selectableUl.find('.ms-optgroup-label').show();
      this.$selectionUl.find('.ms-elem-selection').removeClass('ms-selected').hide();
      this.$selectableUl.focus();
      ms.trigger('change');
      if (typeof this.options.afterDeselect === 'function') {
        this.options.afterDeselect.call(this, values);
      }
    },

    sanitize: function(value){
      var hash = 0, i, character;
      if (value.length == 0) return hash;
      var ls = 0;
      for (i = 0, ls = value.length; i < ls; i++) {
        character  = value.charCodeAt(i);
        hash  = ((hash<<5)-hash)+character;
        hash |= 0; // Convert to 32bit integer
      }
      return hash;
    }
  };

  /* MULTISELECT PLUGIN DEFINITION
   * ======================= */

  $.fn.multiSelect = function () {
    var option = arguments[0],
        args = arguments;

    return this.each(function () {
      var $this = $(this),
          data = $this.data('multiselect'),
          options = $.extend({}, $.fn.multiSelect.defaults, $this.data(), typeof option === 'object' && option);

      if (!data){ $this.data('multiselect', (data = new MultiSelect(this, options))); }

      if (typeof option === 'string'){
        data[option](args[1]);
      } else {
        data.init();
      }
    });
  };

  $.fn.multiSelect.defaults = {
    keySelect: [32],
    selectableOptgroup: false,
    disabledClass : 'disabled',
    dblClick : false,
    keepOrder: false,
    cssClass: ''
  };

  $.fn.multiSelect.Constructor = MultiSelect;

  $.fn.insertAt = function(index, $parent) {
    return this.each(function() {
      if (index === 0) {
        $parent.prepend(this);
      } else {
        $parent.children().eq(index - 1).after(this);
      }
    });
}

}(window.jQuery);
;(function ($) {

    var $window = $(window);

    function UTCDate() {
        return new Date(Date.UTC.apply(Date, arguments));
    }

    function UTCToday() {
        var today = new Date();
        return UTCDate(today.getUTCFullYear(), today.getUTCMonth(), today.getUTCDate());
    }

//时分秒变量
    var hourTemplate = '<td colspan="2"><select>' +
        '<option value ="00">00</option>' +
        '<option value ="01">01</option>' +
        '<option value ="02">02</option>' +
        '<option value ="03">03</option>' +
        '<option value ="04">04</option>' +
        '<option value ="05">05</option>' +
        '<option value ="06">06</option>' +
        '<option value ="07">07</option>' +
        '<option value ="08">08</option>' +
        '<option value ="09">09</option>' +
        '<option value ="10">10</option>' +
        '<option value ="11">11</option>' +
        '<option value ="12">12</option>' +
        '<option value ="13">13</option>' +
        '<option value ="14">14</option>' +
        '<option value ="15">15</option>' +
        '<option value ="16">16</option>' +
        '<option value ="17">17</option>' +
        '<option value ="18">18</option>' +
        '<option value ="19">19</option>' +
        '<option value ="20">20</option>' +
        '<option value ="21">21</option>' +
        '<option value ="22">22</option>' +
        '<option value ="23">23</option>' +
        '</select></td>'
    var minTemplate = '<td colspan="2"><select>' +
        '<option value ="00">00</option>' +
        '<option value ="05">05</option>' +
        '<option value ="10">10</option>' +
        '<option value ="15">15</option>' +
        '<option value ="20">20</option>' +
        '<option value ="25">25</option>' +
        '<option value ="30">30</option>' +
        '<option value ="35">35</option>' +
        '<option value ="40">40</option>' +
        '<option value ="45">45</option>' +
        '<option value ="50">50</option>' +
        '<option value ="55">55</option>' +
        '</select></td>'
    var secTemplate = '<td colspan="2"><select>' +
        '<option value ="00">00</option>' +
        '<option value ="05">05</option>' +
        '<option value ="10">10</option>' +
        '<option value ="15">15</option>' +
        '<option value ="20">20</option>' +
        '<option value ="25">25</option>' +
        '<option value ="30">30</option>' +
        '<option value ="35">35</option>' +
        '<option value ="40">40</option>' +
        '<option value ="45">45</option>' +
        '<option value ="50">50</option>' +
        '<option value ="55">55</option>' +
        '</select></td>'

    // Picker object

    var Datepicker = function (element, options) {
        var that = this;
        this._process_options(options);

        this.element = $(element);
        this.isInline = false;
        this.isInput = this.element.is('input');
        this.component = this.element.is('.date') ? this.element.find('.add-on, .btn') : false;
        this.hasInput = this.component && this.element.find('input').length;
        if (this.component && this.component.length === 0)
            this.component = false;
        //根据time属性确定是否需要时分秒
        var pickerTemplate = options.time ? DPGlobal.timeTemplate : DPGlobal.template;
        var buttonTemplate = '<div class="buttonTemplate" style="display: inline-block">' +
            '<button class="buttonTemplateSubmit btn btn-sm btn-success" type="button" style="display: inline-block; margin-left: 50px; padding-top: 0px; padding-bottom: 0px; border-radius: 0px; height: 24px;">确定</button>'+
            '<button class="buttonTemplateClean btn btn-sm btn-danger" type="button" style="display: inline-block; margin-left: 5px; padding-top: 0px; padding-bottom: 0px; border-radius: 0px; height: 24px;">清除</button></div>';
        this.picker = $(pickerTemplate);
        this.picker.append(buttonTemplate);
        this._buildEvents();
        this._attachEvents();
        if (this.isInline) {
            this.picker.addClass('datepicker-inline').appendTo(this.element);
        } else {
            this.picker.addClass('datepicker-dropdown dropdown-menu');
        }

        if (this.o.rtl) {
            this.picker.addClass('datepicker-rtl');
            this.picker.find('.prev i, .next i')
                .toggleClass('icon-arrow-left icon-arrow-right');
        }


        this.viewMode = this.o.startView;

        if (this.o.calendarWeeks)
            this.picker.find('tfoot th.today')
                .attr('colspan', function (i, val) {
                    return parseInt(val) + 1;
                });

        this._allow_update = false;

        this.setStartDate(this._o.startDate);
        this.setEndDate(this._o.endDate);
        this.setDaysOfWeekDisabled(this.o.daysOfWeekDisabled);

        this.fillDow();
        this.fillMonths();

        this._allow_update = true;

        this.update();
        this.showMode();

        if (this.isInline) {
            this.show();
        }
    };

    Datepicker.prototype = {
        constructor: Datepicker,

        _process_options: function (opts) {
            // Store raw options for reference
            this._o = $.extend({}, this._o, opts);
            // Processed options
            var o = this.o = $.extend({}, this._o);

            // Check if "de-DE" style date is available, if not language should
            // fallback to 2 letter code eg "de"
            var lang = o.language;
            if (!dates[lang]) {
                lang = lang.split('-')[0];
                if (!dates[lang])
                    lang = defaults.language;
            }
            o.language = lang;

            switch (o.startView) {
                case 2:
                case 'decade':
                    o.startView = 2;
                    break;
                case 1:
                case 'year':
                    o.startView = 1;
                    break;
                default:
                    o.startView = 0;
            }

            switch (o.minViewMode) {
                case 1:
                case 'months':
                    o.minViewMode = 1;
                    break;
                case 2:
                case 'years':
                    o.minViewMode = 2;
                    break;
                default:
                    o.minViewMode = 0;
            }

            o.startView = Math.max(o.startView, o.minViewMode);

            o.weekStart %= 7;
            o.weekEnd = ((o.weekStart + 6) % 7);

            var format = DPGlobal.parseFormat(o.format);
            if (o.startDate !== -Infinity) {
                if (!!o.startDate) {
                    if (o.startDate instanceof Date)
                        o.startDate = this._local_to_utc(this._zero_time(o.startDate));
                    else
                        o.startDate = DPGlobal.parseDate(o.startDate, format, o.language);
                } else {
                    o.startDate = -Infinity;
                }
            }
            if (o.endDate !== Infinity) {
                if (!!o.endDate) {
                    if (o.endDate instanceof Date)
                        o.endDate = this._local_to_utc(this._zero_time(o.endDate));
                    else
                        o.endDate = DPGlobal.parseDate(o.endDate, format, o.language);
                } else {
                    o.endDate = Infinity;
                }
            }

            o.daysOfWeekDisabled = o.daysOfWeekDisabled || [];
            if (!$.isArray(o.daysOfWeekDisabled))
                o.daysOfWeekDisabled = o.daysOfWeekDisabled.split(/[,\s]*/);
            o.daysOfWeekDisabled = $.map(o.daysOfWeekDisabled, function (d) {
                return parseInt(d, 10);
            });

            var plc = String(o.orientation).toLowerCase().split(/\s+/g),
                _plc = o.orientation.toLowerCase();
            plc = $.grep(plc, function (word) {
                return (/^auto|left|right|top|bottom$/).test(word);
            });
            o.orientation = {x: 'auto', y: 'auto'};
            if (!_plc || _plc === 'auto')
                ; // no action
            else if (plc.length === 1) {
                switch (plc[0]) {
                    case 'top':
                    case 'bottom':
                        o.orientation.y = plc[0];
                        break;
                    case 'left':
                    case 'right':
                        o.orientation.x = plc[0];
                        break;
                }
            }
            else {
                _plc = $.grep(plc, function (word) {
                    return (/^left|right$/).test(word);
                });
                o.orientation.x = _plc[0] || 'auto';

                _plc = $.grep(plc, function (word) {
                    return (/^top|bottom$/).test(word);
                });
                o.orientation.y = _plc[0] || 'auto';
            }
        },
        _events: [],
        _secondaryEvents: [],
        _applyEvents: function (evs) {
            for (var i = 0, el, ev; i < evs.length; i++) {
                el = evs[i][0];
                ev = evs[i][1];
                el.on(ev);
            }
        },
        _unapplyEvents: function (evs) {
            for (var i = 0, el, ev; i < evs.length; i++) {
                el = evs[i][0];
                ev = evs[i][1];
                el.off(ev);
            }
        },
        _buildEvents: function () {
            if (this.isInput) { // single input
                this._events = [
                    [this.element, {
                        focus: $.proxy(this.show, this),
                        keyup: $.proxy(this.update, this),
                        keydown: $.proxy(this.keydown, this)
                    }]
                ];
            }
            else if (this.component && this.hasInput) { // component: input + button
                this._events = [
                    // For components that are not readonly, allow keyboard nav
                    [this.element.find('input'), {
                        focus: $.proxy(this.show, this),
                        keyup: $.proxy(this.update, this),
                        keydown: $.proxy(this.keydown, this)
                    }],
                    [this.component, {
                        click: $.proxy(this.show, this)
                    }]
                ];
            }
            else if (this.element.is('div')) {  // inline datepicker
                this.isInline = true;
            }
            else {
                this._events = [
                    [this.element, {
                        click: $.proxy(this.show, this)
                    }]
                ];
            }

            this._secondaryEvents = [
                [this.picker, {
                    click: $.proxy(this.click, this)
                }],
                [$(window), {
                    resize: $.proxy(this.place, this)
                }],
                [$(document), {
                    mousedown: $.proxy(function (e) {
                        // Clicked outside the datepicker, hide it
                        if (!(
                            this.element.is(e.target) ||
                            this.element.find(e.target).length ||
                            this.picker.is(e.target) ||
                            this.picker.find(e.target).length
                            )) {
                            this.hide();
                        }
                    }, this)
                }]
            ];
        },
        _attachEvents: function () {
            this._detachEvents();
            this._applyEvents(this._events);
        },
        _detachEvents: function () {
            this._unapplyEvents(this._events);
        },
        _attachSecondaryEvents: function () {
            this._detachSecondaryEvents();
            this._applyEvents(this._secondaryEvents);
        },
        _detachSecondaryEvents: function () {
            this._unapplyEvents(this._secondaryEvents);
        },
        _trigger: function (event, altdate) {
            var date = altdate || this.date,
                local_date = this._utc_to_local(date);

            this.element.trigger({
                type: event,
                date: local_date,
                format: $.proxy(function (altformat) {
                    var format = altformat || this.o.format;
                    return DPGlobal.formatDate(date, format, this.o.language);
                }, this)
            });
        },

        show: function (e) {
            if (!this.isInline)
                this.picker.appendTo('body');
            this.picker.show();
            var me =this;
            //注册确定按钮
            //绑定确定按钮事件
            $('.buttonTemplateSubmit').on('click',function(){
                $(document).trigger('mousedown');
            });
            //注册清除按钮
            $('.buttonTemplateClean').on('click',function(){
                me.hide();
                me.element.val("");
            });
            this.height = this.component ? this.component.outerHeight() : this.element.outerHeight();
            this.place();
            this._attachSecondaryEvents();
            if (e) {
                e.preventDefault();
            }
            this._trigger('show');
        },

        hide: function (e) {
            if (this.isInline) return;
            if (!this.picker.is(':visible')) return;
            this.setValue();
            this.picker.hide().detach();
            this._detachSecondaryEvents();
            this.viewMode = this.o.startView;
            this.showMode();
            if (
                this.o.forceParse &&
                (
                this.isInput && this.element.val() ||
                this.hasInput && this.element.find('input').val()
                )
            )
                this._trigger('hide');
        },

        remove: function () {
            this.hide();
            this._detachEvents();
            this._detachSecondaryEvents();
            this.picker.remove();
            delete this.element.data().datepicker;
            if (!this.isInput) {
                delete this.element.data().date;
            }
        },

        _utc_to_local: function (utc) {
            return new Date(utc.getTime() + (utc.getTimezoneOffset() * 60000));
        },
        _local_to_utc: function (local) {
            return new Date(local.getTime() - (local.getTimezoneOffset() * 60000));
        },
        _zero_time: function (local) {
            return new Date(local.getFullYear(), local.getMonth(), local.getDate());
        },
        _zero_utc_time: function (utc) {
            return new Date(Date.UTC(utc.getUTCFullYear(), utc.getUTCMonth(), utc.getUTCDate()));
        },

        getDate: function () {
            return this._utc_to_local(this.getUTCDate());
        },

        getUTCDate: function () {
            return this.date;
        },

        setDate: function (d) {
            this.setUTCDate(this._local_to_utc(d));
        },

        setUTCDate: function (d) {
            this.date = d;
            this.setValue();
        },

        setValue: function () {
            var timeString = $(this)[0].element.attr("datetype") == "datetime" ? " " + $('.datepicker select')[0].value + ":" + $('.datepicker select')[1].value : "";
            var formatted = this.getFormattedDate() + timeString;
            this.element.trigger('focus');
            if (!this.isInput) {
                if (this.component) {
                    this.element.find('input').val(formatted).change();
                }
            } else {
                this.element.val(formatted).change();
            }
        },

        getFormattedDate: function (format) {
            if (format === undefined)
                format = this.o.format;
            return DPGlobal.formatDate(this.date, format, this.o.language);
        },

        setStartDate: function (startDate) {
            this._process_options({startDate: startDate});
            this.update();
            this.updateNavArrows();
        },

        setEndDate: function (endDate) {
            this._process_options({endDate: endDate});
            this.update();
            this.updateNavArrows();
        },

        setDaysOfWeekDisabled: function (daysOfWeekDisabled) {
            this._process_options({daysOfWeekDisabled: daysOfWeekDisabled});
            this.update();
            this.updateNavArrows();
        },

        place: function () {
            if (this.isInline) return;
            var calendarWidth = this.picker.outerWidth(),
                calendarHeight = this.picker.outerHeight(),
                visualPadding = 10,
                windowWidth = $window.width(),
                windowHeight = $window.height(),
                scrollTop = $window.scrollTop();

            var zIndex = parseInt(this.element.parents().filter(function () {
                    return $(this).css('z-index') != 'auto';
                }).first().css('z-index')) + 10;
            var offset = this.component ? this.component.parent().offset() : this.element.offset();
            var height = this.component ? this.component.outerHeight(true) : this.element.outerHeight(false);
            var width = this.component ? this.component.outerWidth(true) : this.element.outerWidth(false);
            var left = offset.left,
                top = offset.top;

            this.picker.removeClass(
                'datepicker-orient-top datepicker-orient-bottom ' +
                'datepicker-orient-right datepicker-orient-left'
            );

            if (this.o.orientation.x !== 'auto') {
                this.picker.addClass('datepicker-orient-' + this.o.orientation.x);
                if (this.o.orientation.x === 'right')
                    left -= calendarWidth - width;
            }
            // auto x orientation is best-placement: if it crosses a window
            // edge, fudge it sideways
            else {
                // Default to left
                this.picker.addClass('datepicker-orient-left');
                if (offset.left < 0)
                    left -= offset.left - visualPadding;
                else if (offset.left + calendarWidth > windowWidth)
                    left = windowWidth - calendarWidth - visualPadding;
            }

            // auto y orientation is best-situation: top or bottom, no fudging,
            // decision based on which shows more of the calendar
            var yorient = this.o.orientation.y,
                top_overflow, bottom_overflow;
            if (yorient === 'auto') {
                top_overflow = -scrollTop + offset.top - calendarHeight;
                bottom_overflow = scrollTop + windowHeight - (offset.top + height + calendarHeight);
                if (Math.max(top_overflow, bottom_overflow) === bottom_overflow)
                    yorient = 'top';
                else
                    yorient = 'bottom';
            }
            this.picker.addClass('datepicker-orient-' + yorient);
            if (yorient === 'top')
                top += height;
            else
                top -= calendarHeight + parseInt(this.picker.css('padding-top'));

            this.picker.css({
                top: top,
                left: left,
                zIndex: zIndex
            });
        },

        _allow_update: true,
        update: function () {
            if (!this._allow_update) return;

            var oldDate = new Date(this.date),
                date, fromArgs = false;
            if (arguments && arguments.length && (typeof arguments[0] === 'string' || arguments[0] instanceof Date)) {
                date = arguments[0];
                if (date instanceof Date)
                    date = this._local_to_utc(date);
                fromArgs = true;
            } else {
                date = this.isInput ? this.element.val() : this.element.data('date') || this.element.find('input').val();
                delete this.element.data().date;
            }

            this.date = DPGlobal.parseDate(date, this.o.format, this.o.language);

            if (fromArgs) {
                // setting date by clicking
                this.setValue();
            } else if (date) {
                // setting date by typing
                if (oldDate.getTime() !== this.date.getTime())
                    this._trigger('changeDate');
            } else {
                // clearing date
                this._trigger('clearDate');
            }

            if (this.date < this.o.startDate) {
                this.viewDate = new Date(this.o.startDate);
                this.date = new Date(this.o.startDate);
            } else if (this.date > this.o.endDate) {
                this.viewDate = new Date(this.o.endDate);
                this.date = new Date(this.o.endDate);
            } else {
                this.viewDate = new Date(this.date);
                this.date = new Date(this.date);
            }
            this.fill();
        },

        fillDow: function () {
            var dowCnt = this.o.weekStart,
                html = '<tr>';
            if (this.o.calendarWeeks) {
                var cell = '<th class="cw">&nbsp;</th>';
                html += cell;
                this.picker.find('.datepicker-days thead tr:first-child').prepend(cell);
            }
            while (dowCnt < this.o.weekStart + 7) {
                html += '<th class="dow">' + dates[this.o.language].daysMin[(dowCnt++) % 7] + '</th>';
            }
            html += '</tr>';
            this.picker.find('.datepicker-days thead').append(html);
        },

        fillMonths: function () {
            var html = '',
                i = 0;
            while (i < 12) {
                html += '<span class="month">' + dates[this.o.language].monthsShort[i++] + '</span>';
            }
            this.picker.find('.datepicker-months td').html(html);
        },

        setRange: function (range) {
            if (!range || !range.length)
                delete this.range;
            else
                this.range = $.map(range, function (d) {
                    return d.valueOf();
                });
            this.fill();
        },

        getClassNames: function (date) {
            var cls = [],
                year = this.viewDate.getUTCFullYear(),
                month = this.viewDate.getUTCMonth(),
                currentDate = this.date.valueOf(),
                today = new Date();
            if (date.getUTCFullYear() < year || (date.getUTCFullYear() == year && date.getUTCMonth() < month)) {
                cls.push('old');
            } else if (date.getUTCFullYear() > year || (date.getUTCFullYear() == year && date.getUTCMonth() > month)) {
                cls.push('new');
            }
            // Compare internal UTC date with local today, not UTC today
            if (this.o.todayHighlight &&
                date.getUTCFullYear() == today.getFullYear() &&
                date.getUTCMonth() == today.getMonth() &&
                date.getUTCDate() == today.getDate()) {
                cls.push('today');
            }
            if (currentDate && date.valueOf() == currentDate) {
                cls.push('active');
            }
            if (date.valueOf() < this.o.startDate || date.valueOf() > this.o.endDate ||
                $.inArray(date.getUTCDay(), this.o.daysOfWeekDisabled) !== -1) {
                cls.push('disabled');
            }
            if (this.range) {
                if (date > this.range[0] && date < this.range[this.range.length - 1]) {
                    cls.push('range');
                }
                if ($.inArray(date.valueOf(), this.range) != -1) {
                    cls.push('selected');
                }
            }
            return cls;
        },

        fill: function () {
            var d = new Date(this.viewDate),
                year = d.getUTCFullYear(),
                month = d.getUTCMonth(),
                startYear = this.o.startDate !== -Infinity ? this.o.startDate.getUTCFullYear() : -Infinity,
                startMonth = this.o.startDate !== -Infinity ? this.o.startDate.getUTCMonth() : -Infinity,
                endYear = this.o.endDate !== Infinity ? this.o.endDate.getUTCFullYear() : Infinity,
                endMonth = this.o.endDate !== Infinity ? this.o.endDate.getUTCMonth() : Infinity,
                currentDate = this.date && this.date.valueOf(),
                tooltip;
            this.picker.find('.datepicker-days thead th.datepicker-switch')
                .text(dates[this.o.language].months[month] + ' ' + year);
            this.picker.find('tfoot th.today')
                .text(dates[this.o.language].today)
                .toggle(this.o.todayBtn !== false);
            this.picker.find('tfoot th.clear')
                .text(dates[this.o.language].clear)
                .toggle(this.o.clearBtn !== false);
            this.updateNavArrows();
            this.fillMonths();
            var prevMonth = UTCDate(year, month - 1, 28, 0, 0, 0, 0),
                day = DPGlobal.getDaysInMonth(prevMonth.getUTCFullYear(), prevMonth.getUTCMonth());
            prevMonth.setUTCDate(day);
            prevMonth.setUTCDate(day - (prevMonth.getUTCDay() - this.o.weekStart + 7) % 7);
            var nextMonth = new Date(prevMonth);
            nextMonth.setUTCDate(nextMonth.getUTCDate() + 42);
            nextMonth = nextMonth.valueOf();
            var html = [];
            var clsName;
            while (prevMonth.valueOf() < nextMonth) {
                if (prevMonth.getUTCDay() == this.o.weekStart) {
                    html.push('<tr>');
                    if (this.o.calendarWeeks) {
                        // ISO 8601: First week contains first thursday.
                        // ISO also states week starts on Monday, but we can be more abstract here.
                        var
                        // Start of current week: based on weekstart/current date
                            ws = new Date(+prevMonth + (this.o.weekStart - prevMonth.getUTCDay() - 7) % 7 * 864e5),
                        // Thursday of this week
                            th = new Date(+ws + (7 + 4 - ws.getUTCDay()) % 7 * 864e5),
                        // First Thursday of year, year from thursday
                            yth = new Date(+(yth = UTCDate(th.getUTCFullYear(), 0, 1)) + (7 + 4 - yth.getUTCDay()) % 7 * 864e5),
                        // Calendar week: ms between thursdays, div ms per day, div 7 days
                            calWeek = (th - yth) / 864e5 / 7 + 1;
                        html.push('<td class="cw">' + calWeek + '</td>');

                    }
                }
                clsName = this.getClassNames(prevMonth);
                clsName.push('day');

                if (this.o.beforeShowDay !== $.noop) {
                    var before = this.o.beforeShowDay(this._utc_to_local(prevMonth));
                    if (before === undefined)
                        before = {};
                    else if (typeof(before) === 'boolean')
                        before = {enabled: before};
                    else if (typeof(before) === 'string')
                        before = {classes: before};
                    if (before.enabled === false)
                        clsName.push('disabled');
                    if (before.classes)
                        clsName = clsName.concat(before.classes.split(/\s+/));
                    if (before.tooltip)
                        tooltip = before.tooltip;
                }

                clsName = $.unique(clsName);
                html.push('<td class="' + clsName.join(' ') + '"' + (tooltip ? ' title="' + tooltip + '"' : '') + '>' + prevMonth.getUTCDate() + '</td>');
                if (prevMonth.getUTCDay() == this.o.weekEnd) {
                    html.push('</tr>');
                }
                prevMonth.setUTCDate(prevMonth.getUTCDate() + 1);
            }
            this.picker.find('.datepicker-days tbody').empty().append(html.join(''));
            var currentYear = this.date && this.date.getUTCFullYear();

            var months = this.picker.find('.datepicker-months')
                .find('th:eq(1)')
                .text(year)
                .end()
                .find('span').removeClass('active');
            if (currentYear && currentYear == year) {
                months.eq(this.date.getUTCMonth()).addClass('active');
            }
            if (year < startYear || year > endYear) {
                months.addClass('disabled');
            }
            if (year == startYear) {
                months.slice(0, startMonth).addClass('disabled');
            }
            if (year == endYear) {
                months.slice(endMonth + 1).addClass('disabled');
            }

            html = '';
            year = parseInt(year / 10, 10) * 10;
            var yearCont = this.picker.find('.datepicker-years')
                .find('th:eq(1)')
                .text(year + '-' + (year + 9))
                .end()
                .find('td');
            year -= 1;
            for (var i = -1; i < 11; i++) {
                html += '<span class="year' + (i == -1 ? ' old' : i == 10 ? ' new' : '') + (currentYear == year ? ' active' : '') + (year < startYear || year > endYear ? ' disabled' : '') + '">' + year + '</span>';
                year += 1;
            }
            yearCont.html(html);
        },

        updateNavArrows: function () {
            if (!this._allow_update) return;

            var d = new Date(this.viewDate),
                year = d.getUTCFullYear(),
                month = d.getUTCMonth();
            switch (this.viewMode) {
                case 0:
                    if (this.o.startDate !== -Infinity && year <= this.o.startDate.getUTCFullYear() && month <= this.o.startDate.getUTCMonth()) {
                        this.picker.find('.prev').css({visibility: 'hidden'});
                    } else {
                        this.picker.find('.prev').css({visibility: 'visible'});
                    }
                    if (this.o.endDate !== Infinity && year >= this.o.endDate.getUTCFullYear() && month >= this.o.endDate.getUTCMonth()) {
                        this.picker.find('.next').css({visibility: 'hidden'});
                    } else {
                        this.picker.find('.next').css({visibility: 'visible'});
                    }
                    this.picker.find('.buttonTemplate').css({display: 'inline-block'});
                    break;
                case 1:
                case 2:
                    if (this.o.startDate !== -Infinity && year <= this.o.startDate.getUTCFullYear()) {
                        this.picker.find('.prev').css({visibility: 'hidden'});
                    } else {
                        this.picker.find('.prev').css({visibility: 'visible'});
                    }
                    if (this.o.endDate !== Infinity && year >= this.o.endDate.getUTCFullYear()) {
                        this.picker.find('.next').css({visibility: 'hidden'});
                    } else {
                        this.picker.find('.next').css({visibility: 'visible'});
                    }
                    break;
            }
        },

        click: function (e) {
            e.preventDefault();
            var target = $(e.target).closest('span, td, th');
            if (target.length == 1) {
                switch (target[0].nodeName.toLowerCase()) {
                    case 'th':
                        switch (target[0].className) {
                            case 'datepicker-switch':
                                this.showMode(1);
                                break;
                            case 'prev':
                            case 'next':
                                var dir = DPGlobal.modes[this.viewMode].navStep * (target[0].className == 'prev' ? -1 : 1);
                                switch (this.viewMode) {
                                    case 0:
                                        this.viewDate = this.moveMonth(this.viewDate, dir);
                                        this._trigger('changeMonth', this.viewDate);
                                        break;
                                    case 1:
                                    case 2:
                                        this.viewDate = this.moveYear(this.viewDate, dir);
                                        if (this.viewMode === 1)
                                            this._trigger('changeYear', this.viewDate);
                                        break;
                                }
                                this.fill();
                                break;
                            case 'today':
                                var date = new Date();
                                date = UTCDate(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0);

                                this.showMode(-2);
                                var which = this.o.todayBtn == 'linked' ? null : 'view';
                                this._setDate(date, which);
                                break;
                            case 'clear':
                                var element;
                                if (this.isInput)
                                    element = this.element;
                                else if (this.component)
                                    element = this.element.find('input');
                                if (element)
                                    element.val("").change();
                                this._trigger('changeDate');
                                this.update();
                                if (this.o.autoclose)
                                    this.hide();
                                break;
                        }
                        break;
                    case 'span':
                        if (!target.is('.disabled')) {
                            this.viewDate.setUTCDate(1);
                            if (target.is('.month')) {
                                var day = 1;
                                var month = target.parent().find('span').index(target);
                                var year = this.viewDate.getUTCFullYear();
                                this.viewDate.setUTCMonth(month);
                                this._trigger('changeMonth', this.viewDate);
                                if (this.o.minViewMode === 1) {
                                    this._setDate(UTCDate(year, month, day, 0, 0, 0, 0));
                                }
                            } else {
                                var year = parseInt(target.text(), 10) || 0;
                                var day = 1;
                                var month = 0;
                                this.viewDate.setUTCFullYear(year);
                                this._trigger('changeYear', this.viewDate);
                                if (this.o.minViewMode === 2) {
                                    this._setDate(UTCDate(year, month, day, 0, 0, 0, 0));
                                }
                            }
                            this.showMode(-1);
                            this.fill();
                        }
                        break;
                    case 'td':
                        if (target.is('.day') && !target.is('.disabled')) {
                            var day = parseInt(target.text(), 10) || 1;
                            var year = this.viewDate.getUTCFullYear(),
                                month = this.viewDate.getUTCMonth();
                            if (target.is('.old')) {
                                if (month === 0) {
                                    month = 11;
                                    year -= 1;
                                } else {
                                    month -= 1;
                                }
                            } else if (target.is('.new')) {
                                if (month == 11) {
                                    month = 0;
                                    year += 1;
                                } else {
                                    month += 1;
                                }
                            }
                            this._setDate(UTCDate(year, month, day, 0, 0, 0, 0));
                        }
                        break;
                }
            }
        },

        _setDate: function (date, which) {
            if (!which || which == 'date')
                this.date = new Date(date);
            if (!which || which == 'view')
                this.viewDate = new Date(date);
            this.fill();
            this.setValue();
            this._trigger('changeDate');
            var element;
            if (this.isInput) {
                element = this.element;
            } else if (this.component) {
                element = this.element.find('input');
            }
            if (element) {
                element.change();
            }
            if (this.o.autoclose && (!which || which == 'date')) {
                this.hide();
            }
        },

        moveMonth: function (date, dir) {
            if (!dir) return date;
            var new_date = new Date(date.valueOf()),
                day = new_date.getUTCDate(),
                month = new_date.getUTCMonth(),
                mag = Math.abs(dir),
                new_month, test;
            dir = dir > 0 ? 1 : -1;
            if (mag == 1) {
                test = dir == -1
                    // If going back one month, make sure month is not current month
                    // (eg, Mar 31 -> Feb 31 == Feb 28, not Mar 02)
                    ? function () {
                    return new_date.getUTCMonth() == month;
                }
                    // If going forward one month, make sure month is as expected
                    // (eg, Jan 31 -> Feb 31 == Feb 28, not Mar 02)
                    : function () {
                    return new_date.getUTCMonth() != new_month;
                };
                new_month = month + dir;
                new_date.setUTCMonth(new_month);
                // Dec -> Jan (12) or Jan -> Dec (-1) -- limit expected date to 0-11
                if (new_month < 0 || new_month > 11)
                    new_month = (new_month + 12) % 12;
            } else {
                // For magnitudes >1, move one month at a time...
                for (var i = 0; i < mag; i++)
                    // ...which might decrease the day (eg, Jan 31 to Feb 28, etc)...
                    new_date = this.moveMonth(new_date, dir);
                // ...then reset the day, keeping it in the new month
                new_month = new_date.getUTCMonth();
                new_date.setUTCDate(day);
                test = function () {
                    return new_month != new_date.getUTCMonth();
                };
            }
            // Common date-resetting loop -- if date is beyond end of month, make it
            // end of month
            while (test()) {
                new_date.setUTCDate(--day);
                new_date.setUTCMonth(new_month);
            }
            return new_date;
        },

        moveYear: function (date, dir) {
            return this.moveMonth(date, dir * 12);
        },

        dateWithinRange: function (date) {
            return date >= this.o.startDate && date <= this.o.endDate;
        },

        keydown: function (e) {
            if (this.picker.is(':not(:visible)')) {
                if (e.keyCode == 27) // allow escape to hide and re-show picker
                    this.show();
                return;
            }
            var dateChanged = false,
                dir, day, month,
                newDate, newViewDate;
            switch (e.keyCode) {
                case 27: // escape
                    this.hide();
                    e.preventDefault();
                    break;
                case 37: // left
                case 39: // right
                    if (!this.o.keyboardNavigation) break;
                    dir = e.keyCode == 37 ? -1 : 1;
                    if (e.ctrlKey) {
                        newDate = this.moveYear(this.date, dir);
                        newViewDate = this.moveYear(this.viewDate, dir);
                        this._trigger('changeYear', this.viewDate);
                    } else if (e.shiftKey) {
                        newDate = this.moveMonth(this.date, dir);
                        newViewDate = this.moveMonth(this.viewDate, dir);
                        this._trigger('changeMonth', this.viewDate);
                    } else {
                        newDate = new Date(this.date);
                        newDate.setUTCDate(this.date.getUTCDate() + dir);
                        newViewDate = new Date(this.viewDate);
                        newViewDate.setUTCDate(this.viewDate.getUTCDate() + dir);
                    }
                    if (this.dateWithinRange(newDate)) {
                        this.date = newDate;
                        this.viewDate = newViewDate;
                        this.setValue();
                        this.update();
                        e.preventDefault();
                        dateChanged = true;
                    }
                    break;
                case 38: // up
                case 40: // down
                    if (!this.o.keyboardNavigation) break;
                    dir = e.keyCode == 38 ? -1 : 1;
                    if (e.ctrlKey) {
                        newDate = this.moveYear(this.date, dir);
                        newViewDate = this.moveYear(this.viewDate, dir);
                        this._trigger('changeYear', this.viewDate);
                    } else if (e.shiftKey) {
                        newDate = this.moveMonth(this.date, dir);
                        newViewDate = this.moveMonth(this.viewDate, dir);
                        this._trigger('changeMonth', this.viewDate);
                    } else {
                        newDate = new Date(this.date);
                        newDate.setUTCDate(this.date.getUTCDate() + dir * 7);
                        newViewDate = new Date(this.viewDate);
                        newViewDate.setUTCDate(this.viewDate.getUTCDate() + dir * 7);
                    }
                    if (this.dateWithinRange(newDate)) {
                        this.date = newDate;
                        this.viewDate = newViewDate;
                        this.setValue();
                        this.update();
                        e.preventDefault();
                        dateChanged = true;
                    }
                    break;
                case 13: // enter
                    this.hide();
                    e.preventDefault();
                    break;
                case 9: // tab
                    this.hide();
                    break;
            }
            if (dateChanged) {
                this._trigger('changeDate');
                var element;
                if (this.isInput) {
                    element = this.element;
                } else if (this.component) {
                    element = this.element.find('input');
                }
                if (element) {
                    element.change();
                }
            }
        },

        showMode: function (dir) {
            if (dir) {
                this.viewMode = Math.max(this.o.minViewMode, Math.min(2, this.viewMode + dir));
            }
            /*
             vitalets: fixing bug of very special conditions:
             jquery 1.7.1 + webkit + show inline datepicker in bootstrap popover.
             Method show() does not set display css correctly and datepicker is not shown.
             Changed to .css('display', 'block') solve the problem.
             See https://github.com/vitalets/x-editable/issues/37

             In jquery 1.7.2+ everything works fine.
             */
            //this.picker.find('>div').hide().filter('.datepicker-'+DPGlobal.modes[this.viewMode].clsName).show();
            this.picker.find('>div').hide().filter('.datepicker-' + DPGlobal.modes[this.viewMode].clsName).css('display', 'block');
            this.updateNavArrows();
        }
    };

    var DateRangePicker = function (element, options) {
        this.element = $(element);
        this.inputs = $.map(options.inputs, function (i) {
            return i.jquery ? i[0] : i;
        });
        delete options.inputs;

        $(this.inputs)
            .datepicker(options)
            .bind('changeDate', $.proxy(this.dateUpdated, this));

        this.pickers = $.map(this.inputs, function (i) {
            return $(i).data('datepicker');
        });
        this.updateDates();
    };
    DateRangePicker.prototype = {
        updateDates: function () {
            this.dates = $.map(this.pickers, function (i) {
                return i.date;
            });
            this.updateRanges();
        },
        updateRanges: function () {
            var range = $.map(this.dates, function (d) {
                return d.valueOf();
            });
            $.each(this.pickers, function (i, p) {
                p.setRange(range);
            });
        },
        dateUpdated: function (e) {
            var dp = $(e.target).data('datepicker'),
                new_date = dp.getUTCDate(),
                i = $.inArray(e.target, this.inputs),
                l = this.inputs.length;
            if (i == -1) return;

            if (new_date < this.dates[i]) {
                // Date being moved earlier/left
                while (i >= 0 && new_date < this.dates[i]) {
                    this.pickers[i--].setUTCDate(new_date);
                }
            }
            else if (new_date > this.dates[i]) {
                // Date being moved later/right
                while (i < l && new_date > this.dates[i]) {
                    this.pickers[i++].setUTCDate(new_date);
                }
            }
            this.updateDates();
        },
        remove: function () {
            $.map(this.pickers, function (p) {
                p.remove();
            });
            delete this.element.data().datepicker;
        }
    };

    function opts_from_el(el, prefix) {
        // Derive options from element data-attrs
        var data = $(el).data(),
            out = {}, inkey,
            replace = new RegExp('^' + prefix.toLowerCase() + '([A-Z])'),
            prefix = new RegExp('^' + prefix.toLowerCase());
        for (var key in data)
            if (prefix.test(key)) {
                inkey = key.replace(replace, function (_, a) {
                    return a.toLowerCase();
                });
                out[inkey] = data[key];
            }
        return out;
    }

    function opts_from_locale(lang) {
        // Derive options from locale plugins
        var out = {};
        // Check if "de-DE" style date is available, if not language should
        // fallback to 2 letter code eg "de"
        if (!dates[lang]) {
            lang = lang.split('-')[0]
            if (!dates[lang])
                return;
        }
        var d = dates[lang];
        $.each(locale_opts, function (i, k) {
            if (k in d)
                out[k] = d[k];
        });
        return out;
    }

    var old = $.fn.datepicker;
    $.fn.datepicker = function (option) {
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return,
            this_return;
        this.each(function () {
            var $this = $(this),
                data = $this.data('datepicker'),
                options = typeof option == 'object' && option;
            if (!data) {
                var elopts = opts_from_el(this, 'date'),
                // Preliminary otions
                    xopts = $.extend({}, defaults, elopts, options),
                    locopts = opts_from_locale(xopts.language),
                // Options priority: js args, data-attrs, locales, defaults
                    opts = $.extend({}, defaults, locopts, elopts, options);
                if ($this.is('.input-daterange') || opts.inputs) {
                    var ropts = {
                        inputs: opts.inputs || $this.find('input').toArray()
                    };
                    $this.data('datepicker', (data = new DateRangePicker(this, $.extend(opts, ropts))));
                }
                else {
                    $this.data('datepicker', (data = new Datepicker(this, opts)));
                }
            }
            if (typeof option == 'string' && typeof data[option] == 'function') {
                internal_return = data[option].apply(data, args);
                if (internal_return !== undefined)
                    return false;
            }
        });
        if (internal_return !== undefined)
            return internal_return;
        else
            return this;
    };

    var defaults = $.fn.datepicker.defaults = {
        autoclose: false,
        beforeShowDay: $.noop,
        calendarWeeks: false,
        clearBtn: false,
        daysOfWeekDisabled: [],
        endDate: Infinity,
        forceParse: true,
        format: 'mm/dd/yyyy',
        keyboardNavigation: true,
        language: 'en',
        minViewMode: 0,
        orientation: "auto",
        rtl: false,
        startDate: -Infinity,
        startView: 0,
        todayBtn: false,
        todayHighlight: false,
        weekStart: 0
    };
    var locale_opts = $.fn.datepicker.locale_opts = [
        'format',
        'rtl',
        'weekStart'
    ];
    $.fn.datepicker.Constructor = Datepicker;
    var dates = $.fn.datepicker.dates = {
        en: {
            days: ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"],
            daysShort: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],
            daysMin: ["日", "一", "二", "三", "四", "五", "六"],
            months: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
            monthsShort: ["1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"]
        }
    };

    var DPGlobal = {
        modes: [
            {
                clsName: 'days',
                navFnc: 'Month',
                navStep: 1
            },
            {
                clsName: 'months',
                navFnc: 'FullYear',
                navStep: 1
            },
            {
                clsName: 'years',
                navFnc: 'FullYear',
                navStep: 10
            }],
        isLeapYear: function (year) {
            return (((year % 4 === 0) && (year % 100 !== 0)) || (year % 400 === 0));
        },
        getDaysInMonth: function (year, month) {
            return [31, (DPGlobal.isLeapYear(year) ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month];
        },
        validParts: /dd?|DD?|mm?|MM?|yy(?:yy)?/g,
        nonpunctuation: /[^ -\/:-@\[\u3400-\u9fff-`{-~\t\n\r]+/g,
        parseFormat: function (format) {
            // IE treats \0 as a string end in inputs (truncating the value),
            // so it's a bad format delimiter, anyway
            var separators = format.replace(this.validParts, '\0').split('\0'),
                parts = format.match(this.validParts);
            if (!separators || !separators.length || !parts || parts.length === 0) {
                throw new Error("Invalid date format.");
            }
            return {separators: separators, parts: parts};
        },
        parseDate: function (date, format, language) {
            if (date instanceof Date) return date;
            if (typeof format === 'string')
                format = DPGlobal.parseFormat(format);
            if (/^[\-+]\d+[dmwy]([\s,]+[\-+]\d+[dmwy])*$/.test(date)) {
                var part_re = /([\-+]\d+)([dmwy])/,
                    parts = date.match(/([\-+]\d+)([dmwy])/g),
                    part, dir;
                date = new Date();
                for (var i = 0; i < parts.length; i++) {
                    part = part_re.exec(parts[i]);
                    dir = parseInt(part[1]);
                    switch (part[2]) {
                        case 'd':
                            date.setUTCDate(date.getUTCDate() + dir);
                            break;
                        case 'm':
                            date = Datepicker.prototype.moveMonth.call(Datepicker.prototype, date, dir);
                            break;
                        case 'w':
                            date.setUTCDate(date.getUTCDate() + dir * 7);
                            break;
                        case 'y':
                            date = Datepicker.prototype.moveYear.call(Datepicker.prototype, date, dir);
                            break;
                    }
                }
                return UTCDate(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), 0, 0, 0);
            }
            var parts = date && date.match(this.nonpunctuation) || [],
                date = new Date(),
                parsed = {},
                setters_order = ['yyyy', 'yy', 'M', 'MM', 'm', 'mm', 'd', 'dd'],
                setters_map = {
                    yyyy: function (d, v) {
                        return d.setUTCFullYear(v);
                    },
                    yy: function (d, v) {
                        return d.setUTCFullYear(2000 + v);
                    },
                    m: function (d, v) {
                        if (isNaN(d))
                            return d;
                        v -= 1;
                        while (v < 0) v += 12;
                        v %= 12;
                        d.setUTCMonth(v);
                        while (d.getUTCMonth() != v)
                            d.setUTCDate(d.getUTCDate() - 1);
                        return d;
                    },
                    d: function (d, v) {
                        return d.setUTCDate(v);
                    }
                },
                val, filtered, part;
            setters_map['M'] = setters_map['MM'] = setters_map['mm'] = setters_map['m'];
            setters_map['dd'] = setters_map['d'];
            date = UTCDate(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0);
            var fparts = format.parts.slice();
            // Remove noop parts
            if (parts.length != fparts.length) {
                fparts = $(fparts).filter(function (i, p) {
                    return $.inArray(p, setters_order) !== -1;
                }).toArray();
            }
            // Process remainder
            if (parts.length == fparts.length) {
                for (var i = 0, cnt = fparts.length; i < cnt; i++) {
                    val = parseInt(parts[i], 10);
                    part = fparts[i];
                    if (isNaN(val)) {
                        switch (part) {
                            case 'MM':
                                filtered = $(dates[language].months).filter(function () {
                                    var m = this.slice(0, parts[i].length),
                                        p = parts[i].slice(0, m.length);
                                    return m == p;
                                });
                                val = $.inArray(filtered[0], dates[language].months) + 1;
                                break;
                            case 'M':
                                filtered = $(dates[language].monthsShort).filter(function () {
                                    var m = this.slice(0, parts[i].length),
                                        p = parts[i].slice(0, m.length);
                                    return m == p;
                                });
                                val = $.inArray(filtered[0], dates[language].monthsShort) + 1;
                                break;
                        }
                    }
                    parsed[part] = val;
                }
                for (var i = 0, _date, s; i < setters_order.length; i++) {
                    s = setters_order[i];
                    if (s in parsed && !isNaN(parsed[s])) {
                        _date = new Date(date);
                        setters_map[s](_date, parsed[s]);
                        if (!isNaN(_date))
                            date = _date;
                    }
                }
            }
            return date;
        },
        formatDate: function (date, format, language) {
            if (typeof format === 'string')
                format = DPGlobal.parseFormat(format);
            var val = {
                d: date.getUTCDate(),
                D: dates[language].daysShort[date.getUTCDay()],
                DD: dates[language].days[date.getUTCDay()],
                m: date.getUTCMonth() + 1,
                M: dates[language].monthsShort[date.getUTCMonth()],
                MM: dates[language].months[date.getUTCMonth()],
                yy: date.getUTCFullYear().toString().substring(2),
                yyyy: date.getUTCFullYear()
            };
            val.dd = (val.d < 10 ? '0' : '') + val.d;
            val.mm = (val.m < 10 ? '0' : '') + val.m;
            var date = [],
                seps = $.extend([], format.separators);
            for (var i = 0, cnt = format.parts.length; i <= cnt; i++) {
                if (seps.length)
                    date.push(seps.shift());
                date.push(val[format.parts[i]]);
            }
            return date.join('');
        },
        headTemplate: '<thead>' +
        '<tr>' +
        '<th class="prev">&laquo;</th>' +
        '<th colspan="5" class="datepicker-switch"></th>' +
        '<th class="next">&raquo;</th>' +
        '</tr>' +
        '</thead>',
        contTemplate: '<tbody><tr><td colspan="7"></td></tr></tbody>',
        footTimeTemplate: '<tfoot>' + "<tr><div>" + "<td></td>" + "<td>时：</td>" + hourTemplate + "<td>分：</td>" + minTemplate + "</div></tr>" + '</tfoot>',
        footTemplate: '<tfoot><tr><th colspan="7" class="today"></th></tr><tr><th colspan="7" class="clear"></th></tr></tfoot>'
    };
    DPGlobal.template = '<div class="datepicker">' +
    '<div class="datepicker-days">' +
    '<table class=" table-condensed">' +
    DPGlobal.headTemplate +
    '<tbody></tbody>' +
    DPGlobal.footTemplate +
    '</table>' +
    '</div>' +
    '<div class="datepicker-months">' +
    '<table class="table-condensed">' +
    DPGlobal.headTemplate +
    DPGlobal.contTemplate +
    DPGlobal.footTemplate +
    '</table>' +
    '</div>' +
    '<div class="datepicker-years">' +
    '<table class="table-condensed">' +
    DPGlobal.headTemplate +
    DPGlobal.contTemplate +
    DPGlobal.footTemplate +
    '</table>' +
    '</div>' +
    '</div>';
    DPGlobal.timeTemplate = '<div class="datepicker">' +
    '<div class="datepicker-days">' +
    '<table class=" table-condensed">' +
    DPGlobal.headTemplate +
    '<tbody></tbody>' + DPGlobal.footTimeTemplate +
    '</table>' +
    '</div>' +
    '<div class="datepicker-months">' +
    '<table class="table-condensed">' +
    DPGlobal.headTemplate +
    DPGlobal.contTemplate +
    DPGlobal.footTemplate +
    '</table>' +
    '</div>' +
    '<div class="datepicker-years">' +
    '<table class="table-condensed">' +
    DPGlobal.headTemplate +
    DPGlobal.contTemplate +
    DPGlobal.footTemplate +
    '</table>' +
    '</div>' +
    '</div>';
    $.fn.datepicker.DPGlobal = DPGlobal;


    /* DATEPICKER NO CONFLICT
     * =================== */

    $.fn.datepicker.noConflict = function () {
        $.fn.datepicker = old;
        return this;
    };


    /* DATEPICKER DATA-API
     * ================== */

    $(document).on(
        'focus.datepicker.data-api click.datepicker.data-api',
        '[data-provide="datepicker"]',
        function (e) {
            var $this = $(this);
            if ($this.data('datepicker')) return;
            e.preventDefault();
            // component click requires us to explicitly show it
            $this.datepicker('show');
        }
    );
    $(function () {
        $('[data-provide="datepicker-inline"]').datepicker();
    });

}(window.jQuery));
;/*! bootstrap-timepicker v0.2.5 
* http://jdewit.github.com/bootstrap-timepicker 
* Copyright (c) 2013 Joris de Wit 
* MIT License 
*/!function(a,b,c){"use strict";var d=function(b,c){this.widget="",this.$element=a(b),this.defaultTime=c.defaultTime,this.disableFocus=c.disableFocus,this.disableMousewheel=c.disableMousewheel,this.isOpen=c.isOpen,this.minuteStep=c.minuteStep,this.modalBackdrop=c.modalBackdrop,this.orientation=c.orientation,this.secondStep=c.secondStep,this.showInputs=c.showInputs,this.showMeridian=c.showMeridian,this.showSeconds=c.showSeconds,this.template=c.template,this.appendWidgetTo=c.appendWidgetTo,this.showWidgetOnAddonClick=c.showWidgetOnAddonClick,this._init()};d.prototype={constructor:d,_init:function(){var b=this;this.showWidgetOnAddonClick&&(this.$element.parent().hasClass("input-append")||this.$element.parent().hasClass("input-prepend"))?(this.$element.parent(".input-append, .input-prepend").find(".add-on").on({"click.timepicker":a.proxy(this.showWidget,this)}),this.$element.on({"focus.timepicker":a.proxy(this.highlightUnit,this),"click.timepicker":a.proxy(this.highlightUnit,this),"keydown.timepicker":a.proxy(this.elementKeydown,this),"blur.timepicker":a.proxy(this.blurElement,this),"mousewheel.timepicker DOMMouseScroll.timepicker":a.proxy(this.mousewheel,this)})):this.template?this.$element.on({"focus.timepicker":a.proxy(this.showWidget,this),"click.timepicker":a.proxy(this.showWidget,this),"blur.timepicker":a.proxy(this.blurElement,this),"mousewheel.timepicker DOMMouseScroll.timepicker":a.proxy(this.mousewheel,this)}):this.$element.on({"focus.timepicker":a.proxy(this.highlightUnit,this),"click.timepicker":a.proxy(this.highlightUnit,this),"keydown.timepicker":a.proxy(this.elementKeydown,this),"blur.timepicker":a.proxy(this.blurElement,this),"mousewheel.timepicker DOMMouseScroll.timepicker":a.proxy(this.mousewheel,this)}),this.$widget=this.template!==!1?a(this.getTemplate()).on("click",a.proxy(this.widgetClick,this)):!1,this.showInputs&&this.$widget!==!1&&this.$widget.find("input").each(function(){a(this).on({"click.timepicker":function(){a(this).select()},"keydown.timepicker":a.proxy(b.widgetKeydown,b),"keyup.timepicker":a.proxy(b.widgetKeyup,b)})}),this.setDefaultTime(this.defaultTime)},blurElement:function(){this.highlightedUnit=null,this.updateFromElementVal()},clear:function(){this.hour="",this.minute="",this.second="",this.meridian="",this.$element.val("")},decrementHour:function(){if(this.showMeridian)if(1===this.hour)this.hour=12;else{if(12===this.hour)return this.hour--,this.toggleMeridian();if(0===this.hour)return this.hour=11,this.toggleMeridian();this.hour--}else this.hour<=0?this.hour=23:this.hour--},decrementMinute:function(a){var b;b=a?this.minute-a:this.minute-this.minuteStep,0>b?(this.decrementHour(),this.minute=b+60):this.minute=b},decrementSecond:function(){var a=this.second-this.secondStep;0>a?(this.decrementMinute(!0),this.second=a+60):this.second=a},elementKeydown:function(a){switch(a.keyCode){case 9:case 27:this.updateFromElementVal();break;case 37:a.preventDefault(),this.highlightPrevUnit();break;case 38:switch(a.preventDefault(),this.highlightedUnit){case"hour":this.incrementHour(),this.highlightHour();break;case"minute":this.incrementMinute(),this.highlightMinute();break;case"second":this.incrementSecond(),this.highlightSecond();break;case"meridian":this.toggleMeridian(),this.highlightMeridian()}this.update();break;case 39:a.preventDefault(),this.highlightNextUnit();break;case 40:switch(a.preventDefault(),this.highlightedUnit){case"hour":this.decrementHour(),this.highlightHour();break;case"minute":this.decrementMinute(),this.highlightMinute();break;case"second":this.decrementSecond(),this.highlightSecond();break;case"meridian":this.toggleMeridian(),this.highlightMeridian()}this.update()}},getCursorPosition:function(){var a=this.$element.get(0);if("selectionStart"in a)return a.selectionStart;if(c.selection){a.focus();var b=c.selection.createRange(),d=c.selection.createRange().text.length;return b.moveStart("character",-a.value.length),b.text.length-d}},getTemplate:function(){var a,b,c,d,e,f;switch(this.showInputs?(b='<input type="text" class="bootstrap-timepicker-hour" maxlength="2"/>',c='<input type="text" class="bootstrap-timepicker-minute" maxlength="2"/>',d='<input type="text" class="bootstrap-timepicker-second" maxlength="2"/>',e='<input type="text" class="bootstrap-timepicker-meridian" maxlength="2"/>'):(b='<span class="bootstrap-timepicker-hour"></span>',c='<span class="bootstrap-timepicker-minute"></span>',d='<span class="bootstrap-timepicker-second"></span>',e='<span class="bootstrap-timepicker-meridian"></span>'),f='<table><tr><td><a href="#" data-action="incrementHour"><i class="icon-chevron-up"></i></a></td><td class="separator">&nbsp;</td><td><a href="#" data-action="incrementMinute"><i class="icon-chevron-up"></i></a></td>'+(this.showSeconds?'<td class="separator">&nbsp;</td><td><a href="#" data-action="incrementSecond"><i class="icon-chevron-up"></i></a></td>':"")+(this.showMeridian?'<td class="separator">&nbsp;</td><td class="meridian-column"><a href="#" data-action="toggleMeridian"><i class="icon-chevron-up"></i></a></td>':"")+"</tr>"+"<tr>"+"<td>"+b+"</td> "+'<td class="separator">:</td>'+"<td>"+c+"</td> "+(this.showSeconds?'<td class="separator">:</td><td>'+d+"</td>":"")+(this.showMeridian?'<td class="separator">&nbsp;</td><td>'+e+"</td>":"")+"</tr>"+"<tr>"+'<td><a href="#" data-action="decrementHour"><i class="icon-chevron-down"></i></a></td>'+'<td class="separator"></td>'+'<td><a href="#" data-action="decrementMinute"><i class="icon-chevron-down"></i></a></td>'+(this.showSeconds?'<td class="separator">&nbsp;</td><td><a href="#" data-action="decrementSecond"><i class="icon-chevron-down"></i></a></td>':"")+(this.showMeridian?'<td class="separator">&nbsp;</td><td><a href="#" data-action="toggleMeridian"><i class="icon-chevron-down"></i></a></td>':"")+"</tr>"+"</table>",this.template){case"modal":a='<div class="bootstrap-timepicker-widget modal hide fade in" data-backdrop="'+(this.modalBackdrop?"true":"false")+'">'+'<div class="modal-header">'+'<a href="#" class="close" data-dismiss="modal">×</a>'+"<h3>Pick a Time</h3>"+"</div>"+'<div class="modal-content">'+f+"</div>"+'<div class="modal-footer">'+'<a href="#" class="btn btn-primary" data-dismiss="modal">OK</a>'+"</div>"+"</div>";break;case"dropdown":a='<div class="bootstrap-timepicker-widget dropdown-menu">'+f+"</div>"}return a},getTime:function(){return this.hour||this.minute||this.second?this.hour+":"+(1===this.minute.toString().length?"0"+this.minute:this.minute)+(this.showSeconds?":"+(1===this.second.toString().length?"0"+this.second:this.second):"")+(this.showMeridian?" "+this.meridian:""):""},hideWidget:function(){this.isOpen!==!1&&(this.$element.trigger({type:"hide.timepicker",time:{value:this.getTime(),hours:this.hour,minutes:this.minute,seconds:this.second,meridian:this.meridian}}),"modal"===this.template&&this.$widget.modal?this.$widget.modal("hide"):this.$widget.removeClass("open"),a(c).off("mousedown.timepicker, touchend.timepicker"),this.isOpen=!1,this.$widget.detach())},highlightUnit:function(){this.position=this.getCursorPosition(),this.position>=0&&this.position<=2?this.highlightHour():this.position>=3&&this.position<=5?this.highlightMinute():this.position>=6&&this.position<=8?this.showSeconds?this.highlightSecond():this.highlightMeridian():this.position>=9&&this.position<=11&&this.highlightMeridian()},highlightNextUnit:function(){switch(this.highlightedUnit){case"hour":this.highlightMinute();break;case"minute":this.showSeconds?this.highlightSecond():this.showMeridian?this.highlightMeridian():this.highlightHour();break;case"second":this.showMeridian?this.highlightMeridian():this.highlightHour();break;case"meridian":this.highlightHour()}},highlightPrevUnit:function(){switch(this.highlightedUnit){case"hour":this.showMeridian?this.highlightMeridian():this.showSeconds?this.highlightSecond():this.highlightMinute();break;case"minute":this.highlightHour();break;case"second":this.highlightMinute();break;case"meridian":this.showSeconds?this.highlightSecond():this.highlightMinute()}},highlightHour:function(){var a=this.$element.get(0),b=this;this.highlightedUnit="hour",a.setSelectionRange&&setTimeout(function(){b.hour<10?a.setSelectionRange(0,1):a.setSelectionRange(0,2)},0)},highlightMinute:function(){var a=this.$element.get(0),b=this;this.highlightedUnit="minute",a.setSelectionRange&&setTimeout(function(){b.hour<10?a.setSelectionRange(2,4):a.setSelectionRange(3,5)},0)},highlightSecond:function(){var a=this.$element.get(0),b=this;this.highlightedUnit="second",a.setSelectionRange&&setTimeout(function(){b.hour<10?a.setSelectionRange(5,7):a.setSelectionRange(6,8)},0)},highlightMeridian:function(){var a=this.$element.get(0),b=this;this.highlightedUnit="meridian",a.setSelectionRange&&(this.showSeconds?setTimeout(function(){b.hour<10?a.setSelectionRange(8,10):a.setSelectionRange(9,11)},0):setTimeout(function(){b.hour<10?a.setSelectionRange(5,7):a.setSelectionRange(6,8)},0))},incrementHour:function(){if(this.showMeridian){if(11===this.hour)return this.hour++,this.toggleMeridian();12===this.hour&&(this.hour=0)}return 23===this.hour?(this.hour=0,void 0):(this.hour++,void 0)},incrementMinute:function(a){var b;b=a?this.minute+a:this.minute+this.minuteStep-this.minute%this.minuteStep,b>59?(this.incrementHour(),this.minute=b-60):this.minute=b},incrementSecond:function(){var a=this.second+this.secondStep-this.second%this.secondStep;a>59?(this.incrementMinute(!0),this.second=a-60):this.second=a},mousewheel:function(b){if(!this.disableMousewheel){b.preventDefault(),b.stopPropagation();var c=b.originalEvent.wheelDelta||-b.originalEvent.detail,d=null;switch("mousewheel"===b.type?d=-1*b.originalEvent.wheelDelta:"DOMMouseScroll"===b.type&&(d=40*b.originalEvent.detail),d&&(b.preventDefault(),a(this).scrollTop(d+a(this).scrollTop())),this.highlightedUnit){case"minute":c>0?this.incrementMinute():this.decrementMinute(),this.highlightMinute();break;case"second":c>0?this.incrementSecond():this.decrementSecond(),this.highlightSecond();break;case"meridian":this.toggleMeridian(),this.highlightMeridian();break;default:c>0?this.incrementHour():this.decrementHour(),this.highlightHour()}return!1}},place:function(){if(!this.isInline){var c=this.$widget.outerWidth(),d=this.$widget.outerHeight(),e=10,f=a(b).width(),g=a(b).height(),h=a(b).scrollTop(),i=parseInt(this.$element.parents().filter(function(){}).first().css("z-index"),10)+10,j=this.component?this.component.parent().offset():this.$element.offset(),k=this.component?this.component.outerHeight(!0):this.$element.outerHeight(!1),l=this.component?this.component.outerWidth(!0):this.$element.outerWidth(!1),m=j.left,n=j.top;this.$widget.removeClass("timepicker-orient-top timepicker-orient-bottom timepicker-orient-right timepicker-orient-left"),"auto"!==this.orientation.x?(this.picker.addClass("datepicker-orient-"+this.orientation.x),"right"===this.orientation.x&&(m-=c-l)):(this.$widget.addClass("timepicker-orient-left"),j.left<0?m-=j.left-e:j.left+c>f&&(m=f-c-e));var o,p,q=this.orientation.y;"auto"===q&&(o=-h+j.top-d,p=h+g-(j.top+k+d),q=Math.max(o,p)===p?"top":"bottom"),this.$widget.addClass("timepicker-orient-"+q),"top"===q?n+=k:n-=d+parseInt(this.$widget.css("padding-top"),10),this.$widget.css({top:n,left:m,zIndex:i})}},remove:function(){a("document").off(".timepicker"),this.$widget&&this.$widget.remove(),delete this.$element.data().timepicker},setDefaultTime:function(a){if(this.$element.val())this.updateFromElementVal();else if("current"===a){var b=new Date,c=b.getHours(),d=b.getMinutes(),e=b.getSeconds(),f="AM";0!==e&&(e=Math.ceil(b.getSeconds()/this.secondStep)*this.secondStep,60===e&&(d+=1,e=0)),0!==d&&(d=Math.ceil(b.getMinutes()/this.minuteStep)*this.minuteStep,60===d&&(c+=1,d=0)),this.showMeridian&&(0===c?c=12:c>=12?(c>12&&(c-=12),f="PM"):f="AM"),this.hour=c,this.minute=d,this.second=e,this.meridian=f,this.update()}else a===!1?(this.hour=0,this.minute=0,this.second=0,this.meridian="AM"):this.setTime(a)},setTime:function(a,b){if(!a)return this.clear(),void 0;var c,d,e,f,g;"object"==typeof a&&a.getMonth?(d=a.getHours(),e=a.getMinutes(),f=a.getSeconds(),this.showMeridian&&(g="AM",d>12&&(g="PM",d%=12),12===d&&(g="PM"))):(g=null!==a.match(/p/i)?"PM":"AM",a=a.replace(/[^0-9\:]/g,""),c=a.split(":"),d=c[0]?c[0].toString():c.toString(),e=c[1]?c[1].toString():"",f=c[2]?c[2].toString():"",d.length>4&&(f=d.substr(4,2)),d.length>2&&(e=d.substr(2,2),d=d.substr(0,2)),e.length>2&&(f=e.substr(2,2),e=e.substr(0,2)),f.length>2&&(f=f.substr(2,2)),d=parseInt(d,10),e=parseInt(e,10),f=parseInt(f,10),isNaN(d)&&(d=0),isNaN(e)&&(e=0),isNaN(f)&&(f=0),this.showMeridian?1>d?d=1:d>12&&(d=12):(d>=24?d=23:0>d&&(d=0),13>d&&"PM"===g&&(d+=12)),0>e?e=0:e>=60&&(e=59),this.showSeconds&&(isNaN(f)?f=0:0>f?f=0:f>=60&&(f=59))),this.hour=d,this.minute=e,this.second=f,this.meridian=g,this.update(b)},showWidget:function(){if(!this.isOpen&&!this.$element.is(":disabled")){this.$widget.appendTo(this.appendWidgetTo);var b=this;a(c).on("mousedown.timepicker, touchend.timepicker",function(a){b.$element.parent().find(a.target).length||b.$widget.is(a.target)||b.$widget.find(a.target).length||b.hideWidget()}),this.$element.trigger({type:"show.timepicker",time:{value:this.getTime(),hours:this.hour,minutes:this.minute,seconds:this.second,meridian:this.meridian}}),this.place(),this.disableFocus&&this.$element.blur(),this.hour||(this.defaultTime?this.setDefaultTime(this.defaultTime):this.setTime("0:0:0")),"modal"===this.template&&this.$widget.modal?this.$widget.modal("show").on("hidden",a.proxy(this.hideWidget,this)):this.isOpen===!1&&this.$widget.addClass("open"),this.isOpen=!0}},toggleMeridian:function(){this.meridian="AM"===this.meridian?"PM":"AM"},update:function(a){this.updateElement(),a||this.updateWidget(),this.$element.trigger({type:"changeTime.timepicker",time:{value:this.getTime(),hours:this.hour,minutes:this.minute,seconds:this.second,meridian:this.meridian}})},updateElement:function(){this.$element.val(this.getTime()).change()},updateFromElementVal:function(){this.setTime(this.$element.val())},updateWidget:function(){if(this.$widget!==!1){var a=this.hour,b=1===this.minute.toString().length?"0"+this.minute:this.minute,c=1===this.second.toString().length?"0"+this.second:this.second;this.showInputs?(this.$widget.find("input.bootstrap-timepicker-hour").val(a),this.$widget.find("input.bootstrap-timepicker-minute").val(b),this.showSeconds&&this.$widget.find("input.bootstrap-timepicker-second").val(c),this.showMeridian&&this.$widget.find("input.bootstrap-timepicker-meridian").val(this.meridian)):(this.$widget.find("span.bootstrap-timepicker-hour").text(a),this.$widget.find("span.bootstrap-timepicker-minute").text(b),this.showSeconds&&this.$widget.find("span.bootstrap-timepicker-second").text(c),this.showMeridian&&this.$widget.find("span.bootstrap-timepicker-meridian").text(this.meridian))}},updateFromWidgetInputs:function(){if(this.$widget!==!1){var a=this.$widget.find("input.bootstrap-timepicker-hour").val()+":"+this.$widget.find("input.bootstrap-timepicker-minute").val()+(this.showSeconds?":"+this.$widget.find("input.bootstrap-timepicker-second").val():"")+(this.showMeridian?this.$widget.find("input.bootstrap-timepicker-meridian").val():"");this.setTime(a,!0)}},widgetClick:function(b){b.stopPropagation(),b.preventDefault();var c=a(b.target),d=c.closest("a").data("action");d&&this[d](),this.update(),c.is("input")&&c.get(0).setSelectionRange(0,2)},widgetKeydown:function(b){var c=a(b.target),d=c.attr("class").replace("bootstrap-timepicker-","");switch(b.keyCode){case 9:if(this.showMeridian&&"meridian"===d||this.showSeconds&&"second"===d||!this.showMeridian&&!this.showSeconds&&"minute"===d)return this.hideWidget();break;case 27:this.hideWidget();break;case 38:switch(b.preventDefault(),d){case"hour":this.incrementHour();break;case"minute":this.incrementMinute();break;case"second":this.incrementSecond();break;case"meridian":this.toggleMeridian()}this.setTime(this.getTime()),c.get(0).setSelectionRange(0,2);break;case 40:switch(b.preventDefault(),d){case"hour":this.decrementHour();break;case"minute":this.decrementMinute();break;case"second":this.decrementSecond();break;case"meridian":this.toggleMeridian()}this.setTime(this.getTime()),c.get(0).setSelectionRange(0,2)}},widgetKeyup:function(a){(65===a.keyCode||77===a.keyCode||80===a.keyCode||46===a.keyCode||8===a.keyCode||a.keyCode>=46&&a.keyCode<=57)&&this.updateFromWidgetInputs()}},a.fn.timepicker=function(b){var c=Array.apply(null,arguments);return c.shift(),this.each(function(){var e=a(this),f=e.data("timepicker"),g="object"==typeof b&&b;f||e.data("timepicker",f=new d(this,a.extend({},a.fn.timepicker.defaults,g,a(this).data()))),"string"==typeof b&&f[b].apply(f,c)})},a.fn.timepicker.defaults={defaultTime:"current",disableFocus:!1,disableMousewheel:!1,isOpen:!1,minuteStep:15,modalBackdrop:!1,orientation:{x:"auto",y:"auto"},secondStep:15,showSeconds:!1,showInputs:!0,showMeridian:!0,template:"dropdown",appendWidgetTo:"body",showWidgetOnAddonClick:!0},a.fn.timepicker.Constructor=d}(jQuery,window,document);;(function(B,n){"object"===typeof exports?n(exports):"function"===typeof define&&define.amd?define(["exports"],n):n(B)})(this,function(B){function n(a){this._targetElement=a;this._options={nextLabel:"Next &rarr;",prevLabel:"&larr; Back",skipLabel:"Skip",doneLabel:"Done",tooltipPosition:"bottom",tooltipClass:"",highlightClass:"",exitOnEsc:!0,exitOnOverlayClick:!0,showStepNumbers:!0,keyboardNavigation:!0,showButtons:!0,showBullets:!0,showProgress:!1,scrollToElement:!0,overlayOpacity:0.8,positionPrecedence:["bottom",
"top","right","left"],disableInteraction:!1}}function M(a){var b=[],c=this;if(this._options.steps)for(var e=0,d=this._options.steps.length;e<d;e++){var f=H(this._options.steps[e]);f.step=b.length+1;"string"===typeof f.element&&(f.element=document.querySelector(f.element));if("undefined"===typeof f.element||null==f.element){var h=document.querySelector(".introjsFloatingElement");null==h&&(h=document.createElement("div"),h.className="introjsFloatingElement",document.body.appendChild(h));f.element=h;
f.position="floating"}null!=f.element&&b.push(f)}else{d=a.querySelectorAll("*[data-intro]");if(1>d.length)return!1;e=0;for(f=d.length;e<f;e++){var h=d[e],q=parseInt(h.getAttribute("data-step"),10);0<q&&(b[q-1]={element:h,intro:h.getAttribute("data-intro"),step:parseInt(h.getAttribute("data-step"),10),tooltipClass:h.getAttribute("data-tooltipClass"),highlightClass:h.getAttribute("data-highlightClass"),position:h.getAttribute("data-position")||this._options.tooltipPosition})}e=q=0;for(f=d.length;e<
f;e++)if(h=d[e],null==h.getAttribute("data-step")){for(;"undefined"!=typeof b[q];)q++;b[q]={element:h,intro:h.getAttribute("data-intro"),step:q+1,tooltipClass:h.getAttribute("data-tooltipClass"),highlightClass:h.getAttribute("data-highlightClass"),position:h.getAttribute("data-position")||this._options.tooltipPosition}}}e=[];for(d=0;d<b.length;d++)b[d]&&e.push(b[d]);b=e;b.sort(function(a,b){return a.step-b.step});c._introItems=b;N.call(c,a)&&(x.call(c),a.querySelector(".introjs-skipbutton"),a.querySelector(".introjs-nextbutton"),
c._onKeyDown=function(b){if(27===b.keyCode&&!0==c._options.exitOnEsc)void 0!=c._introExitCallback&&c._introExitCallback.call(c),y.call(c,a);else if(37===b.keyCode)C.call(c);else if(39===b.keyCode)x.call(c);else if(13===b.keyCode){var d=b.target||b.srcElement;d&&0<d.className.indexOf("introjs-prevbutton")?C.call(c):d&&0<d.className.indexOf("introjs-skipbutton")?(c._introItems.length-1==c._currentStep&&"function"===typeof c._introCompleteCallback&&c._introCompleteCallback.call(c),void 0!=c._introExitCallback&&
c._introExitCallback.call(c),y.call(c,a)):x.call(c);b.preventDefault?b.preventDefault():b.returnValue=!1}},c._onResize=function(a){t.call(c,document.querySelector(".introjs-helperLayer"));t.call(c,document.querySelector(".introjs-tooltipReferenceLayer"))},window.addEventListener?(this._options.keyboardNavigation&&window.addEventListener("keydown",c._onKeyDown,!0),window.addEventListener("resize",c._onResize,!0)):document.attachEvent&&(this._options.keyboardNavigation&&document.attachEvent("onkeydown",
c._onKeyDown),document.attachEvent("onresize",c._onResize)));return!1}function H(a){if(null==a||"object"!=typeof a||"undefined"!=typeof a.nodeType)return a;var b={},c;for(c in a)b[c]="undefined"!=typeof jQuery&&a[c]instanceof jQuery?a[c]:H(a[c]);return b}function x(){this._direction="forward";"undefined"===typeof this._currentStep?this._currentStep=0:++this._currentStep;if(this._introItems.length<=this._currentStep)"function"===typeof this._introCompleteCallback&&this._introCompleteCallback.call(this),
y.call(this,this._targetElement);else{var a=this._introItems[this._currentStep];"undefined"!==typeof this._introBeforeChangeCallback&&this._introBeforeChangeCallback.call(this,a.element);I.call(this,a)}}function C(){this._direction="backward";if(0===this._currentStep)return!1;var a=this._introItems[--this._currentStep];"undefined"!==typeof this._introBeforeChangeCallback&&this._introBeforeChangeCallback.call(this,a.element);I.call(this,a)}function y(a){var b=a.querySelector(".introjs-overlay");if(null!=
b){b.style.opacity=0;setTimeout(function(){b.parentNode&&b.parentNode.removeChild(b)},500);var c=a.querySelector(".introjs-helperLayer");c&&c.parentNode.removeChild(c);(c=a.querySelector(".introjs-tooltipReferenceLayer"))&&c.parentNode.removeChild(c);(a=a.querySelector(".introjs-disableInteraction"))&&a.parentNode.removeChild(a);(a=document.querySelector(".introjsFloatingElement"))&&a.parentNode.removeChild(a);if(a=document.querySelector(".introjs-showElement"))a.className=a.className.replace(/introjs-[a-zA-Z]+/g,
"").replace(/^\s+|\s+$/g,"");if((a=document.querySelectorAll(".introjs-fixParent"))&&0<a.length)for(c=a.length-1;0<=c;c--)a[c].className=a[c].className.replace(/introjs-fixParent/g,"").replace(/^\s+|\s+$/g,"");window.removeEventListener?window.removeEventListener("keydown",this._onKeyDown,!0):document.detachEvent&&document.detachEvent("onkeydown",this._onKeyDown);this._currentStep=void 0}}function J(a,b,c,e){var d="",f,h;b.style.top=null;b.style.right=null;b.style.bottom=null;b.style.left=null;b.style.marginLeft=
null;b.style.marginTop=null;c.style.display="inherit";"undefined"!=typeof e&&null!=e&&(e.style.top=null,e.style.left=null);if(this._introItems[this._currentStep]){d=this._introItems[this._currentStep];d="string"===typeof d.tooltipClass?d.tooltipClass:this._options.tooltipClass;b.className=("introjs-tooltip "+d).replace(/^\s+|\s+$/g,"");h=this._introItems[this._currentStep].position;if(("auto"==h||"auto"==this._options.tooltipPosition)&&"floating"!=h){d=h;f=this._options.positionPrecedence.slice();
h=F();var q=v(b).height+10,r=v(b).width+20,k=v(a),l="floating";k.left+r>h.width||0>k.left+k.width/2-r?(s(f,"bottom"),s(f,"top")):(k.height+k.top+q>h.height&&s(f,"bottom"),0>k.top-q&&s(f,"top"));k.width+k.left+r>h.width&&s(f,"right");0>k.left-r&&s(f,"left");0<f.length&&(l=f[0]);d&&"auto"!=d&&-1<f.indexOf(d)&&(l=d);h=l}d=v(a);a=v(b);f=F();switch(h){case "top":c.className="introjs-arrow bottom";z(d,15,a,f,b);b.style.bottom=d.height+20+"px";break;case "right":b.style.left=d.width+20+"px";d.top+a.height>
f.height?(c.className="introjs-arrow left-bottom",b.style.top="-"+(a.height-d.height-20)+"px"):c.className="introjs-arrow left";break;case "left":!0==this._options.showStepNumbers&&(b.style.top="15px");d.top+a.height>f.height?(b.style.top="-"+(a.height-d.height-20)+"px",c.className="introjs-arrow right-bottom"):c.className="introjs-arrow right";b.style.right=d.width+20+"px";break;case "floating":c.style.display="none";b.style.left="50%";b.style.top="50%";b.style.marginLeft="-"+a.width/2+"px";b.style.marginTop=
"-"+a.height/2+"px";"undefined"!=typeof e&&null!=e&&(e.style.left="-"+(a.width/2+18)+"px",e.style.top="-"+(a.height/2+18)+"px");break;case "bottom-right-aligned":c.className="introjs-arrow top-right";K(d,0,a,b);b.style.top=d.height+20+"px";break;case "bottom-middle-aligned":c.className="introjs-arrow top-middle";c=d.width/2-a.width/2;K(d,c,a,b)&&(b.style.right=null,z(d,c,a,f,b));b.style.top=d.height+20+"px";break;default:c.className="introjs-arrow top",z(d,0,a,f,b),b.style.top=d.height+20+"px"}}}
function z(a,b,c,e,d){if(a.left+b+c.width>e.width)return d.style.left=e.width-c.width-a.left+"px",!1;d.style.left=b+"px";return!0}function K(a,b,c,e){if(0>a.left+a.width-b-c.width)return e.style.left=-a.left+"px",!1;e.style.right=b+"px";return!0}function s(a,b){-1<a.indexOf(b)&&a.splice(a.indexOf(b),1)}function t(a){if(a&&this._introItems[this._currentStep]){var b=this._introItems[this._currentStep],c=v(b.element),e=10;"floating"==b.position&&(e=0);a.setAttribute("style","width: "+(c.width+e)+"px; height:"+
(c.height+e)+"px; top:"+(c.top-5)+"px;left: "+(c.left-5)+"px;")}}function O(){var a=document.querySelector(".introjs-disableInteraction");null===a&&(a=document.createElement("div"),a.className="introjs-disableInteraction",this._targetElement.appendChild(a));t.call(this,a)}function I(a){"undefined"!==typeof this._introChangeCallback&&this._introChangeCallback.call(this,a.element);var b=this,c=document.querySelector(".introjs-helperLayer"),e=document.querySelector(".introjs-tooltipReferenceLayer"),
d="introjs-helperLayer";v(a.element);"string"===typeof a.highlightClass&&(d+=" "+a.highlightClass);"string"===typeof this._options.highlightClass&&(d+=" "+this._options.highlightClass);if(null!=c){var f=e.querySelector(".introjs-helperNumberLayer"),h=e.querySelector(".introjs-tooltiptext"),q=e.querySelector(".introjs-arrow"),r=e.querySelector(".introjs-tooltip"),k=e.querySelector(".introjs-skipbutton"),l=e.querySelector(".introjs-prevbutton"),p=e.querySelector(".introjs-nextbutton");c.className=d;
r.style.opacity=0;r.style.display="none";if(null!=f){var g=this._introItems[0<=a.step-2?a.step-2:0];if(null!=g&&"forward"==this._direction&&"floating"==g.position||"backward"==this._direction&&"floating"==a.position)f.style.opacity=0}t.call(b,c);t.call(b,e);if((g=document.querySelectorAll(".introjs-fixParent"))&&0<g.length)for(d=g.length-1;0<=d;d--)g[d].className=g[d].className.replace(/introjs-fixParent/g,"").replace(/^\s+|\s+$/g,"");g=document.querySelector(".introjs-showElement");g.className=g.className.replace(/introjs-[a-zA-Z]+/g,
"").replace(/^\s+|\s+$/g,"");b._lastShowElementTimer&&clearTimeout(b._lastShowElementTimer);b._lastShowElementTimer=setTimeout(function(){null!=f&&(f.innerHTML=a.step);h.innerHTML=a.intro;r.style.display="block";J.call(b,a.element,r,q,f);e.querySelector(".introjs-bullets li > a.active").className="";e.querySelector('.introjs-bullets li > a[data-stepnumber="'+a.step+'"]').className="active";e.querySelector(".introjs-progress .introjs-progressbar").setAttribute("style","width:"+L.call(b)+"%;");r.style.opacity=
1;f&&(f.style.opacity=1);-1===p.tabIndex?k.focus():p.focus()},350)}else{var n=document.createElement("div"),l=document.createElement("div"),c=document.createElement("div"),m=document.createElement("div"),s=document.createElement("div"),D=document.createElement("div"),E=document.createElement("div"),u=document.createElement("div");n.className=d;l.className="introjs-tooltipReferenceLayer";t.call(b,n);t.call(b,l);this._targetElement.appendChild(n);this._targetElement.appendChild(l);c.className="introjs-arrow";
s.className="introjs-tooltiptext";s.innerHTML=a.intro;D.className="introjs-bullets";!1===this._options.showBullets&&(D.style.display="none");for(var n=document.createElement("ul"),d=0,B=this._introItems.length;d<B;d++){var z=document.createElement("li"),A=document.createElement("a");A.onclick=function(){b.goToStep(this.getAttribute("data-stepnumber"))};d===a.step-1&&(A.className="active");A.href="javascript:void(0);";A.innerHTML="&nbsp;";A.setAttribute("data-stepnumber",this._introItems[d].step);
z.appendChild(A);n.appendChild(z)}D.appendChild(n);E.className="introjs-progress";!1===this._options.showProgress&&(E.style.display="none");d=document.createElement("div");d.className="introjs-progressbar";d.setAttribute("style","width:"+L.call(this)+"%;");E.appendChild(d);u.className="introjs-tooltipbuttons";!1===this._options.showButtons&&(u.style.display="none");m.className="introjs-tooltip";m.appendChild(s);m.appendChild(D);m.appendChild(E);!0==this._options.showStepNumbers&&(g=document.createElement("span"),
g.className="introjs-helperNumberLayer",g.innerHTML=a.step,l.appendChild(g));m.appendChild(c);l.appendChild(m);p=document.createElement("a");p.onclick=function(){b._introItems.length-1!=b._currentStep&&x.call(b)};p.href="javascript:void(0);";p.innerHTML=this._options.nextLabel;l=document.createElement("a");l.onclick=function(){0!=b._currentStep&&C.call(b)};l.href="javascript:void(0);";l.innerHTML=this._options.prevLabel;k=document.createElement("a");k.className="introjs-button introjs-skipbutton";
k.href="javascript:void(0);";k.innerHTML=this._options.skipLabel;k.onclick=function(){b._introItems.length-1==b._currentStep&&"function"===typeof b._introCompleteCallback&&b._introCompleteCallback.call(b);b._introItems.length-1!=b._currentStep&&"function"===typeof b._introExitCallback&&b._introExitCallback.call(b);y.call(b,b._targetElement)};u.appendChild(k);1<this._introItems.length&&(u.appendChild(l),u.appendChild(p));m.appendChild(u);J.call(b,a.element,m,c,g)}!0===this._options.disableInteraction&&
O.call(b);l.removeAttribute("tabIndex");p.removeAttribute("tabIndex");0==this._currentStep&&1<this._introItems.length?(l.className="introjs-button introjs-prevbutton introjs-disabled",l.tabIndex="-1",p.className="introjs-button introjs-nextbutton",k.innerHTML=this._options.skipLabel):this._introItems.length-1==this._currentStep||1==this._introItems.length?(k.innerHTML=this._options.doneLabel,l.className="introjs-button introjs-prevbutton",p.className="introjs-button introjs-nextbutton introjs-disabled",
p.tabIndex="-1"):(l.className="introjs-button introjs-prevbutton",p.className="introjs-button introjs-nextbutton",k.innerHTML=this._options.skipLabel);p.focus();a.element.className+=" introjs-showElement";g=w(a.element,"position");"absolute"!==g&&"relative"!==g&&(a.element.className+=" introjs-relativePosition");for(g=a.element.parentNode;null!=g&&"body"!==g.tagName.toLowerCase();){c=w(g,"z-index");m=parseFloat(w(g,"opacity"));u=w(g,"transform")||w(g,"-webkit-transform")||w(g,"-moz-transform")||w(g,
"-ms-transform")||w(g,"-o-transform");if(/[0-9]+/.test(c)||1>m||"none"!==u&&void 0!==u)g.className+=" introjs-fixParent";g=g.parentNode}P(a.element)||!0!==this._options.scrollToElement||(m=a.element.getBoundingClientRect(),g=F().height,c=m.bottom-(m.bottom-m.top),m=m.bottom-g,0>c||a.element.clientHeight>g?window.scrollBy(0,c-30):window.scrollBy(0,m+100));"undefined"!==typeof this._introAfterChangeCallback&&this._introAfterChangeCallback.call(this,a.element)}function w(a,b){var c="";a.currentStyle?
c=a.currentStyle[b]:document.defaultView&&document.defaultView.getComputedStyle&&(c=document.defaultView.getComputedStyle(a,null).getPropertyValue(b));return c&&c.toLowerCase?c.toLowerCase():c}function F(){if(void 0!=window.innerWidth)return{width:window.innerWidth,height:window.innerHeight};var a=document.documentElement;return{width:a.clientWidth,height:a.clientHeight}}function P(a){a=a.getBoundingClientRect();return 0<=a.top&&0<=a.left&&a.bottom+80<=window.innerHeight&&a.right<=window.innerWidth}
function N(a){var b=document.createElement("div"),c="",e=this;b.className="introjs-overlay";if("body"===a.tagName.toLowerCase())c+="top: 0;bottom: 0; left: 0;right: 0;position: fixed;",b.setAttribute("style",c);else{var d=v(a);d&&(c+="width: "+d.width+"px; height:"+d.height+"px; top:"+d.top+"px;left: "+d.left+"px;",b.setAttribute("style",c))}a.appendChild(b);b.onclick=function(){!0==e._options.exitOnOverlayClick&&(void 0!=e._introExitCallback&&e._introExitCallback.call(e),y.call(e,a))};setTimeout(function(){c+=
"opacity: "+e._options.overlayOpacity.toString()+";";b.setAttribute("style",c)},10);return!0}function v(a){var b={};b.width=a.offsetWidth;b.height=a.offsetHeight;for(var c=0,e=0;a&&!isNaN(a.offsetLeft)&&!isNaN(a.offsetTop);)c+=a.offsetLeft,e+=a.offsetTop,a=a.offsetParent;b.top=e;b.left=c;return b}function L(){return 100*(parseInt(this._currentStep+1,10)/this._introItems.length)}var G=function(a){if("object"===typeof a)return new n(a);if("string"===typeof a){if(a=document.querySelector(a))return new n(a);
throw Error("There is no element with given selector.");}return new n(document.body)};G.version="1.1.1";G.fn=n.prototype={clone:function(){return new n(this)},setOption:function(a,b){this._options[a]=b;return this},setOptions:function(a){var b=this._options,c={},e;for(e in b)c[e]=b[e];for(e in a)c[e]=a[e];this._options=c;return this},start:function(){M.call(this,this._targetElement);return this},goToStep:function(a){this._currentStep=a-2;"undefined"!==typeof this._introItems&&x.call(this);return this},
nextStep:function(){x.call(this);return this},previousStep:function(){C.call(this);return this},exit:function(){y.call(this,this._targetElement);return this},refresh:function(){t.call(this,document.querySelector(".introjs-helperLayer"));t.call(this,document.querySelector(".introjs-tooltipReferenceLayer"));return this},onbeforechange:function(a){if("function"===typeof a)this._introBeforeChangeCallback=a;else throw Error("Provided callback for onbeforechange was not a function");return this},onchange:function(a){if("function"===
typeof a)this._introChangeCallback=a;else throw Error("Provided callback for onchange was not a function.");return this},onafterchange:function(a){if("function"===typeof a)this._introAfterChangeCallback=a;else throw Error("Provided callback for onafterchange was not a function");return this},oncomplete:function(a){if("function"===typeof a)this._introCompleteCallback=a;else throw Error("Provided callback for oncomplete was not a function.");return this},onexit:function(a){if("function"===typeof a)this._introExitCallback=
a;else throw Error("Provided callback for onexit was not a function.");return this}};return B.introJs=G});
;//! moment.js
//! version : 2.8.1
//! authors : Tim Wood, Iskren Chernev, Moment.js contributors
//! license : MIT
//! momentjs.com
(function(a){function b(a,b,c){switch(arguments.length){case 2:return null!=a?a:b;case 3:return null!=a?a:null!=b?b:c;default:throw new Error("Implement me")}}function c(){return{empty:!1,unusedTokens:[],unusedInput:[],overflow:-2,charsLeftOver:0,nullInput:!1,invalidMonth:null,invalidFormat:!1,userInvalidated:!1,iso:!1}}function d(a){rb.suppressDeprecationWarnings===!1&&"undefined"!=typeof console&&console.warn&&console.warn("Deprecation warning: "+a)}function e(a,b){var c=!0;return l(function(){return c&&(d(a),c=!1),b.apply(this,arguments)},b)}function f(a,b){nc[a]||(d(b),nc[a]=!0)}function g(a,b){return function(c){return o(a.call(this,c),b)}}function h(a,b){return function(c){return this.localeData().ordinal(a.call(this,c),b)}}function i(){}function j(a,b){b!==!1&&E(a),m(this,a),this._d=new Date(+a._d)}function k(a){var b=x(a),c=b.year||0,d=b.quarter||0,e=b.month||0,f=b.week||0,g=b.day||0,h=b.hour||0,i=b.minute||0,j=b.second||0,k=b.millisecond||0;this._milliseconds=+k+1e3*j+6e4*i+36e5*h,this._days=+g+7*f,this._months=+e+3*d+12*c,this._data={},this._locale=rb.localeData(),this._bubble()}function l(a,b){for(var c in b)b.hasOwnProperty(c)&&(a[c]=b[c]);return b.hasOwnProperty("toString")&&(a.toString=b.toString),b.hasOwnProperty("valueOf")&&(a.valueOf=b.valueOf),a}function m(a,b){var c,d,e;if("undefined"!=typeof b._isAMomentObject&&(a._isAMomentObject=b._isAMomentObject),"undefined"!=typeof b._i&&(a._i=b._i),"undefined"!=typeof b._f&&(a._f=b._f),"undefined"!=typeof b._l&&(a._l=b._l),"undefined"!=typeof b._strict&&(a._strict=b._strict),"undefined"!=typeof b._tzm&&(a._tzm=b._tzm),"undefined"!=typeof b._isUTC&&(a._isUTC=b._isUTC),"undefined"!=typeof b._offset&&(a._offset=b._offset),"undefined"!=typeof b._pf&&(a._pf=b._pf),"undefined"!=typeof b._locale&&(a._locale=b._locale),Fb.length>0)for(c in Fb)d=Fb[c],e=b[d],"undefined"!=typeof e&&(a[d]=e);return a}function n(a){return 0>a?Math.ceil(a):Math.floor(a)}function o(a,b,c){for(var d=""+Math.abs(a),e=a>=0;d.length<b;)d="0"+d;return(e?c?"+":"":"-")+d}function p(a,b){var c={milliseconds:0,months:0};return c.months=b.month()-a.month()+12*(b.year()-a.year()),a.clone().add(c.months,"M").isAfter(b)&&--c.months,c.milliseconds=+b-+a.clone().add(c.months,"M"),c}function q(a,b){var c;return b=J(b,a),a.isBefore(b)?c=p(a,b):(c=p(b,a),c.milliseconds=-c.milliseconds,c.months=-c.months),c}function r(a,b){return function(c,d){var e,g;return null===d||isNaN(+d)||(f(b,"moment()."+b+"(period, number) is deprecated. Please use moment()."+b+"(number, period)."),g=c,c=d,d=g),c="string"==typeof c?+c:c,e=rb.duration(c,d),s(this,e,a),this}}function s(a,b,c,d){var e=b._milliseconds,f=b._days,g=b._months;d=null==d?!0:d,e&&a._d.setTime(+a._d+e*c),f&&lb(a,"Date",kb(a,"Date")+f*c),g&&jb(a,kb(a,"Month")+g*c),d&&rb.updateOffset(a,f||g)}function t(a){return"[object Array]"===Object.prototype.toString.call(a)}function u(a){return"[object Date]"===Object.prototype.toString.call(a)||a instanceof Date}function v(a,b,c){var d,e=Math.min(a.length,b.length),f=Math.abs(a.length-b.length),g=0;for(d=0;e>d;d++)(c&&a[d]!==b[d]||!c&&z(a[d])!==z(b[d]))&&g++;return g+f}function w(a){if(a){var b=a.toLowerCase().replace(/(.)s$/,"$1");a=gc[a]||hc[b]||b}return a}function x(a){var b,c,d={};for(c in a)a.hasOwnProperty(c)&&(b=w(c),b&&(d[b]=a[c]));return d}function y(b){var c,d;if(0===b.indexOf("week"))c=7,d="day";else{if(0!==b.indexOf("month"))return;c=12,d="month"}rb[b]=function(e,f){var g,h,i=rb._locale[b],j=[];if("number"==typeof e&&(f=e,e=a),h=function(a){var b=rb().utc().set(d,a);return i.call(rb._locale,b,e||"")},null!=f)return h(f);for(g=0;c>g;g++)j.push(h(g));return j}}function z(a){var b=+a,c=0;return 0!==b&&isFinite(b)&&(c=b>=0?Math.floor(b):Math.ceil(b)),c}function A(a,b){return new Date(Date.UTC(a,b+1,0)).getUTCDate()}function B(a,b,c){return fb(rb([a,11,31+b-c]),b,c).week}function C(a){return D(a)?366:365}function D(a){return a%4===0&&a%100!==0||a%400===0}function E(a){var b;a._a&&-2===a._pf.overflow&&(b=a._a[yb]<0||a._a[yb]>11?yb:a._a[zb]<1||a._a[zb]>A(a._a[xb],a._a[yb])?zb:a._a[Ab]<0||a._a[Ab]>23?Ab:a._a[Bb]<0||a._a[Bb]>59?Bb:a._a[Cb]<0||a._a[Cb]>59?Cb:a._a[Db]<0||a._a[Db]>999?Db:-1,a._pf._overflowDayOfYear&&(xb>b||b>zb)&&(b=zb),a._pf.overflow=b)}function F(a){return null==a._isValid&&(a._isValid=!isNaN(a._d.getTime())&&a._pf.overflow<0&&!a._pf.empty&&!a._pf.invalidMonth&&!a._pf.nullInput&&!a._pf.invalidFormat&&!a._pf.userInvalidated,a._strict&&(a._isValid=a._isValid&&0===a._pf.charsLeftOver&&0===a._pf.unusedTokens.length)),a._isValid}function G(a){return a?a.toLowerCase().replace("_","-"):a}function H(a){for(var b,c,d,e,f=0;f<a.length;){for(e=G(a[f]).split("-"),b=e.length,c=G(a[f+1]),c=c?c.split("-"):null;b>0;){if(d=I(e.slice(0,b).join("-")))return d;if(c&&c.length>=b&&v(e,c,!0)>=b-1)break;b--}f++}return null}function I(a){var b=null;if(!Eb[a]&&Gb)try{b=rb.locale(),require("./locale/"+a),rb.locale(b)}catch(c){}return Eb[a]}function J(a,b){return b._isUTC?rb(a).zone(b._offset||0):rb(a).local()}function K(a){return a.match(/\[[\s\S]/)?a.replace(/^\[|\]$/g,""):a.replace(/\\/g,"")}function L(a){var b,c,d=a.match(Kb);for(b=0,c=d.length;c>b;b++)d[b]=mc[d[b]]?mc[d[b]]:K(d[b]);return function(e){var f="";for(b=0;c>b;b++)f+=d[b]instanceof Function?d[b].call(e,a):d[b];return f}}function M(a,b){return a.isValid()?(b=N(b,a.localeData()),ic[b]||(ic[b]=L(b)),ic[b](a)):a.localeData().invalidDate()}function N(a,b){function c(a){return b.longDateFormat(a)||a}var d=5;for(Lb.lastIndex=0;d>=0&&Lb.test(a);)a=a.replace(Lb,c),Lb.lastIndex=0,d-=1;return a}function O(a,b){var c,d=b._strict;switch(a){case"Q":return Wb;case"DDDD":return Yb;case"YYYY":case"GGGG":case"gggg":return d?Zb:Ob;case"Y":case"G":case"g":return _b;case"YYYYYY":case"YYYYY":case"GGGGG":case"ggggg":return d?$b:Pb;case"S":if(d)return Wb;case"SS":if(d)return Xb;case"SSS":if(d)return Yb;case"DDD":return Nb;case"MMM":case"MMMM":case"dd":case"ddd":case"dddd":return Rb;case"a":case"A":return b._locale._meridiemParse;case"X":return Ub;case"Z":case"ZZ":return Sb;case"T":return Tb;case"SSSS":return Qb;case"MM":case"DD":case"YY":case"GG":case"gg":case"HH":case"hh":case"mm":case"ss":case"ww":case"WW":return d?Xb:Mb;case"M":case"D":case"d":case"H":case"h":case"m":case"s":case"w":case"W":case"e":case"E":return Mb;case"Do":return Vb;default:return c=new RegExp(X(W(a.replace("\\","")),"i"))}}function P(a){a=a||"";var b=a.match(Sb)||[],c=b[b.length-1]||[],d=(c+"").match(ec)||["-",0,0],e=+(60*d[1])+z(d[2]);return"+"===d[0]?-e:e}function Q(a,b,c){var d,e=c._a;switch(a){case"Q":null!=b&&(e[yb]=3*(z(b)-1));break;case"M":case"MM":null!=b&&(e[yb]=z(b)-1);break;case"MMM":case"MMMM":d=c._locale.monthsParse(b),null!=d?e[yb]=d:c._pf.invalidMonth=b;break;case"D":case"DD":null!=b&&(e[zb]=z(b));break;case"Do":null!=b&&(e[zb]=z(parseInt(b,10)));break;case"DDD":case"DDDD":null!=b&&(c._dayOfYear=z(b));break;case"YY":e[xb]=rb.parseTwoDigitYear(b);break;case"YYYY":case"YYYYY":case"YYYYYY":e[xb]=z(b);break;case"a":case"A":c._isPm=c._locale.isPM(b);break;case"H":case"HH":case"h":case"hh":e[Ab]=z(b);break;case"m":case"mm":e[Bb]=z(b);break;case"s":case"ss":e[Cb]=z(b);break;case"S":case"SS":case"SSS":case"SSSS":e[Db]=z(1e3*("0."+b));break;case"X":c._d=new Date(1e3*parseFloat(b));break;case"Z":case"ZZ":c._useUTC=!0,c._tzm=P(b);break;case"dd":case"ddd":case"dddd":d=c._locale.weekdaysParse(b),null!=d?(c._w=c._w||{},c._w.d=d):c._pf.invalidWeekday=b;break;case"w":case"ww":case"W":case"WW":case"d":case"e":case"E":a=a.substr(0,1);case"gggg":case"GGGG":case"GGGGG":a=a.substr(0,2),b&&(c._w=c._w||{},c._w[a]=z(b));break;case"gg":case"GG":c._w=c._w||{},c._w[a]=rb.parseTwoDigitYear(b)}}function R(a){var c,d,e,f,g,h,i;c=a._w,null!=c.GG||null!=c.W||null!=c.E?(g=1,h=4,d=b(c.GG,a._a[xb],fb(rb(),1,4).year),e=b(c.W,1),f=b(c.E,1)):(g=a._locale._week.dow,h=a._locale._week.doy,d=b(c.gg,a._a[xb],fb(rb(),g,h).year),e=b(c.w,1),null!=c.d?(f=c.d,g>f&&++e):f=null!=c.e?c.e+g:g),i=gb(d,e,f,h,g),a._a[xb]=i.year,a._dayOfYear=i.dayOfYear}function S(a){var c,d,e,f,g=[];if(!a._d){for(e=U(a),a._w&&null==a._a[zb]&&null==a._a[yb]&&R(a),a._dayOfYear&&(f=b(a._a[xb],e[xb]),a._dayOfYear>C(f)&&(a._pf._overflowDayOfYear=!0),d=bb(f,0,a._dayOfYear),a._a[yb]=d.getUTCMonth(),a._a[zb]=d.getUTCDate()),c=0;3>c&&null==a._a[c];++c)a._a[c]=g[c]=e[c];for(;7>c;c++)a._a[c]=g[c]=null==a._a[c]?2===c?1:0:a._a[c];a._d=(a._useUTC?bb:ab).apply(null,g),null!=a._tzm&&a._d.setUTCMinutes(a._d.getUTCMinutes()+a._tzm)}}function T(a){var b;a._d||(b=x(a._i),a._a=[b.year,b.month,b.day,b.hour,b.minute,b.second,b.millisecond],S(a))}function U(a){var b=new Date;return a._useUTC?[b.getUTCFullYear(),b.getUTCMonth(),b.getUTCDate()]:[b.getFullYear(),b.getMonth(),b.getDate()]}function V(a){if(a._f===rb.ISO_8601)return void Z(a);a._a=[],a._pf.empty=!0;var b,c,d,e,f,g=""+a._i,h=g.length,i=0;for(d=N(a._f,a._locale).match(Kb)||[],b=0;b<d.length;b++)e=d[b],c=(g.match(O(e,a))||[])[0],c&&(f=g.substr(0,g.indexOf(c)),f.length>0&&a._pf.unusedInput.push(f),g=g.slice(g.indexOf(c)+c.length),i+=c.length),mc[e]?(c?a._pf.empty=!1:a._pf.unusedTokens.push(e),Q(e,c,a)):a._strict&&!c&&a._pf.unusedTokens.push(e);a._pf.charsLeftOver=h-i,g.length>0&&a._pf.unusedInput.push(g),a._isPm&&a._a[Ab]<12&&(a._a[Ab]+=12),a._isPm===!1&&12===a._a[Ab]&&(a._a[Ab]=0),S(a),E(a)}function W(a){return a.replace(/\\(\[)|\\(\])|\[([^\]\[]*)\]|\\(.)/g,function(a,b,c,d,e){return b||c||d||e})}function X(a){return a.replace(/[-\/\\^$*+?.()|[\]{}]/g,"\\$&")}function Y(a){var b,d,e,f,g;if(0===a._f.length)return a._pf.invalidFormat=!0,void(a._d=new Date(0/0));for(f=0;f<a._f.length;f++)g=0,b=m({},a),b._pf=c(),b._f=a._f[f],V(b),F(b)&&(g+=b._pf.charsLeftOver,g+=10*b._pf.unusedTokens.length,b._pf.score=g,(null==e||e>g)&&(e=g,d=b));l(a,d||b)}function Z(a){var b,c,d=a._i,e=ac.exec(d);if(e){for(a._pf.iso=!0,b=0,c=cc.length;c>b;b++)if(cc[b][1].exec(d)){a._f=cc[b][0]+(e[6]||" ");break}for(b=0,c=dc.length;c>b;b++)if(dc[b][1].exec(d)){a._f+=dc[b][0];break}d.match(Sb)&&(a._f+="Z"),V(a)}else a._isValid=!1}function $(a){Z(a),a._isValid===!1&&(delete a._isValid,rb.createFromInputFallback(a))}function _(b){var c,d=b._i;d===a?b._d=new Date:u(d)?b._d=new Date(+d):null!==(c=Hb.exec(d))?b._d=new Date(+c[1]):"string"==typeof d?$(b):t(d)?(b._a=d.slice(0),S(b)):"object"==typeof d?T(b):"number"==typeof d?b._d=new Date(d):rb.createFromInputFallback(b)}function ab(a,b,c,d,e,f,g){var h=new Date(a,b,c,d,e,f,g);return 1970>a&&h.setFullYear(a),h}function bb(a){var b=new Date(Date.UTC.apply(null,arguments));return 1970>a&&b.setUTCFullYear(a),b}function cb(a,b){if("string"==typeof a)if(isNaN(a)){if(a=b.weekdaysParse(a),"number"!=typeof a)return null}else a=parseInt(a,10);return a}function db(a,b,c,d,e){return e.relativeTime(b||1,!!c,a,d)}function eb(a,b,c){var d=rb.duration(a).abs(),e=wb(d.as("s")),f=wb(d.as("m")),g=wb(d.as("h")),h=wb(d.as("d")),i=wb(d.as("M")),j=wb(d.as("y")),k=e<jc.s&&["s",e]||1===f&&["m"]||f<jc.m&&["mm",f]||1===g&&["h"]||g<jc.h&&["hh",g]||1===h&&["d"]||h<jc.d&&["dd",h]||1===i&&["M"]||i<jc.M&&["MM",i]||1===j&&["y"]||["yy",j];return k[2]=b,k[3]=+a>0,k[4]=c,db.apply({},k)}function fb(a,b,c){var d,e=c-b,f=c-a.day();return f>e&&(f-=7),e-7>f&&(f+=7),d=rb(a).add(f,"d"),{week:Math.ceil(d.dayOfYear()/7),year:d.year()}}function gb(a,b,c,d,e){var f,g,h=bb(a,0,1).getUTCDay();return h=0===h?7:h,c=null!=c?c:e,f=e-h+(h>d?7:0)-(e>h?7:0),g=7*(b-1)+(c-e)+f+1,{year:g>0?a:a-1,dayOfYear:g>0?g:C(a-1)+g}}function hb(b){var c=b._i,d=b._f;return b._locale=b._locale||rb.localeData(b._l),null===c||d===a&&""===c?rb.invalid({nullInput:!0}):("string"==typeof c&&(b._i=c=b._locale.preparse(c)),rb.isMoment(c)?new j(c,!0):(d?t(d)?Y(b):V(b):_(b),new j(b)))}function ib(a,b){var c,d;if(1===b.length&&t(b[0])&&(b=b[0]),!b.length)return rb();for(c=b[0],d=1;d<b.length;++d)b[d][a](c)&&(c=b[d]);return c}function jb(a,b){var c;return"string"==typeof b&&(b=a.localeData().monthsParse(b),"number"!=typeof b)?a:(c=Math.min(a.date(),A(a.year(),b)),a._d["set"+(a._isUTC?"UTC":"")+"Month"](b,c),a)}function kb(a,b){return a._d["get"+(a._isUTC?"UTC":"")+b]()}function lb(a,b,c){return"Month"===b?jb(a,c):a._d["set"+(a._isUTC?"UTC":"")+b](c)}function mb(a,b){return function(c){return null!=c?(lb(this,a,c),rb.updateOffset(this,b),this):kb(this,a)}}function nb(a){return 400*a/146097}function ob(a){return 146097*a/400}function pb(a){rb.duration.fn[a]=function(){return this._data[a]}}function qb(a){"undefined"==typeof ender&&(sb=vb.moment,vb.moment=a?e("Accessing Moment through the global scope is deprecated, and will be removed in an upcoming release.",rb):rb)}for(var rb,sb,tb,ub="2.8.1",vb="undefined"!=typeof global?global:this,wb=Math.round,xb=0,yb=1,zb=2,Ab=3,Bb=4,Cb=5,Db=6,Eb={},Fb=[],Gb="undefined"!=typeof module&&module.exports,Hb=/^\/?Date\((\-?\d+)/i,Ib=/(\-)?(?:(\d*)\.)?(\d+)\:(\d+)(?:\:(\d+)\.?(\d{3})?)?/,Jb=/^(-)?P(?:(?:([0-9,.]*)Y)?(?:([0-9,.]*)M)?(?:([0-9,.]*)D)?(?:T(?:([0-9,.]*)H)?(?:([0-9,.]*)M)?(?:([0-9,.]*)S)?)?|([0-9,.]*)W)$/,Kb=/(\[[^\[]*\])|(\\)?(Mo|MM?M?M?|Do|DDDo|DD?D?D?|ddd?d?|do?|w[o|w]?|W[o|W]?|Q|YYYYYY|YYYYY|YYYY|YY|gg(ggg?)?|GG(GGG?)?|e|E|a|A|hh?|HH?|mm?|ss?|S{1,4}|X|zz?|ZZ?|.)/g,Lb=/(\[[^\[]*\])|(\\)?(LT|LL?L?L?|l{1,4})/g,Mb=/\d\d?/,Nb=/\d{1,3}/,Ob=/\d{1,4}/,Pb=/[+\-]?\d{1,6}/,Qb=/\d+/,Rb=/[0-9]*['a-z\u00A0-\u05FF\u0700-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]+|[\u0600-\u06FF\/]+(\s*?[\u0600-\u06FF]+){1,2}/i,Sb=/Z|[\+\-]\d\d:?\d\d/gi,Tb=/T/i,Ub=/[\+\-]?\d+(\.\d{1,3})?/,Vb=/\d{1,2}/,Wb=/\d/,Xb=/\d\d/,Yb=/\d{3}/,Zb=/\d{4}/,$b=/[+-]?\d{6}/,_b=/[+-]?\d+/,ac=/^\s*(?:[+-]\d{6}|\d{4})-(?:(\d\d-\d\d)|(W\d\d$)|(W\d\d-\d)|(\d\d\d))((T| )(\d\d(:\d\d(:\d\d(\.\d+)?)?)?)?([\+\-]\d\d(?::?\d\d)?|\s*Z)?)?$/,bc="YYYY-MM-DDTHH:mm:ssZ",cc=[["YYYYYY-MM-DD",/[+-]\d{6}-\d{2}-\d{2}/],["YYYY-MM-DD",/\d{4}-\d{2}-\d{2}/],["GGGG-[W]WW-E",/\d{4}-W\d{2}-\d/],["GGGG-[W]WW",/\d{4}-W\d{2}/],["YYYY-DDD",/\d{4}-\d{3}/]],dc=[["HH:mm:ss.SSSS",/(T| )\d\d:\d\d:\d\d\.\d+/],["HH:mm:ss",/(T| )\d\d:\d\d:\d\d/],["HH:mm",/(T| )\d\d:\d\d/],["HH",/(T| )\d\d/]],ec=/([\+\-]|\d\d)/gi,fc=("Date|Hours|Minutes|Seconds|Milliseconds".split("|"),{Milliseconds:1,Seconds:1e3,Minutes:6e4,Hours:36e5,Days:864e5,Months:2592e6,Years:31536e6}),gc={ms:"millisecond",s:"second",m:"minute",h:"hour",d:"day",D:"date",w:"week",W:"isoWeek",M:"month",Q:"quarter",y:"year",DDD:"dayOfYear",e:"weekday",E:"isoWeekday",gg:"weekYear",GG:"isoWeekYear"},hc={dayofyear:"dayOfYear",isoweekday:"isoWeekday",isoweek:"isoWeek",weekyear:"weekYear",isoweekyear:"isoWeekYear"},ic={},jc={s:45,m:45,h:22,d:26,M:11},kc="DDD w W M D d".split(" "),lc="M D H h m s w W".split(" "),mc={M:function(){return this.month()+1},MMM:function(a){return this.localeData().monthsShort(this,a)},MMMM:function(a){return this.localeData().months(this,a)},D:function(){return this.date()},DDD:function(){return this.dayOfYear()},d:function(){return this.day()},dd:function(a){return this.localeData().weekdaysMin(this,a)},ddd:function(a){return this.localeData().weekdaysShort(this,a)},dddd:function(a){return this.localeData().weekdays(this,a)},w:function(){return this.week()},W:function(){return this.isoWeek()},YY:function(){return o(this.year()%100,2)},YYYY:function(){return o(this.year(),4)},YYYYY:function(){return o(this.year(),5)},YYYYYY:function(){var a=this.year(),b=a>=0?"+":"-";return b+o(Math.abs(a),6)},gg:function(){return o(this.weekYear()%100,2)},gggg:function(){return o(this.weekYear(),4)},ggggg:function(){return o(this.weekYear(),5)},GG:function(){return o(this.isoWeekYear()%100,2)},GGGG:function(){return o(this.isoWeekYear(),4)},GGGGG:function(){return o(this.isoWeekYear(),5)},e:function(){return this.weekday()},E:function(){return this.isoWeekday()},a:function(){return this.localeData().meridiem(this.hours(),this.minutes(),!0)},A:function(){return this.localeData().meridiem(this.hours(),this.minutes(),!1)},H:function(){return this.hours()},h:function(){return this.hours()%12||12},m:function(){return this.minutes()},s:function(){return this.seconds()},S:function(){return z(this.milliseconds()/100)},SS:function(){return o(z(this.milliseconds()/10),2)},SSS:function(){return o(this.milliseconds(),3)},SSSS:function(){return o(this.milliseconds(),3)},Z:function(){var a=-this.zone(),b="+";return 0>a&&(a=-a,b="-"),b+o(z(a/60),2)+":"+o(z(a)%60,2)},ZZ:function(){var a=-this.zone(),b="+";return 0>a&&(a=-a,b="-"),b+o(z(a/60),2)+o(z(a)%60,2)},z:function(){return this.zoneAbbr()},zz:function(){return this.zoneName()},X:function(){return this.unix()},Q:function(){return this.quarter()}},nc={},oc=["months","monthsShort","weekdays","weekdaysShort","weekdaysMin"];kc.length;)tb=kc.pop(),mc[tb+"o"]=h(mc[tb],tb);for(;lc.length;)tb=lc.pop(),mc[tb+tb]=g(mc[tb],2);mc.DDDD=g(mc.DDD,3),l(i.prototype,{set:function(a){var b,c;for(c in a)b=a[c],"function"==typeof b?this[c]=b:this["_"+c]=b},_months:"January_February_March_April_May_June_July_August_September_October_November_December".split("_"),months:function(a){return this._months[a.month()]},_monthsShort:"Jan_Feb_Mar_Apr_May_Jun_Jul_Aug_Sep_Oct_Nov_Dec".split("_"),monthsShort:function(a){return this._monthsShort[a.month()]},monthsParse:function(a){var b,c,d;for(this._monthsParse||(this._monthsParse=[]),b=0;12>b;b++)if(this._monthsParse[b]||(c=rb.utc([2e3,b]),d="^"+this.months(c,"")+"|^"+this.monthsShort(c,""),this._monthsParse[b]=new RegExp(d.replace(".",""),"i")),this._monthsParse[b].test(a))return b},_weekdays:"Sunday_Monday_Tuesday_Wednesday_Thursday_Friday_Saturday".split("_"),weekdays:function(a){return this._weekdays[a.day()]},_weekdaysShort:"Sun_Mon_Tue_Wed_Thu_Fri_Sat".split("_"),weekdaysShort:function(a){return this._weekdaysShort[a.day()]},_weekdaysMin:"Su_Mo_Tu_We_Th_Fr_Sa".split("_"),weekdaysMin:function(a){return this._weekdaysMin[a.day()]},weekdaysParse:function(a){var b,c,d;for(this._weekdaysParse||(this._weekdaysParse=[]),b=0;7>b;b++)if(this._weekdaysParse[b]||(c=rb([2e3,1]).day(b),d="^"+this.weekdays(c,"")+"|^"+this.weekdaysShort(c,"")+"|^"+this.weekdaysMin(c,""),this._weekdaysParse[b]=new RegExp(d.replace(".",""),"i")),this._weekdaysParse[b].test(a))return b},_longDateFormat:{LT:"h:mm A",L:"MM/DD/YYYY",LL:"MMMM D, YYYY",LLL:"MMMM D, YYYY LT",LLLL:"dddd, MMMM D, YYYY LT"},longDateFormat:function(a){var b=this._longDateFormat[a];return!b&&this._longDateFormat[a.toUpperCase()]&&(b=this._longDateFormat[a.toUpperCase()].replace(/MMMM|MM|DD|dddd/g,function(a){return a.slice(1)}),this._longDateFormat[a]=b),b},isPM:function(a){return"p"===(a+"").toLowerCase().charAt(0)},_meridiemParse:/[ap]\.?m?\.?/i,meridiem:function(a,b,c){return a>11?c?"pm":"PM":c?"am":"AM"},_calendar:{sameDay:"[Today at] LT",nextDay:"[Tomorrow at] LT",nextWeek:"dddd [at] LT",lastDay:"[Yesterday at] LT",lastWeek:"[Last] dddd [at] LT",sameElse:"L"},calendar:function(a,b){var c=this._calendar[a];return"function"==typeof c?c.apply(b):c},_relativeTime:{future:"in %s",past:"%s ago",s:"a few seconds",m:"a minute",mm:"%d minutes",h:"an hour",hh:"%d hours",d:"a day",dd:"%d days",M:"a month",MM:"%d months",y:"a year",yy:"%d years"},relativeTime:function(a,b,c,d){var e=this._relativeTime[c];return"function"==typeof e?e(a,b,c,d):e.replace(/%d/i,a)},pastFuture:function(a,b){var c=this._relativeTime[a>0?"future":"past"];return"function"==typeof c?c(b):c.replace(/%s/i,b)},ordinal:function(a){return this._ordinal.replace("%d",a)},_ordinal:"%d",preparse:function(a){return a},postformat:function(a){return a},week:function(a){return fb(a,this._week.dow,this._week.doy).week},_week:{dow:0,doy:6},_invalidDate:"Invalid date",invalidDate:function(){return this._invalidDate}}),rb=function(b,d,e,f){var g;return"boolean"==typeof e&&(f=e,e=a),g={},g._isAMomentObject=!0,g._i=b,g._f=d,g._l=e,g._strict=f,g._isUTC=!1,g._pf=c(),hb(g)},rb.suppressDeprecationWarnings=!1,rb.createFromInputFallback=e("moment construction falls back to js Date. This is discouraged and will be removed in upcoming major release. Please refer to https://github.com/moment/moment/issues/1407 for more info.",function(a){a._d=new Date(a._i)}),rb.min=function(){var a=[].slice.call(arguments,0);return ib("isBefore",a)},rb.max=function(){var a=[].slice.call(arguments,0);return ib("isAfter",a)},rb.utc=function(b,d,e,f){var g;return"boolean"==typeof e&&(f=e,e=a),g={},g._isAMomentObject=!0,g._useUTC=!0,g._isUTC=!0,g._l=e,g._i=b,g._f=d,g._strict=f,g._pf=c(),hb(g).utc()},rb.unix=function(a){return rb(1e3*a)},rb.duration=function(a,b){var c,d,e,f,g=a,h=null;return rb.isDuration(a)?g={ms:a._milliseconds,d:a._days,M:a._months}:"number"==typeof a?(g={},b?g[b]=a:g.milliseconds=a):(h=Ib.exec(a))?(c="-"===h[1]?-1:1,g={y:0,d:z(h[zb])*c,h:z(h[Ab])*c,m:z(h[Bb])*c,s:z(h[Cb])*c,ms:z(h[Db])*c}):(h=Jb.exec(a))?(c="-"===h[1]?-1:1,e=function(a){var b=a&&parseFloat(a.replace(",","."));return(isNaN(b)?0:b)*c},g={y:e(h[2]),M:e(h[3]),d:e(h[4]),h:e(h[5]),m:e(h[6]),s:e(h[7]),w:e(h[8])}):"object"==typeof g&&("from"in g||"to"in g)&&(f=q(rb(g.from),rb(g.to)),g={},g.ms=f.milliseconds,g.M=f.months),d=new k(g),rb.isDuration(a)&&a.hasOwnProperty("_locale")&&(d._locale=a._locale),d},rb.version=ub,rb.defaultFormat=bc,rb.ISO_8601=function(){},rb.momentProperties=Fb,rb.updateOffset=function(){},rb.relativeTimeThreshold=function(b,c){return jc[b]===a?!1:c===a?jc[b]:(jc[b]=c,!0)},rb.lang=e("moment.lang is deprecated. Use moment.locale instead.",function(a,b){return rb.locale(a,b)}),rb.locale=function(a,b){var c;return a&&(c="undefined"!=typeof b?rb.defineLocale(a,b):rb.localeData(a),c&&(rb.duration._locale=rb._locale=c)),rb._locale._abbr},rb.defineLocale=function(a,b){return null!==b?(b.abbr=a,Eb[a]||(Eb[a]=new i),Eb[a].set(b),rb.locale(a),Eb[a]):(delete Eb[a],null)},rb.langData=e("moment.langData is deprecated. Use moment.localeData instead.",function(a){return rb.localeData(a)}),rb.localeData=function(a){var b;if(a&&a._locale&&a._locale._abbr&&(a=a._locale._abbr),!a)return rb._locale;if(!t(a)){if(b=I(a))return b;a=[a]}return H(a)},rb.isMoment=function(a){return a instanceof j||null!=a&&a.hasOwnProperty("_isAMomentObject")},rb.isDuration=function(a){return a instanceof k};for(tb=oc.length-1;tb>=0;--tb)y(oc[tb]);rb.normalizeUnits=function(a){return w(a)},rb.invalid=function(a){var b=rb.utc(0/0);return null!=a?l(b._pf,a):b._pf.userInvalidated=!0,b},rb.parseZone=function(){return rb.apply(null,arguments).parseZone()},rb.parseTwoDigitYear=function(a){return z(a)+(z(a)>68?1900:2e3)},l(rb.fn=j.prototype,{clone:function(){return rb(this)},valueOf:function(){return+this._d+6e4*(this._offset||0)},unix:function(){return Math.floor(+this/1e3)},toString:function(){return this.clone().locale("en").format("ddd MMM DD YYYY HH:mm:ss [GMT]ZZ")},toDate:function(){return this._offset?new Date(+this):this._d},toISOString:function(){var a=rb(this).utc();return 0<a.year()&&a.year()<=9999?M(a,"YYYY-MM-DD[T]HH:mm:ss.SSS[Z]"):M(a,"YYYYYY-MM-DD[T]HH:mm:ss.SSS[Z]")},toArray:function(){var a=this;return[a.year(),a.month(),a.date(),a.hours(),a.minutes(),a.seconds(),a.milliseconds()]},isValid:function(){return F(this)},isDSTShifted:function(){return this._a?this.isValid()&&v(this._a,(this._isUTC?rb.utc(this._a):rb(this._a)).toArray())>0:!1},parsingFlags:function(){return l({},this._pf)},invalidAt:function(){return this._pf.overflow},utc:function(a){return this.zone(0,a)},local:function(a){return this._isUTC&&(this.zone(0,a),this._isUTC=!1,a&&this.add(this._d.getTimezoneOffset(),"m")),this},format:function(a){var b=M(this,a||rb.defaultFormat);return this.localeData().postformat(b)},add:r(1,"add"),subtract:r(-1,"subtract"),diff:function(a,b,c){var d,e,f=J(a,this),g=6e4*(this.zone()-f.zone());return b=w(b),"year"===b||"month"===b?(d=432e5*(this.daysInMonth()+f.daysInMonth()),e=12*(this.year()-f.year())+(this.month()-f.month()),e+=(this-rb(this).startOf("month")-(f-rb(f).startOf("month")))/d,e-=6e4*(this.zone()-rb(this).startOf("month").zone()-(f.zone()-rb(f).startOf("month").zone()))/d,"year"===b&&(e/=12)):(d=this-f,e="second"===b?d/1e3:"minute"===b?d/6e4:"hour"===b?d/36e5:"day"===b?(d-g)/864e5:"week"===b?(d-g)/6048e5:d),c?e:n(e)},from:function(a,b){return rb.duration({to:this,from:a}).locale(this.locale()).humanize(!b)},fromNow:function(a){return this.from(rb(),a)},calendar:function(a){var b=a||rb(),c=J(b,this).startOf("day"),d=this.diff(c,"days",!0),e=-6>d?"sameElse":-1>d?"lastWeek":0>d?"lastDay":1>d?"sameDay":2>d?"nextDay":7>d?"nextWeek":"sameElse";return this.format(this.localeData().calendar(e,this))},isLeapYear:function(){return D(this.year())},isDST:function(){return this.zone()<this.clone().month(0).zone()||this.zone()<this.clone().month(5).zone()},day:function(a){var b=this._isUTC?this._d.getUTCDay():this._d.getDay();return null!=a?(a=cb(a,this.localeData()),this.add(a-b,"d")):b},month:mb("Month",!0),startOf:function(a){switch(a=w(a)){case"year":this.month(0);case"quarter":case"month":this.date(1);case"week":case"isoWeek":case"day":this.hours(0);case"hour":this.minutes(0);case"minute":this.seconds(0);case"second":this.milliseconds(0)}return"week"===a?this.weekday(0):"isoWeek"===a&&this.isoWeekday(1),"quarter"===a&&this.month(3*Math.floor(this.month()/3)),this},endOf:function(a){return a=w(a),this.startOf(a).add(1,"isoWeek"===a?"week":a).subtract(1,"ms")},isAfter:function(a,b){return b="undefined"!=typeof b?b:"millisecond",+this.clone().startOf(b)>+rb(a).startOf(b)},isBefore:function(a,b){return b="undefined"!=typeof b?b:"millisecond",+this.clone().startOf(b)<+rb(a).startOf(b)},isSame:function(a,b){return b=b||"ms",+this.clone().startOf(b)===+J(a,this).startOf(b)},min:e("moment().min is deprecated, use moment.min instead. https://github.com/moment/moment/issues/1548",function(a){return a=rb.apply(null,arguments),this>a?this:a}),max:e("moment().max is deprecated, use moment.max instead. https://github.com/moment/moment/issues/1548",function(a){return a=rb.apply(null,arguments),a>this?this:a}),zone:function(a,b){var c,d=this._offset||0;return null==a?this._isUTC?d:this._d.getTimezoneOffset():("string"==typeof a&&(a=P(a)),Math.abs(a)<16&&(a=60*a),!this._isUTC&&b&&(c=this._d.getTimezoneOffset()),this._offset=a,this._isUTC=!0,null!=c&&this.subtract(c,"m"),d!==a&&(!b||this._changeInProgress?s(this,rb.duration(d-a,"m"),1,!1):this._changeInProgress||(this._changeInProgress=!0,rb.updateOffset(this,!0),this._changeInProgress=null)),this)},zoneAbbr:function(){return this._isUTC?"UTC":""},zoneName:function(){return this._isUTC?"Coordinated Universal Time":""},parseZone:function(){return this._tzm?this.zone(this._tzm):"string"==typeof this._i&&this.zone(this._i),this},hasAlignedHourOffset:function(a){return a=a?rb(a).zone():0,(this.zone()-a)%60===0},daysInMonth:function(){return A(this.year(),this.month())},dayOfYear:function(a){var b=wb((rb(this).startOf("day")-rb(this).startOf("year"))/864e5)+1;return null==a?b:this.add(a-b,"d")},quarter:function(a){return null==a?Math.ceil((this.month()+1)/3):this.month(3*(a-1)+this.month()%3)},weekYear:function(a){var b=fb(this,this.localeData()._week.dow,this.localeData()._week.doy).year;return null==a?b:this.add(a-b,"y")},isoWeekYear:function(a){var b=fb(this,1,4).year;return null==a?b:this.add(a-b,"y")},week:function(a){var b=this.localeData().week(this);return null==a?b:this.add(7*(a-b),"d")},isoWeek:function(a){var b=fb(this,1,4).week;return null==a?b:this.add(7*(a-b),"d")},weekday:function(a){var b=(this.day()+7-this.localeData()._week.dow)%7;return null==a?b:this.add(a-b,"d")},isoWeekday:function(a){return null==a?this.day()||7:this.day(this.day()%7?a:a-7)},isoWeeksInYear:function(){return B(this.year(),1,4)},weeksInYear:function(){var a=this.localeData()._week;return B(this.year(),a.dow,a.doy)},get:function(a){return a=w(a),this[a]()},set:function(a,b){return a=w(a),"function"==typeof this[a]&&this[a](b),this},locale:function(b){return b===a?this._locale._abbr:(this._locale=rb.localeData(b),this)},lang:e("moment().lang() is deprecated. Use moment().localeData() instead.",function(b){return b===a?this.localeData():(this._locale=rb.localeData(b),this)}),localeData:function(){return this._locale}}),rb.fn.millisecond=rb.fn.milliseconds=mb("Milliseconds",!1),rb.fn.second=rb.fn.seconds=mb("Seconds",!1),rb.fn.minute=rb.fn.minutes=mb("Minutes",!1),rb.fn.hour=rb.fn.hours=mb("Hours",!0),rb.fn.date=mb("Date",!0),rb.fn.dates=e("dates accessor is deprecated. Use date instead.",mb("Date",!0)),rb.fn.year=mb("FullYear",!0),rb.fn.years=e("years accessor is deprecated. Use year instead.",mb("FullYear",!0)),rb.fn.days=rb.fn.day,rb.fn.months=rb.fn.month,rb.fn.weeks=rb.fn.week,rb.fn.isoWeeks=rb.fn.isoWeek,rb.fn.quarters=rb.fn.quarter,rb.fn.toJSON=rb.fn.toISOString,l(rb.duration.fn=k.prototype,{_bubble:function(){var a,b,c,d=this._milliseconds,e=this._days,f=this._months,g=this._data,h=0;g.milliseconds=d%1e3,a=n(d/1e3),g.seconds=a%60,b=n(a/60),g.minutes=b%60,c=n(b/60),g.hours=c%24,e+=n(c/24),h=n(nb(e)),e-=n(ob(h)),f+=n(e/30),e%=30,h+=n(f/12),f%=12,g.days=e,g.months=f,g.years=h},abs:function(){return this._milliseconds=Math.abs(this._milliseconds),this._days=Math.abs(this._days),this._months=Math.abs(this._months),this._data.milliseconds=Math.abs(this._data.milliseconds),this._data.seconds=Math.abs(this._data.seconds),this._data.minutes=Math.abs(this._data.minutes),this._data.hours=Math.abs(this._data.hours),this._data.months=Math.abs(this._data.months),this._data.years=Math.abs(this._data.years),this},weeks:function(){return n(this.days()/7)},valueOf:function(){return this._milliseconds+864e5*this._days+this._months%12*2592e6+31536e6*z(this._months/12)},humanize:function(a){var b=eb(this,!a,this.localeData());return a&&(b=this.localeData().pastFuture(+this,b)),this.localeData().postformat(b)},add:function(a,b){var c=rb.duration(a,b);return this._milliseconds+=c._milliseconds,this._days+=c._days,this._months+=c._months,this._bubble(),this},subtract:function(a,b){var c=rb.duration(a,b);return this._milliseconds-=c._milliseconds,this._days-=c._days,this._months-=c._months,this._bubble(),this},get:function(a){return a=w(a),this[a.toLowerCase()+"s"]()},as:function(a){var b,c;if(a=w(a),b=this._days+this._milliseconds/864e5,"month"===a||"year"===a)return c=this._months+12*nb(b),"month"===a?c:c/12;switch(b+=ob(this._months/12),a){case"week":return b/7;case"day":return b;case"hour":return 24*b;case"minute":return 24*b*60;case"second":return 24*b*60*60;case"millisecond":return 24*b*60*60*1e3;default:throw new Error("Unknown unit "+a)}},lang:rb.fn.lang,locale:rb.fn.locale,toIsoString:e("toIsoString() is deprecated. Please use toISOString() instead (notice the capitals)",function(){return this.toISOString()}),toISOString:function(){var a=Math.abs(this.years()),b=Math.abs(this.months()),c=Math.abs(this.days()),d=Math.abs(this.hours()),e=Math.abs(this.minutes()),f=Math.abs(this.seconds()+this.milliseconds()/1e3);return this.asSeconds()?(this.asSeconds()<0?"-":"")+"P"+(a?a+"Y":"")+(b?b+"M":"")+(c?c+"D":"")+(d||e||f?"T":"")+(d?d+"H":"")+(e?e+"M":"")+(f?f+"S":""):"P0D"},localeData:function(){return this._locale}});for(tb in fc)fc.hasOwnProperty(tb)&&pb(tb.toLowerCase());rb.duration.fn.asMilliseconds=function(){return this.as("ms")},rb.duration.fn.asSeconds=function(){return this.as("s")},rb.duration.fn.asMinutes=function(){return this.as("m")},rb.duration.fn.asHours=function(){return this.as("h")},rb.duration.fn.asDays=function(){return this.as("d")},rb.duration.fn.asWeeks=function(){return this.as("weeks")},rb.duration.fn.asMonths=function(){return this.as("M")},rb.duration.fn.asYears=function(){return this.as("y")},rb.locale("en",{ordinal:function(a){var b=a%10,c=1===z(a%100/10)?"th":1===b?"st":2===b?"nd":3===b?"rd":"th";return a+c}}),Gb?module.exports=rb:"function"==typeof define&&define.amd?(define("moment",function(a,b,c){return c.config&&c.config()&&c.config().noGlobal===!0&&(vb.moment=sb),rb}),qb(!0)):qb()}).call(this);;(function(root, factory) {

  if (typeof define === 'function' && define.amd) {
    define(['moment', 'jquery', 'exports'], function(momentjs, $, exports) {
      root.daterangepicker = factory(root, exports, momentjs, $);
    });

  } else if (typeof exports !== 'undefined') {
    var momentjs = require('moment');
    var jQuery;
    try {
      jQuery = require('jquery');
    } catch (err) {
      jQuery = window.jQuery;
      if (!jQuery) throw new Error('jQuery dependency not found');
    }

    factory(root, exports, momentjs, jQuery);

  // Finally, as a browser global.
  } else {
    root.daterangepicker = factory(root, {}, root.moment, (root.jQuery || root.Zepto || root.ender || root.$));
  }

}(this, function(root, daterangepicker, moment, $) {
    //local cn
    moment.locale('cn', {
        monthsShort : [
            "一月", "二月", "三月", "四月", "五月", "六月",
            "七月", "八月", "九月", "十月", "十一月", "十二月"
        ],
        weekdaysMin:["日", "一", "二", "三", "四", "五", "六"]
    });
    var DateRangePicker = function (element, options, cb) {

        // by default, the daterangepicker element is placed at the bottom of HTML body
        this.parentEl = 'body';

        //element that triggered the date range picker
        this.element = $(element);

        //tracks visible state
        this.isShowing = false;

        //create the picker HTML object
        var DRPTemplate = '<div class="daterangepicker dropdown-menu">' +
                '<div class="calendar left"></div>' +
                '<div class="calendar right"></div>' +
                '<div class="ranges">' +
                  '<div class="range_inputs">' +
                    '<div class="daterangepicker_start_input">' +
                      '<label for="daterangepicker_start"></label>' +
                      '<input class="input-mini" type="text" name="daterangepicker_start" value="" />' +
                    '</div>' +
                    '<div class="daterangepicker_end_input">' +
                      '<label for="daterangepicker_end"></label>' +
                      '<input class="input-mini" type="text" name="daterangepicker_end" value="" />' +
                    '</div>' +
                    '<button class="applyBtn" disabled="disabled"></button>&nbsp;' +
                    '<button class="cancelBtn"></button>' +
                  '</div>' +
                '</div>' +
              '</div>';

        //custom options
        if (typeof options !== 'object' || options === null)
            options = {};

        this.parentEl = (typeof options === 'object' && options.parentEl && $(options.parentEl).length) ? $(options.parentEl) : $(this.parentEl);
        this.container = $(DRPTemplate).appendTo(this.parentEl);

        this.setOptions(options, cb);

        //apply CSS classes and labels to buttons
        var c = this.container;
        $.each(this.buttonClasses, function (idx, val) {
            c.find('button').addClass(val);
        });
        this.container.find('.daterangepicker_start_input label').html(this.locale.fromLabel);
        this.container.find('.daterangepicker_end_input label').html(this.locale.toLabel);
        if (this.applyClass.length)
            this.container.find('.applyBtn').addClass(this.applyClass);
        if (this.cancelClass.length)
            this.container.find('.cancelBtn').addClass(this.cancelClass);
        this.container.find('.applyBtn').html(this.locale.applyLabel);
        this.container.find('.cancelBtn').html(this.locale.cancelLabel);

        //event listeners

        this.container.find('.calendar')
            .on('click.daterangepicker', '.prev', $.proxy(this.clickPrev, this))
            .on('click.daterangepicker', '.next', $.proxy(this.clickNext, this))
            .on('click.daterangepicker', 'td.available', $.proxy(this.clickDate, this))
            .on('mouseenter.daterangepicker', 'td.available', $.proxy(this.hoverDate, this))
            .on('mouseleave.daterangepicker', 'td.available', $.proxy(this.updateFormInputs, this))
            .on('change.daterangepicker', 'select.yearselect', $.proxy(this.updateMonthYear, this))
            .on('change.daterangepicker', 'select.monthselect', $.proxy(this.updateMonthYear, this))
            .on('change.daterangepicker', 'select.hourselect,select.minuteselect,select.ampmselect', $.proxy(this.updateTime, this));

        this.container.find('.ranges')
            .on('click.daterangepicker', 'button.applyBtn', $.proxy(this.clickApply, this))
            .on('click.daterangepicker', 'button.cancelBtn', $.proxy(this.clickCancel, this))
            .on('click.daterangepicker', '.daterangepicker_start_input,.daterangepicker_end_input', $.proxy(this.showCalendars, this))
            .on('change.daterangepicker', '.daterangepicker_start_input,.daterangepicker_end_input', $.proxy(this.inputsChanged, this))
            .on('keydown.daterangepicker', '.daterangepicker_start_input,.daterangepicker_end_input', $.proxy(this.inputsKeydown, this))
            .on('click.daterangepicker', 'li', $.proxy(this.clickRange, this))
            .on('mouseenter.daterangepicker', 'li', $.proxy(this.enterRange, this))
            .on('mouseleave.daterangepicker', 'li', $.proxy(this.updateFormInputs, this));

        if (this.element.is('input')) {
            this.element.on({
                'click.daterangepicker': $.proxy(this.show, this),
                'focus.daterangepicker': $.proxy(this.show, this),
                'keyup.daterangepicker': $.proxy(this.updateFromControl, this)
            });
        } else {
            this.element.on('click.daterangepicker', $.proxy(this.toggle, this));
        }

    };

    DateRangePicker.prototype = {

        constructor: DateRangePicker,

        setOptions: function(options, callback) {

            this.startDate = moment().startOf('day');
            this.endDate = moment().endOf('day');
            this.minDate = false;
            this.maxDate = false;
            this.dateLimit = false;

            this.showDropdowns = false;
            this.showWeekNumbers = false;
            this.timePicker = false;
            this.timePickerIncrement = 30;
            this.timePicker12Hour = true;
            this.singleDatePicker = false;
            this.ranges = {};

            this.opens = 'right';
            if (this.element.hasClass('pull-right'))
                this.opens = 'left';

            this.buttonClasses = ['btn', 'btn-small btn-sm'];
            this.applyClass = 'btn-success';
            this.cancelClass = 'btn-default';

            this.format = 'MM/DD/YYYY';
            this.separator = ' - ';

            this.locale = {
                applyLabel: 'Apply',
                cancelLabel: 'Cancel',
                fromLabel: 'From',
                toLabel: 'To',
                weekLabel: 'W',
                customRangeLabel: 'Custom Range',
                daysOfWeek: moment.weekdaysMin(),
                monthNames: moment.monthsShort(),
                firstDay: moment.localeData()._week.dow
            };

            this.cb = function () { };

            if (typeof options.format === 'string')
                this.format = options.format;

            if (typeof options.separator === 'string')
                this.separator = options.separator;

            if (typeof options.startDate === 'string')
                this.startDate = moment(options.startDate, this.format);

            if (typeof options.endDate === 'string')
                this.endDate = moment(options.endDate, this.format);

            if (typeof options.minDate === 'string')
                this.minDate = moment(options.minDate, this.format);

            if (typeof options.maxDate === 'string')
                this.maxDate = moment(options.maxDate, this.format);

            if (typeof options.startDate === 'object')
                this.startDate = moment(options.startDate);

            if (typeof options.endDate === 'object')
                this.endDate = moment(options.endDate);

            if (typeof options.minDate === 'object')
                this.minDate = moment(options.minDate);

            if (typeof options.maxDate === 'object')
                this.maxDate = moment(options.maxDate);

            if (typeof options.applyClass === 'string')
                this.applyClass = options.applyClass;

            if (typeof options.cancelClass === 'string')
                this.cancelClass = options.cancelClass;

            if (typeof options.dateLimit === 'object')
                this.dateLimit = options.dateLimit;

            if (typeof options.locale === 'object') {

                if (typeof options.locale.daysOfWeek === 'object') {
                    // Create a copy of daysOfWeek to avoid modification of original
                    // options object for reusability in multiple daterangepicker instances
                    this.locale.daysOfWeek = options.locale.daysOfWeek.slice();
                }

                if (typeof options.locale.monthNames === 'object') {
                  this.locale.monthNames = options.locale.monthNames.slice();
                }

                if (typeof options.locale.firstDay === 'number') {
                  this.locale.firstDay = options.locale.firstDay;
                }

                if (typeof options.locale.applyLabel === 'string') {
                  this.locale.applyLabel = options.locale.applyLabel;
                }

                if (typeof options.locale.cancelLabel === 'string') {
                  this.locale.cancelLabel = options.locale.cancelLabel;
                }

                if (typeof options.locale.fromLabel === 'string') {
                  this.locale.fromLabel = options.locale.fromLabel;
                }

                if (typeof options.locale.toLabel === 'string') {
                  this.locale.toLabel = options.locale.toLabel;
                }

                if (typeof options.locale.weekLabel === 'string') {
                  this.locale.weekLabel = options.locale.weekLabel;
                }

                if (typeof options.locale.customRangeLabel === 'string') {
                  this.locale.customRangeLabel = options.locale.customRangeLabel;
                }
            }

            if (typeof options.opens === 'string')
                this.opens = options.opens;

            if (typeof options.showWeekNumbers === 'boolean') {
                this.showWeekNumbers = options.showWeekNumbers;
            }

            if (typeof options.buttonClasses === 'string') {
                this.buttonClasses = [options.buttonClasses];
            }

            if (typeof options.buttonClasses === 'object') {
                this.buttonClasses = options.buttonClasses;
            }

            if (typeof options.showDropdowns === 'boolean') {
                this.showDropdowns = options.showDropdowns;
            }

            if (typeof options.singleDatePicker === 'boolean') {
                this.singleDatePicker = options.singleDatePicker;
                if (this.singleDatePicker) {
                    this.endDate = this.startDate.clone();
                }
            }

            if (typeof options.timePicker === 'boolean') {
                this.timePicker = options.timePicker;
            }

            if (typeof options.timePickerIncrement === 'number') {
                this.timePickerIncrement = options.timePickerIncrement;
            }

            if (typeof options.timePicker12Hour === 'boolean') {
                this.timePicker12Hour = options.timePicker12Hour;
            }

            // update day names order to firstDay
            if (this.locale.firstDay != 0) {
                var iterator = this.locale.firstDay;
                while (iterator > 0) {
                    this.locale.daysOfWeek.push(this.locale.daysOfWeek.shift());
                    iterator--;
                }
            }

            var start, end, range;

            //if no start/end dates set, check if an input element contains initial values
            if (typeof options.startDate === 'undefined' && typeof options.endDate === 'undefined') {
                if ($(this.element).is('input[type=text]')) {
                    var val = $(this.element).val();
                    var split = val.split(this.separator);
                    start = end = null;
                    if (split.length == 2) {
                        start = moment(split[0], this.format);
                        end = moment(split[1], this.format);
                    } else if (this.singleDatePicker) {
                        start = moment(val, this.format);
                        end = moment(val, this.format);
                    }
                    if (start !== null && end !== null) {
                        this.startDate = start;
                        this.endDate = end;
                    }
                }
            }

            if (typeof options.ranges === 'object') {
                for (range in options.ranges) {

                    start = moment(options.ranges[range][0]);
                    end = moment(options.ranges[range][1]);

                    // If we have a min/max date set, bound this range
                    // to it, but only if it would otherwise fall
                    // outside of the min/max.
                    if (this.minDate && start.isBefore(this.minDate))
                        start = moment(this.minDate);

                    if (this.maxDate && end.isAfter(this.maxDate))
                        end = moment(this.maxDate);

                    // If the end of the range is before the minimum (if min is set) OR
                    // the start of the range is after the max (also if set) don't display this
                    // range option.
                    if ((this.minDate && end.isBefore(this.minDate)) || (this.maxDate && start.isAfter(this.maxDate))) {
                        continue;
                    }

                    this.ranges[range] = [start, end];
                }

                var list = '<ul>';
                for (range in this.ranges) {
                    list += '<li>' + range + '</li>';
                }
                list += '<li>' + this.locale.customRangeLabel + '</li>';
                list += '</ul>';
                this.container.find('.ranges ul').remove();
                this.container.find('.ranges').prepend(list);
            }

            if (typeof callback === 'function') {
                this.cb = callback;
            }

            if (!this.timePicker) {
                this.startDate = this.startDate.startOf('day');
                this.endDate = this.endDate.endOf('day');
            }

            if (this.singleDatePicker) {
            	this.opens = 'right';
            	this.container.addClass('single');
                this.container.find('.calendar.right').show();
                this.container.find('.calendar.left').hide();
                if (!this.timePicker) {
	                this.container.find('.ranges').hide();
                } else {
                	this.container.find('.ranges .daterangepicker_start_input, .ranges .daterangepicker_end_input').hide();
                }
                if (!this.container.find('.calendar.right').hasClass('single'))
                    this.container.find('.calendar.right').addClass('single');
            } else {
            	this.container.removeClass('single');
            	this.container.find('.calendar.right').removeClass('single');
                this.container.find('.ranges').show();
            }

            this.oldStartDate = this.startDate.clone();
            this.oldEndDate = this.endDate.clone();
            this.oldChosenLabel = this.chosenLabel;

            this.leftCalendar = {
                month: moment([this.startDate.year(), this.startDate.month(), 1, this.startDate.hour(), this.startDate.minute()]),
                calendar: []
            };

            this.rightCalendar = {
                month: moment([this.endDate.year(), this.endDate.month(), 1, this.endDate.hour(), this.endDate.minute()]),
                calendar: []
            };

            if (this.opens == 'right') {
                //swap calendar positions
                var left = this.container.find('.calendar.left');
                var right = this.container.find('.calendar.right');

                if (right.hasClass('single')) {
                    right.removeClass('single');
                    left.addClass('single');
                }

                left.removeClass('left').addClass('right');
                right.removeClass('right').addClass('left');

                if (this.singleDatePicker) {
                    left.show();
                    right.hide();
                }
            }

            if (typeof options.ranges === 'undefined' && !this.singleDatePicker) {
                this.container.addClass('show-calendar');
            }

            this.container.addClass('opens' + this.opens);

            this.updateView();
            this.updateCalendars();

        },

        setStartDate: function(startDate) {
            if (typeof startDate === 'string')
                this.startDate = moment(startDate, this.format);

            if (typeof startDate === 'object')
                this.startDate = moment(startDate);

            if (!this.timePicker)
                this.startDate = this.startDate.startOf('day');

            this.oldStartDate = this.startDate.clone();

            this.updateView();
            this.updateCalendars();
            this.updateInputText();
        },

        setEndDate: function(endDate) {
            if (typeof endDate === 'string')
                this.endDate = moment(endDate, this.format);

            if (typeof endDate === 'object')
                this.endDate = moment(endDate);

            if (!this.timePicker)
                this.endDate = this.endDate.endOf('day');

            this.oldEndDate = this.endDate.clone();

            this.updateView();
            this.updateCalendars();
            this.updateInputText();
        },

        updateView: function () {
            this.leftCalendar.month.month(this.startDate.month()).year(this.startDate.year()).hour(this.startDate.hour()).minute(this.startDate.minute());
            this.rightCalendar.month.month(this.endDate.month()).year(this.endDate.year()).hour(this.endDate.hour()).minute(this.endDate.minute());
            this.updateFormInputs();
        },

        updateFormInputs: function () {
            this.container.find('input[name=daterangepicker_start]').val(this.startDate.format(this.format));
            this.container.find('input[name=daterangepicker_end]').val(this.endDate.format(this.format));

            if (this.startDate.isSame(this.endDate) || this.startDate.isBefore(this.endDate)) {
                this.container.find('button.applyBtn').removeAttr('disabled');
            } else {
                this.container.find('button.applyBtn').attr('disabled', 'disabled');
            }
        },

        updateFromControl: function () {
            if (!this.element.is('input')) return;
            if (!this.element.val().length) return;

            var dateString = this.element.val().split(this.separator),
                start = null,
                end = null;

            if(dateString.length === 2) {
                start = moment(dateString[0], this.format);
                end = moment(dateString[1], this.format);
            }

            if (this.singleDatePicker || start === null || end === null) {
                start = moment(this.element.val(), this.format);
                end = start;
            }

            if (end.isBefore(start)) return;

            this.oldStartDate = this.startDate.clone();
            this.oldEndDate = this.endDate.clone();

            this.startDate = start;
            this.endDate = end;

            if (!this.startDate.isSame(this.oldStartDate) || !this.endDate.isSame(this.oldEndDate))
                this.notify();

            this.updateCalendars();
        },

        notify: function () {
            this.updateView();
            this.cb(this.startDate, this.endDate, this.chosenLabel);
        },

        move: function () {
            var parentOffset = { top: 0, left: 0 };
            var parentRightEdge = $(window).width();
            if (!this.parentEl.is('body')) {
                parentOffset = {
                    top: this.parentEl.offset().top - this.parentEl.scrollTop(),
                    left: this.parentEl.offset().left - this.parentEl.scrollLeft()
                };
                parentRightEdge = this.parentEl[0].clientWidth + this.parentEl.offset().left;
            }

            if (this.opens == 'left') {
                this.container.css({
                    top: this.element.offset().top + this.element.outerHeight() - parentOffset.top,
                    right: parentRightEdge - this.element.offset().left - this.element.outerWidth(),
                    left: 'auto'
                });
                if (this.container.offset().left < 0) {
                    this.container.css({
                        right: 'auto',
                        left: 9
                    });
                }
            } else {
                this.container.css({
                    top: this.element.offset().top + this.element.outerHeight() - parentOffset.top,
                    left: this.element.offset().left - parentOffset.left,
                    right: 'auto'
                });
                if (this.container.offset().left + this.container.outerWidth() > $(window).width()) {
                    this.container.css({
                        left: 'auto',
                        right: 0
                    });
                }
            }
        },

        toggle: function (e) {
            if (this.element.hasClass('active')) {
                this.hide();
            } else {
                this.show();
            }
        },

        show: function (e) {
            if (this.isShowing) return;

            this.element.addClass('active');
            this.container.show();
            this.move();

            // Create a click proxy that is private to this instance of datepicker, for unbinding
            this._outsideClickProxy = $.proxy(function (e) { this.outsideClick(e); }, this);
            // Bind global datepicker mousedown for hiding and
            $(document)
              .on('mousedown.daterangepicker', this._outsideClickProxy)
              // also explicitly play nice with Bootstrap dropdowns, which stopPropagation when clicking them
              .on('click.daterangepicker', '[data-toggle=dropdown]', this._outsideClickProxy)
              // and also close when focus changes to outside the picker (eg. tabbing between controls)
              .on('focusin.daterangepicker', this._outsideClickProxy);

            this.isShowing = true;
            this.element.trigger('show.daterangepicker', this);
        },

        outsideClick: function (e) {
            var target = $(e.target);
            // if the page is clicked anywhere except within the daterangerpicker/button
            // itself then call this.hide()
            if (
                target.closest(this.element).length ||
                target.closest(this.container).length ||
                target.closest('.calendar-date').length
                ) return;
            this.hide();
        },

        hide: function (e) {
            if (!this.isShowing) return;

            $(document)
              .off('mousedown.daterangepicker')
              .off('click.daterangepicker', '[data-toggle=dropdown]')
              .off('focusin.daterangepicker');

            this.element.removeClass('active');
            this.container.hide();

            if (!this.startDate.isSame(this.oldStartDate) || !this.endDate.isSame(this.oldEndDate))
                this.notify();

            this.oldStartDate = this.startDate.clone();
            this.oldEndDate = this.endDate.clone();

            this.isShowing = false;
            this.element.trigger('hide.daterangepicker', this);
        },

        enterRange: function (e) {
            // mouse pointer has entered a range label
            var label = e.target.innerHTML;
            if (label == this.locale.customRangeLabel) {
                this.updateView();
            } else {
                var dates = this.ranges[label];
                this.container.find('input[name=daterangepicker_start]').val(dates[0].format(this.format));
                this.container.find('input[name=daterangepicker_end]').val(dates[1].format(this.format));
            }
        },

        showCalendars: function() {
            this.container.addClass('show-calendar');
            this.move();
            this.element.trigger('showCalendar.daterangepicker', this);
        },

        hideCalendars: function() {
            this.container.removeClass('show-calendar');
            this.element.trigger('hideCalendar.daterangepicker', this);
        },

        // when a date is typed into the start to end date textboxes
        inputsChanged: function (e) {
            var el = $(e.target);
            var date = moment(el.val(), this.format);
            if (!date.isValid()) return;

            var startDate, endDate;
            if (el.attr('name') === 'daterangepicker_start') {
                startDate = date;
                endDate = this.endDate;
            } else {
                startDate = this.startDate;
                endDate = date;
            }
            this.setCustomDates(startDate, endDate);
        },

        inputsKeydown: function(e) {
            if (e.keyCode === 13) {
                this.inputsChanged(e);
                this.notify();
            }
        },

        updateInputText: function() {
            if (this.element.is('input') && !this.singleDatePicker) {
                this.element.val(this.startDate.format(this.format) + this.separator + this.endDate.format(this.format));
            } else if (this.element.is('input')) {
            	this.element.val(this.endDate.format(this.format));
            }
        },

        clickRange: function (e) {
            var label = e.target.innerHTML;
            this.chosenLabel = label;
            if (label == this.locale.customRangeLabel) {
                this.showCalendars();
            } else {
                var dates = this.ranges[label];

                this.startDate = dates[0];
                this.endDate = dates[1];

                if (!this.timePicker) {
                    this.startDate.startOf('day');
                    this.endDate.endOf('day');
                }

                this.leftCalendar.month.month(this.startDate.month()).year(this.startDate.year()).hour(this.startDate.hour()).minute(this.startDate.minute());
                this.rightCalendar.month.month(this.endDate.month()).year(this.endDate.year()).hour(this.endDate.hour()).minute(this.endDate.minute());
                this.updateCalendars();

                this.updateInputText();

                this.hideCalendars();
                this.hide();
                this.element.trigger('apply.daterangepicker', this);
            }
        },

        clickPrev: function (e) {
            var cal = $(e.target).parents('.calendar');
            if (cal.hasClass('left')) {
                this.leftCalendar.month.subtract(1, 'month');
            } else {
                this.rightCalendar.month.subtract(1, 'month');
            }
            this.updateCalendars();
        },

        clickNext: function (e) {
            var cal = $(e.target).parents('.calendar');
            if (cal.hasClass('left')) {
                this.leftCalendar.month.add(1, 'month');
            } else {
                this.rightCalendar.month.add(1, 'month');
            }
            this.updateCalendars();
        },

        hoverDate: function (e) {
            var title = $(e.target).attr('data-title');
            var row = title.substr(1, 1);
            var col = title.substr(3, 1);
            var cal = $(e.target).parents('.calendar');

            if (cal.hasClass('left')) {
                this.container.find('input[name=daterangepicker_start]').val(this.leftCalendar.calendar[row][col].format(this.format));
            } else {
                this.container.find('input[name=daterangepicker_end]').val(this.rightCalendar.calendar[row][col].format(this.format));
            }
        },

        setCustomDates: function(startDate, endDate) {
            this.chosenLabel = this.locale.customRangeLabel;
            if (startDate.isAfter(endDate)) {
                var difference = this.endDate.diff(this.startDate);
                endDate = moment(startDate).add(difference, 'ms');
            }
            this.startDate = startDate;
            this.endDate = endDate;

            this.updateView();
            this.updateCalendars();
        },

        clickDate: function (e) {
            var title = $(e.target).attr('data-title');
            var row = title.substr(1, 1);
            var col = title.substr(3, 1);
            var cal = $(e.target).parents('.calendar');

            var startDate, endDate;
            if (cal.hasClass('left')) {
                startDate = this.leftCalendar.calendar[row][col];
                endDate = this.endDate;
                if (typeof this.dateLimit === 'object') {
                    var maxDate = moment(startDate).add(this.dateLimit).startOf('day');
                    if (endDate.isAfter(maxDate)) {
                        endDate = maxDate;
                    }
                }
            } else {
                startDate = this.startDate;
                endDate = this.rightCalendar.calendar[row][col];
                if (typeof this.dateLimit === 'object') {
                    var minDate = moment(endDate).subtract(this.dateLimit).startOf('day');
                    if (startDate.isBefore(minDate)) {
                        startDate = minDate;
                    }
                }
            }

            if (this.singleDatePicker && cal.hasClass('left')) {
                endDate = startDate.clone();
            } else if (this.singleDatePicker && cal.hasClass('right')) {
                startDate = endDate.clone();
            }

            cal.find('td').removeClass('active');

            $(e.target).addClass('active');

            this.setCustomDates(startDate, endDate);

            if (!this.timePicker)
                endDate.endOf('day');

            if (this.singleDatePicker && !this.timePicker)
                this.clickApply();
        },

        clickApply: function (e) {
            this.updateInputText();
            this.hide();
            this.element.trigger('apply.daterangepicker', this);
        },

        clickCancel: function (e) {
            this.startDate = this.oldStartDate;
            this.endDate = this.oldEndDate;
            this.chosenLabel = this.oldChosenLabel;
            this.updateView();
            this.updateCalendars();
            this.hide();
            this.element.trigger('cancel.daterangepicker', this);
            this.element.val('');
        },

        updateMonthYear: function (e) {
            var isLeft = $(e.target).closest('.calendar').hasClass('left'),
                leftOrRight = isLeft ? 'left' : 'right',
                cal = this.container.find('.calendar.'+leftOrRight);

            // Month must be Number for new moment versions
            var month = parseInt(cal.find('.monthselect').val(), 10);
            var year = cal.find('.yearselect').val();

            this[leftOrRight+'Calendar'].month.month(month).year(year);
            this.updateCalendars();
        },

        updateTime: function(e) {

            var cal = $(e.target).closest('.calendar'),
                isLeft = cal.hasClass('left');

            var hour = parseInt(cal.find('.hourselect').val(), 10);
            var minute = parseInt(cal.find('.minuteselect').val(), 10);

            if (this.timePicker12Hour) {
                var ampm = cal.find('.ampmselect').val();
                if (ampm === 'PM' && hour < 12)
                    hour += 12;
                if (ampm === 'AM' && hour === 12)
                    hour = 0;
            }

            if (isLeft) {
                var start = this.startDate.clone();
                start.hour(hour);
                start.minute(minute);
                this.startDate = start;
                this.leftCalendar.month.hour(hour).minute(minute);
            } else {
                var end = this.endDate.clone();
                end.hour(hour);
                end.minute(minute);
                this.endDate = end;
                this.rightCalendar.month.hour(hour).minute(minute);
            }

            this.updateCalendars();
        },

        updateCalendars: function () {
            this.leftCalendar.calendar = this.buildCalendar(this.leftCalendar.month.month(), this.leftCalendar.month.year(), this.leftCalendar.month.hour(), this.leftCalendar.month.minute(), 'left');
            this.rightCalendar.calendar = this.buildCalendar(this.rightCalendar.month.month(), this.rightCalendar.month.year(), this.rightCalendar.month.hour(), this.rightCalendar.month.minute(), 'right');
            this.container.find('.calendar.left').empty().html(this.renderCalendar(this.leftCalendar.calendar, this.startDate, this.minDate, this.maxDate));
            this.container.find('.calendar.right').empty().html(this.renderCalendar(this.rightCalendar.calendar, this.endDate, this.singleDatePicker ? this.minDate : this.startDate, this.maxDate));
            
            this.container.find('.ranges li').removeClass('active');
            var customRange = true;
            var i = 0;
            for (var range in this.ranges) {
                if (this.timePicker) {
                    if (this.startDate.isSame(this.ranges[range][0]) && this.endDate.isSame(this.ranges[range][1])) {
                        customRange = false;
                        this.chosenLabel = this.container.find('.ranges li:eq(' + i + ')')
                            .addClass('active').html();
                    }
                } else {
                    //ignore times when comparing dates if time picker is not enabled
                    if (this.startDate.format('YYYY-MM-DD') == this.ranges[range][0].format('YYYY-MM-DD') && this.endDate.format('YYYY-MM-DD') == this.ranges[range][1].format('YYYY-MM-DD')) {
                        customRange = false;
                        this.chosenLabel = this.container.find('.ranges li:eq(' + i + ')')
                            .addClass('active').html();
                    }
                }
                i++;
            }
            if (customRange) {
                this.chosenLabel = this.container.find('.ranges li:last').addClass('active').html();
                this.showCalendars();
            }
        },

        buildCalendar: function (month, year, hour, minute, side) {
            var daysInMonth = moment([year, month]).daysInMonth();
            var firstDay = moment([year, month, 1]);
            var lastDay = moment([year, month, daysInMonth]);
            var lastMonth = moment(firstDay).subtract(1, 'month').month();
            var lastYear = moment(firstDay).subtract(1, 'month').year();

            var daysInLastMonth = moment([lastYear, lastMonth]).daysInMonth();

            var dayOfWeek = firstDay.day();

            var i;

            //initialize a 6 rows x 7 columns array for the calendar
            var calendar = [];
            calendar.firstDay = firstDay;
            calendar.lastDay = lastDay;

            for (i = 0; i < 6; i++) {
                calendar[i] = [];
            }

            //populate the calendar with date objects
            var startDay = daysInLastMonth - dayOfWeek + this.locale.firstDay + 1;
            if (startDay > daysInLastMonth)
                startDay -= 7;

            if (dayOfWeek == this.locale.firstDay)
                startDay = daysInLastMonth - 6;

            var curDate = moment([lastYear, lastMonth, startDay, 12, minute]);
            var col, row;
            for (i = 0, col = 0, row = 0; i < 42; i++, col++, curDate = moment(curDate).add(24, 'hour')) {
                if (i > 0 && col % 7 === 0) {
                    col = 0;
                    row++;
                }
                calendar[row][col] = curDate.clone().hour(hour);
                curDate.hour(12);
            }

            return calendar;
        },

        renderDropdowns: function (selected, minDate, maxDate) {
            var currentMonth = selected.month();
            var monthHtml = '<select class="monthselect">';
            var inMinYear = false;
            var inMaxYear = false;

            for (var m = 0; m < 12; m++) {
                if ((!inMinYear || m >= minDate.month()) && (!inMaxYear || m <= maxDate.month())) {
                    monthHtml += "<option value='" + m + "'" +
                        (m === currentMonth ? " selected='selected'" : "") +
                        ">" + this.locale.monthNames[m] + "</option>";
                }
            }
            monthHtml += "</select>";

            var currentYear = selected.year();
            var maxYear = (maxDate && maxDate.year()) || (currentYear + 5);
            var minYear = (minDate && minDate.year()) || (currentYear - 50);
            var yearHtml = '<select class="yearselect">';

            for (var y = minYear; y <= maxYear; y++) {
                yearHtml += '<option value="' + y + '"' +
                    (y === currentYear ? ' selected="selected"' : '') +
                    '>' + y + '</option>';
            }

            yearHtml += '</select>';

            return monthHtml + yearHtml;
        },

        renderCalendar: function (calendar, selected, minDate, maxDate) {

            var html = '<div class="calendar-date">';
            html += '<table class="table-condensed">';
            html += '<thead>';
            html += '<tr>';

            // add empty cell for week number
            if (this.showWeekNumbers)
                html += '<th></th>';

            if (!minDate || minDate.isBefore(calendar.firstDay)) {
                html += '<th class="prev available"><i class="fa fa-arrow-left icon-arrow-left glyphicon glyphicon-arrow-left"></i></th>';
            } else {
                html += '<th></th>';
            }

            var dateHtml = this.locale.monthNames[calendar[1][1].month()] + calendar[1][1].format(" YYYY");

            if (this.showDropdowns) {
                dateHtml = this.renderDropdowns(calendar[1][1], minDate, maxDate);
            }

            html += '<th colspan="5" class="month">' + dateHtml + '</th>';
            if (!maxDate || maxDate.isAfter(calendar.lastDay)) {
                html += '<th class="next available"><i class="fa fa-arrow-right icon-arrow-right glyphicon glyphicon-arrow-right"></i></th>';
            } else {
                html += '<th></th>';
            }

            html += '</tr>';
            html += '<tr>';

            // add week number label
            if (this.showWeekNumbers)
                html += '<th class="week">' + this.locale.weekLabel + '</th>';

            $.each(this.locale.daysOfWeek, function (index, dayOfWeek) {
                html += '<th>' + dayOfWeek + '</th>';
            });

            html += '</tr>';
            html += '</thead>';
            html += '<tbody>';

            for (var row = 0; row < 6; row++) {
                html += '<tr>';

                // add week number
                if (this.showWeekNumbers)
                    html += '<td class="week">' + calendar[row][0].week() + '</td>';

                for (var col = 0; col < 7; col++) {
                    var cname = 'available ';
                    cname += (calendar[row][col].month() == calendar[1][1].month()) ? '' : 'off';

                    if ((minDate && calendar[row][col].isBefore(minDate, 'day')) || (maxDate && calendar[row][col].isAfter(maxDate, 'day'))) {
                        cname = ' off disabled ';
                    } else if (calendar[row][col].format('YYYY-MM-DD') == selected.format('YYYY-MM-DD')) {
                        cname += ' active ';
                        if (calendar[row][col].format('YYYY-MM-DD') == this.startDate.format('YYYY-MM-DD')) {
                            cname += ' start-date ';
                        }
                        if (calendar[row][col].format('YYYY-MM-DD') == this.endDate.format('YYYY-MM-DD')) {
                            cname += ' end-date ';
                        }
                    } else if (calendar[row][col] >= this.startDate && calendar[row][col] <= this.endDate) {
                        cname += ' in-range ';
                        if (calendar[row][col].isSame(this.startDate)) { cname += ' start-date '; }
                        if (calendar[row][col].isSame(this.endDate)) { cname += ' end-date '; }
                    }

                    var title = 'r' + row + 'c' + col;
                    html += '<td class="' + cname.replace(/\s+/g, ' ').replace(/^\s?(.*?)\s?$/, '$1') + '" data-title="' + title + '">' + calendar[row][col].date() + '</td>';
                }
                html += '</tr>';
            }

            html += '</tbody>';
            html += '</table>';
            html += '</div>';

            var i;
            if (this.timePicker) {

                html += '<div class="calendar-time">';
                html += '<select class="hourselect">';
                var start = 0;
                var end = 23;
                var selected_hour = selected.hour();
                if (this.timePicker12Hour) {
                    start = 1;
                    end = 12;
                    if (selected_hour >= 12)
                        selected_hour -= 12;
                    if (selected_hour === 0)
                        selected_hour = 12;
                }

                for (i = start; i <= end; i++) {
                    if (i == selected_hour) {
                        html += '<option value="' + i + '" selected="selected">' + i + '</option>';
                    } else {
                        html += '<option value="' + i + '">' + i + '</option>';
                    }
                }

                html += '</select> : ';

                html += '<select class="minuteselect">';

                for (i = 0; i < 60; i += this.timePickerIncrement) {
                    var num = i;
                    if (num < 10)
                        num = '0' + num;
                    if (i == selected.minute()) {
                        html += '<option value="' + i + '" selected="selected">' + num + '</option>';
                    } else {
                        html += '<option value="' + i + '">' + num + '</option>';
                    }
                }

                html += '</select> ';

                if (this.timePicker12Hour) {
                    html += '<select class="ampmselect">';
                    if (selected.hour() >= 12) {
                        html += '<option value="AM">AM</option><option value="PM" selected="selected">PM</option>';
                    } else {
                        html += '<option value="AM" selected="selected">AM</option><option value="PM">PM</option>';
                    }
                    html += '</select>';
                }

                html += '</div>';

            }

            return html;

        },

        remove: function() {

            this.container.remove();
            this.element.off('.daterangepicker');
            this.element.removeData('daterangepicker');

        }

    };

    $.fn.daterangepicker = function (options, cb) {
        this.each(function () {
            var el = $(this);
            if (el.data('daterangepicker'))
                el.data('daterangepicker').remove();
            el.data('daterangepicker', new DateRangePicker(el, options, cb));
        });
        return this;
    };

}));
;(function(a){a.fn.zclip=function(c){if(typeof c=="object"&&!c.length){var b=a.extend({path:"ZeroClipboard.swf",copy:null,beforeCopy:null,afterCopy:null,clickAfter:true,setHandCursor:true,setCSSEffects:true},c);return this.each(function(){var e=a(this);if(e.is(":visible")&&(typeof b.copy=="string"||a.isFunction(b.copy))){ZeroClipboard.setMoviePath(b.path);var d=new ZeroClipboard.Client();if(a.isFunction(b.copy)){e.bind("zClip_copy",b.copy)}if(a.isFunction(b.beforeCopy)){e.bind("zClip_beforeCopy",b.beforeCopy)}if(a.isFunction(b.afterCopy)){e.bind("zClip_afterCopy",b.afterCopy)}d.setHandCursor(b.setHandCursor);d.setCSSEffects(b.setCSSEffects);d.addEventListener("mouseOver",function(f){e.trigger("mouseenter")});d.addEventListener("mouseOut",function(f){e.trigger("mouseleave")});d.addEventListener("mouseDown",function(f){e.trigger("mousedown");if(!a.isFunction(b.copy)){d.setText(b.copy)}else{d.setText(e.triggerHandler("zClip_copy"))}if(a.isFunction(b.beforeCopy)){e.trigger("zClip_beforeCopy")}});d.addEventListener("complete",function(f,g){if(a.isFunction(b.afterCopy)){e.trigger("zClip_afterCopy")}else{if(g.length>500){g=g.substr(0,500)+"...\n\n("+(g.length-500)+" characters not shown)"}e.removeClass("hover");alert("已复制到剪切板:\n\n "+g)}if(b.clickAfter){e.trigger("click")}});d.glue(e[0],e.parent()[0]);a(window).bind("load resize",function(){d.reposition()})}})}else{if(typeof c=="string"){return this.each(function(){var f=a(this);c=c.toLowerCase();var e=f.data("zclipId");var d=a("#"+e+".zclip");if(c=="remove"){d.remove();f.removeClass("active hover")}else{if(c=="hide"){d.hide();f.removeClass("active hover")}else{if(c=="show"){d.show()}}}})}}}})(jQuery);var ZeroClipboard={version:"1.0.7",clients:{},moviePath:"ZeroClipboard.swf",nextId:1,$:function(a){if(typeof(a)=="string"){a=document.getElementById(a)}if(!a.addClass){a.hide=function(){this.style.display="none"};a.show=function(){this.style.display=""};a.addClass=function(b){this.removeClass(b);this.className+=" "+b};a.removeClass=function(d){var e=this.className.split(/\s+/);var b=-1;for(var c=0;c<e.length;c++){if(e[c]==d){b=c;c=e.length}}if(b>-1){e.splice(b,1);this.className=e.join(" ")}return this};a.hasClass=function(b){return !!this.className.match(new RegExp("\\s*"+b+"\\s*"))}}return a},setMoviePath:function(a){this.moviePath=a},dispatch:function(d,b,c){var a=this.clients[d];if(a){a.receiveEvent(b,c)}},register:function(b,a){this.clients[b]=a},getDOMObjectPosition:function(c,a){var b={left:0,top:0,width:c.width?c.width:c.offsetWidth,height:c.height?c.height:c.offsetHeight};if(c&&(c!=a)){b.left+=c.offsetLeft;b.top+=c.offsetTop}return b},Client:function(a){this.handlers={};this.id=ZeroClipboard.nextId++;this.movieId="ZeroClipboardMovie_"+this.id;ZeroClipboard.register(this.id,this);if(a){this.glue(a)}}};ZeroClipboard.Client.prototype={id:0,ready:false,movie:null,clipText:"",handCursorEnabled:true,cssEffects:true,handlers:null,glue:function(d,b,e){this.domElement=ZeroClipboard.$(d);var f=99;if(this.domElement.style.zIndex){f=parseInt(this.domElement.style.zIndex,10)+1}if(typeof(b)=="string"){b=ZeroClipboard.$(b)}else{if(typeof(b)=="undefined"){b=document.getElementsByTagName("body")[0]}}var c=ZeroClipboard.getDOMObjectPosition(this.domElement,b);this.div=document.createElement("div");this.div.className="zclip";this.div.id="zclip-"+this.movieId;$(this.domElement).data("zclipId","zclip-"+this.movieId);var a=this.div.style;a.position="absolute";a.left=""+c.left+"px";a.top=""+c.top+"px";a.width=""+c.width+"px";a.height=""+c.height+"px";a.zIndex=f;if(typeof(e)=="object"){for(addedStyle in e){a[addedStyle]=e[addedStyle]}}b.appendChild(this.div);this.div.innerHTML=this.getHTML(c.width,c.height)},getHTML:function(d,a){var c="";var b="id="+this.id+"&width="+d+"&height="+a;if(navigator.userAgent.match(/MSIE/)){var e=location.href.match(/^https/i)?"https://":"http://";c+='<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="'+e+'download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,0,0" width="'+d+'" height="'+a+'" id="'+this.movieId+'" align="middle"><param name="allowScriptAccess" value="always" /><param name="allowFullScreen" value="false" /><param name="movie" value="'+ZeroClipboard.moviePath+'" /><param name="loop" value="false" /><param name="menu" value="false" /><param name="quality" value="best" /><param name="bgcolor" value="#ffffff" /><param name="flashvars" value="'+b+'"/><param name="wmode" value="transparent"/></object>'}else{c+='<embed id="'+this.movieId+'" src="'+ZeroClipboard.moviePath+'" loop="false" menu="false" quality="best" bgcolor="#ffffff" width="'+d+'" height="'+a+'" name="'+this.movieId+'" align="middle" allowScriptAccess="always" allowFullScreen="false" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" flashvars="'+b+'" wmode="transparent" />'}return c},hide:function(){if(this.div){this.div.style.left="-2000px"}},show:function(){this.reposition()},destroy:function(){if(this.domElement&&this.div){this.hide();this.div.innerHTML="";var a=document.getElementsByTagName("body")[0];try{a.removeChild(this.div)}catch(b){}this.domElement=null;this.div=null}},reposition:function(c){if(c){this.domElement=ZeroClipboard.$(c);if(!this.domElement){this.hide()}}if(this.domElement&&this.div){var b=ZeroClipboard.getDOMObjectPosition(this.domElement);var a=this.div.style;a.left=""+b.left+"px";a.top=""+b.top+"px"}},setText:function(a){this.clipText=a;if(this.ready){this.movie.setText(a)}},addEventListener:function(a,b){a=a.toString().toLowerCase().replace(/^on/,"");if(!this.handlers[a]){this.handlers[a]=[]}this.handlers[a].push(b)},setHandCursor:function(a){this.handCursorEnabled=a;if(this.ready){this.movie.setHandCursor(a)}},setCSSEffects:function(a){this.cssEffects=!!a},receiveEvent:function(d,f){d=d.toString().toLowerCase().replace(/^on/,"");switch(d){case"load":this.movie=document.getElementById(this.movieId);if(!this.movie){var c=this;setTimeout(function(){c.receiveEvent("load",null)},1);return}if(!this.ready&&navigator.userAgent.match(/Firefox/)&&navigator.userAgent.match(/Windows/)){var c=this;setTimeout(function(){c.receiveEvent("load",null)},100);this.ready=true;return}this.ready=true;try{this.movie.setText(this.clipText)}catch(h){}try{this.movie.setHandCursor(this.handCursorEnabled)}catch(h){}break;case"mouseover":if(this.domElement&&this.cssEffects){this.domElement.addClass("hover");if(this.recoverActive){this.domElement.addClass("active")}}break;case"mouseout":if(this.domElement&&this.cssEffects){this.recoverActive=false;if(this.domElement.hasClass("active")){this.domElement.removeClass("active");this.recoverActive=true}this.domElement.removeClass("hover")}break;case"mousedown":if(this.domElement&&this.cssEffects){this.domElement.addClass("active")}break;case"mouseup":if(this.domElement&&this.cssEffects){this.domElement.removeClass("active");this.recoverActive=false}break}if(this.handlers[d]){for(var b=0,a=this.handlers[d].length;b<a;b++){var g=this.handlers[d][b];if(typeof(g)=="function"){g(this,f)}else{if((typeof(g)=="object")&&(g.length==2)){g[0][g[1]](this,f)}else{if(typeof(g)=="string"){window[g](this,f)}}}}}}};;(function(r){r.fn.qrcode=function(h){var s;function u(a){this.mode=s;this.data=a}function o(a,c){this.typeNumber=a;this.errorCorrectLevel=c;this.modules=null;this.moduleCount=0;this.dataCache=null;this.dataList=[]}function q(a,c){if(void 0==a.length)throw Error(a.length+"/"+c);for(var d=0;d<a.length&&0==a[d];)d++;this.num=Array(a.length-d+c);for(var b=0;b<a.length-d;b++)this.num[b]=a[b+d]}function p(a,c){this.totalCount=a;this.dataCount=c}function t(){this.buffer=[];this.length=0}u.prototype={getLength:function(){return this.data.length},
write:function(a){for(var c=0;c<this.data.length;c++)a.put(this.data.charCodeAt(c),8)}};o.prototype={addData:function(a){this.dataList.push(new u(a));this.dataCache=null},isDark:function(a,c){if(0>a||this.moduleCount<=a||0>c||this.moduleCount<=c)throw Error(a+","+c);return this.modules[a][c]},getModuleCount:function(){return this.moduleCount},make:function(){if(1>this.typeNumber){for(var a=1,a=1;40>a;a++){for(var c=p.getRSBlocks(a,this.errorCorrectLevel),d=new t,b=0,e=0;e<c.length;e++)b+=c[e].dataCount;
for(e=0;e<this.dataList.length;e++)c=this.dataList[e],d.put(c.mode,4),d.put(c.getLength(),j.getLengthInBits(c.mode,a)),c.write(d);if(d.getLengthInBits()<=8*b)break}this.typeNumber=a}this.makeImpl(!1,this.getBestMaskPattern())},makeImpl:function(a,c){this.moduleCount=4*this.typeNumber+17;this.modules=Array(this.moduleCount);for(var d=0;d<this.moduleCount;d++){this.modules[d]=Array(this.moduleCount);for(var b=0;b<this.moduleCount;b++)this.modules[d][b]=null}this.setupPositionProbePattern(0,0);this.setupPositionProbePattern(this.moduleCount-
7,0);this.setupPositionProbePattern(0,this.moduleCount-7);this.setupPositionAdjustPattern();this.setupTimingPattern();this.setupTypeInfo(a,c);7<=this.typeNumber&&this.setupTypeNumber(a);null==this.dataCache&&(this.dataCache=o.createData(this.typeNumber,this.errorCorrectLevel,this.dataList));this.mapData(this.dataCache,c)},setupPositionProbePattern:function(a,c){for(var d=-1;7>=d;d++)if(!(-1>=a+d||this.moduleCount<=a+d))for(var b=-1;7>=b;b++)-1>=c+b||this.moduleCount<=c+b||(this.modules[a+d][c+b]=
0<=d&&6>=d&&(0==b||6==b)||0<=b&&6>=b&&(0==d||6==d)||2<=d&&4>=d&&2<=b&&4>=b?!0:!1)},getBestMaskPattern:function(){for(var a=0,c=0,d=0;8>d;d++){this.makeImpl(!0,d);var b=j.getLostPoint(this);if(0==d||a>b)a=b,c=d}return c},createMovieClip:function(a,c,d){a=a.createEmptyMovieClip(c,d);this.make();for(c=0;c<this.modules.length;c++)for(var d=1*c,b=0;b<this.modules[c].length;b++){var e=1*b;this.modules[c][b]&&(a.beginFill(0,100),a.moveTo(e,d),a.lineTo(e+1,d),a.lineTo(e+1,d+1),a.lineTo(e,d+1),a.endFill())}return a},
setupTimingPattern:function(){for(var a=8;a<this.moduleCount-8;a++)null==this.modules[a][6]&&(this.modules[a][6]=0==a%2);for(a=8;a<this.moduleCount-8;a++)null==this.modules[6][a]&&(this.modules[6][a]=0==a%2)},setupPositionAdjustPattern:function(){for(var a=j.getPatternPosition(this.typeNumber),c=0;c<a.length;c++)for(var d=0;d<a.length;d++){var b=a[c],e=a[d];if(null==this.modules[b][e])for(var f=-2;2>=f;f++)for(var i=-2;2>=i;i++)this.modules[b+f][e+i]=-2==f||2==f||-2==i||2==i||0==f&&0==i?!0:!1}},setupTypeNumber:function(a){for(var c=
j.getBCHTypeNumber(this.typeNumber),d=0;18>d;d++){var b=!a&&1==(c>>d&1);this.modules[Math.floor(d/3)][d%3+this.moduleCount-8-3]=b}for(d=0;18>d;d++)b=!a&&1==(c>>d&1),this.modules[d%3+this.moduleCount-8-3][Math.floor(d/3)]=b},setupTypeInfo:function(a,c){for(var d=j.getBCHTypeInfo(this.errorCorrectLevel<<3|c),b=0;15>b;b++){var e=!a&&1==(d>>b&1);6>b?this.modules[b][8]=e:8>b?this.modules[b+1][8]=e:this.modules[this.moduleCount-15+b][8]=e}for(b=0;15>b;b++)e=!a&&1==(d>>b&1),8>b?this.modules[8][this.moduleCount-
b-1]=e:9>b?this.modules[8][15-b-1+1]=e:this.modules[8][15-b-1]=e;this.modules[this.moduleCount-8][8]=!a},mapData:function(a,c){for(var d=-1,b=this.moduleCount-1,e=7,f=0,i=this.moduleCount-1;0<i;i-=2)for(6==i&&i--;;){for(var g=0;2>g;g++)if(null==this.modules[b][i-g]){var n=!1;f<a.length&&(n=1==(a[f]>>>e&1));j.getMask(c,b,i-g)&&(n=!n);this.modules[b][i-g]=n;e--; -1==e&&(f++,e=7)}b+=d;if(0>b||this.moduleCount<=b){b-=d;d=-d;break}}}};o.PAD0=236;o.PAD1=17;o.createData=function(a,c,d){for(var c=p.getRSBlocks(a,
c),b=new t,e=0;e<d.length;e++){var f=d[e];b.put(f.mode,4);b.put(f.getLength(),j.getLengthInBits(f.mode,a));f.write(b)}for(e=a=0;e<c.length;e++)a+=c[e].dataCount;if(b.getLengthInBits()>8*a)throw Error("code length overflow. ("+b.getLengthInBits()+">"+8*a+")");for(b.getLengthInBits()+4<=8*a&&b.put(0,4);0!=b.getLengthInBits()%8;)b.putBit(!1);for(;!(b.getLengthInBits()>=8*a);){b.put(o.PAD0,8);if(b.getLengthInBits()>=8*a)break;b.put(o.PAD1,8)}return o.createBytes(b,c)};o.createBytes=function(a,c){for(var d=
0,b=0,e=0,f=Array(c.length),i=Array(c.length),g=0;g<c.length;g++){var n=c[g].dataCount,h=c[g].totalCount-n,b=Math.max(b,n),e=Math.max(e,h);f[g]=Array(n);for(var k=0;k<f[g].length;k++)f[g][k]=255&a.buffer[k+d];d+=n;k=j.getErrorCorrectPolynomial(h);n=(new q(f[g],k.getLength()-1)).mod(k);i[g]=Array(k.getLength()-1);for(k=0;k<i[g].length;k++)h=k+n.getLength()-i[g].length,i[g][k]=0<=h?n.get(h):0}for(k=g=0;k<c.length;k++)g+=c[k].totalCount;d=Array(g);for(k=n=0;k<b;k++)for(g=0;g<c.length;g++)k<f[g].length&&
(d[n++]=f[g][k]);for(k=0;k<e;k++)for(g=0;g<c.length;g++)k<i[g].length&&(d[n++]=i[g][k]);return d};s=4;for(var j={PATTERN_POSITION_TABLE:[[],[6,18],[6,22],[6,26],[6,30],[6,34],[6,22,38],[6,24,42],[6,26,46],[6,28,50],[6,30,54],[6,32,58],[6,34,62],[6,26,46,66],[6,26,48,70],[6,26,50,74],[6,30,54,78],[6,30,56,82],[6,30,58,86],[6,34,62,90],[6,28,50,72,94],[6,26,50,74,98],[6,30,54,78,102],[6,28,54,80,106],[6,32,58,84,110],[6,30,58,86,114],[6,34,62,90,118],[6,26,50,74,98,122],[6,30,54,78,102,126],[6,26,52,
78,104,130],[6,30,56,82,108,134],[6,34,60,86,112,138],[6,30,58,86,114,142],[6,34,62,90,118,146],[6,30,54,78,102,126,150],[6,24,50,76,102,128,154],[6,28,54,80,106,132,158],[6,32,58,84,110,136,162],[6,26,54,82,110,138,166],[6,30,58,86,114,142,170]],G15:1335,G18:7973,G15_MASK:21522,getBCHTypeInfo:function(a){for(var c=a<<10;0<=j.getBCHDigit(c)-j.getBCHDigit(j.G15);)c^=j.G15<<j.getBCHDigit(c)-j.getBCHDigit(j.G15);return(a<<10|c)^j.G15_MASK},getBCHTypeNumber:function(a){for(var c=a<<12;0<=j.getBCHDigit(c)-
j.getBCHDigit(j.G18);)c^=j.G18<<j.getBCHDigit(c)-j.getBCHDigit(j.G18);return a<<12|c},getBCHDigit:function(a){for(var c=0;0!=a;)c++,a>>>=1;return c},getPatternPosition:function(a){return j.PATTERN_POSITION_TABLE[a-1]},getMask:function(a,c,d){switch(a){case 0:return 0==(c+d)%2;case 1:return 0==c%2;case 2:return 0==d%3;case 3:return 0==(c+d)%3;case 4:return 0==(Math.floor(c/2)+Math.floor(d/3))%2;case 5:return 0==c*d%2+c*d%3;case 6:return 0==(c*d%2+c*d%3)%2;case 7:return 0==(c*d%3+(c+d)%2)%2;default:throw Error("bad maskPattern:"+
a);}},getErrorCorrectPolynomial:function(a){for(var c=new q([1],0),d=0;d<a;d++)c=c.multiply(new q([1,l.gexp(d)],0));return c},getLengthInBits:function(a,c){if(1<=c&&10>c)switch(a){case 1:return 10;case 2:return 9;case s:return 8;case 8:return 8;default:throw Error("mode:"+a);}else if(27>c)switch(a){case 1:return 12;case 2:return 11;case s:return 16;case 8:return 10;default:throw Error("mode:"+a);}else if(41>c)switch(a){case 1:return 14;case 2:return 13;case s:return 16;case 8:return 12;default:throw Error("mode:"+
a);}else throw Error("type:"+c);},getLostPoint:function(a){for(var c=a.getModuleCount(),d=0,b=0;b<c;b++)for(var e=0;e<c;e++){for(var f=0,i=a.isDark(b,e),g=-1;1>=g;g++)if(!(0>b+g||c<=b+g))for(var h=-1;1>=h;h++)0>e+h||c<=e+h||0==g&&0==h||i==a.isDark(b+g,e+h)&&f++;5<f&&(d+=3+f-5)}for(b=0;b<c-1;b++)for(e=0;e<c-1;e++)if(f=0,a.isDark(b,e)&&f++,a.isDark(b+1,e)&&f++,a.isDark(b,e+1)&&f++,a.isDark(b+1,e+1)&&f++,0==f||4==f)d+=3;for(b=0;b<c;b++)for(e=0;e<c-6;e++)a.isDark(b,e)&&!a.isDark(b,e+1)&&a.isDark(b,e+
2)&&a.isDark(b,e+3)&&a.isDark(b,e+4)&&!a.isDark(b,e+5)&&a.isDark(b,e+6)&&(d+=40);for(e=0;e<c;e++)for(b=0;b<c-6;b++)a.isDark(b,e)&&!a.isDark(b+1,e)&&a.isDark(b+2,e)&&a.isDark(b+3,e)&&a.isDark(b+4,e)&&!a.isDark(b+5,e)&&a.isDark(b+6,e)&&(d+=40);for(e=f=0;e<c;e++)for(b=0;b<c;b++)a.isDark(b,e)&&f++;a=Math.abs(100*f/c/c-50)/5;return d+10*a}},l={glog:function(a){if(1>a)throw Error("glog("+a+")");return l.LOG_TABLE[a]},gexp:function(a){for(;0>a;)a+=255;for(;256<=a;)a-=255;return l.EXP_TABLE[a]},EXP_TABLE:Array(256),
LOG_TABLE:Array(256)},m=0;8>m;m++)l.EXP_TABLE[m]=1<<m;for(m=8;256>m;m++)l.EXP_TABLE[m]=l.EXP_TABLE[m-4]^l.EXP_TABLE[m-5]^l.EXP_TABLE[m-6]^l.EXP_TABLE[m-8];for(m=0;255>m;m++)l.LOG_TABLE[l.EXP_TABLE[m]]=m;q.prototype={get:function(a){return this.num[a]},getLength:function(){return this.num.length},multiply:function(a){for(var c=Array(this.getLength()+a.getLength()-1),d=0;d<this.getLength();d++)for(var b=0;b<a.getLength();b++)c[d+b]^=l.gexp(l.glog(this.get(d))+l.glog(a.get(b)));return new q(c,0)},mod:function(a){if(0>
this.getLength()-a.getLength())return this;for(var c=l.glog(this.get(0))-l.glog(a.get(0)),d=Array(this.getLength()),b=0;b<this.getLength();b++)d[b]=this.get(b);for(b=0;b<a.getLength();b++)d[b]^=l.gexp(l.glog(a.get(b))+c);return(new q(d,0)).mod(a)}};p.RS_BLOCK_TABLE=[[1,26,19],[1,26,16],[1,26,13],[1,26,9],[1,44,34],[1,44,28],[1,44,22],[1,44,16],[1,70,55],[1,70,44],[2,35,17],[2,35,13],[1,100,80],[2,50,32],[2,50,24],[4,25,9],[1,134,108],[2,67,43],[2,33,15,2,34,16],[2,33,11,2,34,12],[2,86,68],[4,43,27],
[4,43,19],[4,43,15],[2,98,78],[4,49,31],[2,32,14,4,33,15],[4,39,13,1,40,14],[2,121,97],[2,60,38,2,61,39],[4,40,18,2,41,19],[4,40,14,2,41,15],[2,146,116],[3,58,36,2,59,37],[4,36,16,4,37,17],[4,36,12,4,37,13],[2,86,68,2,87,69],[4,69,43,1,70,44],[6,43,19,2,44,20],[6,43,15,2,44,16],[4,101,81],[1,80,50,4,81,51],[4,50,22,4,51,23],[3,36,12,8,37,13],[2,116,92,2,117,93],[6,58,36,2,59,37],[4,46,20,6,47,21],[7,42,14,4,43,15],[4,133,107],[8,59,37,1,60,38],[8,44,20,4,45,21],[12,33,11,4,34,12],[3,145,115,1,146,
116],[4,64,40,5,65,41],[11,36,16,5,37,17],[11,36,12,5,37,13],[5,109,87,1,110,88],[5,65,41,5,66,42],[5,54,24,7,55,25],[11,36,12],[5,122,98,1,123,99],[7,73,45,3,74,46],[15,43,19,2,44,20],[3,45,15,13,46,16],[1,135,107,5,136,108],[10,74,46,1,75,47],[1,50,22,15,51,23],[2,42,14,17,43,15],[5,150,120,1,151,121],[9,69,43,4,70,44],[17,50,22,1,51,23],[2,42,14,19,43,15],[3,141,113,4,142,114],[3,70,44,11,71,45],[17,47,21,4,48,22],[9,39,13,16,40,14],[3,135,107,5,136,108],[3,67,41,13,68,42],[15,54,24,5,55,25],[15,
43,15,10,44,16],[4,144,116,4,145,117],[17,68,42],[17,50,22,6,51,23],[19,46,16,6,47,17],[2,139,111,7,140,112],[17,74,46],[7,54,24,16,55,25],[34,37,13],[4,151,121,5,152,122],[4,75,47,14,76,48],[11,54,24,14,55,25],[16,45,15,14,46,16],[6,147,117,4,148,118],[6,73,45,14,74,46],[11,54,24,16,55,25],[30,46,16,2,47,17],[8,132,106,4,133,107],[8,75,47,13,76,48],[7,54,24,22,55,25],[22,45,15,13,46,16],[10,142,114,2,143,115],[19,74,46,4,75,47],[28,50,22,6,51,23],[33,46,16,4,47,17],[8,152,122,4,153,123],[22,73,45,
3,74,46],[8,53,23,26,54,24],[12,45,15,28,46,16],[3,147,117,10,148,118],[3,73,45,23,74,46],[4,54,24,31,55,25],[11,45,15,31,46,16],[7,146,116,7,147,117],[21,73,45,7,74,46],[1,53,23,37,54,24],[19,45,15,26,46,16],[5,145,115,10,146,116],[19,75,47,10,76,48],[15,54,24,25,55,25],[23,45,15,25,46,16],[13,145,115,3,146,116],[2,74,46,29,75,47],[42,54,24,1,55,25],[23,45,15,28,46,16],[17,145,115],[10,74,46,23,75,47],[10,54,24,35,55,25],[19,45,15,35,46,16],[17,145,115,1,146,116],[14,74,46,21,75,47],[29,54,24,19,
55,25],[11,45,15,46,46,16],[13,145,115,6,146,116],[14,74,46,23,75,47],[44,54,24,7,55,25],[59,46,16,1,47,17],[12,151,121,7,152,122],[12,75,47,26,76,48],[39,54,24,14,55,25],[22,45,15,41,46,16],[6,151,121,14,152,122],[6,75,47,34,76,48],[46,54,24,10,55,25],[2,45,15,64,46,16],[17,152,122,4,153,123],[29,74,46,14,75,47],[49,54,24,10,55,25],[24,45,15,46,46,16],[4,152,122,18,153,123],[13,74,46,32,75,47],[48,54,24,14,55,25],[42,45,15,32,46,16],[20,147,117,4,148,118],[40,75,47,7,76,48],[43,54,24,22,55,25],[10,
45,15,67,46,16],[19,148,118,6,149,119],[18,75,47,31,76,48],[34,54,24,34,55,25],[20,45,15,61,46,16]];p.getRSBlocks=function(a,c){var d=p.getRsBlockTable(a,c);if(void 0==d)throw Error("bad rs block @ typeNumber:"+a+"/errorCorrectLevel:"+c);for(var b=d.length/3,e=[],f=0;f<b;f++)for(var h=d[3*f+0],g=d[3*f+1],j=d[3*f+2],l=0;l<h;l++)e.push(new p(g,j));return e};p.getRsBlockTable=function(a,c){switch(c){case 1:return p.RS_BLOCK_TABLE[4*(a-1)+0];case 0:return p.RS_BLOCK_TABLE[4*(a-1)+1];case 3:return p.RS_BLOCK_TABLE[4*
(a-1)+2];case 2:return p.RS_BLOCK_TABLE[4*(a-1)+3]}};t.prototype={get:function(a){return 1==(this.buffer[Math.floor(a/8)]>>>7-a%8&1)},put:function(a,c){for(var d=0;d<c;d++)this.putBit(1==(a>>>c-d-1&1))},getLengthInBits:function(){return this.length},putBit:function(a){var c=Math.floor(this.length/8);this.buffer.length<=c&&this.buffer.push(0);a&&(this.buffer[c]|=128>>>this.length%8);this.length++}};"string"===typeof h&&(h={text:h});h=r.extend({},{render:"canvas",width:256,height:256,typeNumber:-1,
correctLevel:2,background:"#ffffff",foreground:"#000000"},h);return this.each(function(){var a;if("canvas"==h.render){a=new o(h.typeNumber,h.correctLevel);a.addData(h.text);a.make();var c=document.createElement("canvas");c.width=h.width;c.height=h.height;for(var d=c.getContext("2d"),b=h.width/a.getModuleCount(),e=h.height/a.getModuleCount(),f=0;f<a.getModuleCount();f++)for(var i=0;i<a.getModuleCount();i++){d.fillStyle=a.isDark(f,i)?h.foreground:h.background;var g=Math.ceil((i+1)*b)-Math.floor(i*b),
j=Math.ceil((f+1)*b)-Math.floor(f*b);d.fillRect(Math.round(i*b),Math.round(f*e),g,j)}}else{a=new o(h.typeNumber,h.correctLevel);a.addData(h.text);a.make();c=r("<table></table>").css("width",h.width+"px").css("height",h.height+"px").css("border","0px").css("border-collapse","collapse").css("background-color",h.background);d=h.width/a.getModuleCount();b=h.height/a.getModuleCount();for(e=0;e<a.getModuleCount();e++){f=r("<tr></tr>").css("height",b+"px").appendTo(c);for(i=0;i<a.getModuleCount();i++)r("<td></td>").css("width",
d+"px").css("background-color",a.isDark(e,i)?h.foreground:h.background).appendTo(f)}}a=c;jQuery(a).appendTo(this)})}})(jQuery);
;/*!art-template - Template Engine | http://aui.github.com/artTemplate/*/
!function(){function a(a){return a.replace(t,"").replace(u,",").replace(v,"").replace(w,"").replace(x,"").split(y)}function b(a){return"'"+a.replace(/('|\\)/g,"\\$1").replace(/\r/g,"\\r").replace(/\n/g,"\\n")+"'"}function c(c,d){function e(a){return m+=a.split(/\n/).length-1,k&&(a=a.replace(/\s+/g," ").replace(/<!--[\w\W]*?-->/g,"")),a&&(a=s[1]+b(a)+s[2]+"\n"),a}function f(b){var c=m;if(j?b=j(b,d):g&&(b=b.replace(/\n/g,function(){return m++,"$line="+m+";"})),0===b.indexOf("=")){var e=l&&!/^=[=#]/.test(b);if(b=b.replace(/^=[=#]?|[\s;]*$/g,""),e){var f=b.replace(/\s*\([^\)]+\)/,"");n[f]||/^(include|print)$/.test(f)||(b="$escape("+b+")")}else b="$string("+b+")";b=s[1]+b+s[2]}return g&&(b="$line="+c+";"+b),r(a(b),function(a){if(a&&!p[a]){var b;b="print"===a?u:"include"===a?v:n[a]?"$utils."+a:o[a]?"$helpers."+a:"$data."+a,w+=a+"="+b+",",p[a]=!0}}),b+"\n"}var g=d.debug,h=d.openTag,i=d.closeTag,j=d.parser,k=d.compress,l=d.escape,m=1,p={$data:1,$filename:1,$utils:1,$helpers:1,$out:1,$line:1},q="".trim,s=q?["$out='';","$out+=",";","$out"]:["$out=[];","$out.push(",");","$out.join('')"],t=q?"$out+=text;return $out;":"$out.push(text);",u="function(){var text=''.concat.apply('',arguments);"+t+"}",v="function(filename,data){data=data||$data;var text=$utils.$include(filename,data,$filename);"+t+"}",w="'use strict';var $utils=this,$helpers=$utils.$helpers,"+(g?"$line=0,":""),x=s[0],y="return new String("+s[3]+");";r(c.split(h),function(a){a=a.split(i);var b=a[0],c=a[1];1===a.length?x+=e(b):(x+=f(b),c&&(x+=e(c)))});var z=w+x+y;g&&(z="try{"+z+"}catch(e){throw {filename:$filename,name:'Render Error',message:e.message,line:$line,source:"+b(c)+".split(/\\n/)[$line-1].replace(/^\\s+/,'')};}");try{var A=new Function("$data","$filename",z);return A.prototype=n,A}catch(B){throw B.temp="function anonymous($data,$filename) {"+z+"}",B}}var d=function(a,b){return"string"==typeof b?q(b,{filename:a}):g(a,b)};d.version="3.0.0",d.config=function(a,b){e[a]=b};var e=d.defaults={openTag:"<%",closeTag:"%>",escape:!0,cache:!0,compress:!1,parser:null},f=d.cache={};d.render=function(a,b){return q(a,b)};var g=d.renderFile=function(a,b){var c=d.get(a)||p({filename:a,name:"Render Error",message:"Template not found"});return b?c(b):c};d.get=function(a){var b;if(f[a])b=f[a];else if("object"==typeof document){var c=document.getElementById(a);if(c){var d=(c.value||c.innerHTML).replace(/^\s*|\s*$/g,"");b=q(d,{filename:a})}}return b};var h=function(a,b){return"string"!=typeof a&&(b=typeof a,"number"===b?a+="":a="function"===b?h(a.call(a)):""),a},i={"<":"&#60;",">":"&#62;",'"':"&#34;","'":"&#39;","&":"&#38;"},j=function(a){return i[a]},k=function(a){return h(a).replace(/&(?![\w#]+;)|[<>"']/g,j)},l=Array.isArray||function(a){return"[object Array]"==={}.toString.call(a)},m=function(a,b){var c,d;if(l(a))for(c=0,d=a.length;d>c;c++)b.call(a,a[c],c,a);else for(c in a)b.call(a,a[c],c)},n=d.utils={$helpers:{},$include:g,$string:h,$escape:k,$each:m};d.helper=function(a,b){o[a]=b};var o=d.helpers=n.$helpers;d.onerror=function(a){var b="Template Error\n\n";for(var c in a)b+="<"+c+">\n"+a[c]+"\n\n";"object"==typeof console&&console.error(b)};var p=function(a){return d.onerror(a),function(){return"{Template Error}"}},q=d.compile=function(a,b){function d(c){try{return new i(c,h)+""}catch(d){return b.debug?p(d)():(b.debug=!0,q(a,b)(c))}}b=b||{};for(var g in e)void 0===b[g]&&(b[g]=e[g]);var h=b.filename;try{var i=c(a,b)}catch(j){return j.filename=h||"anonymous",j.name="Syntax Error",p(j)}return d.prototype=i.prototype,d.toString=function(){return i.toString()},h&&b.cache&&(f[h]=d),d},r=n.$each,s="break,case,catch,continue,debugger,default,delete,do,else,false,finally,for,function,if,in,instanceof,new,null,return,switch,this,throw,true,try,typeof,var,void,while,with,abstract,boolean,byte,char,class,const,double,enum,export,extends,final,float,goto,implements,import,int,interface,long,native,package,private,protected,public,short,static,super,synchronized,throws,transient,volatile,arguments,let,yield,undefined",t=/\/\*[\w\W]*?\*\/|\/\/[^\n]*\n|\/\/[^\n]*$|"(?:[^"\\]|\\[\w\W])*"|'(?:[^'\\]|\\[\w\W])*'|\s*\.\s*[$\w\.]+/g,u=/[^\w$]+/g,v=new RegExp(["\\b"+s.replace(/,/g,"\\b|\\b")+"\\b"].join("|"),"g"),w=/^\d[^,]*|,\d[^,]*/g,x=/^,+|,+$/g,y=/^$|,+/;e.openTag="{{",e.closeTag="}}";var z=function(a,b){var c=b.split(":"),d=c.shift(),e=c.join(":")||"";return e&&(e=", "+e),"$helpers."+d+"("+a+e+")"};e.parser=function(a){a=a.replace(/^\s/,"");var b=a.split(" "),c=b.shift(),e=b.join(" ");switch(c){case"if":a="if("+e+"){";break;case"else":b="if"===b.shift()?" if("+b.join(" ")+")":"",a="}else"+b+"{";break;case"/if":a="}";break;case"each":var f=b[0]||"$data",g=b[1]||"as",h=b[2]||"$value",i=b[3]||"$index",j=h+","+i;"as"!==g&&(f="[]"),a="$each("+f+",function("+j+"){";break;case"/each":a="});";break;case"echo":a="print("+e+");";break;case"print":case"include":a=c+"("+b.join(",")+");";break;default:if(/^\s*\|\s*[\w\$]/.test(e)){var k=!0;0===a.indexOf("#")&&(a=a.substr(1),k=!1);for(var l=0,m=a.split("|"),n=m.length,o=m[l++];n>l;l++)o=z(o,m[l]);a=(k?"=":"=#")+o}else a=d.helpers[c]?"=#"+c+"("+b.join(",")+");":"="+a}return a},"function"==typeof define?define(function(){return d}):"undefined"!=typeof exports?module.exports=d:this.template=d}();