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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.jclouds.scriptbuilder.InitScript;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class InitAdminAccessTest {

   public void testWhenNotAdminAccess() {
      AdminAccess.Configuration configuration = createMock(AdminAccess.Configuration.class);

      InitAdminAccess initAdminAccess = new InitAdminAccess(configuration);
      replay(configuration);

      initAdminAccess.visit(Statements.exec("echo hello"));

      initAdminAccess.visit(Statements.newStatementList(Statements.exec("echo hello")));
      verify(configuration);

   }

   public void testWhenAdminAccess() {
      AdminAccess.Configuration configuration = createMock(AdminAccess.Configuration.class);
      AdminAccess statement = createMock(AdminAccess.class);
      AdminAccess newStatement = createMock(AdminAccess.class);

      expect(statement.init(configuration)).andReturn(newStatement);

      replay(configuration);
      replay(statement);
      replay(newStatement);
      InitAdminAccess initAdminAccess = new InitAdminAccess(configuration);

      initAdminAccess.visit(statement);

      verify(configuration);
      verify(statement);
      verify(newStatement);
   }

   public void testWhenAdminAccessInsideList() {
      AdminAccess.Configuration configuration = createMock(AdminAccess.Configuration.class);
      AdminAccess statement = createMock(AdminAccess.class);
      AdminAccess newStatement = createMock(AdminAccess.class);

      expect(statement.init(configuration)).andReturn(newStatement);

      replay(configuration);
      replay(statement);
      replay(newStatement);
      InitAdminAccess initAdminAccess = new InitAdminAccess(configuration);

      initAdminAccess.visit(Statements.newStatementList(statement));

      verify(configuration);
      verify(statement);
      verify(newStatement);
   }
   

   public void testWhenAdminAccessInsideInitBuilder() {
      AdminAccess.Configuration configuration = createMock(AdminAccess.Configuration.class);
      AdminAccess statement = createMock(AdminAccess.class);
      AdminAccess newStatement = createMock(AdminAccess.class);

      expect(statement.init(configuration)).andReturn(newStatement);

      replay(configuration);
      replay(statement);
      replay(newStatement);

      InitScript testInitBuilder = InitScript.builder().name("mkebsboot").home("/mnt/tmp")
            .exportVariables(ImmutableMap.of("tmpDir", "/mnt/tmp")).run(statement).build();
      
      InitAdminAccess initAdminAccess = new InitAdminAccess(configuration);

      initAdminAccess.visit(testInitBuilder);

      verify(configuration);
      verify(statement);
      verify(newStatement);
   }
}
