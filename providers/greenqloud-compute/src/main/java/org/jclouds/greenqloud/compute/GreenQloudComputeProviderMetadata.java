package org.jclouds.greenqloud.compute;

import java.net.URI;
import java.util.Set;

import org.jclouds.aws.ec2.AWSEC2ProviderMetadata;
import org.jclouds.providers.ProviderMetadata;

import com.google.common.collect.ImmutableSet;

public class GreenQloudComputeProviderMetadata extends AWSEC2ProviderMetadata {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getId() {
		return "greenqloud-compute";
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