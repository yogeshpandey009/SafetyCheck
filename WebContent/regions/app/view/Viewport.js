Ext.define('SafetyCheck.view.Viewport', {
    extend: 'Ext.Viewport',    
    layout: 'fit',
    requires: [
        'SafetyCheck.view.RegionsView'
    ],
    initComponent: function() {
        var me = this;        
        Ext.apply(me, {
            items: [
                {
                    xtype: 'regionsview'
                }
            ]
        });             
        me.callParent(arguments);
    }
});