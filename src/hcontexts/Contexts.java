package hcontexts;

import java.util.Map;

import javolution.context.LogContext;
import javolution.util.FastComparator;
import javolution.util.FastMap;

public class Contexts {

	private final static Map<String, Property<?>>	propertyPool	= new FastMap<String, Property<?>>()
																			.setKeyComparator(FastComparator.IDENTITY)
																			.shared();

	public static <T> Property<T> propertyFor(String name, Class<T> type) {
		return propertyFor(name, type, null);
	}

	public static <T> Property<T> propertyFor(String name, T defaultValue) {
		if (defaultValue == null) {
			throw new IllegalArgumentException("default value cannot be null");
		}
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) defaultValue.getClass();

		return propertyFor(name, clazz, defaultValue);
	}

	private static <T> Property<T> propertyFor(String name, Class<T> type, T defaultValue) {
		LogContext.debug("Retrieving property with name: ", name, " and type: ", type);

		String id = id(name, type);
		synchronized (id) {
			@SuppressWarnings("unchecked")
			Property<T> property = (Property<T>) propertyPool.get(id);
			if (property == null) {
				LogContext.debug("Creating new property...");
				property = new Property<T>(name, type, defaultValue);
				propertyPool.put(id, property);
			}
			else if (distinct(defaultValue, property.defaultValue)) {
				throw new PropertyWithADifferentDefaultValueAlreadyExists();
			}

			return property;
		}
	}

	private static boolean distinct(Object a, Object b) {
		if (a == null) {
			return b != null;
		}
		return !a.equals(b);
	}

	/**
	 * @param name
	 *            Property name;
	 * @param clazz
	 *            Type of referenced values
	 * @return An 'interned' id
	 */
	static String id(String name, Class<?> clazz) {
		String uninternedId = canonicalName(clazz) + "@" + name;
		return uninternedId.intern();
	}

	private static String canonicalName(Class<?> clazz) {
		String value = clazz.getCanonicalName();
		while (value == null) {
			clazz = clazz.getSuperclass();
			value = clazz.getCanonicalName();
		}
		return value;
	}
}
