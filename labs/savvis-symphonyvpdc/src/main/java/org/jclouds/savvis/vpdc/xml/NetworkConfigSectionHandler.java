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

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.ovf.xml.SectionHandler;
import org.jclouds.savvis.vpdc.domain.NetworkConfigSection;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class NetworkConfigSectionHandler extends SectionHandler<NetworkConfigSection, NetworkConfigSection.Builder> {

   @Inject
   public NetworkConfigSectionHandler(Provider<NetworkConfigSection.Builder> builderProvider) {
      super(builderProvider);
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "Section") && "vApp:NetworkConfigSectionType".equals(attributes.get("type"))) {
         builder.network(attributes.get("Network"));
         builder.netmask(attributes.get("Netmask"));
         builder.gateway(attributes.get("Gateway"));
      } else if (equalsOrSuffix(qName, "NatRule")) {
         builder.internalToExternalNATRule(attributes.get("internalIP"), attributes.get("externalIP"));
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "FenceMode")) {
         builder.fenceMode(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Dhcp")) {
         builder.dhcp(new Boolean(currentOrNull(currentText)));
      }
      super.endElement(uri, localName, qName);
   }
}
