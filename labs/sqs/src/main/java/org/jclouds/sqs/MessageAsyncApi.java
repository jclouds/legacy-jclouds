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

import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Constants;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.domain.MessageIdAndMD5;
import org.jclouds.sqs.options.ReceiveMessageOptions;
import org.jclouds.sqs.options.SendMessageOptions;
import org.jclouds.sqs.xml.MessageHandler;
import org.jclouds.sqs.xml.ReceiveMessageResponseHandler;
import org.jclouds.sqs.xml.RegexMessageIdAndMD5Handler;

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
    * @see SQSApi#delete
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteMessage")
   ListenableFuture<Void> delete(@FormParam("ReceiptHandle") String receiptHandle);

   /**
    * @see SQSApi#changeVisibility
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ChangeMessageVisibility")
   ListenableFuture<Void> changeVisibility(@FormParam("ReceiptHandle") String receiptHandle,
         @FormParam("VisibilityTimeout") int visibilityTimeout);

   /**
    * @see SQSApi#send
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessage")
   @ResponseParser(RegexMessageIdAndMD5Handler.class)
   ListenableFuture<? extends MessageIdAndMD5> send(@FormParam("MessageBody") String message);

   /**
    * @see SQSApi#send
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessage")
   @ResponseParser(RegexMessageIdAndMD5Handler.class)
   ListenableFuture<? extends MessageIdAndMD5> send(@FormParam("MessageBody") String message, SendMessageOptions options);

   /**
    * @see SQSApi#receive
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(MessageHandler.class)
   ListenableFuture<Message> receive();

   /**
    * @see SQSApi#receive
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(MessageHandler.class)
   ListenableFuture<? extends Message> receive(ReceiveMessageOptions options);

   /**
    * @see SQSApi#receive
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(ReceiveMessageResponseHandler.class)
   ListenableFuture<? extends Set<? extends Message>> receive(@FormParam("MaxNumberOfMessages") int max);

   /**
    * @see SQSApi#receive
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ReceiveMessage")
   @XMLResponseParser(ReceiveMessageResponseHandler.class)
   ListenableFuture<? extends Set<? extends Message>> receive(@FormParam("MaxNumberOfMessages") int max,
         ReceiveMessageOptions options);

}
