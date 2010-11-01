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

package org.jclouds.libvirt.compute.domain;

import java.net.URI;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.StandaloneComputeServiceContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.suppliers.DefaultLocationSupplier;
import org.jclouds.domain.Location;
import org.jclouds.libvirt.Datacenter;
import org.jclouds.libvirt.Image;
import org.jclouds.libvirt.compute.functions.DatacenterToLocation;
import org.jclouds.libvirt.compute.functions.DomainToHardware;
import org.jclouds.libvirt.compute.functions.DomainToNodeMetadata;
import org.jclouds.libvirt.compute.functions.LibvirtImageToImage;
import org.jclouds.libvirt.compute.strategy.LibvirtComputeServiceAdapter;
import org.jclouds.rest.annotations.Provider;
import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

/**
 * 
 * @author Adrian Cole
 */
public class LibvirtComputeServiceContextModule extends
StandaloneComputeServiceContextModule<Domain, Domain, Image, Datacenter> {
	@Override
	protected void configure() {
		super.configure();
		bind(new TypeLiteral<ComputeServiceAdapter<Domain, Domain, Image, Datacenter>>() {
		}).to(LibvirtComputeServiceAdapter.class);
		bind(new TypeLiteral<Supplier<Location>>() {
		}).to(DefaultLocationSupplier.class);
		bind(new TypeLiteral<Function<Domain, NodeMetadata>>() {
		}).to(DomainToNodeMetadata.class);
		bind(new TypeLiteral<Function<Image, org.jclouds.compute.domain.Image>>() {
		}).to(LibvirtImageToImage.class);
		bind(new TypeLiteral<Function<Domain, Hardware>>() {
		}).to(DomainToHardware.class);
		bind(new TypeLiteral<Function<Datacenter, Location>>() {
		}).to(DatacenterToLocation.class);
	}

	@Provides
	@Singleton
	protected Connect createConnection(@Provider URI endpoint, @Named(Constants.PROPERTY_IDENTITY) String identity,
			@Named(Constants.PROPERTY_CREDENTIAL) String credential) throws LibvirtException {
		// ConnectAuth connectAuth = null;
		return new Connect(endpoint.toASCIIString());
	}

	@Override
	protected TemplateBuilder provideTemplate(Injector injector, TemplateBuilder template) {
		String domainDir =  injector.getInstance(Key.get(String.class, Names.named("jclouds.libvirt.domain-dir")));
		String hardwareId = searchForHardwareIdInDomainDir(domainDir);
		String image = searchForImageIdInDomainDir(domainDir);
		return template.hardwareId(hardwareId).imageId(image) ;
	}

	private String searchForImageIdInDomainDir(String domainDir) {
		// TODO
		return "1";
	}

	private String searchForHardwareIdInDomainDir(String domainDir) {
		// TODO
		return "c7ff2039-a9f1-a659-7f91-e0f82f59d52e";
	}
}