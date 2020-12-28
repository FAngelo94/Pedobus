/*
    Espando i nodi più vicini al nodo scelto
*/
package pedobus2;

import java.util.Random;

/**
 *
 * @author Angelo
 */
public class SingolaSoluzione {
    Ottimizza o;
    double archi[][];
    int indexArchi;
    int rami;
    int usati[];
    int prof; 
    
    boolean dorme;
    SingolaSoluzione padre;
    public SingolaSoluzione(Ottimizza o, double archiM[][], int indexArchiM, int ramiM, int usatiM[], int profM, SingolaSoluzione p){
        this.o=o;
        this.rami=ramiM;
        this.indexArchi=indexArchiM;
        this.archi=archiM;
        this.usati=usatiM;
        this.prof=profM;
        padre=p;
        
        NodiVicini nodi=cercaNodiVicini((int)archi[indexArchi][1]);
        int nodoVecchio=(int)archi[indexArchi][1];
        indexArchi++;
        //System.out.println(indexArchi);
        if(nodi!=null)
        {
            int j=0;
            prof++;
            int nodoPrec=0;
            while(nodi!=null && (j<2 && prof<12 || j<3 && prof<7 || j<5 && prof<4 || j<1))//(j<=2 && prof<0 || j<2)
            {
                archi[indexArchi][0]=nodoVecchio;
                archi[indexArchi][1]=nodi.nodo;
                archi[indexArchi][2]=archi[indexArchi-1][2]+o.distanzaTraNodi(nodi.nodo, nodoVecchio);
                archi[indexArchi][3]=o.risk[nodi.nodo][nodoVecchio];
                usati[nodi.nodo-1]=1;
                
                creaFiglioDirettamente();
                //crea_e_dormi();
                               
                usati[nodi.nodo-1]=0;
                nodoPrec=nodi.nodo;
                nodi=nodi.next;
                j++;
            }
        }
        else
        {
            if(indexArchi<o.n)
            {//FINE RAMO, NE CREO UN ALTRO
                rami++;
                if(rami<=o.foglieMax)
                {//posso ancora trovare una soluzione ottima altrimenti non vado avanti
                    int indexVicino=0;
                    double distanzaMin=Double.MAX_VALUE;
                    for(int i=0;i<o.n;i++)
                    {
                        if(usati[i]==0 )
                        {
                            if(o.distanze[i]<distanzaMin)
                            {
                                indexVicino=i;
                                distanzaMin=o.distanze[i];
                            }
                        }
                    }
                    archi[indexArchi][0]=0;
                    archi[indexArchi][1]=indexVicino+1;
                    archi[indexArchi][2]=o.distanze[indexVicino];
                    archi[indexArchi][3]=o.risk[indexVicino+1][0];
                    usati[indexVicino]=1;

                    creaFiglioDirettamente();
                    //crea_e_dormi();

                    usati[indexVicino]=0;
                }
            }
            else
            {//FINE ALBERO
                verificaSoluzione();
            }
        }
        /*o.fineLavoro();
        if(padre!=null)
            padre.sveglia();*/
        /*if(padre==null)
            o.fineLavoro();*/
    }

    private synchronized void crea_e_dormi() {
        try{
            synchronized(this)
            {
                dorme=true;
                Runnable ti=new ThreadNodiSuccessivi();
                o.creaThreadSingolaSoluzione(ti);
                while(dorme)
                    wait();
            }
        }catch(Exception e)
        {
        }
    }
    public synchronized void sveglia()
    {
        dorme=false;
        synchronized(this)
        {
            notify();
        }
    }
    public void creaFiglioDirettamente()
    {
        double archiT[][];
        int indexArchiT;
        int ramiT;
        int usatiT[];
        archiT=new double[archi.length][4];
        for(int i=0;i<archi.length;i++)
        {
            archiT[i][0]=archi[i][0];
            archiT[i][1]=archi[i][1];
            archiT[i][2]=archi[i][2];
            archiT[i][3]=archi[i][3];
        }
        indexArchiT=indexArchi;
        ramiT=rami;
        usatiT=new int[usati.length];
        for(int i=0;i<usati.length;i++)
        {
            usatiT[i]=usati[i];
        }
        //System.out.println("CREO FIGLIO");
        SingolaSoluzione SingolaSoluzione=new SingolaSoluzione(o, archiT, indexArchiT, ramiT, usatiT, prof, this);
        //System.out.println("FIGLIO CREATO");
    }
    
    private class ThreadNodiSuccessivi implements Runnable
    {
        double archiT[][];
        int indexArchiT;
        int ramiT;
        int usatiT[];
        ThreadNodiSuccessivi (){  
            archiT=new double[archi.length][4];
            for(int i=0;i<archi.length;i++)
            {
                archiT[i][0]=archi[i][0];
                archiT[i][1]=archi[i][1];
                archiT[i][2]=archi[i][2];
                archiT[i][3]=archi[i][3];
            }
            indexArchiT=indexArchi;
            ramiT=rami;
            usatiT=new int[usati.length];
            for(int i=0;i<usati.length;i++)
            {
                usatiT[i]=usati[i];
            }
        }
        @Override
        public void run() {
            SingolaSoluzione SingolaSoluzione=new SingolaSoluzione(o, archiT, indexArchiT, ramiT, usatiT, prof, SingolaSoluzione.this);
        }

    }

    private NodiVicini cercaNodiVicini(int nodo)
    {
        NodiVicini n=null;
        for(int i=0;i<o.n;i++)
        {
            if(usati[i]==0)
            {
                double d=o.distanzaTraNodi(nodo,i+1);
                if(o.distanze[i]*o.alfa>=d+archi[indexArchi][2])//o.distanze[i]*o.alfa>=d+archi[indexArchi][2]
                {
                    n=inserimentoOrdinato(n, i, d);//inserimentoOrdinato(n, i, d+o.distanze[i])
                }
            }
        }
        return n;
    }
    
    
    private void verificaSoluzione()
    {
        double rischio=0;
        for(int i=0;i<o.n;i++)
        {
            rischio=rischio+archi[i][3];
        }
        o.stampaSoluzione(rischio,rami,archi);
        
    }
    
    public NodiVicini inserimentoOrdinato(NodiVicini n, int i, double d)
    {
        if(n==null)
        {
            n=new NodiVicini();
            n.next=null;
            n.nodo=i+1;
            n.distanza=d;
        }
        else
        {
            if(d<n.distanza)
            {//inserimento in testa
                NodiVicini tmp=new NodiVicini();
                tmp.next=n;
                tmp.nodo=i+1;
                tmp.distanza=d;
                n=tmp;
            }
            else
            {
                NodiVicini pre=n;
                NodiVicini next=n.next;
                while(next!=null && next.distanza<d)
                {
                    next=next.next;
                    pre=pre.next;
                }
                if(next==null)
                {//inserimento in coda
                    NodiVicini tmp=new NodiVicini();
                    tmp.next=null;
                    tmp.nodo=i+1;
                    tmp.distanza=d;
                    pre.next=tmp;
                }
                else
                {//inserimento centrale
                    NodiVicini tmp=new NodiVicini();
                    tmp.next=next;
                    tmp.nodo=i+1;
                    tmp.distanza=d;
                    pre.next=tmp; 
                }
            }
            
        }
        return n;
  
    }
    
    private class NodiVicini{
        int nodo;
        double distanza;
        NodiVicini next;
    }
}
