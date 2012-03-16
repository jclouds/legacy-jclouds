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

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.util.SaxUtils;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.VCloudError;
import org.jclouds.vcloud.domain.internal.TaskImpl;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TaskHandler extends ParseSax.HandlerWithResult<Task> {
   protected final DateService dateService;
   private String operation;
   private ReferenceType taskLink;
   private ReferenceType owner;
   private TaskStatus status;
   private Date startTime;
   private Date endTime;
   private Date expiryTime;
   private Task task;
   private VCloudError error;
   private boolean inOwner;

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
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "Task")) {
         if (attributes.get("href") != null && !inOwner)// queued tasks may not have an
            // href yet
            taskLink = Utils.newReferenceType(attributes);
         status = TaskStatus.fromValue(attributes.get("status"));
         operation = attributes.get("operation");
         if (attributes.containsKey("startTime"))
            startTime = parseDate(attributes.get("startTime"));
         if (attributes.containsKey("endTime"))
            endTime = parseDate(attributes.get("endTime"));
         if (attributes.containsKey("expiryTime"))
            expiryTime = parseDate(attributes.get("expiryTime"));
         // TODO technically the old Result object should only be owner for copy and delete tasks
      } else if (equalsOrSuffix(qName, "Owner") || equalsOrSuffix(qName, "Result")) {
         owner = Utils.newReferenceType(attributes);
      } else if (equalsOrSuffix(qName, "Link") && "self".equals(attributes.get("rel"))) {
         taskLink = Utils.newReferenceType(attributes);
      } else if (equalsOrSuffix(qName, "Error")) {
         error = Utils.newError(attributes);
      }
   }

   private Date parseDate(String toParse) {
      try {
         return dateService.iso8601DateParse(toParse);
      } catch (RuntimeException e) {
         if (e.getCause() instanceof ParseException) {
            if (!toParse.endsWith("Z"))
               toParse += "Z";
            return dateService.iso8601SecondsDateParse(toParse);
         } else {
            logger.error(e, "error parsing date");
         }
      }
      return null;
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "Task")) {
         this.task = new TaskImpl(taskLink.getHref(), operation, status, startTime, endTime, expiryTime, owner, error);
         operation = null;
         taskLink = null;
         status = null;
         startTime = null;
         endTime = null;
         owner = null;
         error = null;
      } else if (equalsOrSuffix(qName, "Owner")) {
         inOwner = false;
      }
   }

   public void characters(char ch[], int start, int length) {
   }

}
