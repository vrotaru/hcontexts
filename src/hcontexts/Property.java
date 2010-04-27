package hcontexts;

public class Property<T> {

	public final String		name;
	public final Class<T>	clazz;
	public final T			defaultValue;

	Property(String name, Class<T> clazz, T defaultValue) {
		this.name = name;
		this.clazz = clazz;
		this.defaultValue = defaultValue;
	}

	static <T> String id(String name, Class<T> clazz) {
		if (clazz == String.class) {
			return "@" + name;
		}
		else if (clazz == Context.class) {
			return "#" + name;
		}
		else {
			return clazz.getName() + "@" + name;
		}
	}

}
