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
package org.jclouds.compute.callables;

import static java.lang.String.format;
import static org.testng.Assert.assertEquals;

import java.io.File;

import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * @author Adam Lowe
 */
@Test(groups = "unit", singleThreaded = true, testName = "InitScriptConfigurationForTasksTest")
public class InitScriptConfigurationForTasksTest {

   public void testDefaults() {
      InitScriptConfigurationForTasks config = InitScriptConfigurationForTasks.create();
      assertEquals(config.getAnonymousTaskSuffixSupplier().toString(), "currentTimeMillis()");
      assertEquals(config.getBasedir(), "/tmp");
      assertEquals(config.getInitScriptPattern(), "/tmp/init-%s");
   }

   public void testPatternUpdatesBasedir() {
      InitScriptConfigurationForTasks config = InitScriptConfigurationForTasks.create();
      config.initScriptPattern("/var/foo-init-%s");
      assertEquals(config.getBasedir(), format("%svar", File.separator));
      assertEquals(config.getInitScriptPattern(), "/var/foo-init-%s");
   }

   public void testPatternUpdatesBasedirGuice() {
      InitScriptConfigurationForTasks config = Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            bindConstant().annotatedWith(Names.named(InitScriptConfigurationForTasks.PROPERTY_INIT_SCRIPT_PATTERN)).to(
                     "/var/foo-init-%s");
         }

      }).getInstance(InitScriptConfigurationForTasks.class);
      config.initScriptPattern("/var/foo-init-%s");
      assertEquals(config.getBasedir(), format("%svar", File.separator));
      assertEquals(config.getInitScriptPattern(), "/var/foo-init-%s");
   }

   public void testCurrentTimeSupplier() throws InterruptedException {
      InitScriptConfigurationForTasks config = InitScriptConfigurationForTasks.create();
      long time1 = Long.parseLong(config.getAnonymousTaskSuffixSupplier().get());
      assert time1 <= System.currentTimeMillis();
      Thread.sleep(10);
      long time2 = Long.parseLong(config.getAnonymousTaskSuffixSupplier().get());
      assert time2 <= System.currentTimeMillis();
      assert time2 > time1;
   }

   public void testIncrementingTimeSupplier() throws InterruptedException {
      InitScriptConfigurationForTasks config = InitScriptConfigurationForTasks.create()
               .appendIncrementingNumberToAnonymousTaskNames();
      assertEquals(config.getAnonymousTaskSuffixSupplier().get(), "0");
      assertEquals(config.getAnonymousTaskSuffixSupplier().get(), "1");
   }

}
