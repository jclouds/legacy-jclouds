/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.ssh.jsch.config;

import static org.jclouds.util.Utils.isPrivateKeyCredential;

import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.domain.Credentials;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.ssh.ConfiguresSshClient;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.JschSshClient;

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
      bind(SocketOpen.class).to(InetSocketAddressConnect.class).in(Scopes.SINGLETON);
   }

   private static class Factory implements SshClient.Factory {
      @Named(Constants.PROPERTY_CONNECTION_TIMEOUT)
      @Inject(optional = true)
      int timeout = 60000;

      private final BackoffLimitedRetryHandler backoffLimitedRetryHandler;
      private final Injector injector;

      @SuppressWarnings("unused")
      @Inject
      public Factory(BackoffLimitedRetryHandler backoffLimitedRetryHandler, Injector injector) {
         this.backoffLimitedRetryHandler = backoffLimitedRetryHandler;
         this.injector = injector;
      }

      public SshClient create(IPSocket socket, String username, String password) {
         SshClient client = new JschSshClient(backoffLimitedRetryHandler, socket, timeout, username, password, null);
         injector.injectMembers(client);// add logger
         return client;
      }

      public SshClient create(IPSocket socket, String username, byte[] privateKey) {
         SshClient client = new JschSshClient(backoffLimitedRetryHandler, socket, timeout, username, null, privateKey);
         injector.injectMembers(client);// add logger
         return client;
      }

      @Override
      public SshClient create(IPSocket socket, Credentials credentials) {
         return isPrivateKeyCredential(credentials) ? create(socket, credentials.identity,
               credentials.credential.getBytes()) : create(socket, credentials.identity, credentials.credential);
      }
   }
}