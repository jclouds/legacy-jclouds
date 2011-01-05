/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.sqs;

import static org.jclouds.sqs.reference.SQSParameters.ACTION;
import static org.jclouds.sqs.reference.SQSParameters.VERSION;

import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.sqs.domain.Queue;
import org.jclouds.sqs.functions.QueueLocation;
import org.jclouds.sqs.options.CreateQueueOptions;
import org.jclouds.sqs.options.ListQueuesOptions;
import org.jclouds.sqs.xml.RegexListQueuesResponseHandler;
import org.jclouds.sqs.xml.RegexMD5Handler;
import org.jclouds.sqs.xml.RegexQueueHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to SQS via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = SQSAsyncClient.VERSION)
@VirtualHost
public interface SQSAsyncClient {
   public static final String VERSION = "2009-02-01";

   /**
    * @see SQSClient#listQueuesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ListQueues")
   @ResponseParser(RegexListQueuesResponseHandler.class)
   ListenableFuture<? extends Set<Queue>> listQueuesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            ListQueuesOptions... options);

   /**
    * @see SQSClient#createQueueInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateQueue")
   @ResponseParser(RegexQueueHandler.class)
   ListenableFuture<Queue> createQueueInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("QueueName") String queueName, CreateQueueOptions... options);

   /**
    * @see SQSClient#deleteQueue
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteQueue")
   ListenableFuture<Void> deleteQueue(@EndpointParam(parser = QueueLocation.class) Queue queue);

   /**
    * @see SQSClient#sendMessage
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SendMessage")
   @ResponseParser(RegexMD5Handler.class)
   ListenableFuture<byte[]> sendMessage(@EndpointParam(parser = QueueLocation.class) Queue queue,
            @FormParam("MessageBody") String message);

}
