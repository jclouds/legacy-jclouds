/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.glesys.features;

import java.util.Set;
import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.glesys.options.AddDomainOptions;
import org.jclouds.glesys.options.AddRecordOptions;
import org.jclouds.glesys.options.DomainOptions;
import org.jclouds.glesys.options.UpdateRecordOptions;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Domain requests.
 * <p/>
 *
 * @author Adam Lowe
 * @see DomainAsyncApi
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
public interface DomainApi {

   /**
    * Get a list of all domains for this account.
    *
    * @return an account's associated domain objects.
    */
   FluentIterable<Domain> list();

   /**
    * Get a specific domain.
    *
    * @return the requested domain object.
    */
   Domain get(String domain);

   /**
    * Add a domain to the Glesys dns-system
    *
    * @param domain  the name of the domain to add.
    * @param options optional parameters
    * @return information about the added domain
    */
   Domain create(String domain, AddDomainOptions... options);

   /**
    * Update a domain to the Glesys dns-system
    *
    * @param domain  the name of the domain to add.
    * @param options optional parameters
    * @return information about the modified domain
    */
   Domain update(String domain, DomainOptions options);

   /**
    * Remove a domain to the Glesys dns-system
    *
    * @param domain the name of the domain to remove
    */
   void delete(String domain);

   /**
    * Retrieve the DNS records for a given domain
    *
    * @param domain the name of the domain to retrieve records for
    */
   Set<DomainRecord> listRecords(String domain);

   /**
    * Add a DNS Record
    *
    * @param domain  the domain to add the record to
    * @param options optional settings for the record
    */
   DomainRecord createRecord(String domain, String host, String type, String data, AddRecordOptions... options);

   /**
    * Modify a specific DNS Record
    *
    * @param recordId the id for the record to edit
    * @param options  the settings to change
    * @see #listRecords to retrieve the necessary ids
    */
   DomainRecord updateRecord(String recordId, UpdateRecordOptions options);

   /**
    * Delete a DNS record
    *
    * @param recordId the id for the record to delete
    * @see #listRecords to retrieve the necessary ids
    */
   void deleteRecord(String recordId);

}
