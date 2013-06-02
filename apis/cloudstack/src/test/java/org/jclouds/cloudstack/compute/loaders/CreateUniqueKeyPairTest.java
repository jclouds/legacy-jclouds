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
package org.jclouds.cloudstack.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.SshKeyPair;
import org.jclouds.cloudstack.features.SSHKeyPairClient;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.TypeLiteral;

/**
 * @author Adam Lowe
 * @author Andrew Bayer
 */
@Test(groups = "unit", testName = "CreateUniqueKeyPairTest")
public class CreateUniqueKeyPairTest {

   @Test
   public void testLoad() throws UnknownHostException {
      final CloudStackClient client = createMock(CloudStackClient.class);
      SSHKeyPairClient keyClient = createMock(SSHKeyPairClient.class);

      SshKeyPair pair = createMock(SshKeyPair.class);

      expect(client.getSSHKeyPairClient()).andReturn(keyClient);
      expect(keyClient.createSSHKeyPair("group-1")).andReturn(pair);

      replay(client, keyClient);

      CreateUniqueKeyPair parser = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() {
            }).toInstance(Suppliers.ofInstance("1"));
            bind(CloudStackClient.class).toInstance(client);
         }

      }).getInstance(CreateUniqueKeyPair.class);

      assertEquals(parser.load("group-1"), pair);

      verify(client, keyClient);
   }

}
