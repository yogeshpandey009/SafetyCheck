Ext.define('SafetyCheck.model.EarthquakeModel', {
	extend : 'Ext.data.Model',
	idProperty : "id", //erm, primary key
	fields : [ {
		name : 'id',
		type : 'string'
	}, {
		name : 'magnitude',
		type : 'number'
	}, {
		name : 'time',
		type : 'date',
		dateFormat: 'time'
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
	}]
//,
//    identifier: 'uuid' // IMPORTANT, needed to avoid console warnings!
});