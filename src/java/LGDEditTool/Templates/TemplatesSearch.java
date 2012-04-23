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
 * @author J. Nathanael Philipp
 * @version 2.0
 */
public class TemplatesSearch {
	/**
	 * Template for search field.
	 * @return String
	 */
	public static String search() {
		String re = "\t\t\t\t<fieldset class=\"search\">\n";
		re += "\t\t\t\t\t<legend>Search</legend>\n";
		re += "\t\t\t\t\t<form method=\"get\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>\n";
		re += "\t\t\t\t\t\t\t\t<label>Search:</label>\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"text\" id=\"search\" name=\"search\" required />\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"tab\" value=\"search\">\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"submit\" value=\"Search\" />\n";
		re += "\t\t\t\t\t\t\t</li>\n";
		re += "\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</fieldset>\n";
		return re;
	}

	/**
	 * Template for search results.
	 * @param search search query
	 * @return String
	 * @throws Exception 
	 */
	public static String searchResult(String search) throws Exception {
		DatabaseBremen.getInstance().connect();
		String re = "", tmp;

		tmp = kMapping((search.contains("~") ? search.split("~")[0] : search ));
		if ( !tmp.equals("") )
			re += tmp;

		tmp = "\n\t\t\t\t<br /><br />\n\n" + kvMapping(search);
		if ( !tmp.equals("\n\t\t\t\t<br /><br />\n\n") )
			re += tmp;

		tmp = "\n\t\t\t\t<br /><br />\n\n" + datatypeMapping((search.contains("~") ? search.split("~")[0] : search ));
		if ( !tmp.equals("\n\t\t\t\t<br /><br />\n\n") )
			re += tmp;

		tmp = "\n\t\t\t\t<br /><br />\n\n" + literalMapping((search.contains("~") ? search.split("~")[0] : search ));
		if ( !tmp.equals("\n\t\t\t\t<br /><br />\n\n") )
			re += tmp;

		if ( re.equals("") )
			re += "<p>Your search returned no results.</p>";

		return re;
	}

