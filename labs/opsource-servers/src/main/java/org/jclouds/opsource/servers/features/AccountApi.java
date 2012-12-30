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
package org.jclouds.opsource.servers.features;

import org.jclouds.opsource.servers.domain.Account;
import org.jclouds.opsource.servers.domain.DataCentersList;

/**
 * Provides synchronous access to Account.
 * <p/>
 * 
 * @see AccountAsyncApi
 * @author Adrian Cole
 */
public interface AccountApi {

   /**
    * Before you can begin using the range of Server, Network and Image APIs,
    * you will need to first obtain your organization details.
    * 
    * @return the user's details, including their organization ID.
    */
   Account getMyAccount();
   
   /**
    * identifies the list of data centers available to the organization of the authenticating user
    * @param orgId
    * @return
    */
  DataCentersList getDataCentersWithLimits(String orgId);

}
