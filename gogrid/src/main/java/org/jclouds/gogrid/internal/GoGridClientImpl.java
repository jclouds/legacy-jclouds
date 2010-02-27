package org.jclouds.gogrid.internal;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jclouds.gogrid.GoGridClient;
import org.jclouds.gogrid.services.GridServerClient;

/**
 * @author Oleksiy Yarmula
 */
@Singleton
public class GoGridClientImpl implements GoGridClient {

    private GridServerClient gridServerClient;

    @Inject
    public GoGridClientImpl(GridServerClient gridServerClient) {
        this.gridServerClient = gridServerClient;
    }

    @Override
    public GridServerClient getServerClient() {
        return gridServerClient;
    }
}
