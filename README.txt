** Para trabajar con el Nexus de zylk + maven es necesario tener el certificado SSL instalado en nuestra JVM
www.zylk.net/web/guest/web-2-0/blog/-/blogs/accediendo-a-repositorios-de-maven-securizados

 
** Para generar un nuevo empaquetado:

=====================================
= Cambio de versión
=====================================
- install.xml
- pom.xml (core y desktop)
  - cambiar la versión del producto generado (core y desktop)
  - cambiar la versión de la referencia del desktop al core
- configuration.properties (core y desktop)
- modificar el build.properties (estos se usan para algo? el del core? -> si no borrar)
@Irune sí se usa, en los build.xml de desktop y core.
- y en el servidor si se publica como nueva version
(http://www.sinadura.net/server)

=====================================
= Comprobaciones
=====================================
- revisar log4j
- comprobar el valor de la etiqueta "enable.send.button" que está en configuration.properties 
y que es la que controla si se muestra el botón de enviar. 

=====================================
= Generación de paquetería
=====================================

- Descargar commmons-vfs2-sinadura y hacer
mvn install

- Descargar MITyCLibTSA-sinadura y MITyCLibOCSP-sinadura y hacer:
mvn clean install

- Descargar sinaduraCore y hacer:
mvn clean install

Finalmente para generar los empaquetados, en sinaduraDesktop:

* unix 
_______________

mvn clean package -P Unix32
mvn clean package -P Unix64

El archivo de instalador es el que nos dejará en /target/sinaduraDesktop-xxx-standard.jar

* mac
_______________	

mvn clean package -P Mac


* windows
_______________

0. para el empaquetado de windows hay que descargar el launch4j-maven-plugin original,
luego patchearlo con la versión del SVN y finalmente hacer el empaquetado.
> comentar en el pom.xml de sinaduraDesktop las lineas referentes al repositorio de sinadura-zylk 'repositories>repository>sinadura-group'

1. mvn clean pre-integration-test -P Win32
> aquí falla con un 'net.sf.launch4j.BuilderException: Especifique la ruta del jar relativa al ejecutable', es normal

2. descargar el proyecto launch4j-maven-plugings del svn y hacer:
mvn install

1. repetir paso 1
mvn clean pre-integration-test -P Win32

Y con esto ya se genera el EXE.

3. descomentar las lineas del repositorio de sinadura-zylk

(no hay que hacerlo) 2. mvn clean package -P Win32 (este no generara la parte del exe, asi que no hay que utilizarlo!)


- Y renombrarlos con la nomenclatura correcta al subirlos a www.sinadura.net (ver versiones anteriores).


=====================================
= Subir nueva release a Nexus
=====================================

Es necesario tener el siguiente bloque en el ~/.m2/settings.xml
con la autenticación del nexus
<pre>
<servers>
    <server>
      <id>maven-nexus-zylk</id>
      <username>***</username>
      <password>***</password>
      <configuration></configuration>
    </server>
  </servers>

</pre>
En cada proyecto mvn deploy (-DskipTests)

