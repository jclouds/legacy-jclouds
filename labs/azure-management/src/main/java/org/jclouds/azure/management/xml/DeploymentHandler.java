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
package org.jclouds.azure.management.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.net.URI;
import java.util.List;

import com.google.common.collect.Lists;

import org.jclouds.azure.management.domain.Deployment;
import org.jclouds.azure.management.domain.DeploymentSlot;
import org.jclouds.azure.management.domain.DeploymentStatus;
import org.jclouds.azure.management.domain.InstanceStatus;
import org.jclouds.azure.management.domain.RoleSize;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460804" >api</a>
 * @author Gérald Pereira
 */
public class DeploymentHandler extends
		ParseSax.HandlerForGeneratedRequestWithResult<Deployment> {

	private List<String> elements = Lists.newArrayList();
	protected StringBuilder currentText = new StringBuilder();
	private Deployment.Builder builder = Deployment.builder();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Deployment getResult() {
		try {
			return builder.build();
		} finally {
			builder = Deployment.builder();
		}
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		elements.add(qName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(String uri, String name, String qName)
			throws SAXException {
		if (equalsOrSuffix(qName, "Name") && "Deployment".equals(elements.get(elements.size()-2))) {
			builder.deploymentName(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "DeploymentSlot")) {
			final String dpltSlot = currentOrNull(currentText);
			if (dpltSlot != null)
				builder.deploymentSlot(DeploymentSlot.fromValue(dpltSlot));
		} else if (equalsOrSuffix(qName, "Status")) {
			String deploymentStatus = currentOrNull(currentText);
			if (deploymentStatus != null)
				builder.deploymentStatus(DeploymentStatus
						.fromValue(deploymentStatus));
		} else if (equalsOrSuffix(qName, "Label")) {
			String label = currentOrNull(currentText);
			if (label != null)
				builder.deploymentLabel(new String(CryptoStreams.base64(label)));
		} else if (equalsOrSuffix(qName, "Url")) {
			final String url = currentOrNull(currentText);
			if (url != null)
				builder.deploymentURL(URI.create(url));
		} else if (equalsOrSuffix(qName, "RoleName")) {
			builder.roleName(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "InstanceName")) {
			builder.instanceName(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "InstanceStatus")) {
			String instanceStatus = currentOrNull(currentText);
			if (instanceStatus != null)
				builder.instanceStatus(InstanceStatus.fromValue(instanceStatus));
		}  else if (equalsOrSuffix(qName, "InstanceStateDetails")) {
			builder.instanceStateDetails(currentOrNull(currentText));
		}  else if (equalsOrSuffix(qName, "InstanceErrorCode")) {
			builder.instanceErrorCode(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "InstanceSize")) {
			String instanceSize = currentOrNull(currentText);
			if (instanceSize != null)
				builder.instanceSize(RoleSize.fromValue(instanceSize));
		} else if (equalsOrSuffix(qName, "IpAddress")) {
			builder.privateIpAddress(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "Vip")) {
			builder.publicIpAddress(currentOrNull(currentText));
		}

		currentText = new StringBuilder();
		elements.remove(elements.size()-1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		currentText.append(ch, start, length);
	}

}
