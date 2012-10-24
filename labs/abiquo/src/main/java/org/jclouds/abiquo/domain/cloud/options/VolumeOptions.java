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

import org.jclouds.abiquo.domain.options.search.FilterOptions.BaseFilterOptionsBuilder;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Available options to query volumes.
 * 
 * @author Ignasi Barrera
 */
@EnterpriseEdition
public class VolumeOptions extends BaseHttpRequestOptions {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      VolumeOptions options = new VolumeOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder extends BaseFilterOptionsBuilder<Builder> {
      private Boolean onlyAvailable;

      public Builder onlyAvailable(final boolean onlyAvailable) {
         this.onlyAvailable = onlyAvailable;
         return this;
      }

      public VolumeOptions build() {
         VolumeOptions options = new VolumeOptions();

         if (onlyAvailable != null) {
            options.queryParameters.put("available", String.valueOf(onlyAvailable));
         }

         return addFilterOptions(options);
      }
   }
}
