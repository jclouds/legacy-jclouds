/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.net.UnknownHostException;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.services.AWSKeyPairClient;
import org.jclouds.ec2.domain.KeyPair;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ImportOrReturnExistingKeypairTest {
   @Test
   public void testApply() throws UnknownHostException {
      AWSEC2Client client = createMock(AWSEC2Client.class);
      AWSKeyPairClient keyClient = createMock(AWSKeyPairClient.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairServices()).andReturn(keyClient).atLeastOnce();

      expect(keyClient.importKeyPairInRegion("region", "jclouds#group", "ssh-rsa")).andReturn(pair);

      replay(client);
      replay(keyClient);

      ImportOrReturnExistingKeypair parser = new ImportOrReturnExistingKeypair(client);

      assertEquals(parser.importOrReturnExistingKeypair("region", "group", "ssh-rsa"), pair);

      verify(client);
      verify(keyClient);
   }

   @Test
   public void testApplyWithIllegalStateExceptionReturnsExistingKey() throws UnknownHostException {
      AWSEC2Client client = createMock(AWSEC2Client.class);
      AWSKeyPairClient keyClient = createMock(AWSKeyPairClient.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairServices()).andReturn(keyClient).atLeastOnce();

      expect(keyClient.importKeyPairInRegion("region", "jclouds#group", "ssh-rsa")).andThrow(
            new IllegalStateException());
      expect(keyClient.describeKeyPairsInRegion("region", "jclouds#group")).andReturn(ImmutableSet.of(pair));

      replay(client);
      replay(keyClient);

      ImportOrReturnExistingKeypair parser = new ImportOrReturnExistingKeypair(client);

      assertEquals(parser.importOrReturnExistingKeypair("region", "group", "ssh-rsa"), pair);

      verify(client);
      verify(keyClient);

   }

   @Test
   public void testApplyWithIllegalStateExceptionRetriesWhenExistingKeyNotFound() throws UnknownHostException {
      AWSEC2Client client = createMock(AWSEC2Client.class);
      AWSKeyPairClient keyClient = createMock(AWSKeyPairClient.class);

      KeyPair pair = createMock(KeyPair.class);

      expect(client.getKeyPairServices()).andReturn(keyClient).atLeastOnce();

      expect(keyClient.importKeyPairInRegion("region", "jclouds#group", "ssh-rsa")).andThrow(
            new IllegalStateException());
      expect(keyClient.describeKeyPairsInRegion("region", "jclouds#group")).andReturn(ImmutableSet.<KeyPair> of());
      expect(keyClient.importKeyPairInRegion("region", "jclouds#group", "ssh-rsa")).andThrow(
            new IllegalStateException());
      expect(keyClient.describeKeyPairsInRegion("region", "jclouds#group")).andReturn(ImmutableSet.<KeyPair> of(pair));

      replay(client);
      replay(keyClient);

      ImportOrReturnExistingKeypair parser = new ImportOrReturnExistingKeypair(client);

      assertEquals(parser.importOrReturnExistingKeypair("region", "group", "ssh-rsa"), pair);

      verify(client);
      verify(keyClient);

   }
}
