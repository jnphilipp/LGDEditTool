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

import LGDEditTool.db.DatabaseBremen;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import LGDEditTool.Functions;
import LGDEditTool.SiteHandling.User;
import LGDEditTool.db.DatabaseBremen;
/**
 *
 * @author Alexander Richter
 */
public class TemplatesOntology {
    
    static public String ontologie(User user,String tag) throws Exception {
        String s=new String("");
        s +="\t\t\t\t<table>\n";
        s +="\t\t\t\t\t<tr>\n";
        s +="\t\t\t\t\t<td>\n";
        DatabaseBremen database = new DatabaseBremen();
        database.connect();
        s +=leftside(user,tag,database);
        s +="\t\t\t\t\t</td>";
        s +="\t\t\t\t\t<td>";
        
        s +=rightside(user,tag,database);
        database.disconnect();
        s +="\t\t\t\t\t</td>";
        s +="\t\t\t\t\t</tr>";
        s +="\t\t\t\t</table>\n";
        
        return s;
    }
    
   
    static private String leftside(User user,String tag,DatabaseBremen database) throws Exception{
        String s= new String("");
        
        String label=new String("");
        String local=new String("");     
        
        Object[][] a = database.execute("SELECT k,v, language, label FROM lgd_map_label Where k='"+tag+"'");
        label=a[0][3].toString();
        local=a[0][2].toString();
        
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
        s +="\t\t\t\t\t\t\t<td><input type=\"button\" name=\"save\" value=\"Save\" /> <input type=\"button\" name=\"clear\" value=\"Clear\" /> <input type=\"button\" name=\"delete\" value=\"Delete\" /> </td>\n";   
        s +="\t\t\t\t\t\t</tr>\n";
        s +="\t\t\t\t\t</table>\n";
        s +="\t\t\t\t\t</fieldset>\n";
        s +="</form>\n";
       
        return s;       
    }
    
    
    
    static private String rightside(User user,String tag,DatabaseBremen database)throws Exception{
        String s=new String("");
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
                s+="<div><a href=\"javascript:treeview('"+i+"')\">+</a>"+superclasses.get(i);
            
                s+= "<div class=\"hidden\" id=\""+i+"\">";
                s+="<a href=\"javascript:treeview('00"+i+"')\">\\+</a>"+tag;
                s+=addSubClasses(user,tag,Integer.parseInt(""+00+i),database);
                s+="</div>\n";
            
                s+="</div>\n";
            }
        }
        else{int i=0;
            s+= "<div>";
                s+="<a href=\"javascript:treeview('"+i+"')\">\\+</a>"+tag;
                s+=addSubClasses(user,tag,i,database);
                s+="</div>\n";
        }
        
        s +="\t\t\t\t\t</fieldset>\n";
        return s;
    }
    
    
    
    static private String addSubClasses(User user,String tag,int id,DatabaseBremen database)throws Exception
    {
        String s=new String();
        ArrayList<String> subclasses = new ArrayList<String>();
           
        Object[][] a = database.execute("SELECT v FROM lgd_map_resource_kv Where k='"+tag+"'");
      
        for(int i=0;i<a.length;i++)
        {
            subclasses.add(a[i][0].toString());
        } 
        
        for(int i=0;i<subclasses.size();i++)
        {
            s+= "<div class=\"hidden\" id=\""+id+"\">";
            s+="<a href=\"javascript:treeview('"+id+i+"')\">\\+</a>"+subclasses.get(i);
            s+=    addSubClasses(user,subclasses.get(i),Integer.parseInt(""+id+i),database);
            s+="</div>\n";
        }
        
      
        return s;
    }
}
