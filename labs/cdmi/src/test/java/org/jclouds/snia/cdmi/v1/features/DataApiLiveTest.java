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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.functions.MultipartMimeParts;
import org.jclouds.snia.cdmi.v1.functions.MultipartMimePayload;
import org.jclouds.snia.cdmi.v1.internal.BaseCDMIApiLiveTest;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectOptions;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.net.MediaType;

/**
 * Example setup: -Dtest.cdmi.identity=admin:Admin?authType=openstackKeystone
 * -Dtest.cdmi.credential=passw0rd
 * -Dtest.cdmi.endpoint=http://pds-stack2:5000/v2.0/
 * 
 * @author Kenneth Nagin
 */

@Test(groups = "live", testName = "DataApiLiveTest")
public class DataApiLiveTest extends BaseCDMIApiLiveTest {
   ContainerApi containerApi;
   DataApi dataApi;
   DataNonCDMIContentTypeApi dataNonCDMIContentTypeApi;
   String containerName;
   String dataObjectNameIn;
   String serverType;
   static final ImmutableMap<String, String> dataObjectMetaDataIn = new ImmutableMap.Builder<String, String>()
            .put("one", "1").put("two", "2").put("three", "3").build();
   static final ImmutableMap<String, String> dataObjectMetaDataIn1 = new ImmutableMap.Builder<String, String>()
            .put("one", "new1").put("two", "new2").put("three", "new3").build();
   static final ImmutableMap<String, String> dataObjectMetaDataIn2 = new ImmutableMap.Builder<String, String>()
            .put("four", "4").put("five", "5").put("six", "6").build();

   static final Logger logger = Logger.getAnonymousLogger();

