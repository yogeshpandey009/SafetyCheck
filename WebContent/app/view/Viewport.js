Ext.define('SafetyCheck.view.Viewport', {
    extend: 'Ext.Viewport',    
    layout: 'fit',
    requires: [
        'SafetyCheck.view.SafetyCheckView'
    ],
    initComponent: function() {
        var me = this;        
        Ext.apply(me, {
            items: [
                {
                    xtype: 'safetycheckview'
                }
            ]
        });             
        me.callParent(arguments);
    }
});