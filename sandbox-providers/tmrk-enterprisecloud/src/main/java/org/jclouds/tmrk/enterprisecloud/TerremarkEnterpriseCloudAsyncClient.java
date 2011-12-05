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
package org.jclouds.tmrk.enterprisecloud;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.tmrk.enterprisecloud.features.*;

/**
 * Provides asynchronous access to TerremarkEnterpriseCloud via their REST API.
 * <p/>
 * 
 * @see TerremarkEnterpriseCloudClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Adrian Cole
 */
public interface TerremarkEnterpriseCloudAsyncClient {

   /**
    * Provides asynchronous access to Location features.
    */
   @Delegate
   LocationAsyncClient getLocationClient();

   /**
    * Provides asynchronous access to Resource features.
    */
   @Delegate
   ResourceAsyncClient getResourceClient();

   /**
    * Provides asynchronous access to Task features.
    */
   @Delegate
   TaskAsyncClient getTaskClient();

   /**
    * Provides asynchronous access to VirtualMachine features.
    */
   @Delegate
   VirtualMachineAsyncClient getVirtualMachineClient();

   /**
    * Provides asynchronous access to Template features.
    */
   @Delegate
   TemplateAsyncClient getTemplateClient();
}
