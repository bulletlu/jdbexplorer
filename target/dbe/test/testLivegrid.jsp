<%@ page contentType="text/html; charset=GB2312" pageEncoding="GB2312"%>
<%
	String start = request.getParameter("start");
	start = start == null ? "0" : start;
	String limit = request.getParameter("limit");
	limit = limit == null ? "10" : limit;
	System.out.println("start:" + start + ";limit:" + limit);
	
	try {
	    int index = Integer.parseInt(start);
	    int pageSize = Integer.parseInt(limit);

	    String json = "{totalProperty:1000,version:1,root:[";
	    for (int i = index; i < pageSize + index; i++) {
	        json += "{id:" + i + ",name:'name" + i + "',descn:'descn" + i + "'}";
	        if (i != pageSize + index - 1) {
	            json += ",";
	        }
	    }
	    json += "]}";
	    System.out.println("resp:" + json);
	    response.getWriter().write(json);
	} catch(Exception ex) {
		System.out.println("" + ex.getMessage());
	}
	
%>