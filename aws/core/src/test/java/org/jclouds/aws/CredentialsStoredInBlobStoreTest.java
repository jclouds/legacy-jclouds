/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.aws;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.rest.config.CredentialStoreModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests that credentials stored in the blobstore can be reused across compute contexts.
 * <p/>
 * This test is in aws only because it happens to have both blobstore and compute
 * 
 * TODO create a blobstore and compute integration module
 * 
 * @author Adrian Cole
 */
@Test(testName = "jclouds.CredentialsStoredInBlobStoreTest")
public class CredentialsStoredInBlobStoreTest {

   private BlobStoreContext blobContext;
   private Map<String, InputStream> credentialsMap;

   @BeforeTest
   void setupCredentialContainerAndMap() {
      blobContext = new BlobStoreContextFactory().createContext("transient", "foo", "bar");
      blobContext.getBlobStore().createContainerInLocation(null, "credentials");
      credentialsMap = blobContext.createInputStreamMap("credentials");
   }

   @Test
   public void testWeCanUseBlobStoreToStoreCredentialsAcrossContexts() throws RunNodesException, IOException {

      ComputeServiceContext computeContext = new ComputeServiceContextFactory().createContext("stub", "foo", "bar",
               ImmutableSet.of(new CredentialStoreModule(credentialsMap)));

      Set<? extends NodeMetadata> nodes = computeContext.getComputeService().runNodesWithTag("foo", 10);

      verifyCredentialsFromNodesAreInContext(nodes, computeContext);
      computeContext.close();

      // recreate the compute context with the same map and ensure it still works!
      computeContext = new ComputeServiceContextFactory().createContext("stub", "foo", "bar", Collections
               .singleton(new CredentialStoreModule(credentialsMap)));

      verifyCredentialsFromNodesAreInContext(nodes, computeContext);

   }

   protected void verifyCredentialsFromNodesAreInContext(Set<? extends NodeMetadata> nodes,
            ComputeServiceContext computeContext) throws IOException {
      // verify each node's credential is in the map.
      assertEquals(computeContext.credentialStore().size(), 10);
      for (NodeMetadata node : nodes) {
         assertEquals(computeContext.credentialStore().get("node#" + node.getId()), node.getCredentials());
      }

      // verify the credentials are in the backing store and of a known json format
      assertEquals(credentialsMap.size(), 10);
      for (Entry<String, InputStream> entry : credentialsMap.entrySet()) {
         Credentials credentials = computeContext.credentialStore().get(entry.getKey());
         assertEquals(Strings2.toStringAndClose(entry.getValue()), String.format(
                  "{\"identity\":\"%s\",\"credential\":\"%s\"}", credentials.identity, credentials.credential));
      }
   }
}
