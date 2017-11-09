import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class ChangePasswordServlet extends LoginBaseServlet{

	// /changePassword
	
	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException{
		
		if(getUsername(request) == null){
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
		
		prepareResponse("Change Password", response);
		String error = request.getParameter("error");
		PrintWriter out = response.getWriter();
		
		if(error != null){
			out.println("<p class=\"alert alert-danger\">Incorrect Password. Try again.</p>");
		}
		
		out.println("<form action=\"/changePassword\" method=\"post\" class=\"form-inline\">");

		out.println("\t<div class=\"form-group\">");
        out.println("\t\t<label for=\"oldpass\">Old Password:</label>");
		out.println("\t\t<input type=\"password\" name=\"oldpass\" class=\"form-control\" id=\"oldpass\" placeholder=\"Old Password\">");
		out.println("\t</div>\n");

        out.println("\t<div class=\"form-group\">");
        out.println("\t\t<label for=\"newpass\">New Password:</label>");
        out.println("\t\t<input type=\"password\" name=\"newpass\" class=\"form-control\" id=\"newpass\" placeholder=\"New Password\">");
        out.println("\t</div>\n");

		out.println("\t<button type=\"submit\" class=\"btn btn-primary\">Submit</button>\n");
		out.println("</form>");
		out.println("<br/>\n");
		
		finishResponse(response);
		
	}
	
	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException{
		
		String username = getUsername(request);
		String oldpass = request.getParameter("oldpass");
		String newpass = request.getParameter("newpass");
		
		Status status = dbhandler.authenticateUser(username, oldpass);
		
		if(status == Status.OK){
			dbhandler.removeUser(username, oldpass);
			dbhandler.registerUser(username, newpass);
			
			response.addCookie(new Cookie("login", "true"));
			response.addCookie(new Cookie("name", username));
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}
		else{
			response.sendRedirect(response.encodeRedirectURL("/changePassword?error=true"));
		}
		
	}
	
}
