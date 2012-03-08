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

/**
 *
 * @author Alexander Richter
 */
public class TemplatesEditHistory {
    
    
        static ArrayList<String> al = new ArrayList<String>();
        
        
    	/**
	 * Template for EditHistory.
	 */
	static public String editHistory() {
		al.clear();
                //insert tablehead
		String tableHead = "\t\t\t\t<h2>Edit-History</h2>\n";
		tableHead += "\t\t\t\t<table>\n";
		tableHead += "\t\t\t\t\t<tr>\n";
		tableHead += "\t\t\t\t\t\t<th>time</th>\n";
		tableHead += "\t\t\t\t\t\t<th>type</th>\n";
		tableHead += "\t\t\t\t\t\t<th>action</th>\n";
		tableHead += "\t\t\t\t\t\t<th>comment</th>\n";
                tableHead += "\t\t\t\t\t\t<th>user</th>\n";
                tableHead += "\t\t\t\t\t\t<th>restore</th>\n";
		tableHead += "\t\t\t\t\t\t<th>new comment</th>\n";
		tableHead += "\t\t\t\t\t</tr>\n";
		al.add(tableHead);
                
                //insert edithistory from db
                searchDB();
                
                //inser table foot
                String tableFoot = "\t\t\t\t</table>\n";
                al.add(tableFoot);
                
                //array to string for return
                String s=new String();
                for(int i=0;i<al.size();i++){s+=al.get(i);}  
                al.clear();
		return s;
	}
    
        
        /**
	 * add arraylist with data
	 * @param time time
	 * @param type type
	 * @param action action
	 * @param comment comment
         * @param user user
	 */
        static private void fillEditHistoryArray(String time, String type, String action, String comment, String user){
            String s = "";

            s += "\t\t\t\t\t<tr>\n";
            s += "\t\t\t\t\t\t<td>" + time + "</td>\n";
            s += "\t\t\t\t\t\t<td>" + type + "</td>\n";
            s += "\t\t\t\t\t\t<td>" + action + "</td>\n";
            s += "\t\t\t\t\t\t<td>" + comment + "</td>\n";
            s += "\t\t\t\t\t\t<td>" + user + "</td>\n";
            s += "\t\t\t\t\t\t<td>" + "<a href=\"?tab=history\">restore</a>" + "</td>\n";
            s += "\t\t\t\t\t\t<td>" + "<input type=\"text\" name=\"newcomment\" />" + "</td>\n";
            s += "\t\t\t\t\t</tr>\n";
            al.add(s);      
        }
    
        
        /**
	 * gets edithistory from DB
	 */
        static private void searchDB(){
            //static data only for prototype
            
            String[][] testdata={       { "2012-01-05","k-mapping","edit","wrong","abc"},
                                        { "2012-01-04","kv-mapping","edit","bla","def"},
                                        { "2012-01-03","k-mapping","delete","bad","ghi"},
                                        { "2012-01-02","kv-mapping","delete","wtf","jkl"},
                                        { "2012-01-01","k-mapping","edit","haha","mno"}
                                    };
            
            
            for(int i=0;i<5;i++)
            {
                fillEditHistoryArray(testdata[i][0],testdata[i][1],testdata[i][2],testdata[i][3],testdata[i][4]);
            }
           
        }
        
}
