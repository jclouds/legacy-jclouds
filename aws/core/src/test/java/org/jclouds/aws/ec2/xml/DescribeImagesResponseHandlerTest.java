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
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.aws.ec2.domain.Image;
import org.jclouds.aws.ec2.domain.Image.Architecture;
import org.jclouds.aws.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.aws.ec2.domain.Image.ImageState;
import org.jclouds.aws.ec2.domain.Image.ImageType;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code DescribeImagesResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeImagesResponseHandlerTest")
public class DescribeImagesResponseHandlerTest extends BaseHandlerTest {

   public void testUNIX() {
      InputStream is = getClass().getResourceAsStream("/ec2/describe_images.xml");
      SortedSet<Image> contents = Sets.newTreeSet();

      contents.add(new Image(Architecture.I386, null, null, "ami-be3adfd7",
               "ec2-public-images/fedora-8-i386-base-v1.04.manifest.xml", "206029621532",
               ImageState.AVAILABLE, ImageType.MACHINE, false,
               Sets.<String> newHashSet("9961934F"), "aki-4438dd2d", null, "ari-4538dd2c",
               Image.RootDeviceType.INSTANCE_STORE, null, ImmutableMap
                        .<String, EbsBlockDevice> of()));

      Set<Image> result = factory.create(injector.getInstance(DescribeImagesResponseHandler.class))
               .parse(is);

      assertEquals(result, contents);
   }

   public void testWindows() {
      InputStream is = getClass().getResourceAsStream("/ec2/describe_images_windows.xml");
      SortedSet<Image> contents = Sets.newTreeSet();

      contents.add(new Image(Architecture.X86_64, null, null, "ami-02eb086b",
               "aws-solutions-amis/SqlSvrStd2003r2-x86_64-Win_SFWBasic5.1-v1.0.manifest.xml",
               "771350841976", ImageState.AVAILABLE, ImageType.MACHINE, true, Sets
                        .<String> newHashSet("5771E9A6"), null, "windows", null,
               Image.RootDeviceType.INSTANCE_STORE, null, ImmutableMap
                        .<String, EbsBlockDevice> of()));

      Set<Image> result = factory.create(injector.getInstance(DescribeImagesResponseHandler.class))
               .parse(is);

      assertEquals(result, contents);
   }

   public void testEBS() {
      InputStream is = getClass().getResourceAsStream("/ec2/describe_images_ebs.xml");
      SortedSet<Image> contents = Sets.newTreeSet();

      contents.add(new Image(Architecture.I386, "websrv_2009-12-10", "Web Server AMI",
               "ami-246f8d4d", "706093390852/websrv_2009-12-10", "706093390852",
               ImageState.AVAILABLE, ImageType.MACHINE, true, Sets.<String> newHashSet(), null,
               "windows", null, Image.RootDeviceType.EBS, "/dev/sda1", ImmutableMap
                        .<String, EbsBlockDevice> of("/dev/sda1", new EbsBlockDevice(
                                 "snap-d01272b9", 30, true), "xvdf", new EbsBlockDevice(
                                 "snap-d31272ba", 250, false))));

      Set<Image> result = factory.create(injector.getInstance(DescribeImagesResponseHandler.class))
               .parse(is);

      assertEquals(result, contents);
   }
}
