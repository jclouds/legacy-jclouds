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
package org.jclouds.scriptbuilder.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * Description of git coordinates to checkout.
 * 
 * @author Adrian Cole
 */
public class GitRepoAndRef  {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromGitRepoAndRef(this);
   }

   public static class Builder {

      protected URI repository;
      protected Optional<String> branch = Optional.absent();
      protected Optional<String> tag = Optional.absent();

      /**
       * @see GitRepoAndRef#getRepository()
       */
      public Builder repository(URI repository) {
         this.repository = repository;
         return this;
      }

      /**
       * @see GitRepoAndRef#getRepository()
       */
      public Builder repository(String repository) {
         return repository(URI.create(repository));
      }

      /**
       * @see GitRepoAndRef#getBranch()
       */
      public Builder branch(String branch) {
         this.branch = Optional.fromNullable(branch);
         return this;
      }

      /**
       * @see GitRepoAndRef#getTag()
       */
      public Builder tag(String tag) {
         this.tag = Optional.fromNullable(tag);
         return this;
      }

      public GitRepoAndRef build() {
         return new GitRepoAndRef(repository, branch, tag);
      }

      public Builder fromGitRepoAndRef(GitRepoAndRef in) {
         return this.repository(in.getRepository()).branch(in.getBranch().orNull()).tag(in.getTag().orNull());
      }
   }

   protected final URI repository;
   protected final Optional<String> branch;
   protected final Optional<String> tag;

   protected GitRepoAndRef(URI repository, Optional<String> branch, Optional<String> tag) {
      this.repository = checkNotNull(repository, "repository");
      this.branch = checkNotNull(branch, "branch");
      this.tag = checkNotNull(tag, "tag");
   }

   /**
    * The (possibly remote) repository to clone from.
    */
   public URI getRepository() {
      return repository;
   }

   /**
    * Instead of pointing the newly created HEAD to the branch pointed to by the cloned repository's
    * HEAD, point to this branch instead. In a non-bare repository, this is the branch that will be
    * checked out.
    */
   public Optional<String> getBranch() {
      return branch;
   }

   /**
    * checkout the following tag on the branch
    */
   public Optional<String> getTag() {
      return tag;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(repository, branch, tag);
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
      GitRepoAndRef other = GitRepoAndRef.class.cast(obj);
      return Objects.equal(this.repository, other.repository) && Objects.equal(this.branch, other.branch)
               && Objects.equal(this.tag, other.tag);
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("repository", repository).add("branch", branch.orNull())
               .add("tag", tag.orNull()).toString();
   }

}
