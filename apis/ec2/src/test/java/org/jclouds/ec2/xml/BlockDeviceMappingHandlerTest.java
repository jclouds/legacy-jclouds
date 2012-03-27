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
package org.jclouds.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;

import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code BlockDeviceMappingHandler}
 * 
 * @author Adrian Cole
 */
//NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BlockDeviceMappingHandlerTest")
public class BlockDeviceMappingHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream(
               "/describe_image_attribute_blockDeviceMapping.xml");

      DateService dateService = injector.getInstance(DateService.class);
      Map<String, BlockDevice> expected = ImmutableMap.<String, BlockDevice> of("/dev/sda1",
               new BlockDevice("vol-d74b82be", Attachment.Status.ATTACHED, dateService
                        .iso8601DateParse("2010-02-20T18:25:26.000Z"), true), "/dev/sdf",
               new BlockDevice("vol-another", Attachment.Status.DETACHED, dateService
                        .iso8601DateParse("2010-02-20T19:26:26.000Z"), false));

      Map<String, BlockDevice> result = factory.create(
               injector.getInstance(BlockDeviceMappingHandler.class)).parse(is);

      assertEquals(result, expected);
   }
}
