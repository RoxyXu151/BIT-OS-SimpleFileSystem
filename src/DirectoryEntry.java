import java.io.Serializable;

/**
 * 文件系统条目基类
 * 作为文件和目录的共同父类，定义了基本属性
 */
public abstract class DirectoryEntry implements Serializable {
    // 条目名称（文件名或目录名）
    public String name;                     
    // 类型标志（true=目录，false=文件）
    public boolean isDirectory;             
    // 父目录引用
    public Directory parentDir;             

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

    @Override
    public String toString() {
        return "DirectoryEntry{" +
                "name='" + name + '\'' +
                ", isDirectory=" + isDirectory +
                ", parentDir=" + parentDir +
                '}';
    }
}
