/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.text.ParseException;
import java.util.Date;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.Task.Error;
import org.jclouds.vcloud.domain.internal.TaskImpl;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TaskHandler extends ParseSax.HandlerWithResult<Task> {
   protected final DateService dateService;

   private NamedResource taskLink;
   private NamedResource owner;
   private NamedResource result;
   private TaskStatus status;
   private Date startTime;
   private Date endTime;
   private Task task;
   private Error error;

   @Resource
   protected Logger logger = Logger.NULL;


   @Inject
   public TaskHandler(DateService dateService) {
      this.dateService = dateService;
   }

   public Task getResult() {
      return task;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equalsIgnoreCase("Task")) {
         if (attributes.getIndex("href") != -1)// queued tasks may not have an href yet
            taskLink = Utils.newNamedResource(attributes);
         status = TaskStatus.fromValue(attributes.getValue(attributes.getIndex("status")));
         if (attributes.getIndex("startTime") != -1)
            startTime = parseDate(attributes, "startTime");
         if (attributes.getIndex("endTime") != -1) {
            endTime = parseDate(attributes, "endTime");
         }
      } else if (qName.equals("Owner")) {
         owner = Utils.newNamedResource(attributes);
      } else if (qName.equals("Link") && attributes.getIndex("rel") != -1
               && attributes.getValue(attributes.getIndex("rel")).equals("self")) {
         taskLink = Utils.newNamedResource(attributes);
      } else if (qName.equals("Result")) {
         result = Utils.newNamedResource(attributes);
      } else if (qName.equals("Error")) {
         error = Utils.newError(attributes);
      }
   }

   private Date parseDate(Attributes attributes, String attribute) {
      String toParse =attributes.getValue(attributes.getIndex(attribute)); 
      try {
         return dateService.iso8601DateParse(toParse);
      } catch (RuntimeException e) {
         if (e.getCause() instanceof ParseException) {
            try {
               if (!toParse.endsWith("Z"))
                  toParse+="Z";
               return dateService.iso8601SecondsDateParse(toParse);
            } catch (RuntimeException ex) {
               logger.error(e, "error parsing date");
            }
         } else {
            logger.error(e, "error parsing date");
         }
      }
      return null;
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equalsIgnoreCase("Task")) {
         this.task = new TaskImpl(taskLink.getId(), taskLink.getLocation(), status, startTime, endTime, owner, result, error);
         taskLink = null;
         status = null;
         startTime = null;
         endTime = null;
         owner = null;
         result = null;
         error = null;
      }
   }

}
