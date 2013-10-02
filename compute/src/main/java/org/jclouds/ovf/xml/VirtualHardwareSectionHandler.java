/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ovf.xml;

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.cim.xml.ResourceAllocationSettingDataHandler;
import org.jclouds.cim.xml.VirtualSystemSettingDataHandler;
import org.jclouds.ovf.VirtualHardwareSection;
import org.xml.sax.Attributes;

import com.google.common.base.Splitter;

/**
 * @author Adrian Cole
 */
public class VirtualHardwareSectionHandler extends
         SectionHandler<VirtualHardwareSection, VirtualHardwareSection.Builder> {

   private final VirtualSystemSettingDataHandler systemHandler;
   private final ResourceAllocationSettingDataHandler allocationHandler;

   @Inject
   public VirtualHardwareSectionHandler(Provider<VirtualHardwareSection.Builder> builderProvider,
            VirtualSystemSettingDataHandler systemHandler, ResourceAllocationSettingDataHandler allocationHandler) {
      super(builderProvider);
      this.systemHandler = systemHandler;
      this.allocationHandler = allocationHandler;
   }

   private boolean inItem;
   private boolean inSystem;

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "VirtualHardwareSection")) {
         if (attributes.containsKey("transport"))
            builder.transports(Splitter.on(' ').split(attributes.get("transport")));
      } else if (equalsOrSuffix(qName, "System")) {
         inSystem = true;
      } else if (!inSystem && equalsOrSuffix(qName, "Item")) {
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
      if (equalsOrSuffix(qName, "System")) {
         inSystem = false;
         builder.system(systemHandler.getResult());
      } else if (equalsOrSuffix(qName, "Item")) {
         inItem = false;
         builder.item(allocationHandler.getResult());
      }
      if (inSystem) {
         systemHandler.endElement(uri, localName, qName);
      } else if (inItem) {
         allocationHandler.endElement(uri, localName, qName);
      } else {
         if (equalsOrSuffix(qName, "Info"))
            builder.info(currentOrNull(currentText));
         super.endElement(uri, localName, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inSystem) {
         systemHandler.characters(ch, start, length);
      } else if (inItem) {
         allocationHandler.characters(ch, start, length);
      } else {
         super.characters(ch, start, length);
      }
   }

}
