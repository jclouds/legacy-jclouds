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

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.features.OrgClient;
import org.jclouds.vcloud.director.v1_5.features.TaskClient;

import com.google.inject.Provides;

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
    * @return synchronous access to Org features
    */
   @Delegate
   OrgClient getOrgClient();
   
   /**
    * @return synchronous access to Task features
    */
   @Delegate
   TaskClient getTaskClient();
}
