Para generar un nuevo empaquetado:


* Cambiar la version en:
- install.xml
- pom.xml (core y desktop)
  - cambiar la versión del producto generado (core y desktop)
  - cambiar la versión de la referencia del desktop al core
- configuration.properties (core y desktop)
- modificar el build.properties (estos se usan para algo? el del core? -> si no borrar)
@Irune sí se usa, en los build.xml de desktop y core.
- y en el servidor si se publica como nueva version
(http://www.sinadura.net/server)

- revisar log4j

- comprobar el valor de la etiqueta "enable.send.button" que está en configuration.properties 
y que es la que controla si se muestra el botón de enviar. 

- descargar el proyecto launch4j-maven-plugings del svn y hacer:
mvn clean install

- Descargar MITyCLibTSA-sinadura y MITyCLibOCSP-sinadura y hacer:
mvn clean install

- Descargar sinaduraCore y hacer:
mvn clean install


Finalmente para generar los empaquetados, en sinaduraDesktop: 
mvn clean package -P Unix32
mvn clean package -P Unix64
mvn clean package -P Mac
mvn clean pre-integration-test -P Win32
mvn clean package -P Win32 (este no generara la parte del exe, asi que no hay que utilizarlo!)


- Y renombrarlos con la nomenclatura correcta al subirlos a www.sinadura.net (ver versiones anteriores).
