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
import java.util.ArrayList;
/**
 *
 * @author Alexander Richter
 */
public class TemplatesUnmappedTags {
    
    //k,usage_count , distinct_value_count
    static ArrayList<String> al = new ArrayList<String>();
    
    /**
     * template Unmapped Tags
     * @param ksite site k-mappings
     * @param kvsite site kv-mappings
     * @return sitecontent
     */
    static public String unmappedTags(String site){
        String s = new String();
        
        //kmapping table
        String tableHead = "\t\t\t\t<h2>List of all K-Mappings</h2>\n";
		tableHead += "\t\t\t\t<table>\n";
		tableHead += "\t\t\t\t\t<tr>\n";
		tableHead += "\t\t\t\t\t\t<th>k</th>\n";
		tableHead += "\t\t\t\t\t\t<th>usage_count</th>\n";
		tableHead += "\t\t\t\t\t\t<th>distinct_value_count</th>\n";		
                tableHead += "\t\t\t\t\t\t<th>edit/delete</th>\n";
		tableHead += "\t\t\t\t\t</tr>\n";
		al.add(tableHead);
       try{
           listAll(Integer.valueOf(site));
       }catch(Exception e){}
       al.add(new String("\t\t\t\t</table>\n<br />"));
                
       
       
        
       for(int i=0;i<al.size();i++){s+=al.get(i);}
       al.clear(); 
        
       return s;
    }

	static private void listAll(int site) throws Exception {
		String s = new String();
		DatabaseBremen database = new DatabaseBremen();
		database.connect();

		Object[][] a = database.execute("SELECT k, usage_count , distinct_value_count FROM lgd_stat_tags_k WHERE NOT EXISTS (Select b.k ( Select k FROM  lgd_map_datatyp ) b WHERE a.k=b.k");

		int count=0;
		for (int i=0;i<a.length;i++) {
			s += "\t\t\t\t\t<tr>\n";
                        s += "\t\t\t\t\t<td>"+a[i][0]+"</td>\n";
                        s += "\t\t\t\t\t<td>"+a[i][1]+"</td>\n";
                        s += "\t\t\t\t\t<td>"+a[i][2]+"</td>\n";                   
                        s += "\t\t\t\t\t</tr>\n";
                    
		}
                al.add(s);
		database.disconnect();
	}

	
 
}
