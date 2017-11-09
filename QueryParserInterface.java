import java.nio.file.Path;

public interface QueryParserInterface {
	public void parseQuery(Path query, boolean search);
	
	public boolean searchOutput(Path output);
}
