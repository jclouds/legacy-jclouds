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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.jclouds.rimuhosting.miro.RimuHostingContextBuilder;
import org.jclouds.rimuhosting.miro.RimuHostingPropertiesBuilder;
import org.jclouds.compute.ComputeService;
import com.google.inject.Injector;

/**
 * @author Ivan Meredith
 */
public class ComputeTask extends Task {
   private final String ACTION_CREATE = "create";

   private String action;

   private ServerElement serverElement;
   public void execute() throws BuildException {
      if(ACTION_CREATE.equalsIgnoreCase(action)){
         if(getServerElement() != null){
            Injector injector = new RimuHostingContextBuilder(new RimuHostingPropertiesBuilder("test", "Test").relaxSSLHostname().build()).buildInjector();

            ComputeService computeService = injector.getInstance(ComputeService.class);

            computeService.createServerAndWait("test.com","MIRO1B","lenny");
         }
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
}
