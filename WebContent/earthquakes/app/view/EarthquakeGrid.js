Ext.define('SafetyCheck.view.EarthquakeGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.earthquakegrid',
	//title: 'Earthquakes',
    store: 'EarthquakeStore',
    //cls: 'earthquakegrid',
    viewConfig: {
        getRowClass: function(record, rowIndex, rowParams, store){
            return record.get("magnitude") < 5 ? "" : "danger";
        }
    },
    autoScroll: true,
    plugins: {
        ptype: 'bufferedrenderer'
    },
    columns: [{
        xtype: 'rownumberer',
        text: '#',
        width: 50,
        resizable: true
    }, {
        text: 'Magnitude',
        dataIndex: 'magnitude',
        flex: 1
    }, {
        text: 'Epicenter',
        dataIndex: 'coordinates',
        flex: 1
    }, {
        text: 'Info',
        dataIndex: 'desc',
        flex: 3,
        renderer: function (value, metaData) {
            return '<div style="white-space:normal">' + value + '</div>';
        }
    }, {
        text: 'Time',
        dataIndex: 'time',
        renderer: Ext.util.Format.dateRenderer('g:i A m/d/Y T'),
        flex: 1
    }]
});