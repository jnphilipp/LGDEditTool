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

/**
 *
 * @author Alexander Richter, J. Nathanael Philipp
 * @version 2.0
 */
public class TemplatesUnmappedTags {
	/**
	 * search field.
	 * @return 
	 */
	public static String search() {
		String re = "\t\t\t\t<fieldset class=\"search\">\n";
		re += "\t\t\t\t\t<legend>Search</legend>\n";
		re += "\t\t\t\t\t<form method=\"get\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>\n";
		re += "\t\t\t\t\t\t\t\t<label>Search:</label>\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"text\" id=\"search\" name=\"search\" required />\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"tab\" value=\"unmapped\">\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"submit\" value=\"Search\" />\n";
		re += "\t\t\t\t\t\t\t</li>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=unmapped\">Clear search.</a></li>\n";
		re += "\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</fieldset>\n";
		return re;
	}

	/**
	 * Template Unmapped Tags. This Template is used by the 'UnmappedTags'-tab.
	 * @param ksite current K-Mapping site
	 * @param kvsite current KV-Mapping site
	 * @throws Exception
	 * @return Returns a String with HTML-code.
	 */
	static public String unmappedTags(String ksite, String kvsite, String search) throws Exception {
		DatabaseBremen.getInstance().connect();

		//kmapping table
		String s = "\t\t\t\t<h2>List of all Unmapped Tags</h2>\n";
		s += "\t\t\t\t<table class=\"table\">\n";
		s += "\t\t\t\t\t<tr>\n";
		s += "\t\t\t\t\t\t<th>k</th>\n";
		s += "\t\t\t\t\t\t<th>usage_count</th>\n";
		s += "\t\t\t\t\t\t<th>distinct_value_count</th>\n";
		s += "\t\t\t\t\t\t<th>create mapping</th>\n";
		s += "\t\t\t\t\t\t<th>create literal</th>\n";
		s += "\t\t\t\t\t\t<th>create datatype</th>\n";
		s += "\t\t\t\t\t</tr>\n";

		//fill table with k-mappings
		s += listAllk(Integer.valueOf(ksite),Integer.valueOf(kvsite), search);
		s += "\t\t\t\t</table>\n";

		//prev-next-site
		s += "\t\t\t\t<div style=\"float: right;\">\n";
		if(Integer.valueOf(ksite)>1){
			Integer prevsite=Integer.valueOf(ksite)-1;
			s += "\t\t\t\t\t<a href=\"?tab=unmapped&ksite="+ prevsite.toString() + "&kvsite="+kvsite+"\">&#60;prev</a>&nbsp;&nbsp;&nbsp; ";
		}
		Integer nextsite=Integer.valueOf(ksite)+1;
		s += "\t\t\t\t\t<a href=\"?tab=unmapped&ksite="+ nextsite.toString() + "&kvsite="+kvsite+"\">next&#62;</a>\n";
		s += "\t\t\t\t</div>\n";
		s += "\t\t\t\t<br /><br /><br />\n";

		//kvmapping table
		s += "\t\t\t\t<table class=\"table\">\n";
		s += "\t\t\t\t\t<tr>\n";
		s += "\t\t\t\t\t\t<th>k</th>\n";
		s += "\t\t\t\t\t\t<th>v</th>\n";
		s += "\t\t\t\t\t\t<th>usage_count</th>\n";
		s += "\t\t\t\t\t\t<th>create</th>\n";
		s += "\t\t\t\t\t</tr>\n";
                
		//fill table with kv-mappings
		s += listAllkv(Integer.valueOf(ksite),Integer.valueOf(kvsite), search);
		s += "\t\t\t\t</table>\n";

		//prev-next-site
		s += "\t\t\t\t<div style=\"float: right;\">\n";
		if(Integer.valueOf(kvsite)>1){
			Integer prevsite=Integer.valueOf(kvsite)-1;
			s += " \t\t\t\t\t<a href=\"?tab=unmapped&ksite="+ ksite + "&kvsite="+prevsite.toString()+"\">&#60;prev</a>&nbsp;&nbsp;&nbsp; ";
		}
		Integer nextsite2=Integer.valueOf(kvsite)+1;
		s += "\t\t\t\t\t<a href=\"?tab=unmapped&ksite="+ ksite + "&kvsite="+nextsite2.toString()+"\">next&#62;</a>\n";
		s += "\t\t\t\t</div>\n";
		s += "\t\t\t\t<br />\n";

		return s;
	}

	/**
	 * Template for K-Mappings. Fills table with K-mappings
	 * @param ksite current K-Mapping site
	 * @param kvsite current KV-Mapping site
	 * @throws Exception 
	 * @return Returns a String with HTML-code.
	 */
	private static String listAllk(int ksite, int kvsite, String search) throws Exception {
		String s = "";
		DatabaseBremen database = DatabaseBremen.getInstance();

		Object[][] a = database.execute("SELECT k, usage_count, distinct_value_count FROM lgd_stat_tags_k a WHERE NOT EXISTS (Select b.k FROM ( Select k FROM lgd_map_datatype WHERE datatype != 'deleted' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' UNION ALL SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_literal WHERE property != '' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' UNION ALL SELECT k FROM lgd_map_property UNION ALL SELECT k FROM lgd_map_resource_k WHERE property != '' AND object != '' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' UNION ALL SELECT k FROM lgd_map_resource_kv WHERE property != '' AND object != '' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "' UNION ALL SELECT k FROM lgd_map_resource_prefix ) b WHERE a.k=b.k) " + (search.equals("") ? "" : (search.contains("*") ? "AND k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "AND k='" + (search.contains("~") ? search.split("~")[0] : search) + "'")) + " LIMIT 20 OFFSET " + (ksite-1)*20);

		for ( int i = 0; i < a.length; i++ ) {
			s += kMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), ksite, kvsite);
		}

