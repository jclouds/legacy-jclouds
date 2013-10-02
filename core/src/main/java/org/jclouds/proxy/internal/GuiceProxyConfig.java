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
package org.jclouds.proxy.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.Constants.PROPERTY_PROXY_HOST;
import static org.jclouds.Constants.PROPERTY_PROXY_PASSWORD;
import static org.jclouds.Constants.PROPERTY_PROXY_PORT;
import static org.jclouds.Constants.PROPERTY_PROXY_SYSTEM;
import static org.jclouds.Constants.PROPERTY_PROXY_TYPE;
import static org.jclouds.Constants.PROPERTY_PROXY_USER;

import java.net.Proxy;
import java.net.Proxy.Type;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.proxy.ProxyConfig;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.net.HostAndPort;
import com.google.inject.Inject;

/**
 * Configuration derived from Guice properties.
 * 
 * @author Adrian Cole
 */
@Singleton
public class GuiceProxyConfig implements ProxyConfig {

   @Inject(optional = true)
   @Named(PROPERTY_PROXY_SYSTEM)
   private boolean systemProxies = Boolean.parseBoolean(System.getProperty("java.net.useSystemProxies", "false"));
   @Inject(optional = true)
   @Named(PROPERTY_PROXY_HOST)
   private String host;
   @Inject(optional = true)
   @Named(PROPERTY_PROXY_PORT)
   private Integer port;
   @Inject(optional = true)
   @Named(PROPERTY_PROXY_USER)
   private String user;
   @Inject(optional = true)
   @Named(PROPERTY_PROXY_PASSWORD)
   private String password;
   @Inject(optional = true)
   @Named(PROPERTY_PROXY_TYPE)
   private Proxy.Type type = Proxy.Type.HTTP;

   @Override
   public Optional<HostAndPort> getProxy() {
      if (host == null)
         return Optional.absent();
      Integer port = this.port;
      if (port == null) {
         switch (type) {
         case HTTP:
            port = 80;
            break;
         case SOCKS:
            port = 1080;
            break;
         default:
            throw new IllegalArgumentException(type + " not supported");
         }
      }
      return Optional.of(HostAndPort.fromParts(host, port));
   }

   @Override
   public Optional<Credentials> getCredentials() {
      if (user == null)
         return Optional.absent();
      return Optional.of(new Credentials(user, checkNotEmpty(password, "set property %s for user %s",
            PROPERTY_PROXY_PASSWORD, user)));
   }

   private static String checkNotEmpty(String nullableString, String message, Object... args) {
      checkArgument(Strings.emptyToNull(nullableString) != null, message, args);
      return nullableString;
   }

   @Override
   public Type getType() {
      return type;
   }

   @Override
   public boolean useSystem() {
      return systemProxies;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("systemProxies", systemProxies ? "true" : null)
            .add("proxy", getProxy().orNull()).add("user", user).add("type", host != null ? type : null).toString();
   }

}
