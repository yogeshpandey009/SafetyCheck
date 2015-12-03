Ext.define('SafetyCheck.view.RegionGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.regiongrid',
	//title: 'Persons',
    store: 'RegionStore',
    autoScroll: true,
    plugins: {
        ptype: 'bufferedrenderer'
    },
    columns: [{
        text: 'RegionId',
        dataIndex: 'id',
        flex: 1
    }, {
        text: 'Name',
        dataIndex: 'name',
        flex: 1
    }, {
        text: 'Co-ordinates',
        dataIndex: 'coordinates',
        flex: 1
    }]
});