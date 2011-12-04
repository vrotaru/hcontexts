package hcontexts;

import javolution.context.ObjectFactory;

public class HContext extends AbstractContext<HContext> {

	protected HContext(String name) {
		super(name);
	}
	
	protected HContext(String name, HContext parent) {
		super(name, parent);
	}

	@Override
	protected HContext inner(String name) {		
		return new HContext(name, this);
	}
	
	public static HContext create(String name) {
		contextName.set(name);
		contextParent.set(null);
		
		return FACTORY.object();
	}
	
	private static ThreadLocal<String> contextName  = new ThreadLocal<String>();
	private static ThreadLocal<HContext> contextParent = new ThreadLocal<HContext>();
	
	private static ObjectFactory<HContext> FACTORY = new ObjectFactory<HContext>() {
		
		@Override
		protected HContext create() {
			return new HContext(contextName.get(), contextParent.get());
		}
	};

}
