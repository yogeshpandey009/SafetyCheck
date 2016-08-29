Ext.define('SafetyCheck.view.WeatherGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.weathergrid',
	//title: 'Weather',
	store: 'WeatherStore',
    cls: 'weathergrid',
    viewConfig: {
        getRowClass: function(record, rowIndex, rowParams, store){
            return record.get("severity").toLowerCase() === "severe" ? "danger" : "";
        }
    },
    autoScroll: true,
    plugins: {
        ptype: 'bufferedrenderer'
    },
    columns: [{
        text: 'Severity',
        dataIndex: 'severity',
        flex: 1
    }, {
        text: 'Polygon',
        dataIndex: 'points',
        renderer: function (value, metaData) {
            var out = [];
            for(var key in value) {
                out.push(value[key]["latitude"] + " " + value[key]["longitude"]);
            }
            return out.join("</br>");
        },
        flex: 1
    }, {
        text: 'Info',
        dataIndex: 'desc',
        flex: 3,
        renderer: function (value, metaData) {
            return '<div style="white-space:normal">' + value + '</div>';
            //return '<div style="white-space:normal">' + value.toLowerCase().replace(/\.\.\./g, ' ').replace(/\*/g, '') + '</div>';
        }
    }, {
        text: 'Time',
        dataIndex: 'time',
        renderer: Ext.util.Format.dateRenderer('g:i A m/d/Y T'),
        flex: 1
    }]
});