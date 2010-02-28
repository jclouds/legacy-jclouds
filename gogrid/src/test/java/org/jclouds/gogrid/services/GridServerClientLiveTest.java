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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.GoGridContextFactory;
import org.jclouds.gogrid.domain.PowerCommand;
import org.jclouds.gogrid.domain.Server;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.util.Set;
import static org.testng.Assert.assertNotNull;


/**
 * Tests behavior of {@code GoGridClient}
 *
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "gogrid.GoGridClientLiveTest")
public class GridServerClientLiveTest {

    private GoGridClient client;

    @BeforeGroups(groups = { "live" })
    public void setupClient() {
        String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
        String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

        client = GoGridContextFactory.createContext(user, password, new Log4JLoggingModule())
                .getApi();
    }

    @Test
    public void testGetServerList() {
        Set<Server> response = client.getServerClient().getServerList();
        assert (response.size() > 0);
    }

    @Test
    public void testGetServerByName() {
        Set<Server> response = client.getServerClient().getServersByName("PowerServer", "AnotherTestServer");
        assert (response.size() > 0);
    }

    @Test
    public void testGetServerById() {
        Set<Server> response = client.getServerClient().getServersById(75245L, 75523L);
        assert (response.size() > 0);
    }

    @Test
    public void testAddServer() {
        Server createdServer = client.getServerClient().addServer("ServerCreatedFromAPI",
                                        "GSI-f8979644-e646-4711-ad58-d98a5fa3612c",
                                        "1",
                                        "204.51.240.189");
        assertNotNull(createdServer);
    }

    @Test
    public void testRestart() {
        Server createdServer = client.getServerClient().power("PowerServer", PowerCommand.RESTART);
        assertNotNull(createdServer);
    }

}
