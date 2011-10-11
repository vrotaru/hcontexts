package hcontexts;

public interface ContextVisitor<CONTEXT extends AbstractContext<CONTEXT>> {

	void visit(CONTEXT context, int index, int length);
}
