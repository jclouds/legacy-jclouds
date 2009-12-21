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
package org.jclouds.vcloud.terremark.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.SortedSet;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.vcloud.terremark.domain.IpAddress;
import org.jclouds.vcloud.terremark.domain.IpAddress.Status;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code IpAddressesHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.IpAddressesHandlerTest")
public class IpAddressesHandlerTest extends BaseHandlerTest {

   public void test1() throws UnknownHostException {
      InputStream is = getClass().getResourceAsStream("/terremark/ipAddresses.xml");

      SortedSet<IpAddress> result = factory.create(injector.getInstance(IpAddressesHandler.class))
               .parse(is);
      assertEquals(result, ImmutableSortedSet.of(new IpAddress(InetAddress
               .getByName("10.114.34.131"), Status.ASSIGNED, "testforjcloud2"), new IpAddress(
               InetAddress.getByName("10.114.34.132"), Status.AVAILABLE, null)));
   }
}
