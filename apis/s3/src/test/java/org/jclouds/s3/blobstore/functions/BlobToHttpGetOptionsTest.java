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
package org.jclouds.s3.blobstore.functions;
import static org.testng.Assert.assertEquals;

import org.jclouds.blobstore.functions.BlobToHttpGetOptions;
import org.jclouds.http.options.GetOptions;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

@Test(groups = "unit")
public class BlobToHttpGetOptionsTest {

   @Test
   void testOneRange() {
      BlobToHttpGetOptions converter = new BlobToHttpGetOptions();
      org.jclouds.blobstore.options.GetOptions blobGet = new org.jclouds.blobstore.options.GetOptions()
            .range(2, 5);
      GetOptions httpGet = converter.apply(blobGet);
      assertEquals(httpGet.buildRequestHeaders().get("Range"), ImmutableSet
            .of("bytes=2-5"));

   }
}
