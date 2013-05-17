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
package org.jclouds.sts.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Objects;
import com.google.common.collect.Multimap;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/STS/latest/APIReference/API_GetFederationToken.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class FederatedUserOptions extends BaseHttpRequestOptions implements Cloneable {

   // long as this is a more typical unit for duration, hence less casting
   private Long durationSeconds;
   private String policy;

   /**
    * The duration, in seconds, that the credentials should remain valid. 12
    * hours is default. 15 minutes is current minimum.
    */
   public FederatedUserOptions durationSeconds(long durationSeconds) {
      this.durationSeconds = durationSeconds;
      return this;
   }

   /**
    * A supplemental policy that can be associated with the temporary security
    * credentials.
    */
   public FederatedUserOptions policy(String policy) {
      this.policy = policy;
      return this;
   }

   public static class Builder {

      /**
       * @see FederatedUserOptions#durationSeconds
       */
      public static FederatedUserOptions durationSeconds(long durationSeconds) {
         return new FederatedUserOptions().durationSeconds(durationSeconds);
      }

      /**
       * @see FederatedUserOptions#policy
       */
      public static FederatedUserOptions policy(String policy) {
         return new FederatedUserOptions().policy(policy);
      }
   }

   @Override
   public Multimap<String, String> buildFormParameters() {
      Multimap<String, String> params = super.buildFormParameters();
      if (durationSeconds != null)
         params.put("DurationSeconds", durationSeconds.toString());
      if (policy != null)
         params.put("Policy", policy);
      return params;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(durationSeconds, policy);
   }

   @Override
   public FederatedUserOptions clone() {
      return new FederatedUserOptions().durationSeconds(durationSeconds).policy(policy);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      FederatedUserOptions other = FederatedUserOptions.class.cast(obj);
      return Objects.equal(this.durationSeconds, other.durationSeconds) && Objects.equal(this.policy, other.policy);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("durationSeconds", durationSeconds)
            .add("policy", policy).toString();
   }
}
