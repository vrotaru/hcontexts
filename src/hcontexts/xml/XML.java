package hcontexts.xml;

import hcontexts.Context;
import hcontexts.Property;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class XML {

	public static XContext read(InputStream inputStream) throws XMLStreamException, FactoryConfigurationError,
			XmlEndTagMismatchException {
		XContext context = null;
		XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);

		String tagname = null;
		while (reader.hasNext()) {
			int kind = reader.next();
			switch (kind) {
			case XMLEvent.START_DOCUMENT:
				; // TODO: ??
				break;
			case XMLEvent.END_DOCUMENT:
				; // TODO: Anything else?
				return context;
			case XMLEvent.START_ELEMENT:
				QName qname = reader.getName();
				tagname = tagName(qname);

				context = new XContext(tagname);

				readAttributes(context, reader);
				readInnerContexts(context, reader);
				break;
			case XMLEvent.CHARACTERS:
			case XMLEvent.CDATA:
				context.appendText(reader.getText());
				break;
			case XMLEvent.END_ELEMENT:
				tagname = reader.getLocalName();
				if (!tagname.equals(context.name)) {
					throw new XmlEndTagMismatchException();
				}
				break;
			default:
				break;
			}
		}

		return context;
	}

	private static void readAttributes(XContext context, XMLStreamReader reader) {
		for (int i = 0; i < reader.getAttributeCount(); i++) {
			QName qname = reader.getAttributeName(i);
			String value = reader.getAttributeValue(i);
			String propertyName = tagName(qname);

			Property<String> property = Context.propertyFor(propertyName, String.class);
			context.put(property, value);
		}
	}

	private static String tagName(QName qname) {
		String propertyName;
		if (qname.getPrefix() == null) {
			propertyName = qname.getLocalPart();
		}
		else {
			propertyName = qname.getPrefix() + ":" + qname.getLocalPart();
		}
		return propertyName;
	}

	private static void readInnerContexts(XContext outerContext, XMLStreamReader reader) throws XMLStreamException,
			XmlEndTagMismatchException {
		XContext context = null;
		String tagname = null;
		while (reader.hasNext()) {
			int kind = reader.next();
			switch (kind) {
			case XMLEvent.START_ELEMENT:
				QName qname = reader.getName();
				tagname = tagName(qname);

				context = outerContext.addInnerContext(tagname);

				readAttributes(context, reader);
				readInnerContexts(context, reader);

				break;
			case XMLEvent.CHARACTERS:
			case XMLEvent.CDATA:
				context.appendText(reader.getText());
				break;
			case XMLEvent.END_ELEMENT:
				tagname = reader.getLocalName();
				if (!tagname.equals(context.name)) {
					throw new XmlEndTagMismatchException();
				}
				break;
			default:
				break;
			}
		}
	}
}
