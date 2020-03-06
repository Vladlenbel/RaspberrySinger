package service;

import java.util.Timer;
import java.util.TimerTask;

//Класс для получения информации о сервере
public class GetInfoServer extends Thread {
    @Override
    public void run(){
       /* Calendar nowDate = Calendar.getInstance();
        int year  = nowDate.get(Calendar.YEAR) - 1900;
        int month  = nowDate.get(Calendar.MONTH) ;
        int day  = nowDate.get(Calendar.DATE);*/
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new GetInfo(), 1*6*1000 , 6*1000); //
    }
}

class GetInfo extends TimerTask{
    @Override
    public void run() {

    }
}
