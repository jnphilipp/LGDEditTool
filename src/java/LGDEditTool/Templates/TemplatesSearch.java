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

import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import LGDEditTool.Functions;
import LGDEditTool.db.DatabaseBremen;
import LGDEditTool.SiteHandling.User;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class TemplatesSearch {
	/**
	 * Template for Searchfield.
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
	public static String searchResult(String search, User user) throws Exception {
		DatabaseBremen database = new DatabaseBremen();
		String re = "";

		database.connect();

		re += kMapping(database, user, (search.contains("-") ? search.split("-")[0] : search ));
		re += "\n\t\t\t\t<br /><br />\n\n";
		re += kvMapping(database, user, search);

		database.disconnect();
		return re;
	}

	private static String kMapping(DatabaseBremen database, User user, String search) throws Exception {
		String re = "";
		Object[][] a = database.execute("SELECT k, property, object, count(k) FROM lgd_map_resource_k WHERE k='" + search + "' GROUP BY k, object, property ORDER BY k");

		//K-Mappings
		re = "\t\t\t\t<h2>K-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>Edit</th>\n";
		re += "\t\t\t\t\t\t<th>Delete</th>\n";
		re += "\t\t\t\t\t</tr>\n";

		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"k" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][1].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][2].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][3] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + i + "')\">Delete</a></td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += kMappingEdit(search, user, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString());
			//delete
			re += kMappingDelete(search, user, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString());
		}

		re += "\t\t\t\t</table>\n";
		return re;
	}

	private static String kMappingEdit(String search, User user, int i, String k, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + ((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"k" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"property\" value=\"" + property + "\" style=\"width: 27em;\" required /></td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"object\" value=\"" + object + "\" style=\"width: 27em;\" required /></td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";

		if ( !user.isLoggedIn() ) {
			re += "\t\t\t\t\t\t<tr id=\"k" + i + "u\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + user.getUsername() + "\" required />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kmapping\" value=\"Save\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}
		else {
			re += "\t\t\t\t\t\t<tr id=\"k" + i + "u\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"4\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kmapping\" value=\"Save\" />\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + user.getUsername() + "\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}

		re += "\t\t\t\t\t</form>\n";
		return re;
	}

	private static String kMappingDelete(String search, User user, int i, String k, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + ((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kd" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";

		if ( !user.isLoggedIn() ) {
			re += "\t\t\t\t\t\t<tr id=\"kd" + i + "u\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + user.getUsername() + "\" required />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kmapping\" value=\"Delete\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}
		else {
			re += "\t\t\t\t\t\t<tr id=\"kd" + i + "u\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"4\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required >No comment.</textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kmapping\" value=\"Delete\" />\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + user.getUsername() + "\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}

		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	private static String kvMapping(DatabaseBremen database, User user, String search) throws Exception {
		String re = "";
		Object[][] a;
		if ( search.contains("-") )
			a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv WHERE k='" + search.split("-")[0] + "' AND v='" + search.split("-")[1] + "' GROUP BY object, property, k, v ORDER BY k, v");
		else
			a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv WHERE k='" + search + "' GROUP BY object, property, k, v ORDER BY k, v");

		//KV-Mappings
		re += "\t\t\t\t<h2>KV-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>v</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>Edit</th>\n";
		re += "\t\t\t\t\t\t<th>Delete</th>\n";
		re += "\t\t\t\t\t</tr>\n";

		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"kv" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][1] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][2].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + Functions.shortenURL(a[i][3].toString()) + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][4] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvd" + i + "')\">Delete</a></td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += kvMappingEdit(search, user, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString());
			//delete
			re += kvMappingDelete(search, user, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString());
		}

		re += "\t\t\t\t</table>";

		return re;
	}

	private static String kvMappingEdit(String search, User user, int i, String k, String v, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + ((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kv" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"property\" value=\"" + property + "\" style=\"width: 23em;\" /></td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"object\" value=\"" + object + "\" style=\"width: 23em;\" /></td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";

		if ( !user.isLoggedIn() ) {
			re += "\t\t\t\t\t\t<tr id=\"kv" + i + "u\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"3\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + user.getUsername() + "\" required />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kvmapping\" value=\"Save\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}
		else {
			re += "\t\t\t\t\t\t<tr id=\"kv" + i + "u\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"5\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kvmapping\" value=\"Save\" />\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + user.getUsername() + "\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}

		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	private static String kvMappingDelete(String search, User user, int i, String k, String v, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + ((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"kvd" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvd" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";

		if ( !user.isLoggedIn() ) {
			re += "\t\t\t\t\t\t<tr id=\"kvd" + i + "u\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"3\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + user.getUsername() + "\" required />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kvmapping\" value=\"Delete\" />";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}
		else {
			re += "\t\t\t\t\t\t<tr id=\"kvd" + i + "u\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"5\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kvmapping\" value=\"Delete\" />";
			re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + user.getUsername() + "\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}

		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	public static String captcha(HttpServletRequest request, String search) {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha("6LcryM4SAAAAAAxmbh2VvI-GZXGpCRqcaSO2xL1B", "6LcryM4SAAAAAKHGFwoD1t-tQsWB_QGuNInVNYbp", false);
		String re;
		re = "\t\t\t\t<article class=\"captcha\">\n";
		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>"+ c.createRecaptchaHtml(null, null) + "</li>\n";
		re += "\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"" + (request.getParameter("kmapping") != null ? "kmapping" : "kvmapping") + "captcha\" value=\"Send\" /></li>\n";
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
		}
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
		}

		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</article>\n";
		return re;
	}
}