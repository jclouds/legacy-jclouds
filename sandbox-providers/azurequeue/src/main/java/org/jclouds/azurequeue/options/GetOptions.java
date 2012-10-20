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
package org.jclouds.azurequeue.options;

import static com.google.common.base.Preconditions.checkArgument;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains common options supported in the REST API for the GET operation. <h2>
 * Usage</h2> The recommended way to instantiate a GetOptions object is to statically import
 * GetOptions.* and invoke a static creation method followed by an instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.azurequeue.options.GetOptions.Builder.*
 * import org.jclouds.azurequeue.AzureQueueClient;
 * <p/>
 * AzureQueueClient connection = // get connection
 * messages = connection.getMessages("queueName", maxMessages(3));
 * <code> *
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179474%28v=MSDN.10%29.aspx" />
 * @author Adrian Cole
 */
public class GetOptions extends BaseHttpRequestOptions {
   public static final GetOptions NONE = new GetOptions();

   /**
    * A nonzero integer value that specifies the number of messages to retrieve from the queue, up
    * to a maximum of 32. By default, a single message is retrieved from the queue with this
    * operation.
    * 
    */
   public GetOptions maxMessages(int count) {
      checkArgument(count > 0&& count <= 32, "count must be a positive number; max 32");
      queryParameters.replaceValues("numofmessages", ImmutableList.of(count + ""));
      return this;
   }

   /**
    * An integer value that specifies the message's visibility timeout in seconds. The maximum value
    * is 2 hours. The default message visibility timeout is 30 seconds.
    */
   public GetOptions visibilityTimeout(int timeout) {
      checkArgument(timeout > 0 && timeout <= 2 * 60 * 60,
               "timeout is in seconds; must be positive and maximum 2 hours");
      queryParameters.replaceValues("visibilitytimeout", ImmutableList.of(timeout + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see GetOptions#maxMessages(int)
       */
      public static GetOptions maxMessages(int count) {
         GetOptions options = new GetOptions();
         return options.maxMessages(count);
      }

      /**
       * @see GetOptions#visibilityTimeout(int)
       */
      public static GetOptions visibilityTimeout(int visibilityTimeout) {
         GetOptions options = new GetOptions();
         return options.visibilityTimeout(visibilityTimeout);
      }
   }
}
