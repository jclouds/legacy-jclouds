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
package org.jclouds.aws.ec2.services;

import static org.jclouds.ec2.options.DescribeImagesOptions.Builder.executableBy;
import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.aws.ec2.xml.ProductCodesHandler;
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
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code AWSAMIAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AWSAMIAsyncClientTest")
public class AWSAMIAsyncClientTest extends BaseAWSEC2AsyncClientTest<AWSAMIAsyncClient> {
   public AWSAMIAsyncClientTest() {
      provider = "aws-ec2";
   }

   HttpRequest createImage = HttpRequest.builder().method("POST")
                                        .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                        .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                        .addFormParam("Action", "CreateImage")
                                        .addFormParam("InstanceId", "instanceId")
                                        .addFormParam("Name", "name").build();

   public void testCreateImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "createImageInRegion", String.class, String.class, String.class,
               CreateImageOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "name", "instanceId"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(createImage).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest createImageOptions = HttpRequest.builder().method("POST")
                                               .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                               .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                               .addFormParam("Action", "CreateImage")
                                               .addFormParam("Description", "description")
                                               .addFormParam("InstanceId", "instanceId")
                                               .addFormParam("Name", "name")
                                               .addFormParam("NoReboot", "true").build();

   public void testCreateImageOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "createImageInRegion", String.class, String.class, String.class,
               CreateImageOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "name", "instanceId", new CreateImageOptions()
               .withDescription("description").noReboot()));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(createImageOptions).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest describeImages = HttpRequest.builder().method("POST")
                                           .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                           .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                           .addFormParam("Action", "DescribeImages").build();

   public void testDescribeImages() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "describeImagesInRegion", String.class,
               DescribeImagesOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(describeImages).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   HttpRequest describeImagesOptions = HttpRequest.builder().method("POST")
                                                  .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                  .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                  .addFormParam("Action", "DescribeImages")
                                                  .addFormParam("ExecutableBy", "me")
                                                  .addFormParam("ImageId.1", "1")
                                                  .addFormParam("ImageId.2", "2")
                                                  .addFormParam("Owner.1", "fred")
                                                  .addFormParam("Owner.2", "nancy").build();

   public void testDescribeImagesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "describeImagesInRegion", String.class,
               DescribeImagesOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, executableBy("me").ownedBy("fred", "nancy").imageIds(
               "1", "2")));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(describeImagesOptions).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   HttpRequest deregisterImage = HttpRequest.builder().method("POST")
                                            .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                            .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                            .addFormParam("Action", "DeregisterImage")
                                            .addFormParam("ImageId", "imageId").build();

   public void testDeregisterImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "deregisterImageInRegion", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "imageId"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(deregisterImage).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest registerImageFromManifest = HttpRequest.builder().method("POST")
                                                      .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                      .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                      .addFormParam("Action", "RegisterImage")
                                                      .addFormParam("ImageLocation", "pathToManifest")
                                                      .addFormParam("Name", "name").build();

   public void testRegisterImageFromManifest() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "registerImageFromManifestInRegion", String.class, String.class,
               String.class, RegisterImageOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "name", "pathToManifest"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(registerImageFromManifest).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest registerImageFromManifestOptions = HttpRequest.builder().method("POST")
                                                             .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                             .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                             .addFormParam("Action", "RegisterImage")
                                                             .addFormParam("Description", "description")
                                                             .addFormParam("ImageLocation", "pathToManifest")
                                                             .addFormParam("Name", "name").build();

   public void testRegisterImageFromManifestOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "registerImageFromManifestInRegion", String.class, String.class,
               String.class, RegisterImageOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "name", "pathToManifest", new RegisterImageOptions()
               .withDescription("description")));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(registerImageFromManifestOptions).getPayload().getRawContent()
            .toString(), "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest registerImageBackedByEBS = HttpRequest.builder().method("POST")
                                                     .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                     .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                     .addFormParam("Action", "RegisterImage")
                                                     .addFormParam("BlockDeviceMapping.0.DeviceName", "/dev/sda1")
                                                     .addFormParam("BlockDeviceMapping.0.Ebs.SnapshotId", "snapshotId")
                                                     .addFormParam("Name", "imageName")
                                                     .addFormParam("RootDeviceName", "/dev/sda1").build();

   public void testRegisterImageBackedByEBS() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "registerUnixImageBackedByEbsInRegion", String.class,
               String.class, String.class, RegisterImageBackedByEbsOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "imageName", "snapshotId"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(registerImageBackedByEBS).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest registerImageBackedByEBSOptions = HttpRequest.builder().method("POST")
                                                            .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                            .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                            .addFormParam("Action", "RegisterImage")
                                                            .addFormParam("BlockDeviceMapping.0.DeviceName", "/dev/sda1")
                                                            .addFormParam("BlockDeviceMapping.0.Ebs.SnapshotId", "snapshotId")
                                                            .addFormParam("BlockDeviceMapping.1.DeviceName", "/dev/device")
                                                            .addFormParam("BlockDeviceMapping.1.Ebs.DeleteOnTermination", "false")
                                                            .addFormParam("BlockDeviceMapping.1.Ebs.SnapshotId", "snapshot")
                                                            .addFormParam("BlockDeviceMapping.2.DeviceName", "/dev/newdevice")
                                                            .addFormParam("BlockDeviceMapping.2.Ebs.DeleteOnTermination", "false")
                                                            .addFormParam("BlockDeviceMapping.2.Ebs.VolumeSize", "100")
                                                            .addFormParam("BlockDeviceMapping.2.VirtualName", "newblock")
                                                            .addFormParam("Description", "description")
                                                            .addFormParam("Name", "imageName")
                                                            .addFormParam("RootDeviceName", "/dev/sda1").build();

   public void testRegisterImageBackedByEBSOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "registerUnixImageBackedByEbsInRegion", String.class,
               String.class, String.class, RegisterImageBackedByEbsOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "imageName", "snapshotId",
               new RegisterImageBackedByEbsOptions().withDescription("description").addBlockDeviceFromSnapshot(
                        "/dev/device", null, "snapshot").addNewBlockDevice("/dev/newdevice", "newblock", 100)));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(registerImageBackedByEBSOptions).getPayload().getRawContent()
            .toString(), "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest getBlockDeviceMappingsForImage = HttpRequest.builder().method("POST")
                                                           .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                           .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                           .addFormParam("Action", "DescribeImageAttribute")
                                                           .addFormParam("Attribute", "blockDeviceMapping")
                                                           .addFormParam("ImageId", "imageId").build();

   public void testGetBlockDeviceMappingsForImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "getBlockDeviceMappingsForImageInRegion", String.class,
               String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "imageId"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(getBlockDeviceMappingsForImage).getPayload().getRawContent()
            .toString(), "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BlockDeviceMappingHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest getLaunchPermissionForImage = HttpRequest.builder().method("POST")
                                                        .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                        .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                        .addFormParam("Action", "DescribeImageAttribute")
                                                        .addFormParam("Attribute", "launchPermission")
                                                        .addFormParam("ImageId", "imageId").build();

   public void testGetLaunchPermissionForImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "getLaunchPermissionForImageInRegion", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "imageId"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(getLaunchPermissionForImage).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PermissionHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest addLaunchPermission = HttpRequest.builder().method("POST")
                                                          .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                          .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                          .addFormParam("Action", "ModifyImageAttribute")
                                                          .addFormParam("Attribute", "launchPermission")
                                                          .addFormParam("ImageId", "imageId")
                                                          .addFormParam("OperationType", "add")
                                                          .addFormParam("UserGroup.1", "all")
                                                          .addFormParam("UserId.1", "bob")
                                                          .addFormParam("UserId.2", "sue").build();

   public void testAddLaunchPermissionsToImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "addLaunchPermissionsToImageInRegion", String.class,
               Iterable.class, Iterable.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, ImmutableList.of("bob", "sue"), ImmutableList
               .of("all"), "imageId"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(addLaunchPermission).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest removeLaunchPermission = HttpRequest.builder().method("POST")
                                                   .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                   .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                   .addFormParam("Action", "ModifyImageAttribute")
                                                   .addFormParam("Attribute", "launchPermission")
                                                   .addFormParam("ImageId", "imageId")
                                                   .addFormParam("OperationType", "remove")
                                                   .addFormParam("UserGroup.1", "all")
                                                   .addFormParam("UserId.1", "bob")
                                                   .addFormParam("UserId.2", "sue").build();

   public void testRemoveLaunchPermissionsFromImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "removeLaunchPermissionsFromImageInRegion", String.class,
               Iterable.class, Iterable.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, ImmutableList.of("bob", "sue"), ImmutableList
               .of("all"), "imageId"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(removeLaunchPermission).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest resetLaunchPermissionsOnImage = HttpRequest.builder().method("POST")
                                                          .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                          .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                          .addFormParam("Action", "ResetImageAttribute")
                                                          .addFormParam("Attribute", "launchPermission")
                                                          .addFormParam("ImageId", "imageId").build();

   public void testResetLaunchPermissionsOnImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "resetLaunchPermissionsOnImageInRegion", String.class,
               String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "imageId"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            filter.filter(resetLaunchPermissionsOnImage).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetProductCodesForImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "getProductCodesForImageInRegion", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "imageId"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=DescribeImageAttribute&Attribute=productCodes&ImageId=imageId",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ProductCodesHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testAddProductCodesToImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "addProductCodesToImageInRegion", String.class, Iterable.class,
            String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, ImmutableList.of("code1", "code2"), "imageId"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=ModifyImageAttribute&OperationType=add&Attribute=productCodes&ImageId=imageId&ProductCode.1=code1&ProductCode.2=code2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testRemoveProductCodesFromImage() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSAMIAsyncClient.class, "removeProductCodesFromImageInRegion", String.class,
            Iterable.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, ImmutableList.of("code1", "code2"), "imageId"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=ModifyImageAttribute&OperationType=remove&Attribute=productCodes&ImageId=imageId&ProductCode.1=code1&ProductCode.2=code2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
