Ext.define('SafetyCheck.model.WeatherModel', {
	extend : 'Ext.data.Model',
	idProperty : "id", //erm, primary key
	fields : [ {
		name : 'id',
		type : 'string'
	}, {
		name : 'desc',
		type : 'string'
	}, {
		name : 'severity',
		type : 'string'
	}, {
		name : 'time',
		type : 'date',
		dateFormat: 'time'
	}, {
		name : 'points',
		type: 'auto'
	}]
//,
//    identifier: 'uuid' // IMPORTANT, needed to avoid console warnings!
});