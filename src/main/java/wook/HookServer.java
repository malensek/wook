/*
Copyright (c) 2016, Matthew Malensek
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package wook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Arrays;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import com.sun.net.httpserver.Headers;

public class HookServer {

    private static int DEFAULT_PORT = 7000;
    private HttpServer server;

    public HookServer(int port) throws Exception {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/hook", new HookHandler());
        server.setExecutor(null);
    }

    public void start() {
        server.start();
    }

    static class HookHandler implements HttpHandler {

        public void handle(HttpExchange t) throws IOException {
            InputStreamReader isr = new InputStreamReader(
                    t.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);

            int read;
            StringBuilder buf = new StringBuilder(512);
            while ((read = br.read()) != -1) {
                buf.append((char) read);
            }

            br.close();
            isr.close();

            String data = buf.toString();

            Headers h = t.getRequestHeaders();
            String contentType = h.get("Content-type").get(0);
            if (contentType.equals("application/x-www-form-urlencoded")) {
                data = java.net.URLDecoder.decode(data, "UTF-8");
            }

            System.out.println(data);

            t.sendResponseHeaders(202, 0);
            t.close();
        }
    }

    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser();
        OptionSpec<Integer> portSpec = parser.acceptsAll(
                Arrays.asList("p", "port"), "Port to listen on")
            .withRequiredArg()
            .ofType(Integer.class)
            .defaultsTo(DEFAULT_PORT);

        OptionSet opts = parser.parse(args);
        int port = portSpec.value(opts);

        HookServer hs = new HookServer(port);
        hs.start();
        System.err.println("Listening on port " + port + "...");
    }
}
