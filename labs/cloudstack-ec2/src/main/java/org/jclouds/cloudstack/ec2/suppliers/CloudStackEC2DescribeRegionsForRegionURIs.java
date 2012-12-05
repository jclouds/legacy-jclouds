package org.jclouds.cloudstack.ec2.suppliers;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.suppliers.DescribeRegionsForRegionURIs;
import org.jclouds.location.Provider;

import javax.inject.Inject;
import java.net.URI;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 11/28/12
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackEC2DescribeRegionsForRegionURIs extends DescribeRegionsForRegionURIs {
    private Supplier<URI> defaultURISupplier;

    @Inject
    public CloudStackEC2DescribeRegionsForRegionURIs(@Provider Supplier<URI> defaultURISupplier, EC2Client client) {
        super(client);
        this.defaultURISupplier = defaultURISupplier;
    }

    @Override
    public Map<String, Supplier<URI>> get() {
        Map<String, Supplier<URI>> regionToUris = null;
        regionToUris = ImmutableMap.of("AmazonEC2", defaultURISupplier);

        return regionToUris;
    }
}
