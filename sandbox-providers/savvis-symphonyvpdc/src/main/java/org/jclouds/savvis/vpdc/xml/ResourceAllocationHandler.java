package org.jclouds.savvis.vpdc.xml;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.savvis.vpdc.domain.ResourceAllocation;
import org.jclouds.savvis.vpdc.util.Utils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class ResourceAllocationHandler extends ParseSax.HandlerWithResult<ResourceAllocation> {
   protected StringBuilder currentText = new StringBuilder();

   protected ResourceAllocation.Builder builder = ResourceAllocation.builder();

   public ResourceAllocation getResult() {
      try {
         return builder.build();
      } finally {
         builder = ResourceAllocation.builder();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) {
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      String current = Utils.currentOrNull(currentText);
      if (current != null) {
         if (qName.endsWith("AllocationUnits")) {
            builder.allocationUnits(current);
         } else if (qName.endsWith("Description")) {
            builder.description(current);
         } else if (qName.endsWith("ElementName")) {
            builder.name(current);
         } else if (qName.endsWith("InstanceID")) {
            builder.id(Integer.parseInt(current));
         } else if (qName.endsWith("ResourceType")) {
            builder.type(ResourceAllocation.Type.fromValue(current));
         } else if (qName.endsWith("VirtualQuantity")) {
            builder.virtualQuantity(Long.parseLong(current));
         } else if (qName.endsWith("Caption")) {
            builder.caption(current);
         } else if (qName.endsWith("Connection")) {
            builder.connection(current);
         } else if (qName.endsWith("HostResource")) {
            builder.hostResource(current);
         }
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
