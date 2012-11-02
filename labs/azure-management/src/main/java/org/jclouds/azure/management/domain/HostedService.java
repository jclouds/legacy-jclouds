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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import com.google.common.base.CaseFormat;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * System properties for the specified hosted service
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >api</a>
 * @author Adrian Cole
 */
public class HostedService {
   public static enum Status {

      CREATING,

      CREATED,

      DELETING,

      DELETED,

      CHANGING,

      RESOLVING_DNS,

      UNRECOGNIZED;

      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }
   }

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromHostedService(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected URI url;
      protected String name;
      protected HostedServiceProperties properties;

      /**
       * @see HostedService#getUrl()
       */
      public T url(URI url) {
         this.url = url;
         return self();
      }

      /**
       * @see HostedService#getName()
       */
      public T name(String name) {
         this.name = name;
         return self();
      }

      /**
       * @see HostedService#getProperties()
       */
      public T properties(HostedServiceProperties properties) {
         this.properties = properties;
         return self();
      }

      public HostedService build() {
         return new HostedService(url, name, properties);
      }

      public T fromHostedService(HostedService in) {
         return this.url(in.getUrl()).name(in.getName()).properties(in.getProperties());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final URI url;
   protected final String name;
   protected final HostedServiceProperties properties;

   protected HostedService(URI url, String name, HostedServiceProperties properties) {
      this.url = checkNotNull(url, "url");
      this.name = checkNotNull(name, "name");
      this.properties = checkNotNull(properties, "properties");
   }

   /**
    * The Service Management API request URI used to perform Get Hosted Service Properties requests
    * against the hosted service.
    */
   public URI getUrl() {
      return url;
   }

   /**
    * The name of the hosted service. This name is the DNS prefix name and can be used to access the
    * hosted service.
    * 
    * For example, if the service name is MyService you could access the access the service by
    * calling: http://MyService.cloudapp.net
    */
   public String getName() {
      return name;
   }

   /**
    * Provides the url of the database properties to be used for this DB HostedService.
    */
   public HostedServiceProperties getProperties() {
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(url);
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
      HostedService other = (HostedService) obj;
      return Objects.equal(this.url, other.url);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues().add("url", url).add("name", name)
               .add("properties", properties);
   }

}
