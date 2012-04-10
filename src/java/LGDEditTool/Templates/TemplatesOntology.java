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
import java.util.ArrayList;

/**
 *
 * @author Alexander Richter, J. Nathanael Philipp
 * @version 2.0
 */
public class TemplatesOntology {

	/**
	 * Template for Ontology
	 * @param search search
	 * @return String
	 * @throws Exception 
	 */
	public static String ontology(String search) throws Exception {
		DatabaseBremen.getInstance().connect();
		String s = "";

		if ( search.contains("#") )
			search = search.split("#")[0];

		s += "\t\t\t\t<div id=\"ontology\">\n";
		s += leftside(search);
		s += "\t\t\t\t\t<aside>\n";
		s += rightside(search);
		s += "\t\t\t\t\t</aside>\n";
		s += "\t\t\t\t</div>\n";

		return s;
	}

	/**
	 * left side content
	 * @param search
	 * @return
	 * @throws Exception 
	 */
	private static String leftside(String search) throws Exception{
		DatabaseBremen database = DatabaseBremen.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT k, v, language, label FROM lgd_map_label Where k='" + search + "'");

		s += "\t\t\t\t\t<section>\n";
		s += "\t\t\t\t\t\t<form action=\"?tab=ontology&search=" + search + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		s += "\t\t\t\t\t\t\t<fieldset>\n";
		s += "\t\t\t\t\t\t\t\t<table>\n";
		s += "\t\t\t\t\t\t\t\t\t<tr>\n";
		s += "\t\t\t\t\t\t\t\t\t\t<td>label:</td>\n";
		s += "\t\t\t\t\t\t\t\t\t\t<td><input type=\"text\" name=\"label\" value=\"" + search + "\"></td>\n";
		s += "\t\t\t\t\t\t\t\t\t</tr>\n";
		s += "\t\t\t\t\t\t\t\t\t<tr>\n";
		s += "\t\t\t\t\t\t\t\t\t\t<td>localization:</td>\n";
		s += "\t\t\t\t\t\t\t\t\t\t<td><input type=\"text\" name=\"localization\" value=\"\"></td>\n";
		s += "\t\t\t\t\t\t\t\t\t</tr>\n";
		s += "\t\t\t\t\t\t\t\t\t<tr>\n";
		s += "\t\t\t\t\t\t\t\t\t\t<td>superclass:</td>\n";
		s += "\t\t\t\t\t\t\t\t\t\t<td><input type=\"text\" name=\"superclass\" value=\"" + search + "\"></td>\n";
		s += "\t\t\t\t\t\t\t\t\t</tr>\n";
		s += "\t\t\t\t\t\t\t\t\t<tr>\n";
		s += "\t\t\t\t\t\t\t\t\t\t<td colspan=\"2\"><input type=\"submit\" name=\"save\" value=\"Save\" /> <input type=\"submit\" name=\"clear\" value=\"Clear\" /> <input type=\"submit\" name=\"delete\" value=\"Delete\" /> </td>\n";
		s += "\t\t\t\t\t\t\t\t\t</tr>\n";
		s += "\t\t\t\t\t\t\t\t</table>\n";
		s += "\t\t\t\t\t\t\t</fieldset>\n";
		s += "\t\t\t\t\t\t</form>\n";
		s += "\t\t\t\t\t</section>\n";

		return s;
	}

	/**
	 * right-side content
	 * @param search
	 * @return
	 * @throws Exception
	 */
	private static String rightside(String search) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT k FROM lgd_map_resource_kv WHERE v='" + search + "' AND " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "user_id='main' AND object!='' AND property!=''" : "((user_id='main' AND (k, v, property, object) IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='" + User.getInstance().getUsername() + "')) OR (user_id='" + User.getInstance().getUsername() + "' AND property != '' AND object != '' AND (k, v, property, object) NOT IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='main')) OR (user_id='main' AND (k, v) NOT IN (SELECT k, v FROM lgd_map_resource_kv WHERE user_id='" + User.getInstance().getUsername() + "')))") + " GROUP BY k ORDER BY k");

		//one tree for each superclass
		if ( a.length > 0 ) {
			for ( int i = 0; i < a.length; i++ ) {
				s += "\t\t\t\t\t\t<div><a href=\"javascript:treeview('" + i + "')\">+</a>" + a[i][0] + "\n";
				s += "\t\t\t\t\t\t<div class=\"hidden\" id=\"" + i + "\">\n";
				s +="\t\t\t\t\t\t<a href=\"javascript:treeview('00" + i + "')\">\\<b id=\"" + i + "p\" style=\"display:inline;\" >+</b><b id=\"" + i + "m\" style=\"display:none\">-</b></a>" + search + "\n";
				s +="\t\t\t\t\t\t\t" + addSubClasses(search, "00" + i);
				s +="\t\t\t\t\t\t</div>\n";
			}
		}
		else { 
			int i = 0;
			String sub = addSubClasses(search, "" + i);
			if ( sub.equals("") ) {
				s += "\t\t\t\t\t\t<div>\n";
				s +="\t\t\t\t\t\t\\-" + search + "\n";
				s +="\t\t\t\t\t\t</div>\n";
			}
			else {
				s += "\t\t\t\t\t\t<div>\n";
				s +="\t\t\t\t\t\t<a href=\"javascript:treeview('00" + i + "')\">\\<b id=\"" + i + "p\" style=\"display:inline\" >+</b><b id=\"" + i + "m\" style=\"display:none\">-</b></a>" + search + "\n";
				s +="\t\t\t\t\t\t\t" + addSubClasses(search, "00" + i) + "\n";
				s +="\t\t\t\t\t\t</div>\n";
			}
		}

		return s;
	}

	private static String addSubClasses(String search, String id) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		String s = "";

		Object[][] a = database.execute("SELECT v FROM lgd_map_resource_kv WHERE k='" + search + "' AND " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "user_id='main' AND object!='' AND property!=''" : "((user_id='main' AND (k, v, property, object) IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='" + User.getInstance().getUsername() + "')) OR (user_id='" + User.getInstance().getUsername() + "' AND property != '' AND object != '' AND (k, v, property, object) NOT IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='main')) OR (user_id='main' AND (k, v) NOT IN (SELECT k, v FROM lgd_map_resource_kv WHERE user_id='" + User.getInstance().getUsername() + "')))") + " GROUP BY v ORDER BY v");

		s += "\t\t\t\t\t\t<div class=\"hidden\" id=\"" + id + "\">\n";
		for ( int i = 0; i < a.length; i++ ) {
			String sub = "";//addSubClasses(a[i][0].toString(), "" + id + i);
			if ( sub.equals("") ) {
				s +="\t\t\t\t\t\t\\-" + a[i][0] + "<br />\n";
			}
			else {
				s += "\t\t\t\t\t\t<a href=\"javascript:treeview('" + id + i + "')\">\\<b id=\"" + i + "p\" style=\"display:inline\" >+</b><b id=\"" + i + "m\" style=\"display:none\">-</b></a>" + a[i][0] + "<br />\n";
				s += "\t\t\t\t\t\t\t" + sub + "\n";
			}
		}

		s += "\t\t\t\t\t\t</div>\n";
		if ( a.length == 0 )
			s ="";

		return s;
	}
}