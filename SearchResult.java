
public class SearchResult implements Comparable<SearchResult>{

	/**
	 * String representation of the filepath
	 */
	private final String location; // path to the file
	
	/**
	 * First position found
	 */
	private int position; // position in file
	
	/**
	 * Number of times this object was Found
	 */
	private int frequency; //number of times found in file 

	/**
	 * Stores a single Search Result Object
	 * @param loc
	 * 			String representation of the file path
	 * @param position
	 * 			First position found
	 * @param frequency
	 * 			Number of times this object was Found
	 */
	public SearchResult(String loc, int position, int frequency){
		this.location = loc;
		this.position = position;
		this.frequency = frequency;
	}
	
	
	@Override
	/**
	 * Compares Search Results based on Frequency. If equal, then it compares
	 * based on position. If equal, then it compares locations.
	 */
	public int compareTo(SearchResult arg0){
		int freqCompare = Integer.compare(arg0.getFrequency(), frequency);
		
		if(freqCompare == 0){
			int posCompare = Integer.compare(position, arg0.getPosition());
			if(posCompare == 0){
				return location.compareTo(arg0.getLocation());
			}
			return posCompare;
		}
		return freqCompare;
	}

	/**
	 * Returns the location of the file of the search result
	 * @return
	 * 			Location
	 */
	public String getLocation(){
		return location;
	}

	/**
	 * Returns the position of the Search Result
	 * @return
	 * 			Position
	 */
	public int getPosition(){
		return position;
	}

	/**
	 * Returns the frequency of the search Result
	 * @return
	 * 			Frequency
	 */
	public int getFrequency(){
		return frequency;
	}

	/**
	 * Changes position to another if it is smaller
	 * @param otherPos
	 * 			Other Position Value
	 */
	public void changePosition(int otherPos){
		position = Integer.min(position, otherPos);
	}
	
	/**
	 * Adds frequency to the search result
	 * @param freq
	 * 			Frequency to add
	 */
	public void addFreq(int freq){
		frequency += freq;
	}
	
}