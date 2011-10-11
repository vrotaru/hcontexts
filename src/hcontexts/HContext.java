package hcontexts;

public class HContext extends AbstractContext<HContext> {

	public HContext(String name) {
		super(name);
	}
	
	protected HContext(String name, HContext parent) {
		super(name, parent);
	}

	@Override
	protected HContext inner(String name) {		
		return new HContext(name, this);
	}

}
