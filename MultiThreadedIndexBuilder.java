import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MultiThreadedIndexBuilder {
	
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
	 * Initializes work queue and Inverted Index
	 * @param index
	 * 			Thread Safe inverted Index
	 */
	public MultiThreadedIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue queue){
		minions = queue;
		this.index = index;
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

	/**
	 * Traverses directories. When a text file is found it puts it into the Work Queue
	 * @param path
	 * 			Path to traverse
	 */
	public void traverseDirectories(Path path){
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(path)){
			for(Path fileInStream: stream){
				if(Files.isDirectory(fileInStream)){
					traverseDirectories(fileInStream);
				}
				else if(fileInStream.getFileName().toString().toLowerCase().endsWith(".txt")){
					minions.execute(new Task(fileInStream));
				}
			}
		} catch(IOException e){
			logger.debug("An exception occured trying to traverse the directories.");
		}
	}

	/**
	 * 
	 * Inner class that is added to the work queue as a task
	 *
	 */
	private class Task implements Runnable{

		/**
		 * File to parse
		 */
		private Path file;
		
		/**
		 * Local Inverted Index
		 */
		private InvertedIndex localIndex;

		/**
		 * Initializes the inverted index and file
		 * @param file
		 */
		public Task(Path file){
			logger.debug("Minion created for {}", file);
			this.file = file;
			localIndex = new InvertedIndex();
		}
		
		@Override
		public void run() {
			InvertedIndexBuilder.parse(file, localIndex); 
			index.addAll(localIndex);
		}
		
	}
}
