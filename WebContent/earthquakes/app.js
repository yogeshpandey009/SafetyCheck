Ext.Loader.setConfig({
    enabled: true
});
Ext.tip.QuickTipManager.init();
Ext.application({
    name: 'SafetyCheck',
    appFolder: 'earthquakes/app',
    controllers: [
        'EarthquakesController'],
    autoCreateViewport: true
});