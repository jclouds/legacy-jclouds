package org.jclouds.aws.ec2.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.ReservedInstancesOffering;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class DescribeReservedInstancesOfferingResponseHandler extends
      ParseSax.HandlerWithResult<Set<ReservedInstancesOffering>> {

   private Set<ReservedInstancesOffering> reservedInstancesOfferings = Sets.newLinkedHashSet();
   private final ReservedInstancesOfferingHandler reservedInstancesOffering;

   @Inject
   public DescribeReservedInstancesOfferingResponseHandler(ReservedInstancesOfferingHandler reservedInstancesOffering) {
      this.reservedInstancesOffering = reservedInstancesOffering;
   }

   public Set<ReservedInstancesOffering> getResult() {
      return reservedInstancesOfferings;
   }

   @Override
   public HandlerWithResult<Set<ReservedInstancesOffering>> setContext(HttpRequest request) {
      reservedInstancesOffering.setContext(request);
      return super.setContext(request);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (!qName.equals("item"))
         reservedInstancesOffering.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("item")) {
         reservedInstancesOfferings.add(reservedInstancesOffering.getResult());
      }
      reservedInstancesOffering.endElement(uri, localName, qName);
   }

   public void characters(char ch[], int start, int length) {
      reservedInstancesOffering.characters(ch, start, length);
   }

}
