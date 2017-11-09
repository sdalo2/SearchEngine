import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

@SuppressWarnings("serial")
public class ShutdownServlet extends LoginBaseServlet{

	private Server server;

	public ShutdownServlet(Server server){
		this.server = server;
	}

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response)
					throws ServletException, IOException{

		if(getUsername(request) == null || !getUsername(request).equals("admin")){
			response.sendRedirect(response.encodeRedirectURL("/login"));
		}

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>Search Engine</title></head>%n");
		out.printf("<body>%n%n");

		out.printf("<p>Server has been shutdown by an administrator. </p>");

		response.setStatus(HttpServletResponse.SC_OK);
		shutdown();

	}

	public void shutdown(){
		
		//Read online that a new thread should be made to close Jetty
		//So an exception doesnt happen
		new Thread(){
			
			@Override
			public void run(){
				try{
					//Stop handlers first
					for (Handler handler : server.getHandlers()){
						handler.stop();
					}
					//Then server
					server.stop();
					//Then wait for its threads to join
					server.getThreadPool().join();
				}catch(Exception e){
					System.out.println("Unable to stop Jetty.");
				}
			}
		}.start();
	}
}
