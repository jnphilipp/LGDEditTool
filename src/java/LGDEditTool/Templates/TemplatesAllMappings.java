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

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import LGDEditTool.Functions;
import LGDEditTool.db.DatabaseBremen;
import LGDEditTool.SiteHandling.User;

/**
*
* @author Alexander Richter
*/
public class TemplatesAllMappings {
    
        static ArrayList<String> al = new ArrayList<String>();
        /**
* Template for EditHistory.
*/
static public String listAllMappings(String type,String site,User user) {
            
            String s = new String();
            s+="\t\t<a href=\"?tab=all&type=k\">K-mappings</a>";
            s+="\t\t<a href=\"?tab=all&type=kv\">KV-mappings</a>";
            
            if(site.equals("")==false){if(Integer.valueOf(site)<1){site="1";}} //set negativ site value to 1
                
            
            if(type.equalsIgnoreCase("k")){
               try{
                //insert tablehead
                String tableHead = "\t\t\t\t<h2>List of all K-Mappings</h2>\n";
                tableHead += "\t\t\t\t<table class=\"table\">\n";
                tableHead += "\t\t\t\t\t<tr class=mapping>\n";
                tableHead += "\t\t\t\t\t\t<th>k</th>\n";
                tableHead += "\t\t\t\t\t\t<th>property</th>\n";
                tableHead += "\t\t\t\t\t\t<th>object</th>\n";
                tableHead += "\t\t\t\t\t\t<th>affected Entities</th>\n";
                tableHead += "\t\t\t\t\t\t<th>edit</th>\n";
                tableHead += "\t\t\t\t\t\t<th>delete</th>\n";
                tableHead += "\t\t\t\t\t</tr>\n";
                al.add(tableHead);
                
                //insert edithistory from db
                listAllKMappings(Integer.parseInt(site),user);
                        
               }catch(Exception e){};
            }
            else if(type.equalsIgnoreCase("kv")){
               try{
                //insert tablehead
                String tableHead = "\t\t\t\t<h2>List of all KV-Mappings</h2>\n";
                tableHead += "\t\t\t\t<table class=\"table\">\n";
                tableHead += "\t\t\t\t\t<tr class=mapping>\n";
                tableHead += "\t\t\t\t\t\t<th>k</th>\n";
                tableHead += "\t\t\t\t\t\t<th>v</th>\n";
                tableHead += "\t\t\t\t\t\t<th>property</th>\n";
                tableHead += "\t\t\t\t\t\t<th>object</th>\n";
                tableHead += "\t\t\t\t\t\t<th>affected Entities</th>\n";
                tableHead += "\t\t\t\t\t\t<th>edit</th>\n";
                tableHead += "\t\t\t\t\t\t<th>delete</th>\n";
                tableHead += "\t\t\t\t\t</tr>\n";
                al.add(tableHead);
                
                //insert edithistory from db
                listAllKVMappings(Integer.parseInt(site),user);
                
                
               }catch(Exception e){};
            }
            //insert table foot
                String tableFoot = "\t\t\t\t</table>\n";
                al.add(tableFoot);
            //show more
                
            if(type.equalsIgnoreCase("k")){
                if(Integer.valueOf(site)>1){
                    Integer prevsite=Integer.valueOf(site)-1;
                    al.add(new String(" \n\t\t\t\t\t<a href=\"?tab=all&type=k&site="+ prevsite.toString() + "\">prev</a>\n\n "));
                }
                Integer nextsite=Integer.valueOf(site)+1;
                al.add(new String("\n\t\t\t\t\t<a href=\"?tab=all&type=k&site="+ nextsite.toString() + "\">next</a>\n"));
            }
            if(type.equalsIgnoreCase("kv")){
                if(Integer.valueOf(site)>1){
                    Integer prevsite=Integer.valueOf(site)-1;
                    al.add(new String(" \n\t\t\t\t\t<a href=\"?tab=all&type=kv&site="+ prevsite.toString() + "\">prev</a>\n "));
                }
                Integer nextsite=Integer.valueOf(site)+1;
                al.add(new String("\n\t\t\t\t\t<a href=\"?tab=all&type=kv&site="+ nextsite.toString() + "\">next</a>\n"));
            }
            
            for(int i=0;i<al.size();i++){s+=al.get(i);}
            al.clear();
            return s;
           
        }
        
	static private void listAllKMappings(int site,User user) throws Exception {
		String s = new String();
		DatabaseBremen database = new DatabaseBremen();
		database.connect();

		Object[][] a = database.execute("SELECT k, property, object, count(k) FROM lgd_map_resource_k GROUP BY k,property,object ORDER BY k Limit 20 OFFSET "+((site-1)*20));

		for ( int i = 0; i <  20; i++ ) {
			addkMapping(i,a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(),user,site);
		}

		database.disconnect();
	}

