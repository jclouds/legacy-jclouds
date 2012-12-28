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
package org.jclouds.snia.cdmi.v1.features;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.snia.cdmi.v1.ObjectTypes;
import org.jclouds.snia.cdmi.v1.binders.BindQueryParmsToSuffix;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.filters.BasicAuthenticationAndTenantId;
import org.jclouds.snia.cdmi.v1.filters.StripExtraAcceptHeader;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectOptions;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Data Object Resource Operations
 * 
 * @see DataApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
@RequestFilters({ BasicAuthenticationAndTenantId.class, StripExtraAcceptHeader.class })
@Headers(keys = "X-CDMI-Specification-Version", values = "{jclouds.api-version}")
public interface DataAsyncApi {
   /**
    * @see DataApi#get(String dataObjectName)
    */
   @GET
   @Consumes({ ObjectTypes.DATAOBJECT, MediaType.APPLICATION_JSON })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   ListenableFuture<DataObject> get(@PathParam("dataObjectName") String dataObjectName);

   /**
    * @see DataApi#get(String dataObjectName, DataObjectQueryParams queryParams)
    */
   @GET
   @Consumes({ ObjectTypes.DATAOBJECT, MediaType.APPLICATION_JSON })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   ListenableFuture<DataObject> get(@PathParam("dataObjectName") String dataObjectName,
            @BinderParam(BindQueryParmsToSuffix.class) DataObjectQueryParams queryParams);

   /**
    * @see DataApi#create(String dataObjectName, CreateDataObjectOptions... options)
    */
   @PUT
   @Consumes({ ObjectTypes.DATAOBJECT, MediaType.APPLICATION_JSON })
   @Produces({ ObjectTypes.DATAOBJECT })
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   ListenableFuture<DataObject> create(@PathParam("dataObjectName") String dataObjectName,
            CreateDataObjectOptions... options);

   /**
    * @see DataApi#delete(String dataObjectName)
    */
   @DELETE
   @Consumes(MediaType.TEXT_PLAIN)
   // note: MediaType.APPLICATION_JSON work also, however without consumes
   // jclouds throws null exception
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/{dataObjectName}")
   ListenableFuture<Void> delete(@PathParam("dataObjectName") String dataObjectName);

}
