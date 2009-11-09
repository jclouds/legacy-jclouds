/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.xml;

import java.net.URI;
import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.domain.Link;
import org.jclouds.rest.util.Utils;
import org.jclouds.util.DateService;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TaskStatus;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.internal.TaskImpl;
import org.jclouds.vcloud.domain.internal.TasksListImpl;
import org.joda.time.DateTime;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class TasksListHandler extends ParseSax.HandlerWithResult<TasksList> {

   private SortedSet<Task> tasks = Sets.newTreeSet();
   protected final DateService dateService;

   private Link taskLink;
   private Link owner;
   private Link result;
   private TaskStatus status;
   private DateTime startTime;
   private DateTime endTime;
   private URI location;

   @Inject
   public TasksListHandler(DateService dateService) {
      this.dateService = dateService;
   }

   public TasksList getResult() {
      return new TasksListImpl(location, tasks);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      if (qName.equals("TasksList")) {
         location = Utils.newLink(attributes).getLocation();
      } else if (qName.equals("Task")) {
         taskLink = Utils.newLink(attributes);
         status = TaskStatus.fromValue(attributes.getValue(attributes.getIndex("status")));
         startTime = dateService.iso8601DateParse(attributes.getValue(attributes
                  .getIndex("startTime")));
         endTime = dateService
                  .iso8601DateParse(attributes.getValue(attributes.getIndex("endTime")));
      } else if (qName.equals("Owner")) {
         owner = Utils.newLink(attributes);
      } else if (qName.equals("Result")) {
         result = Utils.newLink(attributes);
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equals("Task")) {
         tasks.add(new TaskImpl(taskLink.getType(), taskLink.getLocation(), status, startTime,
                  endTime, owner, result));
      }
   }

}
