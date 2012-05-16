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
import java.sql.SQLException;

/**
 *
 * @author J. Nathanael Philipp
 * @version 2.0
 */
public class TemplatesAccountSettings {
	public static String accountSettings(String setting, String search) throws ClassNotFoundException, SQLException {
		String re = "";

		re += "\t\t\t\t<section class=\"account\">\n";
		re += "\t\t\t\t\t<aside>\n";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=account&setting=password\">Change Password</a></li>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=account&setting=email\">Change Email</a></li>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=account&setting=branch\">Switch Working Branch</a></li>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=account&setting=reset\">Reset Userbranch</a></li>\n";
		re += "\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t</aside>\n";

		if ( setting.equals("start") ) {
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t\t<p>Please choose the setting you wish to change.</p>\n";
			re += "\t\t\t\t\t</article>\n";
		}
		else if ( setting.equals("password") ) {
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t\t<fieldset style=\"width: 25em;\">\n";
			re += "\t\t\t\t\t\t\t<legend>Change Password</legend>\n";
			re += "\t\t\t\t\t\t\t<form action=\"?tab=account&setting=password" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t\t<ul>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>Old password:</label><input type=\"password\" name=\"old\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>New password:</label><input type=\"password\" name=\"new\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>Confirm new password:</label><input type=\"password\" name=\"new2\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"password\" value=\"Save\" /></li>\n";
			re += "\t\t\t\t\t\t\t\t</ul>\n";
			re += "\t\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t\t</fieldset>\n";
			re += "\t\t\t\t\t</article>\n";
		}
		else if ( setting.equals("email") ) {
			DatabaseBremen db = DatabaseBremen.getInstance();
			db.connect();
			Object[][] a = db.execute("SELECT email FROM lgd_user WHERE username='" + User.getInstance().getUsername() + "'");
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t<p>Your current email is: " + a[0][0] + "</p><br />\n";
			re += "\t\t\t\t\t\t<fieldset style=\"width: 25em;\">\n";
			re += "\t\t\t\t\t\t\t<legend>Change Email</legend>\n";
			re += "\t\t\t\t\t\t\t<form action=\"?tab=account&setting=email" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t\t<ul>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>New Email:</label><input type=\"text\" name=\"new\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>Password:</label><input type=\"password\" name=\"password\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"email\" value=\"Save\" /></li>\n";
			re += "\t\t\t\t\t\t\t\t</ul>\n";
			re += "\t\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t\t</fieldset>\n";
			re += "\t\t\t\t\t</article>\n";
		}
		else if ( setting.equals("branch") ) {
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t\t<fieldset style=\"width: 25em;\">\n";
			re += "\t\t\t\t\t\t\t<legend>Switch Working Branch</legend>\n";
			re += "\t\t\t\t\t\t\t<form action=\"?tab=account&setting=branch" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t\t<ul>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>Branches:</label></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li>\n";
			re += "\t\t\t\t\t\t\t\t\t\t<div class=\"select\">";
			re += "\t\t\t\t\t\t\t\t\t\t\t<select name=\"branch\">\n";
			re += "\t\t\t\t\t\t\t\t\t\t\t\t<option value=\"user\" " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "" : "selected") + ">Your user branch.</option>\n";
			re += "\t\t\t\t\t\t\t\t\t\t\t\t<option value=\"main\" " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "selected" : "") + ">The main branch.</option>\n";
			re += "\t\t\t\t\t\t\t\t\t\t\t</select>\n";
			re += "\t\t\t\t\t\t\t\t\t\t</div>\n";
			re += "\t\t\t\t\t\t\t\t\t</li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"userspace\" value=\"Save\" /></li>\n";
			re += "\t\t\t\t\t\t\t\t</ul>\n";
			re += "\t\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t\t</fieldset>\n";
			re += "\t\t\t\t\t</article>\n";
		}
		else if ( setting.equals("reset") ) {
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t\t<p>If you want to reset all Mappings you had changed press the button below, please.</p>\n";
			re += "\t\t\t\t\t\t<form action=\"?tab=account&setting=restore\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t<input type=\"submit\" name=\"userspace\" value=\"Reset\" />\n";
			re += "\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t</article>\n";
		}

		re += "\t\t\t\t</section>\n";

		return re;
	}
        
        public static String accountSettingsAdmin(String setting, String search, String del) throws ClassNotFoundException, SQLException {
		String re = "";
                if(del.equalsIgnoreCase("kdel")){
                    DatabaseBremen db = DatabaseBremen.getInstance();
                    db.connect();                  
                    Object[][] a = db.execute("SELECT k, property, object FROM lgd_map_resource_k WHERE where last_history_id !=0");
                    for ( int i = 0; i <  a.length; i++ ) {
			db.execute("UPDATE lgd_map_resource_k SET last_history_id=null WHERE k="+a[i][0]+" and property="+a[i][1]+" and object="+a[i][2]+"");
                    }
                    db.execute("DELETE FROM lgd_map_resource_k_history");
                    db.disconnect();
                }else if(del.equalsIgnoreCase("kvdel")){
                    DatabaseBremen db = DatabaseBremen.getInstance();
                    db.connect();
                    Object[][] a = db.execute("SELECT k,v,property, object FROM lgd_map_resource_kv WHERE where last_history_id !=0");
                    for ( int i = 0; i <  a.length; i++ ) {
			db.execute("UPDATE lgd_map_resource_kv SET last_history_id=null WHERE k="+a[i][0]+" and v="+a[i][1]+"property="+a[i][2]+" and object="+a[i][3]+"");
                    }
                    db.execute("DELETE FROM lgd_map_resource_kv_history");
                    db.disconnect();
                }else if(del.equalsIgnoreCase("datadel")){
                    DatabaseBremen db = DatabaseBremen.getInstance();
                    db.connect();
                    Object[][] a = db.execute("SELECT k, datatype FROM lgd_map_datatype WHERE where last_history_id !=0");
                    for ( int i = 0; i <  a.length; i++ ) {
			db.execute("UPDATE lgd_map_datatype SET last_history_id=null WHERE k="+a[i][0]+" and datatype="+a[i][1]+"");
                    }
                    db.execute("DELETE FROM lgd_map_datatype_history");
                    db.disconnect();
                }else if(del.equalsIgnoreCase("litdel")){
                    DatabaseBremen db = DatabaseBremen.getInstance();
                    db.connect();
                    Object[][] a = db.execute("SELECT k, property, language FROM lgd_map_literal WHERE where last_history_id !=0");
                    for ( int i = 0; i <  a.length; i++ ) {
			db.execute("UPDATE lgd_map_literal SET last_history_id=null WHERE k="+a[i][0]+" and property="+a[i][1]+"and language="+a[i][2]+"");
                    }
                    db.execute("DELETE FROM lgd_map_literal_history");
                    db.disconnect();
                }
                
                re += "\t\t\t\t\t<article>\n";
		re += "\t\t\t\t\t\t<fieldset style=\"width: 25em;\">\n";
                
		re += "\t\t\t\t\t\t\t<form action=\"?tab=settings&del=kdel" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t\t\t<ul>\n";
                re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"deletek\" value=\"Delete Edithistory K-Mappings\" />\n";
		re += "\t\t\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t\t\t</form>\n";
                
                re += "\t\t\t\t\t\t\t<form action=\"?tab=settings&del=kvdel" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t\t\t<ul>\n";
                re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"deletek\" value=\"Delete Edithistory KV-Mappings\" />\n";
		re += "\t\t\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t\t\t</form>\n";
                
                re += "\t\t\t\t\t\t\t<form action=\"?tab=settings&del=datadel" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t\t\t<ul>\n";
                re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"deletek\" value=\"Delete Edithistory Datatype-Mappings\" />\n";
		re += "\t\t\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t\t\t</form>\n";
                
                re += "\t\t\t\t\t\t\t<form action=\"?tab=settings&del=litdel" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
		re += "\t\t\t\t\t\t\t\t<ul>\n";
                re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"deletek\" value=\"Delete Edithistory Literal-Mappings\" />\n";
		re += "\t\t\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t\t\t</form>\n";
                
		re += "\t\t\t\t\t\t</fieldset>\n";
		re += "\t\t\t\t\t</article>\n";
                
                
                
                return re;
        }
}