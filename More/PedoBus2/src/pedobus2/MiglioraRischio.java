/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedobus2;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Angelo
 */
public class MiglioraRischio {
    
    Ottimizza o;
    double archi[][];
    int rami;
    //SEPARO I VARI RAMI IN DIVERSI VETTORI
    double ramiSpezzati[][][];
    //CALCOLO LA VARIAZIONE MASSIMA CHE POSSO FARE AL NODO DI TESTA
    //IN BASE AL NODO DEL RAMO CHE PU0' VARIARE DI MENO
    double variazioneMaxRamo[];
    /**
     * Matrice dei rischi nel collegare 2 nodi
     */
    double risk[][];
    
    /**
     * Rischio più basso trovato finora
     */

    
    public MiglioraRischio(Ottimizza o, double archi[][], double ri[][]){

        risk=ri;
        this.archi=archi;
        rami=0;
        this.o=o;
        for(int i=0;i<archi.length;i++)
            if(archi[i][0]==0)
                rami++;
        ramiSpezzati=new double[rami][archi.length][4];
        int r=0;
        for(int i=0;i<archi.length;i++)
        {
            int j=0;
            ramiSpezzati[r][j][0]=archi[i][0];
            ramiSpezzati[r][j][1]=archi[i][1];
            ramiSpezzati[r][j][2]=archi[i][2];
            ramiSpezzati[r][j][3]=archi[i][3];
            j++;
            i++;
            while(i<archi.length && archi[i][0]!=0)
            {
                ramiSpezzati[r][j][0]=archi[i][0];
                ramiSpezzati[r][j][1]=archi[i][1];
                ramiSpezzati[r][j][2]=archi[i][2];
                ramiSpezzati[r][j][3]=archi[i][3];
                i++;
                j++;
            }
            i--;
            r++;
        }
        calcolaVariazioneMax(); 
        provaCollegamenti();
    }
    
    public PoolThreadRami ritornaRiferimentoPool()
    {
        return pool;
    }

    /**
    * Calcola la variazione massima sopportabile da un ramo,
    * cioè da quando si può allontanare il ramo dalla scuola rispettando
    * i vincoli di distanza.
    */
    private void calcolaVariazioneMax() {
        variazioneMaxRamo=new double[rami];
        for(int i=0;i<rami;i++)
        {
            int j=0;
            variazioneMaxRamo[i]=Double.MAX_VALUE;
            while(ramiSpezzati[i][j][1]!=0)
            {
                int nodoPartenza=(int)ramiSpezzati[i][j][0];
                int nodoArrivo=(int)ramiSpezzati[i][j][1];
                double varianzioneAttuale;
                varianzioneAttuale=o.distanze[nodoArrivo-1]*o.alfa-(ramiSpezzati[i][j][2]);
                if(varianzioneAttuale<variazioneMaxRamo[i])
                    variazioneMaxRamo[i]=varianzioneAttuale;
                j++;
            }
        }
    }
    
    
    PoolThreadRami pool;
    /**
     * Dati i vari rami e i massimi spostamenti accetati da ognuno di essi,
     * provo a staccare la testa di un ramo della scuola e attaccarla al nodo
     * di un altro ramo.
     * Le foglie finali rimarrano invariate ma magari diminuito il rischio (scelgo
     * strade meno pericolose)
     */
    private void provaCollegamenti() {
        pool=new PoolThreadRami();
        for(int i=0;i<rami;i++)
        {
            Runnable p=new ThreadPosizionaRami(i);
            iniziaLavoro(p);
        }
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

    private class ThreadPosizionaRami implements Runnable
    {
        int i;
        public ThreadPosizionaRami(int i){
            this.i=i;
        }

        @Override
        public void run() {
            PosizionaRamo p=new PosizionaRamo(ramiSpezzati,variazioneMaxRamo,i, MiglioraRischio.this,0);
        }
    }
    
    public synchronized void iniziaLavoro(Runnable r) {
        pool.lavora(r);
    }
    public synchronized void fineLavoro()
    {
        pool.fineLavoro();
    }
    
    public double ritornaRischio(double partenza,double arrivo)
    {
        return risk[(int)partenza][(int)arrivo];
    }
    public double distanzaTraNodi(double nodo1,double nodo2)
    {
        return o.distanzaTraNodi((int)nodo1, nodo2);
    }
    
    public synchronized void stampaSoluzione(double rischio, double archi[][])
    {
        //System.out.println("PROVA");
        if(o.controllaRischio(rischio, archi))
        {            
            for(int i=0;i<archi.length;i++)
            {
                System.out.println((int)archi[i][1]+" "+(int)archi[i][0]);
                archi[i][0]=archi[i][0];
                archi[i][1]=archi[i][1];
                archi[i][2]=archi[i][2];
                archi[i][3]=archi[i][3];
            }
            //System.out.println("foglie="+rami+"\nrischio="+rischio);
            //System.out.println("FINE MIGLIORA \n");
        }
    }
    
    private void checkFine() {
        
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
        pool.termina();
    } 
}
