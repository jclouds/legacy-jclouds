package org.jclouds.atmosonline.saas.domain.internal;

import java.util.TreeSet;

import org.jclouds.atmosonline.saas.domain.BoundedSortedSet;

import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class BoundedTreeSet<T> extends TreeSet<T> implements BoundedSortedSet<T> {

   /** The serialVersionUID */
   private static final long serialVersionUID = -7133632087734650835L;
   protected final String token;

   public BoundedTreeSet(Iterable<T> contents, String token) {
      Iterables.addAll(this, contents);
      this.token = token;
   }

   public String getToken() {
      return token;
   }

}