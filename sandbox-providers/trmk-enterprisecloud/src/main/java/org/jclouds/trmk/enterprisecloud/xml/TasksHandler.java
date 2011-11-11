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
package org.jclouds.trmk.enterprisecloud.xml;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.trmk.enterprisecloud.domain.Task;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
public class TasksHandler extends ParseSax.HandlerWithResult<Set<Task>> {
   public Set<Task> getResult() {
      try {
         return builder.build();
      } finally {
         builder = ImmutableSet.<Task> builder();
      }
   }

   protected final TaskHandler taskHandler;

   protected ImmutableSet.Builder<Task> builder = ImmutableSet.<Task> builder();

   @Inject
   public TasksHandler(TaskHandler taskHandler) {
      this.taskHandler = taskHandler;
   }

   protected boolean inTask;

   protected int depth = 0;

   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      depth++;
      if (depth == 2) {
         if (equalsOrSuffix(qName, "Task")) {
            inTask = true;
         }
      }

      if (inTask) {
         taskHandler.startElement(uri, localName, qName, attrs);
      }

   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      depth--;
      if (depth == 1) {
         if (equalsOrSuffix(qName, "Task")) {
            inTask = false;
            builder.add(taskHandler.getResult());
         }
      }

      if (inTask) {
         taskHandler.endElement(uri, localName, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inTask) {
         taskHandler.characters(ch, start, length);
      }
   }
}
