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

import static org.jclouds.savvis.vpdc.util.Utils.newResource;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.ovf.NetworkSection;
import org.jclouds.ovf.Section;
import org.jclouds.ovf.xml.NetworkSectionHandler;
import org.jclouds.ovf.xml.OperatingSystemSectionHandler;
import org.jclouds.ovf.xml.ProductSectionHandler;
import org.jclouds.ovf.xml.SectionHandler;
import org.jclouds.ovf.xml.VirtualHardwareSectionHandler;
import org.jclouds.ovf.xml.internal.BaseVirtualSystemHandler;
import org.jclouds.savvis.vpdc.domain.NetworkConfigSection;
import org.jclouds.savvis.vpdc.domain.NetworkConnectionSection;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.VM;
import org.jclouds.savvis.vpdc.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;

/**
 * @author Kedar Dave
 */
public class VMHandler extends BaseVirtualSystemHandler<VM, VM.Builder> {

   @SuppressWarnings("unchecked")
   @Inject
   public VMHandler(Provider<VM.Builder> builderProvider, OperatingSystemSectionHandler osHandler,
            VirtualHardwareSectionHandler hardwareHandler, ProductSectionHandler productHandler,
            Provider<NetworkSectionHandler> networkSectionHandler,
            Provider<NetworkConfigSectionHandler> networkConfigSectionHandler,
            Provider<NetworkConnectionSectionHandler> networkConnectionSectionHandler) {
      super(builderProvider, osHandler, hardwareHandler, productHandler);
      this.extensionHandlers = ImmutableMap.<String, Provider<? extends SectionHandler>> of("ovf:NetworkSection",
               networkSectionHandler, "vApp:NetworkConfigSectionType", networkConfigSectionHandler,
               "vApp:NetworkConnectionType", networkConnectionSectionHandler);
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = Utils.cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "VApp")) {
         // savvis doesn't add href in the header for some reason
         if (!attributes.containsKey("href") && getRequest() != null)
            attributes = ImmutableMap.<String, String> builder().putAll(attributes).put("href",
                     getRequest().getEndpoint().toASCIIString()).build();
         Resource vApp = newResource(attributes);
         builder.name(vApp.getName()).type(vApp.getType()).id(vApp.getId()).href(vApp.getHref());
         builder.status(VM.Status.fromValue(attributes.get("status")));
      }
      super.startElement(uri, localName, qName, attrs);
   }

   @Override
   @SuppressWarnings("unchecked")
   protected void addAdditionalSection(String qName, Section additionalSection) {
      if (additionalSection instanceof NetworkSection) {
         builder.networkSection(NetworkSection.class.cast(additionalSection));
      } else if (additionalSection instanceof NetworkConfigSection) {
         builder.networkConfigSection(NetworkConfigSection.class.cast(additionalSection));
      } else if (additionalSection instanceof NetworkConnectionSection) {
         builder.networkConnectionSection(NetworkConnectionSection.class.cast(additionalSection));
      } else {
         builder.additionalSection(qName, additionalSection);
      }
   }

}
