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

<%-- 
    Document   : index
    Created on : 02.02.2012, 07:52:15
    Author     : J. Nathanael Philipp
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.Connection" %>
<%@page import="java.sql.DriverManager" %>
<%@page import="java.sql.SQLException" %>
<%@page import="LGDEditTool.db.DatabasePostgreSQL"%>
<%@page import="LGDEditTool.Templates.TemplatesSearch" %>
<%@page import="LGDEditTool.Templates.TemplatesMapping" %>
<% request.setCharacterEncoding("UTF-8"); %>

<!DOCTYPE HTML>
<html lang="de">
	<head>
		<meta charset="utf-8" />
		<link rel="stylesheet" href="./css/main.css" />
		<link rel="stylesheet" href="./css/tabs.css" />
		<link rel="stylesheet" href="./css/fieldset.css" />
		<link rel="stylesheet" href="./css/jquery.autocomplete.css" />
		<link rel="shortcut icon" href="http://linkedgeodata.org/files/lgdlogo.png" />
		<title>LGDEditTool</title>
		<script type="text/javascript" src="./js/jquery.js"></script>
        <script type="text/javascript" src="./js/jquery.autocomplete.js"></script>
		<script>
			$(function() {
				$("#search").autocomplete("autocomplete_search.jsp")
			});
		</script>
	</head>
	<body>
		<h1>LGDEditTool</h1>

		<ul id="tabs">
			<% if ( request.getParameter("tab") == null ) { %>
			<li><a class="current" href="?tab=search">Search</a></li>
			<li><a href="?tab=mappings">Mappings</a></li>
			<li><a href="?tab=ontologie">Ontologie</a></li>
			<li><a href="?tab=unmapped">Unmapped Tags</a></li>
			<li><a href="?tab=all">All Mappings</a></li>
			<li><a href="?tab=history">Edit-History</a></li>
			<% }
				else { %>
			<li><a <% if ( request.getParameter("tab").toString().equals("search") ) { out.print("class=\"current\""); } %> href="?tab=search">Search</a></li>
			<li><a <% if ( request.getParameter("tab").toString().equals("mappings") ) { out.print("class=\"current\""); } %> href="?tab=mappings">Mappings</a></li>
			<li><a <% if ( request.getParameter("tab").toString().equals("ontologie") ) { out.print("class=\"current\""); } %> href="?tab=ontologie">Ontologie</a></li>
			<li><a <% if ( request.getParameter("tab").toString().equals("unmapped") ) { out.print("class=\"current\""); } %> href="?tab=unmapped">Unmapped Tags</a></li>
			<li><a <% if ( request.getParameter("tab").toString().equals("all") ) { out.print("class=\"current\""); } %> href="?tab=all">All Mappings</a></li>
			<li><a <% if ( request.getParameter("tab").toString().equals("history") ) { out.print("class=\"current\""); } %> href="?tab=history">Edit-History</a></li>
			<% } %>
		</ul>

		<div id="panes">
			<%
			if ( request.getParameter("tab") == null || request.getParameter("tab").toString().equals("search") ) {
				if ( request.getParameter("search") == null ) {
					out.println("<div>");
					out.println(TemplatesSearch.search());
					out.println("</div>");
				}
				else {
					out.println("<div>");
					out.println(TemplatesMapping.kMappingSearch(request.getParameter("search")));
					//out.println(TemplatesMapping.kMapping("amenity", "rdf:type", "lgdo:Bakery", "300k"));
					//out.println(TemplatesMapping.kvMapping("amenity", "bakery", "rdf:type", "lgdo:Bakery", "10k"));
					out.println("\t\t\t</div>");
				}
			}
			else if ( request.getParameter("tab").toString().equals("mappings") ) {
				if ( request.getParameter("v") != null ) {
					out.println("<div>");
					out.println("k:" + request.getParameter("k").toString());
					out.println("v:" + request.getParameter("v").toString());
					out.println("</div>");
				}
				else if ( request.getParameter("k") != null ) {
					out.println("<div>");
					out.println("k:" + request.getParameter("k").toString());
					out.println("</div>");
				}
				else {
			%>
			<div>
			Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
			</div>
			<%
				}
			}
			else if ( request.getParameter("tab").toString().equals("ontologie") ) {
			%>
			<div>
				Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
			</div>
			<%
			}
			else if ( request.getParameter("tab").toString().equals("unmapped") ) {
			%>
			<div>
				Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
			</div>
			<%
			}
			else if ( request.getParameter("tab").toString().equals("all") ) {
			%>
			<div>
				Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
			</div>
			<%
			}
			else if ( request.getParameter("tab").toString().equals("history") ) {
			%>
			<div>
				Lorem ipsum dolor sit amet, consectetur adipisici elit, sed eiusmod tempor incidunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquid ex ea commodi consequat. Quis aute iure reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint obcaecat cupiditat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
			</div>
			<%
			}
			%>
		</div>
		<small>© swp12-10</small>
	</body>
</html>