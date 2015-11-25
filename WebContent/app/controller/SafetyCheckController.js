Ext.define('SafetyCheck.controller.SafetyCheckController', {
    extend: 'Ext.app.Controller',

    stores: ['EarthquakeStore'],

    models: ['EarthquakeModel'],

    views: ['SafetyCheckView'],
    
    refs: [{
        selector: 'safetycheckview grid',
        ref: 'safetycheckGrid'
    }, {
        selector: 'safetycheckview form',
        ref: 'safetycheckForm'
    }, {
        selector: 'safetycheckview #onlineSyncMsg',
        ref: 'onlineSyncMsg'
    }],
    
    init: function() {
        this.getEarthquakeStoreStore().getProxy().on('exception', this.onException, this);
        this.listen({
            store: {
//                '#ContactsOffline': {
//                    refresh: this.onDataChange
//                },
                '#EarthquakeStore': {
                    beforesync: this.showSyncingMsg
                }
            },
            component: {
                'safetycheckview button[itemId=reset]': {
                    click: this.onReset
                },
                'safetycheckview button[itemId=create]': {
                    click: this.addContact
                },
                'safetycheckview button[itemId=update]': {
                    click: this.saveContact
                },
                'safetycheckview button[itemId=clear]': {
                    click: this.clearFilter
                },
                'safetycheckview form': {
                    create: this.create
                },
                'safetycheckview #search': {
                    change: this.filterEarthquakes
                },
                'safetycheckview grid actioncolumn': {
                    itemclick: this.handleActionColumn
                }
            }
        });
    },
    onReset: function() {
        this.getSafetyCheckForm().setActiveRecord(null);
    },
    addContact: function() {
        var form = this.getSafetyCheckForm().getForm();
        if (form.isValid()) {
            this.getSafetyCheckForm().fireEvent('create', form, form.getValues());
            form.reset();
        }
    },
    saveContact: function() {
        var active = this.getSafetyCheckForm().activeRecord,
            form = this.getSafetyCheckForm().getForm();
        if (!active) {
            return;
        }
        if (form.isValid()) {
            form.updateRecord(active);
            this.onReset();
        }
    },
    create: function(form, data) {
        this.getContactsGrid().getStore().insert(0, data);
    },
    handleActionColumn: function(column, action, gridView, rowIndex, colIndex, item, e) {
        if (action == 'edit') {
            this.editEarthquake(gridView, rowIndex, colIndex, item, e)
        } else if (action == 'delete') {
            this.removeEarthquake(gridView, rowIndex, colIndex, item, e)
        }
    },
    editEarthquake: function(gridView, rowIndex, colIndex, item, e) {
        var selection = gridView.getStore().getAt(rowIndex);
        this.getSafetyCheckForm().setActiveRecord(selection || null);
    },
    removeEarthquake: function(gridView, rowIndex, colIndex, item, e) {
        var selection = gridView.getStore().getAt(rowIndex);
        if (selection) {
            gridView.getStore().remove(selection);
        }
    },
    filterEarthquakes: function(txtfld, searchValue) {
        var onlineStore = this.getEarthquakeStoreStore();

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
    	var view = this.getSafetycheckViewView();
    	btn.previousSibling('#search').setValue('');
    }
});
