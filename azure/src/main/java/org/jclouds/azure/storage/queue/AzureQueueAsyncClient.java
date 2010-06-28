/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.azure.storage.queue;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.filters.SharedKeyLiteAuthentication;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.queue.binders.BindToXmlStringPayload;
import org.jclouds.azure.storage.queue.domain.QueueMessage;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.azure.storage.queue.options.GetOptions;
import org.jclouds.azure.storage.queue.options.PutMessageOptions;
import org.jclouds.azure.storage.queue.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.queue.xml.QueueMessagesListHandler;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Azure Queue via their REST API.
 * <p/>
 * The Queue service stores messages that may be read by any client who has access to the storage
 * identity.
 * <p/>
 * A queue can contain an unlimited number of messages, each of which can be up to 8 KB in size.
 * Messages are generally added to the end of the queue and retrieved from the front of the queue,
 * although first in, first out (FIFO) behavior is not guaranteed.
 * <p/>
 * If you need to store messages larger than 8 KB, you can store message data as a blob or in a
 * table, and then store a reference to the data as a message in a queue.
 * <p/>
 * All commands return a ListenableFuture of the result from Azure Queue. Any exceptions incurred
 * during processing will be wrapped in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179363%28v=MSDN.10%29.aspx" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(SharedKeyLiteAuthentication.class)
@Headers(keys = AzureStorageHeaders.VERSION, values = "2009-09-19")
public interface AzureQueueAsyncClient {

   /**
    * @see AzureQueueClient#listQueues
    */
   @GET
   @XMLResponseParser(AccountNameEnumerationResultsHandler.class)
   @Path("/")
   @QueryParams(keys = "comp", values = "list")
   ListenableFuture<? extends BoundedSet<QueueMetadata>> listQueues(ListOptions... listOptions);

   /**
    * @see AzureQueueClient#createQueue
    */
   @PUT
   @Path("{queue}")
   ListenableFuture<Boolean> createQueue(@PathParam("queue") String queue, CreateOptions... options);

   /**
    * @see AzureQueueClient#getMessages
    */
   @GET
   @Path("{queue}/messages")
   @XMLResponseParser(QueueMessagesListHandler.class)
   ListenableFuture<Set<QueueMessage>> getMessages(@PathParam("queue") String queue,
            GetOptions... options);

   /**
    * @see AzureQueueClient#deleteQueue
    */
   @DELETE
   @Path("{queue}")
   ListenableFuture<Void> deleteQueue(@PathParam("queue") String queue);

   /**
    * @see AzureQueueClient#putMessage
    */
   @POST
   @Path("{queue}/messages")
   ListenableFuture<Void> putMessage(@PathParam("queue") String queue,
            @BinderParam(BindToXmlStringPayload.class) String message, PutMessageOptions... options);

   /**
    * @see AzureQueueClient#clearMessages
    */
   @DELETE
   @Path("{queue}/messages")
   ListenableFuture<Void> clearMessages(@PathParam("queue") String queue);
}
