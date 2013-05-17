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
package org.jclouds.sts;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.testng.Assert.assertTrue;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.sts.domain.UserAndSessionCredentials;
import org.jclouds.sts.internal.BaseSTSApiLiveTest;
import org.jclouds.sts.options.AssumeRoleOptions;
import org.jclouds.sts.options.FederatedUserOptions;
import org.jclouds.sts.options.SessionCredentialsOptions;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "STSApiLiveTest")
public class STSApiLiveTest extends BaseSTSApiLiveTest {

   @Test
   protected void testCreateTemporaryCredentials() {
      SessionCredentials creds = api.createTemporaryCredentials(
            new SessionCredentialsOptions().durationSeconds(MINUTES.toSeconds(15)));
      checkTemporaryCredentials(creds);
      // TODO: actually login to some service
      //
      //      context.close();
      //      ProviderMetadata pm = createProviderMetadata();
      //
      //      context = (pm != null ? ContextBuilder.newBuilder(pm) : ContextBuilder.newBuilder(createApiMetadata()))
      //            .credentialsSupplier(Supplier.<Credentials> of(creds)).modules(setupModules()).build();
   }

   @Test
   protected void testCreateFederatedUser() {
      UserAndSessionCredentials user = api.createFederatedUser("Bob", new FederatedUserOptions().durationSeconds(MINUTES.toSeconds(15)));
      checkTemporaryCredentials(user.getCredentials());
      assertTrue(user.getUser().getId().contains("Bob"), user + " id incorrect");
      assertTrue(user.getUser().getArn().contains("Bob"), user + " arn incorrect");
      assertTrue(user.getPackedPolicySize() >= 0, user + " policy size negative");
   }

   @Test
   protected void testAssumeRole() {
      String arnToAssume = getTestArn();
      UserAndSessionCredentials role = api.assumeRole(arnToAssume, "session",
            new AssumeRoleOptions().durationSeconds(MINUTES.toSeconds(15)));
      checkTemporaryCredentials(role.getCredentials());
      assertTrue(role.getUser().getId().contains("session"), role + " id incorrect");
      assertTrue(role.getUser().getArn().contains("session"), role + " arn incorrect");
      assertTrue(role.getPackedPolicySize() >= 0, role + " policy size negative");
   }

   protected String getTestArn() {
      throw new SkipException("TODO: need to query a valid arn to assume");
   }

   private void checkTemporaryCredentials(SessionCredentials creds) {
      checkNotNull(creds.getAccessKeyId(), "AccessKeyId cannot be null for TemporaryCredentials.");
      checkNotNull(creds.getSecretAccessKey(), "SecretAccessKey cannot be null for TemporaryCredentials.");
      checkNotNull(creds.getSessionToken(), "SessionToken cannot be null for TemporaryCredentials.");
      checkNotNull(creds.getExpiration(), "Expiration cannot be null for TemporaryCredentials.");
   }
}
