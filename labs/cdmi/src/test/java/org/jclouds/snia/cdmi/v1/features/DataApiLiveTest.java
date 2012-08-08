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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.jclouds.io.ContentMetadata;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.BaseMutableContentMetadata;
import org.jclouds.io.payloads.ByteArrayPayload;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.io.payloads.InputStreamPayload;
import org.jclouds.io.payloads.StringPayload;
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.internal.BaseCDMIApiLiveTest;
import org.jclouds.snia.cdmi.v1.options.CreateContainerOptions;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectNonCDMIOptions;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectOptions;
import org.jclouds.snia.cdmi.v1.options.GetDataObjectOptions;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

/**
 * @author Kenneth Nagin
 */
@Test(groups = "live", testName = "DataApiLiveTest")
public class DataApiLiveTest extends BaseCDMIApiLiveTest {
	@Test
	public void testCreateDataObjects() throws Exception {

		String containerName = "MyContainer" + System.currentTimeMillis();
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
		GetDataObjectOptions pGetDataObjectOptions;
		DataObject dataObject;
		Iterator<String> keys;
		Map<String, String> dataObjectMetaDataOut;
		Map<String, String> pContainerMetaDataIn = new HashMap<String, String>();
		Map<String, String> pDataObjectMetaDataIn = new LinkedHashMap<String, String>();
		pDataObjectMetaDataIn.put("dataObjectkey1", "value1");
		pDataObjectMetaDataIn.put("dataObjectkey2", "value2");
		pDataObjectMetaDataIn.put("dataObjectkey3", "value3");
		
		Payload payload;

		CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder
				.withMetadata(pContainerMetaDataIn);
		ContainerApi containerApi = cdmiContext.getApi()
				.getContainerApi();
		DataApi dataApi = cdmiContext.getApi().getDataApi();
		Logger.getAnonymousLogger().info("createContainer: " + containerName);
		Container container = containerApi.createContainer(containerName,
				pCreateContainerOptions);
		try {
			assertNotNull(container);
			System.out.println(container);
			container = containerApi.getContainer(containerName);
			assertNotNull(container);
			assertNotNull(container.getChildren());
			assertEquals(container.getChildren().isEmpty(), true);
			
			// exercise create data object with none cdmi put with payload string.
			value = "Hello CDMI World non-cdmi String";
			payload = new StringPayload(value);
			dataApi.createDataObjectNonCDMI(containerName, dataObjectNameIn,
					payload);
			System.out.println(containerApi.getContainer(containerName));
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			//assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			System.out.println("My Metadata: "+dataObject.getUserMetadata());
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with none cdmi put with payload byte array.
			value = "Hello CDMI World non-cdmi byte array";
			bytes = value.getBytes("UTF-8");
			payload = new ByteArrayPayload(bytes);
			dataApi.createDataObjectNonCDMI(containerName, dataObjectNameIn,
					payload);
			System.out.println(containerApi.getContainer(containerName));
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			assertEquals(new String(dataObject.getValueAsByteArray()), value);
			assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);
			

			// exercise create data object with none cdmi put with payload file.
			value = "Hello CDMI World non-cdmi File";
			Files.write(value, tmpFileIn, Charsets.UTF_8);
			payload = new FilePayload(tmpFileIn);
			dataApi.createDataObjectNonCDMI(containerName, dataObjectNameIn,
					payload);
			System.out.println(containerApi.getContainer(containerName));
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
			tmpFileOut.delete();
			assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			System.out.println("My Metadata: "+dataObject.getUserMetadata());
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with none cdmi put with text file payload file.
			inFile = new File(System.getProperty("user.dir")
					+ "/src/test/resources/container.json");
			assertEquals(true, inFile.isFile());
			payload = new FilePayload(inFile);
			dataApi.createDataObjectNonCDMI(containerName, inFile.getName(),
					payload);
			System.out.println(containerApi.getContainer(containerName));
			dataObject = dataApi.getDataObject(containerName,
					inFile.getName());
			assertNotNull(dataObject);
			System.out.println(dataObject);
			//System.out.println("value: " + dataObject.getValueAsString());
			//assertEquals(dataObject.getValueAsString(), value);
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, inFile));
			tmpFileOut.delete();
			assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			//System.out.println("My Metadata: "+dataObject.getUserMetadata());
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), inFile.length());
			assertEquals(dataObject.getObjectName(), inFile.getName());
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(inFile.getName()), true);
			dataApi.deleteDataObject(containerName, inFile.getName());
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);
			
			// exercise create data object with none cdmi put with text file payload file.
			/*
			inFile = new File(System.getProperty("user.dir")
					+ "/src/test/resources/Jellyfish.jpg");
			assertEquals(true, inFile.isFile());
			payload = new FilePayload(inFile);
			dataApi.createDataObjectNonCDMI(containerName, inFile.getName(),
					payload);
			System.out.println(containerApi.getContainer(containerName));
			//getDatObject is throwing an exception
			//dataObject = dataApi.getDataObject(containerName,
			//		inFile.getName());
			//assertNotNull(dataObject);
			//System.out.println(dataObject);
			//tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			//assertEquals(true, Files.equal(tmpFileOut, inFile));
			//tmpFileOut.delete();
			//assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			//System.out.println("My Metadata: "+dataObject.getUserMetadata());
			//assertEquals(
			//		Integer.parseInt(dataObject.getSystemMetadata().get(
			//				"cdmi_size")), inFile.length());
			//assertEquals(dataObject.getObjectName(), inFile.getName());
			//assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			//assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(inFile.getName()), true);
			dataApi.deleteDataObject(containerName, inFile.getName());
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(inFile.getName()), false);
			*/
			
			

			// exercise create data object with none cdmi put with payload inputStream.
			value = "Hello CDMI World non-cdmi inputStream";
			is = new ByteArrayInputStream(value.getBytes());
			payload = new InputStreamPayload(is);
			//ContentMetadata contentMetadata = payload.getContentMetadata().toBuilder().contentLength(new Long(value.length())).build();
			//contentMetadata = contentMetadata.toBuilder().contentLength(new Long(value.length())).build();
			payload.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payload.getContentMetadata().toBuilder().contentLength(new Long(value.length())).build()));
			dataApi.createDataObjectNonCDMI(containerName, dataObjectNameIn,
					payload);
			System.out.println(containerApi.getContainer(containerName));
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			assertNotNull(dataObject.getValueAsInputSupplier());
			assertEquals(CharStreams.toString(CharStreams.newReaderSupplier(dataObject
					.getValueAsInputSupplier(Charsets.UTF_8),Charsets.UTF_8)), value);
			assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			System.out.println("My Metadata: "+dataObject.getUserMetadata());
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);
			
			
			

			// exercise create data object with none cdmi put
			value = "Hello CDMI World1";
			CreateDataObjectNonCDMIOptions pCreateDataObjectNoneCDMIOptions = CreateDataObjectNonCDMIOptions.Builder
					.withStringPayload(value);
			dataApi.createDataObjectNonCDMI(containerName, dataObjectNameIn,
					pCreateDataObjectNoneCDMIOptions);
			System.out.println(containerApi.getContainer(containerName));
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			System.out.println("My Metadata: "+dataObject.getUserMetadata());
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);
			// exercise create data object with value mimetype and metadata
			value = "Hello CDMI World2";
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.value(value).mimetype("text/plain")
					.metadata(pDataObjectMetaDataIn);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
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
				assertEquals(dataObjectMetaDataOut.get(key),
						pDataObjectMetaDataIn.get(key));
			}
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			
			//pGetDataObjectOptions = GetDataObjectOptions.Builder.field("mimetype").field("value");
			//dataObject = dataApi.getDataObject(containerName,
			//		dataObjectNameIn,pGetDataObjectOptions);
			//assertNotNull(dataObject);

			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// verify that options order does not matter
			value = "Hello CDMI World3";
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.metadata(pDataObjectMetaDataIn).mimetype("text/plain")
					.value(value);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
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
				assertEquals(dataObjectMetaDataOut.get(key),
						pDataObjectMetaDataIn.get(key));
			}
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with empty metadata
			value = "Hello CDMI World4";
			pDataObjectMetaDataIn.clear();
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.value(value).mimetype("text/plain")
					.metadata(pDataObjectMetaDataIn);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getMimetype(), "text/plain");
			assertEquals(dataObject.getValueAsString(), value);
			dataObjectMetaDataOut = dataObject.getUserMetadata();
			assertNotNull(dataObjectMetaDataOut);
			assertEquals(dataObjectMetaDataOut.isEmpty(),true);
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with null metadata
			value = "Hello CDMI World5";
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(
					value).mimetype("text/plain");
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getMimetype(), "text/plain");
			assertEquals(dataObject.getValueAsString(), value);
			dataObjectMetaDataOut = dataObject.getUserMetadata();
			assertNotNull(dataObjectMetaDataOut);
			assertEquals(true, dataObjectMetaDataOut.isEmpty());
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with only value
			value = "Hello CDMI World6";
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.value(value);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			dataObjectMetaDataOut = dataObject.getUserMetadata();
			assertNotNull(dataObjectMetaDataOut);
			assertEquals(dataObjectMetaDataOut.isEmpty(),true);
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with empty mimetype only
			value = "";
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.mimetype(new String());
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			//assertEquals(dataObject.getMimetype(), "");
			assertEquals(dataObject.getValueAsString(), "");
			dataObjectMetaDataOut = dataObject.getUserMetadata();
			assertNotNull(dataObjectMetaDataOut);
			//assertEquals(dataObjectMetaDataOut.isEmpty(),true);

			dataApi.deleteDataObject(containerName, dataObjectNameIn);

			// exercise create data object with no value
			value = "";
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value();
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), "");
			dataObjectMetaDataOut = dataObject.getUserMetadata();
			assertNotNull(dataObjectMetaDataOut);
			assertEquals(dataObjectMetaDataOut.isEmpty(),true);
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with byte array
			value = "Hello CDMI World 7";
			out.writeUTF(value);
			out.close();
			bytes = bos.toByteArray();
			// String.getBytes causes an exception CreateDataObjectOptions need to investigate byte arrays
			//bytes = value.getBytes("UTF-8");  
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.value(bytes);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			assertEquals(new String(dataObject.getValueAsByteArray()), value);
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with an existing file
			inFile = new File(System.getProperty("user.dir")
					+ "/src/test/resources/container.json");
			assertEquals(true, inFile.isFile());
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.value(inFile);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, inFile));
			tmpFileOut.delete();
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")),
					Files.toString(inFile, Charsets.UTF_8).length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with a temporary file that we create
			// on the fly
			// with default Charset
			value = "Hello CDMI World 10";
			Files.write(value, tmpFileIn, Charsets.UTF_8);
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.value(tmpFileIn);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
			tmpFileOut.delete();
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with a temporary file that we create
			// on the fly
			// specify charset UTF_8
			Files.write(value, tmpFileIn, Charsets.UTF_8);
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(
					tmpFileIn, Charsets.UTF_8);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
			tmpFileOut.delete();
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with a temporary file that we create
			// on the fly
			// specify charset US_ASCII
			Files.write(value, tmpFileIn, Charsets.US_ASCII);
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(
					tmpFileIn, Charsets.US_ASCII);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
			tmpFileOut.delete();
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with a temporary file with multiple
			// lines
			// with default Charset
			Files.write("line1", tmpFileIn, Charsets.UTF_8);
			Files.append("\nline2", tmpFileIn, Charsets.UTF_8);
			Files.append("\nline3", tmpFileIn, Charsets.UTF_8);
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.value(tmpFileIn);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), "line1\nline2\nline3");
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
			tmpFileOut.delete();
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")),
					Files.toString(tmpFileIn, Charsets.UTF_8).length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with a temporary file with multiple
			// lines
			// with Charset UTF_8
			Files.write("line1", tmpFileIn, Charsets.UTF_8);
			Files.append("\nline2", tmpFileIn, Charsets.UTF_8);
			Files.append("\nline3", tmpFileIn, Charsets.UTF_8);
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.value(new FileInputStream(tmpFileIn));
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), "line1\nline2\nline3");
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
			tmpFileOut.delete();
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")),
					Files.toString(tmpFileIn, Charsets.UTF_8).length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with a temporary file with multiple
			// lines
			// with Charset ISO_8859_1
			Files.write("line1", tmpFileIn, Charsets.ISO_8859_1);
			Files.append("\nline2", tmpFileIn, Charsets.ISO_8859_1);
			Files.append("\nline3", tmpFileIn, Charsets.ISO_8859_1);
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder.value(
					new FileInputStream(tmpFileIn), Charsets.ISO_8859_1);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), "line1\nline2\nline3");
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
			tmpFileOut.delete();
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")),
					Files.toString(tmpFileIn, Charsets.ISO_8859_1).length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with an inputstream
			is = new ByteArrayInputStream(value.getBytes());
			pCreateDataObjectOptions = CreateDataObjectOptions.Builder
					.value(is);
			dataObject = dataApi.createDataObject(containerName,
					dataObjectNameIn, pCreateDataObjectOptions);
			assertNotNull(dataObject);
			dataObject = dataApi.getDataObject(containerName,
					dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			assertNotNull(dataObject.getValueAsInputSupplier());
			assertEquals(CharStreams.toString(CharStreams.newReaderSupplier(dataObject
					.getValueAsInputSupplier(Charsets.UTF_8),Charsets.UTF_8)), value);
			assertEquals(
					Integer.parseInt(dataObject.getSystemMetadata().get(
							"cdmi_size")), value.length());
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getParentURI(), "/" + containerName + "/");
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), true);
			dataApi.deleteDataObject(containerName, dataObjectNameIn);
			assertEquals(containerApi.getContainer(containerName)
					.getChildren().contains(dataObjectNameIn), false);
		} finally {
			tmpFileIn.delete();
			containerApi.deleteContainer(containerName);

		}

	}

}
