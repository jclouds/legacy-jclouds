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
package org.jclouds.sqs.features;

import static org.jclouds.sqs.reference.SQSParameters.ACTION;
import static org.jclouds.sqs.reference.SQSParameters.VERSION;

import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Constants;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.sqs.binders.BindChangeMessageVisibilityBatchRequestEntryToIndexedFormParams;
import org.jclouds.sqs.binders.BindDeleteMessageBatchRequestEntryToIndexedFormParams;
import org.jclouds.sqs.binders.BindSendMessageBatchRequestEntryToIndexedFormParams;
import org.jclouds.sqs.binders.BindSendMessageBatchRequestEntryWithDelaysToIndexedFormParams;
import org.jclouds.sqs.domain.BatchResult;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.domain.MessageIdAndMD5;
import org.jclouds.sqs.options.ReceiveMessageOptions;
import org.jclouds.sqs.options.SendMessageOptions;
import org.jclouds.sqs.xml.ChangeMessageVisibilityBatchResponseHandler;
import org.jclouds.sqs.xml.DeleteMessageBatchResponseHandler;
import org.jclouds.sqs.xml.MessageHandler;
import org.jclouds.sqs.xml.ReceiveMessageResponseHandler;
import org.jclouds.sqs.xml.RegexMessageIdAndMD5Handler;
import org.jclouds.sqs.xml.SendMessageBatchResponseHandler;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Table;
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
public interface MessageAsyncApi {

