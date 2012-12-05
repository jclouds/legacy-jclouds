package org.jclouds.cloudstack.ec2.xml;

import org.jclouds.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.ec2.xml.DescribeVolumesResponseHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 12/4/12
 * Time: 2:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackEC2DescribeVolumesResponseHandler extends DescribeVolumesResponseHandler {

    private boolean inTagSet;

    @Inject
    public CloudStackEC2DescribeVolumesResponseHandler(CreateVolumeResponseHandler volumeHandler) {
        super(volumeHandler);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equals("tagSet")) {
            inTagSet = true;
        } else if(!inTagSet) {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
       if (qName.equals("tagSet")) {
            inTagSet = false;
        } else if(!inTagSet) {
            super.endElement(uri, localName, qName);
        }
    }
}
