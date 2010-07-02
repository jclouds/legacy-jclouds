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
package org.jclouds.gogrid.binders;

import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

/**
 * Tests that id bindings are proper for request
 * 
 * @author Oleksiy Yarmula
 */
public class BindIdsToQueryParamsTest {

   @Test
   public void testBinding() {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      Long[] input = { 123L, 456L };

      BindIdsToQueryParams binder = new BindIdsToQueryParams();

      request.addQueryParam("id", "123");
      request.addQueryParam("id", "456");
      replay(request);

      binder.bindToRequest(request, input);

   }

   @Test
   public void testBinding2() {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      long[] input = { 123L, 456L };

      BindIdsToQueryParams binder = new BindIdsToQueryParams();

      request.addQueryParam("id", "123");
      request.addQueryParam("id", "456");
      replay(request);

      binder.bindToRequest(request, input);

   }
}
