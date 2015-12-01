Ext.define('SafetyCheck.view.EarthquakeGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.earthquakegrid',
	//title: 'Earthquakes',
    store: 'EarthquakeStore',
    cls: 'earthquakegrid',
    viewConfig: {
        getRowClass: function(record, rowIndex, rowParams, store){
            return record.get("magnitude") < 4 ? "" : "danger";
        }
    },
    autoScroll: true,
    plugins: {
        ptype: 'bufferedrenderer'
    },
    columns: [{
        text: 'EarhquakeId',
        dataIndex: 'id',
        flex: 1
    }, {
        text: 'Magnitude',
        dataIndex: 'magnitude',
        flex: 1
    }, {
        text: 'Epicenter',
        dataIndex: 'coordinates',
        flex: 1
    }, {
        text: 'Time',
        dataIndex: 'time',
        flex: 1
    }]
});