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

package org.jclouds.ovf.xml;

import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ovf.OperatingSystemSection;
import org.jclouds.ovf.VirtualHardwareSection;
import org.jclouds.ovf.VirtualSystem;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class VirtualSystemHandler extends ParseSax.HandlerWithResult<VirtualSystem> {
   protected StringBuilder currentText = new StringBuilder();
   private final OperatingSystemSectionHandler osHandler;
   private final VirtualHardwareSectionHandler hardwareHandler;

   @Inject
   public VirtualSystemHandler(OperatingSystemSectionHandler osHandler, VirtualHardwareSectionHandler hardwareHandler) {
      this.osHandler = osHandler;
      this.hardwareHandler = hardwareHandler;
   }

   protected String id;
   protected String info;
   protected String name;
   protected OperatingSystemSection operatingSystem;
   protected Set<VirtualHardwareSection> hardware = Sets.newLinkedHashSet();

   private boolean inHardware;
   private boolean inOs;
   private boolean inNetwork;
   private boolean inGuest;

   public VirtualSystem getResult() {
      VirtualSystem vs = new VirtualSystem(id, info, name, operatingSystem, hardware);
      id = null;
      info = null;
      name = null;
      operatingSystem = null;
      hardware = Sets.newLinkedHashSet();
      return vs;
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "VirtualHardwareSection")) {
         inHardware = true;
      } else if (equalsOrSuffix(qName, "OperatingSystemSection")) {
         inOs = true;
      } else if (equalsOrSuffix(qName, "NetworkConnectionSection")) {
         inNetwork = true;
      } else if (equalsOrSuffix(qName, "GuestCustomizationSection")) {
         inGuest = true;
      }

      if (inHardware) {
         hardwareHandler.startElement(uri, localName, qName, attrs);
      } else if (inOs) {
         osHandler.startElement(uri, localName, qName, attrs);
      } else if (inNetwork) {
         // TODO
      } else if (inGuest) {
         // TODO
      } else if (equalsOrSuffix(qName, "VirtualSystem")) {
         id = attributes.get("id");
      }

   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "VirtualHardwareSection")) {
         inHardware = false;
         hardware.add(hardwareHandler.getResult());
      } else if (equalsOrSuffix(qName, "OperatingSystemSection")) {
         inOs = false;
         operatingSystem = osHandler.getResult();
      } else if (equalsOrSuffix(qName, "NetworkConnectionSection")) {
         inNetwork = false;
         // TODO
      } else if (equalsOrSuffix(qName, "GuestCustomizationSection")) {
         inNetwork = false;
         // TODO
      }
      if (inHardware) {
         hardwareHandler.endElement(uri, localName, qName);
      } else if (inOs) {
         osHandler.endElement(uri, localName, qName);
      } else if (inNetwork) {
         // TODO
      } else if (inGuest) {
         // TODO
      } else if (equalsOrSuffix(qName, "Info")) {
         info = currentText.toString().trim();
      } else if (equalsOrSuffix(qName, "Name")) {
         name = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inHardware)
         hardwareHandler.characters(ch, start, length);
      else if (inOs)
         osHandler.characters(ch, start, length);
      else
         currentText.append(ch, start, length);

   }

}
