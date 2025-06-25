/**
 * 存储系统常量配置类
 * 定义了存储系统的基本参数和配置信息
 */
public class DiskConst {
    /**
     * 存储系统常量定义
     * 
     * STORAGE_CAPACITY: 存储系统总容量（以存储块为单位）
     * BLOCK_CAPACITY: 每个存储块的容量（以字符为单位）
     * PERSISTENCE_FILE: 文件系统持久化存储路径
     */
    public static int DISK_SIZE = 12000;      // 保持原有常量名以兼容现有代码
    public static int BLOCK_SIZE = 100;       // 保持原有常量名以兼容现有代码
    public static String SerializePath = "fileSystem.txt";
}
