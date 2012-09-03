/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.aws.ec2.xml;

import static com.google.common.collect.Iterables.get;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.aws.ec2.compute.strategy.AWSEC2ImageParserTest;
import org.jclouds.aws.ec2.domain.AWSImage;
import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.Image.Architecture;
import org.jclouds.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.ec2.domain.Image.ImageState;
import org.jclouds.ec2.domain.Image.ImageType;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.VirtualizationType;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.location.Region;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@link AWSDescribeImagesResponseHandler}.
 * 
 * @author Adrian Cole
 * @author Andrew Kennedy
 */
@Test(groups = "unit", testName = "AWSDescribeImagesResponseHandlerTest")
public class AWSDescribeImagesResponseHandlerTest {

   public void testTags() {
      Set<AWSImage> contents = ImmutableSet.of(
            new AWSImage(
                  "us-east-1", Architecture.I386, "websrv_2009-12-10", "Web Server AMI", "ami-246f8d4d",
                  "706093390852/websrv_2009-12-10", "706093390852", ImageState.AVAILABLE, "available", ImageType.MACHINE, true, Sets.<String> newHashSet(), null, "windows", null,
	               RootDeviceType.EBS, "/dev/sda1",
	               ImmutableMap.<String, EbsBlockDevice> of(
	                     "/dev/sda1", new EbsBlockDevice("snap-d01272b9", 30, true),
	                     "xvdf", new EbsBlockDevice("snap-d31272ba", 250, false)),
	               VirtualizationType.HVM, Hypervisor.XEN,
	               ImmutableMap.<String, String>of("tagKey", "TAG_VALUE")));

      Set<AWSImage> result = parseImages("/describe_images_tags.xml");

      assertEquals(result.toString(), contents.toString());
      assertEquals(get(result, 0).getImageState(), ImageState.AVAILABLE);
      assertEquals(get(result, 0).getRawState(), "available");
      assertEquals(get(result, 0).getTags().size(), 1);
      assertEquals(get(result, 0).getTags().get("tagKey"), "TAG_VALUE");
   }

   public static ParseSax<Set<AWSImage>> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {
         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() { })
                  .annotatedWith(Region.class)
                  .toInstance(Suppliers.ofInstance("us-east-1"));
         }
      });
      ParseSax<Set<AWSImage>> parser = (ParseSax<Set<AWSImage>>) injector.getInstance(ParseSax.Factory.class)
               .create(injector.getInstance(AWSDescribeImagesResponseHandler.class));
      return parser;
   }

   public static Set<AWSImage> parseImages(String resource) {
      InputStream is = AWSDescribeImagesResponseHandlerTest.class.getResourceAsStream(resource);
      return createParser().parse(is);
   }
}
