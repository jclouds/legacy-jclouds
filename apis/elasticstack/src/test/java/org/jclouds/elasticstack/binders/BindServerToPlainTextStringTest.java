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
package org.jclouds.elasticstack.binders;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.jclouds.elasticstack.domain.IDEDevice;
import org.jclouds.elasticstack.domain.Model;
import org.jclouds.elasticstack.domain.NIC;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.VNC;
import org.jclouds.elasticstack.functions.ServerToMap;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class BindServerToPlainTextStringTest {

   public static final String CREATED_SERVER;
   static {
      try {
         CREATED_SERVER = Strings2.toStringAndClose(BindServerToPlainTextStringTest.class
               .getResourceAsStream("/create_server.txt"));
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }
   public static final Server SERVER = new Server.Builder()
         .name("TestServer")
         .cpu(2000)
         .mem(1024)
         .devices(
               ImmutableMap.of("ide:0:0", new IDEDevice.Builder(0, 0).uuid("08c92dd5-70a0-4f51-83d2-835919d254df")
                     .build())).bootDeviceIds(ImmutableSet.of("ide:0:0"))
         .nics(ImmutableSet.of(new NIC.Builder().model(Model.E1000).

         build())).vnc(new VNC(null, "XXXXXXXX", false)).build();
   private Injector i = Guice.createInjector(new AbstractModule() {

      @Override
      protected void configure() {
         bindConstant().annotatedWith(ApiVersion.class).to("1.0");
         bind(new TypeLiteral<Function<Server, Map<String, String>>>() {
         }).to(ServerToMap.class);
      }

   });

   public void testSimple() throws IOException {
      HttpRequest request = HttpRequest.builder().method("POST").endpoint("https://host/drives/create").build();
      i.getInstance(BindServerToPlainTextString.class).bindToRequest(request, SERVER);
      assertEquals(request.getPayload().getContentMetadata().getContentType(), MediaType.TEXT_PLAIN);
      assertEquals(request.getPayload().getRawContent(), CREATED_SERVER);
   }

}
