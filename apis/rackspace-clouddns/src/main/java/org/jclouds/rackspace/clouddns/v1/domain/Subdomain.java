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
package org.jclouds.rackspace.clouddns.v1.domain;

import java.beans.ConstructorProperties;
import java.util.Date;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Subdomains are domains within a parent domain. Subdomains allow you to delegate domains. Subdomains can themselves
 * have subdomains, so third-level, fourth-level, fifth-level, and deeper levels of nesting are possible.
 * 
 * @author Everett Toews
 */
public class Subdomain {
   private final int id;
   private final String name;
   private final String emailAddress;
   private final Optional<String> comment;
   private final Date created;
   private final Date updated;

   @ConstructorProperties({ "id", "name", "emailAddress", "comment", "created", "updated" })
   private Subdomain(int id, String name, String email, @Nullable String comment, Date created, Date updated) {
      this.id = id;
      this.name = name;
      this.emailAddress = email;
      this.comment = Optional.fromNullable(comment);
      this.created = created;
      this.updated = updated;
   }

   public int getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getEmail() {
      return emailAddress;
   }

   public Optional<String> getComment() {
      return comment;
   }

   public Date getCreated() {
      return created;
   }

   public Date getUpdated() {
      return updated;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Subdomain that = Subdomain.class.cast(obj);

      return Objects.equal(this.id, that.id);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("name", name).add("email", emailAddress)
            .add("comment", comment.orNull()).add("created", created).add("updated", updated).toString();
   }
}
