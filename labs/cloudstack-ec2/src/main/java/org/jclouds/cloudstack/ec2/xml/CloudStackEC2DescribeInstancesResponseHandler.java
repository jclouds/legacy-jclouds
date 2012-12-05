package org.jclouds.cloudstack.ec2.xml;

import com.google.common.base.Supplier;
import com.google.common.collect.Sets;
import com.google.inject.Provider;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.location.Region;
import org.xml.sax.Attributes;

import javax.inject.Inject;
import java.util.Set;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 11/28/12
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackEC2DescribeInstancesResponseHandler extends
        DescribeInstancesResponseHandler {

    private Set<Reservation<? extends RunningInstance>> reservations = Sets.newLinkedHashSet();

    protected boolean inTagSet;
    protected boolean inVpcGroupSet;


    @Inject
    CloudStackEC2DescribeInstancesResponseHandler(DateCodecFactory dateCodecFactory,
                                     @Region Supplier<String> defaultRegion, Provider<RunningInstance.Builder> builderProvider) {
        super(dateCodecFactory, defaultRegion, builderProvider);
    }

    @Override
    public Set<Reservation<? extends RunningInstance>> getResult() {
        return reservations;
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes attrs) {
        if (equalsOrSuffix(qName, "groupSet")) {
            if(!inInstancesSet) {
                inGroupSet = true;
            } else {
                inVpcGroupSet = true;
            }
        }  else if (equalsOrSuffix(qName, "tagSet")) {
            inTagSet = true;
        }  else if(!inTagSet){
            super.startElement(uri, name, qName, attrs);
        }
    }

    @Override
    public void endElement(String uri, String name, String qName) {
        if (equalsOrSuffix(qName, "groupSet")) {
            if(!inInstancesSet) {
                inGroupSet = false;
            } else {
                inVpcGroupSet = false;
            }
        } else if (equalsOrSuffix(qName, "tagSet")) {
            inTagSet = false;
        } else if (equalsOrSuffix(qName, "rootDeviceType")) {
            builder.rootDeviceType(RootDeviceType.fromValue("ebs"));
        } else if (equalsOrSuffix(qName, "groupId") && !inGroupSet) {
            //  to remove null pointer exception because of vpcGroupSet
        }else if(!inTagSet){
            super.endElement(uri, name, qName);
        }
    }

    @Override
    protected boolean endOfInstanceItem() {
        return itemDepth <= 2 && inInstancesSet && !inProductCodes && !inGroupSet && !inTagSet && !inVpcGroupSet;
    }

    protected boolean endOfReservationItem() {
        return itemDepth == 1;
    }

    @Override
    protected void inItem() {
        if (endOfReservationItem()) {
            reservations.add(super.newReservation());
        } else {
            super.inItem();
        }
    }

}
