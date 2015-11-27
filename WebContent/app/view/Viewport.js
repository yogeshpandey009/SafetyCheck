Ext.define('SafetyCheck.view.Viewport', {
    extend: 'Ext.Viewport',    
    layout: 'fit',
    requires: [
        'SafetyCheck.view.EarthquakesView'
    ],
    initComponent: function() {
        var me = this;        
        Ext.apply(me, {
            items: [
                {
                    xtype: 'earthquakesview'
                }
            ]
        });             
        me.callParent(arguments);
    }
});