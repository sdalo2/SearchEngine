import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiThreadedQueryParser implements QueryParserInterface{
	
	/**
	 * Logger
	 */
	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Work Queue
	 */
	private final WorkQueue minions;
	
	/**
	 * Thread Safe Inverted Index
	 */
	private final ThreadSafeInvertedIndex index; 

	/**
	 * Results Map 
	 */
	private final TreeMap<String, ArrayList<SearchResult>> results;

	
	/**
	 * Initializes results and assigns values to index and work queue
	 * @param index
	 * 			Thread Safe Inverted Index
	 * @param threads
	 * 			Number of threads in work queue
	 */
	public MultiThreadedQueryParser(ThreadSafeInvertedIndex index, WorkQueue queue){
		minions = queue;
		this.index = index;
		results = new TreeMap<>();
	}

	/**
	 * Parses the query file and performs specified search
	 * @param query
	 * 			File with all the queries
	 * @param search
	 * 			false if partial, true is exact
	 */
	public void parseQuery(Path query, boolean search){
		try(BufferedReader reader = Files.newBufferedReader(query, Charset.forName("UTF-8"))){
			String line = null;

			while((line = reader.readLine()) != null){
				minions.execute(new Task(line, search));
			}
			finish();
		}catch(IOException e){
			logger.debug("Problems reading the Query File " + query +".");
		}

	}

	/**
	 * Writes the contents of the Result TreeMap in the JSON Format
	 * @param output
	 * 			Path to output
	 * @return
	 * 			True if successful, false otherwise
	 */
	public boolean searchOutput(Path output){
		synchronized (results){
			return JSONWriter.writeSearchJSON(output, results);
		}
	}

	/**
	 * 
	 * Inner class that is added to the work queue as a task
	 *
	 */
	private class Task implements Runnable{
		
		/**
		 * query
		 */
		private String line;
		
		/**
		 * What kind of search to perform
		 */
		private boolean search; 
		
		/**
		 * Assigns line and search a value
		 * @param line
		 * 			Query
		 * @param search
		 * 			partial if false, exact if true
		 */
		public Task(String line, boolean search){
			this.line = line;
			this.search = search; 
		}

		@Override
		public void run() {
			line = line.replaceAll("\\p{Punct}+", "").toLowerCase().trim();

			String[] words = line.split("\\s+");

			Arrays.sort(words);
			line = String.join(" ", words); 

			ArrayList<SearchResult> result = (search) ? index.exactSearch(words) : index.partialSearch(words);
			synchronized (results){
				results.put(line, result);
			}
			
//			
//			if(search == false){
//				ArrayList<SearchResult> search = index.partialSearch(words);
//				synchronized (results){
//					results.put(line, search);
//				}
//			}
//			else{
//				ArrayList<SearchResult> search = index.exactSearch(words);
//				synchronized (results){
//					results.put(line, search);
//				}
//			}
		}
	}


	/**
	 * Helper method, that helps a thread wait until all of the current
	 * work is done. This is useful for resetting the counters or shutting
	 * down the work queue.
	 */
	public synchronized void finish() {
		minions.finish();
	}

	/**
	 * Will shutdown the work queue after all the current pending work is
	 * finished. Necessary to prevent our code from running forever in the
	 * background.
	 */
	public synchronized void shutdown() {
		logger.debug("Shutting down");
		finish();
		minions.shutdown();
	}
}
