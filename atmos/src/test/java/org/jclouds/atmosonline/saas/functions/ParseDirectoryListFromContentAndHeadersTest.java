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

package org.jclouds.atmosonline.saas.functions;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.atmosonline.saas.domain.BoundedSet;
import org.jclouds.atmosonline.saas.domain.DirectoryEntry;
import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.atmosonline.saas.domain.internal.BoundedLinkedHashSet;
import org.jclouds.atmosonline.saas.reference.AtmosStorageHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code ParseDirectoryListFromContentAndHeaders}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ParseDirectoryListFromContentAndHeadersTest")
public class ParseDirectoryListFromContentAndHeadersTest extends BaseHandlerTest {

   Function<HttpResponse, BoundedSet<DirectoryEntry>> createFn() {
      return injector.getInstance(ParseDirectoryListFromContentAndHeaders.class);
   }

   public void testWithToken() {
      HttpResponse response = new HttpResponse(200, "ok", Payloads.newPayload(getClass().getResourceAsStream(
            "/list_basic.xml")), ImmutableMultimap.of(AtmosStorageHeaders.TOKEN, "token"));

      BoundedSet<DirectoryEntry> result = createFn().apply(response);
      assertEquals(result, new BoundedLinkedHashSet<DirectoryEntry>(values(), "token"));
      assertEquals(result.getToken(), "token");
   }

   public void testWithoutToken() {
      HttpResponse response = new HttpResponse(200, "ok", Payloads.newPayload(getClass().getResourceAsStream(
            "/list_basic.xml")));
      BoundedSet<DirectoryEntry> result = createFn().apply(response);

      assertEquals(result, values());
      assertEquals(result.getToken(), null);
   }

   protected Set<DirectoryEntry> values() {
      Set<DirectoryEntry> expected = Sets.newLinkedHashSet();
      expected.add(new DirectoryEntry("4980cdb2a411106a04a4538c92a1b204ad92077de6e3", FileType.DIRECTORY,
            "adriancole-blobstore-2096685753"));
      expected.add(new DirectoryEntry("4980cdb2a410105404980d99e53a0504ad93939e7dc3", FileType.DIRECTORY,
            "adriancole-blobstore247496608"));
      return expected;
   }
}
