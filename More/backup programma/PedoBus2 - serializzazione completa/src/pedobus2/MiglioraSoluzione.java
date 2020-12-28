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
public class MiglioraSoluzione {
    double archi[][];
    int indexArchi;
    int rami;
    int usati[];
    Ottimizza o;
    public MiglioraSoluzione(Ottimizza ottimo, int profonditaPartenza, double archiMigliori[][], int tipologia)
    {
        o=ottimo;
        archi=new double[o.n][4];
        indexArchi=profonditaPartenza-1;
        rami=0;
        usati=new int[o.n];
        
        for(int i=0;i<profonditaPartenza;i++)
        {
            archi[i][0]=archiMigliori[i][0];
            archi[i][1]=archiMigliori[i][1];
            archi[i][2]=archiMigliori[i][2];
            archi[i][3]=archiMigliori[i][3];
            if(archi[i][0]==0)
                rami++;
            usati[(int)archi[i][1]-1]=1;
        }
        SingolaSoluzione singola;
        SingolaSoluzione2 singola2;
        if(tipologia==0)
            singola=new SingolaSoluzione(o, archi, indexArchi, rami, usati, 0,null);
        else
            singola2=new SingolaSoluzione2(o, archi, indexArchi, rami, usati, 0);
        ottimo.fineLavoro();
    }
}
