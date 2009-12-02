package org.jclouds.vcloud.xml;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.ResourceType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class ResourceAllocationHandler extends ParseSax.HandlerWithResult<ResourceAllocation> {
   private StringBuilder currentText = new StringBuilder();

   Integer address;
   Integer addressOnParent;
   String allocationUnits;
   String automaticAllocation;
   Boolean connected;
   String description;
   String elementName;
   int instanceID;
   Integer parent;
   String resourceSubType;
   ResourceType resourceType;
   long virtualQuantity = 1;
   String virtualQuantityUnits;

   private org.jclouds.vcloud.domain.ResourceAllocation allocation;

   public org.jclouds.vcloud.domain.ResourceAllocation getResult() {
      return allocation;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("rasd:Connection")) {
         connected = new Boolean(attributes.getValue(attributes.getIndex("connected")));
      } else if (qName.equals("rasd:HostResource")) {
         virtualQuantity = Long.parseLong(attributes.getValue(attributes.getIndex("capacity")));
         virtualQuantityUnits = "byte * 2^20";
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {

      if (qName.equals("rasd:Address")) {
         address = Integer.parseInt(currentOrNull());
      } else if (qName.equals("rasd:AddressOnParent")) {
         addressOnParent = Integer.parseInt(currentOrNull());
      } else if (qName.equals("rasd:AllocationUnits")) {
         allocationUnits = currentOrNull();
      } else if (qName.equals("rasd:Description")) {
         description = currentOrNull();
      } else if (qName.equals("rasd:ElementName")) {
         elementName = currentOrNull();
      } else if (qName.equals("rasd:InstanceID")) {
         instanceID = Integer.parseInt(currentOrNull());
      } else if (qName.equals("rasd:Parent")) {
         parent = Integer.parseInt(currentOrNull());
      } else if (qName.equals("rasd:ResourceSubType")) {
         resourceSubType = currentOrNull();
      } else if (qName.equals("rasd:ResourceType")) {
         resourceType = ResourceType.fromValue(currentOrNull());
      } else if (qName.equals("rasd:VirtualQuantity")) {
         virtualQuantity = Long.parseLong(currentOrNull());
      } else if (qName.equals("rasd:VirtualQuantityUnits")) {
         virtualQuantityUnits = currentOrNull();
      } else if (qName.equals("Item")) {
         if (allocationUnits != null)
            virtualQuantityUnits = allocationUnits;
         this.allocation = new ResourceAllocation(instanceID, elementName, description,
                  resourceType, resourceSubType, address, addressOnParent, parent, connected,
                  virtualQuantity, virtualQuantityUnits);
         address = null;
         addressOnParent = null;
         allocationUnits = null;
         automaticAllocation = null;
         connected = null;
         description = null;
         elementName = null;
         instanceID = -1;
         parent = null;
         resourceSubType = null;
         resourceType = null;
         virtualQuantity = 1;
         virtualQuantityUnits = null;
      }

      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
