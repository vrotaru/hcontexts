package hcontexts.xml;

import hcontexts.AbstractContext;
import javolution.text.TextBuilder;

public class XmlContext extends AbstractContext<XmlContext> {

	//
	// Fields
	//

	/* common */
	private final XmlContext	root;

	private int					start;
	private int					extent;

	/* root */
	private String				text;
	private TextBuilder			builder;

	//
	// Constructors
	//
	public XmlContext(String name) {
		this(name, null);
	}

	protected XmlContext(String name, XmlContext parent) {
		super(name, parent);

		root = parent == null ? this : parent.root;
		builder = parent == null ? new TextBuilder() : null;

		start = parent == null ? 0 : -1;
		extent = 0;
	}

	//
	// Methods
	//
	@Override
	protected XmlContext inner(String name) {
		return new XmlContext(name, this);
	}

	public void append(String seq) {
		if (parent == null) {
			text = null; // invalidate cached result
			builder.append(seq);
		}
		else {
			if (start < 0) {
				start = root.extent;
			}
			parent.append(seq);
		}
		extent += seq.length();
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
}
