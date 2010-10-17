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

package org.jclouds.slicehost.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.slicehost.domain.Image;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ImageHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "slicehost.ImageHandler")
public class ImageHandlerTest {

   static ParseSax<Image> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule());
      ParseSax<Image> parser = (ParseSax<Image>) injector.getInstance(ParseSax.Factory.class).create(
            injector.getInstance(ImageHandler.class));
      return parser;
   }

   public static Image parseImage() {
      InputStream is = ImageHandlerTest.class.getResourceAsStream("/test_get_image.xml");
      return createParser().parse(is);
   }

   public void test() {
      Image expects = new Image(2, "CentOS 5.2");
      assertEquals(parseImage(), expects);
   }

}
