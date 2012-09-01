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

import javax.inject.Inject;

import org.jclouds.azure.management.domain.Disk;
import org.jclouds.azure.management.domain.OSType;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/jj157176" >api</a>
 * @author Adrian Cole
 */
public class DiskHandler extends
		ParseSax.HandlerForGeneratedRequestWithResult<Disk> {

	protected final AttachmentHandler attachmentHandler;

	@Inject
	protected DiskHandler(AttachmentHandler attachmentHandler) {
		this.attachmentHandler = attachmentHandler;
	}

	protected StringBuilder currentText = new StringBuilder();
	private Disk.Builder<?> builder = Disk.builder();

	protected boolean inAttachment;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Disk getResult() {
		try {
			return builder.build();
		} finally {
			builder = Disk.builder();
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (equalsOrSuffix(qName, "AttachedTo")) {
			inAttachment = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(String uri, String name, String qName)
			throws SAXException {
		if (equalsOrSuffix(qName, "AttachedTo")) {
			builder.attachedTo(attachmentHandler.getResult());
			inAttachment = false;
		} else if (inAttachment) {
			attachmentHandler.endElement(uri, name, qName);
		} else if (equalsOrSuffix(qName, "OS")) {
			builder.os(OSType.fromValue(currentOrNull(currentText)));
		} else if (equalsOrSuffix(qName, "Name")) {
			builder.name(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "LogicalDiskSizeInGB")) {
			String gb = currentOrNull(currentText);
			if (gb != null)
				builder.logicalSizeInGB(Integer.parseInt(gb));
		} else if (equalsOrSuffix(qName, "Description")) {
			builder.description(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "Location")) {
			builder.location(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "AffinityGroup")) {
			builder.affinityGroup(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "MediaLink")) {
			String link = currentOrNull(currentText);
			if (link != null)
				builder.mediaLink(URI.create(link));
		} else if (equalsOrSuffix(qName, "SourceImageName")) {
			builder.sourceImage(currentOrNull(currentText));
		} else if (equalsOrSuffix(qName, "Label")) {
			builder.label(currentOrNull(currentText));
		}
		currentText = new StringBuilder();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (inAttachment) {
			attachmentHandler.characters(ch, start, length);
		} else {
			currentText.append(ch, start, length);
		}
	}

}
