class Bus {

    MAX_MEM = 0xFFFF;

    constructor(name) {
        this.test = "name";
        this.memory = new Uint16Array(this.MAX_MEM);
        this.userinput = [ 0x6E, 0x79, 0x77, 0x73, 0x61, 0x64 ];
        this.count = 0;
        
        /* Memory Mapped Registers */
	    this.MR_KBSR = 0xFE00; /* keyboard status */
	    this.MR_KBDR = 0xFE02;  /* keyboard data */
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
        if (address == this.MR_KBSR) {
           // await sleep(2000);
            this.mem_write(this.MR_KBSR, (1 << 15));
            this.mem_write(this.MR_KBDR, this.userinput[this.count++]);
            //console.log(this.count+": you entered: " + this.memory[this.MR_KBDR]);
        }    
        return this.memory[address];
    }

    mem_write(address,val) {
        return this.memory[address] = val;
    }

    sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
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
        var running = true;
        //while(running) {
            //console.log("running");
            let instr = this.bus.getMemory()[this.reg[this.R_PC]++];
            //console.log(this.reg[this.R_PC]);
            if(this.reg[this.R_PC] >= this.bus.imageSize)
                running = false;
            let op = instr >> 12;
            let r0 = 0;
            let r1 = 0;
            let imm5 = 0;
            let imm_flag = 0;
            let pc_offset = 0;
            let long_pc_offset = 0;
            let cond_flag = 0;
            let long_flag = 0;
            let offset = 0;
            switch(op) {
                case this.OP_ADD:
                //console.log("add");
                    r0 = (instr >> 9) & 0x7;
                    r1 = (instr >> 6) & 0x7;
                    imm_flag = (instr >> 5) & 0x1;
                    if(imm_flag) {
                        imm5 = this.sext(instr & 0x1F, 5);
                        this.reg[r0] = this.reg[r1] + imm5;
                    }
                    else {
                        let r2 = instr & 0x7;
                        this.reg[r0] = this.reg[r1] + this.reg[r2];
                    }    
                    this.update_flags(r0);    
                break;

                case this.OP_AND:
                //console.log("and");
                    r0 = (instr >> 9) & 0x7;
                    r1 = (instr >> 6) & 0x7;
                    imm_flag = (instr >> 5) & 0x1;
                    if(imm_flag) {
                        imm5 = this.sext(instr & 0x1F, 5);
                        this.reg[r0] = this.reg[r1] & imm5;
                    }
                    else {
                        let r2 = instr & 0x7;
                        this.reg[r0] = this.reg[r1] & this.reg[r2];
                    }    
                    this.update_flags(r0);    
                break;

                case this.OP_NOT:
                //console.log("not");
                    r0 = (instr >> 9) & 0x7;
                    r1 = (instr >> 6) & 0x7;

                    this.reg[r0] = ~this.reg[r1];
                    this.update_flags(r0);
                break;

                case this.OP_BR:
                //console.log("br");
                    pc_offset = this.sext(instr & 0x1FF, 9);
                    cond_flag = (instr >> 9) & 0x7;
                    if (cond_flag & this.reg[this.R_COND])
                        this.reg[this.R_PC] += pc_offset;
                    //let current_nzp = this.reg[this.R_COND] & 0b111;
                    //let desired_nzp = (instr >> 9) & 0b111;

                   // if (current_nzp & desired_nzp) {
                   //     pc_offset = this.sext(instr, 9);
                   //     this.reg[this.R_PC] += pc_offset;
                   // }

                break;
                break;

                case this.OP_JMP:
                //console.log("jmp");
                    /* Also handles RET */
                    r1 = (instr >> 6) & 0x7;
                    this.reg[this.R_PC] = this.reg[r1];
                break;

                case this.OP_JSR:
                //console.log("jsr");
                    r1 = (instr >> 6) & 0x7;
                    long_pc_offset = this.sext(instr & 0x7FF, 11);
                    long_flag = (instr >> 11) & 1;
                    this.reg[7] = this.reg[this.R_PC];
                    
                    if (long_flag)
                        this.reg[this.R_PC] += long_pc_offset;  /* JSR */
                    else
                        this.reg[this.R_PC] = this.reg[r1]; /* JSRR */
                break;

                case this.OP_LD:
                //console.log("ld");
	                r0 = (instr >> 9) & 0x7;
	                pc_offset = this.sext(instr & 0x1FF, 9);
	                this.reg[r0] = this.bus.mem_read(this.reg[this.R_PC] + pc_offset);
	                this.update_flags(r0);
                break;
                
                case this.OP_LDI:
                //console.log("ldi");
	                r0 = (instr >> 9) & 0x7;
	                pc_offset = this.sext(instr & 0x1FF, 9);
	                this.reg[r0] = this.bus.mem_read(this.bus.mem_read(this.reg[this.R_PC] + pc_offset));
	                this.update_flags(r0);
                break;

                case this.OP_LDR:
                //console.log("ldr");
	                r0 = (instr >> 9) & 0x7;
	                r1 = (instr >> 6) & 0x7;
	                offset = this.sext(instr & 0x3F, 6);
	                this.reg[r0] = this.bus.mem_read(this.reg[r1] + offset);
	                this.update_flags(r0);
	            break;

                case this.OP_LEA:
                //console.log("lea");
                    r0 = (instr >> 9) & 0x7;
                    pc_offset = this.sext(instr & 0x1FF, 9);
                    this.reg[r0] = this.reg[this.R_PC] + pc_offset;
                    this.update_flags(r0);    
                break;

                case this.OP_ST:
                //console.log("st");
	                r0 = (instr >> 9) & 0x7;
	                pc_offset = this.sext(instr & 0x1FF, 9);
	                this.bus.mem_write(this.reg[this.R_PC] + pc_offset, this.reg[r0]);
                break;

                case this.OP_STI:
                //console.log("sti");
	                r0 = (instr >> 9) & 0x7;
	                pc_offset = this.sext(instr & 0x1FF, 9);
	                this.bus.mem_write(this.bus.mem_read(this.reg[this.R_PC] + pc_offset), this.reg[r0]);
                break;
                
                case this.OP_STR:
                //console.log("str");
	                r0 = (instr >> 9) & 0x7;
	                r1 = (instr >> 6) & 0x7;
	                offset = this.sext(instr & 0x3F, 6);
	                this.bus.mem_write(this.reg[r1] + offset, this.reg[r0]);
	            break;

                case this.OP_TRAP: {    
                    switch (instr & 0xFF)   {
                        case this.TRAP_HALT:
                        console.log("HALT");
                        running = false;    
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
                break;
                }
                case this.OP_RES:
	            case this.OP_RTI:
	            default:
	                /* BAD OPCODE */
                   //console.log("Bad code");
                   running = false;
	            break;
            }
        //}
    }

    


