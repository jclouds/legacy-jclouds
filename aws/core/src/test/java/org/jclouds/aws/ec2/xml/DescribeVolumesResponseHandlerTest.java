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
import java.util.Set;

import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.Volume;
import org.jclouds.aws.ec2.domain.Volume.Attachment;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code DescribeVolumesResponseHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeVolumesResponseHandlerTest")
public class DescribeVolumesResponseHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {
      DateService dateService = injector.getInstance(DateService.class);
      InputStream is = getClass().getResourceAsStream("/ec2/describe_volumes.xml");

      Set<Volume> expected = Sets.newLinkedHashSet();
      expected.add(new Volume("vol-2a21e543", 1, null, AvailabilityZone.US_EAST_1A,
               Volume.Status.AVAILABLE, dateService.iso8601DateParse("2009-12-28T05:42:53.000Z"),
               Sets.<Attachment> newLinkedHashSet()));
      expected.add(new Volume("vol-4282672b", 800, null, AvailabilityZone.US_EAST_1A,
               Volume.Status.IN_USE, dateService.iso8601DateParse("2008-05-07T11:51:50.000Z"), Sets
                        .<Attachment> newHashSet(new Attachment("vol-4282672b", "i-6058a509",
                                 "/dev/sdh", Volume.Attachment.Status.ATTACHED, dateService
                                          .iso8601DateParse("2008-05-07T12:51:50.000Z")))));
      Set<Volume> result = factory.create(
               injector.getInstance(DescribeVolumesResponseHandler.class)).parse(is);

      assertEquals(result, expected);
   }
}