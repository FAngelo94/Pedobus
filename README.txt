UTILIZZO DEL SOLVER:

Per utilizzare il nostro solver occorre: 
- aprire il terminale e posizionarsi nella cartella dove è contenuto il solver;
- eseguire: java -jar PedoBus2.jar /datapath/pedibus_x.dat

Verrà salvata nella medesima cartella del solver il file di output pedibus_x.sol
(datapath è il percorso della cartella contenente il dataset)



FUNZIONAMENTO DEL SOLVER

Il nostro solver implementa un algoritmo di tipo branch & bound, con specifiche approssimazioni in grado
di ridurre il tempo di esecuzione dell'algoritmo stesso a costo di perdere la garanzia di ottimalità
della soluzione.


Nella fase di inizializzazione vengono costruite le stutture dati utilizzate dal solver: l'input viene 
analizzato al fine di costruire un grafo i cui nodi rappresentano le fermate del pedibus e i cui archi 
contengono i due pesi considerati: distanza e rischiosità. In questa prima fase inoltre per ogni nodo n
si crea una lista ordinata contenente i nodi vicini, v, che possono essere collegati a tale node (ovvero i
nodi per cui la somma delle distanze tra la radice e n e da n a v rispetta i requisiti dati in base al 
fattore alfa),il criterio di ordinamento è la distanza e in caso di parità di distanza la rischiosità.

L'algorito iterativamente cerca di costruire un albero partendo dalla radice, ad ogni iterazione aggiunge un node
cercando di estendere un ramo già esistente anzichè crearne uno nuovo. La scelta del nodo da aggiungere è bastaa
dall'ordinamento fatto nella fase iniziale. In particolare:
- si aggiunge un nodo alla radice
- si cerca di estendere tale node finchè il ramo non può più essere esteso
- una valta che non sono presenti più rami estendibili si crea un nuovo ramo partendo dalla radice
(Si noti che in questa fase non vengono create nuovi rami a partire da un nodo interno dell'albero)

Dato un albero parziale, ogni volta che un nodo deve essere aggiunto si prova a ad aggiungere uno dei K nodi
più vicini al nodo che si vuole estendere, vengono quindi creati K nuovi branch ognuno che contiene una soluzione 
parziale diversa.
Il numero K di possibili nodi da attaccare a un nodo stabilitò determina il numero di possibili casi analizzati.
Si nota che k può variare in base alla profondità del nodo che si vuole espandere, infatti vengono analizzate più 
possibilità di espansione di un nodo quando si è prossimi alla radici e meno, fino a 1 sola possibilità, quando 
il nodo è lontano dalla radice (a una profondità superiore a 8)
Ovviamente tale numero K non è pari al numero di nodi del grafo, infatti l'algoritmo impiegerebbe troppo tempo per 
la richerca, ma tipicamente è compreso tra 1 e 5. 
Per esempio partendo dalla radice vengono creati 5 possibili alberi parziali ognuno con uno dei 5 nodi più vicini
collegato alla radice, per ogni nodo collegato si provano i 5 nodi più vicini a lui,... man mano che si scende 
in profondità K viene ridotto fino a 1. In questo modo si riduce il numero di possibilità da provare.

L'approccio utilizzato per la creazione dei vari branch, dell'algoritmo di branch and bound, è di tipo depth first,
ciò ci permette di ottenere la PRIMA soluzione in N-1 step, tale soluzione è anche una buona soluzione infatti viene
creata aggiungendo sempre il nodo più vicino all'ultimo nodo aggiunto utilizzando quindi un approccio che si 
potrebbe definire gready. Vengono comunque verificate anche le altre possibili soluzioni provando ad aggiungere a un
nodo uno dei K più vicini.

Dal momento che la prima soluzione viene troata dopo N-1 step si può applicare alle successive soluzioni parziali una
verifica che permette di effettuare pruning. Infatti una soluzione parziale viene scartata  se il numero di rami è maggiore
della soluzione migliore trovata, in caso di parità del numero di rami si verifica se la richiosità è maggiore della megliore
trovata. La possibilità di ottereen una soluzione buono in N-1 step ci permette di applicare notevolmento l'operazione
di pruning.

Per ottenere una esplorazione più rapida si è utilizzato un approccio multi-thread. In cui più soluzioni parziali vengono
esplorate da un pool di 1000 thread.
NOTA: si noti che l'utilizzo di 100 thread risulta non performante quando il computer utilizzato non supporta una carico
così elevato, noi abbiamo utilizzato un processore intel i7 a 2 Ghz.

L'algoritmo descritto viene eguito una seconda volta modificando però il criterio di scelta del nodo da collegare ad uno da 
espandere, infatti se nel primo caso si sceglieva uno dei K nodi più vicini dal nodo da espandere nel secondo caso si sceglie
un nodo la cui somma tra la distanza più breve dalla radice al nodo da aggiungere più la distanza del nodo da espandere al
nodo da aggiungere sia minima.

Durante l'esecuzione dell'algoritmo sopra descritto viene mantentuto un insieme di M soluzioni trovate. Tali soluzioni sono le
 ultime M migliori trovate. Tali soluzioni vengono processate da un'ulteriore algoritmo. Quest'ultimo, data una suluzione prova 
a modificare il nodo di partenza di tutti i rami che contiene l'albero in modo da considerare dei casi che fin'ora non erano stati 
trattati. Le soluzioni così generate vengono confrontate con la migliore e nel caso in cui si ottiene un miglioramento si considera
migliore la nuova soluzione trovata.

Utilizzando queste tecniche il nostro solver è in grado di provare un vasto numero di soluzioni e ottenerne una che, pur 
non essendo garantita la sua ottimalità, risulta molto soddisfacente.
