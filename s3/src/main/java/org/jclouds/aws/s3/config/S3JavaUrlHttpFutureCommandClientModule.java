/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package org.jclouds.aws.s3.config;

import org.jclouds.aws.s3.internal.S3JavaUrlHttpFutureCommandClient;
import org.jclouds.http.HttpFutureCommandClient;
import org.jclouds.http.config.HttpFutureCommandClientModule;
import org.jclouds.http.config.JavaUrlHttpFutureCommandClientModule;

/**
 * Configures {@link S3JavaUrlHttpFutureCommandClient}.
 * 
 * @author Adrian Cole
 */
@HttpFutureCommandClientModule
public class S3JavaUrlHttpFutureCommandClientModule extends
	JavaUrlHttpFutureCommandClientModule {

    @Override
    protected void bindClient() {
	// note this is not threadsafe, so it cannot be singleton
	bind(HttpFutureCommandClient.class).to(
		S3JavaUrlHttpFutureCommandClient.class);
    }
}
