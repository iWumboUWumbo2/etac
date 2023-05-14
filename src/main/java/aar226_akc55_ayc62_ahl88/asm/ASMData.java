package aar226_akc55_ayc62_ahl88.asm;

import aar226_akc55_ayc62_ahl88.asm.Instructions.ASMLabel;
import static aar226_akc55_ayc62_ahl88.Main.INDENT_SFILE;

/**
 * Class for data such as globals
 */
public class ASMData {
    private long[] data;
    private ASMLabel label;

    /**
     * @param label
     * @param data
     */
    public ASMData(ASMLabel label, long[] data) {
        this.data = data;
        this.label = label;
    }

    public long[] getData() {
        return data;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(label + "\n");
        for (long num : data){
            builder.append(INDENT_SFILE + ".quad ").append(num).append("\n");
        }
        return builder.toString();
    }
}
