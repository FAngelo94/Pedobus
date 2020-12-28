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
public class CreaSoluzione {
    double archi[][];
    int indexArchi;
    int rami;
    int usati[];
    Ottimizza o;
    public CreaSoluzione(Ottimizza ottimo, int primoNodo, int tipologia)
    {
        o=ottimo;
        archi=new double[o.n][4];
        archi[0][0]=0;
        archi[0][1]=primoNodo;
        archi[0][2]=o.distanze[primoNodo-1];
        archi[0][3]=o.risk[primoNodo][0];
        indexArchi=0;
        rami=1;
        usati=new int[o.n];
        usati[primoNodo-1]=1;
        SingolaSoluzione2 singola2;
        SingolaSoluzione singola;
        if(tipologia==0)
            singola=new SingolaSoluzione(o, archi, indexArchi, rami, usati, 0, null);
        else
            singola2=new SingolaSoluzione2(o, archi, indexArchi, rami, usati, 0);
        System.out.println("FINE CREA SOLUZIONE");
        ottimo.fineLavoro();
    }
}
