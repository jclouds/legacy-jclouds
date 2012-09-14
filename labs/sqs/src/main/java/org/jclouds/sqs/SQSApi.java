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

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.domain.MessageIdAndMD5;
import org.jclouds.sqs.options.CreateQueueOptions;
import org.jclouds.sqs.options.ListQueuesOptions;
import org.jclouds.sqs.options.ReceiveMessageOptions;
import org.jclouds.sqs.options.SendMessageOptions;

/**
 * Provides access to SQS via their REST API.
 * <p/>
 * 
 * @see SQSAsyncApi
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface SQSApi {

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
   Set<URI> listQueuesInRegion(@Nullable String region);

   Set<URI> listQueuesInRegion(@Nullable String region, ListQueuesOptions options);

   /**
    * The CreateQueue action creates a new queue.
    * 
    * When you request CreateQueue, you provide a name for the queue. To
    * successfully create a new queue, you must provide a name that is unique
    * within the scope of your own queues.
    * 
    * <h4>Note</h4>
    * 
    * If you delete a queue, you must wait at least 60 seconds before creating a
    * queue with the same name.
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
   // this will gracefully attempt to resolve name issues
   @Timeout(duration = 61, timeUnit = TimeUnit.SECONDS)
   URI createQueueInRegion(@Nullable String region, String queueName);

   /**
    * same as {@link #createQueueInRegion(String, String)} except you can
    * control options such as delay seconds.
    * 
    * @param options
    *           options such as delay seconds
    * @see #createQueueInRegion(String, String)
    */
   @Timeout(duration = 61, timeUnit = TimeUnit.SECONDS)
   URI createQueueInRegion(@Nullable String region, String queueName, CreateQueueOptions options);

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
   void deleteQueue(URI queue);

   /**
    * The DeleteMessage action deletes the specified message from the specified
    * queue. You specify the message by using the message's receipt handle and
    * not the message ID you received when you sent the message. Even if the
    * message is locked by another reader due to the visibility timeout setting,
    * it is still deleted from the queue. If you leave a message in the queue
    * for more than 4 days, SQS automatically deletes it.
    * 
    * <h4>Note</h4>
    * 
    * The receipt handle is associated with a specific instance of receiving the
    * message. If you receive a message more than once, the receipt handle you
    * get each time you receive the message is different. When you request
    * DeleteMessage, if you don't provide the most recently received receipt
    * handle for the message, the request will still succeed, but the message
    * might not be deleted.
    * 
    * <h4>Important</h4>
    * 
    * It is possible you will receive a message even after you have deleted it.
    * This might happen on rare occasions if one of the servers storing a copy
    * of the message is unavailable when you request to delete the message. The
    * copy remains on the server and might be returned to you again on a
    * subsequent receive request. You should create your system to be idempotent
    * so that receiving a particular message more than once is not a problem.
    * 
    * @param queue
    *           the queue the message is in
    * @param receiptHandle
    *           The receipt handle associated with the message you want to
    *           delete.
    */
   void deleteMessage(URI queue, String receiptHandle);

   /**
    * The ChangeMessageVisibility action changes the visibility timeout of a
    * specified message in a queue to a new value. The maximum allowed timeout
    * value you can set the value to is 12 hours. This means you can't extend
    * the timeout of a message in an existing queue to more than a total
    * visibility timeout of 12 hours. (For more information visibility timeout,
    * see Visibility Timeout in the Amazon SQS Developer Guide.)
    * 
    * For example, let's say the timeout for the queue is 30 seconds, and you
    * receive a message. Once you're 20 seconds into the timeout for that
    * message (i.e., you have 10 seconds left), you extend it by 60 seconds by
    * calling ChangeMessageVisibility with VisibilityTimeoutset to 60 seconds.
    * You have then changed the remaining visibility timeout from 10 seconds to
    * 60 seconds.
    * 
    * <h4>Important</h4>
    * 
    * If you attempt to set the VisibilityTimeout to an amount more than the
    * maximum time left, Amazon SQS returns an error. It will not automatically
    * recalculate and increase the timeout to the maximum time remaining.
    * 
    * <h4>Important</h4>
    * 
    * Unlike with a queue, when you change the visibility timeout for a specific
    * message, that timeout value is applied immediately but is not saved in
    * memory for that message. If you don't delete a message after it is
    * received, the visibility timeout for the message the next time it is
    * received reverts to the original timeout value, not the value you set with
    * the ChangeMessageVisibility action.
    * 
    * @param queue
    *           the queue the message is in
    * @param receiptHandle
    *           The receipt handle associated with the message whose visibility
    *           timeout you want to change. This parameter is returned by the
    *           ReceiveMessage action.
    * @param visibilityTimeout
    *           The new value for the message's visibility timeout (in seconds)
    *           from 0 to 43200 (maximum 12 hours)
    */
   void changeMessageVisibility(URI queue, String receiptHandle, int visibilityTimeout);

   /**
    * The SendMessage action delivers a message to the specified queue. The
    * maximum allowed message size is 64 KB.
    * 
    * <h4>Important</h4>
    * 
    * The following list shows the characters (in Unicode) allowed in your
    * message, according to the W3C XML specification (for more information, go
    * to http://www.w3.org/TR/REC-xml/#charsets). If you send any characters not
    * included in the list, your request will be rejected.
    * 
    * 
    * {@code #x9 | #xA | #xD | [#x20 to #xD7FF] | [#xE000 to #xFFFD] | [#x10000 to #x10FFFF]}
    * 
    * @param queue
    *           queue you want to send to
    * 
    * @param message
    *           Type: String maximum 64 KB in size. For a list of allowed
    *           characters, see the preceding important note.
    * @return id of the message and md5 of the content sent
    */
   MessageIdAndMD5 sendMessage(URI queue, String message);

   /**
    * same as {@link #sendMessage(URI, String)} except you can control options
    * such as delay seconds.
    * 
    * @param options
    *           options such as delay seconds
    * @see #sendMessage(URI, String)
    */
   MessageIdAndMD5 sendMessage(URI queue, String message, SendMessageOptions options);

   /**
    * The ReceiveMessage action retrieves one or more messages from the
    * specified queue. The ReceiveMessage action does not delete the message
    * after it is retrieved. To delete a message, you must use the DeleteMessage
    * action. For more information about message deletion in the message life
    * cycle, see Message Lifecycle.
    * 
    * <h4>Note</h4>
    * 
    * Due to the distributed nature of the queue, a weighted random set of
    * machines is sampled on a ReceiveMessage call. That means only the messages
    * on the sampled machines are returned. If the number of messages in the
    * queue is small (less than 1000), it is likely you will get fewer messages
    * than you requested per ReceiveMessage call. If the number of messages in
    * the queue is extremely small, you might not receive any messages in a
    * particular ReceiveMessage response; in which case you should repeat the
    * request.
    * 
    * @param queue
    *           from where you are receiving messages
    * @return message including the receipt handle you can use to delete it
    */
   Message receiveMessage(URI queue);

   /**
    * same as {@link #receiveMessage(URI)} except you can provide options like
    * VisibilityTimeout parameter in your request, which will be applied to the
    * messages that SQS returns in the response. If you do not include the
    * parameter, the overall visibility timeout for the queue is used for the
    * returned messages.
    * 
    * @param options
    *           options such as VisibilityTimeout
    * @see #receiveMessage(URI)
    */
   Message receiveMessage(URI queue, ReceiveMessageOptions options);

   /**
    * same as {@link #receiveMessage(URI)} except you can receive multiple
    * messages.
    * 
    * @param max
    *           maximum messages to receive, current limit is 10
    * @see #receiveMessage(URI)
    */
   Set<Message> receiveMessages(URI queue, int max);

   /**
    * returns all attributes of a queue.
    * 
    * @param queue
    *           queue to get the attributes of
    */
   Map<String, String> getQueueAttributes(URI queue);

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
   void setQueueAttribute(URI queue, String name, String value);

   /**
    * returns some attributes of a queue.
    * 
    * @param queue
    *           queue to get the attributes of
    */
   Map<String, String> getQueueAttributes(URI queue, Iterable<String> attributeNames);

   /**
    * same as {@link #receiveMessages(URI, int)} except you can provide options
    * like VisibilityTimeout parameter in your request, which will be applied to
    * the messages that SQS returns in the response. If you do not include the
    * parameter, the overall visibility timeout for the queue is used for the
    * returned messages.
    * 
    * @param options
    *           options such as VisibilityTimeout
    * @see #receiveMessages(URI, int)
    */
   Set<Message> receiveMessages(URI queue, int max, ReceiveMessageOptions options);
}
