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

import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.InstanceAction;
import org.jclouds.deltacloud.domain.InstanceState;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code InstanceHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class InstanceHandlerTest {

   static ParseSax<Instance> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule());
      ParseSax<Instance> parser = injector.getInstance(ParseSax.Factory.class).create(
            injector.getInstance(InstanceHandler.class));
      return parser;
   }

   public static Instance parseInstance() {
      return parseInstance("/test_get_instance.xml");
   }

   public static Instance parseInstance(String resource) {
      InputStream is = InstanceHandlerTest.class.getResourceAsStream(resource);
      return createParser().parse(is);
   }

   public void test() {
      Instance expects = new Instance(URI.create("http://fancycloudprovider.com/api/instances/inst1"), "inst1", "larry",
            "Production JBoss Instance", URI.create("http://fancycloudprovider.com/api/images/img3"),
            URI.create("http://fancycloudprovider.com/api/hardware_profiles/m1-small"),
            URI.create("http://fancycloudprovider.com/api/realms/us"), InstanceState.RUNNING, ImmutableMap.of(
                  InstanceAction.REBOOT, URI.create("http://fancycloudprovider.com/api/instances/inst1/reboot"),
                  InstanceAction.STOP, URI.create("http://fancycloudprovider.com/api/instances/inst1/stop")),
            ImmutableSet.of("inst1.larry.fancycloudprovider.com"), ImmutableSet.of("inst1.larry.internal"));
      assertEquals(parseInstance(), expects);
   }

}
