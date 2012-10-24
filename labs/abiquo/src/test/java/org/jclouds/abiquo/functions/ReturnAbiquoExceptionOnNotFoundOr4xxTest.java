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

import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.abiquo.model.transport.error.ErrorsDto;
import com.google.common.base.Function;

/**
 * Unit tests for the {@link ReturnAbiquoExceptionOnNotFoundOr4xx} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "ReturnAbiquoExceptionOnNotFoundOr4xxTest")
public class ReturnAbiquoExceptionOnNotFoundOr4xxTest {
   public void testReturnOriginalExceptionIfNotResourceNotFound() {
      Function<Exception, Object> function = new ReturnAbiquoExceptionOnNotFoundOr4xx();
      RuntimeException exception = new RuntimeException();

      try {
         function.apply(exception);
      } catch (Exception ex) {
         assertEquals(ex, exception);
      }
   }

   public void testReturnOriginalExceptionIfNotAbiquoException() {
      Function<Exception, Object> function = new ReturnAbiquoExceptionOnNotFoundOr4xx();
      ResourceNotFoundException exception = new ResourceNotFoundException();

      try {
         function.apply(exception);
      } catch (Exception ex) {
         assertEquals(ex, exception);
      }
   }

   public void testReturnAbiquoException() {
      Function<Exception, Object> function = new ReturnAbiquoExceptionOnNotFoundOr4xx();
      AbiquoException abiquoException = new AbiquoException(Status.NOT_FOUND, new ErrorsDto());
      ResourceNotFoundException exception = new ResourceNotFoundException(abiquoException);

      try {
         function.apply(exception);
      } catch (Exception ex) {
         assertEquals(ex, abiquoException);
      }
   }
}
