import java.io.Serializable;

/**
 * 文件系统条目基类
 * 作为文件和目录的共同父类，定义了基本属性
 */
public abstract class DirectoryEntry implements Serializable {
    // 条目名称（文件名或目录名）
    protected String name;                     
    // 类型标志（true=目录，false=文件）
    protected boolean isDirectory;             
    // 父目录引用
    protected Directory parentDir;             

    /**
     * 创建一个新的文件系统条目
     * 
     * @param name 条目名称
     * @param isDirectory 是否为目录
     * @param parentDir 父目录引用
     */
    public DirectoryEntry(String name, boolean isDirectory, Directory parentDir) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.parentDir = parentDir;
    }

    /**
     * 获取条目名称
     * @return 条目名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 设置条目名称
     * @param name 新的条目名称
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 检查是否为目录
     * @return 如果是目录则返回true，否则返回false
     */
    public boolean isDirectory() {
        return isDirectory;
    }
    
    /**
     * 获取父目录
     * @return 父目录引用
     */
    public Directory getParentDir() {
        return parentDir;
    }
    
    /**
     * 设置父目录
     * @param parentDir 新的父目录引用
     */
    public void setParentDir(Directory parentDir) {
        this.parentDir = parentDir;
    }
    
    @Override
    public String toString() {
        return "DirectoryEntry{" +
                "name='" + name + '\'' +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}
