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

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.ovf.Network;
import org.jclouds.ovf.NetworkSection;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code NetworkSectionHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class NetworkSectionHandlerTest {
   public void test() {
      InputStream is = getClass().getResourceAsStream("/networksection.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      NetworkSection result = factory.create(injector.getInstance(NetworkSectionHandler.class)).parse(is);
      assertEquals(result.toString(), NetworkSection.builder().info("List of logical networks used in the package")
               .network(
                        Network.builder().name("red").description("The network the Red service is available on")
                                 .build()).network(
                        Network.builder().name("blue").description("The network the Blue service is available on")
                                 .build())
               .build().toString()

      );
   }
}
