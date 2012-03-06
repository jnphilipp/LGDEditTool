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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.1
 */
public class DatabasePostgreSQL {
	private boolean driver = true;
	private Connection connection = null;
	private Statement statement = null;
	
	/**
	* Creates a new Database and checks the driver.
	* @throws ClassNotFoundException
	*/
	public DatabasePostgreSQL() throws ClassNotFoundException {
		try {
			Class.forName("org.postgresql.Driver");
		}
		catch ( ClassNotFoundException e ) {
			//System.err.println("No database driver found.");
			this.driver = false;
			throw e;
		}
	}
	
	/**
	* Returns <code>true</code> wenn the driver was found otherwise returns <code>false</code>.
	* @return driver statw
	*/
	public boolean getDriver() {
		return this.driver;
	}
	
	/**
	* Establishes a connection to the given database.
	* @param database database
	* @param user user
	* @param passwd password
	* @param ssl using SSL
	* @return <code>true</code> if connection was established else it returns <code>flase</code>
	* @throws SQLException
	*/
	public boolean connect(String database, String user, String passwd, boolean ssl) throws SQLException {
		if ( this.driver && this.connection == null ) {
			Properties properties = new Properties();
			properties.setProperty("user", user);
			properties.setProperty("password", passwd);
			properties.setProperty("ssl", Boolean.toString(ssl));
			properties.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory");
			this.connection = DriverManager.getConnection((database.startsWith("jdbc:postgresql://") ? database : "jdbc:postgresql://" + database), properties);
			this.statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			return true;
		}
		else {
			return false;
		}
	}

	/**
	* Closes the connection to the database.
	* @throws SQLException
	*/
	public void close() throws SQLException {
		if ( this.connection != null ) {
			this.statement.close();
			this.connection.close();
			this.connection = null;
		}
	}

	/**
	 * Executes the SQL command and returns the value.
	 * @param command SQL command
	 * @return values
	 * @throws SQLException 
	 */
	public Object[][] execute(String command) throws SQLException {
		if ( this.statement != null ) {
			if ( this.statement.execute(command) ) {
				ResultSet rs = this.statement.getResultSet();


				int row = 0;
				for ( row = 0; rs.next(); row++);
				rs.beforeFirst();

				ResultSetMetaData rsmd = rs.getMetaData();

				Object[][]  value = new Object[row][rsmd.getColumnCount()];

				for (int i = 0; rs.next(); i++) {
					for (int j = 0; j < rsmd.getColumnCount(); j++)
						value[i][j] = rs.getObject(j + 1);
				}

				rs.close();

				return value;
			}
			else
				return null;
		}

		return null;
	}
}