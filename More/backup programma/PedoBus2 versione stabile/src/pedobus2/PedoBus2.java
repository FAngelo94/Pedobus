/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pedobus2;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 *
 * @author Angelo
 */
public class PedoBus2 {

    /**
     * @param args the command line arguments
     */
    static int foglieMax;
    static double rischioMax;
    public static void main(String[] args) {
        leggiFile();
        calcolaDistanzaDaScuola();
        trovaRami();
        stampaRisultato();
        contaFoglie();
        Ottimizza ott=new Ottimizza(foglieMax,rischioMax,vettX,vettY,risk,alfa,n,distanze);
        //Test t=new Test();
    }
    
    static int n;
    static double alfa;
    static int vettX[];
    static int vettY[];
    static double risk[][];
    public static void leggiFile(){
        try{
            FileReader f;
            f=new FileReader("soluzione.dat");
            BufferedReader b;
            b=new BufferedReader(f);
            String s;
            //Read N
            while(true)
            {
                s=b.readLine();
                String[]tmp=s.split(" := ");
                if(tmp[0].contains("param n"))
                {
                    n=Integer.parseInt(tmp[1]);
                    System.out.println(n);
                    break;
                }
            }
            //Read alpha
            while(true)
            {
                s=b.readLine();
                String[]tmp=s.split(" := ");
                if(tmp[0].contains("param alpha"))
                {
                    alfa=Double.parseDouble(tmp[1]);
                    System.out.println(alfa);
                    break;
                }
            }
            //Read X
            vettX=new int[n+1];
            while(true)
            {
                s=b.readLine();
                if(s.contains("param coordX [*] :="))
                {
                    break;
                }
            }
            s=b.readLine(); 
            while(!s.equals(";")){
                String riga=s;
                for(int i=0;i<riga.length();i++)
                {
                    String estr="";
                    while(" ".equals(riga.charAt(i)+"") || "\t".equals(riga.charAt(i)+""))
                        i++;
                    while(!" ".equals(riga.charAt(i)+"") || "\t".equals(riga.charAt(i)+""))
                    {
                        estr=estr+riga.charAt(i);
                        i++;
                    }
                    int index=Integer.parseInt(estr);
                    estr="";
                    while(" ".equals(riga.charAt(i)+""))
                        i++;
                    while(i<riga.length() && !" ".equals(riga.charAt(i)+""))
                    {
                        estr=estr+riga.charAt(i);
                        i++;
                    }
                    vettX[index]=Integer.parseInt(estr);
                }
                s=b.readLine();
            }
            //Read Y
            vettY=new int[n+1];
            while(true)
            {
                s=b.readLine();
                if(s.contains("param coordY [*] :="))
                {
                    break;
                }
            }
            s=b.readLine();
            while(!s.contains(";")){
                String riga=s;
                for(int i=0;i<riga.length();i++)
                {
                    String estr="";
                     while(" ".equals(riga.charAt(i)+"") || "\t".equals(riga.charAt(i)+""))
                        i++;
                    while(!" ".equals(riga.charAt(i)+""))
                    {
                        estr=estr+riga.charAt(i);
                        i++;
                    }
                    int index=Integer.parseInt(estr);
                    estr="";
                    while(" ".equals(riga.charAt(i)+"") || "\t".equals(riga.charAt(i)+""))
                        i++;
                    while(i<riga.length() && !" ".equals(riga.charAt(i)+""))
                    {
                        estr=estr+riga.charAt(i);
                        i++;
                    }
                    vettY[index]=Integer.parseInt(estr);
                }
                s=b.readLine();
            }
            //Read risk
            
            while(true)
            {
                s=b.readLine();
                if(s.contains("param d [*,*]:"))
                {
                    break;
                }
            }
            b.readLine();
            s=b.readLine();
            risk=new double[n+1][n+1];
            while(!s.equals(";"))
            {
                int i=0;
                int colo=0;
                while(" ".equals(s.charAt(i)+"")|| "\t".equals(s.charAt(i)+""))
                        i++;
                String estr="";
                while(!" ".equals(s.charAt(i)+"") && !"\t".equals(s.charAt(i)+""))
                {
                    estr=estr+s.charAt(i);
                    i++;
                }
                int index=Integer.parseInt(estr);
                estr="";
                for(int j=i;j<s.length();j++)
                {
                    estr="";
                    while(" ".equals(s.charAt(j)+"")|| "\t".equals(s.charAt(j)+""))
                        j++;
                    while(j<s.length() && (!" ".equals(s.charAt(j)+"") && !"\t".equals(s.charAt(j)+"")))
                    {
                        estr=estr+s.charAt(j);
                        j++;
                    }
                    risk[index][colo]=Double.parseDouble(estr);
                    colo++;
                }
                s=b.readLine();
            }
            System.out.println("FINE");
            
        }catch(Exception e)
        {
            System.out.println(e);
        }
    }

