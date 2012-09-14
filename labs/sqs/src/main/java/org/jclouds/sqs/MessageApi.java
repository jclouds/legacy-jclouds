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
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.domain.MessageIdAndMD5;
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
public interface MessageApi {

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
   void delete(String receiptHandle);

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
   void changeVisibility(String receiptHandle, int visibilityTimeout);

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
   MessageIdAndMD5 send(String message);

   /**
    * same as {@link #sendMessage(URI, String)} except you can control options
    * such as delay seconds.
    * 
    * @param options
    *           options such as delay seconds
    * @see #sendMessage(URI, String)
    */
   MessageIdAndMD5 send(String message, SendMessageOptions options);

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
   Message receive();

   /**
    * same as {@link #receive(URI)} except you can provide options like
    * VisibilityTimeout parameter in your request, which will be applied to the
    * messages that SQS returns in the response. If you do not include the
    * parameter, the overall visibility timeout for the queue is used for the
    * returned messages.
    * 
    * @param options
    *           options such as VisibilityTimeout
    * @see #receive(URI)
    */
   Message receive(ReceiveMessageOptions options);

   /**
    * same as {@link #receive(URI)} except you can receive multiple messages.
    * 
    * @param max
    *           maximum messages to receive, current limit is 10
    * @see #receive(URI)
    */
   Set<Message> receive(int max);

   /**
    * same as {@link #receive(URI, int)} except you can provide options like
    * VisibilityTimeout parameter in your request, which will be applied to the
    * messages that SQS returns in the response. If you do not include the
    * parameter, the overall visibility timeout for the queue is used for the
    * returned messages.
    * 
    * @param options
    *           options such as VisibilityTimeout
    * @see #receive(URI, int)
    */
   Set<Message> receive(int max, ReceiveMessageOptions options);
}
