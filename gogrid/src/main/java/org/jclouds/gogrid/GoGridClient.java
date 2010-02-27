package org.jclouds.gogrid;

import com.google.inject.ImplementedBy;
import org.jclouds.gogrid.internal.GoGridClientImpl;
import org.jclouds.gogrid.services.GridServerClient;

/**
 * @author Oleksiy Yarmula
 */
@ImplementedBy(GoGridClientImpl.class)

public interface GoGridClient {

    GridServerClient getServerClient();

}
