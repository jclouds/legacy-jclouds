/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.config;

import static com.google.common.util.concurrent.Executors.sameThreadExecutor;
import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.EC2AsyncClient;
import org.jclouds.aws.ec2.EC2Client;
import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.util.Jsr330;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.EC2ContextModuleTest")
public class EC2ContextModuleTest {

   Injector createInjector() {
      return Guice.createInjector(new ExecutorServiceModule(sameThreadExecutor()),
               new EC2RestClientModule(), new RestModule(),
               new JavaUrlHttpCommandExecutorServiceModule(), new JDKLoggingModule(),
               new EC2ContextModule() {
                  @Override
                  protected void configure() {
                     bindConstant().annotatedWith(
                              Jsr330.named(EC2Constants.PROPERTY_AWS_ACCESSKEYID)).to("user");
                     bindConstant().annotatedWith(
                              Jsr330.named(EC2Constants.PROPERTY_AWS_SECRETACCESSKEY)).to("key");
                     bindConstant().annotatedWith(Jsr330.named(EC2Constants.PROPERTY_EC2_ENDPOINT))
                              .to("http://localhost");
                     bindConstant().annotatedWith(
                              Jsr330.named(EC2Constants.PROPERTY_EC2_EXPIREINTERVAL)).to(30);
                     super.configure();
                  }
               });
   }

   @Test
   void testContextImpl() {
      RestContext<EC2AsyncClient, EC2Client> handler = createInjector().getInstance(
               Key.get(new TypeLiteral<RestContext<EC2AsyncClient, EC2Client>>() {
               }));
      assertEquals(handler.getClass(), RestContextImpl.class);
   }

}