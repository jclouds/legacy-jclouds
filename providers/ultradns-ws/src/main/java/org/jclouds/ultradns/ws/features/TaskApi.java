/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ultradns.ws.features;

import javax.inject.Named;
import javax.ws.rs.POST;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.ultradns.ws.domain.Task;
import org.jclouds.ultradns.ws.filters.SOAPWrapWithPasswordAuth;
import org.jclouds.ultradns.ws.xml.ElementTextHandler;
import org.jclouds.ultradns.ws.xml.TaskHandler;
import org.jclouds.ultradns.ws.xml.TaskListHandler;

import com.google.common.collect.FluentIterable;

/**
 * @see <a href="https://ultra-api.ultradns.com:8443/UltraDNS_WS/v01?wsdl" />
 * @see <a href="https://www.ultradns.net/api/NUS_API_XML_SOAP.pdf" />
 * @author Adrian Cole
 */
@RequestFilters(SOAPWrapWithPasswordAuth.class)
@VirtualHost
public interface TaskApi {
   /**
    * Runs a test task
    * 
    * @return guid of the task created
    */
   @Named("runTest")
   @POST
   @XMLResponseParser(ElementTextHandler.Guid.class)
   @Payload("<v01:runTest><value>{value}</value></v01:runTest>")
   String runTest(@PayloadParam("value") String value);

   /**
    * @param guid
    *           guid of the task to get information about.
    * @return null if not found
    */
   @Named("getStatusForTask")
   @POST
   @XMLResponseParser(TaskHandler.class)
   @Payload("<v01:getStatusForTask><id><guid>{guid}</guid></id></v01:getStatusForTask>")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Task get(@PayloadParam("guid") String name);

   /**
    * Lists all tasks.
    */
   @Named("getAllTasks")
   @POST
   @XMLResponseParser(TaskListHandler.class)
   @Payload("<v01:getAllTasks/>")
   FluentIterable<Task> list();

   /**
    * clears a background task in either a COMPLETE or ERROR state. 
    * 
    * @param guid
    *           guid of the task to clear.
    */
   @Named("clearTask")
   @POST
   @Payload("<v01:clearTask><id><guid>{guid}</guid></id></v01:clearTask>")
   @Fallback(VoidOnNotFoundOr404.class)
   void clear(@PayloadParam("guid") String name);
}
