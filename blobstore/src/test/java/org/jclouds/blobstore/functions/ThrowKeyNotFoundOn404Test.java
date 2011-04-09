/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.blobstore.functions;

import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ThrowKeyNotFoundOn404Test {
   ThrowKeyNotFoundOn404 fn = new ThrowKeyNotFoundOn404();

   @Test(expectedExceptions = { KeyNotFoundException.class })
   public void testFound404ThrowsKeyNotFound() throws SecurityException, NoSuchMethodException {
      HttpResponse response = new HttpResponse(404, null, null);
      HttpResponseException exception = new HttpResponseException(null, null, response);
      fn.apply(exception);
   }

   @Test(expectedExceptions = { HttpResponseException.class })
   public void testNotFound404PropagatesHttpResponseException() throws SecurityException, NoSuchMethodException {
      HttpResponse response = new HttpResponse(409, null, null);
      HttpResponseException exception = new HttpResponseException(null, null, response);
      fn.apply(exception);
   }

   @Test(expectedExceptions = { RuntimeException.class })
   public void testNotFoundPropagates() throws SecurityException, NoSuchMethodException {
      fn.apply(new RuntimeException());
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      fn.apply(null);
   }
}
