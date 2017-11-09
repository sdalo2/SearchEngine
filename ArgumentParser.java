import java.util.HashMap;
import java.util.Map;

public class ArgumentParser {

	/**
	 * Stores parsed flag, value pairs.
	 */
	private final Map<String, String> argumentMap;



	/**
	 * Creates a new and empty argument parser.
	 */
	public ArgumentParser(){
		argumentMap = new HashMap<>(); 
	}

	/**
	 * Returns the value associated with the argument.
	 * @param arg 
	 * 			Argument to get
	 * @return
	 * 			The value of the argument you want to get
	 */			
	public String get(String arg){
		return argumentMap.get(arg);
	}


	/**
	 * Checks if the flag exists.
	 * 
	 * @param flag
	 *			flag to check for
	 * @return {@code true} if flag exists
	 */
	public boolean hasFlag(String flag){
		return argumentMap.containsKey(flag); 
	}

	/**
	 * Checks if the flag exists and has a non-null value.
	 * 
	 * @param flag
	 *			flag whose associated value is to be checked
	 * @return {@code true} if the flag exists and has a non-null value
	 */
	public boolean hasValue(String flag){
		if(hasFlag(flag) && argumentMap.get(flag) != null){
			return true;
		}
		return false;
	}

	/**
	 * Parses the array of arguments into flag, value pairs. If an argument is a
	 * flag, tests whether the next argument is a value. If it is a value, then
	 * stores the flag, value pair. Otherwise, it stores the flag with no value.
	 * If the flag appears multiple times with different values, only the last
	 * value will be kept. Values without an associated flag are ignored.
	 * 
	 * @param args
	 *			arguments to parse into flag, value pairs
	 * @see #isFlag(String)
	 * @see #isValue(String)
	 */
	public void parseArguments(String[] args){
		for(int i = 0; i<args.length; i++){
			if(i < args.length-1){
				if(isFlag(args[i]) && isValue(args[i+1])){
					argumentMap.put(args[i], args[i+1]);
				}
				else if(isFlag(args[i]) && !isValue(args[i+1])){
					argumentMap.put(args[i], null);
				}
			}
			else{
				if(isFlag(args[i])){
					argumentMap.put(args[i], null);
				}
			}
		}
	}
	
	/**
	 * Tests whether the argument is a valid flag, i.e. it is not null, and
	 * after trimming it starts with a dash "-" character followed by at least 1
	 * character.
	 * 
	 * @param arg
	 *			argument to test
	 * @return {@code true} if the argument is a valid flag
	 */
	public boolean isFlag(String arg){
		if(arg == null){
			return false;
		}
		else{
			arg = arg.trim();
			if(arg.startsWith("-") && arg.length() > 1){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the value associated with the specified flag. May be {@code null}
	 * if a {@code null} value was stored or if the flag does not exist.
	 * 
	 * @param flag
	 *            flag whose associated value is to be returned
	 * @return value associated with the flag or {@code null} if the flag does
	 *         not exist
	 */
	public String getValue(String flag) {

		if(hasValue(flag)){
			return argumentMap.get(flag);
		}

		return null; 
	}

	/**
	 * Returns the value for a flag. If the flag is missing or the value is
	 * {@code null}, returns the default value instead.
	 * 
	 * @param flag
	 *            flag whose associated value is to be returned
	 * @param defaultValue
	 *            the default mapping of the flag
	 * @return value of flag or {@code defaultValue} if the flag is missing or
	 *         the value is {@code null}
	 */
	public String getValue(String flag, String defaultValue){
		String value = getValue(flag);
		if(value == null){
			return defaultValue;
		}
		return value;
	}

	/**
	 * Tests whether the argument is a valid value, i.e. it is not null, does
	 * not start with a dash "-" character, and is not empty after trimming.
	 * 
	 * @param arg
	 *			argument to test
	 * @return {@code true} if the argument is a valid value
	 */
	public boolean isValue(String arg){
		if(arg == null || arg.startsWith("-") || arg.trim().isEmpty()){
			return false;
		}
		return true;
	}
}
