@echo off
javac -h "..\src\main\java\org\crayne\sketch\util\lib" "..\src\main\java\org\crayne\sketch\util\lib\NativeSketchLibrary.java"
move "..\src\main\java\org\crayne\sketch\util\lib\org_crayne_sketch_util_lib_NativeSketchLibrary.h" "."
del "..\src\main\java\org\crayne\sketch\util\lib\NativeSketchLibrary.class"
del "SketchLibrary.h"
ren "org_crayne_sketch_util_lib_NativeSketchLibrary.h" "SketchLibrary.h"
pause