import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ThreadSafeInvertedIndex extends InvertedIndex{

	private ReadWriteLock lock;
	private static final Logger logger = LogManager.getRootLogger();

	public ThreadSafeInvertedIndex (){
		super();
		lock = new ReadWriteLock();
	}

	
	@Override
	public boolean writeJSONFile(Path output){
		logger.debug("Locking readWrite ");
		lock.lockReadOnly();
		try{
			return super.writeJSONFile(output);
		}
		finally{
			lock.unlockReadOnly();
		}
	}

	@Override
	public boolean containsLocations(String word, String location){
		lock.lockReadOnly();
		try{
			return super.containsLocations(word, location);
		}
		finally{
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public void addAll(InvertedIndex index){
		lock.lockReadWrite();
		try{
			super.addAll(index);
		}
		finally{
			lock.unlockReadWrite();
		}
	}

	@Override
	public boolean containsWord(String word){
		lock.lockReadOnly();
		try{
			return super.containsWord(word);
		}
		finally{
			lock.unlockReadOnly();
		}
	}

	@Override
	public void add(String word, String textFile, int position) {
		lock.lockReadWrite();
		try{
			super.add(word, textFile, position);
		}
		finally{
			lock.unlockReadWrite();
		}
	}

	@Override
	public boolean isEmpty(){
		lock.lockReadOnly();
		try{
			return super.isEmpty();
		}
		finally{
			lock.unlockReadOnly();
		}
	}

	@Override
	public int words(){
		lock.lockReadOnly();
		try{
			return super.words();
		}
		finally{
			lock.unlockReadOnly();
		}
	}

	@Override
	public ArrayList<SearchResult> partialSearch(String[] words){
		lock.lockReadOnly();
		try{
			return super.partialSearch(words);
		}
		finally{
			lock.unlockReadOnly();
		}
	}
	
	@Override
	public ArrayList<SearchResult> exactSearch(String[] words){
		lock.lockReadOnly();
		try{
			return super.exactSearch(words);
		}
		finally{
			lock.unlockReadOnly();
		}
	}
	
}
