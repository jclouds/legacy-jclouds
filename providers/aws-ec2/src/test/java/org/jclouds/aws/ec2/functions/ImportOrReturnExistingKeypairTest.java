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
package org.jclouds.aws.ec2.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.jclouds.ssh.SshKeys.fingerprintPublicKey;
import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.AWSEC2Api;
import org.jclouds.aws.ec2.features.AWSKeyPairApi;
import org.jclouds.ec2.domain.KeyPair;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ImportOrReturnExistingKeypairTest {
   private static final String PUBLIC_KEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCcm8DjTHg4r72dVhNLQ33XpUyMLr+ph78i4NR3LqF1bXDP0g4xNLcI/GUTQq6g07X8zs7vIWyjoitqBPFSQ2onaZQ6pXQF/QISRQgrN5hEZ+nH5Aw+isdstBeOMWKdYrCJtm6/qWq2+rByyuNbtulazP3H7SqoozSjRSGNQyFNGpmhjGgTbNQftYDwlFq0T9tCSO/+dYF8j79bNIOEmfsCMiqXQ13hD5vGiEgkRm7zIPDUfpOl3ubDzebpRgGTh5kfv2vd3Z665AxQoi6fItvDu80knyphMlC41giIm5YqfPOPG4lR+6aF06p+NKhvOeECNMtRsD9u1kKJD9NqxXhx";

   private static final KeyPair pair = KeyPair.builder().region("region").keyName("jclouds#group").sha1OfPrivateKey(
            "foo").build();
   private static final KeyPair pairWithFingerprint = pair.toBuilder().fingerprint(fingerprintPublicKey(PUBLIC_KEY))
            .build();

   @Test
   public void testApply() {
      AWSEC2Api client = createMock(AWSEC2Api.class);
      AWSKeyPairApi keyApi = createMock(AWSKeyPairApi.class);

      expect(client.getKeyPairApi()).andReturn((Optional) Optional.of(keyApi)).atLeastOnce();

      expect(keyApi.importKeyPairInRegion("region", "jclouds#group", PUBLIC_KEY)).andReturn(pair);

      replay(client);
      replay(keyApi);

      ImportOrReturnExistingKeypair parser = new ImportOrReturnExistingKeypair(client);

      assertEquals(parser.importOrReturnExistingKeypair("region", "group", PUBLIC_KEY), pairWithFingerprint);

      verify(client);
      verify(keyApi);
   }

   @Test
   public void testApplyWithIllegalStateExceptionReturnsExistingKey() {
      AWSEC2Api client = createMock(AWSEC2Api.class);
      AWSKeyPairApi keyApi = createMock(AWSKeyPairApi.class);

      expect(client.getKeyPairApi()).andReturn((Optional) Optional.of(keyApi)).atLeastOnce();

      expect(keyApi.importKeyPairInRegion("region", "jclouds#group", PUBLIC_KEY)).andThrow(
               new IllegalStateException());
      expect(keyApi.describeKeyPairsInRegion("region", "jclouds#group")).andReturn(ImmutableSet.of(pair));

      replay(client);
      replay(keyApi);

      ImportOrReturnExistingKeypair parser = new ImportOrReturnExistingKeypair(client);

      // enriching to include the ssh fingerprint so that ssh logs are easier to correlate
      assertEquals(parser.importOrReturnExistingKeypair("region", "group", PUBLIC_KEY), pairWithFingerprint);

      verify(client);
      verify(keyApi);

   }

   @Test
   public void testApplyWithIllegalStateExceptionRetriesWhenExistingKeyNotFound() {
      AWSEC2Api client = createMock(AWSEC2Api.class);
      AWSKeyPairApi keyApi = createMock(AWSKeyPairApi.class);

      expect(client.getKeyPairApi()).andReturn((Optional) Optional.of(keyApi)).atLeastOnce();

      expect(keyApi.importKeyPairInRegion("region", "jclouds#group", PUBLIC_KEY)).andThrow(
               new IllegalStateException());
      expect(keyApi.describeKeyPairsInRegion("region", "jclouds#group")).andReturn(ImmutableSet.<KeyPair> of());
      expect(keyApi.importKeyPairInRegion("region", "jclouds#group", PUBLIC_KEY)).andThrow(
               new IllegalStateException());
      expect(keyApi.describeKeyPairsInRegion("region", "jclouds#group")).andReturn(ImmutableSet.<KeyPair> of(pair));

      replay(client);
      replay(keyApi);

      ImportOrReturnExistingKeypair parser = new ImportOrReturnExistingKeypair(client);

      assertEquals(parser.importOrReturnExistingKeypair("region", "group", PUBLIC_KEY), pairWithFingerprint);

      verify(client);
      verify(keyApi);

   }
}
