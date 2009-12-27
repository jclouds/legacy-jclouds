/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;

import org.jclouds.aws.ec2.domain.Image.EbsBlockDevice;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code BlockDeviceMappingHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.BlockDeviceMappingHandlerTest")
public class BlockDeviceMappingHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream(
               "/ec2/describe_image_attribute_blockDeviceMapping.xml");

      Map<String, EbsBlockDevice> expected = ImmutableMap.<String, EbsBlockDevice> of("/dev/sda1",
               new EbsBlockDevice("snap-d01272b9", 30, true), "xvdf", new EbsBlockDevice(
                        "snap-d31272ba", 250, false));

      Map<String, EbsBlockDevice> result = factory.create(
               injector.getInstance(BlockDeviceMappingHandler.class)).parse(is);

      assertEquals(result, expected);
   }
}
