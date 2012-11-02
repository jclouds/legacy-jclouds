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
 * Minimal info about a LastBuild
 * 
 * GET http://host/job/project/lastBuild/api/json
 * 
 * @author Andrea Turli
 */
public class LastBuild {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromJob(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }

   public abstract static class Builder<B extends Builder<B>> {
      private String id;
      private URI url;
      private String description;
      private String building;
      private String duration;
      private String estimatedDuration;
      private String fullDisplayName;
      private String result;
      private String timestamp;

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * @see LastBuild#getId()
       */
      public B id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see LastBuild#getUrl()
       */
      public B url(URI url) {
         this.url = url;
         return self();
      }

      /**
       * @see LastBuild#getDescription()
       */
      public B description(String description) {
         this.description = description;
         return self();
      }

      /**
       * @see LastBuild#getBuilding()
       */
      public B building(String building) {
         this.building = building;
         return self();
      }

      /**
       * @see LastBuild#getDuration()
       */
      public B duration(String duration) {
         this.duration = duration;
         return self();
      }

      /**
       * @see LastBuild#getEstimatedDuration()
       */
      public B estimatedDuration(String estimatedDuration) {
         this.estimatedDuration = estimatedDuration;
         return self();
      }

      /**
       * @see LastBuild#getFullDisplayName()
       */
      public B fullDisplayName(String fullDisplayName) {
         this.fullDisplayName = fullDisplayName;
         return self();
      }

      /**
       * @see LastBuild#getResult()
       */
      public B result(String result) {
         this.result = result;
         return self();
      }

      /**
       * @see LastBuild#getTimestamp()
       */
      public B timestamp(String timestamp) {
         this.timestamp = timestamp;
         return self();
      }

      public LastBuild build() {
         return new LastBuild(this);
      }

      protected B fromJob(LastBuild in) {
         return id(in.getId()).url(in.getUrl()).description(in.getDescription()).building(in.getBuilding())
                  .duration(in.getDuration()).estimatedDuration(in.getEstimatedDuration())
                  .fullDisplayName(in.getFullDisplayName()).result(in.getResult()).timestamp(in.getTimestamp());
      }
   }

   private final String id;
   private final URI url;
   private final String description;
   private final String building;
   private final String duration;
   private final String estimatedDuration;
   private final String fullDisplayName;
   private final String result;
   private final String timestamp;

   protected LastBuild(Builder<?> builder) {
      this.id = checkNotNull(builder.id, "id");
      this.url = checkNotNull(builder.url, "url");
      this.description = checkNotNull(builder.description, "description");
      this.building = checkNotNull(builder.building, "building");
      this.duration = checkNotNull(builder.duration, "duration");
      this.estimatedDuration = checkNotNull(builder.estimatedDuration, "estimatedDuration");
      this.fullDisplayName = checkNotNull(builder.fullDisplayName, "fullDisplayName");
      this.result = checkNotNull(builder.result, "result");
      this.timestamp = checkNotNull(builder.timestamp, "timestamp");
   }

   /**
    * id of the lastBuild
    */
   public String getId() {
      return id;
   }

   /**
    * 
    * url of the lastBuild
    */
   public URI getUrl() {
      return url;
   }

   /**
    * 
    * building of the lastBuild
    */
   public String getBuilding() {
      return building;
   }

   /**
    * 
    * description of the lastBuild
    */
   public String getDescription() {
      return description;
   }

   /**
    * 
    * duration of the lastBuild
    */
   public String getDuration() {
      return duration;
   }

   /**
    * 
    * estimated duration of the lastBuild
    */
   public String getEstimatedDuration() {
      return estimatedDuration;
   }

   /**
    * 
    * full Display Name of the lastBuild
    */
   public String getFullDisplayName() {
      return fullDisplayName;
   }

   /**
    * 
    * result of the lastBuild
    */
   public String getResult() {
      return result;
   }

   /**
    * 
    * timestamp of the lastBuild
    */
   public String getTimestamp() {
      return timestamp;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      LastBuild that = LastBuild.class.cast(o);
      return equal(this.id, that.id) 
               && equal(this.url, that.url) 
               && equal(this.building, that.building)
               && equal(this.description, that.description) 
               && equal(this.duration, that.duration)
               && equal(this.estimatedDuration, that.estimatedDuration)
               && equal(this.fullDisplayName, that.fullDisplayName)
               && equal(this.result, that.result)
               && equal(this.timestamp, that.timestamp);
   }

   public boolean clone(Object o) {
      if (this == o)
         return false;
      if (o == null || getClass() != o.getClass())
         return false;
      LastBuild that = LastBuild.class.cast(o);
      return equal(this.description, that.description);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id, url, description, duration, estimatedDuration, fullDisplayName, result, timestamp);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("")
               .add("id", id)
               .add("url", url)
               .add("description", description)
               .add("duration", duration)
               .add("estimatedDuration", estimatedDuration)
               .add("fullDisplayName", fullDisplayName)
               .add("result", result)
               .add("timestamp", timestamp);
   }
}
