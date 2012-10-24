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

package org.jclouds.abiquo.domain.infrastructure.options;

import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Available options to query storage pools.
 * 
 * @author Francesc Montserrat
 */

@EnterpriseEdition
public class StoragePoolOptions extends BaseHttpRequestOptions {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      StoragePoolOptions options = new StoragePoolOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder {
      private Boolean sync;

      /**
       * Set the optional sync param.
       */
      public Builder sync(final boolean sync) {
         this.sync = sync;
         return this;
      }

      public StoragePoolOptions build() {
         StoragePoolOptions options = new StoragePoolOptions();
         if (sync != null) {
            options.queryParameters.put("sync", sync.toString());
         }

         return options;
      }
   }
}
