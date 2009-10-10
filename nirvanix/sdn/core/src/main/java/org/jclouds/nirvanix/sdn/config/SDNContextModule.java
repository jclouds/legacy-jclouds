package org.jclouds.nirvanix.sdn.config;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.cloud.internal.CloudContextImpl;
import org.jclouds.http.RequiresHttp;
import org.jclouds.lifecycle.Closer;
import org.jclouds.nirvanix.sdn.SDN;
import org.jclouds.nirvanix.sdn.SDNConnection;
import org.jclouds.nirvanix.sdn.SDNContext;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

@RequiresHttp
public class SDNContextModule extends AbstractModule {
   @Override
   protected void configure() {
      bind(SDNContext.class).to(SDNContextImpl.class).in(Scopes.SINGLETON);
   }

   public static class SDNContextImpl extends CloudContextImpl<SDNConnection> implements SDNContext {
      @Inject
      public SDNContextImpl(Closer closer, SDNConnection defaultApi, @SDN URI endPoint,
               @Named(SDNConstants.PROPERTY_SDN_USERNAME) String account) {
         super(closer, defaultApi, endPoint, account);
      }
   }

}