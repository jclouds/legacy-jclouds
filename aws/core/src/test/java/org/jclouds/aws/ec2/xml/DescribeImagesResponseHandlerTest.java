/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.SortedSet;

import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.Image.Architecture;
import org.jclouds.aws.ec2.domain.Image.ImageState;
import org.jclouds.aws.ec2.domain.Image.ImageType;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code DescribeImagesResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeImagesResponseHandlerTest")
public class DescribeImagesResponseHandlerTest extends BaseHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/ec2/describe_images.xml");
      SortedSet<Image> contents = Sets.newTreeSet();

      contents.add(new Image(Architecture.I386, "ami-be3adfd7",
               "ec2-public-images/fedora-8-i386-base-v1.04.manifest.xml", "206029621532",
               ImageState.AVAILABLE, ImageType.MACHINE, false, "aki-4438dd2d", null, Sets
                        .<String> newHashSet(), "ari-4538dd2c"));

      SortedSet<Image> result = (SortedSet<Image>) factory.create(
               injector.getInstance(DescribeImagesResponseHandler.class)).parse(is);

      assertEquals(result, contents);
   }
}
