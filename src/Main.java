import main.Loggers;
import playSoundFile.Player;
import playSoundFile.ThreadListener;
import playSoundFile.ThreadPlayer;
import service.IniReader;


public class Main {

    private static IniReader iniReader = new IniReader();
    private static Player player = new Player();

    public static void main(String[] args) {
        iniReader.read();
        Loggers.info("Main start");

        new ThreadListener().start();
        new ThreadPlayer().start();

        player.startPlay("systemready");
        Loggers.info("System Ready");

    }
}



