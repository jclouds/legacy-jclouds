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
package org.jclouds.aws.s3;

import org.jclouds.date.joda.config.JodaDateServiceModule;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleEncryptionServiceModule;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.httpnio.config.NioTransformingHttpCommandExecutorServiceModule;
import org.testng.annotations.Test;

@Test(sequential = true, testName = "perftest.JCloudsNioPerformanceLiveTest", groups = { "live" })
public class JCloudsNioPerformanceLiveTest extends BaseJCloudsPerformanceLiveTest {

   @ConfiguresHttpCommandExecutorService
   private static final class Module extends NioTransformingHttpCommandExecutorServiceModule {
      @Override
      protected void configure() {
         super.configure();
         install(new JodaDateServiceModule());
         install(new BouncyCastleEncryptionServiceModule());
      }
   }

   @Override
   protected Module createHttpModule() {
      return new Module();
   }
}