package org.jclouds.vcloud.domain;

import org.jclouds.rest.domain.NamedLink;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;

import com.google.inject.ImplementedBy;

/**
 * Location of a Rest resource
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(NamedResourceImpl.class)
public interface NamedResource extends NamedLink, Comparable<NamedResource> {
   String getId();
}