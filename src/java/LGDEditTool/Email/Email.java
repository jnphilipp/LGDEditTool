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

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class for sending an email.
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class Email {
	/**
	 * Holds the information, like host, port, from, password ssl/starttls.
	 */
	private Properties properties;

	/**
	 * Constructor
	 */
	public Email() {
		this.properties = System.getProperties();
	}

	/**
	 * Set the values of the sender.
	 * @param host host
	 * @param port port
	 * @param from email address of the sender
	 * @param password password
	 * @param starttls use starttls
	 * @param ssl use ssl
	 */
	public void setProperties(String host, String port, String from, String password, boolean starttls, boolean ssl) {
		if ( starttls )
			this.properties.put("mail.smtp.starttls.enable", "true");
		if ( ssl ) {
			this.properties.put("mail.smtp.socketFactory.port", "465");
			this.properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		}
		this.properties.put("mail.smtp.host", host);
		this.properties.put("mail.smtp.user", from);
		this.properties.put("mail.smtp.password", password);
		this.properties.put("mail.smtp.port", port);
		this.properties.put("mail.smtp.auth", "true");
	}

	/**
	 * Sends an email with the given values.
	 * @param to receiver of the email
	 * @param subject subject
	 * @param text mail text
	 * @param html send as html message
	 * @return Returns <code>ture</code> after sending the mail.
	 * @throws MessagingException 
	 */
	public boolean send(String to, String subject, String text, boolean html) throws MessagingException {
		Session session = Session.getDefaultInstance(this.properties, null);

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(this.properties.getProperty("mail.smtp.user")));
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		message.setSubject(subject);
		if ( html )
			message.setContent(text, "text/html");
		else
			message.setText(text);

		Transport transport = session.getTransport("smtp");
		transport.connect(this.properties.getProperty("mail.smtp.host"), this.properties.getProperty("mail.smtp.user"), this.properties.getProperty("mail.smtp.password"));
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

		return true;
	}
}