// Copyright (c) 2015 Uber
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.uber.jenkins.phabricator;

import com.uber.jenkins.phabricator.utils.TestUtils;

import net.sf.json.JSONObject;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.impl.bootstrap.HttpServer;
import org.apache.hc.core5.http.impl.bootstrap.ServerBootstrap;
import org.apache.hc.core5.io.CloseMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FakeConduit {

    private List<String> requestBodies;
    
    private ServerBootstrap serverBootstrap = ServerBootstrap.bootstrap();
    
    private HttpServer server;

    public FakeConduit(Map<String, JSONObject> responses) throws Exception {
        this.requestBodies = new ArrayList<>();
        this.setUp();
        for (Map.Entry<String, JSONObject> entry : responses.entrySet()) {
            this.register(entry.getKey(), entry.getValue());
        }
        server.start();
    }

    private void setUp() {
        server = serverBootstrap.create();
    }
    
    public void stop() throws Exception {
        server.close(CloseMode.IMMEDIATE);
    }

    public HttpServer getServer() {
        return server;
    }

    public List<String> getRequestBodies() throws UnsupportedEncodingException {
        return requestBodies;
    }

    public String uri() {
        return TestUtils.getTestServerAddress(server);
    }

    public void register(String method, JSONObject response) {
        this.serverBootstrap.register(
                "/api/" + method,
                TestUtils.makeHttpHandler(HttpStatus.SC_OK, response.toString(2), requestBodies)
        );
    }
}
