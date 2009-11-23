package org.jclouds.rest.domain;

import org.jclouds.rest.internal.NamedResourceImpl;

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