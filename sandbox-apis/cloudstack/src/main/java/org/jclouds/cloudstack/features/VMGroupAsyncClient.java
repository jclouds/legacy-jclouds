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
package org.jclouds.cloudstack.features;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.cloudstack.domain.VMGroup;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.options.CreateVMGroupOptions;
import org.jclouds.cloudstack.options.ListVMGroupsOptions;
import org.jclouds.cloudstack.options.UpdateVMGroupOptions;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Provides asynchronous access to CloudStack VM group features.
 * <p/>
 *
 * @author Richard Downer
 * @see VMGroupClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface VMGroupAsyncClient {
   /**
    * Lists VM groups
    *
    * @param options if present, how to constrain the list.
    * @return VM groups matching query, or empty set, if no zones are found
    */
   @GET
   @QueryParams(keys = "command", values = "listInstanceGroups")
   @SelectJson("instancegroup")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<VMGroup>> listInstanceGroups(ListVMGroupsOptions... options);

   /**
    * @see VMGroupClient#getInstanceGroup
    */
   @GET
   @QueryParams(keys = "command", values = "listInstanceGroups")
   @SelectJson("instancegroup")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<VMGroup> getInstanceGroup(@QueryParam("id") long id);

   /**
    * Creates a VM group
    *
    * @param name    the name of the VM group
    * @param options optional parameters
    * @return the new VMGroup
    */
   @GET
   @QueryParams(keys = "command", values = "createInstanceGroup")
   @SelectJson("instancegroup")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<VMGroup> createInstanceGroup(@QueryParam("name") String name, CreateVMGroupOptions... options);

   /**
    * Modify a VM group
    *
    * @param name the new name of the group
    * @return the modified VMGroup
    */
   @GET
   @QueryParams(keys = "command", values = "updateInstanceGroup")
   @SelectJson("instancegroup")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<VMGroup> updateInstanceGroup(@QueryParam("id") long id, UpdateVMGroupOptions... options);

   /**
    * Delete a VM group
    *
    * @param id the ID of the VM group
    * @return a future with a void data type
    */
   @GET
   @QueryParams(keys = "command", values = "deleteInstanceGroup")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteInstanceGroup(@QueryParam("id") long id);
}
