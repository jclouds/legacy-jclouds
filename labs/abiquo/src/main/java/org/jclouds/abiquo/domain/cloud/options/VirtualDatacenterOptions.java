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
 * Available options to query virtual datacenters.
 * 
 * @author Francesc Montserrat
 */
public class VirtualDatacenterOptions extends BaseHttpRequestOptions {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      VirtualDatacenterOptions options = new VirtualDatacenterOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder {
      private Integer datacenterId;

      private Integer enterpriseId;

      /**
       * Set the optional datacenter.
       */
      public Builder datacenterId(final int datacenterId) {
         this.datacenterId = datacenterId;
         return this;
      }

      /**
       * Set the optional enterprise.
       */
      public Builder enterpriseId(final int enterpriseId) {
         this.enterpriseId = enterpriseId;
         return this;
      }

      public VirtualDatacenterOptions build() {
         VirtualDatacenterOptions options = new VirtualDatacenterOptions();

         if (datacenterId != null) {
            options.queryParameters.put("datacenter", datacenterId.toString());
         }

         if (enterpriseId != null) {
            options.queryParameters.put("enterprise", enterpriseId.toString());
         }
         return options;
      }
   }
}
