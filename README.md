# Sinadura Desktop

![Sinadura Logo](src/main/resources/images/sinadura.png)
## Table of Contents
- [Introduction](#introduction)
- [Releases and downloads](#releases-and-downloads)
- [Build](#build)
- [Contributors](#contributors)
- [Links](#links)

## Introduction

Sinadura Desktop is a digital signature desktop tool for Windows and Linux implemented in java. It provides advanced digital signature features such as timestamps, OCSP validation, or digital signature profiles. The application guarantees the integrity, identity and non-repudiation in any file such payslips, contracts or invoices in PDF or other Office formats. Sinadura 5 includes a mini client designed for web applications, as a replacement of applet technology, using a protocol-based system registered in your operating system.

Sinadura Desktop is composed of 6 git repositories:
 * [zylklab/MITyCLibOCSP-sinadura](https://github.com/zylklab/MITyCLibOCSP-sinadura) : Library for getting certificates status via OCSP protocol - Online Certifate Status Protocol. 
 * [zylklab/MITyCLibTSA-sinadura](https://github.com/zylklab/MITyCLibTSA-sinadura) : Library for managing TSA - Time Stamp Authorities.
 * [zylklab/xmlsec-mityc-sinadura](https://github.com/zylklab/xmlsec-mityc-sinadura)
 * [zylklab/MITyCLibXADES-sinadura](https://github.com/zylklab/MITyCLibXADES-sinadura) : Library for working with XAdES-type digital signatures, XML metatadata and validation.
 * [zylklab/sinaduraCore](https://github.com/zylklab/sinaduraCore) : Sinadura Core.
 * [zylklab/sinaduraDesktop](https://github.com/zylklab/sinaduraDesktop) : Sinadura Desktop Interface. 
 
## Releases and downloads

The last release is 5.0.9. There exist binary installers for 32 and 64 bits, for both Linux and Windows operating systems. The directory for old downloads and community releases is located at:

http://www.sinadura.net/es/community/downloads

```
3.3.3 - Community 
3.3.8 - Community 
3.4.4 - Parlamento (not published)
3.5.0 - Community (internal - not published)
3.5.1 - Community 
3.5.2 - EE (not published)
4.1.0 - LantegiBatuak
4.2.0 - Community 
5.0.0 - UPV/EHU (not published)
5.0.2 - EE (not published)
5.0.9 - Github
``` 

## Build 

For generating the installers, first compile the libraries:

```
SINADURA_PROJECTS="MITyCLibOCSP-sinadura MITyCLibTSA-sinadura xmlsec-mityc-sinadura MITyCLibXADES-sinadura sinaduraCore"

for i in $SINADURA_PROJECTS;
 do
   git clone https://github.com/zylklab/$i
   cd $i && mvn clean install && cd ..
 done

```

Then we download the main project:

```
git clone https://github.com/zylklab/sinaduraDesktop
cd sinaduraDesktop
```

### Linux / Unix 

For 32 bits:
```
mvn clean package -P Unix32,EE
```
If everything goes ok, you should find sinaduraDesktop-X.X.X-unix32-installer.jar in a generated target directory.

For 64 bits:
```
mvn clean package -P Unix64,EE
```
If everything goes ok, you should find sinaduraDesktop-X.X.X-unix34-installer.jar in a generated target directory.

### Windows 

For 32 bits:
```
mvn clean pre-integration-test -P Win32,EE
```
If everything goes ok, you should find sinadura-ee-X.X.X-windows32-installer.exe in a generated target directory.

For 64 bits:
```
mvn clean pre-integration-test -P Win64,EE
```
If everything goes ok, you should find sinadura-ee-X.X.X-windows64-installer.exe in a generated target directory.

## Contributors

- Alfredo Sanchez
- [Gustavo Fernandez](http://github.com/guszylk)
- [Irune Prado](http://github.com/wideawakening)
- [Cesar Capillas](http://github.com/CesarCapillas)
- [Douglas C.R. Paes](https://github.com/douglascrp) - Translation to Brazilian Portuguese.

## Links and resources

- [sinadura.net](http://www.sinadura.net)
- [Sinadura channel in youtube](https://www.youtube.com/channel/UC74dNGYKsZ3bL2YY9rSq_9g)
- [Sinadura documentation in Spanish](http://www.sinadura.net/es/wik)
- [Sinadura forum](http://www.sinadura.net/es/community/forum)
- [Alfresco integration: Protocol-based signatures for Alfresco](https://github.com/zylklab/alfresco-sinadura)
