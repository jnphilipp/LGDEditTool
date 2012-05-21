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

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.1
 */
public class DatabasePostgreSQL {
	/**
	 * Is <code>true</code> if the JDBC driver for PostgreSQL exists, else it is <code>false</code>.
	 */
	private boolean driver = true;
	private Connection connection = null;
	//private Statement statement = null;
	
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
	* @return <code>true</code> if connection was established else it returns <code>false</code>
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
	public void disconnect() throws SQLException {
		if ( this.connection != null ) {
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
		if ( this.connection != null ) {
			Statement statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			if ( statement.execute(command) ) {
				ResultSet rs = statement.getResultSet();


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

	/**
	 * Executes the SQL command and returns the value with a prepared statement.
	 * @param command SQL query
	 * @param arg arguments
	 * @return values
	 * @throws SQLException 
	 */
	public Object[][] executePrepared(String query, String[] arg) throws SQLException {
		if ( this.connection != null ) {
			PreparedStatement statement = this.connection.prepareStatement(query);

			for ( int i = 1; i <= arg.length; i++ )
				statement.setString(i, arg[i - 1]);

			if ( statement.execute() ) {
				ResultSet rs = statement.getResultSet();


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