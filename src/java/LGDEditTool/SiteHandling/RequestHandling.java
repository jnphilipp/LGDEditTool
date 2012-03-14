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
	public static String doRequestHandling(HttpServletRequest request, HttpServletResponse response, User user) throws Exception {
		DatabaseBremen database = new DatabaseBremen();
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
				user.createUser("", false, false);
				re = "Login or password incorrect.";
			}
			else {
				user.createUser(a[0][0].toString(), true, Boolean.parseBoolean(a[0][1].toString()));
				user.createCookie(response);
				re = "Login successful.";
			}
		}
		else if ( request.getParameter("logout") != null ) {
			user.logout();
			user.createCookie(response);
			re = "Logout successful.";
		}///////////////////////////////////////////////////////////////////////////
		else if ( request.getParameter("kmapping") != null && request.getParameter("kmapping").equals("Save") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("aobject") != null && request.getParameter("aproperty") != null && request.getParameter("user") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT email FROM lgd_user WHERE email='" + request.getParameter("user") + "' OR username='" + request.getParameter("user") + "'");
			if (a.length == 0 )
				a = database.execute("INSERT INTO lgd_user (email, admin) VALUES ('" + request.getParameter("user") + "', FALSE) RETURNING email");

			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES ((SELECT MAX(id) + 1 FROM lgd_map_resource_k_history), '" + request.getParameter("k") + "', '" + request.getParameter("object") + "', '" + request.getParameter("property") + "', '" + a[0][0] + "','" + request.getParameter("comment") + "', '" +  Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)) + "', (SELECT last_history_id FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND object='" + request.getParameter("aobject") + "' AND property='" + request.getParameter("aproperty") + "')) RETURNING id");

			database.execute("UPDATE lgd_map_resource_k set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND object='" + request.getParameter("aobject") + "' AND property='" + request.getParameter("aproperty") + "'");

			re = "K-Mapping successfully changed.";
		}///////////////////////////////////////////////////////////////////////////
		else if ( request.getParameter("kmapping") != null && request.getParameter("kmapping").equals("Delete") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("user") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT email FROM lgd_user WHERE email='" + request.getParameter("user") + "' OR username='" + request.getParameter("user") + "'");
			if (a.length == 0 )
				a = database.execute("INSERT INTO lgd_user (email, admin) VALUES ('" + request.getParameter("user") + "', FALSE) RETURNING email");

			a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES ((SELECT MAX(id) + 1 FROM lgd_map_resource_k_history), '" + request.getParameter("k") + "', '" + request.getParameter("object") + "', '" + request.getParameter("property") + "', '" + a[0][0] + "','" + request.getParameter("comment") + "', '" +  Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)) + "', (SELECT last_history_id FROM lgd_map_resource_k WHERE k='" + request.getParameter("k") + "' AND object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "')) RETURNING id");

			database.execute("DELETE FROM lgd_map_resource_k WHERE object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "' AND k='" + request.getParameter("k") + "'");

			re = "K-Mapping successfully deleted.";
		}///////////////////////////////////////////////////////////////////////
		else if ( request.getParameter("kvmapping") != null && request.getParameter("kvmapping").equals("Save") && request.getParameter("k") != null  && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("aobject") != null && request.getParameter("aproperty") != null && request.getParameter("user") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT email FROM lgd_user WHERE email='" + request.getParameter("user") + "' OR username='" + request.getParameter("user") + "'");
			if (a.length == 0 )
				a = database.execute("INSERT INTO lgd_user (email, admin) VALUES ('" + request.getParameter("user") + "', FALSE) RETURNING email");

			a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES ((SELECT MAX(id) + 1 FROM lgd_map_resource_kv_history), '" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("object") + "', '" + request.getParameter("property") + "', '" + a[0][0] + "','" + request.getParameter("comment") + "', '" +  Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)) + "', (SELECT last_history_id FROM lgd_map_resource_kv WHERE k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND object='" + request.getParameter("aobject") + "' AND property='" + request.getParameter("aproperty") + "')) RETURNING id");

			database.execute("UPDATE lgd_map_resource_kv set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND object='" + request.getParameter("aobject") + "' AND property='" + request.getParameter("aproperty") + "'");

			re = "KV-Mapping successfully changed.";
		}///////////////////////////////////////////////////////////////////////////
		else if ( request.getParameter("kvmapping") != null && request.getParameter("kvmapping").equals("Delete") && request.getParameter("k") != null && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("user") != null && request.getParameter("comment") != null ) {
			Object[][] a = database.execute("SELECT email FROM lgd_user WHERE email='" + request.getParameter("user") + "' OR username='" + request.getParameter("user") + "'");
			if (a.length == 0 )
				a = database.execute("INSERT INTO lgd_user (email, admin) VALUES ('" + request.getParameter("user") + "', FALSE) RETURNING email");

			a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES ((SELECT MAX(id) + 1 FROM lgd_map_resource_kv_history), '" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("object") + "', '" + request.getParameter("property") + "', '" + a[0][0] + "','" + request.getParameter("comment") + "', '" +  Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)) + "', (SELECT last_history_id FROM lgd_map_resource_kv WHERE k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "')) RETURNING id");

			database.execute("DELETE FROM lgd_map_resource_kv WHERE object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "' AND k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "'");

			re = "KV-Mapping successfully deleted.";
		}///////////////////////////////////////////////////////////////////////////
		else if ( request.getParameter("kmappingedit") != null && request.getParameter("kmappingedit").equals("Restore") && request.getParameter("k") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("user") != null && request.getParameter("auser") != null && request.getParameter("comment") != null && request.getParameter("acomment") != null && request.getParameter("timestamp") != null ) {
			Object[][] a = database.execute("SELECT email FROM lgd_user WHERE email='" + request.getParameter("user") + "' OR username='" + request.getParameter("user") + "'");
			if (a.length == 0 )
				a = database.execute("INSERT INTO lgd_user (email, admin) VALUES ('" + request.getParameter("user") + "', FALSE) RETURNING email");
			String user_id = a[0][0].toString();

			a = database.execute("SELECT id FROM lgd_map_resource_k_history WHERE k='" + request.getParameter("k") + "' AND object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "' AND timestamp='" + request.getParameter("timestamp") + "' AND user_id='" + request.getParameter("auser") + "' AND comment='" + request.getParameter("acomment") + "'");
			String hid = a[0][0].toString();
			while ( true ) {
				a = database.execute("SELECT k, object, property FROM lgd_map_resource_k WHERE last_history_id="+hid);

				if ( a.length == 0 ) {
					a = database.execute("SELECT id FROM lgd_map_resource_k_history WHERE history_id="+hid);
					if ( a.length == 0 )
						break;
					else
						hid = a[0][0].toString();
				}
				else
					break;
			}

			if ( a.length == 0 ) {
				a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES((SELECT MAX(id) + 1 FROM lgd_map_resource_k_history), '', '', '', '" + user_id + "', '" + request.getParameter("comment") + "', '" +  Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)) + "', " + hid + ") RETURNING id");

				database.execute("INSERT INTO lgd_map_resource_k VALUES('" + request.getParameter("k") + "', '" + request.getParameter("object") + "', '" + request.getParameter("property") + "', " + a[0][0] + ")");

				re = "Deleted K-Mapping successfully restored.";
			}
			else {
				String k = a[0][0].toString();
				String object = a[0][1].toString();
				String property = a[0][2].toString();
				a = database.execute("INSERT INTO lgd_map_resource_k_history VALUES((SELECT MAX(id) + 1 FROM lgd_map_resource_k_history), '" + k + "', '" + object + "', '" + property + "', '" + user_id + "','" + request.getParameter("comment") + "', '" +  Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)) + "', " + hid + ") RETURNING id");

				database.execute("UPDATE lgd_map_resource_k set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND object='" + object + "' AND property='" + property + "'");

				re = "Edited K-Mapping successfully restored.";
			}
		}///////////////////////////////////////////////////////////////////////////
		else if ( request.getParameter("kvmappingedit") != null && request.getParameter("kvmappingedit").equals("Restore") && request.getParameter("k") != null && request.getParameter("v") != null && request.getParameter("object") != null && request.getParameter("property") != null && request.getParameter("user") != null && request.getParameter("auser") != null && request.getParameter("comment") != null && request.getParameter("acomment") != null && request.getParameter("timestamp") != null ) {
			Object[][] a = database.execute("SELECT email FROM lgd_user WHERE email='" + request.getParameter("user") + "' OR username='" + request.getParameter("user") + "'");
			if (a.length == 0 )
				a = database.execute("INSERT INTO lgd_user (email, admin) VALUES ('" + request.getParameter("user") + "', FALSE) RETURNING email");
			String user_id = a[0][0].toString();

			a = database.execute("SELECT id FROM lgd_map_resource_kv_history WHERE k='" + request.getParameter("k") + "' AND v='" + request.getParameter("v") + "' AND object='" + request.getParameter("object") + "' AND property='" + request.getParameter("property") + "' AND timestamp='" + request.getParameter("timestamp") + "' AND user_id='" + request.getParameter("auser") + "' AND comment='" + request.getParameter("acomment") + "'");
			String hid = a[0][0].toString();
			while ( true ) {
				a = database.execute("SELECT k, v, object, property FROM lgd_map_resource_kv WHERE last_history_id="+hid);

				if ( a.length == 0 ) {
					a = database.execute("SELECT id FROM lgd_map_resource_kv_history WHERE history_id="+hid);
					if ( a.length == 0 )
						break;
					else
						hid = a[0][0].toString();
				}
				else
					break;
			}

			if ( a.length == 0 ) {
				a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES((SELECT MAX(id) + 1 FROM lgd_map_resource_kv_history), '', '', '', '', '" + user_id + "','" + request.getParameter("comment") + "', '" +  Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)) + "', " + hid + ") RETURNING id");

				database.execute("INSERT INTO lgd_map_resource_kv VALUES('" + request.getParameter("k") + "', '" + request.getParameter("v") + "', '" + request.getParameter("object") + "', '" + request.getParameter("property") + "', " + a[0][0] + ")");

				re = "Deleted KV-Mapping successfully restored.";
			}
			else {
				String k = a[0][0].toString();
				String v = a[0][1].toString();
				String object = a[0][2].toString();
				String property = a[0][3].toString();
				a = database.execute("INSERT INTO lgd_map_resource_kv_history VALUES((SELECT MAX(id) + 1 FROM lgd_map_resource_kv_history), '" + k + "', '" + v + "', '" + object + "', '" + property + "', '" + user_id + "','" + request.getParameter("comment") + "', '" +  Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)) + "', " + hid + ") RETURNING id");

				database.execute("UPDATE lgd_map_resource_kv set object='" + request.getParameter("object") + "', property='" + request.getParameter("property") + "', last_history_id=" + a[0][0] + " WHERE  k='" + k + "' AND v='" + v + "' AND object='" + object + "' AND property='" + property + "'");

				re = "Edited KV-Mapping successfully restored.";
			}
		}

		database.disconnect();
		return re;
	}

	public static boolean checkCaptcha(HttpServletRequest request) {
		String remoteAddr = request.getRemoteAddr();
		ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
		reCaptcha.setPrivateKey("6LcryM4SAAAAAKHGFwoD1t-tQsWB_QGuNInVNYbp");

		String challenge = request.getParameter("recaptcha_challenge_field");
		String uresponse = request.getParameter("recaptcha_response_field");
		ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

		if ( reCaptchaResponse.isValid() ) {
			return true;
		}
		else {
			return false;
		}
	}
}