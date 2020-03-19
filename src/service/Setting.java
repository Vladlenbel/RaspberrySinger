package service;

import main.Database;
import main.Loggers;
import playSoundFile.Player;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Setting {

    private static Player player = new Player();

    public static String serverIp = "";
    public static String serverPort = "";

    public static String databaseName = "";
    public static String sqlPort = "";
    public static String sqlLogin = "";
    public static String sqlPassword = "";
    public static ArrayList<String> requiredTables = new ArrayList<>();
    public static String serverSoundDirectory = "";

    public static String soundDirectory = "";
    public static ArrayList<String> masterNumber = new ArrayList<>();
    public static int loggerLevel = 0;
    static int daySaveLog = 30;
    public static boolean logUnsendInfo = false;
    public static String serialNumber = "0";
    public static boolean sayStatusToAdmin = false;
    public static boolean writeAllLogsInSingleFile = false;
    public static boolean readIniWhenAdminCome = false;

    public static boolean havError = false;
    public static boolean havUnsendInfo = false;

    static void validate(){ //проверка наличия обязательных параметров
        Loggers.service("Validating");
        if (!getSerialNumber()){
            player.startPlay("NoSerialNumber");
            player.startPlay("exitApp");
            System.exit(2);
        }
        boolean isValid = true;
        if (databaseName.equals("") ){
            Loggers.error("databaseName error");
            isValid = false;
        }
        if(serverIp.equals("")){
            Loggers.error("serverIp error");
            isValid = false;
        }
        if (serverPort.equals("")){
            Loggers.error("serverPort error");
            isValid = false;
        }
        if (soundDirectory.equals("")){
            Loggers.warning("No name for soud directory");
        }
        if (masterNumber.isEmpty()){
            Loggers.warning("no master number card");
        }
        if (sqlPort.equals("")){
            Loggers.error("sqlPort error");
            isValid = false;
        }
        if (sqlLogin.equals("")){
            Loggers.error("sqlLogin error");
            isValid = false;
        }
        if (sqlPassword.equals("")){
            Loggers.error("sqlPassword error");
            isValid = false;
        }
        if (requiredTables.isEmpty()){
            Loggers.error("requiredTables error");
            isValid = false;
        }
        if (serverSoundDirectory.equals("")){
            Loggers.error("serverSoundDirectory error");
            isValid = false;
        }
        if (loggerLevel == 0){
            Loggers.warning("loger level is not specified");
        }
        if (daySaveLog == 0){
            Loggers.warning("no time for day save log");
        }
        if (!isValid){
            player.startPlay("noParameter");
            player.startPlay("exitApp");
            Loggers.error("All required parameters are not specified");
            Loggers.warning("Out from app");
            System.exit(0);

        }
        Database database = new Database(); //проверка работоспособности и наличия необходимого в БД
        database.validateDB();
        Loggers.service("App version - " + database.getValueSetting("versionApp"));
        Loggers.service("Base version - " + database.getValueSetting("versionBase"));
    }

    private static  boolean getSerialNumber(){ //получение серийного номера оборудования
        String pattern = ":.+";
        String commandGetCPUInfo = "cat /proc/cpuinfo | grep Serial";
        List<String> settingList= new ArrayList<>();
        try {
            String line;
            Process p = Runtime.getRuntime().exec(commandGetCPUInfo);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                if (line.contains("Serial"))
                settingList.add(line);
            }
            input.close();
            for ( String settingString: settingList ) {

                Pattern ptrn = Pattern.compile(pattern);
                Matcher matcher = ptrn.matcher(settingString);

                if(matcher.find()){
                    String serialNumber = matcher.group(0).substring(1).trim();
                    Setting.serialNumber = serialNumber;
                    Loggers.service("Serial number " + serialNumber);
                    return true;
                }else {
                    Loggers.error("Failed to get device serial number");
                    return false;
                }
            }
        }catch (Exception e) {
           Loggers.error(e);
        }
        return true;
    }

    static void toFactorySetting(){ //сброс всех настроек в заводские перед чтением ini файла
        serverIp = "";
        serverPort = "";

        databaseName = "";
        sqlPort = "";
        sqlLogin = "";
        sqlPassword = "";
        requiredTables.clear();
        serverSoundDirectory = "";

        soundDirectory = "";
        masterNumber.clear();
        loggerLevel = 0;
        daySaveLog = 30;
        logUnsendInfo = false;
        serialNumber = "0";
        sayStatusToAdmin = false;
        writeAllLogsInSingleFile = false;
    }

    public static void annulledInfoForAdmin(){ //анулирование статусов наличия ошибок и невыгруженных документов
        havError = false;
        havUnsendInfo = false;
    }


}
