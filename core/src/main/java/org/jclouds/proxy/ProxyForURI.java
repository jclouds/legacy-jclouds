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
package org.jclouds.proxy;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getLast;
import static org.jclouds.Constants.PROPERTY_PROXY_FOR_SOCKETS;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.Inject;

/**
 * @author Adrian Cole
 */
@Singleton
public class ProxyForURI implements Function<URI, Proxy> {
   private final ProxyConfig config;

   @Inject(optional = true)
   @Named(PROPERTY_PROXY_FOR_SOCKETS)
   private boolean useProxyForSockets = true;

   @VisibleForTesting
   @Inject
   ProxyForURI(ProxyConfig config) {
      this.config = checkNotNull(config, "config");
   }

   /**
    * @param endpoint
    *           <ul>
    *           <li>http URI for http connections</li>
    *           <li>https URI for https connections</li>
    *           <li>ftp URI for ftp connections</li>
    *           <li>socket://host:port for tcp client sockets connections</li>
    *           </ul>
    */
   @Override
   public Proxy apply(URI endpoint) {
      if (!useProxyForSockets && "socket".equals(endpoint.getScheme())) {
         return Proxy.NO_PROXY;
      } else if (config.useSystem()) {
         System.setProperty("java.net.useSystemProxies", "true");
         Iterable<Proxy> proxies = ProxySelector.getDefault().select(endpoint);
         return getLast(proxies);
      } else if (config.getProxy().isPresent()) {
         SocketAddress addr = new InetSocketAddress(config.getProxy().get().getHostText(), config.getProxy().get()
               .getPort());
         Proxy proxy = new Proxy(config.getType(), addr);

         final Optional<Credentials> creds = config.getCredentials();
         if (creds.isPresent()) {
            Authenticator authenticator = new Authenticator() {
               public PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(creds.get().identity, creds.get().credential.toCharArray());
               }
            };
            Authenticator.setDefault(authenticator);
         }
         return proxy;
      } else {
         return Proxy.NO_PROXY;
      }
   }

}
