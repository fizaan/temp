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
            this.mem_write(this.MR_KBSR, (1 << 15));
            this.mem_write(this.MR_KBDR, this.userinput[this.count++]);
        }    
        return this.memory[address];
    }

    mem_write(address,val) {
        return this.memory[address] = val;
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
            this.ph[4] = this.bus.getMemory()[this.reg[this.R_PC]++];
            
            this.ph[5] = this.ph[4] >> 12;
            switch(this.ph[5]) {
                case this.OP_ADD:
                    this.ph[0] = (this.ph[4] >> 9) & 0x7;
                    this.ph[1] = (this.ph[4] >> 6) & 0x7;
                    this.ph[2] = (this.ph[4] >> 5) & 0x1;
                    if(this.ph[2]) {
                        this.ph[3] = this.sext(this.ph[4] & 0x1F, 5);
                        this.reg[this.ph[0]] = this.reg[this.ph[1]] + this.ph[3];
                    }
                    else {
                        this.ph[3] = this.ph[4] & 0x7;
                        this.reg[this.ph[0]] = this.reg[this.ph[1]] + this.reg[this.ph[3]];
                    }    
                    this.update_flags(this.ph[0]);    
                break;

                case this.OP_AND:
                    this.ph[0] = (this.ph[4] >> 9) & 0x7;
                    this.ph[1] = (this.ph[4] >> 6) & 0x7;
                    this.ph[2] = (this.ph[4] >> 5) & 0x1;
                    if(this.ph[2]) {
                        this.ph[3] = this.sext(this.ph[4] & 0x1F, 5);
                        this.reg[this.ph[0]] = this.reg[this.ph[1]] & this.ph[3];
                    }
                    else {
                        this.ph[3] = this.ph[4] & 0x7;
                        this.reg[this.ph[0]] = this.reg[this.ph[1]] & this.reg[this.ph[3]];
                    }    
                    this.update_flags(this.ph[0]);      
                break;

                case this.OP_NOT:
                    this.ph[0] = (this.ph[4] >> 9) & 0x7;
                    this.ph[1] = (this.ph[4] >> 6) & 0x7;

                    this.reg[this.ph[0]] = ~this.reg[this.ph[1]];
                    this.update_flags(this.ph[0]);
                break;

                case this.OP_BR:
                    this.ph[0] = this.sext(this.ph[4] & 0x1FF, 9);
                    this.ph[1] = (this.ph[4] >> 9) & 0x7;
                    if (this.ph[1] & this.reg[this.R_COND])
                        this.reg[this.R_PC] += this.ph[0];
                    //console.log("br: "+this.reg[this.R_PC]);
                break;

                case this.OP_JMP:
                    this.ph[0] = (this.ph[4] >> 6) & 0x7;
                    this.reg[this.R_PC] = this.reg[this.ph[0]];
                    //console.log("jmp: "+this.reg[this.R_PC] +"...instr: "+this.ph[4] + " reg: " + this.ph[0] + " = " + this.reg[this.ph[0]]);
                break;

                case this.OP_JSR:
                    this.ph[0]  = (this.ph[4] >> 6) & 0x7;
                    this.ph[1]  = this.sext(this.ph[4] & 0x7FF, 11);
                    this.ph[2]  = (this.ph[4] >> 11) & 1;
                    this.reg[7] = this.reg[this.R_PC];
                    
                    if (this.ph[2])
                        this.reg[this.R_PC] += this.ph[1];  /* JSR */
                    else
                        this.reg[this.R_PC] = this.reg[this.ph[0]]; /* JSRR */
                    
                   // console.log("jsr: "+this.reg[this.R_PC]);
                break;

                case this.OP_LD:
	                this.ph[0] = (this.ph[4] >> 9) & 0x7;
	                this.ph[1] = this.sext(this.ph[4] & 0x1FF, 9);
	                this.reg[ this.ph[0]] = this.bus.mem_read(this.reg[this.R_PC] + this.ph[1]);
	                this.update_flags( this.ph[0]);
                break;
                
                case this.OP_LDI:
	                this.ph[0] = (this.ph[4] >> 9) & 0x7;
	                this.ph[1] = this.sext(this.ph[4] & 0x1FF, 9);
	                this.reg[ this.ph[0]] = this.bus.mem_read(this.bus.mem_read(this.reg[this.R_PC] + this.ph[1]));
	                this.update_flags( this.ph[0]);
                break;

                case this.OP_LDR:
	                this.ph[0] = (this.ph[4] >> 9) & 0x7;
	                this.ph[1] = (this.ph[4] >> 6) & 0x7;
	                this.ph[3] = this.sext(this.ph[4] & 0x3F, 6);
	                this.reg[ this.ph[0]] = this.bus.mem_read(this.reg[this.ph[1]] + this.ph[3]);
	                this.update_flags( this.ph[0]);
	            break;

                case this.OP_LEA:
                    this.ph[0] = (this.ph[4] >> 9) & 0x7;
                    this.ph[1] = this.sext(this.ph[4] & 0x1FF, 9);
                    this.reg[ this.ph[0]] = this.reg[this.R_PC] + this.ph[1];
                    this.update_flags( this.ph[0]);    
                break;

                case this.OP_ST:
	                this.ph[0] = (this.ph[4] >> 9) & 0x7;
	                this.ph[1] = this.sext(this.ph[4] & 0x1FF, 9);
	                this.bus.mem_write(this.reg[this.R_PC] + this.ph[1], this.reg[ this.ph[0]]);
                break;

                case this.OP_STI:
	                this.ph[0] = (this.ph[4] >> 9) & 0x7;
	                this.ph[1] = this.sext(this.ph[4] & 0x1FF, 9);
	                this.bus.mem_write(this.bus.mem_read(this.reg[this.R_PC] + this.ph[1]), this.reg[ this.ph[0]]);
                break;
                
                case this.OP_STR:
	                this.ph[0] = (this.ph[4] >> 9) & 0x7;
	                this.ph[1] = (this.ph[4] >> 6) & 0x7;
	                this.ph[3] = this.sext(this.ph[4] & 0x3F, 6);
	                this.bus.mem_write(this.reg[this.ph[1]] + this.ph[3], this.reg[ this.ph[0]]);
	            break;

                case this.OP_TRAP: {    
                    switch (this.ph[4] & 0xFF)   {
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

            console.log("op code = " + this.ph[4]);
            console.log(this.reg);
            console.log("------");
    }

    


    sext(x, bit_count) {
        if ( ( x >> (bit_count - 1) ) & 1) {
            x |= (0xFFFF << bit_count);
        }
        return x;
    }

    
	update_flags(r) {
        this.ph[1] = 1 << 0; //POS
        this.ph[2] = 1 << 1; //ZER
        this.ph[3] = 1 << 2; //NEG

	    if (this.reg[r] == 0)
	        this.reg[this.R_COND] = this.ph[2]; 
	    else if (this.reg[r] >> 15) /* a 1 in the left-most bit indicates negative  */
	        this.reg[this.R_COND] = this.ph[3];
	    else
            this.reg[this.R_COND] = this.ph[1];
	}
    
    constructor(bus) {
        this.bus = bus;
        this.R_PC = 8;
        this.R_COND = 9;
        this.R_COUNT = 10;
        this.PC_START = 0x3000;
        this.reg = new Uint16Array(this.R_COUNT);
        this.ph = new Uint16Array(6); //PLACE HOLDER for temp unint16 variables.
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
    }
}