package org.jclouds.savvis.vpdc.xml;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.savvis.vpdc.domain.vapp.Network;
import org.jclouds.savvis.vpdc.domain.vapp.NetworkSection;
import org.jclouds.savvis.vpdc.util.Utils;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * @author Adrian Cole
 */
public class NetworkSectionHandler extends ParseSax.HandlerWithResult<NetworkSection> {
   protected StringBuilder currentText = new StringBuilder();

   protected String info;
   protected String name;
   protected String description;

   protected Builder<Network> networks = ImmutableSet.<Network> builder();

   public NetworkSection getResult() {
      try {
         return new NetworkSection(info, networks.build());
      } finally {
         this.info = null;
         this.networks = ImmutableSet.<Network> builder();
      }
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = Utils.cleanseAttributes(attrs);
      if (qName.endsWith("Network")) {
         name = attributes.get("name");
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (qName.endsWith("Info")) {
         this.info = Utils.currentOrNull(currentText);
      } else if (qName.endsWith("Description")) {
         this.description = Utils.currentOrNull(currentText);
      } else if (qName.endsWith("Network")) {
         this.networks.add(new Network(name, description));
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
