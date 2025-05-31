@echo off
del main\*.class 2>nul
del entity\*.class 2>nul
del tile\*.class 2>nul
del object\*.class 2>nul
del item\*.class 2>nul
del environment\*.class 2>nul
rmdir /s /q bin 2>nul
mkdir bin
javac -d bin main/*.java entity/*.java tile/*.java object/*.java item/*.java environment/*.java
if %errorlevel% equ 0 (
    echo Compilation successful!
    xcopy res bin\res /E /I /Y
    java -cp bin main.Main
) else (
    echo Compilation failed!
)
pause