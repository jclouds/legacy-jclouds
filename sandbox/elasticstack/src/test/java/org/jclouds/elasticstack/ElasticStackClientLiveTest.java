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

package org.jclouds.elasticstack;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.domain.Credentials;
import org.jclouds.elasticstack.domain.CreateDriveRequest;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.io.Payloads;
import org.jclouds.util.Utils;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code ElasticStackClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "elasticstack.ElasticStackClientLiveTest")
public class ElasticStackClientLiveTest extends
      CommonElasticStackClientLiveTest<ElasticStackClient, ElasticStackAsyncClient> {
   private DriveInfo drive2;
   private DriveInfo drive3;

   public void testWeCanReadAndWriteToDrive() throws IOException {
      drive2 = client.createDrive(new CreateDriveRequest.Builder().name(prefix + "2").size(1 * 1024 * 1024l).build());
      client.writeDrive(drive2.getUuid(), Payloads.newStringPayload("foo"));
      assertEquals(Utils.toStringAndClose(client.readDrive(drive2.getUuid(), 0, 3).getInput()), "foo");
   }

   @Test(dependsOnMethods = "testWeCanReadAndWriteToDrive")
   public void testWeCopyADriveContentsViaGzip() throws IOException {
      try {
         drive3 = client
               .createDrive(new CreateDriveRequest.Builder().name(prefix + "3").size(1 * 1024 * 1024l).build());
         System.err.println("before image; drive 2" + client.getDriveInfo(drive2.getUuid()));
         System.err.println("before image; drive 3" + client.getDriveInfo(drive3.getUuid()));
         client.imageDrive(drive2.getUuid(), drive3.getUuid());
         assert driveNotClaimed.apply(drive3) : client.getDriveInfo(drive3.getUuid());
         assert driveNotClaimed.apply(drive2) : client.getDriveInfo(drive2.getUuid());
         System.err.println("after image; drive 2" + client.getDriveInfo(drive2.getUuid()));
         System.err.println("after image; drive 3" + client.getDriveInfo(drive3.getUuid()));
         assertEquals(Utils.toStringAndClose(client.readDrive(drive3.getUuid(), 0, 3).getInput()), "foo");
      } finally {
         client.destroyDrive(drive2.getUuid());
         client.destroyDrive(drive3.getUuid());
      }
   }

   @Override
   protected Credentials getSshCredentials(Server server) {
      return new Credentials("toor", server.getVnc().getPassword());
   }

   @Override
   protected void prepareDrive() {
      System.err.println("before prepare" + client.getDriveInfo(drive.getUuid()));
      client.imageDrive("38df0986-4d85-4b76-b502-3878ffc80161", drive.getUuid(), ImageConversionType.GUNZIP);
      assert driveNotClaimed.apply(drive) : client.getDriveInfo(drive.getUuid());
      System.err.println("after prepare" + client.getDriveInfo(drive.getUuid()));
   }

}
