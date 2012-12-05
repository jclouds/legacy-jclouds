package org.jclouds.cloudstack.ec2.services;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.services.InstanceAsyncClient;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 11/28/12
 * Time: 11:04 AM
 * To change this template use File | Settings | File Templates.
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface CloudStackEC2InstanceAsyncClient extends InstanceAsyncClient {


}
