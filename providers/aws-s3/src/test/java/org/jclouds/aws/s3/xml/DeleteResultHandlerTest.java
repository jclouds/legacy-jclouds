/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.s3.xml;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.jclouds.aws.s3.domain.DeleteResult;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.testng.Assert.assertEquals;

/**
 * @author Andrei Savu
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DeleteResultHandlerTest")
public class DeleteResultHandlerTest extends BaseHandlerTest {

   @Test
   public void test() {
      InputStream is = getClass().getResourceAsStream("/delete-result.xml");

      DeleteResult expected = expected();

      DeleteResultHandler handler = injector.getInstance(DeleteResultHandler.class);
      DeleteResult result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   private DeleteResult expected() {
      return DeleteResult.builder()
         .add("key1")
         .add("key1.1")
         .putError("key2", new DeleteResult.Error("AccessDenied", "Access Denied"))
         .build();
   }
}
