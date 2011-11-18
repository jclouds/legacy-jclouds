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

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.tmrk.enterprisecloud.features.TaskClient;
import org.jclouds.tmrk.enterprisecloud.features.VirtualMachineClient;

/**
 * Provides synchronous access to TerremarkEnterpriseCloud.
 * <p/>
 * 
 * @see TerremarkEnterpriseCloudAsyncClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface TerremarkEnterpriseCloudClient {

   /**
    * Provides synchronous access to Task features.
    */
   @Delegate
   TaskClient getTaskClient();

   /**
    * Provides synchronous access to VirtualMachine features.
    */
   @Delegate
   VirtualMachineClient getVirtualMachineClient();
}
