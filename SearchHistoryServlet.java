import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SearchHistoryServlet extends LoginBaseServlet{
	
	
	
	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException{
		
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		
		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>Search Engine</title></head>%n");
		out.printf("<body>%n%n");
		
		out.printf("<font size = \"7\" color = \"black\"> Search History</font>");
		
		Cookie[] cookieMap = request.getCookies();
		
//		System.out.println("Cookie size is " + cookieMap.length);
		
		if(cookieMap == null ||cookieMap.length == 0){
			out.printf("<p>There is nothing here to see....sus</p>");
		}
		else{
			for(Cookie cookie : cookieMap){
				if(cookie.getName().equals(getUsername(request)+"History")){
					for(String search : cookie.getValue().split(",")){
						if(!search.equals("null")){
							out.printf("<p>%s</p>", search);
						}
					}
				}
			}
		}
		
		out.printf("<form method=\"post\" action=\"/history\">");
		out.printf("<p>");
				
		out.printf("<input type=\"submit\" value=\"Clear History\">");
		out.printf("</p>");
		out.printf("</form>");
		
		out.println("<p><a href=\"/welcome\" class=\"btn btn-primary\" role=\"button\">Back to Home</a></p>");
	}
	
	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException{
		
		clearCookie(getUsername(request)+"History", response);
		response.sendRedirect(response.encodeRedirectURL("/history"));
		
	}
}
