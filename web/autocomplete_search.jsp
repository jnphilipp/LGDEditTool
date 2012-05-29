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

	if ( query.contains(":") || query.length() > 2 ) {
	
		if ( query.contains("*") )
			query = query.replaceAll("\\*", "%");

		if ( !query.endsWith("%") )
			query += "%";

		if ( !query.contains(":") ) {
			Object[][] a = database.execute("SELECT kvm.k, kvm.v, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_resource_kv AS kvm LEFT OUTER JOIN lgd_stat_tags_kv ON lgd_stat_tags_kv.k=kvm.k AND lgd_stat_tags_kv.v=kvm.v WHERE (user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '' AND object != '') OR (user_id = 'main' AND (kvm.k, kvm.v) NOT IN (SELECT k, v FROM lgd_map_resource_kv WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "')) AND (UPPER(kvm.k) LIKE UPPER('" + query + "') OR UPPER(kvm.v) LIKE UPPER('" + query + "') OR (kvm.k, kvm.v) IN (SELECT k, v FROM lgd_map_label WHERE UPPER(label) LIKE UPPER('" + query + "'))) UNION ALL SELECT km.k, '' AS v, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_resource_k AS km LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=km.k WHERE (user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '' AND object != '') OR (user_id = 'main' AND km.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "')) AND UPPER(km.k) LIKE UPPER('" + query + "') UNION ALL SELECT dm.k, '' AS v, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_datatype AS dm LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=dm.k WHERE ((user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND datatype != 'deleted') OR (user_id = 'main' AND dm.k NOT IN (SELECT k FROM lgd_map_datatype WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'))) AND dm.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE (user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '' AND object != '') OR (user_id = 'main' AND dm.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'))) AND UPPER(dm.k) LIKE UPPER('" + query + "') UNION ALL SELECT lm.k, '' AS v, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_literal AS lm LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=lm.k WHERE ((user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '') OR (user_id = 'main' AND lm.k NOT IN (SELECT k FROM lgd_map_literal WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'))) AND lm.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE (user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '' AND object != '') OR (user_id = 'main' AND lm.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'))) AND UPPER(lm.k) LIKE UPPER('" + query + "') ORDER BY k, v");

			for ( int i = 0; i < a.length; i++ ) {
				out.println(a[i][0].toString() + " (" + (a[i][1].toString().equals("") ? "" : (a[i][1].toString() + ", ")) + a[i][2].toString() + ")");
			}
		}
		else if ( query.startsWith("k:") ) {
			Object[][] a = database.execute("SELECT kvm.k, (SELECT SUM(usage_count) FROM lgd_stat_tags_kv WHERE kvm.k=k) AS usage_count FROM lgd_map_resource_kv AS kvm WHERE (user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '' AND object != '') OR (user_id = 'main' AND (kvm.k, kvm.v) NOT IN (SELECT k, v FROM lgd_map_resource_kv WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "')) AND UPPER(kvm.k) LIKE UPPER('" + query.substring(2) + "') GROUP BY kvm.k UNION ALL SELECT km.k, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_resource_k AS km LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=km.k WHERE (user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '' AND object != '') OR (user_id = 'main' AND km.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "')) AND UPPER(km.k) LIKE UPPER('" + query.substring(2) + "') UNION ALL SELECT dm.k, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_datatype AS dm LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=dm.k WHERE ((user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND datatype != 'deleted') OR (user_id = 'main' AND dm.k NOT IN (SELECT k FROM lgd_map_datatype WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'))) AND dm.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE (user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '' AND object != '') OR (user_id = 'main' AND dm.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'))) AND UPPER(dm.k) LIKE UPPER('" + query.substring(2) + "') UNION ALL SELECT lm.k, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_literal AS lm LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=lm.k WHERE ((user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '') OR (user_id = 'main' AND lm.k NOT IN (SELECT k FROM lgd_map_literal WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'))) AND lm.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE (user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '' AND object != '') OR (user_id = 'main' AND lm.k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'))) AND UPPER(lm.k) LIKE UPPER('" + query.substring(2) + "') ORDER BY k");

			for ( int i = 0; i < a.length; i++ ) {
				out.println("k:" + a[i][0].toString() + " (" + a[i][1].toString() + ")");
			}
		}
		else if ( query.startsWith("v:") ) {
			Object[][] a = database.execute("SELECT kvm.v, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_resource_kv AS kvm LEFT OUTER JOIN lgd_stat_tags_kv ON lgd_stat_tags_kv.k=kvm.k AND lgd_stat_tags_kv.v=kvm.v WHERE (user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND property != '' AND object != '') OR (user_id = 'main' AND (kvm.k, kvm.v) NOT IN (SELECT k, v FROM lgd_map_resource_kv WHERE user_id = '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "')) AND UPPER(kvm.v) LIKE UPPER('" + query.substring(2) + "') ORDER BY v");

			for ( int i = 0; i < a.length; i++ ) {
				out.println("v:" + a[i][0].toString() + " (" + a[i][1].toString() + ")");
			}
		}
		else if ( query.startsWith("l:") ) {
			Object[][] a = database.execute("SELECT label FROM lgd_map_label WHERE UPPER(label) LIKE UPPER('" + query.substring(2) + "') GROUP BY label ORDER BY label");

			for ( int i = 0; i < a.length; i++ ) {
				out.println("l:" + a[i][0].toString());
			}
		}
	}
	else {
		out.println(query);
		out.println("k:");
		out.println("v:");
		out.println("l:");
	}

	database.disconnect();
}
catch ( Exception e ) {
	out.println(e.toString());
}
%>