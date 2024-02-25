import httpServer.HttpTaskServer;
import httpServer.KVServer;
import managers.*;
import tasks.*;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        KVServer kvServer = new KVServer();
        kvServer.start();

        HttpTaskServer server = new HttpTaskServer();
    }


}
