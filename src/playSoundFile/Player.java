package playSoundFile;

import main.Loggers;
import service.Setting;

import java.io.File;
import java.io.IOException;

public class Player {  //класс для воспроизведения аудиофайлов

    private FileDownload fileDownload = new FileDownload();
    
    public void startPlay(String fileToPlay) {
        String filePath;
        if (!Setting.soundDirectory.equals("")){
            filePath = Setting.soundDirectory;
        }else{
            filePath = (new File(".")).getAbsolutePath()+"/sound/";
        }
        Loggers.debug("Path to play " + filePath);
        String[] commands = {"aplay", filePath + fileToPlay + ".wav"};

        ProcessBuilder pb = new ProcessBuilder(commands);
        Loggers.debug(" command  " + commands[1]);
        Process process = null;
        try {
            process = pb.start();
        } catch (IOException e) {
            Loggers.error(e);
        }


        try {
            if (process.waitFor() > 1 ){
                Loggers.debug("This process has enrich value");
            }
        } catch (InterruptedException e) {
            Loggers.error(e,"error while waifFor one PB");
        }
        try {
            if( process.waitFor() != 0){
               if (fileDownload.downloadFileFromServer(fileToPlay)){
                   startPlay(fileToPlay);
               }else{
                   return;
               }

            }
        } catch (InterruptedException e) {
            Loggers.error(e,"error while waifFor not equal zero PB");
        }

        try {
            if (process.waitFor() == 0){
                process.destroy();
                Loggers.debug("I die");
            }
        } catch (InterruptedException e) {
            Loggers.error(e,"error while waifFor equal zero PB" );
        }
    }
}

