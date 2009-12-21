/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.xml;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.terremark.domain.TerremarkVApp;
import org.jclouds.vcloud.terremark.domain.TerremarkVirtualSystem;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkVAppImpl;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class TerremarkVAppHandler extends ParseSax.HandlerWithResult<TerremarkVApp> {

   private final VirtualSystemHandler systemHandler;
   private final ResourceAllocationHandler allocationHandler;
   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public TerremarkVAppHandler(VirtualSystemHandler systemHandler,
            ResourceAllocationHandler allocationHandler) {
      this.systemHandler = systemHandler;
      this.allocationHandler = allocationHandler;
   }

   private TerremarkVirtualSystem system;
   private SortedSet<ResourceAllocation> allocations = Sets.newTreeSet();
   private NamedResource vApp;
   private NamedResource vDC;
   private VAppStatus status;
   private int size;
   private boolean skip;
   private NamedResource computeOptions;
   private NamedResource customizationOptions;
   private final ListMultimap<String, InetAddress> networkToAddresses = ArrayListMultimap.create();
   private StringBuilder currentText = new StringBuilder();
   private String operatingSystemDescription;
   private boolean inOs;
   private String networkName;

   public TerremarkVApp getResult() {
      return new TerremarkVAppImpl(vApp.getId(), vApp.getName(), vApp.getType(),
               vApp.getLocation(), status, size, vDC, computeOptions, customizationOptions,
               networkToAddresses, operatingSystemDescription, system, allocations);
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (attributes.getIndex("xsi:nil") != -1) {
         skip = true;
         return;
      } else {
         skip = false;
      }
      if (qName.equals("Link")) {
         if (attributes.getValue(attributes.getIndex("type")).equals(VCloudMediaType.VDC_XML)) {
            vDC = Utils.newNamedResource(attributes);
         } else if (attributes.getValue(attributes.getIndex("name")).equals("Compute Options")) {
            this.computeOptions = Utils.newNamedResource(attributes);
         } else if (attributes.getValue(attributes.getIndex("name"))
                  .equals("Customization Options")) {
            this.customizationOptions = Utils.newNamedResource(attributes);
         }
      } else if (qName.equals("VApp")) {
         vApp = Utils.newNamedResource(attributes);
         status = VAppStatus.fromValue(attributes.getValue(attributes.getIndex("status")));
         size = Integer.parseInt(attributes.getValue(attributes.getIndex("size")));
      } else if (qName.equals("OperatingSystemSection")) {
         inOs = true;
      } else if (qName.equals("q1:NetworkConnection")) {
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
      } else if (qName.equals("q1:IpAddress")) {
         networkToAddresses.put(networkName, parseInetAddress(currentText.toString().trim()));
      } else if (qName.equals("q2:System")) {
         systemHandler.endElement(uri, localName, qName);
         system = systemHandler.getResult();
      } else if (qName.equals("q2:Item")) {
         allocationHandler.endElement(uri, localName, qName);
         allocations.add(allocationHandler.getResult());
      } else if (!skip) {
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

   private InetAddress parseInetAddress(String string) {
      String[] byteStrings = string.split("\\.");
      byte[] bytes = new byte[4];
      for (int i = 0; i < 4; i++) {
         bytes[i] = (byte) Integer.parseInt(byteStrings[i]);
      }
      try {
         return InetAddress.getByAddress(bytes);
      } catch (UnknownHostException e) {
         logger.warn(e, "error parsing ipAddress", currentText);
      }
      return null;
   }

}
