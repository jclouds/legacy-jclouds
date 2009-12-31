/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.xml;

import static org.easymock.classextension.EasyMock.*;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.aws.ec2.domain.Attachment;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code AttachmentHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.AttachmentHandlerTest")
public class AttachmentHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {
      DateService dateService = injector.getInstance(DateService.class);
      InputStream is = getClass().getResourceAsStream("/ec2/attach.xml");

      Attachment expected = new Attachment(Region.DEFAULT, "vol-4d826724", "i-6058a509",
               "/dev/sdh", Attachment.Status.ATTACHING, dateService
                        .iso8601DateParse("2008-05-07T11:51:50.000Z"));

      AttachmentHandler handler = injector.getInstance(AttachmentHandler.class);
      addDefaultRegionToHandler(handler);
      Attachment result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(new Object[] { Region.DEFAULT });
      replay(request);
      handler.setContext(request);
   }
}