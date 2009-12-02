package org.jclouds.vcloud.hostingdotcom.domain;

import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.hostingdotcom.domain.internal.HostingDotComVAppImpl;

import com.google.inject.ImplementedBy;

/**
 * @author Adrian Cole
 */
@ImplementedBy(HostingDotComVAppImpl.class)
public interface HostingDotComVApp extends VApp {

   String getUsername();

   String getPassword();
}