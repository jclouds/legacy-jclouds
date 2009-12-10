/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rimuhosting.miro.servers;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jclouds.compute.domain.LoginType;
import org.jclouds.domain.Credentials;
import org.jclouds.rimuhosting.miro.data.CreateOptions;
import org.jclouds.rimuhosting.miro.data.NewServerData;
import org.jclouds.rimuhosting.miro.domain.IpAddresses;
import org.jclouds.rimuhosting.miro.domain.NewServerResponse;
import org.jclouds.rimuhosting.miro.domain.Server;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "rimuhosting.RimuHostingCreateServerResponse")
public class RimuHostingCreateServerResponseTest {

   public void testParseInetAddress() throws UnknownHostException {

      Server rhServer = createMock(Server.class);
      IpAddresses addresses = createMock(IpAddresses.class);
      expect(rhServer.getIpAddresses()).andReturn(addresses).atLeastOnce();
      expect(addresses.getPrimaryIp()).andReturn("127.0.0.1");
      expect(addresses.getSecondaryIps()).andReturn(ImmutableSortedSet.of("www.yahoo.com"));
      replay(rhServer);
      replay(addresses);

      assertEquals(Sets.newLinkedHashSet(RimuHostingCreateServerResponse
               .getPublicAddresses(rhServer)), ImmutableSet.of(InetAddress.getByName("127.0.0.1"),
               InetAddress.getByName("www.yahoo.com")));
   }

   public void test() throws UnknownHostException {

      NewServerResponse nsResponse = createMock(NewServerResponse.class);
      Server rhServer = createMock(Server.class);

      expect(nsResponse.getServer()).andReturn(rhServer).atLeastOnce();

      expect(rhServer.getId()).andReturn(new Long(1));
      expect(rhServer.getName()).andReturn("name");

      IpAddresses addresses = createMock(IpAddresses.class);
      expect(rhServer.getIpAddresses()).andReturn(addresses).atLeastOnce();

      expect(addresses.getPrimaryIp()).andReturn("127.0.0.1");
      expect(addresses.getSecondaryIps()).andReturn(ImmutableSortedSet.<String> of());

      NewServerData data = createMock(NewServerData.class);

      expect(nsResponse.getNewInstanceRequest()).andReturn(data).atLeastOnce();
      CreateOptions options = createMock(CreateOptions.class);
      expect(data.getCreateOptions()).andReturn(options);
      expect(options.getPassword()).andReturn("password");

      replay(nsResponse);
      replay(rhServer);
      replay(addresses);
      replay(data);
      replay(options);

      RimuHostingCreateServerResponse response = new RimuHostingCreateServerResponse(nsResponse);
      assertEquals(response.getId(), "1");
      assertEquals(response.getName(), "name");
      assertEquals(response.getPublicAddresses(), ImmutableSet.<InetAddress> of(InetAddress
               .getByName("127.0.0.1")));
      assertEquals(response.getPrivateAddresses(), ImmutableSet.<InetAddress> of());
      assertEquals(response.getLoginPort(), 22);
      assertEquals(response.getLoginType(), LoginType.SSH);
      assertEquals(response.getCredentials(), new Credentials("root", "password"));

   }
}
