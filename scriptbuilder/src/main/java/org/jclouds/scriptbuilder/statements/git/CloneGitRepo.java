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
package org.jclouds.scriptbuilder.statements.git;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Clones a repository into a newly created directory, creates remote-tracking branches for each
 * branch in the cloned repository (visible using git branch -r), and creates and checks out an
 * initial branch that is forked from the cloned repository's currently active branch. PWD is set to
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

      protected URI repository;
      protected Optional<String> branch = Optional.absent();
      protected Optional<String> tag = Optional.absent();
      protected Optional<String> directory = Optional.absent();

      /**
       * @see CloneGitRepo#getRepository()
       */
      public Builder repository(URI repository) {
         this.repository = repository;
         return this;
      }

      /**
       * @see CloneGitRepo#getRepository()
       */
      public Builder repository(String repository) {
         return repository(URI.create(repository));
      }

      /**
       * @see CloneGitRepo#getBranch()
       */
      public Builder branch(String branch) {
         this.branch = Optional.fromNullable(branch);
         return this;
      }

      /**
       * @see CloneGitRepo#getTag()
       */
      public Builder tag(String tag) {
         this.tag = Optional.fromNullable(tag);
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
         return new CloneGitRepo(repository, branch, tag, directory);
      }

      public Builder fromCloneGitRepo(CloneGitRepo in) {
         return this.repository(in.getRepository()).branch(in.getBranch().orNull()).tag(in.getTag().orNull())
                  .directory(in.getDirectory().orNull());
      }
   }

   protected final URI repository;
   protected final Optional<String> branch;
   protected final Optional<String> tag;
   protected final Optional<String> directory;

   protected CloneGitRepo(URI repository, Optional<String> branch, Optional<String> tag, Optional<String> directory) {
      this.repository = checkNotNull(repository, "repository");
      this.branch = checkNotNull(branch, "branch");
      this.tag = checkNotNull(tag, "tag");
      this.directory = checkNotNull(directory, "directory");
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
    * The name of a new directory to clone into. The "humanish" part of the source repository is
    * used if no directory is explicitly given (repo for /path/to/repo.git and foo for
    * host.xz:foo/.git).
    */
   public Optional<String> getDirectory() {
      return directory;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(repository, branch, tag, directory);
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
      return Objects.equal(this.repository, other.repository) && Objects.equal(this.branch, other.branch)
               && Objects.equal(this.tag, other.tag) && Objects.equal(this.directory, other.directory);
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
      if (branch.isPresent())
         command.append(" -b ").append(branch.get());
      command.append(' ').append(repository.toASCIIString());
      if (directory.isPresent())
         command.append(' ').append(directory.get());
      command.append("{lf}");
      command.append("{cd} ").append(
               directory.or(Iterables.getLast(Splitter.on('/').split(repository.getPath())).replace(".git", "")));
      if (tag.isPresent()) {
         command.append("{lf}").append("git checkout ").append(tag.get());
      }
      return Statements.exec(command.toString()).render(arg0);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("repository", repository).add("branch", branch.orNull())
               .add("tag", tag.orNull()).add("directory", directory.orNull()).toString();
   }

}
