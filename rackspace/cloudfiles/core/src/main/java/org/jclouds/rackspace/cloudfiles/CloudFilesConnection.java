/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.rackspace.cloudfiles;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Provides access to Cloud Files via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Files. Any exceptions incurred during
 * processing will be wrapped in an {@link ExecutionException} as documented in {@link Future#get()}.
 * 
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090311.pdf" />
 * @author Adrian Cole
 */
public interface CloudFilesConnection {

}