   /**
    * @see MessageApi#delete(String)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteMessage")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@FormParam("ReceiptHandle") String receiptHandle);

   /**
    * @see MessageApi#delete(Map)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteMessageBatch")
   @XMLResponseParser(DeleteMessageBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<String>> delete(
         @BinderParam(BindDeleteMessageBatchRequestEntryToIndexedFormParams.class) Map<String, String> idReceiptHandle);

   /**
    * @see MessageApi#delete(Iterable)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteMessageBatch")
   @XMLResponseParser(DeleteMessageBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<String>> delete(
         @BinderParam(BindDeleteMessageBatchRequestEntryToIndexedFormParams.class) Iterable<String> receiptHandles);

   /**
    * @see MessageApi#changeVisibility(String, int)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ChangeMessageVisibility")
   ListenableFuture<Void> changeVisibility(@FormParam("ReceiptHandle") String receiptHandle,
         @FormParam("VisibilityTimeout") int visibilityTimeout);

   /**
    * @see MessageApi#changeVisibility(Table)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ChangeMessageVisibilityBatch")
   @XMLResponseParser(ChangeMessageVisibilityBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<String>> changeVisibility(
         @BinderParam(BindChangeMessageVisibilityBatchRequestEntryToIndexedFormParams.class) Table<String, String, Integer> idReceiptHandleVisibilityTimeout);

   /**
    * @see MessageApi#changeVisibility(Map)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ChangeMessageVisibilityBatch")
   @XMLResponseParser(ChangeMessageVisibilityBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<String>> changeVisibility(
         @BinderParam(BindChangeMessageVisibilityBatchRequestEntryToIndexedFormParams.class) Map<String, Integer> receiptHandleVisibilityTimeout);

   /**
    * @see MessageApi#changeVisibility(Map, int)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ChangeMessageVisibilityBatch")
   @MapBinder(BindChangeMessageVisibilityBatchRequestEntryToIndexedFormParams.class)
   @XMLResponseParser(ChangeMessageVisibilityBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<String>> changeVisibility(
         @PayloadParam("idReceiptHandle") Map<String, String> idReceiptHandle,
         @PayloadParam("visibilityTimeout") int visibilityTimeout);

   /**
    * @see MessageApi#changeVisibility(Iterable, int)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ChangeMessageVisibilityBatch")
   @MapBinder(BindChangeMessageVisibilityBatchRequestEntryToIndexedFormParams.class)
   @XMLResponseParser(ChangeMessageVisibilityBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<String>> changeVisibility(
         @PayloadParam("receiptHandles") Iterable<String> receiptHandles,
         @PayloadParam("visibilityTimeout") int visibilityTimeout);

   /**
    * @see MessageApi#send(String)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessage")
   @ResponseParser(RegexMessageIdAndMD5Handler.class)
   ListenableFuture<? extends MessageIdAndMD5> send(@FormParam("MessageBody") String message);

   /**
    * @see MessageApi#send(String, SendMessageOptions)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessage")
   @ResponseParser(RegexMessageIdAndMD5Handler.class)
   ListenableFuture<? extends MessageIdAndMD5> send(@FormParam("MessageBody") String message, SendMessageOptions options);

   /**
    * @see MessageApi#sendWithDelays(Table)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessageBatch")
   @ResponseParser(RegexMessageIdAndMD5Handler.class)
   @XMLResponseParser(SendMessageBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<? extends MessageIdAndMD5>> sendWithDelays(
         @BinderParam(BindSendMessageBatchRequestEntryWithDelaysToIndexedFormParams.class) Table<String, String, Integer> idMessageBodyDelaySeconds);

   /**
    * @see MessageApi#sendWithDelays(Map)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessageBatch")
   @ResponseParser(RegexMessageIdAndMD5Handler.class)
   @XMLResponseParser(SendMessageBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<? extends MessageIdAndMD5>> sendWithDelays(
         @BinderParam(BindSendMessageBatchRequestEntryWithDelaysToIndexedFormParams.class) Map<String, Integer> messageBodyDelaySeconds);

   /**
    * @see MessageApi#sendWithDelay(Map, int)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessageBatch")
   @MapBinder(BindSendMessageBatchRequestEntryWithDelaysToIndexedFormParams.class)
   @XMLResponseParser(SendMessageBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<? extends MessageIdAndMD5>> sendWithDelay(
         @PayloadParam("idMessageBody") Map<String, String> idMessageBody,
         @PayloadParam("delaySeconds") int delaySeconds);

   /**
    * @see MessageApi#sendWithDelay(Iterable, int)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessageBatch")
   @MapBinder(BindSendMessageBatchRequestEntryWithDelaysToIndexedFormParams.class)
   @XMLResponseParser(SendMessageBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<? extends MessageIdAndMD5>> sendWithDelay(
         @PayloadParam("messageBodies") Iterable<String> messageBodies, @PayloadParam("delaySeconds") int delaySeconds);

   /**
    * @see MessageApi#send(Map)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessageBatch")
   @XMLResponseParser(SendMessageBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<? extends MessageIdAndMD5>> send(
         @BinderParam(BindSendMessageBatchRequestEntryToIndexedFormParams.class) Map<String, String> idMessageBody);

   /**
    * @see MessageApi#send(Iterable)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessageBatch")
   @XMLResponseParser(SendMessageBatchResponseHandler.class)
   ListenableFuture<? extends BatchResult<? extends MessageIdAndMD5>> send(
         @BinderParam(BindSendMessageBatchRequestEntryToIndexedFormParams.class) Iterable<String> messageBodies);

   /**
    * @see MessageApi#receive()
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(MessageHandler.class)
   ListenableFuture<Message> receive();

   /**
    * @see MessageApi#receive(ReceiveMessageOptions)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(MessageHandler.class)
   ListenableFuture<? extends Message> receive(ReceiveMessageOptions options);

   /**
    * @see MessageApi#receive(int)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(ReceiveMessageResponseHandler.class)
   ListenableFuture<? extends FluentIterable<? extends Message>> receive(@FormParam("MaxNumberOfMessages") int max);

   /**
    * @see MessageApi#receive(int, ReceiveMessageOptions)
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(ReceiveMessageResponseHandler.class)
   ListenableFuture<? extends FluentIterable<? extends Message>> receive(@FormParam("MaxNumberOfMessages") int max,
         ReceiveMessageOptions options);

}
