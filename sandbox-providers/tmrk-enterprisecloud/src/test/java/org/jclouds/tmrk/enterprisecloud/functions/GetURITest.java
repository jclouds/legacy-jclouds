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
package org.jclouds.tmrk.enterprisecloud.functions;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.assertEquals;

/**
 *
 * @author Jason King
 */
@Test(groups = "unit", testName = "GetURITest")
public class GetURITest {

   private URISource.GetURI function;

   @BeforeMethod
   public void setUp() {
      function = new URISource.GetURI();
   }
   
   public void testApply() {
      URI expected = URI.create("/dev/null");
      URISource source = new TestURISource(expected);
      URI result = function.apply(source);
      assertEquals(result,expected);
   }

   private static class TestURISource implements URISource {

      private URI expected;
      
      public TestURISource(URI expected) {
         this.expected = expected;
      }
      
      @Override
      public URI getURI() {
         return expected;
      }
   }
}
