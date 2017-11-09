import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;


public class InvertedIndexBuilder {
	
	/**
	 * Traverses through the path given, and parsing text files when seen. 
	 * @param path
	 * 				Path to traverse
	 * @param index
	 * 				The Inverted Index Data Structure
	 */
	private static void traverseHelper(Path path, InvertedIndex index){
		try(DirectoryStream<Path> fileName = Files.newDirectoryStream(path))
		{
			for(Path file : fileName){
				if(Files.isDirectory(file)){
					traverseHelper(file, index);
				}
				else if(file.getFileName().toString().toLowerCase().endsWith(".txt")){
					parse(file, index);
				}
			}
		}
		catch (IOException e) {
			System.err.println("Unable to traverse the path: " + path);
		}
	}
	
	/**
	 * Traverses through the directory given. 
	 * @param directory
	 * 				The directory to traverse
	 * @param index
	 * 				The Inverted Index Data structure
	 */
	public static void traverse(Path directory, InvertedIndex index){
		if(Files.isDirectory(directory)){
			traverseHelper(directory, index);
		}
		else{
			parse(directory, index);
		}
	}
	
	/**
	 * Reads a file and adds the positions of each word to the Inverted Index
	 * 
	 * @param file
	 * 		 	Path to the file you want to parse
	 * @param index
	 * 			Inverted Index Data structure to save parsed words 
	 */
	public static void parse(Path file, InvertedIndex index){
		if(Files.isDirectory(file)){
			traverse(file, index);
		}
		else{
			try(BufferedReader reader = Files.newBufferedReader(file, Charset.forName("UTF-8"));){
				
				String line = null;
				int position = 1;
				
				while((line = reader.readLine()) != null){
					line = line.replaceAll("\\p{Punct}+", "");
					
					String[] words = line.split("\\s+");
					
					for(int i = 0; i < words.length; i++){
						words[i] = words[i].trim();
						if(!words[i].isEmpty()){
							index.add(words[i].toLowerCase(), file.toString(), position);
							position++;
						}
						else{
							continue;
						}
					}
				}
			} catch (IOException e) {
				System.err.println("Unable to read the file: " + file);
			}
		}
	}
}