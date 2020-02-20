class Bus {

    MAX_MEM = 0xFFFF;

    constructor(name) {
        this.test = "name";
        this.memory = new Uint16Array(this.MAX_MEM);

        // n, y, w, s, a, d
        this.userinput = [ 0x6E, 0x79, 0x77, 0x73, 0x61, 0x64 ];
        this.count = 0;
        this.kb = new Uint16Array(1);
        this.MR_KBSR = 0;
    }

    setImageSize(x) {
        this.imageSize = x;
    }

    print() {
        alert(this.test);
    }

    swap16(x) {
        return (x << 8) | (x >> 8);
    }

    getMemory() { 
        return this.memory; 
        //console.log("getting mem");
    }

    printMemory() {
        for( var i = 0x3000; i < this.imageSize; i++)
            console.log(i + ": " + this.memory[i]);
    }

    mem_read(address) {
        //console.log(address);
        if (address == 0xFE00) {
            this.kb[this.MR_KBSR] = 1 << 15;
            this.mem_write(0xFE00, this.kb[this.MR_KBSR]); //KBSR
            this.mem_write(0xFE02, this.userinput[this.count++]); //KBDR
        }
        else
            this.kb[this.MR_KBSR] = 0;

        return this.memory[address];
    }

    mem_write(address,val) {
        this.memory[address] = val;
        //console.log("writing..." + address + ", value =  " + val);
    }

}// end class Bus


class CPU {

    constructor() {
        this.bus = new Bus("Another test");
    }

    print() { this.print(); }

    /*
        Note: anything outside of onload = function { } is not
        accessible within the brackets. Don't ask why. This is
        bad design from JS mofos. Took me forever reading online
        to understand this.
    */
    load(event, callbackfunc) {
        //Retrieve the first (and only!) File from the FileList object
        var f = event.target.files[0]; 
        if (f) {
            var reader = new FileReader();
            reader.onload = function(e) { 
                var arrayBuffer = e.target.result;
                var bytes = new Uint16Array(arrayBuffer);
                callbackfunc(bytes);
            }
            reader.readAsArrayBuffer(f);
        }   
        else 
            alert("Failed to load file");
    }

} //end class CPU

class vm {

