@echo off
set JAVA_HOME=C:\Users\Alifa\Desktop\eclipse-java13\jdk13
cd ..
set home=%cd%
set batchfilehome=%cd%\ant
set assemblyfilehome=%cd%\ant\out
set path=%JAVA_HOME%\bin;%home%;
set keywords=%batchfilehome%\keywords.txt
set lc3asmhome=%home%
set lc3vmhome=%home%
set chip8roms=%home%\lc3-obj\chip8roms
cd ant
