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
package org.jclouds.azure.management.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.jclouds.azure.management.domain.Disk;
import org.jclouds.azure.management.domain.Disk.Attachment;
import org.jclouds.azure.management.domain.OSType;
import org.jclouds.azure.management.xml.ListDisksHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Gérald Pereira
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ListDisksTest")
public class ListDisksTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/disks.xml");

      Set<Disk> expected = expected();

      ListDisksHandler handler = injector.getInstance(ListDisksHandler.class);
      Set<Disk> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public Set<Disk> expected() {
	   
      return ImmutableSet.<Disk>builder()
                         .add(Disk.builder()
                                     .os(OSType.LINUX)
                                     .location("West Europe")
                                     .logicalSizeInGB(30)
                                     .mediaLink(URI.create("http://neotysbucket1.blob.core.windows.net/vhds/testimage2-testimage2-2012-08-17.vhd"))
                                     .name("testimage2-testimage2-0-20120817095145")
                                     .sourceImage("OpenLogic__OpenLogic-CentOS-62-20120531-en-us-30GB.vhd")
                                     .build())
                          .add(Disk.builder()
                        		  	 .attachedTo(Attachment.builder().deployment("neotysss").hostedService("neotysss").role("neotysss").build())
                                     .os(OSType.WINDOWS)
                                     .location("West Europe")
                                     .logicalSizeInGB(30)
                                     .mediaLink(URI.create("http://portalvhds0g7xhnq2x7t21.blob.core.windows.net/disks/neotysss/MSFT__Win2K8R2SP1-120612-1520-121206-01-en-us-30GB.vhd"))
                                     .name("neotysss-neotysss-0-20120824091357")
                                     .sourceImage("MSFT__Win2K8R2SP1-120612-1520-121206-01-en-us-30GB.vhd")
                                     .build())
                        .build();
   }

}
