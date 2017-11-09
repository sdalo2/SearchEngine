import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class CrawlServlet extends LoginBaseServlet {
	
	private final WebCrawlerInterface crawler;
	
	public CrawlServlet(WebCrawlerInterface crawler){
		this.crawler = crawler;
	}
	
	//New Crawl
	
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
		
		out.printf("<form method=\"post\" action=\"/addUrl\">");
		out.printf("<p>");
		out.printf("<input type=\"text\" name= \"link\" maxlength=\"50\" size=\"50\">");
		out.printf("<br>");
		
		out.printf("<input type=\"submit\" value=\"Crawl\">");
		out.printf("</p>");
		out.printf("</form>");
		
		out.printf("<p>Please enter a valid url to crawl.</p>");
		
		out.println("<p><a href=\"/login?logout\" class=\"btn btn-primary\" role=\"button\">Logout</a></p>");

		out.printf("</body>");
		out.printf("</html>");
		
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException{
		
		String link = request.getParameter("link");
		
		try{
			link = cleanLink(link);
			crawler.crawlWeb(link);
			response.sendRedirect(response.encodeRedirectURL("/"));
		}catch(MalformedURLException e){
			response.sendRedirect(response.encodeRedirectURL("/addUrl"));
		}
		
		
//		PrintWriter out = response.getWriter();

		
	}
	
	public String cleanLink(String link) throws MalformedURLException{
		URL url = new URL(link);
		URL reconstructedURL = new URL(url.getProtocol(), url.getHost(), url.getFile());
		
		return reconstructedURL.toString();
	}
}
