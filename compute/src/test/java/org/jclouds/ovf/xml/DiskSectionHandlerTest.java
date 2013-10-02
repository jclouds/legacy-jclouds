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
package org.jclouds.ovf.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.ovf.Disk;
import org.jclouds.ovf.DiskSection;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code DiskSectionHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class DiskSectionHandlerTest {
   public void test() {
      InputStream is = getClass().getResourceAsStream("/disksection.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      DiskSection result = factory.create(injector.getInstance(DiskSectionHandler.class)).parse(is);
      assertEquals(result.toString(), DiskSection.builder().info("Describes the set of virtual disks").disk(
               Disk.builder().id("vmdisk1")

               .fileRef("file1").capacity(8589934592l).populatedSize(3549324972l).format(
                        URI.create("http://www.vmware.com/interfaces/specifications/vmdk.html#sparse")).build()).disk(
               Disk.builder().id("vmdisk2").capacity(536870912l).build()).disk(
               Disk.builder().id("vmdisk3").capacityAllocationUnits("byte * 2^30").build())

      .build().toString()

      );
   }
}
