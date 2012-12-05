package org.jclouds.cloudstack.ec2.xml;

import com.google.common.base.Supplier;
import org.jclouds.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.location.Region;
import org.xml.sax.Attributes;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 11/28/12
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackEC2DescribeImagesResponseHandler extends DescribeImagesResponseHandler {

    private boolean inTagSet;

    @Inject
    public CloudStackEC2DescribeImagesResponseHandler(@Region Supplier<String> defaultRegion) {
        super(defaultRegion);
    }

    public void startElement(String uri, String name, String qName, Attributes attrs) {
        if (qName.equals("tagSet")) {
            inTagSet = true;
        } else if(!inTagSet){
            super.startElement(uri, name, qName, attrs);
        }
    }

    public void endElement(String uri, String name, String qName) {
        if (qName.equals("tagSet")) {
            inTagSet = false;
        } else if (!inTagSet) {
            super.endElement(uri, name, qName);
        }
    }
}
