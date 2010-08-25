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

package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.util.Utils.cleanseAttributes;
import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.VirtualHardware;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class VirtualHardwareHandler extends ParseSax.HandlerWithResult<VirtualHardware> {
   protected StringBuilder currentText = new StringBuilder();

   private final VirtualSystemHandler systemHandler;
   private final VCloudResourceAllocationHandler allocationHandler;

   @Inject
   public VirtualHardwareHandler(VirtualSystemHandler systemHandler, VCloudResourceAllocationHandler allocationHandler) {
      this.systemHandler = systemHandler;
      this.allocationHandler = allocationHandler;
   }

   private ReferenceType hardware;
   private String info;
   protected VirtualSystem system;
   protected Set<ResourceAllocation> allocations = Sets.newLinkedHashSet();

   private boolean inItem;
   private boolean inSystem;

   public VirtualHardware getResult() {
      return new VirtualHardware(hardware.getName(), hardware.getType(), hardware.getHref(), info, system, allocations);
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (attributes.containsKey("href") && attributes.get("href").endsWith("/")) {
         String href = attributes.get("href");
         attributes.put("href", href.substring(0, href.lastIndexOf('/')));
      }
      if (qName.endsWith("System")) {
         inSystem = true;
      } else if (!inSystem && qName.endsWith("Item")) {
         inItem = true;
      }
      if (inSystem) {
         systemHandler.startElement(uri, localName, qName, attrs);
      } else if (inItem) {
         allocationHandler.startElement(uri, localName, qName, attrs);
      } else if (qName.endsWith("VirtualHardwareSection")) {
         hardware = newReferenceType(attributes);
      }

   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (qName.endsWith("System")) {
         inSystem = false;
         system = systemHandler.getResult();
      } else if (qName.endsWith("Item")) {
         inItem = false;
         allocations.add(allocationHandler.getResult());
      }
      if (inSystem) {
         systemHandler.endElement(uri, localName, qName);
      } else if (inItem) {
         allocationHandler.endElement(uri, localName, qName);
      } else if (qName.endsWith("Info")) {
         this.info = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
      if (inSystem)
         systemHandler.characters(ch, start, length);
      if (inItem)
         allocationHandler.characters(ch, start, length);
   }

}
