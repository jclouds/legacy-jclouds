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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;

import org.jclouds.domain.Credentials;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;

public class ProxyForURITest {

   private Optional<HostAndPort> noHostAndPort = Optional.absent();
   private Optional<Credentials> noCreds = Optional.absent();
   private Optional<HostAndPort> hostAndPort = Optional.of(HostAndPort.fromParts("proxy.example.com", 8080));
   private Optional<Credentials> creds = Optional.of(new Credentials("user", "pwd"));

   private class MyProxyConfig implements ProxyConfig {
      private boolean useSystem;
      private Type type;
      private Optional<HostAndPort> proxy;
      private Optional<Credentials> credentials;

      MyProxyConfig(boolean useSystem, Type type, Optional<HostAndPort> proxy, Optional<Credentials> credentials) {
         this.useSystem = useSystem;
         this.type = type;
         this.proxy = proxy;
         this.credentials = credentials;
      }

      @Override
      public boolean useSystem() {
         return useSystem;
      }

      @Override
      public Type getType() {
         return type;
      }

      @Override
      public Optional<HostAndPort> getProxy() {
         return proxy;
      }

      @Override
      public Optional<Credentials> getCredentials() {
         return credentials;
      }
   }

   @Test
   public void testDontUseProxyForSockets() throws Exception {
      ProxyConfig config = new MyProxyConfig(false, Proxy.Type.HTTP, hostAndPort, creds);
      ProxyForURI proxy = new ProxyForURI(config);
      Field useProxyForSockets = proxy.getClass().getDeclaredField("useProxyForSockets");
      useProxyForSockets.setAccessible(true);
      useProxyForSockets.setBoolean(proxy, false);
      URI uri = new URI("socket://ssh.example.com:22");
      assertEquals(proxy.apply(uri), Proxy.NO_PROXY);
   }

   @Test
   public void testUseProxyForSockets() throws Exception {
      ProxyConfig config = new MyProxyConfig(false, Proxy.Type.HTTP, hostAndPort, creds);
      ProxyForURI proxy = new ProxyForURI(config);
      URI uri = new URI("socket://ssh.example.com:22");
      assertEquals(proxy.apply(uri), new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.example.com", 8080)));
   }

   @Test
   public void testUseProxyForSocketsSettingShouldntAffectHTTP() throws Exception {
      ProxyConfig config = new MyProxyConfig(false, Proxy.Type.HTTP, hostAndPort, creds);
      ProxyForURI proxy = new ProxyForURI(config);
      Field useProxyForSockets = proxy.getClass().getDeclaredField("useProxyForSockets");
      useProxyForSockets.setAccessible(true);
      useProxyForSockets.setBoolean(proxy, false);
      URI uri = new URI("http://example.com/file");
      assertEquals(proxy.apply(uri), new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.example.com", 8080)));
   }

   @Test
   public void testHTTPDirect() throws URISyntaxException {
      ProxyConfig config = new MyProxyConfig(false, Proxy.Type.DIRECT, noHostAndPort, noCreds);
      URI uri = new URI("http://example.com/file");
      assertEquals(new ProxyForURI(config).apply(uri), Proxy.NO_PROXY);
   }

   @Test
   public void testHTTPSDirect() throws URISyntaxException {
      ProxyConfig config = new MyProxyConfig(false, Proxy.Type.DIRECT, noHostAndPort, noCreds);
      URI uri = new URI("https://example.com/file");
      assertEquals(new ProxyForURI(config).apply(uri), Proxy.NO_PROXY);
   }

   @Test
   public void testFTPDirect() throws URISyntaxException {
      ProxyConfig config = new MyProxyConfig(false, Proxy.Type.DIRECT, noHostAndPort, noCreds);
      URI uri = new URI("ftp://ftp.example.com/file");
      assertEquals(new ProxyForURI(config).apply(uri), Proxy.NO_PROXY);
   }

   @Test
   public void testSocketDirect() throws URISyntaxException {
      ProxyConfig config = new MyProxyConfig(false, Proxy.Type.DIRECT, noHostAndPort, noCreds);
      URI uri = new URI("socket://ssh.example.com:22");
      assertEquals(new ProxyForURI(config).apply(uri), Proxy.NO_PROXY);
   }

   @Test
   public void testHTTPThroughHTTPProxy() throws URISyntaxException {
      ProxyConfig config = new MyProxyConfig(false, Proxy.Type.HTTP, hostAndPort, creds);
      URI uri = new URI("http://example.com/file");
      assertEquals(new ProxyForURI(config).apply(uri), new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
            "proxy.example.com", 8080)));
   }

   @Test
   public void testHTTPThroughSystemProxy() throws URISyntaxException {
      ProxyConfig config = new MyProxyConfig(true, Proxy.Type.DIRECT, noHostAndPort, noCreds);
      URI uri = new URI("http://example.com/file");
      // could return a proxy, could return NO_PROXY, depends on the tester's
      // environment
      assertNotNull(new ProxyForURI(config).apply(uri));
   }

}
