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

package org.jclouds.abiquo.fallbacks;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.AbiquoFallbacks.PropagateAbiquoExceptionOnNotFoundOr4xx;
import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.abiquo.model.transport.error.ErrorsDto;

/**
 * Unit tests for the {@link PropagateAbiquoExceptionOnNotFoundOr4xx} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "PropagateAbiquoExceptionOnNotFoundOr4xxTest")
public class PropagateAbiquoExceptionOnNotFoundOr4xxTest {
   public void testOriginalExceptionIfNotResourceNotFound() {
      PropagateAbiquoExceptionOnNotFoundOr4xx function = new PropagateAbiquoExceptionOnNotFoundOr4xx();
      RuntimeException exception = new RuntimeException();

      try {
         function.create(exception);
      } catch (Exception ex) {
         assertEquals(ex, exception);
      }
   }

   public void testOriginalExceptionIfNotAbiquoException() {
      PropagateAbiquoExceptionOnNotFoundOr4xx function = new PropagateAbiquoExceptionOnNotFoundOr4xx();
      ResourceNotFoundException exception = new ResourceNotFoundException();

      try {
         function.create(exception);
      } catch (Exception ex) {
         assertEquals(ex, exception);
      }
   }

   public void testAbiquoException() {
      PropagateAbiquoExceptionOnNotFoundOr4xx function = new PropagateAbiquoExceptionOnNotFoundOr4xx();
      AbiquoException abiquoException = new AbiquoException(Status.NOT_FOUND, new ErrorsDto());
      ResourceNotFoundException exception = new ResourceNotFoundException(abiquoException);

      try {
         function.create(exception);
      } catch (Exception ex) {
         assertEquals(ex, abiquoException);
      }
   }
}
