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
package org.jclouds.vcloud;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.TasksList;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.options.InstantiateVAppTemplateOptions;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://community.vcloudexpress.terremark.com/en-us/discussion_forums/f/60.aspx" />
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VCloudClient {

   Catalog getCatalog();

   VDC getDefaultVDC();

   TasksList getDefaultTasksList();

   Task deployVApp(String vAppId);

   void deleteVApp(String vAppId);

   Task undeployVApp(String vAppId);

   /**
    * This call powers on the vApp, as specified in the vApp's ovf:Startup element.
    */
   Task powerOnVApp(String vAppId);

   /**
    * This call powers off the vApp, as specified in the vApp's ovf:Startup element.
    */
   Task powerOffVApp(String vAppId);

   /**
    * This call shuts down the vApp.
    */
   void shutdownVApp(String vAppId);

   /**
    * This call resets the vApp.
    */
   Task resetVApp(String vAppId);

   /**
    * This call suspends the vApp.
    */
   Task suspendVApp(String vAppId);

   Task getTask(String taskId);

   void cancelTask(String taskId);

   VApp getVApp(String appId);

   VApp instantiateVAppTemplate(String appName, String templateId,
            InstantiateVAppTemplateOptions... options);
}
