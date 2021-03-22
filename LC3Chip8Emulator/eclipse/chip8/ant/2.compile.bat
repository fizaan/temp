@echo off
set src=%cd%\%1
java -cp %home%\bin; lc3.compiler.main.CompilerDriver %src%
REM java -cp %compilerhome%\bin; lc3.compiler.versionB.main.LC3VBMain %src%