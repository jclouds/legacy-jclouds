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
package org.jclouds.glesys.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.glesys.options.AddDomainOptions;
import org.jclouds.glesys.options.AddRecordOptions;
import org.jclouds.glesys.options.DomainOptions;
import org.jclouds.glesys.options.EditRecordOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Domain (DNS) data via the Glesys REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see DomainClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@RequestFilters(BasicAuthentication.class)
public interface DomainAsyncClient {

   /**
    * @see org.jclouds.glesys.features.DomainClient#listDomains
    */
   @POST
   @Path("/domain/list/format/json")
   @SelectJson("domains")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Domain>> listDomains();

   /**
    * @see org.jclouds.glesys.features.DomainClient#getDomain
    */
   @POST
   @Path("/domain/details/format/json")
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Domain> getDomain(@FormParam("domainname") String name);

   /**
    * @see DomainClient#addDomain
    */
   @POST
   @Path("/domain/add/format/json")
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Domain> addDomain(@FormParam("domainname") String name, AddDomainOptions... options);

   /**
    * @see DomainClient#editDomain
    */
   @POST
   @Path("/domain/edit/format/json")
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Domain> editDomain(@FormParam("domainname") String domain, DomainOptions... options);


   /**
    * @see DomainClient#deleteDomain
    */
   @POST
   @Path("/domain/delete/format/json")
   ListenableFuture<Void> deleteDomain(@FormParam("domainname") String domain);

   /**
    * @see DomainClient#listRecords
    */
   @POST
   @Path("/domain/listrecords/format/json")
   @SelectJson("records")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<DomainRecord>> listRecords(@FormParam("domainname") String domain);

   /**
    * @see DomainClient#addRecord
    */
   @POST
   @Path("/domain/addrecord/format/json")
   @SelectJson("record")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<DomainRecord> addRecord(@FormParam("domainname") String domain, @FormParam("host") String host,
                                    @FormParam("type") String type, @FormParam("data") String data,
                                    AddRecordOptions... options);

   /**
    * @see DomainClient#editRecord
    */
   @POST
   @Path("/domain/updaterecord/format/json")
   @SelectJson("record")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<DomainRecord> editRecord(@FormParam("recordid") String record_id, EditRecordOptions... options);

   /**
    * @see DomainClient#deleteRecord
    */
   @POST
   @Path("/domain/deleterecord/format/json")
   ListenableFuture<Void> deleteRecord(@FormParam("recordid") String recordId);

}