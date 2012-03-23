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
package org.jclouds.openstack.nova.v1_1.compute.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;

import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.domain.KeyPair;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v1_1.extensions.KeyPairClient;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit")
public class CreateUniqueKeyPairTest {

   @Test
   public void testApply() throws UnknownHostException {
      NovaClient client = createMock(NovaClient.class);
      KeyPairClient keyClient = createMock(KeyPairClient.class);
      Supplier<String> uniqueIdSupplier = createMock(Supplier.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairExtensionForZone("zone")).andReturn(Optional.of(keyClient)).atLeastOnce();

      expect(uniqueIdSupplier.get()).andReturn("1");
      expect(keyClient.createKeyPair("group_1")).andReturn(pair);

      replay(client);
      replay(keyClient);
      replay(uniqueIdSupplier);

      CreateUniqueKeyPair parser = new CreateUniqueKeyPair(client, uniqueIdSupplier);

      assertEquals(parser.apply(ZoneAndName.fromZoneAndName("zone", "group")), pair);

      verify(client);
      verify(keyClient);
      verify(uniqueIdSupplier);
   }

   @Test
   public void testApplyWithIllegalStateException() throws UnknownHostException {
      NovaClient client = createMock(NovaClient.class);
      KeyPairClient keyClient = createMock(KeyPairClient.class);
      Supplier<String> uniqueIdSupplier = createMock(Supplier.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairExtensionForZone("zone")).andReturn(Optional.of(keyClient)).atLeastOnce();

      expect(uniqueIdSupplier.get()).andReturn("1");
      expect(keyClient.createKeyPair("group_1")).andThrow(new IllegalStateException());
      expect(uniqueIdSupplier.get()).andReturn("2");
      expect(keyClient.createKeyPair("group_2")).andReturn(pair);

      replay(client);
      replay(keyClient);
      replay(uniqueIdSupplier);

      CreateUniqueKeyPair parser = new CreateUniqueKeyPair(client, uniqueIdSupplier);

      assertEquals(parser.apply(ZoneAndName.fromZoneAndName("zone", "group")), pair);

      verify(client);
      verify(keyClient);
      verify(uniqueIdSupplier);
   }

}
