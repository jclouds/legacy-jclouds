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

import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code AttachmentHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AttachmentHandlerTest")
public class AttachmentHandlerTest extends BaseEC2HandlerTest {
   public void testApplyInputStream() {
      DateService dateService = injector.getInstance(DateService.class);
      InputStream is = getClass().getResourceAsStream("/attach.xml");

      Attachment expected = new Attachment(defaultRegion, "vol-4d826724", "i-6058a509", "/dev/sdh",
            Attachment.Status.ATTACHING, dateService.iso8601DateParse("2008-05-07T11:51:50.000Z"));

      AttachmentHandler handler = injector.getInstance(AttachmentHandler.class);
      addDefaultRegionToHandler(handler);
      Attachment result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of());
      replay(request);
      handler.setContext(request);
   }
}
