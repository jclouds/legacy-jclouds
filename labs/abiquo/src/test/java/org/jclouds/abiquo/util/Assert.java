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

package org.jclouds.abiquo.util;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.io.Payload;
import org.jclouds.xml.XMLParser;
import org.jclouds.xml.internal.JAXBParser;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.error.ErrorDto;

/**
 * Assertion utilities.
 * 
 * @author Ignasi Barrera
 */
public class Assert {
   /**
    * Assert that the exception contains the given error.
    * 
    * @param exception
    *           The exception.
    * @param expectedHttpStatus
    *           The expected HTTP status code.
    * @param expectedErrorCode
    *           The expected error code.
    */
   public static void assertHasError(final AbiquoException exception, final Status expectedHttpStatus,
         final String expectedErrorCode) {
      assertEquals(exception.getHttpStatus(), expectedHttpStatus);
      ErrorDto error = exception.findError(expectedErrorCode);
      assertNotNull(error);
   }

   /**
    * Assert that the given payload matches the given string.
    * 
    * @param payload
    *           The payload to check.
    * @param expected
    *           The expected string.
    * @param entityClass
    *           The entity class for the payload.
    * @throws IOException
    *            If there is an error during serialization.
    */
   public static void assertPayloadEquals(final Payload payload, final String expected,
         final Class<? extends SingleResourceTransportDto> entityClass) throws IOException {
      // Serialize and deserialize to avoid formatting issues
      XMLParser xml = new JAXBParser("false");
      SingleResourceTransportDto entity = xml.fromXML(expected, entityClass);
      String toMatch = xml.toXML(entity, entityClass);

      assertEquals(payload.getRawContent(), toMatch);
   }
}
