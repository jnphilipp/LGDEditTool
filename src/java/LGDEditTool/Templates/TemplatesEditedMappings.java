/*
* This file is part of LGDEditTool (LGDET).
*
* LGDET is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* any later version.
*
* LGDET is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with LGDET. If not, see <http://www.gnu.org/licenses/>.
*/

package LGDEditTool.Templates;

import LGDEditTool.Functions;
import LGDEditTool.SiteHandling.User;
import LGDEditTool.db.LGDDatabase;
import java.sql.SQLException;

/**
*
* @author J. Nathanael Philipp
* @version 1.0
*/
public class TemplatesEditedMappings {
	/**
	 * Generates HTML code for buttons to comment all K/KV/Datatype/Literal-Mappings or for all mappings.
	 * @param type k/kv/datatype/literal
	 * @param search search
	 * @param sort sort
	 * @return HTML code
	 */
	public static String commit(String type, String search, String sort) {
		String s = "";

		s += "\t\t\t\t<fieldset class=\"search\">\n";
		s += "\t\t\t\t\t<legend>Commit Mappings</legend>\n";
		s += "\t\t\t\t\t<form action=\"?tab=edited&type=" + type + (search.equals("") ? "" : "&search=" + search) + (sort.equals("") ? "" : "&sort=" + sort) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t<br />\n";
		s += "\t\t\t\t\t\t<div style=\"text-align: center;\">\n";
		s += "\t\t\t\t\t\t\t<input type=\"submit\" name=\"commitK\" value=\"Commit all K-Mappings\" />\n";
		s += "\t\t\t\t\t\t\t<input type=\"submit\" name=\"commitKV\" value=\"Commit all KV-Mappings\" />\n";
		s += "\t\t\t\t\t\t\t<input type=\"submit\" name=\"commitDatatype\" value=\"Commit all Datatype-Mappings\" />\n";
		s += "\t\t\t\t\t\t\t<input type=\"submit\" name=\"commitLiteral\" value=\"Commit all Literal-Mappings\" />\n";
		s += "\t\t\t\t\t\t\t<input type=\"submit\" name=\"commitAll\" value=\"Commit all Mappings\" />\n";
		s += "\t\t\t\t\t\t\t<br /><br />\n";
		s += "\t\t\t\t\t\t\t<label>Comment:</label></br>\n";
		s += "\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" placeholder=\"No comment.\" required></textarea>\n";
		s += "\t\t\t\t\t\t</div>";
		s += "\t\t\t\t\t\t<br />\n";
		s += "\t\t\t\t\t</form>\n";
		s += "\t\t\t\t</fieldset>\n";

		return s;
	}

