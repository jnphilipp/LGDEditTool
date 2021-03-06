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


package LGDEditTool;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.servlet.ServletContext;

/**
 *
 * @author J. Nathanael Philipp
 * @version 1.0
 */
public class Functions {
	/**
	 * main branch
	 */
	public static final String MAIN_BRANCH = "lgd_user_main";
	/**
	 * PRIVATE_reCAPTCHA_KEY
	 */
	public static final String PRIVATE_reCAPTCHA_KEY = "6Le1b88SAAAAALUjcJ26asXAk2wHDu-JwarKY8z1";
	/**
	 * PUBLIC_reCAPTCHA_KEY
	 */
	public static final String PUBLIC_reCAPTCHA_KEY = "6Le1b88SAAAAALjXm-PM6alI7EQlj-fi9eh-Wm2C ";
	/**
	 * The base URL of the website.
	 */
	public static final String baseURL = "http://pcai042.informatik.uni-leipzig.de:4208/LGDEditTool/";

	/**
	 * Schorting the URL from property and object.
	 * @param url URL
	 * @return short URL
	 */
	public static String shortenURL(String url) {
		if ( url.contains("w3.org") && url.contains("#") )
			return "rdf:" + url.substring(url.indexOf("#") + 1);
		else if ( url.contains("linkedgeodata.org") ) {
			return "LGD:" + url.substring(url.lastIndexOf("/") + 1);
		}
		else
			return url;
	}

	/**
	 * Formating timestamp from database (format: YYYY-MM-ddTHH:mm:ss) into dd.MM.YYYY<br />HH:mm:ss
	 * @param timestamp
	 * @return String in dd.MM.YYYY<br />HH:mm:ss format.
	 */
	public static String showTimestamp(String timestamp) {
		String re = "";
		String[] a = timestamp.split("T")[0].split("-");
		re += a[2] + "." + a[1] + "." + a[0];
		re += "<br />" + timestamp.split("T")[1];
		return re;
	}

	/**
	 * Template for Database. Transform Date-String to Database Date-Type-String
	 * @return String in YYYY-MM-ddTHH:mm:ss format.
	 */
	public static String getTimestamp() {
		return Calendar.getInstance().get(Calendar.YEAR) + "-" + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "-" + (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) + "T" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + ":" + (Calendar.getInstance().get(Calendar.MINUTE) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)) + ":" + (Calendar.getInstance().get(Calendar.SECOND) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND));
	}

	/**
	 * Returns the date of the timestamp in fomat dd.mm.yyyy.
	 * @param timestamp timestamp
	 * @return date
	 */
	public static String date(String timestamp) {
		String sp[] = timestamp.substring(0, timestamp.indexOf("T")).split("-");
		return sp[2] + "." + sp[1] + "." + sp[0];
	}

	/**
	 * Makes a timestamp from the date.
	 * @param date date
	 * @param begin If <code>true</code> the timestamp will end with T00:00:00 else it will end with T23:59:59.
	 * @return timestamp
	 */
	public static String dateToTimestamp(String date, boolean begin) {
		return date.substring(date.lastIndexOf(".") + 1) + "-" + date.substring(date.indexOf(".") + 1, date.lastIndexOf(".")) + "-" + date.substring(0, date.indexOf(".")) + (begin ? "T00:00:00" : "T23:59:59");
	}

	/**
	 * Expands the namespaces, such as lgd to the full URL, specified by the namespaces.properties-file.
	 * @param servlet ServletContext
	 * @param key Key, which is to be expanded.
	 * @return expanded key
	 * @throws IOException 
	 */
	public static String expand(ServletContext servlet, String key) throws IOException {
		InputStream inputStream = servlet.getResourceAsStream("/WEB-INF/namespaces.properties");
		Properties props = new Properties();
		props.load(inputStream);
		String value = props.getProperty(key);
		inputStream.close();

		return value;
	}

	/**
	 * Returns all keys of the namespaces, which can be expanded.
	 * @param servlet ServletContext
	 * @return List of all keys.
	 * @throws IOException 
	 */
	public static Enumeration<Object> getNamespaceKeys(ServletContext servlet) throws IOException {
		InputStream inputStream = servlet.getResourceAsStream("/WEB-INF/namespaces.properties");
		Properties props = new Properties();
		props.load(inputStream);
		Enumeration<Object> keys = props.keys();
		inputStream.close();

		return keys;
	}

	/**
	 * Returns all keys and values of the namespaces.
	 * @param servlet ServletContext
	 * @return HashMap of all keys and values
	 * @throws IOException 
	 */
	public static HashMap<String, String> getNamespaces(ServletContext servlet) throws IOException {
		InputStream inputStream = servlet.getResourceAsStream("/WEB-INF/namespaces.properties");
		Properties props = new Properties();
		props.load(inputStream);
		Enumeration<Object> keys = props.keys();

		HashMap<String, String> namespaces = new HashMap<String, String>();

		while ( keys.hasMoreElements() ) {
			String key = keys.nextElement().toString();
			namespaces.put(key, props.getProperty(key));
		}

		inputStream.close();

		return namespaces;
	}

	/**
	 * Updates the property file for the namespaces to the given map.
	 * @param servlet ServletContext
	 * @param namespaces map of namespaces
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	public static void updateNamespaces(ServletContext servlet, HashMap<String, String> namespaces) throws FileNotFoundException, IOException {
		Properties props = new Properties();
		InputStream inputStream = servlet.getResourceAsStream("/WEB-INF/namespaces.properties");
		FileOutputStream out = new FileOutputStream(servlet.getRealPath("/WEB-INF/namespaces.properties"));
		props.load(inputStream);
		props.clear();
		props.putAll(namespaces);
		props.store(out, null);
		inputStream.close();
		out.close();
	}
}