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
package org.jclouds.openstack.nova.v2_0.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;

import org.jclouds.openstack.nova.v2_0.NovaClient;
import org.jclouds.openstack.nova.v2_0.compute.loaders.CreateUniqueKeyPair;
import org.jclouds.openstack.nova.v2_0.domain.KeyPair;
import org.jclouds.openstack.nova.v2_0.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v2_0.extensions.KeyPairClient;
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
      final NovaClient client = createMock(NovaClient.class);
      KeyPairClient keyClient = createMock(KeyPairClient.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairExtensionForZone("zone")).andReturn(Optional.of(keyClient)).atLeastOnce();

      expect(keyClient.createKeyPair("group-1")).andReturn(pair);

      replay(client, keyClient);

      CreateUniqueKeyPair parser = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(Suppliers.ofInstance("1"));
            bind(NovaClient.class).toInstance(client);
         }

      }).getInstance(CreateUniqueKeyPair.class);

      assertEquals(parser.load(ZoneAndName.fromZoneAndName("zone", "group")), pair);

      verify(client, keyClient);
   }

   @Test
   public void testApplyWithIllegalStateException() throws UnknownHostException {
      final NovaClient client = createMock(NovaClient.class);
      KeyPairClient keyClient = createMock(KeyPairClient.class);
      @SuppressWarnings("unchecked")
      final Supplier<String> uniqueIdSupplier = createMock(Supplier.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairExtensionForZone("zone")).andReturn(Optional.of(keyClient)).atLeastOnce();

      expect(uniqueIdSupplier.get()).andReturn("1");
      expect(keyClient.createKeyPair("group-1")).andThrow(new IllegalStateException());
      expect(uniqueIdSupplier.get()).andReturn("2");
      expect(keyClient.createKeyPair("group-2")).andReturn(pair);

      replay(client, keyClient, uniqueIdSupplier);

      CreateUniqueKeyPair parser = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(uniqueIdSupplier);
            bind(NovaClient.class).toInstance(client);
         }

      }).getInstance(CreateUniqueKeyPair.class);

      assertEquals(parser.load(ZoneAndName.fromZoneAndName("zone", "group")), pair);

      verify(client, keyClient, uniqueIdSupplier);
   }

}
