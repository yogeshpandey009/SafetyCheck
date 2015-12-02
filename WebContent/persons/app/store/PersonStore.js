Ext.define('SafetyCheck.store.PersonStore', {
	extend : 'Ext.data.Store',
	model : 'SafetyCheck.model.PersonModel',
	storeId : 'personStore',
	autoLoad : false,
	//autoSync: true,
	proxy : {
		//type : 'memory',
		reader : {
			type : 'json',
			root: 'data',
			successProperty: 'success',
			messageProperty: 'msg'
		},
		type: 'rest',
		//url: 'api/persons',
		pageParam : false, //to remove param "page"
		startParam : false, //to remove param "start"
		limitParam : false, //to remove param "limit"
		noCache : false, //to remove param "_dc"
		//        reader: {
		//            type: 'json',
		//            root: 'contacts'
		//        },
		//        writer: {
		//            type: 'json'
		//        }//,
		//        headers: {
		//            //encodeRequest: true,
		//            'X-USER': 'yogeshpandey009@gmail.com',
		//            'Content-Type': 'application/x-www-form-urlencoded'
		//        }
	}/*,
	data : [ {
		uri : 'entry1',
		time : "2:57 PM November 21, 2015",
		coordinates : "13.42°N 64.56°E"
	}, {
		uri : 'entry2',
		time : "1:43 PM December 2, 2015",
		coordinates : "33.50°N 36.30°E"
	}, {
		uri : 'entry3',
		time : "5:32 AM January 21, 2015",
		coordinates : "33.66°N 73.16°E"
	}, {
		uri : 'entry4',
		time : "9:57 PM February 21, 2015",
		coordinates : "35.42°S 149.12°E"
	} ]*/
});
