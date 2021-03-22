@echo off 
set assembleroptions=nodebug,C:/Users/Alifa/Desktop/AssemblerDriver.debug.txt,noLSBCheck
set vmtoptions=nodebug,C:/Users/Alifa/Desktop/Disassembler.debug.txt,MemViolationCheck
cd ..
rem usage: run out\src.txt out\src.obj
set src=%cd%\ant\%1
set obj=%cd%\ant\%2
java -cp %lc3asmhome%\bin; lc3.assembler.AssemblerDriver %src% %obj% %3
cd ant
REM argument %3 is optional (hex).
java -cp %lc3vmhome%\bin; lc3.vm.main.RunLite %obj% %3