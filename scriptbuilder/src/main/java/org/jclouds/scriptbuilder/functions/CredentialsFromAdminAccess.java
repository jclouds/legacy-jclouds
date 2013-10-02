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
package org.jclouds.scriptbuilder.functions;

import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.domain.Credentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.AcceptsStatementVisitor;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementVisitor;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Atomics;

/**
 * 
 * @author Adrian Cole
 */
public enum CredentialsFromAdminAccess implements Function<Statement, Credentials> {
   INSTANCE;
   @Override
   public Credentials apply(@Nullable Statement input) {
      if (input == null)
         return null;
      if (input instanceof AcceptsStatementVisitor) {
         final AtomicReference<Credentials> credsHolder = Atomics.newReference();
         AcceptsStatementVisitor.class.cast(input).accept(new StatementVisitor() {

            @Override
            public void visit(Statement in) {
               if (credsHolder.get() == null) {
                  Credentials creds = apply(in);
                  if (creds != null)
                     credsHolder.set(creds);
               }
            }

         });
         return credsHolder.get();
      } else if (input instanceof AdminAccess) {
         return AdminAccess.class.cast(input).getAdminCredentials();
      } else {
         return null;
      }
   }
}
