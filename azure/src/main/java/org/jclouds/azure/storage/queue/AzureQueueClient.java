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

package org.jclouds.azure.storage.queue;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.jclouds.azure.storage.domain.BoundedSet;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.queue.domain.QueueMessage;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.azure.storage.queue.options.GetOptions;
import org.jclouds.azure.storage.queue.options.PutMessageOptions;
import org.jclouds.concurrent.Timeout;
import org.jclouds.http.HttpResponseException;

import java.util.concurrent.Future;

/**
 * Provides access to Azure Queue via their REST API.
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
 * All commands return a Future of the result from Azure Queue. Any exceptions incurred
 * during processing will be wrapped in an {@link ExecutionException} as documented in
 * {@link Future#get()}.
 * 
 * @see AzureQueueAsyncClient
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135733.aspx" />
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface AzureQueueClient {

   /**
    * The List Queues operation returns a list of the queues under the specified identity.
    * <p />
    * The 2009-07-17 version of the List Queues operation times out after 30 seconds.
    * 
    * @param listOptions
    *           controls the number or type of results requested
    * @see ListOptions
    */
   BoundedSet<QueueMetadata> listQueues(ListOptions... listOptions);

   /**
    * The Create Queue operation creates a new queue under the specified identity.
    * <p/>
    * You can specify user-defined metadata as name-value pairs on the queue at the time that it is
    * created.
    * <p/>
    * When a queue with the specified name already exists, the Queue service checks the metadata
    * associated with the existing queue. If the existing metadata is identical to the metadata
    * specified on the Create Queue request, status code 204 (No Content) is returned.
    * <p/>
    * If the existing metadata does not match the metadata provided with the Create Queue request,
    * the operation fails and status code 409 (Conflict) is returned. Clients can take advantage of
    * this behavior to avoid an additional call to check whether a named queue already exists.
    * 
    * @see CreateQueueOptions
    * 
    */
   boolean createQueue(String queue, CreateOptions... options);

   /**
    * The Delete Queue operation permanently deletes the specified queue.
    * 
    * <p/>
    * When a queue is successfully deleted, the queue is immediately marked for deletion and is no
    * longer accessible to clients. The queue is later removed from the Queue service during garbage
    * collection.
    * 
    */
   void deleteQueue(String queue);

   /**
    * The Put Message operation adds a new message to the back of the message queue. A message may
    * be up to 8 KB in size and must be in a format that can be included in an XML request with
    * UTF-8 encoding.
    * 
    * <p/>
    * 
    * The message time-to-live specifies how long a message will remain in the queue, from the time
    * it is added to the time it is retrieved and deleted. If a message is not retrieved before the
    * time-to-live interval expires, the message is removed from the queue.
    * 
    * @throws HttpResponseException
    *            If the message is too large, the service returns status code 400 (Bad Request).
    * 
    */
   void putMessage(String queue, String message, PutMessageOptions... options);

   /**
    * The Clear Messages operation deletes all messages from the specified queue.
    * 
    * <p/>
    * If a queue contains a large number of messages, Clear Messages may time out before all
    * messages have been deleted. In this case the Queue service will return status code 500
    * (Internal Server Error), with the additional error code OperationTimedOut. If the operation
    * times out, the client should continue to retry Clear Messages until it succeeds, to ensure
    * that all messages have been deleted.
    */
   @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
   void clearMessages(String queue);

   /**
    * The Get Messages operation retrieves one or more messages from the front of the queue.
    * 
    * @param queue
    *           the name of the queue to retrieve messages from
    * @param options
    *           controls the number of messages to receive and the visibility window
    */
   Set<QueueMessage> getMessages(String queue, GetOptions... options);

}
