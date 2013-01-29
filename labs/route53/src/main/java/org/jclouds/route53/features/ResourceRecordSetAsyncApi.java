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

import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.route53.binders.BindNextRecord;
import org.jclouds.route53.domain.ResourceRecordSet;
import org.jclouds.route53.domain.ResourceRecordSetIterable;
import org.jclouds.route53.domain.ResourceRecordSetIterable.NextRecord;
import org.jclouds.route53.filters.RestAuthentication;
import org.jclouds.route53.functions.ResourceRecordSetsToPagedIterable;
import org.jclouds.route53.xml.ListResourceRecordSetsResponseHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see ResourceRecordSetApi
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/ActionsOnRRS.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(RestAuthentication.class)
@VirtualHost
@Path("/{jclouds.api-version}/hostedzone/{zoneId}")
public interface ResourceRecordSetAsyncApi {

   /**
    * @see ResourceRecordSetApi#list()
    */
   @Named("ListResourceRecordSets")
   @GET
   @Path("/rrset")
   @XMLResponseParser(ListResourceRecordSetsResponseHandler.class)
   @Transform(ResourceRecordSetsToPagedIterable.class)
   ListenableFuture<PagedIterable<ResourceRecordSet>> list();

   /**
    * @see ResourceRecordSetApi#listFirstPage
    */
   @Named("ListResourceRecordSets")
   @GET
   @Path("/rrset")
   @XMLResponseParser(ListResourceRecordSetsResponseHandler.class)
   ListenableFuture<ResourceRecordSetIterable> listFirstPage();

   /**
    * @see ResourceRecordSetApi#listAt(NextRecord)
    */
   @Named("ListResourceRecordSets")
   @GET
   @Path("/rrset")
   @XMLResponseParser(ListResourceRecordSetsResponseHandler.class)
   ListenableFuture<ResourceRecordSetIterable> listAt(@BinderParam(BindNextRecord.class) NextRecord nextRecord);
}
