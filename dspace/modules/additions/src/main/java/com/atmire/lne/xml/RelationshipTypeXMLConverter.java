package com.atmire.lne.xml;

import com.atmire.dspace.content.RelationshipType;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by: Antoine Snyers (antoine at atmire dot com)
 * Date: 17 Nov 2014
 */
public class RelationshipTypeXMLConverter {

    /**
     * log4j logger
     */
    private static Logger log = Logger.getLogger(RelationshipTypeXMLConverter.class);

    public List<RelationshipType> getTypes(InputStream inputStream) {
        List<RelationshipType> results = new LinkedList<RelationshipType>();

        RelationshipTypeContainerXML container = convert(inputStream);
        List<RelationshipTypeXML> types = container.getType();
        for (RelationshipTypeXML type : types) {

            String leftType = type.getLeftType();
            String rightType = type.getRightType();
            String leftLabel = type.getLeftLabel();
            String rightLabel = type.getRightLabel();
            RelationshipTypeXML.LeftCardinality leftCardinality1 = type.getLeftCardinality();
            Pair<Integer, Integer> leftCardinality = new MutablePair<Integer, Integer>(leftCardinality1.getMin(), leftCardinality1.getMax());
            RelationshipTypeXML.RightCardinality rightCardinality1 = type.getRightCardinality();
            Pair<Integer, Integer> rightCardinality = new MutablePair<Integer, Integer>(rightCardinality1.getMin(), rightCardinality1.getMax());

            RelationshipType relationshipType = new RelationshipType(leftType, rightType, leftLabel, rightLabel, leftCardinality, rightCardinality, null);

            Integer id = type.getId();
            if (id != null) { // if type == null, NullPointerException: cannot unbox null -> int
                type.setId(id);
            }

            results.add(relationshipType);
        }

        return results;
    }

    public RelationshipTypeContainerXML convert(InputStream inputStream) {
        RelationshipTypeContainerXML unmarshal = null;
        try {
            unmarshal = unmarshal(RelationshipTypeContainerXML.class, inputStream);
        } catch (JAXBException e) {
            log.error("Error", e);
        } catch (SAXException e) {
            log.error("Error", e);
        }
        return unmarshal;
    }

    public <T> T unmarshal(Class<T> docClass, InputStream inputStream)
            throws JAXBException, SAXException {
        String packageName = docClass.getPackage().getName();
        JAXBContext jc = JAXBContext.newInstance(packageName);


        Unmarshaller u = jc.createUnmarshaller();
        XMLReader reader = XMLReaderFactory.createXMLReader();
        NamespaceFilter filter = new NamespaceFilter("http://atmire.com/lne", true);
        filter.setParent(reader);


        JAXBElement<T> doc = (JAXBElement<T>) u.unmarshal(new SAXSource(filter, new InputSource(inputStream)));
        return doc.getValue();
    }

    public class NamespaceFilter extends XMLFilterImpl {

        private String usedNamespaceUri;
        private boolean addNamespace;

        //State variable
        private boolean addedNamespace = false;

        public NamespaceFilter(String namespaceUri,
                               boolean addNamespace) {
            super();

            if (addNamespace)
                this.usedNamespaceUri = namespaceUri;
            else
                this.usedNamespaceUri = "";
            this.addNamespace = addNamespace;
        }


        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            if (addNamespace) {
                startControlledPrefixMapping();
            }
        }


        @Override
        public void startElement(String arg0, String arg1, String arg2,
                                 Attributes arg3) throws SAXException {

            super.startElement(this.usedNamespaceUri, arg1, arg2, arg3);
        }

        @Override
        public void endElement(String arg0, String arg1, String arg2)
                throws SAXException {

            super.endElement(this.usedNamespaceUri, arg1, arg2);
        }

        @Override
        public void startPrefixMapping(String prefix, String url)
                throws SAXException {


            if (addNamespace) {
                this.startControlledPrefixMapping();
            } else {
                //Remove the namespace, i.e. don´t call startPrefixMapping for parent!
            }

        }

        private void startControlledPrefixMapping() throws SAXException {

            if (this.addNamespace && !this.addedNamespace) {
                //We should add namespace since it is set and has not yet been done.
                super.startPrefixMapping("", this.usedNamespaceUri);

                //Make sure we dont do it twice
                this.addedNamespace = true;
            }
        }

    }

}
