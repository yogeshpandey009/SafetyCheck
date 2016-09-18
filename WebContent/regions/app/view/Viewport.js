Ext.define('SafetyCheck.view.Viewport', {
	extend : 'Ext.Viewport',
	layout : 'border',
	requires : [ 'SafetyCheck.view.RegionsView' ],
	initComponent : function() {
		var me = this;
		Ext.apply(me, {
			items : [{
		    	xtype: 'box',
		    	region: 'north',
		    	html: "<nav id=\"siteNav\" class=\"navbar navbar-default navbar-fixed-top\" role=\"navigation\">"+
				"        <div class=\"container\">"+
				"            <div class=\"navbar-header\">"+
				"                <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#navbar\">"+
				"                    <span class=\"sr-only\">Toggle navigation<\/span>"+
				"                    <span class=\"icon-bar\"><\/span>"+
				"                    <span class=\"icon-bar\"><\/span>"+
				"                    <span class=\"icon-bar\"><\/span>"+
				"                <\/button>"+
				"                <a class=\"navbar-brand\" href=\"#\">"+
				"                	<span class=\"glyphicon glyphicon-fire\"><\/span> "+
				"                	SC"+
				"                <\/a>"+
				"            <\/div>"+
				"            <!-- Navbar links -->"+
				"            <div class=\"collapse navbar-collapse\" id=\"navbar\">"+
				"                <ul class=\"nav navbar-nav navbar-right\">"+
				"                    <li class=\"active\">"+
				"                        <a href=\"index.html\">Home<\/a>"+
				"                    <\/li>"+
				"					<li class=\"dropdown\">"+
				"						<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">Services <span class=\"caret\"><\/span><\/a>"+
				"						<ul class=\"dropdown-menu\" aria-labelledby=\"about-us\">"+
				"							<li><a href=\"earthquakes.html\">Earthquakes<\/a><\/li>"+
				"							<li><a href=\"weather.html\">Weather<\/a><\/li>"+
				"							<li><a href=\"persons.html\">Persons<\/a><\/li>"+
				"							<li><a href=\"regions.html\">Regions<\/a><\/li>"+
				"						<\/ul>"+
				"					<\/li>"+
				"                    <li>"+
				"                        <a href=\"#\">Contact<\/a>"+
				"                    <\/li>"+
				"                <\/ul>"+
				"            <\/div>"+
				"        <\/div>"+
				"<\/nav>"
		    }, {
		    	region: 'center',
				xtype : 'regionsview'
			}, {
		    	xtype: 'box',
		    	region: 'south',
				html: "<footer class=\"page-footer\">"+
				"        <div class=\"small-print\">"+
				"        	<div class=\"container\">"+
				"        		<p>Copyright &copy+ SER594 : Group 15 2015<\/p>"+
				"        	<\/div>"+
				"        <\/div>"+
				"<\/footer>"
		    }]
		});
		me.callParent(arguments);
	}
});