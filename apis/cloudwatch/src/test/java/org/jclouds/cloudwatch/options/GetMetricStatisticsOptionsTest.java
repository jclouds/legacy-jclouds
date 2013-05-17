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
package org.jclouds.cloudwatch.options;

import static org.jclouds.cloudwatch.options.GetMetricStatisticsOptions.Builder.instanceId;
import static org.jclouds.cloudwatch.options.GetMetricStatisticsOptions.Builder.unit;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudwatch.domain.Unit;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code GetMetricStatisticsOptions}
 *
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class GetMetricStatisticsOptionsTest {

   public void testInstanceId() {
      GetMetricStatisticsOptions options = new GetMetricStatisticsOptions().instanceId("us-east-1/i-123");
      assertEquals(ImmutableSet.of("InstanceId"), options.buildFormParameters().get("Dimensions.member.1.Name"));
      assertEquals(ImmutableSet.of("i-123"), options.buildFormParameters().get("Dimensions.member.1.Value"));
   }

   public void testInstanceIdStatic() {
      GetMetricStatisticsOptions options = instanceId("us-east-1/i-123");
      assertEquals(ImmutableSet.of("InstanceId"), options.buildFormParameters().get("Dimensions.member.1.Name"));
      assertEquals(ImmutableSet.of("i-123"), options.buildFormParameters().get("Dimensions.member.1.Value"));
   }

   public void testUnit() {
      GetMetricStatisticsOptions options = new GetMetricStatisticsOptions().unit(Unit.GIGABYTES_PER_SECOND);
      assertEquals(ImmutableSet.of("Gigabytes/Second"), options.buildFormParameters().get("Unit"));
   }

   public void testUnitStatic() {
      GetMetricStatisticsOptions options = unit(Unit.GIGABYTES_PER_SECOND);
      assertEquals(ImmutableSet.of("Gigabytes/Second"), options.buildFormParameters().get("Unit"));
   }
}
