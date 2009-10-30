package org.jclouds.rackspace.cloudfiles.functions;

import org.jclouds.rackspace.cloudfiles.domain.CFObject;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class ObjectName implements Function<Object, String> {

   public String apply(Object from) {
      return ((CFObject) from).getInfo().getName();
   }

}