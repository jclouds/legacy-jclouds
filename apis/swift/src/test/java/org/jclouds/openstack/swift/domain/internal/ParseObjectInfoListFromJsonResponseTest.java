/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.openstack.swift.domain.internal;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.crypto.CryptoStreams;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.config.GsonModule;
import org.jclouds.json.config.GsonModule.DateAdapter;
import org.jclouds.json.config.GsonModule.Iso8601DateAdapter;
import org.jclouds.openstack.swift.domain.ObjectInfo;
import org.jclouds.openstack.swift.functions.ParseObjectInfoListFromJsonResponse;
import org.jclouds.openstack.swift.options.ListContainerOptions;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseObjectInfoListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseObjectInfoListFromJsonResponseTest {

   Injector i = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bind(DateAdapter.class).to(Iso8601DateAdapter.class);
      }

   }, new GsonModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_container.json");
      Set<ObjectInfo> expects = Sets.newLinkedHashSet();
      ObjectInfoImpl one = i.getInstance(ObjectInfoImpl.class);
      one.name = "test_obj_1";
      one.hash = CryptoStreams.hex("4281c348eaf83e70ddce0e07221c3d28");
      one.bytes = 14l;
      one.content_type = "application/octet-stream";
      one.last_modified = new SimpleDateFormatDateService().iso8601DateParse("2009-02-03T05:26:32.612Z");
      expects.add(one);
      ObjectInfoImpl two = i.getInstance(ObjectInfoImpl.class);
      two.name = ("test_obj_2");
      two.hash = CryptoStreams.hex("b039efe731ad111bc1b0ef221c3849d0");
      two.bytes = (64l);
      two.content_type = ("application/octet-stream");
      two.last_modified = (new SimpleDateFormatDateService().iso8601DateParse("2009-02-03T05:26:32.612Z"));
      expects.add(two);
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      ListContainerOptions options = new ListContainerOptions();
      expect(request.getArgs()).andReturn(
               ImmutableList.<Object> of("containter", new ListContainerOptions[] { options })).atLeastOnce();
      replay(request);
      ParseObjectInfoListFromJsonResponse parser = i.getInstance(ParseObjectInfoListFromJsonResponse.class);
      parser.setContext(request);
      assertEquals(parser.apply(is), expects);
   }
}