    static double distanze[];
    static boolean controllati[];
    static double archi[][];
    static boolean foglie[];
    static int indexArchi;
    public static void calcolaDistanzaDaScuola()
    {
        distanze=new double[n];
        controllati=new boolean[n];
        foglie=new boolean[n];
        for(int i=1;i<n+1;i++)
        {
            distanze[i-1]=Math.pow(vettX[i]-vettX[0], 2)+Math.pow(vettY[i]-vettY[0], 2);
            distanze[i-1]=Math.sqrt(distanze[i-1]);
            controllati[i-1]=false;
            foglie[i-1]=false;
        }
        
    }
    
    public static void trovaRami()
    {
        indexArchi=0;
        archi=new double[n][4];//segno i due nodi collegati, la distanze e il rischio dell'arco, esempio: 2,3,12.63,0.8
        while(indexArchi<n)
        {
            int index=minore();//trovo l'indice del nodo più vicino alla scuola non ancora controllato
            archi[indexArchi][0]=0;
            archi[indexArchi][1]=index+1;
            archi[indexArchi][2]=distanze[index];
            archi[indexArchi][3]=risk[index+1][0];
            foglie[indexArchi]=true;
            trovaNodiVicini();
            indexArchi++;
        }
    }
    public static int minore()
    {
        double minore=Double.MAX_VALUE;
        int index=0;
        for(int i=0;i<n;i++)
        {
            if(distanze[i]<minore && controllati[i]==false)
            {
                index=i;
                minore=distanze[i];
            }
        }
        controllati[index]=true;
        return index;
    }
    public static double distanzaTraNodi(int nodo1,double nodo2)
    {
        double distanza=0;
        int n2=(int)nodo2;
        distanza=Math.pow(vettX[nodo1]-vettX[n2],2)+Math.pow(vettY[nodo1]-vettY[n2],2);
        distanza=Math.sqrt(distanza);
        
        return distanza;
    }
    
    public static void stampaRisultato()
    {
        System.out.println("DATI");
        for(int i=0;i<n+1;i++)
        {
            System.out.println(i+"("+vettX[i]+","+vettY[i]+")");
        }
        for(int i=0;i<n;i++)
        {
            System.out.println((int)archi[i][1]+" "+(int)archi[i][0]);
        }
    }
    
    public static void contaFoglie()
    {
        int conta=0;
        double rischio=0;
        for(int i=0;i<n;i++)
        {
            if(foglie[i])
                conta++;
            rischio=rischio+archi[i][3];
        }
        System.out.println("Foglie="+conta);
        System.out.println("Rischio="+rischio);
        foglieMax=conta;
        rischioMax=rischio;
    }
    
    public static void trovaNodiVicini()
    {
        int nodo=(int)archi[indexArchi][1];
        int index=nodoPiuVicino(nodo);
        if(index!=-1)
        {
            indexArchi++;
            if(indexArchi<300)
                trovaNodiVicini();
        }
        //se non entro nel if sono finiti i nodi vicini
    }
    public static int nodoPiuVicino(int nodo)
    {
        double distanzaDaScuola=archi[indexArchi][2];
        double distanza=Double.MAX_VALUE;
        int index=-1;
        for(int i=0;i<n;i++)
        {
            if(controllati[i]==false)
            {
                double d=Math.pow(vettX[nodo]-vettX[i+1], 2)+Math.pow(vettY[nodo]-vettY[i+1], 2);
                d=Math.sqrt(d);
                if(distanzaDaScuola+d<=distanze[i]*alfa && distanzaDaScuola+d<=distanza)
                {
                    if(distanzaDaScuola+d<distanza)
                    {
                        index=i;
                        distanza=distanzaDaScuola+d;
                    }
                    else
                    {
                        if(distanzaDaScuola+d==distanza && risk[i][nodo]<risk[index][nodo])
                        {
                            index=i;
                        distanza=distanzaDaScuola+d;
                        }
                    }
                }
            }
        }
        if(index!=-1)
        {
            archi[indexArchi+1][0]=archi[indexArchi][1];
            archi[indexArchi+1][1]=index+1;
            archi[indexArchi+1][2]=distanza;
            archi[indexArchi+1][3]=risk[index+1][(int)archi[indexArchi][1]];
            controllati[index]=true;
            foglie[indexArchi+1]=true;
            foglie[indexArchi]=false;
        }
        return index;
    }
}