	/**
	 * Template for K-Mapping results.
	 * @param search search query
	 * @return String
	 * @throws Exception 
	 */
	private static String kMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a = database.execute("SELECT k, property, object, user_id, count(k) FROM lgd_map_resource_k WHERE " + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%' " : "k='" + search + "'") + " AND " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "user_id='main' AND object!='' AND property!=''" : "((user_id='main' AND property != '' AND object != '' AND (k, property, object) IN (SELECT k, property, object FROM lgd_map_resource_k WHERE user_id='" + User.getInstance().getUsername() + "')) OR (user_id='" + User.getInstance().getUsername() + "' AND property != '' AND object != '' AND (k, property, object) NOT IN (SELECT k, property, object FROM lgd_map_resource_k WHERE user_id='main')) OR (user_id='main' AND property != '' AND object != '' AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id='" + User.getInstance().getUsername() + "')))") + " GROUP BY k, property, object, user_id ORDER BY k");

		if ( a.length == 0 )
			return "";

		//K-Mappings
		re += "\t\t\t\t<h2>K-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>edit</th>\n";
		re += "\t\t\t\t\t\t<th>delete</th>\n";
		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t<th>commit</th>\n";
		re += "\t\t\t\t\t</tr>\n";

		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"k" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][1].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][2].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][4] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + i + "')\">Delete</a></td>\n";

			if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				re += "\t\t\t\t\t\t<td>" + (a[i][3].equals("main") ? "Commit" : "<a onclick=\"toggle_visibility('kc" + i + "')\">Commit</a>") + "</td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += kMappingEdit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][4].toString());
			//delete
			re += kMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][4].toString());
			//commit
			if ( !a[i][3].equals("main") && !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				re += kMappingCommit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][4].toString());
		}

		re += "\t\t\t\t</table>\n";
		return re;
	}

	/**
	 * Template for K-Mapping edit fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String kMappingEdit(String search, int i, String k, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"k" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"property\" name=\"property\" value=\"" + property + "\" style=\"width: 27em;\" required /></td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"object\" name=\"object\" value=\"" + object + "\" style=\"width: 27em;\" required /></td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t\t<td>Commit</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("k" + i + "u", "kmapping", "Save", (!User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 7 : 6));
		re += "\t\t\t\t\t</form>\n";
		return re;
	}

	/**
	 * Template for K-Mapping delete fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String kMappingDelete(String search, int i, String k, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kd" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + i + "')\">Hide</a></td>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t\t<td>Commit</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("kd" + i + "u", "kmapping", "Delete", (!User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 7 : 6));
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for K-Mapping edit fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String kMappingCommit(String search, int i, String k, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kc" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kc" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("kc" + i + "u", "kmapping", "Commit", 7);
		re += "\t\t\t\t\t</form>\n";
		return re;
	}

	/**
	 * Template for KV-Mapping results.
	 * @param search search query
	 * @return String
	 * @throws Exception 
	 */
	private static String kvMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a;
		if ( search.contains("~") )
			a = database.execute("SELECT k, v, property, object, user_id, count(k) FROM lgd_map_resource_kv WHERE k='" + search.split("~")[0] + "' AND v='" + search.split("~")[1] + "' AND " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "user_id='main' AND property!='' AND object!=''" : "((user_id='main' AND (k, v, property, object) IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='" + User.getInstance().getUsername() + "' AND property != '' AND object != '')) OR (user_id='" + User.getInstance().getUsername() + "' AND property != '' AND object != '' AND (k, v, property, object) NOT IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='main')) OR (user_id='main' AND property != '' AND object != '' AND (k, v) NOT IN (SELECT k, v FROM lgd_map_resource_kv WHERE user_id='" + User.getInstance().getUsername() + "')))") + " GROUP BY k, v, property, object, user_id ORDER BY count(k), v");
		else
			a = database.execute("SELECT k, v, property, object, user_id, count(k) FROM lgd_map_resource_kv WHERE (" + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "k='" + search + "'") + " OR " + (search.contains("*") ? "v LIKE '" + search.replaceAll("\\*", "%") + "%'" : "v='" + search + "'") + ") AND " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "user_id='main' AND property!='' AND object!=''" : "((user_id='main' AND (k, v, property, object) IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='" + User.getInstance().getUsername() + "' AND property != '' AND object != '')) OR (user_id='" + User.getInstance().getUsername() + "' AND property != '' AND object != '' AND (k, v, property, object) NOT IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='main')) OR (user_id='main' AND property != '' AND object != '' AND (k, v) NOT IN (SELECT k, v FROM lgd_map_resource_kv WHERE user_id='" + User.getInstance().getUsername() + "')))") + " GROUP BY k, v, property, object, user_id ORDER BY count(k), v");

		if ( a.length == 0 )
			return "";

		//KV-Mappings
		re += "\t\t\t\t<h2>KV-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>v</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>edit</th>\n";
		re += "\t\t\t\t\t\t<th>delete</th>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t<th>commit</th>\n";
		re += "\t\t\t\t\t</tr>\n";

		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"kv" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][1] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][2].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][3].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][5] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvd" + i + "')\">Delete</a></td>\n";

			if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				re += "\t\t\t\t\t\t<td>" + (a[i][4].equals("main") ? "Commit" : "<a onclick=\"toggle_visibility('kvc" + i + "')\">Commit</a>") + "</td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += kvMappingEdit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][5].toString());
			//delete
			re += kvMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][5].toString());
			//commit
			if ( !a[i][4].equals("main") && !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				re += kvMappingCommit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][5].toString());
		}

		re += "\t\t\t\t</table>";

		return re;
	}

	/**
	 * Template for KV-Mapping edit fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param v v
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String kvMappingEdit(String search, int i, String k, String v, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kv" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"property\" name=\"property\" value=\"" + property + "\" style=\"width: 23em;\" /></td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"object\" name=\"object\" value=\"" + object + "\" style=\"width: 23em;\" /></td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t\t<td>Commit</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("kv" + i + "u", "kvmapping", "Save", (!User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 8 : 7));
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for KV-Mapping delete fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param v v
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String kvMappingDelete(String search, int i, String k, String v, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kvd" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvd" + i + "')\">Hide</a></td>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t\t<td>Commit</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("kvd" + i +"u", "kvmapping", "Delete", (!User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 8 : 7));
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for KV-Mapping commit fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param v v
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String kvMappingCommit(String search, int i, String k, String v, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kvc" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvc" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("kvc" + i +"u", "kvmapping", "Commit", 8);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for Datatype-Mapping results.
	 * @param search search query
	 * @return String
	 * @throws Exception 
	 */
	private static String datatypeMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a = database.execute("SELECT k, datatype, user_id, count(k) FROM lgd_map_datatype WHERE " + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "k='" + search + "'") + " AND " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "user_id='main'" : "((user_id='main' AND datatype != 'deleted' AND (k, datatype) IN (SELECT k, datatype FROM lgd_map_datatype WHERE user_id='" + User.getInstance().getUsername() + "')) OR (user_id='" + User.getInstance().getUsername() + "' AND datatype!='deleted' AND (k, datatype) NOT IN (SELECT k, datatype FROM lgd_map_datatype WHERE user_id='main')) OR (user_id='main' AND datatype != 'deleted' AND k NOT IN (SELECT k FROM lgd_map_datatype WHERE user_id='" + User.getInstance().getUsername() + "')))") + " GROUP BY k, datatype, user_id ORDER BY k");

		if ( a.length == 0 )
			return "";

		//Datatype-Mappings
		re += "\t\t\t\t<h2>Datatype-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>datatype</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>Edit</th>\n";
		re += "\t\t\t\t\t\t<th>Delete</th>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t<th>Commit</th>\n";
		re += "\t\t\t\t\t</tr>\n";
		
		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"tk" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][1] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][3] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkd" + i + "')\">Delete</a></td>\n";

			if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				re += "\t\t\t\t\t\t<td>" + (a[i][2].equals("main") ? "Commit" : "<a onclick=\"toggle_visibility('tkc" + i + "')\">Commit</a>") + "</td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += datatypeMappingEdit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][3].toString());
			//delete
			re += datatypeMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][3].toString());
			//commit
			if ( !a[i][2].equals("main") && !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				re += datatypeMappingCommit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][3].toString());
		}

		re += "\t\t\t\t</table>";

		return re;
	}

	/**
	 * Template for Datatype-Mapping edit fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param datatype datatype
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String datatypeMappingEdit(String search, int i, String k, String datatype, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"tk" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td align=\"center\"><label>Datatype: </label>\n";
		re += "\t\t\t\t\t\t\t\t<div class=\"select\"><select name=\"datatype\">\n";
		re += "\t\t\t\t\t\t\t\t\t<option value=\"boolean\" " + (datatype.equals("boolean") ? "selected" : "") + ">boolean</option>\n";
		re += "\t\t\t\t\t\t\t\t\t<option value=\"int\" " + (datatype.equals("int") ? "selected" : "") + ">int</option>\n";
		re += "\t\t\t\t\t\t\t\t\t<option value=\"float\" " + (datatype.equals("float") ? "selected" : "") + ">float</option>\n";
		re += "\t\t\t\t\t\t\t\t</select></div>\n";
		re += "\t\t\t\t\t\t\t</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"adatatype\" value=\"" + datatype + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t\t<td>Commit</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("tk" + i + "u", "dmapping", "Save", (!User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 6 : 5));
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for Datatype-Mapping delete fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param datatype datatype
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String datatypeMappingDelete(String search, int i, String k, String datatype, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"tkd" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + datatype + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkd" + i + "')\">Hide</a></td>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t\t<td>Commit</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("tkd" + i + "u", "dmapping", "Delete", (!User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 6 : 5));
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for Datatype-Mapping delete fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param datatype datatype
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String datatypeMappingCommit(String search, int i, String k, String datatype, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"tkc" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + datatype + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkc" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("tkc" + i + "u", "dmapping", "Commit", 6);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	private static String literalMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a = database.execute("SELECT k, property, language, user_id, count(k) FROM lgd_map_literal WHERE " + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "k='" + search + "'") + " AND " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "user_id='main'" : "((user_id='main' AND property != '' AND (k, property, language) IN (SELECT k, property, language FROM lgd_map_literal WHERE user_id='" + User.getInstance().getUsername() + "')) OR (user_id='" + User.getInstance().getUsername() + "' AND property!='' AND (k, property, language) NOT IN (SELECT k, property, language FROM lgd_map_literal WHERE user_id='main')) OR (user_id='main' AND property != '' AND k NOT IN (SELECT k FROM lgd_map_literal WHERE user_id='" + User.getInstance().getUsername() + "')))") + " GROUP BY k, property, language, user_id ORDER BY k");

		if ( a.length == 0 )
			return "";

		//Literal-Mappings
		re += "\t\t\t\t<h2>Literal-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>language</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>Edit</th>\n";
		re += "\t\t\t\t\t\t<th>Delete</th>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t<th>Commit</th>\n";
		re += "\t\t\t\t\t</tr>\n";
		
		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"lk" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][1].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][2] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][4] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lk" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lkd" + i + "')\">Delete</a></td>\n";

			if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				re += "\t\t\t\t\t\t<td>" + (a[i][3].equals("main") ? "Commit" : "<a onclick=\"toggle_visibility('lkc" + i + "')\">Commit</a>") + "</td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += literalMappingEdit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][4].toString());
			//delete
			re += literalMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][4].toString());
			//commit
			if ( !a[i][3].equals("main") && !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
				re += literalMappingCommit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][4].toString());
		}

		re += "\t\t\t\t</table>";

		return re;
	}

	/**
	 * Template for K-Mapping edit fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String literalMappingEdit(String search, int i, String k, String property, String language, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"lk" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"property\" name=\"property\" value=\"" + property + "\" style=\"width: 27em;\" required /></td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"language\" value=\"" + language + "\" style=\"width: 27em;\" /></td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"alanguage\" value=\"" + language + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lk" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t\t<td>Commit</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("lk" + i + "u", "lmapping", "Save", (!User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 7 : 6));
		re += "\t\t\t\t\t</form>\n";
		return re;
	}

	/**
	 * Template for K-Mapping delete fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param property property
	 * @param language object
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String literalMappingDelete(String search, int i, String k, String property, String language, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"lkd" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + language + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"language\" value=\"" + language + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lkd" + i + "')\">Hide</a></td>\n";

		if ( !User.getInstance().getView().equals(Functions.MAIN_BRANCH) )
			re += "\t\t\t\t\t\t\t<td>Commit</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("lkd" + i + "u", "lmapping", "Delete", (!User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? 7 : 6));
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for K-Mapping edit fields.
	 * @param search search query
	 * @param i counter
	 * @param k k
	 * @param property property
	 * @param language object
	 * @param affectedEntities affected entities
	 * @return String
	 */
	private static String literalMappingCommit(String search, int i, String k, String property, String language, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (User.getInstance().isLoggedIn() ? "" : "&captcha=yes") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"lkc" + i + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + language + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"language\" value=\"" + language + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lkc" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserField("lkc" + i + "u", "lmapping", "Commit", 7);
		re += "\t\t\t\t\t</form>\n";
		return re;
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

	public static String captcha(HttpServletRequest request, String search) {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha(Functions.PUBLIC_reCAPTCHA_KEY, Functions.PRIVATE_reCAPTCHA_KEY, false);

		String re = "\t\t\t\t<article class=\"captcha\">\n";
		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>"+ c.createRecaptchaHtml(null, null) + "</li>\n";
		re += "\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"fcaptcha\" value=\"Send\" /></li>\n";
		re += "\t\t\t\t\t\t</ul>\n";

		if ( request.getParameter("kmapping") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			if ( !request.getParameter("kmapping").equals("Delete") ) {
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + request.getParameter("aproperty") + "\" />\n";
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + request.getParameter("aobject") + "\" />\n";
			}

			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kmapping\" value=\"" + request.getParameter("kmapping") + "\" />\n";
		}//#########################################################################
		else if ( request.getParameter("kvmapping") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + request.getParameter("v") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			if ( !request.getParameter("kvmapping").equals("Delete") ) {
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + request.getParameter("aproperty") + "\" />\n";
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + request.getParameter("aobject") + "\" />\n";
			}

			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kvmapping\" value=\"" + request.getParameter("kvmapping") + "\" />\n";
		}//#########################################################################
		else if ( request.getParameter("dmapping") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + request.getParameter("datatype") + "\" />\n";
			if ( !request.getParameter("dmapping").equals("Delete") )
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"adatatype\" value=\"" + request.getParameter("adatatype") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"dmapping\" value=\"" + request.getParameter("dmapping") + "\" />\n";
		}//#########################################################################
		else if ( request.getParameter("lmapping") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"language\" value=\"" + request.getParameter("language") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			if ( !request.getParameter("lmapping").equals("Delete") ) {
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + request.getParameter("aproperty") + "\" />\n";
				re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"alanguage\" value=\"" + request.getParameter("alanguage") + "\" />\n";
			}

			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"lmapping\" value=\"" + request.getParameter("lmapping") + "\" />\n";
		}

		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</article>\n";

		return re;
	}
}