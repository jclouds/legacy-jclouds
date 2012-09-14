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
package org.jclouds.sqs;

import static org.jclouds.sqs.reference.SQSParameters.ACTION;
import static org.jclouds.sqs.reference.SQSParameters.VERSION;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Constants;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.sqs.binders.BindAttributeNamesToIndexedFormParams;
import org.jclouds.sqs.domain.Action;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.domain.MessageIdAndMD5;
import org.jclouds.sqs.domain.QueueAttributes;
import org.jclouds.sqs.functions.MapToQueueAttributes;
import org.jclouds.sqs.options.CreateQueueOptions;
import org.jclouds.sqs.options.ListQueuesOptions;
import org.jclouds.sqs.options.ReceiveMessageOptions;
import org.jclouds.sqs.options.SendMessageOptions;
import org.jclouds.sqs.xml.AttributesHandler;
import org.jclouds.sqs.xml.MessageHandler;
import org.jclouds.sqs.xml.ReceiveMessageResponseHandler;
import org.jclouds.sqs.xml.RegexListQueuesResponseHandler;
import org.jclouds.sqs.xml.RegexMessageIdAndMD5Handler;
import org.jclouds.sqs.xml.RegexQueueHandler;
import org.jclouds.sqs.xml.ValueHandler;

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
public interface SQSAsyncApi {

   /**
    * @see SQSApi#listQueuesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ListQueues")
   @ResponseParser(RegexListQueuesResponseHandler.class)
   ListenableFuture<Set<URI>> listQueuesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region);

   /**
    * @see SQSApi#listQueuesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ListQueues")
   @ResponseParser(RegexListQueuesResponseHandler.class)
   ListenableFuture<Set<URI>> listQueuesInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         ListQueuesOptions options);

   /**
    * @see SQSApi#createQueueInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateQueue")
   @ResponseParser(RegexQueueHandler.class)
   ListenableFuture<URI> createQueueInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("QueueName") String queueName);

   /**
    * @see SQSApi#createQueueInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateQueue")
   @ResponseParser(RegexQueueHandler.class)
   ListenableFuture<URI> createQueueInRegion(
         @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
         @FormParam("QueueName") String queueName, CreateQueueOptions options);

   /**
    * @see SQSApi#deleteQueue
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteQueue")
   ListenableFuture<Void> deleteQueue(@EndpointParam URI queue);

   /**
    * @see SQSApi#deleteMessage
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteMessage")
   ListenableFuture<Void> deleteMessage(@EndpointParam URI queue, @FormParam("ReceiptHandle") String receiptHandle);

   /**
    * @see SQSApi#changeMessageVisibility
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ChangeMessageVisibility")
   ListenableFuture<Void> changeMessageVisibility(@EndpointParam URI queue,
         @FormParam("ReceiptHandle") String receiptHandle, @FormParam("VisibilityTimeout") int visibilityTimeout);

   /**
    * @see SQSApi#sendMessage
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessage")
   @ResponseParser(RegexMessageIdAndMD5Handler.class)
   ListenableFuture<? extends MessageIdAndMD5> sendMessage(@EndpointParam URI queue,
         @FormParam("MessageBody") String message);

   /**
    * @see SQSApi#sendMessage
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessage")
   @ResponseParser(RegexMessageIdAndMD5Handler.class)
   ListenableFuture<? extends MessageIdAndMD5> sendMessage(@EndpointParam URI queue,
         @FormParam("MessageBody") String message, SendMessageOptions options);

   /**
    * @see SQSApi#receiveMessage
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(MessageHandler.class)
   ListenableFuture<Message> receiveMessage(@EndpointParam URI queue);

   /**
    * @see SQSApi#receiveMessage
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(MessageHandler.class)
   ListenableFuture<? extends Message> receiveMessage(@EndpointParam URI queue, ReceiveMessageOptions options);

   /**
    * @see SQSApi#getQueueAttributes(URI)
    */
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "AttributeName.1" }, values = { "GetQueueAttributes", "All" })
   @Transform(MapToQueueAttributes.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @XMLResponseParser(AttributesHandler.class)
   ListenableFuture<? extends QueueAttributes> getQueueAttributes(@EndpointParam URI queue);

   /**
    * @see SQSApi#getQueueAttributes(URI, Iterable)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueAttributes")
   @XMLResponseParser(AttributesHandler.class)
   ListenableFuture<Map<String, String>> getQueueAttributes(@EndpointParam URI queue,
         @BinderParam(BindAttributeNamesToIndexedFormParams.class) Iterable<String> attributeNames);

   /**
    * @see SQSApi#getQueueAttribute
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueAttributes")
   @XMLResponseParser(ValueHandler.class)
   ListenableFuture<String> getQueueAttribute(@EndpointParam URI queue,
         @FormParam("AttributeName.1") String attributeName);

   /**
    * @see SQSApi#setQueueAttribute
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SetQueueAttributes")
   ListenableFuture<Void> setQueueAttribute(@EndpointParam URI queue, @FormParam("Attribute.Name") String name,
         @FormParam("Attribute.Value") String value);

   /**
    * @see SQSApi#receiveMessages
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(ReceiveMessageResponseHandler.class)
   ListenableFuture<? extends Set<? extends Message>> receiveMessages(@EndpointParam URI queue,
         @FormParam("MaxNumberOfMessages") int max);

   /**
    * @see SQSApi#receiveMessages
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(ReceiveMessageResponseHandler.class)
   ListenableFuture<? extends Set<? extends Message>> receiveMessages(@EndpointParam URI queue,
         @FormParam("MaxNumberOfMessages") int max, ReceiveMessageOptions options);

   /**
    * @see SQSApi#addPermissionToAccount
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "AddPermission")
   ListenableFuture<Void> addPermissionToAccount(@EndpointParam URI queue, @FormParam("Label") String label,
         @FormParam("ActionName.1") Action permission, @FormParam("AWSAccountId.1") String accountId);

   /**
    * @see SQSApi#removePermission
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RemovePermission")
   ListenableFuture<Void> removePermission(@EndpointParam URI queue, @FormParam("Label") String label);

}
