Ext.define('SafetyCheck.model.PersonModel', {
    extend: 'Ext.data.Model',
    idProperty: "uri", //erm, primary key
    fields: [{
        name: 'uri',
        type: 'string'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'location',
        type: 'string'
    }]//,
//    identifier: 'uuid' // IMPORTANT, needed to avoid console warnings!
});