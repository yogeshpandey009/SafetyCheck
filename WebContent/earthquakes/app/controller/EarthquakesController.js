Ext.define('SafetyCheck.controller.EarthquakesController', {
	extend : 'Ext.app.Controller',

	stores : [ 'EarthquakeStore' ],

	models : [ 'EarthquakeModel' ],

	views : [ 'EarthquakesView' ],

	refs : [ {
		selector : 'earthquakesview grid',
		ref : 'earthquakesGrid'
	}, {
		selector : 'earthquakesview form',
		ref : 'earthquakeForm'
	}, {
		selector : 'earthquakesview #onlineSyncMsg',
		ref : 'onlineSyncMsg'
	} ],

	init : function() {
		this.getEarthquakeStoreStore().getProxy().on('exception',
				this.onException, this);
		this.listen({
			store : {
				//                '#ContactsOffline': {
				//                    refresh: this.onDataChange
				//                },
				'#EarthquakeStore' : {
					beforesync : this.showSyncingMsg
				}
			},
			component : {
				'earthquakesview button[itemId=reset]' : {
					click : this.onReset
				},
				'earthquakesview button[itemId=create]' : {
					click : this.addEarthquake
				},
				'earthquakesview button[itemId=update]' : {
					click : this.saveContact
				},
				'earthquakesview button[itemId=clear]' : {
					click : this.clearFilter
				},
				'earthquakesview form' : {
					create : this.create
				},
				'earthquakesview #search' : {
					change : this.filterEarthquakes
				},
				'earthquakesview grid actioncolumn' : {
					itemclick : this.handleActionColumn
				}
			}
		});
	},
	onReset : function() {
		this.getEarthquakeForm().setActiveRecord(null);
	},
	addEarthquake : function() {
		var form = this.getEarthquakeForm().getForm();
		if (form.isValid()) {
			this.getEarthquakeForm()
					.fireEvent('create', this.getEarthquakeForm());
			//form.reset();
		}
	},
	saveContact : function() {
		var active = this.getEarthquakeForm().activeRecord, form = this
				.getEarthquakeForm().getForm();
		if (!active) {
			return;
		}
		if (form.isValid()) {
			form.updateRecord(active);
			this.onReset();
		}
	},
	create : function(form) {
		//this.getEarthquakesGrid().getStore().insert(0, data);
		/*Ext.Ajax.request({
			url : 'api/earthquake',
			method : 'POST',
			params : {
				data : Ext.JSON.encode(data)
			},
			success : function(response) {
				// do something
			},
			failure : function(response) {
				alert("Error: " - response.responseText);
			}
		});*/
		form.submit({ 
		    url: 'api/earthquake', 
		    method: 'POST', 
		    success: function(form, action) { 
		        Ext.Msg.alert('Success', action.result.msg); 
		    },
			fsuccess: function(form, action) {
				Ext.Msg.alert('Error', action.result.msg);
			}
		});  
	},
	handleActionColumn : function(column, action, gridView, rowIndex, colIndex,
			item, e) {
		if (action == 'edit') {
			this.editEarthquake(gridView, rowIndex, colIndex, item, e)
		} else if (action == 'delete') {
			this.removeEarthquake(gridView, rowIndex, colIndex, item, e)
		}
	},
	editEarthquake : function(gridView, rowIndex, colIndex, item, e) {
		var selection = gridView.getStore().getAt(rowIndex);
		this.getEarthquakeForm().setActiveRecord(selection || null);
	},
	removeEarthquake : function(gridView, rowIndex, colIndex, item, e) {
		var selection = gridView.getStore().getAt(rowIndex);
		if (selection) {
			gridView.getStore().remove(selection);
		}
	},
	filterEarthquakes : function(txtfld, searchValue) {
		var onlineStore = this.getEarthquakeStoreStore();

		var requestParam = {
			q : searchValue
		};
		onlineStore.load({
			params : requestParam,
			scope : this,
			callback : function(records, operation, success) {
				if (success) {
					//Ext.Msg.alert('Notice', 'You are in online mode', Ext.emptyFn);
					var reg = new RegExp(searchValue, "i");
					onlineStore.filterBy(function(record, id) {
						return (reg.test(record.get("id"))
								|| reg.test(record.get("time"))
								|| reg.test(record.get("coordinates")) || reg
								.test(record.get("magnitude")));
					}, this);
				}
			}
		});
	},
	//        If app is offline a Proxy exception will be thrown. If that happens then use
	//        the fallback / local stoage store instead
	onException : function(proxy, response, operation) {
		//localStore.load(); // This causes the "loading" mask to disappear
		//this.getContactsGrid().getView().setLoading(false);
		this.getOnlineSyncMsg().setValue(
				"Exception occured in fetching data...");
		this.getOnlineSyncMsg().setFieldStyle({
			"color" : "red"
		});
		//Ext.Msg.alert('Notice', 'You are in offline mode', Ext.emptyFn); //alert the user that they are in offline mode
	},
	showSyncingMsg : function() {
		this.getOnlineSyncMsg().setValue("Trying to connect to server...");
		this.getOnlineSyncMsg().setFieldStyle({
			"color" : "blue"
		});
	},
	clearFilter : function(btn) {
		var view = this.EarthquakesviewView();
		btn.previousSibling('#search').setValue('');
	}
});
