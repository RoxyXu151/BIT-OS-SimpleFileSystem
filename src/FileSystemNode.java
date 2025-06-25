import java.io.Serializable;

/**
 * 文件系统节点基类
 * 作为文件和目录的共同父类，定义了基本属性
 */
public abstract class FileSystemNode implements Serializable {
    // 条目名称（文件名或目录名）
    protected String name;                     
    // 类型标志（true=目录，false=文件）
    protected boolean isDirectory;             
    // 父目录引用
    protected FileSystemFolder parentDir;             

    /**
     * 创建一个新的文件系统节点
     * 
     * @param name 节点名称
     * @param isDirectory 是否为目录
     * @param parentDir 父目录引用
     */
    public FileSystemNode(String name, boolean isDirectory, FileSystemFolder parentDir) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.parentDir = parentDir;
    }

    /**
     * 获取节点名称
     * @return 节点名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 设置节点名称
     * @param name 新的节点名称
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
    public FileSystemFolder getParentDir() {
        return parentDir;
    }
    
    /**
     * 设置父目录
     * @param parentDir 新的父目录引用
     */
    public void setParentDir(FileSystemFolder parentDir) {
        this.parentDir = parentDir;
    }
    
    @Override
    public String toString() {
        return "FileSystemNode{" +
                "name='" + name + '\'' +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}
