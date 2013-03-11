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
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.domain.ResourceRecord;
import org.jclouds.ultradns.ws.domain.ResourceRecordMetadata;
import org.jclouds.ultradns.ws.domain.RoundRobinPool;

import com.google.common.collect.FluentIterable;

/**
 * @see RoundRobinPoolAsyncApi
 * @author Adrian Cole
 */
public interface RoundRobinPoolApi {
   /**
    * creates a round robin pool for {@code A} (ipv4) records
    * 
    * @param name
    *           {@link RoundRobinPool#getName() name} of the RR pool
    * @param hostname
    *           {@link RoundRobinPool#getDName() dname} of the RR pool {ex.
    *           www.jclouds.org.}
    * @return the {@code guid} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a pool already exists with the same attrs
    */
   String createAPoolForHostname(String name, String hostname) throws ResourceAlreadyExistsException;

   /**
    * adds a new {@code A} record to the pool
    * 
    * @param lbPoolID
    *           the pool to add the record to.
    * @param ipv4Address
    *           the ipv4 address
    * @param ttl
    *           the {@link ResourceRecord#getTTL ttl} of the record
    * @return the {@code guid} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   String addARecordWithAddressAndTTL(String lbPoolID, String ipv4Address, int ttl)
         throws ResourceAlreadyExistsException;

   /**
    * creates a round robin pool for {@code AAAA} (ipv6) records
    * 
    * @param name
    *           {@link RoundRobinPool#getName() name} of the RR pool
    * @param hostname
    *           {@link RoundRobinPool#getDName() hostname} {ex.
    *           www.jclouds.org.}
    * @return the {@code guid} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a pool already exists with the same attrs
    */
   String createAAAAPoolForHostname(String name, String hostname) throws ResourceAlreadyExistsException;

   /**
    * adds a new {@code AAAA} record to the pool
    * 
    * @param lbPoolID
    *           the pool to add the record to.
    * @param ipv6Address
    *           the ipv6 address
    * @param ttl
    *           the {@link ResourceRecord#getTTL ttl} of the record
    * @return the {@code guid} of the new record
    * @throws ResourceAlreadyExistsException
    *            if a record already exists with the same attrs
    */
   String addAAAARecordWithAddressAndTTL(String lbPoolID, String ipv6Address, int ttl)
         throws ResourceAlreadyExistsException;

   /**
    * updates an existing A or AAAA record in the pool.
    * 
    * @param lbPoolID
    *           the pool to add the record to.
    * @param guid
    *           the global unique identifier for the resource record {@see
    *           ResourceRecordMetadata#getGuid()}
    * @param address
    *           the ipv4 or ipv6 address
    * @param ttl
    *           the {@link ResourceRecord#getTTL ttl} of the record
    * 
    * @throws ResourceNotFoundException
    *            if the guid doesn't exist
    */
   void updateRecordWithAddressAndTTL(String lbPoolID, String guid, String address, int ttl)
         throws ResourceNotFoundException;

   /**
    * Returns all round robin pools in the zone.
    * 
    * @throws ResourceNotFoundException
    *            if the zone doesn't exist
    */
   FluentIterable<RoundRobinPool> list() throws ResourceNotFoundException;

   /**
    * Returns all records in the round robin pool.
    * 
    * @throws ResourceNotFoundException
    *            if the pool doesn't exist
    */
   FluentIterable<ResourceRecordMetadata> listRecords(String poolId) throws ResourceNotFoundException;

   /**
    * deletes a specific pooled resource record
    * 
    * @param guid
    *           the global unique identifier for the resource record {@see
    *           ResourceRecordMetadata#getGuid()}
    */
   void deleteRecord(String guid);

   /**
    * removes a pool and all its records and probes
    * 
    * @param id
    *           the {@link RoundRobinPool#getId() id}
    */
   void delete(String id);
}
