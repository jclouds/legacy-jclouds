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
package org.jclouds.openstack.nova.v2_0.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;

import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairApi;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "CreateUniqueKeyPairTest")
public class CreateUniqueKeyPairTest {

   @Test
   public void testApply() throws UnknownHostException {
      final NovaApi api = createMock(NovaApi.class);
      KeyPairApi keyApi = createMock(KeyPairApi.class);

      KeyPair pair = createMock(KeyPair.class);

      Optional optKeyApi = Optional.of(keyApi);
      
      expect(api.getKeyPairExtensionForZone("zone")).andReturn(optKeyApi).atLeastOnce();

      expect(keyApi.create("group-1")).andReturn(pair);

      replay(api, keyApi);

      CreateUniqueKeyPair parser = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(Suppliers.ofInstance("1"));
            bind(NovaApi.class).toInstance(api);
         }

      }).getInstance(CreateUniqueKeyPair.class);

      assertEquals(parser.load(ZoneAndName.fromZoneAndName("zone", "group")), pair);

      verify(api, keyApi);
   }

   @Test
   public void testApplyWithIllegalStateException() throws UnknownHostException {
      final NovaApi api = createMock(NovaApi.class);
      KeyPairApi keyApi = createMock(KeyPairApi.class);
      @SuppressWarnings("unchecked")
      final Supplier<String> uniqueIdSupplier = createMock(Supplier.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(api.getKeyPairExtensionForZone("zone")).andReturn((Optional) Optional.of(keyApi)).atLeastOnce();

      expect(uniqueIdSupplier.get()).andReturn("1");
      expect(keyApi.create("group-1")).andThrow(new IllegalStateException());
      expect(uniqueIdSupplier.get()).andReturn("2");
      expect(keyApi.create("group-2")).andReturn(pair);

      replay(api, keyApi, uniqueIdSupplier);

      CreateUniqueKeyPair parser = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(uniqueIdSupplier);
            bind(NovaApi.class).toInstance(api);
         }

      }).getInstance(CreateUniqueKeyPair.class);

      assertEquals(parser.load(ZoneAndName.fromZoneAndName("zone", "group")), pair);

      verify(api, keyApi, uniqueIdSupplier);
   }

}
