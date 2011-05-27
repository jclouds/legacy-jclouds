/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.scriptbuilder.functions;

import javax.inject.Inject;

import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.scriptbuilder.statements.login.AdminAccess.Configuration;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Statement used in a shell script
 * 
 * @author Adrian Cole
 */
public class InitAdminAccess implements Function<Statement, Statement> {
   private final AdminAccess.Configuration adminAccessConfiguration;

   @Inject
   public InitAdminAccess(Configuration adminAccessConfiguration) {
      this.adminAccessConfiguration = adminAccessConfiguration;
   }

   @Override
   public Statement apply(Statement input) {
      if (input instanceof StatementList) {
         Builder<Statement> statements = ImmutableList.<Statement> builder();
         for (Statement statement : StatementList.class.cast(input).getStatements())
            statements.add(apply(statement));
         return new StatementList(statements.build());
      } else if (input instanceof AdminAccess) {
         return AdminAccess.class.cast(input).apply(adminAccessConfiguration);
      } else {
         return input;
      }
   }
}