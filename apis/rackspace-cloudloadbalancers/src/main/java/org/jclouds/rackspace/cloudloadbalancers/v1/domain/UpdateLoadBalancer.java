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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseLoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseLoadBalancer.Algorithm;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Used to update Load Balancers.
 * 
 * @author Everett Toews
 */
public class UpdateLoadBalancer {
   private final String name;
   private final String protocol;
   private final Integer port;
   private final Algorithm algorithm;
   private final Integer timeout;
   private final Boolean halfClosed;

   protected UpdateLoadBalancer(@Nullable String name, @Nullable String protocol, @Nullable Integer port,
         @Nullable Algorithm algorithm, @Nullable Integer timeout, @Nullable Boolean halfClosed) {
      this.name = name;
      this.protocol = protocol;
      this.port = port;
      this.algorithm = algorithm;
      this.timeout = timeout;
      this.halfClosed = halfClosed;
   }

   public String getName() {
      return name;
   }

   public String getProtocol() {
      return protocol;
   }

   public Integer getPort() {
      return port;
   }

   public Algorithm getAlgorithm() {
      return algorithm;
   }

   public Integer getTimeout() {
      return timeout;
   }

   public Boolean isHalfClosed() {
      return halfClosed;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("name", name).add("algorithm", algorithm)
            .add("port", port).add("protocol", protocol).add("timeout", timeout).add("halfClosed", halfClosed);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, algorithm, port, protocol, timeout, halfClosed);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;

      UpdateLoadBalancer that = UpdateLoadBalancer.class.cast(obj);
      return Objects.equal(this.name, that.name) && Objects.equal(this.algorithm, that.algorithm)
            && Objects.equal(this.port, that.port) && Objects.equal(this.protocol, that.protocol)
            && Objects.equal(this.timeout, that.timeout) && Objects.equal(this.halfClosed, that.halfClosed);
   }

   public static class Builder {
      private String name;
      private String protocol;
      private Integer port;
      private Algorithm algorithm;
      private Integer timeout;
      private Boolean halfClosed;

      /**
       * @see BaseLoadBalancer.Builder#name(String)
       */
      public Builder name(String name) {
         this.name = name;
         return this;
      }

      /**
       * @see BaseLoadBalancer.Builder#protocol(String)
       */
      public Builder protocol(String protocol) {
         this.protocol = protocol;
         return this;
      }

      /**
       * @see BaseLoadBalancer.Builder#port(Integer)
       */
      public Builder port(Integer port) {
         this.port = port;
         return this;
      }

      /**
       * @see BaseLoadBalancer.Builder#algorithm(Algorithm)
       */
      public Builder algorithm(Algorithm algorithm) {
         this.algorithm = algorithm;
         return this;
      }

      /**
       * @see BaseLoadBalancer.Builder#timeout(Integer)
       */
      public Builder timeout(Integer timeout) {
         this.timeout = timeout;
         return this;
      }

      /**
       * @see BaseLoadBalancer.Builder#halfClosed(Boolean)
       */
      public Builder halfClosed(Boolean halfClosed) {
         this.halfClosed = halfClosed;
         return this;
      }

      public UpdateLoadBalancer build() {
         return new UpdateLoadBalancer(name, protocol, port, algorithm, timeout, halfClosed);
      }

      public Builder from(UpdateLoadBalancer lb) {
         return this.name(lb.getName()).port(lb.getPort()).protocol(lb.getProtocol()).algorithm(lb.getAlgorithm())
               .timeout(lb.getTimeout()).halfClosed(lb.isHalfClosed());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
