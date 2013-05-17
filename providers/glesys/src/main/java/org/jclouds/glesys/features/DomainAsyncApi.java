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

import org.jclouds.Fallbacks.EmptyFluentIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
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
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Domain (DNS) data via the Glesys REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see DomainApi
 * @see <a href="https://github.com/GleSYS/API/wiki/API-Documentation" />
 */
@RequestFilters(BasicAuthentication.class)
public interface DomainAsyncApi {

   /**
    * @see org.jclouds.glesys.features.DomainApi#list
    */
   @Named("domain:list")
   @POST
   @Path("/domain/list/format/json")
   @SelectJson("domains")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyFluentIterableOnNotFoundOr404.class)
   ListenableFuture<FluentIterable<Domain>> list();

   /**
    * @see org.jclouds.glesys.features.DomainApi#get
    */
   @Named("domain:details")
   @POST
   @Path("/domain/details/format/json")
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Domain> get(@FormParam("domainname") String name);

   /**
    * @see DomainApi#create
    */
   @Named("domain:add")
   @POST
   @Path("/domain/add/format/json")
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Domain> create(@FormParam("domainname") String name, AddDomainOptions... options);

   /**
    * @see DomainApi#update
    */
   @Named("domain:edit")
   @POST
   @Path("/domain/edit/format/json")
   @SelectJson("domain")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Domain> update(@FormParam("domainname") String domain, DomainOptions options);


   /**
    * @see DomainApi#delete
    */
   @Named("domain:delete")
   @POST
   @Path("/domain/delete/format/json")
   ListenableFuture<Void> delete(@FormParam("domainname") String domain);

   /**
    * @see DomainApi#listRecords
    */
   @Named("domain:listrecords")
   @POST
   @Path("/domain/listrecords/format/json")
   @SelectJson("records")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<DomainRecord>> listRecords(@FormParam("domainname") String domain);

   /**
    * @see DomainApi#createRecord
    */
   @Named("domain:addrecord")
   @POST
   @Path("/domain/addrecord/format/json")
   @SelectJson("record")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<DomainRecord> createRecord(@FormParam("domainname") String domain, @FormParam("host") String host,
                                    @FormParam("type") String type, @FormParam("data") String data,
                                    AddRecordOptions... options);

   /**
    * @see DomainApi#updateRecord
    */
   @Named("domain:updaterecord")
   @POST
   @Path("/domain/updaterecord/format/json")
   @SelectJson("record")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<DomainRecord> updateRecord(@FormParam("recordid") String record_id, UpdateRecordOptions options);

   /**
    * @see DomainApi#deleteRecord
    */
   @Named("domain:deleterecord")
   @POST
   @Path("/domain/deleterecord/format/json")
   ListenableFuture<Void> deleteRecord(@FormParam("recordid") String recordId);

}
