# Sinadura Desktop

## Intro

Sinadura Desktop is a java desktop application for digital signature of any file type. The application guarantees the integrity, identity and non-repudiation in any file such payslips, contracts or invoices. It provides advanced digital signature features such as OCSP validation, Timestamps or digital signature profiles.

Sinadura Desktop is composed of 6 github repos:
 * zylklab/MITyCLibOCSP-sinadura (MITycLibOCSP-sinadura is a library for getting certificates status via OCSP protocol - Online Certifate Status Protocol). 
 * zylklab/MITyCLibTSA-sinadura (MITycLibTSA-sinadura is a library for managing TSA - Time Stamp Authorities).
 * zylklab/xmlsec-mityc-sinadura
 * zylklab/MITyCLibXADES-sinadura (MITyCLibXADES-sinadura is a library for working with XAdES-type digital signatures, XML metatadata and validation).
 * zylklab/sinaduraCore (Sinadura Core)
 * zylklab/sinaduraDesktop (Sinadura Desktop interface) 
 
## Releases and downloads

The last release is 5.0.9. There exist binary installes for 32 and 64 bits, for both Linux and Windows operating systems.

The directory for old downloads and community releases is located at:

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

## Build (TODO)

### Linux / Unix 

For 32 bits:
```
mvn clean package -P Unix32,EE
```

For 64 bits:
```
mvn clean package -P Unix64,EE
```

### Windows 

TODO

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
- [Alfresco integration](https://github.com/zylklab/alfresco-sinadura)
