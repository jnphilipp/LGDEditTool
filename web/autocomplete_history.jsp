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

<%@page import="LGDEditTool.Functions"%>
<%@page import="LGDEditTool.SiteHandling.User"%>
<%-- 
    Document   : autocomplete_search
    Created on : 17.02.2012, 19:55:28
    Author     : J. Nathanael Philipp
--%>

<%@page import="LGDEditTool.db.DatabaseBremen"%>
<%
try {
	DatabaseBremen database = DatabaseBremen.getInstance();
	database.connect();
	User.getInstance().createUser(request);
	String query = request.getParameter("q");

	if ( query.contains("*") )
		query = query.replaceAll("\\*", "%");

	if ( !query.endsWith("%") )
		query += "%";

	Object[][] a = database.execute("SELECT k, v, count FROM " + User.getInstance().getView() + "_history WHERE UPPER(k) LIKE UPPER('" + query + "') OR UPPER(v) LIKE UPPER('" + query + "') OR (k, v) IN (SELECT k, v FROM lgd_map_label WHERE UPPER(label) LIKE UPPER('" + query + "'))");

	for ( int i = 0; i < a.length; i++ ) {
		out.println(a[i][0].toString() + " (" + (a[i][1].toString().equals("") ? "" : (a[i][1].toString() + ", ")) + a[i][2].toString() + ")");
	}

	database.disconnect();
}
catch ( Exception e ) {
	out.println(e.toString());
}
%>