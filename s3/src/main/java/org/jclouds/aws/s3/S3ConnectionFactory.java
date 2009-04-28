/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Names;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;

import java.util.Properties;

/**
 * // TODO: Adrian: Document return getConnection!
 *
 * @author Adrian Cole
 */
public class S3ConnectionFactory {
    public static final Properties DEFAULT_PROPERTIES;

    static {
        DEFAULT_PROPERTIES = new Properties();
        DEFAULT_PROPERTIES.setProperty("jclouds.http.address", "s3.amazonaws.com");
        DEFAULT_PROPERTIES.setProperty("jclouds.http.port", "443");
        DEFAULT_PROPERTIES.setProperty("jclouds.http.secure", "true");
        DEFAULT_PROPERTIES.setProperty("jclouds.http.pool.max_connection_reuse", "75");
        DEFAULT_PROPERTIES.setProperty("jclouds.http.pool.max_session_failures", "2");
        DEFAULT_PROPERTIES.setProperty("jclouds.http.pool.request_invoker_threads", "1");
        DEFAULT_PROPERTIES.setProperty("jclouds.http.pool.io_worker_threads", "2");
        DEFAULT_PROPERTIES.setProperty("jclouds.pool.max_connections", "12");
    }

    public static S3Connection getConnection(String awsAccessKeyId, String awsSecretAccessKey) {
        Properties properties = new Properties(DEFAULT_PROPERTIES);
        properties.setProperty("jclouds.aws.accesskeyid", awsAccessKeyId);
        properties.setProperty("jclouds.aws.secretaccesskey", awsSecretAccessKey);
        return getConnection(properties, new JavaUrlHttpFutureCommandClientModule());
    }

    public static S3Connection getConnection(String awsAccessKeyId, String awsSecretAccessKey, boolean isSecure) {
        Properties properties = new Properties(DEFAULT_PROPERTIES);
        properties.setProperty("jclouds.aws.accesskeyid", awsAccessKeyId);
        properties.setProperty("jclouds.aws.secretaccesskey", awsSecretAccessKey);
        properties.setProperty("jclouds.http.secure", Boolean.toString(isSecure));
        if (!isSecure)
            properties.setProperty("jclouds.http.port", "80");

        return getConnection(properties, new JavaUrlHttpFutureCommandClientModule());
    }

    public static S3Connection getConnection(String awsAccessKeyId, String awsSecretAccessKey, boolean isSecure,
                                             String server) {
        Properties properties = new Properties(DEFAULT_PROPERTIES);
        properties.setProperty("jclouds.aws.accesskeyid", awsAccessKeyId);
        properties.setProperty("jclouds.aws.secretaccesskey", awsSecretAccessKey);
        properties.setProperty("jclouds.http.secure", Boolean.toString(isSecure));
        properties.setProperty("jclouds.http.address", server);
        if (!isSecure)
            properties.setProperty("jclouds.http.port", "80");
        return getConnection(properties, new JavaUrlHttpFutureCommandClientModule());
    }

    public static S3Connection getConnection(String awsAccessKeyId, String awsSecretAccessKey, boolean isSecure,
                                             String server, int port) {
        Properties properties = new Properties(DEFAULT_PROPERTIES);
        properties.setProperty("jclouds.aws.accesskeyid", awsAccessKeyId);
        properties.setProperty("jclouds.aws.secretaccesskey", awsSecretAccessKey);
        properties.setProperty("jclouds.http.secure", Boolean.toString(isSecure));
        properties.setProperty("jclouds.http.address", server);
        properties.setProperty("jclouds.http.port", port + "");
        return getConnection(properties, new JavaUrlHttpFutureCommandClientModule());
    }

    /**
     * Create a new interface to interact with S3 with the given credential and connection
     * parameters
     */
    public static synchronized S3Connection getConnection(final Properties properties, Module httpModule) {
        return getInjector(properties, httpModule).getInstance(S3Connection.class);
    }

    /**
     * Create a new interface to interact with S3 with the given credential and connection
     * parameters
     */
    public static synchronized Injector getInjector(final Properties properties, Module httpModule) {
        return Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Names.bindProperties(binder(), properties);
            }
        }, httpModule, new S3ConnectionModule());
    }
}
