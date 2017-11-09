import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ResultServlet extends LoginBaseServlet{
	
	private final InvertedIndex index;
	
	public ResultServlet(InvertedIndex index){
		this.index = index;
	}
	
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
		
		boolean partial;
		
		if(request.getParameter("partial") == null){
			partial = true;
		}
		else{
			partial = false;
		}
		
		String search = request.getParameter("query");
		
		Map<String, String> cookieMap = getCookieMap(request);
		
		
		String userHistory = getUsername(request) + "History";
//		searchHistory = search+","+searchHistory;
//		System.out.println("Search History is now " + searchHistory);
		
		response.addCookie(new Cookie(userHistory, search.concat(","+cookieMap.get(userHistory))));
		
		long start = System.currentTimeMillis();
		ArrayList<SearchResult> results = (partial) ? index.partialSearch(search.toLowerCase().split("\\s+")) :
			index.exactSearch(search.toLowerCase().split("//s+"));
		long end = System.currentTimeMillis();
		
		double duration = ((double)end-start);
		
		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>Search Engine</title></head>%n");
		out.printf("<body>%n%n");
		
		
		out.printf("<p><b><u>Here are your results for the query: \"%s\".</u></b></p>", search);
		
		out.printf("<small>Fetched %s Results in %s milliseconds</small>%n", results.size(), duration);
		
		
		if(results.isEmpty()){
			out.printf("<p>Sorry, there are no results for that query</p>");
		}
		else{
			for(SearchResult result : results){
				out.printf("<p><a href=\"%s\">%s</a></p>", result.getLocation(), result.getLocation());
			}
		}
		out.printf("<p>For another search, please press this button.</p>");
		out.printf("<form method=\"get\" action=\"/search\">");

		out.printf("<input type=\"submit\" value=\"Search\">");
		out.printf("</form>");
		
		out.printf("<p> If you want to crawl more links, press this button</p>");
		out.printf("<form method=\"get\" action=\"/addUrl\">");

		out.printf("<input type=\"submit\" value=\"Crawl\">");
		out.printf("</form>");
		
		out.println("<p><a href=\"/login?logout\" class=\"btn btn-primary\" role=\"button\">Logout</a></p>");

		
		out.printf("</body>");
		out.printf("</html>");

		response.setStatus(HttpServletResponse.SC_OK);
	}
	
//	public static void clearSearchHistory(){
//		searchHistory = "";
//	}
	
}
