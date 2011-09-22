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
package org.jclouds.ec2.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.Map;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.services.KeyPairClient;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CreateUniqueKeyPairTest {
   @SuppressWarnings({ "unchecked" })
   @Test
   public void testApply() throws UnknownHostException {
      EC2Client client = createMock(EC2Client.class);
      KeyPairClient keyClient = createMock(KeyPairClient.class);
      Supplier<String> uniqueIdSupplier = createMock(Supplier.class);
      Map<RegionAndName, KeyPair> knownKeys = createMock(Map.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairServices()).andReturn(keyClient).atLeastOnce();

      expect(uniqueIdSupplier.get()).andReturn("1");
      expect(keyClient.createKeyPairInRegion("region", "jclouds#group#region#1")).andReturn(pair);

      replay(client);
      replay(knownKeys);
      replay(keyClient);
      replay(uniqueIdSupplier);

      CreateUniqueKeyPair parser = new CreateUniqueKeyPair(knownKeys, client, uniqueIdSupplier);

      assertEquals(parser.createNewKeyPairInRegion("region", "group"), pair);

      verify(client);
      verify(knownKeys);
      verify(keyClient);
      verify(uniqueIdSupplier);
   }

   @SuppressWarnings({ "unchecked" })
   @Test
   public void testApplyWithIllegalStateException() throws UnknownHostException {
      EC2Client client = createMock(EC2Client.class);
      KeyPairClient keyClient = createMock(KeyPairClient.class);
      Supplier<String> uniqueIdSupplier = createMock(Supplier.class);
      Map<RegionAndName, KeyPair> knownKeys = createMock(Map.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairServices()).andReturn(keyClient).atLeastOnce();

      expect(uniqueIdSupplier.get()).andReturn("1");
      expect(keyClient.createKeyPairInRegion("region", "jclouds#group#region#1")).andThrow(new IllegalStateException());
      expect(uniqueIdSupplier.get()).andReturn("2");
      expect(keyClient.createKeyPairInRegion("region", "jclouds#group#region#2")).andReturn(pair);

      replay(client);
      replay(knownKeys);
      replay(keyClient);
      replay(uniqueIdSupplier);

      CreateUniqueKeyPair parser = new CreateUniqueKeyPair(knownKeys, client, uniqueIdSupplier);

      assertEquals(parser.createNewKeyPairInRegion("region", "group"), pair);

      verify(client);
      verify(knownKeys);
      verify(keyClient);
      verify(uniqueIdSupplier);

   }

   @SuppressWarnings({ "unchecked" })
   @Test
   public void testApplyWhenKnownKeyExists() throws UnknownHostException {
      EC2Client client = createMock(EC2Client.class);
      KeyPairClient keyClient = createMock(KeyPairClient.class);
      Supplier<String> uniqueIdSupplier = createMock(Supplier.class);
      Map<RegionAndName, KeyPair> knownKeys = createMock(Map.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(knownKeys.containsKey(new RegionAndName("region", "group"))).andReturn(true);
      expect(knownKeys.get(new RegionAndName("region", "group"))).andReturn(pair);

      replay(client);
      replay(knownKeys);
      replay(keyClient);
      replay(uniqueIdSupplier);

      CreateUniqueKeyPair parser = new CreateUniqueKeyPair(knownKeys, client, uniqueIdSupplier);

      assertEquals(parser.load(new RegionAndName("region", "group")), pair);

      verify(client);
      verify(knownKeys);
      verify(keyClient);
      verify(uniqueIdSupplier);

   }

   @SuppressWarnings({ "unchecked" })
   @Test
   public void testApplyWhenKnownKeyDoesntExist() throws UnknownHostException {
      EC2Client client = createMock(EC2Client.class);
      KeyPairClient keyClient = createMock(KeyPairClient.class);
      Supplier<String> uniqueIdSupplier = createMock(Supplier.class);
      Map<RegionAndName, KeyPair> knownKeys = createMock(Map.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairServices()).andReturn(keyClient).atLeastOnce();

      expect(knownKeys.containsKey(new RegionAndName("region", "group"))).andReturn(false);
      expect(uniqueIdSupplier.get()).andReturn("1");
      expect(keyClient.createKeyPairInRegion("region", "jclouds#group#region#1")).andThrow(new IllegalStateException());
      expect(uniqueIdSupplier.get()).andReturn("2");
      expect(keyClient.createKeyPairInRegion("region", "jclouds#group#region#2")).andReturn(pair);
      expect(pair.getKeyName()).andReturn("jclouds#group#region#2").times(2);
      // seeding the cache explicitly.  both by keyName and also by group
      expect(knownKeys.put(new RegionAndName("region", "jclouds#group#region#2"), pair)).andReturn(null);
      expect(knownKeys.put(new RegionAndName("region", "group"), pair)).andReturn(null);

      replay(pair);
      replay(client);
      replay(knownKeys);
      replay(keyClient);
      replay(uniqueIdSupplier);

      CreateUniqueKeyPair parser = new CreateUniqueKeyPair(knownKeys, client, uniqueIdSupplier);

      assertEquals(parser.load(new RegionAndName("region", "group")), pair);

      verify(pair);
      verify(client);
      verify(knownKeys);
      verify(keyClient);
      verify(uniqueIdSupplier);

   }
}
