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
public class TemplatesEditHistory {
    
    
        static ArrayList<String> al = new ArrayList<String>();
        
        
    	/**
	 * Template for EditHistory.
	 */
	static public String editHistory(String ksite,String kvsite,User user) {
		al.clear();
                
                //kmappings
                //insert tablehead
		String tableHead = "\t\t\t\t<h2>K-Mappings history</h2>\n";
                tableHead += "\t\t\t\t<table class=\"table\">\n";
                tableHead += "\t\t\t\t\t<tr class=mapping>\n";
                tableHead += "\t\t\t\t\t\t<th>timestamp</th>\n";
                tableHead += "\t\t\t\t\t\t<th>k</th>\n";
                tableHead += "\t\t\t\t\t\t<th>property</th>\n";
                tableHead += "\t\t\t\t\t\t<th>object</th>\n";
                tableHead += "\t\t\t\t\t\t<th>affected Entities</th>\n";
                tableHead += "\t\t\t\t\t\t<th>action</th>\n";
                tableHead += "\t\t\t\t\t\t<th>user</th>\n";
                tableHead += "\t\t\t\t\t\t<th>comment</th>\n";
                tableHead += "\t\t\t\t\t\t<th>restore</th>\n";
                tableHead += "\t\t\t\t\t</tr>\n";
                al.add(tableHead);
		           
                //insert edithistory from db for k-mappings
                try{
                    searchKHistoryDB(Integer.parseInt(ksite),Integer.parseInt(kvsite),user);
                }catch(Exception e){}
                
                //inser table foot
                String tableFoot = "\t\t\t\t</table>\n";
                al.add(tableFoot);
                

								al.add(new String("\t\t\t\t<div style=\"float: right;\">\n"));
								if ( Integer.valueOf(ksite)>1 ) {
                    Integer prevsite=Integer.valueOf(ksite)-1;
                    al.add(new String("\t\t\t\t\t<a href=\"?tab=history&ksite="+ prevsite.toString() + "&kvsite="+kvsite+"\">&#60;prev</a>&nbsp;&nbsp;&nbsp;"));
								}
                Integer nextsite=Integer.valueOf(ksite)+1;
                al.add(new String("<a href=\"?tab=history&ksite="+ nextsite.toString() + "&kvsite="+kvsite+"\">next&#62;</a>\n"));
								al.add(new String("\t\t\t\t</div>\n"));
            
            
                
                //kmappings
                //insert tablehead
		tableHead = "\t\t\t\t<h2>KV-Mappings history</h2>\n";
                tableHead += "\t\t\t\t<table class=\"table\">\n";
                tableHead += "\t\t\t\t\t<tr class=mapping>\n";
                tableHead += "\t\t\t\t\t\t<th>timestamp</th>\n";
                tableHead += "\t\t\t\t\t\t<th>k</th>\n";
                tableHead += "\t\t\t\t\t\t<th>v</th>\n";
                tableHead += "\t\t\t\t\t\t<th>property</th>\n";
                tableHead += "\t\t\t\t\t\t<th>object</th>\n";
                tableHead += "\t\t\t\t\t\t<th>affected Entities</th>\n";
                tableHead += "\t\t\t\t\t\t<th>action</th>\n";
                tableHead += "\t\t\t\t\t\t<th>user</th>\n";
                tableHead += "\t\t\t\t\t\t<th>comment</th>\n";
                tableHead += "\t\t\t\t\t\t<th>restore</th>\n";
                tableHead += "\t\t\t\t\t</tr>\n";
                al.add(tableHead);
		           
                //insert edithistory from db for kv-mappings
                try{
                   searchKVHistoryDB(Integer.parseInt(ksite),Integer.parseInt(kvsite),user);
                }catch(Exception e){}
                
                //inser table foot
                tableFoot = "\t\t\t\t</table>\n";
                al.add(tableFoot);                

								al.add(new String("\t\t\t\t<div style=\"float: right;\">\n"));
                if(Integer.valueOf(kvsite)>1){
                    Integer prevsite=Integer.valueOf(kvsite)-1;
                    al.add(new String("\t\t\t\t\t<a href=\"?tab=history&ksite="+ ksite + "&kvsite="+prevsite.toString()+"\">&#60;prev</a>&nbsp;&nbsp;&nbsp;"));
                }
                Integer nextsite2=Integer.valueOf(kvsite)+1;
                al.add(new String("\t\t\t\t\t<a href=\"?tab=history&ksite="+ ksite + "&kvsite="+nextsite2.toString()+"\">next&#62;</a>\n"));
								al.add(new String("\t\t\t\t</div>\n"));
            
                
                
                //array to string for return
                String s=new String();
                for(int i=0;i<al.size();i++){s+=al.get(i);}  
                al.clear();
		return s;
	}
    
        
    
        
        /**
	 * gets edithistory from DB
	 */
        static private void searchKHistoryDB(int ksite,int kvsite,User user) throws Exception{
                String s = new String();
		DatabaseBremen database = new DatabaseBremen();
		database.connect();
                //CREATE TABLE lgd_map_resource_k_history(id INTEGER PRIMARY KEY, k TEXT NOT NULL, object TEXT NOT NULL, property TEXT NOT NULL, user_id TEXT REFERENCES lgd_user(email), comment TEXT, timestamp TEXT NOT NULL);
		Object[][] a = database.execute("SELECT k, property, object, count(k),user_id,comment,timestamp,history_id, id FROM lgd_map_resource_k_history GROUP BY k,property,object,user_id,comment,timestamp,history_id,id ORDER BY timestamp DESC Limit 10 OFFSET "+((ksite-1)*10));
                
		for ( int i = 0; i <  10; i++ ) {
                    String action=new String("");
                    int flag=0;
                    Object[][] b = database.execute("SELECT * FROM lgd_map_resource_k WHERE last_history_id="+(a[i][8].toString()));
                    Object[][] c = database.execute("SELECT * FROM lgd_map_resource_k_history WHERE history_id= "+(a[i][8].toString()));
                    //if(c!=null){flag=1;}
                 
                    if(b.length == 0 && c.length == 0){action="delete";}else{action="edit";}
			addkMapping(i,a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[i][3].toString(),action,a[i][4].toString(),a[i][5].toString(),a[i][6].toString(),user,ksite,kvsite);
		}
                
		database.disconnect();
           
        }
        /**
	 * gets edithistory from DB
	 */
        static private void searchKVHistoryDB(int ksite,int kvsite,User user) throws Exception{
            
            
             String s = new String();
		DatabaseBremen database = new DatabaseBremen();
		database.connect();
                //CREATE TABLE lgd_map_resource_k_history(id INTEGER PRIMARY KEY, k TEXT NOT NULL, object TEXT NOT NULL, property TEXT NOT NULL, user_id TEXT REFERENCES lgd_user(email), comment TEXT, timestamp TEXT NOT NULL);
		Object[][] a = database.execute("SELECT k,v, property, object, count(k),user_id,comment,timestamp,history_id, id FROM lgd_map_resource_kv_history GROUP BY k,v,property,object,user_id,comment,timestamp,history_id, id ORDER BY timestamp DESC Limit 10 OFFSET "+((kvsite-1)*10));

		for ( int i = 0; i <  10; i++ ) {
                    String action=new String("");
                    int flag=0;
                    Object[][] b = database.execute("SELECT * FROM lgd_map_resource_kv WHERE last_history_id="+(a[i][9].toString()));
                    Object[][] c = database.execute("SELECT * FROM lgd_map_resource_kv_history WHERE history_id= "+(a[i][9].toString()));
                    if(c!=null){flag=1;}
                    if(b.length==0 && flag==1|| flag==0){action="delete";}else{action="edit";}
                    addkvMapping(i,a[i][0].toString(),a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[i][4].toString(),action,a[i][5].toString(),a[i][6].toString(),a[i][7].toString(),user,ksite,kvsite);
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
static private void addkMapping(int id,String k, String property, String object, String affectedEntities,String action,String user_id,String comment,String timestamp,User user,int ksite,int kvsite) {
    String s = new String();
    s += "\t\t\t\t\t<tr id=\"k" + id + "a\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">restore</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=history&ksite="+ ksite +"&kvsite=" + kvsite + ((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
    //s += "\t\t\t\t\t<form action=\"?tab=all&type=k&site=" + site +((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
    s += "\t\t\t\t\t\t<tr id=\"k" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + id + "')\">Hide</a></td>\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + timestamp + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + user_id + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + comment + "\" />\n";
    s += "\t\t\t\t\t\t</tr>\n";
    

    
    if ( !user.isLoggedIn() ) {
        s += "\t\t\t\t\t\t<tr id=\"k" + id + "u\" class=\"mapping\" style=\"display: none;\">\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"3\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
	s += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + user.getUsername() + "\" required />\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"4\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
	s += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"2\">\n";
	s += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kmappingedit\" value=\"Restore\" />\n";
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
	s += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kmappingedit\" value=\"Restore\" />\n";
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
static private void addkvMapping(int id,String k, String v, String property, String object, String affectedEntities,String action,String user_id,String comment,String timestamp,User user,int ksite,int kvsite) {
    String s = new String();
    s += "\t\t\t\t\t<tr id=\"kv" + id + "a\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + v + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(property) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + Functions.shortenURL(object) + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    
        s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + id + "')\">restore</a></td>\n";
    s += "\t\t\t\t\t</tr>\n";
    s += "\t\t\t\t\t<form action=\"?tab=history&ksite="+ ksite +"&kvsite=" + kvsite + ((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
    //s += "\t\t\t\t\t<form action=\"?tab=all&type=kv&site=" + site + ((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
    s += "\t\t\t\t\t\t<tr id=\"kv" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
    s += "\t\t\t\t\t\t<td style=\"text-align: right;\">" + Functions.showTimestamp(timestamp) + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + k + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + v + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + property + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + object + "</td>\n";
    s += "\t\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + user_id + "</td>\n";
    s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
    s += "\t\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + id + "')\">Hide</a></td>\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + k + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + v + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + property + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + object + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + timestamp + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + user_id + "\" />\n";
    s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + comment + "\" />\n";
    s += "\t\t\t\t\t\t</tr>\n";

    if ( !user.isLoggedIn() ) {
	s += "\t\t\t\t\t\t<tr id=\"kv" + id + "u\" class=\"mapping\" style=\"display: none;\">\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"4\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
	s += "\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + user.getUsername() + "\" required />\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"4\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
	s += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
	s += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kvmappingedit\" value=\"Restore\" />";
	s += "\t\t\t\t\t\t\t</td>\n";
	s += "\t\t\t\t\t\t</tr>\n";
    }
    else {
        s += "\t\t\t\t\t\t<tr id=\"kv" + id + "u\" class=\"mapping\" style=\"display: none;\">\n";
        s += "\t\t\t\t\t\t\t<td colspan=\"6\" align=\"center\">\n";
        s += "\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
        s += "\t\t\t\t\t\t\t\t<textarea name=\"comment\" style=\"width: 30em; height: 5em;\" required>No comment.</textarea>\n";
        s += "\t\t\t\t\t\t\t</td>\n";
        s += "\t\t\t\t\t\t\t<td colspan=\"2\" align=\"center\">\n";
        s += "\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"kvmappingedit\" value=\"Restore\" />\n";
        s +=  "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\""+ user.getUsername() + "\"/>\n";
        s += "\t\t\t\t\t\t\t</td>\n";
        s += "\t\t\t\t\t\t</tr>\n";
    }
    s += "\t\t\t\t\t</form>\n";

    
    al.add(s);
    }
        
	public static String captcha(HttpServletRequest request,String ksite,String kvsite) {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha("6LcryM4SAAAAAAxmbh2VvI-GZXGpCRqcaSO2xL1B", "6LcryM4SAAAAAKHGFwoD1t-tQsWB_QGuNInVNYbp", false);
		String re;
		re = "\t\t\t\t<article class=\"captcha\">\n";
		re += "\t\t\t\t\t<form action=\"?tab=history&ksite=" + ksite + "&kvsite=" + kvsite +"\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li>"+ c.createRecaptchaHtml(null, null) + "</li>\n";
		re += "\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"" + (request.getParameter("kmappingedit") != null ? "kmapping" : "kvmapping") + "captcha\" value=\"Send\" /></li>\n";
		re += "\t\t\t\t\t\t</ul>\n";

		if ( request.getParameter("kmappingedit") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kmappingedit\" value=\"" + request.getParameter("kmappingedit") + "\" />\n";
                        re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + request.getParameter("auser") + "\" />\n";
                        re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + request.getParameter("acomment") + "\" />\n";
                        re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + request.getParameter("timestamp") + "\" />\n";
		}
		else if ( request.getParameter("kvmappingedit") != null ) {
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + request.getParameter("k") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + request.getParameter("v") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"object\" value=\"" + request.getParameter("object") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"property\" value=\"" + request.getParameter("property") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + request.getParameter("user") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"comment\" value=\"" + request.getParameter("comment") + "\" />\n";
			re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"kvmappingedit\" value=\"" + request.getParameter("kvmappingedit") + "\" />\n";
                        re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"auser\" value=\"" + request.getParameter("auser") + "\" />\n";
                        re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"acomment\" value=\"" + request.getParameter("acomment") + "\" />\n";
                        re += "\t\t\t\t\t\t<input type=\"hidden\" name=\"timestamp\" value=\"" + request.getParameter("timestamp") + "\" />\n";
		}

		re += "\t\t\t\t\t</form>\n";
		re += "\t\t\t\t</article>\n";
		return re;
	}
}
