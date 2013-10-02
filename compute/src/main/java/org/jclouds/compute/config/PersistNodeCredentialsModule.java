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

import java.util.Map;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.functions.CredentialsFromAdminAccess;

import com.google.common.base.Function;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
public class PersistNodeCredentialsModule extends AbstractModule {

   static class RefreshCredentialsForNodeIfRanAdminAccess implements Function<NodeMetadata, NodeMetadata> {
      protected final Map<String, Credentials> credentialStore;
      protected final Statement statement;

      @Inject
      protected RefreshCredentialsForNodeIfRanAdminAccess(Map<String, Credentials> credentialStore,
            @Nullable @Assisted Statement statement) {
         this.credentialStore = credentialStore;
         this.statement = statement;
      }

      @Override
      public NodeMetadata apply(NodeMetadata input) {
         if (statement == null)
            return input;
         Credentials credentials = CredentialsFromAdminAccess.INSTANCE.apply(statement);
         if (credentials != null) {
            LoginCredentials creds = LoginCredentials.fromCredentials(credentials);
            input = NodeMetadataBuilder.fromNodeMetadata(input).credentials(creds).build();
            credentialStore.put("node#" + input.getId(), input.getCredentials());
         }
         return input;
      }

   }

   static class RefreshCredentialsForNode extends RefreshCredentialsForNodeIfRanAdminAccess {

      @Inject
      public RefreshCredentialsForNode(Map<String, Credentials> credentialStore, @Assisted @Nullable Statement statement) {
         super(credentialStore, statement);
      }

      @Override
      public NodeMetadata apply(NodeMetadata input) {
         input = super.apply(input);
         if (input.getCredentials() != null)
            credentialStore.put("node#" + input.getId(), input.getCredentials());
         return input;
      }

   }

   @Override
   protected void configure() {

      install(new FactoryModuleBuilder().implement(new TypeLiteral<Function<NodeMetadata, NodeMetadata>>() {
      }, Names.named("ifAdminAccess"), RefreshCredentialsForNodeIfRanAdminAccess.class)
            .implement(new TypeLiteral<Function<NodeMetadata, NodeMetadata>>() {
            }, Names.named("always"), RefreshCredentialsForNode.class).build(PersistNodeCredentials.class));
   }

}
