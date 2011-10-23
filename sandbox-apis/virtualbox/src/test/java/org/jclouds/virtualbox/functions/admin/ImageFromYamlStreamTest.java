package org.jclouds.virtualbox.functions.admin;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * Licensed to jclouds, Inc. (jclouds) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership. jclouds licenses this file to you
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * 
 * @author Andrea Turli
 */
@Test(groups = "unit")
public class ImageFromYamlStreamTest {

   public static final Image TEST1 = new ImageBuilder()
         .id("myTestId")
         .name("ubuntu-11.04-server-i386")
         .description("ubuntu 11.04 server (i386)")
         .operatingSystem(
               OperatingSystem.builder().description("ubuntu").family(OsFamily.UBUNTU).version("11.04").build())
         .build();

   @Test
   public void testNodesParse() throws Exception {
      InputStream is = getClass().getResourceAsStream("/testImages.yaml");
      ImageFromYamlStream parser = new ImageFromYamlStream();
      assertEquals(parser.apply(is).asMap(), ImmutableMap.of(TEST1.getId(), TEST1));
      is.close();
   }

}