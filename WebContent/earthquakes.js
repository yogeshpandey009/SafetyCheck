Ext.Loader.setConfig({
    enabled: true
});
Ext.tip.QuickTipManager.init();
Ext.application({
    name: 'SafetyCheck',
    controllers: [
        'EarthquakesController'],
    autoCreateViewport: true
});