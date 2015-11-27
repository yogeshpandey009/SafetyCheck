Ext.define('SafetyCheck.controller.PersonsController', {
    extend: 'Ext.app.Controller',

    stores: ['PersonStore'],

    models: ['PersonModel'],

    views: ['PersonsView'],
    
    refs: [{
        selector: 'personsview grid',
        ref: 'personsGrid'
    }, {
        selector: 'personsview form',
        ref: 'personForm'
    }, {
        selector: 'personsview #onlineSyncMsg',
        ref: 'onlineSyncMsg'
    }],
    
    init: function() {
        this.getPersonStoreStore().getProxy().on('exception', this.onException, this);
        this.listen({
            store: {
//                '#ContactsOffline': {
//                    refresh: this.onDataChange
//                },
                '#PersonStore': {
                    beforesync: this.showSyncingMsg
                }
            },
            component: {
                'personsview button[itemId=reset]': {
                    click: this.onReset
                },
                'personsview button[itemId=create]': {
                    click: this.addPerson
                },
                'personsview button[itemId=update]': {
                    click: this.saveContact
                },
                'personsview button[itemId=clear]': {
                    click: this.clearFilter
                },
                'personsview #search': {
                    change: this.filterPersons
                },
                'personsview grid actioncolumn': {
                    itemclick: this.handleActionColumn
                }
            }
        });
    },
    removePerson: function(gridView, rowIndex, colIndex, item, e) {
        var selection = gridView.getStore().getAt(rowIndex);
        if (selection) {
            gridView.getStore().remove(selection);
        }
    },
    filterPersons: function(txtfld, searchValue) {
        var onlineStore = this.getPersonStoreStore();

        var requestParam = {
            q: searchValue
        };
        onlineStore.load({
            params: requestParam,
            scope: this,
            callback: function(records, operation, success) {
                if (success) {
                    //Ext.Msg.alert('Notice', 'You are in online mode', Ext.emptyFn);
                	var reg = new RegExp(searchValue, "i");
                	onlineStore.filterBy(function(record, id) {
                    	return (reg.test(record.get("id")) || reg.test(record.get("time")) || reg.test(record.get("coordinates")) || reg.test(record.get("magnitude")));
                    }, this);
                }
            }
        });
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
    	var view = this.PersonsviewView();
    	btn.previousSibling('#search').setValue('');
    }
});
