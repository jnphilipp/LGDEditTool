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
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class TemplatesMapping {
	/**
	 * Template for K-Mappings.
	 * @param k K
	 * @param property property
	 * @param object object
	 * @param affectedEntities affected Entities
	 * @return String
	 */
	static public String kMapping(String k, String property, String object, String affectedEntities) {
		String re = "\t\t\t\t<h2>K-Mappings</h2>\n";
		re += "\t\t\t\t<table>\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th class=\"edit\"></th>\n";
		re += "\t\t\t\t\t</tr>\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t<td class=\"edit\"><a href=\"?tab=mappings&k=" + k + "\">Edit/Delete</a></td>\n";
		re += "\t\t\t\t\t</tr>\n";
		re += "\t\t\t\t</table>\n";
		return re;
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
	static public String kvMapping(String k, String v, String property, String object, String affectedEntities) {
		String re = "\t\t\t\t<h2>KV-Mappings</h2>\n";
		re += "\t\t\t\t<table>\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>v</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th class=\"edit\"></th>\n";
		re += "\t\t\t\t\t</tr>\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<td>" + k + "</td>\n";
		re += "\t\t\t\t\t\t<td>" + v + "</td>\n";
		re += "\t\t\t\t\t\t<td>" + property + "</td>\n";
		re += "\t\t\t\t\t\t<td>" + object + "</td>\n";
		re += "\t\t\t\t\t\t<td>" + affectedEntities + "</td>\n";
		re += "\t\t\t\t\t\t<td class=\"edit\"><a href=\"?tab=mappings&k=" + k + "&v=" + v + "\">Edit/Delete</a></td>\n";
		re += "\t\t\t\t\t</tr>\n";
		re += "\t\t\t\t</table>\n";
		return re;
	}

	/**
	 * Search Resutl.
	 * @param search search query
	 * @return String
	 * @throws Exception 
	 */
	public static String kMappingSearch(String search) throws Exception {
		DatabaseBremen database = new DatabaseBremen();
		String re = "";
		//database.connect("192.168.0.101:5432/bremen", "lgd", "lgd", true);
		database.connect();
		Object[][] a = database.execute("SELECT k, count(k) FROM lgd_map_resource_k WHERE k='" + search.substring(0, search.indexOf(" ")) + "' GROUP BY k");
		re += kMapping(a[0][0].toString(), "", "", a[0][1].toString());

		a = database.execute("SELECT k, v, count(k) FROM lgd_map_resource_kv WHERE k='" + search.substring(0, search.indexOf(" ")) + "' GROUP BY k, v");
		
		re = "\t\t\t\t<h2>KV-Mappings</h2>\n";
		re += "\t\t\t\t<table>\n";
		re += "\t\t\t\t\t<tr>\n";
		re += "\t\t\t\t\t\t<th>k</th>\n";
		re += "\t\t\t\t\t\t<th>v</th>\n";
		re += "\t\t\t\t\t\t<th>property</th>\n";
		re += "\t\t\t\t\t\t<th>object</th>\n";
		re += "\t\t\t\t\t\t<th>affected Entities</th>\n";
		re += "\t\t\t\t\t\t<th class=\"edit\"></th>\n";
		re += "\t\t\t\t\t</tr>\n";

		for ( int i = 0; i < a.length; i++ ) {
			//re += kvMapping(a[i][0].toString(), a[i][1].toString(), "", "", a[i][2].toString());
			re += "\t\t\t\t\t<tr>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][0].toString() + "</td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][1].toString() + "</td>\n";
			re += "\t\t\t\t\t\t<td></td>\n";
			re += "\t\t\t\t\t\t<td></td>\n";
			re += "\t\t\t\t\t\t<td>" + a[i][2].toString() + "</td>\n";
			re += "\t\t\t\t\t\t<td class=\"edit\"><a href=\"?tab=mappings&k=" + a[i][0].toString() + "&v=" + a[i][1].toString() + "\">Edit/Delete</a></td>\n";
			re += "\t\t\t\t\t</tr>\n";
		}
		re += "\t\t\t\t</table>\n";

		database.close();
		//re = TemplatesMapping.kMapping("amenity", "rdf:type", "lgdo:Bakery", "300k");
		//re += TemplatesMapping.kvMapping("amenity", "bakery", "rdf:type", "lgdo:Bakery", "10k");
		return re;
	}
}