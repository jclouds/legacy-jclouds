/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.filters.QuerySigner;
import org.jclouds.cloudstack.functions.ReturnVoidOnNotFoundOr404OrUnableToFindAccountOwner;
import org.jclouds.cloudstack.options.AssociateIPAddressOptions;
import org.jclouds.cloudstack.options.ListPublicIPAddressesOptions;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to cloudstack via their REST API.
 * <p/>
 * 
 * @see AddressClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api/TOC_User.html" />
 * @author Adrian Cole
 */
@RequestFilters(QuerySigner.class)
@QueryParams(keys = "response", values = "json")
public interface AddressAsyncClient {

   /**
    * @see AddressClient#listPublicIPAddresses
    */
   @GET
   @QueryParams(keys = "command", values = "listPublicIpAddresses")
   @SelectJson("publicipaddress")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<PublicIPAddress>> listPublicIPAddresses(ListPublicIPAddressesOptions... options);

   /**
    * @see AddressClient#getPublicIPAddress
    */
   @GET
   @QueryParams(keys = "command", values = "listPublicIpAddresses")
   @SelectJson("publicipaddress")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<PublicIPAddress> getPublicIPAddress(@QueryParam("id") long id);

   /**
    * @see AddressClient#associateIPAddressInZone
    */
   @GET
   @QueryParams(keys = "command", values = "associateIpAddress")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<AsyncCreateResponse> associateIPAddressInZone(@QueryParam("zoneid") long zoneId,
            AssociateIPAddressOptions... options);

   /**
    * @see AddressClient#disassociateIPAddress
    */
   @GET
   @QueryParams(keys = "command", values = "disassociateIpAddress")
   @ExceptionParser(ReturnVoidOnNotFoundOr404OrUnableToFindAccountOwner.class)
   ListenableFuture<Void> disassociateIPAddress(@QueryParam("id") long id);

}