    start() {
            //instruction instr
            this.ph[this.instr] = this.bus.getMemory()[this.reg[this.R_PC]++];
            this.ph[this.opcode] = this.ph[this.instr] >> 12;
            this.ph[this.r0] = (this.ph[this.instr] >> 9) & 0x7;
            this.ph[this.r1] = (this.ph[this.instr] >> 6) & 0x7;
            this.ph[this.r2] = this.ph[this.instr] & 0x7;
            this.ph[this.offset6] = this.sext(this.ph[this.instr] & 0x3F, 6);
            this.ph[this.offset9] = this.sext(this.ph[this.instr] & 0x1FF, 9);
            this.ph[this.offset11] = this.sext(this.ph[this.instr] & 0x7FF, 11);
            this.ph[this.imm5flag] = (this.ph[this.instr] >> 5) & 0x1;
            this.ph[this.imm5] = this.sext(this.ph[this.instr] & 0x1F, 5);
            this.ph[this.longflagcond]  = (this.ph[this.instr] >> 11) & 1;
            

            switch(this.ph[this.opcode]) {
                case this.OP_ADD:
                    if(this.ph[this.imm5flag]) 
                       this.reg[this.ph[this.r0]] = this.reg[this.ph[this.r1]] + this.ph[this.imm5];
                    else 
                        this.reg[this.ph[this.r0]] = this.reg[this.ph[this.r1]] + this.reg[this.r2];   
                    
                    this.update_flags(this.ph[this.r0]);    
                break;

                case this.OP_AND:
                    if(this.ph[this.imm5flag]) 
                        this.reg[this.ph[this.r0]] = this.reg[this.ph[this.r1]] & this.ph[this.imm5];
                   else
                        this.reg[this.ph[this.r0]] = this.reg[this.ph[this.r1]] & this.reg[this.r2];
                     
                    this.update_flags(this.ph[this.r0]);      
                break;

                case this.OP_NOT:
                    this.reg[this.ph[this.r0]] = ~this.reg[this.ph[this.r1]];
                    this.update_flags(this.ph[this.r0]);
                break;

                case this.OP_BR:
                    if (this.ph[this.r0] & this.reg[this.R_COND])
                        this.reg[this.R_PC] += this.ph[this.offset9];
                break;

                case this.OP_JMP:
                    this.reg[this.R_PC] = this.reg[this.ph[this.r1]];
                    //console.log("jmp: "+this.reg[this.R_PC] +"...instr: "+this.ph[this.instr] + " reg: " + this.ph[this.r0] + " = " + this.reg[this.ph[this.r0]]);
                break;

                case this.OP_JSR://##############################
                    //console.log("jsr");
                    this.reg[7] = this.reg[this.R_PC];
                    
                    if (this.ph[this.longflagcond])
                        this.reg[this.R_PC] += this.ph[this.offset11];  /* JSR */
                    else
                        this.reg[this.R_PC] = this.reg[this.ph[this.r1]]; /* JSRR */
                    
                   // console.log("jsr: "+this.reg[this.R_PC]);
                break;

                case this.OP_LD:
                    this.ph[this.pc_sum] = this.reg[this.R_PC] + this.ph[this.offset9];
                    this.reg[ this.ph[this.r0]] = this.bus.mem_read(this.ph[this.pc_sum]);
	                this.update_flags( this.ph[this.r0]);
                break;
                
                case this.OP_LDI:
                    this.ph[this.pc_sum] = this.reg[this.R_PC] + this.ph[this.offset9];
	                this.reg[ this.ph[this.r0]] = this.bus.mem_read(this.bus.mem_read(this.ph[this.pc_sum]));
	                this.update_flags( this.ph[this.r0]);
                break;

                case this.OP_LDR:
                    //console.log("LDR: " + this.ph[this.instr]);
                    this.ph[this.pc_sum] = this.reg[this.ph[this.r1]] + this.ph[this.offset6];
                    this.reg[ this.ph[this.r0]] = this.bus.mem_read(this.ph[this.pc_sum]);
                    //console.log("r0 = " + this.ph[this.r0]);
                   // console.log("r1 = " + this.ph[this.r1]);
                   // console.log("offset = " + this.ph[this.offset6]);
                    //console.log("r6 = " + this.reg[this.ph[this.r1]]);
                   // console.log( "PC offset: " + (this.reg[this.ph[this.r1]] + this.ph[this.offset6]) );
                    //console.log("value at this mem location: " + this.bus.mem_read(this.reg[this.ph[this.r1]] + this.ph[this.offset6]));
                    this.update_flags( this.ph[this.r0]);
                    
	            break;

                case this.OP_LEA:
                    this.reg[ this.ph[this.r0]] = this.reg[this.R_PC] + this.ph[this.offset9];
                    this.update_flags( this.ph[this.r0]);    
                break;

                case this.OP_ST:
                    //console.log("st");
                    this.ph[this.pc_sum] = this.reg[this.R_PC] + this.ph[this.offset9];
	                this.bus.mem_write(this.ph[this.pc_sum], this.reg[ this.ph[this.r0]]);
                break;

                case this.OP_STI:
                    //console.log("sti");
                    this.ph[this.pc_sum] = this.reg[this.R_PC] + this.ph[this.offset9];
	                this.bus.mem_write(this.bus.mem_read(this.ph[this.pc_sum], this.reg[this.ph[this.r0]]));
                break;
                
                case this.OP_STR:
                    //console.log("str " + this.ph[this.instr]);
                    //console.log("r0 = " + this.ph[this.r0]);
                    //console.log("r0 value = " + this.reg[this.ph[this.r0]]);
                    //console.log("r1 = " + this.ph[this.r1]);
                    //console.log("r1 value = " + this.reg[this.ph[this.r1]]);
                    //console.log("offset value = " + this.ph[this.offset6]);
                    
                    //console.log( "PC offset sum: " + (this.reg[this.ph[this.r1]] + this.ph[this.offset6]) );
                    this.ph[this.pc_sum] = this.reg[this.ph[this.r1]] + this.ph[this.offset6];
                    this.bus.mem_write(this.ph[this.pc_sum], this.reg[ this.ph[this.r0]]);
	            break;

                case this.OP_TRAP: {    
                    switch (this.ph[this.instr] & 0xFF)   {
                        case this.TRAP_HALT:
                            console.log("HALT");
                        break;

                        case this.TRAP_GETC:
                            console.log("GETC");
                        break;

                        case this.TRAP_OUT:
                            let val =  this.reg[0];
                            val = String.fromCharCode(val);
	                        console.log(val);
                        break;

                        case this.TRAP_IN:
                            console.log("TRAP_IN");
                        break;

                        case this.TRAP_PUTS:
                            let str = "";
                            let address = this.reg[0];
                            for(var i = address;;i++) {
                                let val = this.bus.getMemory()[i];
                                
                                if(val == 0x00 || val == undefined )
                                    break;               
                              
                                val = String.fromCharCode(val);
                                str += val;
                            }
                            console.log(str);    
                        break;
                    }
                }
                break;
                case this.OP_RES:
	            case this.OP_RTI:
	            default:
	                /* BAD OPCODE */
                   console.log("Bad code");
                break;
            }

            //console.log("op code = " + this.ph[this.instr]);
            console.log(this.reg);
            //console.log("------");
    }

    


