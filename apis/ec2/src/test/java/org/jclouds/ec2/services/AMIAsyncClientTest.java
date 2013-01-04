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

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
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
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * Tests behavior of {@code AMIAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AMIAsyncClientTest")
public class AMIAsyncClientTest extends BaseEC2AsyncClientTest<AMIAsyncClient> {

   HttpRequest createImage = HttpRequest.builder().method("POST")
                                        .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                        .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                        .addFormParam("Action", "CreateImage")
                                        .addFormParam("InstanceId", "instanceId")
                                        .addFormParam("Name", "name")
                                        .addFormParam("Signature", "hBIUf4IUOiCKGQKehaNwwbZUjRN4NC4RSNfJ%2B8kvJdY%3D")
                                        .addFormParam("SignatureMethod", "HmacSHA256")
                                        .addFormParam("SignatureVersion", "2")
                                        .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                        .addFormParam("Version", "2010-06-15")
                                        .addFormParam("AWSAccessKeyId", "identity").build();

   public void testCreateImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("createImageInRegion", String.class, String.class, String.class,
               Array.newInstance(CreateImageOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "name", "instanceId");

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, createImage.getPayload().getRawContent().toString(),
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
                                               .addFormParam("NoReboot", "true")
                                               .addFormParam("Signature", "fz3KW27JxlSq9ivmVOl4IujcHXXw1cOhdig80I7wR6o%3D")
                                               .addFormParam("SignatureMethod", "HmacSHA256")
                                               .addFormParam("SignatureVersion", "2")
                                               .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                               .addFormParam("Version", "2010-06-15")
                                               .addFormParam("AWSAccessKeyId", "identity").build();

   public void testCreateImageOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("createImageInRegion", String.class, String.class, String.class,
               Array.newInstance(CreateImageOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "name", "instanceId", new CreateImageOptions()
               .withDescription("description").noReboot());

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, createImageOptions.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest describeImages = HttpRequest.builder().method("POST")
                                           .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                           .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                           .addFormParam("Action", "DescribeImages")
                                           .addFormParam("Signature", "qE4vexSFJqS0UWK%2BccV3s%2BP9woL3M5HI5bTBoM7s/LY%3D")
                                           .addFormParam("SignatureMethod", "HmacSHA256")
                                           .addFormParam("SignatureVersion", "2")
                                           .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                           .addFormParam("Version", "2010-06-15")
                                           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testDescribeImages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("describeImagesInRegion", String.class, Array.newInstance(
               DescribeImagesOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, (String) null);

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, describeImages.getPayload().getRawContent().toString(),
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
                                                  .addFormParam("Owner.2", "nancy")
                                                  .addFormParam("Signature", "%2BE9wji7oFnNUaGmOBggYNNp6v%2BL8OzSGjuI4nx1l2Jw%3D")
                                                  .addFormParam("SignatureMethod", "HmacSHA256")
                                                  .addFormParam("SignatureVersion", "2")
                                                  .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                  .addFormParam("Version", "2010-06-15")
                                                  .addFormParam("AWSAccessKeyId", "identity").build();

   public void testDescribeImagesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("describeImagesInRegion", String.class, Array.newInstance(
               DescribeImagesOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, executableBy("me").ownedBy("fred", "nancy").imageIds(
               "1", "2"));

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, describeImagesOptions.getPayload().getRawContent().toString(),
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
                                            .addFormParam("ImageId", "imageId")
                                            .addFormParam("Signature", "3sk9LAJAIr2lG04OMuI0qtzCoBtCU1Ac9I6TTmAWjyA%3D")
                                            .addFormParam("SignatureMethod", "HmacSHA256")
                                            .addFormParam("SignatureVersion", "2")
                                            .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                            .addFormParam("Version", "2010-06-15")
                                            .addFormParam("AWSAccessKeyId", "identity").build();

   public void testDeregisterImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("deregisterImageInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "imageId");

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, deregisterImage.getPayload().getRawContent().toString(),
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
                                                      .addFormParam("Name", "name")
                                                      .addFormParam("Signature", "alGqfUiV/bpmpCAj/YzG9VxdTeCwOYyoPjNfwYhm7os%3D")
                                                      .addFormParam("SignatureMethod", "HmacSHA256")
                                                      .addFormParam("SignatureVersion", "2")
                                                      .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                      .addFormParam("Version", "2010-06-15")
                                                      .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRegisterImageFromManifest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerImageFromManifestInRegion", String.class, String.class,
               String.class, Array.newInstance(RegisterImageOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "name", "pathToManifest");

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, registerImageFromManifest.getPayload().getRawContent().toString(),
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
                                                             .addFormParam("Name", "name")
                                                             .addFormParam("Signature", "p77vQLVlPoak6cP/8eoM%2Bz6zkSXx9e2iSlGgLvIwP7I%3D")
                                                             .addFormParam("SignatureMethod", "HmacSHA256")
                                                             .addFormParam("SignatureVersion", "2")
                                                             .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                             .addFormParam("Version", "2010-06-15")
                                                             .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRegisterImageFromManifestOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerImageFromManifestInRegion", String.class, String.class,
               String.class, Array.newInstance(RegisterImageOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "name", "pathToManifest", new RegisterImageOptions()
               .withDescription("description"));

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, registerImageFromManifestOptions.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

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
                                                     .addFormParam("RootDeviceName", "/dev/sda1")
                                                     .addFormParam("Signature", "KGqYXGpJ/UQVTM172Y2TwU4tlG21JXd3Qrx5nSLBVuA%3D")
                                                     .addFormParam("SignatureMethod", "HmacSHA256")
                                                     .addFormParam("SignatureVersion", "2")
                                                     .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                     .addFormParam("Version", "2010-06-15")
                                                     .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRegisterImageBackedByEBS() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerUnixImageBackedByEbsInRegion", String.class,
               String.class, String.class, Array.newInstance(RegisterImageBackedByEbsOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "imageName", "snapshotId");

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, registerImageBackedByEBS.getPayload().getRawContent().toString(),
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
                                                            .addFormParam("RootDeviceName", "/dev/sda1")
                                                            .addFormParam("Signature", "xuWi0w8iODQrg4E0azwqNm2lz/Rf4hBa7m%2BunDTZvVI%3D")
                                                            .addFormParam("SignatureMethod", "HmacSHA256")
                                                            .addFormParam("SignatureVersion", "2")
                                                            .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                            .addFormParam("Version", "2010-06-15")
                                                            .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRegisterImageBackedByEBSOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerUnixImageBackedByEbsInRegion", String.class,
               String.class, String.class, Array.newInstance(RegisterImageBackedByEbsOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "imageName", "snapshotId",
               new RegisterImageBackedByEbsOptions().withDescription("description").addBlockDeviceFromSnapshot(
                        "/dev/device", null, "snapshot").addNewBlockDevice("/dev/newdevice", "newblock", 100));

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, registerImageBackedByEBSOptions.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

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
                                                           .addFormParam("ImageId", "imageId")
                                                           .addFormParam("Signature", "puwfzm8BlfeKiEZ9CNn5ax86weZ6SQ2xyZhN6etu4gA%3D")
                                                           .addFormParam("SignatureMethod", "HmacSHA256")
                                                           .addFormParam("SignatureVersion", "2")
                                                           .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                           .addFormParam("Version", "2010-06-15")
                                                           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testGetBlockDeviceMappingsForImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("getBlockDeviceMappingsForImageInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "imageId");

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, getBlockDeviceMappingsForImage.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

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
                                                        .addFormParam("ImageId", "imageId")
                                                        .addFormParam("Signature", "ocCMlLh3Kpg6HwIcPKlrwoPPg9C5rt5nD0dl717mOq8%3D")
                                                        .addFormParam("SignatureMethod", "HmacSHA256")
                                                        .addFormParam("SignatureVersion", "2")
                                                        .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                        .addFormParam("Version", "2010-06-15")
                                                        .addFormParam("AWSAccessKeyId", "identity").build();

   public void testGetLaunchPermissionForImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("getLaunchPermissionForImageInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "imageId");

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, getLaunchPermissionForImage.getPayload().getRawContent().toString(),
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
                                                          .addFormParam("Signature", "WZzNWOC1KHbuySvXEuLTiBA%2BVUfKpSBN2Lud6MrhlCQ%3D")
                                                          .addFormParam("SignatureMethod", "HmacSHA256")
                                                          .addFormParam("SignatureVersion", "2")
                                                          .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                          .addFormParam("UserGroup.1", "all")
                                                          .addFormParam("UserId.1", "bob")
                                                          .addFormParam("UserId.2", "sue")
                                                          .addFormParam("Version", "2010-06-15")
                                                          .addFormParam("AWSAccessKeyId", "identity").build();

   public void testAddLaunchPermissionsToImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("addLaunchPermissionsToImageInRegion", String.class,
               Iterable.class, Iterable.class, String.class);
      HttpRequest request = processor.createRequest(method, null, ImmutableList.of("bob", "sue"), ImmutableList
               .of("all"), "imageId");

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, addLaunchPermission.getPayload().getRawContent().toString(),
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
                                                   .addFormParam("Signature", "z8OYGQBAwu4HwXV6VF/vuOZlBtptxLxtCQiLXY7UvMU%3D")
                                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                                   .addFormParam("SignatureVersion", "2")
                                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                   .addFormParam("UserGroup.1", "all")
                                                   .addFormParam("UserId.1", "bob")
                                                   .addFormParam("UserId.2", "sue")
                                                   .addFormParam("Version", "2010-06-15")
                                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRemoveLaunchPermissionsFromImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("removeLaunchPermissionsFromImageInRegion", String.class,
               Iterable.class, Iterable.class, String.class);
      HttpRequest request = processor.createRequest(method, null, ImmutableList.of("bob", "sue"), ImmutableList
               .of("all"), "imageId");

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, removeLaunchPermission.getPayload().getRawContent().toString(),
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
                                                          .addFormParam("ImageId", "imageId")
                                                          .addFormParam("Signature", "mOVwrqAzidhz%2B4E1dqOJAzG9G9ZX7eDpi8BobN4dA%2BE%3D")
                                                          .addFormParam("SignatureMethod", "HmacSHA256")
                                                          .addFormParam("SignatureVersion", "2")
                                                          .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                          .addFormParam("Version", "2010-06-15")
                                                          .addFormParam("AWSAccessKeyId", "identity").build();

   public void testResetLaunchPermissionsOnImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("resetLaunchPermissionsOnImageInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "imageId");

      request = request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, resetLaunchPermissionsOnImage.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
