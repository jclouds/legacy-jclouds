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
package org.jclouds.cloudstack.predicates;

import static org.jclouds.cloudstack.predicates.TemplatePredicates.isPasswordEnabled;
import static org.jclouds.cloudstack.predicates.TemplatePredicates.isReady;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.jclouds.cloudstack.domain.Template;
import org.testng.annotations.Test;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class TemplatePredicatesTest {

   @Test
   public void testTemplateIsReady() {
      assertTrue(isReady().apply(
         Template.builder().id("a").ready(true).build()
      ));
      assertFalse(isReady().apply(
         Template.builder().id("b").ready(false).build()
      ));
   }

   @Test
   public void testTemplateIsPasswordEnabled() {
      assertTrue(isPasswordEnabled().apply(
         Template.builder().id("anid").passwordEnabled(true).build()
      ));
      assertFalse(isPasswordEnabled().apply(
         Template.builder().id("someid").passwordEnabled(false).build()
      ));
   }
}
