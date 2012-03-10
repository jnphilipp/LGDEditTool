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
package LGDEditTool.SiteHandling;

import LGDEditTool.db.DatabaseBremen;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class RequestHandling {
	/**
	 * Handling Requests.
	 * @param request Request
	 * @throws Exception 
	 */
	public static void doRequestHandling(HttpServletRequest request) throws Exception {
		DatabaseBremen database = new DatabaseBremen();
		database.connect();
		
		if ( request.getParameter("kmapping") != null && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null ) {
			database.execute("UPDATE lgd_map_resource_k set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "' WHERE  k='" + request.getParameter("k") + "'");
		}
		else if ( request.getParameter("kvmapping") != null && request.getParameter("k") != null && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null ) {
			database.execute("UPDATE lgd_map_resource_kv set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "' WHERE  k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "'");
		}

		database.disconnect();
	}
}