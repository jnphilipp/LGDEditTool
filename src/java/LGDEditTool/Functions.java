/*
 *    This file is part of LGDEditTool (LGDET).
 *
 *    LGDET is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    any later version.
 *
 *    LGDET is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with LGDET.  If not, see <http://www.gnu.org/licenses/>.
 */


package LGDEditTool;

import java.util.Calendar;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class Functions {
	/**
	 * main branch
	 */
	public static final String MAIN_BRANCH = "lgd_user_main";
	/**
	 * PRIVATE_reCAPTCHA_KEY
	 */
	public static final String PRIVATE_reCAPTCHA_KEY = "6Le1b88SAAAAALUjcJ26asXAk2wHDu-JwarKY8z1";
	/**
	 * PUBLIC_reCAPTCHA_KEY
	 */
	public static final String PUBLIC_reCAPTCHA_KEY = "6Le1b88SAAAAALjXm-PM6alI7EQlj-fi9eh-Wm2C ";

	/**
	 * Schorting the URL from property and object.
	 * @param url URL
	 * @return short URL
	 */
	public static String shortenURL(String url) {
		if ( url.contains("w3.org") && url.endsWith("#type") )
			return "w3#type";
		else if ( url.equals("http://www.w3.org/2000/01/rdf-schema#label") )
			return "w3/rdf-schema#label";
		else if ( url.equals("http://www.w3.org/2004/02/skos/core#altLabel") )
			return "w3/core#altLabel";
		else if ( url.contains("linkedgeodata.org") ) {
			return "LGD:" + url.substring(url.lastIndexOf("/") + 1);
		}
		else
			return url;
	}

	/**
	 * Formating timestamp from database (format: YYYY-MM-ddTHH:mm:ss) into dd.MM.YYYY<br />HH:mm:ss
	 * @param timestamp
	 * @return String in dd.MM.YYYY<br />HH:mm:ss format.
	 */
	public static String showTimestamp(String timestamp) {
		String re = "";
		String[] a = timestamp.split("T")[0].split("-");
		re += a[2] + "." + a[1] + "." + a[0];
		re += "<br />" + timestamp.split("T")[1];
		return re;
	}

        /**
         * Template for Database. Transform Date-String to Database Date-Type-String
         * @return String in YYYY-MM-ddTHH:mm:ss format.
         */
	public static String getTimestamp() {
		return Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND));
	}

	public static String createView(String username) {
		return "CREATE VIEW lgd_user_" + username + " AS SELECT k, v, COUNT(k) FROM lgd_map_resource_kv WHERE (user_id = '" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND (k, v) NOT IN (SELECT k, v FROM lgd_map_resource_kv WHERE user_id = '" + username + "')) GROUP BY k, v UNION ALL SELECT k, '' AS v, COUNT(k) + (SELECT COUNT(k) FROM lgd_map_resource_kv WHERE k=lgd_map_resource_k.k AND ((user_id = '" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND (k, v) NOT IN (SELECT k, v FROM  lgd_map_resource_kv WHERE user_id = '" + username + "')))) + (SELECT COUNT(k) FROM lgd_map_datatype WHERE k=lgd_map_resource_k.k AND ((user_id = '" + username + "' AND datatype != 'deleted') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_datatype WHERE user_id = '" + username + "')))) + (SELECT COUNT(k) FROM lgd_map_literal WHERE k=lgd_map_resource_k.k AND ((user_id = '" + username + "' AND property != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_literal WHERE user_id = '" + username + "')))) FROM lgd_map_resource_k WHERE (user_id='" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + username + "')) GROUP BY k UNION ALL SELECT k, '' AS v, COUNT(k) FROM lgd_map_datatype WHERE ((user_id = '" + username + "' AND datatype != 'deleted') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_datatype WHERE user_id = '" + username + "'))) AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE (user_id='" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + username + "'))) GROUP BY k UNION ALL SELECT k, '' AS v, COUNT(k) FROM lgd_map_literal WHERE ((user_id = '" + username + "' AND property != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_literal WHERE user_id = '" + username + "'))) AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE (user_id='" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + username + "'))) GROUP BY k ORDER BY k, v";
	}
	
	public static String createViewHistory(String username) {
		return "CREATE VIEW lgd_user_" + username + "_history AS SELECT k, v, COUNT(k) FROM lgd_map_resource_kv_history WHERE userspace='" + username + "' GROUP BY k, v UNION ALL SELECT k, '' AS v, COUNT(k) + (SELECT COUNT(k) FROM lgd_map_resource_kv_history WHERE k=lgd_map_resource_k_history.k AND userspace='" + username + "') + (SELECT COUNT(k) FROM lgd_map_datatype_history WHERE k=lgd_map_resource_k_history.k AND userspace='" + username + "') + (SELECT COUNT(k) FROM lgd_map_literal_history WHERE k=lgd_map_resource_k_history.k AND userspace='" + username + "') FROM lgd_map_resource_k_history WHERE userspace='" + username + "' GROUP BY k UNION ALL SELECT k, '' AS v, COUNT(k) FROM lgd_map_datatype_history WHERE userspace='" + username + "' AND k NOT IN (SELECT k FROM lgd_map_resource_k_history WHERE userspace='" + username + "') GROUP BY k UNION ALL SELECT k, '' AS v, COUNT(k) FROM lgd_map_literal_history WHERE userspace='" + username + "' AND k NOT IN (SELECT k FROM lgd_map_resource_k_history WHERE userspace='" + username + "') GROUP BY k ORDER BY k, v";
	}

	public static String createViewUnmapped(String username) {
		return "CREATE VIEW lgd_user_" + username + "_unmapped AS SELECT k, '' AS v, COUNT(k) + (SELECT COUNT(k) FROM lgd_stat_tags_kv c WHERE NOT EXISTS (Select b.k FROM (SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_resource_kv WHERE user_id='main' OR user_id='" + username + "') b WHERE c.k=b.k) AND c.k=a.k) AS count FROM lgd_stat_tags_k a WHERE NOT EXISTS (Select b.k FROM ( Select k FROM lgd_map_datatype WHERE user_id='main' OR user_id='" + username + "' UNION ALL SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_literal WHERE user_id='main' OR user_id='" + username + "' UNION ALL SELECT k FROM lgd_map_property UNION ALL SELECT k FROM lgd_map_resource_k WHERE user_id='main' OR user_id='" + username + "' UNION ALL SELECT k FROM lgd_map_resource_kv WHERE user_id='main' OR user_id='" + username + "' UNION ALL SELECT k FROM lgd_map_resource_prefix ) b WHERE a.k=b.k) GROUP BY k UNION ALL SELECT k, v, COUNT(k) AS count FROM lgd_stat_tags_kv a WHERE NOT EXISTS (Select b.k FROM (SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_resource_kv WHERE user_id='main' OR user_id='" + username + "') b WHERE a.k=b.k) GROUP BY k, v ORDER BY k, v";
	}
}
