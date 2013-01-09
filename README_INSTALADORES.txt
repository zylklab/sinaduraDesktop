** Para trabajar con el Nexus de zylk + maven es necesario tener el certificado SSL instalado en nuestra JVM
www.zylk.net/web/guest/web-2-0/blog/-/blogs/accediendo-a-repositorios-de-maven-securizados

 
** Para generar un nuevo empaquetado:

=====================================
= Cambio de versión
=====================================
Actual: 3.3.3

- install.xml
- pom.xml (core, desktop, ee, ee-interfaces, parent)
  - cambiar la versión del producto generado (core y desktop)
  - cambiar la versión de las referencias a sinadura*
- configuration.properties (core y desktop)
- modificar el build.properties (estos se usan para algo? el del core? -> si no borrar)
@Irune sí se usa, en los build.xml de desktop y core.
- y en el servidor si se publica como nueva version
(http://www.sinadura.net/server)

=====================================
= Comprobaciones
=====================================

- revisar log4j.properties (desktop) que el 'log4j.rootLogger' esté a INFO

¿esto para qué?
- comprobar el valor de la etiqueta "enable.send.button" que está en configuration.properties 
y que es la que controla si se muestra el botón de enviar.
 

=====================================
= Generación de paquetería
=====================================

*  Si se han tocado los siguientes módulos, descargarlos y hacer un 'mvn clean install'.
Si no se cogerá la última versión subida al nexus

 - commmons-vfs2-sinadura
 - MITyCLibTSA-sinadura
 - MITyCLibOCSP-sinadura
 - xmlsec-mityc-sinadura

____
 
 * Desde 'sinaduraParent'
 mvn clean install
 
 Sinadura tiene como módulos 'sinaduraEE-Interface, sinaduraEE, sinaduraCore'
____
 
 * Desde 'sinaduraDesktop'

* Independientemente del so, si queremos generar la versión 'EE', deberemos insertar este perfil en el comando;
p.e; mvn clean package -P Unix64,EE

A) unix 
_______________

mvn clean package -P Unix32,EE?
mvn clean package -P Unix64,EE?

El archivo de instalador es el que nos dejará en /target/sinaduraDesktop-xxx-standard.jar

B) mac (NOK)
_______________	

mvn clean package -P Mac32,EE?


C) windows
_______________

1. mvn clean pre-integration-test -P Win32,EE?
1. mvn clean pre-integration-test -P Win64,EE?
__


Aquí falla con un
'net.sf.launch4j.BuilderException: Especifique la ruta del jar relativa al ejecutable'

Esto es porque se baja del nexus-zylk una versión del launch4j incorrecta, con lo que tenemos que arreglarlo.
Para ello;

2 - borramos del repositorio de mavel el paquete launch4j

$ ~/.m2/repository/com/akathist/maven/plugins/launch4j


3 - comentamos del pom.xml de sinaduraDekstop la linea del repositorio del nexus-zylk en 'repositories>repository>sinadura-group'

4 - volvemos a lanzar el comando del punto 1 (con esto descargamos el launch4j original)
(dará el mismo error que antes)

5 - descargamos del SVN el proyecto '/launch4j-maven-plugin' y hacemos un
mvn install

Con esto parcheamos la versión oficial del launch4j.
A mi este paso sólo me funciona desde el Eclipse, en consola me da un

Reason: POM 'org.eclipse.m2e:lifecycle-mapping' not found in repository: Unable to download the arti
fact from any repository



6 - volvemos a lanzar el punto 0

mvn clean pre-integration-test -P Win32

Y con esto ya se genera el EXE.

7. volver a activar las lineas del repositorio de sinadura-zylk en el pom.xml

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

