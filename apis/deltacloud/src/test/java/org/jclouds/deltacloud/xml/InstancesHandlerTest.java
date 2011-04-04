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

import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code InstancesHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "InstancesHandlerTest")
public class InstancesHandlerTest extends BaseHandlerTest {

   @Test
   public void test() {
      InputStream is = getClass().getResourceAsStream("/test_list_instances.xml");
      Set<? extends Instance> expects = ImmutableSet.of(new Instance(URI
               .create("http://fancycloudprovider.com/api/instances/inst1"), "inst1", "larry",
               "Production JBoss Instance", URI.create("http://fancycloudprovider.com/api/images/img3"), URI
                        .create("http://fancycloudprovider.com/api/hardware_profiles/m1-small"), URI
                        .create("http://fancycloudprovider.com/api/realms/us"), Instance.State.RUNNING, ImmutableMap
                        .of(Instance.Action.REBOOT, new HttpRequest("POST", URI
                                 .create("http://fancycloudprovider.com/api/instances/inst1/reboot")),
                                 Instance.Action.STOP, new HttpRequest("POST", URI
                                          .create("http://fancycloudprovider.com/api/instances/inst1/stop"))), null,
               ImmutableSet.of("inst1.larry.fancycloudprovider.com"), ImmutableSet.of("inst1.larry.internal")));
      assertEquals(factory.create(injector.getInstance(InstancesHandler.class)).parse(is).toString(), expects.toString());
   }
}
