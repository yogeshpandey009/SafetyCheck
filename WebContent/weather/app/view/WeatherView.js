Ext.define('SafetyCheck.view.WeatherView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.weatherview',
    requires: ['SafetyCheck.view.WeatherGrid'],
    //title: 'safetycheck',
    //frame: true,
    padding: '50 10% 0 10%',
    bodyPadding: 20,
    fieldDefaults: {
        labelAlign: 'left'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    autoScroll: true,
    border: 0,
    items: [{
        xtype: 'form',
        activeRecord: null,
        hidden: true,
        frame: true,
        flex: 1,
        defaultType: 'textfield',
        bodyPadding: 5,
        jsonSubmit: true,
        fieldDefaults: {
            //anchor: '100%',
            labelAlign: 'right',
            //msgTarget: 'side',
            width: 300
        },
        items: [{
            xtype: 'textfield',
            fieldLabel: 'Severity',
            name: 'Severity',
            value: "SEVERE",
            allowBlank: false
        }, {
            xtype: 'textfield',
            fieldLabel: 'Latitude',
            name: 'latitude',
            value: "33.5,",
            allowBlank: false
        }, {
            xtype: 'textfield',
            fieldLabel: 'Longitude',
            name: 'longitude',
            value: "-112,",
            allowBlank: false
        }, {
            xtype: 'textarea',
            fieldLabel: 'Description',
            name: 'desc',
            value: 'An weather with magnitude 4.3 occurred near Tempe, AZ (Note: dummy weather)',
            allowBlank: false
        }],
        dockedItems: [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            items: [{
                itemId: 'update',
                text: 'Save',
                disabled: true
            }, {
                text: 'Add Weather',
                itemId: 'create'
            }, {
                text: 'Cancel',
                itemId: 'reset'
            }]
        }],
        setActiveRecord: function(record) {
            this.activeRecord = record;
            if (record) {
                this.show();
                this.down('#update').enable();
                this.getForm().loadRecord(record);
            } else {
                this.down('#update').disable();
                this.getForm().reset();
            }
        }
    }, {
        xtype: 'splitter'
    }, {
        xtype: 'splitter'
    }, {
        xtype: 'fieldcontainer',
        layout: 'hbox',
        items: [{
            xtype: 'textfield',
            itemId: 'search',
            flex: 1
        }, {
            xtype: 'splitter'
        }, {
            xtype: 'button',
            text: 'Search'
        }, {
            xtype: 'splitter'
        }, {
            xtype: 'button',
            text: 'Clear Filters',
            itemId: 'clear'
        }]
    }, {
        xtype: 'splitter'
    }, {
        xtype: 'box',
        html: 'Double click an weather to show impacted persons'
    }, {
        xtype: 'splitter'
    }, {
        xtype: 'weathergrid',
    	flex: 1
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'displayfield',
            itemId: 'onlineSyncMsg'
            },
            '->', {
            xtype: 'button',
            text: '+ Add',
            enableToggle: true,
            toggleHandler: function(btn, state) {
                if (state) {
                    btn.up('weatherview').down('form').show();
                } else {
                    btn.up('weatherview').down('form').hide();
                }
            }
        }]
    }]
});