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

<%@page import="LGDEditTool.Templates.TemplatesShowMessage"%>
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
<%@page import="LGDEditTool.db.DatabasePostgreSQL"%>
<%@page import="LGDEditTool.SiteHandling.User"%>
<%@page import="LGDEditTool.SiteHandling.RequestHandling" %>
<%@page import="LGDEditTool.Templates.TemplatesSearch" %>
<%@page import="LGDEditTool.Templates.TemplatesMapping" %>
<%@page import="LGDEditTool.Templates.TemplatesUnmappedTags" %>
<%@page import="LGDEditTool.Templates.TemplatesAllMappings" %>
<%@page import="LGDEditTool.Templates.TemplatesEditHistory" %>
<% request.setCharacterEncoding("UTF-8");
	String search = "";
	String message = "";
	boolean captcha = true;

	if ( request.getParameter("search") != null )
		search = request.getParameter("search").substring(0, (request.getParameter("search").lastIndexOf(" ") == -1 ? request.getParameter("search").length() : request.getParameter("search").lastIndexOf(" ")));

	User user = User.getInstance();
	user.createUser(request);

	if ( request.getParameter("recaptcha_challenge_field") != null && request.getParameter("recaptcha_response_field") != null ) {
		captcha = RequestHandling.checkCaptcha(request);
	}

	if ( request.getParameter("captcha") == null && captcha )
		message = RequestHandling.doRequestHandling(request, response, user);

/*	if ( message.equals("Login successful.") ) {
		out.println(user.isLoggedIn());
	}*/
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
		<link rel="shortcut icon" href="http://linkedgeodata.org/files/lgdlogo.png" />
		<title>LGDEditTool</title>
		<script type="text/javascript" src="./js/jquery.js"></script>
        <script type="text/javascript" src="./js/jquery.autocomplete.js"></script>
		<script>
			$(function() {
				$("#search").autocomplete("autocomplete_search.jsp")
			});

			function toggle_visibility(id) {
				var e = document.getElementById(id);
				var u = document.getElementById(id + 'u');

				if ( id.indexOf("d") != -1 )
					id = id.replace("d", "");
				var s = document.getElementById(id + 'a');

				if ( e.style.display == 'none' ) {
					e.style.display = 'table-row';
					u.style.display = 'table-row';
					s.style.display = 'none';
				}
				else {
					e.style.display = 'none';
					u.style.display = 'none';
					s.style.display = 'table-row';
				}
			}

			 var RecaptchaOptions = {theme : 'white'};
		</script>
	</head>
	<body>
		<% if ( !message.equals("") ) {
			out.println(TemplatesShowMessage.showMessage(message));
		}
