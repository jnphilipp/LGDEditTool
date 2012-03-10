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

import LGDEditTool.db.DatabaseBremen;

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
		String re = "<fieldset class=\"search\">\n";
		re += "\t\t\t\t\t<legend>Search</legend>\n";
		re += "\t\t\t\t\t<form method=\"get\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>\n";
		re += "\t\t\t\t\t\t\t\t<label>Suche:</label>\n";
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
		DatabaseBremen database = new DatabaseBremen();
		String re = "";

		database.connect();

		Object[][] a = database.execute("SELECT k, property, object, count(k) FROM lgd_map_resource_k WHERE k='" + search + "' GROUP BY k, object, property ORDER BY k");

		//K-Mappings
		re = "\t\t\t\t<h2>K-Mappings</h2>\n";
		re += "\t\t\t\t<table style=\"width: 100%;\">\n";
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
			re += "\t\t\t\t\t\t<td>" + a[i][1] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][2] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][3] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a>Delete</a></td>\n";
			re += "\t\t\t\t\t</tr>\n";
			re += "\t\t\t\t\t<tr id=\"k" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t<form action=\"?tab=search&search=" + search + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"property\" value=\"" + a[i][1] + "\" style=\"width: 27em;\" /></td>\n";
			re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"object\" value=\"" + a[i][2] + "\" style=\"width: 27em;\" /></td>\n";
			re += "\t\t\t\t\t\t\t<td>" + a[i][3] + "</td>\n";
			re += "\t\t\t\t\t\t\t<td><input type=\"submit\" name=\"kmapping\" value=\"Save\" /></td>\n";
			re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + a[i][0] + "\" />\n";
			re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Hide</a></td>\n";
			re += "\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t</tr>\n";
		}

		re += "\t\t\t\t</table>\n\n\t\t\t\t<br /><br />\n\n";


		a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv WHERE k='" + search + "' GROUP BY object, property, k, v ORDER BY k, v");

		//KV-Mappings
		re += "\t\t\t\t<h2>KV-Mappings</h2>\n";
		re += "\t\t\t\t<table style=\"width: 100%;\">\n";
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
			re += "\t\t\t\t\t\t<td>" + a[i][2] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][3] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][4] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a>Delete</a></td>\n";
			re += "\t\t\t\t\t</tr>\n";
			re += "\t\t\t\t\t<tr id=\"kv" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t<form action=\"?tab=search&search=" + search + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t\t<td>" + a[i][1] + "</td>\n";
			re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"property\" value=\"" + a[i][2] + "\" style=\"width: 23em;\" /></td>\n";
			re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"object\" value=\"" + a[i][3] + "\" style=\"width: 23em;\" /></td>\n";
			re += "\t\t\t\t\t\t\t<td>" + a[i][4] + "</td>\n";
			re += "\t\t\t\t\t\t\t<td><input type=\"submit\" name=\"kvmapping\" value=\"Save\" /></td>\n";
			re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + a[i][0] + "\" />\n";
			re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + a[i][1] + "\" />\n";
			re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Hide</a></td>\n";
			re += "\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t</tr>\n";
		}

		re += "\t\t\t\t</table>";
		return re;
	}
}