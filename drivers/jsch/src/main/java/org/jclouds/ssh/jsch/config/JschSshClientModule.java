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
package org.jclouds.ssh.jsch.config;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.proxy.ProxyConfig;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.config.ConfiguresSshClient;
import org.jclouds.ssh.jsch.JschSshClient;

import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Scopes;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresSshClient
public class JschSshClientModule extends AbstractModule {

   protected void configure() {
      bind(SshClient.Factory.class).to(Factory.class).in(Scopes.SINGLETON);
   }

   private static class Factory implements SshClient.Factory {
      @Named(Constants.PROPERTY_CONNECTION_TIMEOUT)
      @Inject(optional = true)
      int timeout = 60000;

      private final ProxyConfig proxyConfig;
      private final BackoffLimitedRetryHandler backoffLimitedRetryHandler;
      private final Injector injector;

      @Inject
      public Factory(ProxyConfig proxyConfig, BackoffLimitedRetryHandler backoffLimitedRetryHandler, Injector injector) {
         this.proxyConfig = checkNotNull(proxyConfig, "proxyConfig");
         this.backoffLimitedRetryHandler = checkNotNull(backoffLimitedRetryHandler, "backoffLimitedRetryHandler");
         this.injector = checkNotNull(injector, "injector");
      }

      @Override
      public SshClient create(HostAndPort socket, LoginCredentials credentials) {
         SshClient client = new JschSshClient(proxyConfig, backoffLimitedRetryHandler, socket, credentials, timeout);
         injector.injectMembers(client);// add logger
         return client;
      }
   }
}
