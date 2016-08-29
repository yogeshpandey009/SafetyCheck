Ext.define('SafetyCheck.controller.WeatherController', {
	extend : 'Ext.app.Controller',

	stores : [ 'WeatherStore' ],

	models : [ 'WeatherModel' ],

	views : [ 'WeatherView' ],

	refs : [ {
		selector : 'weatherview grid',
		ref : 'weatherGrid'
	}, {
		selector : 'weatherview form',
		ref : 'weatherForm'
	}, {
		selector : 'weatherview #onlineSyncMsg',
		ref : 'onlineSyncMsg'
	} ],

	weatherUrl: 'api/weather',

	init : function() {
		var weatherStore = this.getWeatherStoreStore();
		weatherStore.getProxy().on('exception',
				this.onException, this);
		this.listen({
			store : {
				//                '#ContactsOffline': {
				//                    refresh: this.onDataChange
				//                },
				'#WeatherStore' : {
					beforesync : this.showSyncingMsg
				}
			},
			component : {
				'weatherview button[itemId=reset]' : {
					click : this.onReset
				},
				'weatherview button[itemId=create]' : {
					click : this.addWeather
				},
				'weatherview button[itemId=update]' : {
					click : this.saveContact
				},
				'weatherview button[itemId=clear]' : {
					click : this.clearFilter
				},
				'weatherview form' : {
					create : this.create
				},
				'weatherview #search' : {
					change : this.filterWeather
				},
                'weatherview grid': {
                    itemdblclick: this.onDoubleClick
                }
			}
		});
		var queryParam = location.search.substr(1);
		var weatherUrl = this.weatherUrl;
		if(queryParam != '') {
			weatherUrl = weatherUrl + "?" + queryParam;
		}
		this.loadWeather(weatherUrl);
	},
	loadWeather: function(weatherUrl) {
		var weatherStore = this.getWeatherStoreStore();
		weatherStore.load({
			url: weatherUrl
		});
	},
	onReset : function() {
		this.getWeatherForm().setActiveRecord(null);
	},
	addWeather : function() {
		var form = this.getWeatherForm().getForm();
		if (form.isValid()) {
			this.getWeatherForm()
					.fireEvent('create', this.getWeatherForm());
			//form.reset();
		}
	},
	create : function(form) {
		//this.getWeatherGrid().getStore().insert(0, data);
		/*Ext.Ajax.request({
			url : 'api/weather',
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
		    url: 'api/weather', 
		    method: 'POST',
		    params: {
                time: new Date().getTime()
            },
		    success: function(form, action) { 
		        Ext.Msg.alert('Success', action.result.msg);
		        me.loadWeather(me.weatherUrl);
		    },
			fsuccess: function(form, action) {
				Ext.Msg.alert('Error', action.result.msg);
			}
		});  
	},
	editWeather : function(gridView, rowIndex, colIndex, item, e) {
		var selection = gridView.getStore().getAt(rowIndex);
		this.getWeatherForm().setActiveRecord(selection || null);
	},
	removeWeather : function(gridView, rowIndex, colIndex, item, e) {
		var selection = gridView.getStore().getAt(rowIndex);
		if (selection) {
			gridView.getStore().remove(selection);
		}
	},
	filterWeather : function(txtfld, searchValue) {
		var weatherStore = this.getWeatherStoreStore();
		var reg = new RegExp(searchValue, "i");
		weatherStore.filterBy(function(record, id) {
			return (reg.test(record.get("id"))
					|| reg.test(record.get("time"))
					|| reg.test(record.get("points"))
					|| reg.test(record.get("severity")));
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
    	var url = 'persons.html?weather=' + id.split('#')[1];
    	window.location = url;
    }
});
