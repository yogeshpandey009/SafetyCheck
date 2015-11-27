Ext.define('SafetyCheck.model.PersonModel', {
    extend: 'Ext.data.Model',
    idProperty: "uri", //erm, primary key
    fields: [{
        name: 'id',
        type: 'string'
    }, {
        name: 'name',
        type: 'string'
    }, {
        name: 'location',
        type: 'string'
    }, {
		name : 'latitude',
		type : 'number'
	}, {
		name : 'longitude',
		type : 'number'
	}, {
		name : 'coordinates',
		type : 'string',
		convert : function(v, rec) {
			return rec.get('latitude') + " " + rec.get('longitude');
		}
	}]//,
//    identifier: 'uuid' // IMPORTANT, needed to avoid console warnings!
});