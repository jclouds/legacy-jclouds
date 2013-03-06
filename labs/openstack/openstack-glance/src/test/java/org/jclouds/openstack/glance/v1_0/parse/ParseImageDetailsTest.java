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

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * 
 * @author Adam Lowe
 */
@Test(groups = "unit", testName = "ParseImageDetailTest")
public class ParseImageDetailsTest extends BaseItemParserTest<ImageDetails> {

   @Override
   public String resource() {
      return "/image.json";
   }

   @Override
   @SelectJson("image")
   @Consumes(MediaType.APPLICATION_JSON)
   public ImageDetails expected() {
      return ImageDetails
                  .builder()
                  .id("02fa0378-f305-43cf-8058-8572fe1da795")
                  .name("jclouds-live-test")
                  .containerFormat(ContainerFormat.BARE)
                  .diskFormat(DiskFormat.RAW)
                  .checksum("6ae4e0fdc3c108a1bfe10ef5e436f4f4")
                  .size(27L)
                  .status(Image.Status.ACTIVE)
                  .owner("68a7c7abb7bf45ada1536dfa28ec2115")
                  .isPublic(false)
                  .createdAt(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-31T10:13:47"))
                  .updatedAt(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-31T10:13:47"))
                  .build();
   }

}
