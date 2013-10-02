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
package org.jclouds.scriptbuilder.statements.ruby;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.extractTargzAndFlattenIntoDirectory;

import java.net.URI;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Installs RubyGems onto a host.
 * 
 * @author Ignasi Barrera
 */
public class InstallRubyGems implements Statement {

   public static final String DEFAULT_RUBYGEMS_VERSION = "1.8.10";
   private static final String RUBYGEMS_URI_TEMPLATE = "http://production.cf.rubygems.org/rubygems/rubygems-%s.tgz";

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Optional<String> version = Optional.absent();
      private boolean updateSystem = false;
      private Optional<String> updateSystemVersion = Optional.absent();
      private boolean updateExistingGems = false;

      /**
       * The version of RubyGems to install.
       */
      public Builder version(@Nullable String version) {
         this.version = Optional.fromNullable(version);
         return this;
      }

      /**
       * Update the gem system after installing RubyGems.
       */
      public Builder updateSystem(boolean updateSystem) {
         this.updateSystem = updateSystem;
         this.updateSystemVersion = Optional.absent();
         return this;
      }

      /**
       * Update the gem system after installing RubyGems, forcing the update to
       * a concrete version.
       */
      public Builder updateSystem(boolean updateSystem, @Nullable String updateSystemVersion) {
         this.updateSystem = updateSystem;
         this.updateSystemVersion = Optional.fromNullable(updateSystemVersion);
         return this;
      }

      /**
       * Update the existing gems after installing RubyGems.
       */
      public Builder updateExistingGems(boolean updateExistingGems) {
         this.updateExistingGems = updateExistingGems;
         return this;
      }

      public InstallRubyGems build() {
         return new InstallRubyGems(version, updateSystem, updateSystemVersion, updateExistingGems);
      }

   }

   private Optional<String> version;
   private boolean updateSystem;
   private Optional<String> updateSystemVersion;
   private boolean updateExistingGems;

   protected InstallRubyGems(Optional<String> version, boolean updateSystem, Optional<String> updateSystemVersion,
         boolean updateExistingGems) {
      this.version = checkNotNull(version, "version must be set");
      this.updateSystem = updateSystem;
      this.updateSystemVersion = checkNotNull(updateSystemVersion, "updateSystemVersion must be set");
      this.updateExistingGems = updateExistingGems;
   }

   @Override
   public String render(OsFamily family) {
      if (family == OsFamily.WINDOWS) {
         throw new UnsupportedOperationException("windows not yet implemented");
      }

      URI rubygemsUri = URI.create(String.format(RUBYGEMS_URI_TEMPLATE, version.or(DEFAULT_RUBYGEMS_VERSION)));

      ImmutableList.Builder<Statement> statements = ImmutableList.builder();
      statements.add(exec("if ! hash gem 2>/dev/null; then"));
      statements.add(exec("("));
      statements.add(extractTargzAndFlattenIntoDirectory(rubygemsUri, "/tmp/rubygems"));
      statements.add(exec("{cd} /tmp/rubygems"));
      statements.add(exec("ruby setup.rb --no-format-executable"));
      statements.add(exec("{rm} -fr /tmp/rubygems"));
      statements.add(exec(")"));
      statements.add(exec("fi"));

      if (updateSystem) {
         statements.add(updateSystemVersion.isPresent() ? exec("gem update --system " + updateSystemVersion.get())
               : exec("gem update --system"));
      }
      if (updateExistingGems) {
         statements.add(exec("gem update --no-rdoc --no-ri"));
      }

      return new StatementList(statements.build()).render(family);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableSet.<String> of();
   }

}
