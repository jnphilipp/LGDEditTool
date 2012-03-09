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
    
    
    static ArrayList<String> al = new ArrayList<String>();
    
    /**
     * template Unmapped Tags
     * @param ksite site k-mappings
     * @param kvsite site kv-mappings
     * @return sitecontent
     */
    static public String unmappedTags(String ksite,String kvsite){
        String s = new String();
        
        //kmapping table
        String tableHeadk = "\t\t\t\t<h2>List of all K-Mappings</h2>\n";
		tableHeadk += "\t\t\t\t<table>\n";
		tableHeadk += "\t\t\t\t\t<tr>\n";
		tableHeadk += "\t\t\t\t\t\t<th>k</th>\n";
		tableHeadk += "\t\t\t\t\t\t<th>property</th>\n";
		tableHeadk += "\t\t\t\t\t\t<th>object</th>\n";
		tableHeadk += "\t\t\t\t\t\t<th>affected Entities</th>\n";
                tableHeadk += "\t\t\t\t\t\t<th>edit/delete</th>\n";
		tableHeadk += "\t\t\t\t\t</tr>\n";
		al.add(tableHeadk);
       try{
           listAllKMappings(Integer.parseInt(ksite));
       }catch(Exception e){}
       al.add(new String("\t\t\t\t</table>\n<br />"));
                
       
       //kvmapping table
       String tableHead = "\t\t\t\t<h2>List of all KV-Mappings</h2>\n";
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
        
        try{
           listAllKVMappings(Integer.parseInt(kvsite));
       }catch(Exception e){}
       al.add(new String("\t\t\t\t</table>\n<br />")); 
        
       for(int i=0;i<al.size();i++){s+=al.get(i);}
       al.clear(); 
        
       return s;
    }
    
    
    
    
    
    
    
    
    
    
    static private void listAllKMappings(int site) throws Exception{
            String s = new String();
            DatabaseBremen database = new DatabaseBremen();
            
            database.connect();
		Object[][] a = database.execute("SELECT k, property, object, count(k) FROM lgd_map_resource_k GROUP BY k,property,object");
                int count=0;
		for (int i=0;i<a.length;i++) {
                        if(a[i][1].toString().equals("") || a[i][2].toString().equals("") ){
                            addkMapping(a[i][0].toString(), a[i][1].toString(), a[i][2].toString(), a[0][3].toString());
                            count++;
                            if(count==20){break;}
                        }
		}
		database.close();
        }
        
        
        static private void listAllKVMappings(int site) throws Exception{
            String s = new String();
            DatabaseBremen database = new DatabaseBremen();
            
            database.connect();
		Object[][] a = database.execute("SELECT k, v, property, object, count(k) FROM lgd_map_resource_kv GROUP BY k,v,property,object");	
                int count=0;
		for (int i=0;i<a.length;i++) {
                    if(a[i][1].toString().equals("") || a[i][2].toString().equals("") || a[i][3].toString().equals("") ){
                        addkvMapping(a[i][0].toString(),a[i][1].toString(), a[i][2].toString(), a[i][3].toString(), a[0][4].toString());
                        count++;
                        if(count==20){break;}
                    } 
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
	static private void addkMapping(String k, String property, String object, String affectedEntities) {
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
	static private void addkvMapping(String k, String v, String property, String object, String affectedEntities) {
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
