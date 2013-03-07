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

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.domain.LBPool;
import org.jclouds.ultradns.ws.domain.LBPool.Type;
import org.jclouds.ultradns.ws.domain.PoolRecord;

import com.google.common.collect.FluentIterable;

/**
 * @see LBPoolAsyncApi
 * @author Adrian Cole
 */
public interface LBPoolApi {

   /**
    * Returns all pools in the zone.
    * 
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   FluentIterable<LBPool> list() throws ResourceNotFoundException;

   /**
    * Returns all records in the pool.
    * 
    * @throws ResourceNotFoundException
    *            if the pool doesn't exist
    */
   FluentIterable<PoolRecord> listRecords(String poolId) throws ResourceNotFoundException;

   /**
    * Returns all pools with the specified {@link LBPool#getType()}
    * 
    * @param type
    *           the {@link LBPool#getType() type}
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   FluentIterable<LBPool> listByType(Type type) throws ResourceNotFoundException;

   /**
    * removes a pool and all its records and probes
    * 
    * @param id the {@link LBPool#getId() id}
    */
   void delete(String id);
}
