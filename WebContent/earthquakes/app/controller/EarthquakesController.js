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

	earthquakesUrl: 'api/earthquakes',

	init : function() {
		var earthquakeStore = this.getEarthquakeStoreStore();
		earthquakeStore.getProxy().on('exception',
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
                'earthquakesview grid': {
                    itemdblclick: this.onDoubleClick
                }
			}
		});
		var queryParam = location.search.substr(1);
		var earthquakesUrl = this.earthquakesUrl;
		if(queryParam != '') {
			earthquakesUrl = earthquakesUrl + "?" + queryParam;
		}
		this.loadEarthquakes(earthquakesUrl);
	},
	loadEarthquakes: function(earthquakesUrl) {
		var earthquakeStore = this.getEarthquakeStoreStore();
		earthquakeStore.load({
			url: earthquakesUrl
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
		var me = this;
		form.submit({ 
		    url: 'api/earthquake', 
		    method: 'POST', 
		    success: function(form, action) { 
		        Ext.Msg.alert('Success', action.result.msg);
		        me.loadEarthquakes(me.earthquakesUrl);
		    },
			fsuccess: function(form, action) {
				Ext.Msg.alert('Error', action.result.msg);
			}
		});  
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
		var earthquakeStore = this.getEarthquakeStoreStore();
		var reg = new RegExp(searchValue, "i");
		earthquakeStore.filterBy(function(record, id) {
			return (reg.test(record.get("id"))
					|| reg.test(record.get("time"))
					|| reg.test(record.get("coordinates")) || reg
					.test(record.get("magnitude")));
		}, this);
	
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
		btn.previousSibling('#search').setValue('');
	},
    onDoubleClick: function(grid, record) {
    	var id = record.get('id');
    	var url = 'persons.html?earthquake=' + id.split('#')[1];
    	window.location = url;
    }
});
