package service;

import main.Loggers;
import playSoundFile.Player;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IniReader {
    private Player player = new Player();

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
                                Loggers.test("serverIp OK");
                                break;
                            case ("serverPort"):
                                Setting.serverPort = values[1].trim();
                                Loggers.test("serverPort OK");
                                break;
                            case ("masterNumber"):
                                Loggers.test("masterNumber OK");
                                String masterNumber = values[1].trim().replace(" ","");
                                Setting.masterNumber.addAll(Arrays.asList(masterNumber.substring(1,masterNumber.length()-1).split(",")));
                                break;
                            case ("databaseName"):
                                Setting.databaseName = values[1].trim();
                                Loggers.test("databaseName OK");
                                break;
                            case ("sqlPort"):
                                Setting.sqlPort = values[1].trim();
                                Loggers.test("sqlPort OK");
                                break;
                            case ("soundDirectory"):
                                Setting.soundDirectory = values[1].trim();
                                Loggers.test("soundDirectory OK");
                                break;
                            case ("sqlLogin"):
                                Setting.sqlLogin = values[1].trim();
                                Loggers.test("masterNumber OK");
                                break;
                            case ("sqlPassword"):
                                Setting.sqlPassword = values[1].trim();
                                Loggers.test("sqlPassword OK");
                                break;
                            case ("requiredTables"):
                                Loggers.test("requiredTables OK");
                                String tables = values[1].trim().replace(" ","");
                                Setting.requiredTables.addAll(Arrays.asList(tables.substring(1,tables.length()-1).split(",")));
                                break;
                            case ("loggerLevel"):
                                Setting.loggerLevel = Integer.parseInt(values[1].trim());
                                Loggers.test("loggerLevel OK");
                                break;
                            case ("daySaveLog"):
                                Setting.daySaveLog = Integer.parseInt(values[1].trim());
                                Loggers.test("daySaveLog OK");
                                break;
                            case ("serverSoundDirectory"):
                                Setting.serverSoundDirectory = values[1].trim();
                                Loggers.test("serverSoundDirectory OK");
                                break;
                            case ("logUnsendInfo"):
                                Setting.logUnsendInfo = Boolean.parseBoolean(values[1].trim());
                                Loggers.test("logUnsendInfo OK " + Boolean.parseBoolean(values[1].trim()));
                                break;
                            case ("sayStatusToAdmin"):
                                Setting.sayStatusToAdmin = Boolean.parseBoolean(values[1].trim());
                                Loggers.test("sayStatusToAdmin OK " + Boolean.parseBoolean(values[1].trim()));
                                break;
                            case ("writeAllLogsInSingleFile"):
                                Setting.writeAllLogsInSingleFile = Boolean.parseBoolean(values[1].trim());
                                Loggers.test("writeAllLogsInSingleFile OK");
                                break;
                            case ("readIniWhenAdminCome"):
                                Setting.readIniWhenAdminCome = Boolean.parseBoolean(values[1].trim());
                                Loggers.test("readIniWhenAdminCome OK");
                                break;
                        }
                    }
            }
            Setting.validate(); //валидация настроек
        } catch (IOException e) {
           Loggers.error(e);
           player.startPlay("noSettingFile");
           player.startPlay("exitApp");
           System.exit(1);
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
