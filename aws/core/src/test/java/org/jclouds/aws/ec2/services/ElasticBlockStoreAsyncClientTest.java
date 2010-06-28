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
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.ec2.options.DescribeSnapshotsOptions.Builder.ownedBy;
import static org.jclouds.aws.ec2.options.DetachVolumeOptions.Builder.fromInstance;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.functions.ReturnVoidOnVolumeAvailable;
import org.jclouds.aws.ec2.options.CreateSnapshotOptions;
import org.jclouds.aws.ec2.options.DescribeSnapshotsOptions;
import org.jclouds.aws.ec2.options.DetachVolumeOptions;
import org.jclouds.aws.ec2.xml.AttachmentHandler;
import org.jclouds.aws.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeSnapshotsResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeVolumesResponseHandler;
import org.jclouds.aws.ec2.xml.PermissionHandler;
import org.jclouds.aws.ec2.xml.SnapshotHandler;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ElasticBlockStoreAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.ElasticBlockStoreAsyncClientTest")
public class ElasticBlockStoreAsyncClientTest extends
         BaseEC2AsyncClientTest<ElasticBlockStoreAsyncClient> {

   public void testCreateVolume() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod(
               "createVolumeInAvailabilityZone", String.class, int.class);
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, AvailabilityZone.US_EAST_1A, 20);

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 74\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=CreateVolume&AvailabilityZone=us-east-1a&Size=20");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CreateVolumeResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateVolumeFromSnapShot() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod(
               "createVolumeFromSnapshotInAvailabilityZone", String.class, String.class);
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, AvailabilityZone.US_EAST_1A, "snapshotId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 88\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=CreateVolume&AvailabilityZone=us-east-1a&SnapshotId=snapshotId");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CreateVolumeResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateVolumeFromSnapShotWithSize() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod(
               "createVolumeFromSnapshotInAvailabilityZone", String.class, int.class, String.class);
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, AvailabilityZone.US_EAST_1A, 15, "snapshotId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 96\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=CreateVolume&AvailabilityZone=us-east-1a&SnapshotId=snapshotId&Size=15");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CreateVolumeResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeleteVolume() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("deleteVolumeInRegion",
               String.class, String.class);
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, "id");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 50\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DeleteVolume&VolumeId=id");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeVolumes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("describeVolumesInRegion",
               String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, (String) null);

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 41\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeVolumes");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeVolumesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeVolumesArgs() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("describeVolumesInRegion",
               String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 41\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeVolumes&VolumeId.1=1&VolumeId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeVolumesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAttachVolume() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("attachVolumeInRegion",
               String.class, String.class, String.class, String.class);
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, "id", "instanceId", "/device");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 89\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=AttachVolume&InstanceId=instanceId&VolumeId=id&Device=%2Fdevice");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AttachmentHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDetachVolume() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("detachVolumeInRegion",
               String.class, String.class, boolean.class, Array.newInstance(
                        DetachVolumeOptions.class, 0).getClass());
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, "id", false);

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 62\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DetachVolume&Force=false&VolumeId=id");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnVolumeAvailable.class);

      checkFilters(httpMethod);
   }

   public void testDetachVolumeOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("detachVolumeInRegion",
               String.class, String.class, boolean.class, Array.newInstance(
                        DetachVolumeOptions.class, 0).getClass());
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, "id", true, fromInstance("instanceId").fromDevice("/device"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 100\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=DetachVolume&Force=true&VolumeId=id&InstanceId=instanceId&Device=%2Fdevice");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnVolumeAvailable.class);

      checkFilters(httpMethod);
   }

   public void testCreateSnapshot() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("createSnapshotInRegion",
               String.class, String.class, Array.newInstance(CreateSnapshotOptions.class, 0)
                        .getClass());
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, "volumeId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 58\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=CreateSnapshot&VolumeId=volumeId");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SnapshotHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateSnapshotOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("createSnapshotInRegion",
               String.class, String.class, Array.newInstance(CreateSnapshotOptions.class, 0)
                        .getClass());
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, "volumeId", CreateSnapshotOptions.Builder
                        .withDescription("description"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 82\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=CreateSnapshot&VolumeId=volumeId&Description=description");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, SnapshotHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeSnapshots() throws SecurityException, NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("describeSnapshotsInRegion",
               String.class, Array.newInstance(DescribeSnapshotsOptions.class, 0).getClass());
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, (String) null);

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 43\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeSnapshots");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSnapshotsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeSnapshotsArgs() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod("describeSnapshotsInRegion",
               String.class, Array.newInstance(DescribeSnapshotsOptions.class, 0).getClass());
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, ownedBy("o1", "o2").restorableBy("r1", "r2").snapshotIds("s1", "s2"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 133\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=DescribeSnapshots&Owner.1=o1&Owner.2=o2&RestorableBy.1=r1&RestorableBy.2=r2&SnapshotId.1=s1&SnapshotId.2=s2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSnapshotsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetCreateVolumePermissionForSnapshot() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod(
               "getCreateVolumePermissionForSnapshotInRegion", String.class, String.class);
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, "snapshotId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 106\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=DescribeSnapshotAttribute&Attribute=createVolumePermission&SnapshotId=snapshotId");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PermissionHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAddCreateVolumePermissionsToSnapshot() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod(
               "addCreateVolumePermissionsToSnapshotInRegion", String.class, Iterable.class,
               Iterable.class, String.class);
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, ImmutableList.of("bob", "sue"), ImmutableList.of("all"), "snapshotId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 122\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifySnapshotAttribute&OperationType=add&Attribute=createVolumePermission&SnapshotId=snapshotId&UserGroup.1=all&UserId.1=bob&UserId.2=sue");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRemoveCreateVolumePermissionsFromSnapshot() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod(
               "removeCreateVolumePermissionsFromSnapshotInRegion", String.class, Iterable.class,
               Iterable.class, String.class);
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, ImmutableList.of("bob", "sue"), ImmutableList.of("all"), "snapshotId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 125\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifySnapshotAttribute&OperationType=remove&Attribute=createVolumePermission&SnapshotId=snapshotId&UserGroup.1=all&UserId.1=bob&UserId.2=sue");
      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testResetCreateVolumePermissionsOnSnapshot() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = ElasticBlockStoreAsyncClient.class.getMethod(
               "resetCreateVolumePermissionsOnSnapshotInRegion", String.class, String.class);
      GeneratedHttpRequest<ElasticBlockStoreAsyncClient> httpMethod = processor.createRequest(
               method, null, "snapshotId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 103\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ResetSnapshotAttribute&Attribute=createVolumePermission&SnapshotId=snapshotId");
      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<ElasticBlockStoreAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<ElasticBlockStoreAsyncClient>>() {
      };
   }

}
