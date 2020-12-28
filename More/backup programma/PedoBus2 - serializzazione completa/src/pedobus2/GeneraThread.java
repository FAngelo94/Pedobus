/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedobus2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Angelo
 */
public class GeneraThread {
    ExecutorService pool;
    int nodiIniziali;
    int runner;
    public GeneraThread(int n){
        pool = Executors.newFixedThreadPool(1000);
        runner=0;
        nodiIniziali=n;
    }
    public synchronized void lavora(Runnable r)
    {
        //System.out.println("Lavori="+runner);
        runner++;
        pool.execute(r);
    }
    public synchronized void fineLavoro()
    {
        runner--;
    }
    public synchronized void reset()
    {
        runner=0;
    }
    
    public boolean checkFree(int migliora, int primiNodiProvati)
    {
        //System.out.println("Lavori="+runner);
        int val=runner+migliora;
        //System.out.println("Lavori="+runner+"\tVAL="+val);
        if(val==0)
            return true;
        return false;
    }
    public void termina()
    {
        pool.shutdownNow();
    }
}
