/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.libvirt.compute.functions;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.libvirt.StorageVol;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import com.jamesmurty.utils.XMLBuilder;

/**
 * @author Adrian Cole
 */
@Singleton
public class DomainToHardware implements Function<Domain, Hardware> {

	@Override
	public Hardware apply(Domain from) {
		HardwareBuilder builder = new HardwareBuilder();

		try {
			builder.id(from.getUUIDString());
			builder.providerId(from.getID() + "");
			builder.name(from.getName());
			List<Processor> processors = Lists.newArrayList();
			for (int i = 0; i < from.getInfo().nrVirtCpu; i++) {
				processors.add(new Processor(i + 1, 1));
			}
			builder.processors(processors);

			builder.ram((int) from.getInfo().maxMem);
			List<Volume> volumes = Lists.newArrayList();
			XMLBuilder xmlBuilder = XMLBuilder.parse(new InputSource(new StringReader(from.getXMLDesc(0))));
			Document doc = xmlBuilder.getDocument();
			XPathExpression expr = XPathFactory.newInstance().newXPath().compile("//devices/disk[@device='disk']/source/@file");
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			String diskFileName = nodes.item(0).getNodeValue();
			for (int i = 0; i < nodes.getLength(); i++) {
				System.out.println("disk " + diskFileName);
				StorageVol storageVol = from.getConnect().storageVolLookupByPath(diskFileName);
				String id = storageVol.getKey();
				float size = new Long(storageVol.getInfo().capacity).floatValue();
				volumes.add(new VolumeImpl(id, Volume.Type.LOCAL, size, null, true, false));
			}
			builder.volumes((List<Volume>) volumes);
		} catch (LibvirtException e) {
			propagate(e);
		} catch (XPathExpressionException e) {
			propagate(e);
		} catch (ParserConfigurationException e) {
			propagate(e);
		} catch (SAXException e) {
			propagate(e);
		} catch (IOException e) {
			propagate(e);
		}
		return builder.build();
	}
	
	protected <T> T propagate(Exception e) {
		Throwables.propagate(e);
		assert false;
		return null;
	}
}