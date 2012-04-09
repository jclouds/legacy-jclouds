/**
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
package org.jclouds.elb;

import java.net.URI;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.ApiType;
import org.jclouds.apis.BaseApiMetadata;

/**
 * Implementation of {@link ApiMetadata} for Amazon's Elastic Load Balancing api.
 * 
 * @author Adrian Cole
 */
public class ELBApiMetadata extends BaseApiMetadata {

   public ELBApiMetadata() {
      this(builder()
            .id("elb")
            .type(ApiType.LOADBALANCER)
            .name("Amazon Elastic Load Balancing Api")
            .identityName("Access Key ID")
            .credentialName("Secret Access Key")
            .documentation(URI.create("http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference")));
   }

   // below are so that we can reuse builders, toString, hashCode, etc.
   // we have to set concrete classes here, as our base class cannot be
   // concrete due to serviceLoader
   protected ELBApiMetadata(ConcreteBuilder builder) {
      super(builder);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {

      @Override
      public ELBApiMetadata build() {
         return new ELBApiMetadata(this);
      }
   }

   public static ConcreteBuilder builder() {
      return new ConcreteBuilder();
   }

   @Override
   public ConcreteBuilder toBuilder() {
      return builder().fromApiMetadata(this);
   }
}