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

import LGDEditTool.Functions;
import LGDEditTool.db.DatabaseBremen;
import java.security.MessageDigest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Specified exactly one user, and handles all necessary attributes.
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public final class User {
	/**
	 * Holding the instance of User.
	 */
	private static User user;

	/**
	 * Contains the username of the user.
	 */
	private String username = "";
	/**
	 * Is <code>flase</code> if the user isn't logged in, else it is <code>true</code>.
	 */
	private boolean loggedIn = false;
	/**
	 * Is <code>true</code> if the user is an administrator, else it is <code>false</code>.
	 */
	private boolean admin = false;
	/**
	 * Representing the view for the auto completion and is user to determine whether the user is working on his own branch or the main branch.
	 */
	private String view = Functions.MAIN_BRANCH;

	/**
	 * Constructor, not used.
	 */
	private User() {}

	/**
	 * Creates, if necessary, a new instance of the User and returns it.
	 * @return instance of this class
	 */
	public static synchronized User getInstance() {
		if ( user == null )
			user = new User();

		return user;
	}

	/**
	 * Sets the attributes from the values passed by cookies.
	 * @param request HTTP request
	 * @throws Exception 
	 */
	public void createUser(HttpServletRequest request) throws Exception {
		Cookie[] cookies = request.getCookies();
                
                if ( cookies == null )
                    return;

		if ( cookies == null )
			return;

		for ( int i = 0; i < cookies.length; i++ ) {
			if ( cookies[i].getName().equals("lgd_username") )
				this.username = cookies[i].getValue();
			else if ( cookies[i].getName().equals("lgd_loggedIn") ) {
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update((this.username + Boolean.toString(true)).getBytes());

				byte byteData[] = md.digest();
				//convert the byte to hex
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < byteData.length; j++) {
					sb.append(Integer.toString((byteData[j] & 0xff) + 0x100, 16).substring(1));
				}
				this.loggedIn = cookies[i].getValue().equals(sb.toString());
			}
		}

		if ( this.loggedIn ) {
			DatabaseBremen db = DatabaseBremen.getInstance();
			db.connect();
			Object[][] a = db.execute("SELECT admin, view FROM lgd_user WHERE username='" + this.username + "'");
			this.admin = Boolean.parseBoolean(a[0][0].toString());//t -> user ist admin
			this.view = a[0][1].toString();//view
		}
	}

	/**
	 * Sets the attributes specified by the parameters.
	 * @param username Email
	 * @param view View
	 * @param loggedIn is logged in
	 * @param admin  is administrator
	 */
	public void createUser(String username, String view, boolean loggedIn, boolean admin) {
		this.username = username;
		this.view = view;
		this.loggedIn = loggedIn;
		this.admin = admin;
	}

	/**
	 * Returns the email. (primary key of lgd_user)
	 * @return Email
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Returns the view, which is mostly used to determine the branch.
	 * @return view
	 */
	public String getView() {
		return this.view;
	}

	/**
	 * Returns whether the user is logged in or not.
	 * @return <code>true</code> if the user is logged in, else <code>false</code>
	 */
	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	/**
	 * Returns whether the user is an administrator or not.
	 * @return <code>true</code> if the user is an administrator in, else <code>false</code>
	 */
	public boolean isAdmin() {
		return this.admin;
	}

	public void logout() {
		this.loggedIn = false;
		this.admin = false;
		this.view = Functions.MAIN_BRANCH;
	}

	/**
	 * Creates to cookies, one contains the username (email), the other is a md5-hash and determines whether the user is logged in or not.
	 * @param response HTTP response, for saving the cookies
	 * @throws Exception 
	 */
	public void createCookie(HttpServletResponse response) throws Exception {
		if ( this.username.equals("") )
			return;

		Cookie u = new Cookie("lgd_username", this.username);
		u.setMaxAge(365 * 24 * 60 * 60);

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update((this.username + Boolean.toString(this.loggedIn)).getBytes());

		byte byteData[] = md.digest();
		//convert the byte to hex
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		Cookie l = new Cookie("lgd_loggedIn", sb.toString());
		l.setMaxAge(60 * 60);

		response.addCookie(u);
		response.addCookie(l);
	}

	/**
	 * Updates the view to the given value an saves it in the database.
	 * @param newView the new view
	 * @throws Exception 
	 */
	public void updateView(String newView) throws Exception {
		DatabaseBremen db = DatabaseBremen.getInstance();
		db.connect();
		this.view = db.execute("UPDATE lgd_user set view='" + (newView.equals("main") ? Functions.MAIN_BRANCH : "lgd_user_" + db.execute("SELECT username FROM lgd_user WHERE username='" + this.username + "'")[0][0]) + "' WHERE username='" + this.username + "' RETURNING view")[0][0].toString();
	}
}