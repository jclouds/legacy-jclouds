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

import static org.jclouds.vcloud.util.Utils.newNamedResource;
import static org.jclouds.vcloud.util.Utils.putNamedResource;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.VDCStatus;
import org.jclouds.vcloud.domain.internal.VDCImpl;
import org.jclouds.vcloud.util.Utils;
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

   protected NamedResource vDC;
   protected VDCStatus status = VDCStatus.READY;
   protected NamedResource org;
   protected String description;
   protected List<Task> tasks = Lists.newArrayList();
   protected AllocationModel allocationModel = AllocationModel.UNRECOGNIZED_MODEL;

   protected Capacity storageCapacity;
   protected Capacity cpuCapacity;
   protected Capacity memoryCapacity;

   protected String units;
   protected long allocated = 0;
   protected long limit = 0;
   protected int used = 0;
   protected long overhead = 0;

   protected Map<String, NamedResource> resourceEntities = Maps.newLinkedHashMap();
   protected Map<String, NamedResource> availableNetworks = Maps.newLinkedHashMap();

   protected int nicQuota;
   protected int networkQuota;
   protected int vmQuota;
   protected boolean isEnabled = true;

   public VDC getResult() {
      return new VDCImpl(vDC.getName(), vDC.getType(), vDC.getId(), status, org, description, tasks, allocationModel,
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
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("Vdc")) {
         vDC = newNamedResource(attributes);
         String status = Utils.attrOrNull(attributes, "status");
         if (status != null)
            this.status = VDCStatus.fromValue(Integer.parseInt(status));
      } else if (qName.equals("Network")) {
         putNamedResource(availableNetworks, attributes);
      } else if (qName.equals("ResourceEntity")) {
         putNamedResource(resourceEntities, attributes);
      } else if (qName.equals("Link") && "up".equals(Utils.attrOrNull(attributes, "rel"))) {
         org = newNamedResource(attributes);
      } else {
         taskHandler.startElement(uri, localName, qName, attributes);
      }

   }

   public void endElement(String uri, String name, String qName) {
      taskHandler.endElement(uri, name, qName);
      if (qName.equals("Task")) {
         this.tasks.add(taskHandler.getResult());
      } else if (qName.equals("Description")) {
         description = currentOrNull();
      } else if (qName.equals("AllocationModel")) {
         allocationModel = AllocationModel.fromValue(currentOrNull());
      } else if (qName.equals("Units")) {
         units = currentOrNull();
      } else if (qName.equals("Allocated")) {
         allocated = Integer.parseInt(currentOrNull());
      } else if (qName.equals("Used")) {
         used = Integer.parseInt(currentOrNull());
      } else if (qName.equals("Limit")) {
         limit = Integer.parseInt(currentOrNull());
      } else if (qName.equals("Overhead")) {
         overhead = Integer.parseInt(currentOrNull());
      } else if (qName.equals("StorageCapacity")) {
         storageCapacity = new Capacity(units, allocated, limit, used, overhead);
         resetCapacity();
      } else if (qName.equals("Cpu")) {
         cpuCapacity = new Capacity(units, allocated, limit, used, overhead);
         resetCapacity();
      } else if (qName.equals("Memory")) {
         memoryCapacity = new Capacity(units, allocated, limit, used, overhead);
         resetCapacity();
      } else if (qName.equals("DeployedVmsQuota")) {
         vmQuota = (int) limit;
         // vcloud express doesn't have the zero is unlimited rule
         if (vmQuota == -1)
            vmQuota = 0;
      } else if (qName.equals("VmQuota")) {
         vmQuota = Integer.parseInt(currentOrNull());
      } else if (qName.equals("NicQuota")) {
         nicQuota = Integer.parseInt(currentOrNull());
      } else if (qName.equals("NetworkQuota")) {
         networkQuota = Integer.parseInt(currentOrNull());
      } else if (qName.equals("IsEnabled")) {
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
