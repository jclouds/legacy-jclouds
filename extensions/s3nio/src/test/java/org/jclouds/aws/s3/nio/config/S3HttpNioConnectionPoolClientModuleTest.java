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
package org.jclouds.aws.s3.nio.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import org.apache.http.nio.protocol.NHttpRequestExecutionHandler;
import org.jclouds.aws.s3.nio.S3HttpNioFutureCommandExecutionHandler;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
@Test
public class S3HttpNioConnectionPoolClientModuleTest {

    public void testConfigureBindsS3Handler() {
        final Properties properties = new Properties();
        properties.put("jclouds.http.address", "localhost");
        properties.put("jclouds.http.port", "8088");
        properties.put("jclouds.http.secure", "false");
        properties.setProperty("jclouds.http.pool.max_connection_reuse", "75");
        properties.setProperty("jclouds.http.pool.max_session_failures", "2");
        properties.setProperty("jclouds.http.pool.request_invoker_threads", "1");
        properties.setProperty("jclouds.http.pool.io_worker_threads", "2");
        properties.setProperty("jclouds.pool.max_connections", "12");

        Injector i = Guice.createInjector(new S3HttpNioConnectionPoolClientModule() {
            @Override
            protected void configure() {
                Names.bindProperties(binder(), properties);
                super.configure();
            }
        });
        NHttpRequestExecutionHandler handler = i.getInstance(NHttpRequestExecutionHandler.class);
        assert handler instanceof S3HttpNioFutureCommandExecutionHandler;
    }
}
