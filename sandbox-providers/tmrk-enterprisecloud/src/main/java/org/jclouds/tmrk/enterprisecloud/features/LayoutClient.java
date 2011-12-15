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
package org.jclouds.tmrk.enterprisecloud.features;

import org.jclouds.concurrent.Timeout;
import org.jclouds.tmrk.enterprisecloud.domain.layout.DeviceLayout;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to Location.
 * <p/>
 * 
 * @see LayoutAsyncClient
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=984&Lang=1&SID="
 *      />
 * @author Jason King
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface LayoutClient {

   /**
    * The Get Layouts call returns information regarding the row and group of network hosts in an environment.
    * Rows and groups allow aggregation of servers along logical boundaries defined by the organization.
    * @param uri the uri based on the environment
    *  e.g. /cloudapi/ecloud/layout/environments/{id}
    * @return the DeviceLayout
    */
   DeviceLayout getLayouts(URI uri);

   /**
    * The Get Layouts by Compute Pool call returns information regarding the row and group of network hosts
    * for a specified compute pool in an environment.
    * Rows and groups allow aggregation of servers along logical boundaries defined by the organization.
    * @param uri the uri based on the compute pool
    *  e.g. /cloudapi/ecloud/layout/computePools/{id}
    * @return the DeviceLayout
    */
   DeviceLayout getLayoutsInComputePool(URI uri);

}
