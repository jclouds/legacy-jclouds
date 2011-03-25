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

package org.jclouds.savvis.vpdc.xml;

import static org.jclouds.savvis.vpdc.util.Utils.cleanseAttributes;
import static org.jclouds.savvis.vpdc.util.Utils.newResource;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.Task;
import org.jclouds.savvis.vpdc.domain.TaskError;
import org.jclouds.savvis.vpdc.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TaskHandler extends ParseSax.HandlerWithResult<Task> {
   @javax.annotation.Resource
   protected Logger logger = Logger.NULL;

   protected final DateService dateService;

   private Task.Builder builder = Task.builder();

   @Inject
   public TaskHandler(DateService dateService) {
      this.dateService = dateService;
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
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "Task")) {
         Resource task = newResource(attributes);
         builder.id(task.getId());
         builder.type(task.getType());
         builder.href(task.getHref());
         if (attributes.containsKey("startTime"))
            builder.startTime(parseDate(attributes.get("startTime")));
         if (attributes.containsKey("endTime"))
            builder.endTime(parseDate(attributes.get("endTime")));
         builder.status(Task.Status.fromValue(attributes.get("status")));
      } else if (equalsOrSuffix(qName, "Owner")) {
         builder.owner(Utils.newResource(attributes));
      } else if (equalsOrSuffix(qName, "Result")) {
         builder.result(Utils.newResource(attributes));
      } else if (equalsOrSuffix(qName, "Error")) {
         builder.error(new TaskError(attributes.get("message"), Integer.parseInt(attributes.get("majorErrorCode")),
               Integer.parseInt(attributes.get("minorErrorCode")), attributes.get("vendorSpecificErrorCode")));
      }

   }

   private Date parseDate(String toParse) {
      try {
         return dateService.iso8601DateParse(toParse);
      } catch (RuntimeException e) {
         logger.error(e, "error parsing date, %s", toParse);
      }
      return null;
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
   }

   @Override
   public void characters(char ch[], int start, int length) {
   }

}
