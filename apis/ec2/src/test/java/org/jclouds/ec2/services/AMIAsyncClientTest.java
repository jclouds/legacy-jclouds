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
package org.jclouds.ec2.services;

import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.executableBy;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.jclouds.ec2.options.CreateImageOptions;
import org.jclouds.ec2.options.DescribeImagesOptions;
import org.jclouds.ec2.options.RegisterImageBackedByEbsOptions;
import org.jclouds.ec2.options.RegisterImageOptions;
import org.jclouds.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.ec2.xml.ImageIdHandler;
import org.jclouds.ec2.xml.PermissionHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AMIAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AMIAsyncClientTest")
public class AMIAsyncClientTest extends BaseEC2AsyncClientTest<AMIAsyncClient> {

   public void testCreateImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("createImageInRegion", String.class, String.class, String.class,
               Array.newInstance(CreateImageOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "name", "instanceId");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=CreateImage&InstanceId=instanceId&Name=name",
               "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testCreateImageOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("createImageInRegion", String.class, String.class, String.class,
               Array.newInstance(CreateImageOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "name", "instanceId", new CreateImageOptions()
               .withDescription("description").noReboot());

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=CreateImage&InstanceId=instanceId&Name=name&Description=description&NoReboot=true",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeImages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("describeImagesInRegion", String.class, Array.newInstance(
               DescribeImagesOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeImages", "application/x-www-form-urlencoded",
               false);
      filter.filter(request);
      assertPayloadEquals(
               request,
               "Action=DescribeImages&Signature=qE4vexSFJqS0UWK%2BccV3s%2BP9woL3M5HI5bTBoM7s%2FLY%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeImagesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("describeImagesInRegion", String.class, Array.newInstance(
               DescribeImagesOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, executableBy("me").ownedBy("fred", "nancy").imageIds(
               "1", "2"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=DescribeImages&ExecutableBy=me&Owner.1=fred&Owner.2=nancy&ImageId.1=1&ImageId.2=2",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDeregisterImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("deregisterImageInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "imageId");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DeregisterImage&ImageId=imageId",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRegisterImageFromManifest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerImageFromManifestInRegion", String.class, String.class,
               String.class, Array.newInstance(RegisterImageOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "name", "pathToManifest");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=RegisterImage&ImageLocation=pathToManifest&Name=name",
               "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRegisterImageFromManifestOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerImageFromManifestInRegion", String.class, String.class,
               String.class, Array.newInstance(RegisterImageOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "name", "pathToManifest", new RegisterImageOptions()
               .withDescription("description"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=RegisterImage&ImageLocation=pathToManifest&Name=name&Description=description",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRegisterImageBackedByEBS() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerUnixImageBackedByEbsInRegion", String.class,
               String.class, String.class, Array.newInstance(RegisterImageBackedByEbsOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "imageName", "snapshotId");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=RegisterImage&RootDeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.0.DeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.0.Ebs.SnapshotId=snapshotId&Name=imageName",
               "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRegisterImageBackedByEBSOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerUnixImageBackedByEbsInRegion", String.class,
               String.class, String.class, Array.newInstance(RegisterImageBackedByEbsOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "imageName", "snapshotId",
               new RegisterImageBackedByEbsOptions().withDescription("description").addBlockDeviceFromSnapshot(
                        "/dev/device", null, "snapshot").addNewBlockDevice("/dev/newdevice", "newblock", 100));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=RegisterImage&RootDeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.0.DeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.0.Ebs.SnapshotId=snapshotId&Name=imageName&Description=description&BlockDeviceMapping.1.Ebs.DeleteOnTermination=false&BlockDeviceMapping.1.DeviceName=%2Fdev%2Fdevice&BlockDeviceMapping.1.Ebs.SnapshotId=snapshot&BlockDeviceMapping.2.Ebs.DeleteOnTermination=false&BlockDeviceMapping.2.DeviceName=%2Fdev%2Fnewdevice&BlockDeviceMapping.2.VirtualName=newblock&BlockDeviceMapping.2.Ebs.VolumeSize=100",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetBlockDeviceMappingsForImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("getBlockDeviceMappingsForImageInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "imageId");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=DescribeImageAttribute&Attribute=blockDeviceMapping&ImageId=imageId",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BlockDeviceMappingHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetLaunchPermissionForImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("getLaunchPermissionForImageInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "imageId");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=DescribeImageAttribute&Attribute=launchPermission&ImageId=imageId",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PermissionHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddLaunchPermissionsToImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("addLaunchPermissionsToImageInRegion", String.class,
               Iterable.class, Iterable.class, String.class);
      HttpRequest request = processor.createRequest(method, null, ImmutableList.of("bob", "sue"), ImmutableList
               .of("all"), "imageId");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=ModifyImageAttribute&OperationType=add&Attribute=launchPermission&ImageId=imageId&UserGroup.1=all&UserId.1=bob&UserId.2=sue",
               "application/x-www-form-urlencoded", false);
      filter.filter(request);
      assertPayloadEquals(
               request,
               "Action=ModifyImageAttribute&Attribute=launchPermission&ImageId=imageId&OperationType=add&Signature=WZzNWOC1KHbuySvXEuLTiBA%2BVUfKpSBN2Lud6MrhlCQ%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&UserGroup.1=all&UserId.1=bob&UserId.2=sue&Version=2010-06-15&AWSAccessKeyId=identity",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRemoveLaunchPermissionsFromImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("removeLaunchPermissionsFromImageInRegion", String.class,
               Iterable.class, Iterable.class, String.class);
      HttpRequest request = processor.createRequest(method, null, ImmutableList.of("bob", "sue"), ImmutableList
               .of("all"), "imageId");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=ModifyImageAttribute&OperationType=remove&Attribute=launchPermission&ImageId=imageId&UserGroup.1=all&UserId.1=bob&UserId.2=sue",
               "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testResetLaunchPermissionsOnImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("resetLaunchPermissionsOnImageInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "imageId");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=ResetImageAttribute&Attribute=launchPermission&ImageId=imageId",
               "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AMIAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AMIAsyncClient>>() {
      };
   }

}
