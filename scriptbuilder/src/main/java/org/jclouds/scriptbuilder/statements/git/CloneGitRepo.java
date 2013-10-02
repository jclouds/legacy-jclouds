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
package org.jclouds.scriptbuilder.statements.git;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.scriptbuilder.domain.GitRepoAndRef;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Clones a gitRepoAndRef into a newly created directory, creates
 * remote-tracking branches for each branch in the cloned gitRepoAndRef (visible
 * using git branch -r), and creates and checks out an initial branch that is
 * forked from the cloned gitRepoAndRef's currently active branch. PWD is set to
 * the directory being checked out.
 * 
 * @author Adrian Cole
 */
public class CloneGitRepo implements Statement {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromCloneGitRepo(this);
   }

   public static class Builder {

      protected GitRepoAndRef.Builder gitRepoAndRef = GitRepoAndRef.builder();
      protected Optional<String> directory = Optional.absent();

      /**
       * @see GitRepoAndRef#getRepository()
       */
      public Builder repository(URI repository) {
         this.gitRepoAndRef.repository(repository);
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
         this.gitRepoAndRef.branch(branch);
         return this;
      }

      /**
       * @see GitRepoAndRef#getTag()
       */
      public Builder tag(String tag) {
         this.gitRepoAndRef.tag(tag);
         return this;
      }

      /**
       * @see CloneGitRepo#getGitRepoAndRef()
       */
      public Builder gitRepoAndRef(GitRepoAndRef gitRepoAndRef) {
         this.gitRepoAndRef.fromGitRepoAndRef(gitRepoAndRef);
         return this;
      }

      /**
       * @see CloneGitRepo#getDirectory()
       */
      public Builder directory(String directory) {
         this.directory = Optional.fromNullable(directory);
         return this;
      }

      public CloneGitRepo build() {
         return new CloneGitRepo(gitRepoAndRef.build(), directory);
      }

      public Builder fromCloneGitRepo(CloneGitRepo in) {
         return this.gitRepoAndRef(in.getGitRepoAndRef()).directory(in.getDirectory().orNull());
      }
   }

   protected final GitRepoAndRef gitRepoAndRef;
   protected final Optional<String> directory;

   protected CloneGitRepo(GitRepoAndRef gitRepoAndRef, Optional<String> directory) {
      this.gitRepoAndRef = checkNotNull(gitRepoAndRef, "gitRepoAndRef");
      this.directory = checkNotNull(directory, "directory");
   }

   /**
    * The coordinates to checkout
    */
   public GitRepoAndRef getGitRepoAndRef() {
      return gitRepoAndRef;
   }
   
   /**
    * The name of a new directory to clone into. The "humanish" part of the
    * source gitRepoAndRef is used if no directory is explicitly given (repo for
    * /path/to/repo.git and foo for host.xz:foo/.git).
    */
   public Optional<String> getDirectory() {
      return directory;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(gitRepoAndRef, directory);
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
      CloneGitRepo other = CloneGitRepo.class.cast(obj);
      return Objects.equal(this.gitRepoAndRef, other.gitRepoAndRef) && Objects.equal(this.directory, other.directory);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<String> functionDependencies(OsFamily arg0) {
      return ImmutableSet.<String> of();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String render(OsFamily arg0) {
      StringBuilder command = new StringBuilder();
      command.append("git clone");
      if (gitRepoAndRef.getBranch().isPresent())
         command.append(" -b ").append(gitRepoAndRef.getBranch().get());
      command.append(' ').append(gitRepoAndRef.getRepository().toASCIIString());
      if (directory.isPresent())
         command.append(' ').append(directory.get());
      command.append("{lf}");
      command.append("{cd} ").append(
            directory.or(Iterables.getLast(Splitter.on('/').split(gitRepoAndRef.getRepository().getPath())).replace(".git", "")));
      if (gitRepoAndRef.getTag().isPresent()) {
         command.append("{lf}").append("git checkout ").append(gitRepoAndRef.getTag().get());
      }
      return Statements.exec(command.toString()).render(arg0);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("gitRepoAndRef", gitRepoAndRef)
            .add("directory", directory.orNull()).toString();
   }

}
