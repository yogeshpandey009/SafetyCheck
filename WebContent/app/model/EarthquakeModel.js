Ext.define('SafetyCheck.model.EarthquakeModel', {
    extend: 'Ext.data.Model',
    idProperty: "uri", //erm, primary key
    fields: [{
        name: 'uri',
        type: 'string'
    }, {
        name: 'time',
        type: 'string'
    }, {
        name: 'coordinates',
        type: 'string'
    }]//,
//    identifier: 'uuid' // IMPORTANT, needed to avoid console warnings!
});