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

import org.jclouds.json.BaseSetParserTest;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseImageListTest")
public class ParseImagesTest extends BaseSetParserTest<Image> {

   @Override
   public String resource() {
      return "/images.json";
   }

   @Override
   @SelectJson("images")
   @Consumes(MediaType.APPLICATION_JSON)
   public Set<Image> expected() {
      return ImmutableSet.<Image>builder()
            .add(Image
                  .builder()
                  .id("f0209a30-25b8-4d9a-8e2f-dbc028e20b2b")
                  .name("debian")
                  .containerFormat(ContainerFormat.BARE)
                  .diskFormat(DiskFormat.RAW)
                  .checksum("233afa7b8809d840679b5f0d36d7350a")
                  .size(65645798l)
                  .build())
            .add(Image
                  .builder()
                  .id("fcc451d0-f6e4-4824-ad8f-70ec12326d07")
                  .name("debian")
                  .containerFormat(ContainerFormat.BARE)
                  .diskFormat(DiskFormat.RAW)
                  .checksum("233afa7b8809d840679b5f0d36d7350a")
                  .size(65645798l)
                  .build())
            .add(Image
                  .builder()
                  .id("f9fcb127-071d-4670-883e-eedb7efac183")
                  .name("debian")
                  .containerFormat(ContainerFormat.BARE)
                  .diskFormat(DiskFormat.RAW)
                  .checksum("233afa7b8809d840679b5f0d36d7350a")
                  .size(65645798l)
                  .build())                  
            .build();
   }
}
