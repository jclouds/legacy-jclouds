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

package org.jclouds.vcloud.savvis.xml;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.vcloud.util.Utils.cleanseAttributes;
import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudExpressMediaType;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.domain.internal.VCloudExpressVAppImpl;
import org.jclouds.vcloud.domain.ovf.ResourceAllocation;
import org.jclouds.vcloud.domain.ovf.System;
import org.jclouds.vcloud.xml.ovf.ResourceAllocationHandler;
import org.jclouds.vcloud.xml.ovf.SystemHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

/**
 * @author Kedar Dave
 */
public class SymphonyVPDCVAppHandler extends ParseSax.HandlerWithResult<VCloudExpressVApp> {
   private final String apiVersion;
   private final SystemHandler systemHandler;
   private final ResourceAllocationHandler allocationHandler;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public SymphonyVPDCVAppHandler(@Named(PROPERTY_API_VERSION) String apiVersion, SystemHandler systemHandler,
            ResourceAllocationHandler allocationHandler) {
      this.apiVersion = apiVersion;
      this.systemHandler = systemHandler;
      this.allocationHandler = allocationHandler;
   }

   protected System system;
   protected Set<ResourceAllocation> allocations = Sets.newLinkedHashSet();
   protected Status status;
   protected final ListMultimap<String, String> networkToAddresses = ArrayListMultimap.create();
   protected StringBuilder currentText = new StringBuilder();
   protected String operatingSystemDescription;
   protected boolean inOs;
   protected String networkName;
   protected String name;
   protected Integer osType;
   protected URI location;
   protected Long size;
   protected ReferenceType vDC;

   public VCloudExpressVApp getResult() {
      return new VCloudExpressVAppImpl(name, location, status, size, vDC, networkToAddresses, osType,
               operatingSystemDescription, system, allocations);
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (qName.endsWith("VApp")) {
         ReferenceType resource = newReferenceType(attributes);
         name = resource.getName();
         location = resource.getHref();
         String statusString = attributes.get("status");
         if (apiVersion.indexOf("0.8") != -1 && "2".equals(statusString))
            status = Status.OFF;
         else
            status = Status.fromValue(statusString);
         if (attributes.containsKey("size"))
            size = new Long(attributes.get("size"));
      } else if (qName.equals("Link")) { // type should never be missing
         if (attributes.containsKey("type") && attributes.get("type").equals(VCloudExpressMediaType.VDC_XML)) {
            vDC = newReferenceType(attributes);
         }
      } else if (qName.endsWith("OperatingSystemSection")) {
         inOs = true;
         if (attributes.containsKey("id"))
            osType = Integer.parseInt(attributes.get("id"));
      } else if (qName.endsWith("NetworkConnection")) {
         networkName = attributes.get("Network");
      } else {
         systemHandler.startElement(uri, localName, qName, attrs);
         allocationHandler.startElement(uri, localName, qName, attrs);
      }

   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.endsWith("OperatingSystemSection")) {
         inOs = false;
      } else if (inOs && qName.endsWith("Description")) {
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
