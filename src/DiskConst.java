/**
 * 存储系统常量配置类
 * 定义了存储系统的基本参数和配置信息
 */
public class DiskConst {
    /**
     * 存储系统常量定义
     * 
     * DISK_SIZE: 存储系统总容量（以存储块为单位）
     * BLOCK_SIZE: 每个存储块的容量（以字符为单位）
     * SERIALIZE_PATH: 文件系统持久化存储路径
     */
    public static final int DISK_SIZE = 12000;
    public static final int BLOCK_SIZE = 100;
    public static final String SERIALIZE_PATH = "fileSystem.txt";
}
