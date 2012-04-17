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
package LGDEditTool.Templates;

import LGDEditTool.Functions;
import LGDEditTool.SiteHandling.User;

/**
 *
 * @author J. Nathanael Philipp
 * @version 2.0
 */
public class TemplatesAccountSettings {
	public static String accountSettings(String setting, String search) {
		String re = "";

		re += "\t\t\t\t<section class=\"account\">\n";
		re += "\t\t\t\t\t<aside>\n";
		re += "\t\t\t\t\t\t<ul>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=account&setting=password\">Change Password</a></li>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=account&setting=email\">Change Email</a></li>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=account&setting=branch\">Switch Working Branch</a></li>\n";
		re += "\t\t\t\t\t\t\t<li><a href=\"?tab=account&setting=reset\">Reset Userbranch</a></li>\n";
		re += "\t\t\t\t\t\t</ul>\n";
		re += "\t\t\t\t\t</aside>\n";

		if ( setting.equals("start") ) {
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t\t<p>Please choose the setting you wish to change.</p>\n";
			re += "\t\t\t\t\t</article>\n";
		}
		else if ( setting.equals("password") ) {
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t\t<fieldset style=\"width: 25em;\">\n";
			re += "\t\t\t\t\t\t\t<legend>Change Password</legend>\n";
			re += "\t\t\t\t\t\t\t<form action=\"?tab=account&setting=password" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t\t<ul>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>Old password:</label><input type=\"text\" name=\"old\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>New password:</label><input type=\"text\" name=\"new\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>Confirm New Password:</label><input type=\"text\" name=\"new2\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"change\" value=\"Save\" /></li>\n";
			re += "\t\t\t\t\t\t\t\t</ul>\n";
			re += "\t\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t\t</fieldset>\n";
			re += "\t\t\t\t\t</article>\n";
		}
		else if ( setting.equals("email") ) {
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t\t<fieldset style=\"width: 25em;\">\n";
			re += "\t\t\t\t\t\t\t<legend>Change Email</legend>\n";
			re += "\t\t\t\t\t\t\t<form action=\"?tab=account&setting=email" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t\t<ul>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>New Email:</label><input type=\"text\" name=\"email\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>Password:</label><input type=\"text\" name=\"password\" required /></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"change\" value=\"Save\" /></li>\n";
			re += "\t\t\t\t\t\t\t\t</ul>\n";
			re += "\t\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t\t</fieldset>\n";
			re += "\t\t\t\t\t</article>\n";
		}
		else if ( setting.equals("branch") ) {
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t\t<fieldset style=\"width: 25em;\">\n";
			re += "\t\t\t\t\t\t\t<legend>Switch Working Branch</legend>\n";
			re += "\t\t\t\t\t\t\t<form action=\"?tab=account&setting=branch" + (search.equals("") ? "" : "&search=" + search) + "\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t\t<ul>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><label>Branches:</label></li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li>\n";
			re += "\t\t\t\t\t\t\t\t\t\t<div class=\"select\">";
			re += "\t\t\t\t\t\t\t\t\t\t\t<select name=\"branch\">\n";
			re += "\t\t\t\t\t\t\t\t\t\t\t\t<option value=\"user\" " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "" : "selected") + ">Your user branch.</option>\n";
			re += "\t\t\t\t\t\t\t\t\t\t\t\t<option value=\"main\" " + (User.getInstance().getView().equals(Functions.MAIN_BRANCH) ? "selected" : "") + ">The main branch.</option>\n";
			re += "\t\t\t\t\t\t\t\t\t\t\t</select>\n";
			re += "\t\t\t\t\t\t\t\t\t\t</div>\n";
			re += "\t\t\t\t\t\t\t\t\t</li>\n";
			re += "\t\t\t\t\t\t\t\t\t<li><input type=\"submit\" name=\"userspace\" value=\"Save\" /></li>\n";
			re += "\t\t\t\t\t\t\t\t</ul>\n";
			re += "\t\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t\t</fieldset>\n";
			re += "\t\t\t\t\t</article>\n";
		}
		else if ( setting.equals("reset") ) {
			re += "\t\t\t\t\t<article>\n";
			re += "\t\t\t\t\t\t<p>If you want to reset all Mappings you had changed press the button below, please.</p>\n";
			re += "\t\t\t\t\t\t<form action=\"?tab=account&setting=restore\" method=\"post\" accept-charset=\"UTF-8\" autocomplete=\"off\">\n";
			re += "\t\t\t\t\t\t\t<input type=\"submit\" name=\"userspace\" value=\"Reset\" />\n";
			re += "\t\t\t\t\t\t</form>\n";
			re += "\t\t\t\t\t</article>\n";
		}

		re += "\t\t\t\t</section>\n";

		return re;
	}
}

/*
 * <div class="pane">
				<section class="account">
					<aside>
						<ul>
							<li><a>Change Password</a></li>
							<li><a>Change Email</a></li>
						</ul>
					</aside>
					<article>
						<fieldset>
							<legend>Change Password</legend>
							<form autocomplete="off">
								<ul>
									<li>
										<label>Old password:</label>
										<input type="password" name="opassword" required />
									</li>
									<li>
										<label>New password:</label>
										<input type="password" name="npassword" required />
									</li>
									<li>
										<label>New password:</label>
										<input type="password" name="npassword2" required />
									</li>
									<li>
										<input type="submit" name="send" value="Change" />
									</li>
								</ul>
							</form>
						</fieldset>
					</article>
				</section>
			</div>
 */