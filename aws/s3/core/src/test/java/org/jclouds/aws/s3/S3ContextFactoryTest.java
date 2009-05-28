/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.s3;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import org.jclouds.aws.s3.config.LiveS3ConnectionModule;
import org.jclouds.http.config.HttpFutureCommandClientModule;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests behavior of modules configured in S3ContextFactory
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3ContextFactoryTest")
public class S3ContextFactoryTest {

    @HttpFutureCommandClientModule
    static class HttpModule extends AbstractModule {

        @Override
        protected void configure() {

        }
    }

    @Test
    public void testAddHttpModuleIfNotPresent() {
        List<Module> modules = new ArrayList<Module>();
        HttpModule module = new HttpModule();
        modules.add(module);
        S3ContextFactory.addHttpModuleIfNeededAndNotPresent(modules);
        assertEquals(modules.size(), 1);
        assertEquals(modules.remove(0), module);
    }

    @Test
    public void testAddLoggingModuleIfNotPresent() {
        List<Module> modules = new ArrayList<Module>();
        LoggingModule module = new NullLoggingModule();
        modules.add(module);
        S3ContextFactory.addLoggingModuleIfNotPresent(modules);
        assertEquals(modules.size(), 1);
        assertEquals(modules.remove(0), module);
    }

    @Test
    public void testAddNone() {
        List<Module> modules = new ArrayList<Module>();
        LoggingModule loggingModule = new NullLoggingModule();
        modules.add(loggingModule);
        HttpModule httpModule = new HttpModule();
        modules.add(httpModule);
        S3ContextFactory.addHttpModuleIfNeededAndNotPresent(modules);
        S3ContextFactory.addLoggingModuleIfNotPresent(modules);
        assertEquals(modules.size(), 2);
        assertEquals(modules.remove(0), loggingModule);
        assertEquals(modules.remove(0), httpModule);
    }

    @Test
    public void testAddBothWhenNotLive() {
        List<Module> modules = new ArrayList<Module>();
        S3ContextFactory.addHttpModuleIfNeededAndNotPresent(modules);
        S3ContextFactory.addLoggingModuleIfNotPresent(modules);
        assertEquals(modules.size(), 1);
        assert modules.remove(0) instanceof JDKLoggingModule;
    }

    @Test
    public void testAddBothWhenLive() {
        List<Module> modules = new ArrayList<Module>();
        modules.add(new LiveS3ConnectionModule());
        S3ContextFactory.addHttpModuleIfNeededAndNotPresent(modules);
        S3ContextFactory.addLoggingModuleIfNotPresent(modules);
        assertEquals(modules.size(), 3);
        assert modules.remove(0) instanceof LiveS3ConnectionModule;
        assert modules.remove(0) instanceof JavaUrlHttpFutureCommandClientModule;
        assert modules.remove(0) instanceof JDKLoggingModule;
    }
}
