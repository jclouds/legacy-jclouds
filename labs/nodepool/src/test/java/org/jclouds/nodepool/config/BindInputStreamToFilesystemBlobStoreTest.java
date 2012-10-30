/*
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
package org.jclouds.nodepool.config;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * 
 */
@Test(groups = "unit", testName = "BindInputStreamToFilesystemBlobStoreTest")
public class BindInputStreamToFilesystemBlobStoreTest {

   public void testCreatesDir() {
      final String basedir = "target/" + this.getClass().getSimpleName();
      new File(basedir).delete();
      Map<String, InputStream> file = Guice
               .createInjector(new AbstractModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(Names.named(NodePoolProperties.BASEDIR)).to(basedir);
                     bindConstant().annotatedWith(Names.named(NodePoolProperties.METADATA_CONTAINER)).to("barr");
                     bindConstant().annotatedWith(Names.named(NodePoolProperties.POOL_ADMIN_ACCESS)).to(
                              "adminUsername=pooluser,adminPassword=poolpass");
                     bindConstant().annotatedWith(Names.named(NodePoolProperties.BACKEND_MODULES)).to(
                              SLF4JLoggingModule.class.getName());
                  }
               }, new BindInputStreamToFilesystemBlobStore())
               .getInstance(Key.get(new TypeLiteral<Supplier<Map<String, InputStream>>>() {
               }, Names.named("METADATA"))).get();

      assert (new File(basedir + "/barr").exists());
      assertEquals(file.keySet(), ImmutableSet.of());
      new File(basedir).delete();
   }

}
