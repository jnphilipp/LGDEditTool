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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class User {
	private String username = "";
	private boolean loggedIn = false;

	public User(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		for ( int i = 0; i < cookies.length; i++ ) {
			if ( cookies[i].getName().equals("lgd_username") )
				this.username = cookies[i].getValue();
			else if ( cookies[i].getName().equals("lgd_loggedIn") )
				this.loggedIn = Boolean.valueOf(cookies[i].getValue());
		}
	}

	public User(String username) {
		this.username = username;
	}

	public User(String username, boolean loggedIn) {
		this.username = username;
		this.loggedIn = loggedIn;
	}

	public String getUsername() {
		return this.username;
	}

	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	public void logout() {
		this.loggedIn = false;
	}

	public void createCookie(HttpServletResponse response) {
		Cookie u = new Cookie("lgd_username", this.username);
		u.setMaxAge(365 * 24 * 60 * 60);
		Cookie l = new Cookie("lgd_loggedIn", Boolean.toString(this.loggedIn));
		l.setMaxAge(60 *60);

		response.addCookie(u);
		response.addCookie(l);
	}

	public static User createUser(HttpServletRequest request) {
		Cookie[] c = request.getCookies();
		boolean create = false;
		User user = null;

		
		if ( c == null)
			return user;

		for ( int i = 0; i < c.length; i++ )
			if ( c[i].getName().equals("lgd_username") )
				create = true;

		if ( create )
			user = new User(request);

		return user;
	}
}