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
package org.jclouds.compute.functions;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.inject.Singleton;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.ssh.AuthorizeRSAPublicKeys;
import org.jclouds.scriptbuilder.statements.ssh.InstallRSAPrivateKey;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class TemplateOptionsToStatement implements Function<TemplateOptions, Statement> {

   @Override
   public Statement apply(TemplateOptions options) {
      List<Statement> bootstrap = newArrayList();
      if (options.getPublicKey() != null)
         bootstrap.add(new AuthorizeRSAPublicKeys(ImmutableSet.of(options.getPublicKey())));
      if (options.getRunScript() != null)
         bootstrap.add(options.getRunScript());
      if (options.getPrivateKey() != null)
         bootstrap.add(new InstallRSAPrivateKey(options.getPrivateKey()));
      if (bootstrap.size() >= 1) {
         if (options.getTaskName() == null && !(options.getRunScript() instanceof InitScript))
            options.nameTask("bootstrap");
         return bootstrap.size() == 1 ? bootstrap.get(0) : new StatementList(bootstrap);
      }
      return null;
   }

}
