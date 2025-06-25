import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 目录类
 * 表示文件系统中的目录结构，管理子目录和文件
 */
public class Directory extends DirectoryEntry implements Serializable {
    // 目录内容表，存储子目录和文件的映射关系
    private final HashMap<String, DirectoryEntry> dirTable;

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
    public DirectoryEntry getEntry(String name) {
        return dirTable.get(name);
    }
    
    /**
     * 添加条目到目录
     * 
     * @param name 条目名称
     * @param entry 条目对象
     */
    public void addEntry(String name, DirectoryEntry entry) {
        dirTable.put(name, entry);
    }
    
    /**
     * 从目录中移除条目
     * 
     * @param name 条目名称
     * @return 被移除的条目，如果不存在则返回null
     */
    public DirectoryEntry removeEntry(String name) {
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
     * 获取目录中所有条目
     * 
     * @return 条目映射表
     */
    public Map<String, DirectoryEntry> getEntries() {
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
        return "Directory{" +
                "dirTable=" + dirTable +
                ", name='" + name + '\'' +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}