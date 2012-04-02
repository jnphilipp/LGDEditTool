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


import LGDEditTool.SiteHandling.User;
import LGDEditTool.db.DatabaseBremen;
import java.util.ArrayList;

/**
 *
 * @author Alexander Richter
 */
public class TemplatesOntology {

	/**
	 * Template for Ontology. This Template is used by the 'Ontology'-tab.
	 * @param user user-session
	 * @param tag Label
	 * @return Returns a String with HTML-code.
	 * @throws Exception 
	 */
	static public String ontologie(User user,String tag) throws Exception {
		DatabaseBremen.getInstance().connect();
		String s = "";

		if ( tag.contains("-") )
			tag = tag.split("-")[0];

		s +="\t\t\t\t<table>\n";
		s +="\t\t\t\t\t<tr>\n";
		s +="\t\t\t\t\t<td>\n";
		s +=leftside(user,tag);
		s +="\t\t\t\t\t</td>\n";
		s +="\t\t\t\t\t<td>\n";
		s +=rightside(user,tag);
		s +="\t\t\t\t\t</td>\n";
		s +="\t\t\t\t\t</tr>\n";
		s +="\t\t\t\t</table>\n";

		return s;
	}
    
    /**
     * left side content of Ontology-Tab.
     * @param user user-session
     * @param tag Label
     * @return Returns a String with HTML-code.
     * @throws Exception 
     */
    static private String leftside(User user, String tag) throws Exception{
	DatabaseBremen database = DatabaseBremen.getInstance();
        String s= "";        
        
        Object[][] a = database.execute("SELECT k,v, language, label FROM lgd_map_label Where k='"+tag+"'");
        String label=tag;//a[0][3].toString();
        String local="";//a[0][2].toString();
        
        s +="\t\t\t\t\t<form action=\"?tab=ontologie"+ ((user == null || !user.isLoggedIn()) ? "&captcha=yes" : "") + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
        s +="\t\t\t\t\t<fieldset>\n";
        s +="\t\t\t\t\t<table>\n";
        s +="\t\t\t\t\t\t<tr class=mapping>\n";
        s +="\t\t\t\t\t\t\t<td>label:</td>\n";
        s +="\t\t\t\t\t\t\t<td><input name=\"label\" value=\""+tag+"\"></td>\n";
        s +="\t\t\t\t\t\t</tr>\n";
        s +="\t\t\t\t\t\t<tr class=mapping>\n";
        s +="\t\t\t\t\t\t\t<td>localization:</td>\n";
        s +="\t\t\t\t\t\t\t<td><input name=\"localization\" value=\""+local+"\"></td>\n";
        s +="\t\t\t\t\t\t</tr>\n";
        s +="\t\t\t\t\t\t<tr class=mapping>\n";
        s +="\t\t\t\t\t\t\t<td>superclass:</td>\n";
        s +="\t\t\t\t\t\t\t<td><input name=\"superclass\" value=\""+tag+"\"></td>\n";
        s +="\t\t\t\t\t\t</tr>\n";
        s +="\t\t\t\t\t\t<tr class=mapping>\n";
        s +="\t\t\t\t\t\t\t<td><input type=\"submit\" name=\"save\" value=\"Save\" /> <input type=\"submit\" name=\"clear\" value=\"Clear\" /> <input type=\"submit\" name=\"delete\" value=\"Delete\" /> </td>\n";   
        s +="\t\t\t\t\t\t</tr>\n";
        s +="\t\t\t\t\t</table>\n";
        s +="\t\t\t\t\t</fieldset>\n";
        s +="</form>\n";
       
        return s;       
    }
    
    
    /**
     * right-side content of Ontology-Tab.
     * @param user user-session
     * @param tag Label
     * @return Returns a String with HTML-code.
     * @throws Exception 
     */
    static private String rightside(User user,String tag) throws Exception {
	DatabaseBremen database = DatabaseBremen.getInstance();
        String s="";
        ArrayList<String> superclasses = new ArrayList<String>();
                
        s +="\t\t\t\t\t<fieldset>\n";
        
        //get superclasses
        Object[][] a = database.execute("SELECT k FROM lgd_map_resource_kv Where v='"+tag+"'");
        for(int i=0;i<a.length;i++)
        {
            superclasses.add(a[i][0].toString());
        } 

        
        //one tree for each superclass
        if(superclasses.size()>0){
            for(int i=0;i<superclasses.size();i++)
            {
                s+="\t\t\t\t\t\t<div><a href=\"javascript:treeview('"+i+"')\">+</a>"+superclasses.get(i)+"\n";
            
                s+= "\t\t\t\t\t\t<div class=\"hidden\" id=\""+i+"\">\n";
                s+="\t\t\t\t\t\t<a href=\"javascript:treeview('00"+i+"')\">\\+</a>"+tag+"\n";
                s+="\t\t\t\t\t\t\t"+addSubClasses(user,tag,"00"+i);
                s+="\t\t\t\t\t\t</div>\n";
            
                s+="\t\t\t\t\t\t</div>\n";
            }
        }
        else{int i=0;
            s+= "\t\t\t\t\t\t<div>\n";
                s+="\t\t\t\t\t\t<a href=\"javascript:treeview('"+i+"')\">\\+</a>"+tag+"\n";
                s+="\t\t\t\t\t\t\t"+addSubClasses(user,tag,Integer.toString(i))+"\n";
                s+="\t\t\t\t\t\t</div>\n";
        }
        
        s +="\t\t\t\t\t</fieldset>\n";
        return s;
    }
    
    
    /**
     * Get Subclasses for Label.
     * @param user user-session
     * @param tag Label
     * @param id div-element ID
     * @return Returns a String with HTML-code.
     * @throws Exception 
     */
    static private String addSubClasses(User user,String tag,String id)throws Exception
    {
        String s= "";
        DatabaseBremen database = DatabaseBremen.getInstance();
        ArrayList<String> subclasses = new ArrayList<String>();
           
        Object[][] a = database.execute("SELECT v FROM lgd_map_resource_kv Where k='"+tag+"'");
      
        for(int i=0;i<a.length;i++)
        {
            subclasses.add(a[i][0].toString());
        }
        s+= "\t\t\t\t\t\t<div class=\"hidden\" id=\""+id+"\">\n";
        for(int i=0;i<subclasses.size();i++)
        {
            
            s+="\t\t\t\t\t\t<a href=\"javascript:treeview('"+id+i+"')\">\\+</a>"+subclasses.get(i)+"<br />\n";
            s+="\t\t\t\t\t\t\t"+addSubClasses(user,subclasses.get(i),""+id+i)+"\n";
            
        }
        s+="\t\t\t\t\t\t</div>\n";
      
        return s;
    }
}