#!/bin/bash
#compruebo si existe una referencia previa y si existe la borro
sed -i.bak s#x-scheme-handler/sinadura=.*##g $HOME/.local/share/applications/mimeapps.list
sed -i.bak-blank '/^$/d' $HOME/.local/share/applications/mimeapps.list

#añado a la lista de mimetypes la nueva entrada 
echo "x-scheme-handler/sinadura=sinadura-desktop-protocol.desktop" >> $HOME/.local/share/applications/mimeapps.list
