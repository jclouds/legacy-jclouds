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

package org.jclouds.cloudsigma.binders;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.io.IOException;
import java.util.List;

import org.jclouds.cloudsigma.options.CloneDriveOptions;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class BindCloneDriveOptionsToPlainTextStringTest {

   private static final BindCloneDriveOptionsToPlainTextString binder = Guice.createInjector().getInstance(
         BindCloneDriveOptionsToPlainTextString.class);

   public void testDefault() throws IOException {
      assertInputAndArgsCreatesPayload(ImmutableMap.of("name", "newdrive"), ImmutableList.<Object> of(),
            "name newdrive");
   }

   public void testWithSize() throws IOException {
      assertInputAndArgsCreatesPayload(ImmutableMap.of("name", "newdrive"),
            ImmutableList.<Object> of(new CloneDriveOptions().size(1024)), "name newdrive\nsize 1024");
   }

   protected void assertInputAndArgsCreatesPayload(ImmutableMap<String, String> inputMap, List<Object> args,
         String expected) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(args).atLeastOnce();
      request.setPayload(expected);
      Payload payload = createMock(Payload.class);
      expect(request.getPayload()).andReturn(payload);
      MutableContentMetadata md = createMock(MutableContentMetadata.class);
      expect(payload.getContentMetadata()).andReturn(md);
      md.setContentType("text/plain");

      replay(request);
      replay(payload);
      replay(md);

      binder.bindToRequest(request, inputMap);

      verify(request);
      verify(payload);
      verify(md);
   }

}