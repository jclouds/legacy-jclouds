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

import java.util.Date;
import java.util.Map;

import org.jclouds.azure.management.domain.HostedService.Status;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
public class DetailedHostedServiceProperties extends HostedServiceProperties {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromDetailedHostedServiceProperties(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends HostedServiceProperties.Builder<T> {

      protected String rawStatus;
      protected Status status;
      protected Date created;
      protected Date lastModified;
      protected ImmutableMap.Builder<String, String> extendedProperties = ImmutableMap.<String, String> builder();

      /**
       * @see DetailedHostedServiceProperties#getRawStatus()
       */
      public T rawStatus(String rawStatus) {
         this.rawStatus = rawStatus;
         return self();
      }

      /**
       * @see DetailedHostedServiceProperties#getStatus()
       */
      public T status(Status status) {
         this.status = status;
         return self();
      }

      /**
       * @see DetailedHostedServiceProperties#getCreated()
       */
      public T created(Date created) {
         this.created = created;
         return self();
      }

      /**
       * @see DetailedHostedServiceProperties#getLastModified()
       */
      public T lastModified(Date lastModified) {
         this.lastModified = lastModified;
         return self();
      }

      /**
       * @see DetailedHostedServiceProperties#getExtendedProperties()
       */
      public T extendedProperties(Map<String, String> extendedProperties) {
         this.extendedProperties.putAll(checkNotNull(extendedProperties, "extendedProperties"));
         return self();
      }

      /**
       * @see DetailedHostedServiceProperties#getExtendedProperties()
       */
      public T addExtendedProperty(String name, String value) {
         this.extendedProperties.put(checkNotNull(name, "name"), checkNotNull(value, "value"));
         return self();
      }

      public DetailedHostedServiceProperties build() {
         return new DetailedHostedServiceProperties(description, location, affinityGroup, label, rawStatus, status,
                  created, lastModified, extendedProperties.build());
      }

      public T fromDetailedHostedServiceProperties(DetailedHostedServiceProperties in) {
         return fromHostedServiceProperties(in).rawStatus(in.getRawStatus()).status(in.getStatus())
                  .created(in.getCreated()).lastModified(in.getLastModified())
                  .extendedProperties(in.getExtendedProperties());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final String rawStatus;
   protected final Status status;
   protected final Date created;
   protected final Date lastModified;
   protected final Map<String, String> extendedProperties;

   protected DetailedHostedServiceProperties(Optional<String> description, Optional<String> location,
            Optional<String> affinityGroup, String label, String rawStatus, Status status, Date created,
            Date lastModified, Map<String, String> extendedProperties) {
      super(description, location, affinityGroup, label);
      this.rawStatus = checkNotNull(rawStatus, "rawStatus of %s", description);
      this.status = checkNotNull(status, "status of %s", description);
      this.created = checkNotNull(created, "created of %s", description);
      this.lastModified = checkNotNull(lastModified, "lastModified of %s", description);
      this.extendedProperties = ImmutableMap.copyOf(checkNotNull(extendedProperties, "extendedProperties of %s",
               description));
   }

   /**
    * The status of the hosted service.
    */
   public Status getStatus() {
      return status;
   }

   /**
    * The status of the hosted service unparsed.
    */
   public String getRawStatus() {
      return rawStatus;
   }

   /**
    * The date that the hosted service was created.
    */
   public Date getCreated() {
      return created;
   }

   /**
    * The date that the hosted service was last updated.
    */
   public Date getLastModified() {
      return lastModified;
   }

   /**
    * Represents the name of an extended hosted service property. Each extended property must have
    * both a defined name and value. You can have a maximum of 50 extended property name/value
    * pairs.
    * 
    * The maximum length of the Name element is 64 characters, only alphanumeric characters and
    * underscores are valid in the Name, and the name must start with a letter. Each extended
    * property value has a maximum length of 255 characters.
    */
   public Map<String, String> getExtendedProperties() {
      return extendedProperties;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ToStringHelper string() {
      return super.string().add("status", rawStatus).add("created", created).add("lastModified", lastModified)
               .add("extendedProperties", extendedProperties);
   }

}
