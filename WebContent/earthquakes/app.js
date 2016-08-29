Ext.Loader.setConfig({
    enabled: true,
    disableCaching: false
});
Ext.tip.QuickTipManager.init();
Ext.application({
    name: 'SafetyCheck',
    appFolder: 'earthquakes/app',
    controllers: [
        'EarthquakesController'],
    autoCreateViewport: true
});