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
package org.jclouds.tools.ant;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceFactory;
import org.jclouds.http.HttpUtils;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.io.Resources;

/**
 * @author Ivan Meredith
 */
public class ComputeTask extends Task {
   private final Map<URI, ComputeService> computeMap;

   public ComputeTask() throws IOException {
      this(buildComputeMap(loadDefaultProperties()));
   }

   static Properties loadDefaultProperties() throws IOException {
      Properties properties = new Properties();
      properties.load(Resources.newInputStreamSupplier(Resources.getResource("compute.properties"))
               .getInput());
      return properties;
   }

   static Map<URI, ComputeService> buildComputeMap(final Properties props) {
      return new MapMaker().makeComputingMap(new Function<URI, ComputeService>() {

         @Override
         public ComputeService apply(URI from) {
            return new ComputeServiceFactory(props).create(from);
         }

      });

   }

   public ComputeTask(Map<URI, ComputeService> computeMap) {
      this.computeMap = computeMap;
   }

   private final String ACTION_CREATE = "create";

   private String provider;
   private String action;
   private ServerElement serverElement;

   public void execute() throws BuildException {
      if (ACTION_CREATE.equalsIgnoreCase(action)) {
         ComputeService computeService = computeMap.get(HttpUtils.createUri(provider));
         log("hello");
         computeService.createServer("test.com", "MIRO1B", "lenny");
      }
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public ServerElement getServerElement() {
      return serverElement;
   }

   public void setServerElement(ServerElement serverElement) {
      this.serverElement = serverElement;
   }

   public void setProvider(String provider) {
      this.provider = provider;
   }

   public String getProvider() {
      return provider;
   }
}
