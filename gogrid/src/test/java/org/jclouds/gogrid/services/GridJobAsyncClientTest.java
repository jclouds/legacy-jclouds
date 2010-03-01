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
package org.jclouds.gogrid.services;

import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.gogrid.GoGrid;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseJobListFromJsonResponse;
import org.jclouds.gogrid.options.GetJobListOptions;
import org.jclouds.logging.Logger;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Date;

import static org.testng.Assert.assertEquals;

/**
 * @author Oleksiy Yarmula
 */
public class GridJobAsyncClientTest extends RestClientTest<GridJobAsyncClient> {

    @Test
    public void testGetJobListWithOptions() throws NoSuchMethodException, IOException {
        Method method = GridJobAsyncClient.class.getMethod("getJobList", GetJobListOptions[].class);
        GeneratedHttpRequest<GridJobAsyncClient> httpRequest = processor.createRequest(method,
                                    new GetJobListOptions.Builder().
                                            create().
                                            withStartDate(new Date(1267385381770L)));

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/job/list?v=1.3&startdate=1267385381770 HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseJobListFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                    "GET https://api.gogrid.com/api/grid/job/list?v=1.3&startdate=1267385381770&" +
                            "sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                            "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }


    @Test
    public void testGetJobsForServerName() throws NoSuchMethodException, IOException {
        Method method = GridJobAsyncClient.class.getMethod("getJobsForObjectName", String.class);
        GeneratedHttpRequest<GridJobAsyncClient> httpRequest = processor.createRequest(method,
                                                                    "MyServer");

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/job/list?v=1.3&" +
                        "object=MyServer HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseJobListFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                    "GET https://api.gogrid.com/api/grid/job/list?v=1.3&" +
                            "object=MyServer&sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                            "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }



    @Test
    public void testGetJobsById() throws NoSuchMethodException, IOException {
        Method method = GridJobAsyncClient.class.getMethod("getJobsById", Long[].class);
        GeneratedHttpRequest<GridJobAsyncClient> httpRequest = processor.createRequest(method,
                                                                    123L, 456L);

        assertRequestLineEquals(httpRequest,
                "GET https://api.gogrid.com/api/grid/job/get?v=1.3&" +
                        "id=123&id=456 HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);

        assertResponseParserClassEquals(method, httpRequest, ParseJobListFromJsonResponse.class);
        assertSaxResponseParserClassEquals(method, null);
        assertExceptionParserClassEquals(method, null);

        checkFilters(httpRequest);
        Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

        assertRequestLineEquals(httpRequest,
                    "GET https://api.gogrid.com/api/grid/job/get?v=1.3&" +
                            "id=123&id=456&sig=3f446f171455fbb5574aecff4997b273&api_key=foo " +
                            "HTTP/1.1");
        assertHeadersEqual(httpRequest, "");
        assertPayloadEquals(httpRequest, null);
    }

    @Override
    protected void checkFilters(GeneratedHttpRequest<GridJobAsyncClient> httpMethod) {
        assertEquals(httpMethod.getFilters().size(), 1);
        assertEquals(httpMethod.getFilters().get(0).getClass(), SharedKeyLiteAuthentication.class);
    }

    @Override
    protected TypeLiteral<RestAnnotationProcessor<GridJobAsyncClient>> createTypeLiteral() {
        return new TypeLiteral<RestAnnotationProcessor<GridJobAsyncClient>>() {
        };
    }

    @Override
    protected Module createModule() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(URI.class).annotatedWith(GoGrid.class).toInstance(
                        URI.create("https://api.gogrid.com/api"));
                bind(Logger.LoggerFactory.class).toInstance(new Logger.LoggerFactory() {
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
