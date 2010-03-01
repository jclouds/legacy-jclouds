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
package org.jclouds.gogrid.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Provides;
import org.jclouds.Constants;
import org.jclouds.gogrid.config.GoGridContextModule;
import org.jclouds.gogrid.domain.*;
import org.jclouds.gogrid.functions.internal.CustomDeserializers;
import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.inject.Singleton;

/**
 * Tests behavior of {@code ParseStatusesFromJsonResponse}
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "gogrid.ParseServersFromJsonResponseTest")
public class ParseServersFromJsonResponseTest {

    @Test
    public void testApplyInputStreamDetails() throws UnknownHostException {
        InputStream is = getClass().getResourceAsStream("/test_get_server_list.json");

        ParseServerListFromJsonResponse parser = new ParseServerListFromJsonResponse(i
                .getInstance(Gson.class));
        SortedSet<Server> response = parser.apply(is);

        Option centOs = new Option(13L, "CentOS 5.2 (32-bit)", "CentOS 5.2 (32-bit)");
        Option webServer = new Option(1L, "Web Server", "Web or Application Server");
        Server server = new Server(75245L, false, "PowerServer", "server to test the api. created by Alex",
                new Option(1L, "On", "Server is in active state."),
                webServer,
                new Option(1L, "512MB", "Server with 512MB RAM"),
                centOs,
                new Ip(1313079L, "204.51.240.178", "204.51.240.176/255.255.255.240", true,
                        IpState.ASSIGNED),
                new ServerImage(1946L, "GSI-f8979644-e646-4711-ad58-d98a5fa3612c",
                        "BitNami Gallery 2.3.1-0", "http://bitnami.org/stack/gallery",
                        centOs, null, webServer,
                        new Option(2L, "Available", "Image is available for adds"),
                        0.0, "24732/GSI-f8979644-e646-4711-ad58-d98a5fa3612c.img",
                        true, true,
                        new Date(1261504577971L),
                        new Date(1262649582180L),
                        Arrays.asList(
                                new BillingToken(38L, "CentOS 5.2 32bit", 0.0),
                                new BillingToken(56L, "BitNami: Gallery", 0.0)
                        ),
                        new Customer(24732L, "BitRock")));
        assertEquals(Iterables.getOnlyElement(response), server);
    }


    Injector i = Guice.createInjector(new ParserModule() {
        @Override
        protected void configure() {
            bind(DateAdapter.class).to(GoGridContextModule.DateSecondsAdapter.class);
            super.configure();
        }


        @Provides
        @Singleton
        @com.google.inject.name.Named(Constants.PROPERTY_GSON_ADAPTERS)
        public Map<Class, Object> provideCustomAdapterBindings() {
            Map<Class, Object> bindings = Maps.newHashMap();
            bindings.put(IpState.class, new CustomDeserializers.IpStateAdapter());
            return bindings;
        }
    });
}
