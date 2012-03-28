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
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

/**
*
* @author Alexander Richter
*/
public class TemplatesAllMappings {
	/**
	 * Template for EditHistory.
	 * @param type k or kv mapping
	 * @param site current displsyed site
	 * @return
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
			//al.add(tableHead);

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
	 * SQL query to get all k-mappings
	 * @param site current site
	 * @return String
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
	 * SQL query to get all K-Mappings.
	 * @param site current site
	 * @throws Exception 
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
	 * SQL query to get all Datatype-Mappings.
	 * @param site current site
	 * @throws Exception 
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
	 * Template for K-Mappings.
	 * @param id id
	 * @param k k
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected Entities
	 * @param site current site
	 * @return String
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
	 * Template for KV-Mappings.
	 * @param id id
	 * @param k k
	 * @param v v
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected Entities
	 * @param site current site
	 * @return String
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
	 * Template for Datatype-Mappings.
	 * @param id id
	 * @param k k
	 * @param datatype datatype
	 * @param affectedEntities affected Entities
	 * @param site current site
	 * @return String
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
			re += "\t\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"" + (columns == 7 ? "3" : "2") + "\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + User.getInstance().getUsername() + "\" required />\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
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
			re += "\t\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
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
	 * reCatpcha form
	 * @param request
	 * @param type
	 * @para site
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
}