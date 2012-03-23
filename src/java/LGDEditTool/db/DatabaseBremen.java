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

	/**
	* Creates a new Database and checks the driver.
	* @throws ClassNotFoundException
	*/
	public DatabaseBremen () throws ClassNotFoundException {
		super();
	}

	/**
	* Establishes a connection to the Bremen database.
	* @return <code>true</code> if connection was established else it returns <code>false</code>
	* @throws SQLException
	*/
	public boolean connect() throws SQLException {
		return super.connect("jnphilipp.dyndns.org:5432/bremen", "lgd", "lgd", true);
	}
}