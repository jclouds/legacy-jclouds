/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.vdc/licenses/LICENSE-2.0
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

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VirtualHardware;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.internal.VmImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
public class VmHandler extends ParseSax.HandlerWithResult<Vm> {

   protected final TaskHandler taskHandler;
   protected final VirtualHardwareHandler virtualHardwareHandler;

   @Inject
   public VmHandler(TaskHandler taskHandler, VirtualHardwareHandler virtualHardwareHandler) {
      this.taskHandler = taskHandler;
      this.virtualHardwareHandler = virtualHardwareHandler;
   }

   protected StringBuilder currentText = new StringBuilder();

   protected ReferenceType vm;
   protected Status status;
   protected ReferenceType vdc;
   protected String description;
   protected List<Task> tasks = Lists.newArrayList();
   protected VirtualHardware hardware;
   protected String vAppScopedLocalId;

   private boolean inTasks;
   private boolean inHardware;

   public Vm getResult() {
      return new VmImpl(vm.getName(), vm.getType(), vm.getHref(), status, vdc, description, tasks, hardware,
               vAppScopedLocalId);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (qName.endsWith("VirtualHardwareSection")) {
         inHardware = true;
      } else if (qName.endsWith("Tasks")) {
         inTasks = true;
      }
      if (inHardware) {
         virtualHardwareHandler.startElement(uri, localName, qName, attrs);
      } else if (inTasks) {
         taskHandler.startElement(uri, localName, qName, attrs);
      } else if (qName.equals("Vm")) {
         vm = newReferenceType(attributes);
         String status = attributes.get("status");
         if (status != null)
            this.status = Status.fromValue(Integer.parseInt(status));
      } else if (qName.equals("Link") && "up".equals(attributes.get("rel"))) {
         vdc = newReferenceType(attributes);
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.endsWith("VirtualHardwareSection")) {
         inHardware = false;
         this.hardware = virtualHardwareHandler.getResult();
      } else if (qName.endsWith("Tasks")) {
         inTasks = false;
         this.tasks.add(taskHandler.getResult());
      }
      if (inHardware) {
         virtualHardwareHandler.endElement(uri, name, qName);
      } else if (inTasks) {
         taskHandler.endElement(uri, name, qName);
      } else if (qName.equals("Description")) {
         description = currentOrNull();
      } else if (qName.equals("VAppScopedLocalId")) {
         vAppScopedLocalId = currentOrNull();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      if (inTasks)
         taskHandler.characters(ch, start, length);
      if (inHardware)
         virtualHardwareHandler.characters(ch, start, length);
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
