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

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class TemplatesShowMessage {
	public static String showMessage(String message) {
		String re = "\t\t\t\t<article class=\"message\">\n";
		re += "\t\t\t\t\t<p>" + message + "</p>\n";
		re += "\t\t\t\t</article>\n";
		return re;
	}
}