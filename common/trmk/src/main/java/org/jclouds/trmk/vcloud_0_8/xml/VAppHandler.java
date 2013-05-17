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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.jclouds.trmk.vcloud_0_8.util.Utils.newReferenceType;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.cim.ResourceAllocationSettingData;
import org.jclouds.cim.VirtualSystemSettingData;
import org.jclouds.cim.xml.ResourceAllocationSettingDataHandler;
import org.jclouds.cim.xml.VirtualSystemSettingDataHandler;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.VApp;
import org.jclouds.trmk.vcloud_0_8.domain.internal.VAppImpl;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class VAppHandler extends ParseSax.HandlerWithResult<VApp> {
   private final VirtualSystemSettingDataHandler systemHandler;
   private final ResourceAllocationSettingDataHandler allocationHandler;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public VAppHandler(VirtualSystemSettingDataHandler systemHandler,
         ResourceAllocationSettingDataHandler allocationHandler) {
      this.systemHandler = systemHandler;
      this.allocationHandler = allocationHandler;
   }

   protected VirtualSystemSettingData system;
   protected Set<ResourceAllocationSettingData> allocations = Sets.newLinkedHashSet();
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
   protected Set<ReferenceType> extendedInfo = Sets.newLinkedHashSet();

   public VApp getResult() {
      return new VAppImpl(name, location, status, size, vDC, networkToAddresses, osType, operatingSystemDescription,
            system, allocations, extendedInfo);
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equals("VApp")) {
         ReferenceType resource = newReferenceType(attributes);
         name = resource.getName();
         location = resource.getHref();
         String statusString = attributes.get("status");
         status = Status.fromValue(statusString);
         if (attributes.containsKey("size"))
            size = Long.valueOf(attributes.get("size"));
      } else if (qName.equals("Link")) { // type should never be missing
         if (attributes.containsKey("type")) {
            if (attributes.get("type").equals(TerremarkVCloudMediaType.VDC_XML)) {
               vDC = newReferenceType(attributes);
            } else {
               extendedInfo.add(newReferenceType(attributes));
            }
         }
      } else if (qName.equals("OperatingSystemSection")) {
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
