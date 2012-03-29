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
package org.jclouds.ec2.xml;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Set;

import org.jclouds.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code DescribeAddressesResponseHandler}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeAddressesResponseHandlerTest")
public class DescribeAddressesResponseHandlerTest extends BaseEC2HandlerTest {
   public void testApplyInputStream() throws UnknownHostException {

      InputStream is = getClass().getResourceAsStream("/describe_addresses.xml");

      DescribeAddressesResponseHandler handler = injector
               .getInstance(DescribeAddressesResponseHandler.class);
      addDefaultRegionToHandler(handler);

      Set<PublicIpInstanceIdPair> result = factory.create(handler).parse(is);

      assertEquals(result, ImmutableList.of(new PublicIpInstanceIdPair(defaultRegion,
               "67.202.55.255", "i-f15ebb98"), new PublicIpInstanceIdPair(defaultRegion,
               "67.202.55.233", null)));
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object>of()).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
