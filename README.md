Contexts, can't do without them
===============================

1. In Java at least.
 
2. Pretty much every framework, I know of, offers to objects it instantiates and manages a context Object to save and retrieve atrributes needed at runtime. And almost invariably the keys to set/get the said attributes are plain strings. Which then need to be cast to their types.

3. This is project is an attempt to see if it would be handier to access those "Context Attributes" like this

	Property<FooBar> _foo = Context.propertyFor("foo", FooBar.class)
	FooBar foobar = context.get(_foo);

4. Properties can have default values.

5. Traversal code is written using 2 visitor interfaces.

	public interface ContextVisitor<CONTEXT extends AbstractContext<CONTEXT>> {
		void visit(CONTEXT context, int index, int length);
	}
	public interface InnerVisitor<CONTEXT extends AbstractContext<CONTEXT>> {
		void visitInner(Property<?> property, Object value, int index, int length);
	}
	
The first one loops over the siblings of current context, the second one over context properties. 

6. It turned out that (and with a little twist) Contexts can be used to slurp in XML files with a simple XML.read(InputStream stream)

7. I'm using Javolution's (http://javolution.org) list, map, string/text instead of standard ones, because I found them to be a lot faster.

8. I've written a class called ToJsonStringVisitor and survived it. 