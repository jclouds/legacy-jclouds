/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.ec2.predicates;

import static com.google.common.collect.Sets.newHashSet;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.Attachment.Status;
import org.jclouds.ec2.domain.Volume;
import org.jclouds.ec2.services.ElasticBlockStoreClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Andrei Savu
 */
@Test(groups = "unit", singleThreaded = true)
public class VolumeDetachedTest {

   private ElasticBlockStoreClient client;
   private VolumeDetached volumeDetached;

   @BeforeMethod
   public void setUp() {
      client = createMock(ElasticBlockStoreClient.class);
      volumeDetached = new VolumeDetached(client);
   }

   @Test
   public void testVolumeWithEmptyListOfAttachments() {
      Attachment attachment = newAttachmentWithStatus(Status.ATTACHED);
      Set<Volume> volumes = newHashSet(newVolumeWithAttachments(/* empty */));

      expect(client.describeVolumesInRegion(attachment.getRegion(),
         attachment.getVolumeId())).andReturn(volumes);
      replay(client);

      assertTrue(volumeDetached.apply(attachment));
      verify(client);
   }

   @DataProvider(name = "notDetachedStatuses")
   public Object[][] provideNotDetachedStatuses() {
      return new Object[][]{
         {Status.ATTACHED},
         {Status.ATTACHING},
         {Status.BUSY},
         {Status.DETACHING},
         {Status.UNRECOGNIZED}
      };
   }

   @Test(dataProvider = "notDetachedStatuses")
   public void testWithDifferentStatus(Status attachmentStatus) {
      Attachment attachment = newAttachmentWithStatus(attachmentStatus);
      Set<Volume> volumes = newHashSet(newVolumeWithAttachments(attachment));

      expect(client.describeVolumesInRegion(attachment.getRegion(),
         attachment.getVolumeId())).andReturn(volumes);
      replay(client);

      assertFalse(volumeDetached.apply(attachment));
      verify(client);
   }

   @Test
   public void testWithStatusDetached() {
      Attachment attachment = newAttachmentWithStatus(Status.DETACHED);
      Set<Volume> volumes = newHashSet(newVolumeWithAttachments(attachment));

      expect(client.describeVolumesInRegion(attachment.getRegion(),
         attachment.getVolumeId())).andReturn(volumes);
      replay(client);

      assertTrue(volumeDetached.apply(attachment));
      verify(client);
   }

   private Volume newVolumeWithAttachments(Attachment... attachments) {
      return Volume.builder().region("us-east-1").attachments(attachments).build();
   }

   private Attachment newAttachmentWithStatus(Status status) {
      return Attachment.builder()
         .volumeId("1").status(status).region("us-east-1").attachTime(new Date())
         .device("/dev/sda").instanceId("us-east-1/i-1234").build();
   }
}
