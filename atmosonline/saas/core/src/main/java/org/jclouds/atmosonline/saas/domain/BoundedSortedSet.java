package org.jclouds.atmosonline.saas.domain;

import java.util.SortedSet;

import org.jclouds.atmosonline.saas.domain.internal.BoundedTreeSet;

import com.google.inject.ImplementedBy;

/**
 * 
 * @author Adrian Cole
 * 
 */
@ImplementedBy(BoundedTreeSet.class)
public interface BoundedSortedSet<T> extends SortedSet<T> {

   String getToken();

}