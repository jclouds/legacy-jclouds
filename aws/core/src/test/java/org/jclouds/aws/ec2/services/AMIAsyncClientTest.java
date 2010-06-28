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

import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.executableBy;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.jclouds.aws.ec2.options.CreateImageOptions;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.aws.ec2.options.RegisterImageBackedByEbsOptions;
import org.jclouds.aws.ec2.options.RegisterImageOptions;
import org.jclouds.aws.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.aws.ec2.xml.ImageIdHandler;
import org.jclouds.aws.ec2.xml.PermissionHandler;
import org.jclouds.aws.ec2.xml.ProductCodesHandler;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AMIAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.AMIAsyncClientTest")
public class AMIAsyncClientTest extends BaseEC2AsyncClientTest<AMIAsyncClient> {

   public void testCreateImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("createImageInRegion", String.class,
               String.class, String.class, Array.newInstance(CreateImageOptions.class, 0)
                        .getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "name", "instanceId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 69\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=CreateImage&InstanceId=instanceId&Name=name");
      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateImageOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("createImageInRegion", String.class,
               String.class, String.class, Array.newInstance(CreateImageOptions.class, 0)
                        .getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "name", "instanceId", new CreateImageOptions().withDescription("description")
                        .noReboot());

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 107\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=CreateImage&InstanceId=instanceId&Name=name&Description=description&NoReboot=true");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeImages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("describeImagesInRegion", String.class, Array
               .newInstance(DescribeImagesOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method,
               (String) null);

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 40\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeImages");
      filter.filter(httpMethod);
      assertPayloadEquals(
               httpMethod,
               "Action=DescribeImages&Signature=IYist5Mfzd44GO3%2BX8WJ4Ti%2BWe3UmrZQC10XdCkT5Fk%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2009-11-30&AWSAccessKeyId=identity");
      
      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeImagesOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("describeImagesInRegion", String.class, Array
               .newInstance(DescribeImagesOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               executableBy("me").ownedBy("fred", "nancy").imageIds("1", "2"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 107\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=DescribeImages&ExecutableBy=me&Owner.1=fred&Owner.2=nancy&ImageId.1=1&ImageId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeregisterImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("deregisterImageInRegion", String.class,
               String.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "imageId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 57\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DeregisterImage&ImageId=imageId");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRegisterImageFromManifest() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("registerImageFromManifestInRegion",
               String.class, String.class, String.class, Array.newInstance(
                        RegisterImageOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "name", "pathToManifest");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 78\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=RegisterImage&ImageLocation=pathToManifest&Name=name");
      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRegisterImageFromManifestOptions() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerImageFromManifestInRegion",
               String.class, String.class, String.class, Array.newInstance(
                        RegisterImageOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "name", "pathToManifest", new RegisterImageOptions().withDescription("description"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 102\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=RegisterImage&ImageLocation=pathToManifest&Name=name&Description=description");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRegisterImageBackedByEBS() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("registerUnixImageBackedByEbsInRegion",
               String.class, String.class, String.class, Array.newInstance(
                        RegisterImageBackedByEbsOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "imageName", "snapshotId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 176\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=RegisterImage&RootDeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.0.DeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.0.Ebs.SnapshotId=snapshotId&Name=imageName");
      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRegisterImageBackedByEBSOptions() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("registerUnixImageBackedByEbsInRegion",
               String.class, String.class, String.class, Array.newInstance(
                        RegisterImageBackedByEbsOptions.class, 0).getClass());
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "imageName", "snapshotId", new RegisterImageBackedByEbsOptions().withDescription(
                        "description").addBlockDeviceFromSnapshot("/dev/device", null, "snapshot")
                        .addNewBlockDevice("/dev/newdevice", "newblock", 100));

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 528\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=RegisterImage&RootDeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.0.DeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.0.Ebs.SnapshotId=snapshotId&Name=imageName&Description=description&BlockDeviceMapping.1.Ebs.DeleteOnTermination=false&BlockDeviceMapping.1.DeviceName=%2Fdev%2Fdevice&BlockDeviceMapping.1.Ebs.SnapshotId=snapshot&BlockDeviceMapping.2.Ebs.DeleteOnTermination=false&BlockDeviceMapping.2.DeviceName=%2Fdev%2Fnewdevice&BlockDeviceMapping.2.VirtualName=newblock&BlockDeviceMapping.2.Ebs.VolumeSize=100");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ImageIdHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetProductCodesForImage() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("getProductCodesForImageInRegion",
               String.class, String.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "imageId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 87\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeImageAttribute&Attribute=productCodes&ImageId=imageId");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, ProductCodesHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetBlockDeviceMappingsForImage() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("getBlockDeviceMappingsForImageInRegion",
               String.class, String.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "imageId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 93\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeImageAttribute&Attribute=blockDeviceMapping&ImageId=imageId");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BlockDeviceMappingHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetLaunchPermissionForImage() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("getLaunchPermissionForImageInRegion",
               String.class, String.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "imageId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 91\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeImageAttribute&Attribute=launchPermission&ImageId=imageId");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, PermissionHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAddLaunchPermissionsToImage() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("addLaunchPermissionsToImageInRegion",
               String.class, Iterable.class, Iterable.class, String.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               ImmutableList.of("bob", "sue"), ImmutableList.of("all"), "imageId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 107\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyImageAttribute&OperationType=add&Attribute=launchPermission&ImageId=imageId&UserGroup.1=all&UserId.1=bob&UserId.2=sue");
      filter.filter(httpMethod);
      assertPayloadEquals(
               httpMethod,
               "Action=ModifyImageAttribute&Attribute=launchPermission&ImageId=imageId&OperationType=add&Signature=Nf2oLuEQ%2BDgwhAxNt7Cdicjacz3PYTVR08%2BaGuXMfwU%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&UserGroup.1=all&UserId.1=bob&UserId.2=sue&Version=2009-11-30&AWSAccessKeyId=identity");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRemoveLaunchPermissionsFromImage() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = AMIAsyncClient.class.getMethod("removeLaunchPermissionsFromImageInRegion",
               String.class, Iterable.class, Iterable.class, String.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               ImmutableList.of("bob", "sue"), ImmutableList.of("all"), "imageId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 110\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyImageAttribute&OperationType=remove&Attribute=launchPermission&ImageId=imageId&UserGroup.1=all&UserId.1=bob&UserId.2=sue");
      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testResetLaunchPermissionsOnImage() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("resetLaunchPermissionsOnImageInRegion",
               String.class, String.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               "imageId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 88\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=ResetImageAttribute&Attribute=launchPermission&ImageId=imageId");
      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAddProductCodesToImage() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("addProductCodesToImageInRegion",
               String.class, Iterable.class, String.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               ImmutableList.of("code1", "code2"), "imageId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 103\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyImageAttribute&OperationType=add&Attribute=productCodes&ImageId=imageId&ProductCode.1=code1&ProductCode.2=code2");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRemoveProductCodesFromImage() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = AMIAsyncClient.class.getMethod("removeProductCodesFromImageInRegion",
               String.class, Iterable.class, String.class);
      GeneratedHttpRequest<AMIAsyncClient> httpMethod = processor.createRequest(method, null,
               ImmutableList.of("code1", "code2"), "imageId");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 106\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyImageAttribute&OperationType=remove&Attribute=productCodes&ImageId=imageId&ProductCode.1=code1&ProductCode.2=code2");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AMIAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AMIAsyncClient>>() {
      };
   }

}
