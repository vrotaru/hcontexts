package hcontexts.xml;

import hcontexts.Contexts;
import hcontexts.Property;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import javolution.context.LogContext;

public class XmlContextReader {

	//
	// Fields
	//
	private final boolean	condenseWhiteSpace;

	//
	// Constructors
	//
	public XmlContextReader() {
		this(false);
	}

	public XmlContextReader(boolean condenseWhiteSpace) {
		this.condenseWhiteSpace = condenseWhiteSpace;
	}

	//
	// Methods
	//
	public XmlContext read(InputStream inputStream) throws Exception {
		XmlContext context = null;
		XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);

		String tagname = null;
		while (reader.hasNext()) {
			switch (reader.next()) {
			case XMLEvent.START_DOCUMENT:
				LogContext.debug("Xml Document Start");
				break;
			case XMLEvent.END_DOCUMENT:
				LogContext.debug("Xml Document End");
				return context;
			case XMLEvent.START_ELEMENT:
				tagname = reader.getLocalName();
				context = new XmlContext(tagname);

				readAttributes(context, reader);
				readInnerElements(context, reader);
				break;
			case XMLEvent.END_ELEMENT:
				LogContext.error("Unexpected end tag: ", reader.getLocalName());
				throw new XmlEndTagMismatchException();
			case XMLEvent.CDATA:
			case XMLEvent.CHARACTERS:
				context.append(reader.getText());
				break;
			case XMLEvent.SPACE:
				if (condenseWhiteSpace) {
					context.append(" ");
				}
				else {
					context.append(reader.getText());
				}
				break;
			default:
				break;
			}
		}

		return context;
	}

	private void readAttributes(XmlContext context, XMLStreamReader reader) {
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			String propertyName = reader.getAttributeName(i).getLocalPart();
			String value = reader.getAttributeValue(i);

			Property<String> property = Contexts.propertyFor(propertyName, String.class);
			context.set(property, value);
		}

	}

	private void readInnerElements(XmlContext outerContext, XMLStreamReader reader) throws Exception {
		XmlContext context = null;
		String tagname = null;

		while (reader.hasNext()) {
			int elementType = reader.next();
			switch (elementType) {
			// Start of inner element
			case XMLEvent.START_ELEMENT:
				tagname = reader.getLocalName();
				context = outerContext.addInnerContext(tagname);

				readAttributes(context, reader);
				readInnerElements(context, reader);
				break;
			// End of outer element
			case XMLEvent.END_ELEMENT:
				tagname = reader.getLocalName();
				if (!tagname.equals(outerContext.name)) {
					LogContext.error("Tag Mismatch: ", context == null ? "" : context.name, " /= ", outerContext.name);
					throw new XmlEndTagMismatchException();
				}
				return;
			case XMLEvent.CHARACTERS:
			case XMLEvent.CDATA:
				outerContext.append(reader.getText());
				break;
			case XMLEvent.SPACE:
				if (condenseWhiteSpace) {
					outerContext.append(" ");
				}
				else {
					outerContext.append(reader.getText());
				}
			default:
				break;
			}
		}
	}
}
