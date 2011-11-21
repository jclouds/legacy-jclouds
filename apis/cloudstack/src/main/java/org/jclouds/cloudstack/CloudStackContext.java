package org.jclouds.cloudstack;

import org.jclouds.cloudstack.internal.CloudStackContextImpl;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.RestContext;

import com.google.inject.ImplementedBy;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(CloudStackContextImpl.class)
public interface CloudStackContext extends ComputeServiceContext {

    RestContext<CloudStackDomainClient, CloudStackDomainAsyncClient> getDomainContext();
    
    RestContext<CloudStackGlobalClient, CloudStackGlobalAsyncClient> getGlobalContext();

}