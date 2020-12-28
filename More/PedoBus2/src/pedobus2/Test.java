/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedobus2;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Angelo
 */
public class Test {
    
    public static void Test(String[] args) {
	if(args.length < 1){
		System.err.println("Wrong command. You must specify the data file path");
		return;
	}
	
	String filePathWithExtention = args[0];
	File f = new File(filePathWithExtention);
	String fileName = f.getName().substring(0, f.getName().indexOf('.'));
	
	/*
	 * Reading file
	 */
	System.out.println("Reading file: " + filePathWithExtention);
	try {
		//Utility.readFile(filePathWithExtention);
	} catch (Exception e) {
		System.err.println("IO error: " + e.toString());
		return;
	}
	System.out.println("File correctly parsed");
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
