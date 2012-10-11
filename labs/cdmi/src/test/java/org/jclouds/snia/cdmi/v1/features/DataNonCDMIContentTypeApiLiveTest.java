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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

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
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
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
	@Test
	public void testCreateDataObjectsNonCDMI() throws Exception {
		String serverType = System.getProperty("test.cdmi.serverType", "openstack");
		String containerName = "MyContainer" + System.currentTimeMillis() + "/";
		String dataObjectNameIn = "dataobject.txt";
		File tmpFileIn = new File("temp.txt");
		String value;
		InputStream is;
		File tmpFileOut;
		File inFile;
		Files.touch(tmpFileIn);
		byte[] bytes;
		DataObject dataObject;
		Map<String, String> pContainerMetaDataIn = new HashMap<String, String>();
		Map<String, String> pDataObjectMetaDataIn = new LinkedHashMap<String, String>();
		pDataObjectMetaDataIn.put("dataObjectkey1", "value1");
		pDataObjectMetaDataIn.put("dataObjectkey2", "value2");
		pDataObjectMetaDataIn.put("dataObjectkey3", "value3");

		Payload payloadIn;
		Payload payloadOut;
		FileOutputStream fos;

		CreateContainerOptions pCreateContainerOptions = CreateContainerOptions.Builder.metadata(pContainerMetaDataIn);
		ContainerApi containerApi = cdmiContext.getApi().getApi();
		DataApi dataApi = cdmiContext.getApi().getDataApiForContainer(containerName);
		DataNonCDMIContentTypeApi dataNonCDMIContentTypeApi = cdmiContext.getApi()
					.getDataNonCDMIContentTypeApiForContainer(containerName);
		Logger.getAnonymousLogger().info("createContainer: " + containerName);
		Container container = containerApi.create(containerName, pCreateContainerOptions);
		try {

			assertNotNull(container);
			System.out.println("container: " + container);
			container = containerApi.get(containerName);
			assertNotNull(container);
			assertNotNull(container.getChildren());
			assertEquals(container.getChildren().isEmpty(), true);

			// exercise create data object with none cdmi put with payload
			// string.
			value = "Hello CDMI World non-cdmi String";
			dataNonCDMIContentTypeApi.create(containerName + dataObjectNameIn, value);
			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + dataObjectNameIn);
			assertNotNull(payloadOut);
			assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")), value);
			payloadIn = new StringPayload(value);
			payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
						.toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));
			dataNonCDMIContentTypeApi.create(containerName + dataObjectNameIn, payloadIn);

			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + dataObjectNameIn);
			assertNotNull(payloadOut);
			assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")), value);
			// openstack only returning payload for filtered queries
			if (serverType.matches("openstack")) {
				System.out.println("retrieving objectName with dataApi");
				dataObject = dataApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder
							.field("objectName").field("objectType").field("mimetype"));
			} else {
				System.out.println("retrieving objectName with dataNonCDMIContentTypeApi");
				dataObject = dataNonCDMIContentTypeApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder
							.field("objectName").field("objectType").field("mimetype"));
			}
			assertNotNull(dataObject);
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			assertEquals(dataObject.getMimetype(), MediaType.PLAIN_TEXT_UTF_8.toString());

			if (!serverType.matches("openstack")) { // openstack not handling
				// query filters, parent
				// uri, or cdmi-size

				System.out.println("retrieving parent URI");
				dataObject = dataNonCDMIContentTypeApi.get(containerName + dataObjectNameIn,
							DataObjectQueryParams.Builder.field("parentURI"));
				System.out.println("retrieved parent URI");

				assertNotNull(dataObject);
				System.out.println(dataObject);
				assertEquals(dataObject.getParentURI(), "/" + containerName);
				dataObject = dataNonCDMIContentTypeApi.get(containerName + dataObjectNameIn, DataObjectQueryParams.Builder
							.metadata().field("parentURI").field("objectName").field("objectType").field("mimetype"));
				assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
				assertEquals(dataObject.getObjectName(), dataObjectNameIn);
				assertEquals(dataObject.getObjectType(), "application/cdmi-object");
				assertEquals(dataObject.getParentURI(), "/" + containerName);
				assertEquals(dataObject.getMimetype(), MediaType.PLAIN_TEXT_UTF_8.toString());
			}

			dataNonCDMIContentTypeApi.delete(containerName + dataObjectNameIn);
			assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with none cdmi put with payload byte
			// array.
			value = "Hello CDMI World non-cdmi byte array";
			bytes = value.getBytes("UTF-8");
			payloadIn = new ByteArrayPayload(bytes);
			payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
						.toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));
			dataNonCDMIContentTypeApi.create(containerName + dataObjectNameIn, payloadIn);
			System.out.println(containerApi.get(containerName));
			dataObject = dataApi.get(containerName + dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			assertEquals(new String(dataObject.getValueAsByteArray()), value);
			assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			// openstack not returning cdmi_size
			if (dataObject.getSystemMetadata().get("cdmi_size") != null) {
				assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
			}
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			if (!serverType.matches("openstack")) { // openstack not handling
				// parentURI properly
				assertEquals(dataObject.getParentURI(), "/" + containerName);
				assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
				payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + dataObjectNameIn);
				assertNotNull(payloadOut);
				assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")), value);
			}

			dataNonCDMIContentTypeApi.delete(containerName + dataObjectNameIn);
			assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with none cdmi put with payload file.
			value = "Hello CDMI World non-cdmi File";
			Files.write(value, tmpFileIn, Charsets.UTF_8);
			payloadIn = new FilePayload(tmpFileIn);
			payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
						.toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));

			dataNonCDMIContentTypeApi.create(containerName + dataObjectNameIn, payloadIn);
			System.out.println(containerApi.get(containerName));
			dataObject = dataApi.get(containerName + dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			tmpFileOut = dataObject.getValueAsFile(Files.createTempDir());
			assertEquals(true, Files.equal(tmpFileOut, tmpFileIn));
			tmpFileOut.delete();
			assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			// openstack not returning cdmi_size
			if (dataObject.getSystemMetadata().get("cdmi_size") != null) {
				assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
			}
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			if (!serverType.matches("openstack")) { // openstack not handling
				// parentURI properly
				assertEquals(dataObject.getParentURI(), "/" + containerName);
			}
			assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);

			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + dataObjectNameIn);
			assertNotNull(payloadOut);
			// assertEquals(CharStreams.toString(new
			// InputStreamReader(payloadOut.getInput(), "UTF-8")),value);
			// byte[] _bytes = ByteStreams.toByteArray(payloadOut.getInput());
			tmpFileOut = new File(Files.createTempDir(), "temp.txt");
			fos = new FileOutputStream(tmpFileOut);
			ByteStreams.copy(payloadOut.getInput(), fos);
			fos.flush();
			fos.close();
			assertEquals(Files.equal(tmpFileOut, tmpFileIn), true);
			tmpFileOut.delete();

			dataNonCDMIContentTypeApi.delete(containerName + dataObjectNameIn);
			assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with none cdmi put with text file
			// payload file.
			inFile = new File(System.getProperty("user.dir") + "/src/test/resources/container.json");
			assertEquals(true, inFile.isFile());
			payloadIn = new FilePayload(inFile);
			payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
						.toBuilder().contentType(MediaType.JSON_UTF_8.toString()).build()));

			dataNonCDMIContentTypeApi.create(containerName + inFile.getName(), payloadIn);
			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + inFile.getName());
			assertNotNull(payloadOut);
			tmpFileOut = new File(Files.createTempDir(), "temp.json");
			fos = new FileOutputStream(tmpFileOut);
			ByteStreams.copy(payloadOut.getInput(), fos);
			fos.flush();
			fos.close();
			assertEquals(Files.equal(tmpFileOut, inFile), true);
			tmpFileOut.delete();

			System.out.println(containerApi.get(containerName));

			dataObject = dataApi.get(containerName + inFile.getName());
			assertNotNull(dataObject);
			System.out.println(dataObject);
			assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			// openstack not returning cdmi_size
			if (dataObject.getSystemMetadata().get("cdmi_size") != null) {
				assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), inFile.length());
			}
			assertEquals(dataObject.getObjectName(), inFile.getName());
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			if (!serverType.matches("openstack")) { // openstack not handling
				// parentURI properly
				assertEquals(dataObject.getParentURI(), "/" + containerName);
			}
			assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), true);
			dataApi.delete(containerName + inFile.getName());
			assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with none cdmi put with text file
			// payload file.
			// inFile = new File(System.getProperty("user.dir")
			// + "/src/test/resources/Jellyfish.jpg"); // takes too long when
			// working from home
			inFile = new File(System.getProperty("user.dir") + "/src/test/resources/yellow-flowers.jpg");
			assertEquals(true, inFile.isFile());
			payloadIn = new FilePayload(inFile);
			payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
						.toBuilder().contentType(MediaType.JPEG.toString()).build()));
			dataNonCDMIContentTypeApi.create(containerName + inFile.getName(), payloadIn);
			System.out.println(containerApi.get(containerName));
			// note dataApi.getDataObject when the data object is not a string
			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + inFile.getName());
			assertNotNull(payloadOut);
			tmpFileOut = new File(Files.createTempDir(), "temp.jpg");
			fos = new FileOutputStream(tmpFileOut);
			ByteStreams.copy(payloadOut.getInput(), fos);
			fos.flush();
			fos.close();
			assertEquals(Files.equal(tmpFileOut, inFile), true);
			tmpFileOut.delete();

			assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), true);
			dataApi.delete(containerName + inFile.getName());
			assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), false);

			// exercise create data object with none cdmi put with payload
			// inputStream riginating from string.
			value = "Hello CDMI World non-cdmi inputStream originating from string";
			is = new ByteArrayInputStream(value.getBytes());
			payloadIn = new InputStreamPayload(is);
			payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
						.toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString())
						.contentLength(new Long(value.length())).build()));
			dataNonCDMIContentTypeApi.create(containerName + dataObjectNameIn, payloadIn);
			System.out.println(containerApi.get(containerName));
			dataObject = dataApi.get(containerName + dataObjectNameIn);
			assertNotNull(dataObject);
			System.out.println(dataObject);
			System.out.println("value: " + dataObject.getValueAsString());
			assertEquals(dataObject.getValueAsString(), value);
			assertNotNull(dataObject.getValueAsInputSupplier());
			assertEquals(CharStreams.toString(CharStreams.newReaderSupplier(
						dataObject.getValueAsInputSupplier(Charsets.UTF_8), Charsets.UTF_8)), value);
			assertEquals(dataObject.getUserMetadata().isEmpty(), true);
			// openstack not returning cdmi_size
			if (dataObject.getSystemMetadata().get("cdmi_size") != null) {
				assertEquals(Integer.parseInt(dataObject.getSystemMetadata().get("cdmi_size")), value.length());
			}
			assertEquals(dataObject.getObjectName(), dataObjectNameIn);
			assertEquals(dataObject.getObjectType(), "application/cdmi-object");
			if (!serverType.matches("openstack")) { // openstack not handling
				// parentURI properly
				assertEquals(dataObject.getParentURI(), "/" + containerName);
			}
			assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), true);
			dataNonCDMIContentTypeApi.delete(containerName + dataObjectNameIn);
			assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with none cdmi put with payload
			// inputStream originating from jpeg file.
			inFile = new File(System.getProperty("user.dir") + "/src/test/resources/yellow-flowers.jpg");
			assertEquals(true, inFile.isFile());
			FileInputStream fileInputStream = new FileInputStream(inFile);
			payloadIn = new InputStreamPayload(fileInputStream);
			payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
						.toBuilder().contentType(MediaType.JPEG.toString()).contentLength(new Long(inFile.length())).build()));
			dataNonCDMIContentTypeApi.create(containerName + inFile.getName(), payloadIn);
			System.out.println(containerApi.get(containerName));
			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + inFile.getName());
			assertNotNull(payloadOut);
			tmpFileOut = new File(Files.createTempDir(), "temp.jpg");
			fos = new FileOutputStream(tmpFileOut);
			ByteStreams.copy(payloadOut.getInput(), fos);
			fos.flush();
			fos.close();
			assertEquals(Files.equal(tmpFileOut, inFile), true);
			tmpFileOut.delete();

			// get payload with any queryParms
			// on openstack returns payload and allows user to indicate special
			// handling.
			if (serverType.matches("openstack")) {
				payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + inFile.getName(),
							DataObjectQueryParams.Builder.any("query1=anything").field("objectName").any("anyQueryParam")
										.field("mimetype").metadata());
				assertNotNull(payloadOut);
				tmpFileOut = new File(Files.createTempDir(), "temp.jpg");
				fos = new FileOutputStream(tmpFileOut);
				ByteStreams.copy(payloadOut.getInput(), fos);
				fos.flush();
				fos.close();
				assertEquals(Files.equal(tmpFileOut, inFile), true);
				tmpFileOut.delete();
			}

			assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), true);
			dataApi.delete(containerName + inFile.getName());
			assertEquals(containerApi.get(containerName).getChildren().contains(inFile.getName()), false);

			// exercise get with none cdmi get range.
			value = "Hello CDMI World non-cdmi String";
			payloadIn = new StringPayload(value);
			payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
						.toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));
			dataNonCDMIContentTypeApi.create(containerName + dataObjectNameIn, payloadIn);

			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + dataObjectNameIn, "bytes=0-10");
			assertNotNull(payloadOut);
			assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")),
						value.substring(0, 11));
			assertEquals(payloadOut.getContentMetadata().getContentLength(), new Long(11));

			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + dataObjectNameIn, "bytes=11-20");
			assertNotNull(payloadOut);
			assertEquals(CharStreams.toString(new InputStreamReader(payloadOut.getInput(), "UTF-8")),
						value.substring(11, 21));
			assertEquals(payloadOut.getContentMetadata().getContentLength(), new Long(10));

			dataNonCDMIContentTypeApi.delete(containerName + dataObjectNameIn);
			assertEquals(containerApi.get(containerName).getChildren().contains(dataObjectNameIn), false);

			// exercise create data object with none cdmi partial.
			// server does not actually support cdmi partial but
			// trace allows me to see that request was constructed properly
			value = "Hello CDMI World non-cdmi String";
			payloadIn = new StringPayload(value);
			payloadIn.setContentMetadata(BaseMutableContentMetadata.fromContentMetadata(payloadIn.getContentMetadata()
						.toBuilder().contentType(MediaType.PLAIN_TEXT_UTF_8.toString()).build()));
			dataNonCDMIContentTypeApi.createPartial(containerName + dataObjectNameIn, payloadIn);
			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + dataObjectNameIn);
			assertNotNull(payloadOut);
			System.out.println("payload " + payloadOut);

			dataNonCDMIContentTypeApi.createPartial(containerName + dataObjectNameIn, payloadIn);
			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + dataObjectNameIn);
			assertNotNull(payloadOut);
			System.out.println("payload " + payloadOut);

			dataNonCDMIContentTypeApi.create(containerName + dataObjectNameIn, payloadIn);

			payloadOut = dataNonCDMIContentTypeApi.getValue(containerName + dataObjectNameIn);
			assertNotNull(payloadOut);
			System.out.println("payload " + payloadOut);

		} finally {
			tmpFileIn.delete();
			for (String containerChild : containerApi.get(containerName).getChildren()) {
				System.out.println("deleting: " + containerChild);
				dataNonCDMIContentTypeApi.delete(containerName + containerChild);
			}
			containerApi.delete(containerName);

		}

	}

}
