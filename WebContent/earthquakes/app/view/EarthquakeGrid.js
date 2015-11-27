Ext.define('SafetyCheck.view.EarthquakeGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.earthquakegrid',
	//title: 'Earthquakes',
    store: 'EarthquakeStore',
    autoScroll: true,
    columns: [{
        text: 'EarhquakeId',
        dataIndex: 'id',
        flex: 1
    }, {
        text: 'Magnitude',
        dataIndex: 'magnitude',
        flex: 1
    }, {
        text: 'Co-ordinates',
        dataIndex: 'coordinates',
        flex: 1
    }, {
        text: 'Time',
        dataIndex: 'time',
        flex: 1
    }]
});