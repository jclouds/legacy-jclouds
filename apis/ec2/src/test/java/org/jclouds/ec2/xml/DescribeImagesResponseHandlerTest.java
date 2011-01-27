/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.ec2.compute.functions.EC2ImageParserTest;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.Image.Architecture;
import org.jclouds.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.ec2.domain.Image.ImageState;
import org.jclouds.ec2.domain.Image.ImageType;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.location.Region;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code DescribeImagesResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class DescribeImagesResponseHandlerTest {

   public void testUNIX() {
      Set<Image> contents = ImmutableSet.of(new Image("us-east-1", Architecture.I386, null, null, "ami-be3adfd7",
            "ec2-public-images/fedora-8-i386-base-v1.04.manifest.xml", "206029621532", ImageState.AVAILABLE,
            ImageType.MACHINE, false, Sets.<String> newHashSet("9961934F"), "aki-4438dd2d", null, "ari-4538dd2c",
            RootDeviceType.INSTANCE_STORE, null, ImmutableMap.<String, EbsBlockDevice> of(), "paravirtual"));

      Set<Image> result = parseImages("/describe_images.xml");

      assertEquals(result, contents);
   }

   public void testWindows() {
      Set<Image> contents = ImmutableSet.of(new Image("us-east-1", Architecture.X86_64, null, null, "ami-02eb086b",
            "aws-solutions-amis/SqlSvrStd2003r2-x86_64-Win_SFWBasic5.1-v1.0.manifest.xml", "771350841976",
            ImageState.AVAILABLE, ImageType.MACHINE, true, Sets.<String> newHashSet("5771E9A6"), null, "windows", null,
            RootDeviceType.INSTANCE_STORE, null, ImmutableMap.<String, EbsBlockDevice> of(), "paravirtual"));

      Set<Image> result = parseImages("/describe_images_windows.xml");

      assertEquals(result, contents);
   }

   public void testEBS() {
      Set<Image> contents = ImmutableSet.of(new Image("us-east-1", Architecture.I386, "websrv_2009-12-10",
            "Web Server AMI", "ami-246f8d4d", "706093390852/websrv_2009-12-10", "706093390852", ImageState.AVAILABLE,
            ImageType.MACHINE, true, Sets.<String> newHashSet(), null, "windows", null, RootDeviceType.EBS,
            "/dev/sda1", ImmutableMap.<String, EbsBlockDevice> of("/dev/sda1", new EbsBlockDevice("snap-d01272b9", 30,
                  true), "xvdf", new EbsBlockDevice("snap-d31272ba", 250, false)), "hvm"));

      Set<Image> result = parseImages("/describe_images_ebs.xml");

      assertEquals(result, contents);
   }

   static ParseSax<Set<Image>> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(String.class).annotatedWith(Region.class).toInstance("us-east-1");
         }

      });
      ParseSax<Set<Image>> parser = (ParseSax<Set<Image>>) injector.getInstance(ParseSax.Factory.class).create(
            injector.getInstance(DescribeImagesResponseHandler.class));
      return parser;
   }

   public static Set<Image> parseImages(String resource) {
      InputStream is = EC2ImageParserTest.class.getResourceAsStream(resource);
      return createParser().parse(is);
   }
}
