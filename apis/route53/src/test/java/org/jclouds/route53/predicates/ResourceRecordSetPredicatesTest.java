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
package org.jclouds.route53.predicates;

import static org.jclouds.route53.predicates.ResourceRecordSetPredicates.typeEquals;

import org.jclouds.route53.domain.ResourceRecordSet;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ResourceRecordSetPredicatesTest")
public class ResourceRecordSetPredicatesTest {
   ResourceRecordSet rrs = ResourceRecordSet.builder().name("jclouds.org.").type("NS").add("ns-119.awsdns-14.com.")
         .build();

   @Test
   public void testTypeEqualsWhenEqual() {
      assert typeEquals("NS").apply(rrs);
   }

   @Test
   public void testTypeEqualsWhenNotEqual() {
      assert !typeEquals("AAAA").apply(rrs);
   }
}
