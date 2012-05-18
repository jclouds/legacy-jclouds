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
package org.jclouds.openstack.glance.v1_0.parse;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseSetParserTest;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseImageDetailsListTest")
public class ParseImagesInDetailTest extends BaseSetParserTest<ImageDetails> {

   @Override
   public String resource() {
      return "/images_detail.json";
   }

   @Override
   @SelectJson("images")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<ImageDetails> expected() {
      return ImmutableSet.<ImageDetails>builder()
            .add(ImageDetails
                  .builder()
                  .id("fcc451d0-f6e4-4824-ad8f-70ec12326d07")
                  .name("debian")
                  .containerFormat(ContainerFormat.BARE)
                  .diskFormat(DiskFormat.RAW)
                  .checksum("233afa7b8809d840679b5f0d36d7350a")
                  .size(65645798l)
                  .status(Image.Status.ACTIVE)
                  .owner("5821675")
                  .isPublic(true)
                  .createdAt(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-18T18:06:44"))
                  .updatedAt(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-18T18:06:45"))
                  .build())      
             .add(ImageDetails
                  .builder()
                  .id("f9fcb127-071d-4670-883e-eedb7efac183")
                  .name("debian")
                  .containerFormat(ContainerFormat.BARE)
                  .diskFormat(DiskFormat.RAW)
                  .checksum("233afa7b8809d840679b5f0d36d7350a")
                  .size(65645798l)
                  .status(Image.Status.ACTIVE)
                  .owner("5821675")
                  .isPublic(true)
                  .createdAt(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-11T15:04:47"))
                  .updatedAt(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-11T15:04:48"))
                  .build())
            .build();
   }

}
