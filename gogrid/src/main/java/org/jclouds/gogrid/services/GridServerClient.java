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

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.gogrid.domain.Server;

/**
 * Provides synchronous access to GoGrid.
 * <p/>
 *
 * @see GridServerAsyncClient
 * @see <a href="http://wiki.gogrid.com/wiki/index.php/API" />
 *
 * @author Adrian Cole
 * @author Oleksiy Yarmula
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface GridServerClient {

    Set<Server> getServerList();

    /**
     * Retrieves the server(s) by unique name(s).
     *
     * Given a name or a set of names, finds one or
     * multiple servers.
     * @param names to get the servers
     * @return server(s) matching the name(s)
     */
    Set<Server> getServersByName(String... names);

    /**
     * Retrieves the server(s) by unique id(s).
     *
     * Given an id or a set of ids, finds one or
     * multiple servers. 
     * @param ids to get the servers
     * @return server(s) matching the ids
     */
    Set<Server> getServersById(Long... ids);


}
