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
import org.jclouds.ovf.ProductSection;
import org.jclouds.ovf.Property;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ProductSectionHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ProductSectionHandlerTest {
   public void test() {
      InputStream is = getClass().getResourceAsStream("/productsection.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      ProductSection result = factory.create(injector.getInstance(ProductSectionHandler.class)).parse(is);
      assertEquals(result.toString(), ProductSection.builder().info("vCenter Information").property(
               Property.builder().value("false").key("vmwareToolsEnabled").label("VMWare Tools Enabled status")
                        .description("VMWare Tools Enabled status").build()).property(

               Property.builder().value("10.12.46.171").key("vmwareESXHost").label("VMWare ESXHost Ipaddress")
                        .description("VMWare ESXHost Ipaddress").build()).property(

               Property.builder().value("cussl01s01c01alun088bal,cussl01s01c01alun089bal").key("datastores").label(
                        "Datastore Name").description("Datastore Name").build()).property(
               Property.builder().value("[Totalcapacity=1335915184128,UsedCapacity=549755813888]").key(
                        "cussl01s01c01alun088bal").label("Datastore Capacity").description(
                        "Datastore cussl01s01c01alun088bal Total Capacity, Used Capacity in comma separated").build())
               .property(

                        Property.builder().value("[Totalcapacity=1335915184129,UsedCapacity=549755813889]").key(
                                 "cussl01s01c01alun089bal").label("Datastore Capacity").description(
                                 "Datastore cussl01s01c01alun089bal Total Capacity, Used Capacity in comma separated")
                                 .build()).property(

                        Property.builder().value("[name=3282176-1949-bal-tier01,ip=0.0.0.0,mac=00:50:56:8c:3f:3c]")
                                 .key("customerPortprofile").label("customerPortprofile").description(
                                          "customerPortprofile").build()).property(

                        Property.builder().value("[name=vm-server-mgmt,ip=0.0.0.0,mac=00:50:56:8c:39:75]").key(
                                 "savvisPortprofile").label("savvisPortprofile").description("savvisPortprofile")
                                 .build()).build().toString()

      );
   }
}
