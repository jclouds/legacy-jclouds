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
package org.jclouds.ultradns.ws.features;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.domain.Zone.Type;
import org.jclouds.ultradns.ws.domain.ZoneProperties;

import com.google.common.collect.FluentIterable;

/**
 * @see ZoneAsyncApi
 * @author Adrian Cole
 */
public interface ZoneApi {

   /**
    * Retrieves information about the specified zone
    * 
    * @param name
    *           Name of the zone to get information about.
    * @return null if not found
    */
   @Nullable
   ZoneProperties get(String name);

   /**
    * Lists all zones in the specified account.
    * 
    * @throws ResourceNotFoundException
    *            if the account doesn't exist
    */
   FluentIterable<Zone> listByAccount(String accountId) throws ResourceNotFoundException;

   /**
    * Lists all zones in the specified account of type
    * 
    * @throws ResourceNotFoundException
    *            if the account doesn't exist
    */
   FluentIterable<Zone> listByAccountAndType(String accountId, Type type) throws ResourceNotFoundException;
}
