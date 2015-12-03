Ext.define('SafetyCheck.controller.RegionsController', {
    extend: 'Ext.app.Controller',

    stores: ['RegionStore'],

    models: ['RegionModel'],

    views: ['RegionsView'],
    
    refs: [{
        selector: 'regionsview grid',
        ref: 'regionsGrid'
    }, {
        selector: 'regionsview form',
        ref: 'regionForm'
    }, {
        selector: 'regionsview #onlineSyncMsg',
        ref: 'onlineSyncMsg'
    }],
    
    init: function() {
    	var regionStore = this.getRegionStoreStore();
    	regionStore.getProxy().on('exception', this.onException, this);
        this.listen({
            store: {
//                '#ContactsOffline': {
//                    refresh: this.onDataChange
//                },
                '#RegionStore': {
                    beforesync: this.showSyncingMsg
                }
            },
            component: {
                'regionsview button[itemId=reset]': {
                    click: this.onReset
                },
                'regionsview button[itemId=create]': {
                    click: this.addRegion
                },
                'regionsview button[itemId=update]': {
                    click: this.saveContact
                },
                'regionsview button[itemId=clear]': {
                    click: this.clearFilter
                },
                'regionsview #search': {
                    change: this.filterRegions
                }
            }
        });
		var regionsUrl = "api/regions";
		regionStore.load({
			url: regionsUrl
		});
    },
    removeRegion: function(gridView, rowIndex, colIndex, item, e) {
        var selection = gridView.getStore().getAt(rowIndex);
        if (selection) {
            gridView.getStore().remove(selection);
        }
    },
    filterRegions: function(txtfld, searchValue) {
        var regionStore = this.getRegionStoreStore();
        //Ext.Msg.alert('Notice', 'You are in online mode', Ext.emptyFn);
    	var reg = new RegExp(searchValue, "i");
    	regionStore.filterBy(function(record, id) {
        	return (reg.test(record.get("id")) || reg.test(record.get("name")) || reg.test(record.get("coordinates")));
        }, this);
    
    },
    //        If app is offline a Proxy exception will be thrown. If that happens then use
    //        the fallback / local stoage store instead
    onException: function(proxy, response, operation) {
        //localStore.load(); // This causes the "loading" mask to disappear
        //this.getContactsGrid().getView().setLoading(false);
        this.getOnlineSyncMsg().setValue("Exception occured in fetching data...");
        this.getOnlineSyncMsg().setFieldStyle({"color": "red"});
        //Ext.Msg.alert('Notice', 'You are in offline mode', Ext.emptyFn); //alert the user that they are in offline mode
    },
    showSyncingMsg: function(){
    	this.getOnlineSyncMsg().setValue("Trying to connect to server...");
    	this.getOnlineSyncMsg().setFieldStyle({"color": "blue"});
    },
    clearFilter: function(btn) {
    	btn.previousSibling('#search').setValue('');
    }
});
