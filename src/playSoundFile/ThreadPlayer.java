package playSoundFile;

import main.Database;
import main.Loggers;
import service.Boot;
import service.IniReader;
import service.Setting;
import java.util.*;

public class ThreadPlayer extends Thread{ //поток для последовательного воспроизведения аудиофайлов
    private Database database = new Database();
    private Server server = new Server();
    private Player player = new Player();
    private Boot boot = new Boot();
    private IniReader iniReader = new IniReader();

    @Override
    public void run() {
        String listenedNumber;
        boolean isUnload;

        while(ThreadListener.queueListenedNumber.isEmpty()) {
            Loggers.debug("I am in PlayerThread");
            synchronized (ThreadListener.queueListenedNumber) {
                try {
                    ThreadListener.queueListenedNumber.wait();
                }
                catch (InterruptedException e) {
                    Loggers.error(e);
                }
            }

            while (!ThreadListener.queueListenedNumber.isEmpty()) {
                listenedNumber = Objects.requireNonNull(ThreadListener.queueListenedNumber.poll()).toString();

                if ( Setting.masterNumber.contains(listenedNumber) ){ //2675320064
                    Loggers.warning("reboot by master card " + listenedNumber);
                    boot.reboot();
                }

                HashMap<String, String> answerFromLocalDB = database.getFileNameListFromLocalDB(listenedNumber);
                Loggers.debug(answerFromLocalDB.get("filePlay"));
                String filePlay;
                try {
                    String serverAnswerFileName = server.getConnection(listenedNumber);
                    if( serverAnswerFileName.equals(answerFromLocalDB.get("filePlay") ) ||
                            ( serverAnswerFileName.equals("late,entry,notrecognized") ||
                              serverAnswerFileName.equals("entry,notrecognized") ||
                              serverAnswerFileName.equals("exit,notrecognized")
                            )
                        || answerFromLocalDB.get("filePlay").contains("updateApp")
                    ){
                        filePlay = answerFromLocalDB.get("filePlay");
                    }else {
                        Loggers.warning("Inconsistency of information from the server and from the local database");
                        Loggers.warning("Local DB " + answerFromLocalDB.get("filePlay") +  " Remote DB " + serverAnswerFileName);
                        filePlay = serverAnswerFileName;
                    }
                    isUnload = true;
                } catch (Exception e) {
                    isUnload = false;
                    Loggers.debug( "status logUnsend: " + Setting.logUnsendInfo);

                    if (!answerFromLocalDB.isEmpty()){
                        filePlay = answerFromLocalDB.get("filePlay");
                    }else {
                        filePlay = "noAnswerServerAndDb";
                    }
                    Loggers.error(e,"threadListenerFirst");
                }

                List<String> fileListToPlay = new ArrayList<>();
                Collections.addAll(fileListToPlay, filePlay.split(","));
                Loggers.info(answerFromLocalDB.toString());
                if (Boolean.parseBoolean(answerFromLocalDB.get("isAdmin"))) {
                    if (Setting.readIniWhenAdminCome) {
                        iniReader.read();
                    }
                    if (Setting.sayStatusToAdmin) {
                        if ( Integer.parseInt(answerFromLocalDB.get("status")) == 102 ||
                             Integer.parseInt(answerFromLocalDB.get("status")) == 101 ||
                             Integer.parseInt(answerFromLocalDB.get("status")) == 200 ) {
                            if (Setting.havError) {
                                fileListToPlay.add("errorFile");
                            }
                            if (Setting.havUnsendInfo) {
                                fileListToPlay.add("unsendInfo");
                            }
                        }
                        Setting.annulledInfoForAdmin();
                    }
                }
                for ( String file: fileListToPlay){
                    try {
                        Loggers.debug(file);
                        player.startPlay(file);
                    } catch (Exception e) {
                        Loggers.error(e);
                    }

                }
                new ThreadInfoDownloader(answerFromLocalDB, isUnload).run();
            }
        }
    }
}