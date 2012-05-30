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

package LGDEditTool.Email;

import LGDEditTool.Functions;
import LGDEditTool.db.LGDDatabase;
import java.sql.SQLException;
import javax.mail.MessagingException;


/**
 * 
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class EmailLGD extends EmailYahoo {
	private static EmailLGD email;

	/**
	 * Constructor.
	 */
	public EmailLGD() {
		super();
	}

	/**
	 * Returns an instance of the class. If none exists it will be created.
	 * @return instance
	 * @throws ClassNotFoundException 
	 */
	public static EmailLGD getInstance() {
		if ( email == null )
			email = new EmailLGD();

		return email;
	}

	/**
	 * Set the values of the sender.
	 */
	public void setProperties() {
		super.setProperties("lgdedittool@yahoo.de", "fakultaet");
	}

	/**
	 * Sends an email containing a password forgotten message to the given user.
	 * @param to receiver of the mail
	 * @param user username
	 * @param hash hash of the new password
	 * @return Returns <code>ture</code> after sending the mail.
	 * @throws MessagingException
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	public boolean sendPasswordForgotten(String to, String user, String hash) throws MessagingException, ClassNotFoundException, SQLException {
		String message = "Dear " + user + ",<br /><br />You requested a new password for your LGDEditTool account. Click the following link to create a new password for your account. If you did not request a new password, please ignore this notification.<br /><br /><a href=\"" + Functions.baseURL + "?tab=forgotten&type=hash&user=" + user + "&hash=" + hash + "\">" + Functions.baseURL + "?tab=forgotten&type=hash&user=" + user + "&hash=" + hash + "</a><br /><br /><a href=\"" + Functions.baseURL + "\">LGDEditTool</a><br /><br />";

		LGDDatabase.getInstance().connect();
		String[][] admin = LGDDatabase.getInstance().getAdministatorEmailAddresses();
		if ( admin.length == 1 )
			message +=  "For questions or in case of any problems feel free to contact the administrator " + admin[0][1] + " (" + admin[0][0] + ").";
		else {
			message +=  "For questions or in case of any problems feel free to contact one of the administrators:<br />";
			for ( int i = 0; i < admin.length; i++ )
				message += admin[i][1] + " (" + admin[i][0] + ")<br />" ;
		}
		
		return super.send(to, "Password Request", message, true);
	}

	/**
	 * Sends an email containing a welcome message to the given user.
	 * @param to receiver of the email
	 * @param user username
	 * @return Returns <code>ture</code> after sending the mail.
	 * @throws MessagingException
	 * @throws ClassNotFoundException
	 * @throws SQLException 
	 */
	public boolean sendSignUP(String to, String user) throws MessagingException, ClassNotFoundException, SQLException {
		String message = "Dear " + user + ",<br /><br />your user account for the LGDEditTool has successfully been created. Your edits to the LGD mapping will be saved to your user branch. Commit them to the main branch to make them available to other users.";

		LGDDatabase.getInstance().connect();
		String[][] admin = LGDDatabase.getInstance().getAdministatorEmailAddresses();
		if ( admin.length == 1 )
			message +=  "For questions or in case of any problems feel free to contact the administrator " + admin[0][1] + " (" + admin[0][0] + ").";
		else {
			message +=  "For questions or in case of any problems feel free to contact one of the administrators:<br />";
			for ( int i = 0; i < admin.length; i++ )
				message += admin[i][1] + " (" + admin[i][0] + ")<br />" ;
		}

		
		return super.send(to, "Welcome to LGDEditTool", message, true);
	}
}