/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.azure.storage.queue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.azure.storage.AzureQueue;
import org.jclouds.azure.storage.domain.BoundedSortedSet;
import org.jclouds.azure.storage.filters.SharedKeyAuthentication;
import org.jclouds.azure.storage.options.CreateOptions;
import org.jclouds.azure.storage.options.ListOptions;
import org.jclouds.azure.storage.queue.domain.QueueMetadata;
import org.jclouds.azure.storage.queue.xml.AccountNameEnumerationResultsHandler;
import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to Azure Queue via their REST API.
 * <p/>
 * The Queue service stores messages that may be read by any client who has access to the storage
 * account.
 * <p/>
 * A queue can contain an unlimited number of messages, each of which can be up to 8 KB in size.
 * Messages are generally added to the end of the queue and retrieved from the front of the queue,
 * although first in, first out (FIFO) behavior is not guaranteed.
 * <p/>
 * If you need to store messages larger than 8 KB, you can store message data as a blob or in a
 * table, and then store a reference to the data as a message in a queue.
 * <p/>
 * All commands return a Future of the result from Azure Queue. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd135733.aspx" />
 * @author Adrian Cole
 */
@SkipEncoding('/')
@RequestFilters(SharedKeyAuthentication.class)
@Headers(keys = AzureStorageHeaders.VERSION, values = "2009-07-17")
@Endpoint(AzureQueue.class)
public interface AzureQueueClient {

   /**
    * The List Queues operation returns a list of the queues under the specified account.
    * <p />
    * The 2009-07-17 version of the List Queues operation times out after 30 seconds.
    * 
    * @param listOptions
    *           controls the number or type of results requested
    * @see ListOptions
    */
   @GET
   @XMLResponseParser(AccountNameEnumerationResultsHandler.class)
   @Path("/")
   @QueryParams(keys = "comp", values = "list")
   BoundedSortedSet<QueueMetadata> listQueues(ListOptions... listOptions);

   /**
    * The Create Queue operation creates a new queue under the specified account.
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
   @PUT
   @Path("{queue}")
   @QueryParams(keys = "restype", values = "queue")
   boolean createQueue(@PathParam("queue") String queue, CreateOptions... options);

   /**
    * The Delete Queue operation permanently deletes the specified queue.
    * 
    * <p/>
    * When a queue is successfully deleted, the queue is immediately marked for deletion and is no
    * longer accessible to clients. The queue is later removed from the Queue service during garbage
    * collection.
    * 
    */
   @DELETE
   @Path("{queue}")
   @QueryParams(keys = "restype", values = "queue")
   boolean deleteQueue(@PathParam("queue") String queue);

}