   @Test
   public void testCreateDataObjects() throws Exception {
      containerName = "testCreateDataObjectsContainer" + System.currentTimeMillis();
      dataObjectNameIn = "dataobject.txt";
      File tmpFileIn = new File("temp.txt");
      String value;
      File inFile;
      Files.touch(tmpFileIn);

      CreateDataObjectOptions pCreateDataObjectOptions;
      DataObject dataObject;
      containerApi = cdmiContext.getApi().getApi();
      dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
      logger.info("createContainer: " + containerName);
      Container container = containerApi.create(containerName);

      try {

         assertNotNull(container);
         logger.info(container.toString());
         container = containerApi.get(containerName);
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);

         // exercise create data object with value mimetype and metadata
         value = "Hello CDMI data object with value mimetype and metadata";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value).mimetype("text/plain")
                  .metadata(dataObjectMetaDataIn);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null, dataObjectMetaDataIn);
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // verify that options order does not matter
         value = "Hello CDMI World3";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.metadata(dataObjectMetaDataIn)
                  .mimetype("text/plain").value(value);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null, dataObjectMetaDataIn);
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with no value
         value = "";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value().metadata(dataObjectMetaDataIn);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null, dataObjectMetaDataIn);
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, value, null, dataObjectMetaDataIn);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with text file
         inFile = new File(System.getProperty("user.dir") + "/src/test/resources/container.json");
         assertEquals(true, inFile.isFile());
         dataObject = dataApi.create(dataObjectNameIn,
                  CreateDataObjectOptions.Builder.value(Payloads.newPayload(inFile)).mimetype("text/plain"));
         validateDataObject(dataObject, inFile.length(), null, new HashMap<String, String>());
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, Files.toString(inFile, Charsets.UTF_8), "text/plain",
                  new HashMap<String, String>());
         validatePayload(dataObject.getPayload(), inFile);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with inputstream
         dataObject = dataApi.create(
                  dataObjectNameIn,
                  CreateDataObjectOptions.Builder.value(Payloads.newPayload(new FileInputStream(inFile))).mimetype(
                           "text/plain"));
         validateDataObject(dataObject, inFile.length(), null, new HashMap<String, String>());
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, Files.toString(inFile, Charsets.UTF_8), "text/plain",
                  new HashMap<String, String>());
         validatePayload(dataObject.getPayload(), inFile);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

      } finally {
         tmpFileIn.delete();
         for (String containerChild : containerApi.get(containerName).getChildren()) {
            logger.info("deleting: " + containerChild);
            dataApi.delete(containerChild);
         }

         containerApi.delete(containerName);

      }

   }

   @Test
   public void testGetDataObjects() throws Exception {
      String serverType = System.getProperty("test.cdmi.serverType", "openstack");
      containerName = "testGetDataObjectsContainer" + System.currentTimeMillis();
      dataObjectNameIn = "dataobject08121.txt";
      File tmpFileIn = new File("temp.txt");
      String value;
      if (!tmpFileIn.exists()) {
         Files.touch(tmpFileIn);
      }
      CreateDataObjectOptions pCreateDataObjectOptions;
      DataObject dataObject;
      Iterator<String> keys;
      Map<String, String> dataObjectMetaDataOut;
      containerApi = cdmiContext.getApi().getApi();
      dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
      logger.info("running tests on serverType: " + serverType);
      logger.info("createContainer: " + containerName);
      Container container = containerApi.create(containerName);
      try {
         assertNotNull(container);
         logger.info(container.toString());
         container = containerApi.get(containerName);
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);

         // exercise create data object with value mimetype and metadata
         value = "Hello CDMI data object with value mimetype and metadata";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value).mimetype("text/plain")
                  .metadata(dataObjectMetaDataIn);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null, dataObjectMetaDataIn);
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn);
         // exercise filters
         dataObject = dataApi
                  .get(dataObjectNameIn, DataObjectQueryParams.Builder.field("objectName").field("mimetype"));
         assertNotNull(dataObject);
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getMimetype(), "text/plain");

         dataObject = dataApi.get(dataObjectNameIn,
                  DataObjectQueryParams.Builder.metadata().field("objectName").field("mimetype"));
         assertNotNull(dataObject);
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getMimetype(), "text/plain");

         dataObject = dataApi.get(dataObjectNameIn,
                  DataObjectQueryParams.Builder.field("mimetype").metadata("dataObjectkey2").field("objectName"));
         assertNotNull(dataObject);
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getMimetype(), "text/plain");
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         assertEquals(dataObjectMetaDataOut.containsKey("two"), true);
         assertEquals(dataObjectMetaDataOut.get("two"), "2");

         if (!serverType.matches("openstack")) {
            // openstack is returning parentURI == to authorization string!
            // so all this fails
            logger.info("running parentURI tests.");
            dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("parentURI"));
            assertNotNull(dataObject);
            logger.info(dataObject.toString());
            assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
            dataObject = dataApi.get(dataObjectNameIn,
                     DataObjectQueryParams.Builder.field("parentURI").field("objectName"));
            assertNotNull(dataObject);
            logger.info(dataObject.toString());
            assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
            assertEquals(dataObject.getObjectName(), dataObjectNameIn);
            dataObject = dataApi.get(dataObjectNameIn,
                     DataObjectQueryParams.Builder.field("parentURI").field("objectName").field("mimetype"));
            assertNotNull(dataObject);
            logger.info(dataObject.toString());
            assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
            assertEquals(dataObject.getObjectName(), dataObjectNameIn);
            assertEquals(dataObject.getMimetype(), "text/plain");

            dataObject = dataApi.get(dataObjectNameIn,
                     DataObjectQueryParams.Builder.field("parentURI").field("objectName").field("mimetype").metadata());
            assertNotNull(dataObject);
            logger.info(dataObject.toString());
            assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
            assertEquals(dataObject.getObjectName(), dataObjectNameIn);
            assertEquals(dataObject.getMimetype(), "text/plain");
            dataObjectMetaDataOut = dataObject.getUserMetadata();
            assertNotNull(dataObjectMetaDataOut);
            keys = dataObjectMetaDataIn.keySet().iterator();
            while (keys.hasNext()) {
               String key = keys.next();
               assertEquals(dataObjectMetaDataOut.containsKey(key), true);
               assertEquals(dataObjectMetaDataOut.get(key), dataObjectMetaDataIn.get(key));
            }
            assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());

            dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.metadata("cdmi_size"));
            assertNotNull(dataObject);
            logger.info(dataObject.toString());
            assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());

            dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("mimetype").value());
            assertNotNull(dataObject);
            logger.info(dataObject.toString());
            logger.info(dataObject.getValue());
            assertEquals(dataObject.getMimetype(), "text/plain");
            assertEquals(dataObject.getValue(), value);

            dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("mimetype").value(0, 3));
            assertNotNull(dataObject);
            logger.info(dataObject.toString());
            logger.info(dataObject.getValue());
            assertEquals(dataObject.getMimetype(), "text/plain");
            // value is SGVsbA==. This needs investigating to determine if this
            // is problem with wss-sonas CDMI
            // server or
            // the jcloud client or
            // must understanding of spec
            logger.info(dataObject.toString());
         }
         // validate any query: allows user to indicate special handling with
         // any query.
         dataObject = dataApi.get(
                  dataObjectNameIn,
                  DataObjectQueryParams.Builder.any("query1=anything").field("objectName").any("anyQueryParam")
                           .field("mimetype").metadata());
         assertNotNull(dataObject);
         logger.info(dataObject.toString());
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

      } finally {
         tmpFileIn.delete();
         for (String containerChild : containerApi.get(containerName).getChildren()) {
            logger.info("deleting: " + containerChild);
            dataApi.delete(containerChild);
         }
         containerApi.delete(containerName);

      }

   }

   @Test
   public void testMultipartMime() throws Exception {
      String serverType = System.getProperty("test.cdmi.serverType", "openstack");
      containerName = "testMultipartMimeContainer" + System.currentTimeMillis();
      MultipartMimeParts parts;
      DataObject dataObject;
      containerApi = cdmiContext.getApi().getApi();
      dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
      dataNonCDMIContentTypeApi = cdmiContext.getApi().getDataNonCDMIContentTypeApiForContainer(containerName);
      logger.info("running tests on serverType: " + serverType);
      Container container = containerApi.create(containerName);
      File files[] = { new File(System.getProperty("user.dir") + "/src/test/resources/container.json"),
               new File(System.getProperty("user.dir") + "/src/test/resources/yellow-flowers.jpg") };
      try {
         assertNotNull(container);
         for (File fileIn : files) {
            dataObjectNameIn = fileIn.getName();
            parts = new MultipartMimeParts(dataObjectMetaDataIn, Payloads.newPayload(fileIn));
            dataObject = dataApi.create(dataObjectNameIn, new MultipartMimePayload(parts));
            validateDataObject(dataObject, fileIn.length(), "application/octet-stream", dataObjectMetaDataIn);
            dataObject = dataApi.getMultipartMime(dataObjectNameIn);
            System.out.println("getMultipartMime completed: " + dataObject);
            validateDataObject(dataObject, fileIn.length(), "application/octet-stream", dataObjectMetaDataIn);
            validatePayload(dataObject.getPayload(), fileIn);
            System.out.println("getMultipartMime validation completed successfully: " + dataObject);
            validatePayload(dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn), fileIn);
            dataObject = dataApi.get(dataObjectNameIn);
            System.out.println("dataApi.get completed: " + dataObject);
            validateDataObject(dataObject, null, fileIn.length(), "application/octet-stream", dataObjectMetaDataIn);
         }

      } finally {
         for (String containerChild : containerApi.get(containerName).getChildren()) {
            logger.info("deleting: " + containerChild);
            dataApi.delete(containerChild);
         }
         containerApi.delete(containerName);

      }
   }

   @Test
   public void testBase64() throws Exception {
      String serverType = System.getProperty("test.cdmi.serverType", "openstack");
      containerName = "TestBase64Container" + System.currentTimeMillis();
      dataObjectNameIn = "dataobject.txt";
      File tmpFileIn = new File("temp.txt");
      if (!tmpFileIn.exists()) {
         Files.touch(tmpFileIn);
      }
      DataObject dataObject;
      Payload payload;
      containerApi = cdmiContext.getApi().getApi();
      dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
      dataNonCDMIContentTypeApi = cdmiContext.getApi().getDataNonCDMIContentTypeApiForContainer(containerName);
      logger.info("running tests on serverType: " + serverType);
      logger.info("createContainer: " + containerName);
      Container container = containerApi.create(containerName);
      try {
         assertNotNull(container);
         File fileIn = new File(System.getProperty("user.dir") + "/src/test/resources/yellow-flowers.jpg");
         dataObjectNameIn = fileIn.getName();
         payload = Payloads.newPayload(fileIn);
         payload.getContentMetadata().setContentEncoding(CreateDataObjectOptions.BASE64);
         dataObject = dataApi.create(
                  dataObjectNameIn,
                  CreateDataObjectOptions.Builder.value(payload).mimetype(MediaType.JPEG.toString())
                           .metadata(dataObjectMetaDataIn));
         validateDataObject(dataObject, fileIn.length(), MediaType.JPEG.toString(), dataObjectMetaDataIn);
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, null, fileIn.length(), null, dataObjectMetaDataIn);
         validatePayload(dataObject.getPayload(), fileIn);
         validatePayload(dataNonCDMIContentTypeApi.getPayload(dataObjectNameIn), fileIn);
      } finally {
         tmpFileIn.delete();
         for (String containerChild : containerApi.get(containerName).getChildren()) {
            dataApi.delete(containerChild);
         }
         containerApi.delete(containerName);

      }
   }

   @Test
   public void testUpdateDataObjects() throws Exception {
      String serverType = System.getProperty("test.cdmi.serverType", "openstack");
      containerName = "testUpdateDataObjectsContainer" + System.currentTimeMillis();
      dataObjectNameIn = "dataobject.txt";
      File tmpFileIn = new File("temp.txt");
      String value;
      if (!tmpFileIn.exists()) {
         Files.touch(tmpFileIn);
      }
      CreateDataObjectOptions pCreateDataObjectOptions;
      DataObject dataObject;
      containerApi = cdmiContext.getApi().getApi();
      dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
      logger.info("running tests on serverType: " + serverType);
      logger.info("createContainer: " + containerName);
      Container container = containerApi.create(containerName);
      try {
         assertNotNull(container);
         logger.info(container.toString());
         container = containerApi.get(containerName);
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);

         value = "Hello CDMI data object with value mimetype and metadata";
         Files.write(value, tmpFileIn, Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value).mimetype("text/plain")
                  .metadata(dataObjectMetaDataIn);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), "text/plain", dataObjectMetaDataIn);
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn);
         assertNotNull(dataApi.update(dataObjectNameIn, DataObjectQueryParams.Builder.metadata(),
                  CreateDataObjectOptions.Builder.metadata(dataObjectMetaDataIn2)));
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn2);
         validatePayload(dataObject.getPayload(), tmpFileIn);
         assertNotNull(dataApi.update(dataObjectNameIn, DataObjectQueryParams.Builder.metadata("one").metadata("two")
                  .metadata("three"), CreateDataObjectOptions.Builder.metadata(dataObjectMetaDataIn)));
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn);
         assertNotNull(dataApi.update(dataObjectNameIn, DataObjectQueryParams.Builder.metadata("one").metadata("two")
                  .metadata("three"), CreateDataObjectOptions.Builder.metadata(dataObjectMetaDataIn1)));
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn1);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn2);
         value = "new value";
         dataApi.update(dataObjectNameIn, DataObjectQueryParams.Builder.value(),
                  CreateDataObjectOptions.Builder.value(value));
         dataObject = dataApi.get(dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn1);
         validateDataObject(dataObject, value, "text/plain", dataObjectMetaDataIn2);
      } finally {
         tmpFileIn.delete();
         for (String containerChild : containerApi.get(containerName).getChildren()) {
            logger.info("deleting: " + containerChild);
            dataApi.delete(containerChild);
         }
         containerApi.delete(containerName);

      }

   }

   private void validateDataObject(DataObject dataObject, String value, String mimetype,
            Map<String, String> userMetaDataIn) throws UnsupportedEncodingException, IOException {
      validateDataObject(dataObject, value, value.length(), mimetype, userMetaDataIn);
   }

   private void validateDataObject(DataObject dataObject, long valueLength, String mimetype,
            Map<String, String> userMetaDataIn) throws IOException {
      validateDataObject(dataObject, null, valueLength, mimetype, userMetaDataIn);
   }

   private void validateDataObject(DataObject dataObject, String value, long valueLength, String mimetype,
            Map<String, String> userMetaDataIn) throws UnsupportedEncodingException, IOException {
      assertNotNull(dataObject);
      logger.info(dataObject.toString());
      // openstack does not return mimetype for base64
      if (mimetype != null && dataObject.getMimetype() != null) {
         assertEquals(dataObject.getMimetype(), mimetype);
      }
      if (value != null) {
         assertEquals(dataObject.getValue().trim(), value);
         assertEquals(CharStreams.toString(new InputStreamReader(dataObject.getPayload().getInput(), "UTF-8")).trim(),
                  value);
      }
      // openstack does not return cdmi-size
      if (dataObject.getSystemMetadata().get("cdmi_size") != null) {
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), valueLength);
      }
      assertEquals(dataObject.getObjectName(), dataObjectNameIn);
      assertEquals(dataObject.getObjectType(), "application/cdmi-object");
      Map<String, String> userMetadata = dataObject.getUserMetadata();
      if (userMetaDataIn == null) {
         userMetaDataIn = Maps.newHashMap();
      }
      if (userMetaDataIn.isEmpty()) {
         assertEquals(userMetadata.isEmpty(), true);
      } else {
         Iterator<String> keys = userMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(userMetadata.containsKey(key), true);
            assertEquals(userMetadata.get(key), userMetaDataIn.get(key));
         }
      }
      assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
   }

   private void validatePayload(Payload payloadOut, File fileIn) throws IOException {
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
