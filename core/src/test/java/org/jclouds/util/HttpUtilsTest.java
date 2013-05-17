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
package org.jclouds.util;

import static com.google.common.base.Predicates.equalTo;
import static org.jclouds.http.HttpUtils.returnValueOnCodeOrNull;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.testng.annotations.Test;

/**
* 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class HttpUtilsTest  {

   public void test404() {
      Exception from = new HttpResponseException("message", null, HttpResponse.builder().statusCode(404).message("not found").build());
      assertEquals(returnValueOnCodeOrNull(from, true, equalTo(404)), Boolean.TRUE);
   }

   public void testNullResponse() {
      Exception from = new HttpResponseException("message", null, null);
      assertEquals(returnValueOnCodeOrNull(from, true, equalTo(404)), null);
   }
}
