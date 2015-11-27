Ext.define('SafetyCheck.view.Viewport', {
    extend: 'Ext.Viewport',    
    layout: 'fit',
    requires: [
        'SafetyCheck.view.PersonsView'
    ],
    initComponent: function() {
        var me = this;        
        Ext.apply(me, {
            items: [
                {
                    xtype: 'personsview'
                }
            ]
        });             
        me.callParent(arguments);
    }
});