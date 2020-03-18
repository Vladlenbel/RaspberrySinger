package main;

import playSoundFile.Player;
import service.Setting;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

public class Database {

    private  Logger LOGGER = Logger.getLogger(Loggers.class.getName());
    private final String user = Setting.sqlLogin/*"root" "root""passagesys"*/;
    private final String password = Setting.sqlPassword/*"serverps""valdistroer" "AstZhq4"*/;
    private final String url = "jdbc:mysql://localhost:"+Setting.sqlPort+"/"+Setting.databaseName+"?useUnicode=true&characterEncoding=UTF-8";

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

    private Player player = new Player();


    public Database() {
        Loggers.sql("username " + user + " password "+ password + " url " + url);
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            Loggers.error(e);
        }
    }

    private void closeDB() {
        try {
            connection.close();
            statement.close();
            resultSet.close();
        } catch (Exception e) {
            player.startPlay("noConnectionWithLocalDB");
            Loggers.error(e);
        }
    }

    private void sendQuery(String query) {
        Loggers.sql(query);
        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            Loggers.error(e);
        } finally {
            try {
                connection.close();
                statement.close();
            } catch (SQLException e) {
                Loggers.error(e ,query);
            }
        }
    }

    public void validateDB(){
        if(!isDatabaseExists() || !isAllTableExist()){
            try {
                player.startPlay("noDB");
                player.startPlay("exitApp");
            }
            catch (Exception e){
                Loggers.error(e);
            }
           // Loggers.warning("Shutdown system");
            Loggers.warning("Out from app");
            System.exit(0);
        }
    }

    //служебная функция для проверки наличия БД на серверре
    private boolean isDatabaseExists(){
        int kol = 0;
        String sqlQuery = String.format("SHOW DATABASES like '%s'", Setting.databaseName);
        Loggers.sql(sqlQuery);
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            while (resultSet.next()) {
                kol++;
            }
        }catch (SQLException e) {
            Loggers.error(e);
        } finally {
            closeDB();
        }
        if (kol > 0 ) {
            Loggers.service("DB created");
            return true;
        }else {
            Loggers.error("DB not created");
            return false;
        }
    }

    private boolean isAllTableExist(){
        String sqlQuery = String.format("SELECT TABLE_NAME as tables " +
                "FROM information_schema.tables where TABLE_SCHEMA =\"%s\"", Setting.databaseName);
        Loggers.sql(sqlQuery);
        ArrayList<String> tablesInDB = new ArrayList<>();
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);

            while (resultSet.next()) {
                tablesInDB.add(resultSet.getString("tables"));
            }
        }catch (SQLException e) {
            Loggers.error(e);
        } finally {
            closeDB();
        }
        if(tablesInDB.containsAll(Setting.requiredTables)){
            Loggers.service("All table contains");
            return true;
        }else {
            for (String tabInSetting: Setting.requiredTables ) {
                boolean tableExist = false;
                for (String tableInDb: tablesInDB) {
                    if (tableInDb.equals(tabInSetting)) {
                        tableExist = true;
                    }
                }
                if (!tableExist){
                    Loggers.error("In DB is absent: " + tabInSetting);
                }
            }
        return false;
        }
    }

    public void loadIntoLocalDB(HashMap<String,String> infoForLoad,  boolean isUnload){ //запись информации в локальную базу даннных

        String uuid =  UUID.randomUUID().toString();
        String queryInsertCheckInformation = "INSERT INTO check_information (workerCardNum,idStatus,serialNo," +
                "personalNumber,timeCheck,isUnload, uuid ) VALUES(" +
                "\"" + infoForLoad.get("workerCardNum") + "\""  + "," +
                "\"" + Integer.parseInt(infoForLoad.get("statusCom")) + "\"" +  "," +
                "\"" + Setting.serialNumber  + "\""  + "," +
                "\"" + infoForLoad.get("personalNumber") + "\"" +  "," +
                "\"" + infoForLoad.get("timeCheck")  + "\""  + "," +
                isUnload  + "," +
                "\"" + uuid + "\")" ;
        sendQuery(queryInsertCheckInformation);
        Loggers.sql("Record is unloaded");
        if(!isUnload) {
            logUnsentInfo(infoForLoad, uuid);
        }
    }

    public HashMap<String, String> getFileNameListFromLocalDB(String workerCardNum){ //запись информации в локальную базу даннных
        HashMap<String, String> personalData = getPersonalInformation(workerCardNum);
        String soundAnswer = "";
        String personalNumber;
        int statusCom = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String timeCheck = dateFormat.format(new Date());

        SimpleDateFormat dateFormatForBirthday = new SimpleDateFormat("dd-MM-YYYY");
        String dayForBirthday = dateFormatForBirthday.format(new Date());

        if( Boolean.parseBoolean(personalData.get("isRegistered")) ) {
            personalNumber = personalData.get("personalNumber");

            int directionOrIsFirsCome = getDirectionOrFirstCome(personalData.get("personalNumber"));
            statusCom = statusWorker(
                    timeCheck,
                    Integer.parseInt(personalData.get("workerHourId")),
                    directionOrIsFirsCome
            ); //проверка статуса входа

            String message = getMessages(personalData.get("personalNumber"), statusCom); //проверка наличия сообщений

            if (directionOrIsFirsCome == 0) { //проверка на день рождения и первый вход для поздравления
                String birthdayString = personalData.get("birthday");
                SimpleDateFormat birDateFormat = new SimpleDateFormat("YYYY");
                String curBirDate = birDateFormat.format(new Date());
                birthdayString =  birthdayString.substring(0, 6) + curBirDate ;
                Loggers.sql("Check birthday: Today date: " + dayForBirthday + " Birthday from db: " + birthdayString );
                if (dayForBirthday.equals(birthdayString)) {
                    soundAnswer = "birthday,";
                }
            }
            if (!message.isEmpty()) {
                soundAnswer += message;
            }
            if (statusCom == 101) {
                soundAnswer += "late,";
            }
            if (statusCom < 105) {
                soundAnswer += "entry,";
            } else {
                soundAnswer += "exit,";
            }
            soundAnswer += getSoundName(personalData.get("personalNumber"));
            Loggers.info("Out string for play: " + soundAnswer);
        } else {
            personalNumber = workerCardNum;
            soundAnswer = "unknownNumberCard";
        }
        HashMap<String, String> answerFromLocalBase = new HashMap<>();
        answerFromLocalBase.put("filePlay",soundAnswer);
        answerFromLocalBase.put("isAdmin",personalData.get("isAdmin"));

        answerFromLocalBase.put("workerCardNum",workerCardNum);
        answerFromLocalBase.put("statusCom",Integer.toString(statusCom));
        answerFromLocalBase.put("timeCheck",timeCheck);
        answerFromLocalBase.put("personalNumber",personalNumber);

        answerFromLocalBase.put("status", Integer.toString(statusCom));

        return answerFromLocalBase;
    }

    private HashMap<String,String > getPersonalInformation(String workerCardNum){
        HashMap<String, String> personalData = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        boolean isRegistered = false;
        String getPersonalInformation = String.format(" SELECT * FROM worker_personal_data WHERE " +
                "workerCardNum =\"%s\"AND status < 400", workerCardNum);
        Loggers.sql(getPersonalInformation);
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(getPersonalInformation);

            while (resultSet.next()) {
                personalData.put("id",resultSet.getString("id"));
                personalData.put("personalNumber", resultSet.getString("personalNumber"));
                personalData.put("workerCardNum", resultSet.getString("workerCardNum"));
                personalData.put("birthday", dateFormat.format(resultSet.getDate("birthday")));
                personalData.put("workerHourId", resultSet.getString("workerHourId"));
                personalData.put("departmentId", resultSet.getString("departmentId"));
                personalData.put("isAdmin", resultSet.getString("isAdmin"));
                personalData.put("isRegistered", "true");
                isRegistered = true;
            }
        }catch (SQLException e) {
            Loggers.error(e);
        } finally {
            closeDB();
        }
        if(!isRegistered){
            personalData.put("isRegistered","false");
            personalData.put("isAdmin","false");
        }
        Loggers.sql("Is registered " + isRegistered);
        Loggers.sql("Is Admin user " + personalData.get("isAdmin"));
        return personalData;
    }

    private String getSoundName(String personalNumber) {
        String fileName = "";
        String query = String.format("SELECT filename FROM sound_info  WHERE personalNumber = '%s';", personalNumber);
        Loggers.sql(query);
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                fileName = resultSet.getString("filename");
            }
        }catch (SQLException e) {
            Loggers.error(e);
        } finally {
            closeDB();
        }
        if (fileName.equals("")) {
            fileName = "unknown";
        }
        Loggers.sql(fileName);
        return fileName;
    }

    private int statusWorker (String curDate, int workerHourId, int directionOrIsFirsCome/* boolean isFirstCome*/) {
        int status;
        if ( directionOrIsFirsCome == 0 ) { Loggers.sql("i her");
            if (isLate(curDate, workerHourId)) {
                status = 101;
            } else {
                status = 102;
            }
        } else if( directionOrIsFirsCome == 100 ||
                   directionOrIsFirsCome == 101 ||
                   directionOrIsFirsCome == 102){
            status =  200;
        } else {
            status =  100;
        }
        Loggers.sql("Status worker: " + status);
        return status;
    }

    private boolean isLate (String date, int workerHourId) {

        String timeFromWorkingHourType = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        String curDate = dateFormat.format(new Date());

        String query = String.format("SELECT startWork FROM working_hour WHERE id = \"%d\";", workerHourId);
        Loggers.sql(query);
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                timeFromWorkingHourType = resultSet.getString("startWork");
            }
            timeFromWorkingHourType = curDate + " " + timeFromWorkingHourType;

            Loggers.sql("now date before parse = " + date + " notLateTime before parse= " + timeFromWorkingHourType);

            Date nowDate = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").parse(date);
            Date notLateTime = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").parse(timeFromWorkingHourType);

            Loggers.sql("now date = " + nowDate + " notLateTime = " + notLateTime);

            return nowDate.compareTo(notLateTime) > 0;

        }catch (SQLException | ParseException e) {
            Loggers.error(e);
        } finally {
            closeDB();
        }

        return false;
    }

    private int getDirectionOrFirstCome (String personalNumber){ //проверка первого входа и направления

        int directionOrFirstCome = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        String curDate = dateFormat.format(new Date());
        curDate += " 23:59:59";
        String curDates = dateFormat.format(new Date());

        String query = String.format("SELECT * FROM check_information WHERE personalNumber ='%s' AND timeCheck >= '%s' AND timeCheck <= '%s' order by id DESC LIMIT 1;", personalNumber, curDates, curDate);
        Loggers.sql(query);
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                directionOrFirstCome = Integer.parseInt(resultSet.getString("idStatus"));
            }
        }catch (SQLException e) {
            Loggers.error(e);
        } finally {
            closeDB();
        }
        Loggers.sql( "isFirstComeOrDirection: " + directionOrFirstCome);

        return  directionOrFirstCome;
    }

    private String getMessages(String personalNumber, int statusId) {
        StringBuilder answer = new StringBuilder();
        int isCompleted;
        String giveMess = String.format(" SELECT * FROM message " +
                "WHERE personalNumber =\"%s\"AND isCompleted != \"0\"", personalNumber);
        Loggers.sql(giveMess);
        try{
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(giveMess);

            while (resultSet.next()) {
                isCompleted = Integer.parseInt(resultSet.getString("isCompleted"));
                Loggers.sql(Integer.toString(isCompleted));
                if ( resultSet.getString("notificationType").equals("вход") && statusId > 105 ) {
                    break;
                }else {
                    answer.append(resultSet.getString("fileName")).append(",");
                    if( statusId > 105 ) {
                        isCompleted = isCompleted - 10;
                    }
                    else {
                        isCompleted = isCompleted - 20;
                    }
                    if (isCompleted < 0 ) {
                        isCompleted = 0;
                    }
                    String updateMes = "UPDATE  message SET "
                            + "isCompleted =" + "\"" +  isCompleted + "\"" +
                            "WHERE id =" + "\"" + resultSet.getString("id") + "\"" + ";" ;
                    sendQuery(updateMes);
                }
            }
        }catch (SQLException e) {
            Loggers.error(e);
        } finally {
            closeDB();
        }
        Loggers.sql("List message" + answer.toString());
        return answer.toString();

    }

    private void logUnsentInfo(HashMap<String, String> infoForLoad, String uuid){

        String queryToRemoteServer = "INSERT INTO worker VALUES(" +
                "\"" +  infoForLoad.get("workerCardNum") + "\""  + "," +
                "\"" + infoForLoad.get("timeCheck")  + "\"" +  "," +
                "\"" + infoForLoad.get("personalNumber")  + "\""  + "," +
                "\"" + infoForLoad.get("statusCom") + "\"" + ");" ;
        try {
            Loggers.unsendInfo(queryToRemoteServer);
            changeRecordStatus(uuid);
        } catch (IOException e) {
            LOGGER.warning("WriteLog unsend info Exception: " + e.getMessage());
        }
    }

   /* public String getUnloadRecord(){ //функция получения незагруженных на сервер записей
        return "ok";
    }*/

    private void changeRecordStatus(String uuid){ //изменения статуса записи
        String updateStatusCheckInformation = String.format("UPDATE check_information SET isUnload = true WHERE uuid = \"%s\"", uuid);
        sendQuery(updateStatusCheckInformation);
    }

    public void updateVersionInSetting(String version, String code){
        String updateVersionApp = "INSERT INTO settings (code,value) VALUES " +
                "('"+ code + "','" + version + "') " +
                "ON DUPLICATE KEY UPDATE value = '" + version+"'";
        sendQuery(updateVersionApp);
    }
}