package org.aliece.server;

import java.io.File;
import java.io.IOException;

public class Start {
    public static void main(String[] args) throws IOException {
        args = new String[2];
        args[0] = "12345";
        args[1] = "F://";
        int port = Integer.parseInt(args[0]);
        final Server s = new Server(port, new ServerEventHandler(args[1], 4));
        System.out.println("Starting server on port " + port);
        System.out.println("Web root folder: "
                + new File(args[1]).getAbsolutePath());
        while (true)
            s.listen();
    }
}
