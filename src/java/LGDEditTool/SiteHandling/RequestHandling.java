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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

/**
 * Timestap: YYYY-MM-ddThh:mm:ss (2011-05-31T23:51:36)
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class RequestHandling {
	/**
	 * Handling Requests.
	 * @param request Request
	 * @return message
	 * @throws Exception 
	 */
	public static String doRequestHandling(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DatabaseBremen database = DatabaseBremen.getInstance();
		database.connect();
		String re = "";

		if ( request.getParameter("user") != null && request.getParameter("password") != null && request.getParameter("login") != null ) {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(request.getParameter("password").getBytes());

			byte byteData[] = md.digest();
			//convert the byte to hex
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			Object[][] a = database.execute("SELECT username, view, admin FROM lgd_user WHERE (username='" + request.getParameter("user") + "' OR email='" + request.getParameter("user") + "') AND password='" + sb + "'");

			if ( a.length == 0 ) {
				User.getInstance().createUser("", Functions.MAIN_BRANCH, false, false);
				re = "Login or password incorrect.";
			}
			else {
				User.getInstance().createUser(a[0][0].toString(), a[0][1].toString(), true, Boolean.parseBoolean(a[0][2].toString()));
				User.getInstance().createCookie(response);
				re = "Login successful.";
			}
		}//#########################################################################
		else if ( request.getParameter("logout") != null && request.getParameter("logout").equals("yes") ) {
			User.getInstance().logout();
			User.getInstance().createCookie(response);
			re = "Logout successful.";
		}//#########################################################################
		else if ( request.getParameter("kmapping") != null && request.getParameter("kmapping").equals("Save") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("aobject") != null && request.getParameter("aproperty") != null && request.getParameter("comment") != null ) {
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				Object[][] a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND property='" + request.getParameter("aproperty") + "' AND object='" + request.getParameter("aobject") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("aproperty") + "', '" + request.getParameter("aobject") + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'edit', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_k VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + username + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_k set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND object='" + request.getParameter("aobject") + "' AND property='" + request.getParameter("aproperty") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			re = "K-Mapping successfully changed.";
		}//#########################################################################
		else if ( request.getParameter("kmapping") != null && request.getParameter("kmapping").equals("Commit") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT last_history_id, property, object FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND user_id='main'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + (hid == -1 ? "" : a[0][1]) + "', '" + (hid == -1 ? "" : a[0][2]) + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'commit', 'main'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_k VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', 'main', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_k set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND user_id='main'");


			/*** If you want to delete everything from a userspace after a commit ***/
			//database.execute("DELETE FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND user_id='" + User.getInstance().getUsername() + "'");
			//database.execute("DELETE FROM lgd_map_resource_k_history WHERE k='" + request.getParameter("k") + "' AND userspace='" + User.getInstance().getUsername() + "'");

			re = "K-Mapping successfully commited.";
		}//#########################################################################
		else if ( request.getParameter("kmapping") != null && request.getParameter("kmapping").equals("Delete") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				Object[][] a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND property='" + request.getParameter("property") + "' AND object='" + request.getParameter("object") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'deleted', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_k VALUES ('" + request.getParameter("k") + "', '', '', '" + username + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_k set object='', property='', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			re = "K-Mapping successfully deleted.";
		}//#########################################################################
		else if ( request.getParameter("kmapping") != null && request.getParameter("kmapping").equals("Create") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '', '', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'create', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + ") RETURNING id");
			database.execute("INSERT INTO lgd_map_resource_k VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");

			re = "K-Mapping successfully created.";
		}//#########################################################################
		else if ( request.getParameter("kmapping") != null && request.getParameter("kmapping").equals("Clear") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND property='" + request.getParameter("property") + "' AND object='" + request.getParameter("object") + "' AND user_id='" + User.getInstance().getUsername() + "'");
			int hid = Integer.parseInt(a[0][0].toString());
			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'cleared', '" + User.getInstance().getUsername() + "'," + hid + ") RETURNING id");
			database.execute("UPDATE lgd_map_resource_k set object=(SELECT object FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND user_id='main'), property=(SELECT property FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND user_id='main'), last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "' AND user_id='" + User.getInstance().getUsername() + "'");

			re = "K-Mapping successfully cleared.";
		}//#########################################################################
		else if ( request.getParameter("kvmapping") != null && request.getParameter("kvmapping").equals("Save") && request.getParameter("k") != null  && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("aobject") != null && request.getParameter("aproperty") != null && request.getParameter("comment") != null ) {
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				Object[][] a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_resource_kv WHERE k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND property='" + request.getParameter("aproperty") + "' AND object='" + request.getParameter("aobject") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("aproperty") + "', '" + request.getParameter("aobject") + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'edit', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_kv VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + username + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_kv set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND object='" + request.getParameter("aobject") + "' AND property='" + request.getParameter("aproperty") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			re = "KV-Mapping successfully changed.";
		}//#########################################################################
		else if ( request.getParameter("kvmapping") != null && request.getParameter("kvmapping").equals("Commit") && request.getParameter("k") != null && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT last_history_id, property, object FROM lgd_map_resource_kv WHERE k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND user_id='main'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + (hid == -1 ? "" : a[0][1]) + "', '" + (hid == -1 ? "" : a[0][2]) + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'commit', 'main'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_kv VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', 'main', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_kv set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND user_id='main'");


			/*** If you want to delete everything from a userspace after a commit ***/
			//database.execute("DELETE FROM lgd_map_resource_kv WHERE k='" + request.getParameter("k") + "' AND  v='" + request.getParameter("v") + "' AND user_id='" + User.getInstance().getUsername() + "'");
			//database.execute("DELETE FROM lgd_map_resource_kv_history WHERE k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND userspace='" + User.getInstance().getUsername() + "'");

			re = "KV-Mapping successfully commited.";
		}//#########################################################################
		else if ( request.getParameter("kvmapping") != null && request.getParameter("kvmapping").equals("Delete") && request.getParameter("k") != null && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				Object[][] a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_resource_kv WHERE k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND property='" + request.getParameter("property") + "' AND object='" + request.getParameter("object") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'deleted', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_kv VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '', '', '" + username + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_kv set object='', property='', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			re = "KV-Mapping successfully deleted.";
		}//#########################################################################
		else if ( request.getParameter("kvmapping") != null && request.getParameter("kvmapping").equals("Create") && request.getParameter("k") != null && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '', '', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'create', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + ") RETURNING id");
			database.execute("INSERT INTO lgd_map_resource_kv VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");

			re = "KV-Mapping successfully created.";
		}//#########################################################################
		else if ( request.getParameter("kmappingedit") != null && request.getParameter("kmappingedit").equals("Restore") && request.getParameter("id") != null && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a;
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			String hid = request.getParameter("id");
			while ( true ) {
				a = database.execute("SELECT k, property, object FROM lgd_map_resource_k WHERE last_history_id="+hid);

				if ( a.length == 0 )
					hid = database.execute("SELECT id FROM lgd_map_resource_k_history WHERE history_id="+hid)[0][0].toString();
				else
					break;
			}

			String k = a[0][0].toString();
			String property = a[0][1].toString();
			String object = a[0][2].toString();
			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES(DEFAULT, '" + k + "', '" + property + "', '" + object + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'restore', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "', " + hid + ") RETURNING id");

			database.execute("UPDATE lgd_map_resource_k set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND object='" + object + "' AND property='" + property + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			if ( object.equals("") && property.equals("") )
				re = "Deleted K-Mapping successfully restored.";
			else
				re = "Edited K-Mapping successfully restored.";
		}//#########################################################################
		else if ( request.getParameter("kvmappingedit") != null && request.getParameter("kvmappingedit").equals("Restore") && request.getParameter("id") != null && request.getParameter("k") != null && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a;
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			String hid = request.getParameter("id");
			while ( true ) {
				a = database.execute("SELECT k, v, property, object FROM lgd_map_resource_kv WHERE last_history_id="+hid);

				if ( a.length == 0 )
					hid = database.execute("SELECT id FROM lgd_map_resource_kv_history WHERE history_id="+hid)[0][0].toString();
				else
					break;
			}

			String k = a[0][0].toString();
			String v = a[0][1].toString();
			String property = a[0][2].toString();
			String object = a[0][3].toString();
			a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES(DEFAULT, '" + k + "', '" + v + "', '" + property + "', '" + object + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'restore', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "', " + hid + ") RETURNING id");

			database.execute("UPDATE lgd_map_resource_kv set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND v='" + v + "' AND object='" + object + "' AND property='" + property + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			if ( object.equals("") && property.equals("") )
				re = "Deleted KV-Mapping successfully restored.";
			else
				re = "Edited KV-Mapping successfully restored.";
		}//#########################################################################
		else if ( request.getParameter("dmapping") != null && request.getParameter("dmapping").equals("Save") && request.getParameter("k") != null && request.getParameter("datatype") != null && request.getParameter("adatatype") != null && request.getParameter("comment") != null ) {
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				Object[][] a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_datatype WHERE k='" + request.getParameter("k") + "' AND datatype='" + request.getParameter("adatatype") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_datatype_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("adatatype") + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'edit', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_datatype VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("datatype") + "', '" + username + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_datatype SET datatype='" + request.getParameter("datatype") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND datatype='" + request.getParameter("adatatype") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			re = "Datatype-Mapping successfully changed.";
		}//#########################################################################
		else if ( request.getParameter("dmapping") != null && request.getParameter("dmapping").equals("Commit") && request.getParameter("k") != null && request.getParameter("datatype") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT last_history_id, datatype FROM lgd_map_datatype WHERE k='" + request.getParameter("k") + "' AND user_id='main'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_datatype_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + (hid == -1 ? "deleted" : a[0][1]) + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'commit', 'main'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_datatype VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("datatype") + "', 'main', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_datatype SET datatype='" + request.getParameter("datatype") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND user_id='main'");

			/*** If you want to delete everything from a userspace after a commit ***/
			//database.execute("DELETE FROM lgd_map_datatype WHERE k='" + request.getParameter("k") + "' AND user_id='" + User.getInstance().getUsername() + "'");
			//database.execute("DELETE FROM lgd_map_datatype_history WHERE k='" + request.getParameter("k") + "' AND userspace='" + User.getInstance().getUsername() + "'");

			re = "Datatype-Mapping successfully commited.";
		}//#########################################################################
		else if ( request.getParameter("dmapping") != null && request.getParameter("dmapping").equals("Delete") && request.getParameter("k") != null && request.getParameter("datatype") != null && request.getParameter("comment") != null ) {
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				Object[][] a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_datatype WHERE k='" + request.getParameter("k") + "' AND datatype='" + request.getParameter("datatype") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_datatype_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("datatype") + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'deleted', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_datatype VALUES ('" + request.getParameter("k") + "', 'deleted', '" + username + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_datatype set datatype='deleted', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND datatype='" + request.getParameter("datatype") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			re = "Datatype-Mapping successfully deleted.";
		}//#########################################################################
		else if ( request.getParameter("dmapping") != null && request.getParameter("dmapping").equals("Create") && request.getParameter("k") != null && request.getParameter("datatype") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("INSERT INTO lgd_map_datatype_history VALUES (DEFAULT, '" + request.getParameter("k") + "', 'deleted', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'create', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + ") RETURNING id");
			database.execute("INSERT INTO lgd_map_datatype VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("datatype") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");

			re = "Datatype-Mapping successfully created.";
		}//#########################################################################
		else if ( request.getParameter("dmappingedit") != null && request.getParameter("dmappingedit").equals("Restore") && request.getParameter("id") != null && request.getParameter("k") != null && request.getParameter("datatype") != null && request.getParameter("comment") != null ) {
			Object[][] a;
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			String hid = request.getParameter("id");
			while ( true ) {
				a = database.execute("SELECT k, datatype FROM lgd_map_datatype WHERE last_history_id="+hid);

				if ( a.length == 0 )
					hid = database.execute("SELECT id FROM lgd_map_datatype_history WHERE history_id="+hid)[0][0].toString();
				else
					break;
			}

			String k = a[0][0].toString();
			String datatype = a[0][1].toString();
			a = database.execute("INSERT INTO lgd_map_datatype_history VALUES(DEFAULT, '" + k + "', '" + datatype + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'restore', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "', " + hid + ") RETURNING id");

			database.execute("UPDATE lgd_map_datatype set datatype='" + request.getParameter("datatype") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND datatype='" + datatype + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			if ( datatype.equals("deleted") )
				re = "Deleted Datatype-Mapping successfully restored.";
			else
				re = "Edited Datatype-Mapping successfully restored.";
		}//#########################################################################
		else if ( request.getParameter("lmapping") != null && request.getParameter("lmapping").equals("Save") && request.getParameter("k") != null && request.getParameter("language") != null && request.getParameter("property") != null && request.getParameter("alanguage") != null && request.getParameter("aproperty") != null && request.getParameter("comment") != null ) {
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				Object[][] a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_literal WHERE k='" + request.getParameter("k") + "' AND property='" + request.getParameter("aproperty") + "' AND language='" + request.getParameter("alanguage") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_literal_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("aproperty") + "', '" + request.getParameter("alanguage") + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'edit', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_literal VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("language") + "', '" + username + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_literal set language='" + request.getParameter("language") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND language='" + request.getParameter("alanguage") + "' AND property='" + request.getParameter("aproperty") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			re = "Literal-Mapping successfully changed.";
		}//#########################################################################
		else if ( request.getParameter("lmapping") != null && request.getParameter("lmapping").equals("Commit") && request.getParameter("k") != null && request.getParameter("language") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT last_history_id, property, language FROM lgd_map_literal WHERE k='" + request.getParameter("k") + "' AND user_id='main'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_literal_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + (hid == -1 ? "" : a[0][1]) + "', '" + (hid == -1 ? "" : a[0][2]) + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'commit', 'main'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_literal VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("language") + "', 'main', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_literal SET language='" + request.getParameter("language") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND user_id='main'");

			/*** If you want to delete everything from a userspace after a commit ***/
			//database.execute("DELETE FROM lgd_map_literal WHERE k='" + request.getParameter("k") + "' AND user_id='" + User.getInstance().getUsername() + "'");
			//database.execute("DELETE FROM lgd_map_literal_history WHERE k='" + request.getParameter("k") + "' AND userspace='" + User.getInstance().getUsername() + "'");

			re = "Literal-Mapping successfully commited.";
		}//#########################################################################
		else if ( request.getParameter("lmapping") != null && request.getParameter("lmapping").equals("Delete") && request.getParameter("k") != null && request.getParameter("language") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				Object[][] a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_literal WHERE k='" + request.getParameter("k") + "' AND property='" + request.getParameter("property") + "' AND language='" + request.getParameter("language") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_literal_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("language") + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'deleted', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_literal VALUES ('" + request.getParameter("k") + "', '', '', '" + username + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_literal set language='', property='', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND language='" + request.getParameter("language") + "' AND property='" + request.getParameter("property") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			re = "Literal-Mapping successfully deleted.";
		}//#########################################################################
		else if ( request.getParameter("lmapping") != null && request.getParameter("lmapping").equals("Create") && request.getParameter("k") != null && request.getParameter("language") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("INSERT INTO lgd_map_literal_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '', '', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'create', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + ") RETURNING id");
			database.execute("INSERT INTO lgd_map_literal VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("language") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");

			re = "Literal-Mapping successfully created.";
		}//#########################################################################
		else if ( request.getParameter("lmappingedit") != null && request.getParameter("lmappingedit").equals("Restore") && request.getParameter("id") != null && request.getParameter("k") != null && request.getParameter("property") != null && request.getParameter("language") != null && request.getParameter("comment") != null ) {
			Object[][] a;
			String username;
			if ( request.getParameter("user") != null ) {
				username = request.getParameter("user");
				a = database.execute("SELECT username FROM lgd_user WHERE username='" + username + "'");
				if ( a.length == 0 )
					database.execute("INSERT INTO lgd_user VALUES ('" + username + "', '', '', FALSE, '" + Functions.MAIN_BRANCH + "')");
				User.getInstance().createUser(username, Functions.MAIN_BRANCH, false, false);
				User.getInstance().createCookie(response);
			}
			else
				username = User.getInstance().getUsername();

			String hid = request.getParameter("id");
			while ( true ) {
				a = database.execute("SELECT k, property, language FROM lgd_map_literal WHERE last_history_id="+hid);

				if ( a.length == 0 )
					hid = database.execute("SELECT id FROM lgd_map_literal_history WHERE history_id="+hid)[0][0].toString();
				else
					break;
			}

			String k = a[0][0].toString();
			String property = a[0][1].toString();
			String language = a[0][2].toString();
			a = database.execute("INSERT INTO lgd_map_literal_history VALUES(DEFAULT, '" + k + "', '" + property + "', '" + language + "', '" + username + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'restore', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "', " + hid + ") RETURNING id");

			database.execute("UPDATE lgd_map_literal SET property='" + request.getParameter("property") + "', language='" + request.getParameter("language") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND property='" + property + "' AND language='" + language + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : username) + "'");

			if ( property.equals("") )
				re = "Deleted Literal-Mapping successfully restored.";
			else
				re = "Edited Literal-Mapping successfully restored.";
		}//#########################################################################
		else if ( request.getParameter("userspace") != null && request.getParameter("userspace").equals("Save") && request.getParameter("branch") != null ) {
			User.getInstance().updateView(request.getParameter("branch"));

			re = "Working branch successfully changed.";
		}//#########################################################################
		else if ( request.getParameter("userspace") != null && request.getParameter("userspace").equals("Reset") ) {
			database.execute("DELETE FROM lgd_map_resource_k WHERE user_id='" + User.getInstance().getUsername() + "'");
			database.execute("DELETE FROM lgd_map_resource_k_history WHERE userspace='" + User.getInstance().getUsername() + "'");
			database.execute("DELETE FROM lgd_map_resource_kv WHERE user_id='" + User.getInstance().getUsername() + "'");
			database.execute("DELETE FROM lgd_map_resource_kv_history WHERE userspace='" + User.getInstance().getUsername() + "'");
			database.execute("DELETE FROM lgd_map_literal WHERE user_id='" + User.getInstance().getUsername() + "'");
			database.execute("DELETE FROM lgd_map_literal_history WHERE userspace='" + User.getInstance().getUsername() + "'");
			database.execute("DELETE FROM lgd_map_datatype WHERE user_id='" + User.getInstance().getUsername() + "'");
			database.execute("DELETE FROM lgd_map_datatype_history WHERE userspace='" + User.getInstance().getUsername() + "'");

			re = "Userbranch successfully reseted.";
		}//#########################################################################
		else if ( request.getParameter("password") != null && request.getParameter("password").equals("Save") && request.getParameter("old") != null && request.getParameter("new") != null && request.getParameter("new2") != null ) {
			if ( !request.getParameter("new").equals(request.getParameter("new2")) )
				return "The two passwords do not match.";

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(request.getParameter("old").getBytes());

			byte[] byteData = md.digest();
			//convert the byte to hex
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			Object[][] a = database.execute("SELECT email FROM lgd_user WHERE username='" + User.getInstance().getUsername() + "' AND password='" + sb + "'");

			md.reset();
			md.update(request.getParameter("new").getBytes());
			byteData = md.digest();
			//convert the byte to hex
			sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			if ( a.length == 0 ) {
				re = "Password incorrect.";
			}
			else {
				database.execute("UPDATE lgd_user SET password='" + sb + "' WHERE email='" + a[0][0] + "'");
				re = "Password successfully changed.";
			}
		}//#########################################################################
		else if ( request.getParameter("email") != null && request.getParameter("email").equals("Save") && request.getParameter("password") != null && request.getParameter("new") != null ) {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(request.getParameter("password").getBytes());

			byte[] byteData = md.digest();
			//convert the byte to hex
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			Object[][] a = database.execute("SELECT email, username FROM lgd_user WHERE username='" + User.getInstance().getUsername() + "' AND password='" + sb + "'");

			if ( a.length == 0 ) {
				re = "Password incorrect.";
			}
			else {
				database.execute("UPDATE lgd_user SET email='" + request.getParameter("new") + "' WHERE email='" + a[0][0] + "'");
				User.getInstance().createUser(request.getParameter("new"), User.getInstance().getView(), true, User.getInstance().isAdmin());
				re = "Email successfully changed.";
			}
		}//#########################################################################
		else if ( request.getParameter("signup") != null && request.getParameter("signup").equals("Sign up") && request.getParameter("user") != null && request.getParameter("email") != null && request.getParameter("password") != null && request.getParameter("password2") != null ) {
			if ( !request.getParameter("password").equals(request.getParameter("password2")) )
				return "The two passwords do not match.";

			Object[][] a = database.execute("SELECT username, email FROM lgd_user WHERE username='" + request.getParameter("user") + "'");
			boolean update = false;

			if ( a.length != 0 ) {
				if ( a[0][1].equals("") )
					update = true;
				else
					return "Your username exists already.";
			}

			a = database.execute("SELECT email FROM lgd_user WHERE email='" + request.getParameter("email") + "'");
			if ( a.length != 0 )
				return "Your Email exists already.";

			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(request.getParameter("password").getBytes());

			byte[] byteData = md.digest();
			//convert the byte to hex
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			if ( update )
				database.execute("UPDATE lgd_user SET email='" + request.getParameter("email") + "', password='" + sb + "', admin=FALSE, view='lgd_user_" + request.getParameter("user") + "' WHERE username='" + request.getParameter("user") + "'");
			else
				database.execute("INSERT INTO lgd_user VALUES ('" + request.getParameter("user") + "', '" + request.getParameter("email") + "', '" + sb + "', FALSE, 'lgd_user_" + request.getParameter("user") + "')");
			database.execute(Functions.createView(request.getParameter("user")));
			database.execute(Functions.createViewHistory(request.getParameter("user")));
			database.execute(Functions.createViewUnmapped(request.getParameter("user")));

			User.getInstance().createUser(request.getParameter("user"), "lgd_user_" + request.getParameter("email"), true, false);
			User.getInstance().createCookie(response);
			re = "Sign up successful.";
		}//#########################################################################
		if ( (request.getParameter("commitK") != null && request.getParameter("comment") != null) || (request.getParameter("commitAll") != null && request.getParameter("comment") != null) ) {
			Object[][] b = database.execute("SELECT k, property, object FROM lgd_map_resource_k WHERE user_id = '" + User.getInstance().getUsername() + "' AND (k, property, object) NOT IN (SELECT k, property, object FROM lgd_map_resource_k WHERE user_id='main')");

			for ( Object[] k : b ) {
				Object[][] a = database.execute("SELECT last_history_id, property, object FROM lgd_map_resource_k WHERE k='" + k[0] + "' AND user_id='main'");

				int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
				a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES (DEFAULT, '" + k[0] + "', '" + (hid == -1 ? "" : a[0][1]) + "', '" + (hid == -1 ? "" : a[0][2]) + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'commit', 'main'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

				if ( hid == -1 )
					database.execute("INSERT INTO lgd_map_resource_k VALUES ('" + k[0] + "', '" + k[1] + "', '" + k[2] + "', 'main', " + a[0][0] + ")");
				else
					database.execute("UPDATE lgd_map_resource_k SET object='" + k[2] + "', property='" + k[1] + "', last_history_id=" + a[0][0] + " WHERE  k='" + k[0] + "' AND user_id='main'");
			}

			re = "All K-Mappings successfully commited.";
		}//#########################################################################
		if ( (request.getParameter("commitKV") != null && request.getParameter("comment") != null) || (request.getParameter("commitAll") != null && request.getParameter("comment") != null) ) {
			Object[][] b = database.execute("SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id = '" + User.getInstance().getUsername() + "' AND (k, v, property, object) NOT IN (SELECT k, v, property, object FROM lgd_map_resource_kv WHERE user_id='main')");

			for ( Object[] k : b ) {
				Object[][] a = database.execute("SELECT last_history_id, property, object FROM lgd_map_resource_kv WHERE k='" + k[0] + "' AND v='" + k[1] + "' AND user_id='main'");

				int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
				a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES (DEFAULT, '" + k[0] + "', '" + k[1] + "', '" + (hid == -1 ? "" : a[0][1]) + "', '" + (hid == -1 ? "" : a[0][2]) + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'commit', 'main'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

				if ( hid == -1 )
					database.execute("INSERT INTO lgd_map_resource_kv VALUES ('" + k[0] + "', '" + k[1] + "', '" + k[2] + "', '" + k[3] + "', 'main', " + a[0][0] + ")");
				else
					database.execute("UPDATE lgd_map_resource_kv SET object='" + k[3] + "', property='" + k[2] + "', last_history_id=" + a[0][0] + " WHERE  k='" + k[0] + "' AND v='" + k[1] + "' AND user_id='main'");
			}

			re = "All KV-Mappings successfully commited.";
		}//#########################################################################
		if ( (request.getParameter("commitDatatype") != null && request.getParameter("comment") != null) || (request.getParameter("commitAll") != null && request.getParameter("comment") != null) ) {
			Object[][] b = database.execute("SELECT k, datatype FROM lgd_map_datatype WHERE user_id = '" + User.getInstance().getUsername() + "' AND (k, datatype) NOT IN (SELECT k, datatype FROM lgd_map_datatype WHERE user_id='main')");

			for ( Object[] k : b ) {
				Object[][] a = database.execute("SELECT last_history_id, datatype FROM lgd_map_datatype WHERE k='" + k[0] + "' AND user_id='main'");

				int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
				a = database.execute("INSERT INTO lgd_map_datatype_history VALUES (DEFAULT, '" + k[0] + "', '" + (hid == -1 ? "" : a[0][1]) + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'commit', 'main'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

				if ( hid == -1 )
					database.execute("INSERT INTO lgd_map_datatype VALUES ('" + k[0] + "', '" + k[1] + "', 'main', " + a[0][0] + ")");
				else
					database.execute("UPDATE lgd_map_datatype SET datatype='" + k[1] + "', last_history_id=" + a[0][0] + " WHERE  k='" + k[0] + "' AND user_id='main'");
			}

			re = "All Datatype-Mappings successfully commited.";
		}//#########################################################################
		if ( (request.getParameter("commitLiteral") != null && request.getParameter("comment") != null) || (request.getParameter("commitAll") != null && request.getParameter("comment") != null) ) {
			Object[][] b = database.execute("SELECT k, property, language FROM lgd_map_literal WHERE user_id = '" + User.getInstance().getUsername() + "' AND (k, property, language) NOT IN (SELECT k, property, language FROM lgd_map_literal WHERE user_id='main')");

			for ( Object[] k : b ) {
				Object[][] a = database.execute("SELECT last_history_id, property, language FROM lgd_map_literal WHERE k='" + k[0] + "' AND user_id='main'");

				int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
				a = database.execute("INSERT INTO lgd_map_literal_history VALUES (DEFAULT, '" + k[0] + "', '" + (hid == -1 ? "" : a[0][1]) + "', '" + (hid == -1 ? "" : a[0][2]) + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'commit', 'main'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

				if ( hid == -1 )
					database.execute("INSERT INTO lgd_map_literal VALUES ('" + k[0] + "', '" + k[1] + "', '" + k[2] + "', 'main', " + a[0][0] + ")");
				else
					database.execute("UPDATE lgd_map_literal SET language='" + k[2] + "', property='" + k[1] + "', last_history_id=" + a[0][0] + " WHERE  k='" + k[0] + "' AND user_id='main'");
			}

			re = "All Literal-Mappings successfully commited.";
		}//#########################################################################
		if ( request.getParameter("commitAll") != null && request.getParameter("comment") != null ) {
			re = "All Mappings successfully commited.";
		}

		return re;
	}

	public static boolean checkCaptcha(HttpServletRequest request) {
		String remoteAddr = request.getRemoteAddr();
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey(Functions.PRIVATE_reCAPTCHA_KEY);

		String challenge = request.getParameter("recaptcha_challenge_field");
		String uresponse = request.getParameter("recaptcha_response_field");
		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

		if ( reCaptchaResponse.isValid() )
			return true;
		else
			return false;
	}
}