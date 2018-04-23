This is a fully functioning compiler written for my compiler course. It essentially converts Crux code into MIPS assembly code. The MIPS assembly code can be processed through SPIM (a MIPS emulator) that takes MIPS assembly code and generates output.

Examples of the Crux code, converted MIPS assembly code, MIPS input, and MIPS output are located within the tests folder

How to use:

1. Use a terminal/command_prompt to navigate to the main directory
2. Compile the program: javac ast/*.java crux/*.java mips/*.java types/*.java
3. Convert Crux code (testxx.crx) into MIPS assembly code (testxx.asm) through: java crux.Compiler testxx.crx
4. Use SPIM (a MIPS emulator) to load the MIPS assembly code (testxx.asm) and generate output



