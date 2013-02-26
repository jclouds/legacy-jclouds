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
package org.jclouds.snia.cdmi.v1.features;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.internal.BaseCDMIApiLiveTest;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

/**
 * Example setup: -Dtest.cdmi.identity=admin:Admin?authType=openstackKeystone
 * -Dtest.cdmi.credential=passw0rd
 * -Dtest.cdmi.endpoint=http://pds-stack2:5000/v2.0/
 * -Dtest.cdmi.serverType=openstack
 * 
 * @author Kenneth Nagin
 */
@Test(groups = "live", testName = "DataNonCDMIContentTypeApiLiveTest")
public class DataNonCDMIContentTypeApiLiveTest extends BaseCDMIApiLiveTest {
   DataApi dataApi;
   DataNonCDMIContentTypeApi dataNonCDMIContentTypeApi;
   ContainerApi containerApi;
   String serverType;
   String containerName;
   static final ImmutableMap<String, String> pDataObjectMetaDataIn = new ImmutableMap.Builder<String, String>()
            .put("one", "1").put("two", "2").put("three", "3").build();
   static final Logger logger = Logger.getAnonymousLogger();

   @Test
   public void testCreateDataObjectsNonCDMI() throws Exception {
      serverType = System.getProperty("test.cdmi.serverType", "openstack");
      containerName = "MyContainer" + System.currentTimeMillis();
      String dataObjectNameIn = "dataobject.txt";
      File tmpFileIn = new File("temp.txt");
      String value;
      InputStream is;
      File inFile;
      Files.touch(tmpFileIn);
      Payload payloadIn;
      Payload payloadOut;
      containerApi = cdmiContext.getApi().getApi();
      dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
      dataNonCDMIContentTypeApi = cdmiContext.getApi().getDataNonCDMIContentTypeApiForContainer(containerName);
      logger.info("createContainer: " + containerName);
      Container container = containerApi.create(containerName);
      try {
         assertNotNull(container);
         // exercise create data object with none cdmi put with payload string.
         value = "Hello CDMI World non-cdmi String";
         dataNonCDMIContentTypeApi.create(dataObjectNameIn, value);
         payloadOut = dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn);
         assertNotNull(payloadOut);
         assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")), value);
         dataNonCDMIContentTypeApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         payloadIn = new StringPayload(value);
         payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
                  .toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));
         dataNonCDMIContentTypeApi.create(dataObjectNameIn, payloadIn);
         validateDataObject(dataObjectNameIn, value.length(), MediaType.PLAIN_TEXT_UTF_8.toString());
         payloadOut = dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn);
         assertNotNull(payloadOut);
         assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")), value);
         dataNonCDMIContentTypeApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with none cdmi put with text file
         // payload file.
         inFile = new File(System.getProperty("user.dir") + "/src/test/resources/container.json");
         assertEquals(true, inFile.isFile());
         payloadIn = Payloads.newPayload(inFile);
         payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
                  .toBuilder().contentType(MediaType.JSON_UTF_8.toString()).build()));
         dataNonCDMIContentTypeApi.create(inFile.getName(), payloadIn);
         validateDataObject(inFile.getName(), inFile.length(), MediaType.JSON_UTF_8.toString());
         validateDataObjectPayload(inFile.getName(), inFile);
         assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), true);
         dataNonCDMIContentTypeApi.delete(inFile.getName());
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with none cdmi put with jpg file
         inFile = new File(System.getProperty("user.dir") + "/src/test/resources/yellow-flowers.jpg");
         assertEquals(true, inFile.isFile());
         payloadIn = Payloads.newPayload(inFile);
         payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
                  .toBuilder().contentType(MediaType.JPEG.toString()).build()));
         dataNonCDMIContentTypeApi.create(inFile.getName(), payloadIn);
         validateDataObject(inFile.getName(), inFile.length(), MediaType.JPEG.toString());
         validateDataObjectPayload(inFile.getName(), inFile);
         assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), true);
         dataNonCDMIContentTypeApi.delete(inFile.getName());
         assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), false);

         // exercise create data object with none cdmi put with payload
         // inputStream originating from string.
         value = "Hello CDMI World non-cdmi inputStream originating from string";
         is = new ByteArrayInputStream(value.getBytes());
         payloadIn = new InputStreamPayload(is);
         payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
                  .toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString())
                  .contentLength(new Long(value.length())).build()));
         dataNonCDMIContentTypeApi.create(dataObjectNameIn, payloadIn);
         validateDataObject(dataObjectNameIn, value.length(), MediaType.PLAIN_TEXT_UTF_8.toString());
         payloadOut = dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn);
         assertNotNull(payloadOut);
         assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")), value);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataNonCDMIContentTypeApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with none cdmi put with payload
         // inputStream originating from jpeg file.
         inFile = new File(System.getProperty("user.dir") + "/src/test/resources/yellow-flowers.jpg");
         assertEquals(true, inFile.isFile());
         payloadIn = Payloads.newPayload(new FileInputStream(inFile));
         payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
                  .toBuilder().contentType(MediaType.JPEG.toString()).contentLength(new Long(inFile.length())).build()));
         String etag = dataNonCDMIContentTypeApi.create(inFile.getName(), payloadIn);
         System.out.println("etag: " + etag);
         validateDataObject(inFile.getName(), inFile.length(), MediaType.JPEG.toString());
         validateDataObjectPayload(inFile.getName(), inFile);
         assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), true);
         dataNonCDMIContentTypeApi.delete(inFile.getName());
         assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), false);

         // exercise get with none cdmi get range.
         value = "Hello CDMI World non-cdmi String";
         payloadIn = new StringPayload(value);
         payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
                  .toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));
         dataNonCDMIContentTypeApi.create(dataObjectNameIn, payloadIn);

         payloadOut = dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn, "bytes=0-10");
         assertNotNull(payloadOut);
         assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")),
                  value.substring(0, 11));
         assertEquals(payloadOut.getContentMetadata().getContentLength(), new Long(11));

         payloadOut = dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn, "bytes=11-20");
         assertNotNull(payloadOut);
         assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")),
                  value.substring(11, 21));
         assertEquals(payloadOut.getContentMetadata().getContentLength(), new Long(10));
         // get payload with any queryParms on openstack returns payload and
         // allows user to indicate special
         // handling.
         if (serverType.matches("openstack")) {

            payloadOut = dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn, "bytes=11-20",
                     DataObjectQueryParams.Builder.any("query1=byteQuery").field("objectName").any("anyQueryParam")
                              .field("mimetype").metadata());

            assertNotNull(payloadOut);
            assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")),
                     value.substring(11, 21));
            assertEquals(payloadOut.getContentMetadata().getContentLength(), new Long(10));
         }

         dataNonCDMIContentTypeApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with none cdmi partial.
         // server does not actually support cdmi partial but
         // trace allows me to see that request was constructed properly
         String part1 = "abcde";
         String part2 = "fghijklmnop";
         String part3 = "qrstwxyz";
         String allparts = part1 + part2 + part3;
         payloadIn = new StringPayload(part1);
         payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
                  .toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));
         int index = 0;
         dataNonCDMIContentTypeApi.createPartial(dataObjectNameIn, payloadIn, true,
                  "bytes " + index + "-" + String.valueOf(index + part1.length() - 1) + "/" + allparts.length());
         index = index + part1.length();
         payloadIn = new StringPayload(part2);
         payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
                  .toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));
         dataNonCDMIContentTypeApi.createPartial(dataObjectNameIn, payloadIn, true,
                  "bytes " + index + "-" + String.valueOf(index + part2.length() - 1) + "/" + allparts.length());
         index = index + part2.length();
         payloadIn = new StringPayload(part3);
         payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
                  .toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));
         dataNonCDMIContentTypeApi.createPartial(dataObjectNameIn, payloadIn, false,
                  "bytes " + index + "-" + String.valueOf(index + part3.length() - 1) + "/" + allparts.length());
         payloadOut = dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn);
         assertNotNull(payloadOut);
         logger.info("payload " + payloadOut);
         logger.info("payload " + CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")));
      } finally {
         tmpFileIn.delete();
         for (String containerChild : containerApi.get(containerName).getChildren()) {
            logger.info("deleting: " + containerChild);
            dataApi.delete(containerChild);
         }

         containerApi.delete(containerName);
         assertEquals(containerApi.containerExists(containerName), false);
      }

   }

   private void validateDataObject(String dataObjectNameIn, long valueLength, String mimetype) {
      DataObject dataObject;
      // openstack's NonCDMIContentType does not support getting dataobject with
      // for NonCDMIContentTypeApi
      if (!serverType.matches("openstack")) {
         logger.info("retrieving objectName with dataNonCDMIContentTypeApi");
         dataObject = dataNonCDMIContentTypeApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("objectName")
                  .field("objectType").field("mimetype").field("parentURI").metadata());
      } else {
         logger.info("retrieving objectName with dataApi");
         dataObject = dataApi.get(dataObjectNameIn,
                  DataObjectQueryParams.Builder.field("objectName").field("objectType").field("mimetype"));
      }

      assertNotNull(dataObject);
      logger.info(dataObject.toString());
      assertEquals(dataObject.getObjectName(), dataObjectNameIn);
      assertEquals(dataObject.getObjectType(), "application/cdmi-object");
      assertEquals(dataObject.getMimetype(), mimetype);
      // openstack not handling parentURI or cdmi-size properly
      if (!serverType.matches("openstack")) {
         assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), valueLength);
      }

   }

   private void validateDataObjectPayload(String dataObjectNameIn, File fileIn) throws IOException {
      validatePayload(dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn), fileIn);
      DataObject dataObject = dataApi.get(dataObjectNameIn);
      validatePayload(dataObject.getPayload(), fileIn);
   }

   private void validatePayload(Payload payloadOut, File fileIn) throws IOException {
      assertNotNull(payloadOut);
      File tempDir = Files.createTempDir();
      File fileOut = new File(Files.createTempDir(), fileIn.getName());
      FileOutputStream fos = new FileOutputStream(fileOut);
      ByteStreams.copy(payloadOut.getInput(), fos);
      fos.flush();
      fos.close();
      assertEquals(Files.equal(fileOut, fileIn), true);
      fileOut.delete();
      tempDir.delete();
   }

}
