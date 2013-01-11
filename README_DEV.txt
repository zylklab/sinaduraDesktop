
* maven/eclipse
Para lanzar sinadura con maven (+perfiles), usar;
 - goals: 'resources:resources exec:java'
 - profiles; 'Unix32,EE' o 'Unix64,EE'
 - argumentos:
 	exec.mainClass=net.esle.sinadura.gui.Sinadura
____
 	
* maven/shell
$ mvn resources:resources exec:java -Dexec.mainClass=net.esle.sinadura.gui.Sinadura -P Unix64,EE