import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {

	/**
	 * The Inverted Index
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;


	/**
	 * Initializes the Inverted Index 
	 */
	public InvertedIndex(){
		index = new TreeMap<>();
	}

	/**
	 * Writes the contents of the inverted index into a JSON format.
	 * 
	 * @param output
	 * 			path to write the Output to. 
	 * @return true if able to write to file false if otherwise
	 */
	public boolean writeJSONFile(Path output){
		return JSONWriter.writeJSON(output, index);
	}

	/**
	 * Checks to see if the index at key has an entry of that text file
	 * 
	 * @param word
	 * 			word in Inverted Index
	 * @param location
	 * 			Path where word was Found
	 * 
	 * @return true if it contains an entry of that text file mapped to the word 
	 */
	public boolean containsLocations(String word, String location){
		return index.get(word) != null && index.get(word).containsKey(location);
	}

	/**
	 * Checks to see if the index contains Word
	 * 
	 * @param word
	 * 			Word that could be in Inverted Index
	 * @return true if index contains word 
	 */
	public boolean containsWord(String word) {
		return index.containsKey(word);

	}
	/**
	 * Adds a word from a textfile into the inverted index with its
	 * corresponding position.
	 * 
	 * @param word 
	 * 				The word you want to add to the Index
	 * @param textFile 
	 * 				The textfile where the word was located
	 * @param position 
	 * 				The position in the textfile where the word was located
	 */
	public void add(String word, String textFile, int position) {
		if (!index.containsKey(word)) {
			index.put(word, new TreeMap<String, TreeSet<Integer>>());
			index.get(word).put(textFile, new TreeSet<Integer>());
		} 
		else if (!index.get(word).containsKey(textFile)) { 
			index.get(word).put(textFile, new TreeSet<Integer>());
		}
		index.get(word).get(textFile).add(position);
	}

	/**
	 * Checks the index to see if it is empty
	 * 
	 * @return true if empty, false if otherwise.
	 */
	public boolean isEmpty(){
		return index.isEmpty();
	}

	/**
	 * Returns the number of words stored in the index.
	 * 
	 * @return number of words
	 */
	public int words(){
		return index.size();
	}

	/**
	 * Adds everything from index2 to this index
	 * @param index2
	 * 			Index to add from
	 */
	public void addAll(InvertedIndex index2) {
		for (String key : index2.index.keySet()) {
			if (!index.containsKey(key)) {
				index.put(key, index2.index.get(key));
			} else {
				for (String path : index2.index.get(key).keySet()) {
					if (!index.get(key).containsKey(path)) {
						index.get(key).put(path, index2.index.get(key).get(path));
					} else {
						index.get(key).get(path).addAll(index2.index.get(key).get(path));
					}
				}
			}
		}
	}


	/**
	 * Returns an array list of exact results from a word parameter.
	 * 
	 * @param word
	 * 			Word to search for
	 * @return
	 * 			A sorted ArrayList of Results from the Inverted Index
	 */
	public ArrayList<SearchResult> exactSearch(String[] words){
		ArrayList<SearchResult> results = new ArrayList<>();

		HashMap<String, SearchResult> map = new HashMap<>();

		for(String word : words){
			if(index.containsKey(word)){
				checkResultMap(map, index.get(word));
			}
		}
		results.addAll(map.values());

		Collections.sort(results);
		return results;
	}

	/**
	 * Check to see if a SearchResult is in the given map.
	 * @param map
	 * 			Map to check
	 * @param freq
	 * 			Frequency of Search Result
	 * @param pos
	 * 			Position of Search Result
	 * @param loc
	 * 			Location of Search Result
	 */
	private void checkResultMap(HashMap<String, SearchResult> map, TreeMap<String, TreeSet<Integer>> innerMap){

		for(String location : innerMap.keySet()){
			int position = innerMap.get(location).first();
			int frequency = innerMap.get(location).size();

			if(map.containsKey(location)){
				map.get(location).addFreq(frequency);
				map.get(location).changePosition(position);
			}
			else{
				map.put(location, new SearchResult(location, position, frequency));
			}
		}

	}

	/**
	 * Returns a sorted ArrayList of results from the partial search. 
	 * @param word
	 * 			Word to perform partial search on
	 * @return
	 * 			A Sorted ArrayList of results from the Inverted Index
	 */
	public ArrayList<SearchResult> partialSearch(String[] words){
		ArrayList<SearchResult> results = new ArrayList<>();
		HashMap<String, SearchResult> map = new HashMap<>();

		for(String word: words){
			for(String keyWord: index.tailMap(word).keySet()){
				if(keyWord.startsWith(word)){
					checkResultMap(map, index.get(keyWord));
				}
				else{
					break;
				}
			}
		}
		results.addAll(map.values());

		Collections.sort(results);
		return results;
	}
}