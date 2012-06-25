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
package org.jclouds.vcloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.internal.CatalogItemImpl;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code CatalogItemHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CatalogItemHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/catalogItem-hosting.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      CatalogItem result = factory.create(injector.getInstance(CatalogItemHandler.class)).parse(is);

      assertEquals(result, new CatalogItemImpl("Windows 2008 Datacenter 64 Bit", URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/2"), "Windows 2008 Datacenter 64 Bit",
               new ReferenceTypeImpl("Windows 2008 Datacenter 64 Bit",
                        "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                                 .create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/2")),
               ImmutableSortedMap.of("Foo", "Bar", "Hello", "World"

               )));

   }
   
   public void testApplyInputStreamWithNamespaceUsingVcloud() {
      InputStream is = getClass().getResourceAsStream("/catalogItem-carrenza-with-vcloud-namespace.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      CatalogItem result = factory.create(injector.getInstance(CatalogItemHandler.class)).parse(is);

      assertEquals(result, new CatalogItemImpl("ubuntu10.10x64", 
               URI.create("https://myvdc.carrenza.net/api/v1.0/catalogItem/ecd4d3a0-0d12-4195-a6d2-14cdf9f925a3"), 
               null, new ReferenceTypeImpl("ubuntu10.10x64", "application/vnd.vmware.vcloud.vAppTemplate+xml", 
                        URI.create("https://myvdc.carrenza.net/api/v1.0/vAppTemplate/vappTemplate-123766ea-2b55-482c-8adf-735ab1952834")),
               ImmutableSortedMap.<String,String>of()));
   }
   
   public void testApplyInputStreamWithNamespaceUsingDefault() {
      InputStream is = getClass().getResourceAsStream("/catalogItem-carrenza-with-default-namespace.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      CatalogItem result = factory.create(injector.getInstance(CatalogItemHandler.class)).parse(is);

      assertEquals(result, new CatalogItemImpl("ubuntu10.10x64", 
               URI.create("https://myvdc.carrenza.net/api/v1.0/catalogItem/ecd4d3a0-0d12-4195-a6d2-14cdf9f925a3"), 
               null, new ReferenceTypeImpl("ubuntu10.10x64", "application/vnd.vmware.vcloud.vAppTemplate+xml", 
                        URI.create("https://myvdc.carrenza.net/api/v1.0/vAppTemplate/vappTemplate-123766ea-2b55-482c-8adf-735ab1952834")),
               ImmutableSortedMap.<String,String>of()));
   }
}
