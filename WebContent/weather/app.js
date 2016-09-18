Ext.Loader.setConfig({
    enabled: true,
    disableCaching: false
});
Ext.tip.QuickTipManager.init();
Ext.application({
    name: 'SafetyCheck',
    appFolder: 'weather/app',
    controllers: ['WeatherController'],
    autoCreateViewport: true
});