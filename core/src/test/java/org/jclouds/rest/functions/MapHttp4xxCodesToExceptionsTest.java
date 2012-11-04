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
package org.jclouds.rest.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class MapHttp4xxCodesToExceptionsTest {

   @Test
   public void test401And403ToAuthorizationException() {
      assertCodeMakes(401, AuthorizationException.class);
      assertCodeMakes(403, AuthorizationException.class);
   }

   @Test
   public void test404ToResourceNotFoundException() {
      assertCodeMakes(404, ResourceNotFoundException.class);
   }

   @Test
   public void test409ToIllegalStateException() {
      assertCodeMakes(409, IllegalStateException.class);
   }

   private void assertCodeMakes(int statuscode, Class<?> expected) {
      Function<Exception, Object> function = new MapHttp4xxCodesToExceptions();
      HttpResponseException responseException = createMock(HttpResponseException.class);

      HttpResponse response = createMock(HttpResponse.class);
      expect(response.getStatusCode()).andReturn(statuscode).atLeastOnce();
      expect(responseException.getResponse()).andReturn(response).atLeastOnce();

      replay(responseException);
      replay(response);

      try {
         function.apply(responseException);
         fail();
      } catch (Exception e) {
         assertEquals(e.getClass(), expected);
      }

      verify(responseException);
      verify(response);
   }

}
