package playSoundFile;

import main.Loggers;
import service.Setting;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


class FileDownload { //класс загрузки аудиофайлов с сервера


    boolean downloadFileFromServer(String fileNameToDownload) {
        int readBytes = 0;
        Loggers.debug("fileDownload nameFile need to be downloaded " + fileNameToDownload);
        try {
            URL url = new URL("http://" + Setting.serverIp + ":"+ Setting.serverPort +
                                    Setting.serverSoundDirectory + fileNameToDownload + ".wav");
            Loggers.debug(url.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(connection.getInputStream());
            File soundFile = new File((new File(".")).getAbsolutePath()+"/sound/"+fileNameToDownload+".wav");
            Loggers.debug(soundFile.getName());
            FileOutputStream fileOutputStream = new FileOutputStream(soundFile);

            byte[] btBuffer = new byte[1024];
            int intRead;

            while((intRead=bufferedInputStream.read(btBuffer))!= -1){
                fileOutputStream.write(btBuffer, 0, intRead);
                readBytes=readBytes+intRead;
            }
            fileOutputStream.close();
        } catch (IOException e) {
            Loggers.error(e);
            return false;
        }
        return true;
    }
}