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

package org.jclouds.elastichosts;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.elastichosts.domain.CreateDriveRequest;
import org.jclouds.elastichosts.domain.DriveInfo;
import org.jclouds.elastichosts.options.ReadDriveOptions;
import org.jclouds.io.Payloads;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ElasticHostsClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "elastichosts.ElasticHostsClientLiveTest")
public class ElasticHostsClientLiveTest extends
      CommonElasticHostsClientLiveTest<ElasticHostsClient, ElasticHostsAsyncClient> {
   private DriveInfo info2;

   @Override
   public void testGetDrive() throws Exception {
      super.testGetDrive();
   }

   @Override
   public void testCreate() throws Exception {
      super.testCreate();
   }

   @Override
   public void testSetDriveData() throws Exception {
      super.testSetDriveData();
   }

   @Override
   public void testDestroyDrive() throws Exception {
      super.testDestroyDrive();
   }

   @Test(dependsOnMethods = "testCreate")
   public void testWeCanReadAndWriteToDrive() throws IOException {
      client.writeDrive(info.getUuid(), Payloads.newStringPayload("foo"));
      assertEquals(Utils.toStringAndClose(client.readDrive(info.getUuid(), ReadDriveOptions.Builder.offset(0).size(3))
            .getInput()), "foo");
   }

   @Test(dependsOnMethods = "testWeCanReadAndWriteToDrive")
   public void testWeCopyADriveContentsViaGzip() throws IOException {

      try {
         info2 = client.createDrive(new CreateDriveRequest.Builder().name(prefix + "2").size(4 * 1024 * 1024l).build());
         client.imageDrive(info.getUuid(), info2.getUuid());

         // TODO block until complete
         System.err.println("state " + client.getDriveInfo(info2.getUuid()));
         assertEquals(Utils.toStringAndClose(client.readDrive(info2.getUuid()).getInput()), "foo");
      } finally {
         client.destroyDrive(info2.getUuid());
      }

   }

}
