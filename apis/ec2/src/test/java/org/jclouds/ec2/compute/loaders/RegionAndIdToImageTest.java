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
package org.jclouds.ec2.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.testng.Assert.assertEquals;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.jclouds.compute.domain.Image;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.services.AMIClient;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RegionAndIdToImageTest {

   @SuppressWarnings("unchecked")
   @Test
   public void testApply() throws ExecutionException {

      EC2ImageParser parser = createMock(EC2ImageParser.class);
      EC2Client caller = createMock(EC2Client.class);
      AMIClient client = createMock(AMIClient.class);

      org.jclouds.ec2.domain.Image ec2Image = createMock(org.jclouds.ec2.domain.Image.class);
      Image image = createNiceMock(Image.class);
      Set<? extends org.jclouds.ec2.domain.Image> images = ImmutableSet.<org.jclouds.ec2.domain.Image> of(ec2Image);

      expect(caller.getAMIServices()).andReturn(client).atLeastOnce();
      expect(client.describeImagesInRegion("region", imageIds("ami"))).andReturn(Set.class.cast(images));
      expect(parser.apply(ec2Image)).andReturn(image);

      replay(caller);
      replay(image);
      replay(parser);
      replay(client);

      RegionAndIdToImage function = new RegionAndIdToImage(parser, caller);

      assertEquals(function.load(new RegionAndName("region", "ami")), image);

      verify(caller);
      verify(image);
      verify(image);
      verify(client);

   }

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = ExecutionException.class)
   public void testApplyNotFoundMakesExecutionException() throws ExecutionException {

      EC2ImageParser parser = createMock(EC2ImageParser.class);
      EC2Client caller = createMock(EC2Client.class);
      AMIClient client = createMock(AMIClient.class);

      org.jclouds.ec2.domain.Image ec2Image = createMock(org.jclouds.ec2.domain.Image.class);
      Image image = createNiceMock(Image.class);
      Set<? extends org.jclouds.ec2.domain.Image> images = ImmutableSet.<org.jclouds.ec2.domain.Image> of(ec2Image);

      expect(caller.getAMIServices()).andReturn(client).atLeastOnce();
      expect(client.describeImagesInRegion("region", imageIds("ami"))).andReturn(Set.class.cast(images));
      expect(parser.apply(ec2Image)).andThrow(new ResourceNotFoundException());

      replay(caller);
      replay(image);
      replay(parser);
      replay(client);

      RegionAndIdToImage function = new RegionAndIdToImage(parser, caller);

      assertEquals(function.load(new RegionAndName("region", "ami")), null);

      verify(caller);
      verify(image);
      verify(parser);
      verify(client);

   }

   @SuppressWarnings("unchecked")
   @Test(expectedExceptions = ExecutionException.class)
   public void testApplyNoSuchElementExceptionMakesExecutionException() throws ExecutionException {

      EC2ImageParser parser = createMock(EC2ImageParser.class);
      EC2Client caller = createMock(EC2Client.class);
      AMIClient client = createMock(AMIClient.class);

      org.jclouds.ec2.domain.Image ec2Image = createMock(org.jclouds.ec2.domain.Image.class);
      Image image = createNiceMock(Image.class);
      Set<? extends org.jclouds.ec2.domain.Image> images = ImmutableSet.<org.jclouds.ec2.domain.Image> of(ec2Image);

      expect(caller.getAMIServices()).andReturn(client).atLeastOnce();
      expect(client.describeImagesInRegion("region", imageIds("ami"))).andReturn(Set.class.cast(images));
      expect(parser.apply(ec2Image)).andThrow(new NoSuchElementException());

      replay(caller);
      replay(image);
      replay(parser);
      replay(client);

      RegionAndIdToImage function = new RegionAndIdToImage(parser, caller);

      assertEquals(function.load(new RegionAndName("region", "ami")), null);

      verify(caller);
      verify(image);
      verify(parser);
      verify(client);

   }
}
