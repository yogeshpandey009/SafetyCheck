Ext.define('SafetyCheck.view.SafetyCheckView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.safetycheckview',
    title: 'safetycheck',
    frame: true,
    bodyPadding: 5,
    fieldDefaults: {
        labelAlign: 'left'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    autoScroll: true,
    padding: '5 0 0',
    border: 0,
    items: [{
        xtype: 'form',
    	activeRecord: null,
        hidden: true,
        frame: true,
        defaultType: 'textfield',
        bodyPadding: 5,
        fieldDefaults: {
            //anchor: '100%',
            labelAlign: 'right',
            //msgTarget: 'side',
            width: 300
        },
        items: [{
            fieldLabel: 'Uri',
            name: 'uri',
            allowBlank: false
        }, {
            fieldLabel: 'Time',
            name: 'time',
            allowBlank: false
        }, {
            fieldLabel: 'Co-ordinates',
            name: 'coordinates',
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
                text: 'Add Earthquake',
                itemId: 'create'
            }, {
                text: 'Canel',
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
        xtype: 'splitter'
    }, {
        xtype: 'grid',
        store: 'EarthquakeStore',
        autoScroll: true,
        columns: [{
            text: 'Earhquake',
            dataIndex: 'uri',
            flex: 1
        }, {
            text: 'Time',
            dataIndex: 'time',
            flex: 1
        }, {
            text: 'Co-ordinates',
            dataIndex: 'coordinates',
            flex: 1
        }, {
            xtype: 'actioncolumn',
            width: 100,
            align: 'center',
            items: [{
                icon: 'app/resources/images/cog_edit.png', // Use a URL in the icon config
                tooltip: 'Edit',
                width: 50,
                itemId: 'editAction',
                handler: function(gridView, rowIndex, colIndex, item, e) {
                    this.fireEvent('itemclick', this, 'edit', gridView, rowIndex, colIndex, item, e);
                }
            }, {
                xtype: 'splitter'
            }, {
                icon: 'app/resources/images/delete.png',
                tooltip: 'Delete',
                width: 50,
                itemId: 'deleteAction',
                handler: function(gridView, rowIndex, colIndex, item, e) {
                    this.fireEvent('itemclick', this, 'delete', gridView, rowIndex, colIndex, item, e);
                }
            }]
        }]
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
                    btn.up('safetycheckview').down('form').show();
                } else {
                    btn.up('safetycheckview').down('form').hide();
                }
            }
        }]
    }]
});
