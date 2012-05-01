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
package org.jclouds.snia.cdmi.v1.config;

import java.util.Map;

import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.snia.cdmi.v1.CDMIAsyncClient;
import org.jclouds.snia.cdmi.v1.CDMIClient;
import org.jclouds.snia.cdmi.v1.features.ContainerAsyncClient;
import org.jclouds.snia.cdmi.v1.features.ContainerClient;
import org.jclouds.snia.cdmi.v1.features.DataAsyncClient;
import org.jclouds.snia.cdmi.v1.features.DataClient;
import org.jclouds.snia.cdmi.v1.features.DomainAsyncClient;
import org.jclouds.snia.cdmi.v1.features.DomainClient;
import org.jclouds.snia.cdmi.v1.handlers.CDMIErrorHandler;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the CDMI connection.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class CDMIRestClientModule extends RestClientModule<CDMIClient, CDMIAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder().put(
            DomainClient.class, DomainAsyncClient.class).put(ContainerClient.class, ContainerAsyncClient.class).put(
            DataClient.class, DataAsyncClient.class).build();

   public CDMIRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(CDMIErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(CDMIErrorHandler.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(CDMIErrorHandler.class);
   }
}
