package hcontexts;

import javolution.util.FastComparator;
import javolution.util.FastMap;
import javolution.util.FastTable;

public abstract class AbstractContext<SELF_TYPE extends AbstractContext<SELF_TYPE>> {

	//
	// Fields
	//
	public final String						name;
	public final SELF_TYPE					parent;

	public final FastTable<SELF_TYPE>		siblings;

	@SuppressWarnings("rawtypes")
	private final FastMap<Property, Object>	properties	= new FastMap<Property, Object>()
																.setKeyComparator(FastComparator.IDENTITY);

	//
	// Constructor
	//
	protected AbstractContext(String name) {
		this(name, null);
	}

	protected AbstractContext(String name, SELF_TYPE parent) {
		this.name = name;
		this.parent = parent;

		siblings = new FastTable<SELF_TYPE>();
		siblings.add(self());
	}

	//
	// Methods
	//	
	protected abstract SELF_TYPE inner(String name);

	@SuppressWarnings("unchecked")
	public final SELF_TYPE self() {
		return (SELF_TYPE) this;
	}

	@SuppressWarnings("unchecked")
	public final <T> T get(Property<T> property) {
		T value = (T) properties.get(property);
		if (value == null) {
			return property.defaultValue;
		}
		return value;
	}

	public final <T> void set(Property<T> property, T value) {
		properties.put(property, value);
	}

	public final SELF_TYPE addInnerContext(String contextName) {
		@SuppressWarnings("unchecked")
		Property<SELF_TYPE> innerProperty = (Property<SELF_TYPE>) Contexts.propertyFor(contextName, self().getClass());

		SELF_TYPE newInnner = inner(contextName);
		SELF_TYPE oldInner = get(innerProperty);
		if (oldInner == null) {
			oldInner = newInnner;
			set(innerProperty, newInnner);

			return oldInner;
		}

		oldInner.siblings.add(newInnner);
		return newInnner;
	}

	//
	// ContextVisitor support. Calls the visitor#visit with all the siblings.
	//
	public void accept(ContextVisitor<SELF_TYPE> visitor) {

		FastTable<SELF_TYPE> siblings = self().siblings;
		int i = 0, n = siblings.size();
		for (; i < n; ++i) {
			SELF_TYPE sibling = siblings.get(i);
			visitor.visit(sibling, i, n);
		}
	}

	//
	// InnerVisitor support. Calls visitor#visitInner with all properties.
	//
	public void acceptInnner(InnerVisitor<SELF_TYPE> visitor) {
		int index = 0;
		int length = properties.size();

		// Get the first real entry.
		@SuppressWarnings("rawtypes")
		FastMap.Entry<Property, Object> entry = properties.head().getNext();
		@SuppressWarnings("rawtypes")
		FastMap.Entry<Property, Object> end = properties.tail();
		while (entry != end) {
			Property<?> key = entry.getKey();
			if (key != null) {
				visitor.visitInner(key, entry.getValue(), index++, length);
			}

			entry = entry.getNext();
		}
	}
}
