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
package org.jclouds.gogrid.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.gogrid.reference.GoGridQueryParams.DESCRIPTION_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IS_SANDBOX_KEY;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Oleksiy Yarmula
 */
public class AddServerOptions extends BaseHttpRequestOptions {

   public AddServerOptions withDescription(String description) {
      checkArgument(description.length() <= 500, "Description cannot be longer than 500 characters");
      checkState(!queryParameters.containsKey(DESCRIPTION_KEY),
               "Can't have duplicate server description");
      queryParameters.put(DESCRIPTION_KEY, description);
      return this;
   }

   /**
    * Make server a sandbox instance. By default, it's not.
    * 
    * @return itself for convenience
    */
   public AddServerOptions asSandboxType() {
      checkState(!queryParameters.containsKey(IS_SANDBOX_KEY),
               "Can only have one sandbox option per server");
      queryParameters.put(IS_SANDBOX_KEY, "true");
      return this;
   }

   public static class Builder {
      /**
       * @see AddServerOptions#withDescription(String)
       */
      public static AddServerOptions withDescription(String description) {
         AddServerOptions options = new AddServerOptions();
         return options.withDescription(description);
      }

      /**
       * @see AddServerOptions#asSandboxType()
       */
      public static AddServerOptions asSandboxType() {
         AddServerOptions options = new AddServerOptions();
         return options.asSandboxType();
      }
   }
}
