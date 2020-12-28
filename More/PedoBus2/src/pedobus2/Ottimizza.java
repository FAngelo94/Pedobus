/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedobus2;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.application.Platform.exit;

/**
 *
 * @author Angelo
 */
public class Ottimizza {
    private static final int NODI_iNIZIALI=3;
    private int MIGLIORAMENTO_MAX=100;
    
    //PARAMETRI DA VARIARE MANUALMENTE
    private int PROFONDITA_MAX=5;
    private static final int SOLUZIONI_SALVATE=100;
    
    public int foglieMax;
    public double rischioMax;
    public int[]vettX;
    public int[]vettY;
    public double[][]risk;
    public double alfa;
    public int n;
    public double[]distanze;
    
    GeneraThread t;
    int iGlobale;
    
    //VARIABILI PER ITERAZIONI SUCCESSIVE
    int migliora;
    double archiMigliori[][];
    int foglieMigliori[];
    
    //VARIABILI PER SALVARE PIU' SOLUZIONI
    int soluzioni;
    int soluzioniTot;
    double soluzioniMigliori[][][];
    
    //VARIABILE CHE IDENTIFICA L'ALGORITMO CHE VOGLIO APPLICARE
    int tipologia;
    
    //POOL INIZIALE
    ExecutorService executor;
    public  Ottimizza()
    {
        
    }
    
    Ottimizza(int foglieMax, double rischioMax, int[] vettX, int[] vettY, double[][] risk, double alfa, int n,double[]distanze, String fileName) {
        creaThreadKiller(fileName);
        
        //Selettore in base ai casi
        switch(n)
        {
            case 10:
                PROFONDITA_MAX=2;
                MIGLIORAMENTO_MAX=20;
                break;
            case 20:
                PROFONDITA_MAX=2;
                MIGLIORAMENTO_MAX=30;
                break;
            case 30:
                PROFONDITA_MAX=2;
                MIGLIORAMENTO_MAX=50;
                break;
            case 50:
                PROFONDITA_MAX=5;
                MIGLIORAMENTO_MAX=60;
                break;
            case 80:
                PROFONDITA_MAX=5;
                MIGLIORAMENTO_MAX=80;
                break;
            case 100:
                PROFONDITA_MAX=5;
                MIGLIORAMENTO_MAX=110;
                break;
            case 150:
                PROFONDITA_MAX=10;
                MIGLIORAMENTO_MAX=130;
                break;
            case 200:
                PROFONDITA_MAX=15;
                MIGLIORAMENTO_MAX=180;
                break;
            case 250:
                PROFONDITA_MAX=15;
                MIGLIORAMENTO_MAX=200;
                break;
            case 300:
                PROFONDITA_MAX=20;
                MIGLIORAMENTO_MAX=200;
                break;
        }
        
        //salvare più soluzioni
        soluzioni=0;
        soluzioniMigliori=new double[SOLUZIONI_SALVATE][n][4];
        foglieMigliori=new int[SOLUZIONI_SALVATE];
        
        
        this.foglieMax=foglieMax;
        this.rischioMax=rischioMax;
        this.vettX=vettX;
        this.vettY=vettY;
        this.risk=risk;
        this.alfa=alfa;
        this.n=n;
        this.distanze=distanze;
        soluzioniTot=0;
        
        int nt=1;
        
        executor = Executors.newFixedThreadPool(1);
        t=new GeneraThread(NODI_iNIZIALI);
            
        for(int i=0;i<NODI_iNIZIALI;i++)
        {
            
            migliora=0;
            soluzioniTot=0;
            soluzioni=0;
            tipologia=0;
            soluzioniMigliori=new double[SOLUZIONI_SALVATE][n][4];
            foglieMigliori=new int[SOLUZIONI_SALVATE];
            System.out.println("INIZIO ANALISI ALGORITMO 1 SU NODO "+i);
            
            executor = Executors.newFixedThreadPool(1);
            t=new GeneraThread(NODI_iNIZIALI);
            eseguiSoluzione(i);
            executor.shutdownNow();
            t.termina();
        
            System.out.println("INIZIO ANALISI ALGORITMO 2 SU NODO "+i);
            tipologia=1;
            soluzioni=0;
            migliora=0;
            soluzioniTot=0;
            soluzioniMigliori=new double[SOLUZIONI_SALVATE][n][4];
            foglieMigliori=new int[SOLUZIONI_SALVATE];
            
            executor = Executors.newFixedThreadPool(1);
            t=new GeneraThread(NODI_iNIZIALI);
            eseguiSoluzione(i);
            executor.shutdownNow();
            t.termina();
        }   
        stampaMigliore(fileName);
        System.out.println("FINE ELABORAZIONE");
        
    }
     
