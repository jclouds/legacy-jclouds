/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.config.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Available options to query icons.
 * 
 * @author Francesc Montserrat
 */
public class IconOptions extends BaseHttpRequestOptions {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      IconOptions options = new IconOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder {
      private String path;

      public Builder path(final String path) {
         this.path = path;
         return this;
      }

      public IconOptions build() {
         IconOptions options = new IconOptions();
         if (path != null) {
            options.queryParameters.put("path", path);
         }

         return options;
      }
   }
}
