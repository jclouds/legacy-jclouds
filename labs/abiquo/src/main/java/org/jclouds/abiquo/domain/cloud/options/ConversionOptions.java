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

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.HypervisorType;

/**
 * Available options to filter virtual machine template conversions
 */
public class ConversionOptions extends BaseHttpRequestOptions {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      ConversionOptions options = new ConversionOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder {
      private HypervisorType hypervisorType;

      private ConversionState conversionState;

      /** Only conversions compatible with this hypervisor */
      public Builder hypervisorType(final HypervisorType hypervisorType) {
         this.hypervisorType = hypervisorType;
         return this;
      }

      /** Only conversions with the provided state */
      public Builder conversionState(final ConversionState conversionState) {
         this.conversionState = conversionState;
         return this;
      }

      public ConversionOptions build() {
         ConversionOptions options = new ConversionOptions();

         if (hypervisorType != null) {
            options.queryParameters.put("hypervisor", hypervisorType.name());
         }
         if (conversionState != null) {
            options.queryParameters.put("state", conversionState.name());
         }

         return options;
      }
   }
}
