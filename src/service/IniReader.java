package service;

import main.Loggers;
import playSoundFile.Player;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IniReader {
    private Player player = new Player();
    private List<String> fullListSettingFromIni = new ArrayList<>();

    public void read(){
        File FILE_NAME = new File((new File(".")).getAbsolutePath()+"//setting.ini" );
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(FILE_NAME), StandardCharsets.UTF_8))){
            String line;
            Setting.toFactorySetting(); //сброс всех настроек в завод
            while ((line = reader.readLine()) != null) {
                String STRING_PATTERN = "([A-Za-z_]* *= * [A-Za-z0-9\\x20-\\x7E]*)";
                Pattern pattern = Pattern.compile(STRING_PATTERN);
                Matcher regexp = pattern.matcher(line.trim());
                    if (regexp.matches() ) { //если строк файла не является закоментированной
                        String[] values = line.split("=");
                        String nameVariableSetting = toCamelCase(values[0].trim()); //перевод переменных ini файла в camelCase
                        switch (nameVariableSetting){
                            case ("serverIp"):
                                Setting.serverIp = values[1].trim();
                                Loggers.service("serverIp OK");
                                fullListSettingFromIni.remove("serverIp");
                                break;
                            case ("serverPort"):
                                Setting.serverPort = values[1].trim();
                                Loggers.service("serverPort OK");
                                fullListSettingFromIni.remove("serverPort");
                                break;
                            case ("masterNumber"):
                                Loggers.service("masterNumber OK");
                                String masterNumber = values[1].trim().replace(" ","");
                                Setting.masterNumber.addAll(Arrays.asList(masterNumber.substring(1,masterNumber.length()-1).split(",")));
                                fullListSettingFromIni.remove("masterNumber");
                                break;
                            case ("databaseName"):
                                Setting.databaseName = values[1].trim();
                                Loggers.service("databaseName OK");
                                fullListSettingFromIni.remove("databaseName");
                                break;
                            case ("sqlPort"):
                                Setting.sqlPort = values[1].trim();
                                Loggers.service("sqlPort OK");
                                fullListSettingFromIni.remove("sqlPort");
                                break;
                            case ("soundDirectory"):
                                Setting.soundDirectory = values[1].trim();
                                Loggers.service("soundDirectory OK");
                                //fullListSettingFromIni.remove("soundDirectory");
                                break;
                            case ("sqlLogin"):
                                Setting.sqlLogin = values[1].trim();
                                Loggers.service("masterNumber OK");
                                fullListSettingFromIni.remove("sqlLogin");
                                break;
                            case ("sqlPassword"):
                                Setting.sqlPassword = values[1].trim();
                                Loggers.service("sqlPassword OK");
                                fullListSettingFromIni.remove("sqlPassword");
                                break;
                            case ("requiredTables"):
                                Loggers.service("requiredTables OK");
                                String tables = values[1].trim().replace(" ","");
                                Setting.requiredTables.addAll(Arrays.asList(tables.substring(1,tables.length()-1).split(",")));
                                fullListSettingFromIni.remove("requiredTables");
                                break;
                            case ("loggerLevel"):
                                Setting.loggerLevel = Integer.parseInt(values[1].trim());
                                Loggers.service("loggerLevel OK");
                                fullListSettingFromIni.remove("loggerLevel");
                                break;
                            case ("daySaveLog"):
                                Setting.daySaveLog = Integer.parseInt(values[1].trim());
                                Loggers.service("daySaveLog OK");
                                fullListSettingFromIni.remove("daySaveLog");
                                break;
                            case ("serverSoundDirectory"):
                                Setting.serverSoundDirectory = values[1].trim();
                                Loggers.service("serverSoundDirectory OK");
                                fullListSettingFromIni.remove("serverSoundDirectory");
                                break;
                            case ("logUnsendInfo"):
                                Setting.logUnsendInfo = Boolean.parseBoolean(values[1].trim());
                                Loggers.service("logUnsendInfo OK " + Boolean.parseBoolean(values[1].trim()));
                                fullListSettingFromIni.remove("logUnsendInfo");
                                break;
                            case ("sayStatusToAdmin"):
                                Setting.sayStatusToAdmin = Boolean.parseBoolean(values[1].trim());
                                Loggers.service("sayStatusToAdmin OK " + Boolean.parseBoolean(values[1].trim()));
                                fullListSettingFromIni.remove("sayStatusToAdmin");
                                break;
                            case ("writeAllLogsInSingleFile"):
                                Setting.writeAllLogsInSingleFile = Boolean.parseBoolean(values[1].trim());
                                Loggers.service("writeAllLogsInSingleFile OK");
                                fullListSettingFromIni.remove("writeAllLogsInSingleFile");
                                break;
                            case ("readIniWhenAdminCome"):
                                Setting.readIniWhenAdminCome = Boolean.parseBoolean(values[1].trim());
                                Loggers.service("readIniWhenAdminCome OK");
                                fullListSettingFromIni.remove("readIniWhenAdminCome");
                                break;
                        }
                    }
            }
            if (!fullListSettingFromIni.isEmpty()){
                addMissingFieldToIni(fullListSettingFromIni);
                read();
            }
            Setting.validate(); //валидация настроек
        } catch (IOException e) {
           Loggers.error(e);
           player.startPlay("noSettingFile");
           player.startPlay("exitApp");
           System.exit(1);
        }
    }

    //заполнение списка файла настроек необходимыми полями
    private void createSettingListObject(){
        fullListSettingFromIni.clear();
        fullListSettingFromIni.add("serverIp");
        fullListSettingFromIni.add("serverPort");
        fullListSettingFromIni.add("masterNumber");
        fullListSettingFromIni.add("databaseName");
        fullListSettingFromIni.add("sqlPort");
        fullListSettingFromIni.add("sqlLogin");
        fullListSettingFromIni.add("sqlPassword");
        //fullListSettingFromIni.add("soundDirectory");
        fullListSettingFromIni.add("requiredTables");
        fullListSettingFromIni.add("loggerLevel");
        fullListSettingFromIni.add("daySaveLog");
        fullListSettingFromIni.add("serverSoundDirectory");
        fullListSettingFromIni.add("logUnsendInfo");
        fullListSettingFromIni.add("sayStatusToAdmin");
        fullListSettingFromIni.add("writeAllLogsInSingleFile");
        fullListSettingFromIni.add("readIniWhenAdminCome");
    }

    //добавление недостающих полей настроек
    private void addMissingFieldToIni(List<String> addToIniList){
        for (String addToIni: addToIniList ) {
            switch (addToIni){
                case ("loggerLevel"):
                    addVariableToIni("LOGGER_LEVEL = 4", "уровень логирования (не обязательный параметр) по умолчанию 0\n" +
                            ";0-error,service - логируется всегда\n" +
                            ";1-логирование warning\n" +
                            ";2-логирование 1+info\n" +
                            ";3-логирование 2+sql_log\n" +
                            ";4-логирование 3+debug\n" +
                            ";5-логирование 4+super debug\n");
                    break;
                case ("serverIp"):
                    addVariableToIni("SERVER_IP = 10.10.10.63", "ip сервера на который пойдет отправка данных");
                    break;
                case ("serverPort"):
                    addVariableToIni("SERVER_PORT = 8080", "порт сервера");
                break;
                //case ("soundDirectory"):
                //    addVariableToIni(";SOUND_DIRECTORY = /home/pi/Desktop/sound/", "место расположения директории с аудиофайлами");
                //    break;
                case ("serverSoundDirectory"):
                    addVariableToIni("SERVER_SOUND_DIRECTORY = /AddRecord/sound/", "расположение аудиофайлов на сервере");
                    break;
                case ("databaseName"):
                    addVariableToIni("DATABASE_NAME = raspberry_schema", "имя базы данных для устройстве");
                    break;
                case ("sqlPort"):
                    addVariableToIni("SQL_PORT = 3306", "порт базы данных");
                    break;
                case ("sqlLogin"):
                    addVariableToIni("SQL_LOGIN = pi", "логин и пароль локальной базы данных");
                    break;
                case ("sqlPassword"):
                    addVariableToIni("SQL_PASSWORD = valdistroer", "");
                    break;
                case ("requiredTables"):
                    addVariableToIni("REQUIRED_TABLES = [department,working_hour,message,sound_info,worker_personal_data,check_information]", "список необходимых таблиц");
                    break;
                case ("masterNumber"):
                    addVariableToIni("MASTER_NUMBER = [0]", "номер мастер карты для перезагрузки системы (не обязательеый параметр)");
                    break;
                case ("daySaveLog"):
                    addVariableToIni("DAY_SAVE_LOG = 30", "период времени хранения файлов лога (не обязательеый параметр) по умолчанию 30 дней");
                    break;
                case ("logUnsendInfo"):
                    addVariableToIni("LOG_UNSEND_INFO = false", "логирование записей в файл не переданных на сервер (не обязательеый параметр) по умолчанию false");
                    break;
                case ("writeAllLogsInSingleFile"):
                    addVariableToIni("WRITE_ALL_LOGS_IN_SINGLE_FILE = false", "писать все логирование в общий файл (не обязательеый параметр) по умолчанию false");
                    break;
                case ("sayStatusToAdmin"):
                    addVariableToIni("SAY_STATUS_TO_ADMIN = false", "говорить администратору о наличии ошибок и непереданной информации (не обязательеый параметр) по умолчанию false");
                    break;
                case ("readIniWhenAdminCome"):
                    addVariableToIni("READ_INI_WHEN_ADMIN_COME = false", "перечитывание конфигурационного файла при входе администратора (не обязательный параметр) по умолчанию false\n");
                    break;
            }
        }
    }

    //добавление настройки в ini файл
    private void addVariableToIni(String variable, String description){
        File FILE_NAME = new File((new File(".")).getAbsolutePath()+"//setting.ini" );
        try {
            description = "\n\n;" + description+ "\n";
            Files.write(Paths.get(FILE_NAME.toString()), description.getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get(FILE_NAME.toString()), variable.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {

            Loggers.error(e);
        }

    }

    private  String toCamelCase(String string) {
        char currentChar, previousChar = '\u0000'; // Текущий и предыдущий символ прохода
        StringBuilder result = new StringBuilder(); // Результат функции в виде строкового билдера

        boolean firstLetterArrived = false; // Флаг, отвечающий за написание первого символа результата в lowercase
        boolean nextLetterInUpperCase = true; // Флаг, приказывающий следующий добавляемый символ писать в UPPERCASE

        // Проходимся по всем символам полученной строки
        for (int i = 0; i < string.length(); i++) {
            currentChar = string.charAt(i);

			/* Если текущий символ не цифробуква -
				приказываем следующий символ писать Большим (начать новое слово) и идем на следующую итерацию.
			   Если предыдущий символ это маленькая буква или цифра, а текущий это большая буква -
			    приказываем текущий символ писать Большим (начать новое слово).
			*/
            if (!Character.isLetterOrDigit(currentChar) || (
                    ((Character.isLetter(previousChar) && Character.isLowerCase(previousChar)) || Character.isDigit(previousChar)) &&
                            Character.isLetter(currentChar) && Character.isUpperCase(currentChar))
            ) {
                nextLetterInUpperCase = true;
                if (!Character.isLetterOrDigit(currentChar)) {
                    previousChar = currentChar;
                    continue;
                }
            }
            // Если приказано писать Большую букву, и первая буква уже написана.
            if (nextLetterInUpperCase && firstLetterArrived) {
                result.append(Character.toUpperCase(currentChar));
            }
            else {
                result.append(Character.toLowerCase(currentChar));
            }

            // Устанавливаем флаги.
            firstLetterArrived = true;
            nextLetterInUpperCase = false;
            previousChar = currentChar;
        }
        return result.toString();
    }
}
