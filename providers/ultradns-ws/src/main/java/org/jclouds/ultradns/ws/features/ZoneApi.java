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
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
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
    * creates a primary zone and its supporting records (SOA, NS and A). The
    * user who issues this request becomes the owner of this zone.
    * 
    * @param name
    *           the fully qualified name of the new zone.
    * @param accountId
    *           the account to create the zone in
    */
   void createInAccount(String name, String accountId) throws ResourceAlreadyExistsException;

   /**
    * Retrieves information about the specified zone
    * 
    * @param name
    *           the fully-qualified name, including the trailing dot, of the
    *           zone to get information about.
    * @return null if not found
    */
   @Nullable
   ZoneProperties get(String name);

   /**
    * Lists all zones in the specified account.
    * 
    * @returns empty if no zones, or account doesn't exist
    */
   FluentIterable<Zone> listByAccount(String accountId);

   /**
    * Lists all zones in the specified account of type
    * 
    * @throws ResourceNotFoundException
    *            if the account doesn't exist
    */
   FluentIterable<Zone> listByAccountAndType(String accountId, Type type) throws ResourceNotFoundException;

   /**
    * deletes a zone and all its resource records
    * 
    * @param name
    *           the fully-qualified name, including the trailing dot, of the
    *           zone you want to delete.
    * @return null if not found
    */
   void delete(String name);
}
