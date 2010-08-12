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

package org.jclouds.chef.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.ChefClient;
import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContextFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code ChefService} strategies
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "chef.BaseChefStrategyLiveTest")
public abstract class BaseChefStrategyLiveTest {

   protected Injector injector;

   protected String prefix = System.getProperty("user.name") + getClass().getSimpleName();

   @BeforeTest(groups = { "live" })
   public void setupClient() throws IOException {
      String endpoint = checkNotNull(System.getProperty("jclouds.test.endpoint"), "jclouds.test.endpoint");
      String user = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String keyfile = System.getProperty("jclouds.test.credential");
      if (keyfile == null || keyfile.equals(""))
         keyfile = System.getProperty("user.home") + "/.chef/" + user + ".pem";
      Properties props = new Properties();
      props.setProperty("chef.endpoint", endpoint);
      Set<Module> modules = Sets.newHashSet();
      modules.add(new Log4JLoggingModule());
      addTestModulesTo(modules);
      injector = new RestContextFactory().<ChefClient, ChefAsyncClient> createContextBuilder("chef", user,
            Files.toString(new File(keyfile), Charsets.UTF_8), modules, props).buildInjector();
   }

   protected void addTestModulesTo(Set<Module> modules) {

   }

   @AfterTest(groups = { "live" })
   public void teardownClient() throws IOException {
      if (injector != null)
         injector.getInstance(Closer.class).close();
   }
}
