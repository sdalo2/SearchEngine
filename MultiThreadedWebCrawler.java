import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MultiThreadedWebCrawler implements WebCrawlerInterface{

	/**
	 * Logger
	 */
	private static final Logger logger = LogManager.getRootLogger();

	/**
	 * Work queue
	 */
	private final WorkQueue minions;

	/**
	 * Thread Safe Inverted Index
	 */
	private final ThreadSafeInvertedIndex index;


	/**
	 * Links already crawled
	 */
	private final HashSet<String> crawledLinks;

	/**
	 * Initializes the work queue and ThreadSafe Index
	 * @param index
	 * 			Thread Safe Inverted Index
	 */
	public MultiThreadedWebCrawler(ThreadSafeInvertedIndex index, WorkQueue queue){
		minions = queue;
		this.index = index;
		crawledLinks = new HashSet<>();
	}

	/**
	 * Crawls the web for and adds to the index
	 * @param seedURL
	 */
	public void crawlWeb(String seedURL){ 
		synchronized (crawledLinks){
			if(crawledLinks.size() != 50 && !crawledLinks.contains(seedURL)){
				crawledLinks.add(seedURL);
				minions.execute(new Task(seedURL));
			}
		}
		finish();
	}

	/**
	 * 
	 * Inner class that is added to the work queue as a task
	 *
	 */
	private class Task implements Runnable{

		/**
		 * URL of website
		 */
		private String url;

		/**
		 * Inverted Index
		 */
		private InvertedIndex localIndex;

		/**
		 * gives values for index and url
		 * @param url
		 */
		public Task(String url){
			logger.debug("Minion created for {}", url);
			this.url = url;
			localIndex = new InvertedIndex();
		}

		@Override
		public void run() {
			try{
				String linkHTML = HTMLCleaner.fetchHTML(url);
				addLinks(url, linkHTML);
				WebCrawler.addToIndex(url, linkHTML, index);
				index.addAll(localIndex);
			} catch(MalformedURLException e){
				logger.debug(e.getMessage(), e);
			}
		}

		/**
		 * Adds unique links to the queue from a given link
		 * @param linkInQueue
		 * 			Current link in the Queue
		 * @param linkInQueueHTML
		 * 			HTML of the link to find more links
		 * @throws MalformedURLException
		 */
		private void addLinks(String link, String html) throws MalformedURLException{
			ArrayList<String> links = LinkParser.listLinks(html);

			URL base = new URL(link);

			synchronized (crawledLinks){
				for(String incompleteUrl : links){
					if(crawledLinks.size() == 50){
						break;
					}

					URL url = new URL(base, incompleteUrl);
					URI newLink = URI.create(url.toString());
					String cleanedLink = newLink.getScheme() + "://" + newLink.getHost() + newLink.getPath();

					if(!crawledLinks.contains(cleanedLink)){
						crawledLinks.add(cleanedLink);
						minions.execute(new Task(cleanedLink));
					}
				}
			}
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
		minions.shutdown();
	}
}
