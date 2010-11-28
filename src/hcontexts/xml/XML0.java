package hcontexts.xml;

import hcontexts.Contexts;
import hcontexts.Property;

import java.io.InputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class XML0 {

	public static XmlContext read(InputStream in) throws XMLStreamException, FactoryConfigurationError,
			XmlEndTagMismatchException {
		XmlContext context = null;
		XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(in);

		String tagname = null;
		while (reader.hasNext()) {
			int elementType = reader.next();
			switch (elementType) {
			case XMLEvent.START_DOCUMENT:
				; // TODO:
				break;
			case XMLEvent.END_DOCUMENT:
				; // TODO:
				return context;
			case XMLEvent.START_ELEMENT:
				tagname = reader.getLocalName();
				context = new XmlContext(tagname);

				readAttributes(context, reader);
				readInnerContexts(context, reader);
				break;
			case XMLEvent.END_ELEMENT:
				// should be handled in readInnerContexts
				throw new XmlEndTagMismatchException();
			case XMLEvent.CHARACTERS:
			case XMLEvent.SPACE:
			case XMLEvent.CDATA:
				String fragment = reader.getText();
				context.append(fragment);
				break;
			default:
				break;
			}
		}

		return context;
	}

	private static void readAttributes(XmlContext context, XMLStreamReader reader) {
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			String propertyName = reader.getAttributeName(i).getLocalPart();
			String value = reader.getAttributeValue(i);

			Property<String> property = Contexts.propertyFor(propertyName, String.class);
			context.put(property, value);
		}
	}

	private static void readInnerContexts(XmlContext outerContext, XMLStreamReader reader) throws XMLStreamException,
			XmlEndTagMismatchException {
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
				readInnerContexts(context, reader);
				break;
			// End of outer element
			case XMLEvent.END_ELEMENT:
				tagname = reader.getLocalName();
				if (!tagname.equals(outerContext.name)) {
					System.err.printf("%s / %s / %s%n",
							tagname, context == null ? "" : context.name, outerContext.name);
					throw new XmlEndTagMismatchException();
				}
				return;
			case XMLEvent.CHARACTERS:
			case XMLEvent.SPACE:
			case XMLEvent.CDATA:
				String fragment = reader.getText();
				outerContext.append(fragment);
				break;
			default:
				break;
			}
		}
	}
}
