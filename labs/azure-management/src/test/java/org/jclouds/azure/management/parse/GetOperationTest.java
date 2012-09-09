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
package org.jclouds.azure.management.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.azure.management.domain.Error;
import org.jclouds.azure.management.domain.Error.Code;
import org.jclouds.azure.management.domain.Operation;
import org.jclouds.azure.management.domain.Operation.Status;
import org.jclouds.azure.management.xml.OperationHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetOperationTest")
public class GetOperationTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/operation.xml");

      Operation expected = expected();

      OperationHandler handler = injector.getInstance(OperationHandler.class);
      Operation result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public Operation expected() {
      return Operation.builder()
                      .id("request-id")
                      .rawStatus("Failed")
                      .status(Status.FAILED)
                      .httpStatusCode(400)
                      .error(Error.builder()
                                  .rawCode("MissingOrInvalidRequiredQueryParameter")
                                  .code(Code.MISSING_OR_INVALID_REQUIRED_QUERY_PARAMETER)
                                  .message("A required query parameter was not specified for this request or was specified incorrectly.")
                                  .build())
                      .build();
   }
}
