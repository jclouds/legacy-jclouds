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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.trmk.enterprisecloud.domain.NamedResource;
import org.jclouds.trmk.enterprisecloud.domain.Task;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TaskHandler extends ParseSax.HandlerWithResult<Task> {

   protected final DateService dateService;

   protected StringBuilder currentText = new StringBuilder();
   protected Task.Builder builder = Task.builder();

   @Inject
   public TaskHandler(DateService dateService) {
      this.dateService = checkNotNull(dateService, "dateService");
   }

   public Task getResult() {
      try {
         return builder.build();
      } finally {
         builder = Task.builder();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "Task")) {
         builder.fromAttributes(attributes);
      } else if (equalsOrSuffix(qName, "ImpactedItem")) {
         builder.impactedItem(NamedResource.builder().fromAttributes(attributes).build());
      } else if (equalsOrSuffix(qName, "InitiatedBy")) {
         builder.initiatedBy(NamedResource.builder().fromAttributes(attributes).build());
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "Operation")) {
         builder.operation(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Status")) {
         String status = currentOrNull(currentText);
         if (status != null)
            builder.status(Task.Status.fromValue(status));
      } else if (equalsOrSuffix(qName, "StartTime")) {
         String date = currentOrNull(currentText);
         if (date != null)
            builder.startTime(dateService.iso8601DateParse(date));
      } else if (equalsOrSuffix(qName, "CompletedTime")) {
         String date = currentOrNull(currentText);
         if (date != null)
            builder.completedTime(dateService.iso8601DateParse(date));
      } else if (equalsOrSuffix(qName, "Notes")) {
         builder.notes(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "ErrorMessage")) {
         builder.errorMessage(currentOrNull(currentText));

      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
