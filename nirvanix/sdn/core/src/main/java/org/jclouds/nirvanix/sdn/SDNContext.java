package org.jclouds.nirvanix.sdn;

import org.jclouds.cloud.CloudContext;

/**
 * Represents an authenticated context to Nirvanix SDN.
 * 
 * @see <a href="http://developer.nirvanix.com/sitefiles/1000/API.html" />
 * @see SDNConnection
 * @see CloudContext
 * @author Adrian Cole
 * 
 */
public interface SDNContext extends CloudContext<SDNConnection> {

}