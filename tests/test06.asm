.data                         # BEGIN Data Segment
data.newline:      .asciiz       "\n"
data.floatquery:   .asciiz       "float?"
data.intquery:     .asciiz       "int?"
data.trueString:   .asciiz       "true"
data.falseString:  .asciiz       "false"
                              # END Data Segment
.text                         # BEGIN Code Segment
func.printBool:
lw $a0, 0($sp)
beqz $a0, label.printBool.loadFalse
la $a0, data.trueString
j label.printBool.join
label.printBool.loadFalse:
la $a0, data.falseString
label.printBool.join:
li   $v0, 4
syscall
jr $ra
func.printFloat:
l.s  $f12, 0($sp)
li   $v0,  2
syscall
jr $ra
func.printInt:
lw   $a0, 0($sp)
li   $v0, 1
syscall
jr $ra
func.println:
la   $a0, data.newline
li   $v0, 4
syscall
jr $ra
func.readFloat:
la   $a0, data.floatquery
li   $v0, 4
syscall
li   $v0, 6
syscall
mfc1 $v0, $f0
jr $ra
func.readInt:
la   $a0, data.intquery
li   $v0, 4
syscall
li   $v0, 5
syscall
jr $ra
.text                         # BEGIN Crux Program
func.myPrintZero:
subu $sp, $sp, 8
sw $fp, 0($sp)
sw $ra, 4($sp)
addi $fp, $sp, 8
subu $sp, $sp, 0
addi $t0, $0, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
label.0:
addu $sp, $sp, 0
lw $ra, 4($sp)
lw $fp, 0($sp)
addu $sp, $sp, 8
jr $ra
func.myPrintOne:
subu $sp, $sp, 8
sw $fp, 0($sp)
sw $ra, 4($sp)
addi $fp, $sp, 8
subu $sp, $sp, 0
addi $t0 , $fp, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
label.1:
addu $sp, $sp, 0
lw $ra, 4($sp)
lw $fp, 0($sp)
addu $sp, $sp, 8
jr $ra
func.myPrintTwo:
subu $sp, $sp, 8
sw $fp, 0($sp)
sw $ra, 4($sp)
addi $fp, $sp, 8
subu $sp, $sp, 0
addi $t0 , $fp, 4
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
addi $t0 , $fp, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
label.2:
addu $sp, $sp, 0
lw $ra, 4($sp)
lw $fp, 0($sp)
addu $sp, $sp, 8
jr $ra
func.myPrintThree:
subu $sp, $sp, 8
sw $fp, 0($sp)
sw $ra, 4($sp)
addi $fp, $sp, 8
subu $sp, $sp, 0
addi $t0 , $fp, 8
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
addi $t0 , $fp, 4
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
addi $t0 , $fp, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
label.3:
addu $sp, $sp, 0
lw $ra, 4($sp)
lw $fp, 0($sp)
addu $sp, $sp, 8
jr $ra
main:
subu $sp, $sp, 8
sw $fp, 0($sp)
sw $ra, 4($sp)
addi $fp, $sp, 8
subu $sp, $sp, 0
jal func.myPrintZero
addi $sp, $sp, 0
jal func.println
addi $sp, $sp, 0
addi $t0, $0, 1
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.myPrintOne
addi $sp, $sp, 4
jal func.println
addi $sp, $sp, 0
addi $t0, $0, 1
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 2
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.myPrintTwo
addi $sp, $sp, 8
jal func.println
addi $sp, $sp, 0
addi $t0, $0, 1
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 2
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 3
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.myPrintThree
addi $sp, $sp, 12
jal func.println
addi $sp, $sp, 0
label.4:
li    $v0, 10
syscall
                              # END Code Segment
