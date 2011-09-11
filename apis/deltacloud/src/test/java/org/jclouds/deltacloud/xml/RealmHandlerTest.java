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
package org.jclouds.deltacloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.deltacloud.domain.Realm;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code RealmHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class RealmHandlerTest {

   static ParseSax<Realm> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule());
      ParseSax<Realm> parser = injector.getInstance(ParseSax.Factory.class).create(
               injector.getInstance(RealmHandler.class));
      return parser;
   }

   public static Realm parseRealm() {
      return parseRealm("/test_get_realm.xml");
   }

   public static Realm parseRealm(String resource) {
      InputStream is = RealmHandlerTest.class.getResourceAsStream(resource);
      return createParser().parse(is);
   }

   public void test() {
      Realm expects = new Realm(URI.create("http://fancycloudprovider.com/api/realms/us"), "us", "United States", null,
               Realm.State.AVAILABLE);
      assertEquals(parseRealm(), expects);
   }

}
