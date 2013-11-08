<%@page import="java.io.File"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page pageEncoding="GBK"%>
<%
	//Enumeration<String> names = request.getParameterNames();
	//String paramList = "";
	//while(names.hasMoreElements()){
	//	String name = (String)names.nextElement();
	//	if(paramList.length()>0){
	//		paramList += "<br/>\n";
	//	}
	//	paramList += name + "=" + request.getParameter(name);
	//}
	//out.println("<h1>Params:</h1>:" + paramList);
	//System.out.println("<h1>Params:</h1>:\n" + paramList);

	//InputStream stream = request.getInputStream();
	//int value = -1;
	//do {
	//	value = stream.read();
	//	System.out.println(value + ",");
	//} while (value != -1);

	DiskFileItemFactory dfif = new DiskFileItemFactory();
	//dfif.setSizeThreshold(yourMaxMemorySize);
	//dfif.setRepository(yourTempDirectory);
	ServletFileUpload sfu = new ServletFileUpload(dfif);
	//sfu.setSizeMax(yourMaxRequestSize);

	Iterator it = sfu.parseRequest(request).iterator();

	while (it.hasNext()) {
		FileItem item = (FileItem) it.next();
		if (!item.isFormField()) {
			//String fieldName = item.getFieldName();
			//String fileName = item.getName();
			//String contentType = item.getContentType();
			//boolean isInMemory = item.isInMemory();
			//long sizeInBytes = item.getSize();
			File file = new File("c:/temp/wangw22.jpg");
			item.write(file);
			System.out.println("file upload OK:" + file.getAbsolutePath());
		} else {
			String name = item.getFieldName();
			String fvalue = item.getString();
			System.out.println("submit.params:" + name + "=" + fvalue);
		}
	}

	response.getWriter().append("file upload OK~!");
%>