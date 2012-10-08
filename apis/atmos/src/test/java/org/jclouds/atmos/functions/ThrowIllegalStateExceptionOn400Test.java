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
package org.jclouds.atmos.functions;

import org.jclouds.atmos.domain.AtmosError;
import org.jclouds.atmos.util.AtmosUtils;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.testng.annotations.Test;

/**
 * @author Andrei Savu
 */
public class ThrowIllegalStateExceptionOn400Test {

   @Test(expectedExceptions = IllegalStateException.class)
   public void testResourceAlreadyExists() {
      new ThrowIllegalStateExceptionOn400(new AtmosUtils()) {
         @Override
         protected AtmosError parseErrorFromResponse(HttpResponseException ignore) {
            return new AtmosError(1016, "Resource already exists");
         }
      }.apply(new HttpResponseException("Resource already exists", null,
          HttpResponse.builder().statusCode(400).build(), (Throwable) null));

   }

   @Test(expectedExceptions = RuntimeException.class)
   public void testNotFoundPropagates() {
      new ThrowIllegalStateExceptionOn400(new AtmosUtils()).apply(new RuntimeException());
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testNullIsBad() {
      new ThrowIllegalStateExceptionOn400(new AtmosUtils()).apply(null);
   }

}