	static private void listAllKVMappings(int site,User user) throws Exception {
		String s = new String();
		DatabaseBremen database = new DatabaseBremen();

		database.connect();
		Object[][] a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv GROUP BY k,v,property,object ORDER BY k,v Limit 20 OFFSET "+((site-1)*20));

		for ( int i = 0; i < 20; i++ ) {
			addkvMapping(i,a[i][0].toString(),a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString(),user,site);
		}

		database.disconnect();
        }
    
     /**
* Template for K-Mappings.
* @param k K
* @param property property
* @param object object
* @param affectedEntities affected Entities
* @return String
*/
static private void addkMapping(int id,String k, String property, String object, String affectedEntities,User user,int site) {
    String s = new String();
    s += "\t\t\t\t\t<tr id=\"k" + id + "a\">\n";
    s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">Edit</a></td>\n";
    s += "\t\t\t\t\t\t<td><a>Delete</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=all&type=k&site=" + site +((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
    s += "\t\t\t\t\t\t<tr id=\"k" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
    s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"property\" value=\"" + property + "\" style=\"width: 27em;\" required /></td>\n";
    s += "\t\t\t\t\t\t\t<td><input type=\"text\" name=\"object\" value=\"" + object + "\" style=\"width: 27em;\" required /></td>\n";
    s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">Hide</a></td>\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aproperty\" value=\"" + property + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"aobject\" value=\"" + object + "\" />\n";
    s += "\t\t\t\t\t\t\t<td><a>Delete</a></td>\n";
    s += "\t\t\t\t\t\t</tr>\n";
    
    if ( !user.isLoggedIn() ) {
        s += "\t\t\t\t\t\t<tr id=\"k" + id + "u\" class=\"mapping\" style=\"display: none;\">\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
	s += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + user.getUsername() + "\" required />\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
	s += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"2\">\n";
	s += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kmapping\" value=\"Save\" />";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t</tr>\n";
    }
    else {
	s += "\t\t\t\t\t\t<tr id=\"k" + id + "u\" class=\"mapping\" style=\"display: none;\">\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"4\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
	s += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kmapping\" value=\"Save\" />\n";
        s +=  "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\""+ user.getUsername() + "\"/>\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t</tr>\n";
    } 
    s += "\t\t\t\t\t</form>\n";
    al.add(s);

}

/**
* Template for KV-Mappings.
* @param k K
* @param v V
* @param property property
* @param object object
* @param affectedEntities affected Entities
* @return String
*/
static private void addkvMapping(int id,String k, String v, String property, String object, String affectedEntities,User user,int site) {
    String s = new String();
    s += "\t\t\t\t\t<tr id=\"kv" + id + "a\">\n";
    s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + v + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + id + "')\">Edit</a></td>\n";
    s += "\t\t\t\t\t\t<td><a>Delete</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=all&type=kv&site=" + site + ((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
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
    s += "\t\t\t\t\t\t\t<td><a>Delete</a></td>\n";
    s += "\t\t\t\t\t\t</tr>\n";

    if ( !user.isLoggedIn() ) {
	s += "\t\t\t\t\t\t<tr id=\"kv" + id + "u\" class=\"mapping\" style=\"display: none;\">\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"3\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
	s += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + user.getUsername() + "\" required />\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
	s += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kvmapping\" value=\"Save\" />";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t</tr>\n";
    }
    else {
        s += "\t\t\t\t\t\t<tr id=\"kv" + id + "u\" class=\"mapping\" style=\"display: none;\">\n";
        s += "\t\t\t\t\t\t\t<td colspan=\"5\" align=\"center\">\n";
        s += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
        s += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
        s += "\t\t\t\t\t\t\t</td>\n";
        s += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
        s += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kvmapping\" value=\"Save\" />\n";
        s +=  "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\""+ user.getUsername() + "\"/>\n";
        s += "\t\t\t\t\t\t\t</td>\n";
        s += "\t\t\t\t\t\t</tr>\n";
    }
    s += "\t\t\t\t\t</form>\n";

    
    al.add(s);
    }
 
    	public static String captcha(HttpServletRequest request,String type,String site) {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha("6LcryM4SAAAAAAxmbh2VvI-GZXGpCRqcaSO2xL1B", "6LcryM4SAAAAAKHGFwoD1t-tQsWB_QGuNInVNYbp", false);
		String re;
		re = "\t\t\t\t<article class=\"captcha\">\n";
		re += "\t\t\t\t\t<form action=\"?tab=all&type="+ type +"&site=" + site + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">";
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