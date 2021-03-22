@echo off
java -cp %home%\bin; lc3.compiler.main.CompilerDriver %1
REM java -cp %compilerhome%\bin; lc3.compiler.versionB.main.LC3VBMain %src%