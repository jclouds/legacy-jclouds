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

package org.jclouds.ohai.config;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.lang.management.RuntimeMXBean;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.domain.JsonBall;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.ohai.Automatic;
import org.jclouds.ohai.config.JMXOhaiModule;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code JMX}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ohai.JMXTest")
public class JMXTest {

   @Test
   public void test() {

      final RuntimeMXBean runtime = createMock(RuntimeMXBean.class);

      expect(runtime.getUptime()).andReturn(69876000l);

      replay(runtime);

      Injector injector = Guice.createInjector(new ChefParserModule(), new GsonModule(), new JMXOhaiModule() {
         @Override
         protected RuntimeMXBean provideRuntimeMXBean() {
            return runtime;
         }
      });
      Json json = injector.getInstance(Json.class);
      Ohai ohai = injector.getInstance(Ohai.class);
      assertEquals(json.toJson(ohai.ohai.get().get("uptime_seconds")), "69876");
   }

   static class Ohai {
      private Supplier<Map<String, JsonBall>> ohai;

      @Inject
      public Ohai(@Automatic Supplier<Map<String, JsonBall>> ohai) {
         this.ohai = ohai;
      }
   }
}
