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

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.glesys.domain.Domain;
import org.jclouds.glesys.domain.DomainRecord;
import org.jclouds.glesys.options.DomainOptions;
import org.jclouds.glesys.options.DomainRecordAddOptions;
import org.jclouds.glesys.options.DomainRecordModifyOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Set;

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
   ListenableFuture<Set<Domain>> listDomains();

   /**
    * @see DomainClient#addDomain
    */
   @POST
   @Path("/domain/add/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> addDomain(@FormParam("name") String domain, DomainOptions... options);

   /**
    * @see DomainClient#editDomain
    */
   @POST
   @Path("/domain/add/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> editDomain(@FormParam("domain") String domain, DomainOptions... options);

   @POST
   @Path("/domain/delete/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> deleteDomain(@FormParam("domain") String domain);

   @GET
   @Path("/domain/list_records/domain/{domain}/format/json")
   @SelectJson("records")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<DomainRecord>> listRecords(@PathParam("domain") String domain);

   @POST
   @Path("/domain/add_record/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> addRecord(@FormParam("domain") String domain, @FormParam("host") String host,
                                    @FormParam("type") String type, @FormParam("data") String data,
                                    DomainRecordAddOptions... options);

   @POST
   @Path("/domain/update_record/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> editRecord(@FormParam("record_id") String record_id, DomainRecordModifyOptions... options);

   @POST
   @Path("/domain/delete_record/format/json")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Void> deleteRecord(@FormParam("record_id") String recordId);

}