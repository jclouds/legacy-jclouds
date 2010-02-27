package org.jclouds.gogrid.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.services.GridServerClient;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class GoGridAsyncClientImpl implements GoGridAsyncClient {

    private GridServerClient gridServerClient;

    @Inject
    public GoGridAsyncClientImpl(GridServerClient gridServerClient) {
        this.gridServerClient = gridServerClient;
    }

    @Override
    public GridServerClient getServerClient() {
        return gridServerClient;
    }
}
