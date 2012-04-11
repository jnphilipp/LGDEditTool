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

import java.util.Calendar;
import java.security.MessageDigest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;
import LGDEditTool.Functions;
import LGDEditTool.db.DatabaseBremen;

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

			Object[][] a = database.execute("SELECT email, admin FROM lgd_user WHERE (username='" + request.getParameter("user") + "' OR email='" + request.getParameter("user") + "') AND password='" + sb + "'");

			if ( a.length == 0 ) {
				User.getInstance().createUser("", false, false);
				re = "Login or password incorrect.";
			}
			else {
				User.getInstance().createUser(a[0][0].toString(), true, Boolean.parseBoolean(a[0][1].toString()));
				User.getInstance().createCookie(response);
				re = "Login successful.";
			}
		}//#########################################################################
		else if ( request.getParameter("logout") != null ) {
			User.getInstance().logout();
			User.getInstance().createCookie(response);
			re = "Logout successful.";
		}//#########################################################################
		else if ( request.getParameter("kmapping") != null && request.getParameter("kmapping").equals("Save") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("aobject") != null && request.getParameter("aproperty") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND property='" + request.getParameter("aproperty") + "' AND object='" + request.getParameter("aobject") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("aproperty") + "', '" + request.getParameter("aobject") + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'edit', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_k VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_k set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND object='" + request.getParameter("aobject") + "' AND property='" + request.getParameter("aproperty") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

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
			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND property='" + request.getParameter("property") + "' AND object='" + request.getParameter("object") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'deleted', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_k VALUES ('" + request.getParameter("k") + "', '', '', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_k set object='', property='', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

			re = "K-Mapping successfully deleted.";
		}//#########################################################################
		else if ( request.getParameter("kmapping") != null && request.getParameter("kmapping").equals("Create") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '', '', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'create', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + ") RETURNING id");
			database.execute("INSERT INTO lgd_map_resource_k VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");

			re = "K-Mapping successfully created.";
		}//#########################################################################
		else if ( request.getParameter("kvmapping") != null && request.getParameter("kvmapping").equals("Save") && request.getParameter("k") != null  && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("aobject") != null && request.getParameter("aproperty") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_resource_kv WHERE k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND property='" + request.getParameter("aproperty") + "' AND object='" + request.getParameter("aobject") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("aproperty") + "', '" + request.getParameter("aobject") + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'edit', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_kv VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_kv set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND object='" + request.getParameter("aobject") + "' AND property='" + request.getParameter("aproperty") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

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
			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_resource_kv WHERE k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND property='" + request.getParameter("property") + "' AND object='" + request.getParameter("object") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'deleted', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_resource_kv VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '', '', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_resource_kv set object='', property='', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

			re = "KV-Mapping successfully deleted.";
		}//#########################################################################
		else if ( request.getParameter("kvmapping") != null && request.getParameter("kvmapping").equals("Create") && request.getParameter("k") != null && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '', '', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'create', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + ") RETURNING id");
			database.execute("INSERT INTO lgd_map_resource_kv VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("property") + "', '" + request.getParameter("object") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");

			re = "KV-Mapping successfully created.";
		}//#########################################################################
		else if ( request.getParameter("kmappingedit") != null && request.getParameter("kmappingedit").equals("Restore") && request.getParameter("id") != null && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a;

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
			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES(DEFAULT, '" + k + "', '" + property + "', '" + object + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'restore', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "', " + hid + ") RETURNING id");

			database.execute("UPDATE lgd_map_resource_k set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND object='" + object + "' AND property='" + property + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

			if ( object.equals("") && property.equals("") )
				re = "Deleted K-Mapping successfully restored.";
			else
				re = "Edited K-Mapping successfully restored.";
		}//#########################################################################
		else if ( request.getParameter("kvmappingedit") != null && request.getParameter("kvmappingedit").equals("Restore") && request.getParameter("id") != null && request.getParameter("k") != null && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a;
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
			a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES(DEFAULT, '" + k + "', '" + v + "', '" + property + "', '" + object + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'restore', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "', " + hid + ") RETURNING id");

			database.execute("UPDATE lgd_map_resource_kv set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND v='" + v + "' AND object='" + object + "' AND property='" + property + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

			if ( object.equals("") && property.equals("") )
				re = "Deleted KV-Mapping successfully restored.";
			else
				re = "Edited KV-Mapping successfully restored.";
		}//#########################################################################
		else if ( request.getParameter("dmapping") != null && request.getParameter("dmapping").equals("Save") && request.getParameter("k") != null && request.getParameter("datatype") != null && request.getParameter("adatatype") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_datatype WHERE k='" + request.getParameter("k") + "' AND datatype='" + request.getParameter("adatatype") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_datatype_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("adatatype") + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'edit', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_datatype VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("datatype") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_datatype SET datatype='" + request.getParameter("datatype") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND datatype='" + request.getParameter("adatatype") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

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
			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_datatype WHERE k='" + request.getParameter("k") + "' AND datatype='" + request.getParameter("datatype") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_datatype_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("datatype") + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'deleted', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_datatype VALUES ('" + request.getParameter("k") + "', 'deleted', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_datatype set datatype='deleted', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND datatype='" + request.getParameter("datatype") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

			re = "Datatype-Mapping successfully deleted.";
		}//#########################################################################
		else if ( request.getParameter("dmapping") != null && request.getParameter("dmapping").equals("Create") && request.getParameter("k") != null && request.getParameter("datatype") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("INSERT INTO lgd_map_datatype_history VALUES (DEFAULT, '" + request.getParameter("k") + "', 'deleted', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'create', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + ") RETURNING id");
			database.execute("INSERT INTO lgd_map_datatype VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("datatype") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");

			re = "Datatype-Mapping successfully created.";
		}//#########################################################################
		else if ( request.getParameter("dmappingedit") != null && request.getParameter("dmappingedit").equals("Restore") && request.getParameter("k") != null && request.getParameter("datatype") != null && request.getParameter("comment") != null ) {
			Object[][] a;
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
			a = database.execute("INSERT INTO lgd_map_datatype_history VALUES(DEFAULT, '" + k + "', '" + datatype + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'restore', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "', " + hid + ") RETURNING id");

			database.execute("UPDATE lgd_map_datatype set datatype='" + request.getParameter("datatype") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND datatype='" + datatype + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

			if ( datatype.equals("deleted") )
				re = "Deleted Datatype-Mapping successfully restored.";
			else
				re = "Edited Datatype-Mapping successfully restored.";
		}//#########################################################################
		else if ( request.getParameter("lmapping") != null && request.getParameter("lmapping").equals("Save") && request.getParameter("k") != null && request.getParameter("language") != null && request.getParameter("property") != null && request.getParameter("alanguage") != null && request.getParameter("aproperty") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_literal WHERE k='" + request.getParameter("k") + "' AND property='" + request.getParameter("aproperty") + "' AND language='" + request.getParameter("alanguage") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_literal_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("aproperty") + "', '" + request.getParameter("alanguage") + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'edit', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");
			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_literal VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("language") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_literal set language='" + request.getParameter("language") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND language='" + request.getParameter("alanguage") + "' AND property='" + request.getParameter("aproperty") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

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
			Object[][] a = database.execute("SELECT last_history_id FROM lgd_map_literal WHERE k='" + request.getParameter("k") + "' AND property='" + request.getParameter("property") + "' AND language='" + request.getParameter("language") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");
			int hid = (a.length == 0 ? -1 : Integer.parseInt(a[0][0] == null ? "-2" : a[0][0].toString()));
			a = database.execute("INSERT INTO lgd_map_literal_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("language") + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'deleted', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + (hid == -1 || hid == -2 ? "" : "," + hid) + ") RETURNING id");

			if ( hid == -1 )
				database.execute("INSERT INTO lgd_map_literal VALUES ('" + request.getParameter("k") + "', '', '', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");
			else
				database.execute("UPDATE lgd_map_literal set language='', property='', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND language='" + request.getParameter("language") + "' AND property='" + request.getParameter("property") + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

			re = "Literal-Mapping successfully deleted.";
		}//#########################################################################
		else if ( request.getParameter("lmapping") != null && request.getParameter("lmapping").equals("Create") && request.getParameter("k") != null && request.getParameter("language") != null && request.getParameter("property") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("INSERT INTO lgd_map_literal_history VALUES (DEFAULT, '" + request.getParameter("k") + "', '', '', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'create', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'" + ") RETURNING id");
			database.execute("INSERT INTO lgd_map_literal VALUES ('" + request.getParameter("k") + "', '" + request.getParameter("property") + "', '" + request.getParameter("language") + "', '" + User.getInstance().getUsername() + "', " + a[0][0] + ")");

			re = "Literal-Mapping successfully created.";
		}//#########################################################################
		else if ( request.getParameter("lmappingedit") != null && request.getParameter("lmappingedit").equals("Restore") && request.getParameter("k") != null && request.getParameter("property") != null && request.getParameter("language") != null && request.getParameter("comment") != null ) {
			Object[][] a;
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
			a = database.execute("INSERT INTO lgd_map_literal_history VALUES(DEFAULT, '" + k + "', '" + property + "', '" + language + "', '" + User.getInstance().getUsername() + "','" + request.getParameter("comment") + "', '" + Functions.getTimestamp() + "', 'restore', '" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "', " + hid + ") RETURNING id");

			database.execute("UPDATE lgd_map_literal SET property='" + request.getParameter("property") + "', language='" + request.getParameter("language") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND property='" + property + "' AND language='" + language + "' AND user_id='" + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "main" : User.getInstance().getUsername()) + "'");

			if ( property.equals("") )
				re = "Deleted Literal-Mapping successfully restored.";
			else
				re = "Edited Literal-Mapping successfully restored.";
		}//#########################################################################
		else if ( request.getParameter("userspace") != null && request.getParameter("userspace").equals("Save") && request.getParameter("branch") != null ) {
			User.getInstance().updateView(request.getParameter("branch"));

			re = "Working branch successfully changed.";
		}

		return re;
	}
}