/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedobus2;

/**
 *
 * @author Angelo
 */
public class PosizionaRamo {
    /**
     * Quantità di nodi iniziali di un ramo ai quali provo ad attacarne un altro ramo
     */
    private static final int NODI_PROVATI=50;
    
    double rami[][][];
    double variazioniMax[];
    int ramoControllato;
    MiglioraRischio migliora;
    int controllati;
    
    /**
     * 
     * @param r insieme dei rami
     * @param v variazioni massime che ogni ramo può subire
     * @param ra ramo da provare a collegare
     * @param m riferimento alla classe padre
     * @param controllati quantità di rami che ho provato ad attaccare ad altri, se coincide con il
     * numero totale di rami allora non è necessario provare ulteriori attacchi.
     * Lo azzero ogni volta che ho trovato un nuovo attacco migliore
     */
    public PosizionaRamo(double[][][]r,double v[],int ra, MiglioraRischio m, int controllati){
        rami=r;
        variazioniMax=v;
        ramoControllato=ra;
        migliora=m;
        
        //System.out.println(controllati);
        
        this.controllati=controllati;
        this.controllati++;
        
        if(this.controllati<rami.length+1)
            provaAttacchi();
        calcolaSoluzioneTrovata();
        migliora.fineLavoro();
    }
    
    private void provaAttacchi() { 
        for(int i=0;i<rami.length;i++)
        {
            if(i!=ramoControllato)
            {
                for(int j=0;j<rami[i].length && j<NODI_PROVATI && rami[i][j][1]!=0;j++)
                {
                    double distNodi=migliora.distanzaTraNodi(rami[i][j][1], rami[ramoControllato][0][1]);
                    double variazione=(rami[i][j][2]+distNodi)-rami[ramoControllato][0][2];
                    if(variazioniMax[ramoControllato]>variazione && 
                            migliora.ritornaRischio(rami[ramoControllato][0][1],rami[i][j][1])<=migliora.ritornaRischio(rami[ramoControllato][0][1], 0))
                    {//Ho trovato un nuovo punto dove conviene attaccare
                        
                        //effettuo modifica
                        rami[ramoControllato][0][0]=rami[i][j][1];
                        rami[ramoControllato][0][3]=migliora.ritornaRischio(rami[ramoControllato][0][1], rami[i][j][1]);
                        double variazioneOriginale=variazioniMax[i];
                        double variazioneOriginaleControllato=variazioniMax[ramoControllato];
                        
                        //System.out.println("variazioniMax(r)="+variazioniMax[ramoControllato]+"\t var(i)="+variazioniMax[i]+"\t var="+variazione);
                        
                        variazioniMax[ramoControllato]=variazioniMax[ramoControllato]-variazione;
                        if(variazioniMax[i]>variazioniMax[ramoControllato])//La variazione permessa al ramo a cui ho attaccoto il ramo controllato
                            variazioniMax[i]=variazioniMax[ramoControllato];//dipenderà dal nuovo ramo che è stato attaccato a lui
                        
                        
                        //creo nuovo thread
                        int newRamoControllato=ramoControllato+1;
                        if(newRamoControllato>=rami.length)
                            newRamoControllato=0;
                        Runnable p=new ThreadPosizionaRami(newRamoControllato);
                        migliora.iniziaLavoro(p);
                        
                        //riportno tutto allo stato prima di entrare nell'if
                        rami[ramoControllato][0][0]=0;
                        variazioniMax[ramoControllato]=variazioneOriginaleControllato;
                        variazioniMax[i]=variazioneOriginale;
                    }
                }
            }
        }
    }
    
    /**
     * Thread che richiama una nuova istanza della stessa classe con i dati
     * aggiornati
     */
    private class ThreadPosizionaRami implements Runnable
    {
        int i;
        double variazioniMaxT[];
        double ramiT[][][];
        public ThreadPosizionaRami(int i){
            this.i=i;
            ramiT=new double[rami.length][rami[0].length][4];
            for(int k=0;k<rami.length;k++)
            {
                for(int j=0;j<rami[0].length;j++)
                {
                    for(int x=0;x<4;x++)
                    {
                        ramiT[k][j][x]=rami[k][j][x];
                    }
                }
            }
            variazioniMaxT=new double[variazioniMax.length];
            for(int j=0;j<variazioniMax.length;j++)
            {
                variazioniMaxT[j]=variazioniMax[j];
            }
        }

        @Override
        public void run() {
            PosizionaRamo p=new PosizionaRamo(ramiT,variazioniMaxT,i, migliora,controllati);
        }
    }
    
    private void calcolaSoluzioneTrovata() {
        double rischioTotale=0;
        double[][] ramiAttaccati=new double[rami[0].length][4];
        int j=0;
        for(int i=0;i<rami.length;i++)
        {
            int k=0;
            while(rami[i][k][1]!=0)
            {
                ramiAttaccati[j][0]=rami[i][k][0];
                ramiAttaccati[j][1]=rami[i][k][1];
                ramiAttaccati[j][2]=rami[i][k][2];
                ramiAttaccati[j][3]=rami[i][k][3];
                rischioTotale=rischioTotale+migliora.ritornaRischio(ramiAttaccati[j][1], ramiAttaccati[j][0]);
                j++;
                k++;
            }
        }
        migliora.stampaSoluzione(rischioTotale, ramiAttaccati);
    }
}
