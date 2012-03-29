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
	//static ArrayList<String> al = new ArrayList<String>();

	/**
	 * Template for EditHistory.
	 * @param ksite current site k-mappings
	 * @param kvsite current site kv-mappings
	 * @param user user-session
	 * @param sort sort object
	 * @return 
	 */
	static public String editHistory(String ksite, String kvsite, String dsite, String search, String sort) throws Exception {
		DatabaseBremen.getInstance().connect();

		//kmappings
		//insert tablehead
		String re = "\t\t\t\t<h2>K-Mapping-History</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr class=mapping>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">timestamp</a></th>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&sort=k&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">k</a></th>\n";
		re += "\t\t\t\t\t\t<th>property</a></th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>action</th>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&sort=user_id&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">user</a></th>\n";
		re += "\t\t\t\t\t\t<th>comment</th>\n";
		re += "\t\t\t\t\t\t<th>restore</th>\n";
		re += "\t\t\t\t\t</tr>\n";

		//insert edithistory from db for k-mappings
		re += searchKHistoryDB(Integer.parseInt(ksite), Integer.parseInt(kvsite), Integer.parseInt(dsite), search, sort);

		//inser table foot
		re += "\t\t\t\t</table>\n";
		re += "\t\t\t\t<div style=\"float: right;\">\n";
		if ( Integer.valueOf(ksite) > 1 ) {
			int prevsite = Integer.valueOf(ksite) - 1;
			re += "\t\t\t\t\t<a href=\"?tab=history&ksite="+ prevsite + "&kvsite=" + kvsite + "&dsite=" + dsite + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "\">&#60;prev</a>&nbsp;&nbsp;&nbsp;";
		}
		int nextsite = Integer.valueOf(ksite) + 1;
		re += "<a href=\"?tab=history&ksite=" + nextsite + "&kvsite=" + kvsite + "&dsite=" + dsite + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "\">next&#62;</a>\n";
		re += "\t\t\t\t</div>\n";

		//kvmappings
		//insert tablehead
		re += "\t\t\t\t<h2>KV-Mapping-History</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr class=mapping>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">timestamp</a></th>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&sort=k&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">k</a></th>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&sort=v&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">v</a></th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>action</th>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&sort=user_id&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">user</a></th>\n";
		re += "\t\t\t\t\t\t<th>comment</th>\n";
		re += "\t\t\t\t\t\t<th>restore</th>\n";
		re += "\t\t\t\t\t</tr>\n";

		//insert edithistory from db for kv-mappings
		re += searchKVHistoryDB(Integer.parseInt(ksite), Integer.parseInt(kvsite), Integer.parseInt(dsite), search, sort);

		//inser table foot
		re += "\t\t\t\t</table>\n";
		re += "\t\t\t\t<div style=\"float: right;\">\n";

		if( Integer.valueOf(kvsite) > 1 ) {
			int prevsite = Integer.valueOf(kvsite) - 1;
			re += "\t\t\t\t\t<a href=\"?tab=history&ksite=" + ksite + "&kvsite=" + prevsite + "&dsite=" + dsite + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "\">&#60;prev</a>&nbsp;&nbsp;&nbsp;";
		}

		nextsite = Integer.valueOf(kvsite) + 1;
		re += "\t\t\t\t\t<a href=\"?tab=history&ksite="+ ksite + "&kvsite=" + nextsite + "&dsite=" + dsite + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "\">next&#62;</a>\n";
		re += "\t\t\t\t</div>\n";

		//datatype mappings
		//insert tablehead
		re += "\t\t\t\t<h2>Datatype-Mapping-History</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr class=mapping>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">timestamp</a></th>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&sort=k&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">k</a></th>\n";
		re += "\t\t\t\t\t\t<th>datatype</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>action</th>\n";
		re += "\t\t\t\t\t\t<th><a href=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + "&sort=user_id&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\">user</a></th>\n";
		re += "\t\t\t\t\t\t<th>comment</th>\n";
		re += "\t\t\t\t\t\t<th>restore</th>\n";
		re += "\t\t\t\t\t</tr>\n";

		//insert edithistory from db for kv-mappings
		re += searchDatatypeHistoryDB(Integer.parseInt(ksite), Integer.parseInt(kvsite), Integer.parseInt(dsite), search, sort);

		//inser table foot
		re += "\t\t\t\t</table>\n";
		re += "\t\t\t\t<div style=\"float: right;\">\n";

		if( Integer.valueOf(kvsite) > 1 ) {
			int prevsite = Integer.valueOf(dsite) - 1;
			re += "\t\t\t\t\t<a href=\"?tab=history&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + prevsite + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "\">&#60;prev</a>&nbsp;&nbsp;&nbsp;";
		}

		nextsite = Integer.valueOf(dsite) + 1;
		re += "\t\t\t\t\t<a href=\"?tab=history&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + nextsite + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "\">next&#62;</a>\n";
		re += "\t\t\t\t</div>\n";

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
	static private String searchKHistoryDB(int ksite, int kvsite, int dsite, String search, String sort) throws Exception {
		String re = "";
		DatabaseBremen database = DatabaseBremen.getInstance();

		Object[][] a = database.execute("SELECT k, property, object, count(k), user_id, comment, timestamp, action, history_id, id FROM lgd_map_resource_k_history GROUP BY k,property,object,user_id,comment,timestamp,history_id,id ORDER BY " + (sort.equals("") || sort.equals("v") ? "" : sort + ",") + " timestamp DESC Limit 10 OFFSET " + ((ksite-1)*10));

		for ( int i = 0; i <  a.length; i++ ) {
			re += addkMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][7].toString(), a[i][4].toString(), a[i][5].toString(), a[i][6].toString(), ksite, kvsite, dsite, search, sort);
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
	static private String searchKVHistoryDB(int ksite, int kvsite, int dsite, String search, String sort) throws Exception {
		String s = "";
		DatabaseBremen database = DatabaseBremen.getInstance();

		Object[][] a = database.execute("SELECT k, v, property, object, count(k), user_id, comment, timestamp, action, history_id, id FROM lgd_map_resource_kv_history GROUP BY k,v,property,object,user_id,comment,timestamp,history_id, id ORDER BY " + (sort.equals("") ? "" : sort + ",") + " timestamp DESC Limit 10 OFFSET " + ((kvsite-1)*10));

		for ( int i = 0; i <  a.length; i++ ) {
			s += addkvMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString(), a[i][8].toString(), a[i][5].toString(), a[i][6].toString(), a[i][7].toString(), ksite, kvsite, dsite, search, sort);
		}
		return s;
	}

	static private String searchDatatypeHistoryDB(int ksite, int kvsite, int dsite, String search, String sort) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT k, datatype, count(k), user_id, comment, timestamp, action, history_id, id FROM lgd_map_datatype_history GROUP BY k, datatype, user_id, comment, timestamp, history_id, id ORDER BY " + (sort.equals("") || sort.equals("v") ? "" : sort + ",") + " timestamp DESC Limit 10 OFFSET " + ((dsite-1)*10));

		for ( int i = 0; i <  a.length; i++ ) {
			s += addDatatypeMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][6].toString(), a[i][3].toString(), a[i][4].toString(), a[i][5].toString(), ksite, kvsite, dsite, search, sort);
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
	static private String addkMapping(int id, String k, String property, String object, String affectedEntities,String action, String user_id, String comment, String timestamp, int ksite, int kvsite, int dsite, String search, String sort) {
		String s = "\t\t\t\t\t<tr id=\"k" + id + "a\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">restore</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "&ksite="+ ksite +"&kvsite=" + kvsite + "&dsite=" + dsite + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";  
    s += "\t\t\t\t\t\t<tr id=\"k" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">Hide</a></td>\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + timestamp + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + user_id + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + comment + "\" />\n";
    s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("k" + id + "u", "kmappingedit", "Restore", 9);
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
	static private String addkvMapping(int id, String k, String v, String property, String object, String affectedEntities, String action, String user_id, String comment, String timestamp, int ksite, int kvsite, int dsite, String search, String sort) {
                String s = "\t\t\t\t\t<tr id=\"kv" + id + "a\">\n";
                s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + v + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
                s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + id + "')\">restore</a></td>\n";
                s += "\t\t\t\t\t</tr>\n";
                s += "\t\t\t\t\t<form action=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "&ksite="+ ksite +"&kvsite=" + kvsite + "&dsite=" + dsite + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
                s += "\t\t\t\t\t\t<tr id=\"kv" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
                s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
                s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
                s += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
                s += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
                s += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
                s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
                s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + id + "')\">Hide</a></td>\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + timestamp + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + user_id + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + comment + "\" />\n";
                s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("kv" + id + "u", "kvmappingedit", "Restore", 10);
		s += "\t\t\t\t\t</form>\n";
		return s;
	}

	static private String addDatatypeMapping(int id, String k, String datatype, String affectedEntities, String action, String user_id, String comment, String timestamp, int ksite, int kvsite, int dsite, String search, String sort) {
                String s = "\t\t\t\t\t<tr id=\"tk" + id + "a\">\n";
                s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + datatype + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
                s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + id + "')\">restore</a></td>\n";
                s += "\t\t\t\t\t</tr>\n";
                s += "\t\t\t\t\t<form action=\"?tab=history" + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "&ksite="+ ksite +"&kvsite=" + kvsite + "&dsite=" + dsite + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
                s += "\t\t\t\t\t\t<tr id=\"tk" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
                s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
                s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
                s += "\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
                s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
                s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + id + "')\">Hide</a></td>\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + datatype + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + timestamp + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + user_id + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + comment + "\" />\n";
                s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("tk" + id + "u", "dmappingedit", "Restore", 8);
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
		String re = "";

		if ( !User.getInstance().isLoggedIn() ) {
			re += "\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"4\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + User.getInstance().getUsername() + "\" required />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 10 ? "4" : "3") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 8 ? "1" : "2") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}
		else {
			re += "\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 10 ? "8" : "7") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 8 ? "1" : "2") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />";
			re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + User.getInstance().getUsername() + "\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}

		return re;
	}

	/**
	 * reCatpcha
	 * @param request
	 * @param ksite
	 * @param kvsite
	 * @return 
	 */
	public static String captcha(HttpServletRequest request, String ksite, String kvsite, String dsite) {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha(Functions.PUBLIC_reCAPTCHA_KEY, Functions.PRIVATE_reCAPTCHA_KEY, false);

		String re;
		re = "\t\t\t\t<article class=\"captcha\">\n";
		re += "\t\t\t\t\t<form action=\"?tab=history&ksite=" + ksite + "&kvsite=" + kvsite + "&dsite=" + dsite + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>"+ c.createRecaptchaHtml(null, null) + "</li>\n";
		re += "\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"fcaptcha\" value=\"Send\" /></li>\n";
		re += "\t\t\t\t\t\t</ul>\n";

		if ( request.getParameter("kmappingedit") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kmappingedit\" value=\"" + request.getParameter("kmappingedit") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + request.getParameter("auser") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + request.getParameter("acomment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + request.getParameter("timestamp") + "\" />\n";
		}//#########################################################################
		else if ( request.getParameter("kvmappingedit") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + request.getParameter("v") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kvmappingedit\" value=\"" + request.getParameter("kvmappingedit") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + request.getParameter("auser") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + request.getParameter("acomment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + request.getParameter("timestamp") + "\" />\n";
		}//#########################################################################
		else if ( request.getParameter("dmappingedit") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + request.getParameter("datatype") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"dmappingedit\" value=\"" + request.getParameter("dmappingedit") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + request.getParameter("auser") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + request.getParameter("acomment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + request.getParameter("timestamp") + "\" />\n";
		}

		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</article>\n";
		return re;
    }
}