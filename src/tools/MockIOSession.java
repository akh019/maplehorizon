package tools;

import java.net.SocketAddress;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSessionConfig;

/**
 *
 * @author Administrator
 */


public class MockIOSession extends DummySession{    
   /*
    /**
     *
     * @return
     */
     
  /*  @Override
    @Override
    public IoProcessor getProcessor() {
        return null;
    } */

    public IoService getService() {
        return null;
    }

    public IoHandler getHandler() {
        return null;
    }

    public IoSessionConfig getConfig() {
        return null;
    }

    public IoFilterChain getFilterChain() {
        return null;
    }

    public TransportMetadata getTransportMetadata() {
        return null;
    }

    public SocketAddress getRemoteAddress() {
        return null;
    }

    public SocketAddress getLocalAddress() {
        return null;
    }

    public WriteFuture write(Object o) {
        // do we need to send anything?
        return null;
    }

    public WriteFuture write(Object o, SocketAddress sa) {
        // do we need to send anything?
        return null;
    } 
}

