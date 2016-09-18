Ext.Loader.setConfig({
    enabled: true,
    disableCaching: false
});
Ext.tip.QuickTipManager.init();
Ext.application({
    name: 'SafetyCheck',
    appFolder: 'regions/app',
    controllers: [
        'RegionsController'],
    autoCreateViewport: true
});