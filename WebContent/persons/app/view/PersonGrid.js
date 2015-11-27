Ext.define('SafetyCheck.view.PersonGrid', {
	extend: 'Ext.grid.Panel',
	alias: 'widget.persongrid',
	//title: 'Persons',
    store: 'PersonStore',
    autoScroll: true,
    plugins: {
        ptype: 'bufferedrenderer'
    },
    columns: [{
        text: 'PersonId',
        dataIndex: 'id',
        flex: 1
    }, {
        text: 'Name',
        dataIndex: 'name',
        flex: 1
    }, {
        text: 'location',
        dataIndex: 'location',
        flex: 1
    }, {
        text: 'Co-ordinates',
        dataIndex: 'coordinates',
        flex: 1
    }]
});