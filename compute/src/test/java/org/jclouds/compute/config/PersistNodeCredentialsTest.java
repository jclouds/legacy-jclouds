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
package org.jclouds.compute.config;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.compute.config.PersistNodeCredentialsModule.RefreshCredentialsForNode;
import org.jclouds.compute.config.PersistNodeCredentialsModule.RefreshCredentialsForNodeIfRanAdminAccess;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class PersistNodeCredentialsTest {

   public void testReturnsCorrectFunction() {
      PersistNodeCredentials persistNodeCredentials = Guice.createInjector(new PersistNodeCredentialsModule(),
            new AbstractModule() {

               @Override
               protected void configure() {
                  bind(new TypeLiteral<Map<String, Credentials>>() {
                  }).toInstance(ImmutableMap.<String, Credentials> of());
               }

            }).getInstance(PersistNodeCredentials.class);
      assertEquals(persistNodeCredentials.always(null).getClass(),
            PersistNodeCredentialsModule.RefreshCredentialsForNode.class);
      assertEquals(persistNodeCredentials.ifAdminAccess(null).getClass(),
            PersistNodeCredentialsModule.RefreshCredentialsForNodeIfRanAdminAccess.class);
   }

   public void testRefreshCredentialsForNodeIfRanAdminAccessWhenStatementIsNullSameCredentialsAndNoCaching() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);

      replay(credstore);

      NodeMetadata node = new NodeMetadataBuilder().ids("id").status(Status.RUNNING).build();
      RefreshCredentialsForNodeIfRanAdminAccess fn = new PersistNodeCredentialsModule.RefreshCredentialsForNodeIfRanAdminAccess(
            credstore, null);
      assertEquals(node, fn.apply(node));

      verify(credstore);

   }

   public void testRefreshCredentialsForNodeWhenStatementIsNullSameCredentialsAndDoesCache() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);
      LoginCredentials credentials = createMock(LoginCredentials.class);

      expect(credstore.put("node#id", credentials)).andReturn(null);

      replay(credstore);

      NodeMetadata node = new NodeMetadataBuilder().ids("id").status(Status.RUNNING).credentials(credentials).build();
      RefreshCredentialsForNode fn = new PersistNodeCredentialsModule.RefreshCredentialsForNode(credstore, null);
      assertEquals(node, fn.apply(node));

      verify(credstore);

   }

   public void testRefreshCredentialsForNodeIfRanAdminAccessWhenStatementIsAdminAccessNewCredentialsAndDoesCache() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);

      AdminAccess statement = createMock(AdminAccess.class);
      LoginCredentials credentials = LoginCredentials.builder().user("foo").build();

      expect(statement.getAdminCredentials()).andReturn(credentials).atLeastOnce();
      expect(credstore.put("node#id", credentials)).andReturn(null);

      replay(statement);
      replay(credstore);

      NodeMetadata node = new NodeMetadataBuilder().ids("id").status(Status.RUNNING).build();
      RefreshCredentialsForNodeIfRanAdminAccess fn = new PersistNodeCredentialsModule.RefreshCredentialsForNodeIfRanAdminAccess(
            credstore, statement);
      assertEquals(fn.apply(node).getCredentials(), credentials);

      verify(statement);
      verify(credstore);

   }

   public void testRefreshCredentialsForNodeWhenStatementIsAdminAccessNewCredentialsAndDoesCache() {
      @SuppressWarnings("unchecked")
      Map<String, Credentials> credstore = createMock(Map.class);

      AdminAccess statement = createMock(AdminAccess.class);
      LoginCredentials credentials = LoginCredentials.builder().user("foo").build();
      expect(statement.getAdminCredentials()).andReturn(credentials).atLeastOnce();
      expect(credstore.put("node#id", credentials)).andReturn(null);
      expect(credstore.put("node#id", credentials)).andReturn(null); // TODO
                                                                     // optimize
                                                                     // this

      replay(statement);
      replay(credstore);

      NodeMetadata node = new NodeMetadataBuilder().ids("id").status(Status.RUNNING).build();
      RefreshCredentialsForNode fn = new PersistNodeCredentialsModule.RefreshCredentialsForNode(credstore, statement);
      assertEquals(fn.apply(node).getCredentials(), credentials);

      verify(statement);
      verify(credstore);

   }

}
