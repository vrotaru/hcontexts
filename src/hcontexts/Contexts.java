package hcontexts;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Contexts {

	// Static utility methods and fields
	private final static ConcurrentMap<String, Property<?>>	propertyPool	= new ConcurrentHashMap<String, Property<?>>();

	public static <T> Property<T> propertyFor(String name, Class<T> clazz) {
		return propertyFor(name, clazz, null);
	}

	public static <T> Property<T> propertyFor(String name, T defaultValue)
			throws PropertyWithADifferentDefaultValueAlreadyExists {
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) defaultValue.getClass();

		Property<T> property = propertyFor(name, clazz, defaultValue);
		if (!defaultValue.equals(property.defaultValue)) {
			throw new PropertyWithADifferentDefaultValueAlreadyExists();
		}
		return property;
	}

	private static <T> Property<T> propertyFor(String name, Class<T> clazz, T defaultValue) {
		final String id = Property.id(name, clazz);

		@SuppressWarnings("unchecked")
		Property<T> property = (Property<T>) propertyPool.get(id);
		if (property == null) {
			Property<T> newProperty = new Property<T>(name, clazz, defaultValue);
			@SuppressWarnings("unchecked")
			Property<T> oldProperty = (Property<T>) propertyPool.put(id, newProperty);
			if (oldProperty == null) {
				return newProperty;
			}
			else {
				return oldProperty;
			}
		}

		return property;
	}

	static <T> Property<T> tailProperty(Property<T> p) {
		final String id = "tail:" + Property.id(p.name, p.clazz);

		@SuppressWarnings("unchecked")
		Property<T> property = (Property<T>) propertyPool.get(id);
		if (property == null) {
			Property<T> newProperty = new Property<T>(p.name, p.clazz, p.defaultValue);
			@SuppressWarnings("unchecked")
			Property<T> oldProperty = (Property<T>) propertyPool.put(id, newProperty);
			if (oldProperty == null) {
				return newProperty;
			}
			else {
				return oldProperty;
			}
		}

		return property;
	}
}
