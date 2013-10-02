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
package org.jclouds.elasticstack.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.jclouds.elasticstack.domain.ClaimType;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.DriveMetrics;
import org.jclouds.elasticstack.domain.DriveStatus;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class MapToDriveInfoTest {
   public static DriveInfo ONE = new DriveInfo.Builder()
         .status(DriveStatus.ACTIVE)
         .name("Ubuntu 10.10 Server Edition Linux 64bit Preinstalled System")
         .metrics(
               new DriveMetrics.Builder().readBytes(4096l).writeBytes(8589938688l).writeRequests(2097153l)
                     .readRequests(1l).build())
         .user("58ca3c1f-7629-4771-9b71-863f40153ba4")
         .encryptionCipher("aes-xts-plain")
         .uuid("b8171d28-755a-4271-b891-7998871a160e")
         .claimType(ClaimType.SHARED)
         .claimed(
               ImmutableSet.of(
                     "00109617-2c6b-424b-9cfa-5b572c17bafe:guest:692cd1c7-a863-4a22-8170-fc6e6feb68af:ide:0:0",
                     "00031836-a624-4b22-bc7d-41ff8977087b:guest:a1414360-7c24-4730-8c97-180bf7775a71:ide:0:0",
                     "0002c6df-a1d2-4d1d-96f0-f95405a28183:guest:386f1cc7-affc-49c1-82a5-2f8e412170e4:ide:0:0",
                     "00031836-a624-4b22-bc7d-41ff8977087b:guest:17b076be-430d-4a76-9df3-b9896fec82a5:ide:0:0",
                     "000663ee-9fb6-4461-90f6-01327a4aff07:guest:f83b519f-feab-42cf-859c-f61495681ada:ide:0:1"))//
         .readers(ImmutableSet.of("ffffffff-ffff-ffff-ffff-ffffffffffff"))//
         .size(8589934592l)//
         .userMetadata(ImmutableMap.of("foo", "bar", "baz", "raz")).build();

   private static final MapToDriveInfo MAP_TO_DRIVE = new MapToDriveInfo();

   public void testEmptyMapReturnsNull() {
      assertEquals(MAP_TO_DRIVE.apply(ImmutableMap.<String, String> of()), null);
   }

   public void testBasics() {
      DriveInfo expects = new DriveInfo.Builder().name("foo").size(100l).metrics(new DriveMetrics.Builder().build())
            .build();
      assertEquals(MAP_TO_DRIVE.apply(ImmutableMap.of("name", "foo", "size", "100")), expects);
   }

   public void testComplete() throws IOException {

      Map<String, String> input = new ListOfKeyValuesDelimitedByBlankLinesToListOfMaps().apply(
            Strings2.toStringAndClose(MapToDriveInfoTest.class.getResourceAsStream("/drive.txt"))).get(0);

      assertEquals(MAP_TO_DRIVE.apply(input), ONE);

   }
}
