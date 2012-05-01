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
package org.jclouds.demo.paas.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.inject.name.Names.bindProperties;
import static org.jclouds.Constants.*;

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.demo.tweetstore.config.util.PropertiesLoader;

import com.google.inject.AbstractModule;
import com.sun.jersey.api.uri.UriBuilderImpl;

/**
 * @author Andrew Phillips
 */
public class HttpClientModule extends AbstractModule {
    private final ServletContext context;

    HttpClientModule(ServletContext context) {
        this.context = context;
    }

    @Override
    protected void configure() {
        // URL connection defaults
        Properties toBind = defaultProperties();
        toBind.putAll(checkNotNull(new PropertiesLoader(context).get(), "properties"));
        toBind.putAll(System.getProperties());
        bindProperties(binder(), toBind);
        bind(UriBuilder.class).to(UriBuilderImpl.class);
    }

    private static Properties defaultProperties() {
        Properties props = new Properties();
        props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 20 + "");
        props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 0 + "");
        props.setProperty(PROPERTY_SO_TIMEOUT, 60000 + "");
        props.setProperty(PROPERTY_CONNECTION_TIMEOUT, 60000 + "");
        props.setProperty(PROPERTY_USER_THREADS, 0 + "");
        props.setProperty(PROPERTY_IO_WORKER_THREADS, 20 + "");
        return props;
     }
}
