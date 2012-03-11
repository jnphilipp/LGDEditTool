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
    Document   : autocomplete_search
    Created on : 17.02.2012, 19:55:28
    Author     : J. Nathanael Philipp
--%>

<%@page import="LGDEditTool.db.DatabaseBremen"%>
<%
try {
	DatabaseBremen database = new DatabaseBremen();
	//database.connect("192.168.0.101:5432/bremen", "lgd", "lgd", true);
	database.connect();
	String query = request.getParameter("q");
	/*Object[][] a = database.execute("SELECT k, count(k) FROM lgd_map_label WHERE UPPER(k) LIKE UPPER('%" + query + "%') GROUP BY k ORDER BY k");

	for ( int i = 0; i < a.length; i++ ) {
		out.println(a[i][0].toString() + " (" + a[i][1].toString() + ")");
	}

	a = database.execute("SELECT v, count(v) FROM lgd_map_label WHERE UPPER(v) LIKE UPPER('%" + query + "%') GROUP BY v ORDER BY v");

	for ( int i = 0; i < a.length; i++ ) {
		out.println(a[i][0].toString() + " (" + a[i][1].toString() + ")");
	}

	a = database.execute("SELECT label, count(label) FROM lgd_map_label WHERE UPPER(label) LIKE UPPER('%" + query + "%') GROUP BY label ORDER BY label");

	for ( int i = 0; i < a.length; i++ ) {
		out.println(a[i][0].toString() + " (" + a[i][1].toString() + ")");
	}*/

	Object[][] a = database.execute("SELECT k, COUNT(k) + (SELECT COUNT(k) FROM lgd_map_resource_k WHERE k=(lgd_map_resource_kv.k) GROUP BY k) FROM lgd_map_resource_kv WHERE UPPER(k) LIKE UPPER('%" + query.toUpperCase() + "%') GROUP BY k ORDER BY k");

	for ( int i = 0; i < a.length; i++ ) {
		out.println(a[i][0].toString() + " (" + a[i][1].toString() + ")");
	}

	/*a = database.execute("SELECT k, count(k) FROM lgd_map_resource_kv WHERE UPPER(object) LIKE UPPER('%" + query.toUpperCase() + "%') GROUP BY object ORDER BY object");

	for ( int i = 0; i < a.length; i++ ) {
		out.println(a[i][0].toString() + " (kv:" + a[i][1].toString() + ")");
	}*/

	/*a = database.execute("SELECT v, count(v) FROM lgd_map_resource_kv WHERE UPPER(v) LIKE UPPER('%" + query + "%') GROUP BY v ORDER BY v");

	for ( int i = 0; i < a.length; i++ ) {
		out.println(a[i][0].toString() + " (" + a[i][1].toString() + ")");
	}*/

	database.disconnect();
}
catch ( Exception e ) {
	out.println(e.toString());
}
%>