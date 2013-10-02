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

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * @author Adrian Cole
 */
public class SaveImageOptions extends BaseHttpRequestOptions {

   public SaveImageOptions withDescription(String description) {
      checkArgument(description.length() <= 500, "Description cannot be longer than 500 characters");
      checkState(!queryParameters.containsKey(DESCRIPTION_KEY), "Can't have duplicate image description");
      queryParameters.put(DESCRIPTION_KEY, description);
      return this;
   }

   public static class Builder {
      /**
       * @see SaveImageOptions#withDescription(String)
       */
      public static SaveImageOptions withDescription(String description) {
         SaveImageOptions options = new SaveImageOptions();
         return options.withDescription(description);
      }
   }
}
