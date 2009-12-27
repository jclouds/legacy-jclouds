/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.EC2ContextFactory;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code MonitoringClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "ec2.MonitoringClientLiveTest")
public class MonitoringClientLiveTest {

   private MonitoringClient client;
   private static final String DEFAULT_INSTANCE = "i-TODO";
   private RestContext<EC2AsyncClient, EC2Client> context;

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String user = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String password = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");

      context = EC2ContextFactory.createContext(user, password, new Log4JLoggingModule());
      client = context.getApi().getMonitoringServices();
   }

   @Test(enabled = false)
   // TODO get instance
   public void testMonitorInstances() {
      Map<String, MonitoringState> monitoringState = client.monitorInstances(DEFAULT_INSTANCE);
      assertEquals(monitoringState.get(DEFAULT_INSTANCE), MonitoringState.PENDING);
   }

   @Test(enabled = false)
   // TODO get instance
   public void testUnmonitorInstances() {
      Map<String, MonitoringState> monitoringState = client.unmonitorInstances(DEFAULT_INSTANCE);
      assertEquals(monitoringState.get(DEFAULT_INSTANCE), MonitoringState.PENDING);
   }

}
