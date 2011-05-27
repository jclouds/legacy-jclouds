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
import static org.testng.Assert.assertEquals;

import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class InitAdminAccessTest {

   public void testWhenNotAdminAccess() {

      InitAdminAccess initAdminAccess = new InitAdminAccess(createMock(AdminAccess.Configuration.class));
      Statement statement = Statements.exec("echo hello");
      assertEquals(initAdminAccess.apply(statement), statement);

      Statement statementList = Statements.newStatementList(statement);
      assertEquals(initAdminAccess.apply(statementList), statementList);

   }

   public void testWhenAdminAccess() {
      AdminAccess.Configuration configuration = createMock(AdminAccess.Configuration.class);
      AdminAccess statement = createMock(AdminAccess.class);
      AdminAccess newStatement = createMock(AdminAccess.class);

      expect(statement.apply(configuration)).andReturn(newStatement);

      replay(configuration);
      replay(statement);
      replay(newStatement);
      InitAdminAccess initAdminAccess = new InitAdminAccess(configuration);

      assertEquals(initAdminAccess.apply(statement), newStatement);

      verify(configuration);
      verify(statement);
      verify(newStatement);
   }

   public void testWhenAdminAccessInsideList() {
      AdminAccess.Configuration configuration = createMock(AdminAccess.Configuration.class);
      AdminAccess statement = createMock(AdminAccess.class);
      AdminAccess newStatement = createMock(AdminAccess.class);

      expect(statement.apply(configuration)).andReturn(newStatement);

      replay(configuration);
      replay(statement);
      replay(newStatement);
      InitAdminAccess initAdminAccess = new InitAdminAccess(configuration);

      assertEquals(initAdminAccess.apply(Statements.newStatementList(statement)),
            Statements.newStatementList(newStatement));

      verify(configuration);
      verify(statement);
      verify(newStatement);
   }
}
