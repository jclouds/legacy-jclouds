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

import javax.ws.rs.PathParam;

import org.jclouds.concurrent.Timeout;
import org.jclouds.io.Payload;
//import org.jclouds.io.Payload;
import org.jclouds.snia.cdmi.v1.domain.DataObject;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectNonCDMIOptions;
import org.jclouds.snia.cdmi.v1.options.CreateDataObjectOptions;
import org.jclouds.snia.cdmi.v1.options.GetDataObjectOptions;

/**
 * Data Object Resource Operations
 * 
 * @see DataAsyncApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface DataApi {
	DataObject createDataObject(String containerName, String dataObjectName,
			CreateDataObjectOptions... options);
	 
	void createDataObjectNonCDMI(String containerName, String dataObjectName, 
			CreateDataObjectNonCDMIOptions... options );
	
	void createDataObjectNonCDMI(String containerName, String dataObjectName, 
			Payload payload);


	DataObject getDataObject(String containerName, String dataObjectName);
	
	DataObject getDataObject(String containerName, String dataObjectName, String queryParams);
	
	//DataObject getDataObject(String containerName, String dataObjectName,
	//		GetDataObjectOptions... options);

	void deleteDataObject(String containerName, String dataObjectName);


}
