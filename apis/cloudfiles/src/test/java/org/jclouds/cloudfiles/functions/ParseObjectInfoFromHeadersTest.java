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

package org.jclouds.cloudfiles.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertNotNull;

import java.net.URI;

import org.jclouds.Constants;
import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.jclouds.cloudfiles.domain.MutableObjectInfoWithMetadata;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code ParseContainerListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ParseObjectInfoFromHeadersTest {

   Injector i = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bindConstant().annotatedWith(Names.named(BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX)).to("sdf");
         bindConstant().annotatedWith(Names.named(Constants.PROPERTY_API_VERSION)).to("1");
      }

   });

   public void testEtagCaseIssue() {
      ParseObjectInfoFromHeaders parser = i.getInstance(ParseObjectInfoFromHeaders.class);
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of("container", "key")).atLeastOnce();

      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/test")).atLeastOnce();
      replay(request);
      parser.setContext(request);
      HttpResponse response = new HttpResponse(200, "ok", Payloads.newStringPayload(""),
            ImmutableMultimap.<String, String> of("Last-Modified", "Fri, 12 Jun 2007 13:40:18 GMT", "Content-Length",
                  "0", "Etag", "feb1"));

      response.getPayload().getContentMetadata().setContentType("text/plain");
      MutableObjectInfoWithMetadata md = parser.apply(response);
      assertNotNull(md.getHash());
   }
}
