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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
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
import org.jclouds.route53.domain.ResourceRecordSet;
import org.jclouds.route53.domain.ResourceRecordSetIterable;
import org.jclouds.route53.domain.ResourceRecordSetIterable.NextRecord;
import org.jclouds.route53.filters.RestAuthentication;
import org.jclouds.route53.functions.ResourceRecordSetIterableToPagedIterable;
import org.jclouds.route53.functions.SerializeRRS;
import org.jclouds.route53.xml.ChangeHandler;
import org.jclouds.route53.xml.ListResourceRecordSetsResponseHandler;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/ActionsOnRRS.html"
 *      />
 * @author Adrian Cole
 */
@RequestFilters(RestAuthentication.class)
@VirtualHost
public interface ResourceRecordSetApi {

   /**
    * schedules creation of the resource record set.
    */
   @Named("ChangeResourceRecordSets")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/rrset")
   @Payload("<ChangeResourceRecordSetsRequest xmlns=\"https://route53.amazonaws.com/doc/2012-02-29/\"><ChangeBatch><Changes><Change><Action>CREATE</Action>{rrs}</Change></Changes></ChangeBatch></ChangeResourceRecordSetsRequest>")
   @XMLResponseParser(ChangeHandler.class)
   Change create(@PayloadParam("rrs") @ParamParser(SerializeRRS.class) ResourceRecordSet rrs);

   /**
    * applies a batch of changes atomically.
    */
   @Named("ChangeResourceRecordSets")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/rrset")
   @XMLResponseParser(ChangeHandler.class)
   Change apply(@BinderParam(BindChangeBatch.class) ChangeBatch changes);

   /**
    * returns all resource record sets in order.
    */
   @Named("ListResourceRecordSets")
   @GET
   @Path("/rrset")
   @XMLResponseParser(ListResourceRecordSetsResponseHandler.class)
   @Transform(ResourceRecordSetIterableToPagedIterable.class)
   PagedIterable<ResourceRecordSet> list();

   /**
    * retrieves up to 100 resource record sets in order.
    */
   @Named("ListResourceRecordSets")
   @GET
   @Path("/rrset")
   @XMLResponseParser(ListResourceRecordSetsResponseHandler.class)
   ResourceRecordSetIterable listFirstPage();

   /**
    * retrieves up to 100 resource record sets in order, starting at
    * {@code nextRecord}
    */
   @Named("ListResourceRecordSets")
   @GET
   @Path("/rrset")
   @XMLResponseParser(ListResourceRecordSetsResponseHandler.class)
   ResourceRecordSetIterable listAt(@BinderParam(BindNextRecord.class) NextRecord nextRecord);

   /**
    * This action deletes a resource record set.
    * 
    * @param rrs
    *           the resource record set to delete
    * @return null if not found or the change in progress
    */
   @Named("ChangeResourceRecordSets")
   @POST
   @Produces(APPLICATION_XML)
   @Path("/rrset")
   @Payload("<ChangeResourceRecordSetsRequest xmlns=\"https://route53.amazonaws.com/doc/2012-02-29/\"><ChangeBatch><Changes><Change><Action>DELETE</Action>{rrs}</Change></Changes></ChangeBatch></ChangeResourceRecordSetsRequest>")
   @XMLResponseParser(ChangeHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Change delete(@PayloadParam("rrs") @ParamParser(SerializeRRS.class) ResourceRecordSet rrs);
}
