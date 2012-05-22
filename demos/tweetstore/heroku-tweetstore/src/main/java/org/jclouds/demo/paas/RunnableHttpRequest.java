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
package org.jclouds.demo.paas;

import static java.lang.String.format;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpCommandExecutorService;
import org.jclouds.http.HttpRequest;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

public class RunnableHttpRequest implements Runnable {
    public static final String PLATFORM_REQUEST_ORIGINATOR_HEADER = "X-Platform-Originator";

    public static Factory factory(HttpCommandExecutorService httpClient) {
        return factory(httpClient, format("%s@%d", Factory.class.getName(), System.currentTimeMillis()));
    }

    public static Factory factory(HttpCommandExecutorService httpClient, String originator) {
        return new Factory(httpClient, originator);
    }

    public static class Factory {
        protected final HttpCommandExecutorService httpClient;
        protected final String originator;

        private Factory(HttpCommandExecutorService httpClient, String originator) {
            this.httpClient = httpClient;
            this.originator = originator;
        }

        public RunnableHttpRequest create(HttpRequest request) {
            HttpRequest requestWithSubmitter = request.toBuilder()
                    .headers(copyOfWithEntry(request.getHeaders(), 
                            PLATFORM_REQUEST_ORIGINATOR_HEADER, originator)).build();
            return new RunnableHttpRequest(httpClient, requestWithSubmitter);
        }

        private static <K, V> Multimap<K, V> copyOfWithEntry(
                Multimap<? extends K, ? extends V> multimap, K k1, V v1) {
            return ImmutableMultimap.<K, V>builder().putAll(multimap).put(k1, v1).build();
        }
    }

    private final HttpCommandExecutorService httpClient;
    private final HttpRequest request;

    private RunnableHttpRequest(HttpCommandExecutorService httpClient, HttpRequest request) {
        this.httpClient = httpClient;
        this.request = request;
    }

    @Override
    public void run() {
        httpClient.submit(new ImmutableHttpCommand(request));
    }

    private class ImmutableHttpCommand implements HttpCommand {
        private final HttpRequest request;

        public ImmutableHttpCommand(HttpRequest request) {
            this.request = request;
        }

        @Override
        public void setException(Exception exception) {
        }

        @Override
        public void setCurrentRequest(HttpRequest request) {
        }

        @Override
        public boolean isReplayable() {
            return false;
        }

        @Override
        public int incrementRedirectCount() {
            return 0;
        }

        @Override
        public int incrementFailureCount() {
            return 0;
        }

        @Override
        public int getRedirectCount() {
            return 0;
        }

        @Override
        public int getFailureCount() {
            return 0;
        }

        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public HttpRequest getCurrentRequest() {
            return request;
        }
    }
}
