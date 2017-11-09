import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;


public class QueryParser implements QueryParserInterface{
	
	/**
	 * The results data structure
	 */
	private final TreeMap<String, ArrayList<SearchResult>> results;

	/**
	 * Inverted Index
	 */
	private final InvertedIndex index;
	
	/**
	 * Initializes the results tree map
	 */
	public QueryParser(InvertedIndex index){
		results = new TreeMap<>();
		this.index = index;
	}

	/**
	 * Writes the contents of the results Map in JSON Format
	 * @param output
	 * 			Location to write the contents
	 * @return
	 * 			True if successful
	 * 			False otherwise
	 */
	public boolean searchOutput(Path output){
		return JSONWriter.writeSearchJSON(output, results);
	}
	
	/**
	 * Parses the Query file and performs the desired search on the Inverted Index
	 * 
	 * @param index
	 * 			Inverted Index Data Structure
	 * @param query
	 * 			Path of the Query file to read
	 * @param search
	 * 			Type of search to perform
	 */
	public void parseQuery(Path query, boolean search){
		try(BufferedReader reader = Files.newBufferedReader(query, Charset.forName("UTF-8"))){
			String line = null;
			
			while((line = reader.readLine()) != null){
				line = line.replaceAll("\\p{Punct}+", "").toLowerCase().trim();
				
				String[] words = line.split("\\s+");
				
				Arrays.sort(words);
								
				line = String.join(" ", words); 
				
				if(search == false){
					results.put(line, index.partialSearch(words));
				}
				else{
					results.put(line, index.exactSearch(words));
				}
			}
		} catch (IOException e) {
			System.err.println("Problems reading the Query File " + query +".");
		}
	}
}
