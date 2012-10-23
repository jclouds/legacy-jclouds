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

import org.jclouds.abiquo.domain.config.Category;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.StatefulInclusion;

/**
 * Available options to query virtual machine templates.
 * 
 * @author Ignasi Barrera
 */
public class VirtualMachineTemplateOptions extends BaseHttpRequestOptions {
   public static Builder builder() {
      return new Builder();
   }

   @Override
   protected Object clone() throws CloneNotSupportedException {
      VirtualMachineTemplateOptions options = new VirtualMachineTemplateOptions();
      options.queryParameters.putAll(queryParameters);
      return options;
   }

   public static class Builder {
      private StatefulInclusion persistent;

      private HypervisorType hypervisorType;

      private Category category;

      private String categoryName;

      private Integer idTemplate;

      public Builder persistent(final StatefulInclusion persistent) {
         this.persistent = persistent;
         return this;
      }

      public Builder hypervisorType(final HypervisorType hypervisorType) {
         this.hypervisorType = hypervisorType;
         return this;
      }

      public Builder category(final Category category) {
         this.category = category;
         return this;
      }

      public Builder categoryName(final String categoryName) {
         this.categoryName = categoryName;
         return this;
      }

      public Builder idTemplate(final Integer idTemplate) {
         this.idTemplate = idTemplate;
         return this;
      }

      public VirtualMachineTemplateOptions build() {
         VirtualMachineTemplateOptions options = new VirtualMachineTemplateOptions();

         if (persistent != null) {
            options.queryParameters.put("stateful", persistent.name());
         }
         if (hypervisorType != null) {
            options.queryParameters.put("hypervisorTypeName", hypervisorType.name());
         }
         if (category != null) {
            options.queryParameters.put("categoryName", category.getName());
         }

         if (category == null && categoryName != null) {
            options.queryParameters.put("categoryName", categoryName);
         }

         if (idTemplate != null) {
            options.queryParameters.put("idTemplate", String.valueOf(idTemplate));
         }

         return options;
      }
   }
}
