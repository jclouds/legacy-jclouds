package org.jclouds.predicates;

import org.jclouds.net.IPSocket;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * 
 * Tests to see if a socket is open.
 * 
 * @author Adrian Cole
 */
@ImplementedBy(SocketOpenUnsupported.class)
public interface SocketOpen extends Predicate<IPSocket> {

}
