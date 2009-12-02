package org.jclouds.vcloud.xml;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class VirtualSystemHandler extends ParseSax.HandlerWithResult<VirtualSystem> {
   private StringBuilder currentText = new StringBuilder();

   private String elementName;
   private int instanceID;
   private String virtualSystemIdentifier;
   private String virtualSystemType;

   private org.jclouds.vcloud.domain.VirtualSystem system;

   public org.jclouds.vcloud.domain.VirtualSystem getResult() {
      return system;
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {

      if (qName.equals("rasd:ElementName")) {
         this.elementName = currentText.toString().trim();
      } else if (qName.equals("rasd:InstanceID")) {
         this.instanceID = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("rasd:VirtualSystemIdentifier")) {
         this.virtualSystemIdentifier = currentText.toString().trim();
      } else if (qName.equals("rasd:VirtualSystemType")) {
         this.virtualSystemType = currentText.toString().trim();
      } else if (qName.equals("System")) {
         this.system = new org.jclouds.vcloud.domain.VirtualSystem(instanceID, elementName,
                  virtualSystemIdentifier, virtualSystemType);
         this.elementName = null;
         this.instanceID = -1;
         this.virtualSystemIdentifier = null;
         this.virtualSystemType = null;
      }

      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
