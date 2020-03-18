package service;

import main.Database;
import main.Loggers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Service  extends Thread {

    @Override
    public void run() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new writeLog(), 10*1000 , 60*60*1000); //смотреть на задержку
    }

    class  writeLog extends TimerTask {
        IniReader iniReader = new IniReader();
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        @Override
        public void run() {
            int hour = Integer.parseInt(hourFormat.format(new Date()));
            Loggers.debug("Time scheduler work on " + new Date());
            if (hour == 3) { //работа по рассписании в 03 ( перечитывание ini файла, и удаление старых логов
                Loggers.service("Task scheduler start");
                iniReader.read();
                deleteOldLogs();
                Loggers.service("Task scheduler finish");
            }
        }

        private void deleteOldLogs(){
            String directory = (new File(".")).getAbsolutePath() + "//logs/appLog//";
            File path = new File(directory);
            String[] listDir = path.list();
            Loggers.LOGGER.info( "Count directories: " + listDir.length);
            for (String dir : listDir) {
                Path file = Paths.get(directory).resolve(dir);
                BasicFileAttributes attrs = null;
                try {
                    attrs = Files.readAttributes(file, BasicFileAttributes.class);
                } catch (IOException e) {
                    Loggers.LOGGER.warning(e.getMessage());
                }
                long diff = (new Date().getTime() - attrs.creationTime().toMillis()) / (24 * 60 * 60 * 1000);
                if (diff >= Setting.daySaveLog) {
                    Loggers.LOGGER.info("Deleting directory with old logs " + dir);

                    if ( deleteDirAndFile(new File(directory + dir)) ) {
                        Loggers.LOGGER.info("Directory successful deleted");
                    } else {
                        Loggers.LOGGER.warning("Directory can't deleted");
                    }
                }
            }
        }
    }

    private boolean deleteDirAndFile(File dir){
        Loggers.LOGGER.info("dir to delete " + dir.getAbsolutePath());
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirAndFile(new File(dir, children[i]));
                if (!success) {
                    Loggers.LOGGER.warning("Directory or file can't deleted");
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
