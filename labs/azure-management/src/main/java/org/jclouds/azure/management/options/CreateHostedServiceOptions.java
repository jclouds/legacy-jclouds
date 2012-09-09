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
package org.jclouds.azure.management.options;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Optional parameters for creating a hosted service
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441304" >docs</a>
 * 
 * @author Adrian Cole
 */
public class CreateHostedServiceOptions implements Cloneable {

   private Optional<String> description = Optional.absent();
   private Optional<Map<String, String>> extendedProperties = Optional.absent();

   /**
    * @see CreateHostedServiceOptions#getDescription()
    */
   public CreateHostedServiceOptions description(String description) {
      this.description = Optional.fromNullable(description);
      return this;
   }

   /**
    * @see CreateHostedServiceOptions#getExtendedProperties()
    */
   public CreateHostedServiceOptions extendedProperties(Map<String, String> extendedProperties) {
      this.extendedProperties = Optional.fromNullable(extendedProperties);
      return this;
   }

   /**
    * A description for the hosted service. The description can be up to 1024 characters in length.
    */
   public Optional<String> getDescription() {
      return description;
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
   public Optional<Map<String, String>> getExtendedProperties() {
      return extendedProperties;
   }

   public static class Builder {

      /**
       * @see CreateHostedServiceOptions#getDescription()
       */
      public static CreateHostedServiceOptions description(String description) {
         return new CreateHostedServiceOptions().description(description);
      }

      /**
       * @see CreateHostedServiceOptions#getExtendedProperties()
       */
      public static CreateHostedServiceOptions extendedProperties(Map<String, String> extendedProperties) {
         return new CreateHostedServiceOptions().extendedProperties(extendedProperties);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(description, extendedProperties);
   }

   @Override
   public CreateHostedServiceOptions clone() {
      return new CreateHostedServiceOptions().description(description.orNull()).extendedProperties(
               extendedProperties.orNull());
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
      CreateHostedServiceOptions other = CreateHostedServiceOptions.class.cast(obj);
      return Objects.equal(this.description, other.description)
               && Objects.equal(this.extendedProperties, other.extendedProperties);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper("").omitNullValues().add("description", description.orNull())
               .add("extendedProperties", extendedProperties.orNull()).toString();
   }
}
