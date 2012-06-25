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

import java.util.Map;
import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.internal.TasksListImpl;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class TasksListHandler extends ParseSax.HandlerWithResult<TasksList> {

   private SortedSet<Task> tasks = Sets.newTreeSet();
   private final TaskHandler taskHandler;
   private ReferenceType resource;

   @Inject
   public TasksListHandler(TaskHandler taskHandler) {
      this.taskHandler = taskHandler;
   }

   public TasksList getResult() {
      return new TasksListImpl(resource.getHref(), tasks);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (SaxUtils.equalsOrSuffix(qName, "TasksList")) {
         resource = Utils.newReferenceType(attributes);
      } else if (SaxUtils.equalsOrSuffix(qName, "Link") && "self".equals(attributes.get("rel"))) {
         resource = Utils.newReferenceType(attributes);
      } else {
         taskHandler.startElement(uri, localName, qName, attrs);
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      taskHandler.endElement(uri, localName, qName);
      if (SaxUtils.equalsOrSuffix(qName, "Task")) {
         this.tasks.add(taskHandler.getResult());
      }
   }

}
