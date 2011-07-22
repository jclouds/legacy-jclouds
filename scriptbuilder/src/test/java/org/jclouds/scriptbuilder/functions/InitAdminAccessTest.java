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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
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
      
      InitBuilder testInitBuilder = new InitBuilder("mkebsboot", "/mnt/tmp", "/mnt/tmp", ImmutableMap.of("tmpDir",
      "/mnt/tmp"), ImmutableList.<Statement> of(statement));
      
      InitAdminAccess initAdminAccess = new InitAdminAccess(configuration);

      initAdminAccess.visit(testInitBuilder);

      verify(configuration);
      verify(statement);
      verify(newStatement);
   }
}
