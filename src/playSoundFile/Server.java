package playSoundFile;

import main.Loggers;
import service.Setting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class Server {

    String  getConnection(String listenNumberCard) throws Exception {
        URL id_card = new URL("http://" + Setting.serverIp + ":" + Setting.serverPort +
                                "//AddRecord/AddRecord?id="+listenNumberCard);
        Loggers.serverСommunication(id_card.toString());
        URLConnection urlCon = id_card.openConnection();
        BufferedReader in = new BufferedReader(
                                 new InputStreamReader(
                                urlCon.getInputStream()));

        String inputLine;
        String files = null;
        while((inputLine = in.readLine()) != null)
            files = inputLine;
        Loggers.serverСommunication("serverAns: " + files);
        in.close();

        return files;
    }
}
