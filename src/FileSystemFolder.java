import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件系统目录类
 * 表示文件系统中的目录结构，管理子目录和文件
 */
public class FileSystemFolder extends FileSystemNode implements Serializable {
    // 目录内容表，存储子目录和文件的映射关系
    private final HashMap<String, FileSystemNode> dirTable;

    /**
     * 创建一个新的目录
     * 
     * @param name 目录名称
     * @param isDirectory 是否为目录（应始终为true）
     * @param parentDir 父目录引用
     */
    public FileSystemFolder(String name, boolean isDirectory, FileSystemFolder parentDir) {
        super(name, isDirectory, parentDir);
        dirTable = new HashMap<>();
    }

    /**
     * 检查目录中是否包含指定名称的条目
     * 
     * @param name 条目名称
     * @return 如果存在则返回true，否则返回false
     */
    public boolean containsEntry(String name) {
        return dirTable.containsKey(name);
    }
    
    /**
     * 获取目录中的条目
     * 
     * @param name 条目名称
     * @return 条目对象，如果不存在则返回null
     */
    public FileSystemNode getEntry(String name) {
        return dirTable.get(name);
    }
    
    /**
     * 添加条目到目录
     * 
     * @param name 条目名称
     * @param entry 条目对象
     */
    public void addEntry(String name, FileSystemNode entry) {
        dirTable.put(name, entry);
    }
    
    /**
     * 移除目录中的条目
     * 
     * @param name 条目名称
     * @return 被移除的条目对象，如果不存在则返回null
     */
    public FileSystemNode removeEntry(String name) {
        return dirTable.remove(name);
    }
    
    /**
     * 获取目录中所有条目的名称
     * 
     * @return 条目名称集合
     */
    public Iterable<String> getEntryNames() {
        return dirTable.keySet();
    }
    
    /**
     * 获取目录中的所有条目
     * 
     * @return 条目映射表的副本
     */
    public Map<String, FileSystemNode> getEntries() {
        return new HashMap<>(dirTable);
    }
    
    /**
     * 检查目录是否为空
     * 
     * @return 如果目录为空则返回true，否则返回false
     */
    public boolean isEmpty() {
        return dirTable.isEmpty();
    }
    
    @Override
    public String toString() {
        return "FileSystemFolder{" +
                "name='" + name + '\'' +
                "dirTable=" + dirTable +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}