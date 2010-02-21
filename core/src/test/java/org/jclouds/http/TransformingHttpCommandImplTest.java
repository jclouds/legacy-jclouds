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
package org.jclouds.http;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Collections;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.RuntimeDelegate;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RuntimeDelegateImpl;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class TransformingHttpCommandImplTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testChangeSchemeHostAndPortTo() {
      RuntimeDelegate.setInstance(new RuntimeDelegateImpl());

      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      TransformingHttpCommandImpl<?> command = new TransformingHttpCommandImpl(null, request,
               null);
      expect(request.getEndpoint()).andReturn(URI.create("http://localhost/mypath"));
      request.setEndpoint(URI.create("https://remotehost:443/mypath"));
      Multimap<String, String> headers = HashMultimap.create();
      expect(request.getHeaders()).andReturn(headers);
      replay(request);
      command.changeSchemeHostAndPortTo("https", "remotehost", 443);
      assertEquals(headers.get(HttpHeaders.HOST), Collections.singletonList("remotehost"));

   }

   @Test
   public void testTransformingHttpCommandImpl() {
      // TODO
   }

   @Test
   public void testExecute() {
      // TODO
   }

   @Test
   public void testGetFailureCount() {
      // TODO
   }

   @Test
   public void testIncrementFailureCount() {
      // TODO
   }

   @Test
   public void testSetCurrentEndpoint() {
      // TODO
   }

   @Test
   public void testGetCurrentEndpoint() {
      // TODO
   }

   @Test
   public void testSetException() {
      // TODO
   }

   @Test
   public void testGetException() {
      // TODO
   }

   @Test
   public void testIncrementRedirectCount() {
      // TODO
   }

   @Test
   public void testGetRedirectCount() {
      // TODO
   }

   @Test
   public void testIsReplayable() {
      // TODO
   }

   @Test
   public void testBuildRequest() {
      // TODO
   }

   @Test
   public void testSetCurrentMethod() {
      // TODO
   }

}
