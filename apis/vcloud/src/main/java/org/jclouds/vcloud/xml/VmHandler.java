/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.GuestCustomizationSection;
import org.jclouds.vcloud.domain.NetworkConnectionSection;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.internal.VmImpl;
import org.jclouds.vcloud.domain.ovf.VCloudOperatingSystemSection;
import org.jclouds.vcloud.domain.ovf.VCloudVirtualHardwareSection;
import org.jclouds.vcloud.xml.ovf.VCloudOperatingSystemHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
public class VmHandler extends ParseSax.HandlerWithResult<Vm> {

   protected final TaskHandler taskHandler;
   protected final VCloudVirtualHardwareHandler virtualHardwareHandler;
   protected final VCloudOperatingSystemHandler operatingSystemHandler;
   protected final GuestCustomizationSectionHandler guestCustomizationHandler;
   protected final NetworkConnectionSectionHandler networkConnectionSectionHandler;

   @Inject
   public VmHandler(TaskHandler taskHandler, VCloudVirtualHardwareHandler virtualHardwareHandler,
            VCloudOperatingSystemHandler operatingSystemHandler,
            NetworkConnectionSectionHandler networkConnectionSectionHandler,
            GuestCustomizationSectionHandler guestCustomizationHandler) {
      this.taskHandler = taskHandler;
      this.virtualHardwareHandler = virtualHardwareHandler;
      this.operatingSystemHandler = operatingSystemHandler;
      this.networkConnectionSectionHandler = networkConnectionSectionHandler;
      this.guestCustomizationHandler = guestCustomizationHandler;
   }

   protected StringBuilder currentText = new StringBuilder();

   protected ReferenceType vm;
   protected Status status;
   protected ReferenceType vdc;
   protected String description;
   protected List<Task> tasks = Lists.newArrayList();
   protected VCloudVirtualHardwareSection hardware;
   protected VCloudOperatingSystemSection os;
   protected NetworkConnectionSection networkConnectionSection;
   protected GuestCustomizationSection guestCustomization;
   protected String vAppScopedLocalId;

   private boolean inTasks;
   private boolean inHardware;
   private boolean inOs;
   private boolean inNetworkConnectionSection;
   private boolean inGuestCustomization;

   public Vm getResult() {
      return vm == null ? null : new VmImpl(vm.getName(), vm.getType(), vm.getHref(), status, vdc, description, tasks,
               hardware, os, networkConnectionSection, guestCustomization, vAppScopedLocalId);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.endsWith("VirtualHardwareSection")) {
         inHardware = true;
      } else if (qName.endsWith("OperatingSystemSection")) {
         inOs = true;
      } else if (qName.endsWith("GuestCustomizationSection")) {
         inGuestCustomization = true;
      } else if (qName.endsWith("NetworkConnectionSection")) {
         inNetworkConnectionSection = true;
      } else if (qName.endsWith("Tasks")) {
         inTasks = true;
      }
      if (inHardware) {
         virtualHardwareHandler.startElement(uri, localName, qName, attrs);
      } else if (inOs) {
         operatingSystemHandler.startElement(uri, localName, qName, attrs);
      } else if (inNetworkConnectionSection) {
         networkConnectionSectionHandler.startElement(uri, localName, qName, attrs);
      } else if (inGuestCustomization) {
         guestCustomizationHandler.startElement(uri, localName, qName, attrs);
      } else if (inTasks) {
         taskHandler.startElement(uri, localName, qName, attrs);
      } else if (SaxUtils.equalsOrSuffix(qName, "Vm")) {
         vm = newReferenceType(attributes);
         String status = attributes.get("status");
         if (status != null)
            this.status = Status.fromValue(Integer.parseInt(status));
      } else if (SaxUtils.equalsOrSuffix(qName, "Link") && "up".equals(attributes.get("rel"))) {
         vdc = newReferenceType(attributes);
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.endsWith("VirtualHardwareSection")) {
         inHardware = false;
         this.hardware = virtualHardwareHandler.getResult();
      } else if (qName.endsWith("OperatingSystemSection")) {
         inOs = false;
         os = operatingSystemHandler.getResult();
      } else if (qName.endsWith("NetworkConnectionSection")) {
         inNetworkConnectionSection = false;
         networkConnectionSection = networkConnectionSectionHandler.getResult();
      } else if (qName.endsWith("GuestCustomizationSection")) {
         inGuestCustomization = false;
         guestCustomization = guestCustomizationHandler.getResult();
      } else if (qName.endsWith("Tasks")) {
         inTasks = false;
         this.tasks.add(taskHandler.getResult());
      }
      if (inHardware) {
         virtualHardwareHandler.endElement(uri, name, qName);
      } else if (inOs) {
         operatingSystemHandler.endElement(uri, name, qName);
      } else if (inGuestCustomization) {
         guestCustomizationHandler.endElement(uri, name, qName);
      } else if (inNetworkConnectionSection) {
         networkConnectionSectionHandler.endElement(uri, name, qName);
      } else if (inTasks) {
         taskHandler.endElement(uri, name, qName);
      } else if (SaxUtils.equalsOrSuffix(qName, "Description")) {
         description = currentOrNull();
      } else if (SaxUtils.equalsOrSuffix(qName, "VAppScopedLocalId")) {
         vAppScopedLocalId = currentOrNull();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      if (inHardware)
         virtualHardwareHandler.characters(ch, start, length);
      else if (inOs)
         operatingSystemHandler.characters(ch, start, length);
      else if (inGuestCustomization)
         guestCustomizationHandler.characters(ch, start, length);
      else if (inNetworkConnectionSection)
         networkConnectionSectionHandler.characters(ch, start, length);
      else if (inTasks)
         taskHandler.characters(ch, start, length);
      else
         currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
