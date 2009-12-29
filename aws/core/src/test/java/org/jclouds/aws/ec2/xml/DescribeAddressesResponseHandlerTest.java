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
package org.jclouds.aws.ec2.xml;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import org.jclouds.aws.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code DescribeAddressesResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeAddressesResponseHandlerTest")
public class DescribeAddressesResponseHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() throws UnknownHostException {

      InputStream is = getClass().getResourceAsStream("/ec2/describe_addresses.xml");

      DescribeAddressesResponseHandler handler = injector
               .getInstance(DescribeAddressesResponseHandler.class);
      addDefaultRegionToHandler(handler);

      Set<PublicIpInstanceIdPair> result = factory.create(handler).parse(is);

      assertEquals(result, ImmutableList.of(new PublicIpInstanceIdPair(Region.DEFAULT, InetAddress
               .getByName("67.202.55.255"), "i-f15ebb98"), new PublicIpInstanceIdPair(
               Region.DEFAULT, InetAddress.getByName("67.202.55.233"), null)));
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(new Object[] { Region.DEFAULT }).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
