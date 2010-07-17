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
package org.jclouds.rackspace.cloudfiles.domain.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudfiles.domain.ObjectInfo;
import org.jclouds.rackspace.cloudfiles.functions.ParseObjectInfoListFromJsonResponse;
import org.jclouds.rackspace.cloudfiles.options.ListContainerOptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseObjectInfoListFromJsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.ParseObjectInfoListFromJsonResponseTest")
public class ParseObjectInfoListFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         super.configure();
      }

   });

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream(
            "/cloudfiles/test_list_container.json");
      Set<ObjectInfo> expects = Sets.newHashSet();
      ObjectInfoImpl one = i.getInstance(ObjectInfoImpl.class);
      one.name = "test_obj_1";
      one.hash = i.getInstance(EncryptionService.class).fromHex(
            "4281c348eaf83e70ddce0e07221c3d28");
      one.bytes = 14l;
      one.content_type = "application/octet-stream";
      one.last_modified = new SimpleDateFormatDateService()
            .iso8601DateParse("2009-02-03T05:26:32.612Z");
      expects.add(one);
      ObjectInfoImpl two = i.getInstance(ObjectInfoImpl.class);
      two.name = ("test_obj_2");
      two.hash = (i.getInstance(EncryptionService.class)
            .fromHex("b039efe731ad111bc1b0ef221c3849d0"));
      two.bytes = (64l);
      two.content_type = ("application/octet-stream");
      two.last_modified = (new SimpleDateFormatDateService()
            .iso8601DateParse("2009-02-03T05:26:32.612Z"));
      expects.add(two);
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      ListContainerOptions options = new ListContainerOptions();
      expect(request.getArgs())
            .andReturn(
                  new Object[] { "containter",
                        new ListContainerOptions[] { options } }).atLeastOnce();
      replay(request);
      ParseObjectInfoListFromJsonResponse parser = i
            .getInstance(ParseObjectInfoListFromJsonResponse.class);
      parser.setContext(request);
      assertEquals(parser.apply(is), expects);
   }
}
