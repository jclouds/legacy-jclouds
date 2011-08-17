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
import static org.jclouds.vcloud.util.Utils.putReferenceType;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.VDCStatus;
import org.jclouds.vcloud.domain.internal.VDCImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class VDCHandler extends ParseSax.HandlerWithResult<VDC> {

   protected final TaskHandler taskHandler;

   @Inject
   public VDCHandler(TaskHandler taskHandler) {
      this.taskHandler = taskHandler;
   }

   protected StringBuilder currentText = new StringBuilder();

   protected ReferenceType vDC;
   protected VDCStatus status = VDCStatus.READY;
   protected ReferenceType org;
   protected String description;
   protected List<Task> tasks = Lists.newArrayList();
   protected AllocationModel allocationModel = AllocationModel.UNRECOGNIZED;

   protected Capacity storageCapacity;
   protected Capacity cpuCapacity;
   protected Capacity memoryCapacity;

   protected String units;
   protected long allocated = 0;
   protected long limit = 0;
   protected int used = 0;
   protected long overhead = 0;

   protected Map<String, ReferenceType> resourceEntities = Maps.newLinkedHashMap();
   protected Map<String, ReferenceType> availableNetworks = Maps.newLinkedHashMap();

   protected int nicQuota;
   protected int networkQuota;
   protected int vmQuota;
   protected boolean isEnabled = true;

   public VDC getResult() {
      return new VDCImpl(vDC.getName(), vDC.getType(), vDC.getHref(), status, org, description, tasks, allocationModel,
               storageCapacity, cpuCapacity, memoryCapacity, resourceEntities, availableNetworks, nicQuota,
               networkQuota, vmQuota, isEnabled);
   }

   void resetCapacity() {
      units = null;
      allocated = 0;
      limit = 0;
      used = 0;
      overhead = 0;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.endsWith("Vdc")) {
         vDC = newReferenceType(attributes);
         String status = attributes.get("status");
         if (status != null)
            this.status = VDCStatus.fromValue(Integer.parseInt(status));
      } else if (qName.endsWith("Network")) {
         putReferenceType(availableNetworks, attributes);
      } else if (qName.endsWith("ResourceEntity")) {
         putReferenceType(resourceEntities, attributes);
      } else if (qName.endsWith("Link") && "up".equals(attributes.get("rel"))) {
         org = newReferenceType(attributes);
      } else {
         taskHandler.startElement(uri, localName, qName, attrs);
      }

   }

   public void endElement(String uri, String name, String qName) {
      taskHandler.endElement(uri, name, qName);
      if (qName.endsWith("Task")) {
         this.tasks.add(taskHandler.getResult());
      } else if (qName.endsWith("Description")) {
         description = currentOrNull();
      } else if (qName.endsWith("AllocationModel")) {
         allocationModel = AllocationModel.fromValue(currentOrNull());
      } else if (qName.endsWith("Units")) {
         units = currentOrNull();
      } else if (qName.endsWith("Allocated")) {
         allocated = Integer.parseInt(currentOrNull());
      } else if (qName.endsWith("Used")) {
         used = Integer.parseInt(currentOrNull());
      } else if (qName.endsWith("Limit")) {
         limit = Integer.parseInt(currentOrNull());
      } else if (qName.endsWith("Overhead")) {
         overhead = Integer.parseInt(currentOrNull());
      } else if (qName.endsWith("StorageCapacity")) {
         storageCapacity = new Capacity(units, allocated, limit, used, overhead);
         resetCapacity();
      } else if (qName.endsWith("Cpu")) {
         cpuCapacity = new Capacity(units, allocated, limit, used, overhead);
         resetCapacity();
      } else if (qName.endsWith("Memory")) {
         memoryCapacity = new Capacity(units, allocated, limit, used, overhead);
         resetCapacity();
      } else if (qName.endsWith("DeployedVmsQuota")) {
         vmQuota = (int) limit;
         // vcloud express doesn't have the zero is unlimited rule
         if (vmQuota == -1)
            vmQuota = 0;
      } else if (qName.endsWith("VmQuota")) {
         vmQuota = Integer.parseInt(currentOrNull());
      } else if (qName.endsWith("NicQuota")) {
         nicQuota = Integer.parseInt(currentOrNull());
      } else if (qName.endsWith("NetworkQuota")) {
         networkQuota = Integer.parseInt(currentOrNull());
      } else if (qName.endsWith("IsEnabled")) {
         isEnabled = Boolean.parseBoolean(currentOrNull());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
