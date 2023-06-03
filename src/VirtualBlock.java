import java.io.Serializable;

public class VirtualBlock implements Serializable {
    /**
     * 虚拟块类：
     *      用来标识磁盘和内存中的基本存储单位，模拟按字节编址（基本单元为字节），
     *      实际采用按照字符编址的方法，并以字符串对象代替字符数组，以简化操作流程。
     * 属性：
     *      块号：虚拟块的唯一标识。一旦创建则不可修改。
     *      块容量：虚拟块的容量大小。虚拟块容量默认设置为 100，代表一个虚拟块可以存储 100 个字符，即字符串长度不能超过 100。
     *      块空闲容量：虚拟块的空闲容量。当虚拟块未满时，记录虚拟块的剩余空间。
     *      块空闲标志：虚拟块的空闲标志。当虚拟块未满时，该标记未真，否则为假。在这里为了简化操作，该标志表示虚拟块是否被使用，
     *                若已被使用则未真，否则为假。
     *      块内容：虚拟块的内容。用于记录虚拟块的真实内容，这里使用字符串代替字符数组，以简化流程。
     * 方法：
     *      构造方法
     *      变量的 Get 方法和 Set 方法
     * 补充：
     *      若想将虚拟块模拟的更为细致，首先可以将内容变量的变量类型改为固定大小的字符数组，并调用字符串和字符数组的相互转换函数。
     *      但是在各个组件的数据传输中，仍然建议使用字符串进行传递，方便且高效。其次，可以设置 ArrayList<Integer> Position
     *      用来记录不同文件的分割之处的位置，并针对此添加 append 附加函数和 remove 删除函数，还需要对别的细节进行实现，这样
     *      就可以避免外部碎片的产生，提高存储占用率。
     * */
    private final static int blockSize = DiskConst.BLOCK_SIZE;      // 块容量
    private final int blockID;                                      // 块号
    private int freeSize;                                           // 块空闲容量
    private boolean isUSED;                                         // 块是否被占用
    private String content;                                         // 块内容

    public VirtualBlock(int blockID) {
        this.blockID = blockID;
        this.freeSize = blockSize;
        this.isUSED = false;
        this.content = "";
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getBlockID() {
        return blockID;
    }

    public boolean isUSED() {
        return isUSED;
    }

    public void setUSED(boolean USED) {
        isUSED = USED;
    }

    public String readBlock() {
        return content;
    }

    public void writeBlock(String content) {
        this.content = content;
        this.freeSize = blockSize - content.length();
        this.isUSED = true;
    }

    public void clearBlock() {
        this.content = "";
        this.freeSize = blockSize;
        this.isUSED = false;
    }

    @Override
    public String toString() {
        return "VirtualBlock{" +
                "blockID=" + blockID +
                ", freeSize=" + freeSize +
                ", isUSED=" + isUSED +
                ", content='" + content + '\'' +
                '}';
    }
}