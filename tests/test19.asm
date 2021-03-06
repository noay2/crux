.data                         # BEGIN Data Segment
cruxdata.x: .space 60
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
main:
subu $sp, $sp, 8
sw $fp, 0($sp)
sw $ra, 4($sp)
addi $fp, $sp, 8
subu $sp, $sp, 16
addi $t0, $0, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -8
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
addi $t0, $0, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -12
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
addi $t0, $0, 4
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
addi $t0, $0, 2
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
label.1: 
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
sge $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
blez $t0, label.2
label.3: 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
sge $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
blez $t0, label.4
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 1000
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
mul $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 100
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
mul $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
add $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
addi $t0, $0, 99
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
add $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
la $t0 , cruxdata.x
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
mul $t1, $t1, 20
add  $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
mul $t1, $t1, 4
add  $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 1
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
sub $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
j label.3
label.4: 
addi $t0, $0, 2
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 1
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
sub $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
j label.1
label.2: 
addi $t0, $0, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
addi $t0, $0, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
label.5: 
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 5
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
slt $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
blez $t0, label.6
label.7: 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 3
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
slt $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
blez $t0, label.8
la $t0 , cruxdata.x
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
mul $t1, $t1, 20
add  $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
mul $t1, $t1, 4
add  $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 1
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
add $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
j label.7
label.8: 
jal func.println
addi $sp, $sp, 0
addi $t0, $0, 0
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0 , $fp, -20
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
addi $t0, $0, 1
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t1, 0($sp) 
addi $sp, $sp, 4
add $t1, $t1, $t0
subu $sp, $sp, 4
sw $t1, 0($sp) 
addi $t0 , $fp, -16
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t1, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($sp) 
addi $sp, $sp, 4
sw $t0, 0($t1)
j label.5
label.6: 
addi $t0 , $fp, -8
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
addi $t0 , $fp, -12
subu $sp, $sp, 4
sw $t0, 0($sp) 
lw $t0, 0($sp) 
addi $sp, $sp, 4
lw $t0, 0($t0)
subu $sp, $sp, 4
sw $t0, 0($sp) 
jal func.printInt
addi $sp, $sp, 4
label.0:
li    $v0, 10
syscall
                              # END Code Segment
