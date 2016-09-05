** Para trabajar con el Nexus de zylk + maven es necesario tener el certificado SSL instalado en nuestra JVM
www.zylk.net/web/guest/web-2-0/blog/-/blogs/accediendo-a-repositorios-de-maven-securizados


REGISTRO DE VERSIONES (para conocer el perfil de cada versión)
--------------------------------------------------------------
...
3.3.3 - Community (publicada y publicitada)
3.3.8 - Community (publicada)
3.4.4 - Parlamento
3.5.0 - Community (interna)
3.5.1 - Community (publicada)
3.5.2 - EE
4.1.0 - LantegiBatuak
4.2.0 - Community (publicada y publicitada)
5.0.0 - UPV
5.0.1 - EE

* Las versiones para parlamento se pueden ver en la intranet, y el resto son todas de la Community.
Y si ademas están en la web pues es que se han publicado.



PUBLICACION DE VERSIONES - JENKINS
----------------------------------

Ahora la públicacion de versiones esta automatizada en el jenkins. Así que para generar/publicar una nueva versión hay que hacer lo siguiente:

Hacer un "perform maven release" en el jenkins de los proyectos que se hayan modificado. Hay que indicar: 
 "Release Version:" versión que se va a publicar (por ejemplo: 3.3.7)
 "Development version:" versión a la que se actualiza el pom despues de generar la nueva versión (por ejemplo: 3.3.8-SNAPSHOT)

Hay que tener en cuenta que si se hace un "release" de un proyecto hay que hacer un "release" también de los proyectos que dependen de él.

El orden en el que hay que ir realizado este proceso es el siguiente:
 - MITyCLibOCSP-sinadura
 - MITyCLibTSA-sinadura
 - MITyCLibXADES-sinadura
 - xmlsec-mityc-sinadura
 - sinaduraEE-Interface
 - sinaduraCore
 - sinaduraEE
 - sinaduraDesktop (en este no hay que hacer "perform maven release")


Antes de hacer el "release" es importante revisar el pom del proyecto en cuestión, por si hay referencias con "-SNAPSHOT". Si existen estas
dependencias hay que quitarle dicho sufijo (para apuntar a la versión estable). Es decir, NO hay que editar la version del 
propio pom (esto lo hace automaticamente el jenkins), unicamente hay que revisar las dependencias.
   * (no se si hay alguna forma de que se haga esto automaticamente)


Por último en el proyecto "sinaduraDesktop" no hay que hacer "perform maven release", sino un build normal del jenkins. Aunque antes hay que 
revisar tambien el pom.xml, quitando los "-SNAPSHOT" de las dependencias, y en este caso quitandolo tambien de la versión principal del pom.

A nivel de codigo fuente hay que especificar a mano la versión en:
- En el pom.xml de sinaduraDesktop
- En el "configuration.properties" (desktop) la propiedad "application.local.version". El valor debe tener un valor numérico de unicamente 
3 dígitos ya que se utiliza para realizar comparaciones entre versiones. Así que la version del desktop debe seguir siempre el formato 
x.x.x (y no x.x.xx o x.xx.x) para que se pueda hacer la correspondencia a 3 digitos.   

Una vez hecho esto (y subido al svn) ya se puede hacer el build.

Y una vez publicada la versión hay que hacer también a mano:
- La tag correspondiente en el svn.
- Actualizar el pom, incrementando la versión y poniendole el sufijo "-SNAPSHOT".




=====================================
= Cambio de versión
=====================================


- En el pom.xml (core, desktop, ee, ee-interfaces, parent, mityc...)
  - cambiar la versión del producto generado (core y desktop)
  - cambiar la versión de las referencias a sinadura*
- En el "configuration.properties" (desktop) la propiedad "application.local.version" 
- Y en el servidor si se publica como nueva version (esto solo para la comunnity. Y para la EE?).
(http://www.sinadura.net/server)

=====================================
= Comprobaciones adicionales
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

D) Parlamento
_______________

mvn clean pre-integration-test -P Win32,EE,Parlamento


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


1. Es necesario tener configurada la variable MAVEN_OPTS para que confie en certificado servidor.
<pre>
export MAVEN_OPTS="-Djavax.net.ssl.trustStore=/usr/lib/jvm/java-6-sun/jre/lib/security/cacert -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.debug=ssl"
</pre>


Es necesario tener el siguiente bloque en el ~/.m2/settings.xml con la autenticación del nexus
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

(si se han modificado)
- vfs2
- launch4j
- mityc*-sinadura

- sinaduraCore
- sinaduraDesktop NO ya que no es una dependencia para nadie

