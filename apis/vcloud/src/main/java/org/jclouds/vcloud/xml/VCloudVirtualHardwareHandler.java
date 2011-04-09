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
package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.util.Utils.cleanseAttributes;
import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.ovf.VCloudVirtualHardwareSection;
import org.jclouds.vcloud.domain.ovf.VirtualHardwareSection;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class VCloudVirtualHardwareHandler extends ParseSax.HandlerWithResult<VCloudVirtualHardwareSection> {

   private final VirtualHardwareSectionHandler hardwareHandler;

   private ReferenceType hardware;

   @Inject
   public VCloudVirtualHardwareHandler(VirtualHardwareSectionHandler hardwareHandler) {
      this.hardwareHandler = hardwareHandler;
   }

   public VCloudVirtualHardwareSection getResult() {
      VirtualHardwareSection hardware = hardwareHandler.getResult();
      return new VCloudVirtualHardwareSection(this.hardware.getType(), this.hardware.getHref(), hardware.getInfo(), hardware
               .getSystem(), hardware.getResourceAllocations());
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (qName.endsWith("VirtualHardwareSection")) {
         hardware = newReferenceType(attributes);
      }
      hardwareHandler.startElement(uri, localName, qName, attrs);
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      hardwareHandler.endElement(uri, localName, qName);

   }

   @Override
   public void characters(char ch[], int start, int length) {
      hardwareHandler.characters(ch, start, length);
   }

}
