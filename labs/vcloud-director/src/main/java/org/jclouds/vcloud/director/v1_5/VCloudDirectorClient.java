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
package org.jclouds.vcloud.director.v1_5;

import com.google.inject.Provides;
import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.*;
import org.jclouds.vcloud.director.v1_5.domain.ovf.Network;
import org.jclouds.vcloud.director.v1_5.features.*;

import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to VCloudDirector.
 * 
 * @see VCloudDirectorAsyncClient
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface VCloudDirectorClient {
   /**
    * @return the current login session
    */
   @Provides
   Session getCurrentSession();

   /**
    * @return asynchronous access to query features
    */
   @Delegate
   QueryClient getQueryClient();

   /**
    * @return synchronous access to {@link Org} features
    */
   @Delegate
   OrgClient getOrgClient();
   
   /**
    * @return synchronous access to {@link Task} features
    */
   @Delegate
   TaskClient getTaskClient();

   /**
    * @return synchronous access to {@link Network} features
    */
   @Delegate
   NetworkClient getNetworkClient();

   /**
    * @return synchronous access to {@link Catalog} features
    */
   @Delegate
   CatalogClient getCatalogClient();

   /**
    * @return synchronous access to {@link Media} features
    */
   @Delegate
   MediaClient getMediaClient();

   /**
    * @return synchronous access to {@link Vdc} features
    */
   @Delegate
   VdcClient getVdcClient();

   /*
    * @return synchronous access to {@link org.jclouds.vcloud.director.v1_5.domain.VAppTemplate} features
    */
   @Delegate
   VAppTemplateClient getVAppTemplateClient();

}
