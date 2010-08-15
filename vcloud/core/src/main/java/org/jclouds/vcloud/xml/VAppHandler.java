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

import java.net.URI;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.domain.VirtualSystem;
import org.jclouds.vcloud.domain.internal.VAppImpl;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class VAppHandler extends ParseSax.HandlerWithResult<VApp> {

   private final VirtualSystemHandler systemHandler;
   private final ResourceAllocationHandler allocationHandler;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VAppHandler(VirtualSystemHandler systemHandler, ResourceAllocationHandler allocationHandler) {
      this.systemHandler = systemHandler;
      this.allocationHandler = allocationHandler;
   }

   protected VirtualSystem system;
   protected Set<ResourceAllocation> allocations = Sets.newLinkedHashSet();
   protected VAppStatus status;
   protected final ListMultimap<String, String> networkToAddresses = ArrayListMultimap.create();
   protected StringBuilder currentText = new StringBuilder();
   protected String operatingSystemDescription;
   protected boolean inOs;
   protected String networkName;
   protected String name;
   protected URI location;
   protected Long size;
   protected NamedResource vDC;

   public VApp getResult() {
      return new VAppImpl(name, location, status, size, vDC, networkToAddresses, operatingSystemDescription, system,
            allocations);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("VApp")) {
         NamedResource resource = Utils.newNamedResource(attributes);
         name = resource.getName();
         location = resource.getId();
         status = VAppStatus.fromValue(attributes.getValue(attributes.getIndex("status")));
         if (attributes.getIndex("size") != -1)
            size = new Long(attributes.getValue(attributes.getIndex("size")));
      } else if (qName.equals("Link")) { // type should never be missing
         if (attributes.getIndex("type") != -1
               && attributes.getValue(attributes.getIndex("type")).equals(VCloudMediaType.VDC_XML)) {
            vDC = Utils.newNamedResource(attributes);
         }
      } else if (qName.equals("OperatingSystemSection")) {
         inOs = true;
      } else if (qName.endsWith("NetworkConnection")) {
         networkName = attributes.getValue(attributes.getIndex("Network"));
      } else {
         systemHandler.startElement(uri, localName, qName, attributes);
         allocationHandler.startElement(uri, localName, qName, attributes);
      }

   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("OperatingSystemSection")) {
         inOs = false;
      } else if (inOs && qName.equals("Description")) {
         operatingSystemDescription = currentText.toString().trim();
      } else if (qName.endsWith("IpAddress")) {
         networkToAddresses.put(networkName, currentText.toString().trim());
      } else if (qName.equals("System")) {
         systemHandler.endElement(uri, localName, qName);
         system = systemHandler.getResult();
      } else if (qName.equals("Item")) {
         allocationHandler.endElement(uri, localName, qName);
         allocations.add(allocationHandler.getResult());
      } else {
         systemHandler.endElement(uri, localName, qName);
         allocationHandler.endElement(uri, localName, qName);
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
      systemHandler.characters(ch, start, length);
      allocationHandler.characters(ch, start, length);
   }

}
