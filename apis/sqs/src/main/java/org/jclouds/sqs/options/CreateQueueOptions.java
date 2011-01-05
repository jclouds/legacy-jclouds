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

package org.jclouds.sqs.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the Form API for the CreateQueue operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateQueueOptions object is to statically import
 * CreateQueueOptions.Builder.* and invoke a static creation method followed by an instance mutator
 * (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.sqs.options.CreateQueueOptions.Builder.*
 * <p/>
 * SQSClient connection = // get connection
 * Queue queue = connection.createQueueInRegion(defaultVisibilityTimeout("foo"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryCreateQueue.html"
 *      />
 */
public class CreateQueueOptions extends BaseHttpRequestOptions {

   /**
    * A default value for the queue's visibility timeout (30 seconds) is set when the queue is
    * created. You can override this value with the DefaultVisibilityTimeout request parameter. For
    * more information, see Visibility Timeout in the Amazon SQS Developer Guide.
    * 
    * @param seconds
    *           The visibility timeout (in seconds) to use for this queue. 0 to 43200 (maximum 12
    *           hours); Default: 30 seconds
    */
   public CreateQueueOptions defaultVisibilityTimeout(int seconds) {
      //TODO validate
      formParameters.put("DefaultVisibilityTimeout", seconds+"");
      return this;
   }

   public String getRestorableBy() {
      return getFirstFormOrNull("DefaultVisibilityTimeout");
   }

   public static class Builder {

      /**
       * @see CreateQueueOptions#defaultVisibilityTimeout(int )
       */
      public static CreateQueueOptions defaultVisibilityTimeout(int seconds) {
         CreateQueueOptions options = new CreateQueueOptions();
         return options.defaultVisibilityTimeout(seconds);
      }

   }
}
