/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedobus2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger; 

/**
 *
 * @author Angelo
 */
public class Ottimizza {
    private static final int NODI_iNIZIALI=5;
    private static final int PROFONDITA_MAX=2;
    private static final int MIGLIORAMENTO_MAX=400;
    
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
    double soluzioniMigliori[][][];
    
    //VARIABILE CHE IDENTIFICA L'ALGORITMO CHE VOGLIO APPLICARE
    int tipologia;
    
    //POOL INIZIALE
    public  Ottimizza()
    {
        
    }
    
    Ottimizza(int foglieMax, double rischioMax, int[] vettX, int[] vettY, double[][] risk, double alfa, int n,double[]distanze) {
        //salvare più soluzioni
        soluzioni=0;
        soluzioniMigliori=new double[2000][n][4];
        foglieMigliori=new int[2000];
        
        
        this.foglieMax=foglieMax;
        this.rischioMax=rischioMax;
        this.vettX=vettX;
        this.vettY=vettY;
        this.risk=risk;
        this.alfa=alfa;
        this.n=n;
        this.distanze=distanze;
        
        
        int nt=1;
        t=new GeneraThread(NODI_iNIZIALI);
        //for(int i=0;i<NODI_iNIZIALI;i++)
        {
            int NNN=0;
            System.out.println("ANALISI NODO PRIMO="+NNN);
            System.out.println("INIZIO ANALISI ALGORITMO 1");
            soluzioni=0;
            tipologia=0;
            migliora=0;
            eseguiSoluzione(NNN);
            t.reset();
            /*t.termina();
                 
            t=new GeneraThread(NODI_iNIZIALI);
            System.out.println("INIZIO ANALISI ALGORITMO 2");
            tipologia=1;
            migliora=0;
            //eseguiSoluzione(i);
            t.termina();*/
        }   
        System.out.println("FINE ELABORAZIONE");
        t.termina();
        
    }
     
    private void eseguiSoluzione(int i)
    {
        int vicini[]=trovaViciniAScuola();
        //for(int i=vicini.length-1;i>=0;i--)
        //{
            //Runnable thread=new ThreadIniziale(vicini[i]+1);
            Runnable run=new ThreadIniziale(vicini[i]+1);
            t.lavora(run);
            

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
                this.wait(500);
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
        System.out.println("CHECK FINE");
        while(!t.checkFree(migliora,iGlobale))
        {
            try {
            synchronized(this)
            {
                this.wait(1000);
            }            
            } catch (InterruptedException ex) {
                Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("MIGLIORA="+migliora);
        migliora++;
        if(migliora*PROFONDITA_MAX<n && migliora*PROFONDITA_MAX<MIGLIORAMENTO_MAX)
        {
            nuovaIterazione();
        }
        else
        {
            System.out.println("MIGLIORA RISCHIO "+tipologia);
            PoolThreadRami pool=null;
            for(int i=0;i<soluzioniMigliori.length;i++)
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
            }
            try {
            synchronized(this)
            {
                this.wait(500);
            }            
            } catch (InterruptedException ex) {
                Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
            }
             while(!pool.checkFree())
            {
                try {
                synchronized(this)
                {
                    this.wait(100);
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
    
    //AGGIUNGERE SYNCHRONIZED A QUESTO METODO SE USO IL PARALLELO
    public void stampaSoluzione(double rischio, int rami, double archi[][])
    {
        //System.out.println("PROVA NUOVA SOLUZIONE");
        if(rami<=foglieMax && rischio>22)
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
            if(soluzioni>=foglieMigliori.length)
                soluzioni=0;
           
        }
        boolean check=true;
        if(rami<foglieMax || (rami==foglieMax && rischio<=rischioMax))
        {            
            System.out.println("\nINIZIO SOLUZIONE");
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
            System.out.println("foglie="+rami+"\nrischio="+rischio);
            System.out.println("FINE SOLUZIONE\n");
            MiglioraRischio miglioraRischio=new MiglioraRischio(this, archiMigliori, risk);
            check=false;
        }
        if(archiMigliori!=null && rami<=foglieMax && rischio<=22 && check)
        {
            MiglioraRischio miglioraRischio=new MiglioraRischio(this, archiMigliori, risk);
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
        if(archiMigliori!=null)
        {
            MiglioraSoluzione miglioraSoluzione=new MiglioraSoluzione(this, PROFONDITA_MAX*migliora, archiMigliori, tipologia);
            try {
                synchronized(this)
                { 
                    this.wait(500);
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(Ottimizza.class.getName()).log(Level.SEVERE, null, ex);
            }
            checkFine();
        }
        
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
    
    public synchronized boolean controllaRischio(double r)
    {
        if(r<rischioMax)
        {
            rischioMax=r;
            return true;
        }
        return false;
    }
    
    public synchronized void aggiornaRischio(double r)
    {
        rischioMax=r;
    }
}
