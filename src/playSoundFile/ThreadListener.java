package playSoundFile;

import main.Loggers;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class ThreadListener extends Thread{  //чтение данных сканера карт

    static final Queue queueListenedNumber = new LinkedList<>();

    @Override
    public void run() {
        Loggers.info("Thread for listen reader run");
        Scanner scanner = new Scanner(System.in);
        String listenNumberCard = "";

        //для отображения индикации процесса работы приложения
       /* String[] commandGrin = {"gpio"," -g write 17 0","gpio"," -g write 27 1" };
         String[] commandRed = {"gpio"," -g write 17 0","gpio"," -g write 27 1"};
         ProcessBuilder pb = new ProcessBuilder(commandRed);
        Process process = pb.start();*/

        while(true) {
            try{
                listenNumberCard = scanner.nextLine();
                Loggers.info("listenNumber: " + listenNumberCard);
            }
            catch(Exception e) {
                Loggers.error(e,"threadListenerScannerExp");
            }

            if(listenNumberCard.length() != 0){
                synchronized (queueListenedNumber) {
                    queueListenedNumber.add(listenNumberCard);
                    queueListenedNumber.notify();
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Loggers.error(e, "threadListenerSecond");
            }
        }
    }
}