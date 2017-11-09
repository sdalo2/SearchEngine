import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SearchServlet extends LoginBaseServlet {
	
	
	
	// this is the / servlet
	
	
	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException{
		
		
		if(getUsername(request) == null){
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
		
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>Search Engine</title></head>%n");
		out.printf("<body>%n%n");
		
//		out.printf("<head>");
//		out.printf("<style type = \"text/css\" >");
//		out.printf(".footer{ position: fixed; text-align: center; bottom: 0px; width: 100 }");
//		out.printf("</style>");
//		out.printf("</head>");
//		out.printf("<body> <div class =\"footer\"Alec Sucks</div></body>");
		
		
		out.printf("<p>Please enter a search query.</p>");
		
		out.printf("<form method=\"get\" action=\"/results\">");
		out.printf("<p>");
		
		out.printf("<input type=\"text\" name= \"query\" maxlength=\"50\" size=\"50\">");
		out.printf("<br>");
		
		out.printf("<input type=\"submit\" value=\"Search\">");
		out.printf("<input type = \"checkbox\" name = \"partial\" id = \"partial\" value = \"off\">"
				+ "<label for = \"partial\"> Toggle partial search off</label>");
		out.printf("</p>");
		out.printf("</form>");
		
		out.printf("<p> If you want to crawl more links, press this button</p>");
		out.printf("<form method=\"get\" action=\"/addUrl\">");

		out.printf("<input type=\"submit\" value=\"Crawl\">");
		out.printf("</form>");
		
		
		out.println("<p><a href=\"/history\" class=\"btn btn-primary\" role=\"button\">View History</a></p>");
		out.println("<p><a href=\"/welcome\" class=\"btn btn-primary\" role=\"button\">Back to Home</a></p>");
		out.println("<p><a href=\"/login?logout\" class=\"btn btn-primary\" role=\"button\">Logout</a></p>");
		
		
		out.printf("</body>");
		out.printf("</html>");

		response.setStatus(HttpServletResponse.SC_OK);
	}
}
