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

/**
 *
 * @author Alexander Richter
 */
public class TemplatesUnmappedTags {
	/**
	 * template Unmapped Tags
	 * @param ksite site k-mappings
	 * @param kvsite site kv-mappings
	 * @return sitecontent
	 */
	static public String unmappedTags(String ksite,String kvsite) throws Exception{
		DatabaseBremen.getInstance().connect();

		//kmapping table
		String s = "\t\t\t\t<h2>List of all Unmapped Tags</h2>\n";
		s += "\t\t\t\t<table class=\"table\">\n";
		s += "\t\t\t\t\t<tr class=mapping>\n";
		s += "\t\t\t\t\t\t<th>k</th>\n";
		s += "\t\t\t\t\t\t<th>usage_count</th>\n";
		s += "\t\t\t\t\t\t<th>distinct_value_count</th>\n";		
		s += "\t\t\t\t\t</tr>\n";

		//fill table with k-mappings
		s += listAllk(Integer.valueOf(ksite));
		s += "\t\t\t\t</table>\n<br />";

		//prev-next-site
		s += "\t\t\t\t<div style=\"float: right;\">\n";
		if(Integer.valueOf(ksite)>1){
			Integer prevsite=Integer.valueOf(ksite)-1;
			s += "\t\t\t\t\t<a href=\"?tab=unmapped&ksite="+ prevsite.toString() + "&kvsite="+kvsite+"\">&#60;prev</a>&nbsp;&nbsp;&nbsp; ";
		}
		Integer nextsite=Integer.valueOf(ksite)+1;
		s += "\t\t\t\t\t<a href=\"?tab=unmapped&ksite="+ nextsite.toString() + "&kvsite="+kvsite+"\">next</a>\n";
		s += "\t\t\t\t</div>\n";

		//kvmapping table
		s += "\t\t\t\t<table class=\"table\">\n";
		s += "\t\t\t\t\t<tr class=mapping>\n";
		s += "\t\t\t\t\t\t<th>k</th>\n";
		s += "\t\t\t\t\t\t<th>v</th>\n";
		s += "\t\t\t\t\t\t<th>usage_count</th>\n";
		s += "\t\t\t\t\t</tr>\n";
		//fill table with kv-mappings

		s += listAllkv(Integer.valueOf(kvsite));
		s += "\t\t\t\t</table>\n<br />";

		//prev-next-site
		s += "\t\t\t\t<div style=\"float: right;\">\n";
		if(Integer.valueOf(kvsite)>1){
			Integer prevsite=Integer.valueOf(kvsite)-1;
			s += " \t\t\t\t\t<a href=\"?tab=unmapped&ksite="+ ksite + "&kvsite="+prevsite.toString()+"\">&#60;prev</a>&nbsp;&nbsp;&nbsp; ";
		}
		Integer nextsite2=Integer.valueOf(kvsite)+1;
		s += "\t\t\t\t\t<a href=\"?tab=unmapped&ksite="+ ksite + "&kvsite="+nextsite2.toString()+"\">next</a>\n";
		s += "\t\t\t\t</div>\n";

		return s;
	}

	/**
	 * fills table with k-mappings
	 * @param ksite current displayed site for k-mappings
	 * @throws Exception 
	 */
	static private String listAllk(int ksite) throws Exception {
		String s = "";
		DatabaseBremen database = DatabaseBremen.getInstance();

		Object[][] a = database.execute("SELECT k, usage_count , distinct_value_count FROM lgd_stat_tags_k a WHERE NOT EXISTS (Select b.k FROM ( Select k FROM  lgd_map_datatype UNION ALL SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_literal UNION ALL SELECT k FROM lgd_map_property UNION ALL SELECT k FROM lgd_map_resource_k UNION ALL SELECT k FROM lgd_map_resource_kv UNION ALL SELECT k FROM lgd_map_resource_prefix ) b WHERE a.k=b.k) LIMIT 20 OFFSET " + (ksite-1)*20);

		int count=0;
		for (int i=0;i<a.length;i++) {
			s += "\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t<td>"+a[i][0]+"</td>\n";
			s += "\t\t\t\t\t<td>"+a[i][1]+"</td>\n";
			s += "\t\t\t\t\t<td>"+a[i][2]+"</td>\n";
			s += "\t\t\t\t\t</tr>\n";
		}

		return s;
	}

	/**
	 * fills table with kv-mappings
	 * @param kvsite current displayed site for kv-mappings
	 * @throws Exception 
	 */
	static private String listAllkv(int kvsite) throws Exception {
		String s = new String();
		DatabaseBremen database = DatabaseBremen.getInstance();

		Object[][] a = database.execute("SELECT k,v, usage_count  FROM lgd_stat_tags_kv a WHERE NOT EXISTS (Select b.k FROM ( SELECT k,v FROM lgd_map_label UNION ALL  SELECT k,v FROM lgd_map_resource_kv ) b WHERE a.k=b.k) LIMIT 20 OFFSET "+(kvsite-1)*20);

		int count=0;
		for (int i=0;i<a.length;i++) {
			s += "\t\t\t\t\t<tr>\n";
			s += "\t\t\t\t\t<td>"+a[i][0]+"</td>\n";
			s += "\t\t\t\t\t<td>"+a[i][1]+"</td>\n";
			s += "\t\t\t\t\t<td>"+a[i][2]+"</td>\n";                   
			s += "\t\t\t\t\t</tr>\n";
		}

		return s;
	}
 }