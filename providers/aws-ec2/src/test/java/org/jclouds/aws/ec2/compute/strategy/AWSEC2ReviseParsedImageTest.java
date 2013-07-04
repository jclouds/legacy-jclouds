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
package org.jclouds.aws.ec2.compute.strategy;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.ec2.compute.strategy.ReviseParsedImage;
import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.VirtualizationType;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;

public class AWSEC2ReviseParsedImageTest {
    private Map<OsFamily, Map<String, String>> osVersionMap;

    @BeforeClass
    public void testFixtureSetUp() {
        osVersionMap = new BaseComputeServiceContextModule() {
        }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
                .getInstance(Json.class));
    }

    @Test
    public void testNewWindowsName() throws Exception {

        ReviseParsedImage rpi = new AWSEC2ReviseParsedImage(osVersionMap);

        Image from = newImage("amazon", "Windows_Server-2008-R2_SP1-English-64Bit-Base-2012.03.13");
        OperatingSystem.Builder osBuilder = OperatingSystem.builder().description("test");
      ImageBuilder builder = new ImageBuilder().id("1").operatingSystem(osBuilder.build()).status(
               org.jclouds.compute.domain.Image.Status.AVAILABLE).description("test");
        OsFamily family = OsFamily.WINDOWS;

        rpi.reviseParsedImage(from, builder, family, osBuilder);
        OperatingSystem os = osBuilder.build();
        assertEquals(os.getFamily(), OsFamily.WINDOWS);
        assertEquals(os.getVersion(), "2008");
        assertEquals(builder.build().getVersion(), "2012.03.13");
    }

    @Test
    public void testOldWindowsName() throws Exception {

        ReviseParsedImage rpi = new AWSEC2ReviseParsedImage(osVersionMap);

        Image from = newImage("amazon", "Windows-2008R2-SP1-English-Base-2012.01.12");
        OperatingSystem.Builder osBuilder = OperatingSystem.builder().description("test");
        ImageBuilder builder = new ImageBuilder().id("1").operatingSystem(osBuilder.build()).status(
                 org.jclouds.compute.domain.Image.Status.AVAILABLE).description("test");
        OsFamily family = OsFamily.WINDOWS;

        rpi.reviseParsedImage(from, builder, family, osBuilder);
        OperatingSystem os = osBuilder.build();
        assertEquals(os.getFamily(), OsFamily.WINDOWS);
        assertEquals(os.getVersion(), "2008");
        assertEquals(builder.build().getVersion(), "2012.01.12");
    }

    private static Image newImage(String imageOwnerId, String imageName) {
        String region = "us-east-1";
        Image.Architecture architecture = Image.Architecture.X86_64;
        String description = "";
        String imageId = "";
        Image.ImageState imageState = Image.ImageState.AVAILABLE;
        Image.ImageType imageType = Image.ImageType.MACHINE;
        boolean isPublic = true;
        Iterable<String> productCodes = ImmutableSet.of();
        String kernelId = "";
        String platform = "";
        String ramdiskId = "";
        RootDeviceType rootDeviceType = RootDeviceType.EBS;
        String rootDeviceName = "";
        Map<String, Image.EbsBlockDevice> ebsBlockDevices = ImmutableMap.of();
        Map<String, String> tags = ImmutableMap.of();
        VirtualizationType virtualizationType = VirtualizationType.HVM;
        Hypervisor hypervisor = Hypervisor.XEN;
      Image from = new Image(region, architecture, imageName, description, imageId, imageOwnerId + "/" + imageName,
            imageOwnerId, imageState, "available", imageType, isPublic, productCodes, kernelId, platform, ramdiskId,
            rootDeviceType, rootDeviceName, ebsBlockDevices, tags, virtualizationType, hypervisor);
        return from;
    }
}
