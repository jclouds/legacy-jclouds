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

package org.jclouds.ibmdev;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.functions.GetFirstInstanceInList;
import org.jclouds.ibmdev.functions.ParseAddressFromJson;
import org.jclouds.ibmdev.functions.ParseAddressesFromJson;
import org.jclouds.ibmdev.functions.ParseExpirationTimeFromJson;
import org.jclouds.ibmdev.functions.ParseImageFromJson;
import org.jclouds.ibmdev.functions.ParseImagesFromJson;
import org.jclouds.ibmdev.functions.ParseInstanceFromJson;
import org.jclouds.ibmdev.functions.ParseInstancesFromJson;
import org.jclouds.ibmdev.functions.ParseKeysFromJson;
import org.jclouds.ibmdev.functions.ParseVolumeFromJson;
import org.jclouds.ibmdev.functions.ParseVolumesFromJson;
import org.jclouds.ibmdev.options.CreateInstanceOptions;
import org.jclouds.ibmdev.options.RestartInstanceOptions;
import org.jclouds.ibmdev.xml.LocationHandler;
import org.jclouds.ibmdev.xml.LocationsHandler;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Tests annotation parsing of {@code IBMDeveloperCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ibmdevelopercloud.IBMDeveloperCloudAsyncClientTest")
public class IBMDeveloperCloudAsyncClientTest extends RestClientTest<IBMDeveloperCloudAsyncClient> {

   public void testListImages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listImages");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/offerings/image HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      // now make sure request filters apply by replaying
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);
      Iterables.getOnlyElement(httpRequest.getFilters()).filter(httpRequest);

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/offerings/image HTTP/1.1");
      // for example, using basic authentication, we should get "only one"
      // header
      assertNonPayloadHeadersEqual(httpRequest,
            "Accept: application/json\nAuthorization: Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseImagesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("getImage", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/offerings/image/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("deleteImage", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "DELETE https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/offerings/image/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testSetImageVisibility() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("setImageVisibility", String.class,
            Image.Visibility.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1",
            Image.Visibility.PUBLIC);

      assertRequestLineEquals(httpRequest,
            "PUT https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/offerings/image/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "visibility=PUBLIC", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listInstances");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/instances HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseInstancesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListInstancesFromRequest() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listInstancesFromRequest", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/requests/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseInstancesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetInstance() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("getInstance", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/instances/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseInstanceFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testExtendReservationForInstance() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("extendReservationForInstance", String.class,
            Date.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1", new Date(
            123215235l));

      assertRequestLineEquals(httpRequest,
            "PUT https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/instances/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "expirationTime=123215235", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ParseExpirationTimeFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testRestartInstance() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("restartInstance", String.class,
            RestartInstanceOptions[].class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "PUT https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/instances/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, "state=restart", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testRestartInstanceNewKey() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("restartInstance", String.class,
            RestartInstanceOptions[].class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1",
            new RestartInstanceOptions().authorizePublicKey("keyName"));

      assertRequestLineEquals(httpRequest,
            "PUT https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/instances/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, "state=restart&keyName=keyName", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testSaveInstanceToImage() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("saveInstanceToImage", String.class, String.class,
            String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1",
            "imageName", "imageDescription");

      assertRequestLineEquals(httpRequest,
            "PUT https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/instances/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "state=save&description=imageDescription&name=imageName",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ParseImageFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);
   }

   public void testDeleteInstance() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("deleteInstance", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "DELETE https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/instances/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListKeys() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listKeys");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/keys HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseKeysFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetKey() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("getKey", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/keys/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGenerateKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("generateKeyPair", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "key");

      assertRequestLineEquals(httpRequest,
            "POST https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/keys HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "name=key", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ParseJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testAddPublicKey() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("addPublicKey", String.class, String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "key",
            "publicbits");

      assertRequestLineEquals(httpRequest,
            "POST https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/keys HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, "name=key&publicKey=publicbits", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testUpdatePublicKey() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("updatePublicKey", String.class, String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "key",
            "publicbits");

      assertRequestLineEquals(httpRequest,
            "PUT https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/keys/key HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, "publicKey=publicbits", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testSetDefaultStatusOfKey() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class
            .getMethod("setDefaultStatusOfKey", String.class, boolean.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "key", true);

      assertRequestLineEquals(httpRequest,
            "PUT https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/keys/key HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, "default=true", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testDeleteKey() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("deleteKey", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "DELETE https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/keys/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListVolumes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listVolumes");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/storage HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseVolumesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetVolume() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("getVolume", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/storage/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseVolumeFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateVolumeInLocation() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("createVolumeInLocation", String.class,
            String.class, String.class, String.class, String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "location",
            "name", "format", "size", "offering");

      assertRequestLineEquals(httpRequest,
            "POST https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/storage HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "location=location&format=format&name=name&size=size&offeringID=offering",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ParseVolumeFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateInstanceInLocation() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("createInstanceInLocation", String.class,
            String.class, String.class, String.class, CreateInstanceOptions[].class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1", "name",
            "22", "instanceType");

      assertRequestLineEquals(httpRequest,
            "POST https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/instances HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "location=1&imageID=22&name=name&instanceType=instanceType",
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, httpRequest, GetFirstInstanceInList.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testCreateInstanceInLocationWithOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("createInstanceInLocation", String.class,
            String.class, String.class, String.class, CreateInstanceOptions[].class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "location",
            "name", "22", "instanceType", new CreateInstanceOptions().attachIp("1").authorizePublicKey("MOO")
                  .mountVolume("2", "/mnt").configurationData(
                        ImmutableMap.of("insight_admin_password", "myPassword1", "db2_admin_password", "myPassword2",
                              "report_user_password", "myPassword3")));

      assertRequestLineEquals(httpRequest,
            "POST https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/instances HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(
            httpRequest,
            "location=location&imageID=22&name=name&instanceType=instanceType&ip=1&publicKey=MOO&volumeID=2&oss.storage.id.0.mnt=%2Fmnt&insight_admin_password=myPassword1&db2_admin_password=myPassword2&report_user_password=myPassword3",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, GetFirstInstanceInList.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testDeleteVolume() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("deleteVolume", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "DELETE https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/storage/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListLocations() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listLocations");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/locations HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/xml\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSax.class);
      assertSaxResponseParserClassEquals(method, LocationsHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetLocation() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("getLocation", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/locations/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: text/xml\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseSax.class);
      assertSaxResponseParserClassEquals(method, LocationHandler.class);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListAddresses() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listAddresses");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/addresses HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseAddressesFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testAllocateAddressInLocation() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("allocateAddressInLocation", String.class,
            String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1", "offering");

      assertRequestLineEquals(httpRequest,
            "POST https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/addresses HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, "location=1&offeringID=offering", "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, httpRequest, ParseAddressFromJson.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpRequest);

   }

   public void testReleaseAddress() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("releaseAddress", String.class);
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method, "1");

      assertRequestLineEquals(httpRequest,
            "DELETE https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/addresses/1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListStorageOfferings() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listStorageOfferings");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/offerings/storage HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testListAddressOfferings() throws SecurityException, NoSuchMethodException, IOException {
      Method method = IBMDeveloperCloudAsyncClient.class.getMethod("listAddressOfferings");
      GeneratedHttpRequest<IBMDeveloperCloudAsyncClient> httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET https://www-147.ibm.com/computecloud/enterprise/api/rest/20100331/offerings/address HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), BasicAuthentication.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<IBMDeveloperCloudAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<IBMDeveloperCloudAsyncClient>>() {
      };
   }

   @Override
   public ContextSpec<IBMDeveloperCloudClient, IBMDeveloperCloudAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec("ibmdev", "identity", "credential", new Properties());
   }
}
