import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.TreeSet;

public class JSONWriter {

	/**
	 * Returns a quoted version of the provided text.
	 * 
	 * @param text
	 * @return "text" in quotes
	 */
	private static String quote(String text) {
		return String.format("\"%s\"", text);
	}

	/**
	 * Returns n tab characters.
	 * 
	 * @param n
	 *            number of tab characters
	 * @return n tab characters concatenated
	 */
	private static String tab(int n) {
		char[] tabs = new char[n];
		Arrays.fill(tabs, '\t');
		return String.valueOf(tabs);
	}

	/**
	 * Writes the contents of the Result TreeMap in the JSON Format
	 * @param output
	 * 			Path to write to
	 * @param index
	 * 			Result Map
	 * @return
	 * 			True if successful, false if otherwise
	 */
	public static boolean writeSearchJSON(Path output, TreeMap<String, ArrayList<SearchResult>> index) {
		
		try(BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"));){
			writer.write("{\n");

			for(String words : index.keySet()){
				writer.write(JSONWriter.tab(1));
				writer.write(JSONWriter.quote(words) + ": [\n");
				
				for(int i = 0; i < index.get(words).size(); i++){
					writer.write(JSONWriter.tab(2) + "{\n");
					writer.write(JSONWriter.tab(3));
					writer.write(JSONWriter.quote("where")+ ": ");
					writer.write(JSONWriter.quote(index.get(words).get(i).getLocation()) + ",\n");

					writer.write(JSONWriter.tab(3));
					writer.write(JSONWriter.quote("count") + ": ");
					writer.write(Integer.toString(index.get(words).get(i).getFrequency()) + ",\n");

					writer.write(JSONWriter.tab(3));
					writer.write(JSONWriter.quote("index") + ": ");
					writer.write(Integer.toString(index.get(words).get(i).getPosition()) + "\n");

					if(i == index.get(words).size()-1){
						writer.write(JSONWriter.tab(2));
						writer.write("}\n");
					}
					else{
						writer.write(JSONWriter.tab(2));
						writer.write("},\n");
					}
				}
				writer.write(JSONWriter.tab(1));
				if(words.equalsIgnoreCase(index.lastKey())){
					writer.write("]\n");
				}
				else{
					writer.write("],\n");
				}
			}
			writer.write("}");
			return true;
		} catch (IOException e){
			System.err.println("Unable to write index to the path " + output + ".");
			return false;
		}
	}

	/**
	 * Writes the contents of the inverted index into a JSon format.
	 * 
	 * @param output
	 *            Path of the file to output to
	 * @param index
	 *            The Inverted Index
	 * @return true if able to write to file false if otherwise
	 */
	public static boolean writeJSON(Path output, TreeMap<String, TreeMap<String, TreeSet<Integer>>> index) {

		try (BufferedWriter writer = Files.newBufferedWriter(output, Charset.forName("UTF-8"));) {
			
			writer.write("{\n");

			for (String key : index.keySet()) { // loops through the words
//				System.out.println("word is " + key);
				writer.write(JSONWriter.tab(1));
				writer.write(JSONWriter.quote(key) + ": {\n");

				for (String file : index.get(key).keySet()) {
					writer.write(JSONWriter.tab(2));
					writer.write(JSONWriter.quote(file) + ": [\n");

					for (Integer position : index.get(key).get(file)) {
						writer.write(JSONWriter.tab(3));

						if (position == index.get(key).get(file).last()) {
							writer.write(position.toString() + "\n");
						} else {
							writer.write(position.toString() + ",\n");
						}
					}

					writer.write(JSONWriter.tab(2));

					if (file.equalsIgnoreCase(index.get(key).lastKey())) {
						writer.write("]\n");
					} else {
						writer.write("],\n");
					}
				}

				writer.write(JSONWriter.tab(1));

				if (key.equalsIgnoreCase(index.lastKey())) {
					writer.write("}\n");
					break;
				}
				writer.write("},\n");
			}
			writer.write("}\n");
			return true;

		} catch (IOException e) {
			System.err.println("Unable to write index to the path " + output + ".");
			return false;
		}
	}

}
