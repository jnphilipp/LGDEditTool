<%--
    This file is part of LGDEditTool (LGDET).

    LGDET is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    LGDET is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with LGDET.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@page import="LGDEditTool.Templates.TemplatesEditedMappings"%>
<%@page import="LGDEditTool.Templates.TemplatesAccountSettings"%>
<%@page import="LGDEditTool.db.DatabaseBremen"%>
<%@page import="LGDEditTool.Templates.Templates"%>
<%@page import="java.security.MessageDigest"%>
<%-- 
    Document   : index
    Created on : 02.02.2012, 07:52:15
    Author     : J. Nathanael Philipp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.Connection" %>
<%@page import="java.sql.DriverManager" %>
<%@page import="java.sql.SQLException" %>
<%@page import="java.util.Calendar"%>
<%@page import="LGDEditTool.Functions"%>
<%@page import="LGDEditTool.db.DatabasePostgreSQL"%>
<%@page import="LGDEditTool.SiteHandling.User"%>
<%@page import="LGDEditTool.SiteHandling.RequestHandling" %>
<%@page import="LGDEditTool.Templates.TemplatesSearch" %>
<%@page import="LGDEditTool.Templates.TemplatesUnmappedTags" %>
<%@page import="LGDEditTool.Templates.TemplatesAllMappings" %>
<%@page import="LGDEditTool.Templates.TemplatesEditHistory" %>
<%@page import="LGDEditTool.Templates.TemplatesOntology" %>
<% request.setCharacterEncoding("UTF-8");
	String search = "";
	String tab = "";
	String type = "k";
	String sort = "k";
	String message = "";
	boolean captcha = true;

	if ( request.getParameter("search") != null )
		search = request.getParameter("search").substring(0, (request.getParameter("search").indexOf("(") == -1 ? request.getParameter("search").length() : request.getParameter("search").lastIndexOf("(") - 1)) + (request.getParameter("search").contains(",") ? "~" + request.getParameter("search").substring(request.getParameter("search").indexOf("(") + 1, request.getParameter("search").indexOf(",")) : "");

	if ( request.getParameter("tab") != null )
		tab = request.getParameter("tab");

	if ( request.getParameter("sort") != null )
		sort = request.getParameter("sort");

	if ( request.getParameter("type") != null )
		type = request.getParameter("type");

	User.getInstance().createUser(request);

	if ( request.getParameter("recaptcha_challenge_field") != null && request.getParameter("recaptcha_response_field") != null )
		captcha = RequestHandling.checkCaptcha(request);

	if ( request.getParameter("captcha") == null && captcha )
		message = RequestHandling.doRequestHandling(request, response);
%>

