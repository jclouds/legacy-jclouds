/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import org.jclouds.deltacloud.domain.KeyAuthentication;
import org.jclouds.deltacloud.domain.PasswordAuthentication;
import org.jclouds.deltacloud.domain.Instance.Authentication;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpRequest;
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

   public void testWithNoAuthentication() {
      Instance expects = instanceWithAuthentication(null);
      assertEquals(parseInstance("/test_get_instance.xml").toString(), expects.toString());
   }

   public void testWithPasswordAuthentication() {
      Instance expects = instanceWithAuthentication(new PasswordAuthentication(new Credentials("root", "FOO")));
      assertEquals(parseInstance("/test_get_instance_pw.xml").toString(), expects.toString());
   }

   public void testWithKeyAuthentication() {
      Instance expects = instanceWithAuthentication(new KeyAuthentication("keyname"));
      assertEquals(parseInstance("/test_get_instance_key.xml").toString(), expects.toString());
   }

   public void testWithKeyAuthenticationBlank() {
      Instance expects = instanceWithAuthentication(null);
      assertEquals(parseInstance("/test_get_instance_nokey.xml").toString(), expects.toString());
   }

   private Instance instanceWithAuthentication(Authentication authentication) {
      return new Instance(URI.create("http://fancycloudprovider.com/api/instances/inst1"), "inst1", "larry",
               "Production JBoss Instance", URI.create("http://fancycloudprovider.com/api/images/img3"), URI
                        .create("http://fancycloudprovider.com/api/hardware_profiles/m1-small"), URI
                        .create("http://fancycloudprovider.com/api/realms/us"), Instance.State.RUNNING, ImmutableMap
                        .of(Instance.Action.REBOOT, new HttpRequest("POST", URI
                                 .create("http://fancycloudprovider.com/api/instances/inst1/reboot")),
                                 Instance.Action.STOP, new HttpRequest("POST", URI
                                          .create("http://fancycloudprovider.com/api/instances/inst1/stop"))),
               authentication, ImmutableSet.of("inst1.larry.fancycloudprovider.com"), ImmutableSet
                        .of("inst1.larry.internal"));
   }

}
