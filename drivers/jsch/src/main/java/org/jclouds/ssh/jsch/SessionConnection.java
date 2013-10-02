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
package org.jclouds.ssh.jsch;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.proxy.ProxyConfig;
import org.jclouds.ssh.jsch.JschSshClient.Connection;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Proxy;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.ProxySOCKS5;
import com.jcraft.jsch.Session;

public final class SessionConnection implements Connection<Session> {
   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private HostAndPort hostAndPort;
      private LoginCredentials loginCredentials;
      private Optional<Proxy> proxy = Optional.absent();
      private int connectTimeout;
      private int sessionTimeout;

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
       * @see SessionConnection#getProxy()
       */
      public Builder proxy(Proxy proxy) {
         this.proxy = Optional.fromNullable(proxy);
         return this;
      }

      /**
       * @see #proxy(Proxy)
       */
      public Builder proxy(ProxyConfig proxyConfig) {
         Optional<HostAndPort> proxyEndpoint = proxyConfig.getProxy();
         if (!proxyEndpoint.isPresent())
            return proxy((Proxy) null);

         Optional<Credentials> creds = proxyConfig.getCredentials();
         switch (proxyConfig.getType()) {
         case HTTP:
            ProxyHTTP httpProxy = new ProxyHTTP(proxyEndpoint.get().getHostText(), proxyEndpoint.get().getPort());
            if (creds.isPresent())
               httpProxy.setUserPasswd(creds.get().identity, creds.get().credential);
            return proxy(httpProxy);
         case SOCKS:
            ProxySOCKS5 socksProxy = new ProxySOCKS5(proxyEndpoint.get().getHostText(), proxyEndpoint.get().getPort());
            if (creds.isPresent())
               socksProxy.setUserPasswd(creds.get().identity, creds.get().credential);
            return proxy(socksProxy);
         default:
            throw new IllegalArgumentException(proxyConfig.getType() + " not supported");
         }
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
         return new SessionConnection(hostAndPort, loginCredentials, proxy, connectTimeout, sessionTimeout);
      }

      public Builder from(SessionConnection in) {
         return hostAndPort(in.hostAndPort).loginCredentials(in.loginCredentials).proxy(in.proxy.orNull())
               .connectTimeout(in.connectTimeout).sessionTimeout(in.sessionTimeout);
      }

   }

   private SessionConnection(HostAndPort hostAndPort, LoginCredentials loginCredentials, Optional<Proxy> proxy,
         int connectTimeout, int sessionTimeout) {
      this.hostAndPort = checkNotNull(hostAndPort, "hostAndPort");
      this.loginCredentials = checkNotNull(loginCredentials, "loginCredentials for %", hostAndPort);
      this.connectTimeout = connectTimeout;
      this.sessionTimeout = sessionTimeout;
      this.proxy = checkNotNull(proxy, "proxy for %", hostAndPort);
   }

   private static final byte[] emptyPassPhrase = new byte[0];

   private final HostAndPort hostAndPort;
   private final LoginCredentials loginCredentials;
   private final Optional<Proxy> proxy;
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
         checkArgument(!loginCredentials.getPrivateKey().contains("Proc-Type: 4,ENCRYPTED"),
               "JschSshClientModule does not support private keys that require a passphrase");
         byte[] privateKey = loginCredentials.getPrivateKey().getBytes();
         jsch.addIdentity(loginCredentials.getUser(), privateKey, null, emptyPassPhrase);
      }
      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);
      if (proxy.isPresent())
         session.setProxy(proxy.get());
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
    * @return proxy used for this connection
    */
   public Optional<Proxy> getProxy() {
      return proxy;
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
   @Nullable
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
      return Objects.toStringHelper("").omitNullValues()
            .add("hostAndPort", hostAndPort).add("loginUser", loginCredentials.getUser())
            .add("session", session != null ? session.hashCode() : null)
            .add("connectTimeout", connectTimeout)
            .add("proxy", proxy.orNull())
            .add("sessionTimeout", sessionTimeout).toString();
   }

}
