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
package org.jclouds.jenkins.v1.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Minimal info about a Job
 * 
 * @author Adrian Cole
 */
public class Job {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromJob(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public abstract static class Builder<B extends Builder<B>> {
      private String name;
      private URI url;
      private String color;

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see Job#getName()
       */
      public B name(String name) {
         this.name = name;
         return self();
      }
      
      /**
       * @see Job#getUrl()
       */
      public B url(URI url) {
         this.url = url;
         return self();
      }
      
      /**
       * @see Job#getColor()
       */
      public B color(String color) {
         this.color = color;
         return self();
      }

      public Job build() {
         return new Job(this);
      }

      protected B fromJob(Job in) {
         return name(in.getName()).color(in.getColor()).url(in.getUrl());
      }
   }

   private final String name;
   private final String color;
   private final URI url;

   protected Job(Builder<?> builder) {
      this.name = checkNotNull(builder.name, "name");
      this.color = checkNotNull(builder.color, "color");
      this.url = checkNotNull(builder.url, "url");
   }

   /**
    * name of the job
    */
   public String getName() {
      return name;
   }

   /**
    * 
    * color of the job
    */
   public String getColor() {
      return color;
   }

   /**
    * 
    * url of the job
    */
   public URI getUrl() {
      return url;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      Job that = Job.class.cast(o);
      return equal(this.name, that.name) && equal(this.url, that.url)
               && equal(this.color, that.color);
   }

   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      Job that = Job.class.cast(o);
      return equal(this.color, that.color);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(name, url, color);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("name", name).add("url", url).add("color",
               color);
   }
}
