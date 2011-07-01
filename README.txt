Para generar un nuevo empaquetado:


- Cambiar la version en:
install.xml
pom.xml (core y desktop)
configuration.properties (core y desktop)
modificar el build.properties (estos se usan para algo? el del core? -> si no borrar)
y en el servidor si se publica como nueva version
(http://www.sinadura.net/server)


- revisar log4j

- comprobar el valor de la etiqueta "" que está en configuration.properties y que es la que controla si se muestra el botón de enviar 

- descargar el proyecto launch4j-maven-plugings del svn y hacer un:
mvn clean install

- Instalar sinadura xadestsa y xadesocsp

- Instalar el core
cd sinadura/trunk/sinaduraCore
mvn clean install


- Generar empaquetados
mvn clean package -P Unix32
mvn clean package -P Unix64
mvn clean package -P Mac
mvn clean package -P Win32

mvn clean pre-integration-test -P Win32


- renombrarlos con la nomenclatura correcta