<!DOCTYPE HTML>
<html lang="de">
	<head>
		<meta charset="utf-8" />
		<link rel="stylesheet" href="./css/main.css" />
		<link rel="stylesheet" href="./css/tabs.css" />
		<link rel="stylesheet" href="./css/fieldset.css" />
		<link rel="stylesheet" href="./css/mapping.css" />
		<link rel="stylesheet" href="./css/login.css" />
		<link rel="stylesheet" href="./css/captcha.css" />
		<link rel="stylesheet" href="./css/jquery.autocomplete.css" />
		<link rel="stylesheet" href="./css/treeview.css" />
		<link rel="stylesheet" href="./css/account.css" />
		<link rel="shortcut icon" href="http://linkedgeodata.org/files/lgdlogo.png" />
		<title>LGDEditTool</title>
		<script type="text/javascript" src="./js/jquery.js"></script>
        <script type="text/javascript" src="./js/jquery.autocomplete.js"></script>
		<script>
			$(function() {
			<% if ( tab.equals("") || tab.equals("search") )
				out.println("$(\"#search\").autocomplete(\"autocomplete_search.jsp\")");
			else if ( tab.equals("history") )
				out.println("$(\"#search\").autocomplete(\"autocomplete_history.jsp\")");
			else if ( tab.equals("unmapped") )
				out.println("$(\"#search\").autocomplete(\"autocomplete_unmapped.jsp\")");
			%>
					$(".property").autocomplete("autocomplete_property.jsp")
					$(".object").autocomplete("autocomplete_object.jsp")
			});

			function toggle_visibility(id) {
				var e = document.getElementById(id);
				var u = document.getElementById(id + 'u');
				<% if ( tab.equals("unmapped") )
					out.println("var m = document.getElementById(id + 'm');");
				%>

				if ( id.indexOf("d") != -1 )
					id = id.replace("d", "");
				else if ( id.indexOf("c") != -1 )
					id = id.replace("c", "");
				else if ( id.indexOf("kl") != -1 )
					id = id.replace("kl", "k");
				var s = document.getElementById(id + 'a');

				if ( e.style.display == 'none' ) {
					e.style.display = 'table-row';
					u.style.display = 'table-row';
					s.style.display = 'none';
					<% if ( tab.equals("unmapped") )
						out.println("m.style.display = 'table-row';");
					%>
				}
				else {
					e.style.display = 'none';
					u.style.display = 'none';
					s.style.display = 'table-row';
					<% if ( tab.equals("unmapped") )
						out.println("m.style.display = 'none';");
					%>
				}
			}

			function treeview(id){
				var e = document.getElementById(id);
				if ( e.style.display != 'block' ){
					e.style.display='block';
				}
				else {
					e.style.display='none';
				}
				var p= document.getElementById(id+"p");
				if ( p.style.display != 'inline' ){
					e.style.display='inline';
				}
				else {
					e.style.display='none';
				}
				var m= document.getElementById(id+"m");
				if ( p.style.display != 'inline' ){
					e.style.display='inline';
				}
				else {
					e.style.display='none';
				}
			}


			 var RecaptchaOptions = {theme : 'white'}; 
		</script>
	</head>
	<body>
		<% if ( !message.equals("") ) {
			out.println(Templates.showMessage(message));
		}
		if ( !User.getInstance().isLoggedIn() ) { %>
		<div class="login" style="right: 20px; position: absolute;">
			<a>Login</a>
			<article>
				<fieldset>
					<legend>Log in (<a href="?tab=signup" style="font-size: 9pt;">Sign up</a>)</legend>
					<form action="?tab=search<% out.print(search.equals("") ? "" : "&amp;search=" + search); %>" method="post" accept-charset="UTF-8">
						<ul>
							<li>
								<label>Login or Email</label>
								<input type="text" name="user" required />
							</li>
							<li>
								<label>Password (<a href="?tab=forgotten" style="font-size: 9pt;">forgot password</a>)</label>
								<input type="password" name="password" required />
							</li>
							<li><input type="submit" name="login" value="Log in" /></li>
						</ul>
					</form>
				</fieldset>
			</article>
		</div>
		<% } else { %>
		<div class="login" style="right: 20px; position: absolute;">
			<i><% out.print(User.getInstance().getUsername()); %></i>&nbsp;&nbsp;&nbsp;&nbsp;<a href="?tab=account">Account Settings</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="?tab=search&amp;logout=yes<% out.print(search.equals("") ? "" : "&amp;search=" + search); %>">Logout</a>
		</div>
		<% } %>
		<h1>LGDEditTool</h1>

		<ul id="tabs">
			<li><a <% if ( tab.equals("search") || tab.equals("") ) { out.print("class=\"current\""); } %> href="<% out.print("?tab=search" + (search.equals("") ? "" : "&amp;search=" + search)); %>">Search</a></li>
			<% out.print(search.equals("") ? "" : "<li><a  " + (tab.equals("ontology") ? "class=\"current\"" : "") + " href=\"?tab=ontology&amp;search=" + search + "\">Ontology</a></li>");
			if ( User.getInstance().isLoggedIn() )
				out.print("<li><a " + (tab.equals("unmapped") ? "class=\"current\"" : "") + " href=\"?tab=unmapped&type=k" + (search.equals("") ? "" : "&amp;search=" + search) + "&amp;sort=dusage_count" + "\">Unmapped Tags</a></li>");
			%>
			<li><a <% if ( tab.equals("all") ) { out.print("class=\"current\""); } %> href="?tab=all&amp;type=k">All Mappings</a></li>
			<li><a <% if ( tab.equals("history") ) { out.print("class=\"current\""); } %> href="?tab=history&type=k<% out.print((search.equals("") ? "" : "&amp;search=" + search)); %>">Edit-History</a></li>
			<% if ( !User.getInstance().getView().equals("lgd_user_main") && User.getInstance().isLoggedIn() ) {
				out.println("<li><a " + (tab.equals("edited") ? "class=\"current\"" : "") + " href=\"?tab=edited&amp;type=k" + (search.equals("") ? "" : "&amp;search=" + search) + "\">Edited Mappings</a></li>");
			}
			if ( User.getInstance().isAdmin() ) {
				out.println("<li><a " + (tab.equals("settings") ? "class=\"current\"" : "" ) + " href=\"?tab=settings\">Settings</a></li>");
			}
			if ( tab.equals("signup") && !User.getInstance().isLoggedIn() )
				out.println("<li><a class=\"current\" href=\"?tab=login" + (search.equals("") ? "" : "&amp;search=" + search) + "\">Sign up</a></li>");
			else if ( tab.equals("forgotten") && !User.getInstance().isLoggedIn() )
				out.println("<li><a class=\"current\" href=\"?tab=forgotten" + (search.equals("") ? "" : "&amp;search=" + search) + "\">Password forgotten</a></li>");
			else if ( User.getInstance().isLoggedIn() && tab.equals("account") ) {
				out.println("<li><a class=\"current\" href=\"?tab=account\">Acoount Settings</a></li>");
			} %>
		</ul>

		<div id="panes">
			<% if ( tab.equals("") || tab.equals("search") ) {
				if ( search.equals("") ) {
					out.println("<div class=\"pane\">");
					if ( User.getInstance().isLoggedIn() )
						out.println(Templates.branch(search));
					out.println(TemplatesSearch.search());
					out.println("\t\t\t</div>");
				}
				else {
					out.println("<div class=\"pane\">");
					if ( (request.getParameter("captcha") != null && request.getParameter("captcha").equals("yes")) || !captcha )
						out.println(TemplatesSearch.captcha(request, search));
					if ( User.getInstance().isLoggedIn() )
						out.println(Templates.branch(search));
					out.println(TemplatesSearch.search());
					out.println("\t\t\t\t<br /><br />");
					out.println(TemplatesSearch.searchResult(search, sort));
					out.println("\t\t\t</div>");
				}
			}
			else if ( tab.equals("ontology") ) {
				out.println("<div class=\"pane\">\n");
				out.println(TemplatesOntology.ontology(search));
				out.println("\t\t\t</div>\n");
			}
			else if ( tab.equals("unmapped") && User.getInstance().isLoggedIn() ) {
				out.println("<div class=\"pane\">");
				out.println("\t\t\t\t<ul id=\"tabs\">");
				out.println("\t\t\t\t\t<li><a " + (type.equals("k") ? "class=\"current\"" : "") + " href=\"?tab=unmapped&amp;type=k&amp;sort=dusage_count" + (search.equals("") ? "" : "&search=" + search) + "\">K-Tags</a></li>");
				out.println("\t\t\t\t\t<li><a " + (type.equals("kv") ? "class=\"current\"" : "") + " href=\"?tab=unmapped&amp;type=kv&amp;sort=dusage_count" + (search.equals("") ? "" : "&search=" + search) + "\">KV-Tags</a></li>");
				out.println("\t\t\t\t</ul>");
				out.println("\t\t\t\t<div class=\"pane\">");
				out.println(Templates.branch(search));
				out.println(TemplatesUnmappedTags.search(type));
				out.println("\t\t\t\t<br /><br />");
				out.println(TemplatesUnmappedTags.unmappedTags(type, (request.getParameter("site") == null ? "1" : request.getParameter("site")), search, sort));
				out.println("\t\t\t\t</div>");
				out.println("\t\t\t</div>");
			}
			else if ( tab.equals("all") ) {
				out.println("<div class=\"pane\">");
				out.println("\t\t\t\t<ul id=\"tabs\">");
				out.println("\t\t\t\t\t<li><a " + (type.equals("k") ? "class=\"current\"" : "") + " href=\"?tab=all&amp;type=k\">K-Mappings</a></li>");
				out.println("\t\t\t\t\t<li><a " + (type.equals("kv") ? "class=\"current\"" : "") + " href=\"?tab=all&amp;type=kv\">KV-Mappings</a></li>");
				out.println("\t\t\t\t\t<li><a " + (type.equals("datatype") ? "class=\"current\"" : "") + " href=\"?tab=all&amp;type=datatype\">Datatype-Mappings</a></li>");
				out.println("\t\t\t\t\t<li><a " + (type.equals("literal") ? "class=\"current\"" : "") + " href=\"?tab=all&amp;type=literal\">Literal-Mappings</a></li>");
				out.println("\t\t\t\t</ul>");
				out.println("\t\t\t\t<div class=\"pane\">");
				if ( (request.getParameter("captcha") != null && request.getParameter("captcha").equals("yes")) || !captcha )
					out.print(TemplatesAllMappings.captcha(request, type, (request.getParameter("site") == null ? "1" : request.getParameter("site"))));
				if ( User.getInstance().isLoggedIn() )
					out.println(Templates.branch(search));
				out.print(TemplatesAllMappings.listAllMappings(type, (request.getParameter("site") == null ? "1" : request.getParameter("site")), sort));
				out.println("\t\t\t\t</div>");
				out.println("\t\t\t</div>");
			}
			else if ( tab.equals("history") ) {
				out.println("<div class=\"pane\">");
				out.println("\t\t\t\t<ul id=\"tabs\">");
				out.println("\t\t\t\t\t<li><a " + (type.equals("k") ? "class=\"current\"" : "") + " href=\"?tab=history&amp;type=k" + (search.equals("") ? "" : "&search=" + search) + "\">K-Mappings</a></li>");
				out.println("\t\t\t\t\t<li><a " + (type.equals("kv") ? "class=\"current\"" : "") + " href=\"?tab=history&amp;type=kv" + (search.equals("") ? "" : "&search=" + search) + "\">KV-Mappings</a></li>");
				out.println("\t\t\t\t\t<li><a " + (type.equals("datatype") ? "class=\"current\"" : "") + " href=\"?tab=history&amp;type=datatype" + (search.equals("") ? "" : "&search=" + search) + "\">Datatype-Mappings</a></li>");
				out.println("\t\t\t\t\t<li><a " + (type.equals("literal") ? "class=\"current\"" : "") + " href=\"?tab=history&amp;type=literal" + (search.equals("") ? "" : "&search=" + search) + "\">Literal-Mappings</a></li>");
				out.println("\t\t\t\t</ul>");
				out.println("\t\t\t\t<div class=\"pane\">");
				if ( (request.getParameter("captcha") != null && request.getParameter("captcha").equals("yes")) || !captcha )
					out.println(TemplatesEditHistory.captcha(request, type, (request.getParameter("site") != null ? request.getParameter("site") : "1"), search, sort));
				if ( User.getInstance().isLoggedIn() )
					out.println(Templates.branch(search));
				out.println(TemplatesEditHistory.search(type));
				out.println("\t\t\t\t<br /><br />");
				out.println(TemplatesEditHistory.editHistory(type, (request.getParameter("site") != null ? request.getParameter("site") : "1"), search, sort));
				out.println("\t\t\t\t</div>");
				out.println("\t\t\t</div>");
			}
			else if ( tab.equals("edited") && !User.getInstance().getView().equals("lgd_user_main") ) {
				out.println("<div class=\"pane\">");
				out.println("\t\t\t\t<ul id=\"tabs\">");
				out.println("\t\t\t\t\t<li><a " + (request.getParameter("type") == null || request.getParameter("type").equals("k") ? "class=\"current\"" : "") + " href=\"?tab=edited&amp;type=k\">K-Mappings</a></li>");
				out.println("\t\t\t\t\t<li><a " + (request.getParameter("type") != null && request.getParameter("type").equals("kv") ? "class=\"current\"" : "") + " href=\"?tab=edited&amp;type=kv\">KV-Mappings</a></li>");
				out.println("\t\t\t\t\t<li><a " + (request.getParameter("type") != null && request.getParameter("type").equals("datatype") ? "class=\"current\"" : "") + " href=\"?tab=edited&amp;type=datatype\">Datatype-Mappings</a></li>");
				out.println("\t\t\t\t\t<li><a " + (request.getParameter("type") != null && request.getParameter("type").equals("literal") ? "class=\"current\"" : "") + " href=\"?tab=edited&amp;type=literal\">Literal-Mappings</a></li>");
				out.println("\t\t\t\t</ul>");
				out.println("\t\t\t\t<div class=\"pane\">");
				out.print(TemplatesEditedMappings.listEditedMappings((request.getParameter("type") == null ? "" : request.getParameter("type")), (request.getParameter("site") == null ? "1" : request.getParameter("site"))));
				out.println("\t\t\t\t</div>");
				out.println("\t\t\t\t<br /><br />");
				out.println(TemplatesEditedMappings.commit(type, search, sort));
				out.println("\t\t\t</div>");
			}
			else if ( User.getInstance().isAdmin() && tab.equals("settings") ) {
			%>
			<div class="pane">
				Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
			</div>
			<%
			} else if ( User.getInstance().isLoggedIn() && tab.equals("account") ) {
				out.println("<div class=\"pane\">");
				out.println(TemplatesAccountSettings.accountSettings((request.getParameter("setting") == null ? "start" : request.getParameter("setting")), (request.getParameter("search") != null ? request.getParameter("search") : "")));
				out.println("\t\t\t</div>");
			}
			else if ( tab.equals("forgotten") && !type.equals("hash") && !User.getInstance().isLoggedIn() ) { %>
			<div class="pane">
				<article style="width: 60%; margin: 0.5em auto;">
					<p style="padding: 0em 1em;">In order to get a new password, please enter your email address in the field below. You will receive a email, containing a link to a site where you can change your email.</p>
					<fieldset>
							<legend>Password forgotten</legend>
							<form action="?tab=search<% out.print(search.equals("") ? "" : "&amp;search=" + search); %>" method="post" accept-charset="UTF-8">
								<table style="margin: 0 auto;">
									<tr>
										<th>Email:</th>
										<td><input type="email" name="email" required /></td>
									</tr>
									<tr><td></td><td><input type="submit" name="forgotten" value="Submit" /></td></tr>
								</table>
							</form>
						</fieldset>
				</article>
			</div>
			<% }
			else if ( tab.equals("forgotten") && type.equals("hash") && !User.getInstance().isLoggedIn() ) { %>
			<div class="pane">
				<article style="width: 60%; margin: 0.5em auto;">
					<p style="padding: 0em 1em;">Please enter the new password in the fields below.</p>
					<fieldset>
							<legend>Change Password</legend>
							<form action="?tab=search<% out.print(search.equals("") ? "" : "&amp;search=" + search); %>" method="post" accept-charset="UTF-8">
								<table style="margin: 0 auto;">
									<tr>
										<th><label>New password:</label></th>
										<td><input type="password" name="new" required /></td>
									</tr>
									<tr>
										<th><label>Confirm new password:</label></th>
										<td><input type="password" name="new2" required /></td>
									</tr>
									<tr>
										<th></th><td><input type="submit" name="password" value="Save" /></td>
										<input type="hidden" name="hash" value="<% out.print(request.getParameter("hash")); %>" />
										<input type="hidden" name="user" value="<% out.print(request.getParameter("user")); %>" />
									</tr>
								</table>
							</form>
						</fieldset>
				</article>
			</div>
			<% }
			else if ( tab.equals("signup") && !User.getInstance().isLoggedIn() ) { %>
			<div class="pane">
				<div class="tlogin">
					<article>
						<fieldset>
							<legend>Sign up</legend>
							<form action="?tab=search<% out.print(search.equals("") ? "" : "&amp;search=" + search); %>" method="post" accept-charset="UTF-8">
								<table>
									<tr>
										<th>Username:</th>
										<td><input type="text" name="user" required /></td>
									</tr>
									<tr>
										<th>Email:</th>
										<td><input type="email" name="email" required /></td>
									</tr>
									<tr>
										<th>Password:</th>
										<td><input type="password" name="password" required /></td>
									</tr>
									<tr>
										<th>Confirm password:</th>
										<td><input type="password" name="password2" required /></td>
									</tr>
									<tr><td></td><td><input type="submit" name="signup" value="Sign up" /></td></tr>
								</table>
							</form>
						</fieldset>
					</article>
				</div>
			</div>
		<% } %>
		</div>
		<small style="float: right;">© swp12-10 (<% out.print((Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ) + "." + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "." + Calendar.getInstance().get(Calendar.YEAR)); %>)</small>
		<%--
		/*Properties p = new Properties();
		FileOutputStream stream = new FileOutputStream("namespaces.properties");
		p.store(stream, null);
		String key = "lgd";
		String val = "http://linkedgeodata.org/ontology/";
		p.setProperty(key, val);
		p.store(stream,null);
		stream.close();*/
		
		out.println(Functions.expand(getServletContext(), "lgd"));
		
		--%>
	</body>
</html>
<%
User.getInstance().createCookie(response);
DatabaseBremen db = DatabaseBremen.getInstance();
db.disconnect();
%>