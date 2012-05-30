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

	out.println(query);

	if ( query.contains("*") )
		query = query.replaceAll("\\*", "%");

	if ( !query.endsWith("%") )
		query += "%";

	Object[][] a = database.execute("SELECT k, '' AS v, usage_count FROM lgd_stat_tags_k a WHERE NOT EXISTS (Select b.k FROM ( Select k FROM lgd_map_datatype WHERE user_id='main' OR user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' UNION ALL SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_literal WHERE user_id='main' OR user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' UNION ALL SELECT k FROM lgd_map_property UNION ALL SELECT k FROM lgd_map_resource_k WHERE user_id='main' OR user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' UNION ALL SELECT k FROM lgd_map_resource_kv WHERE user_id='main' OR user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' UNION ALL SELECT k FROM lgd_map_resource_prefix ) b WHERE a.k=b.k) AND UPPER(k) LIKE UPPER('" + query + "') UNION ALL SELECT k, v, usage_count AS count FROM lgd_stat_tags_kv a WHERE NOT EXISTS (Select b.k FROM (SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_resource_kv WHERE user_id='main' OR user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "') b WHERE a.k=b.k) AND (UPPER(k) LIKE UPPER('" + query + "') OR UPPER(v) LIKE UPPER('" + query + "') OR (k, v) IN (SELECT k, v FROM lgd_map_label WHERE UPPER(label) LIKE UPPER('" + query + "'))) ORDER BY k, v;");

	for ( int i = 0; i < a.length; i++ ) {
		out.println(a[i][0].toString() + " (" + (a[i][1].toString().equals("") ? "" : (a[i][1].toString() + ", ")) + a[i][2].toString() + ")");
	}

	database.disconnect();
}
catch ( Exception e ) {
	out.println(e.toString());
}
%>