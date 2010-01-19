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
package org.jclouds.azure.storage.queue.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the REST API for the Create Container operation. <h2>
 * Usage</h2> The recommended way to instantiate a PutMessageOptions object is to statically
 * import PutMessageOptions.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.azure.storage.queue.options.PutMessageOptions.Builder.*
 * import org.jclouds.azure.storage.queue.AzureQueueClient;
 * <p/>
 * AzureQueueClient connection = // get connection
 * connection.putMessage("containerName", withTTL());
 * <code> *
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179466.aspx" />
 * @author Adrian Cole
 */
public class PutMessageOptions extends BaseHttpRequestOptions {
   public static final PutMessageOptions NONE = new PutMessageOptions();

   /**
    * Specifies the time-to-live interval for the message, in seconds. The maximum time-to-live
    * allowed is 7 days. If this parameter is omitted, the default time-to-live is 7 days.
    */
   public PutMessageOptions withTTL(int seconds) {
      this.queryParameters.put("messagettl", seconds + "");
      return this;
   }

   public static class Builder {

      /**
       * @see PutMessageOptions#withTTL
       */
      public static PutMessageOptions withTTL(int seconds) {
         PutMessageOptions options = new PutMessageOptions();
         return options.withTTL(seconds);
      }

   }
}
