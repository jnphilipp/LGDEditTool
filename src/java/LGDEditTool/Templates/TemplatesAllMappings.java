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
import LGDEditTool.db.DatabaseBremen;
import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

/**
*
* @author Alexander Richter
*/
public class TemplatesAllMappings {
	/**
	 * Template for EditHistory. This Template is used by the 'AllMappings'-tab.
	 * @param type mapping type, K or KV-Mapping
	 * @param site current displayed site
	 * @return Returns a String with HTML-code.
	 * @throws Exception 
	 */
	static public String listAllMappings(String type, String site) throws Exception {
		DatabaseBremen.getInstance().connect();// make sure database is connected
		String s = "";
                
		if ( !site.equals("") ) {
			if ( Integer.valueOf(site) < 1 ) {
				site = "1";
			}
		} //set negativ site value to 1

		if ( type.equalsIgnoreCase("k") ) {
			//insert tablehead
			s = "\t\t\t\t\t<h2>List of all K-Mappings</h2>\n";
			s += "\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t<tr class=\"mapping\">\n";
			s += "\t\t\t\t\t\t\t<th>k</th>\n";
			s += "\t\t\t\t\t\t\t<th>property</th>\n";
			s += "\t\t\t\t\t\t\t<th>object</th>\n";
			s += "\t\t\t\t\t\t\t<th>affected Entities</th>\n";
			s += "\t\t\t\t\t\t\t<th>edit</th>\n";
			s += "\t\t\t\t\t\t\t<th>delete</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert edithistory from db
			s += listAllKMappings(Integer.parseInt(site));
		}
		else if ( type.equalsIgnoreCase("kv") ) {
			//insert tablehead
			s = "\t\t\t\t\t<h2>List of all KV-Mappings</h2>\n";
			s += "\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t<tr class=\"mapping\">\n";
			s += "\t\t\t\t\t\t\t<th>k</th>\n";
			s += "\t\t\t\t\t\t\t<th>v</th>\n";
			s += "\t\t\t\t\t\t\t<th>property</th>\n";
			s += "\t\t\t\t\t\t\t<th>object</th>\n";
			s += "\t\t\t\t\t\t\t<th>affected Entities</th>\n";
			s += "\t\t\t\t\t\t\t<th>edit</th>\n";
			s += "\t\t\t\t\t\t\t<th>delete</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert edithistory from db
			s += listAllKVMappings(Integer.parseInt(site));
		}
		else if ( type.equalsIgnoreCase("datatype") ) {
			//insert tablehead
			s = "\t\t\t\t\t<h2>List of all Datatype-Mappings</h2>\n";
			s += "\t\t\t\t\t<table class=\"table\">\n";
			s += "\t\t\t\t\t\t<tr class=\"mapping\">\n";
			s += "\t\t\t\t\t\t\t<th>k</th>\n";
			s += "\t\t\t\t\t\t\t<th>datatype</th>\n";
			s += "\t\t\t\t\t\t\t<th>affected Entities</th>\n";
			s += "\t\t\t\t\t\t\t<th>edit</th>\n";
			s += "\t\t\t\t\t\t\t<th>delete</th>\n";
			s += "\t\t\t\t\t\t</tr>\n";

			//insert edithistory from db
			s += listAllDatatypeMappings(Integer.parseInt(site));
		}

		//insert table foot
		s += "\t\t\t\t\t</table>\n";

		//prev-next-site
		s += "\t\t\t\t\t<div style=\"float: right;\">\n";
		if ( Integer.valueOf(site) > 1 ) {
			int prevsite = Integer.valueOf(site) - 1;
			s += "\t\t\t\t\t\t<a href=\"?tab=all&type=" + type + "&site=" + prevsite + "\">&#60;prev</a>&nbsp;&nbsp;&nbsp; ";
		}
		int nextsite = Integer.valueOf(site) + 1;
		s += "\t\t\t\t\t\t<a href=\"?tab=all&type=" + type + "&site="+ nextsite + "\">next&#62;</a>\n";
		s += "\t\t\t\t\t</div>\n";

		return s;
	}
        


	/**
	 * Template for All K-Mappings. SQL query to get all K-mappings.
	 * @param site current site
	 * @return Returns a String with HTML-code.
	 * @throws Exception 
	 */
	static private String listAllKMappings(int site) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT k, property, object, count(k) FROM lgd_map_resource_k GROUP BY k,property, object ORDER BY k Limit 20 OFFSET " + ((site-1)*20));

