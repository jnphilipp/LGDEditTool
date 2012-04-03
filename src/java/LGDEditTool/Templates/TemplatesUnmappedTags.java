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
import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;

/**
 *
 * @author Alexander Richter
 */
public class TemplatesUnmappedTags {
	/**
	 * Template Unmapped Tags. This Template is used by the 'UnmappedTags'-tab.
	 * @param ksite current K-Mapping site
	 * @param kvsite current KV-Mapping site
         * @throws Exception
	 * @return Returns a String with HTML-code.
	 */
	static public String unmappedTags(String ksite,String kvsite) throws Exception{
		DatabaseBremen.getInstance().connect();

		//kmapping table
		String s = "\t\t\t\t<h2>List of all Unmapped Tags</h2>\n";
		s += "\t\t\t\t<table class=\"table\">\n";
		s += "\t\t\t\t\t<tr class=\"mapping\">\n";
		s += "\t\t\t\t\t\t<th>k</th>\n";
		s += "\t\t\t\t\t\t<th>usage_count</th>\n";
		s += "\t\t\t\t\t\t<th>distinct_value_count</th>\n";
                s += "\t\t\t\t\t\t<th>create mapping</th>\n";
                s += "\t\t\t\t\t\t<th>create datatype</th>\n";
		s += "\t\t\t\t\t</tr>\n";

		//fill table with k-mappings
		s += listAllk(Integer.valueOf(ksite),Integer.valueOf(kvsite));
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
		s += "\t\t\t\t\t<tr class=\"mapping\">\n";
		s += "\t\t\t\t\t\t<th>k</th>\n";
		s += "\t\t\t\t\t\t<th>v</th>\n";
		s += "\t\t\t\t\t\t<th>usage_count</th>\n";
                s += "\t\t\t\t\t\t<th>create</th>\n";
		s += "\t\t\t\t\t</tr>\n";
                
		//fill table with kv-mappings
		s += listAllkv(Integer.valueOf(ksite),Integer.valueOf(kvsite));
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
	static private String listAllk(int ksite,int kvsite) throws Exception {
		String s = "";
		DatabaseBremen database = DatabaseBremen.getInstance();

		Object[][] a = database.execute("SELECT k, usage_count , distinct_value_count FROM lgd_stat_tags_k a WHERE NOT EXISTS (Select b.k FROM ( Select k FROM  lgd_map_datatype UNION ALL SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_literal UNION ALL SELECT k FROM lgd_map_property UNION ALL SELECT k FROM lgd_map_resource_k UNION ALL SELECT k FROM lgd_map_resource_kv UNION ALL SELECT k FROM lgd_map_resource_prefix ) b WHERE a.k=b.k) LIMIT 20 OFFSET " + (ksite-1)*20);

		int count=0;
		for (int i=0;i<a.length;i++) {
			s += "\t\t\t\t\t<tr id=\"k"+i+"a\">\n";
			s += "\t\t\t\t\t<td>"+a[i][0]+"</td>\n";
			s += "\t\t\t\t\t<td>"+a[i][1]+"</td>\n";
			s += "\t\t\t\t\t<td>"+a[i][2]+"</td>\n";
			s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('k" + i + "')\">mapping</a></td>\n";
			s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('dk" + i + "')\">datatype</a></td>\n";
                        s += "\t\t\t\t\t</tr>\n";
                        
                        //create mapping
                        s += "\t\t\t\t\t\t<form action=\"?tab=unmapped&ksite="+ksite+"&kvsite=" + kvsite + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
                        s += "\t\t\t\t\t\t\t<tr id=\"k" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
                        s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + a[i][0] + "\" />\n";
                        s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + User.getInstance().getUsername() + "\" />\n";
                        s += "\t\t\t\t\t\t\t</tr>\n";
                        s += getUserField("k" + i + "u", "kmapping", "Create", "k",a[i][0].toString(),"");
                        s += "\t\t\t\t\t\t</form>\n";
                        
                        //create datatype
                        s += "\t\t\t\t\t\t<form action=\"?tab=unmapped&ksite="+ksite+"&kvsite=" + kvsite + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
                        s += "\t\t\t\t\t\t\t<tr id=\"dk" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
                        s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + a[i][0] + "\" />\n";
                        s += "\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + User.getInstance().getUsername() + "\" />\n";
                        s += "\t\t\t\t\t\t\t</tr>\n";
                        s += getUserField("dk" + i + "u", "dmapping", "Create", "d",a[i][0].toString(),"");
                        s += "\t\t\t\t\t\t</form>\n";
                        
                        
		}

		return s;
	}

	/**
	 * Template for KV-Mappings. Fills table with KV-mappings
         * @param ksite current K-Mapping site
	 * @param kvsite current KV-Mapping site
	 * @throws Exception 
         * @return Returns a String with HTML-code.
	 */
	static private String listAllkv(int ksite,int kvsite) throws Exception {
		String s = new String();
		DatabaseBremen database = DatabaseBremen.getInstance();

		Object[][] a = database.execute("SELECT k,v, usage_count  FROM lgd_stat_tags_kv a WHERE NOT EXISTS (Select b.k FROM ( SELECT k,v FROM lgd_map_label UNION ALL  SELECT k,v FROM lgd_map_resource_kv ) b WHERE a.k=b.k) LIMIT 20 OFFSET "+(kvsite-1)*20);

		int count=0;
		for (int i=0;i<a.length;i++) {
			s += "\t\t\t\t\t<tr id=\"kv"+i+"a\">\n";
			s += "\t\t\t\t\t<td>"+a[i][0]+"</td>\n";
			s += "\t\t\t\t\t<td>"+a[i][1]+"</td>\n";
			s += "\t\t\t\t\t<td>"+a[i][2]+"</td>\n";   
                        s += "\t\t\t\t\t\t<td><a onclick=\"toggle_visibility('kv" + i + "')\">create</a></td>\n";
			s += "\t\t\t\t\t</tr>\n";
                        
                        //create
                        s += "\t\t\t\t\t\t<form action=\"?tab=unmapped&ksite="+ksite+"&kvsite=" + kvsite + (!User.getInstance().isLoggedIn() ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
                        s += "\t\t\t\t\t\t\t<tr id=\"kv" + i + "\" class=\"mapping\" style=\"display: none;\">\n";
                        s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"k\" value=\"" + a[i][0] + "\" />\n";
                        s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"v\" value=\"" + a[i][1] + "\" />\n";
                        s += "\t\t\t\t\t\t\t\t<input type=\"hidden\" name=\"user\" value=\"" + User.getInstance().getUsername() + "\" />\n";
                        s += "\t\t\t\t\t\t\t</tr>\n";
                        s += getUserField("kv" + i + "u", "kvmapping", "Create", "kv",a[i][0].toString(),a[i][1].toString());
                        s += "\t\t\t\t\t\t</form>\n";
		}

		return s;
	}
        
        /**
         * Template for user fields.
         * @param id id for toggle visiblity
         * @param submitName submit name
         * @param submitValue submit value
         * @param type Mapping-Type (K,KV,Datatype)
         * @param k Mapping Key
         * @param v Mapping Value
         * @return Returns a String with HTML-code.
         */
	private static String getUserField(String id, String submitName, String submitValue, String type,String k,String v) {
		String re = "";
                //create datatype
                if(type.equalsIgnoreCase("d")){
                    if ( !User.getInstance().isLoggedIn() ) {
			re += "\t\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>k: "+k+"</label>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>Datatype:</label>\n";
                        re += "\t\t\t\t\t\t\t\t<select name=\"datatype\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<option value=\"boolean\">boolean</option>\n";
                        re += "\t\t\t\t\t\t\t\t\t<option value=\"int\">int</option>\n";
                        re += "\t\t\t\t\t\t\t\t\t<option value=\"float\">float</option>\n";
                        re += "\t\t\t\t\t\t\t\t</select>\n";
                        re += "\t\t\t\t\t\t\t\t</td>\n";
                        re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + User.getInstance().getUsername() + "\" required />\n";	
			re += "\t\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
                        re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\">\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />\n";
                        re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<a onclick=\"toggle_visibility('" + id.substring(0, id.length() - 1) + "')\">Hide</a>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t</tr>\n";
                    }
                    else {
			re += "\t\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>k: "+k+"</label>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
                        re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>Datatype:</label>\n";
                        re += "\t\t\t\t\t\t\t\t<select name=\"datatype\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<option value=\"boolean\">boolean</option>\n";
                        re += "\t\t\t\t\t\t\t\t\t<option value=\"int\">int</option>\n";
                        re += "\t\t\t\t\t\t\t\t\t<option value=\"float\">float</option>\n";
                        re += "\t\t\t\t\t\t\t\t</select>\n";
                        re += "\t\t\t\t\t\t\t\t</td>\n";
                        re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
                        re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />\n";
                        re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<a onclick=\"toggle_visibility('" + id.substring(0, id.length() - 1) + "')\">Hide</a>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t</tr>\n";
                   } 
                //create k/kv mapping
                }else{
                    if ( !User.getInstance().isLoggedIn() ) {
			re += "\t\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>k: "+k+"</label>\n";
                        if(type.equalsIgnoreCase("kv")){re += "\t\t\t\t\t\t\t\t\t<label>v: "+v+"</label>\n";}
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>Property:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"text\" name=\"property\" placeholder=\"property\" required>\n";
                        re += "\t\t\t\t\t\t\t\t\t<label>Object:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"text\" name=\"object\" placeholder=\"object\" required>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
                        re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<label>Login or Email:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"text\" name=\"user\" style=\"width: 20em;\" value=\"" + User.getInstance().getUsername() + "\" required />\n";	
			re += "\t\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
                        re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\">\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />\n";
                        if(type.equalsIgnoreCase("k")){
                            re += "\t\t\t\t\t\t\t\t</td>\n";
                            re += "\t\t\t\t\t\t\t\t<td colspan=\"1\">\n";
                        }
                        re += "\t\t\t\t\t\t\t\t\t<a onclick=\"toggle_visibility('" + id.substring(0, id.length() - 1) + "')\">Hide</a>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t</tr>\n";
                    }
                    else {
			re += "\t\t\t\t\t\t\t<tr id=\"" + id + "\" class=\"mapping\" style=\"display: none;\">\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>k: "+k+"</label>\n";
                        if(type.equalsIgnoreCase("kv")){re += "\t\t\t\t\t\t\t\t\t<label>v: "+v+"</label>\n";}
			re += "\t\t\t\t\t\t\t\t</td>\n";
                        re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
			re += "\t\t\t\t\t\t\t\t\t<label>Property:</label>\n";
                        re += "\t\t\t\t\t\t\t\t\t<input type=\"text\" name=\"property\" placeholder=\"property\" required>\n";
                        re += "\t\t\t\t\t\t\t\t\t<label>Object:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<input type=\"text\" name=\"object\" placeholder=\"object\" required>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
                        re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<label>Comment:</label>\n";
			re += "\t\t\t\t\t\t\t\t\t<textarea name=\"comment\" placeholder=\"No comment.\" style=\"width: 30em; height: 5em;\" required></textarea>\n";
                        re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t\t<td colspan=\"1\" align=\"center\">\n";
                        re += "\t\t\t\t\t\t\t\t\t<input type=\"submit\" name=\"" + submitName + "\" value=\"" + submitValue + "\" />\n";
                        if(type.equalsIgnoreCase("k")){
                            re += "\t\t\t\t\t\t\t\t</td>\n";
                            re += "\t\t\t\t\t\t\t\t<td colspan=\"1\">\n";
                        }
                        re += "\t\t\t\t\t\t\t\t\t<a onclick=\"toggle_visibility('" + id.substring(0, id.length() - 1) + "')\">Hide</a>\n";
			re += "\t\t\t\t\t\t\t\t</td>\n";
			re += "\t\t\t\t\t\t\t</tr>\n";
                   }
                }
		

		return re;
	}
        
        
        /**
         * reCatpcha form. Request ReCaptcha from a user who is not logged in.
         * @param request request
         * @param ksite current K-Mapping site
         * @param kvsite current KV-Mapping site
         * @return Returns a String with HTML-code.
         */
	public static String captcha(HttpServletRequest request,String ksite,String kvsite) {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha(Functions.PUBLIC_reCAPTCHA_KEY, Functions.PRIVATE_reCAPTCHA_KEY, false);

		String re = "\t\t\t\t<article class=\"captcha\">\n";
		re += "\t\t\t\t\t<form action=\"?tab=unmapped&ksite="+ ksite +"&kvsite=" + kvsite + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
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