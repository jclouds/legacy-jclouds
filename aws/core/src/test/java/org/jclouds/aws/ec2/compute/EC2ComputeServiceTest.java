/**
 *
 * Copyright (C) 2010 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */

package org.jclouds.aws.ec2.compute;

import static java.lang.String.format;

import org.jclouds.aws.ec2.compute.domain.EC2Size;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.compute.internal.TemplateBuilderImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Tests compute service specifically to EC2.
 * 
 * These tests are designed to verify the local functionality of jclouds, rather than the
 * interaction with Amazon Web Services.
 * 
 * @see EC2ComputeServiceLiveTest
 * 
 * @author Oleksiy Yarmula
 */
public class EC2ComputeServiceTest {

   /**
    * Verifies that {@link TemplateBuilderImpl} would choose the correct size of the instance, based
    * on {@link org.jclouds.compute.domain.Size} from {@link EC2Size}.
    * 
    * Expected size: m2.xlarge
    */
   @Test
   public void testTemplateChoiceForInstanceBySizeId() throws Exception {
      Template template = newTemplateBuilder().architecture(Architecture.X86_64)
               .sizeId("m2.xlarge").locationId("us-east-1").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assert EC2Size.M2_XLARGE.equals(template.getSize()) : format(
               "Incorrect image determined by the template. Expected: %s. Found: %s.", "m2.xlarge",
               String.valueOf(template.getSize()));
   }

   /**
    * Verifies that {@link TemplateBuilderImpl} would choose the correct size of the instance, based
    * on physical attributes (# of cores, ram, etc).
    * 
    * Expected size: m2.xlarge
    */
   @Test
   public void testTemplateChoiceForInstanceByAttributes() throws Exception {
      Template template = newTemplateBuilder().architecture(Architecture.X86_64).minRam(17510)
               .minCores(6.5).smallest().locationId("us-east-1").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assert EC2Size.M2_XLARGE.equals(template.getSize()) : format(
               "Incorrect image determined by the template. Expected: %s. Found: %s.", "m2.xlarge",
               String.valueOf(template.getSize()));
   }

   /**
    * Negative test version of {@link #testTemplateChoiceForInstanceByAttributes}.
    * 
    * Verifies that {@link TemplateBuilderImpl} would not choose the insufficient size of the
    * instance, based on physical attributes (# of cores, ram, etc).
    * 
    * Expected size: anything but m2.xlarge
    */
   @Test
   public void testNegativeTemplateChoiceForInstanceByAttributes() throws Exception {
      Template template = newTemplateBuilder().architecture(Architecture.X86_64).minRam(17510)
               .minCores(6.7).smallest().locationId("us-east-1").build();

      assert template != null : "The returned template was null, but it should have a value.";
      assert !EC2Size.M2_XLARGE.equals(template.getSize()) : format(
               "Incorrect image determined by the template. Expected: not %s. Found: %s.",
               "m2.xlarge", String.valueOf(template.getSize()));
   }

   private TemplateBuilder newTemplateBuilder() {
      Location location = new LocationImpl(LocationScope.REGION, "us-east-1", "us east", null, true);
      Image image = new ImageImpl("ami-image", "image", "us-east-1", null, Maps
               .<String, String> newHashMap(), "description", "1.0", null, "ubuntu",
               Architecture.X86_64);

      return new TemplateBuilderImpl(ImmutableMap.of("us-east-1", location), ImmutableMap.of(
               "ami-image", image), Maps.uniqueIndex(ImmutableSet.of(EC2Size.C1_MEDIUM,
               EC2Size.C1_XLARGE, EC2Size.M1_LARGE, EC2Size.M1_SMALL, EC2Size.M1_XLARGE,
               EC2Size.M2_XLARGE, EC2Size.M2_2XLARGE, EC2Size.M2_4XLARGE), indexer()), location);
   }

   Function<ComputeMetadata, String> indexer() {
      return new Function<ComputeMetadata, String>() {
         @Override
         public String apply(ComputeMetadata from) {
            return from.getId();
         }
      };
   }

}
