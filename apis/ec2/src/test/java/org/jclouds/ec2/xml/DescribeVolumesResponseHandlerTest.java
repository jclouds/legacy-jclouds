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
import java.util.Set;

import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code DescribeVolumesResponseHandler}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeVolumesResponseHandlerTest")
public class DescribeVolumesResponseHandlerTest extends BaseEC2HandlerTest {

   public void testApplyInputStream() {
      DateService dateService = injector.getInstance(DateService.class);
      InputStream is = getClass().getResourceAsStream("/describe_volumes.xml");

      Set<Volume> expected = Sets.newLinkedHashSet();
      expected.add(new Volume(defaultRegion, "vol-2a21e543", 1, null, "us-east-1a",
               Volume.Status.AVAILABLE, dateService.iso8601DateParse("2009-12-28T05:42:53.000Z"),
               Sets.<Attachment> newLinkedHashSet()));
      expected.add(new Volume(defaultRegion, "vol-4282672b", 800, "snap-536d1b3a",
               "us-east-1a", Volume.Status.IN_USE, dateService
                        .iso8601DateParse("2008-05-07T11:51:50.000Z"), Sets
                        .<Attachment> newHashSet(new Attachment(defaultRegion, "vol-4282672b", "i-6058a509",
                                 "/dev/sdh", Attachment.Status.ATTACHED, dateService
                                          .iso8601DateParse("2008-05-07T12:51:50.000Z")))));

      DescribeVolumesResponseHandler handler = injector
               .getInstance(DescribeVolumesResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Set<Volume> result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object>of()).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
