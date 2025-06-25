import java.io.Serializable;
import java.util.HashMap;

/**
 * 目录类
 * 表示文件系统中的目录结构，管理子目录和文件
 */
public class Directory extends DirectoryEntry implements Serializable {
    // 目录内容表，存储子目录和文件的映射关系
    public HashMap<String, DirectoryEntry> dirTable;        

    /**
     * 创建一个新的目录
     * 
     * @param name 目录名称
     * @param isDirectory 是否为目录（应始终为true）
     * @param parentDir 父目录引用
     */
    public Directory(String name, boolean isDirectory, Directory parentDir) {
        super(name, isDirectory, parentDir);
        dirTable = new HashMap<>();
    }

    @Override
    public String toString() {
        return "Directory{" +
                "dirTable=" + dirTable +
                ", name='" + name + '\'' +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}