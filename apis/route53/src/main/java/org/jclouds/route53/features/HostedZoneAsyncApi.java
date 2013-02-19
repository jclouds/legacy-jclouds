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

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import javax.inject.Named;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.HostedZone;
import org.jclouds.route53.domain.HostedZoneAndNameServers;
import org.jclouds.route53.domain.NewHostedZone;
import org.jclouds.route53.filters.RestAuthentication;
import org.jclouds.route53.functions.HostedZonesToPagedIterable;
import org.jclouds.route53.xml.ChangeHandler;
import org.jclouds.route53.xml.CreateHostedZoneResponseHandler;
import org.jclouds.route53.xml.GetHostedZoneResponseHandler;
import org.jclouds.route53.xml.ListHostedZonesResponseHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see HostedZoneApi
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/ActionsOnHostedZones.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(RestAuthentication.class)
@VirtualHost
@Path("/{jclouds.api-version}")
public interface HostedZoneAsyncApi {
   /**
    * @see HostedZoneApi#createWithReference
    */
   @Named("CreateHostedZone")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/hostedzone")
   @Payload("<CreateHostedZoneRequest xmlns=\"https://route53.amazonaws.com/doc/2012-02-29/\"><Name>{name}</Name><CallerReference>{callerReference}</CallerReference></CreateHostedZoneRequest>")
   @XMLResponseParser(CreateHostedZoneResponseHandler.class)
   ListenableFuture<NewHostedZone> createWithReference(@PayloadParam("name") String name,
         @PayloadParam("callerReference") String callerReference);

   /**
    * @see HostedZoneApi#createWithReferenceAndComment
    */
   @Named("CreateHostedZone")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/hostedzone")
   @Payload("<CreateHostedZoneRequest xmlns=\"https://route53.amazonaws.com/doc/2012-02-29/\"><Name>{name}</Name><CallerReference>{callerReference}</CallerReference><HostedZoneConfig><Comment>{comment}</Comment></HostedZoneConfig></CreateHostedZoneRequest>")
   @XMLResponseParser(CreateHostedZoneResponseHandler.class)
   ListenableFuture<NewHostedZone> createWithReferenceAndComment(@PayloadParam("name") String name,
         @PayloadParam("callerReference") String callerReference, @PayloadParam("comment") String comment);

   /**
    * @see HostedZoneApi#list()
    */
   @Named("ListHostedZones")
   @GET
   @Path("/hostedzone")
   @XMLResponseParser(ListHostedZonesResponseHandler.class)
   @Transform(HostedZonesToPagedIterable.class)
   ListenableFuture<PagedIterable<HostedZone>> list();

   /**
    * @see HostedZoneApi#listFirstPage
    */
   @Named("ListHostedZones")
   @GET
   @Path("/hostedzone")
   @XMLResponseParser(ListHostedZonesResponseHandler.class)
   ListenableFuture<IterableWithMarker<HostedZone>> listFirstPage();

   /**
    * @see HostedZoneApi#listAt(String)
    */
   @Named("ListHostedZones")
   @GET
   @Path("/hostedzone")
   @XMLResponseParser(ListHostedZonesResponseHandler.class)
   ListenableFuture<IterableWithMarker<HostedZone>> listAt(@QueryParam("marker") String nextMarker);

   /**
    * @see HostedZoneApi#get()
    */
   @Named("GetHostedZone")
   @GET
   @Path("/hostedzone/{zoneId}")
   @XMLResponseParser(GetHostedZoneResponseHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<HostedZoneAndNameServers> get(@PathParam("zoneId") String zoneId);

   /**
    * @see HostedZoneApi#delete()
    */
   @Named("DeleteHostedZone")
   @DELETE
   @Path("/hostedzone/{zoneId}")
   @XMLResponseParser(ChangeHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Change> delete(@PathParam("zoneId") String zoneId);
}
