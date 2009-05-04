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

import org.apache.http.nio.protocol.NHttpRequestExecutionHandler;
import org.jclouds.aws.s3.nio.S3HttpNioFutureCommandExecutionHandler;
import org.jclouds.http.config.HttpFutureCommandClientModule;
import org.jclouds.http.httpnio.config.HttpNioConnectionPoolClientModule;

import com.google.inject.AbstractModule;
import com.google.inject.util.Modules;

/**
 * This installs a {@link HttpNioConnectionPoolClientModule}, but overrides it
 * binding {@link S3HttpNioFutureCommandExecutionHandler}.
 * 
 * @author Adrian Cole
 */
@HttpFutureCommandClientModule
public class S3HttpNioConnectionPoolClientModule extends AbstractModule {
    protected void configure() {
	install(Modules.override(new HttpNioConnectionPoolClientModule()).with(
		new AbstractModule() {
		    protected void configure() {
			bind(NHttpRequestExecutionHandler.class).to(
				S3HttpNioFutureCommandExecutionHandler.class);
		    }
		}));
    }
}
