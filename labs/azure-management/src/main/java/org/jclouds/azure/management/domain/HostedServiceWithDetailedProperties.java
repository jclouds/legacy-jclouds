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
package org.jclouds.azure.management.domain;

import java.net.URI;

/**
 * 
 * @author Adrian Cole
 */
public class HostedServiceWithDetailedProperties extends HostedService {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHostedServiceWithDetailedProperties(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends HostedService.Builder<T> {

      @Override
      public T properties(HostedServiceProperties properties) {
         this.properties = DetailedHostedServiceProperties.class.cast(properties);
         return self();
      }

      public HostedServiceWithDetailedProperties build() {
         return new HostedServiceWithDetailedProperties(url, name,
                  DetailedHostedServiceProperties.class.cast(properties));
      }

      public T fromHostedServiceWithDetailedProperties(HostedServiceWithDetailedProperties in) {
         return fromHostedService(in);
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected HostedServiceWithDetailedProperties(URI url, String serviceName, DetailedHostedServiceProperties properties) {
      super(url, serviceName, properties);
   }

   @Override
   public DetailedHostedServiceProperties getProperties() {
      return DetailedHostedServiceProperties.class.cast(properties);
   }

}
