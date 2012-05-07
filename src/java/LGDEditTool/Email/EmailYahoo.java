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

import javax.mail.MessagingException;


/**
 * Class for sending an email from a Yahoo address.
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class EmailYahoo extends Email {
	/**
	 * Constructor.
	 */
	public EmailYahoo() {
		super();
	}

	/**
	 * Set the values of the sender.
	 * @param from email address of the sender
	 * @param password password
	 */
	public void setProperties(String from, String password) {
		super.setProperties("smtp.mail.yahoo.com", "465", from, password, false, true);
	}
}