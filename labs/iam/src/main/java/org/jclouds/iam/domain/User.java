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

import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference/API_GetUser.html" />
 * 
 * @author Adrian Cole
 */
public class User {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromUser(this);
   }

   public abstract static class Builder<T extends Builder<T>>  {
      protected abstract T self();

      private Optional<String> path = Optional.absent();
      private String id;
      private Optional<String> name = Optional.absent();
      private String arn;
      private Date createDate;

      /**
       * @see User#getPath()
       */
      public T path(String path) {
         this.path = Optional.fromNullable(path);
         return self();
      }

      /**
       * @see User#getId()
       */
      public T id(String id) {
         this.id = id;
         return self();
      }

      /**
       * @see User#getName()
       */
      public T name(String name) {
         this.name = Optional.fromNullable(name);
         return self();
      }

      /**
       * @see User#getArn()
       */
      public T arn(String arn) {
         this.arn = arn;
         return self();
      }

      /**
       * @see User#getCreateDate()
       */
      public T createDate(Date createDate) {
         this.createDate = createDate;
         return self();
      }
      
      public User build() {
         return new User(path, name, id, arn, createDate);
      }

      public T fromUser(User in) {
         return this
               .path(in.getPath().orNull())
               .name(in.getName().orNull())
               .id(in.getId())
               .arn(in.getArn())
               .createDate(in.getCreateDate())
               ;
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }
   
   private final Optional<String> path;
   private final Optional<String> name;
   private final String id;
   private final String arn;
   private final Date createDate;
   
   protected User(Optional<String> path, Optional<String> name, String id, String arn, Date createDate) {
      this.path = path;
      this.name = name;
      this.id = id;
      this.arn = arn;
      this.createDate = createDate;
   }

   /**
    * you can also optionally give the entity a path that you define. You might use the path to
    * identify which division or part of the organization the entity belongs in. For example:
    * /division_abc/subdivision_xyz/product_1234/engineering/
    */
   public Optional<String> getPath() {
      return path;
   }

   /**
    * When you create a user, a role, or a group, or when you upload a server certificate, you give
    * it a friendly name, such as Bob, TestApp1, Developers, or ProdServerCert. Whenever you need to
    * specify a particular entity in an API call to IAM (for example, to delete a user, or update a
    * group with a new user), you use the friendly name.
    */
   public Optional<String> getName() {
      return name;
   }

   /**
    * We assign each user, group, and server certificate a globally unique identifier (GUID), which
    * we return to you when you use the API or CLI to create it. We recommend you store the GUID in
    * your own database along with the user, group, or certificate name. Internally we use the GUID
    * to identify the user, group, or certificate, and we translate the value into the ARN or
    * friendly name as appropriate when displaying the user, group, or certificate information to
    * you. If you delete a user, group, or server certificate, any residual remote references to
    * that item display the GUID as the friendly name part in the ARN. If you've stored the GUID in
    * your own system, you can then use the displayed GUID to identify the deleted item being
    * referred to.
    */
   public String getId() {
      return id;
   }

   /**
    * Although most resources have a friendly name (for example, a user named Bob or a group named
    * Developers), the access policy language requires you to specify the resource or resources
    * using the following Amazon Resource Name (ARN) format.
    * 
    * {@code arn:aws:<service>:<region>:<namespace>:<relative-id>}
    */
   public String getArn() {
      return arn;
   }

   /**
    * Date the user was created
    */
   public Date getCreateDate() {
      return createDate;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(id, arn);
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
      User other = (User) obj;
      return Objects.equal(this.id, other.id) && Objects.equal(this.arn, other.arn);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("path", path).add("name", name).add("id", id).add("arn", arn).add(
               "createDate", createDate).toString();
   }

}
