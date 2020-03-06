package service;

import main.Loggers;
import playSoundFile.Player;

import java.io.IOException;

public class Boot {

    private Player player = new Player();

    public void reboot() {
        String rebootCommand;
        String operatingSystem = System.getProperty("os.name");

        if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
            rebootCommand = "shutdown -r now";
        }
        else if ("Windows".equals(operatingSystem)) {
            rebootCommand = "shutdown.exe -s -t 0";
        }
        else {
            Loggers.error("Unsupported operating system");
            System.exit(0);
            throw new RuntimeException("Unsupported operating system.");
        }

        Loggers.warning("Reboot device");
        try {
            player.startPlay("reboot");
            /*while(Main.player.isActive() == 1) {
            }*/
        }
        catch (Exception e){
            Loggers.error(e);
        }
        try {
            Runtime.getRuntime().exec(rebootCommand);
        } catch (IOException e) {
            Loggers.error(e,"Problen with reboot");
        }
        System.exit(0);
    }

    public void shutdown() {
        String shutdownCommand;
        String operatingSystem = System.getProperty("os.name");
        Loggers.debug(operatingSystem);

        if (operatingSystem.contains("Linux") || operatingSystem.contains("Mac OS X")) {
            shutdownCommand = "shutdown now";
        }
        else if (operatingSystem.contains("Windows")) {
            shutdownCommand = "shutdown.exe";
        }
        else {
            Loggers.error("Unsupported operating system");
            throw new RuntimeException("Unsupported operating system.");
        }

        Loggers.warning("Shutdown device");
        try {
            player.startPlay("shutdown");
            /*while(Main.player.isActive() == 1) {
            }*/
        }
        catch (Exception e){
            Loggers.error(e);
        }
        try {
            Runtime.getRuntime().exec(shutdownCommand);
        } catch (IOException e) {
            Loggers.error(e, "Проблема с выключением");
        }
        System.exit(0);
    }
}
