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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.trmk.vcloud_0_8.domain.FenceMode;
import org.jclouds.trmk.vcloud_0_8.domain.Network;
import org.jclouds.trmk.vcloud_0_8.domain.internal.NetworkImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VDCNetworkHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class NetworkHandlerTest {

   private Injector injector;

   private Factory factory;

   public void testTerremark() {
      InputStream is = getClass().getResourceAsStream("/network-terremark.xml");
      injector = Guice.createInjector(new SaxParserModule());
      factory = injector.getInstance(ParseSax.Factory.class);
      Network result = factory.create(injector.getInstance(NetworkHandler.class)).parse(is);
      assertEquals(
            result,
            new NetworkImpl(
                  "10.122.209.128/28",
                  "application/vnd.vmware.vcloud.network+xml",
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/network/40031"),
                  null,
                  "10.122.209.129",
                  "255.255.255.240",
                  FenceMode.ISOLATED,
                  new ReferenceTypeImpl(
                        "10.122.209.128/28",
                        "application/xml",
                        URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/extensions/network/40031")),
                  new ReferenceTypeImpl(
                        "IP Addresses",
                        "application/xml",
                        URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.5/extensions/network/40031/ips"))));
   }
}
