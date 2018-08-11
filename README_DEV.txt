
* perfiles actuales
 - Unix32/Unix64
 - EE
 - Parlamento (configs productivos)
 	- insertar el fichero zain.p12 en
 		/sinaduraDesktop/src/main/resources/zain/zain.p12
 		
 	- setear password 
 		/sinaduraDesktop/src/main/resources/config/configuration.properties
 		zain.p12.password=
___

* maven/eclipse
Para lanzar sinadura con maven (+perfiles), usar;
 - Goals: 'resources:resources exec:java'
 - Profiles:
 	'Unix32'
 	'Unix64'
 	'Unix32,EE' 
 	'Unix64,EE'
 	'Unix32,Parlamento,EE'
 	'Unix64,Parlamento,EE' 
 - Argumentos:
 	exec.mainClass=net.esle.sinadura.gui.Sinadura
____
 	
* maven/shell
$ mvn resources:resources exec:java -Dexec.mainClass=net.esle.sinadura.gui.Sinadura -P Unix64,EE

