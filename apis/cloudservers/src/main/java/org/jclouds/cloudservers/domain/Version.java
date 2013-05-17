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
package org.jclouds.cloudservers.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Class Version
*/
public class Version {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromVersion(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      protected String docURL;
      protected String id;
      protected VersionStatus status;
      protected String wadl;
   
      /** 
       * @see Version#getDocURL()
       */
      public T docURL(String docURL) {
         this.docURL = docURL;
         return self();
      }

      /** 
       * @see Version#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /** 
       * @see Version#getStatus()
       */
      public T status(VersionStatus status) {
         this.status = status;
         return self();
      }

      /** 
       * @see Version#getWadl()
       */
      public T wadl(String wadl) {
         this.wadl = wadl;
         return self();
      }

      public Version build() {
         return new Version(docURL, id, status, wadl);
      }
      
      public T fromVersion(Version in) {
         return this
                  .docURL(in.getDocURL())
                  .id(in.getId())
                  .status(in.getStatus())
                  .wadl(in.getWadl());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String docURL;
   private final String id;
   private final VersionStatus status;
   private final String wadl;

   @ConstructorProperties({
      "docURL", "id", "status", "wadl"
   })
   protected Version(String docURL, String id, @Nullable VersionStatus status, @Nullable String wadl) {
      this.docURL = checkNotNull(docURL, "docURL");
      this.id = checkNotNull(id, "id");
      this.status = status == null ? VersionStatus.UNRECOGNIZED : status;
      this.wadl = wadl;
   }

   public String getDocURL() {
      return this.docURL;
   }

   public String getId() {
      return this.id;
   }

   public VersionStatus getStatus() {
      return this.status;
   }

   @Nullable
   public String getWadl() {
      return this.wadl;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(docURL, id, status, wadl);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      Version that = Version.class.cast(obj);
      return Objects.equal(this.docURL, that.docURL)
               && Objects.equal(this.id, that.id)
               && Objects.equal(this.status, that.status)
               && Objects.equal(this.wadl, that.wadl);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this)
            .add("docURL", docURL).add("id", id).add("status", status).add("wadl", wadl);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

}
