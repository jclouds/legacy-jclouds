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
package org.jclouds.openstack.nova.ec2.strategy;

import static org.testng.Assert.assertEquals;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.config.BaseComputeServiceContextModule;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.ec2.compute.config.EC2ComputeServiceDependenciesModule;
import org.jclouds.ec2.compute.functions.EC2ImageParser;
import org.jclouds.ec2.compute.strategy.EC2PopulateDefaultLoginCredentialsForImageStrategy;
import org.jclouds.ec2.domain.Image;
import org.jclouds.ec2.xml.DescribeImagesResponseHandlerTest;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.nova.v2_0.compute.functions.ImageToOperatingSystem;
import org.testng.annotations.Test;

import com.google.common.base.Predicates;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Guice;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "NovaReviseParsedImageTest")
public class NovaReviseParsedImageTest {

   public void test() {

      Set<org.jclouds.compute.domain.Image> result = convertImages("/nova_ec2_images.xml");
      assertEquals(result.size(), 7);

      assertEquals(
            Iterables.get(result, 4).toString(),
            new ImageBuilder()
                  .operatingSystem(
                        OperatingSystem.builder().family(OsFamily.UBUNTU).arch("paravirtual").version("10.10")
                              .name("Ubuntu Maverick 10.10 Server 64-bit 20111212")
                              .description("Ubuntu Maverick 10.10 Server 64-bit 20111212").is64Bit(true)
                              .build())
                  .name("Ubuntu Maverick 10.10 Server 64-bit 20111212")
                  .description("")
                  .defaultCredentials(LoginCredentials.builder().user("root").build())
                  .id("us-east-1/ami-000004d6")
                  .providerId("ami-000004d6")
                  .location(defaultLocation)
                  .status(org.jclouds.compute.domain.Image.Status.AVAILABLE)
                  .backendStatus("available")
                  .userMetadata(
                        ImmutableMap.of("owner", "", "rootDeviceType", "instance-store", "virtualizationType",
                              "paravirtual", "hypervisor", "xen")).build().toString());
      assertEquals(Iterables.get(result, 4).getStatus(), org.jclouds.compute.domain.Image.Status.AVAILABLE);
   }

   static Location defaultLocation = new LocationBuilder().scope(LocationScope.REGION).id("us-east-1").description(
            "us-east-1").build();

   public static Set<org.jclouds.compute.domain.Image> convertImages(String resource) {

      Map<OsFamily, Map<String, String>> map = new BaseComputeServiceContextModule() {
      }.provideOsVersionMap(new ComputeServiceConstants.ReferenceData(), Guice.createInjector(new GsonModule())
               .getInstance(Json.class));

      Set<Image> result = DescribeImagesResponseHandlerTest.parseImages(resource);
      EC2ImageParser parser = new EC2ImageParser(EC2ComputeServiceDependenciesModule.toPortableImageStatus,
               new EC2PopulateDefaultLoginCredentialsForImageStrategy(), map, Suppliers
                        .<Set<? extends Location>> ofInstance(ImmutableSet.<Location> of(defaultLocation)), Suppliers
                        .ofInstance(defaultLocation), new NovaReviseParsedImage(new ImageToOperatingSystem(map)));
      return Sets.newLinkedHashSet(Iterables.filter(Iterables.transform(result, parser), Predicates.notNull()));
   }

}
