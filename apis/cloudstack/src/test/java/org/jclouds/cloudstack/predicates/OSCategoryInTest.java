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

package org.jclouds.cloudstack.predicates;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.OSType;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.features.GuestOSClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit",  singleThreaded=true)
public class OSCategoryInTest {

   private CloudStackClient client;
   private GuestOSClient guestOSClient;
   private Set<String> acceptableCategories = ImmutableSet.<String>of("Ubuntu");

   @BeforeMethod
   public void setUp() {
      client = createMock(CloudStackClient.class);
      guestOSClient = createMock(GuestOSClient.class);

      expect(client.getGuestOSClient()).andReturn(guestOSClient).times(2);

      Map<Long, String> osCategories = Maps.newHashMap();
      osCategories.put(1L, "Ubuntu");
      osCategories.put(2L, "CentOS");
      osCategories.put(3L, "RHEL");

      expect(guestOSClient.listOSCategories()).andReturn(osCategories);

      Set<OSType> osTypes = ImmutableSet.of(
         OSType.builder().id(10L).OSCategoryId(1).description("Ubuntu 10.04 LTS").build(),
         OSType.builder().id(20L).OSCategoryId(2).description("CentOS 5.4").build(),
         OSType.builder().id(30L).OSCategoryId(3).description("RHEL 6").build()
      );

      expect(guestOSClient.listOSTypes()).andReturn(osTypes);
      replay(client, guestOSClient);
   }

   @Test
   public void testTemplateInAcceptableCategory() {
      assertTrue(new OSCategoryIn(client).apply(acceptableCategories).apply(
         Template.builder().OSTypeId(10L).build()
      ));
      verify(client, guestOSClient);
   }

   @Test
   public void testTemplateNotInAcceptableCategory() {
      assertFalse(new OSCategoryIn(client).apply(acceptableCategories).apply(
         Template.builder().OSTypeId(30L).build()
      ));
      verify(client, guestOSClient);
   }
}
