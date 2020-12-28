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
public class Test {
    
    public Test()
    {
        ExecutorService exe= Executors.newFixedThreadPool(5);
        Runnable ti=new ThreadIniziale(5, this);
        exe.execute(ti);
        try{
            synchronized(this)
            {
                this.wait();
            }
        }catch(Exception e)
        {
            
        }
        System.out.println("FINE");
    }
    
    private class ThreadIniziale implements Runnable
    {
        int i;
        Test test;
        ThreadIniziale (int primoNodo, Test test){
            this.test=test;
            i=primoNodo;
        }
        @Override
        public void run() {
            System.out.println("Runnable");
            
            synchronized(test){
                test.notify();
            }
        }

    }
    
}
