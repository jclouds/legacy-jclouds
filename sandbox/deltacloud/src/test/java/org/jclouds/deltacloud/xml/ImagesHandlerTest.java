/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.deltacloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;

import org.jclouds.deltacloud.domain.Image;
import org.jclouds.deltacloud.xml.ImagesHandler;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code ImagesHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ImagesHandlerTest extends BaseHandlerTest {

   @Test
   public void test() {
      InputStream is = getClass().getResourceAsStream("/test_list_images.xml");
      Set<? extends Image> expects = ImmutableSet.of(

      new Image(URI.create("http://fancycloudprovider.com/api/images/img1"), "img1", "fedoraproject", "Fedora 10",
            "Fedora 10", "x86_64"), new Image(URI.create("http://fancycloudprovider.com/api/images/img2"), "img2",
            "fedoraproject", "Fedora 10", "Fedora 10", "i386"),
            new Image(URI.create("http://fancycloudprovider.com/api/images/img3"), "img3", "ted", "JBoss", "JBoss",
                  "i386"));
      System.out.println(factory);
      System.out.println(injector);

      // not sure why this isn't always automatically called from surefire.
      setUpInjector();
      assertEquals(factory.create(injector.getInstance(ImagesHandler.class)).parse(is), expects);
   }
}
