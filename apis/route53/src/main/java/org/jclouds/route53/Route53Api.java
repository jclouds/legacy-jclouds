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
package org.jclouds.route53;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.features.HostedZoneApi;
import org.jclouds.route53.features.ResourceRecordSetApi;
import org.jclouds.route53.filters.RestAuthentication;
import org.jclouds.route53.xml.ChangeHandler;

/**
 * Provides access to Amazon Route53 via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/Route53/latest/APIReference"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(RestAuthentication.class)
@VirtualHost
@Path("/{jclouds.api-version}")
public interface Route53Api extends Closeable {

   /**
    * returns the current status of a change batch request.
    * 
    * @param changeID
    *           The ID of the change batch request.
    * @return null, if not found
    */
   @Named("GetChange")
   @GET
   @Path("/change/{changeId}")
   @XMLResponseParser(ChangeHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Change getChange(@PathParam("changeId") String changeID);

   /**
    * Provides access to Zone features.
    */
   @Delegate
   HostedZoneApi getHostedZoneApi();
   
   /**
    * Provides access to record set features.
    */
   @Delegate
   @Path("/hostedzone/{zoneId}")
   ResourceRecordSetApi getResourceRecordSetApiForHostedZone(@PathParam("zoneId") String zoneId);
}
