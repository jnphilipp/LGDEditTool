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
public class TemplatesAllMappings {
    
    static ArrayList<String> al = new ArrayList<String>();
    /**
	 * Template for EditHistory.
	 */
	static public String listAllMappings(String type,String site) {
            String s = new String();
            //s+="\t\t<button type=\"button\" name=\"kmapping\" target=\"?tab=all&type=k\">K-mappings</button>";
            //s+="\t\t<button type=\"button\" name=\"kvmapping\" target=\"?tab=all&type=kv\">KV-mappings</button>\n";
            s+="\t\t<a href=\"?tab=all&type=k\">K-mappings</a>";
            s+="\t\t<a href=\"?tab=all&type=kv\">KV-mappings</a>";
            
            
            
            if(type.equalsIgnoreCase("k")){
               try{
                //insert tablehead
		String tableHead = "\t\t\t\t<h2>Edit-History</h2>\n";
		tableHead += "\t\t\t\t<table>\n";
		tableHead += "\t\t\t\t\t<tr>\n";
		tableHead += "\t\t\t\t\t\t<th>k</th>\n";
		tableHead += "\t\t\t\t\t\t<th>property</th>\n";
		tableHead += "\t\t\t\t\t\t<th>object</th>\n";
		tableHead += "\t\t\t\t\t\t<th>affected Entities</th>\n";
                tableHead += "\t\t\t\t\t\t<th>edit/delete</th>\n";
		tableHead += "\t\t\t\t\t</tr>\n";
		al.add(tableHead);
                
                //insert edithistory from db
                listAllKMappings(Integer.parseInt(site));
                
                //inser table foot
                String tableFoot = "\t\t\t\t</table>\n";
                al.add(tableFoot);
               }catch(Exception e){};
            }
            else if(type.equalsIgnoreCase("kv")){
               try{
                //insert tablehead
		String tableHead = "\t\t\t\t<h2>Edit-History</h2>\n";
		tableHead += "\t\t\t\t<table>\n";
		tableHead += "\t\t\t\t\t<tr>\n";
		tableHead += "\t\t\t\t\t\t<th>k</th>\n";
                tableHead += "\t\t\t\t\t\t<th>v</th>\n";
		tableHead += "\t\t\t\t\t\t<th>property</th>\n";
		tableHead += "\t\t\t\t\t\t<th>object</th>\n";
		tableHead += "\t\t\t\t\t\t<th>affected Entities</th>\n";
                tableHead += "\t\t\t\t\t\t<th>edit/delete</th>\n";
		tableHead += "\t\t\t\t\t</tr>\n";
		al.add(tableHead);
                
                //insert edithistory from db
                listAllKVMappings(Integer.parseInt(site));
                
                //inser table foot
                String tableFoot = "\t\t\t\t</table>\n";
                al.add(tableFoot);
                
                //show more
                
                if(type.equalsIgnoreCase("k")){
                    Integer nextsite=Integer.valueOf(site)+1;
                    al.add(new String("\n\t\t\t\t\t<a href=\"?tab=all&type=k&site="+ nextsite.toString() + "\">show more</a>\n"));
                }
                else if(type.equalsIgnoreCase("kv")){
                    Integer nextsite=Integer.valueOf(site)+1;
                    al.add(new String("\n\t\t\t\t\t<a href=\"?tab=all&type=kv&site="+ nextsite.toString() + "\">show more</a>\n"));
                }
               }catch(Exception e){};
            }
            for(int i=0;i<al.size();i++){s+=al.get(i);}
            return s;
           
        }
        
        static public void listAllKMappings(int site) throws Exception{
            String s = new String();
            DatabaseBremen database = new DatabaseBremen();
            
            database.connect();
		Object[][] a = database.execute("SELECT k, count(k) FROM lgd_map_resource_k GROUP BY k");

		for (int i=(site-1)*20;i<site*20;i++) {
                        addkMapping(a[i][0].toString(), "", "", a[0][1].toString());
		}
		database.close();
        }
        
        
        static public void listAllKVMappings(int site) throws Exception{
            String s = new String();
            DatabaseBremen database = new DatabaseBremen();
            
            database.connect();
		Object[][] a = database.execute("SELECT k, v, count(k) FROM lgd_map_resource_kv GROUP BY k,v");	

		for (int i=(site-1)*20;i<site*20;i++) {
                        addkvMapping(a[i][0].toString(),a[i][1].toString(), "", "", a[0][2].toString());
		}
		database.close();
        }
    
    	/**
	 * Template for K-Mappings.
	 * @param k K
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected Entities
	 * @return String
	 */
	static public void addkMapping(String k, String property, String object, String affectedEntities) {
		String s = new String();
		s += "\t\t\t\t\t<tr>\n";
		s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t<td>" + property + "</td>\n";
		s += "\t\t\t\t\t\t<td>" + object + "</td>\n";
		s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t<td class=\"edit\"><a href=\"?tab=mappings&k=" + k + "\">Edit/Delete</a></td>\n";
		s += "\t\t\t\t\t</tr>\n";
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
	static public void addkvMapping(String k, String v, String property, String object, String affectedEntities) {
		String s = new String();
		s += "\t\t\t\t\t<tr>\n";
		s += "\t\t\t\t\t\t<td>" + k + "</td>\n";
		s += "\t\t\t\t\t\t<td>" + v + "</td>\n";
		s += "\t\t\t\t\t\t<td>" + property + "</td>\n";
		s += "\t\t\t\t\t\t<td>" + object + "</td>\n";
		s += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		s += "\t\t\t\t\t\t<td class=\"edit\"><a href=\"?tab=mappings&k=" + k + "&v=" + v + "\">Edit/Delete</a></td>\n";
		s += "\t\t\t\t\t</tr>\n";
                al.add(s);
		
	}

    
}
