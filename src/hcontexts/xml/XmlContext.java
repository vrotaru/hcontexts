package hcontexts.xml;

import hcontexts.AbstractContext;

public class XmlContext extends AbstractContext<XmlContext> {

	// Root element
	private String text;
	private StringBuilder builder;
	
	// Common
	private int start;
	private int extent;
	private XmlContext root;
	
	public XmlContext(String name) {
		this(name, null);
	}
	
	private XmlContext(String name, XmlContext parentContext) {
		super(name, parentContext);
		
		extent = 0;
		if (parentContext == null) {
			root = this;
			
			builder = new StringBuilder();
			start = 0;
		}
		else {
			root  = parentContext.root;
			start = -1;
		}
	}
	
	@Override
	protected XmlContext inner(String name) {
		XmlContext context = new XmlContext(name, this);
		
		return context;
	}	
	
	public String getText() {
		if (parent == null) {
			if (text == null) {
				text = builder.toString();
			}
			return text;
		}
		else {
			return root.getText().substring(start, start + extent);
		}
	}
	
	public void append(String text) {
		extent += text.length();
		
		if (parent == null) {			
			this.text = null; // invalidate cached result.
			builder.append(text);
		}
		else {
			if (start < 0) {
				start = root.extent;
			}
			parent.append(text);
		}
	}
}
