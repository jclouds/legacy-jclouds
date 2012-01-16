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

import static org.jclouds.util.SaxUtils.equalsOrSuffix;
import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.internal.VAppTemplateImpl;
import org.jclouds.vcloud.domain.ovf.VCloudNetworkSection;
import org.jclouds.vcloud.xml.ovf.VCloudNetworkSectionHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class VAppTemplateHandler extends ParseSax.HandlerWithResult<VAppTemplate> {

   protected final TaskHandler taskHandler;
   protected final VmHandler vmHandler;
   protected final VCloudNetworkSectionHandler networkSectionHandler;

   @Inject
   public VAppTemplateHandler(TaskHandler taskHandler, VmHandler vmHandler,
            VCloudNetworkSectionHandler networkSectionHandler) {
      this.taskHandler = taskHandler;
      this.vmHandler = vmHandler;
      this.networkSectionHandler = networkSectionHandler;
   }

   protected StringBuilder currentText = new StringBuilder();

   protected ReferenceType template;
   protected Status status;
   protected ReferenceType vdc;
   protected String description;
   protected List<Task> tasks = Lists.newArrayList();
   protected boolean ovfDescriptorUploaded = true;
   protected String vAppScopedLocalId;

   private boolean inChildren;
   private boolean inTasks;
   private boolean inNetworkSection;
   protected Set<Vm> children = Sets.newLinkedHashSet();
   private VCloudNetworkSection networkSection;

   public VAppTemplate getResult() {
      return new VAppTemplateImpl(template.getName(), template.getType(), template.getHref(), status, vdc, description,
               tasks, ovfDescriptorUploaded, vAppScopedLocalId, children, networkSection);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "Children")) {
         inChildren = true;
      } else if (equalsOrSuffix(qName, "Tasks")) {
         inTasks = true;
      } else if (equalsOrSuffix(qName, "NetworkSection")) {
         inNetworkSection = true;
      }
      if (inChildren) {
         vmHandler.startElement(uri, localName, qName, attrs);
      } else if (inTasks) {
         taskHandler.startElement(uri, localName, qName, attrs);
      } else if (inNetworkSection) {
         networkSectionHandler.startElement(uri, localName, qName, attrs);
      } else if (equalsOrSuffix(qName, "VAppTemplate")) {
         template = newReferenceType(attributes);
         if (attributes.containsKey("status"))
            this.status = Status.fromValue(Integer.parseInt(attributes.get("status")));
      } else if (equalsOrSuffix(qName, "Link") && "up".equals(attributes.get("rel"))) {
         vdc = newReferenceType(attributes);
      }

   }

   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "Children")) {
         inChildren = false;
         Vm vm = vmHandler.getResult();
         if (vm != null)
            this.children.add(vmHandler.getResult());
      } else if (equalsOrSuffix(qName, "Tasks")) {
         inTasks = false;
         this.tasks.add(taskHandler.getResult());
      } else if (equalsOrSuffix(qName, "NetworkSection")) {
         inNetworkSection = false;
         this.networkSection = networkSectionHandler.getResult();
      }
      if (inChildren) {
         vmHandler.endElement(uri, name, qName);
      } else if (inTasks) {
         taskHandler.endElement(uri, name, qName);
      } else if (inNetworkSection) {
         networkSectionHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "Description")) {
         description = currentOrNull();
      } else if (equalsOrSuffix(qName, "VAppScopedLocalId")) {
         vAppScopedLocalId = currentOrNull();
      } else if (equalsOrSuffix(qName, "ovfDescriptorUploaded")) {
         ovfDescriptorUploaded = Boolean.parseBoolean(currentOrNull());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      if (inTasks)
         taskHandler.characters(ch, start, length);
      else if (inChildren)
         vmHandler.characters(ch, start, length);
      else if (inNetworkSection)
         networkSectionHandler.characters(ch, start, length);
      else
         currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
