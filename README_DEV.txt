
* maven/eclipse
Para lanzar sinadura con maven (+perfiles), usar;
 - goal: 'resources:resources' y 'exec:ava'
 - profiles; 'Unix32' o 'Unix64' y 'EE'
 - argumentos:
 	exec.mainClass=net.esle.sinadura.gui.Sinadura
____
 	
* maven/shell
$ mvn resources:resources exec:java -Dexec.mainClass=net.esle.sinadura.gui.Sinadura -P Unix64,EE