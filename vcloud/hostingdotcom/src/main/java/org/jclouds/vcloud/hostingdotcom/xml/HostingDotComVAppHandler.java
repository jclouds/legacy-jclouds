package org.jclouds.vcloud.hostingdotcom.xml;

import javax.inject.Inject;

import org.jclouds.vcloud.hostingdotcom.domain.HostingDotComVApp;
import org.jclouds.vcloud.hostingdotcom.domain.internal.HostingDotComVAppImpl;
import org.jclouds.vcloud.xml.ResourceAllocationHandler;
import org.jclouds.vcloud.xml.VAppHandler;
import org.jclouds.vcloud.xml.VirtualSystemHandler;
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
      return new HostingDotComVAppImpl(id, name, location, status, networkToAddresses,
               operatingSystemDescription, system, allocations, username, password);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("hmsns:username")) {
         username = currentText.toString().trim();
      } else if (qName.equals("hmsns:password")) {
         password = currentText.toString().trim();
      } else {
         super.endElement(uri, localName, qName);
      }
      currentText = new StringBuilder();
   }

}
