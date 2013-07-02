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
package org.jclouds.azureblob.xml;

import org.jclouds.azureblob.domain.BlobBlockProperties;
import org.jclouds.azureblob.domain.ListBlobBlocksResponse;
import org.jclouds.azureblob.domain.internal.BlobBlockPropertiesImpl;
import org.jclouds.azureblob.domain.internal.ListBlobBlocksResponseImpl;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Test XML Parsing of the Blob Block List
 */
@Test(groups = "unit", testName = "BlobBlocksResultsHandlerTest")
public class BlobBlocksResultsHandlerTest extends BaseHandlerTest {

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
   }

   public void testGetResult() throws Exception {
      InputStream is = getClass().getResourceAsStream("/test_list_blob_blocks.xml");

      List<BlobBlockProperties> blocks = new LinkedList<BlobBlockProperties>();
      blocks.add(new BlobBlockPropertiesImpl("blockIdA", 1234, true));
      blocks.add(new BlobBlockPropertiesImpl("blockIdB", 4321, true));
      blocks.add(new BlobBlockPropertiesImpl("blockIdC", 5678, false));
      blocks.add(new BlobBlockPropertiesImpl("blockIdD", 8765, false));
      ListBlobBlocksResponse expected = new ListBlobBlocksResponseImpl(blocks);

      ListBlobBlocksResponse result = factory.create(
            injector.getInstance(BlobBlocksResultsHandler.class)).parse(is);

      assertEquals(expected, result);
   }
}
