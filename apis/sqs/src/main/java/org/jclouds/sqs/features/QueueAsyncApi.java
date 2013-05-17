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
package org.jclouds.sqs.features;

import static org.jclouds.sqs.reference.SQSParameters.ACTION;
import static org.jclouds.sqs.reference.SQSParameters.VERSION;

import java.net.URI;
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Constants;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.sqs.binders.BindAttributeNamesToIndexedFormParams;
import org.jclouds.sqs.domain.QueueAttributes;
import org.jclouds.sqs.functions.MapToQueueAttributes;
import org.jclouds.sqs.options.CreateQueueOptions;
import org.jclouds.sqs.options.ListQueuesOptions;
import org.jclouds.sqs.xml.AttributesHandler;
import org.jclouds.sqs.xml.RegexListQueuesResponseHandler;
import org.jclouds.sqs.xml.RegexQueueHandler;
import org.jclouds.sqs.xml.ValueHandler;

import com.google.common.collect.FluentIterable;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to SQS via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "{" + Constants.PROPERTY_API_VERSION + "}")
@VirtualHost
public interface QueueAsyncApi {

   /**
    * @see QueueApi#list
    */
   @Named("ListQueues")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ListQueues")
   @ResponseParser(RegexListQueuesResponseHandler.class)
   ListenableFuture<FluentIterable<URI>> list();

   /**
    * @see QueueApi#list(ListQueuesOptions)
    */
   @Named("ListQueues")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ListQueues")
   @ResponseParser(RegexListQueuesResponseHandler.class)
   ListenableFuture<FluentIterable<URI>> list(ListQueuesOptions options);

   /**
    * @see QueueApi#get(String)
    */
   @Named("GetQueueUrl")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueUrl")
   @ResponseParser(RegexQueueHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<URI> get(@FormParam("QueueName") String queueName);

   /**
    * @see QueueApi#getInAccount
    */
   @Named("GetQueueUrl")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueUrl")
   @ResponseParser(RegexQueueHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<URI> getInAccount(@FormParam("QueueName") String queueName,
         @FormParam("QueueOwnerAWSAccountId") String accountId);

   /**
    * @see QueueApi#create
    */
   @Named("CreateQueue")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateQueue")
   @ResponseParser(RegexQueueHandler.class)
   ListenableFuture<URI> create(@FormParam("QueueName") String queueName);

   /**
    * @see QueueApi#create
    */
   @Named("CreateQueue")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateQueue")
   @ResponseParser(RegexQueueHandler.class)
   ListenableFuture<URI> create(@FormParam("QueueName") String queueName, CreateQueueOptions options);

   /**
    * @see QueueApi#delete
    */
   @Named("DeleteQueue")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteQueue")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@EndpointParam URI queue);

   /**
    * @see QueueApi#getAttributes(URI)
    */
   @Named("GetQueueAttributes")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "AttributeName.1" }, values = { "GetQueueAttributes", "All" })
   @Transform(MapToQueueAttributes.class)
   @Fallback(NullOnNotFoundOr404.class)
   @XMLResponseParser(AttributesHandler.class)
   ListenableFuture<? extends QueueAttributes> getAttributes(@EndpointParam URI queue);

   /**
    * @see QueueApi#getAttributes(URI, Iterable)
    */
   @Named("GetQueueAttributes")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueAttributes")
   @XMLResponseParser(AttributesHandler.class)
   ListenableFuture<Map<String, String>> getAttributes(@EndpointParam URI queue,
         @BinderParam(BindAttributeNamesToIndexedFormParams.class) Iterable<String> attributeNames);

   /**
    * @see QueueApi#getAttribute
    */
   @Named("GetQueueAttributes")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueAttributes")
   @XMLResponseParser(ValueHandler.class)
   ListenableFuture<String> getAttribute(@EndpointParam URI queue, @FormParam("AttributeName.1") String attributeName);

   /**
    * @see QueueApi#setAttribute
    */
   @Named("SetQueueAttributes")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SetQueueAttributes")
   ListenableFuture<Void> setAttribute(@EndpointParam URI queue, @FormParam("Attribute.Name") String name,
         @FormParam("Attribute.Value") String value);

}
