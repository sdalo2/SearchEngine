import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;


public class Driver {

	/**
	 * Reads in command line args and acts accordingly
	 * @param args
	 * 			Command Line Arguments
	 */
	public static void main(String[] args) {
		ArgumentParser parser = new ArgumentParser();
		parser.parseArguments(args);
		//		System.out.println("hi");

		if(parser.hasValue("-dir") == false && parser.hasValue("-url") == false 
				&& parser.hasFlag("-port") == false){
			System.err.println("Invalid command line arguments. Program will now terminate.");
			return;
		}

		InvertedIndex index = null;
		QueryParserInterface query = null;
		WebCrawlerInterface crawler = null;
		WorkQueue queue = null;

		if(parser.hasFlag("-multi")){
			try{
				queue = new WorkQueue(Integer.parseInt(parser.getValue("-multi", "5")));

				ThreadSafeInvertedIndex threadSafe = new ThreadSafeInvertedIndex();
				index = threadSafe;
				query = new MultiThreadedQueryParser(threadSafe, queue);
				crawler = new MultiThreadedWebCrawler(threadSafe, queue);

				if(parser.hasValue("-dir") && queue.size() > 0){
					MultiThreadedIndexBuilder builder = new MultiThreadedIndexBuilder((ThreadSafeInvertedIndex) index, queue);
					builder.traverseDirectories(Paths.get(parser.getValue("-dir")));
					builder.finish();
				}
			}catch(NumberFormatException e){
				System.err.println("Unable to parse number of threads.");
			}
		}
		else{
			index = new InvertedIndex();
			query = new QueryParser(index);
			crawler = new WebCrawler(index);

			if(parser.hasValue("-dir")){
				Path directory = Paths.get(parser.get("-dir"));
				InvertedIndexBuilder.traverse(directory, index);
			}
		}

		if(parser.hasFlag("-url")){
			crawler.crawlWeb(parser.getValue("-url"));
			queue.finish();
		}

		if(parser.hasFlag("-query") && parser.hasValue("-query")){
			query.parseQuery(Paths.get(parser.getValue("-query")), false);
		}

		if(parser.hasFlag("-exact") && parser.hasValue("-exact")){
			query.parseQuery(Paths.get(parser.getValue("-exact")), true);
		}

		if(parser.hasFlag("-index")) {
			String output = parser.getValue("-index", "index.json");
			index.writeJSONFile(Paths.get(output));
		}

		if(parser.hasFlag("-results")){
			String output = parser.getValue("-results", "results.json");
			query.searchOutput(Paths.get(output));
		}
		
		if(parser.hasFlag("-port")){
			try{
				int port = Integer.parseInt(parser.getValue("-port", "8080"));
				Server server = new Server(port);
				ServletHandler handler = new ServletHandler();
				handler.addServletWithMapping(LoginUserServlet.class,"/login");
				handler.addServletWithMapping(SearchServlet.class, "/search");
				handler.addServletWithMapping(new ServletHolder(new ResultServlet(index)), "/results");
				handler.addServletWithMapping(new ServletHolder(new CrawlServlet(crawler)), "/addUrl");
				handler.addServletWithMapping(LoginRegisterServlet.class,"/register");
				handler.addServletWithMapping(LoginWelcomeServlet.class,"/welcome");
				handler.addServletWithMapping(LoginRedirectServlet.class,"/*");
				handler.addServletWithMapping(SearchHistoryServlet.class, "/history");
				handler.addServletWithMapping(new ServletHolder(new ShutdownServlet(server)),"/shutdown");
				handler.addServletWithMapping(ChangePasswordServlet.class, "/changePassword");
				
				server.setHandler(handler);
				server.start();
				
				server.join();

			} catch(Exception e){
				System.err.println("Something went wrong with the server.");
			}

		}

		if(queue != null){
			queue.finish();
			queue.shutdown();
		}

	}
}
