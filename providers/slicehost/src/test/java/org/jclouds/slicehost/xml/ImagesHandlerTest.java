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
package org.jclouds.slicehost.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.slicehost.domain.Image;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ImagesHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "ImagesHandlerTest")
public class ImagesHandlerTest extends BaseHandlerTest {

   ParseSax<Set<? extends Image>> createParser() {
      ParseSax<Set<? extends Image>> parser = (ParseSax<Set<? extends Image>>) factory.create(injector
               .getInstance(ImagesHandler.class));
      return parser;
   }

   public void test() {
      InputStream is = getClass().getResourceAsStream("/test_list_images.xml");
      Set<? extends Image> expects = ImmutableSet.of(

      new Image(2, "CentOS 5.2"), new Image(3, "Gentoo 2008.0"),

      new Image(4, "Debian 5.0 (lenny)"),

      new Image(5, "Fedora 10 (Cambridge)"),

      new Image(7, "CentOS 5.3"),

      new Image(8, "Ubuntu 9.04 (jaunty)"),

      new Image(9, "Arch 2009.02"),

      new Image(10, "Ubuntu 8.04.2 LTS (hardy)"),

      new Image(11, "Ubuntu 8.10 (intrepid)"),

      new Image(70, "Ubuntu 10.10 (maverick) 32-bit"),

      new Image(12, "Red Hat EL 5.3"),
      
      new Image(13, "Fedora 11 (Leonidas)")

      );
      assertEquals(createParser().parse(is), expects);
   }
}
