package hcontexts.xml;

import hcontexts.Context;

public class XContext extends Context {

	private final StringBuilder	textbBuilder;

	public XContext(String name) {
		this(name, null);
	}

	protected XContext(String name, Context parent) {
		super(name, parent);
		textbBuilder = new StringBuilder();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T extends Context> T newIntance(String name, T parent) {
		return (T) new XContext(name, parent);
	}

	public void appendText(String moreText) {
		textbBuilder.append(moreText);
	}

	public String getText() {
		return textbBuilder.toString();
	}
}
