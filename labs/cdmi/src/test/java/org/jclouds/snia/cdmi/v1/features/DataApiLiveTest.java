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
import org.jclouds.snia.cdmi.v1.options.CreateContainerOptions;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectOptions;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

/**
 * @author Kenneth Nagin
 */
@Test(groups = "live", testName = "DataApiLiveTest")
public class DataApiLiveTest extends BaseCDMIApiLiveTest {
   @Test
   public void testCreateDataObjects() throws Exception {

      String containerName = "MyContainer" + System.currentTimeMillis() + "/";
      String dataObjectNameIn = "dataobject08121.txt";
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
      Iterator<String> keys;
      Map<String, String> dataObjectMetaDataOut;
      Map<String, String> pContainerMetaDataIn = Maps.newHashMap();
      Map<String, String> pDataObjectMetaDataIn = Maps.newLinkedHashMap();
      pDataObjectMetaDataIn.put("dataObjectkey1", "value1");
      pDataObjectMetaDataIn.put("dataObjectkey2", "value2");
      pDataObjectMetaDataIn.put("dataObjectkey3", "value3");

      CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.metadata(pContainerMetaDataIn);
      ContainerApi containerApi = cdmiContext.getApi().getApi();
      DataApi dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
      Logger.getAnonymousLogger().info("create: " + containerName);
      Container container = containerApi.create(containerName, pCreateContainerOptions);
      try {
         assertNotNull(container);
         System.out.println(container);
         container = containerApi.get(containerName);
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);

         // exercise create data object with value mimetype and metadata
         value = "Hello CDMI data object with value mimetype and metadata";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value).mimetype("text/plain")
                  .metadata(pDataObjectMetaDataIn);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getMimetype(), "text/plain");
         assertEquals(dataObject.getValueAsString(), value);
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         keys = pDataObjectMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(dataObjectMetaDataOut.containsKey(key), true);
            assertEquals(dataObjectMetaDataOut.get(key), pDataObjectMetaDataIn.get(key));
         }
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);

         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // verify that options order does not matter
         value = "Hello CDMI World3";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.metadata(pDataObjectMetaDataIn)
                  .mimetype("text/plain").value(value);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getMimetype(), "text/plain");
         assertEquals(dataObject.getValueAsString(), value);
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         keys = pDataObjectMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(dataObjectMetaDataOut.containsKey(key), true);
            assertEquals(dataObjectMetaDataOut.get(key), pDataObjectMetaDataIn.get(key));
         }
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with empty metadata
         value = "Hello CDMI World4";
         pDataObjectMetaDataIn.clear();
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value).mimetype("text/plain")
                  .metadata(pDataObjectMetaDataIn);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getMimetype(), "text/plain");
         assertEquals(dataObject.getValueAsString(), value);
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         assertEquals(dataObjectMetaDataOut.isEmpty(), true);
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with null metadata
         value = "Hello CDMI World5";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value).mimetype("text/plain");
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getMimetype(), "text/plain");
         assertEquals(dataObject.getValueAsString(), value);
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         assertEquals(true, dataObjectMetaDataOut.isEmpty());
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with only value
         value = "Hello CDMI World6";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), value);
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         assertEquals(dataObjectMetaDataOut.isEmpty(), true);
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with empty mimetype only
         value = "";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.mimetype(new String());
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         // assertEquals(dataObject.getMimetype(), "");
         assertEquals(dataObject.getValueAsString(), "");
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         // assertEquals(dataObjectMetaDataOut.isEmpty(),true);

         dataApi.delete(dataObjectNameIn);

         // exercise create data object with no value
         value = "";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value();
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), "");
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         assertEquals(dataObjectMetaDataOut.isEmpty(), true);
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with byte array
         value = "Hello CDMI World 7";
         out.writeUTF(value);
         out.close();
         bytes = bos.toByteArray();
         // String.getBytes causes an exception CreateDataObjectOptions need to investigate byte
         // arrays
         // bytes = value.getBytes(Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(bytes);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), value);
         assertEquals(new String(dataObject.getValueAsByteArray()), value);
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with an existing file
         inFile = new File(System.getProperty("user.dir") + "/src/test/resources/container.json");
         assertEquals(true, inFile.isFile());
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(inFile);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, inFile));
         tmpFileOut.delete();
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")),
                  Files.toString(inFile, Charsets.UTF_8).length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file that we create
         // on the fly
         // with default Charset
         value = "Hello CDMI World 10";
         Files.write(value, tmpFileIn, Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(tmpFileIn);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), value);
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file that we create
         // on the fly
         // specify charset UTF_8
         Files.write(value, tmpFileIn, Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(tmpFileIn, Charsets.UTF_8);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), value);
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file that we create
         // on the fly
         // specify charset US_ASCII
         Files.write(value, tmpFileIn, Charsets.US_ASCII);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(tmpFileIn, Charsets.US_ASCII);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), value);
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file with multiple
         // lines
         // with default Charset
         Files.write("line1", tmpFileIn, Charsets.UTF_8);
         Files.append("\nline2", tmpFileIn, Charsets.UTF_8);
         Files.append("\nline3", tmpFileIn, Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(tmpFileIn);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), "line1\nline2\nline3");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")),
                  Files.toString(tmpFileIn, Charsets.UTF_8).length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file with multiple
         // lines
         // with Charset UTF_8
         Files.write("line1", tmpFileIn, Charsets.UTF_8);
         Files.append("\nline2", tmpFileIn, Charsets.UTF_8);
         Files.append("\nline3", tmpFileIn, Charsets.UTF_8);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(new FileInputStream(tmpFileIn));
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), "line1\nline2\nline3");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")),
                  Files.toString(tmpFileIn, Charsets.UTF_8).length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with a temporary file with multiple
         // lines
         // with Charset ISO_8859_1
         Files.write("line1", tmpFileIn, Charsets.ISO_8859_1);
         Files.append("\nline2", tmpFileIn, Charsets.ISO_8859_1);
         Files.append("\nline3", tmpFileIn, Charsets.ISO_8859_1);
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(new FileInputStream(tmpFileIn),
                  Charsets.ISO_8859_1);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), "line1\nline2\nline3");
         tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
         assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
         tmpFileOut.delete();
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")),
                  Files.toString(tmpFileIn, Charsets.ISO_8859_1).length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

         // exercise create data object with an inputstream
         is = new ByteArrayInputStream(value.getBytes());
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(is);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getValueAsString(), value);
         assertNotNull(dataObject.getValueAsInputSupplier());
         assertEquals(CharStreams.toString(CharStreams.newReaderSupplier(
                  dataObject.getValueAsInputSupplier(Charsets.UTF_8), Charsets.UTF_8)), value);
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);
      } finally {
         tmpFileIn.delete();
         containerApi.delete(containerName);

      }

   }

   @Test
   public void testGetDataObjects() throws Exception {

      String containerName = "MyContainer" + System.currentTimeMillis() + "/";
      String dataObjectNameIn = "dataobject08121.txt";
      File tmpFileIn = new File("temp.txt");
      String value;
      Files.touch(tmpFileIn);

      CreateDataObjectOptions pCreateDataObjectOptions;
      DataObject dataObject;
      Iterator<String> keys;
      Map<String, String> dataObjectMetaDataOut;
      Map<String, String> pContainerMetaDataIn = Maps.newHashMap();
      Map<String, String> pDataObjectMetaDataIn = Maps.newLinkedHashMap();
      pDataObjectMetaDataIn.put("dataObjectkey1", "value1");
      pDataObjectMetaDataIn.put("dataObjectkey2", "value2");
      pDataObjectMetaDataIn.put("dataObjectkey3", "value3");

      CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.metadata(pContainerMetaDataIn);
      ContainerApi containerApi = cdmiContext.getApi().getApi();
      DataApi dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
      Logger.getAnonymousLogger().info("create: " + containerName);
      Container container = containerApi.create(containerName, pCreateContainerOptions);
      try {
         assertNotNull(container);
         System.out.println(container);
         container = containerApi.get(containerName);
         assertNotNull(container);
         assertNotNull(container.getChildren());
         assertEquals(container.getChildren().isEmpty(), true);

         // exercise create data object with value mimetype and metadata
         value = "Hello CDMI data object with value mimetype and metadata";
         pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(value).mimetype("text/plain")
                  .metadata(pDataObjectMetaDataIn);
         dataObject = dataApi.create(dataObjectNameIn, pCreateDataObjectOptions);
         assertNotNull(dataObject);
         dataObject = dataApi.get(dataObjectNameIn);
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println("value: " + dataObject.getValueAsString());
         assertEquals(dataObject.getMimetype(), "text/plain");
         assertEquals(dataObject.getValueAsString(), value);
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         keys = pDataObjectMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(dataObjectMetaDataOut.containsKey(key), true);
            assertEquals(dataObjectMetaDataOut.get(key), pDataObjectMetaDataIn.get(key));
         }
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getObjectType(), "application/cdmi-object");
         assertEquals(dataObject.getParentURI(), "/" + containerName);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);

         dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("parentURI"));
         assertNotNull(dataObject);
         System.out.println(dataObject);
         assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());

         dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("parentURI")
                  .field("objectName"));
         assertNotNull(dataObject);
         System.out.println(dataObject);
         assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);

         dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("parentURI")
                  .field("objectName").field("mimetype"));
         assertNotNull(dataObject);
         System.out.println(dataObject);
         assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getMimetype(), "text/plain");

         dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("parentURI")
                  .field("objectName").field("mimetype").metadata());
         assertNotNull(dataObject);
         System.out.println(dataObject);
         assertEquals(dataObject.getParentURI(), container.getParentURI() + container.getObjectName());
         assertEquals(dataObject.getObjectName(), dataObjectNameIn);
         assertEquals(dataObject.getMimetype(), "text/plain");
         dataObjectMetaDataOut = dataObject.getUserMetadata();
         assertNotNull(dataObjectMetaDataOut);
         keys = pDataObjectMetaDataIn.keySet().iterator();
         while (keys.hasNext()) {
            String key = keys.next();
            assertEquals(dataObjectMetaDataOut.containsKey(key), true);
            assertEquals(dataObjectMetaDataOut.get(key), pDataObjectMetaDataIn.get(key));
         }
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());

         dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.metadata("cdmi_size"));
         assertNotNull(dataObject);
         System.out.println(dataObject);
         assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());

         dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("mimetype").value());
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println(dataObject.getValueAsString());
         assertEquals(dataObject.getMimetype(), "text/plain");
         assertEquals(dataObject.getValueAsString(), value);

         dataObject = dataApi.get(dataObjectNameIn, DataObjectQueryParams.Builder.field("mimetype").value(0, 3));
         assertNotNull(dataObject);
         System.out.println(dataObject);
         System.out.println(dataObject.getValueAsString());
         assertEquals(dataObject.getMimetype(), "text/plain");
         // value is SGVsbA==. This needs investigating to determine if this
         // is problem with CDMI server or the jcloud client or must understanding of spec

         dataApi.delete(dataObjectNameIn);
         assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);
      } finally {
         tmpFileIn.delete();
         containerApi.delete(containerName);

      }

   }

}
