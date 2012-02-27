package org.jclouds.gq.compute;

import java.net.URI;
import java.util.Set;

import org.jclouds.providers.BaseProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

import com.google.common.collect.ImmutableSet;

public class GQComputeProviderMetadata extends BaseProviderMetadata {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return "gq-compute";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType() {
		return ProviderMetadata.COMPUTE_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return "Greenqloud Compute Cloud";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIdentityName() {
		return "Access Key ID";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCredentialName() {
		return "Secret Access Key";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URI getHomepage() {
		return URI.create("http://www.greenqloud.com");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URI getConsole() {
		return URI.create("https://manage.greenqloud.com");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URI getApiDocumentation() {
		return URI
				.create("http://docs.amazonwebservices.com/AWSEC2/latest/APIReference");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getLinkedServices() {
		return ImmutableSet.of("gq-storage", "gq-compute");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getIso3166Codes() {
		return ImmutableSet.of("IS-1");
	}
}