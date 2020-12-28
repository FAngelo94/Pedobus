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
public class PoolThreadRami {
    ExecutorService pool;
    int runner;
    public PoolThreadRami(){
        pool = Executors.newFixedThreadPool(1000);
        runner=0;
    }
    public synchronized void lavora(Runnable r)
    {
        runner++;
        pool.execute(r);
    }
    public synchronized void fineLavoro()
    {
        runner--;
    }
    public boolean checkFree()
    {
        //System.out.println("Lavori="+runner);
        if(runner==0)
            return true;
        return false;
    }
    public void termina()
    {
        pool.shutdownNow();
    }
}
