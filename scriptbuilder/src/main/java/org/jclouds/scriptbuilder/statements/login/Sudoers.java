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
package org.jclouds.scriptbuilder.statements.login;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.createOrOverwriteFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

/**
 * Control /etc/sudoers
 * 
 * @author Adrian Cole
 */
@Beta
public class Sudoers implements Statement {
   private static final String sudoers = "/etc/sudoers";

   public String render(OsFamily family) {
      checkNotNull(family, "family");
      if (family == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      Builder<Statement> statements = ImmutableList.builder();
      statements.add(createOrOverwriteFile(sudoers, ImmutableSet.of("root ALL = (ALL) ALL", "%wheel ALL = (ALL) NOPASSWD:ALL")));
      statements.add(exec("chmod 0440 " + sudoers));
      return new StatementList(statements.build()).render(family);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }
}