		return s;
	}

	private static String kMapping(int i, String k, String usage_count, String distinct_value_count, int ksite, int kvsite) {
		String s;

		s = "\t\t\t\t\t<tr id=\"k" + i + "a\">\n";
		s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t<td>" + usage_count + "</td>\n";
		s += "\t\t\t\t\t\t<td>" +  distinct_value_count + "</td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Mapping</a></td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kl" + i + "')\">Literal</a></td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + i + "')\">Datatype</a></td>\n";
		s += "\t\t\t\t\t</tr>\n";

		//create mapping
		s += "\t\t\t\t\t<form action=\"?tab=search&search=" + k + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t<tr id=\"k" + i + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"property\" name=\"property\" placeholder=\"property\" style=\"width: 27em;\" required /></td>\n";
		s += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"object\" name=\"object\" placeholder=\"object\" style=\"width: 27em;\" required /></td>\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t<td>Literal</td>\n";
		s += "\t\t\t\t\t\t\t<td>Datatype</td>\n";
		s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("k" + i + "u", "kmapping", "Create", 6);
		s += "\t\t\t\t\t</form>\n";

		//create literal
		s += "\t\t\t\t\t<form action=\"?tab=search&search=" + k + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t<tr id=\"kl" + i + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"property\" name=\"property\" placeholder=\"property\" style=\"width: 27em;\" required /></td>\n";
		s += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"language\" placeholder=\"language\" style=\"width: 27em;\" required /></td>\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t<td>Mapping</td>\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kl" + i + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t<td>Datatype</td>\n";
		s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("kl" + i + "u", "lmapping", "Create", 6);
		s += "\t\t\t\t\t</form>\n";

		//create datatype
		s += "\t\t\t\t\t<form action=\"?tab=search&search=" + k + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t<tr id=\"kd" + i + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\"><label>Datatype: </label>\n";
		s += "\t\t\t\t\t\t\t\t<div class=\"select\"><select name=\"datatype\">\n";
		s += "\t\t\t\t\t\t\t\t\t<option value=\"boolean\">boolean</option>\n";
		s += "\t\t\t\t\t\t\t\t\t<option value=\"int\">int</option>\n";
		s += "\t\t\t\t\t\t\t\t\t<option value=\"float\">float</option>\n";
		s += "\t\t\t\t\t\t\t\t</select></div>\n";
		s += "\t\t\t\t\t\t\t</td>\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t<td>Mapping</td>\n";
		s += "\t\t\t\t\t\t\t<td>Literal</td>\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + i + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("kd" + i + "u", "dmapping", "Create", 6);
		s += "\t\t\t\t\t</form>\n";
												
		return s;
	}

	/**
	 * Template for KV-Mappings. Fills table with KV-mappings
         * @param ksite current K-Mapping site
	 * @param kvsite current KV-Mapping site
	 * @throws Exception 
         * @return Returns a String with HTML-code.
	 */
	static private String listAllkv(int ksite,int kvsite, String search) throws Exception {
		String s = new String();
		DatabaseBremen database = DatabaseBremen.getInstance();

		Object[][] a = database.execute("SELECT k, v, usage_count FROM lgd_stat_tags_kv a WHERE NOT EXISTS (Select b.k FROM (SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_resource_kv WHERE property != '' AND object != '' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "') b WHERE a.k=b.k) " + (search.equals("") ? "" : (search.contains("*") ? "AND k LIKE '" + search.replaceAll("\\*", "%") + "%' OR v LIKE '" + search.replaceAll("\\*", "%") + "%'" : (search.contains("~") ? "AND k='" + search.split("~")[0] + "' AND v='" + search.split("~")[1] + "'" : "AND k='" + search + "' OR v='" + search + "'"))) + " LIMIT 20 OFFSET " + (kvsite-1)*20);

		for ( int i = 0; i < a.length; i++ ) {
			s += kvMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString());
		}

		return s;
	}

	private static String kvMapping(int i, String k, String v, String usage_count) {
		String s;
		s = "\t\t\t\t\t<tr id=\"kv" + i + "a\">\n";
		s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t<td>" + v + "</td>\n";
		s += "\t\t\t\t\t\t<td>" +  usage_count + "</td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Create</a></td>\n";
		s += "\t\t\t\t\t</tr>\n";

		//create mapping
		s += "\t\t\t\t\t<form action=\"?tab=search&search=" + k + "-" + v + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t<tr id=\"kv" + i + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t<td>k: " + k + "<br />v: " + v + "</td>\n";
		s += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"property\" name=\"property\" placeholder=\"property\" style=\"width: 27em;\" required /></td>\n";
		s += "\t\t\t\t\t\t\t<td><input type=\"text\" class=\"object\" name=\"object\" placeholder=\"object\" style=\"width: 27em;\" required /></td>\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("kv" + i + "u", "kvmapping", "Create", 4);
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
		String re = "\t\t\t\t\t\t<tr id=\"" + id + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td colspan=\"" + 3 + "\" align=\"center\">\n";
		re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
		re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" placeholder=\"No comment.\" required></textarea>\n";
		re += "\t\t\t\t\t\t\t</td>\n";
		re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 4 ? 1 : 3) + "\" align=\"center\">\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />\n";
		re += "\t\t\t\t\t\t\t</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";

		return re;
	}
}