    private void eseguiSoluzione(int i)
    {
        int vicini[]=trovaViciniAScuola();
        //for(int i=vicini.length-1;i>=0;i--)
        //{
            //Runnable thread=new ThreadIniziale(vicini[i]+1);
            Runnable thread=new ThreadIniziale(vicini[i]+1);
            executor.execute(thread);

        //DA TOGLIERE SE NON TROVO DOVE SVEGLIARLO
           /*synchronized(this)
            {
                try {
                this.wait(30000);
                } catch (InterruptedException ex) {
                Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
                }
            }*/
        //}
        try {
            synchronized(this)
            {
                this.wait(100);
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        checkFine();
    }
    
    public synchronized void creaThreadSingolaSoluzione(Runnable r) {
        t.lavora(r);
    }
    public synchronized void fineLavoro()
    {
        t.fineLavoro();
    }

    private int[] trovaViciniAScuola() {
        int[]vicini=new int[NODI_iNIZIALI];
        for(int i=0;i<vicini.length;i++)
        {
            int indexMinore=0;
            double distMin=Double.MAX_VALUE;
            for(int j=0;j<n;j++)
            {
                if(distMin>distanze[j] && nonPresente(vicini,j))
                {
                    indexMinore=j;
                    distMin=distanze[j];
                }
            }
            vicini[i]=indexMinore;
        }
        return vicini;
    }

    private boolean nonPresente(int[] vicini, int j) {
        for(int i=0;i<vicini.length;i++)
        {
            if(vicini[i]==j)
                return false;
        }
        return true;
    }
    
    private void checkFine() {
        while(!t.checkFree(migliora,iGlobale))
        {
            try {
            synchronized(this)
            {
                this.wait(2000);
            }            
            } catch (InterruptedException ex) {
                Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        migliora++;
        if(migliora*PROFONDITA_MAX<n && migliora*PROFONDITA_MAX<MIGLIORAMENTO_MAX)
        {
            if(archiMigliori!=null && archiMigliori[0][1]!=0)
            nuovaIterazione();
        }
        else
        {
            System.out.println("MIGLIORA RISCHIO "+(tipologia+1)+ "- SOLUZIONI DA TESTARE ="+soluzioniTot);
            PoolThreadRami pool=null;
            if(foglieMigliori[0]!=0)
            {
                for(int i=0;i<foglieMigliori.length;i++)
                {
                    if(foglieMigliori[i]==foglieMax)
                    {
                        MiglioraRischio rischio=new MiglioraRischio(this, soluzioniMigliori[i], risk);
                        pool=rischio.ritornaRiferimentoPool();
                        try {
                        synchronized(this)
                        {
                            this.wait(10);
                        }            
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
                        }
                         while(!pool.checkFree())
                        {
                            try {
                            synchronized(this)
                            {
                                this.wait(10);
                            }            
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    if(foglieMigliori[i]==0)
                        break;
                }
            }
            try {
            synchronized(this)
            {
                this.wait(1000);
            }            
            } catch (InterruptedException ex) {
                Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
            }
             while(pool!=null && !pool.checkFree())
            {
                try {
                synchronized(this)
                {
                    this.wait(500);
                }            
                } catch (InterruptedException ex) {
                    Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }  

    
    
    private class ThreadIniziale implements Runnable
    {
        int i;
        ThreadIniziale (int primoNodo){
            i=primoNodo;
        }
        @Override
        synchronized public void run() {
            CreaSoluzione soluzioni=new CreaSoluzione(Ottimizza.this,i,tipologia);
        }

    }
    
    public synchronized void stampaSoluzione(double rischio, int rami, double archi[][])
    {
        //System.out.println("PROVA");
        if(rami<=foglieMax)
        {
            foglieMigliori[soluzioni]=rami;
            for(int i=0;i<n;i++)
            {
                soluzioniMigliori[soluzioni][i][0]=archi[i][0];
                soluzioniMigliori[soluzioni][i][1]=archi[i][1];
                soluzioniMigliori[soluzioni][i][2]=archi[i][2];
                soluzioniMigliori[soluzioni][i][3]=archi[i][3];
            }
            soluzioni++;
            soluzioniTot++;
            if(soluzioni>=foglieMigliori.length)
                soluzioni=0;
        }
        if(rami<foglieMax || (rami==foglieMax && rischio<rischioMax))
        {            
            //System.out.println("\nINIZIO SOLUZIONE");
            archiMigliori=new double[n][4];
            foglieMax=rami;
            rischioMax=rischio;
            for(int i=0;i<n;i++)
            {
                System.out.println((int)archi[i][1]+" "+(int)archi[i][0]);
                archiMigliori[i][0]=archi[i][0];
                archiMigliori[i][1]=archi[i][1];
                archiMigliori[i][2]=archi[i][2];
                archiMigliori[i][3]=archi[i][3];
            }         
            //System.out.println("foglie="+rami+"\nrischio="+rischio);
            //System.out.println("FINE SOLUZIONE\n");
        }
        
    }
    
    public boolean diverso(double archi[][])
    {
        if(archiMigliori==null)
            return true;
        for(int i=0;i<archi.length;i++)
        {
            int check=0;
            for(int j=0;j<archiMigliori.length;j++)
            {
                if(archi[i][0]==archiMigliori[j][0] && archi[i][1]==archiMigliori[j][1])
                {
                    check=1;
                }
            }
            if(check==0)
                return true;
        }
        return false;
    }
    
    private void nuovaIterazione() {
        MiglioraSoluzione miglioraSoluzione=new MiglioraSoluzione();
        miglioraSoluzione.esegui(this, PROFONDITA_MAX*migliora, archiMigliori, tipologia);
        try {
            synchronized(this)
            {
                this.wait(2000);
            }
            
        } catch (InterruptedException ex) {
            Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
        }
        checkFine();
    }
    
    //METODI COMUNI A DIVERSE CLASSI
    /**
    * Returns the distance between 2 nodes
    * @param  nodo1 an integer variable
    * @param  nodo2 an integer variable
    * @return distance between nodo1 and node2
    */
    public double distanzaTraNodi(int nodo1,double nodo2)
    {
        double distanza=0;
        int n2=(int)nodo2;
        distanza=Math.pow(vettX[nodo1]-vettX[n2],2)+Math.pow(vettY[nodo1]-vettY[n2],2);
        distanza=Math.sqrt(distanza);
        
        return distanza;
    }
    
    public synchronized boolean controllaRischio(double r, double archi[][])
    {
        if(r<rischioMax)
        {
            rischioMax=r;
            archiMigliori=new double[n][4];
            for(int i=0;i<n;i++)
            {
                archiMigliori[i][0]=archi[i][0];
                archiMigliori[i][1]=archi[i][1];
                archiMigliori[i][2]=archi[i][2];
                archiMigliori[i][3]=archi[i][3];
            }   
            return true;
        }
        return false;
    }
    
    public synchronized void aggiornaRischio(double r)
    {
        rischioMax=r;
    }
    
    public synchronized void stampaMigliore(String fileName) {
        String solution="";
        for(int i=0;i<n;i++)
        {
            solution=solution+(int)archiMigliori[i][1]+" "+(int)archiMigliori[i][0];
            if(i!=n-1)
                solution=solution+"\n";
        } 
        PrintWriter writer=null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
            writer.print(solution);
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            writer.close();
            System.exit(0);
        }
    }
    
    private void creaThreadKiller(String file) {
        Killer killer=new Killer(file);
        Thread t=new Thread(killer);
        t.start();
    }

    private class Killer implements Runnable
    {
        String file;
        public Killer (String s){
            file=s;
        }
        @Override
        synchronized public void run() {
            try {
                this.wait(3300000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
            }
            stampaMigliore(file);
        }

    }
}
