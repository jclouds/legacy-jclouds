/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.savvis.vpdc.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

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
      if (current != null) {
         if (equalsOrSuffix(qName, "Type")) {
            builder.firewallType(current);
         } else if (equalsOrSuffix(qName, "IsEnabled")) {
            builder.isEnabled(Boolean.parseBoolean(current));
         } else if (equalsOrSuffix(qName, "Source")) {
            builder.source(current);
         } else if (equalsOrSuffix(qName, "Destination")) {
            builder.destination(current);
         } else if (equalsOrSuffix(qName, "Port")) {
            builder.port(current);
         } else if (equalsOrSuffix(qName, "Policy")) {
            builder.policy(current);
         } else if (equalsOrSuffix(qName, "Description")) {
            builder.description(current);
         } else if (equalsOrSuffix(qName, "Log")) {
            builder.isLogged(Boolean.parseBoolean(current));
         } else if (equalsOrSuffix(qName, "Tcp")) {
            builder.protocol("Tcp");
         } else if (qName.contains("Udp") || qName.contains("udp")) {
            builder.protocol("Udp");
         } else if (qName.contains("Icmp") || qName.contains("icmp") || qName.contains("Ping")
               || qName.contains("ping")) {
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
