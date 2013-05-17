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
package org.jclouds.scriptbuilder.statements.chef;

import static org.jclouds.scriptbuilder.domain.Statements.exec;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * Installs Chef gems onto a host.
 * 
 * @author Ignasi Barrera
 */
public class InstallChefGems implements Statement {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private Optional<String> version = Optional.absent();

      /**
       * The version of the Chef gem to install.
       * <p>
       * Can be something like '>= 0.10.8'.
       */
      public Builder version(@Nullable String version) {
         this.version = Optional.fromNullable(version);
         return this;
      }

      public InstallChefGems build() {
         return new InstallChefGems(version);
      }
   }

   private Optional<String> version;

   protected InstallChefGems(Optional<String> version) {
      this.version = version;
   }

   @Override
   public String render(OsFamily family) {
      if (family == OsFamily.WINDOWS) {
         throw new UnsupportedOperationException("windows not yet implemented");
      }

      Statement statement = version.isPresent() ? exec(String.format("gem install chef -v '%s' --no-rdoc --no-ri",
            version.get())) : exec("gem install chef --no-rdoc --no-ri");

      return statement.render(family);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableSet.<String> of();
   }

}
