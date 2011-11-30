package hcontexts;

public class Property<T> {

	public final String name;
	public final Class<T> type;
	public final T defaultValue;
	
	private final String repr;
	
	Property(String name, Class<T> type, T defaultValue) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		
		repr = name + "<" + type.getName() + ">";
	}
	
	@Override
	public String toString() {
		return repr;
	}
}
