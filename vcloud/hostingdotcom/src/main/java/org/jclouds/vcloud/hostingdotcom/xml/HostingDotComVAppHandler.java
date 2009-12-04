package org.jclouds.vcloud.hostingdotcom.xml;

import javax.inject.Inject;

import org.jclouds.vcloud.hostingdotcom.domain.HostingDotComVApp;
import org.jclouds.vcloud.hostingdotcom.domain.internal.HostingDotComVAppImpl;
import org.jclouds.vcloud.xml.ResourceAllocationHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VirtualSystemHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class HostingDotComVAppHandler extends VAppHandler {

   private String username;
   private String password;

   @Inject
   public HostingDotComVAppHandler(VirtualSystemHandler systemHandler,
            ResourceAllocationHandler allocationHandler) {
      super(systemHandler, allocationHandler);
   }

   public HostingDotComVApp getResult() {
      return new HostingDotComVAppImpl(id, name, location, status, size, networkToAddresses,
               operatingSystemDescription, system, allocations, username, password);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      super.startElement(uri, localName, qName, attributes);
      if (attributes.getIndex("key") != -1) {
         String key = attributes.getValue(attributes.getIndex("key"));
         if ("username".equals(key)) {
            username = attributes.getValue(attributes.getIndex("value"));
         } else if ("password".equals(key)) {
            password = attributes.getValue(attributes.getIndex("value"));
         }
      }
   }

}
