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

<%@page import="LGDEditTool.db.LGDDatabase"%>
<%
try {
	LGDDatabase database = LGDDatabase.getInstance();
	database.connect();
	User.getInstance().createUser(request);
	String query = request.getParameter("q");

	out.println(query);

	if ( query.contains(":") || query.length() > 2 ) {
		if ( query.contains("*") )
			query = query.replaceAll("\\*", "%");

		if ( !query.endsWith("%") )
			query += "%";

		if ( query.startsWith("k:") ) {
			Object[][] a = database.execute("SELECT kvmh.k, COALESCE((SELECT SUM(usage_count) FROM lgd_stat_tags_kv WHERE kvmh.k=k), 0) AS usage_count FROM lgd_map_resource_kv_history AS kvmh WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND UPPER(kvmh.k) LIKE UPPER('" + query.substring(2) + "') GROUP BY kvmh.k UNION ALL SELECT kmh.k, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_resource_k_history AS kmh LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=kmh.k WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND UPPER(kmh.k) LIKE UPPER('" + query.substring(2) + "') UNION ALL SELECT dmh.k, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_datatype_history AS dmh LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=dmh.k WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND dmh.k NOT IN (SELECT k FROM lgd_map_resource_k_history WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "') AND UPPER(dmh.k) LIKE UPPER('" + query.substring(2) + "') UNION ALL SELECT lmh.k, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_literal_history AS lmh LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=lmh.k WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND lmh.k NOT IN (SELECT k FROM lgd_map_resource_k_history WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "') AND UPPER(lmh.k) LIKE UPPER('" + query.substring(2) + "') ORDER BY k");

			for ( int i = 0; i < a.length; i++ ) {
				out.println("k:" + a[i][0].toString() + " (" + a[i][1].toString() + ")");
			}
		}
		else if ( query.startsWith("v:") ) {
			Object[][] a = database.execute("SELECT kvmh.v, COALESCE((SELECT SUM(usage_count) FROM lgd_stat_tags_kv WHERE v=kvmh.v AND k IN (SELECT k FROM lgd_map_resource_kv_history WHERE v=kvmh.v)), 0) AS usage_count FROM lgd_map_resource_kv_history AS kvmh WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND UPPER(kvmh.v) LIKE UPPER('" + query.substring(2) + "') GROUP BY v ORDER BY v");

			for ( int i = 0; i < a.length; i++ ) {
				out.println("v:" + a[i][0].toString() + " (" + a[i][1].toString() + ")");
			}
		}
		else if ( query.startsWith("l:") ) {
			Object[][] a = database.execute("SELECT label FROM lgd_map_label WHERE UPPER(label) LIKE UPPER('" + query.substring(2) + "') AND (k, v) IN (SELECT k, v FROM lgd_map_resource_kv_history WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "') GROUP BY label ORDER BY label");

			for ( int i = 0; i < a.length; i++ ) {
				out.println("l:" + a[i][0].toString());
			}
		}
		else {
			Object[][] a = database.execute("SELECT kvmh.k, kvmh.v, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_resource_kv_history AS kvmh LEFT OUTER JOIN lgd_stat_tags_kv ON lgd_stat_tags_kv.k=kvmh.k AND lgd_stat_tags_kv.v=kvmh.v WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND (UPPER(kvmh.k) LIKE UPPER('" + query + "') OR UPPER(kvmh.v) LIKE UPPER('" + query + "') OR (kvmh.k, kvmh.v) IN (SELECT k, v FROM lgd_map_label WHERE UPPER(label) LIKE UPPER('" + query + "'))) UNION ALL SELECT kmh.k, '' AS v, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_resource_k_history AS kmh LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=kmh.k WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND UPPER(kmh.k) LIKE UPPER('" + query + "') UNION ALL SELECT dmh.k, '' AS v, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_datatype_history AS dmh LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=dmh.k WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND dmh.k NOT IN (SELECT k FROM lgd_map_resource_k_history WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "') AND UPPER(dmh.k) LIKE UPPER('" + query + "') UNION ALL SELECT lmh.k, '' AS v, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_literal_history AS lmh LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=lmh.k WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' AND lmh.k NOT IN (SELECT k FROM lgd_map_resource_k_history WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "') AND UPPER(lmh.k) LIKE UPPER('" + query + "') ORDER BY k, v");

			for ( int i = 0; i < a.length; i++ ) {
				out.println(a[i][0].toString() + " (" + (a[i][1].toString().equals("") ? "" : (a[i][1].toString() + ", ")) + a[i][2].toString() + ")");
			}
		}
	}
	else {
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
