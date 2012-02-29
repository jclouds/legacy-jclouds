/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.features;

import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@link NetworkClient}
 * 
 * @author danikov
 */
@Test(groups = { "live", "api", "user" }, singleThreaded = true, testName = "NetworkClientLiveTest")
public class UploadClientLiveTest extends BaseVCloudDirectorClientLiveTest {
   
   public static final String UPLOAD = "upload";
 
   /*
    * Convenience reference to API client.
    */
   protected UploadClient uploadClient;
 
   @BeforeClass(inheritGroups = true)
   @Override
   public void setupRequiredClients() {
//      uploadClient = context.getApi().getUploadClient();
   }

   @Test(testName = "PUT ???")
   public void testUpload() {
  ////TODO upload to target
   // long sourceChecksum = 0;// =  Files.getChecksum(sourceFile, new CRC32())
   // 
   // // TODO: await task to complete
   // media = mediaClient.getMedia(media);
   // TasksInProgress tasks = media.getTasksInProgress();
   // if (tasks.getTasks().size() > 0) {
//       assertTrue(tasks.getTasks().size() == 1, "");
//       Task uploadTask = Iterables.getFirst(tasks.getTasks(), null);
//       Checks.checkTask(uploadTask);
//       assertTrue(retryTaskSuccess.apply(uploadTask), String.format(TASK_COMPLETE_TIMELY, "createMedia"));
   // }
   // 
   // // TODO: make assertions that the task was successful
   // media = mediaClient.getMedia(media);
   // assertTrue(equal(sourceFile.length(), uploadFile.getSize()), "");
   // assertTrue(equal(sourceChecksum, uploadFile.getChecksum()), "");
   }
}
