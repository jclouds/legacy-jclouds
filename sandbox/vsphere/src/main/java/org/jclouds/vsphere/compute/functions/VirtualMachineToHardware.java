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

package org.jclouds.vsphere.compute.functions;

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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.jamesmurty.utils.XMLBuilder;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author Adrian Cole
 */
@Singleton
public class VirtualMachineToHardware implements Function<VirtualMachine, Hardware> {

	@Override
	public Hardware apply(VirtualMachine from) {
		HardwareBuilder builder = new HardwareBuilder();

			builder.id(from.getMOR().get_value() + "");
			builder.providerId(from.getMOR().get_value() + "");
			builder.name(from.getName());
			List<Processor> processors = Lists.newArrayList();
			for (int i = 0; i < from.getConfig().getHardware().getNumCPU(); i++) {
				processors.add(new Processor(i + 1, 1));
			}
			builder.processors(processors);

			builder.ram((int) from.getConfig().getHardware().getMemoryMB());
			List<Volume> volumes = Lists.newArrayList();
			
			/*
			XMLBuilder xmlBuilder = XMLBuilder.parse(new InputSource(new StringReader(from.getXMLDesc(0))));
			Document doc = xmlBuilder.getDocument();
			XPathExpression expr = XPathFactory.newInstance().newXPath().compile("//devices/disk[@device='disk']/source/@file");
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			String diskFileName = nodes.item(0).getNodeValue();
			for (int i = 0; i < nodes.getLength(); i++) {
				StorageVol storageVol = from.getConnect().storageVolLookupByPath(diskFileName);
				String id = storageVol.getKey();
				float size = new Long(storageVol.getInfo().capacity).floatValue();
				volumes.add(new VolumeImpl(id, Volume.Type.LOCAL, size, null, true, false));
			}
			*/
			
			// TODO
			builder.volumes((List<Volume>) volumes);
			Float size = new Float(21345);
			String id = "dglffdbdflmb";
			volumes.add(new VolumeImpl(id, Volume.Type.LOCAL, size, null, true, false));
		return builder.build();
	}
	
	protected <T> T propagate(Exception e) {
		Throwables.propagate(e);
		assert false;
		return null;
	}
}