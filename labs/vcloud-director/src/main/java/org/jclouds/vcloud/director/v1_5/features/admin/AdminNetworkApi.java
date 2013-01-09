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
package org.jclouds.vcloud.director.v1_5.features.admin;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.features.NetworkApi;

/**
 * Provides synchronous access to admin {@link Network} objects.
 * 
 * @see AdminNetworkAsyncApi
 * @author danikov, Adrian Cole
 */
public interface AdminNetworkApi extends NetworkApi {

   /**
    * Gets admin representation of network. This operation could return admin representation of
    * organization network or external network. vApp networks do not have admin representation.
    * 
    * <pre>
    * GET /admin/network/{id}
    * </pre>
    * 
    * @param networkUrn
    *           the reference for the network
    * @return the network
    */
   @Override
   Network get(String networkUrn);

   @Override
   Network get(URI networkAdminHref);

   /**
    * Modifies an org network
    * 
    * <pre>
    * PUT /admin/network/{id}
    * </pre>
    * 
    * @param networkUrn
    *           the reference for the network
    * @param network
    *           the edited network
    * @return a task. This operation is asynchronous and the user should monitor the returned task
    *         status in order to check when it is completed.
    */
   Task edit(String networkUrn, OrgNetwork network);

   Task edit(URI networkAdminHref, OrgNetwork network);

   /**
    * Reset(undeploy & redeploy) networking services on a logical network. The reset operation can
    * be performed on: - external networks - organization networks - vApp networks The reset
    * operation can be performed only on deployed networks.
    * 
    * <pre>
    * POST /admin/network/{id}/action/reset
    * </pre>
    * 
    * @param networkUrn
    *           the reference for the network
    * @return a task. This operation is asynchronous and the user should monitor the returned task
    *         status in order to check when it is completed.
    */
   Task reset(String networkUrn);
   
   Task reset(URI networkAdminHref);
}
