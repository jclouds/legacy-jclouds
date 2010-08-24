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

import static org.jclouds.Constants.PROPERTY_API_VERSION;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.internal.VAppImpl;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

/**
 * @author Adrian Cole
 */
public class VAppHandler extends ParseSax.HandlerWithResult<VApp> {
   protected final String apiVersion;
   protected final TaskHandler taskHandler;

   @Inject
   public VAppHandler(@Named(PROPERTY_API_VERSION) String apiVersion, TaskHandler taskHandler) {
      this.apiVersion = apiVersion;
      this.taskHandler = taskHandler;
   }

   private StringBuilder currentText = new StringBuilder();

   protected NamedResource vApp;
   protected List<Task> tasks = Lists.newArrayList();
   protected String description;
   protected NamedResource vdc;
   protected Status status;

   public VApp getResult() {
      return new VAppImpl(vApp.getName(), vApp.getType(), vApp.getId(), status, vdc, description, tasks);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("VApp")) {
         vApp = Utils.newNamedResource(attributes);
         String statusString = attributes.getValue(attributes.getIndex("status"));
         status = Status.fromValue(statusString);
         // } else if (qName.equals("VAppItem")) {
         // Utils.putNamedResource(contents, attributes);
      } else if (qName.equals("Link") && "up".equals(Utils.attrOrNull(attributes, "rel"))) {
         vdc = Utils.newNamedResource(attributes);
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
