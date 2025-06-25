import java.io.Serializable;

/**
 * 存储块类
 * 表示存储系统中的基本存储单元，使用字符串存储内容以简化实现
 */
public class VirtualBlock implements Serializable {
    // 存储块的最大容量（字符数）
    private static final int MAX_CAPACITY = DiskConst.BLOCK_SIZE;    
    // 存储块的唯一标识符
    private final int blockIndex;                                   
    // 存储块的剩余可用空间
    private int availableSpace;                                     
    // 存储块的使用状态标志
    private boolean allocated;                                      
    // 存储块中的实际内容
    private String data;                                            

    /**
     * 构造一个新的存储块
     * @param blockIndex 存储块的唯一标识符
     */
    public VirtualBlock(int blockIndex) {
        this.blockIndex = blockIndex;
        this.availableSpace = MAX_CAPACITY ;
        this.allocated = false;
        this.data = "";
    }

    /**
     * 获取存储块的最大容量
     * @return 存储块容量（字符数）
     */
    public int getBlockSize() {
        return MAX_CAPACITY;
    }

    /**
     * 获取存储块的唯一标识符
     * @return 存储块标识符
     */
    public int getBlockID() {
        return blockIndex;
    }

    /**
     * 检查存储块是否已被分配使用
     * @return 如果已分配则返回true，否则返回false
     */
    public boolean isUsed() {
        return allocated;
    }

    /**
     * 设置存储块的分配状态
     * @param status 分配状态，true表示已分配，false表示未分配
     */
    public void setUsed(boolean status) {
        allocated = status;
    }

    /**
     * 读取存储块中的内容
     * @return 存储块中的数据字符串
     */
    public String readBlock() {
        return data;
    }

    /**
     * 向存储块写入内容
     * @param newData 要写入的数据字符串
     */
    public void writeBlock(String newData) {
        this.data = newData;
        this.availableSpace = MAX_CAPACITY - newData.length();
        this.allocated = true;
    }

    /**
     * 清空存储块内容并重置状态
     */
    public void clearBlock() {
        this.data = "";
        this.availableSpace = MAX_CAPACITY;
        this.allocated = false;
    }

    @Override
    public String toString() {
        return "VirtualBlock{" +
                "blockIndex=" + blockIndex +
                ", availableSpace=" + availableSpace +
                ", allocated=" + allocated +
                ", data='" + data + '\'' +
                '}';
    }
}