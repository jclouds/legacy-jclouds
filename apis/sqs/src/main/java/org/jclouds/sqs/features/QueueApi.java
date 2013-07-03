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

/**
 * Provides access to SQS via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "{" + Constants.PROPERTY_API_VERSION + "}")
@VirtualHost
public interface QueueApi {

   /**
    * The ListQueues action returns a list of your queues. The maximum number of
    * queues that can be returned is 1000. If you specify a value for the
    * optional QueueNamePrefix parameter, only queues with a name beginning with
    * the specified value are returned
    * 
    * @param region
    *           Queues are Region-specific.
    * @param options
    *           specify prefix or other options
    * 
    * @see <a href=
    *      "http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Query_QueryListQueues.html"
    *      />
    */
   @Named("ListQueues")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ListQueues")
   @ResponseParser(RegexListQueuesResponseHandler.class)
   FluentIterable<URI> list();

   @Named("ListQueues")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "ListQueues")
   @ResponseParser(RegexListQueuesResponseHandler.class)
   FluentIterable<URI> list(ListQueuesOptions options);

   /**
    * The GetQueueUrl action returns the Uniform Resource Locater (URL) of a
    * queue. This action provides a simple way to retrieve the URL of an SQS
    * queue.
    * 
    * @param queueName
    *           The name of an existing queue.
    * @return uri of the queue or null if not found
    */
   @Named("GetQueueUrl")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueUrl")
   @ResponseParser(RegexQueueHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   URI get(@FormParam("QueueName") String queueName);

   /**
    * like {@link #get(String)}, except specifying the owner of the queue.
    * 
    * To access a queue that belongs to another AWS account, use the
    * QueueOwnerAWSAccountId parameter to specify the account ID of the queue's
    * owner. The queue's owner must grant you permission to access the queue.
    * 
    * @param accountId
    * @return The AWS account ID of the account that created the queue.
    */
   @Named("GetQueueUrl")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueUrl")
   @ResponseParser(RegexQueueHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   URI getInAccount(@FormParam("QueueName") String queueName,
         @FormParam("QueueOwnerAWSAccountId") String accountId);

   /**
    * The CreateQueue action creates a new queue.
    * 
    * When you request CreateQueue, you provide a name for the queue. To
    * successfully create a new queue, you must provide a name that is unique
    * within the scope of your own queues.
    * 
    * <h4>Note</h4>
    * 
    * This method will gracefully retry in case the queue name was recently taken.
    * 
    * If you provide the name of an existing queue, along with the exact names
    * and values of all the queue's attributes, CreateQueue returns the queue
    * URL for the existing queue. If the queue name, attribute names, or
    * attribute values do not match an existing queue, CreateQueue returns an
    * error.
    * 
    * <h4>Tip</h4>
    * 
    * Use GetQueueUrl to get a queue's URL. GetQueueUrl requires only the
    * QueueName parameter.
    * 
    * @param region
    *           Queues are Region-specific.
    * @param queueName
    *           The name to use for the queue created. Constraints: Maximum 80
    *           characters; alphanumeric characters, hyphens (-), and
    *           underscores (_) are allowed.
    */
   @Named("CreateQueue")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateQueue")
   @ResponseParser(RegexQueueHandler.class)
   URI create(@FormParam("QueueName") String queueName);

   /**
    * same as {@link #create(String, String)} except you can
    * control options such as delay seconds.
    * 
    * @param options
    *           options such as delay seconds
    * @see #create(String, String)
    */
   @Named("CreateQueue")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "CreateQueue")
   @ResponseParser(RegexQueueHandler.class)
   URI create(@FormParam("QueueName") String queueName, CreateQueueOptions options);

   /**
    * The DeleteQueue action deletes the queue specified by the queue URL,
    * regardless of whether the queue is empty. If the specified queue does not
    * exist, SQS returns a successful response.
    * 
    * <h4>Caution</h4>
    * 
    * Use DeleteQueue with care; once you delete your queue, any messages in the
    * queue are no longer available.
    * 
    * When you delete a queue, the deletion process takes up to 60 seconds.
    * Requests you send involving that queue during the 60 seconds might
    * succeed. For example, a SendMessage request might succeed, but after the
    * 60 seconds, the queue and that message you sent no longer exist. Also,
    * when you delete a queue, you must wait at least 60 seconds before creating
    * a queue with the same name.
    * 
    * We reserve the right to delete queues that have had no activity for more
    * than 30 days. For more information, see About SQS Queues in the Amazon SQS
    * Developer Guide.
    * 
    * @param queue
    *           queue you want to delete
    */
   @Named("DeleteQueue")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteQueue")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@EndpointParam URI queue);

   /**
    * returns all attributes of a queue.
    * 
    * @param queue
    *           queue to get the attributes of
    */
   @Named("GetQueueAttributes")
   @POST
   @Path("/")
   @FormParams(keys = { ACTION, "AttributeName.1" }, values = { "GetQueueAttributes", "All" })
   @Transform(MapToQueueAttributes.class)
   @Fallback(NullOnNotFoundOr404.class)
   @XMLResponseParser(AttributesHandler.class)
   QueueAttributes getAttributes(@EndpointParam URI queue);

   /**
    * returns an attribute of a queue.
    * 
    * @param queue
    *           queue to get the attributes of
    */
   @Named("GetQueueAttributes")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueAttributes")
   @XMLResponseParser(AttributesHandler.class)
   Map<String, String> getAttributes(@EndpointParam URI queue,
         @BinderParam(BindAttributeNamesToIndexedFormParams.class) Iterable<String> attributeNames);

   /**
    * returns an attribute of a queue.
    * 
    * @param queue
    *           queue to get the attributes of
    */
   @Named("GetQueueAttributes")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "GetQueueAttributes")
   @XMLResponseParser(ValueHandler.class)
   String getAttribute(@EndpointParam URI queue, @FormParam("AttributeName.1") String attributeName);

   /**
    * The SetQueueAttributes action sets one attribute of a queue per request.
    * When you change a queue's attributes, the change can take up to 60 seconds
    * to propagate throughout the SQS system.
    * 
    * @param queue
    *           queue to set the attribute on
    * @param name
    * 
    *           The name of the attribute you want to set.
    * 
    *           VisibilityTimeout - The length of time (in seconds) that a
    *           message received from a queue will be invisible to other
    *           receiving components when they ask to receive messages. For more
    *           information about VisibilityTimeout, see Visibility Timeout in
    *           the Amazon SQS Developer Guide.
    * 
    *           Policy - The formal description of the permissions for a
    *           resource. For more information about Policy, see Basic Policy
    *           Structure in the Amazon SQS Developer Guide.
    * 
    *           MaximumMessageSize - The limit of how many bytes a message can
    *           contain before Amazon SQS rejects it.
    * 
    *           MessageRetentionPeriod - The number of seconds Amazon SQS
    *           retains a message.
    * 
    *           DelaySeconds - The time in seconds that the delivery of all
    *           messages in the queue will be delayed.
    * @param value
    *           The value of the attribute you want to set. To delete a queue's
    *           access control policy, set the policy to "".
    * 
    *           Constraints: Constraints are specific for each value.
    * 
    *           VisibilityTimeout - An integer from 0 to 43200 (12 hours). The
    *           default for this attribute is 30 seconds.
    * 
    *           Policy - A valid form-url-encoded policy. For more information
    *           about policy structure, see Basic Policy Structure in the Amazon
    *           SQS Developer Guide. For more information about
    *           form-url-encoding, see
    *           http://www.w3.org/MarkUp/html-spec/html-spec_8.html#SEC8.2.1.
    * 
    *           MaximumMessageSize - An integer from 1024 bytes (1 KiB) up to
    *           65536 bytes (64 KiB). The default for this attribute is 65536
    *           (64 KiB).
    * 
    *           MessageRetentionPeriod - Integer representing seconds, from 60
    *           (1 minute) to 1209600 (14 days). The default for this attribute
    *           is 345600 (4 days).
    * 
    *           DelaySeconds - An integer from 0 to 900 (15 minutes). The
    *           default for this attribute is 0.
    */
   @Named("SetQueueAttributes")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "SetQueueAttributes")
   void setAttribute(@EndpointParam URI queue, @FormParam("Attribute.Name") String name,
         @FormParam("Attribute.Value") String value);

}
