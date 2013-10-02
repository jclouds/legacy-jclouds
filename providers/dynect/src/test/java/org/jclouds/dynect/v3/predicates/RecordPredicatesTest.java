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
package org.jclouds.dynect.v3.predicates;

import static org.jclouds.dynect.v3.domain.RecordId.recordIdBuilder;
import static org.jclouds.dynect.v3.predicates.RecordPredicates.typeEquals;

import org.jclouds.dynect.v3.domain.RecordId;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RecordPredicatesTest {
   RecordId recordId = recordIdBuilder()
                               .zone("adrianc.zone.dynecttest.jclouds.org")
                               .fqdn("adrianc.zone.dynecttest.jclouds.org")
                               .type("SOA")
                               .id(50976579l).build();

   @Test
   public void testTypeEqualsWhenEqual() {
      assert typeEquals("SOA").apply(recordId);
   }

   @Test
   public void testTypeEqualsWhenNotEqual() {
      assert !typeEquals("NS").apply(recordId);
   }
}
