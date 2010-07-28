/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.ohai.util;

import static org.testng.Assert.assertEquals;

import java.util.Date;

import org.jclouds.domain.JsonBall;
import org.jclouds.ohai.Util.OhaiUtils;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code OhaiUtils}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "ohai.OhaiUtilsTest")
public class OhaiUtilsTest {
   public static long nanotime = 1280251180727244000l;
   public static String nanotimeString = "1280251180727.244";
   public static Date now = new Date(1280251180727l);

   public void testToOhaiTime() {
      assertEquals(OhaiUtils.toOhaiTime(nanotime).toString(), nanotimeString);
   }

   public void testFromOhaiTime() {
      assertEquals(OhaiUtils.fromOhaiTime(new JsonBall(nanotimeString)), now);

   }

}
