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
package org.jclouds.apis;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The BaseApiMetadata class is an abstraction of {@link ApiMetadata} to be
 * extended by those implementing ApiMetadata.
 * 
 * (Note: This class must be abstract to allow {@link java.util.ServiceLoader}
 * to work properly.
 * 
 * @author Jeremy Whitlock <jwhitlock@apache.org>, Adrian Cole
 */
public abstract class BaseApiMetadata implements ApiMetadata {

   public static abstract class Builder<B extends Builder<B>> implements ApiMetadata.Builder<B> {
      protected String id;
      protected String name;
      protected ApiType type;
      protected String identityName;
      protected String credentialName;
      protected URI documentation;

      @SuppressWarnings("unchecked")
      protected B self() {
         return (B) this;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B id(String id) {
         this.id = checkNotNull(id, "id");
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B name(String name) {
         this.name = checkNotNull(name, "name");
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B type(ApiType type) {
         this.type = checkNotNull(type, "type");
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B identityName(String identityName) {
         this.identityName = checkNotNull(identityName, "identityName");
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B credentialName(@Nullable String credentialName) {
         this.credentialName = credentialName;
         return self();
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public B documentation(URI documentation) {
         this.documentation = checkNotNull(documentation, "documentation");
         return self();
      }

      public B fromApiMetadata(ApiMetadata in) {
         return id(in.getId()).type(in.getType()).name(in.getName()).identityName(in.getIdentityName())
               .credentialName(in.getCredentialName());
      }
   }

   protected final String id;
   protected final String name;
   protected final ApiType type;
   protected final String identityName;
   protected final String credentialName;
   protected final URI documentation;

   protected BaseApiMetadata(Builder<?> builder) {
      this.id = builder.id;
      this.name = builder.name;
      this.type = builder.type;
      this.identityName = builder.identityName;
      this.credentialName = builder.credentialName;
      this.documentation = builder.documentation;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      // subclass equivalence is ok, since we don't know the classloader
      // we'll get things from
      if (o == null || !(o instanceof ApiMetadata))
         return false;
      ApiMetadata that = ApiMetadata.class.cast(o);
      return equal(this.getId(), that.getId()) && equal(this.getName(), that.getName())
            && equal(this.getType(), that.getType()) && equal(this.getIdentityName(), that.getIdentityName())
            && equal(this.getCredentialName(), that.getCredentialName())
            && equal(this.getDocumentation(), that.getDocumentation());
   }

   @Override
   public int hashCode() {
      return Objects
            .hashCode(getId(), getName(), getType(), getIdentityName(), getCredentialName(), getDocumentation());
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").add("id", getId()).add("name", getName()).add("type", getType())
            .add("identityName", getIdentityName()).add("credentialName", getCredentialName())
            .add("documentation", getDocumentation());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId() {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ApiType getType() {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getIdentityName() {
      return identityName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCredentialName() {
      return credentialName;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public URI getDocumentation() {
      return documentation;
   }

}