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
import org.jclouds.snia.cdmi.v1.domain.Container;
import org.jclouds.snia.cdmi.v1.options.CreateContainerOptions;
import org.jclouds.snia.cdmi.v1.options.GetContainerOptions;

/**
 * CDMI Container Object Resource Operations
 * 
 * @see ContainerAsyncApi
 * @author Kenneth Nagin
 * @see <a href="http://www.snia.org/cdmi">api doc</a>
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface ContainerApi {

	/**
	 * get CDMI Container
	 * 
	 * @param containerName
	 */
	Container getContainer(String containerName);

	/**
	 * get CDMI Container
	 * 
	 * @param parentURI
	 * @param containerName
	 */
	Container getContainer(String parentContainerURI, String containerName);

	/**
	 * get CDMI Container
	 * 
	 * @param containerName
	 * @param options
	 *            enables getting only certain fields, metadata, children range
	 * @see GetContainerOptions
	 */
	Container getContainer(String containerName, GetContainerOptions... options);

	/**
	 * get CDMI Container
	 * 
	 * @param parentURI
	 * @param containerName
	 * @param options
	 *            enables getting only certain fields, metadata, children range
	 * @see GetContainerOptions
	 */
	Container getContainer(String parentContainerURI, String containerName,
			GetContainerOptions... options);

	/**
	 * Create CDMI Container
	 * 
	 * @param containerName
	 */
	Container createContainer(String containerName);

	/**
	 * Create CDMI Container
	 * 
	 * @param parentContainerURI
	 * @param containerName
	 */
	Container createContainer(String parentContainerURI, String containerName);

	/**
	 * Create CDMI Container
	 * 
	 * @param containerName
	 * @param options
	 *            enables adding metadata
	 * @see CreateContainerOptions
	 */
	Container createContainer(String containerName,
			CreateContainerOptions... options);

	/**
	 * Create CDMI Container
	 * 
	 * @param parentContainerURI
	 * @param containerName
	 * @param options
	 *            enables adding metadata
	 * @see CreateContainerOptions
	 */
	Container createContainer(String parentContainerURI, String containerName,
			CreateContainerOptions... options);

	/**
	 * Delete CDMI Container
	 * 
	 * @param containerName
	 */
	void deleteContainer(String containerName);

	/**
	 * Delete CDMI Container
	 * 
	 * @param parentContainerURI
	 * @param containerName
	 */
	void deleteContainer(String parentContainerURI, String containerName);

}
