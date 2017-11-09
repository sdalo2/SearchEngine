import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.net.URI;

public class WebCrawler implements WebCrawlerInterface{

	/**
	 * A queue that stores string representation of links
	 */
	private final LinkedList<String> queue;

	/**
	 * Inverted Index to add findings to
	 */
	private final InvertedIndex index;

	/**
	 * HashSet of all crawled links
	 */
	private final HashSet<String> crawledLinks;

	/**
	 * initializes the queue, and hash set
	 */
	public WebCrawler(InvertedIndex index){
		queue = new LinkedList<>();
		crawledLinks = new HashSet<>();
		this.index = index;
	}

	/**
	 * Crawls the web starting from a seedURL, getting up to 50 unique links
	 * and parsing all the words on the page and putting them in the inverted index. 
	 * @param index
	 * 			Index to add to
	 * @param seedURL
	 * 			URL to start crawling
	 */
	public void crawlWeb(String seedURL){
		try {
			if(crawledLinks.size() != 50 && !crawledLinks.contains(seedURL)){
				queue.add(seedURL);
				crawledLinks.add(seedURL);
			}

			while(!queue.isEmpty()){
				String link = queue.removeFirst();
				String linkHTML = HTMLCleaner.fetchHTML(link);
				addLinks(link, linkHTML);
				addToIndex(link, linkHTML, index);
			}

		}
		catch(MalformedURLException e){
			System.err.println("Unable to read this url: " + seedURL);
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

		for(String incompleteUrl : links){
			if(crawledLinks.size() == 50){
				break;
			}

			URL url = new URL(base, incompleteUrl);
			URI newLink = URI.create(url.toString());
			String cleanedLink = newLink.getScheme() + "://" + newLink.getHost() + newLink.getPath();

			if(!crawledLinks.contains(cleanedLink)){
				queue.add(cleanedLink);
				crawledLinks.add(cleanedLink);
			}
		}
	} 
	
	/**
	 * Parses the words from a given link and adds them to the Inverted index
	 * @param index
	 * 			Inverted Index to add words to
	 * @param linkInQueue
	 * 			Link where the words are
	 * @param linkInQueueHTML
	 * 			HTML of the link in order to parse the words
	 */
	public static void addToIndex(String linkInQueue, String linkInQueueHTML, InvertedIndex index){
		int position = 1;
		for(String word: HTMLCleaner.parseWords(HTMLCleaner.cleanHTML(linkInQueueHTML))){
			if(word.equals("")){
				continue;
			}
			index.add(word, linkInQueue, position);
			position++;
		}
	}
}
