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

package LGDEditTool.Templates;

import LGDEditTool.Functions;
import LGDEditTool.SiteHandling.User;
import LGDEditTool.db.DatabaseBremen;
import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

/**
 *
 * @author Alexander Richter
 */
public class TemplatesEditHistory {
	public static String search(String type) {
		String re = "\t\t\t\t<fieldset class=\"search\">\n";
		re += "\t\t\t\t\t<legend>Search</legend>\n";
		re += "\t\t\t\t\t<form method=\"get\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>\n";
		re += "\t\t\t\t\t\t\t\t<label>Search:</label>\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"text\" id=\"search\" name=\"search\" required />\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"tab\" value=\"history\">\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"submit\" value=\"Search\" />\n";
		re += "\t\t\t\t\t\t\t</li>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=history&type=" + type + "\">Clear search.</a></li>\n";
		re += "\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</fieldset>\n";
		return re;
	}

	/**
	 * Template for EditHistory.
	 * @param ksite current site k-mappings
	 * @param kvsite current site kv-mappings
	 * @param user user-session
	 * @param sort sort object
	 * @return 
	 */
	static public String editHistory(String type, String site, String search, String sort) throws Exception {
		DatabaseBremen.getInstance().connect();// make sure database is connected
		String s = "";
                
		if ( !site.equals("") ) {
			if ( Integer.valueOf(site) < 1 ) {
				site = "1";
			}
		} //set negativ site value to 1

		if ( type.equals("k") ) {//K-Mappings
			//insert tablehead
			s = "\t\t\t\t\t<h2>K-Mapping-History</h2>\n";
			s += "\t\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=k" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("timestamp") ? "dtimestamp" : "timestamp") + "&site=" + site + "\">timestamp</a></th>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=k" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("k") ? "dk" : "k") + "&site=" + site + "\">k</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>property</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>object</th>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=k" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("count") ? "dcount" : "count") + "&site=" + site + "\">affected Entities</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>action</th>\n";
			if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=k" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("username") ? "dusername" : "username") + "&site=" + site + "\">user</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>comment</th>\n";
			s += "\t\t\t\t\t\t\t<th>restore</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert history from db
			s += searchKHistoryDB(Integer.parseInt(site), search, sort);
			s += "\t\t\t\t\t</table>\n";
		}
		else if ( type.equals("kv") ) {//KV-Mappings
			//insert tablehead
			s = "\t\t\t\t\t<h2>KV-Mapping-History</h2>\n";
			s += "\t\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=kv" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("timestamp") ? "dtimestamp" : "timestamp") + "&site=" + site + "\">timestamp</a></th>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=kv" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("k") ? "dk" : "k") + "&site=" + site + "\">k</a></th>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=kv" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("v") ? "dv" : "v") + "&site=" + site + "\">v</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>property</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>object</th>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=kv" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("count") ? "dcount" : "count") + "&site=" + site + "\">affected Entities</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>action</th>\n";
			if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=kv" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("username") ? "dusername" : "username") + "&site=" + site + "\">user</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>comment</th>\n";
			s += "\t\t\t\t\t\t\t<th>restore</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert history from db
			s += searchKVHistoryDB(Integer.parseInt(site), search, sort);
			s += "\t\t\t\t\t</table>\n";
		}
		else if ( type.equals("datatype") ) {//Datatype-Mappings
			//insert tablehead
			s = "\t\t\t\t\t<h2>Datatype-Mapping-History</h2>\n";
			s += "\t\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=datatype" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("timestamp") ? "dtimestamp" : "timestamp") + "&site=" + site + "\">timestamp</a></th>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=datatype" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("k") ? "dk" : "k") + "&site=" + site + "\">k</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>datatype</a></th>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=datatype" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("count") ? "dcount" : "count") + "&site=" + site + "\">affected Entities</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>action</th>\n";
			if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=datatype" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("username") ? "dusername" : "username") + "&site=" + site + "\">user</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>comment</th>\n";
			s += "\t\t\t\t\t\t\t<th>restore</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert history from db
			s += searchDatatypeHistoryDB(Integer.parseInt(site), search, sort);
			s += "\t\t\t\t\t</table>\n";
		}
		if ( type.equals("literal") ) {//Literal-Mappings
			//insert tablehead
			s = "\t\t\t\t\t<h2>Literal-Mapping-History</h2>\n";
			s += "\t\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=literal" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("timestamp") ? "dtimestamp" : "timestamp") + "&site=" + site + "\">timestamp</a></th>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=literal" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("k") ? "dk" : "k") + "&site=" + site + "\">k</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>property</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>language</th>\n";
			s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=literal" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("count") ? "dcount" : "count") + "&site=" + site + "\">affected Entities</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>action</th>\n";
			if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				s += "\t\t\t\t\t\t\t<th><a href=\"?tab=history&type=literal" + (search.equals("") ? "" : "&search=" + search) + "&sort=" + (sort.equals("username") ? "dusername" : "username") + "&site=" + site + "\">user</a></th>\n";
			s += "\t\t\t\t\t\t\t<th>comment</th>\n";
			s += "\t\t\t\t\t\t\t<th>restore</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert history from db
			s += searchKHistoryDB(Integer.parseInt(site), search, sort);
			s += "\t\t\t\t\t</table>\n";
		}

		//next/prev-site links
		s += "\t\t\t\t\t<div style=\"float: right;\">\n";
		if ( Integer.valueOf(site) > 1 ) {
			int prevsite = Integer.valueOf(site) - 1;
			s += "\t\t\t\t\t\t<a href=\"?tab=history&type=" + type + "&site="+ prevsite + (search.equals("") ? "" : "&search=" + search) + "&sort=" + sort + "\">&#60;prev</a>&nbsp;&nbsp;&nbsp;";
		}
		int nextsite = Integer.valueOf(site) + 1;
		s += "\t\t\t\t\t\t<a href=\"?tab=history&type=" + type + "&site=" + nextsite + (search.equals("") ? "" : "&search=" + search) + "&sort=" + sort + "\">next&#62;</a>\n";
		s += "\t\t\t\t\t</div>\n";


		return s;
	}

	/**
	 * gets edithistory from DB
	 * @param ksite
	 * @param kvsite
	 * @param search search query
	 * @param sort sort element
	 * @throws Exception 
	 */
	static private String searchKHistoryDB(int site, String search, String sort) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a;

			a = database.execute("SELECT k, property, object, count(k), user_id, comment, timestamp, action, id FROM lgd_map_resource_k_history WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (search.equals("") ? "" : (search.contains("*") ? " AND k LIKE '" + search.replaceAll("\\*", "%") + "%'" : " AND k='" + (search.contains("~") ? search.split("~")[0] : search) + "'")) + " GROUP BY k, property, object, user_id, comment, timestamp, history_id, id ORDER BY " + (sort.startsWith("d") ? (sort.contains("v") ? "k" : sort.replaceFirst("d", "")) + " DESC" : (sort.equals("v") ? "k" : sort) + " ASC") + " Limit 10 OFFSET " + ((site-1)*10));

		for ( int i = 0; i <  a.length; i++ ) {
			re += addkMapping(i, a[i][8].toString(), a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][7].toString(), a[i][4].toString(), a[i][5].toString(), a[i][6].toString(), site, search, sort);
		}

		return re;
	}

	/**
	 * gets edithistory from DB
	 * @param ksite
	 * @param kvsite
	 * @param search search query
	 * @param sort sort element
	 * @throws Exception 
	 */
	static private String searchKVHistoryDB(int site, String search, String sort) throws Exception {
		String s = "";
		DatabaseBremen database = DatabaseBremen.getInstance();

		Object[][] a = database.execute("SELECT k, v, property, object, count(k), user_id, comment, timestamp, action, id FROM lgd_map_resource_kv_history WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (search.equals("") ? "" : (search.contains("*") ? " AND k LIKE '" + search.replaceAll("\\*", "%") + "%' OR v LIKE '" + search.replaceAll("\\*", "%") + "%'" : (search.contains("~") ? " AND k='" + search.split("~")[0] + "' AND v='" + search.split("~")[1] + "'" : " AND k='" + search + "' OR v='" + search + "'"))) + " GROUP BY k, property, object, user_id, comment, timestamp, id ORDER BY " + (sort.startsWith("d") ? (sort.contains("k") ? sort.replaceFirst("d", "") + ", v" : sort.replaceFirst("d", "")) + " DESC" : (sort.equals("k") ? sort + ", v" : sort) + " ASC") + " Limit 10 OFFSET " + ((site-1)*10));

		for ( int i = 0; i <  a.length; i++ ) {
			s += addkvMapping(i, a[i][9].toString(), a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString(), a[i][8].toString(), a[i][5].toString(), a[i][6].toString(), a[i][7].toString(), site, search, sort);
		}
		return s;
	}

	static private String searchDatatypeHistoryDB(int site, String search, String sort) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String s = "";
		Object[][] a = database.execute("SELECT k, datatype, count(k), user_id, comment, timestamp, action, id FROM lgd_map_datatype_history WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (search.equals("") ? "" : (search.contains("*") ? " AND k LIKE '" + search.replaceAll("\\*", "%") + "%'" : " AND k='" + (search.contains("~") ? search.split("~")[0] : search) + "'")) + " GROUP BY k, datatype, user_id, comment, timestamp, id ORDER BY " + (sort.startsWith("d") ? (sort.contains("v") ? "k" : sort.replaceFirst("d", "")) + " DESC" : (sort.equals("v") ? "k" : sort) + " ASC") + " Limit 10 OFFSET " + ((site-1)*10));

		for ( int i = 0; i <  a.length; i++ ) {
			s += addDatatypeMapping(i, a[i][7].toString(), a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][6].toString(), a[i][3].toString(), a[i][4].toString(), a[i][5].toString(), site, search, sort);
		}
		return s;
	}

	private static String searchLiteralHistoryDB(int site, String search, String sort) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String s = "";
		Object[][] a = database.execute("SELECT k, property, language, count(k), user_id, comment, timestamp, action, id FROM lgd_map_literal_history WHERE userspace='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (search.equals("") ? "" : (search.contains("*") ? " AND k LIKE '" + search.replaceAll("\\*", "%") + "%'" : " AND k='" + (search.contains("~") ? search.split("~")[0] : search) + "'")) + " GROUP BY k, property, language, user_id, comment, timestamp, id ORDER BY " + (sort.startsWith("d") ? (sort.contains("v") ? "k" : sort.replaceFirst("d", "")) + " DESC" : (sort.equals("v") ? "k" : sort) + " ASC") + " Limit 10 OFFSET " + ((site-1)*10));

		for ( int i = 0; i <  a.length; i++ ) {
			s += addLiteralMapping(i, a[i][8].toString(), a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][7].toString(), a[i][4].toString(), a[i][5].toString(), a[i][6].toString(), site, search, sort);
		}
		return s;
	}

	/**
	 * Template for K-Mappings.
	 * @param id
	 * @param k k
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @param action action
	 * @param user_id user
	 * @param comment comment
	 * @param timestamp timestamp
	 * @param ksite
	 * @param kvsite
	 * @param search search query
	 * @param sort sort element
	 * @return 
	 */
	private static String addkMapping(int i, String id, String k, String property, String object, String affectedEntities, String action, String user_id, String comment, String timestamp, int site, String search, String sort) {
		String s = "\t\t\t\t\t<tr id=\"k" + i + "a\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t<td><a href=\"?tab=search&search=" + k + "\">" + k + "</a></td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";

		if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			s += "\t\t\t\t\t\t<td>" + user_id+ "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">restore</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=history&type=k" + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "&site="+ site + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";  
    s += "\t\t\t\t\t\t<tr id=\"k" + i + "\" style=\"display: none;\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><a href=\"?tab=search&search=" + k + "\">" + k + "</a></td>\n";
    s += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";

		if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"id\" value=\"" + id + "\" />\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
    s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Hide</a></td>\n";
    s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("k" + i + "u", "kmappingedit", "Restore", (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 9 : 8));
		s += "\t\t\t\t\t</form>\n";
    return s;
	}

	/**
	 * Template for KV-Mappings.
	 * @param id
	 * @param k k
	 * @param v v
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @param action action
	 * @param user_id user
	 * @param comment comment
	 * @param timestamp timestamp
	 * @param ksite
	 * @param kvsite
	 * @param search search query
	 * @param sort sort element
	 * @return 
	 */
	static private String addkvMapping(int i, String id, String k, String v, String property, String object, String affectedEntities, String action, String user_id, String comment, String timestamp, int site, String search, String sort) {
    String s = "\t\t\t\t\t<tr id=\"kv" + i + "a\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t<td><a href=\"?tab=search&search=" + k + "\">" + k + "</a></td>\n";
    s += "\t\t\t\t\t\t<td><a href=\"?tab=search&search=" + k + "~" + v + "\">" + v + "</a></td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";

		if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">restore</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=history&type=kv" + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "&site="+ site + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
    s += "\t\t\t\t\t\t<tr id=\"kv" + i + "\" style=\"display: none;\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><a href=\"?tab=search&search=" + k + "\">" + k + "</a></td>\n";
    s += "\t\t\t\t\t\t\t<td><a href=\"?tab=search&search=" + k + "~" + v + "\">" + v + "</a></td>\n";
    s += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";

		if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"id\" value=\"" + id + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
    s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("kv" + i + "u", "kvmappingedit", "Restore", (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 10 : 9));
		s += "\t\t\t\t\t</form>\n";
		return s;
	}

	static private String addDatatypeMapping(int i, String id, String k, String datatype, String affectedEntities, String action, String user_id, String comment, String timestamp, int site, String search, String sort) {
    String s = "\t\t\t\t\t<tr id=\"tk" + i + "a\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t<td><a href=\"?tab=search&search=" + k + "\">" + k + "</a></td>\n";
    s += "\t\t\t\t\t\t<td>" + (datatype.equals("deleted") ? "" : datatype) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";

		if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + i + "')\">restore</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=history&type=datatype" + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "&site="+ site + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
    s += "\t\t\t\t\t\t<tr id=\"tk" + i + "\" style=\"display: none;\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + (datatype.equals("deleted") ? "" : datatype) + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";

		if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + i + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"id\" value=\"" + id + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + datatype + "\" />\n";
    s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("tk" + i + "u", "dmappingedit", "Restore", (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 8 : 7));
		s += "\t\t\t\t\t</form>\n";
		return s;
	}

	/**
	 * Template for K-Mappings.
	 * @param id
	 * @param k k
	 * @param property property
	 * @param language object
	 * @param affectedEntities affected entities
	 * @param action action
	 * @param user_id user
	 * @param comment comment
	 * @param timestamp timestamp
	 * @param ksite
	 * @param kvsite
	 * @param search search query
	 * @param sort sort element
	 * @return 
	 */
	private static String addLiteralMapping(int i, String id, String k, String property, String language, String affectedEntities, String action, String user_id, String comment, String timestamp, int site, String search, String sort) {
		String s = "\t\t\t\t\t<tr id=\"lk" + i + "a\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t<td><a href=\"?tab=search&search=" + k + "\">" + k + "</a></td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + language + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";

		if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lk" + i + "')\">restore</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=history&type=literal" + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "&site="+ site + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";  
    s += "\t\t\t\t\t\t<tr id=\"lk" + i + "\" style=\"display: none;\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><a href=\"?tab=search&search=" + k + "\">" + k + "</a></td>\n";
    s += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + language + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";

		if ( User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"id\" value=\"" + id + "\" />\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"language\" value=\"" + language + "\" />\n";
    s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lk" + i + "')\">Hide</a></td>\n";
    s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("lk" + i + "u", "lmappingedit", "Restore", (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 9 : 8));
		s += "\t\t\t\t\t</form>\n";
    return s;
	}

	/**
	 * Template for user fields.
	 * @param id id for toggle visiblity
	 * @param submitName submit name
	 * @param submitValue submit value
	 * @param columns column count
	 * @return String
	 */
	private static String getUserField(String id, String submitName, String submitValue, int columns) {
		String re;
		if ( !User.getInstance().isLoggedIn() ) {
			re = "\t\t\t\t\t\t<tr id=\"" + id + "\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns - 3) + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Username:</label>\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" value=\"" + User.getInstance().getUsername() + "\" style=\"width: 25em;\" placeholder=\"Username\" required />\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" placeholder=\"No comment.\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + 3 + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}
		else {
			re = "\t\t\t\t\t\t<tr id=\"" + id + "\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns - 3) + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" placeholder=\"No comment.\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + 3 + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}

		return re;
	}

	/**
	 * reCatpcha
	 * @param request request
	 * @param ksite current K-mappings Site
	 * @param kvsite current KV-mappings Site
	 * @return Returns a String with HTML-code.
	 */
	public static String captcha(HttpServletRequest request, String type, String site, String search, String sort) {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha(Functions.PUBLIC_reCAPTCHA_KEY, Functions.PRIVATE_reCAPTCHA_KEY, false);
		String re;

		re = "\t\t\t\t<article class=\"captcha\">\n";
		re += "\t\t\t\t\t<form action=\"?tab=history&type=" + type + "&site=" + site + (search.equals("") ? "" : "&search=" + search) + "&sort=" + sort + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>"+ c.createRecaptchaHtml(null, null) + "</li>\n";
		re += "\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"fcaptcha\" value=\"Send\" /></li>\n";
		re += "\t\t\t\t\t\t</ul>\n";

		if ( request.getParameter("kmappingedit") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"id\" value=\"" + request.getParameter("id") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kmappingedit\" value=\"" + request.getParameter("kmappingedit") + "\" />\n";
		}//#########################################################################
		else if ( request.getParameter("kvmappingedit") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + request.getParameter("v") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"id\" value=\"" + request.getParameter("id") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kvmappingedit\" value=\"" + request.getParameter("kvmappingedit") + "\" />\n";
		}//#########################################################################
		else if ( request.getParameter("dmappingedit") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + request.getParameter("datatype") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"id\" value=\"" + request.getParameter("id") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"dmappingedit\" value=\"" + request.getParameter("dmappingedit") + "\" />\n";
		}//#########################################################################
		else if ( request.getParameter("lmappingedit") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"language\" value=\"" + request.getParameter("language") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"id\" value=\"" + request.getParameter("id") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"lmappingedit\" value=\"" + request.getParameter("lmappingedit") + "\" />\n";
		}

		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</article>\n";

		return re;
	}
}