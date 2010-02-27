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
/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseServerListFromJsonResponse;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.gogrid.config.GoGridRestClientModule;
import org.jclouds.gogrid.reference.GoGridConstants;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of modules configured in GoGridContextBuilder
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "gogrid.GoGridContextBuilderTest")
public class GoGridContextBuilderTest {

    public void testNewBuilder() {
        GoGridContextBuilder builder = newBuilder();
        assertEquals(builder.getProperties().getProperty(GoGridConstants.PROPERTY_GOGRID_ENDPOINT),
                "https://api.gogrid.com/api");
        assertEquals(builder.getProperties().getProperty(GoGridConstants.PROPERTY_GOGRID_USER),
                "user");
        assertEquals(builder.getProperties().getProperty(GoGridConstants.PROPERTY_GOGRID_PASSWORD),
                "password");
    }

    public void testBuildContext() {
        RestContext<GoGridAsyncClient, GoGridClient> context = newBuilder().buildContext();
        assertEquals(context.getClass(), RestContextImpl.class);
        assertEquals(context.getAccount(), "user");
        assertEquals(context.getEndPoint(), URI.create("https://api.gogrid.com/api"));
    }

    public void testBuildInjector() {
        Injector i = newBuilder().buildInjector();
        assert i.getInstance(Key.get(new TypeLiteral<RestContext<GoGridAsyncClient, GoGridClient>>() {
        })) != null;
        assert i.getInstance(SharedKeyLiteAuthentication.class) != null;
        assert i.getInstance(ParseServerListFromJsonResponse.class) != null;        
    }

    protected void testAddContextModule() {
        List<Module> modules = new ArrayList<Module>();
        GoGridContextBuilder builder = newBuilder();
        builder.addContextModule(modules);
        assertEquals(modules.size(), 1);
        assertEquals(modules.get(0).getClass(), GoGridRestClientModule.class);
    }

    private GoGridContextBuilder newBuilder() {
        GoGridContextBuilder builder = new GoGridContextBuilder(new GoGridPropertiesBuilder(
                "user", "password").build());
        return builder;
    }

    protected void addClientModule() {
        List<Module> modules = new ArrayList<Module>();
        GoGridContextBuilder builder = newBuilder();
        builder.addClientModule(modules);
        assertEquals(modules.size(), 1);
        assertEquals(modules.get(0).getClass(), GoGridRestClientModule.class);
    }

}
