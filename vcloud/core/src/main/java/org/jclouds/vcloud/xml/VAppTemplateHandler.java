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
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.Vm;
import org.jclouds.vcloud.domain.internal.VAppTemplateImpl;
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

   @Inject
   public VAppTemplateHandler(TaskHandler taskHandler, VmHandler vmHandler) {
      this.taskHandler = taskHandler;
      this.vmHandler = vmHandler;
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
   protected Set<Vm> children = Sets.newLinkedHashSet();

   public VAppTemplate getResult() {
      return new VAppTemplateImpl(template.getName(), template.getType(), template.getHref(), status, vdc, description,
               tasks, ovfDescriptorUploaded, vAppScopedLocalId, children);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (qName.endsWith("Children")) {
         inChildren = true;
      } else if (qName.endsWith("Tasks")) {
         inTasks = true;
      }
      if (inChildren) {
         vmHandler.startElement(uri, localName, qName, attrs);
      } else if (inTasks) {
         taskHandler.startElement(uri, localName, qName, attrs);
      } else if (qName.equals("VAppTemplate")) {
         template = newReferenceType(attributes);
         if (attributes.containsKey("status"))
            this.status = Status.fromValue(Integer.parseInt(attributes.get("status")));
      } else if (qName.equals("Link") && "up".equals(attributes.get("rel"))) {
         vdc = newReferenceType(attributes);
      }

   }

   public void endElement(String uri, String name, String qName) {
      if (qName.endsWith("Children")) {
         inChildren = false;
         this.children.add(vmHandler.getResult());
      } else if (qName.endsWith("Tasks")) {
         inTasks = false;
         this.tasks.add(taskHandler.getResult());
      }
      if (inChildren) {
         vmHandler.endElement(uri, name, qName);
      } else if (inTasks) {
         taskHandler.endElement(uri, name, qName);
      } else if (qName.equals("Description")) {
         description = currentOrNull();
      } else if (qName.equals("VAppScopedLocalId")) {
         vAppScopedLocalId = currentOrNull();
      } else if (qName.equals("ovfDescriptorUploaded")) {
         ovfDescriptorUploaded = Boolean.parseBoolean(currentOrNull());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      if (inTasks)
         taskHandler.characters(ch, start, length);
      if (inChildren)
         vmHandler.characters(ch, start, length);
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