    sext(x, bit_count) {
        if ( ( x >> (bit_count - 1) ) & 1) {
            x |= (0xFFFF << bit_count);
        }
        return x;
    }

    
	update_flags(r) {
        // 1 << 0; //POS
        // 1 << 1; //ZER
        // 1 << 2; //NEG

	    if (this.reg[r] == 0)
	        this.reg[this.R_COND] =  1 << 1;    // 2 = ZERO 
	    else if (this.reg[r] >> 15) /* a 1 in the left-most bit indicates negative  */
	        this.reg[this.R_COND] = 1 << 2; //4 = NEG
	    else
            this.reg[this.R_COND] = 1 << 0; // 1 = POS
	}
    
    constructor(bus) {
        this.bus = bus;
        this.R_PC = 8;
        this.R_COND = 9;
        this.R_COUNT = 10;
        this.PC_START = 0x3000;
        this.reg = new Uint16Array(this.R_COUNT);
        this.ph = new Uint16Array(14); //PLACE HOLDER for temp unint16 variables. (1)
        this.reg[this.R_PC] = this.PC_START;

        //place holder values.(1)
        this.r0 = 0;
        this.r1 = 1;
        this.r2 = 2;
        this.r3 = 3;
        this.instr = 4;
        this.opcode = 5;
        this.offset6 = 6;
        this.offset9 = 7;
        this.offset11 = 8;
        this.imm5 = 9;
        this.imm5flag = 10
        this.condflag = 11;
        this.longflagcond = 12;
        this.pc_sum = 13;

        /* Opcodes */
	    this.OP_BR = 0; /* branch */
	    this.OP_ADD = 1;    /* add  */
	    this.OP_LD = 2;    /* load */
	    this.OP_ST = 3; /* store */
	    this.OP_JSR = 4; /* jump register */
	    this.OP_AND = 5;    /* bitwise and */
	    this.OP_LDR = 6;    /* load register */
	    this.OP_STR = 7;    /* store register */
	    this.OP_RTI = 8;    /* unused */
	    this.OP_NOT = 9;    /* bitwise not */
	    this.OP_LDI = 10;    /* load indirect */
	    this.OP_STI = 11;    /* store indirect */
	    this.OP_JMP = 12;    /* jump */
	    this.OP_RES = 13;    /* reserved (unused) */
	    this.OP_LEA = 14;    /* load effective address */
        this.OP_TRAP = 15;    /* execute trap */
        
        /* TRAP Codes */
	
        this.TRAP_GETC = 0x20;  /* get character from keyboard, not echoed onto the terminal */
        this.TRAP_OUT = 0x21;   /* output a character */
        this.TRAP_PUTS = 0x22;  /* output a word string */
        this.TRAP_IN = 0x23;    /* get character from keyboard, echoed onto the terminal */
        this.TRAP_PUTSP = 0x24; /* output a byte string */
        this.TRAP_HALT = 0x25;   /* halt the program */
    }
}