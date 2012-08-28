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

import java.util.concurrent.TimeUnit;
import org.jclouds.concurrent.Timeout;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectOptions;
import org.jclouds.snia.cdmi.v1.queryparams.DataObjectQueryParams;

/**
 * Data Object Resource Operations
 * 
 * @see DataAsyncApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface DataApi {
	/**
	 * get CDMI Data object
	 * 
	 * @param containerName
	 *            containerName must end with a forward slash, /.
	 * @param  dataObjectName
	 *            dataObjectName must not end with a forward slash, /.           
	 * @return DataObject
	 *  <pre>
	 *  Examples: 
	 *  {@code
	 *  dataObject = getDataObject("myContainer/","myDataObject");
	 *  dataObject = getDataObject("parentContainer/childContainer/","myDataObject");
	 *  }
	 *  <pre>
	 */
	DataObject getDataObject(String containerName, String dataObjectName);
	/**
	 * get CDMI Data object
	 * 
	 * @param containerName
	 *            containerName must end with a forward slash, /.
	 * @param dataObjectName
	 *            dataObjectName must not end with a forward slash, /. 
	 * @param queryParams 
	 *            enables getting only certain fields, metadata, value range
	 * @return DataObject
	 *  <pre>
	 *  Examples: 
	 *  {@code
	 *  dataObject = getContainer("myContainer/","myDataObject",ContainerQueryParams.Builder.field("parentURI").field("objectName"));
	 *  dataObject = getContainer("myContainer/","myDataObject",ContainerQueryParams.Builder.value(0,10));
	 *  }
	 *  <pre>
	 */
	DataObject getDataObject(String containerName, String dataObjectName,
			DataObjectQueryParams queryParams);

	/**
	 * create CDMI Data object
	 * 
	 * @param containerName
	 *            containerName must end with a forward slash, /.
	 * @param dataObjectName
	 *            dataObjectName must not end with a forward slash, /. 
	 * @param options 
	 *            enables defining the body i.e. metadata, mimetype, value
	 * @return DataObject
	 *  <pre>
	 *  Examples: 
	 *  {@code
	 *  dataObject = createDataObject("myContainer/",
	 *                                "myDataObject",
	 *                                CreateDataObjectOptions.Builder
	 *                                                    .value(value)
	 *                                                    .mimetype("text/plain")
	 *                                                    .metadata(pDataObjectMetaDataIn);
	 *  }
	 *  <pre>
	 */	
	DataObject createDataObject(String containerName, String dataObjectName,
			CreateDataObjectOptions... options);

	/**
	 * delete CDMI Data object
	 * 
	 * @param containerName
	 *            containerName must end with a forward slash, /.
	 * @param  dataObjectName
	 *            dataObjectName must not end with a forward slash, /.           
	 *  <pre>
	 *  Examples: 
	 *  {@code
	 *  deleteDataObject("myContainer/","myDataObject");
	 *  }
	 *  <pre>
	 */
	void deleteDataObject(String containerName, String dataObjectName);

}
