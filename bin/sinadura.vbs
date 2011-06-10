set cmd = createobject("wscript.shell")
Set shell = CreateObject("Scripting.FileSystemObject")
dta=" @echo off"&vbcrlf& _
"%c:\windows\system32%"&vbcrlf& _
""
cmd.run "sinadura.bat", vbHide