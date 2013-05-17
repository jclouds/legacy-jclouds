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
package org.jclouds.enterprise.config;

import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.date.joda.config.JodaDateServiceModule;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.netty.config.NettyPayloadModule;

import com.google.common.util.concurrent.ListeningExecutorService;

/**
 * Configures Enterprise-grade components
 * 
 * @author Adrian Cole
 * 
 */
@ConfiguresExecutorService
public class EnterpriseConfigurationModule extends ExecutorServiceModule {

   public EnterpriseConfigurationModule(ListeningExecutorService userExecutor, ListeningExecutorService ioExecutor) {
      super(userExecutor, ioExecutor);
   }

   public EnterpriseConfigurationModule() {
      super();
   }

   @Override
   protected void configure() {
      install(new BouncyCastleCryptoModule());
      install(new JodaDateServiceModule());
      install(new NettyPayloadModule());
   }

}
