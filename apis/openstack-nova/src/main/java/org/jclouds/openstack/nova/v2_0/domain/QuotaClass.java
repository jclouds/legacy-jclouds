/*
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.nova.v2_0.domain;

import java.beans.ConstructorProperties;

/**
 * Represents the set of limits (quota class) returned by the Quota Class Extension
 * 
 * @see org.jclouds.openstack.nova.v2_0.extensions.QuotaClassClient
*/
public class QuotaClass extends Quotas {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromQuotaClass(this);
   }

   public static abstract class Builder<T extends Builder<T>> extends Quotas.Builder<T>  {
   
      public QuotaClass build() {
         return new QuotaClass(id, metadataItems, injectedFileContentBytes, volumes, gigabytes, ram, floatingIps, instances, injectedFiles, cores, securityGroups, securityGroupRules, keyPairs);
      }
      
      public T fromQuotaClass(QuotaClass in) {
         return super.fromQuotas(in);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }


   @ConstructorProperties({
      "id", "metadata_items", "injected_file_content_bytes", "volumes", "gigabytes", "ram", "floating_ips", "instances", "injected_files", "cores", "security_groups", "security_group_rules", "key_pairs"
   })
   protected QuotaClass(String id, int metadataItems, int injectedFileContentBytes, int volumes, int gigabytes, int ram, int floatingIps, int instances, int injectedFiles, int cores, int securityGroups, int securityGroupRules, int keyPairs) {
      super(id, metadataItems, injectedFileContentBytes, volumes, gigabytes, ram, floatingIps, instances, injectedFiles, cores, securityGroups, securityGroupRules, keyPairs);
   }

}
