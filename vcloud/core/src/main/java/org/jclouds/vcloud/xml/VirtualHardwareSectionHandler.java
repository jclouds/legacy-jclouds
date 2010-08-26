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

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.System;
import org.jclouds.vcloud.domain.ovf.VirtualHardwareSection;
import org.jclouds.vcloud.xml.ovf.SystemHandler;
import org.jclouds.vcloud.xml.ovf.VCloudResourceAllocationHandler;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class VirtualHardwareSectionHandler extends ParseSax.HandlerWithResult<VirtualHardwareSection> {
   protected StringBuilder currentText = new StringBuilder();

   private final SystemHandler systemHandler;
   private final VCloudResourceAllocationHandler allocationHandler;

   @Inject
   public VirtualHardwareSectionHandler(SystemHandler systemHandler, VCloudResourceAllocationHandler allocationHandler) {
      this.systemHandler = systemHandler;
      this.allocationHandler = allocationHandler;
   }

   private String info;
   protected System system;
   protected Set<ResourceAllocation> allocations = Sets.newLinkedHashSet();

   private boolean inItem;
   private boolean inSystem;

   public VirtualHardwareSection getResult() {
      return new VirtualHardwareSection(info, system, allocations);
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      if (qName.endsWith("System")) {
         inSystem = true;
      } else if (!inSystem && qName.endsWith("Item")) {
         inItem = true;
      }
      if (inSystem) {
         systemHandler.startElement(uri, localName, qName, attrs);
      } else if (inItem) {
         allocationHandler.startElement(uri, localName, qName, attrs);
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
