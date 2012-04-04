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
import java.security.MessageDigest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import LGDEditTool.db.DatabaseBremen;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public final class User {
	private static User user;

	private String username = "";
	private boolean loggedIn = false;
	private boolean admin = false;
	private String view = "";

	private User() {}

	public static synchronized User getInstance() {
		if ( user == null )
			user = new User();

		return user;
	}

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
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < byteData.length; j++) {
					sb.append(Integer.toString((byteData[j] & 0xff) + 0x100, 16).substring(1));
				}
				this.loggedIn = cookies[i].getValue().equals(sb.toString());
			}
		}

		if ( this.loggedIn ) {
			DatabaseBremen db = DatabaseBremen.getInstance();
			db.connect();
			Object[][] a = db.execute("SELECT admin, view FROM lgd_user WHERE email='" + this.username + "'");
			this.admin = Boolean.parseBoolean(a[0][0].toString());
			this.view = a[0][1].toString();
		}
	}

	public void createUser(String username) {
		this.username = username;
	}

	public void createUser(String username, boolean loggedIn, boolean admin) {
		this.username = username;
		this.loggedIn = loggedIn;
		this.admin = admin;
	}

	public String getUsername() {
		return this.username;
	}

	public String getView() {
		return this.view;
	}

	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	public boolean isAdmin() {
		return this.admin;
	}

	public void logout() {
		this.loggedIn = false;
		this.admin = false;
	}

	public void createCookie(HttpServletResponse response) throws Exception {
		Cookie u = new Cookie("lgd_username", this.username);
		u.setMaxAge(365 * 24 * 60 * 60);

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update((this.username + Boolean.toString(this.loggedIn)).getBytes());

		byte byteData[] = md.digest();
		//convert the byte to hex
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}

		Cookie l = new Cookie("lgd_loggedIn", sb.toString());
		l.setMaxAge(60 * 60);

		response.addCookie(u);
		response.addCookie(l);
	}

	public void updateView(String newView) throws Exception {
		DatabaseBremen db = DatabaseBremen.getInstance();
		db.connect();
		this.view = db.execute("UPDATE lgd_user set view='" + (newView.equals("main") ? Functions.MAIN_BRANCH : "lgd_user_" + db.execute("SELECT username FROM lgd_user WHERE email='" + this.username + "'")[0][0]) + "' WHERE email='" + this.username + "' RETURNING view")[0][0].toString();
	}
}