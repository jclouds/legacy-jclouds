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
package org.jclouds.aws.sqs;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.sqs.domain.Queue;
import org.jclouds.aws.sqs.options.CreateQueueOptions;
import org.jclouds.aws.sqs.options.ListQueuesOptions;
import org.jclouds.concurrent.Timeout;

/**
 * Provides access to SQS via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface SQSClient {

   /**
    * The ListQueues action returns a list of your queues. The maximum number of queues that can be
    * returned is 1000. If you specify a value for the optional QueueNamePrefix parameter, only
    * queues with a name beginning with the specified value are returned
    * 
    * @param region
    *           Queues are Region-specific.
    * @param options
    *           specify prefix or other options
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSSimpleQueueService/2009-02-01/APIReference/Query_QueryListQueues.html"
    *      />
    */
   Set<Queue> listQueuesInRegion(@Nullable Region region, ListQueuesOptions... options);

   /**
    * 
    * The CreateQueue action creates a new queue.
    * <p/>
    * When you request CreateQueue, you provide a name for the queue. To successfully create a new
    * queue, you must provide a name that is unique within the scope of your own queues. If you
    * provide the name of an existing queue, a new queue isn't created and an error isn't returned.
    * Instead, the request succeeds and the queue URL for the existing queue is returned (for more
    * information about queue URLs, see Queue and Message Identifiers in the Amazon SQS Developer
    * Guide). Exception: if you provide a value for DefaultVisibilityTimeout that is different from
    * the value for the existing queue, you receive an error.
    * <h3>Note</h3>
    * 
    * If you delete a queue, you must wait at least 60 seconds before creating a queue with the same
    * name.
    * <p/>
    * A default value for the queue's visibility timeout (30 seconds) is set when the queue is
    * created. You can override this value with the DefaultVisibilityTimeout request parameter. For
    * more information, see Visibility Timeout in the Amazon SQS Developer Guide.
    * 
    * @param region
    *           Queues are Region-specific.
    * @param queueName
    *           The name to use for the queue created. Constraints: Maximum 80 characters;
    *           alphanumeric characters, hyphens (-), and underscores (_) are allowed.
    * @param options
    *           like the visibility timeout (in seconds) to use for this queue.
    */
   Queue createQueueInRegion(@Nullable Region region, String queueName, CreateQueueOptions... options);

   /**
    * The DeleteQueue action deletes the queue specified by the queue URL, regardless of whether the
    * queue is empty. If the specified queue does not exist, SQS returns a successful response. <h3>
    * Caution</h3>
    * 
    * Use DeleteQueue with care; once you delete your queue, any messages in the queue are no longer
    * available.
    * <p/>
    * When you delete a queue, the deletion process takes up to 60 seconds. Requests you send
    * involving that queue during the 60 seconds might succeed. For example, a SendMessage request
    * might succeed, but after the 60 seconds, the queue and that message you sent no longer exist.
    * Also, when you delete a queue, you must wait at least 60 seconds before creating a queue with
    * the same name.
    * <p/>
    * We reserve the right to delete queues that have had no activity for more than 30 days. For
    * more information, see About SQS Queues in the Amazon SQS Developer Guide.
    * 
    * @param queue
    *           queue you want to delete
    */
   void deleteQueue(Queue queue);

   /**
    * The SendMessage action delivers a message to the specified queue. The maximum allowed message
    * size is 8 KB.
    * <p/>
    * Important
    * <p/>
    * The following list shows the characters (in Unicode) allowed in your message, according to the
    * W3C XML specification (for more information, go to http://www.w3.org/TR/REC-xml/#charsets). If
    * you send any characters not included in the list, your request will be rejected.
    * <p/>
    * #x9 | #xA | #xD | [#x20 to #xD7FF] | [#xE000 to #xFFFD] | [#x10000 to #x10FFFF]
    * 
    * @param queue
    *           queue you want to send to
    * 
    * @param message
    *           The message to send. Type: String maximum 8 KB in size. For a list of allowed
    *           characters, see the preceding important note
    * @return md5 of the content sent
    */
   byte[] sendMessage(Queue queue, String message);
}
