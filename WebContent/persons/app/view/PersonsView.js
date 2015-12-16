Ext.define('SafetyCheck.view.PersonsView', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.personsview',
    requires: [
               'SafetyCheck.view.PersonGrid'
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
    	xtype: 'box',
    	html: 'Double click a person to show impactedBy which earthquakes'
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
