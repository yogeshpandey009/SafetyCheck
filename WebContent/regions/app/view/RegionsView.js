Ext.define('SafetyCheck.view.RegionsView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.regionsview',
    requires: [
               'SafetyCheck.view.RegionGrid'
    ],
    // title: 'safetycheck',
    // frame: true,
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
        xtype: 'regiongrid',
        flex: 1
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        items: [{
        	xtype: 'displayfield',
        	itemId: 'onlineSyncMsg'
        }]
    }]
});
