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

package org.jclouds.abiquo.domain.cloud.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Available options to query virtual appliances.
 * 
 * @author Francesc Montserrat
 * @author Ignasi Barrera
 */
public class VirtualApplianceOptions extends BaseHttpRequestOptions {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      VirtualApplianceOptions options = new VirtualApplianceOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder {

      private Boolean available;

      public Builder available(final boolean available) {
         this.available = available;
         return this;
      }

      public VirtualApplianceOptions build() {
         VirtualApplianceOptions options = new VirtualApplianceOptions();

         if (available != null) {
            options.queryParameters.put("available", String.valueOf(available));
         }

         return options;
      }
   }
}
