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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.Change.Status;
import org.jclouds.route53.domain.HostedZone;
import org.jclouds.route53.domain.HostedZoneAndNameServers;
import org.jclouds.route53.domain.NewHostedZone;
import org.jclouds.route53.filters.RestAuthentication;
import org.jclouds.route53.functions.HostedZonesToPagedIterable;
import org.jclouds.route53.xml.ChangeHandler;
import org.jclouds.route53.xml.CreateHostedZoneResponseHandler;
import org.jclouds.route53.xml.GetHostedZoneResponseHandler;
import org.jclouds.route53.xml.ListHostedZonesResponseHandler;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/ActionsOnHostedZones.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(RestAuthentication.class)
@VirtualHost
public interface HostedZoneApi {

   /**
    * This action creates a new hosted zone.
    * 
    * <h4>Note</h4>
    * 
    * You cannot create a hosted zone for a top-level domain (TLD).
    * 
    * @param name
    *           The name of the domain. ex. {@code  www.example.com.} The
    *           trailing dot is optional.
    * @param callerReference
    *           A unique string that identifies the request and allows safe
    *           retries. ex. {@code MyDNSMigration_01}
    * @return the new zone in progress, in {@link Status#PENDING}.
    */
   @Named("CreateHostedZone")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/hostedzone")
   @Payload("<CreateHostedZoneRequest xmlns=\"https://route53.amazonaws.com/doc/2012-02-29/\"><Name>{name}</Name><CallerReference>{callerReference}</CallerReference></CreateHostedZoneRequest>")
   @XMLResponseParser(CreateHostedZoneResponseHandler.class)
   NewHostedZone createWithReference(@PayloadParam("name") String name,
         @PayloadParam("callerReference") String callerReference);

   /**
    * like {@link #createWithReference(String, String)}, except you can specify
    * a comment.
    */
   @Named("CreateHostedZone")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/hostedzone")
   @Payload("<CreateHostedZoneRequest xmlns=\"https://route53.amazonaws.com/doc/2012-02-29/\"><Name>{name}</Name><CallerReference>{callerReference}</CallerReference><HostedZoneConfig><Comment>{comment}</Comment></HostedZoneConfig></CreateHostedZoneRequest>")
   @XMLResponseParser(CreateHostedZoneResponseHandler.class)
   NewHostedZone createWithReferenceAndComment(@PayloadParam("name") String name,
         @PayloadParam("callerReference") String callerReference, @PayloadParam("comment") String comment);

   /**
    * returns all zones in order.
    */
   @Named("ListHostedZones")
   @GET
   @Path("/hostedzone")
   @XMLResponseParser(ListHostedZonesResponseHandler.class)
   @Transform(HostedZonesToPagedIterable.class)
   PagedIterable<HostedZone> list();

   /**
    * retrieves up to 100 zones in order.
    */
   @Named("ListHostedZones")
   @GET
   @Path("/hostedzone")
   @XMLResponseParser(ListHostedZonesResponseHandler.class)
   IterableWithMarker<HostedZone> listFirstPage();

   /**
    * retrieves up to 100 zones in order, starting at {@code nextMarker}
    */
   @Named("ListHostedZones")
   @GET
   @Path("/hostedzone")
   @XMLResponseParser(ListHostedZonesResponseHandler.class)
   IterableWithMarker<HostedZone> listAt(@QueryParam("marker") String nextMarker);

   /**
    * Retrieves information about the specified zone, including its nameserver
    * configuration
    * 
    * @param id
    *           id of the zone to get information about. ex
    *           {@code Z1PA6795UKMFR9}
    * @return null if not found
    */
   @Named("GetHostedZone")
   @GET
   @Path("/hostedzone/{zoneId}")
   @XMLResponseParser(GetHostedZoneResponseHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   HostedZoneAndNameServers get(@PathParam("zoneId") String zoneId);

   /**
    * This action deletes a hosted zone.
    * 
    * @param id
    *           id of the zone to delete. ex {@code Z1PA6795UKMFR9}
    * @return null if not found or the change in progress
    */
   @Named("DeleteHostedZone")
   @DELETE
   @Path("/hostedzone/{zoneId}")
   @XMLResponseParser(ChangeHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Change delete(@PathParam("zoneId") String zoneId);
}
