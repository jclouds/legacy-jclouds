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
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.internal.BaseCDMIApiLiveTest;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectOptions;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

/**
 * Example setup: -Dtest.cdmi.identity=admin:Admin?authType=openstackKeystone -Dtest.cdmi.credential=passw0rd
 * -Dtest.cdmi.endpoint=http://pds-stack2:5000/v2.0/
 * 
 * @author Kenneth Nagin
 */
@Test(groups = "live", testName = "DataApiLiveTest")
public class DataApiLiveTest extends BaseCDMIApiLiveTest {  
   ContainerApi containerApi;
   DataApi dataApi;
   String containerName;
   String dataObjectNameIn;
   String serverType;
   static final ImmutableMap<String, String> dataObjectMetaDataIn =
      new ImmutableMap.Builder<String, String>()
          .put("one", "1")
          .put("two", "2")
          .put("three", "3")
          .build();
   @Test
   public void testCreateDataObjects() throws Exception {
      containerName = "MyContainer" + System.currentTimeMillis() + "/";
      dataObjectNameIn = "dataobject08121.txt";
      File tmpFileIn = new File("temp.txt");
      String value;
      InputStream is;
      File tmpFileOut;
      File inFile;
      Files.touch(tmpFileIn);
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(bos);
      byte[] bytes;

      CreateDataObjectOptions pCreateDataObjectOptions;
      DataObject dataObject;      
      containerApi = cdmiContext.getApi().getApi();
      dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
      Logger.getAnonymousLogger().info("createContainer: " + containerName);
      Container container = containerApi.create(containerName);

      try {

         assertNotNull(container);
         Logger.getAnonymousLogger().info(container.toString());
         container = containerApi.get(containerName);
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);

         // exercise create data object with value mimetype and metadata
         value = "Hello CDMI data object with value mimetype and metadata";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value).mimetype("text/plain")
                  .metadata(dataObjectMetaDataIn);
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain");
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // verify that options order does not matter
         value = "Hello CDMI World3";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.metadata(dataObjectMetaDataIn)
                  .mimetype("text/plain").value(value);
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain");
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with empty mimetype only
         value = "";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.mimetype(new String()).metadata(dataObjectMetaDataIn);
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, value, null);
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with no value
         value = "";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value().metadata(dataObjectMetaDataIn);
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, value, null);
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with byte array
         value = "Hello CDMI World 7";
         out.writeUTF(value);
         out.close();
         bytes = bos.toByteArray();
         // String.getBytes causes an exception CreateDataObjectOptions need
         // to investigate byte arrays
         // bytes = value.getBytes("UTF-8");
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(bytes).mimetype("text/plain");
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain");
         assertEquals(new String(dataObject.getValueAsByteArray()), value);
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with an existing file
         inFile = new File(System.getProperty("user.dir") + "/src/test/resources/container.json");
         assertEquals(true, inFile.isFile());
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(inFile).mimetype("text/plain");
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, inFile.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, Files.toString(inFile, Charsets.UTF_8), "text/plain");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, inFile));
         tmpFileOut.delete();
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file that we create on the fly  with default Charset
         value = "Hello CDMI World 10";
         Files.write(value, tmpFileIn, Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(tmpFileIn).mimetype("text/plain");
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, tmpFileIn.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file that we create on the fly specify charset UTF_8
         Files.write(value, tmpFileIn, Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(tmpFileIn, Charsets.UTF_8).mimetype(
                  "text/plain");
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, tmpFileIn.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file that we create on the fly specify charset US_ASCII
         Files.write(value, tmpFileIn, Charsets.US_ASCII);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(tmpFileIn, Charsets.US_ASCII).mimetype(
                  "text/plain");
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, tmpFileIn.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, value, "text/plain");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file with multiple lines with default Charset
         Files.write("line1", tmpFileIn, Charsets.UTF_8);
         Files.append("\nline2", tmpFileIn, Charsets.UTF_8);
         Files.append("\nline3", tmpFileIn, Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(tmpFileIn).mimetype("text/plain");
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, tmpFileIn.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, "line1\nline2\nline3", "text/plain");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file with multiple lines with Charset UTF_8
         Files.write("line1", tmpFileIn, Charsets.UTF_8);
         Files.append("\nline2", tmpFileIn, Charsets.UTF_8);
         Files.append("\nline3", tmpFileIn, Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(new FileInputStream(tmpFileIn)).mimetype(
                  "text/plain");
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, tmpFileIn.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, "line1\nline2\nline3", "text/plain");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file with multiple lines with Charset ISO_8859_1
         Files.write("line1", tmpFileIn, Charsets.ISO_8859_1);
         Files.append("\nline2", tmpFileIn, Charsets.ISO_8859_1);
         Files.append("\nline3", tmpFileIn, Charsets.ISO_8859_1);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(new FileInputStream(tmpFileIn),
                  Charsets.ISO_8859_1).mimetype("text/plain");
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, tmpFileIn.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, "line1\nline2\nline3", "text/plain");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with an inputstream
         value = "CDMI dataobject created with input stream";
         is = new ByteArrayInputStream(value.getBytes());
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(is).mimetype("text/plain");
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject, value.length(), null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         validateDataObject(dataObject, value.length(), "text/plain");
         assertNotNull(dataObject.getValueAsInputSupplier());
         assertEquals(CharStreams.toString(CharStreams.newReaderSupplier(
                  dataObject.getValueAsInputSupplier(Charsets.UTF_8), Charsets.UTF_8)), value);
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

      } finally {
         tmpFileIn.delete();
         for (String containerChild : containerApi.get(containerName).getChildren()) {
            Logger.getAnonymousLogger().info("deleting: " + containerChild);
            dataApi.delete(containerName + containerChild);
         }

         containerApi.delete(containerName);

      }

   }

   @Test
   public void testGetDataObjects() throws Exception {
      String serverType = System.getProperty("test.cdmi.serverType", "openstack");
      containerName = "MyContainer" + System.currentTimeMillis() + "/";
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
      Logger.getAnonymousLogger().info("running tests on serverType: " + serverType);
      Logger.getAnonymousLogger().info("createContainer: " + containerName);
      Container container = containerApi.create(containerName);
      try {
         assertNotNull(container);
         Logger.getAnonymousLogger().info(container.toString());
         container = containerApi.get(containerName);
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);

         // exercise create data object with value mimetype and metadata
         value = "Hello CDMI data object with value mimetype and metadata";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value).mimetype("text/plain")
                  .metadata(dataObjectMetaDataIn);
         dataObject = dataApi.create(containerName + dataObjectNameIn, pCreateDataObjectOptions);
         validateDataObject(dataObject,value.length(),null);
         dataObject = dataApi.get(containerName + dataObjectNameIn);
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         keys = dataObjectMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(dataObjectMetaDataOut.containsKey(key), true);
            assertEquals(dataObjectMetaDataOut.get(key), dataObjectMetaDataIn.get(key));
         }         
         validateDataObject(dataObject, value, "text/plain");

         dataObject = dataApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder.field("objectName")
                  .field("mimetype"));
         assertNotNull(dataObject);
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getMimetype(), "text/plain");

         dataObject = dataApi.get(containerName + dataObjectNameIn,
                  DataObjectQueryParams.Builder.metadata().field("objectName").field("mimetype"));
         assertNotNull(dataObject);
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getMimetype(), "text/plain");

         dataObject = dataApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder.field("mimetype")
                  .metadata("dataObjectkey2").field("objectName"));
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
            Logger.getAnonymousLogger().info("running parentURI tests.");
            dataObject = dataApi
                     .get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder.field("parentURI"));
            assertNotNull(dataObject);
            Logger.getAnonymousLogger().info(dataObject.toString());
            assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
            dataObject = dataApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder.field("parentURI")
                     .field("objectName"));
            assertNotNull(dataObject);
            Logger.getAnonymousLogger().info(dataObject.toString());
            assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
            assertEquals(dataObject.getObjectName(), dataObjectNameIn);
            dataObject = dataApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder.field("parentURI")
                     .field("objectName").field("mimetype"));
            assertNotNull(dataObject);
            Logger.getAnonymousLogger().info(dataObject.toString());
            assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
            assertEquals(dataObject.getObjectName(), dataObjectNameIn);
            assertEquals(dataObject.getMimetype(), "text/plain");

            dataObject = dataApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder.field("parentURI")
                     .field("objectName").field("mimetype").metadata());
            assertNotNull(dataObject);
            Logger.getAnonymousLogger().info(dataObject.toString());
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

            dataObject = dataApi.get(containerName + dataObjectNameIn,
                     DataObjectQueryParams.Builder.metadata("cdmi_size"));
            assertNotNull(dataObject);
            Logger.getAnonymousLogger().info(dataObject.toString());
            assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());

            dataObject = dataApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder.field("mimetype")
                     .value());
            assertNotNull(dataObject);
            Logger.getAnonymousLogger().info(dataObject.toString());
            Logger.getAnonymousLogger().info(dataObject.getValueAsString());
            assertEquals(dataObject.getMimetype(), "text/plain");
            assertEquals(dataObject.getValueAsString(), value);

            dataObject = dataApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder.field("mimetype")
                     .value(0, 3));
            assertNotNull(dataObject);
            Logger.getAnonymousLogger().info(dataObject.toString());
            Logger.getAnonymousLogger().info(dataObject.getValueAsString());
            assertEquals(dataObject.getMimetype(), "text/plain");
            // value is SGVsbA==. This needs investigating to determine if this is problem with wss-sonas CDMI
            // server or
            // the jcloud client or
            // must understanding of spec
            Logger.getAnonymousLogger().info(dataObject.toString());
         }
         // validate any query: allows user to indicate special handling with any query.
         dataObject = dataApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder
                  .any("query1=anything").field("objectName").any("anyQueryParam").field("mimetype").metadata());
         assertNotNull(dataObject);
         Logger.getAnonymousLogger().info(dataObject.toString());
         dataApi.delete(containerName + dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

      } finally {
         tmpFileIn.delete();
         for (String containerChild : containerApi.get(containerName).getChildren()) {
            Logger.getAnonymousLogger().info("deleting: " + containerChild);
            dataApi.delete(containerName + containerChild);
         }
         containerApi.delete(containerName);

      }

   }

   private void validateDataObject(DataObject dataObject, String value, String mimetype) {
      validateDataObject(dataObject,value,value.length(),mimetype);
   }

   private void validateDataObject(DataObject dataObject, long valueLength, String mimetype) {
      validateDataObject(dataObject,null,valueLength,mimetype);
   }
   
   private void validateDataObject(DataObject dataObject, String value, long valueLength, String mimetype) {
      assertNotNull(dataObject);
      Logger.getAnonymousLogger().info("validateDataObject("+dataObject.getObjectName()+","+value+","+valueLength+","+mimetype+")");

      Logger.getAnonymousLogger().info(dataObject.toString());
      Logger.getAnonymousLogger().info("value: " + dataObject.getValueAsString());
      if(mimetype!=null) {
         assertEquals(dataObject.getMimetype(), mimetype);
      }
      if(value!=null) {
         assertEquals(dataObject.getValueAsString(), value);
      }
      // openstack does not return cdmi-size
      if (dataObject.getSystemMetadata().get("cdmi_size") != null) {
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), valueLength);
      }
      assertEquals(dataObject.getObjectName(), dataObjectNameIn);
      assertEquals(dataObject.getObjectType(), "application/cdmi-object");
      Logger.getAnonymousLogger().info("parentURI: " + dataObject.getParentURI());
      assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
   }


}
