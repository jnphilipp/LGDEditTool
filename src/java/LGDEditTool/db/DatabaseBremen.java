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

package LGDEditTool.db;

import java.sql.SQLException;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class DatabaseBremen extends DatabasePostgreSQL {
	private static DatabaseBremen bremen;

	/**
	* Creates a new Database and checks the driver.
	* @throws ClassNotFoundException
	*/
	private DatabaseBremen () throws ClassNotFoundException {
		super();
	}

	/**
	 * Returns an instance of the class. If none exists it will be created.
	 * @return instance
	 * @throws ClassNotFoundException 
	 */
	public static synchronized DatabaseBremen getInstance() throws ClassNotFoundException {
		if ( bremen == null )
			bremen = new DatabaseBremen();

		return bremen;
	}

	/**
	* Establishes a connection to the Bremen database.
	* @return <code>true</code> if connection was established else it returns <code>false</code>
	* @throws SQLException
	*/
	public boolean connect() throws SQLException {
		return super.connect("jnphilipp.dyndns.org:5432/bremen", "lgd", "lgd", true);
	}

	/**
	 * Creates a new view, containing the K/KV/Datatype/Literal-Mappings, for the specified user.
	 * @param username Username
	 * @throws SQLException 
	 */
	public void createView(String username) throws SQLException {
		super.execute("CREATE VIEW lgd_user_" + username + " AS SELECT k, v, COUNT(k) FROM lgd_map_resource_kv WHERE (user_id = '" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND (k, v) NOT IN (SELECT k, v FROM lgd_map_resource_kv WHERE user_id = '" + username + "')) GROUP BY k, v UNION ALL SELECT k, '' AS v, COUNT(k) + (SELECT COUNT(k) FROM lgd_map_resource_kv WHERE k=lgd_map_resource_k.k AND ((user_id = '" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND (k, v) NOT IN (SELECT k, v FROM  lgd_map_resource_kv WHERE user_id = '" + username + "')))) + (SELECT COUNT(k) FROM lgd_map_datatype WHERE k=lgd_map_resource_k.k AND ((user_id = '" + username + "' AND datatype != 'deleted') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_datatype WHERE user_id = '" + username + "')))) + (SELECT COUNT(k) FROM lgd_map_literal WHERE k=lgd_map_resource_k.k AND ((user_id = '" + username + "' AND property != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_literal WHERE user_id = '" + username + "')))) FROM lgd_map_resource_k WHERE (user_id='" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + username + "')) GROUP BY k UNION ALL SELECT k, '' AS v, COUNT(k) FROM lgd_map_datatype WHERE ((user_id = '" + username + "' AND datatype != 'deleted') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_datatype WHERE user_id = '" + username + "'))) AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE (user_id='" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + username + "'))) GROUP BY k UNION ALL SELECT k, '' AS v, COUNT(k) FROM lgd_map_literal WHERE ((user_id = '" + username + "' AND property != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_literal WHERE user_id = '" + username + "'))) AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE (user_id='" + username + "' AND property != '' AND object != '') OR (user_id = 'main' AND k NOT IN (SELECT k FROM lgd_map_resource_k WHERE user_id = '" + username + "'))) GROUP BY k ORDER BY k, v");
	}

	/**
	 * Creates a new view, containing the history tables, for the specified user.
	 * @param username Username
	 * @throws SQLException 
	 */
	public void createViewHistory(String username) throws SQLException {
		super.execute("CREATE VIEW lgd_user_" + username + "_history AS SELECT k, v, COUNT(k) FROM lgd_map_resource_kv_history WHERE userspace='" + username + "' GROUP BY k, v UNION ALL SELECT k, '' AS v, COUNT(k) + (SELECT COUNT(k) FROM lgd_map_resource_kv_history WHERE k=lgd_map_resource_k_history.k AND userspace='" + username + "') + (SELECT COUNT(k) FROM lgd_map_datatype_history WHERE k=lgd_map_resource_k_history.k AND userspace='" + username + "') + (SELECT COUNT(k) FROM lgd_map_literal_history WHERE k=lgd_map_resource_k_history.k AND userspace='" + username + "') FROM lgd_map_resource_k_history WHERE userspace='" + username + "' GROUP BY k UNION ALL SELECT k, '' AS v, COUNT(k) FROM lgd_map_datatype_history WHERE userspace='" + username + "' AND k NOT IN (SELECT k FROM lgd_map_resource_k_history WHERE userspace='" + username + "') GROUP BY k UNION ALL SELECT k, '' AS v, COUNT(k) FROM lgd_map_literal_history WHERE userspace='" + username + "' AND k NOT IN (SELECT k FROM lgd_map_resource_k_history WHERE userspace='" + username + "') GROUP BY k ORDER BY k, v");
	}

	/**
	 * Creates a new view, containing the unmapped tags, for the specified user.
	 * @param username Username
	 * @throws SQLException 
	 */
	public void createViewUnmapped(String username) throws SQLException {
		super.execute("CREATE VIEW lgd_user_" + username + "_unmapped AS SELECT k, '' AS v, COUNT(k) + (SELECT COUNT(k) FROM lgd_stat_tags_kv c WHERE NOT EXISTS (Select b.k FROM (SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_resource_kv WHERE user_id='main' OR user_id='" + username + "') b WHERE c.k=b.k) AND c.k=a.k) AS count FROM lgd_stat_tags_k a WHERE NOT EXISTS (Select b.k FROM ( Select k FROM lgd_map_datatype WHERE user_id='main' OR user_id='" + username + "' UNION ALL SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_literal WHERE user_id='main' OR user_id='" + username + "' UNION ALL SELECT k FROM lgd_map_property UNION ALL SELECT k FROM lgd_map_resource_k WHERE user_id='main' OR user_id='" + username + "' UNION ALL SELECT k FROM lgd_map_resource_kv WHERE user_id='main' OR user_id='" + username + "' UNION ALL SELECT k FROM lgd_map_resource_prefix ) b WHERE a.k=b.k) GROUP BY k UNION ALL SELECT k, v, COUNT(k) AS count FROM lgd_stat_tags_kv a WHERE NOT EXISTS (Select b.k FROM (SELECT k FROM lgd_map_label UNION ALL SELECT k FROM lgd_map_resource_kv WHERE user_id='main' OR user_id='" + username + "') b WHERE a.k=b.k) GROUP BY k, v ORDER BY k, v");
	}

	/**
	 * Returns all administrators with username and email address.
	 * @return List of email addresses and usernames, the email address is in the first field
	 * @throws SQLException 
	 */
	public String[][] getAdministatorEmailAddresses() throws SQLException {
		Object[][] a = super.execute("SELECT email, username FROM lgd_user WHERE admin ORDER BY username");
		String[][] email = new String[a.length][2];

		for ( int i = 0; i < email.length; i++ ) {
			email[i][0] = a[i][0].toString();
			email[i][1] = a[i][1].toString();
		}

		return email;
	}
}