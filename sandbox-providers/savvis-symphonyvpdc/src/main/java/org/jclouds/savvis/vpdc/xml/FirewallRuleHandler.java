/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.savvis.vpdc.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.savvis.vpdc.domain.FirewallRule;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Kedar Dave
 */
public class FirewallRuleHandler extends ParseSax.HandlerWithResult<FirewallRule> {
   protected StringBuilder currentText = new StringBuilder();
   private FirewallRule.Builder builder = FirewallRule.builder();

   public FirewallRule getResult() {
      try {
         return builder.build();
      } finally {
         builder = FirewallRule.builder();
      }
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {

   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
	  String current = currentOrNull(currentText);
	  if(current != null){
		  if (qName.endsWith("Type")) {
			  builder.firewallType(current);
		  } else if (qName.endsWith("IsEnabled")) {
			  builder.isEnabled(Boolean.parseBoolean(current));
		  } else if (qName.endsWith("Source")) {
			  builder.source(current);
		  } else if (qName.endsWith("Destination")) {
			  builder.destination(current);
		  } else if (qName.endsWith("Port")) {
			  builder.port(current);
		  } else if (qName.endsWith("Policy")) {
			  builder.policy(current);
		  } else if (qName.endsWith("Description")) {
			  builder.description(current);
		  } else if (qName.endsWith("Log")) {
			  builder.isLogged(Boolean.parseBoolean(current));
		  } else if (qName.endsWith("Tcp")) {
			  builder.protocol("Tcp");
		  } else if (qName.contains("Udp") || qName.contains("udp")) {
			  builder.protocol("Udp");
		  } else if (qName.contains("Icmp") || qName.contains("icmp") || 
				  qName.contains("Ping") || qName.contains("ping")) {
			  builder.protocol("Icmp-ping");
		  }
	  }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
