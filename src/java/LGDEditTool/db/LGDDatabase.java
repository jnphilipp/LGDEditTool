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
public class LGDDatabase extends DatabasePostgreSQL {
	private static LGDDatabase bremen;

	/**
	* Creates a new Database and checks the driver.
	* @throws ClassNotFoundException
	*/
	private LGDDatabase () throws ClassNotFoundException {
		super();
	}

	/**
	 * Returns an instance of the class. If none exists it will be created.
	 * @return instance
	 * @throws ClassNotFoundException 
	 */
	public static LGDDatabase getInstance() throws ClassNotFoundException {
		if ( bremen == null )
			bremen = new LGDDatabase();

		return bremen;
	}

	/**
	* Establishes a connection to the LGD database.
	* @return <code>true</code> if connection was established else it returns <code>false</code>
	* @throws SQLException
	*/
	public boolean connect() throws SQLException {
		return super.connect("localhost:5432/lgd", "lgd", "lgd", true);
	}

	/**
	 * Creates a new user or updates if a username exists but no email is stored.
	 * @param update update or insert
	 * @param user username
	 * @param email email
	 * @param pw password
	 * @throws SQLException 
	 */
	public void createUser(boolean update, String user, String email, String pw) throws SQLException {
		if ( update ) {
			String arg[] = {email, pw, "lgd_user" + user, user};
			this.executePrepared("UPDATE lgd_user SET email=?, password=?, admin=FALSE, view=? WHERE username=?", arg);
		}
		else {
			String arg[] = {user, email, pw, "lgd_user_" + user};
			this.executePrepared("INSERT INTO lgd_user VALUES (?, ?, ?, FALSE, ?)", arg);
		}
	}

	/**
	 * Returns all administrators with username and email address.
	 * @return List of email addresses and usernames, the email address is in the first field
	 * @throws SQLException 
	 */
	public String[][] getAdministatorEmailAddresses() throws SQLException {
		Object[][] a = this.execute("SELECT email, username FROM lgd_user WHERE admin ORDER BY username");
		String[][] email = new String[a.length][2];

		for ( int i = 0; i < email.length; i++ ) {
			email[i][0] = a[i][0].toString();
			email[i][1] = a[i][1].toString();
		}

		return email;
	}
}