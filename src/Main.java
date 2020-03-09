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

        Database database = new Database();
        database.updateVersionApp("1.0.1", "versionApp");
        database.updateVersionApp("1.1", "versionBase");
        database.updateVersionApp("0.0", "versionServer");
        Loggers.info("Main start");

        new ThreadListener().start();
        new ThreadPlayer().start();

        player.startPlay("systemready");
        Loggers.info("System Ready");

    }
}



