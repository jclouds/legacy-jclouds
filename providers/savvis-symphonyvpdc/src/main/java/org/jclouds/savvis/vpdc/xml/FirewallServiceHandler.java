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
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.savvis.vpdc.domain.FirewallService;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Kedar Dave
 */
public class FirewallServiceHandler extends ParseSax.HandlerWithResult<FirewallService> {
   protected StringBuilder currentText = new StringBuilder();
   private FirewallRuleHandler firewallRuleHandler;
   private FirewallService.Builder builder = FirewallService.builder();
   boolean inFirewallService;
   boolean inFirewallRule;

   @Inject
   public FirewallServiceHandler(FirewallRuleHandler firewallRuleHandler) {
      this.firewallRuleHandler = firewallRuleHandler;
   }

   public FirewallService getResult() {
      try {
         return builder.build();
      } finally {
         builder = FirewallService.builder();
      }
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      if (equalsOrSuffix(qName, "FirewallService")) {
         inFirewallService = true;
      } else if (equalsOrSuffix(qName, "FirewallRule")) {
         inFirewallRule = true;
         firewallRuleHandler.startElement(uri, localName, qName, attrs);
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "FirewallService")) {
         inFirewallService = false;
      } else if (equalsOrSuffix(qName, "FirewallRule")) {
         builder.firewallRule(firewallRuleHandler.getResult());
         inFirewallRule = false;
      } else if (equalsOrSuffix(qName, "isEnabled")) {
         if (inFirewallService) {
            String current = currentOrNull(currentText);
            if (current != null) {
               builder.isEnabled(Boolean.parseBoolean(current));
            }
         }
      }

      if (inFirewallRule) {
         firewallRuleHandler.endElement(uri, localName, qName);
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
      firewallRuleHandler.characters(ch, start, length);
   }

}