    sext(x, bit_count) {
        if ((x >> (bit_count - 1) & 1)) 
	        x |= (0xFFFF << bit_count);
	    return x;
    }

    /*sext(val, n) {
        var m = 1 << (n - 1);
        val &= (1 << n) - 1;
        return (val ^ m) - m;
    }*/

    
	update_flags(r) {
	    if (this.reg[r] == 0)
	        this.reg[this.R_COND] = this.FL_ZRO;
	    else if (this.reg[r] >> 15) /* a 1 in the left-most bit indicates negative  */
	        this.reg[this.R_COND] = this.FL_NEG;
	    else
            this.reg[this.R_COND] = this.FL_POS;
            
            /*if (r == 0) {
                this.reg[this.R_COND] = this.FL_ZRO;
            }
            else if (r & (1 << 15)) {
                this.reg[this.R_COND] = this.FL_NEG;
            }
            else {
                this.reg[this.R_COND] = this.FL_POS
            }*/
	}
    
    constructor(bus) {
        this.bus = bus;
        this.R_PC = 8;
        this.R_COND = 9;
        this.PC_START = 0x3000;
        this.reg = [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ];
        this.reg[this.R_PC] = this.PC_START;

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

        /* Condition Flags */
	    this.FL_POS = 1 << 0; /* P */
	    this.FL_ZRO = 1 << 1; /* Z */
        this.FL_NEG = 1 << 2; /* N */
    }
}