import main.Database;
import main.Loggers;
import playSoundFile.Player;
import playSoundFile.ThreadListener;
import playSoundFile.ThreadPlayer;
import service.IniReader;

public class Main {

    private static IniReader iniReader = new IniReader();
    private static Player player = new Player();

    public static void main(String[] args) {
        new Loggers();
        iniReader.read();

        Loggers.info("Main start");

        Database database = new Database();
        database.updateVersionInSetting("1.0.2", "versionApp");
        database.updateVersionInSetting("1.1", "versionBase");
        database.updateVersionInSetting("0.0", "versionServer");

        new ThreadListener().start();
        new ThreadPlayer().start();

        player.startPlay("systemready");
        Loggers.info("System Ready");

    }
}



