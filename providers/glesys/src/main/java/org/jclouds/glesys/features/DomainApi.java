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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.glesys.options.AddDomainOptions;
import org.jclouds.glesys.options.AddRecordOptions;
import org.jclouds.glesys.options.DomainOptions;
import org.jclouds.glesys.options.UpdateRecordOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Domain requests.
 * <p/>
 *
 * @author Adam Lowe
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
@RequestFilters(BasicAuthentication.class)
public interface DomainApi {

   /**
    * Get a list of all domains for this account.
    *
    * @return an account's associated domain objects.
    */
   @Named("domain:list")
   @POST
   @Path("/domain/list/format/json")
   @SelectJson("domains")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.EmptyFluentIterableOnNotFoundOr404.class)
   FluentIterable<Domain> list();

   /**
    * Get a specific domain.
    *
    * @return the requested domain object.
    */
   @Named("domain:details")
   @POST
   @Path("/domain/details/format/json")
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   Domain get(@FormParam("domainname") String name);

   /**
    * Add a domain to the Glesys dns-system
    *
    * @param name  the name of the domain to add.
    * @param options optional parameters
    * @return information about the added domain
    */
   @Named("domain:add")
   @POST
   @Path("/domain/add/format/json")
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   Domain create(@FormParam("domainname") String name, AddDomainOptions... options);

   /**
    * Update a domain to the Glesys dns-system
    *
    * @param domain  the name of the domain to add.
    * @param options optional parameters
    * @return information about the modified domain
    */
   @Named("domain:edit")
   @POST
   @Path("/domain/edit/format/json")
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   Domain update(@FormParam("domainname") String domain, DomainOptions options);

   /**
    * Remove a domain to the Glesys dns-system
    *
    * @param domain the name of the domain to remove
    */
   @Named("domain:delete")
   @POST
   @Path("/domain/delete/format/json")
   void delete(@FormParam("domainname") String domain);

   /**
    * Retrieve the DNS records for a given domain
    *
    * @param domain the name of the domain to retrieve records for
    */
   @Named("domain:listrecords")
   @POST
   @Path("/domain/listrecords/format/json")
   @SelectJson("records")
   @Consumes(MediaType.APPLICATION_JSON)
   Set<DomainRecord> listRecords(@FormParam("domainname") String domain);

   /**
    * Add a DNS Record
    *
    * @param domain  the domain to add the record to
    * @param options optional settings for the record
    */
   @Named("domain:addrecord")
   @POST
   @Path("/domain/addrecord/format/json")
   @SelectJson("record")
   @Consumes(MediaType.APPLICATION_JSON)
   DomainRecord createRecord(@FormParam("domainname") String domain, @FormParam("host") String host,
                                               @FormParam("type") String type, @FormParam("data") String data,
                                               AddRecordOptions... options);

   /**
    * Modify a specific DNS Record
    *
    * @param recordId the id for the record to edit
    * @param options  the settings to change
    * @see #listRecords to retrieve the necessary ids
    */
   @Named("domain:updaterecord")
   @POST
   @Path("/domain/updaterecord/format/json")
   @SelectJson("record")
   @Consumes(MediaType.APPLICATION_JSON)
   DomainRecord updateRecord(@FormParam("recordid") String recordId, UpdateRecordOptions options);

   /**
    * Delete a DNS record
    *
    * @param recordId the id for the record to delete
    * @see #listRecords to retrieve the necessary ids
    */
   @Named("domain:deleterecord")
   @POST
   @Path("/domain/deleterecord/format/json")
   void deleteRecord(@FormParam("recordid") String recordId);

}
