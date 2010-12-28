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

import org.jclouds.deltacloud.domain.EnumHardwareProperty;
import org.jclouds.deltacloud.domain.FixedHardwareProperty;
import org.jclouds.deltacloud.domain.HardwareParameter;
import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.HardwareProperty;
import org.jclouds.deltacloud.domain.RangeHardwareProperty;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code HardwareProfileHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class HardwareProfileHandlerTest {

   static ParseSax<HardwareProfile> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule());
      ParseSax<HardwareProfile> parser = (ParseSax<HardwareProfile>) injector.getInstance(ParseSax.Factory.class)
            .create(injector.getInstance(HardwareProfileHandler.class));
      return parser;
   }

   public static HardwareProfile parseHardwareProfile() {
      return parseHardwareProfile("/test_get_hardware_profile.xml");
   }

   public static HardwareProfile parseHardwareProfile(String resource) {
      InputStream is = HardwareProfileHandlerTest.class.getResourceAsStream(resource);
      return createParser().parse(is);
   }

   public void test() {
      HardwareProfile expects = new HardwareProfile(
            URI.create("http://localhost:3001/api/hardware_profiles/m1-xlarge"), "m1-xlarge", "m1-xlarge",
            ImmutableSet.<HardwareProperty> of(
                  new FixedHardwareProperty("cpu", "count", new Long(4)),
                  new RangeHardwareProperty("memory", "MB", new Long(12288), new HardwareParameter(URI
                        .create("http://localhost:3001/api/instances"), "post", "hwp_memory", "create"),
                        new Long(12288), new Long(32768)),
                  new EnumHardwareProperty("storage", "GB", new Long(1024), new HardwareParameter(URI
                        .create("http://localhost:3001/api/instances"), "post", "hwp_storage", "create"),
                        ImmutableSet.<Object> of(new Long(1024), new Long(2048), new Long(4096))),
                  new FixedHardwareProperty("architecture", "label", "x86_64"))
      );
      assertEquals(parseHardwareProfile(), expects);
   }
}
