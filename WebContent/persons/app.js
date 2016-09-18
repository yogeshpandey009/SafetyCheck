Ext.Loader.setConfig({
    enabled: true,
    disableCaching: false
});
Ext.tip.QuickTipManager.init();
Ext.application({
    name: 'SafetyCheck',
    appFolder: 'persons/app',
    controllers: [
        'PersonsController'],
    autoCreateViewport: true
});