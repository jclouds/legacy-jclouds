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
package org.jclouds.vcloud.hostingdotcom.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.domain.ResourceAllocation;
import org.jclouds.vcloud.domain.VAppStatus;
import org.jclouds.vcloud.hostingdotcom.domain.HostingDotComVApp;
import org.jclouds.vcloud.hostingdotcom.domain.internal.HostingDotComVAppImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code HostingDotComVAppHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.HostingDotComVAppHandlerTest")
public class HostingDotComVAppHandlerTest extends BaseHandlerTest {

   public void testApplyInputStream() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/hostingdotcom/instantiatevapp.xml");

      HostingDotComVApp result = (HostingDotComVApp) factory.create(
               injector.getInstance(HostingDotComVAppHandler.class)).parse(is);

      HostingDotComVApp expects = new HostingDotComVAppImpl("188849-33", "188849-33", URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/vapp/188849-33"),
               VAppStatus.TODO_I_DONT_KNOW, ImmutableListMultimap.<String, InetAddress> of(), null,
               null, ImmutableSortedSet.<ResourceAllocation> of(), "root", "meatisyummy");

      assertEquals(result, expects);
   }
}
