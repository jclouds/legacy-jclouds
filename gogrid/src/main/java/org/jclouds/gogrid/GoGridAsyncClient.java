package org.jclouds.gogrid;

import com.google.inject.ImplementedBy;
import org.jclouds.gogrid.internal.GoGridAsyncClientImpl;
import org.jclouds.gogrid.services.GridServerClient;

/**
 * @author Oleksiy Yarmula
 */
@ImplementedBy(GoGridAsyncClientImpl.class)
public interface GoGridAsyncClient {

    GridServerClient getServerClient();

}
