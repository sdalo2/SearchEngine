import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles display of user information.
 *
 * @see LoginServer
 */
@SuppressWarnings("serial")
public class LoginWelcomeServlet extends LoginBaseServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String user = getUsername(request);

		if (user != null) {
			prepareResponse("Welcome", response);

			PrintWriter out = response.getWriter();
			out.println("<p>Hello " + user + "!</p>");
			
			out.println("<p>What would you like to do?</p>");
			
			
			out.println("<p><a href=\"/search\" class=\"btn btn-primary\" role=\"button\">Search</a></p>");
			out.println("<p><a href=\"/addUrl\" class=\"btn btn-primary\" role=\"button\">Crawl Links</a></p>");
			out.println("<p><a href=\"/changePassword\" class=\"btn btn-primary\" role=\"button\">Change Password</a></p>");
			if(getUsername(request).equals("admin")){
				out.println("<p><a href=\"/shutdown\" class=\"btn btn-primary\" role=\"button\">Shut Down</a></p>");
			}
			out.println("<p><a href=\"/login?logout\" class=\"btn btn-primary\" role=\"button\">Logout</a></p>");

			finishResponse(response);
		}
		else {
			response.sendRedirect("/login");
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}
}
