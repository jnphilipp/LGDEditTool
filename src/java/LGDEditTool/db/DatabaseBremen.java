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
		return super.connect("jnphilipp.dyndns.org:5432/bremen_userspace", "lgd", "lgd", true);
	}
}