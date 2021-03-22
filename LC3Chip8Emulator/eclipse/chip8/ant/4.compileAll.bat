@echo off 
del out\*.obj
del out\*.txt
set srcfolder=C:\Users\Alifa\Desktop\lc3-backup\eclipse\LC3Compiler\ant\src
call custom.compile %srcfolder%\1.Hello.lct
call custom.compile %srcfolder%\2.localVarTest.lct
call custom.compile %srcfolder%\3.strings.lct
call custom.compile %srcfolder%\4.functions.lct
call custom.compile %srcfolder%\5.params-int.lct
rem call custom.compile %srcfolder%\6.params-str.lct
call custom.compile %srcfolder%\7.params-int-LET.lct
call custom.compile %srcfolder%\8.params-int-LETR.lct
call custom.compile %srcfolder%\9.arithmetic.lct
call custom.compile %srcfolder%\conditions\11.IF-Else.lct
call custom.compile %srcfolder%\conditions\12.SimpleNestedIF.lct
call custom.compile %srcfolder%\conditions\13.paramTest.lct
call custom.compile %srcfolder%\loops\14.paramLoop.lct
call custom.compile %srcfolder%\loops\15.doLoop.lct
call custom.compile %srcfolder%\loops\15b.print50.lct
call custom.compile %srcfolder%\loops\15c.fibonacci.lct
call custom.compile %srcfolder%\loops\15d.fibonacci.lct
call custom.compile %srcfolder%\global\17a.global.lct
call custom.compile %srcfolder%\global\17b.gbLoop.lct
call custom.compile %srcfolder%\bugs\bug2.whileLoop.lct
call custom.compile %srcfolder%\recursion\16.recursion.lct
call custom.compile %srcfolder%\recursion\16b.addtorial.lct
call custom.compile %srcfolder%\recursion\16d.factorial.lct
call custom.compile %srcfolder%\registers\reg.lct
call custom.compile %srcfolder%\division\divA.lct
call custom.compile %srcfolder%\rawprint\rawPrint.lct
call custom.compile %srcfolder%\pointers\pointerA.lct
call custom.compile %srcfolder%\pointers\pointerBprint.lct
call custom.compile %srcfolder%\pointers\strcpy.lct
call custom.compile %srcfolder%\pointers\strswap.lct
call custom.compile %srcfolder%\pointers\str-reverse.lct
call custom.compile %srcfolder%\loops\15e.nestedLoop.lct
call custom.compile %srcfolder%\arrays\arraysort.lct