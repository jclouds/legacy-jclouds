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
package org.jclouds.gogrid.compute.options;

import static org.testng.Assert.assertEquals;

import org.jclouds.compute.options.TemplateOptions;
import org.testng.annotations.Test;

/**
 * Tests possible uses of {@code GoGridTemplateOptions} and {@code GoGridTemplateOptions.Builder.*}.
 * 
 * @author Andrew Kennedy
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GoGridTemplateOptionsTest")
public class GoGridTemplateOptionsTest {
   @Test
   public void testAs() {
      TemplateOptions options = new GoGridTemplateOptions();
      assertEquals(options.as(GoGridTemplateOptions.class), options);
   }
}