		for ( int i = 0; i < a.length; i++ ) {
			s += addKMapping(i,a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), site);
		}

		return s;
	}

	/**
	 * Template for All KV-Mappings. SQL-Query for KV-Mappings and fills KV-Mapping table.
	 * @param site current site
	 * @throws Exception 
         * @return Returns a String with HTML-code.
	 */
	static private String listAllKVMappings(int site) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv GROUP BY k, v, property, object ORDER BY k,v Limit 20 OFFSET " + ((site-1)*20));

		for ( int i = 0; i < a.length; i++ ) {
			s += addKVMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString(), site);
		}

		return s;
	}

	/**
	 * Template for All Datatype-Mappings. SQL-Query for Datatype-Mappings.Fills Datatype-Mapping table.
	 * @param site current site
	 * @throws Exception 
         * @return Returns a String with HTML-code.
	 */
	static private String listAllDatatypeMappings(int site) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT k, datatype, count(k) FROM lgd_map_datatype GROUP BY k, datatype ORDER BY k Limit 20 OFFSET " + ((site-1)*20));

		for ( int i = 0; i < a.length; i++ ) {
			s += addDatatypeMapping(i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), site);
		}

		return s;
	}
    
	/**
	 * Template for K-Mappings. Fill K-Mapping columns.
	 * @param id table-column id
	 * @param k K-Mapping key
	 * @param property K-Mapping property
	 * @param object K-Mapping object
	 * @param affectedEntities K-Mapping affected Entities
	 * @param site current K-Mapping site
	 * @return Returns a String with HTML-code.
	 */
	static private String addKMapping(int id, String k, String property, String object, String affectedEntities, int site) {
		String s = "\t\t\t\t\t\t<tr id=\"k" + id + "a\">\n";
		s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">Edit</a></td>\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + id + "')\">Delete</a></td>\n";
		s += "\t\t\t\t\t\t</tr>\n";
		s += "\t\t\t\t\t\t<form action=\"?tab=all&type=k&site=" + site +(!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<tr id=\"k" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<td><input type=\"text\" name=\"property\" value=\"" + property + "\" style=\"width: 27em;\" required /></td>\n";
		s += "\t\t\t\t\t\t\t\t<td><input type=\"text\" name=\"object\" value=\"" + object + "\" style=\"width: 27em;\" required /></td>\n";
		s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + object + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<td>Delete</td>\n";
		s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("k" + id + "u", "kmapping", "Save", 6);
                s += "\t\t\t\t\t\t</form>\n";
    
                //delete
                s += "\t\t\t\t\t\t<form action=\"?tab=all&type=k&site=" + site + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
                s += "\t\t\t\t\t\t\t<tr id=\"kd" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
                s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
                s += "\t\t\t\t\t\t\t\t<td>" + property + "</td>\n";
                s += "\t\t\t\t\t\t\t\t<td>" + object + "</td>\n";
                s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
                s += "\t\t\t\t\t\t\t\t<td>Edit</td>\n";
                s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
                s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
                s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
                s += "\t\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kd" + id + "')\">Hide</a></td>\n";
                s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("kd" + id + "u", "kmapping", "Delete", 6);
		s += "\t\t\t\t\t\t</form>\n";

		return s;
	}

	/**
	 * Template for KV-Mappings. Fill KV-Mapping columns.
	 * @param id table-column id
	 * @param k KV-Mappings key
	 * @param v KV-Mappings value
	 * @param property KV-Mappings property
	 * @param object KV-Mappings object
	 * @param affectedEntities KV-Mappings affected Entities
	 * @param site current KV-Mapping site
	 * @return Returns a String with HTML-code.
	 */
	static private String addKVMapping(int id, String k, String v, String property, String object, String affectedEntities, int site) {
		String s = "\t\t\t\t\t<tr id=\"kv" + id + "a\">\n";
                s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + v + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
                s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
                s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + id + "')\">Edit</a></td>\n";
                s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvd" + id + "')\">Delete</a></td>\n";
                s += "\t\t\t\t\t</tr>\n";
                s += "\t\t\t\t\t<form action=\"?tab=all&type=kv&site=" + site + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
                s += "\t\t\t\t\t\t<tr id=\"kv" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
                s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
                s += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
                s += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"property\" value=\"" + property + "\" style=\"width: 23em;\" /></td>\n";
                s += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"object\" value=\"" + object + "\" style=\"width: 23em;\" /></td>\n";
                s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
                s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + id + "')\">Hide</a></td>\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + object + "\" />\n";
                s += "\t\t\t\t\t\t\t<td>Delete</td>\n";
                s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("kv" + id + "u", "kvmapping", "Save", 7);
		s += "\t\t\t\t\t</form>\n";

		//delete
                s += "\t\t\t\t\t<form action=\"?tab=all&type=kv&site=" + site + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t<tr id=\"kvd" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
                s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
                s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kvd" + id + "')\">Hide</a></td>\n";
                s += "\t\t\t\t\t\t</tr>\n";
		s += getUserField("kvd" + id + "u", "kvmapping", "Delete", 7);
		s += "\t\t\t\t\t</form>\n";

		return s;
	}

	/**
	 * Template for Datatype-Mappings. Fill Datatype-Mapping columns.
	 * @param id table-column id
	 * @param k Datatype-Mappings key
	 * @param datatype Datatype-Mappings datatype
	 * @param affectedEntities Datatype-Mappings affected Entities
	 * @param site current Datatype-Mappings site
	 * @return Returns a String with HTML-code.
	 */
	static private String addDatatypeMapping(int id, String k, String datatype, String affectedEntities, int site) {
		String s = "\t\t\t\t\t\t<tr id=\"tk" + id + "a\">\n";
		s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
		s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + id + "')\">Edit</a></td>\n";
		s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkd" + id + "')\">Delete</a></td>\n";
		s += "\t\t\t\t\t\t</tr>\n";
		s += "\t\t\t\t\t\t<form action=\"?tab=all&type=datatype&site=" + site +(!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<tr id=\"tk" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
		s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<td><input type=\"text\" name=\"datatype\" value=\"" + datatype + "\" style=\"width: 27em;\" required /></td>\n";
		s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + id + "')\">Hide</a></td>\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"adatatype\" value=\"" + datatype + "\" />\n";
		s += "\t\t\t\t\t\t\t\t<td>Delete</td>\n";
		s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("tk" + id + "u", "dmapping", "Save", 5);
                s += "\t\t\t\t\t\t</form>\n";
    
                //delete
                s += "\t\t\t\t\t\t<form action=\"?tab=all&type=datatype&site=" + site + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
                s += "\t\t\t\t\t\t\t<tr id=\"tkd" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
                s += "\t\t\t\t\t\t\t\t<td>" + k + "</td>\n";
                s += "\t\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
                s += "\t\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
                s += "\t\t\t\t\t\t\t\t<td>Edit</td>\n";
                s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
                s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + datatype + "\" />\n";
                s += "\t\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkd" + id + "')\">Hide</a></td>\n";
                s += "\t\t\t\t\t\t\t</tr>\n";
		s += getUserField("tkd" + id + "u", "dmapping", "Delete", 5);
		s += "\t\t\t\t\t\t</form>\n";

		return s;
	}

	/**
	 * Template for user fields. Contains User and Comment input-box for Editing and Deleting.
	 * @param id id for toggle visiblity
	 * @param submitName submit name
	 * @param submitValue submit value
	 * @param columns column count
	 * @return Returns a String with HTML-code.
	 */
	private static String getUserField(String id, String submitName, String submitValue, int columns) {
		String re = "";

		if ( !User.getInstance().isLoggedIn() ) {
			re += "\t\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"" + (columns == 7 ? "3" : "2") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + User.getInstance().getUsername() + "\" required />\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"" + (columns == 5 ? "1" : "2") + "\">\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t</tr>\n";
		}
		else {
			re += "\t\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"" + (columns == 7 ? "5" : "4") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"" + (columns == 5 ? "1" : "2") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + User.getInstance().getUsername() + "\" />\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t</tr>\n";
		}

		return re;
	}

	/**
	 * reCatpcha form. Request ReCaptcha from a user who is not logged in.
	 * @param request request
	 * @param type mapping type, K or KV-Mapping
	 * @param site current site
         * @return Returns a String with HTML-code.
	 */
	public static String captcha(HttpServletRequest request,String type,String site) {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha(Functions.PUBLIC_reCAPTCHA_KEY, Functions.PRIVATE_reCAPTCHA_KEY, false);

		String re = "\t\t\t\t<article class=\"captcha\">\n";
		re += "\t\t\t\t\t<form action=\"?tab=all&type="+ type +"&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
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
		}

		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</article>\n";
		return re;
	}
        
        //#######################################################
        //######  search functions    ###########################
        //#######################################################
        
        /**
         * Template for Searchfield.
         * @return @return Returns a String with HTML-code.
         */
        public static String search() {
		String re = "\t\t\t\t<fieldset class=\"search\">\n";
		re += "\t\t\t\t\t<legend>Search</legend>\n";
		re += "\t\t\t\t\t<form method=\"get\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>\n";
		re += "\t\t\t\t\t\t\t\t<label>Search:</label>\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"text\" id=\"search\" name=\"search\" required />\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"tab\" value=\"all\">\n";
		re += "\t\t\t\t\t\t\t\t<input type=\"submit\" value=\"Search\" />\n";
		re += "\t\t\t\t\t\t\t</li>\n";
		re += "\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</fieldset>\n";
		return re;
	}
        
        /**
	 * Template for search results. Builds String with HTML-Code that represent the search Result.
	 * @param search Search-string, which the user typed in.
	 * @return String with HTML-Code
	 * @throws Exception 
	 */
	public static String searchResult(String search) throws Exception {
		DatabaseBremen.getInstance().connect();
		String re = "", tmp = "";
                boolean result = false;

		tmp = kMapping((search.contains("-") ? search.split("-")[0] : search ));
		if ( !tmp.equals("") ) {
			re += tmp;
                        result = true;
                }

		tmp = "\n\t\t\t\t<br /><br />\n\n" + kvMapping(search);
		if ( !tmp.equals("\n\t\t\t\t<br /><br />\n\n") ) {
			re += tmp;
                        result = true;
                }

		tmp = "\n\t\t\t\t<br /><br />\n\n" + datatypeMapping((tmp.equals("\n\t\t\t\t<br /><br />\n\n") ? search : (search.contains("-") ? search.split("-")[0] : search )));
		if ( !tmp.equals("\n\t\t\t\t<br /><br />\n\n") ) {
			re += tmp;
                        result = true;
                }

                if ( !result )
                    re += "\t\t\t\t<p>Your search returned no results.</p>";

		return re;
	}
        
        
        /**
	 * Template for K-Mapping results. SQL-Query for K-Mappings and fills K-Mapping table.
	 * @param search Search-string, which the user typed in.
	 * @return String with HTML-Code
	 * @throws Exception 
	 */
	private static String kMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a = database.execute("SELECT k, property, object, count(k) FROM lgd_map_resource_k WHERE " + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "k='" + search + "'") + " GROUP BY k, property, object ORDER BY k");

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
			re += kMappingEdit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString());
			//delete
			re += kMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString());
		}

		re += "\t\t\t\t</table>\n";
		return re;
	}

	/**
	 * Template for K-Mapping edit fields. Elements for editing K-Mappings.
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k K-Mapping key
	 * @param property K-Mapping property
	 * @param object K-Mapping object
	 * @param affectedEntities K-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String kMappingEdit(String search, int i, String k, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
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
		re += getUserFieldSearch("k" + i + "u", "kmapping", "Save", 6);
		re += "\t\t\t\t\t</form>\n";
		return re;
	}

	/**
	 * Template for K-Mapping delete fields. Elements for deleting K-Mappings
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k K-Mapping key
	 * @param property K-Mapping property
	 * @param object K-Mapping object
	 * @param affectedEntities K-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String kMappingDelete(String search, int i, String k, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
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
		re += getUserFieldSearch("kd" + i + "u", "kmapping", "Delete", 6);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for KV-Mapping results. SQL-Query for KV-Mappings and fills KV-Mapping table.
	 * @param search Search-string, which the user typed in.
	 * @return String with HTML-Code
	 * @throws Exception 
	 */
	private static String kvMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a;
		if ( search.contains("-") )
			a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv WHERE k='" + search.split("-")[0] + "' AND v='" + search.split("-")[1] + "' GROUP BY k, v, property, object ORDER BY k, v");
		else
			a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv WHERE " + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "k='" + search + "'") + " OR " + (search.contains("*") ? "v LIKE '" + search.replaceAll("\\*", "%") + "%'" : "v='" + search + "'") + " GROUP BY k, v, property, object ORDER BY k, v");

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
			re += kvMappingEdit(search,i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString());
			//delete
			re += kvMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString());
		}

		re += "\t\t\t\t</table>";

		return re;
	}

	/**
	 * Template for KV-Mapping edit fields. Elements for editing KV-Mappings.
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k KV-Mapping key
	 * @param v KV-Mapping value
	 * @param property KV-Mapping property
	 * @param object KV-Mapping object
	 * @param affectedEntities KV-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String kvMappingEdit(String search, int i, String k, String v, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
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
		re += getUserFieldSearch("kv" + i + "u", "kvmapping", "Save", 7);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for KV-Mapping delete fields. Elements for deleting KV-Mappings
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k KV-Mapping key
	 * @param v KV-Mapping value
	 * @param property KV-Mapping property
	 * @param object KV-Mapping object
	 * @param affectedEntities KV-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String kvMappingDelete(String search,int i, String k, String v, String property, String object, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
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
		re += getUserFieldSearch("kvd" + i +"u", "kvmapping", "Delete", 7);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for Datatype-Mapping results. SQL-Query for Datatype-Mappings and fills Datatype-Mapping table.
	 * @param search Search-string, which the user typed in.
	 * @return String with HTML-Code
	 * @throws Exception 
	 */
	private static String datatypeMapping(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String re = "";
		Object[][] a;

		if ( search.contains("-") )
			a = database.execute("SELECT k, datatype, count(k) FROM lgd_map_datatype WHERE k='" + search.split("-")[0] + "' " + (search.split("-")[1].equals("int") || search.split("-")[1].equals("float") || search.split("-")[1].equals("boolean") ? "AND datatype='" + search.split("-")[1] + "'" : "" ) + " GROUP BY k, datatype ORDER BY k, datatype");
		else
			a = database.execute("SELECT k, datatype, count(k) FROM lgd_map_datatype WHERE " + (search.contains("*") ? "k LIKE '" + search.replaceAll("\\*", "%") + "%'" : "k='" + search + "'") + " GROUP BY k, datatype ORDER BY k, datatype");

		if ( a.length == 0 )
			return "";

		//Datatype-Mappings
		re = "\t\t\t\t<h2>Datatype-Mappings</h2>\n";
		re += "\t\t\t\t<table class=\"table\">\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>datatype</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th>Edit</th>\n";
		re += "\t\t\t\t\t\t<th>Delete</th>\n";
		re += "\t\t\t\t\t</tr>\n";
		
		for ( int i = 0; i < a.length; i++ ) {
			re += "\t\t\t\t\t<tr id=\"tk" + i + "a\">\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][1] + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][2] + "</td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + i + "')\">Edit</a></td>\n";
			re += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkd" + i + "')\">Delete</a></td>\n";
			re += "\t\t\t\t\t</tr>\n";

			//edit
			re += datatypeMappingEdit(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString());
			//delete
			re += datatypeMappingDelete(search, i, a[i][0].toString(), a[i][1].toString(), a[i][2].toString());
		}

		re += "\t\t\t\t</table>";

		return re;
	}

	/**
	 * Template for Datatype-Mapping edit fields. Elements for editing Datatype-Mappings.
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k Datatype-Mapping key
	 * @param datatype Datatype-Mapping datatype
	 * @param affectedEntities Datatype-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String datatypeMappingEdit(String search, int i, String k, String datatype, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"tk" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"datatype\" value=\"" + datatype + "\" style=\"width: 23em;\" /></td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tk" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"adatatype\" value=\"" + datatype + "\" />\n";
		re += "\t\t\t\t\t\t\t<td>Delete</td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserFieldSearch("tk" + i + "u", "dmapping", "Save", 5);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for Datatype-Mapping delete fields.  Elements for deleting Datatype-Mappings
	 * @param search Search-string, which the user typed in.
	 * @param i table-column id
	 * @param k Datatype-Mapping key
	 * @param datatype Datatype-Mapping datatype
	 * @param affectedEntities Datatype-Mapping affected entities
	 * @return String with HTML-Code
	 */
	private static String datatypeMappingDelete(String search, int i, String k, String datatype, String affectedEntities) {
		String re = "";

		re += "\t\t\t\t\t<form action=\"?tab=search&search=" + search + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t<tr id=\"tkd" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
		re += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + datatype + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t\t<td>Edit</td>\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
		re += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"datatype\" value=\"" + datatype + "\" />\n";
		re += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('tkd" + i + "')\">Hide</a></td>\n";
		re += "\t\t\t\t\t\t</tr>\n";
		re += getUserFieldSearch("tkd" + i + "u", "dmapping", "Delete", 5);
		re += "\t\t\t\t\t</form>\n";

		return re;
	}

	/**
	 * Template for user fields. Contains User and Comment input-box for Editing and Deleting.
	 * @param id id for toggle visiblity
	 * @param submitName submit name
	 * @param submitValue submit value
	 * @param columns column count
	 * @return String with HTML-Code
	 */
	private static String getUserFieldSearch(String id, String submitName, String submitValue, int columns) {
		String re = "";

		if ( !User.getInstance().isLoggedIn() ) {
			re += "\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 7 ? "3" : "2") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + User.getInstance().getUsername() + "\" required />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 5 ? "1" : "2") + "\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}
		else {
			re += "\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 7 ? "5" : "4") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t<td colspan=\"" + (columns == 5 ? "1" : "2") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />";
			re += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + User.getInstance().getUsername() + "\" />\n";
			re += "\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t</tr>\n";
		}

		return re;
	}
}