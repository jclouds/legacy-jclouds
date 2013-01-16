/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.virtualbox.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.Provider;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class HardcodedHostToHostNodeMetadata implements Function<NodeMetadata, NodeMetadata> {
   
   private final Supplier<URI> providerSupplier;
   private final Supplier<Credentials> creds;

   @Inject
   public HardcodedHostToHostNodeMetadata(@Provider Supplier<URI> providerSupplier,
         @Provider Supplier<Credentials> creds) {
      this.providerSupplier = checkNotNull(providerSupplier, "endpoint to virtualbox websrvd is needed");
      this.creds = creds;
   }

   @Override
   public NodeMetadata apply(NodeMetadata host) {
      Credentials currentCreds = checkNotNull(creds.get(), "credential supplier returned null");
      String username = currentCreds.identity;
      String password = currentCreds.credential.equals("CHANGE_ME") ? "" : currentCreds.credential;

      LoginCredentials.Builder credentialsBuilder = LoginCredentials.builder(host.getCredentials()).user(username);
      if (!password.isEmpty())
         credentialsBuilder.password(password);

      return NodeMetadataBuilder
            .fromNodeMetadata(host)
            .credentials(credentialsBuilder.build())
            .publicAddresses(ImmutableList.of(providerSupplier.get().getHost()))
            .build();
   }

}
