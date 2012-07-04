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
package org.jclouds.openstack.quantum.v1_0.config;

import java.util.Map;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.quantum.v1_0.QuantumAsyncClient;
import org.jclouds.openstack.quantum.v1_0.QuantumClient;
import org.jclouds.openstack.quantum.v1_0.features.NetworkAsyncClient;
import org.jclouds.openstack.quantum.v1_0.features.NetworkClient;
import org.jclouds.openstack.quantum.v1_0.features.PortAsyncClient;
import org.jclouds.openstack.quantum.v1_0.features.PortClient;
import org.jclouds.openstack.quantum.v1_0.handlers.QuantumErrorHandler;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the Quantum connection.
 * 
 * @author Adam Lowe
 */
@ConfiguresRestClient
public class QuantumRestClientModule extends RestClientModule<QuantumClient, QuantumAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()
         .put(NetworkClient.class, NetworkAsyncClient.class)
         .put(PortClient.class, PortAsyncClient.class)
         .build();

   public QuantumRestClientModule() {
      super(DELEGATE_MAP);
   }
   
   @Override
   protected void configure() {
      bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      super.configure();
   }
   
   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(QuantumErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(QuantumErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(QuantumErrorHandler.class);
   }
}
