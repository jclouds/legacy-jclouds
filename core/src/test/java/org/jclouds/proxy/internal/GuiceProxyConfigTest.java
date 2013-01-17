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
package org.jclouds.proxy.internal;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ExecutionList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jclouds.domain.Credentials;
import org.testng.annotations.Test;

import java.net.Proxy;

import static com.google.inject.name.Names.named;
import static org.jclouds.Constants.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Shane Witbeck
 */
public class GuiceProxyConfigTest {

   @Test
   public void useSystemTrue() {
      GuiceProxyConfig guiceProxyConfig = createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_PROXY_SYSTEM)).to(true);
         }
      });
      assertEquals(guiceProxyConfig.useSystem(), true);
   }

   @Test
   public void useSystemFalse() {
      GuiceProxyConfig guiceProxyConfig = createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_PROXY_SYSTEM)).to(false);
         }
      });
      assertEquals(guiceProxyConfig.useSystem(), false);
   }

   @Test
   public void getCredentials() {
      GuiceProxyConfig guiceProxyConfig = createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_PROXY_USER)).to("bob");
            bindConstant().annotatedWith(named(PROPERTY_PROXY_PASSWORD)).to("secret");
         }
      });
      Optional<Credentials> creds = guiceProxyConfig.getCredentials();
      assertEquals(creds, Optional.of(new Credentials("bob", "secret")));
   }

   @Test(expectedExceptions = {IllegalArgumentException.class})
   public void getCredentialsNullPassword() {
      GuiceProxyConfig guiceProxyConfig = createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_PROXY_USER)).to("bob");
         }
      });
      guiceProxyConfig.getCredentials();
   }

   @Test
   public void getCredentialsNullUser() {
      GuiceProxyConfig guiceProxyConfig = createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            // no op
         }
      });
      Optional<Credentials> creds = guiceProxyConfig.getCredentials();
      assertTrue(!creds.isPresent());
   }

   @Test
   public void getProxyNullHost() {
      GuiceProxyConfig guiceProxyConfig = createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_PROXY_TYPE)).to(Proxy.Type.SOCKS);
         }
      });
      assertEquals(guiceProxyConfig.getType(), Proxy.Type.SOCKS);
      assertEquals(guiceProxyConfig.getProxy(), Optional.absent());
   }

   @Test
   public void getProxySocks() {
      GuiceProxyConfig guiceProxyConfig = createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_PROXY_TYPE)).to(Proxy.Type.SOCKS);
            bindConstant().annotatedWith(named(PROPERTY_PROXY_HOST)).to("proxy.foo.com");
         }
      });
      assertEquals(guiceProxyConfig.getType(), Proxy.Type.SOCKS);
      assertEquals(guiceProxyConfig.getProxy(), Optional.of(HostAndPort.fromParts("proxy.foo.com", 1080)));
   }

   @Test(expectedExceptions = {IllegalArgumentException.class})
   public void getProxyDirect() {
      GuiceProxyConfig guiceProxyConfig = createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_PROXY_TYPE)).to(Proxy.Type.DIRECT);
            bindConstant().annotatedWith(named(PROPERTY_PROXY_HOST)).to("proxy.foo.com");
         }
      });
      assertEquals(guiceProxyConfig.getType(), Proxy.Type.DIRECT);
      guiceProxyConfig.getProxy();
   }

   @Test
   public void getProxyHttp() {
      GuiceProxyConfig guiceProxyConfig = createInjector(new AbstractModule() {
         @Override
         protected void configure() {
            bindConstant().annotatedWith(named(PROPERTY_PROXY_TYPE)).to(Proxy.Type.HTTP);
            bindConstant().annotatedWith(named(PROPERTY_PROXY_HOST)).to("proxy.foo.com");
         }
      });
      assertEquals(guiceProxyConfig.getType(), Proxy.Type.HTTP);
      assertEquals(guiceProxyConfig.getProxy(), Optional.of(HostAndPort.fromParts("proxy.foo.com", 80)));
   }

   private GuiceProxyConfig createInjector(AbstractModule module) {
      Injector i = Guice.createInjector(module);
      GuiceProxyConfig guiceProxyConfig = new GuiceProxyConfig();
      i.injectMembers(guiceProxyConfig);
      i.getInstance(ExecutionList.class).execute();
      return guiceProxyConfig;
   }
}
