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
package org.jclouds.scriptbuilder.statements.ssh;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.appendFile;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class InstallRSAPrivateKey implements Statement {
   private final String sshDir;
   private final String privateKey;

   public InstallRSAPrivateKey(String privateKey) {
      this("~/.ssh", privateKey);
   }

   public InstallRSAPrivateKey(String sshDir, String privateKey) {
      this.sshDir = checkNotNull(sshDir, "sshDir");
      this.privateKey = checkNotNull(privateKey, "privateKey");
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      checkNotNull(family, "family");
      if (family == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      Builder<Statement> statements = ImmutableList.builder();
      statements.add(exec("{md} " + sshDir));
      String idRsa = sshDir + "{fs}id_rsa";
      statements.add(exec("{rm} " + idRsa));
      statements.add(appendFile(idRsa, Splitter.on('\n').split(privateKey)));
      statements.add(exec("chmod 600 " + idRsa));
      return new StatementList(statements.build()).render(family);
   }
}
