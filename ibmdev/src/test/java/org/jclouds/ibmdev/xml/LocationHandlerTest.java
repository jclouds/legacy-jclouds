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

package org.jclouds.ibmdev.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Map;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ibmdev.domain.Location;
import org.jclouds.ibmdev.reference.IBMDeveloperCloudConstants;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code LocationHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ibmdev.LocationHandlerTest")
public class LocationHandlerTest extends BaseHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/location.xml");

      Location result = factory.create(injector.getInstance(LocationHandler.class)).parse(is);

      Map<String, Map<String, String>> capabilites = ImmutableMap.<String, Map<String, String>> of(
               IBMDeveloperCloudConstants.CAPABILITY_CAPACITY, ImmutableMap.<String, String> of(
                        "SMALL", "50", "MEDIUM", "100", "LARGE", "200"),
               IBMDeveloperCloudConstants.CAPABILITY_FORMAT, ImmutableMap.<String, String> of(
                        "EXT3", "ext3"), IBMDeveloperCloudConstants.CAPABILITY_I386,
               ImmutableMap.<String, String> of("SMALL", "SMALL", "MEDIUM", "MEDIUM", "LARGE",
                        "LARGE"), IBMDeveloperCloudConstants.CAPABILITY_x86_64, ImmutableMap
                        .<String, String> of(

                        ));
      Location expects = new Location("1", "US North East: Poughkeepsie, NY", null, "POK",
               capabilites);
      assertEquals(result, expects);
   }
}
