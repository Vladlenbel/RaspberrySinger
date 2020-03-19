import main.Database;
import main.Loggers;
import playSoundFile.Player;
import playSoundFile.ThreadListener;
import playSoundFile.ThreadPlayer;
import service.IniReader;
import service.Service;

public class Main {

    private static IniReader iniReader = new IniReader();
    private static Player player = new Player();

    public static void main(String[] args) {
        new Loggers();
        iniReader.read();

        Loggers.info("Main start");

        Database database = new Database();
        database.updateVersionInSetting("1.x.x", "versionApp");
        database.updateVersionInSetting("1.x", "versionBase");

        new ThreadListener().start();
        new ThreadPlayer().start();
        new Service().start();

        player.startPlay("systemready");
        Loggers.info("System Ready");

    }
}



