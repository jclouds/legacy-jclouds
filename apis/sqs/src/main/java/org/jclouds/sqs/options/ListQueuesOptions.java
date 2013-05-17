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
package org.jclouds.sqs.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the Form API for the ListQueues operation. <h2>
 * Usage</h2> The recommended way to instantiate a ListQueuesOptions object is to statically import
 * ListQueuesOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.sqs.options.ListQueuesOptions.Builder.*
 * <p/>
 * SQSApi connection = // get connection
 * Set<Queue> queues = connection.listQueuesInRegion(queuePrefix("foo"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryListQueues.html"
 *      />
 */
public class ListQueuesOptions extends BaseHttpRequestOptions {

   /**
    * String to use for filtering the list results. Only those queues whose name begins with the
    * specified string are returned.
    * 
    * @param prefix
    *           Maximum 80 characters; alphanumeric characters, hyphens (-), and underscores (_) are
    *           allowed.
    */
   public ListQueuesOptions queuePrefix(String prefix) {
      formParameters.put("QueueNamePrefix", prefix);
      return this;
   }

   public static class Builder {

      /**
       * @see ListQueuesOptions#queuePrefix(String )
       */
      public static ListQueuesOptions queuePrefix(String prefix) {
         ListQueuesOptions options = new ListQueuesOptions();
         return options.queuePrefix(prefix);
      }

   }
}
