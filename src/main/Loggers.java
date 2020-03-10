package main;

import service.Setting;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


public class Loggers {
    public static Logger LOGGER = Logger.getLogger(Loggers.class.getName());
    private static FileHandler fileHandler;

    public Loggers(){
        File dirForLog = new File((new File(".")).getAbsolutePath() + "//logs//");
        if(!dirForLog.exists()){
            if (!dirForLog.mkdir()){
                LOGGER.warning("dir logs can't create");
            }
        }

        File logDirApp = new File("./logs/appLog/");
        if( !(logDirApp.exists()) ) {
           if ( !logDirApp.mkdir() ){
               LOGGER.warning("dir appLog can't create");
           }
        }


        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String curDate = sdf.format(new Date());
        try {
            File logDir = new File("./logs/logger/");
            if( !(logDir.exists()) )
                if ( !logDir.mkdir() ){
                    LOGGER.warning("dir logger can't create");
                }
            fileHandler = new FileHandler("logs/logger/log_" + curDate);
        } catch (SecurityException | IOException e) {
            Loggers.error(e);
        }
        LOGGER.addHandler(fileHandler);
    }

    public static void service(String message){ //логирование тестирования информации о системе
        prepareLine(message, "info_log");
    }

    public static void warning(String message){
        if (Setting.loggerLevel >= 1) {
            prepareLine(message, "info_log");
        }
    }

    public static void info(String message) {
        if (Setting.loggerLevel >= 2) {
            prepareLine(message, "info_log");
        }
    }

    static void sql(String message){
        if (Setting.loggerLevel >= 3) {
            prepareLine(message, "sql_log");
        }
    }

    public static void debug(String message) {
        if (Setting.loggerLevel >= 4) {
            prepareLine(message, "info_log");
        }
    }

    public static void error(Exception exception){
        Setting.havError = true;
        prepareLine(exception,"");
    }

    public static void error(Exception exception,String message){
        Setting.havError = true;
        prepareLine(exception,message);
    }

    public static void error(String message) {
        Setting.havError = true;
        prepareLine(message, "error_log");
    }

    static void unsendInfo(String info) throws IOException {
        Setting.havUnsendInfo = true;
        writeLog(info+"\n", "unsend_info");
    }

    static  private void prepareLine(Exception exception,String message) {

        String messageToLog = ": [" +
                Thread.currentThread().getStackTrace()[2].getMethodName().toUpperCase() + "] "+
                Thread.currentThread().getStackTrace()[3].getClassName() + " >> "+
                Thread.currentThread().getStackTrace()[3].getMethodName()  + " : line:"+
                exception.getStackTrace()[0].getLineNumber() + "  { "+
                message +" "+ exception.toString() + "}  [" +
                exception.getStackTrace()[0].getClassName() + " >> "+
                exception.getStackTrace()[0].getMethodName() + "]\n";
        try {
            writeLog(messageToLog, "error_log");
        } catch (IOException e) {
          //  LOGGER.warning("File can't created: " + filePath.getName());
            LOGGER.warning("writeLog Exception: " + e.getMessage());
        }
    }

    static private void prepareLine(String message, String fileName) {
        String messageToLog = ": [" + Thread.currentThread().getStackTrace()[2].getMethodName().toUpperCase() +"] " + Thread.currentThread().getStackTrace()[3].getClassName() + " >> "+
                            Thread.currentThread().getStackTrace()[3].getMethodName() + " : " +"{"+ message + "}\n";
        try {
            writeLog(messageToLog, fileName);
        } catch (IOException e) {
            LOGGER.warning("writeLog Exception: " + e.getMessage());
        }
    }

    private static void writeLog(String message, String fileName) throws IOException {
        if( Setting.writeAllLogsInSingleFile &&
                !Thread.currentThread().getStackTrace()[2].getMethodName().toUpperCase().equals("UNSENDINFO") ){
            fileName = "common_file_log";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
        String curDay = simpleDateFormat.format(new Date());
        File dir = new File((new File(".")).getAbsolutePath() + "//logs//appLog//"+curDay);

        if(!dir.exists()){
            boolean created = dir.mkdir();
            //обнуление информации об ошибках и неотправленной информации за день
            Setting.havUnsendInfo = false;
            Setting.havError = false;
            if(created) {
                LOGGER.info("Dir created: " + dir.getName());
            }else{
                LOGGER.warning("Dir can't created: " + dir.getName() );
            }
        }

        File filePath = new File((new File(".")).getAbsolutePath() + "//logs//appLog//"+curDay+"//" +  fileName + "_" + curDay+ ".log");

        SimpleDateFormat sdfLog = new SimpleDateFormat("[dd-MM-yyyy  HH:mm:ss.SSSXXX] ");
        String curDateLog = sdfLog.format(new Date());

        String textToFile = curDateLog + message;


        if (!filePath.exists()) {
            if (!filePath.createNewFile()){
                LOGGER.warning("Can't create file " + filePath.getName());
            }
            LOGGER.info("File created: " + filePath.getName());
        }
        Files.write(Paths.get(filePath.toString()), textToFile.getBytes(), StandardOpenOption.APPEND);
    }
}