	/**
	 * Generating HTML code for the edited mappings tab.
	 * @param type k/kv/datatype/literal
	 * @param site current displsyed site
	 * @return HTML code
	 * @throws Exception
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	public static String listEditedMappings(String type, String site) throws Exception, ClassNotFoundException, SQLException {
		LGDDatabase.getInstance().connect();// make sure database is connected
		String s = "";

		if ( !site.equals("") ) {
			if ( Integer.valueOf(site) < 1 ) {
				site = "1";
			}
		} //set negativ site value to 1

		if ( type.equalsIgnoreCase("k") ) {
			//insert tablehead
			s = "\t\t\t\t\t<h2>List of all edited K-Mappings</h2>\n";
			s += "\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t\t\t<th>k</th>\n";
			s += "\t\t\t\t\t\t\t<th>property</th>\n";
			s += "\t\t\t\t\t\t\t<th>object</th>\n";
			s += "\t\t\t\t\t\t\t<th>affected Entities</th>\n";
			s += "\t\t\t\t\t\t\t<th>clear</th>\n";
			s += "\t\t\t\t\t\t<th>commit</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert edithistory from db
			s += listAllKMappings(Integer.parseInt(site));
		}
		else if ( type.equalsIgnoreCase("kv") ) {
			//insert tablehead
			s = "\t\t\t\t\t<h2>List of all edited KV-Mappings</h2>\n";
			s += "\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t\t\t<th>k</th>\n";
			s += "\t\t\t\t\t\t\t<th>v</th>\n";
			s += "\t\t\t\t\t\t\t<th>property</th>\n";
			s += "\t\t\t\t\t\t\t<th>object</th>\n";
			s += "\t\t\t\t\t\t\t<th>affected Entities</th>\n";
			s += "\t\t\t\t\t\t\t<th>clear</th>\n";
			s += "\t\t\t\t\t\t<th>commit</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert edithistory from db
			s += listAllKVMappings(Integer.parseInt(site));
		}
		else if ( type.equalsIgnoreCase("datatype") ) {
			//insert tablehead
			s = "\t\t\t\t\t<h2>List of all edited Datatype-Mappings</h2>\n";
			s += "\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t\t\t<th>k</th>\n";
			s += "\t\t\t\t\t\t\t<th>datatype</th>\n";
			s += "\t\t\t\t\t\t\t<th>affected Entities</th>\n";
			s += "\t\t\t\t\t\t\t<th>clear</th>\n";
			s += "\t\t\t\t\t\t<th>commit</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert edithistory from db
			s += listAllDatatypeMappings(Integer.parseInt(site));
		}
		else if ( type.equalsIgnoreCase("literal") ) {
			//insert tablehead
			s = "\t\t\t\t\t<h2>List of all edited Literal-Mappings</h2>\n";
			s += "\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t\t\t<th>k</th>\n";
			s += "\t\t\t\t\t\t\t<th>property</th>\n";
			s += "\t\t\t\t\t\t\t<th>language</th>\n";
			s += "\t\t\t\t\t\t\t<th>affected Entities</th>\n";
			s += "\t\t\t\t\t\t\t<th>clear</th>\n";
			s += "\t\t\t\t\t\t<th>commit</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert edithistory from db
			s += listAllLiteralMappings(Integer.parseInt(site));
		}

		//insert table foot
		s += "\t\t\t\t\t</table>\n";

		//prev-next-site
		s += "\t\t\t\t\t<div style=\"float: right;\">\n";
		if ( Integer.valueOf(site) > 1 ) {
			int prevsite = Integer.valueOf(site) - 1;
			s += "\t\t\t\t\t\t<a href=\"?tab=edited&type=" + type + "&site=" + prevsite + "\">&#60;prev</a>&nbsp;&nbsp;&nbsp; ";
		}
		int nextsite = Integer.valueOf(site) + 1;
		s += "\t\t\t\t\t\t<a href=\"?tab=edited&type=" + type + "&site="+ nextsite + "\">next&#62;</a>\n";
		s += "\t\t\t\t\t</div>\n";

		return s;
	}
        


	/**
	 * Generates the HTML code for K-Mappings.
	 * @param site current site
	 * @return HTML code
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	private static String listAllKMappings(int site) throws ClassNotFoundException, SQLException {
		LGDDatabase database = LGDDatabase.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT km.k, property, object, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_resource_k AS km LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=km.k WHERE user_id='" + User.getInstance().getUsername() + "' AND (km.k, property, object) NOT IN (SELECT k, property, object FROM lgd_map_resource_k WHERE user_id='main') ORDER BY k Limit 20 OFFSET " + ((site-1)*20));

		for ( int i = 0; i < a.length; i++ ) {
			s += addKMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), site);
		}

		return s;
	}

	/**
	 * Generates the HTML code for KV-Mappings.
	 * @param site current site
	 * @return HTML code
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	private static String listAllKVMappings(int site) throws ClassNotFoundException, SQLException {
		LGDDatabase database = LGDDatabase.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT kvm.k, kvm.v, property, object, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_resource_kv AS kvm LEFT OUTER JOIN lgd_stat_tags_kv ON lgd_stat_tags_kv.k=kvm.k AND lgd_stat_tags_kv.v=kvm.v WHERE user_id='" + User.getInstance().getUsername() + "' AND (kvm.k, kvm.v, property, object) NOT IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='main') ORDER BY k, v Limit 20 OFFSET " + ((site-1)*20));

		for ( int i = 0; i < a.length; i++ ) {
			s += addKVMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString(), site);
		}

		return s;
	}

	/**
	 * Generates the HTML code for Datatype-Mappings.
	 * @param site current site
	 * @return HTML code
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	private static String listAllDatatypeMappings(int site) throws ClassNotFoundException, SQLException {
		LGDDatabase database = LGDDatabase.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT dm.k, datatype, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_datatype AS dm LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=dm.k WHERE user_id='" + User.getInstance().getUsername() + "' AND (dm.k, datatype) NOT IN (SELECT k, datatype FROM lgd_map_datatype WHERE user_id='main') ORDER BY k Limit 20 OFFSET " + ((site-1)*20));

		for ( int i = 0; i < a.length; i++ ) {
			s += addDatatypeMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), site);
		}

		return s;
	}

	/**
	 * Generates the HTML code for Literal-Mappings.
	 * @param site current site
	 * @return HTML code
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	private static String listAllLiteralMappings(int site) throws ClassNotFoundException, SQLException {
		LGDDatabase database = LGDDatabase.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT lm.k, property, language, COALESCE(usage_count, 0) AS usage_count FROM lgd_map_literal AS lm LEFT OUTER JOIN lgd_stat_tags_k ON lgd_stat_tags_k.k=lm.k WHERE user_id='" + User.getInstance().getUsername() + "' AND (lm.k, property, language) NOT IN (SELECT k, property, language FROM lgd_map_literal WHERE user_id='main') ORDER BY k Limit 20 OFFSET " + ((site-1)*20));

		for ( int i = 0; i < a.length; i++ ) {
			s += addLiteralMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), site);
		}

		return s;
	}

	/**
	 * Generates the HTML code for K-Mappings.
	 * @param id id
	 * @param k k
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @param site site
	 * @return HTML code
	 */
	private static String addKMapping(int id, String k, String property, String object, String affectedEntities, int site) {
		String s = "\t\t\t\t\t\t<tr id=\"k" + id + "a\">\n";
		s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		if ( property.equals("") && object.equals("") )
			s += "\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">deleted</td>\n";
		else {
			s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
			s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
		}
		s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">Clear</a></td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kc" + id + "')\">Commit</a></td>\n";
		s += "\t\t\t\t\t\t</tr>\n";
		s += "\t\t\t\t\t\t<form action=\"?tab=edited&type=k&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<tr id=\"k" + id + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		if ( property.equals("") && object.equals("") )
			s += "\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">deleted</td>\n";
		else {
			s += "\t\t\t\t\t\t<td>" + property + "</td>\n";
			s += "\t\t\t\t\t\t<td>" + object + "</td>\n";
		}
		s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t<td>Commit</td>\n";
		s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("k" + id + "u", "kmapping", "Clear", 6);
    s += "\t\t\t\t\t\t</form>\n";

		//commit
		s += "\t\t\t\t\t\t<form action=\"?tab=edited&type=k&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<tr id=\"kc" + id + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		if ( property.equals("") && object.equals("") )
			s += "\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">deleted</td>\n";
		else {
			s += "\t\t\t\t\t\t<td>" + property + "</td>\n";
			s += "\t\t\t\t\t\t<td>" + object + "</td>\n";
		}
		s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<td>Clear</td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kc" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("kc" + id + "u", "kmapping", "Commit", 6);
		s += "\t\t\t\t\t\t</form>\n";

		return s;
	}

