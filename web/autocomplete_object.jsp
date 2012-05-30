<%--
    This file is part of LGDEditTool (LGDET).

    LGDET is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    LGDET is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with LGDET.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%-- 
    Document   : autocomplete_object
    Created on : Apr 21, 2012, 2:05:47 PM
    Author     : J. Nathanael Philipp
--%>

<%@page import="java.util.Enumeration"%>
<%@page import="LGDEditTool.Functions"%>
<%@page import="LGDEditTool.db.LGDDatabase"%>
<%
try {
	LGDDatabase database = LGDDatabase.getInstance();
	database.connect();
	String query = request.getParameter("q");

	if ( query.contains(":") ) {
		String expand = Functions.expand(getServletContext(), query.substring(0, query.indexOf(":")));
		out.println(expand + query.substring(query.indexOf(":") + 1, query.length()));

		Object[][] a = database.execute("SELECT object FROM (SELECT object FROM lgd_map_resource_k WHERE object != '' UNION ALL SELECT object FROM lgd_map_resource_kv WHERE object != '') as obj WHERE object LIKE '" + expand + "%" + query.substring(query.indexOf(":") + 1, query.length()) + "%' GROUP BY object ORDER BY object");

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