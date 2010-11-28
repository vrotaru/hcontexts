package hcontexts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractContext<SELF extends AbstractContext<SELF>> implements Iterable<SELF> {
	private static final int				TAIL_LIMIT	= 7;

	private final Map<Property<?>, Object>	properties	= new HashMap<Property<?>, Object>();

	protected String name;	
	protected SELF parent;
	
	private SELF next;
	
	public AbstractContext(String name) {
		this.name  = name;
	}
	
	@SuppressWarnings("unchecked")
	protected SELF self() {
		return (SELF) this;
	}
	
	protected abstract SELF inner(String name);
	
	public <T> T get(Property<T> property) {
		@SuppressWarnings("unchecked")
		T value = (T) properties.get(property);
		if (value == null) {
			return property.defaultValue;
		}
		return value;
	}

	public <T> SELF put(Property<T> property, T value) {
		properties.put(property, value);
		return self();
	}
	
	@SuppressWarnings("unchecked")
	public SELF addInnerContext(String contextName) {
		SELF context = inner(contextName);
		
		Property<SELF> contextProperty = (Property<SELF>) Contexts.propertyFor(contextName, context.getClass());
		append(contextProperty, context);

		return context;
	}
	
	private void append(Property<SELF> contextProperty, SELF newContext) {
		SELF context = get(contextProperty);
		if (context == null) {
			put(contextProperty, newContext);
		}
		else {
			Property<SELF> tailProperty = Contexts.tailProperty(contextProperty);
			SELF tailContext = get(tailProperty);
			if (tailContext == null) {
				int limit = 0;
				while (context.next != null) {
					limit++;
					context = context.next;
				}
				context.next = newContext;
				// the limit > TAIL_LIMIT case can happen if the same context is accessed from
				// multiple threads. Which is almost surely a horrible idea.
				if (limit >= TAIL_LIMIT) {
					put(tailProperty, newContext);
				}
			}
			else {
				tailContext.next = newContext;
				put(tailProperty, newContext);
			}
		}
	}
	
	@Override
	public Iterator<SELF> iterator() {
		Iterator<SELF> iterator = new Iterator<SELF>() {
			SELF currentContext = self();
			@Override
			public boolean hasNext() {
				return currentContext != null;
			}

			@Override
			public SELF next() {
				SELF context = currentContext;
				currentContext = currentContext.next;
				return context;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("Remove method not supported");
			}
		};
		return iterator;
	}
}
