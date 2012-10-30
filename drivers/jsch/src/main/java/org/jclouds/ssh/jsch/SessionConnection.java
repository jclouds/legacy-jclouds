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
package org.jclouds.ssh.jsch;

import static com.google.common.base.Objects.equal;

import java.util.Arrays;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.ssh.jsch.JschSshClient.Connection;
import org.jclouds.util.CredentialUtils;

import com.google.common.base.Objects;
import com.google.common.net.HostAndPort;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SessionConnection implements Connection<Session> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      protected HostAndPort hostAndPort;
      protected LoginCredentials loginCredentials;
      protected int connectTimeout;
      protected int sessionTimeout;

      /**
       * @see SessionConnection#getHostAndPort()
       */
      public Builder hostAndPort(HostAndPort hostAndPort) {
         this.hostAndPort = hostAndPort;
         return this;
      }

      /**
       * @see SessionConnection#getLoginCredentials()
       */
      public Builder loginCredentials(LoginCredentials loginCredentials) {
         this.loginCredentials = loginCredentials;
         return this;
      }

      /**
       * @see SessionConnection#getConnectTimeout()
       */
      public Builder connectTimeout(int connectTimeout) {
         this.connectTimeout = connectTimeout;
         return this;
      }

      /**
       * @see SessionConnection#getConnectTimeout()
       */
      public Builder sessionTimeout(int sessionTimeout) {
         this.sessionTimeout = sessionTimeout;
         return this;
      }

      public SessionConnection build() {
         return new SessionConnection(hostAndPort, loginCredentials, connectTimeout, sessionTimeout);
      }

      protected Builder fromSessionConnection(SessionConnection in) {
         return hostAndPort(in.getHostAndPort()).connectTimeout(in.getConnectTimeout()).loginCredentials(
                  in.getLoginCredentials());
      }
   }

   private SessionConnection(HostAndPort hostAndPort, LoginCredentials loginCredentials, int connectTimeout,
            int sessionTimeout) {
      this.hostAndPort = hostAndPort;
      this.loginCredentials = loginCredentials;
      this.connectTimeout = connectTimeout;
      this.sessionTimeout = sessionTimeout;
   }

   private static final byte[] emptyPassPhrase = new byte[0];

   private final HostAndPort hostAndPort;
   private final LoginCredentials loginCredentials;
   private final int connectTimeout;
   private final int sessionTimeout;

   private transient Session session;

   @Override
   public void clear() {
      if (session != null && session.isConnected()) {
         session.disconnect();
         session = null;
      }
   }

   @Override
   public Session create() throws Exception {
      JSch jsch = new JSch();
      session = jsch
               .getSession(loginCredentials.getUser(), hostAndPort.getHostText(), hostAndPort.getPortOrDefault(22));
      if (sessionTimeout != 0)
         session.setTimeout(sessionTimeout);
      if (loginCredentials.getPrivateKey() == null) {
         session.setPassword(loginCredentials.getPassword());
      } else {
         byte[] privateKey = loginCredentials.getPrivateKey().getBytes();
         if (CredentialUtils.isPrivateKeyEncrypted(privateKey)) {
            throw new IllegalArgumentException(
                     "JschSshClientModule does not support private keys that require a passphrase");
         }
         jsch.addIdentity(loginCredentials.getUser(), Arrays.copyOf(privateKey, privateKey.length), null,
                  emptyPassPhrase);
      }
      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      session.connect(connectTimeout);
      return session;
   }

   /**
    * @return host and port, where port if not present defaults to {@code 22}
    */
   public HostAndPort getHostAndPort() {
      return hostAndPort;
   }

   /**
    * 
    * @return login used in this session
    */
   public LoginCredentials getLoginCredentials() {
      return loginCredentials;
   }

   /**
    * 
    * @return how long to wait for the initial connection to be made
    */
   public int getConnectTimeout() {
      return connectTimeout;
   }

   /**
    * 
    * @return how long to keep the session open, or {@code 0} for indefinitely
    */
   public int getSessionTimeout() {
      return sessionTimeout;
   }

   /**
    * 
    * @return the current session or {@code null} if not connected
    */
   public Session getSession() {
      return session;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      SessionConnection that = SessionConnection.class.cast(o);
      return equal(this.hostAndPort, that.hostAndPort) && equal(this.loginCredentials, that.loginCredentials)
               && equal(this.session, that.session);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(hostAndPort, loginCredentials, session);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("hostAndPort", hostAndPort).add("loginUser", loginCredentials.getUser())
               .add("session", session != null ? session.hashCode() : null).add("connectTimeout", connectTimeout).add(
                        "sessionTimeout", sessionTimeout).toString();
   }


}
