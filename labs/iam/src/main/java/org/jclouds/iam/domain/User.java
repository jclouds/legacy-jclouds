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
package org.jclouds.iam.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference/API_GetUser.html" />
 * 
 * @author Adrian Cole
 */
public final class User {

   private final String arn;
   private final String id;
   private final Optional<String> name;
   private final Optional<String> path;
   private final Date createDate;

   private User(String arn, String id, Optional<String> name, Optional<String> path, Date createDate) {
      this.arn = checkNotNull(arn, "arn");
      this.id = checkNotNull(id, "id for %s", arn);
      this.name = checkNotNull(name, "name for %s", arn);
      this.path = checkNotNull(path, "path for %s", arn);
      this.createDate = checkNotNull(createDate, "createDate for %s", arn);
   }

   /**
    * how to specify the resource in the access policy language ex.
    * {@code arn:aws:<service>:<region>:<namespace>:<relative-id>}
    */
   public String getArn() {
      return arn;
   }

   /**
    * a globally unique identifier (GUID), returned from the api.
    */
   public String getId() {
      return id;
   }

   /**
    * friendly name ex. {@code Developers}
    */
   public Optional<String> getName() {
      return name;
   }

   /**
    * path ex {@code  /division_abc/subdivision_xyz/product_1234/engineering/}
    */
   public Optional<String> getPath() {
      return path;
   }

   /**
    * Date the user was created
    */
   public Date getCreateDate() {
      return createDate;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(arn, id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      User that = User.class.cast(obj);
      return equal(this.arn, that.arn) && equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return toStringHelper(this).omitNullValues().add("arn", arn).add("id", id).add("name", name.orNull())
            .add("path", path.orNull()).add("createDate", createDate).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static class Builder {
      private String arn;
      private String id;
      private Optional<String> name = Optional.absent();
      private Optional<String> path = Optional.absent();
      private Date createDate;

      /**
       * @see User#getArn()
       */
      public Builder arn(String arn) {
         this.arn = arn;
         return this;
      }

      /**
       * @see User#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see User#getName()
       */
      public Builder name(String name) {
         this.name = Optional.fromNullable(name);
         return this;
      }

      /**
       * @see User#getPath()
       */
      public Builder path(String path) {
         this.path = Optional.fromNullable(path);
         return this;
      }

      /**
       * @see User#getCreateDate()
       */
      public Builder createDate(Date createDate) {
         this.createDate = createDate;
         return this;
      }

      public User build() {
         return new User(arn, id, name, path, createDate);
      }

      public Builder from(User in) {
         return arn(in.arn).id(in.id).name(in.name.orNull()).path(in.path.orNull()).createDate(in.createDate);
      }
   }
}
