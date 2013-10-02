/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.http;

import static org.jclouds.Constants.PROPERTY_IO_WORKER_THREADS;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_CONTEXT;
import static org.jclouds.Constants.PROPERTY_MAX_CONNECTIONS_PER_HOST;
import static org.jclouds.Constants.PROPERTY_USER_THREADS;

import java.util.Properties;

import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.http.internal.JavaUrlHttpCommandExecutorService;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Tests the functionality of the {@link JavaUrlHttpCommandExecutorService}
 * 
 * @author Adrian Cole
 */
@Test
public class JavaUrlHttpCommandExecutorServiceIntegrationTest extends BaseHttpCommandExecutorServiceIntegrationTest {

   protected Module createConnectionModule() {
      return new JavaUrlHttpCommandExecutorServiceModule();
   }

   protected void addConnectionProperties(Properties props) {
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_CONTEXT, 50 + "");
      props.setProperty(PROPERTY_MAX_CONNECTIONS_PER_HOST, 0 + "");
      // IO workers not used in this executor
      props.setProperty(PROPERTY_IO_WORKER_THREADS, 0 + "");
      props.setProperty(PROPERTY_USER_THREADS, 5 + "");
   }

}