	/**
	 * Generates the HTML code for KV-Mappings.
	 * @param id id
	 * @param k k
	 * @param v v
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected entities
	 * @param site site
	 * @return HTML code
	 */
	private static String addKVMapping(int id, String k, String v, String property, String object, String affectedEntities, int site) {
		String s = "\t\t\t\t\t<tr id=\"kv" + id + "a\">\n";
    s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + v + "</td>\n";
		if ( property.equals("") && object.equals("") )
			s += "\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">deleted</td>\n";
		else {
			s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
			s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
		}
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + id + "')\">Clear</a></td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvc" + id + "')\">Commit</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=edited&type=kv&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
    s += "\t\t\t\t\t\t<tr id=\"kv" + id + "\" style=\"display: none;\">\n";
    s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
    if ( property.equals("") && object.equals("") )
			s += "\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">deleted</td>\n";
		else {
			s += "\t\t\t\t\t\t<td>" + property + "</td>\n";
			s += "\t\t\t\t\t\t<td>" + object + "</td>\n";
		}
    s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
    s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t<td>Commit</td>\n";
    s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("kv" + id + "u", "kvmapping", "Clear", 7);
		s += "\t\t\t\t\t</form>\n";

		//commit
		s += "\t\t\t\t\t\t<form action=\"?tab=edited&type=kv&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<tr id=\"kvc" + id + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<td>" + v + "</td>\n";
		if ( property.equals("") && object.equals("") )
			s += "\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">deleted</td>\n";
		else {
			s += "\t\t\t\t\t\t<td>" + property + "</td>\n";
			s += "\t\t\t\t\t\t<td>" + object + "</td>\n";
		}
		s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<td>Clear</td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvc" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("kvc" + id + "u", "kvmapping", "Commit", 7);
		s += "\t\t\t\t\t\t</form>\n";

		return s;
	}

