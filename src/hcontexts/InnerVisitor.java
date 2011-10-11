package hcontexts;

public interface InnerVisitor<CONTEXT extends AbstractContext<CONTEXT>> {

	void visitInner(Property<?> property, Object value, int index, int length);
}