if ( (user == null || !user.isLoggedIn()) ) { %>
		<div class="login" style="right: 20px; position: absolute;">
			<a>Login</a>
			<article>
				<fieldset>
					<legend>Log in (<a style="font-size: 9pt;">Signup</a>)</legend>
					<form action="<% out.print("?tab=" + (request.getParameter("tab") == null ? "search" : request.getParameter("tab")) + (search.equals("") ? "" : "&search=" + search)); %>" method="post" accept-charset="UTF-8">
						<ul>
							<li>
								<label>Login or Email</label>
								<input type="text" name="user" required />
							</li>
							<li>
								<label>Password (<a style="font-size: 9pt;">forgot password</a>)</label>
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
			<a>Account Settings</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href="<% out.print("?tab=" + (request.getParameter("tab") == null ? "search" : (request.getParameter("tab").equals("settings") ? "search" : request.getParameter("tab"))) + (search.equals("") ? "" : "&search=" + search)); %>&logout=yes">Logout</a>
		</div>
		<% } %>
		<h1>LGDEditTool</h1>

		<ul id="tabs">
			<% if ( request.getParameter("tab") == null ) { %>
			<li><a class="current" href="<% out.print("?tab=search" + (search.equals("") ? "" : "&search=" + search)); %>">Search</a></li>
			<li><a href="<% out.print("?tab=ontologie" + (search.equals("") ? "" : "&search=" + search)); %>" >Ontologie</a></li>
			<li><a href="?tab=unmapped">Unmapped Tags</a></li>
			<li><a href="?tab=all">All Mappings</a></li>
			<li><a href="?tab=history">Edit-History</a></li>
			<% }
				else { %>
			<li><a <% if ( request.getParameter("tab").toString().equals("search") ) { out.print("class=\"current\""); } %> href="<% out.print("?tab=search" + (search.equals("") ? "" : "&search=" + search)); %>">Search</a></li>
			<li><a <% if ( request.getParameter("tab").toString().equals("ontologie") ) { out.print("class=\"current\""); } %> href="<% out.print("?tab=ontologie" + (search.equals("") ? "" : "&search=" + search)); %>">Ontologie</a></li>
			<li><a <% if ( request.getParameter("tab").toString().equals("unmapped") ) { out.print("class=\"current\""); } %> href="?tab=unmapped">Unmapped Tags</a></li>
			<li><a <% if ( request.getParameter("tab").toString().equals("all") ) { out.print("class=\"current\""); } %> href="?tab=all">All Mappings</a></li>
			<li><a <% if ( request.getParameter("tab").toString().equals("history") ) { out.print("class=\"current\""); } %> href="?tab=history">Edit-History</a></li>
			<% if ( user.isAdmin() ) {
					out.println("<li><a " + (request.getParameter("tab").toString().equals("settings") ? "class=\"current\"" : "" ) + " href=\"?tab=settings\">Settings</a></li>");
				}
			} %>
		</ul>

		<div id="panes">
			<%
			if ( request.getParameter("tab") == null || request.getParameter("tab").toString().equals("search") ) {
				if ( search.equals("") ) {
					out.println("<div class=\"pane\">");
					out.println(TemplatesSearch.search());
					out.println("\t\t\t</div>");
				}
				else {
					out.println("<div class=\"pane\">");
					if ( (request.getParameter("captcha") != null && request.getParameter("captcha").equals("yes")) || !captcha )
					out.println(TemplatesSearch.captcha(request, search));
					out.println(TemplatesSearch.search());
					out.println("\t\t\t\t<br /><br />");
					out.println(TemplatesSearch.searchResult(search, user));
					out.println("\t\t\t</div>");
				}
			}
			else if ( request.getParameter("tab").toString().equals("ontologie") ) {
			%>
			<div class="pane">
				Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
			</div>
			<%
			}
			else if ( request.getParameter("tab").toString().equals("unmapped") ) {
				out.println("<div class=\"pane\">");
				out.println(TemplatesUnmappedTags.unmappedTags("1", "1"));
				out.println("</div>");
			}
			else if ( request.getParameter("tab").toString().equals("all") ) {
				out.println("<div class=\"pane\">");
				if(request.getParameter("type")==null){
					out.println(TemplatesAllMappings.listAllMappings("", "",user));
				}
				else if(request.getParameter("site")==null){
					out.println(TemplatesAllMappings.listAllMappings(request.getParameter("type").toString(), "1",user));
				}
				else {
					out.println(TemplatesAllMappings.listAllMappings(request.getParameter("type").toString(), request.getParameter("site").toString(),user));
                                        if ( (request.getParameter("captcha") != null && request.getParameter("captcha").equals("yes")) || !captcha )
						out.println(TemplatesAllMappings.captcha(request, request.getParameter("type").toString(), request.getParameter("site").toString()));
				}
				out.println("</div>");
			}
			else if ( request.getParameter("tab").toString().equals("history") ) {
				out.println("<div class=\"pane\">");
				out.println(TemplatesEditHistory.editHistory());
				out.println("</div>");
			}
			else if ( request.getParameter("tab").toString().equals("settings") ) {
			%>
			<div class="pane">
				Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
			</div>
			<%
			}
			%>
		</div>
		<small style="float: right;">© swp12-10 (<% out.print((Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10 ? "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) : Calendar.getInstance().get(Calendar.DAY_OF_MONTH) ) + "." + ((Calendar.getInstance().get(Calendar.MONTH) + 1) < 10 ? "0" + (Calendar.getInstance().get(Calendar.MONTH) + 1) : (Calendar.getInstance().get(Calendar.MONTH) + 1)) + "." + Calendar.getInstance().get(Calendar.YEAR)); %>)</small>
	</body>
</html>
