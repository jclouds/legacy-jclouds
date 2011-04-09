/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud.xml.ovf;

import java.util.Map;
import java.util.Set;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.ovf.NetworkSection;
import org.jclouds.vcloud.domain.ovf.network.Network;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class NetworkSectionHandler extends ParseSax.HandlerWithResult<NetworkSection> {
   protected StringBuilder currentText = new StringBuilder();

   protected String info;
   protected String name;
   protected String description;

   protected Set<Network> networks = Sets.newLinkedHashSet();

   public NetworkSection getResult() {
      NetworkSection system = new NetworkSection(info, networks);
      this.info = null;
      this.networks = Sets.newLinkedHashSet();
      return system;
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
         this.info = currentOrNull();
      } else if (qName.endsWith("Description")) {
         this.description = currentOrNull();
      } else if (qName.endsWith("Network")) {
         this.networks.add(new Network(name, description));
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
