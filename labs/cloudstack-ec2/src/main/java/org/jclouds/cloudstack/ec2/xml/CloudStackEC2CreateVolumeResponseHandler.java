package org.jclouds.cloudstack.ec2.xml;

import com.google.common.base.Supplier;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.xml.sax.Attributes;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 12/4/12
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackEC2CreateVolumeResponseHandler extends CreateVolumeResponseHandler {

    private boolean inTagSet;

    @Inject
    protected CloudStackEC2CreateVolumeResponseHandler(DateCodecFactory dateCodecFactory, @Region Supplier<String> defaultRegion, @Zone Supplier<Map<String, Supplier<Set<String>>>> regionToZonesSupplier, @Zone Supplier<Set<String>> zonesSupplier) {
        super(dateCodecFactory, defaultRegion, regionToZonesSupplier, zonesSupplier);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("tagSet")) {
            inTagSet = true;
        } else if (!inTagSet) {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("tagSet")) {
            inTagSet = false;
        } else if (!inTagSet) {
            super.endElement(uri, localName, qName);
        }
    }
}
