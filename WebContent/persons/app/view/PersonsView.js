Ext.define('SafetyCheck.view.PersonsView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.personsview',
    requires: [
               'SafetyCheck.view.PersonGrid'
    ],
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
        xtype: 'persongrid',
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
