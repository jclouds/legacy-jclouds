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
package org.jclouds.route53.features;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.route53.domain.Zone;
import org.jclouds.route53.domain.ZoneAndNameServers;
import org.jclouds.route53.filters.RestAuthentication;
import org.jclouds.route53.functions.ZonesToPagedIterable;
import org.jclouds.route53.options.ListZonesOptions;
import org.jclouds.route53.xml.GetHostedZoneResponseHandler;
import org.jclouds.route53.xml.ListHostedZonesResponseHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see ZoneApi
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/ActionsOnHostedZones.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(RestAuthentication.class)
@VirtualHost
@Path("/{jclouds.api-version}")
public interface ZoneAsyncApi {

   /**
    * @see ZoneApi#get()
    */
   @Named("GetHostedZone")
   @GET
   @Path("{zoneId}")
   @XMLResponseParser(GetHostedZoneResponseHandler.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ZoneAndNameServers> get(@PathParam("zoneId") String zoneId);

   /**
    * @see ZoneApi#list()
    */
   @Named("ListHostedZones")
   @GET
   @Path("/hostedzone")
   @XMLResponseParser(ListHostedZonesResponseHandler.class)
   @Transform(ZonesToPagedIterable.class)
   ListenableFuture<PagedIterable<Zone>> list();

   /**
    * @see ZoneApi#list(ListZonesOptions)
    */
   @Named("ListHostedZones")
   @GET
   @Path("/hostedzone")
   @XMLResponseParser(ListHostedZonesResponseHandler.class)
   ListenableFuture<IterableWithMarker<Zone>> list(ListZonesOptions options);

}
