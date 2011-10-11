package hcontexts.json;

import hcontexts.AbstractContext;
import hcontexts.ContextVisitor;
import hcontexts.InnerVisitor;
import hcontexts.Property;
import javolution.text.TextBuilder;

public class ToJsonStringVisitor<CONTEXT extends AbstractContext<CONTEXT>> implements ContextVisitor<CONTEXT>,
		InnerVisitor<CONTEXT> {

	private TextBuilder	builder	= new TextBuilder();

	@Override
	public void visit(CONTEXT context, int index, int length) {
		boolean asArray = length > 1;

		if (asArray && index == 0) {
			builder.append('[');
		}
		builder.append('{');
		context.acceptInnner(this);
		builder.append('}');

		if (index < length - 1) {
			builder.append(',');
		}
		if (asArray && index == length - 1) {
			builder.append(']');
		}
	}

	private void appendName(String name) {
		builder.append('"');
		builder.append(name);
		builder.append('"');
		builder.append(':');
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void visitInner(Property<?> property, Object value, int index, int length) {
		appendName(property.name);
		if (AbstractContext.class.isAssignableFrom(property.type)) {
			ToJsonStringVisitor innerVisitor = new ToJsonStringVisitor();
			((AbstractContext) value).accept(innerVisitor);

			builder.append(innerVisitor.builder.toText());
		}
		else if (value.getClass() == String.class) {
			builder.append('"');
			builder.append(value);
			builder.append('"');
		}
		else {
			builder.append(value);
		}

		if (index < length - 1) {
			builder.append(',');
		}
	}
}