	/**
	 * Generates the HTML code for Datatype-Mappings.
	 * @param id id
	 * @param k k
	 * @param datatype datatype
	 * @param affectedEntities affected entities
	 * @param site site
	 * @return HTML code
	 */
	private static String addDatatypeMapping(int id, String k, String datatype, String affectedEntities, int site) {
		String s = "\t\t\t\t\t\t<tr id=\"tk" + id + "a\">\n";
		s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + id + "')\">Clear</a></td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkc" + id + "')\">Commit</a></td>\n";
		s += "\t\t\t\t\t\t</tr>\n";
		s += "\t\t\t\t\t\t<form action=\"?tab=edited&type=datatype&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<tr id=\"tk" + id + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + datatype + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t<td>Commit</td>\n";
		s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("tk" + id + "u", "dmapping", "Clear", 5);
    s += "\t\t\t\t\t\t</form>\n";

		//commit
		s += "\t\t\t\t\t\t<form action=\"?tab=edited&type=datatype&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<tr id=\"tkc" + id + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + datatype + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<td>Clear</td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkc" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("tkc" + id + "u", "dmapping", "Commit", 5);
		s += "\t\t\t\t\t\t</form>\n";

		return s;
	}

	/**
	 * Generates the HTML code for Language-Mappings.
	 * @param id id
	 * @param k k
	 * @param property property
	 * @param language language
	 * @param affectedEntities affected entities
	 * @param site site
	 * @return HTML code
	 */
	private static String addLiteralMapping(int id, String k, String property, String language, String affectedEntities, int site) {
		String s = "\t\t\t\t\t\t<tr id=\"lk" + id + "a\">\n";
		s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		if ( property.equals("") )
			s += "\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">deleted</td>\n";
		else {
			s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
			s += "\t\t\t\t\t\t<td>" + language + "</td>\n";
		}
		s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lk" + id + "')\">Clear</a></td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lkc" + id + "')\">Commit</a></td>\n";
		s += "\t\t\t\t\t\t</tr>\n";
		s += "\t\t\t\t\t\t<form action=\"?tab=edited&type=literal&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<tr id=\"lk" + id + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		if ( property.equals("") )
			s += "\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">deleted</td>\n";
		else {
			s += "\t\t\t\t\t\t<td>" + property + "</td>\n";
			s += "\t\t\t\t\t\t<td>" + language + "</td>\n";
		}
		s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"language\" value=\"" + language + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lk" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t<td>Commit</td>\n";
		s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("lk" + id + "u", "lmapping", "Clear", 6);
    s += "\t\t\t\t\t\t</form>\n";

		//commit
		s += "\t\t\t\t\t\t<form action=\"?tab=edited&type=literal&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<tr id=\"lkc" + id + "\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		if ( property.equals("") )
			s += "\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">deleted</td>\n";
		else {
			s += "\t\t\t\t\t\t<td>" + property + "</td>\n";
			s += "\t\t\t\t\t\t<td>" + language + "</td>\n";
		}
		s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"language\" value=\"" + language + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<td>Edit</td>\n";
		s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('lkc" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("lkc" + id + "u", "lmapping", "Commit", 6);
		s += "\t\t\t\t\t\t</form>\n";

		return s;
	}

	/**
	 * Template for user fields.
	 * @param id id for toggle visiblity
	 * @param submitName submit name
	 * @param submitValue submit value
	 * @param columns column count
	 * @return HTML code
	 */
	private static String getUserField(String id, String submitName, String submitValue, int columns) {
		String re = "\t\t\t\t\t\t\t<tr id=\"" + id + "\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t\t<td colspan=\"" + (columns - 2) + "\" align=\"center\">\n";
		re += "\t\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
		re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" placeholder=\"No comment.\" required></textarea>\n";
		re += "\t\t\t\t\t\t\t\t</td>\n";
		re += "\t\t\t\t\t\t\t\t<td colspan=\"" + 2 + "\" align=\"center\">\n";
		re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />";
		re += "\t\t\t\t\t\t\t\t</td>\n";
		re += "\t\t\t\t\t\t\t</tr>\n";

		return re;
	}
}