<%-- 
    Document   : autocomplete_property
    Created on : Apr 21, 2012, 2:05:47 PM
    Author     : J. Nathanael Philipp
--%>

<%@page import="java.util.Enumeration"%>
<%@page import="LGDEditTool.Functions"%>
<%@page import="LGDEditTool.db.DatabaseBremen"%>
<%
try {
	DatabaseBremen database = DatabaseBremen.getInstance();
	database.connect();
	String query = request.getParameter("q");

	if ( query.contains(":") ) {
		String expand = Functions.expand(getServletContext(), query.substring(0, query.indexOf(":")));
		out.println(expand + query.substring(query.indexOf(":") + 1, query.length()));

		Object[][] a = database.execute("SELECT property FROM (SELECT property FROM lgd_map_resource_k WHERE property != '' UNION ALL SELECT property FROM lgd_map_resource_kv WHERE property != '' UNION ALL SELECT property FROM lgd_map_literal WHERE property != '' UNION ALL SELECT property FROM lgd_map_property) as prop WHERE property LIKE '" + expand + "%" + query.substring(query.indexOf(":") + 1, query.length()) + "%' GROUP BY property ORDER BY property");

		for ( int i = 0; i < a.length; i++ ) {
			out.println(a[i][0].toString());
		}
	}
	else {
		Enumeration<Object> a = Functions.getNamespaceKeys(getServletContext());

		while ( a.hasMoreElements() ) {
			out.println(a.nextElement().toString() + ":");
		}
	}

	database.disconnect();
}
catch ( Exception e ) {
	out.println(e.toString());
}
%>