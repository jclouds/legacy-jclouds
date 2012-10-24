/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.easymock.EasyMock;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/**
 * Unit tests for the {@link ReturnFalseIfNotAvailable} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "ReturnFalseIfNotAvailableTest")
public class ReturnFalseIfNotAvailableTest {
   public void testReturnOriginalExceptionIfUnknownException() {
      Function<Exception, Object> function = new ReturnFalseIfNotAvailable();
      RuntimeException exception = new RuntimeException();

      try {
         function.apply(exception);
      } catch (Exception ex) {
         assertEquals(ex, exception);
      }
   }

   public void testReturnFalseIf5xx() {
      Function<Exception, Object> function = new ReturnFalseIfNotAvailable();
      HttpResponse response = EasyMock.createMock(HttpResponse.class);
      HttpResponseException exception = EasyMock.createMock(HttpResponseException.class);

      // Status code is called twice
      expect(response.getStatusCode()).andReturn(503);
      expect(response.getStatusCode()).andReturn(503);
      // Get response gets called twice
      expect(exception.getResponse()).andReturn(response);
      expect(exception.getResponse()).andReturn(response);
      // Get cause is called to determine the root cause
      expect(exception.getCause()).andReturn(null);

      replay(response);
      replay(exception);

      assertEquals(function.apply(exception), false);

      verify(response);
      verify(exception);
   }

   public void testReturnExceptionIfNot5xx() {
      Function<Exception, Object> function = new ReturnFalseIfNotAvailable();
      HttpResponse response = EasyMock.createMock(HttpResponse.class);
      HttpResponseException exception = EasyMock.createMock(HttpResponseException.class);

      // Status code is called twice
      expect(response.getStatusCode()).andReturn(600);
      expect(response.getStatusCode()).andReturn(600);
      // Get response gets called twice
      expect(exception.getResponse()).andReturn(response);
      expect(exception.getResponse()).andReturn(response);
      // Get cause is called to determine the root cause
      expect(exception.getCause()).andReturn(null);

      replay(response);
      replay(exception);

      try {
         function.apply(exception);
      } catch (Exception ex) {
         assertEquals(ex, exception);
      }

      verify(response);
      verify(exception);
   }

   public void testReturnFalseIfResourceNotFound() {
      Function<Exception, Object> function = new ReturnFalseIfNotAvailable();
      ResourceNotFoundException exception = new ResourceNotFoundException();

      assertEquals(function.apply(exception), false);
   }

}
