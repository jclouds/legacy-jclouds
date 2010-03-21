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
package org.jclouds.gogrid.services;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;

import javax.inject.Singleton;

import com.google.common.collect.Iterables;
import org.jclouds.gogrid.GoGrid;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseOptionsFromJsonResponse;
import org.jclouds.gogrid.functions.ParseServerFromJsonResponse;
import org.jclouds.gogrid.options.AddServerOptions;
import org.jclouds.gogrid.options.GetServerListOptions;
import org.jclouds.gogrid.services.GridServerAsyncClient;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.gogrid.functions.ParseServerListFromJsonResponse;
import org.jclouds.encryption.EncryptionService;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code GoGridAsyncClient}
 *
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
@Test(groups = "unit", testName = "gogrid.GoGridAsyncClientTest")
public class GridServerAsyncClientTest extends RestClientTest<GridServerAsyncClient> {

    @Test
    public void testGetServerListNoOptions() throws NoSuchMethodException, IOException {
        Method method = GridServerAsyncClient.class.getMethod("getServerList", GetServerListOptions[].class);
        GeneratedHttpRequest<GridServerAsyncClient> httpRequest = processor.createRequest(method);

        assertRequestLineEquals(httpRequest, "GET https://api.gogrid.com/api/grid/server/list?v=1.3 HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseServerListFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                    "GET https://api.gogrid.com/api/grid/server/list?" +
                            "v=1.3&sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                            "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }

    @Test
    public void testGetServerListWithOptions() throws NoSuchMethodException, IOException {
        Method method = GridServerAsyncClient.class.getMethod("getServerList", GetServerListOptions[].class);
        GeneratedHttpRequest<GridServerAsyncClient> httpRequest = processor.createRequest(method,
                                    new GetServerListOptions.Builder().onlySandboxServers());

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/list?v=1.3&isSandbox=true HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseServerListFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                    "GET https://api.gogrid.com/api/grid/server/list?" +
                            "v=1.3&isSandbox=true&sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                            "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }


    @Test
    public void testGetServersByName() throws NoSuchMethodException, IOException {
        Method method = GridServerAsyncClient.class.getMethod("getServersByName", String[].class);
        GeneratedHttpRequest<GridServerAsyncClient> httpRequest = processor.createRequest(method, "server1");

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/get?v=1.3&name=server1 HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseServerListFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/get?" +
                        "v=1.3&name=server1&" +
                        "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }


    @Test
    public void testGetServersById() throws NoSuchMethodException, IOException {
        Method method = GridServerAsyncClient.class.getMethod("getServersById", Long[].class);
        GeneratedHttpRequest<GridServerAsyncClient> httpRequest = processor.createRequest(method, 123L);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/get?v=1.3&id=123 HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseServerListFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/get?" +
                        "v=1.3&id=123&" +
                        "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }
    

    @Test
    public void testAddServerNoOptions() throws NoSuchMethodException, IOException {
        Method method = GridServerAsyncClient.class.getMethod("addServer", String.class, String.class,
                                                                String.class, String.class,
                                                                AddServerOptions[].class);
        GeneratedHttpRequest<GridServerAsyncClient> httpRequest =
                processor.createRequest(method, "serverName", "img55",
                                                              "memory", "127.0.0.1");

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/add?v=1.3&" +
                        "name=serverName&server.ram=memory&image=img55&ip=127.0.0.1 " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseServerFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/add?" +
                        "v=1.3&name=serverName&server.ram=memory&" +
                        "image=img55&ip=127.0.0.1&" +
                        "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }


    @Test
    public void testPowerServer() throws NoSuchMethodException, IOException {
        Method method = GridServerAsyncClient.class.getMethod("power", String.class, PowerCommand.class);
        GeneratedHttpRequest<GridServerAsyncClient> httpRequest =
                processor.createRequest(method, "PowerServer", PowerCommand.RESTART);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/power?v=1.3&" +
                        "server=PowerServer&power=restart " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseServerFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/power?v=1.3&" +
                        "server=PowerServer&power=restart&" +
                        "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }

    
    @Test
    public void testDeleteByName() throws NoSuchMethodException, IOException {
        Method method = GridServerAsyncClient.class.getMethod("deleteByName", String.class);
        GeneratedHttpRequest<GridServerAsyncClient> httpRequest =
                processor.createRequest(method, "PowerServer");

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/delete?v=1.3&" +
                        "name=PowerServer " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseServerFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/server/delete?v=1.3&" +
                        "name=PowerServer&" +
                        "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }


    @Test
    public void testGetRamSizes() throws NoSuchMethodException, IOException {
        Method method = GridServerAsyncClient.class.getMethod("getRamSizes");
        GeneratedHttpRequest<GridServerAsyncClient> httpRequest =
                processor.createRequest(method);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/common/lookup/list?v=1.3&lookup=server.ram " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseOptionsFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/common/lookup/list?v=1.3&lookup=server.ram&" +
                        "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                        "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }


    @Override
    protected void checkFilters(GeneratedHttpRequest<GridServerAsyncClient> httpMethod) {
        assertEquals(httpMethod.getFilters().size(), 1);
        assertEquals(httpMethod.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
    }

    @Override
    protected TypeLiteral<RestAnnotationProcessor<GridServerAsyncClient>> createTypeLiteral() {
        return new TypeLiteral<RestAnnotationProcessor<GridServerAsyncClient>>() {
        };
    }

    @Override
    protected Module createModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(URI.class).annotatedWith(GoGrid.class).toInstance(
                        URI.create("https://api.gogrid.com/api"));
                bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
                    public Logger getLogger(String category) {
                        return Logger.NULL;
                    }
                });
            }

            @Provides
            @Singleton
            public SharedKeyLiteAuthentication provideAuthentication(EncryptionService encryptionService)
                    throws UnsupportedEncodingException {
                return new SharedKeyLiteAuthentication("foo", "bar", 1267243795L, encryptionService);
            }
        };
    }
}
