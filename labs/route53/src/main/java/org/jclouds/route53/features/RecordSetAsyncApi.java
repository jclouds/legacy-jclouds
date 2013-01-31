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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.route53.binders.BindChangeBatch;
import org.jclouds.route53.binders.BindNextRecord;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.ChangeBatch;
import org.jclouds.route53.domain.RecordSet;
import org.jclouds.route53.domain.RecordSetIterable;
import org.jclouds.route53.domain.RecordSetIterable.NextRecord;
import org.jclouds.route53.filters.RestAuthentication;
import org.jclouds.route53.functions.RecordSetIterableToPagedIterable;
import org.jclouds.route53.functions.SerializeRRS;
import org.jclouds.route53.xml.ChangeHandler;
import org.jclouds.route53.xml.ListResourceRecordSetsResponseHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see RecordSetApi
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/ActionsOnRRS.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(RestAuthentication.class)
@VirtualHost
@Path("/{jclouds.api-version}/hostedzone/{zoneId}")
public interface RecordSetAsyncApi {
   /**
    * @see RecordSetApi#create
    */
   @Named("ChangeResourceRecordSets")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/rrset")
   @Payload("<ChangeResourceRecordSetsRequest xmlns=\"https://route53.amazonaws.com/doc/2012-02-29/\"><ChangeBatch><Changes><Change><Action>CREATE</Action>{rrs}</Change></Changes></ChangeBatch></ChangeResourceRecordSetsRequest>")
   @XMLResponseParser(ChangeHandler.class)
   ListenableFuture<Change> create(@PayloadParam("rrs") @ParamParser(SerializeRRS.class) RecordSet rrs);
   
   /**
    * @see RecordSetApi#apply
    */
   @Named("ChangeResourceRecordSets")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/rrset")
   @XMLResponseParser(ChangeHandler.class)
   ListenableFuture<Change> apply(@BinderParam(BindChangeBatch.class) ChangeBatch changes);

   /**
    * @see RecordSetApi#list()
    */
   @Named("ListResourceRecordSets")
   @GET
   @Path("/rrset")
   @XMLResponseParser(ListResourceRecordSetsResponseHandler.class)
   @Transform(RecordSetIterableToPagedIterable.class)
   ListenableFuture<PagedIterable<RecordSet>> list();

   /**
    * @see RecordSetApi#listFirstPage
    */
   @Named("ListResourceRecordSets")
   @GET
   @Path("/rrset")
   @XMLResponseParser(ListResourceRecordSetsResponseHandler.class)
   ListenableFuture<RecordSetIterable> listFirstPage();

   /**
    * @see RecordSetApi#listAt(NextRecord)
    */
   @Named("ListResourceRecordSets")
   @GET
   @Path("/rrset")
   @XMLResponseParser(ListResourceRecordSetsResponseHandler.class)
   ListenableFuture<RecordSetIterable> listAt(@BinderParam(BindNextRecord.class) NextRecord nextRecord);

   /**
    * @see RecordSetApi#delete
    */
   @Named("ChangeResourceRecordSets")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/rrset")
   @Payload("<ChangeResourceRecordSetsRequest xmlns=\"https://route53.amazonaws.com/doc/2012-02-29/\"><ChangeBatch><Changes><Change><Action>DELETE</Action>{rrs}</Change></Changes></ChangeBatch></ChangeResourceRecordSetsRequest>")
   @XMLResponseParser(ChangeHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Change> delete(@PayloadParam("rrs") @ParamParser(SerializeRRS.class) RecordSet rrs);
}
