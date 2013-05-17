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
package org.jclouds.aws.ec2.compute.extensions;

import static com.google.common.collect.Iterables.transform;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.extensions.internal.BaseImageExtensionLiveTest;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Live test for aws-ec2 {@link ImageExtension} implementation
 * 
 * @author David Alves
 * 
 */
@Test(groups = "live", singleThreaded = true, testName = "AWSEC2ImageExtensionLiveTest")
public class AWSEC2ImageExtensionLiveTest extends BaseImageExtensionLiveTest {

   public AWSEC2ImageExtensionLiveTest() {
      provider = "aws-ec2";
   }

   @Override
   protected Iterable<? extends Image> listImages() {
      AWSEC2Client client = view.utils().injector().getInstance(AWSEC2Client.class);
      String[] parts = AWSUtils.parseHandle(imageId);
      String region = parts[0];
      String imageId = parts[1];
      EC2ImageParser parser = view.utils().injector().getInstance(EC2ImageParser.class);
      return transform(
            client.getAMIServices().describeImagesInRegion(region, new DescribeImagesOptions().imageIds(imageId)),
            parser);
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}
